package org.openmhealth.reference.request;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.OmhException;

import com.sun.mail.smtp.SMTPTransport;

/**
 * <p>
 * Creates a new registration for a new user.
 * </p>
 *
 * @author John Jenkins
 */
public class UserRegistrationRequest extends Request<Object> {
	/**
	 * The path to this API after the mandatory path and the version, e.g.
	 * /omh/v1.
	 */
	public static final String PATH = "/users/registration";
	
	/**
	 * The logger for this request.
	 */
	private static final Logger LOGGER =
		Logger.getLogger(UserRegistrationRequest.class.toString());
	
	/**
	 * The algorithm to use to create the random registration ID.
	 */
	private static final String DIGEST_ALGORITHM = "SHA-512";

	/**
	 * The mail protocol to use when sending mail.
	 */
	private static final String MAIL_PROTOCOL = "smtp";
	/**
	 * The mail property key for SSL.
	 */
	private static final String MAIL_PROPERTY_SSL_ENABLED =
			"mail." + MAIL_PROTOCOL + ".ssl.enable";
	/**
	 * The mail session properties to use for every session.
	 */
	private static final Properties MAIL_SESSION_PROPERTIES = new Properties();
	static {
		// Always use SSL.
		MAIL_SESSION_PROPERTIES.put(MAIL_PROPERTY_SSL_ENABLED, true);
	}
	
	/**
	 * The email address of the sender of the registration emails.
	 */
	private static final String MAIL_SENDER_EMAIL_STRING =
		"no-reply@openmhealth.org";
	/**
	 * The InternetAddress object to use for the sender of the registration
	 * emails.
	 */
	private static final InternetAddress MAIL_SENDER_EMAIL;
	static {
		try {
			MAIL_SENDER_EMAIL = new InternetAddress(MAIL_SENDER_EMAIL_STRING);
		}
		catch(AddressException e) {
			throw
				new IllegalStateException(
					"The sender email address is invalid.",
					e);
		}
	}
	
	/**
	 * The subject line for the registration email.
	 */
	private static final String REGISTRATION_SUBJECT =
		"Open mHealth Registration";
	/**
	 * The specialized text to use as a placeholder for the activation link
	 * within the {@link #REGISTRATION_TEXT}.
	 */
	private static final String ACTIVATION_LINK_PLACEHOLDER =
		"<ACTIVATION_LINK>";
	/**
	 * The text to send in the registration email.
	 */
	private static final String REGISTRATION_TEXT =
		"<h3>Registration Activation</h3>" +
		"<p>Thank you for creating an account. To activate your account, " +
			"follow the link.</p>" +
		"<br/>" +
		ACTIVATION_LINK_PLACEHOLDER +
		"<br/>" +
		"<h6>If you are not attempting to create an account, please " +
			"disregard this email.</h6>";
	/**
	 * The URL to the activation end-point of our system.
	 */
	private static final String ACTIVATION_URL =
		"http://localhost:8080/omh/v1" + UserActivationRequest.ACTIVATION_PAGE;
	
	/**
	 * The new user.
	 */
	private final User user;
	
