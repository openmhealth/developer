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

import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * The collection of users.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class UserBin {
	/**
	 * The name of the DB document/table/whatever that contains the user
	 * definitions.
	 */
	public static final String DB_NAME_USER_BIN = "users";
	
	/**
	 * The instance of this MongoUserBin to use. 
	 */
	protected static UserBin instance;

	/**
	 * Default constructor.
	 */
	protected UserBin() {
		UserBin.instance = this;
	}
	
	/**
	 * Returns the singular instance of this class.
	 * 
	 * @return The singular instance of this class.
	 */
	public static UserBin getInstance() {
		return instance;
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
	public abstract User getUser(final String username) throws OmhException;
}
