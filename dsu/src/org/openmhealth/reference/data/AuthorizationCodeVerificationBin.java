package org.openmhealth.reference.data;

import org.openmhealth.reference.domain.AuthorizationCode;
import org.openmhealth.reference.domain.AuthorizationCodeVerification;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * The interface to the database-backed authorization code verification
 * repository.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class AuthorizationCodeVerificationBin {
	/**
	 * The name of the DB document/table/whatever that contains the
	 * authorization codes' verification.
	 */
	public static final String DB_NAME = "authorization_code_verification_bin";
	
	/**
	 * The instance of this AuthorizationCodeVerificationBin to use.
	 */
	protected static AuthorizationCodeVerificationBin instance;
	
	/**
	 * Default constructor.
	 */
	protected AuthorizationCodeVerificationBin() {
		instance = this;
	}
	
	/**
	 * Returns the singular instance of this class.
	 * 
	 * @return The singular instance of this class.
	 */
	public static AuthorizationCodeVerificationBin getInstance() {
		return instance;
	}
	
	/**
	 * Stores an existing authorization code verification.
	 * 
	 * @param verification
	 *        The verification to be saved.
	 * 
	 * @throws OmhException
	 *         The verification is null.
	 */
	public abstract void storeVerification(
		final AuthorizationCodeVerification verification)
		throws OmhException;
	
	/**
	 * Retrieves the {@link AuthorizationCode} object based on the given
	 * authorization code string.
	 * 
	 * @param code
	 *        The authorization code string.
	 * 
	 * @return The {@link AuthorizationCode} or null if the authorization code
	 *         has not yet been verified.
	 * 
	 * @throws OmhException
	 *         Multiple copes of the same authorization code verification
	 *         exist.
	 */
	public abstract AuthorizationCodeVerification getVerification(
		final String code)
		throws OmhException;
}