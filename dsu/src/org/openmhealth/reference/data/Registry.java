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
package org.openmhealth.reference.data;

import org.openmhealth.reference.domain.MultiValueResult;
import org.openmhealth.reference.domain.Schema;

/**
 * <p>
 * The collection of schemas.
 * </p>
 * 
 * @author John Jenkins
 */
public abstract class Registry {
	/**
	 * The name of the DB document/table/whatever that contains the registry.
	 */
	public static final String DB_NAME = "registry";
	
	/**
	 * The instance of this Registry to use. 
	 */
	private static Registry instance;
	
	/**
	 * Default constructor.
	 */
	protected Registry() {
		Registry.instance = this;
	}
	
	/**
	 * Returns the singular instance of this class.
	 * 
	 * @return The singular instance of this class.
	 */
	public static Registry getInstance() {
		return instance;
	}
	
	/**
	 * Retrieves all of the schema IDs in the system.
	 * 
	 * @param numToSkip
	 *        The number of schemas to skip.
	 * 
	 * @param numToReturn
	 *        The number of schemas to return.
	 * 
	 * @return All of the schema IDs in the system. If no schemas exist, the
	 *         result will be empty, but never null.
	 */
	public abstract MultiValueResult<String> getSchemaIds(
		final long numToSkip,
		final long numToReturn);
	
	/**
	 * Retrieves all of the versions for a given schema ID.
	 * 
	 * @param schemaId
	 *        The schema ID.
	 * 
	 * @param numToSkip
	 *        The number of schemas to skip.
	 * 
	 * @param numToReturn
	 *        The number of schemas to return.
	 * 
	 * @return All of the versions of a given schema. If the schema is unknown,
	 *         the result will be empty, but never null.
	 */
	public abstract MultiValueResult<Long> getSchemaVersions(
		final String schemaId,
		final long numToSkip,
		final long numToReturn);
	
	/**
	 * Retrieves the schema for a specific schema ID-version pair.
	 * 
	 * @param schemaId
	 *        The schema ID.
	 * 
	 * @param schemaVersion
	 *        The schema version.
	 * 
	 * @return The Schema that corresponds to the given ID-version pair or null
	 *         if no such ID-version pair exists.
	 */
	public abstract Schema getSchema(
		final String schemaId,
		final long schemaVersion);

	/**
	 * Retrieves all of the schemas that are part of the registry. All of the
	 * parameters are optional and limit the results.
	 * 
	 * @param schemaId
	 *        Limits the results to only those with the given schema ID.
	 * 
	 * @param schemaVersion
	 *        Limits the results to only those with the given schema version.
	 * 
	 * @param numToSkip
	 *        The number of schemas to skip.
	 * 
	 * @param numToReturn
	 *        The number of schemas to return.
	 * 
	 * @return A {@link MultiValueResult} that references all of the schemas
	 *         that matched the parameters.
	 */
	public abstract MultiValueResult<? extends Schema> getSchemas(
		final String schemaId, 
		final Long schemaVersion,
		final long numToSkip,
		final long numToReturn);
}