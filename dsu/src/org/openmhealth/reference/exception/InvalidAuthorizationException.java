package org.openmhealth.reference.exception;

/**
 * <p>
 * A specific exception that should be sued when a user's authorization
 * information is invalid.
 * </p>
 *
 * @author John Jenkins
 */
public class InvalidAuthorizationException extends OmhException {
	/**
	 * The version of this class to be used with serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception with a reason describing what was wrong with the
	 * user's authorization information.
	 * 
	 * @param reason
	 *        A user-friendly explanation as to why the user's authorization
	 *        information was rejected.
	 */
	public InvalidAuthorizationException(final String reason) {
		super(reason);
	}

	/**
	 * Creates a new exception with a reason describing what was wrong with the
	 * user's authorization information as well as another exception that was
	 * thrown while processing the user's authorization information.
	 * 
	 * @param reason
	 *        A user-friendly explanation as to why the user's authorization
	 *        information was rejected.
	 * 
	 * @param cause
	 *        An underlying exception that caused this exception.
	 */
	public InvalidAuthorizationException(
		final String reason,
		final Throwable cause) {

		super(reason, cause);
	}
}
