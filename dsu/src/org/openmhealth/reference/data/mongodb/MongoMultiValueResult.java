package org.openmhealth.reference.data.mongodb;

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