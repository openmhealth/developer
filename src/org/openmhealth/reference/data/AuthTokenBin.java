package org.openmhealth.reference.data;

import org.openmhealth.reference.domain.AuthToken;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * The interface to the database-backed authentication token repository.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class AuthTokenBin {
	/**
	 * The name of the DB document/table/whatever that contains the
	 * authentication tokens.
	 */
	public static final String AUTH_TOKEN_BIN_DB_NAME = "auth_token_bin";
	
	/**
	 * The instance of this AuthTokenBin to use. 
	 */
	protected static AuthTokenBin instance;
	
	/**
	 * Default constructor.
	 */
	protected AuthTokenBin() {
		AuthTokenBin.instance = this;
	}
	
	/**
	 * Returns the singular instance of this class.
	 * 
	 * @return The singular instance of this class.
	 */
	public static AuthTokenBin getInstance() {
		return instance;
	}

	/**
	 * Stores an existing authentication token.
	 * 
	 * @param token
	 *        The token to be saved.
	 * 
	 * @throws OmhException
	 *         The token is null.
	 */
	public abstract void storeToken(final AuthToken token) throws OmhException;
	
	/**
	 * Retrieves the user to which an authentication token has been applied.
	 * 
	 * @param token
	 *        The authentication token.
	 * 
	 * @return The user to which the authentication token belongs or null if
	 *         the the authentication token does not exist or is expired.
	 * 
	 * @throws OmhException
	 *         Multiple copies of the same authentication token exist.
	 */
	public abstract AuthToken getUser(final String token) throws OmhException;
}