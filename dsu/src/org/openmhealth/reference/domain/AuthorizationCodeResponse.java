package org.openmhealth.reference.domain;

import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The link between an authorization code, a user, and whether or not the
 * authorization was granted.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthorizationCodeResponse implements OmhObject {
	/**
	 * The version of this class used for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The JSON key for the authorization code.
	 */
	public static final String JSON_KEY_AUTHORIZATION_CODE = "code";
	/**
	 * The JSON key for the owner.
	 */
	public static final String JSON_KEY_OWNER = "owner";
	/**
	 * The JSON key for the granted value.
	 */
	public static final String JSON_KEY_GRANTED = "granted";
	
	/**
	 * The authorization code to which this key applies.
	 */
	@JsonProperty(JSON_KEY_AUTHORIZATION_CODE)
	private final String authorizationCode;
	/**
	 * The ID for the user that granted or rejected the authorization.
	 */
	@JsonProperty(JSON_KEY_OWNER)
	private final String owner;
	/**
	 * Whether or not the authorization was granted.
	 */
	@JsonProperty(JSON_KEY_GRANTED)
	private final boolean granted;
	
	/**
	 * Creates a new authorization code response based on an authorization
	 * code, a user, and whether or not it was granted.
	 * 
	 * @param authorizationCode
	 *        The authorization code.
	 * 
	 * @param user
	 *        The user granting or rejecting the authorization.
	 * 
	 * @param granted
	 *        Whether or not the authorization was granted or rejected.
	 * 
	 * @throws OmhException
	 *         A parameter was invalid.
	 */
	public AuthorizationCodeResponse(
		final AuthorizationCode authorizationCode,
		final User user,
		final boolean granted)
		throws OmhException {
		
		// Verify the authorization code.
		if(authorizationCode == null) {
			throw new OmhException("The authorization code is null.");
		}
		else {
			this.authorizationCode = authorizationCode.getCode();
		}
		
		// Verify the owner.
		if(user == null) {
			throw new OmhException("The user is null.");
		}
		else {
			this.owner = user.getUsername();
		}
		
		// Store the granted value.
		this.granted = granted;
	}
	
	/**
	 * Creates an authorization code response presumably from an existing one
	 * since all of the fields are given. To create a new response, it is
	 * recommended that
	 * {@link #AuthorizationCodeResponse(AuthorizationCode, User, boolean)}
	 * be used.
	 * 
	 * @param authorizationCode
	 *        The authorization code to which this response applies.
	 * 
	 * @param owner
	 *        The ID for the user that granted or rejected this authorization
	 *        code.
	 * 
	 * @param granted
	 *        Whether or not the authorization is granted.
	 * 
	 * @throws OmhException
	 *         A parameter is invalid.
	 *         
	 * @see #AuthorizationCodeResponse(AuthorizationCode, User, boolean)
	 */
	@JsonCreator
	public AuthorizationCodeResponse(
		@JsonProperty(JSON_KEY_AUTHORIZATION_CODE)
			final String authorizationCode,
		@JsonProperty(JSON_KEY_OWNER) final String owner,
		@JsonProperty(JSON_KEY_GRANTED) final boolean granted)
		throws OmhException {
		
		// Verify the authorization code.
		if(authorizationCode == null) {
			throw new OmhException("The authorization code is null.");
		}
		else {
			this.authorizationCode = authorizationCode;
		}
		
		// Verify the owner.
		if(owner == null) {
			throw new OmhException("The owner is null.");
		}
		else {
			this.owner = owner;
		}
		
		// Store the granted value.
		this.granted = granted;
	}
	
	/**
	 * Returns the username of the owner of this response.
	 * 
	 * @return The username of the owner of this response.
	 */
	public String getOwnerUsername() {
		return owner;
	}
	
	/**
	 * Returns the user that generated the response.
	 * 
	 * @return The user that generated the response.
	 */
	public User getOwner() {
		return UserBin.getInstance().getUser(owner);
	}
	
	/**
	 * Returns the authorization code to which this response applies.
	 * 
	 * @return The authorization code to which this response applies.
	 */
	public String getAuthorizationCode() {
		return authorizationCode;
	}
	
	/**
	 * Returns whether or not the authorization was granted.
	 * 
	 * @return Whether or not the authorization was granted.
	 */
	public boolean getGranted() {
		return granted;
	}
}