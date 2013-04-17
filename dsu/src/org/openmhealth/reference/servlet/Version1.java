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
package org.openmhealth.reference.servlet;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.openmhealth.reference.exception.InvalidAuthenticationException;
import org.openmhealth.reference.exception.OmhException;
import org.openmhealth.reference.request.AuthTokenRequest;
import org.openmhealth.reference.request.DataReadRequest;
import org.openmhealth.reference.request.DataWriteRequest;
import org.openmhealth.reference.request.Request;
import org.openmhealth.reference.request.SchemaRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;

/**
 * <p>
 * The controller for the version 1 of the Open mHealth API.
 * </p>
 * 
 * <p>
 * This class has no state and, therefore, is immutable.
 * </p>
 *
 * @author John Jenkins
 */
@Controller
@RequestMapping("/v1")
public class Version1 {
	/**
	 * The username parameter for the authenticate requests.
	 */
	public static final String PARAM_AUTHENTICATION_USERNAME = "username";
	/**
	 * The password parameter for the authenticate requests.
	 */
	public static final String PARAM_AUTHENTICATION_PASSWORD = "password";
	/**
	 * The authentication token parameter for requests that require
	 * authentication.
	 */
	public static final String PARAM_AUTHENTICATION_AUTH_TOKEN = 
		"omh_auth_token";

	/**
	 * The parameter for the number of records to skip in requests that use
	 * paging.
	 */
	public static final String PARAM_PAGING_NUM_TO_SKIP = "num_to_skip";
	/**
	 * The parameter for the number of records to return in requests that use
	 * paging.
	 */
	public static final String PARAM_PAGING_NUM_TO_RETURN = "num_to_return";
	
	/**
	 * The parameter for the unique identifier for a schema. This is sometimes
	 * used as part of the URI for the RESTful implementation.
	 */
	public static final String PARAM_SCHEMA_ID = "schema_id";
	/**
	 * The parameter for the version of a schema. This is sometimes used as
	 * part of the URI for the RESTful implementation.
	 */
	public static final String PARAM_SCHEMA_VERSION = "schema_version";
	
	/**
	 * A parameter that limits the results to only those that were created on
	 * or after the given date.
	 */
	public static final String PARAM_DATE_START = "t_start";
	/**
	 * A parameter that limits the results to only those that were created on
	 * or before the given date.
	 */
	public static final String PARAM_DATE_END = "t_end";
	
	/**
	 * The parameter that indicates to which user the data should perain.
	 */
	public static final String PARAM_OWNER = "owner";
	/**
	 * The parameter that indicates that the data should be summarized, if
	 * possible.
	 */
	public static final String PARAM_SUMMARIZE = "summarize";
	/**
	 * The parameter that indicates which columns of the data should be
	 * returned.
	 */
	public static final String PARAM_COLUMN_LIST = "column_list";
	
	/**
	 * The parameter for the data when it is being uploaded.
	 */
	public static final String PARAM_DATA = "data";
	
	/**
	 * Creates an authentication request, authenticates the user and, if
	 * successful, returns the user's credentials.
	 * 
	 * @param username
	 *        The username of the user attempting to authenticate.
	 * 
	 * @param password
	 *        The password of the user attempting to authenticate.
	 * 
	 * @return A View object that will contain the user's authentication token.
	 */
	@RequestMapping(value = "auth", method = RequestMethod.POST)
	public @ResponseBody Object getAuthentication(
		@RequestParam(
			value = PARAM_AUTHENTICATION_USERNAME,
			required = true)
			final String username,
		@RequestParam(
			value = PARAM_AUTHENTICATION_PASSWORD,
			required = true)
			final String password,
		final HttpServletResponse response) {

		return
			handleRequest(new AuthTokenRequest(username, password), response);
	}
	
	/**
	 * Creates an authorization request, and returns the corresponding response
	 * based on the given parameters.
	 * 
	 * @return A View based on the current state of the authentication process.
	 */
	@RequestMapping(value = "auth/oauth/**", method = RequestMethod.POST)
	public View getAuthorization() {
		// TODO: Wire this up to an underlying request.
		return null;
	}
	
	/**
	 * If the root of the hierarchy is requested, return the registry, which is
	 * a map of all of the payload IDs to their high-level information, e.g.
	 * name, description, latest version, etc.
	 * 
	 * @return All of the known schema ID-version pairs and their corresponding
	 *         schema, based on paging.
	 */
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public @ResponseBody Object getRegistry(
		@RequestParam(
			value = PARAM_PAGING_NUM_TO_SKIP,
			required = false,
			defaultValue = Request.DEFAULT_NUMBER_TO_SKIP_STRING)
			final long numToSkip,
		@RequestParam(
			value = PARAM_PAGING_NUM_TO_RETURN,
			required = false,
			defaultValue = Request.DEFAULT_NUMBER_TO_RETURN_STRING)
			final long numToReturn,
		final HttpServletResponse response) {
		
		return
			handleRequest(
				new SchemaRequest(null, null, numToSkip, numToReturn),
				response);
	}
	
