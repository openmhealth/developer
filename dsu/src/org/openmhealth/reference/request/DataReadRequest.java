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

import org.openmhealth.reference.data.DataSet;
import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.domain.AuthenticationToken;
import org.openmhealth.reference.domain.AuthorizationToken;
import org.openmhealth.reference.domain.ColumnList;
import org.openmhealth.reference.domain.Data;
import org.openmhealth.reference.domain.MultiValueResult;
import org.openmhealth.reference.exception.InvalidAuthenticationException;
import org.openmhealth.reference.exception.InvalidAuthorizationException;
import org.openmhealth.reference.exception.NoSuchSchemaException;
import org.openmhealth.reference.exception.OmhException;
import org.openmhealth.reference.servlet.Version1;

/**
 * <p>
 * Retrieves data based on the given parameters.
 * </p>
 *
 * @author John Jenkins
 */
public class DataReadRequest extends ListRequest<Data> {
	/**
	 * The authentication token for the requesting user.
	 */
	private final AuthenticationToken authenticationToken;
	/**
	 * The authorization token for the requesting third-party.
	 */
	private final AuthorizationToken authorizationToken;
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
	 * @param authenticationToken The requesting user's authentication token.
	 * 
	 * @param authorizationToken The third-party's authorization token.
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
		final AuthenticationToken authenticationToken,
		final AuthorizationToken authorizationToken,
		final String schemaId,
		final long version,
		final String owner,
		final List<String> columnList,
		final Long numToSkip,
		final Long numToReturn)
		throws OmhException {
		
		super(numToSkip, numToReturn);
		
		if(authenticationToken == null) {
			throw
				new InvalidAuthenticationException(
					"No authentication token was provided.");
		}
		if(schemaId == null) {
			throw new OmhException("The schema ID is missing.");
		}

		this.authenticationToken = authenticationToken;
		this.authorizationToken = authorizationToken;
		this.schemaId = schemaId;
		this.version = version;
		this.columnList = new ColumnList(columnList);
		
		if(owner == null) {
			this.owner = authenticationToken.getUsername();
		}
		else {
			this.owner = owner;
		}
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
		if(
			Registry
				.getInstance()
				.getSchemas(schemaId, version, 0, 1).count() == 0) {
			
			throw
				new NoSuchSchemaException(
					"The schema ID, '" +
						schemaId +
						"', and version, '" +
						version +
						"', pair is unknown.");
		}
		
		// If the owner value is not the same as the requesting user, validate
		// that the authorization token grants them access.
		if(! authenticationToken.getUsername().equals(owner)) {
			// Ensure that the given authorization token grants access to the
			// given schema.
			if(
				! authorizationToken
					.getAuthorizationCode()
					.getScopes()
					.contains(schemaId)) {
				
				throw
					new InvalidAuthorizationException(
						"The authorization token does not grant access to " +
							"the given schema: " +
							schemaId);
			}
			
			// Ensure that the given authorization token grants access to the
			// user in question.
			if(
				! authorizationToken
					.getAuthorizationCodeVerification()
					.getOwnerUsername()
					.equals(owner)) {
				
				throw
					new InvalidAuthorizationException(
						"The authorization token does not grant access to " +
							"the given user's data: " +
							owner);
			}
		}
		
		// Get the data.
		MultiValueResult<Data> result =
			DataSet
				.getInstance()
				.getData(
					owner, 
					schemaId, 
					version, 
					columnList, 
					getNumToSkip(), 
					getNumToReturn());
		
		// Set the meta-data.
		Map<String, Object> metaData = new HashMap<String, Object>();
		metaData.put(METADATA_KEY_COUNT, result.count());
		setMetaData(metaData);
		
		// Set the data.
		setData(result);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.request.ListRequest#getPreviousNextParameters()
	 */
	@Override
	public Map<String, String> getPreviousNextParameters() {
		// Create the result map.
		Map<String, String> result = new HashMap<String, String>();
		
		// Add the owner if it's not the requesting user.
		if(! authenticationToken.getUsername().equals(owner)) {
			result.put(Version1.PARAM_OWNER, owner);
		}
		
		// Add the columns if they were given.
		if((columnList != null) && (columnList.size() > 0)) {
			result.put(Version1.PARAM_COLUMN_LIST, columnList.toString());
		}
		
		// Return the map.
		return result;
	}
}