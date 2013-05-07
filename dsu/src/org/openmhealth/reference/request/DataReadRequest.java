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
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.exception.InvalidAuthenticationException;
import org.openmhealth.reference.exception.NoSuchSchemaException;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Retrieves data based on the given parameters.
 * </p>
 *
 * @author John Jenkins
 */
public class DataReadRequest
	extends ListRequest<MultiValueResult<? extends Data>> {
	
	/**
	 * The meta-data key that indicates the total number of data points that
	 * matched the query before paging was applied.
	 */
	public static final String METADATA_KEY_COUNT = "Count";
	
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
		
		if((authenticationToken == null) && (authorizationToken == null)) {
			throw
				new InvalidAuthenticationException(
					"No authentication credentials were provided.");
		}
		if((authenticationToken != null) && (authorizationToken != null)) {
			throw
				new InvalidAuthenticationException(
					"Both an authentication token and an authorization " +
						"token were given, but only one should be.");
		}
		if(schemaId == null) {
			throw new OmhException("The schema ID is missing.");
		}

		this.authenticationToken = authenticationToken;
		this.authorizationToken = authorizationToken;
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
		
		// Validate the 'owner' parameter by ensuring that the requesting user
		// is the same as the owner or that the authorization token gives it
		// permission to do so.
		String validatedOwner;
		// Because we only allow one of authentication or validation tokens and
		// each token only represents one user, the user's data who is being
		// read must be the user associated with the given token.
		if(authenticationToken != null) {
			validatedOwner = authenticationToken.getUsername();
		}
		else if(authorizationToken != null) {
			validatedOwner =
				authorizationToken
					.getAuthorizationCodeVerification()
					.getOwner()
					.getUsername();
		}
		else {
			throw
				new OmhException(
					"No authentication credentials were provided.");
		}
		
		// If the owner is given, it must be the same as the one based on the
		// authentication or authorization token. For other systems, this may
		// be extended to other ACLs, but, for this reference implementation,
		// it must be exactly that same user.
		if((owner != null) && (! owner.equals(validatedOwner))) {
			throw
				new OmhException(
					"For the given authentication/authorization token, only " +
						"information about the user associated with that " +
						"token can be read.");
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