	/**
	 * Creates a request to get the information about the given payload ID,
	 * e.g. the name, description, version list, etc.
	 * 
	 * @request schemaId The schema ID from the URL.
	 * 
	 * @return The schema for each version of the schema ID, based on paging.
	 */
	@RequestMapping(
		value = "{" + PARAM_SCHEMA_ID + "}",
		method = RequestMethod.GET)
	public @ResponseBody Object getPayloads(
		@PathVariable(PARAM_SCHEMA_ID) final String schemaId,
		@RequestParam(
			value = PARAM_PAGING_NUM_TO_SKIP,
			required = false,
			defaultValue = Request.DEFAULT_NUMBER_TO_SKIP_STRING)
			final long numToSkip,
		@RequestParam(
			value = PARAM_PAGING_NUM_TO_RETURN,
			required = false,
			defaultValue = Request.DEFAULT_NUMBER_TO_RETURN_STRING)
			final long numToReturn,
		final HttpServletResponse response) {
		
		return 
			handleRequest(
				new SchemaRequest(schemaId, null, numToSkip, numToReturn),
				response);
	}
	
	/**
	 * Creates a request to get the definition of a specific schema ID's
	 * version.
	 * 
	 * @param schemaId
	 *        The schema ID from the URL.
	 * 
	 * @param version
	 *        The schema version from the URL.
	 * 
	 * @return The definition of the schema ID-version pair.
	 */
	@RequestMapping(
		value = "{" + PARAM_SCHEMA_ID + "}/{" + PARAM_SCHEMA_VERSION + "}",
		method = RequestMethod.GET)
	public @ResponseBody Object getPayloadDefinition(
		@PathVariable(PARAM_SCHEMA_ID) final String schemaId,
		@PathVariable(PARAM_SCHEMA_VERSION) final Long version,
		@RequestParam(
			value = PARAM_PAGING_NUM_TO_SKIP,
			required = false,
			defaultValue = Request.DEFAULT_NUMBER_TO_SKIP_STRING)
			final long numToSkip,
		@RequestParam(
			value = PARAM_PAGING_NUM_TO_RETURN,
			required = false,
			defaultValue = Request.DEFAULT_NUMBER_TO_RETURN_STRING)
			final long numToReturn,
		final HttpServletResponse response) {
		
		return 
			handleRequest(
				new SchemaRequest(schemaId, version, numToSkip, numToReturn),
				response);
	}
	
	/**
	 * Retrieves the requested data.
	 * 
	 * @param authTokenCookie
	 *        The authentication token as a cookie. The token must be provided
	 *        either here or as a parameter.
	 * 
	 * @param authTokenParameter
	 *        The authentication token as a parameter. The token must be
	 *        provided either here or as a cookie.
	 * 
	 * @param schemaId
	 *        The ID for the schema to which the data pertains. This is part of
	 *        the request's path.
	 * 
	 * @param version
	 *        The version of the schema to which the data pertains. This is
	 *        part of the request's path.
	 * 
	 * @param owner
	 *        The user that owns the desired data.
	 * 
	 * @param columnList
	 *        The list of columns to return to the user.
	 * 
	 * @param numToSkip
	 *        The number of data points to skip to facilitate paging.
	 * 
	 * @param numToReturn
	 *        The number of data points to return to facilitate paging.
	 * 
	 * @param response
	 *        The HTTP response object.
	 * 
	 * @return The data as a JSON array of JSON objects where each object
	 *         represents a single data point.
	 */
	@RequestMapping(
		value = "{" + PARAM_SCHEMA_ID + "}/{" + PARAM_SCHEMA_VERSION + "}/data",
		method = RequestMethod.GET)
	public @ResponseBody Object getData(
		@CookieValue(
			value = PARAM_AUTHENTICATION_AUTH_TOKEN,
			required = false)
			final String authTokenCookie,
		@RequestParam(
			value = PARAM_AUTHENTICATION_AUTH_TOKEN,
			required = false)
			final String authTokenParameter,
		@PathVariable(PARAM_SCHEMA_ID) final String schemaId,
		@PathVariable(PARAM_SCHEMA_VERSION) final Long version,
		@RequestParam(
			value = PARAM_OWNER,
			required = false)
			final String owner,
		@RequestParam(
			value = PARAM_COLUMN_LIST,
			required = false)
			final List<String> columnList,
		@RequestParam(
			value = PARAM_PAGING_NUM_TO_SKIP,
			required = false,
			defaultValue = Request.DEFAULT_NUMBER_TO_SKIP_STRING)
			final long numToSkip,
		@RequestParam(
			value = PARAM_PAGING_NUM_TO_RETURN,
			required = false,
			defaultValue = Request.DEFAULT_NUMBER_TO_RETURN_STRING)
			final long numToReturn,
		final HttpServletResponse response) {
		
		// Handle authentication.
		String authToken =
			handleAuthentication(authTokenCookie, authTokenParameter, false);

		// Handle the request.
		return 
			handleRequest(
				new DataReadRequest(
					authToken,
					schemaId,
					version,
					owner,
					columnList,
					numToSkip,
					numToReturn),
				response);
	}
	
