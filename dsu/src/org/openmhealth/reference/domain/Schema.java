package org.openmhealth.reference.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import name.jenkins.paul.john.concordia.Concordia;
import name.jenkins.paul.john.concordia.exception.ConcordiaException;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * A schema as defined by the Open mHealth specification.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 * 
 * @author John Jenkins
 */
public class Schema implements OmhObject {
	/**
	 * The version of this class for serialization purposes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The JSON key for the ID of a schema.
	 */
	public static final String JSON_KEY_ID = "schema_id";
	/**
	 * The JSON key for the version of the schema.
	 */
	public static final String JSON_KEY_VERSION = "schema_version";
	/**
	 * The JSON key for the maximum chunk size.
	 */
	public static final String JSON_KEY_CHUNK_SIZE = "chunk_size";
	/**
	 * The JSON key for authoritativeness of the time.
	 */
	public static final String JSON_KEY_TIME_AUTHORITATIVE =
		"time_authoritative";
	/**
	 * The JSON key for the authoritativeness of the time-zone.
	 */
	public static final String JSON_KEY_TIME_ZONE_AUTHORITATIVE =
		"time_zone_authoritative";
	/**
	 * The JSON key for the Concordia schema.
	 */
	public static final String JSON_KEY_SCHEMA = "schema";
	/**
	 * The JSON key for the {@link ValidationController} that will be used to
	 * build the underlying {@link Concordia} object. If not set or not given,
	 * the default one, {@link ValidationController#BASIC_CONTROLLER} will be
	 * used.
	 */
	public static final String JSON_KEY_VALIDATION_CONTROLLER =
		"validation_controller";

	/**
	 * The schema's ID.
	 */
	@JsonProperty(JSON_KEY_ID)
	private final String id;
	/**
	 * The schema's version.
	 */
	@JsonProperty(JSON_KEY_VERSION)
	private final long version;
	/**
	 * The largest number of records that can be read in a single request.
	 */
	@JsonProperty(JSON_KEY_CHUNK_SIZE)
	private final long chunkSize;
	/**
	 * Whether or not the given time-stamp represents a real time or an
	 * artificial one.
	 */
	@JsonProperty(JSON_KEY_TIME_AUTHORITATIVE)
	private final boolean timeAuthoritative;
	/**
	 * Whether or not the given time-zone represents a real time-zone or a
	 * generated one.
	 */
	@JsonProperty(JSON_KEY_TIME_ZONE_AUTHORITATIVE)
	private final boolean timeZoneAuthoritative;
	/**
	 * The actual schema for this {@link Schema} object.
	 */
	@JsonProperty(JSON_KEY_SCHEMA)
	private final Concordia schema;

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
	public Schema(
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

		// Validate the ID.
		if(id == null) {
			throw new OmhException("The ID is null.");
		}
		else if(id.trim().length() == 0) {
			throw new OmhException("The ID is empty.");
		}
		else {
			this.id = validateId(id);
		}

		// Validate the version.
		this.version = validateVersion(version);

		// Validate the chunk size.
		this.chunkSize = validateChunkSize(chunkSize);

		this.timeAuthoritative = timeAuthoritative;
		this.timeZoneAuthoritative = timeZoneAuthoritative;
		try {
			this.schema =
				new Concordia(
					new ByteArrayInputStream(schema.toString().getBytes()),
					controller);
		}
		catch(IllegalArgumentException e) {
			throw new OmhException("The schema is missing.", e);
		}
		catch(ConcordiaException e) {
			throw new OmhException("The schema is invalid.", e);
		}
		catch(IOException e) {
			throw new OmhException("The schema cannot be read.", e);
		}
	}

	/**
	 * Returns the unique identifier for this schema.
	 * 
	 * @return The unique identifier for this schema.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the version of this schema.
	 * 
	 * @return The version of this schema.
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * Returns the schema.
	 * 
	 * @return The schema.
	 */
	public Concordia getSchema() {
		return schema;
	}

	/**
	 * Validates some data.
	 * 
	 * @param owner
	 *        The owner of the data that is being validated. This is needed to
	 *        build the {@link Data} object.
	 * 
	 * @param metaData
	 *        The meta-data for the data that is being validated. This is
	 *        needed to build the {@link Data} object.
	 * 
	 * @param data
	 *        The data to be validated.
	 * 
	 * @return The validated data, which is the same as it was given.
	 * 
	 * @throws OmhException
	 *         The data was null or invalid.
	 */
	public Data validateData(
		final String owner,
		final MetaData metaData,
		final JsonNode data)
		throws OmhException {

		// Ensure the data is not null.
		if(data == null) {
			throw new OmhException("The data is null.");
		}
		
		// Validate the data.
		try {
			schema.validateData(data);
		}
		catch(ConcordiaException e) {
			throw new OmhException("The data is invalid.", e);
		}
		
		// Return the result.
		return new Data(owner, this, metaData, data);
	}

	/**
	 * Validates that the ID follows our rules.
	 * 
	 * @param id
	 *        The ID to be validated.
	 * 
	 * @return The validated and, potentially, simplified schema, e.g. trimmed.
	 * 
	 * @throws OmhException
	 *         The ID is invalid.
	 */
	public static String validateId(final String id) throws OmhException {
		// Currently, there are no rules for schema IDs.
		return id.trim();
	}

	/**
	 * Validates that the version follows our rules.
	 * 
	 * @param version
	 *        The version to be validated.
	 * 
	 * @return The version as it was given.
	 * 
	 * @throws OmhException
	 *         The version is invalid.
	 */
	public static long validateVersion(
		final long version)
		throws OmhException {

		// The version must be positive.
		if(version <= 0) {
			throw new OmhException("The version must be positive.");
		}

		return version;
	}

	/**
	 * Validates that the chunk size follows our rules.
	 * 
	 * @param chunkSize
	 *        The chunk size to validate.
	 * 
	 * @return The chunk size as it was given.
	 * 
	 * @throws OmhException
	 *         The chunk size is invalid.
	 */
	public static long validateChunkSize(
		final long chunkSize)
		throws OmhException {

		// The chunk size must be positive.
		if(chunkSize <= 0) {
			throw new OmhException("The chunk size must be positive.");
		}

		return chunkSize;
	}
}