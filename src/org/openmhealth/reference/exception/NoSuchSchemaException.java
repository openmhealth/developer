package org.openmhealth.reference.exception;

/**
 * A specific exception that should be used when a specific schema is missing
 * that the user is requesting.
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