package org.openmhealth.reference.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.openmhealth.reference.data.AuthenticationTokenBin;
import org.openmhealth.reference.data.AuthorizationTokenBin;
import org.openmhealth.reference.domain.AuthenticationToken;
import org.openmhealth.reference.domain.AuthorizationToken;
import org.openmhealth.reference.exception.InvalidAuthenticationException;
import org.openmhealth.reference.exception.InvalidAuthorizationException;
import org.openmhealth.reference.servlet.Version1;

/**
 * <p>
 * This filter is responsible for retrieving the authentication and
 * authorization information, validating them, and associating them with the
 * request.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthFilter implements Filter {
	/**
	 * The Authorization header from HTTP requests.
	 */
	public static final String HEADER_AUTHORIZATION = "Authorization";
	/**
	 * The OAuth Authorization type.
	 */
	public static final String HEADER_AUTHORIZATION_BEARER = "Bearer";
	
	/**
	 * The attribute for an authenticated authentication token from the
	 * request. 
	 */
	public static final String ATTRIBUTE_AUTHENTICATION_TOKEN =
		"omh_authentication_token";
	/**
	 * The attribute that indicates whether or not the authentication came from
	 * the parameter list, as opposed to from a cookie.
	 */
	public static final String ATTRIBUTE_AUTHENTICATION_TOKEN_IS_PARAM =
		"omh_user_is_param";
	/**
	 * The attribute for an authorization token from a third party.
	 */
	public static final String ATTRIBUTE_AUTHORIZATION_TOKEN =
		"omh_authenticated_authorization_token";
	
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(final FilterConfig config) throws ServletException {
		// Do nothing.
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(
		final ServletRequest request,
		final ServletResponse response,
		final FilterChain chain)
		throws IOException, ServletException {
		
		// Create a holder for the authentication token. There is no concern
		// how many times it is sent as long as they are all the same.
		String authToken = null;
		
		// Get the authentication tokens from the cookies.
		if(request instanceof HttpServletRequest) {
			// Cast the HTTP request.
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			
			// Get the array of cookies.
			Cookie[] cookies = httpRequest.getCookies();
			
			// If any cookies were given, cycle through them to find any that
			// match the authentication cookie name.
			if(cookies != null) {
				for(Cookie cookie : cookies) {
					if(Version1
						.PARAM_AUTHENTICATION_AUTH_TOKEN
						.equals(cookie.getName())) {
						
						// If we have not yet found a token, save this one.
						if(authToken == null) {
							authToken = cookie.getValue();
						}
						// If we have a token, make sure that this token is
						// identical to it. 
						else if(! authToken.equals(cookie.getValue())) {
							throw
								new InvalidAuthenticationException(
									"Multiple, different authentication " +
										"token cookies were given.");
						}
					}
				}
			}
		}
		
		// Get the authentication tokens from the parameters.
		boolean authTokenIsFromParameters = false;
		String[] authTokenParameters =
			request
				.getParameterValues(Version1.PARAM_AUTHENTICATION_AUTH_TOKEN);
		// If any authentication token parameters exist, validate that there is
		// only one and then remember it.
		if(authTokenParameters != null) {
			// Cycle through the array and check all of the authentication
			// tokens.
			for(int i = 0; i < authTokenParameters.length; i++) {
				// As long as their is one element in the array, this can be
				// set. If there is a problem with the input, this being set
				// will be irrelevant.
				authTokenIsFromParameters = true;
				
				// If we have not yet found a token, save this one.
				if(authToken == null) {
					authToken = authTokenParameters[i];
				}
				// If we have a token, make sure that this token is
				// identical to it. 
				else if(! authToken.equals(authTokenParameters[i])) {
					throw
						new InvalidAuthenticationException(
							"Multiple, different authentication token " +
								"parameters were given.");
				}
			}
		}
		
		// If we found a token, store it.
		if(authToken != null) {
			// Attempt to get the authentication token.
			AuthenticationToken authTokenObject =
				AuthenticationTokenBin.getInstance().getToken(authToken);
			if(authTokenObject == null) {
				throw
					new InvalidAuthenticationException(
						"The authentication token is unknown or has expired.");
			}
				
			// Associate the authentication token with the request.
			request
				.setAttribute(ATTRIBUTE_AUTHENTICATION_TOKEN, authTokenObject);
		}

		// Indicate if the token used to find this user was a parameter or not.
		request
			.setAttribute(
				ATTRIBUTE_AUTHENTICATION_TOKEN_IS_PARAM,
				authTokenIsFromParameters);
		
		// Attempt to get the authentication from the third-party.
		if(request instanceof HttpServletRequest) {
			// Cast the request.
			HttpServletRequest httpRequest = (HttpServletRequest) request;
				
			// Get a placeholder for the authorization token.
			String authorizationTokenString = null;
			
			// Get the authorization headers, of which there may be multiple.
			Enumeration<String> authorizations =
				httpRequest.getHeaders(HEADER_AUTHORIZATION);
			
			// Cycle through each element and process the "Bearer" type.
			String currElement;
			while(authorizations.hasMoreElements()) {
				// Get the next element.
				currElement = authorizations.nextElement();
				
				// Split it to find the type and value.
				String[] currElementParts = currElement.split(" ");
				
				// If there aren't exactly two parts, then we cannot
				// process it.
				if(currElementParts.length != 2) {
					continue;
				}
				
				// If the type is the Bearer, then process it.
				if(HEADER_AUTHORIZATION_BEARER.equals(currElementParts[0])) {
					if(authorizationTokenString == null) {
						authorizationTokenString = currElementParts[1];
					}
					else if(
						! authorizationTokenString
							.equals(currElementParts[1])) {
						
						throw
							new InvalidAuthorizationException(
								"Multiple, different third-party " +
									"credentials were provided as '" + 
									HEADER_AUTHORIZATION_BEARER +
									"' " +
									HEADER_AUTHORIZATION +
									" headers.");
					}
				}
			}
			
			// If the authorization token was given, attempt to add the
			// third-party to the request.
			if(authorizationTokenString != null) {
				// Attempt to get the authorization token.
				AuthorizationToken authorizationToken =
					AuthorizationTokenBin
						.getInstance()
						.getTokenFromAccessToken(authorizationTokenString);
				
				// If the token is null, it does not exist or is expired.
				if(authorizationToken == null) {
					throw
						new InvalidAuthorizationException(
							"The authorization token is unknown or " +
								"expired.");
				}
				
				// Add the token to the request as an attribute.
				request
					.setAttribute(
						ATTRIBUTE_AUTHORIZATION_TOKEN, 
						authorizationToken);
			}
		}
		
		// Continue along the filter chain.
		chain.doFilter(request, response);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// Do nothing.
	}
}