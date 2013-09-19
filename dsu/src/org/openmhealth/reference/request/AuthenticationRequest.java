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
package org.openmhealth.reference.request;

import org.openmhealth.reference.data.AuthenticationTokenBin;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.AuthenticationToken;
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
public class AuthenticationRequest extends Request<AuthenticationToken> {
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
	public AuthenticationRequest(
		final String username,
		final String password)
		throws OmhException {
		
		// Validate the username.
		if(username == null) {
			throw new OmhException("The username is missing.");
		}
		else {
			this.username = username;
		}
		
		// Validate the password.
		if(password == null) {
			throw new OmhException("The password is missing.");
		}
		else {
			this.password = password;
		}
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
		User user = getUser(username, password);
		
		// Verify that the account is active.
		if(! user.isActivated()) {
			throw new OmhException("The account has not been activated.");
		}
		
		// Create the user's authentication token.
		AuthenticationToken token = new AuthenticationToken(user);
		
		// Save the token.
		AuthenticationTokenBin.getInstance().storeToken(token);
		
		// Return the token to the user.
		setData(token);
	}
	
	/**
	 * Retrieves a {@link User} object from the given credentials if one exists
	 * or throws an exception if they are invalid.
	 * 
	 * @param username
	 *        The user's username.
	 * 
	 * @param password
	 *        The user's password.
	 * 
	 * @return The authenticated User object.
	 * 
	 * @throws OmhException
	 *         The username was unknown or the password was incorrect.
	 */
	public static User getUser(
		final String username,
		final String password) 
		throws OmhException {
		
		// Get the user.
		User user = UserBin.getInstance().getUser(username);
		
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
		
		return user;
	}
}