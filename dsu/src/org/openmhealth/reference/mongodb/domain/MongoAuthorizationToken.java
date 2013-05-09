package org.openmhealth.reference.mongodb.domain;

import org.openmhealth.reference.domain.AuthorizationToken;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The MongoDB-specific variant of an {@link AuthorizationToken} object.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoAuthorizationToken
	extends AuthorizationToken
	implements MongoDbObject {
	
	/**
	 * The ID for this class which is used for serialization. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The database ID for this object.
	 */
	@JsonIgnore
	private final String dbId;

	/**
	 * Used for deserializing a MongoDB-variant of an
	 * {@link AuthorizationToken} entity.
	 * 
	 * @param dbId
	 *        The MongoDB ID for this entity.
	 * 
	 * @param authorizationCode
	 *        The authorization code that backs this token.
	 * 
	 * @param accessToken
	 *        The access token for this authorization token.
	 * 
	 * @param refreshToken
	 *        The refresh token for this authorization token.
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
	 */
	public MongoAuthorizationToken(
		@JsonProperty(DATABASE_FIELD_ID) final String dbId,
		@JsonProperty(JSON_KEY_AUTHORIZATION_CODE)
			final String authorizationCode,
		@JsonProperty(JSON_KEY_ACCESS_TOKEN) final String accessToken,
		@JsonProperty(JSON_KEY_REFRESH_TOKEN) final String refreshToken,
		@JsonProperty(JSON_KEY_CREATION_TIME) final long creationTime,
		@JsonProperty(JSON_KEY_EXPIRATION_TIME) final long expirationTime)
		throws OmhException {
		
		super(
			authorizationCode,
			accessToken,
			refreshToken,
			creationTime,
			expirationTime);
		
		// Store the MongoDB ID.
		if(dbId == null) {
			throw new OmhException("The MongoDB ID is missing.");
		}
		else {
			this.dbId = dbId;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.mongodb.data.MongoDbObject#getDatabaseId()
	 */
	@Override
	public String getDatabaseId() {
		return dbId;
	}
}