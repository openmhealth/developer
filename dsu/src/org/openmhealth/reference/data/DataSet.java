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

import java.util.List;

import org.openmhealth.reference.domain.ColumnList;
import org.openmhealth.reference.domain.Data;
import org.openmhealth.reference.domain.MultiValueResult;

/**
 * <p>
 * The collection of data.
 * </p>
 * 
 * @author John Jenkins
 */
public abstract class DataSet {
	/**
	 * The name of the DB document/table/whatever that contains the data.
	 */
	public static final String DB_NAME = "data";
	
	/**
	 * The instance of this DataSet to use. 
	 */
	private static DataSet instance;

	/**
	 * Default constructor. All access to the data set is static.
	 */
	public DataSet() {
		DataSet.instance = this;
	}
	
	/**
	 * Returns the singular instance of this class.
	 * 
	 * @return The singular instance of this class.
	 */
	public static DataSet getInstance() {
		return instance;
	}
	
	/**
	 * Stores some data.
	 * 
	 * @param data
	 *        The data to store.
	 */
	public abstract void storeData(final List<Data> data);

	/**
	 * Retrieves some data based on the parameters. Some parameters are
	 * required, and others are optional. Each parameter indicates its
	 * respective requirements.
	 * 
	 * @param owner
	 *        The unique identifier of the user whose data is requested. This
	 *        parameter is required.
	 * 
	 * @param schemaId
	 *        The unique identifier for the schema for the requested data. This
	 *        parameter is required.
	 * 
	 * @param version
	 *        The version of the schema for the requested data. This parameter
	 *        is required.
	 * 
	 * @param columnList
	 *        The list of columns within the data to return. This can include
	 *        both meta-data and data columns. This is optional, and null
	 *        indicates that all data should be returned.
	 * 
	 * @param numToSkip
	 *        The number of data points to skip.
	 * 
	 * @param numToReturn
	 *        The number of data points to return.
	 * 
	 * @return A {@link MultiValueResult} that references all of the applicable
	 *         data.
	 */
	public abstract MultiValueResult<Data> getData(
		final String owner,
		final String schemaId,
		final long version,
		final ColumnList columnList,
		final long numToSkip,
		final long numToReturn);
}