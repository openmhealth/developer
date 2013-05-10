package org.openmhealth.reference.request;

import java.util.HashMap;
import java.util.Map;

import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.domain.MultiValueResult;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Retrieves all of the known schema versions.
 * </p>
 *
 * @author John Jenkins
 */
public class SchemaVersionsRequest extends ListRequest<Long> {
	private final String schemaId;

	/**
	 * Creates a request for the list of known versions for a given schema.
	 * 
	 * @param schemaId The schema ID.
	 * 
	 * @param numToSkip The number of schema versions to skip.
	 *
	 * @param numToReturn The number of schema versions to return.
	 * 
	 * @throws OmhException A parameter was invalid.
	 */
	public SchemaVersionsRequest(
		final String schemaId,
		final Long numToSkip,
		final Long numToReturn)
		throws OmhException {
		
		super(numToSkip, numToReturn);
		
		// Validate the schema ID.
		if(schemaId == null) {
			throw new OmhException("The schema ID is missing.");
		}
		else {
			this.schemaId = schemaId;
		}
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

		// Get the schema versions.
		MultiValueResult<Long> result =
			Registry
				.getInstance()
					.getSchemaVersions(
						schemaId, 
						getNumToSkip(), 
						getNumToReturn());
		
		// Set the meta-data.
		Map<String, Object> metaData = new HashMap<String, Object>();
		metaData.put(METADATA_KEY_COUNT, result.count());
		setMetaData(metaData);
		
		// Set the data.
		setData(result);
	}
}