	/**
	 * Writes the requested data.
	 * 
	 * @param authTokenParameter
	 *        The authentication token as a parameter. The token must be
	 *        provided either here or as a cookie.
	 * 
	 * @param schemaId
	 *        The ID for the schema to which the data pertains. This is part of
	 *        the request's path.
	 * 
	 * @param version
	 *        The version of the schema to which the data pertains. This is
	 *        part of the request's path.
	 *        
	 * @param data
	 *        The data to be uploaded, which should be a JSON array of JSON
	 *        objects where each object is a single data point.
	 * 
	 * @param response
	 *        The HTTP response object.
	 */
	@RequestMapping(
		value = "{" + PARAM_SCHEMA_ID + "}/{" + PARAM_SCHEMA_VERSION + "}/data",
		method = RequestMethod.POST)
	public @ResponseBody void setData(
		@RequestParam(
			value = PARAM_AUTHENTICATION_AUTH_TOKEN,
			required = true)
			final String authToken,
		@PathVariable(PARAM_SCHEMA_ID) final String schemaId,
		@PathVariable(PARAM_SCHEMA_VERSION) final Long version,
		@RequestParam(
			value = PARAM_DATA,
			required = true)
			final String data,
		final HttpServletResponse response) {
		
		// Handle the request.
		handleRequest(
			new DataWriteRequest(
				authToken,
				schemaId,
				version,
				data),
			response);
	}
	
	/**
	 * Checks the cookie and parameter authentication tokens and returns the
	 * appropriate one or null if none were given.
	 * 
	 * @param cookie
	 *        The authentication token from the HTTP cookies.
	 * 
	 * @param parameter
	 *        The authentication token from the parameters.
	 * 
	 * @param onlyParameter
	 *        A flag indicating if the authentication token may only be a
	 *        parameter. If true, there will still be a check to ensure that,
	 *        if a cookie is given, it matches the parameter, if given.
	 * 
	 * @return The most appropriate authentication token.
	 * 
	 * @throws InvalidAuthenticationException
	 *         The authentication tokens did not match, or it was only given as
	 *         a cookie but required to be a parameter.
	 */
	private String handleAuthentication(
		final String cookie,
		final String parameter,
		final boolean onlyParameter)
		throws OmhException {
		
		// If neither was given, then return null.
		if((cookie == null) && (parameter == null)) {
			return null;
		}
		// If they were both given,
		else if((cookie != null) && (parameter != null)) {
			// If they are equal, then return one.
			if(cookie.equals(parameter)) {
				return parameter;
			}
			// Otherwise, complain about them not being equal.
			else {
				throw
					new InvalidAuthenticationException(
						"The authentication token cookie was not equal to " +
							"the authentication token parameter.");
			}
		}
		// If only the cookie was given, then check to be sure it is allowed as
		// only a cookie.
		else if(cookie != null) {
			if(onlyParameter) {
				throw
					new InvalidAuthenticationException(
						"The authentication token was only given as a " +
							"cookie, but it is required to be a parameter.");
			}
			else {
				return cookie;
			}
		}
		// If they were both given, then return that.
		else {
			return parameter;
		}
	}
	
	/**
	 * Handles a request then sets the meta-data as HTTP headers and returns
	 * the data to be returned to the user.
	 * 
	 * @param request
	 *        The already-built request to be serviced.
	 * 
	 * @param response
	 *        The HTTP response to use to set the headers.
	 * 
	 * @return The object to be returned to the user.
	 */
	private Object handleRequest(
		final Request request,
		final HttpServletResponse response) {
		
		// Service the request.
		request.service();
		
		// Retrieve the meta-data and add it as HTTP headers.
		Map<String, Object> metaData = request.getMetaData();
		if(metaData != null) {
			for(String metaDataKey : metaData.keySet()) {
				response
					.setHeader(
						metaDataKey,
						metaData.get(metaDataKey).toString());
			}
		}
		
		// Return the data.
		return request.getData();
	}
}
