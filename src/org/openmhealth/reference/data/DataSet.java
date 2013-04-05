package org.openmhealth.reference.data;

import java.util.List;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.openmhealth.reference.domain.ColumnList;
import org.openmhealth.reference.domain.Data;
import org.openmhealth.reference.domain.MetaData;
import org.openmhealth.reference.domain.Schema;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

/**
 * The data associated with their schema as defined by the Open mHealth.
 *
 * @author John Jenkins
 */
public class DataSet {
	/**
	 * The name of the DB document/table/whatever that contains the data.
	 */
	public static final String DATA_DB_NAME = "data";

	/**
	 * Default constructor. All access to the data set is static.
	 */
	public DataSet() {
		// Do nothing.
	}

	/**
	 * Reads the data from the system.
	 * 
	 * @param schemaId The unique identifier for the schema for the requested
	 * 				   data. This parameter is required.
	 * 
	 * @param version The version of the schema for the requested data. This
	 * 				  parameter is required.
	 * 
	 * @param owner The identifier of the user whose data is requested. This
	 * 				parameter is required.
	 * 
	 * @param columnList The list of columns within the data to return.
	 * 
	 * @param numToSkip The number of data points to skip.
	 * 
	 * @param numToReturn The number of data points to return.
	 * 
	 * @return A database cursor that references the applicable data.
	 */
	public static DBCursor<Data> getData(
		final String owner,
		final String schemaId,
		final long version,
		final ColumnList columnList,
		final Long numToSkip,
		final Long numToReturn) {
		
		// Get the connection to the database.
		DB db = Dao.getInstance().getDb();
		
		// Get the connection to the data with the Jackson wrapper.
		JacksonDBCollection<Data, Object> collection =
			JacksonDBCollection
				.wrap(db.getCollection(DATA_DB_NAME), Data.class);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Only select data for a single user.
		queryBuilder.and(Data.JSON_KEY_OWNER).is(owner);
		
		// Only select data for a given schema.
		queryBuilder.and(Schema.JSON_KEY_ID).is(schemaId);
		
		// Only select data for a given version of the the given schema.
		queryBuilder.and(Schema.JSON_KEY_VERSION).is(version);
		
		// Create the projection.
		DBObject projection = new BasicDBObject();
		// Add the owner field.
		projection.put(Data.JSON_KEY_OWNER, 1);
		// Add the schema ID field.
		projection.put(Schema.JSON_KEY_ID, 1);
		// Add the schema version.
		projection.put(Schema.JSON_KEY_VERSION, 1);
		// Add the meta-data field.
		projection.put(Data.JSON_KEY_METADATA, 1);
		// Add all of the data or add only the specified columns if given.
		if(columnList.size() == 0) {
			projection.put(Data.JSON_KEY_DATA, 1);
		}
		else {
			if((columnList != null) && (columnList.size() > 0)) {
				for(String column : columnList.toList()) {
					projection
						.put(
							Data.JSON_KEY_DATA +
								ColumnList.COLUMN_SEPARATOR +
								column,
							1);
				}
			}
		}
		
		// Build the query.
		DBCursor<Data> result =
			collection.find(queryBuilder.get(), projection);
		
		// Build the sort field by sorting in reverse chronological order.
		DBObject sort = new BasicDBObject();
		sort.put(
			Data.JSON_KEY_METADATA + 
				ColumnList.COLUMN_SEPARATOR + 
				MetaData.JSON_KEY_TIMESTAMP,
			-1);
		result.sort(sort);
		
		return 
			result
				.skip((new Long(numToSkip)).intValue())
				.limit((new Long(numToReturn)).intValue());
	}
	
	/**
	 * Stores some data.
	 * 
	 * @param data
	 *        The data to store.
	 */
	public static void setData(final List<Data> data) {
		// Get the connection to the database.
		DB db = Dao.getInstance().getDb();
		
		// Get the connection to the data with the Jackson wrapper.
		JacksonDBCollection<Data, Object> collection =
			JacksonDBCollection
				.wrap(db.getCollection(DATA_DB_NAME), Data.class);
		
		// Insert the data.
		collection.insert(data);
	}
}