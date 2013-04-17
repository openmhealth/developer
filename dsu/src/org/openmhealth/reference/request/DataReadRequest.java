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
package org.openmhealth.reference.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmhealth.reference.data.AuthTokenBin;
import org.openmhealth.reference.data.DataSet;
import org.openmhealth.reference.data.MultiValueResult;
import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.AuthToken;
import org.openmhealth.reference.domain.ColumnList;
import org.openmhealth.reference.domain.Data;
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.InvalidAuthenticationException;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Retrieves data based on the given parameters.
 * </p>
 *
 * @author John Jenkins
 */
public class DataReadRequest extends Request {
	/**
	 * The meta-data key that indicates the total number of data points that
	 * matched the query before paging was applied.
	 */
	public static final String METADATA_KEY_COUNT = "Count";
	
	/**
	 * The authentication token for the requesting user.
	 */
	private final String authToken;
	/**
	 * The ID of the schema from which the data was generated.
	 */
	private final String schemaId;
	/**
	 * The version of the schema from which the data was generated.
	 */
	private final long version;
	/**
	 * The identifier for the user to which the data should belong.
	 */
	private final String owner;
	/**
	 * The list of columns to select from the data.
	 */
	private final ColumnList columnList;

	/**
	 * Creates a request for data.
	 * 
	 * @param authToken The requesting user's authentication token.
	 * 
	 * @param schemaId The ID of the schema from which the data was generated.
	 * 
	 * @param version The version of the schema from which the data was
	 * 				  generated.
	 * 
	 * @param owner Defines whose data should be read.
	 * 
	 * @param columnList The list of columns in the data to return.
	 * 
	 * @param numToSkip The number of data points to skip.
	 * 
	 * @param numToReturn The number of data points to return.
	 * 
	 * @throws OmhException A parameter was invalid.
	 */
	public DataReadRequest(
		final String authToken,
		final String schemaId,
		final long version,
		final String owner,
		final List<String> columnList,
		final Long numToSkip,
		final Long numToReturn)
		throws OmhException {
		
		super(numToSkip, numToReturn);
		
		if(authToken == null) {
			throw
				new InvalidAuthenticationException(
					"The authentication token is missing.");
		}
		if(schemaId == null) {
			throw new OmhException("The schema ID is missing.");
		}
		
		this.authToken = authToken;
		this.schemaId = schemaId;
		this.version = version;
		this.owner = owner;
		this.columnList = new ColumnList(columnList);
	}

	/**
	 * Authenticates the user, authorizes the request if it was for data that
	 * belongs to a different user, and retrieves the applicable data.
	 */
	@Override
	public void service() throws OmhException {
		// First, short-circuit if this request has already been serviced.
		if(isServiced()) {
			return;
		}
		else {
			setServiced();
		}
		
		// Check to be sure the schema is known.
		if(Registry.getInstance().getSchemas(schemaId, version, 0, 1).count() == 0) {
			throw
				new OmhException(
					"The schema ID, '" +
						schemaId +
						"', and version, '" +
						version +
						"', pair is unknown.");
		}
		
		// Get the authentication token object based on the parameterized
		// authentication token.
		AuthToken tokenObject = AuthTokenBin.getInstance().getUser(authToken);
		if(tokenObject == null) {
			throw new OmhException("The token is unknown.");
		}
		
		// Get the user to which the token belongs.
		User requestingUser = 
			UserBin.getInstance().getUser(tokenObject.getUsername());
		if(requestingUser == null) {
			throw new OmhException("The user no longer exists.");
		}

		// Get the user's username.
		String validatedOwner = 
			(owner == null) ? requestingUser.getUsername() : owner;
		
		// If the user is attempting to read data about another user, fail the
		// request. This is perfectly fine to allow with proper ACLs in another
		// system. However, this reference implementation disallows it for
		// simplicity.
		if(! requestingUser.getUsername().equals(validatedOwner)) {
			throw
				new InvalidAuthenticationException(
					"The requesting user is not allowed to read other " +
						"users' data.");
		}
		
		// Get the data.
		MultiValueResult<? extends Data> result =
			DataSet
				.getInstance()
				.getData(
					validatedOwner, 
					schemaId, 
					version, 
					columnList, 
					getNumToSkip(), 
					getNumToReturn());
		
		// Set the meta-data.
		Map<String, Object> metaData = new HashMap<String, Object>();
		metaData.put(Schema.JSON_KEY_ID, schemaId);
		metaData.put(Schema.JSON_KEY_VERSION, version);
		metaData.put(METADATA_KEY_COUNT, result.count());
		setMetaData(metaData);
		
		// Set the data.
		setData(result);
	}
}
