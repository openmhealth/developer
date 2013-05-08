package org.openmhealth.reference.domain;

import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.openmhealth.reference.data.AuthorizationCodeBin;
import org.openmhealth.reference.data.AuthorizationCodeVerificationBin;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The authorization token for a user to grant access to some scope (e.g. 
 * schema IDs) to some third-party.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthorizationToken implements OmhObject {
	/**
	 * The version of this class used for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The default number of milliseconds that a token should live.
	 */
	public static final long DEFAULT_TOKEN_LIFETIME_MILLIS = 1000 * 60 * 60;

	/**
	 * The JSON key for the authorization code that backs this authorization
	 * token.
	 */
	public static final String JSON_KEY_AUTHORIZATION_CODE = 
		"authorization_code";

	/**
	 * The JSON key for the access token.
	 */
	public static final String JSON_KEY_ACCESS_TOKEN = "access_token";
	/**
	 * The JSON key for the refresh token.
	 */
	public static final String JSON_KEY_REFRESH_TOKEN = "refresh_token";
	/**
	 * The JSON key for the time the token was created.
	 */
	public static final String JSON_KEY_CREATION_TIME = "creation_time";
	/**
	 * The JSON key for the time the token expires.
	 */
	public static final String JSON_KEY_EXPIRATION_TIME = "expiration_time";
	
	/**
	 * The authorization code that backs this authorization token. When
	 * referencing or refreshing this token, this code and its corresponding
	 * verification should be consulted to determine the scope and whether or
	 * not this authorization has been revoked.
	 */
	@JsonProperty(JSON_KEY_AUTHORIZATION_CODE)
	private final String authorizationCode;
	/**
	 * The access token.
	 */
	@JsonProperty(JSON_KEY_ACCESS_TOKEN)
	private final String accessToken;
	/**
	 * The refresh token.
	 */
	@JsonProperty(JSON_KEY_REFRESH_TOKEN)
	private final String refreshToken;
	/**
	 * The number of milliseconds since the epoch at which time this token was
	 * created.
	 */
	@JsonProperty(JSON_KEY_CREATION_TIME)
	private final long creationTime;
	/**
	 * The number of milliseconds since the epoch at which time this token
	 * expires.
	 */
	@JsonProperty(JSON_KEY_EXPIRATION_TIME)
	private final long expirationTime;
	
	/**
	 * Creates a new authorization token.
	 * 
	 * @param verification
	 *        The authorization code verification that will be associated with
	 *        this token and all subsequent tokens that are created by
	 *        refreshing this and those tokens.
	 * 
	 * @throws OmhException
	 *         A parameter is invalid.
	 */
	public AuthorizationToken(
		final AuthorizationCodeVerification verification) 
		throws OmhException {
		
		// Validate the parameters.
		if(verification == null) {
			throw new OmhException("The verification is null.");
		}
		else if(! verification.getGranted()) {
			throw new OmhException(
				"An authorization token cannot be created for an " +
					"authorization code that was denied.");
		}
		
		// Store the relevant information.
		this.authorizationCode = verification.getAuthorizationCode();
		this.accessToken = UUID.randomUUID().toString();
		this.refreshToken = UUID.randomUUID().toString();
		this.creationTime = DateTime.now().getMillis();
		this.expirationTime =
			this.creationTime + DEFAULT_TOKEN_LIFETIME_MILLIS;
	}
	
	/**
	 * Creates a new authentication token with new access and refresh tokens
	 * and new creation and expiration times. This is not a copy constructor.
	 * This is designed for creating new tokens via a refresh.
	 * 
	 * @param oldToken
	 *        The old token that will be used to create the new token.
	 * 
	 * @throws OmhException
	 *         The old token was null or already invalidated.
	 */
	public AuthorizationToken(
		final AuthorizationToken oldToken)
		throws OmhException {
		
		// Validate the token.
		if(oldToken == null) {
			throw new OmhException("The token is null.");
		}

		// Store the relevant information.
		this.authorizationCode = oldToken.authorizationCode;
		this.accessToken = UUID.randomUUID().toString();
		this.refreshToken = UUID.randomUUID().toString();
		this.creationTime = DateTime.now().getMillis();
		this.expirationTime =
			this.creationTime + DEFAULT_TOKEN_LIFETIME_MILLIS;
	}
	
	/**
	 * Creates an authorization token presumably from an existing one since all
	 * of the fields are given. To create a new token, it is recommended that
	 * {@link #AuthorizationToken(User, ThirdParty, Set)} be used.
	 * 
	 * @param owner
	 *        The unique identifier for the user to whom this token pertains.
	 * 
	 * @param thirdParty
	 *        The unique identifier for the third-party to which this token
	 *        pertains.
	 * 
	 * @param authorizationCode
	 *        The unique identifier for the authorization code that backs this
	 *        token.
	 * 
	 * @param accessToken
	 *        The access token value for this authorization token.
	 * 
	 * @param refreshToken
	 *        The refresh token value for this authorization token.
	 * 
	 * @param creationTime
	 *        The number of milliseconds since the epoch at which time this
	 *        token was created.
	 * 
	 * @param expirationTime
	 *        The number of milliseconds since the epoch at which time this
	 *        token expires.
	 * 
	 * @throws OmhException
	 *         A parameter is invalid.
	 * 
	 * @see #AuthorizationToken(User, ThirdParty, Set)
	 */
	@JsonCreator
	protected AuthorizationToken(
		@JsonProperty(JSON_KEY_AUTHORIZATION_CODE)
			final String authorizationCode,
		@JsonProperty(JSON_KEY_ACCESS_TOKEN) final String accessToken,
		@JsonProperty(JSON_KEY_REFRESH_TOKEN) final String refreshToken,
		@JsonProperty(JSON_KEY_CREATION_TIME) final long creationTime,
		@JsonProperty(JSON_KEY_EXPIRATION_TIME) final long expirationTime)
		throws OmhException {
		
		// Validate the authorization code.
		if(authorizationCode == null) {
			throw new OmhException("The authorization code is null.");
		}
		else {
			this.authorizationCode = authorizationCode;
		}
		
		// Validate the access token.
		if(accessToken == null) {
			throw new OmhException("The access token is null.");
		}
		else {
			this.accessToken = accessToken;
		}
		
		// Validate the refresh token.
		if(refreshToken == null) {
			throw new OmhException("The refresh token is null.");
		}
		else {
			this.refreshToken = refreshToken;
		}
		
		// Validate the creation time.
		DateTime creationTimeDateTime = new DateTime(creationTime);
		if(creationTimeDateTime.isAfterNow()) {
			throw
				new OmhException(
					"The token's creation time cannot be in the future.");
		}
		else {
			this.creationTime = creationTime;
		}
		
		// Validate the expiration time.
		if(creationTimeDateTime.isAfter(expirationTime)) {
			throw
				new OmhException(
					"The token's expiration time cannot be before its " +
						"creation time.");
		}
		else {
			this.expirationTime = expirationTime;
		}
	}
	
	/**
	 * Returns the authorization code verification that backs this
	 * authorization token.
	 * 
	 * @return The authorization code verification that backs this
	 *         authorization token.
	 */
	public AuthorizationCodeVerification getAuthorizationCodeVerification() {
		return
			AuthorizationCodeVerificationBin
				.getInstance().getVerification(authorizationCode);
	}
	
	/**
	 * Returns the third-party associated with this authorization token.
	 * 
	 * @return The third-party associated with this authorization token. 
	 */
	public ThirdParty getThirdParty() {
		return
			AuthorizationCodeBin
				.getInstance().getCode(authorizationCode).getThirdParty();
	}
	
	/**
	 * Returns the access token.
	 * 
	 * @return The access token.
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * Returns the refresh token.
	 * 
	 * @return The refresh token.
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	
	/**
	 * Returns the number of milliseconds before the access token expires.
	 * 
	 * @return The number of milliseconds before the access token expires. This
	 *         may be negative if the token has already expired.
	 */
	public long getExpirationIn() {
		return expirationTime - DateTime.now().getMillis();
	}
}