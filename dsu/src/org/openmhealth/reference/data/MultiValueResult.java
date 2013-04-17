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
