package org.openmhealth.reference.data.mongodb;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.openmhealth.reference.data.AuthorizationCodeResponseBin;
import org.openmhealth.reference.domain.AuthorizationCodeResponse;
import org.openmhealth.reference.domain.mongodb.MongoAuthorizationCodeResponse;
import org.openmhealth.reference.exception.OmhException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.QueryBuilder;

/**
 * <p>
 * The interface to the database-backed authorization code response repository.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoAuthorizationCodeResponseBin
	extends AuthorizationCodeResponseBin {

	/**
	 * Default constructor.
	 */
	protected MongoAuthorizationCodeResponseBin() {
		// Get the collection to add indexes to.
		DBCollection collection =
			MongoDao.getInstance().getDb().getCollection(DB_NAME);
		
		// Ensure that there is an unique index on the code.
		collection
			.ensureIndex(
				new BasicDBObject(
					AuthorizationCodeResponse.JSON_KEY_AUTHORIZATION_CODE,
					1),
				DB_NAME + 
					"_" + 
					AuthorizationCodeResponse.JSON_KEY_AUTHORIZATION_CODE + 
					"_unique",
				true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeResponseBin#storeVerification(org.openmhealth.reference.domain.AuthorizationCodeResponse)
	 */
	@Override
	public void storeVerification(
		final AuthorizationCodeResponse response)
		throws OmhException {

		// Validate the parameter.
		if(response == null) {
			throw new OmhException("The response is null.");
		}
		
		// Get the authentication token collection.
		JacksonDBCollection<AuthorizationCodeResponse, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					AuthorizationCodeResponse.class);
		
		// Make sure the token doesn't already exist.
		if(collection
			.count(
				new BasicDBObject(
					AuthorizationCodeResponse.JSON_KEY_AUTHORIZATION_CODE,
					response.getAuthorizationCode())) > 0) {
			
			throw
				new OmhException(
					"A response already exists for the given authorizaion " +
						"code.");
		}
		
		// Save it.
		collection.insert(response);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeResponseBin#getVerification(java.lang.String)
	 */
	@Override
	public AuthorizationCodeResponse getResponse(
		final String code)
		throws OmhException {
		
		// Get the connection to the authorization code response bin with the
		// Jackson wrapper.
		JacksonDBCollection<MongoAuthorizationCodeResponse, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					MongoAuthorizationCodeResponse.class);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the authentication code to the query.
		queryBuilder
			.and(AuthorizationCodeResponse.JSON_KEY_AUTHORIZATION_CODE)
			.is(code);
		
		// Execute query.
		DBCursor<MongoAuthorizationCodeResponse> result =
			collection.find(queryBuilder.get());
		
		// If multiple responses of the same authorization code were returned,
		// that is a violation of the system.
		if(result.count() > 1) {
			throw
				new OmhException(
					"Multiple responses of the same authorization code " +
						"exist: " +
						code);
		}
		
		// If no responses were returned, then return null.
		if(result.count() == 0) {
			return null;
		}
		else {
			return result.next();
		}
	}
}