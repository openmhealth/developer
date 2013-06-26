package org.openmhealth.reference.domain.mongodb;

import java.net.URI;

import org.mongojack.MongoCollection;
import org.openmhealth.reference.data.ThirdPartyBin;
import org.openmhealth.reference.domain.ThirdParty;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The MongoDB-specific variant of a {@link ThirdParty} object.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
@MongoCollection(name = ThirdPartyBin.DB_NAME)
public class MongoThirdParty extends ThirdParty implements MongoDbObject {
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
	 * Used for deserializing a MongoDB-variant of a {@link ThirdParty} entity.
	 * 
	 * @param dbId
	 * 		  The MongoDB ID for this entity.
	 * 
	 * @param owner
	 *        The ID for the user that created this third-party.
	 * 
	 * @param id
	 *        The unique ID for this third-party
	 * 
	 * @param sharedSecret
	 *        The secret that will be used to authenticate this third-party.
	 * 
	 * @param name
	 *        A user-friendly name for this third-party.
	 * 
	 * @param description
	 *        A user-friendly explanation of who this third-party is.
	 * 
	 * @param redirectUri
	 *        The URI to redirect the user back to after they have granted or
	 *        rejected this third-party's authorization request.
	 * 
	 * @throws OmhException
	 *         Any of the parameters is null or empty.
	 */
	public MongoThirdParty(
		@JsonProperty(DATABASE_FIELD_ID) final String dbId,
		@JsonProperty(JSON_KEY_OWNER) final String owner,
		@JsonProperty(JSON_KEY_ID) final String id,
		@JsonProperty(JSON_KEY_SHARED_SECRET) final String sharedSecret,
		@JsonProperty(JSON_KEY_NAME) final String name,
		@JsonProperty(JSON_KEY_DESCRIPTION) final String description,
		@JsonProperty(JSON_KEY_REDIRECT_URI) final URI redirectUri)
		throws OmhException {
		
		super(owner, id, sharedSecret, name, description, redirectUri);
		
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