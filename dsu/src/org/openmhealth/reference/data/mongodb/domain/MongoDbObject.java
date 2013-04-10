package org.openmhealth.reference.data.mongodb.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * The base class for any MongoDB object. This contains basic MongoDB
 * information like the MongoDB-generated ID.
 *
 * @author John Jenkins
 */
@JsonAutoDetect(
	fieldVisibility = Visibility.DEFAULT,
	getterVisibility = Visibility.NONE,
	setterVisibility = Visibility.NONE,
	creatorVisibility = Visibility.DEFAULT)
public interface MongoDbObject {
	/**
	 * The JSON key for the database's ID field.
	 */
	public static final String DATABASE_FIELD_ID = "_id";
	
	/**
	 * Returns the database ID for this entity.
	 * 
	 * @return The database ID for this entity.
	 */
	public String getDatabaseId();
}