package org.openmhealth.reference.data;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.OmhException;

import com.mongodb.QueryBuilder;

public class UserBin {
	/**
	 * The name of the DB document/table/whatever that contains the user
	 * definitions.
	 */
	public static final String DB_NAME_USER_BIN = "users";

	/**
	 * Default constructor. All access to the user bin is static.
	 */
	private UserBin() {
		// Do nothing.
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
	public static User getUser(final String username) throws OmhException {
		// Validate the parameter.
		if(username == null) {
			throw new OmhException("The username is null.");
		}
		
		// Get the authentication token collection.
		JacksonDBCollection<User, Object> collection =
			JacksonDBCollection
				.wrap(
					Dao.getInstance()
						.getDb()
						.getCollection(DB_NAME_USER_BIN),
					User.class);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the authentication token to the query
		queryBuilder.and(User.JSON_KEY_USERNAME).is(username);
		
		// Execute query.
		DBCursor<User> result = collection.find(queryBuilder.get());
		
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