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
 * A specific exception that should be used when a specific schema is missing
 * that the user is requesting.
 * </p>
 * 
 * @author John Jenkins
 */
public class NoSuchSchemaException extends OmhException {
	/**
	 * The version of this class to be used with serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception indicating that a schema does not exist. The
	 * reason should contain the ID and/or version.
	 * 
	 * @param reason
	 *        A user-friendly explanation as which ID and/or version is
	 *        missing.
	 */
	public NoSuchSchemaException(String reason) {
		super(reason);
	}

	/**
	 * A reason that this exception is thrown, which should indicate which
	 * schema is missing, as well as another exception that will be output for
	 * debugging purposes.
	 * 
	 * @param reason
	 *        A user-friendly explanation as which ID and/or version is
	 *        missing.
	 * 
	 * @param cause
	 *        Another exception that caused this exception.
	 */
	public NoSuchSchemaException(String reason, Throwable cause) {
		super(reason, cause);
	}
}