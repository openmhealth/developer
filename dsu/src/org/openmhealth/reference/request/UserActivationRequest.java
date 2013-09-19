package org.openmhealth.reference.request;

import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Activates a user account from a valid registration ID.
 * </p>
 *
 * @author John Jenkins
 */
public class UserActivationRequest extends Request<Object> {
	/**
	 * The path to the web page that will perform the actual activation.
	 */
	public static final String ACTIVATION_PAGE = "/users/Activation.html";
	/**
	 * The path to this API after the mandatory path and the version, e.g.
	 * /omh/v1.
	 */
	public static final String PATH = "/users/activation";
	
	/**
	 * The registration ID to use to find and activate a user.
	 */
	private final String registrationId;

	/**
	 * Creates an user activation request.
	 * 
	 * @param registrationId
	 *        The registration ID.
	 */
	public UserActivationRequest(
		final String registrationId)
		throws OmhException {
		
		if(registrationId == null) {
			throw new OmhException("The registration ID is null.");
		}
		if(registrationId.trim().length() == 0) {
			throw new OmhException("The registration ID is empty.");
		}
		
		this.registrationId = registrationId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.request.Request#service()
	 */
	@Override
	public void service() throws OmhException {
		// Get the user.
		User user =
			UserBin.getInstance().getUserFromRegistrationId(registrationId);
		
		// Verify that the registration ID returned an actual user.
		if(user == null) {
			throw new OmhException("The registration ID is unknown.");
		}
		
		// Activate the account.
		user.activate();
		
		// Save the account.
		UserBin.getInstance().updateUser(user);
	}
}