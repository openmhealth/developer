package org.openmhealth.reference.data;

import org.openmhealth.reference.domain.ThirdParty;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * The collection of third-party entities.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class ThirdPartyBin {
	/**
	 * The name of the DB document/table/whatever that contains the user
	 * definitions.
	 */
	public static final String DB_NAME = "third_party_bin";
	
	/**
	 * The instance of this ThirdPartyBin to use. 
	 */
	protected static ThirdPartyBin instance;

	/**
	 * Default constructor.
	 */
	protected ThirdPartyBin() {
		instance = this;
	}
	
	/**
	 * Returns the singular instance of this class.
	 * 
	 * @return The singular instance of this class.
	 */
	public static ThirdPartyBin getInstance() {
		return instance;
	}
	
	/**
	 * Stores an existing ThirdParty object.
	 * 
	 * @param thirdParty
	 *        The third-party to be saved.
	 * 
	 * @throws OmhException
	 *         The third-party is null.
	 */
	public abstract void storeThirdParty(
		final ThirdParty thirdParty)
		throws OmhException;

	/**
	 * Retrieves the {@link ThirdParty} object from a third-party ID.
	 * 
	 * @param thirdParty
	 *        The third-party's unique identifier.
	 * 
	 * @return A {@link ThirdParty} object for the third-party or null if the
	 *         third party does not exist.
	 * 
	 * @throws OmhException
	 *         The user-name is null or multiple users have the same user-name.
	 */
	public abstract ThirdParty getThirdParty(
		final String thirdParty)
		throws OmhException;
}