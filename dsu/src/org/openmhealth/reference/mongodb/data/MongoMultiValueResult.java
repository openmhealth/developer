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
package org.openmhealth.reference.mongodb.data;

import java.util.Iterator;

import org.mongojack.DBCursor;
import org.openmhealth.reference.data.MultiValueResult;

/**
 * <p>
 * The {@link MultiValueResult} for MongoDB.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoMultiValueResult<T> implements MultiValueResult<T> {
	/**
	 * The cursor that was used to make the query and contains the results.
	 */
	private final DBCursor<T> cursor;
	
	/**
	 * Creates a new MongoDB multi-value result from a cursor.
	 * 
	 * @param cursor
	 *        The cursor used to make the query and that contains the results.
	 */
	public MongoMultiValueResult(final DBCursor<T> cursor) {
		this.cursor = cursor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.MultiValueResult#count()
	 */
	@Override
	public int count() {
		return cursor.count();
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.MultiValueResult#size()
	 */
	@Override
	public int size() {
		return cursor.size();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return cursor.iterator();
	}
}
