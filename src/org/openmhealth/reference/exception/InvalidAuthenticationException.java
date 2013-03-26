package org.openmhealth.reference.exception;

/**
 * A specific exception that should be used when a user's authentication
 * information is invalid.
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
