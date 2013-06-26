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
import org.mongojack.JacksonDBCollection;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.domain.mongodb.MongoUser;
import org.openmhealth.reference.exception.OmhException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
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
	}

	/**
	 * Retrieves the {@link User} object from a user-name.
	 * 
	 * @param username
	 *        The desired user's user-name.
	 * 
	 * @return A {@link User} object for the user or null if the user does not
	 *         exist.
	 * 
	 * @throws OmhException
	 *         The user-name is null or multiple users have the same user-name.
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
					MongoUser.class);
		
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
}