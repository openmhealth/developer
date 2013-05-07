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

import java.util.UUID;

import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A user's authentication token.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthenticationToken implements OmhObject {
	/**
	 * The version of this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The JSON key for the authentication token.
	 */
	public static final String JSON_KEY_TOKEN = "token";
	/**
	 * The JSON key for the time the token was granted. 
	 */
	public static final String JSON_KEY_GRANTED = "granted";
	/**
	 * The JSON key for the time the token expires.
	 */
	public static final String JSON_KEY_EXPIRES = "expires";
	/**
	 * The default duration of the authentication token.
	 */
	public static final Long AUTH_TOKEN_LIFETIME = 1000 * 60 * 30L;
	
	/**
	 * The authentication token.
	 */
	@JsonProperty(JSON_KEY_TOKEN)
	private final String token;
	/**
	 * The user-name of the user to whom the token applies.
	 */
	@JsonProperty(User.JSON_KEY_USERNAME)
	private final String username;
	/**
	 * The number of milliseconds since the epoch at which time the token was
	 * granted.
	 */
	@JsonProperty(JSON_KEY_GRANTED)
	private final long granted;
	/**
	 * The number of milliseconds since the epoch at which time the token will
	 * expire.
	 */
	@JsonProperty(JSON_KEY_EXPIRES)
	private final long expires;

	/**
	 * Creates an {@link AuthenticationToken} object via injection from the
	 * data layer.
	 * 
	 * @param token The authentication token.
	 * 
	 * @param username The user's user-name.
	 * 
	 * @param granted The time when the token was granted.
	 * 
	 * @param expires The time when the token expires.
	 * 
	 * @throws OmhException The token and/or user-name are null.
	 */
	@JsonCreator
	public AuthenticationToken(
		@JsonProperty(JSON_KEY_TOKEN) final String token,
		@JsonProperty(User.JSON_KEY_USERNAME) final String username,
		@JsonProperty(JSON_KEY_GRANTED) final long granted,
		@JsonProperty(JSON_KEY_EXPIRES) final long expires) 
		throws OmhException {
		
		if(token == null) {
			throw new OmhException("The authentication token is null.");
		}
		if(username == null) {
			throw new OmhException("The user-name is null.");
		}
		if(granted > System.currentTimeMillis()) {
			throw
				new OmhException(
					"An authentication token cannot be granted in the " +
						"future.");
		}
		if(granted > expires) {
			throw
				new OmhException(
					"A token cannot expire before it was granted.");
		}
		
		this.token = token;
		this.username = username;
		this.granted = granted;
		this.expires = expires;
	}
	
	/**
	 * Creates a new authentication token for a user.
	 * 
	 * @param user The authentication token for a user.
	 * 
	 * @throws OmhException The user is null.
	 */
	public AuthenticationToken(final User user) throws OmhException {
		if(user == null) {
			throw new OmhException("The user is null.");
		}
		
		token = UUID.randomUUID().toString();
		username = user.getUsername();
		granted = System.currentTimeMillis();
		expires = granted + AUTH_TOKEN_LIFETIME;
	}
	
	/**
	 * Retrieves the authentication token.
	 * 
	 * @return The authentication token.
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * Returns the user-name of the user associated with this authentication
	 * token.
	 * 
	 * @return The user-name of the user associated with this authentication
	 *         token.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns the user associated with this authentication token.
	 * 
	 * @return The user associated with this authentication token.
	 * 
	 * @throws OmhException
	 *         There is an internal error or the user associated with this
	 *         token no longer exists.
	 */
	public User getUser() throws OmhException {
		// Attempt to get the user.
		User user = UserBin.getInstance().getUser(username);
		
		// If the user no longer exists, throw an exception.
		if(user == null) {
			throw
				new OmhException(
					"The user that is associated with this token no longer " +
						"exists.");
		}
		
		// Return the user.
		return user; 
	}
	
	/**
	 * Returns the number of milliseconds since the epoch when this token was
	 * granted.
	 * 
	 * @return The number of milliseconds since the epoch when this token was
	 *         granted.
	 */
	public long getGranted() {
		return granted;
	}

	/**
	 * Returns the number of milliseconds since the epoch when this token
	 * (will) expire(d).
	 * 
	 * @return The number of milliseconds since the epoch when this token
	 * 		   (will) expire(d).
	 */
	public long getExpires() {
		return expires;
	}
}