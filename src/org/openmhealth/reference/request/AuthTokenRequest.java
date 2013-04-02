package org.openmhealth.reference.request;

import org.openmhealth.reference.data.AuthTokenBin;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.AuthToken;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.InvalidAuthenticationException;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Creates a new authentication token for an existing user.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthTokenRequest extends Request {
	/**
	 * The message to return to the user if the authentication fails. This
	 * should be used in all non-system failure cases to mitigate leaking any
	 * additional information about why the authentication failed.
	 */
	private static final String MESSAGE_AUTHENTICATION_FAILURE =
		"The username does not exist or the password is incorrect.";
	
	/**
	 * The user-name of the user requesting the authentication token.
	 */
	private final String username;
	/**
	 * The password for the user requesting the authentication token.
	 */
	private final String password;
	
	/**
	 * Creates a request for an authentication token.
	 * 
	 * @param username The requesting user's user-name.
	 * 
	 * @param password The requesting user's password.
	 * 
	 * @throws OmhException The user-name or password was null.
	 */
	public AuthTokenRequest(
		final String username,
		final String password)
		throws OmhException {
		
		// There is no paging for this request, so dummy values can be given.
		super(0L, 1L);
		
		if(username == null) {
			throw new OmhException("The username is missing.");
		}
		if(password == null) {
			throw new OmhException("The password is missing.");
		}
		
		this.username = username;
		this.password = password;
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

		// Get the user.
		User user = UserBin.getUser(username);
		
		// Make sure the user exists.
		if(user == null) {
			throw
				new InvalidAuthenticationException(
					MESSAGE_AUTHENTICATION_FAILURE);
		}
		
		// Check the password.
		if(! user.checkPassword(password)) {
			throw
				new InvalidAuthenticationException(
					MESSAGE_AUTHENTICATION_FAILURE);
		}
		
		// Create the user's authentication token.
		AuthToken token = new AuthToken(user);
		
		// Save the token.
		AuthTokenBin.storeToken(token);
		
		// Return the token to the user.
		setData(token.getToken());
	}
}