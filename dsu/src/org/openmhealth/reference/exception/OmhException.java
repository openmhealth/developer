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
 * The parent class for all exceptions within this domain.
 * </p>
 * 
 * @author John Jenkins
 */
public class OmhException extends RuntimeException {
	/**
	 * The version of this class used for serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A reason for the exception. This will be returned to the user.
	 * 
	 * @param reason
	 *        The reason for the exception, which will be returned to the user.
	 */
	public OmhException(final String reason) {
		super(reason);
	}

	/**
	 * What caused this exception to be thrown. Its reason will be used to
	 * respond to the user.
	 * 
	 * @param cause
	 *        The OmhException that raised this OmhException. The reason
	 *        for the parameterized exception will be returned to the user.
	 */
	public OmhException(final OmhException cause) {
		super(cause);
	}

	/**
	 * A reason for the exception and a OmhException that caused it. This
	 * reason is what will be returned to the user. Be wary of overriding the
	 * cause's reason as it will probably be more specific, although
	 * potentially less-helpful to the user.
	 * 
	 * @param reason
	 *        The reason for the exception, which will be returned to the user.
	 * 
	 * @param cause
	 *        The OmhException that raised this OmhException.
	 */
	public OmhException(final String reason, final Throwable cause) {
		super(reason, cause);
	}
}