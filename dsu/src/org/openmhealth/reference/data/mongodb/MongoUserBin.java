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

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.mongojack.internal.MongoJackModule;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.domain.mongodb.MongoUser;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;

/**
 * <p>
 * The MongoDB implementation of the interface to the database-backed
 * collection of users.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoUserBin extends UserBin {
	/**
	 * The object mapper that should be used to parse {@link Schema}s.
	 */
	private static final ObjectMapper JSON_MAPPER;
	static {
		// Create the object mapper.
		ObjectMapper mapper = new ObjectMapper();
		
		// Create the FilterProvider.
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.setFailOnUnknownId(false);
		mapper.setFilters(filterProvider);
		
		// Finally, we must configure the mapper to work with the MongoJack
		// configuration.
		JSON_MAPPER = MongoJackModule.configure(mapper);
	}	
	
	/**
	 * Default constructor that creates any tables and indexes if necessary.
	 */
	protected MongoUserBin() {
		// Get the collection to add indexes to.
		DBCollection collection =
			MongoDao.getInstance().getDb().getCollection(DB_NAME);
		
		// Ensure that there is an index on the username.
		collection
			.ensureIndex(
				new BasicDBObject(User.JSON_KEY_USERNAME, 1),
				DB_NAME + "_" + User.JSON_KEY_USERNAME + "_unique",
				true);
		
		// Ensure that there is an index on the username.
		collection
			.ensureIndex(
				new BasicDBObject(User.JSON_KEY_REGISTRATION_KEY, 1),
				DB_NAME + "_" + User.JSON_KEY_REGISTRATION_KEY,
				false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.UserBin#createUser(org.openmhealth.reference.domain.User)
	 */
	@Override
	public void createUser(final User user) throws OmhException {
		// Validate the input.
		if(user == null) {
			throw new OmhException("The user is null.");
		}
		
		// Get the user collection.
		JacksonDBCollection<User, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					User.class,
					Object.class,
					JSON_MAPPER);
		
		// Save the user.
		try {
			collection.insert(user);
		}
		catch(MongoException.DuplicateKey e) {
			throw
				new OmhException(
					"A user with that username already exists.",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.UserBin#getUser(java.lang.String)
	 */
	@Override
	public User getUser(final String username) throws OmhException {
		// Validate the parameter.
		if(username == null) {
			throw new OmhException("The username is null.");
		}
		
		// Get the authentication token collection.
		JacksonDBCollection<MongoUser, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					MongoUser.class,
					Object.class,
					JSON_MAPPER);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the authentication token to the query
		queryBuilder.and(MongoUser.JSON_KEY_USERNAME).is(username);
		
		// Execute query.
		DBCursor<MongoUser> result = collection.find(queryBuilder.get());
		
		// If multiple authentication tokens were returned, that is a violation
		// of the system.
		if(result.count() > 1) {
			throw
				new OmhException(
					"Multiple users exist with the same username: " +
						username);
		}
		
		// If no tokens were returned, then return null.
		if(result.count() == 0) {
			return null;
		}
		else {
			return result.next();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.UserBin#getUserFormRegistration(java.lang.String)
	 */
	@Override
	public User getUserFromRegistrationId(
		final String registrationId) 
		throws OmhException {
		
		// Validate the parameter.
		if(registrationId == null) {
			throw new OmhException("The registration ID is null.");
		}
		
		// Get the authentication token collection.
		JacksonDBCollection<MongoUser, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					MongoUser.class,
					Object.class,
					JSON_MAPPER);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the authentication token to the query
		queryBuilder
			.and(MongoUser.JSON_KEY_REGISTRATION_KEY)
			.is(registrationId);
		
		// Execute query.
		DBCursor<MongoUser> result = collection.find(queryBuilder.get());
		
		// If multiple authentication tokens were returned, that is a violation
		// of the system.
		if(result.count() > 1) {
			throw
				new OmhException(
					"Multiple users exist with the same registration ID: " +
						registrationId);
		}
		
		// If no tokens were returned, then return null.
		if(result.count() == 0) {
			return null;
		}
		else {
			return result.next();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.UserBin#updateUser(org.openmhealth.reference.domain.User)
	 */
	@Override
	public void updateUser(final User user) throws OmhException {
		// Validate the input.
		if(user == null) {
			throw new OmhException("The user is null.");
		}
		
		// Get the user collection.
		JacksonDBCollection<User, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					User.class,
					Object.class,
					JSON_MAPPER);
		
		// Ensure that we are only updating the user with the same user-name.
		Query query = DBQuery.is(User.JSON_KEY_USERNAME, user.getUsername());
		
		// Save the user.
		try {
			collection.update(query, user);
		}
		catch(MongoException e) {
			throw new OmhException("An internal error occurred.", e);
		}
	}
}