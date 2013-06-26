package org.openmhealth.reference.domain.sql;

import java.util.Iterator;
import java.util.List;

import org.openmhealth.reference.domain.MultiValueResult;

/**
 * <p>
 * The {@link MultiValueResult} for SQL-based results.
 * </p>
 *
 * @author John Jenkins
 */
public class SqlMultiValueResult<T> implements MultiValueResult<T> {
	/**
	 * The list of results.
	 */
	private final List<T> list;
	/**
	 * The total number of results before paging.
	 */
	private final int count;

	/**
	 * Creates a new SqlMultiValueResult object from a Java List.
	 * 
	 * @param list The list to back this result.
	 * 
	 * @param count The total number of results before paging.
	 */
	public SqlMultiValueResult(final List<T> list, final int count) {
		this.list = list;
		this.count = count;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.domain.MultiValueResult#count()
	 */
	@Override
	public int count() {
		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.domain.MultiValueResult#size()
	 */
	@Override
	public int size() {
		return list.size();
	}
}