package org.openmhealth.reference.data.mongodb.data;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.openmhealth.reference.data.AuthorizationTokenBin;
import org.openmhealth.reference.data.mongodb.domain.MongoAuthorizationToken;
import org.openmhealth.reference.domain.AuthorizationToken;
import org.openmhealth.reference.exception.OmhException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.QueryBuilder;

/**
 * <p>
 * The interface to the database-backed authorization token repository.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoAuthorizationTokenBin extends AuthorizationTokenBin {
	/**
	 * Default constructor.
	 */
	public MongoAuthorizationTokenBin() {
		// Get the collection to add indexes to.
		DBCollection collection =
			MongoDao.getInstance().getDb().getCollection(DB_NAME);

		// Ensure that there is an index on the access token.
		collection.ensureIndex(AuthorizationToken.JSON_KEY_ACCESS_TOKEN);
		// Ensure that there is an index on the refresh token.
		collection.ensureIndex(AuthorizationToken.JSON_KEY_REFRESH_TOKEN);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationTokenBin#storeToken(org.openmhealth.reference.domain.AuthorizationToken)
	 */
	@Override
	public void storeToken(
		final AuthorizationToken token)
		throws OmhException {
		
		// Validate the parameter.
		if(token == null) {
			throw new OmhException("The token is null.");
		}
		
		// Get the authentication token collection.
		JacksonDBCollection<AuthorizationToken, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					AuthorizationToken.class);
		
		// Make sure the access token doesn't already exist.
		if(collection
			.count(
				new BasicDBObject(
					AuthorizationToken.JSON_KEY_ACCESS_TOKEN,
					token.getAccessToken())) > 0) {
			
			throw new OmhException("The access token already exists.");
		}
		
		// Also, make sure the refresh token doesn't already exist.
		if(collection
			.count(
				new BasicDBObject(
					AuthorizationToken.JSON_KEY_REFRESH_TOKEN,
					token.getRefreshToken())) > 0) {
			
			throw new OmhException("The refresh token already exists.");
		}
		
		// Save it.
		collection.insert(token);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationTokenBin#getTokenFromAccessToken(java.lang.String)
	 */
	@Override
	public AuthorizationToken getTokenFromAccessToken(
		final String accessToken)
		throws OmhException {
		
		// Get the connection to the authorization token bin with the Jackson
		// wrapper.
		JacksonDBCollection<MongoAuthorizationToken, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					MongoAuthorizationToken.class);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the access token to the query.
		queryBuilder
			.and(AuthorizationToken.JSON_KEY_ACCESS_TOKEN).is(accessToken);
		
		// Execute query.
		DBCursor<MongoAuthorizationToken> result =
			collection.find(queryBuilder.get());
		
		// If multiple authorization tokens were returned, that is a violation
		// of the system.
		if(result.count() > 1) {
			throw
				new OmhException(
					"Multiple copies of the same authorization access token " +
						"exist: " +
						accessToken);
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
	 * @see org.openmhealth.reference.data.AuthorizationTokenBin#getTokenFromRefreshToken(java.lang.String)
	 */
	@Override
	public AuthorizationToken getTokenFromRefreshToken(
		final String refreshToken)
		throws OmhException {
		
		// Get the connection to the authorization token bin with the Jackson
		// wrapper.
		JacksonDBCollection<MongoAuthorizationToken, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					MongoAuthorizationToken.class);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the refresh token to the query.
		queryBuilder
			.and(AuthorizationToken.JSON_KEY_REFRESH_TOKEN).is(refreshToken);
		
		// Execute query.
		DBCursor<MongoAuthorizationToken> result =
			collection.find(queryBuilder.get());
		
		// If multiple authorization tokens were returned, that is a violation
		// of the system.
		if(result.count() > 1) {
			throw
				new OmhException(
					"Multiple copies of the same authorization refresh " +
						"token exist: " +
						refreshToken);
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