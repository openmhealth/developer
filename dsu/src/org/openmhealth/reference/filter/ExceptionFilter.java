/*******************************************************************************
 * Copyright 2013 Open mHealth
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmhealth.reference.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.openmhealth.reference.exception.InvalidAuthenticationException;
import org.openmhealth.reference.exception.NoSuchSchemaException;
import org.openmhealth.reference.exception.OmhException;
import org.springframework.web.util.NestedServletException;

/**
 * <p>
 * A filter responsible for catching exceptions thrown by the requests and
 * adjusting the response accordingly.
 * </p>
 * 
 * <p>
 * For example, HTTP responses have their status code set to
 * {@link HttpServletResponse#SC_BAD_REQUEST} and the body of the response is
 * the error message.
 * </p>
 * 
 * @author John Jenkins
 */
public class ExceptionFilter implements Filter {
	/**
	 * The attribute key used to store any exception that was thrown for this
	 * request.
	 */
	public static final String ATTRIBUTE_KEY_EXCEPTION = "omh.exception";

	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = Logger
		.getLogger(ExceptionFilter.class.getName());

	/**
	 * Does nothing.
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		// Do nothing.
	}

	/**
	 * <p>
	 * If the request throws an exception, specifically a OmhException,
	 * attempt to respond with that message from the exception.
	 * </p>
	 * 
	 * <p>
	 * For example, HTTP responses have their status codes changed to
	 * {@link HttpServletResponse#SC_BAD_REQUEST} and the body of the response
	 * is the error message.
	 * </p>
	 */
	@Override
	public void doFilter(
		final ServletRequest request,
		final ServletResponse response,
		final FilterChain chain) throws IOException, ServletException {

		// Get a handler for the correct exception type.
		Throwable exception = null;

		// Always let the request continue but setup to catch exceptions.
		try {
			chain.doFilter(request, response);
		}
		// The servlet container may wrap the exception, in which case we
		// must first unwrap it, then delegate it.
		catch(NestedServletException e) {
			// Get the underlying cause.
			Throwable cause = e.getCause();

			// If the underlying exception is one of ours, then store the
			// underlying exception.
			if(cause instanceof OmhException) {
				exception = cause;
			}
			// Otherwise, store this exception.
			else {
				exception = e;
			}
		}
		// Otherwise, store the exception,
		catch(Exception e) {
			exception = e;
		}

		// If an exception was thrown, attempt to handle it.
		if(exception != null) {
			// Save the exception in the request.
			request.setAttribute(ATTRIBUTE_KEY_EXCEPTION, exception);

			// Handle the exception.
			if(exception instanceof NoSuchSchemaException) {
				LOGGER.log(
					Level.INFO,
					"An unknown schema was requested.",
					exception);

				// If it's a HTTP response, set the status code and set the
				// exception's message as the body of the response.
				if(response instanceof HttpServletResponse) {
					((HttpServletResponse) response).sendError(
						HttpServletResponse.SC_NOT_FOUND,
						exception.getMessage());
				}
				// Otherwise, simply set the request's body to the exception's
				// message.
				else {
					response.getWriter().write(exception.getMessage());
				}
			}
			else if(exception instanceof InvalidAuthenticationException) {
				LOGGER.log(
					Level.INFO,
					"A user's authentication information was invalid.",
					exception);

				// If it's a HTTP response, set the status code and set the
				// exception's message as the body of the response.
				if(response instanceof HttpServletResponse) {
					((HttpServletResponse) response).sendError(
						HttpServletResponse.SC_UNAUTHORIZED,
						exception.getMessage());
				}
				// Otherwise, simply set the request's body to the exception's
				// message.
				else {
					response.getWriter().write(exception.getMessage());
				}
			}
			else if(exception instanceof OmhException) {
				LOGGER.log(
					Level.INFO,
					"An invalid request was made.",
					exception);

				// If it's a HTTP response, set the status code and set the
				// exception's message as the body of the response.
				if(response instanceof HttpServletResponse) {
					((HttpServletResponse) response).sendError(
						HttpServletResponse.SC_BAD_REQUEST,
						exception.getMessage());
				}
				// Otherwise, simply set the request's body to the exception's
				// message.
				else {
					response.getWriter().write(exception.getMessage());
				}
			}
			// If the exception was not one of ours, the server must have
			// crashed.
			else {
				LOGGER.log(
					Level.SEVERE,
					"The server threw an unexpected exception.",
					exception);

				// If it's a HTTP response, set the status code and set the
				// exception's message as the body of the response.
				if(response instanceof HttpServletResponse) {
					((HttpServletResponse) response).sendError(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						"");
				}
				// Otherwise, simply set the request's body to the exception's
				// message.
				else {
					response.getWriter().write("");
				}
			}
		}
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void destroy() {
		// Do nothing.
	}
}