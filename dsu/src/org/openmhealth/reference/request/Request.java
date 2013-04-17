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

import java.util.Map;

import org.openmhealth.reference.domain.OmhObject;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * The root class for all requests.
 * </p>
 * 
 * <p>
 * This class is mutable.
 * </p>
 * 
 * @author John Jenkins
 */
public abstract class Request {
	/**
	 * The number must be a String to be used in the annotations. When
	 * referencing this value, always use its decoded form
	 * {@link #DEFAULT_NUMBER_TO_SKIP}.
	 */
	public static final String DEFAULT_NUMBER_TO_SKIP_STRING = "0";
	/**
	 * For paging, the default numer of items to skip if not given.
	 */
	public static final long DEFAULT_NUMBER_TO_SKIP = Long
		.decode(DEFAULT_NUMBER_TO_SKIP_STRING);

	/**
	 * The number must be a String to be used in the annotations. When
	 * referencing this value, always use its decoded form
	 * {@link #DEFAULT_NUMBER_TO_RETURN};
	 */
	public static final String DEFAULT_NUMBER_TO_RETURN_STRING = "100";
	/**
	 * For paging, the default number of items to skip if not given.
	 */
	public static final long DEFAULT_NUMBER_TO_RETURN = Long
		.decode(DEFAULT_NUMBER_TO_RETURN_STRING);

	/**
	 * The number of elements to skip.
	 */
	private final long numToSkip;
	/**
	 * The number of elements to return.
	 */
	private final long numToReturn;

	/**
	 * The meta-data to be returned. This won't be generated until after this
	 * request was serviced.
	 */
	private Map<String, Object> metaData = null;
	/**
	 * The data to be returned. This won't be generated until after this
	 * request was serviced.
	 */
	private Object data = null;

	/**
	 * An internal state variable
	 */
	private boolean serviced = false;

	/**
	 * Creates the base part of the request.
	 * 
	 * @param numToSkip
	 *        The number of elements to skip while processing this request. If
	 *        this is null, the {@link #DEFAULT_NUMBER_TO_SKIP default} is
	 *        used.
	 * 
	 * @param numToReturn
	 *        The number of elements to return from this request. If this is
	 *        null, the {@link #DEFAULT_NUMBER_TO_RETURN default} is used.
	 * 
	 * @throws OmhException
	 *         A parameter was invalid.
	 */
	public Request(final Long numToSkip, final Long numToReturn)
		throws OmhException {

		// Validate the number of elements to skip.
		if(numToSkip == null) {
			this.numToSkip = DEFAULT_NUMBER_TO_SKIP;
		}
		else if(numToSkip < 0) {
			throw new OmhException(
				"The number to skip must be 0 or positive: " + numToSkip);
		}
		else {
			this.numToSkip = numToSkip;
		}

		// Validate the number of elements to return.
		if(numToReturn == null) {
			this.numToReturn = DEFAULT_NUMBER_TO_RETURN;
		}
		else if(numToReturn <= 0) {
			throw new OmhException(
				"The number to return must be positive: " + numToReturn);
		}
		else if(numToReturn > DEFAULT_NUMBER_TO_RETURN) {
			throw new OmhException(
				"The number to return is greater than the allowed " +
					"default (" +
					DEFAULT_NUMBER_TO_RETURN +
					"): " +
					numToReturn);
		}
		else {
			this.numToReturn = numToReturn;
		}
	}

	/**
	 * Returns the number of elements to skip.
	 * 
	 * @return The number of elements to skip.
	 */
	public long getNumToSkip() {
		return numToSkip;
	}

	/**
	 * Returns the number of elements to return.
	 * 
	 * @return The number of elements to return.
	 */
	public long getNumToReturn() {
		return numToReturn;
	}

	/**
	 * Returns a modifiable map representing the meta-data produced by
	 * servicing this request.
	 * 
	 * @return A map of the meta-data produced by servicing this request. This
	 *         is modifiable, so great care should be taken with the resulting
	 *         map.
	 */
	public Map<String, Object> getMetaData() {
		return metaData;
	}

	/**
	 * Returns the data produced by servicing this request.
	 * 
	 * @return The data produced by servicing this request or null if the
	 *         request has not yet been serviced.
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Returns whether or not this request has already been serviced.
	 * 
	 * @return Whether or not this request has already been serviced.
	 */
	protected boolean isServiced() {
		return serviced;
	}

	/**
	 * Sets this request as already having been serviced.
	 */
	protected void setServiced() {
		serviced = true;
	}

	/**
	 * <p>
	 * Sets the meta-data for this request.
	 * </p>
	 * 
	 * <p>
	 * An empty map indicates that an empty map may be serialized at some
	 * point; whereas, null indicates that any serialization should completely
	 * omit the empty map.
	 * </p>
	 * 
	 * <p>
	 * This should probably be used in conjunction with {@link #getMetaData()}
	 * to preserve any previously added elements.
	 * </p>
	 * 
	 * @param metaData
	 *        The meta-data that will completely replace the existing meta-data
	 *        map.
	 */
	protected void setMetaData(final Map<String, Object> metaData) {
		this.metaData = metaData;
	}

	/**
	 * Sets the data for this request after it has been serviced.
	 * 
	 * @param data
	 *        The data resulting from processing this request.
	 * 
	 * @throws OmhException
	 *         There was an error executing the request.
	 */
	protected void setData(final Object data) throws OmhException {
		if(data == null) {
			throw new OmhException("The data is null.");
		}

		this.data = data;
	}

	/**
	 * Services the request and returns the domain-specific result. If the
	 * request encounters any errors, a {@link OmhException} should be
	 * thrown; otherwise, any {@link OmhObject} may be returned or null.
	 * 
	 * @return A {@link OmhObject} that represents the result of servicing
	 *         this request. If null, the request was successfully serviced,
	 *         but no relevant information was returned.
	 * 
	 * @throws OmhException
	 *         There was an error processing the request. The exception's
	 *         "reason" will be returned to the user and an appropriate status
	 *         code will be used.
	 */
	public abstract void service() throws OmhException;
}
