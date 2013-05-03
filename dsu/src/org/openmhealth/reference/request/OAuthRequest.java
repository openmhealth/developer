package org.openmhealth.reference.request;

import java.net.URI;

/**
 * <p>
 * The interface for all OAuth requests.
 * </p>
 *
 * @author John Jenkins
 */
public interface OAuthRequest {
	/**
	 * Returns the URI to use to redirect the user when responding to a token
	 * request.
	 * 
	 * @return The URI to redirect the user back to the third-party.
	 */
	public URI getRedirectUri();
}
