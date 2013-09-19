/*******************************************************************************
 * Copyright 2013 Open mHealth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmhealth.reference.domain;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import jbcrypt.BCrypt;

import org.openmhealth.reference.exception.OmhException;
import org.openmhealth.reference.util.OmhObjectMapper;
import org.openmhealth.reference.util.OmhObjectMapper.JacksonFieldFilter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * <p>
 * A user in the system.
 * </p>
 *
 * @author John Jenkins
 */
@JsonFilter(User.JACKSON_FILTER_GROUP_ID)
public class User implements OmhObject {
	/**
	 * The version of this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The number of rounds for BCrypt to use when generating a salt.
	 */
	private static final int BCRYPT_SALT_ROUNDS = 12;
	
	/**
	 * The group ID for the Jackson filter. This must be unique to our class,
	 * whatever the value is.
	 */
	protected static final String JACKSON_FILTER_GROUP_ID =
		"org.openmhealth.reference.domain.User";
	// Register this class with the Open mHealth object mapper.
	static {
		OmhObjectMapper.register(User.class);
	}
	
	/**
	 * The JSON key for the user's user-name.
	 */
	public static final String JSON_KEY_USERNAME = "username";
	/**
	 * The JSON key for the user's password.
	 */
	public static final String JSON_KEY_PASSWORD = "password";
	/**
	 * The JSON key for the user's email address.
	 */
	public static final String JSON_KEY_EMAIL = "email";
	/**
	 * The JSON key for the registration key for this user.
	 */
	public static final String JSON_KEY_REGISTRATION_KEY = "registration_key";
	/**
	 * The JSON key for the date the user's account was registered.
	 */
	public static final String JSON_KEY_DATE_REGISTERED = "date_registered";
	/**
	 * The JSON key for the date the user's account was activated.
	 */
	public static final String JSON_KEY_DATE_ACTIVATED = "date_activated";

	/**
	 * The user's user-name.
	 */
	@JsonProperty(JSON_KEY_USERNAME)
	private final String username;
	/**
	 * The user's password.
	 */
	@JsonProperty(JSON_KEY_PASSWORD)
	@JacksonFieldFilter(JACKSON_FILTER_GROUP_ID)
	private final String password;
	/**
	 * The user's email address.
	 */
	@JsonProperty(JSON_KEY_EMAIL)
	@JsonSerialize(using = ToStringSerializer.class)
	private final InternetAddress emailAddress;
	/**
	 * The registration key for this account. This may be null if the account
	 * was created in some way that did not require an activation link.
	 */
	@JsonProperty(JSON_KEY_REGISTRATION_KEY)
	private final String registrationKey;
	/**
	 * The date this account was registered. This may be null if the account
	 * was never required to be activated.
	 */
	@JsonProperty(JSON_KEY_DATE_REGISTERED)
	private final Long dateRegistered;
	/**
	 * The date this account was activated. This may be null if the account was
	 * never activated either because the account did not require activation or
	 * because the user has not yet activated it.
	 */
	@JsonProperty(JSON_KEY_DATE_ACTIVATED)
	private Long dateActivated;

