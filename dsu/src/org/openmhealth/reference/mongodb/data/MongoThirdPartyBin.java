package org.openmhealth.reference.mongodb.data;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.openmhealth.reference.data.ThirdPartyBin;
import org.openmhealth.reference.domain.ThirdParty;
import org.openmhealth.reference.exception.OmhException;
import org.openmhealth.reference.mongodb.domain.MongoThirdParty;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.QueryBuilder;

/**
 * <p>
 * The interface to the database-backed third-party repository.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoThirdPartyBin extends ThirdPartyBin {
	/**
	 * Default constructor.
	 */
	protected MongoThirdPartyBin() {
		// Get the collection to add indexes to.
		DBCollection collection =
			MongoDao.getInstance().getDb().getCollection(DB_NAME);
		
		// Ensure that there is an index on the ID.
		collection
			.ensureIndex(
				new BasicDBObject(ThirdParty.JSON_KEY_ID, 1),
				DB_NAME + "_" + ThirdParty.JSON_KEY_ID + "_unique",
				true);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.ThirdPartyBin#storeThirdParty(org.openmhealth.reference.domain.ThirdParty)
	 */
	@Override
	public void storeThirdParty(
		final ThirdParty thirdParty)
		throws OmhException {
		
		// Validate the parameter.
		if(thirdParty == null) {
			throw new OmhException("The third-party is null.");
		}
		
		// Get the connection to the third-party bin with the Jackson wrapper.
		JacksonDBCollection<ThirdParty, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					ThirdParty.class);

		// Make sure a third-party with the same ID doesn't already exist.
		if(collection
			.count(
				new BasicDBObject(
					ThirdParty.JSON_KEY_ID,
					thirdParty.getId())) > 0) {
			
			throw
				new OmhException(
					"A third-party with the given ID already exists.");
		}
		
		// Save it.
		collection.insert(thirdParty);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.ThirdPartyBin#getThirdParty(java.lang.String)
	 */
	@Override
	public ThirdParty getThirdParty(
		final String thirdParty)
		throws OmhException {
		
		// Get the connection to the third-party bin with the Jackson wrapper.
		JacksonDBCollection<MongoThirdParty, Object> collection =
			JacksonDBCollection
				.wrap(
					MongoDao
						.getInstance()
						.getDb()
						.getCollection(DB_NAME),
					MongoThirdParty.class);
		
		// Build the query.
		QueryBuilder queryBuilder = QueryBuilder.start();
		
		// Add the third-party ID to the query.
		queryBuilder.and(ThirdParty.JSON_KEY_ID).is(thirdParty);
		
		// Execute query.
		DBCursor<MongoThirdParty> result = collection.find(queryBuilder.get());
		
		// If multiple authentication tokens were returned, that is a violation
		// of the system.
		if(result.count() > 1) {
			throw
				new OmhException(
					"Multiple third-parties have the same ID: " + thirdParty);
		}
		
		// If no third-parties were returned, then return null.
		if(result.count() == 0) {
			return null;
		}
		else {
			return result.next();
		}
	}
}