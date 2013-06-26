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

import org.mongojack.MongoCollection;
import org.openmhealth.reference.data.AuthenticationTokenBin;
import org.openmhealth.reference.domain.AuthenticationToken;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A MongoDB extension of the {@link AuthenticationToken} type.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
@MongoCollection(name = AuthenticationTokenBin.DB_NAME)
public class MongoAuthenticationToken
	extends AuthenticationToken
	implements MongoDbObject {
	
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
	 * Creates an {@link AuthenticationToken} object via injection from the
	 * data layer.
	 * 
	 * @param id
	 *        The database ID for this authentication token.
	 * 
	 * @param token
	 *        The authentication token.
	 * 
	 * @param username
	 *        The user's user-name.
	 * 
	 * @param granted
	 *        The time when the token was granted.
	 * 
	 * @param expires
	 *        The time when the token expires.
	 * 
	 * @throws OmhException
	 *         The token and/or user-name are null.
	 */
	@JsonCreator
	protected MongoAuthenticationToken(
		@JsonProperty(DATABASE_FIELD_ID) final String dbId, 
		@JsonProperty(JSON_KEY_TOKEN) final String token,
		@JsonProperty(User.JSON_KEY_USERNAME) final String username,
		@JsonProperty(JSON_KEY_GRANTED) final long granted,
		@JsonProperty(JSON_KEY_EXPIRES) final long expires) 
		throws OmhException {
		
		super(token, username, granted, expires);
		
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