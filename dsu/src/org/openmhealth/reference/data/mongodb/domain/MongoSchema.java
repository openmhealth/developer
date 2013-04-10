package org.openmhealth.reference.data.mongodb.domain;

import name.jenkins.paul.john.concordia.validator.ValidationController;

import org.mongojack.MongoCollection;
import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * A MongoDB extension of the {@link Schema} type.
 * </p>
 *
 * @author John Jenkins
 */
@MongoCollection(name = Registry.REGISTRY_DB_NAME)
public class MongoSchema extends Schema implements MongoDbObject {
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
	 * Creates a new schema (registry entry).
	 * 
	 * @param id
	 *        The ID for this schema.
	 * 
	 * @param version
	 *        The version of this schema.
	 * 
	 * @param chunkSize
	 *        The maximum number of data points that may be returned for this
	 *        schema in a single request.
	 * 
	 * @param timeAuthoritative
	 *        Whether or not the time in the meta-data for points refers to an
	 *        actual time or a context-specific one.
	 * 
	 * @param timeZoneAuthoritative
	 *        If the time is authoritative, this indicates whether or not the
	 *        time-zone associated with that time represents a valid time-zone.
	 * 
	 * @param schema
	 *        The specific schema.
	 * 
	 * @throws OmhException
	 *         A parameter was invalid.
	 */
	@JsonCreator
	public MongoSchema(
		@JsonProperty(DATABASE_FIELD_ID) final String dbId,
		@JsonProperty(JSON_KEY_ID) final String id,
		@JsonProperty(JSON_KEY_VERSION) final long version,
		@JsonProperty(JSON_KEY_CHUNK_SIZE) final long chunkSize,
		@JsonProperty(JSON_KEY_TIME_AUTHORITATIVE)
			final boolean timeAuthoritative,
		@JsonProperty(JSON_KEY_TIME_ZONE_AUTHORITATIVE) 
			final boolean timeZoneAuthoritative,
		@JsonProperty(JSON_KEY_SCHEMA) final JsonNode schema,
		@JacksonInject(JSON_KEY_VALIDATION_CONTROLLER)
			final ValidationController controller)
		throws OmhException {

		super(
			id,
			version,
			chunkSize,
			timeAuthoritative,
			timeZoneAuthoritative,
			schema,
			controller);
		
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