package org.openmhealth.reference.domain;

import java.util.UUID;

import org.openmhealth.reference.data.MongoDbObject;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthToken extends MongoDbObject implements OmhObject {
	/**
	 * The version of this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The database token 
	 */
	public static final String DB_KEY_TOKEN = "token";
	public static final String DB_KEY_GRANTED = "granted";
	public static final String DB_KEY_EXPIRES = "expires";
	public static final Long AUTH_TOKEN_LIFETIME = 1000 * 60 * 30L;
	
	private final String token;
	private final String username;
	private final long granted;
	private final long expires;
	
	/**
	 * Creates a new authentication token for a user.
	 * 
	 * @param user The authentication token for a user.
	 * 
	 * @throws OmhException The user is null.
	 */
	public AuthToken(final User user) throws OmhException {
		if(user == null) {
			throw new OmhException("The user is null.");
		}
		
		token = UUID.randomUUID().toString();
		username = user.getUsername();
		granted = System.currentTimeMillis();
		expires = granted + AUTH_TOKEN_LIFETIME;
	}

	/**
	 * Creates an {@link AuthToken} object via a static build.
	 * 
	 * @param token The authentication token.
	 * 
	 * @param username The user's user-name.
	 * 
	 * @param granted The time when the token was granted.
	 * 
	 * @param expires The time when the token expires. 
	 */
	@JsonCreator
	private AuthToken(
		@JsonProperty(DB_KEY_TOKEN) final String token,
		@JsonProperty(User.JSON_KEY_USERNAME) final String username,
		@JsonProperty(DB_KEY_GRANTED) final long granted,
		@JsonProperty(DB_KEY_EXPIRES) final long expires) 
		throws OmhException {
		
		if(token == null) {
			throw new OmhException("The authentication token is null.");
		}
		if(username == null) {
			throw new OmhException("The user-name is null.");
		}
		
		this.token = token;
		this.username = username;
		this.granted = granted;
		this.expires = expires;
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