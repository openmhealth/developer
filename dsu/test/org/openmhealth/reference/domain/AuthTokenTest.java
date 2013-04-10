package org.openmhealth.reference.domain;

import org.junit.Assert;
import org.junit.Test;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Test everything about the {@link AuthToken} class.
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
		new AuthToken(null, USERNAME, GRANTED, EXPIRES);
	}

	/**
	 * Test that an username must be given to the constructor.
	 */
	@Test(expected = OmhException.class)
	public void testAuthTokenStringStringLongLongUsernameNull() {
		new AuthToken(TOKEN, null, GRANTED, EXPIRES);
	}

	/**
	 * Test that the take cannot be granted in the future.
	 */
	@Test(expected = OmhException.class)
	public void testAuthTokenStringStringLongLongGrantedInFuture() {
		new AuthToken(TOKEN, USERNAME, GRANTED + (60000), EXPIRES);
	}

	/**
	 * Test that the token cannot expire before it was granted.
	 */
	@Test(expected = OmhException.class)
	public void testAuthTokenStringStringLongLongExpiresBeforeGranted() {
		new AuthToken(TOKEN, USERNAME, GRANTED, GRANTED - 1);
	}

	/**
	 * Test that an authentication token can be created when the expiration
	 * equals the granted time.
	 */
	@Test
	public void testAuthTokenStringStringLongLongExpiresEqualsGranted() {
		new AuthToken(TOKEN, USERNAME, GRANTED, GRANTED);
	}

	/**
	 * Test that an authentication token can be created from specific 
	 * parameters.
	 */
	@Test
	public void testAuthTokenStringStringLongLong() {
		new AuthToken(TOKEN, USERNAME, GRANTED, EXPIRES);
	}

	/**
	 * Test that a user must be given to the constructor.
	 */
	@Test(expected = OmhException.class)
	public void testAuthTokenUserNull() {
		new AuthToken(null);
	}

	/**
	 * Test that an authentication token can be created from a user.
	 */
	@Test
	public void testAuthTokenUser() {
		new AuthToken(USER);
	}

	/**
	 * Test that the same authentication token given can be retrieved.
	 */
	@Test
	public void testGetToken() {
		AuthToken authToken = new AuthToken(TOKEN, USERNAME, GRANTED, EXPIRES);
		Assert.assertEquals(TOKEN, authToken.getToken());
	}

	/**
	 * Test that a random authentication token will be generated when creating
	 * an authentication token from a user.
	 */
	@Test
	public void testGetTokenUser() {
		AuthToken authToken = new AuthToken(USER);
		Assert.assertNotNull(authToken.getToken());
	}

	/**
	 * Test that the same username given can be retrieved.
	 */
	@Test
	public void testGetUsername() {
		AuthToken authToken = new AuthToken(TOKEN, USERNAME, GRANTED, EXPIRES);
		Assert.assertEquals(USERNAME, authToken.getUsername());
	}

	/**
	 * Test that, when creating an authentication token with a User object, the
	 * user's username is the same as the one associated with the 
	 * authentication token.
	 */
	@Test
	public void testGetUsernameUser() {
		AuthToken authToken = new AuthToken(USER);
		Assert.assertEquals(USER.getUsername(), authToken.getUsername());
	}

	/**
	 * Test that the same granted given can be retrieved.
	 */
	@Test
	public void testGetGranted() {
		AuthToken authToken = new AuthToken(TOKEN, USERNAME, GRANTED, EXPIRES);
		Assert.assertEquals(GRANTED, authToken.getGranted());
	}

	/**
	 * Test that the same expires given can be retrieved.
	 */
	@Test
	public void testGetExpires() {
		AuthToken authToken = new AuthToken(TOKEN, USERNAME, GRANTED, EXPIRES);
		Assert.assertEquals(EXPIRES, authToken.getExpires());
	}

	/**
	 * Test that the expiration on the request is the default authentication
	 * token lifetime.
	 */
	@Test
	public void testGetExpiresUser() {
		AuthToken authToken = new AuthToken(USER);
		Assert
			.assertEquals(
				authToken.getGranted() + AuthToken.AUTH_TOKEN_LIFETIME,
				authToken.getExpires());
	}
}