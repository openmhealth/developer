package org.openmhealth.reference.data;

import org.openmhealth.reference.domain.AuthorizationCode;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * The interface to the database-backed authorization code repository.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class AuthorizationCodeBin {
	/**
	 * The name of the DB document/table/whatever that contains the
	 * authorization codes.
	 */
	public static final String DB_NAME = "authorization_code_bin";
	
	/**
	 * The instance of this AuthorizationCodeBin to use.
	 */
	protected static AuthorizationCodeBin instance;
	
	/**
	 * Default constructor.
	 */
	protected AuthorizationCodeBin() {
		instance = this;
	}
	
	/**
	 * Returns the singular instance of this class.
	 * 
	 * @return The singular instance of this class.
	 */
	public static AuthorizationCodeBin getInstance() {
		return instance;
	}
	
	/**
	 * Stores an existing authorization code.
	 * 
	 * @param code
	 *        The code to be saved.
	 * 
	 * @throws OmhException
	 *         The code is null.
	 */
	public abstract void storeCode(
		final AuthorizationCode code)
		throws OmhException;
	
	/**
	 * Retrieves the {@link AuthorizationCode} object based on the given
	 * authorization code string.
	 * 
	 * @param code
	 *        The authorization code string.
	 * 
	 * @return The {@link AuthorizationCode}.
	 * 
	 * @throws OmhException
	 *         Multiple copes of the same authorization code exist.
	 */
	public abstract AuthorizationCode getCode(
		final String code)
		throws OmhException;
}