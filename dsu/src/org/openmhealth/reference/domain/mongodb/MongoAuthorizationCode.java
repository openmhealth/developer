package org.openmhealth.reference.domain.mongodb;

import java.util.Set;

import org.openmhealth.reference.domain.AuthorizationCode;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The MongoDB-specific variant of an {@link AuthorizationCode} object.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoAuthorizationCode
	extends AuthorizationCode
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
	 * Used for deserializing a MongoDB-variant of an {@link AuthorizationCode}
	 * entity.
	 * 
	 * @param dbId
	 *        The MongoDB ID for this entity.
	 * 
	 * @param thirdParty
	 *        The unique identifier for the third-party to which this token
	 *        pertains.
	 * 
	 * @param code
	 *        The code value for this authorization token.
	 * 
	 * @param creationTime
	 *        The number of milliseconds since the epoch at which time this
	 *        token was created.
	 * 
	 * @param expirationTime
	 *        The number of milliseconds since the epoch at which time this
	 *        token expires.
	 * 
	 * @param scopes
	 *        The set of scopes for this token. These are generally the schema
	 *        IDs.
	 * 
	 * @param state
	 *        The state given by the third-party when creating requesting this
	 *        authorization code.
	 * 
	 * @throws OmhException
	 *         A parameter is invalid.
	 */
	public MongoAuthorizationCode(
		@JsonProperty(DATABASE_FIELD_ID) final String dbId,
		@JsonProperty(JSON_KEY_THIRD_PARTY) final String thirdParty,
		@JsonProperty(JSON_KEY_CODE) final String code,
		@JsonProperty(JSON_KEY_CREATION_TIME) final long creationTime,
		@JsonProperty(JSON_KEY_EXPIRATION_TIME) final long expirationTime,
		@JsonProperty(JSON_KEY_SCOPES) final Set<String> scopes,
		@JsonProperty(JSON_KEY_STATE) final String state)
		throws OmhException {
		
		super(thirdParty, code, creationTime, expirationTime, scopes, state);
		
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
	 * @see org.openmhealth.reference.data.mongodb.MongoDbObject#getDatabaseId()
	 */
	@Override
	public String getDatabaseId() {
		return dbId;
	}
}