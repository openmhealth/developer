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
package org.openmhealth.reference.mongodb.domain;

import java.util.Iterator;

import org.mongojack.DBCursor;
import org.openmhealth.reference.domain.MultiValueResult;

/**
 * <p>
 * The {@link MultiValueResult} for MongoDB based on a {@link DBCursor}.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoMultiValueResultCursor<T> implements MultiValueResult<T> {
	/**
	 * The cursor that was used to make the query and contains the results.
	 */
	private final DBCursor<? extends T> cursor;
	
	/**
	 * Creates a new MongoDB multi-value result from a cursor.
	 * 
	 * @param cursor
	 *        The cursor used to make the query and that contains the results.
	 */
	public MongoMultiValueResultCursor(final DBCursor<? extends T> cursor) {
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
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return (Iterator<T>) cursor.iterator();
	}
}