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
package org.openmhealth.reference.domain.mongodb;

import name.jenkins.paul.john.concordia.validator.ValidationController;

import org.mongojack.MongoCollection;
import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.domain.ThirdParty;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>
 * The MongoDB-specific variant of a {@link Schema} object.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
@MongoCollection(name = Registry.DB_NAME)
public class MongoSchema extends Schema implements MongoDbObject {
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
	 *        The MongoDB ID for this entity.
	 * 
	 * @param id
	 *        The unique ID for this schema.
	 * 
	 * @param version
	 *        The version of this schema.
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
		@JsonProperty(JSON_KEY_SCHEMA) final JsonNode schema,
		@JacksonInject(JSON_KEY_VALIDATION_CONTROLLER)
			final ValidationController controller)
		throws OmhException {

		super(id, version, schema, controller);
		
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