package org.openmhealth.reference.request;

import java.util.Map;

import org.openmhealth.reference.data.MultiValueResult;
import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.exception.NoSuchSchemaException;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * Retrieves schemas based on the given parameters. This is analogous to the
 * registry.
 * </p>
 *
 * @author John Jenkins
 */
public class SchemaRequest extends Request {
	/**
	 * The specific schema ID being requested or null if no specific schema ID
	 * is being requested.
	 */
	private final String schemaId;
	/**
	 * The specific schema version being requested or null if no specific
	 * schema version is being requested.
	 */
	private final Long schemaVersion;

	/**
	 * Creates a new request for a schema.
	 * 
	 * @param schemaId
	 *        Indicates that only the schemas for the different versions of
	 *        this schema ID should be returned. This may be null indicating
	 *        that schemas for all schema IDs should be returned.
	 * 
	 * @param schemaVersion
	 *        Indicates that only the schemas with this version number should
	 *        be returned. This is usually used in conjunction with the
	 *        'schemaId' parameter.
	 * 
	 * @param numToSkip
	 *        The number of schemas that should be skipped.
	 * 
	 * @param numToReturn
	 *        The number of schemas that should be returned. This is limited by
	 *        {@link Request#DEFAULT_NUMBER_TO_RETURN}.
	 */
	public SchemaRequest(
		final String schemaId,
		final Long schemaVersion,
		final Long numToSkip,
		final Long numToReturn) {

		super(numToSkip, numToReturn);

		this.schemaId = schemaId;
		this.schemaVersion = schemaVersion;
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.request.Request#service()
	 */
	@Override
	public void service() throws OmhException {
		// First, short-circuit if this request has already been serviced.
		if(isServiced()) {
			return;
		}
		else {
			setServiced();
		}
		
		// Get the schema(s).
		MultiValueResult<? extends Schema> schemas =
			Registry
				.getInstance()
				.getSchemas(
					schemaId, 
					schemaVersion, 
					getNumToSkip(), 
					getNumToReturn());
		
		// If a specific schema was being requested but no such schema exists,
		// throw an exception indicating this.
		if((schemaId != null) && (schemas.size() == 0)) {
			throw
				new NoSuchSchemaException(
					"The schema is unknown: " + schemaId);
		}
		
		// If the user was requesting a specific schema, set the result as that
		// specific schema.
		if((schemaId != null) && (schemaVersion != null)) {
			// This is a violation of the system.
			if(schemas.size() > 1) {
				throw
					new OmhException(
						"Multiple schemas were returned for ID '" +
							schemaId +
							"' and version '" +
							schemaVersion +
							"'.");
			}
			
			// Get the singular result.
			Schema result = schemas.iterator().next();
			
			// Convert the result to a map.
			ObjectMapper mapper = new ObjectMapper();
			// We need to suppress Java's type erasure. :(
			@SuppressWarnings("unchecked")
			Map<String, Object> metaData = 
				mapper.convertValue(result, Map.class);
			
			// Remove the schema from the meta-data.
			metaData.remove(Schema.JSON_KEY_SCHEMA);
			
			// Save the meta-data.
			setMetaData(metaData);
			
			// Set the schema itself as the data.
			setData(result.getSchema());
		}
		else {
			// Return the list of schemas.
			setData(schemas);
		}
	}
}