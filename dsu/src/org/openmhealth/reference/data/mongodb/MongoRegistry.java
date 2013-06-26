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
package org.openmhealth.reference.data.mongodb;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.mongojack.internal.MongoJacksonMapperModule;
import org.openmhealth.reference.concordia.OmhValidationController;
import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.domain.MultiValueResult;
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.domain.mongodb.MongoMultiValueResultCursor;
import org.openmhealth.reference.domain.mongodb.MongoMultiValueResultList;
import org.openmhealth.reference.domain.mongodb.MongoSchema;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

/**
 * <p>
 * The interface to the database-backed Registry.
 * </p>
 * 
 * @author John Jenkins
 */
public class MongoRegistry extends Registry {
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
	 * Default constructor.
	 */
	protected MongoRegistry() {
		// Get the collection to add indexes to.
		DBCollection collection =
			MongoDao.getInstance().getDb().getCollection(DB_NAME);

		// Ensure that there is an index on the ID.
		collection
			.ensureIndex(
				new BasicDBObject(Schema.JSON_KEY_ID, 1),
				DB_NAME + "_" + Schema.JSON_KEY_ID + "_index",
				false);
		// Ensure that there is an index on the version.
		collection
			.ensureIndex(
				new BasicDBObject(Schema.JSON_KEY_VERSION, 1),
				DB_NAME + "_" + Schema.JSON_KEY_VERSION + "_index",
				false);
		
		// Ensure that there is a compound, unique key on the ID and version.
		collection
			.ensureIndex(
				(new BasicDBObject(Schema.JSON_KEY_ID, 1))
					.append(Schema.JSON_KEY_VERSION, 1),
				DB_NAME + 
					"_" + 
					Schema.JSON_KEY_ID + 
					"_" + 
					Schema.JSON_KEY_VERSION + 
					"_unique",
				true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Registry#getSchemaIds()
	 */
	public MultiValueResult<String> getSchemaIds(
		final long numToSkip,
		final long numToReturn) {
		
		// Get the connection to the database.
		DB db = MongoDao.getInstance().getDb();
		
		// Get the connection to the registry with the Jackson wrapper.
		JacksonDBCollection<MongoSchema, Object> collection =
			JacksonDBCollection
				.wrap(db.getCollection(DB_NAME), MongoSchema.class);
		
		// Get the list of results.
		@SuppressWarnings("unchecked")
		List<String> results = collection.distinct(Schema.JSON_KEY_ID);
		
		// Remember the total number of results.
		int numResults = results.size();
		
		// Sort the results.
		Collections.sort(results);
		
		// Get the lower index.
		int lowerIndex =
			(new Long(Math.min(numToSkip, results.size()))).intValue();
		// Get the upper index.
		int upperIndex =
			(new Long(Math.min(numToSkip + numToReturn, results.size())))
				.intValue();
		
		// Get the results based on the upper and lower bounds.
		results = results.subList(lowerIndex, upperIndex);
		
		// Create a MultiValueResult.
		MultiValueResult<String> result =
			new MongoMultiValueResultList<String>(results, numResults);
		
		// Return the list.
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Registry#getSchemaVersions(java.lang.String)
	 */
	public MultiValueResult<Long> getSchemaVersions(
		final String schemaId,
		final long numToSkip,
		final long numToReturn) {
		
		// Get the connection to the database.
		DB db = MongoDao.getInstance().getDb();
		
		// Get the connection to the registry with the Jackson wrapper.
		JacksonDBCollection<MongoSchema, Object> collection =
			JacksonDBCollection
				.wrap(db.getCollection(DB_NAME), MongoSchema.class);
		
		// Get the list of results.
		@SuppressWarnings("unchecked")
		List<Long> results = 
			collection
				.distinct(
					Schema.JSON_KEY_VERSION,
					new BasicDBObject(Schema.JSON_KEY_ID, schemaId));
		
		// Remember the total number of results.
		int numResults = results.size();
		
		// Sort the results.
		Collections.sort(results);
		
		// Get the lower index.
		int lowerIndex =
			(new Long(Math.min(numToSkip, results.size()))).intValue();
		// Get the upper index.
		int upperIndex =
			(new Long(Math.min(numToSkip + numToReturn, results.size())))
				.intValue();
		
		// Get the results based on the upper and lower bounds.
		results = results.subList(lowerIndex, upperIndex);
		
		// Create a MultiValueResult.
		MultiValueResult<Long> result =
			new MongoMultiValueResultList<Long>(results, numResults);
		
		// Return the list.
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Registry#getSchema(java.lang.String, long)
	 */
	public Schema getSchema(final String schemaId, final long schemaVersion) {
		// Get the connection to the database.
		DB db = MongoDao.getInstance().getDb();
		
		// Get the connection to the registry with the Jackson wrapper.
		JacksonDBCollection<MongoSchema, Object> collection =
			JacksonDBCollection
				.wrap(
					db.getCollection(DB_NAME),
					MongoSchema.class,
					Object.class,
					JSON_MAPPER);
		
		// Build the query
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the schema ID.
		queryBuilder.and(MongoSchema.JSON_KEY_ID).is(schemaId);
		
		// Add the schema version.
		queryBuilder.and(MongoSchema.JSON_KEY_VERSION).is(schemaVersion);
		
		// Execute query.
		DBCursor<MongoSchema> result = collection.find(queryBuilder.get());
		
		// Return null or the schema based on what the query returned.
		if(result.count() == 0) {
			return null;
		}
		else {
			return result.next();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Registry#getSchemas(java.lang.String, java.lang.Long, long, long)
	 */
	@Override
	public MultiValueResult<? extends Schema> getSchemas(
		final String schemaId, 
		final Long schemaVersion,
		final long numToSkip,
		final long numToReturn) {
		
		// Get the connection to the database.
		DB db = MongoDao.getInstance().getDb();
		
		// Get the connection to the registry with the Jackson wrapper.
		JacksonDBCollection<MongoSchema, Object> collection =
			JacksonDBCollection
				.wrap(
					db.getCollection(DB_NAME),
					MongoSchema.class,
					Object.class,
					JSON_MAPPER);
		
		// Create the fields to limit the query.
		List<Query> queries = new LinkedList<Query>();
		
		// Add the schema ID, if given.
		if(schemaId != null) {
			queries.add(DBQuery.is(MongoSchema.JSON_KEY_ID, schemaId));
		}
		
		// Add the schema version, if given.
		if(schemaVersion != null) {
			queries
				.add(DBQuery.is(MongoSchema.JSON_KEY_VERSION, schemaVersion));
		}
		
		// Build the query based on the number of parameters.
		DBCursor<MongoSchema> result;
		if(queries.size() == 0) {
			result = collection.find();
		}
		else {
			result =
				collection.find(DBQuery.and(queries.toArray(new Query[0])));
		}

		// Build the sort field.
		DBObject sort = new BasicDBObject();
		sort.put(MongoSchema.JSON_KEY_ID, -1);
		sort.put(MongoSchema.JSON_KEY_VERSION, -1);
		
		return
			new MongoMultiValueResultCursor<MongoSchema>(
				result
					.sort(sort)
					.skip((new Long(numToSkip)).intValue())
					.limit((new Long(numToReturn)).intValue()));
	}
}