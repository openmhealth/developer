package org.openmhealth.reference.data;

import java.util.LinkedList;
import java.util.List;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.mongojack.internal.MongoJacksonMapperModule;
import org.openmhealth.reference.concordia.OmhValidationController;
import org.openmhealth.reference.domain.Schema;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

/**
 * <p>
 * The collection of known schemas as defined by the Open mHealth
 * specification.
 * </p>
 * 
 * @author John Jenkins
 */
public class Registry {
	/**
	 * The name of the DB document/table/whatever that contains the registry.
	 */
	public static final String REGISTRY_DB_NAME = "registry";
	
	/**
	 * The object mapper that should be used to parse {@link Schema}s.
	 */
	private static final ObjectMapper JSON_MAPPER;
	static {
		// Create the object mapper.
		ObjectMapper mapper = new ObjectMapper();
		
		// Add our custom validation controller as an injectable parameter to
		// the Schema's constructor.
		InjectableValues.Std injectableValues = new InjectableValues.Std();
		injectableValues
			.addValue(
				Schema.JSON_KEY_VALIDATION_CONTROLLER,
				OmhValidationController.VALIDATION_CONTROLLER);
		mapper.setInjectableValues(injectableValues);
		
		// Finally, we must configure the mapper to work with the MongoJack
		// configuration.
		JSON_MAPPER = MongoJacksonMapperModule.configure(mapper);
	}
	
	/**
	 * Default constructor. All access to the registry is static.
	 */
	private Registry() {
		// Do nothing.
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
	public static DBCursor<Schema> getSchemas(
		final String schemaId, 
		final Long schemaVersion,
		final long numToSkip,
		final long numToReturn) {
		
		// Get the connection to the database.
		DB db = Dao.getInstance().getDb();
		
		// Get the connection to the registry with the Jackson wrapper.
		JacksonDBCollection<Schema, Object> collection =
			JacksonDBCollection
				.wrap(
					db.getCollection(REGISTRY_DB_NAME),
					Schema.class,
					Object.class,
					JSON_MAPPER);
		
		// Create the fields to limit the query.
		List<Query> queries = new LinkedList<Query>();
		
		// Add the schema ID, if given.
		if(schemaId != null) {
			queries.add(DBQuery.is(Schema.JSON_KEY_ID, schemaId));
		}
		
		// Add the schema version, if given.
		if(schemaVersion != null) {
			queries.add(DBQuery.is(Schema.JSON_KEY_VERSION, schemaVersion));
		}
		
		// Build the query based on the number of parameters.
		DBCursor<Schema> result;
		if(queries.size() == 0) {
			result = collection.find();
		}
		else {
			result =
				collection.find(DBQuery.and(queries.toArray(new Query[0])));
		}

		// Build the sort field.
		DBObject sort = new BasicDBObject();
		sort.put(Schema.JSON_KEY_ID, -1);
		sort.put(Schema.JSON_KEY_VERSION, -1);
		
		return
			result
				.sort(sort)
				.skip((new Long(numToSkip)).intValue())
				.limit((new Long(numToReturn)).intValue());
	}
}