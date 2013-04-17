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
package org.openmhealth.reference.data.mongodb.domain;

import org.mongojack.MongoCollection;
import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.domain.Data;
import org.openmhealth.reference.domain.MetaData;
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * A MongoDB extension of the {@link Data} type.
 * </p>
 *
 * @author John Jenkins
 */
@MongoCollection(name = Registry.REGISTRY_DB_NAME)
public class MongoData extends Data implements MongoDbObject {
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
	private MongoData(
		@JsonProperty(DATABASE_FIELD_ID) final String dbId,
		@JsonProperty(JSON_KEY_OWNER) final String owner,
		@JsonProperty(Schema.JSON_KEY_ID) final String schemaId,
		@JsonProperty(Schema.JSON_KEY_VERSION) final long schemaVersion,
		@JsonProperty(JSON_KEY_METADATA) final MetaData metaData,
		@JsonProperty(JSON_KEY_DATA) final JsonNode data)
		throws OmhException {

		super(owner, schemaId, schemaVersion, metaData, data);
		
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
