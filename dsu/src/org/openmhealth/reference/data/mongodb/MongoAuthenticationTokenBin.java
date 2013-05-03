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
import org.openmhealth.reference.data.AuthenticationTokenBin;
import org.openmhealth.reference.data.mongodb.domain.MongoAuthenticationToken;
import org.openmhealth.reference.domain.AuthenticationToken;
import org.openmhealth.reference.exception.OmhException;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;

/**
 * <p>
 * The interface to the database-backed authentication token repository.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoAuthenticationTokenBin extends AuthenticationTokenBin {
	/**
	 * Default constructor.
	 */
	protected MongoAuthenticationTokenBin() {
		// Do nothing.
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthenticationTokenBin#storeToken(org.openmhealth.reference.domain.AuthenticationToken)
	 */
	@Override
	public void storeToken(final AuthenticationToken token) throws OmhException {
		// Validate the parameter.
		if(token == null) {
			throw new OmhException("The token is null.");
		}
		
		// Get the authentication token collection.
		JacksonDBCollection<AuthenticationToken, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					AuthenticationToken.class);
		
		// Make sure the token doesn't already exist.
		if(collection
			.count(
				new BasicDBObject(
					AuthenticationToken.JSON_KEY_TOKEN,
					token.getToken())) > 0) {
			
			throw new OmhException("The token already exists.");
		}
		
		// Save it.
		collection.insert(token);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthenticationTokenBin#getUser(java.lang.String)
	 */
	@Override
	public AuthenticationToken getToken(
		final String token)
		throws OmhException {
		
		// Get the connection to the authentication token bin with the Jackson
		// wrapper.
		JacksonDBCollection<MongoAuthenticationToken, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					MongoAuthenticationToken.class);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the authentication token to the query.
		queryBuilder.and(AuthenticationToken.JSON_KEY_TOKEN).is(token);
		
		// Add the expiration timer to ensure that this token has not expired.
		queryBuilder
			.and(MongoAuthenticationToken.JSON_KEY_EXPIRES)
			.greaterThan(System.currentTimeMillis());
		
		// Execute query.
		DBCursor<MongoAuthenticationToken> result =
			collection.find(queryBuilder.get());
		
		// If multiple authentication tokens were returned, that is a violation
		// of the system.
		if(result.count() > 1) {
			throw
				new OmhException(
					"Multiple copies of the same authentication token " +
						"exist: " +
						token);
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
