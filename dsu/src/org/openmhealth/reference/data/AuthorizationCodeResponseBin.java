package org.openmhealth.reference.data;

import org.openmhealth.reference.domain.AuthorizationCode;
import org.openmhealth.reference.domain.AuthorizationCodeResponse;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * The collection of authorization code responses.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class AuthorizationCodeResponseBin {
	/**
	 * The name of the DB document/table/whatever that contains the
	 * authorization codes' response.
	 */
	public static final String DB_NAME = "authorization_code_response_bin";
	
	/**
	 * The instance of this AuthorizationCodeResponseBin to use.
	 */
	private static AuthorizationCodeResponseBin instance;
	
	/**
	 * Default constructor.
	 */
	protected AuthorizationCodeResponseBin() {
		instance = this;
	}
	
	/**
	 * Returns the singular instance of this class.
	 * 
	 * @return The singular instance of this class.
	 */
	public static AuthorizationCodeResponseBin getInstance() {
		return instance;
	}
	
	/**
	 * Stores an existing authorization code response.
	 * 
	 * @param response
	 *        The response to be saved.
	 * 
	 * @throws OmhException
	 *         The response is null.
	 */
	public abstract void storeVerification(
		final AuthorizationCodeResponse response)
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
	 *         Multiple responses exist for the same authorization code.
	 */
	public abstract AuthorizationCodeResponse getResponse(
		final String code)
		throws OmhException;
}