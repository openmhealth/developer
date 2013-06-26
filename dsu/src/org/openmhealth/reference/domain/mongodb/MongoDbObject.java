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
package org.openmhealth.reference.domain.mongodb;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * <p>
 * The base class for any MongoDB object. This contains basic MongoDB
 * information like the MongoDB-generated ID.
 * </p>
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