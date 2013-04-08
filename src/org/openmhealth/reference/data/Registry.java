package org.openmhealth.reference.data;

import org.mongojack.DBCursor;
import org.openmhealth.reference.domain.Schema;

/**
 * <p>
 * The collection of known schemas as defined by the Open mHealth
 * specification.
 * </p>
 * 
 * @author John Jenkins
 */
public abstract class Registry {
	/**
	 * The name of the DB document/table/whatever that contains the registry.
	 */
	public static final String REGISTRY_DB_NAME = "registry";
	
	/**
	 * The instance of this Registry to use. 
	 */
	protected static Registry instance;
	
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
	 * Gets all schemas that are part of the registry. All of the parameters
	 * are optional and limit results.
	 * 
	 * @param schemaId Limits the results to only those with the given schema
	 * 				   ID.
	 * 
	 * @param schemaVersion Limits the results to only those with the given
	 * 						schema version.
	 * 
	 * @return A cursor for the list of schemas, which is more efficient than
	 * 		   creating a new list here.
	 */
	public abstract DBCursor<Schema> getSchemas(
		final String schemaId, 
		final Long schemaVersion,
		final long numToSkip,
		final long numToReturn);
}