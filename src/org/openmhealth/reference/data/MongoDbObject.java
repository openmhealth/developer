package org.openmhealth.reference.data;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * The base class for any MongoDB object. This contains basic MongoDB
 * information like the MongoDB-generated ID.
 *
 * @author John Jenkins
 */
public class MongoDbObject {
	/**
	 * The database ID.
	 */
	@Id
	@ObjectId
	private String id;

	/**
	 * Default constructor which sets the database ID to null.
	 */
	@JsonCreator
	protected MongoDbObject() {
	}
	
	/**
	 * The method for setting the database ID.
	 * 
	 * @param id The database ID.
	 */
	@JsonSetter("_id")
	private void setDbId(final String id) {
		this.id = id;
	}
}