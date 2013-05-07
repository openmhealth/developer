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
package org.openmhealth.reference.exception;

/**
 * <p>
 * A specific exception that should be used when a user's authentication
 * information is invalid.
 * </p>
 * 
 * @author John Jenkins
 */
public class InvalidAuthenticationException extends OmhException {
	/**
	 * The version of this class to be used with serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception with a reason describing what was wrong with the
	 * user's authentication information.
	 * 
	 * @param reason
	 *        A user-friendly explanation as to why the user's authentication
	 *        information was rejected.
	 */
	public InvalidAuthenticationException(final String reason) {
		super(reason);
	}

	/**
	 * Creates a new exception with a reason describing what was wrong with the
	 * user's authentication information as well as another exception that was
	 * thrown while processing the user's authentication information.
	 * 
	 * @param reason
	 *        A user-friendly explanation as to why the user's authentication
	 *        information was rejected.
	 * 
	 * @param cause
	 *        An underlying exception that caused this exception.
	 */
	public InvalidAuthenticationException(
		final String reason,
		final Throwable cause) {

		super(reason, cause);
	}
}