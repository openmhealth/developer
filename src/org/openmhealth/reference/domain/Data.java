package org.openmhealth.reference.domain;

import org.mongojack.MongoCollection;
import org.openmhealth.reference.data.DataSet;
import org.openmhealth.reference.data.MongoDbObject;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * A data point as defined by the Open mHealth specification.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 * 
 * @author John Jenkins
 */
@MongoCollection(name = DataSet.DATA_DB_NAME)
public class Data extends MongoDbObject {
	/**
	 * <p>
	 * The JSON key for the identifier of a user that owns this data.
	 * </p>
	 * 
	 * <p>
	 * This probably belongs in some sort of "User" class.
	 * </p>
	 */
	public static final String JSON_KEY_OWNER = "owner";
	
	/**
	 * The JSON key for the meta-data.
	 */
	public static final String JSON_KEY_METADATA = "metadata";
	/**
	 * The JSON key for the data.
	 */
	public static final String JSON_KEY_DATA = "data";
	
	/**
	 * The identifier for the user that owns this data.
	 */
	@JsonProperty(JSON_KEY_OWNER)
	private final String owner;

	/**
	 * The schema for this node. This is used when creating the object but may
	 * be null if deserialized.
	 */
	@JsonIgnore
	private final Schema schema;
	/**
	 * The unique identifier for the schema that validated this data. This
	 * should always exist, even if the schema is null.
	 */
	@JsonProperty(Schema.JSON_KEY_ID)
	private final String schemaId;
	/**
	 * The version of the schema that validated this data. This should always
	 * exist, even if the schema is null.
	 */
	@JsonProperty(Schema.JSON_KEY_VERSION)
	private final long schemaVersion;
	
	/**
	 * The meta-data for this point.
	 */
	@JsonProperty(JSON_KEY_METADATA)
	private final MetaData metaData;
	/**
	 * The data for this point.
	 */
	@JsonProperty(JSON_KEY_DATA)
	private final JsonNode data;

	/**
	 * Creates a new data object.
	 * 
	 * @param owner
	 * 		  The identifier for the user that owns the data.
	 * 
	 * @param schema
	 *        The schema to which this data must conform.
	 * 
	 * @param metaData
	 *        The meta-data for this data.
	 * 
	 * @param data
	 *        The data.
	 * 
	 * @throws OmhException
	 *         Any of the parameters is null.
	 */
	public Data(
		final String owner,
		final Schema schema,
		final MetaData metaData,
		final JsonNode data)
		throws OmhException {

		if(owner == null) {
			throw new OmhException("The owner is null.");
		}
		if(schema == null) {
			throw new OmhException("The schema is null.");
		}
		if(data == null) {
			throw new OmhException("The data is null.");
		}

		this.owner = owner;
		
		this.schema = schema;
		schemaId = schema.getId();
		schemaVersion = schema.getVersion();
		
		this.metaData = metaData;
		this.data = data;
	}

	/**
	 * Creates a new data object. This should only be used by serialization
	 * methods when they are pulling already-validated data from the database.
	 * 
	 * @param owner
	 * 		  The identifier for the user that owns the data.
	 * 
	 * @param schemaId
	 * 		  The ID of the schema that was used to validate this data.
	 * 
	 * @param schemaVersion
	 * 		  The version of the schema that was used to validate this data.
	 * 
	 * @param metaData
	 *        The meta-data for this data.
	 * 
	 * @param data
	 *        The data.
	 * 
	 * @throws OmhException
	 *         Any of the parameters is null.
	 */
	@JsonCreator
	private Data(
		@JsonProperty(JSON_KEY_OWNER)
		final String owner,
		@JsonProperty(Schema.JSON_KEY_ID)
		final String schemaId,
		@JsonProperty(Schema.JSON_KEY_VERSION)
		final long schemaVersion,
		@JsonProperty(JSON_KEY_METADATA)
		final MetaData metaData,
		@JsonProperty(JSON_KEY_DATA)
		final JsonNode data)
		throws OmhException {

		if(owner == null) {
			throw new OmhException("The owner is null.");
		}
		if(schemaId == null) {
			throw new OmhException("The schema ID is null.");
		}
		if(data == null) {
			throw new OmhException("The data is null.");
		}

		this.owner = owner;
		
		schema = null;
		this.schemaId = schemaId;
		this.schemaVersion = schemaVersion;
		
		this.metaData = metaData;
		this.data = data;
	}
}