package org.openmhealth.reference.data.mongodb.domain;

import org.mongojack.MongoCollection;
import org.openmhealth.reference.data.AuthTokenBin;
import org.openmhealth.reference.data.mongodb.MongoDbObject;
import org.openmhealth.reference.domain.AuthToken;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A MongoDB extension of the {@link AuthToken} type.
 * </p>
 *
 * @author John Jenkins
 */
@MongoCollection(name = AuthTokenBin.AUTH_TOKEN_BIN_DB_NAME)
public class MongoAuthToken extends AuthToken implements MongoDbObject {
	/**
	 * The ID for this class which is used for serialization. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The database ID for this object.
	 */
	@JsonIgnore
	private final String id;

	/**
	 * Creates an {@link AuthToken} object via injection from the data layer.
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
	private MongoAuthToken(
		@JsonProperty(DATABASE_FIELD_ID) final String dbId, 
		@JsonProperty(JSON_KEY_TOKEN) final String token,
		@JsonProperty(User.JSON_KEY_USERNAME) final String username,
		@JsonProperty(JSON_KEY_GRANTED) final long granted,
		@JsonProperty(JSON_KEY_EXPIRES) final long expires) 
		throws OmhException {
		
		super(token, username, granted, expires);
		
		// Store the MongoDB ID.
		if(dbId == null) {
			throw new OmhException("The MongoDB ID is missing.");
		}
		else {
			this.id = dbId;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.mongodb.MongoDbObject#getDatabaseId()
	 */
	@Override
	public String getDatabaseId() {
		return id;
	}
}