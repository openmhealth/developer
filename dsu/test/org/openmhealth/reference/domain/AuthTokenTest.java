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
package org.openmhealth.reference.domain;

import org.junit.Assert;
import org.junit.Test;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Test everything about the {@link AuthenticationToken} class.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthTokenTest {
	/**
	 * A valid token to use for testing.
	 */
	public static final String TOKEN = "abc123";
	/**
	 * A valid username to use for testing.
	 */
	public static final String USERNAME = "Test.User";
	/**
	 * A valid granted value to use for testing.
	 */
	public static final long GRANTED = System.currentTimeMillis();
	/**
	 * A valid expiration value to use for testing.
	 */
	public static final long EXPIRES = GRANTED + 1;
	/**
	 * A {@link User} object to use for testing.
	 */
	public static final User USER = new User(USERNAME, "Test.Password0");

	/**
	 * Test that an authentication token must be given to the constructor.
	 */
	@Test(expected = OmhException.class)
	public void testAuthTokenStringStringLongLongTokenNull() {
		new AuthenticationToken(null, USERNAME, GRANTED, EXPIRES);
	}

	/**
	 * Test that an username must be given to the constructor.
	 */
	@Test(expected = OmhException.class)
	public void testAuthTokenStringStringLongLongUsernameNull() {
		new AuthenticationToken(TOKEN, null, GRANTED, EXPIRES);
	}

	/**
	 * Test that the take cannot be granted in the future.
	 */
	@Test(expected = OmhException.class)
	public void testAuthTokenStringStringLongLongGrantedInFuture() {
		new AuthenticationToken(TOKEN, USERNAME, GRANTED + (60000), EXPIRES);
	}

	/**
	 * Test that the token cannot expire before it was granted.
	 */
	@Test(expected = OmhException.class)
	public void testAuthTokenStringStringLongLongExpiresBeforeGranted() {
		new AuthenticationToken(TOKEN, USERNAME, GRANTED, GRANTED - 1);
	}

	/**
	 * Test that an authentication token can be created when the expiration
	 * equals the granted time.
	 */
	@Test
	public void testAuthTokenStringStringLongLongExpiresEqualsGranted() {
		new AuthenticationToken(TOKEN, USERNAME, GRANTED, GRANTED);
	}

	/**
	 * Test that an authentication token can be created from specific 
	 * parameters.
	 */
	@Test
	public void testAuthTokenStringStringLongLong() {
		new AuthenticationToken(TOKEN, USERNAME, GRANTED, EXPIRES);
	}

	/**
	 * Test that a user must be given to the constructor.
	 */
	@Test(expected = OmhException.class)
	public void testAuthTokenUserNull() {
		new AuthenticationToken(null);
	}

	/**
	 * Test that an authentication token can be created from a user.
	 */
	@Test
	public void testAuthTokenUser() {
		new AuthenticationToken(USER);
	}

	/**
	 * Test that the same authentication token given can be retrieved.
	 */
	@Test
	public void testGetToken() {
		AuthenticationToken authToken = new AuthenticationToken(TOKEN, USERNAME, GRANTED, EXPIRES);
		Assert.assertEquals(TOKEN, authToken.getToken());
	}

	/**
	 * Test that a random authentication token will be generated when creating
	 * an authentication token from a user.
	 */
	@Test
	public void testGetTokenUser() {
		AuthenticationToken authToken = new AuthenticationToken(USER);
		Assert.assertNotNull(authToken.getToken());
	}

	/**
	 * Test that the same username given can be retrieved.
	 */
	@Test
	public void testGetUsername() {
		AuthenticationToken authToken = new AuthenticationToken(TOKEN, USERNAME, GRANTED, EXPIRES);
		Assert.assertEquals(USERNAME, authToken.getUsername());
	}

	/**
	 * Test that, when creating an authentication token with a User object, the
	 * user's username is the same as the one associated with the 
	 * authentication token.
	 */
	@Test
	public void testGetUsernameUser() {
		AuthenticationToken authToken = new AuthenticationToken(USER);
		Assert.assertEquals(USER.getUsername(), authToken.getUsername());
	}

	/**
	 * Test that the same granted given can be retrieved.
	 */
	@Test
	public void testGetGranted() {
		AuthenticationToken authToken = new AuthenticationToken(TOKEN, USERNAME, GRANTED, EXPIRES);
		Assert.assertEquals(GRANTED, authToken.getGranted());
	}

	/**
	 * Test that the same expires given can be retrieved.
	 */
	@Test
	public void testGetExpires() {
		AuthenticationToken authToken = new AuthenticationToken(TOKEN, USERNAME, GRANTED, EXPIRES);
		Assert.assertEquals(EXPIRES, authToken.getExpires());
	}

	/**
	 * Test that the expiration on the request is the default authentication
	 * token lifetime.
	 */
	@Test
	public void testGetExpiresUser() {
		AuthenticationToken authToken = new AuthenticationToken(USER);
		Assert
			.assertEquals(
				authToken.getGranted() + AuthenticationToken.AUTH_TOKEN_LIFETIME,
				authToken.getExpires());
	}
}