	/**
	 * Creates a new user.
	 * 
	 * @param username
	 *        This user's user-name.
	 * 
	 * @param password
	 *        This user's password.
	 * 
	 * @param email
	 *        The user's email address.
	 * 
	 * @param registrationKey
	 *        The unique key generated when this account was registered. This
	 *        may be null if this account was never required to be activated.
	 * 
	 * @param dateRegistered
	 *        The date and time that this account was registered. This may be
	 *        null if this account was never required to be activated.
	 * 
	 * @param dateActivated
	 *        The date and time that this account was activated. This may be
	 *        null if this account was never required to be activated or if the
	 *        owner has not yet registered it.
	 * 
	 * @throws OmhException
	 *         The user-name was invalid.
	 */
	@JsonCreator
	public User(
		@JsonProperty(JSON_KEY_USERNAME) final String username,
		@JsonProperty(JSON_KEY_PASSWORD) final String password,
		@JsonProperty(JSON_KEY_EMAIL) final String email,
		@JsonProperty(JSON_KEY_REGISTRATION_KEY) final String registrationKey,
		@JsonProperty(JSON_KEY_DATE_REGISTERED) final Long dateRegistered,
		@JsonProperty(JSON_KEY_DATE_ACTIVATED) final Long dateActivated)
		throws OmhException {
		
		// Validate and store the user-name.
		this.username = validateUsername(username);
		
		// Validate and store the password.
		this.password = validatePassword(password);
		
		// Validate and store the email address.
		this.emailAddress = validateEmail(email);
		
		// Verify that the registration key and date align.
		if(registrationKey == null) {
			if(dateRegistered != null) {
				throw
					new OmhException(
						"The account does not have a registration key but " +
							"has a registration date.");
			}
		}
		else {
			if(dateRegistered == null) {
				throw
					new OmhException(
						"The account has a registration key but does not " +
							"have a registration date.");
			}
			
			// Verify that if a registration key was given that it is not empty.
			if(registrationKey.length() == 0) {
				throw new OmhException("The registration key is empty.");
			}
		}
		
		// Verify that the account was not registered after it was activated.
		if(
			(dateRegistered != null) && 
			(dateActivated != null) && 
			dateRegistered > dateActivated) {
			
			throw
				new OmhException(
					"The account was activated before it was registered.");
		}
		
		// Store the registration and activation information.
		this.registrationKey = registrationKey;
		this.dateRegistered = dateRegistered;
		this.dateActivated = dateActivated;
	}
	
	/**
	 * Returns the user's user-name.
	 * 
	 * @return The user's user-name.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the user's hashed password.
	 * 
	 * @return The user's hashed password. 
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Returns the user's email address.
	 * 
	 * @return The user's email address.
	 */
	public InternetAddress getEmail() {
		return emailAddress;
	}
	
	/**
	 * Returns the generated key when the user was registered.
	 * 
	 * @return The generated key when the user was registered.
	 */
	public String getRegistratioKey() {
		return registrationKey;
	}
	
	/**
	 * Returns whether or not this account has been activated.
	 * 
	 * @return Whether or not this account has been activated.
	 */
	public boolean isActivated() {
		return ((registrationKey == null) || (dateActivated != null));
	}
	
	/**
	 * Returns the date and time that the account was registered or null if it
	 * was never required to be activated. If {@link #isActivated()} returns
	 * true, then this account was never required to be activated.
	 * 
	 * @return The date and time that this account was registered or null.
	 */
	public Long getDateRegistered() {
		return dateRegistered;
	}
	
	/**
	 * Returns the date and time that the account was activated or null if it
	 * was never activated. If {@link #isActivated()} returns true, then this
	 * account was never required to activate; otherwise, the user has not yet
	 * activated.
	 * 
	 * @return The date and time that this account was required to be
	 *         registered or null.
	 */
	public Long getDateActivated() {
		return dateActivated;
	}
	
	public void activate() throws OmhException {
		if(isActivated()) {
			throw new OmhException("The account has already been activated.");
		}
		
		if(registrationKey == null) {
			throw
				new OmhException(
					"This account is not elegible for activation.");
		}
		
		dateActivated = System.currentTimeMillis();
	}
	
	/**
	 * Checks if a given password matches this user's password.
	 * 
	 * @param password
	 *        The plain-text password to check.
	 * 
	 * @return True if the passwords match; false, otherwise.
	 * 
	 * @throws OmhException
	 *         The password is null.
	 */
	public boolean checkPassword(final String password) throws OmhException {
		// Validate the parameter.
		String validatedPassword = validatePassword(password);
		
		// Use BCrypt to check the password.
		return BCrypt.checkpw(validatedPassword, this.password);
	}
	
