package org.openmhealth.reference.request;

import java.net.URI;
import java.net.URISyntaxException;

import org.openmhealth.reference.data.ThirdPartyBin;
import org.openmhealth.reference.domain.AuthenticationToken;
import org.openmhealth.reference.domain.ThirdParty;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Allows a user to register a third-party application for this DSU.
 * </p>
 *
 * @author John Jenkins
 */
public class OauthRegistrationRequest extends Request<ThirdParty> {
	/**
	 * The requesting user's authentication token.
	 */
	private final AuthenticationToken authToken;
	/**
	 * The name of the third-party.
	 */
	private final String name;
	/**
	 * The description for the third-party.
	 */
	private final String description;
	/**
	 * The URI to redirect the user to after they have responded to an
	 * authorization request.
	 */
	private final URI redirectUri;

	/**
	 * Creates a new registration request for an OAuth third-party (a.k.a.
	 * "client").
	 * 
	 * @param authToken
	 *        The requesting user's authentication token.
	 * 
	 * @param name
	 *        The name for this third-party.
	 * 
	 * @param description
	 *        The description of this third-party.
	 * 
	 * @param redirectUri
	 *        The URI to redirect the user to after they have been given the
	 *        option to grant or deny an authorization request.
	 */
	public OauthRegistrationRequest(
		final AuthenticationToken authToken,
		final String name,
		final String description,
		final String redirectUri) {
		
		// Basic validation for the authentication token.
		if(authToken == null) {
			throw new OmhException("The authentication token is null.");
		}
		else {
			this.authToken = authToken;
		}
		
		// Basic validation for the name.
		if(name == null) {
			throw new OmhException("The name is null.");
		}
		else {
			this.name = name;
		}
		
		// Basic validation for the description.
		if(description == null) {
			throw new OmhException("The description is null.");
		}
		else {
			this.description = description;
		}
		
		// Basic validation for the redirect URI.
		if(redirectUri == null) {
			throw new OmhException("The redirect URI is null.");
		}
		else {
			try {
				this.redirectUri = new URI(redirectUri);
			}
			catch(URISyntaxException e) {
				throw
					new OmhException(
						"The redirect URI is not a valid URI.",
						e);
			}
			
			// TODO: We may want to do more thorough validation of the request
			// URI either here or in the ThirdParty constructor.
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.request.Request#service()
	 */
	@Override
	public void service() throws OmhException {
		// First, short-circuit if this request has already been serviced.
		if(isServiced()) {
			return;
		}
		else {
			setServiced();
		}
		
		// Create the ThirdParty object.
		ThirdParty result =
			new ThirdParty(
				authToken.getUser(), 
				name, 
				description, 
				redirectUri);
		
		// Store it.
		ThirdPartyBin.getInstance().storeThirdParty(result);
		
		// Return it.
		setData(result);
	}
}