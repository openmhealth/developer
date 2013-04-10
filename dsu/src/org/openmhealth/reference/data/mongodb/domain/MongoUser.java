package org.openmhealth.reference.data.mongodb.domain;

import org.mongojack.MongoCollection;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A MongoDB extension of the {@link User} type.
 * </p>
 *
 * @author John Jenkins
 */
@MongoCollection(name = UserBin.DB_NAME_USER_BIN)
public class MongoUser extends User implements MongoDbObject {
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
	 * Creates a new user.
	 * 
	 * @param username This user's username.
	 * 
	 * @throws OmhException The username was invalid.
	 */
	@JsonCreator
	public MongoUser(
		@JsonProperty(DATABASE_FIELD_ID) final String dbId,
		@JsonProperty(JSON_KEY_USERNAME) final String username,
		@JsonProperty(JSON_KEY_PASSWORD) final String password)
		throws OmhException {
		
		super(username, password);
		
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