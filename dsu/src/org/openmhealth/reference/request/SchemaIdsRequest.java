package org.openmhealth.reference.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.domain.MultiValueResult;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Retrieves all of the known schema IDs.
 * </p>
 *
 * @author John Jenkins
 */
public class SchemaIdsRequest extends ListRequest<String> {
	/**
	 * Creates a request for the list of known schema IDs.
	 * 
	 * @param numToSkip The number of schema IDs to skip.
	 * 
	 * @param numToReturn The number of schema IDs to return.
	 * 
	 * @throws OmhException A parameter was invalid.
	 */
	public SchemaIdsRequest(
		final Long numToSkip,
		final Long numToReturn)
		throws OmhException {
		
		super(numToSkip, numToReturn);
	}

	/**
	 * Retrieves the list of known schema IDs.
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

		// Get the schema IDs.
		MultiValueResult<String> result =
			Registry
				.getInstance().getSchemaIds(getNumToSkip(), getNumToReturn());
		
		// Set the meta-data.
		Map<String, Object> metaData = new HashMap<String, Object>();
		metaData.put(METADATA_KEY_COUNT, result.count());
		setMetaData(metaData);
		
		// Set the data.
		setData(result);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.request.ListRequest#getPreviousNextParameters()
	 */
	@Override
	public Map<String, String> getPreviousNextParameters() {
		return Collections.emptyMap();
	}
}