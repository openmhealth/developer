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
	public static final String DB_NAME = "users";
	
	/**
	 * The instance of this UserBin to use. 
	 */
	private static UserBin instance;

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
	 * Creates a new user.
	 * 
	 * @param user
	 *        The user to be created.
	 * 
	 * @throws OmhException
	 *         A user with that user-name already exists or there was an
	 *         internal error creating the user.
	 */
	public abstract void createUser(final User user) throws OmhException;
	
//	/**
//	 * Creates a registration for a user.
//	 * 
//	 * @param user
//	 *        The user that is being registered.
//	 * 
//	 * @throws OmhException
//	 *         There was an error creating the registration.
//	 */
//	public abstract void registerUser(final User user) throws OmhException;
//	
//	/**
//	 * Activates an account that has not yet been activated and has the given
//	 * registration ID.
//	 * 
//	 * @param registrationId
//	 *        The registration ID for the user to use to activate their
//	 *        account.
//	 * 
//	 * @throws OmhException
//	 *         There was an error activating the user.
//	 */
//	public abstract void activateUser(final User user) throws OmhException;

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
	
	/**
	 * Retrieves the user with the given registration ID.
	 * 
	 * @param registrationId
	 *        The registration ID in question.
	 * 
	 * @return The user with the given registration ID or null if no user
	 *         exists with that registration ID.
	 * 
	 * @throws OmhException
	 *         The registration ID is null or multiple users have the same
	 *         registration ID.
	 */
	public abstract User getUserFromRegistrationId(
		final String registrationId) 
		throws OmhException;
	
	/**
	 * Updates an existing user.
	 * 
	 * @param user
	 *        The user to be updated.
	 * 
	 * @return The updated user.
	 * 
	 * @throws OmhException
	 *         There was an error updating the user.
	 */
	public abstract void updateUser(final User user) throws OmhException;
}