	/**
	 * Creates a registration request.
	 * 
	 * @param username
	 *        The new user's user-name.
	 * 
	 * @param password
	 *        The new user's plain-text password.
	 * 
	 * @param email
	 *        The new user's email address.
	 */
	public UserRegistrationRequest(
		final String username,
		final String password,
		final String email)
		throws OmhException {
		
		// Create the new user from the parameters and a random registration
		// ID.
		this.user =
			new User(
				username,
				User.hashPassword(password),
				email,
				createRegistrationId(username, email),
				System.currentTimeMillis(),
				null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.request.Request#service()
	 */
	@Override
	public void service() throws OmhException {
		// First, short-circuit if this request has already been serviced.
		if(isServiced()) {
			return;
		}
		else {
			setServiced();
		}
		
		// Create the registration entry in the database.
		UserBin.getInstance().createUser(user);
		
		// Create a mail session.
		Session smtpSession =
			Session.getDefaultInstance(MAIL_SESSION_PROPERTIES);
		
		// Create the message.
		MimeMessage message = new MimeMessage(smtpSession);
		
		// Add the recipient.
		try {
			message.setRecipient(Message.RecipientType.TO, user.getEmail());
		}
		catch(MessagingException e) {
			throw
				new OmhException(
					"There was an error setting the recipient of the message.",
					e);
		}
		
		// Add the sender.
		try {
			message.setFrom(MAIL_SENDER_EMAIL);
		}
		catch(MessagingException e) {
			throw
				new OmhException(
					"There was an error setting the sender's email address.",
					e);
		}
		
		// Set the subject.
		try {
			message.setSubject(REGISTRATION_SUBJECT);
		}
		catch(MessagingException e) {
			throw
				new OmhException(
					"There was an error setting the subject on the message.",
					e);
		}

		// Set the content of the message.
		try {
			message.setContent(createRegistrationText(), "text/html");
		}
		catch(MessagingException e) {
			throw
				new OmhException(
					"There was an error constructing the message.",
					e);
		}
		
		// Prepare the message to be sent.
		try {
			message.saveChanges();
		}
		catch(MessagingException e) {
			throw
				new OmhException(
					"There was an error saving the changes to the message.",
					e);
		}
		
		// Send the registration email.
		sendMailMessage(smtpSession, message);
	}
	
	/**
	 * Creates a random registration ID.
	 * 
	 * @param username
	 *        The user's user-name.
	 * 
	 * @param email
	 *        The user's email address.
	 * 
	 * @return A random value that can be used as a registration ID.
	 * 
	 * @throws OmhException
	 *         There was a problem creating the registration ID.
	 */
	private String createRegistrationId(
		final String username,
		final String email)
		throws OmhException {
		
		// Generate the digest.
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
		}
		catch(NoSuchAlgorithmException e) {
			throw new OmhException("The SHA-512 algorithm is unknown.", e);
		}
		
		digest.update(username.getBytes());
		digest.update(email.getBytes());
		digest
			.update(
				new Long(System.currentTimeMillis()).toString().getBytes());
		digest.update(UUID.randomUUID().toString().getBytes());
		byte[] digestBytes = digest.digest();
		
		StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < digestBytes.length; i++) {
        	buffer.append(
        			Integer.toString(
        					(digestBytes[i] & 0xff) + 0x100, 16)
        						.substring(1));
        }
		return buffer.toString();
	}
	
	/**
	 * Creates the registration text.
	 * 
	 * @return The registration text.
	 * 
	 * @throws OmhException
	 *         There was a problem creating the registration text.
	 */
	private String createRegistrationText() throws OmhException {
		return
			REGISTRATION_TEXT
				.replace(
					ACTIVATION_LINK_PLACEHOLDER,
					"<a href=\"" +
						ACTIVATION_URL +
						"?" +
						User.JSON_KEY_REGISTRATION_KEY +
						"=" +
						user.getRegistratioKey() +
						"\">Click here to activate your account.</a>");
	}
	
	/**
	 * Sends a mail message.
	 * 
	 * @param smtpSession
	 *        The session used to create the message.
	 * 
	 * @param message
	 *        The message to be sent.
	 * 
	 * @throws OmhException
	 *         There was a problem creating the connection to the mail server
	 *         or sending the message.
	 */
	private void sendMailMessage(
		final Session smtpSession,
		final Message message)
		throws OmhException {
		
		// Get the transport from the session.
		SMTPTransport transport;
		try {
			transport = 
				(SMTPTransport) smtpSession.getTransport(MAIL_PROTOCOL);
		}
		catch(NoSuchProviderException e) {
			throw
				new OmhException(
					"There is no provider for " + MAIL_PROTOCOL + ".",
					e);
		}

		// Connect to the transport.
		try {
			transport.connect();
		}
		catch(MessagingException e) {
			throw new OmhException("Could not connect to the mail server.", e);
		}
		
		// Send the message.
		try {
			transport.sendMessage(message, message.getAllRecipients());
		}
		catch(SendFailedException e) {
			throw new OmhException("Failed to send the message.", e);
		}
		catch(MessagingException e) {
			throw
				new OmhException(
					"There was a problem while sending the message.",
					e);
		}
		finally {
			// Close the connection to the transport.
			try {
				transport.close();
			}
			catch(MessagingException e) {
				LOGGER
					.log(
						Level.WARNING,
						"After sending the message there was an error " +
							"closing the connection.",
						e);
			}
		}
	}
}