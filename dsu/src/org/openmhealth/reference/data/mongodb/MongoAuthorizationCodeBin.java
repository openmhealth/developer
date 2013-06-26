package org.openmhealth.reference.data.mongodb;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.openmhealth.reference.data.AuthorizationCodeBin;
import org.openmhealth.reference.domain.AuthorizationCode;
import org.openmhealth.reference.domain.mongodb.MongoAuthorizationCode;
import org.openmhealth.reference.exception.OmhException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.QueryBuilder;

/**
 * <p>
 * The interface to the database-backed authorization code repository.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoAuthorizationCodeBin extends AuthorizationCodeBin {
	/**
	 * Default constructor.
	 */
	protected MongoAuthorizationCodeBin() {
		// Get the collection to add indexes to.
		DBCollection collection =
			MongoDao.getInstance().getDb().getCollection(DB_NAME);
		
		// Ensure that there is an index on the code.
		collection
			.ensureIndex(
				new BasicDBObject(AuthorizationCode.JSON_KEY_CODE, 1),
				DB_NAME + "_" + AuthorizationCode.JSON_KEY_CODE + "_unique",
				true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeBin#storeCode(org.openmhealth.reference.domain.AuthorizationCode)
	 */
	@Override
	public void storeCode(final AuthorizationCode code) throws OmhException {
		// Validate the parameter.
		if(code == null) {
			throw new OmhException("The code is null.");
		}
		
		// Get the authorization code collection.
		JacksonDBCollection<AuthorizationCode, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					AuthorizationCode.class);
		
		// Make sure the token doesn't already exist.
		if(collection
			.count(
				new BasicDBObject(
					AuthorizationCode.JSON_KEY_CODE,
					code.getCode())) > 0) {
			
			throw new OmhException("The token already exists.");
		}
		
		// Save it.
		collection.insert(code);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeBin#getCode(java.lang.String)
	 */
	@Override
	public AuthorizationCode getCode(final String code) throws OmhException {
		// Get the connection to the authorization code bin with the Jackson
		// wrapper.
		JacksonDBCollection<MongoAuthorizationCode, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					MongoAuthorizationCode.class);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the authorization code to the query.
		queryBuilder.and(AuthorizationCode.JSON_KEY_CODE).is(code);
		
		// Execute query.
		DBCursor<MongoAuthorizationCode> result =
			collection.find(queryBuilder.get());
		
		// If multiple authorization codes were returned, that is a violation
		// of the system.
		if(result.count() > 1) {
			throw
				new OmhException(
					"Multiple copies of the same authorization code exist: " +
						code);
		}
		
		// If no codes were returned, then return null.
		if(result.count() == 0) {
			return null;
		}
		else {
			return result.next();
		}
	}
}