	/**
	 * Verifies that a user-name is valid.
	 * 
	 * @param username
	 *        The user-name to validate.
	 * 
	 * @return The trimmed user-name.
	 * 
	 * @throws OmhException
	 *         The user-name was invalid.
	 */
	public static String validateUsername(
		final String username)
		throws OmhException {
		
		if(username == null) {
			throw new OmhException("The username is null.");
		}
		String trimmedUsername = username.trim();
		if(trimmedUsername.length() == 0) {
			throw new OmhException("The username is empty.");
		}
		
		return trimmedUsername;
	}
	
	/**
	 * Verifies that a password is not null or empty. This can be used for both
	 * un-hashed and hashed passwords. This is not designed to hash passwords;
	 * for that, use {@link #hashPassword(String)}. This is not designed to
	 * verify that a plain-text password is the same as this user's password;
	 * for that, use {@link #checkPassword(String)}.
	 * 
	 * @param password The password to verify.
	 * 
	 * @return The password exactly as it was given.
	 * 
	 * @throws OmhException The password is null or empty.
	 */
	public static String validatePassword(
		final String password)
		throws OmhException {
		
		if(password == null) {
			throw new OmhException("The password is null.");
		}
		if(password.length() == 0) {
			throw new OmhException("The password is empty.");
		}
		
		return password;
	}
	
	/**
	 * Hashes a plain-text password.
	 * 
	 * @param plaintextPassword
	 *        The plain-text password to hash.
	 * 
	 * @return The hashed password.
	 * 
	 * @throws OmhException
	 *         The given password was invalid.
	 */
	public static String hashPassword(
		final String plaintextPassword)
		throws OmhException {
		
		String validatedPassword = validatePassword(plaintextPassword);
		
		return
			BCrypt
				.hashpw(validatedPassword, BCrypt.gensalt(BCRYPT_SALT_ROUNDS));
	}
	
	/**
	 * Verifies that an email address is a valid email address, but it does not
	 * guarantee that the location that it references exists.
	 * 
	 * @param email
	 *        The email address to be validated.
	 * 
	 * @return The email address as an InternetAddress object.
	 * 
	 * @throws OmhException
	 *         The email address is invalid.
	 */
	public static InternetAddress validateEmail(
		final String email)
		throws OmhException {

		if(email == null) {
			throw new OmhException("The email address is null");
		}
		String trimmedEmail = email.trim();
		if(trimmedEmail.length() == 0) {
			throw new OmhException("The email address is empty.");
		}
		
		// Create the InternetAddress object.
		InternetAddress result;
		try {
			result = new InternetAddress(email);
		}
		catch(AddressException e) {
			throw
				new OmhException(
					"The email address is not a valid email address.",
					e);
		}
		
		// Validate the address.
		try {
			result.validate();
		}
		catch(AddressException e) {
			throw
			new OmhException(
				"The email address is not a valid email address.",
				e);
		}
		
		return result;
	}
	
//	public static class UserJacksonFieldFilter implements JacksonFieldFilter {
//		public static final String FILTER_ID =
//			"org.openmhealth.reference.domain.User";
//		
//		/**
//		 * A private constructor to ensure that only the User class ever
//		 * instantiates this.
//		 */
//		private UserJacksonFieldFilter() {}
//
//		/*
//		 * (non-Javadoc)
//		 * @see org.openmhealth.reference.util.OmhObjectMapper.JacksonFieldFilter#getFilterId()
//		 */
//		@Override
//		public String getFilterId() {
//			return FILTER_ID;
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * @see org.openmhealth.reference.util.OmhObjectMapper.JacksonFieldFilter#getFieldNames()
//		 */
//		@Override
//		public Set<String> getFieldNames() {
//			Set<String> fieldNames = new HashSet<String>();
//			fieldNames.add(JSON_KEY_PASSWORD);
//			return fieldNames;
//		}
//	}
}