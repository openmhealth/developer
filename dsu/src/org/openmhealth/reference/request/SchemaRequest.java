/*******************************************************************************
 * Copyright 2013 Open mHealth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmhealth.reference.request;

import java.util.Map;

import name.jenkins.paul.john.concordia.Concordia;

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
public class SchemaRequest extends Request<Concordia> {
	/**
	 * The specific schema ID being requested.
	 */
	private final String schemaId;
	/**
	 * The specific schema version being requested.
	 */
	private final long schemaVersion;

	/**
	 * Creates a new request for a schema.
	 * 
	 * @param schemaId
	 *        The ID for the schema that is desired.
	 * 
	 * @param schemaVersion
	 *        The version of the schema that is desired.
	 */
	public SchemaRequest(
		final String schemaId,
		final long schemaVersion)
		throws OmhException {
		
		// Validate the schema ID.
		if(schemaId == null) {
			throw new OmhException("The schema ID is missing.");
		}
		else {
			this.schemaId = schemaId;
		}
		
		// Store the schema version.
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
		
		// Get the schema.
		Schema schema =
			Registry.getInstance().getSchema(schemaId, schemaVersion);
		
		// Make sure the schema exists.
		if(schema == null) {
			throw
				new NoSuchSchemaException(
					"The schema with id '" +
						schemaId +
						"' and version '" +
						schemaVersion +
						"' does not exist.");
		}
		
		// Convert the result to a map.
		ObjectMapper mapper = new ObjectMapper();
		// We need to suppress Java's type erasure. :(
		@SuppressWarnings("unchecked")
		Map<String, Object> metaData = 
			mapper.convertValue(schema, Map.class);
		
		// Remove the schema from the meta-data.
		metaData.remove(Schema.JSON_KEY_SCHEMA);
		
		// Save the meta-data.
		setMetaData(metaData);
		
		// Set the schema itself as the data.
		setData(schema.getSchema());
	}
}