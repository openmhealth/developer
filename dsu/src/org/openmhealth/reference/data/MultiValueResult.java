package org.openmhealth.reference.data;

/**
 * <p>
 * This represents a collection of results that are being returned.
 * </p>
 *
 * @author John Jenkins
 */
public interface MultiValueResult<T> extends Iterable<T> {
	/**
	 * Returns the total number of results that matched the query before any
	 * skipping or limiting is done.
	 * 
	 * @return The total number of results that matched the query before any
	 *         skipping or limiting is done.
	 */
	public int count();
	
	/**
	 * Returns the total number of results that are being returned.
	 * 
	 * @return The total number of results that are being returned.
	 */
	public int size();
}