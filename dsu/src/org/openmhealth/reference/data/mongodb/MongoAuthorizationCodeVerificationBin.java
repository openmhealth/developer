package org.openmhealth.reference.data.mongodb;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.openmhealth.reference.data.AuthorizationCodeVerificationBin;
import org.openmhealth.reference.data.mongodb.domain.MongoAuthorizationCodeVerification;
import org.openmhealth.reference.domain.AuthorizationCodeVerification;
import org.openmhealth.reference.exception.OmhException;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;

/**
 * <p>
 * The interface to the database-backed authorization code verification
 * repository.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoAuthorizationCodeVerificationBin
	extends AuthorizationCodeVerificationBin {

	/**
	 * Default constructor.
	 */
	protected MongoAuthorizationCodeVerificationBin() {
		// Do nothing.
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeVerificationBin#storeVerification(org.openmhealth.reference.domain.AuthorizationCodeVerification)
	 */
	@Override
	public void storeVerification(
		final AuthorizationCodeVerification verification)
		throws OmhException {

		// Validate the parameter.
		if(verification == null) {
			throw new OmhException("The verification is null.");
		}
		
		// Get the authentication token collection.
		JacksonDBCollection<AuthorizationCodeVerification, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					AuthorizationCodeVerification.class);
		
		// Make sure the token doesn't already exist.
		if(collection
			.count(
				new BasicDBObject(
					AuthorizationCodeVerification.JSON_KEY_AUTHORIZATION_CODE,
					verification.getAuthorizationCode())) > 0) {
			
			throw
				new OmhException(
					"A verification already exists for the given " +
						"authorizaion code.");
		}
		
		// Save it.
		collection.insert(verification);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeVerificationBin#getVerification(java.lang.String)
	 */
	@Override
	public AuthorizationCodeVerification getVerification(
		final String code)
		throws OmhException {
		
		// Get the connection to the authorization code verification bin with
		// the Jackson wrapper.
		JacksonDBCollection<MongoAuthorizationCodeVerification, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					MongoAuthorizationCodeVerification.class);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the authentication token to the query.
		queryBuilder
			.and(AuthorizationCodeVerification.JSON_KEY_AUTHORIZATION_CODE)
			.is(code);
		
		// Execute query.
		DBCursor<MongoAuthorizationCodeVerification> result =
			collection.find(queryBuilder.get());
		
		// If multiple verifications of the same authorization token were 
		// returned, that is a violation of the system.
		if(result.count() > 1) {
			throw
				new OmhException(
					"Multiple verification of the same authorization token " +
						"exist: " +
						code);
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