package org.openmhealth.reference.domain;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Test everything about the {@link AuthorizationToken} class.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthorizationTokenTest {
	/**
	 * The username for these tests.
	 */
	public static final String USERNAME = UserTest.USERNAME;
	/**
	 * The password for these tests.
	 */
	public static final String PASSWORD = UserTest.PASSWORD;
	/**
	 * A valid user for these tests.
	 */
	public static final User USER = new User(USERNAME, PASSWORD);

	/**
	 * A valid third-party ID for these tests.
	 */
	public static final String THIRD_PARTY_ID = UUID.randomUUID().toString();
	/**
	 * A valid name for the testing third-party.
	 */
	public static final String THIRD_PARTY_NAME = "third_party_name";
	/**
	 * A valid description for the testing third-party.
	 */
	public static final String THIRD_PARTY_DESCRIPTION =
		"third_party_description";
	/**
	 * A valid URI for the testing third-party.
	 */
	public static final URI THIRD_PARTY_URI;
	static {
		URI result = null;
		try {
			result = new URI("http://localhost:8080/");
		}
		catch(URISyntaxException e) {
			fail("Could not build the testing URI.");
		}
		THIRD_PARTY_URI = result;
	}
	/**
	 * A valid third-party for these tests.
	 */
	public static final ThirdParty THIRD_PARTY = new ThirdParty(
		USER,
		THIRD_PARTY_NAME,
		THIRD_PARTY_DESCRIPTION,
		THIRD_PARTY_URI);

	/**
	 * A valid code for these tests.
	 */
	public static final String CODE = UUID.randomUUID().toString();

	/**
	 * A valid creation time for the authorization code for these tests.
	 */
	public static final long AURTHORIZATION_CODE_CREATION_TIME = System
		.currentTimeMillis();

	/**
	 * A valid expiration time for these tests.
	 */
	public static final long AURTHORIZATION_CODE_EXPIRATION_TIME =
		AURTHORIZATION_CODE_CREATION_TIME +
		AuthorizationCode.DEFAULT_CODE_LIFETIME_MILLIS;

	/**
	 * A valid set of scopes for the authorization code for these tests.
	 */
	public static final Set<String> SCOPES = new HashSet<String>();
	static {
		SCOPES.add("omh:test");
	}

	/**
	 * A valid state for these tests.
	 */
	public static final String STATE = "";

	/**
	 * A valid authorization code for these tests.
	 */
	public static final AuthorizationCode AUTHORIZATION_CODE =
		new AuthorizationCode(
			THIRD_PARTY_ID,
			CODE,
			AURTHORIZATION_CODE_CREATION_TIME,
			AURTHORIZATION_CODE_EXPIRATION_TIME,
			SCOPES,
			STATE);

	/**
	 * A valid access token for these tests.
	 */
	public static final String ACCESS_TOKEN = UUID.randomUUID().toString();
	/**
	 * A valid refresh token for these tests.
	 */
	public static final String REFRESH_TOKEN = UUID.randomUUID().toString();

	/**
	 * A valid creation time for these tests.
	 */
	public static final long CREATION_TIME = System.currentTimeMillis();

	/**
	 * A valid expiration time for these tests.
	 */
	public static final long EXPIRATION_TIME = CREATION_TIME + 1;

	/**
	 * A valid, authorized verification for these tests.
	 */
	public static final AuthorizationCodeVerification VERIFICATION =
		new AuthorizationCodeVerification(AUTHORIZATION_CODE, USER, true);

	/**
	 * Test that an exception is thrown when the verification is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationTokenAuthorizationCodeVerificationVerificationNull() {
		new AuthorizationToken((AuthorizationCodeVerification) null);
	}

	/**
	 * Test that a token can be created given a valid verification.
	 */
	@Test
	public void testAuthorizationTokenAuthorizationCodeVerification() {
		new AuthorizationToken(VERIFICATION);
	}

	/**
	 * Test that an exception is thrown when the original authorization token
	 * is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationTokenAuthorizationTokenAuthorizationTokenNull() {
		new AuthorizationToken((AuthorizationToken) null);
	}

	/**
	 * Test that a new authorization token can be created from an existing
	 * authorization token.
	 */
	@Test
	public void testAuthorizationTokenAuthorizationToken() {
		AuthorizationToken token = new AuthorizationToken(VERIFICATION);
		new AuthorizationToken(token);
	}

	/**
	 * Test that an exception is thrown when the authorization code is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationTokenCodeAccessTokenRefreshTokenCreationTimeExpirationTimeCodeNull() {
		new AuthorizationToken(
			null,
			ACCESS_TOKEN,
			REFRESH_TOKEN,
			CREATION_TIME,
			EXPIRATION_TIME);
	}

	/**
	 * Test that an exception is thrown when the access token is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationTokenCodeAccessTokenRefreshTokenCreationTimeExpirationTimeAccessTokenNull() {
		new AuthorizationToken(
			CODE,
			null,
			REFRESH_TOKEN,
			CREATION_TIME,
			EXPIRATION_TIME);
	}

	/**
	 * Test that an exception is thrown when the refresh token is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationTokenCodeAccessTokenRefreshTokenCreationTimeExpirationTimeRefreshTokenNull() {
		new AuthorizationToken(
			CODE,
			ACCESS_TOKEN,
			null,
			CREATION_TIME,
			EXPIRATION_TIME);
	}

	/**
	 * Test that an exception is thrown when the creation time is in the
	 * future.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationTokenCodeAccessTokenRefreshTokenCreationTimeExpirationTimeCreationTimeInFuture() {
		new AuthorizationToken(
			CODE,
			ACCESS_TOKEN,
			REFRESH_TOKEN,
			System.currentTimeMillis() + 60000,
			EXPIRATION_TIME);
	}

	/**
	 * Test that an exception is thrown when the expiration time is before the
	 * creation time.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationTokenCodeAccessTokenRefreshTokenCreationTimeExpirationTimeExpirationTimeBeforeCreationTime() {
		new AuthorizationToken(
			CODE,
			ACCESS_TOKEN,
			REFRESH_TOKEN,
			CREATION_TIME,
			CREATION_TIME - 1);
	}

	/**
	 * Test that a valid authorization token can be recreated.
	 */
	@Test
	public void testAuthorizationTokenCodeAccessTokenRefreshTokenCreationTimeExpirationTime() {
		new AuthorizationToken(
			CODE,
			ACCESS_TOKEN,
			REFRESH_TOKEN,
			CREATION_TIME,
			EXPIRATION_TIME);
	}

	/**
	 * Test that a new authorization token built from an authorization code
	 * verification returns a non-null access token.
	 */
	@Test
	public void testGetAccessTokenBrandNewToken() {
		AuthorizationToken token = new AuthorizationToken(VERIFICATION);
		Assert.assertNotNull(token.getAccessToken());
	}

	/**
	 * Test that a new authorization token based off of an existing
	 * authorization token results its a distinct, non-null access token.
	 */
	@Test
	public void testGetAccessTokenNewToken() {
		AuthorizationToken originalToken =
			new AuthorizationToken(VERIFICATION);
		AuthorizationToken newToken = new AuthorizationToken(originalToken);
		Assert.assertNotNull(newToken.getAccessToken());
		Assert
			.assertNotEquals(
				originalToken.getAccessToken(),
				newToken.getAccessToken());
	}

	/**
	 * Test that an authorization token that is being reconstructed from an
	 * existing authorization token has a getter that returns the same access
	 * token with which it was built.
	 */
	@Test
	public void testGetAccessTokenOldToken() {
		AuthorizationToken token =
			new AuthorizationToken(
				CODE,
				ACCESS_TOKEN,
				REFRESH_TOKEN,
				CREATION_TIME,
				EXPIRATION_TIME);
		Assert.assertEquals(ACCESS_TOKEN, token.getAccessToken());
	}

	/**
	 * Test that a new authorization token built from an authorization code
	 * verification returns a non-null refresh token.
	 */
	@Test
	public void testGetRefreshTokenBrandNewToken() {
		AuthorizationToken token = new AuthorizationToken(VERIFICATION);
		Assert.assertNotNull(token.getRefreshToken());
	}

	/**
	 * Test that a new authorization token based off of an existing
	 * authorization token results its a distinct, non-null refresh token.
	 */
	@Test
	public void testGetRefreshTokenNewToken() {
		AuthorizationToken originalToken =
			new AuthorizationToken(VERIFICATION);
		AuthorizationToken newToken = new AuthorizationToken(originalToken);
		Assert.assertNotNull(newToken.getRefreshToken());
		Assert
			.assertNotEquals(
				originalToken.getRefreshToken(),
				newToken.getRefreshToken());
	}

	/**
	 * Test that an authorization token that is being reconstructed from an
	 * existing authorization token has a getter that returns the same refresh
	 * token with which it was built.
	 */
	@Test
	public void testGetRefreshTokenOldToken() {
		AuthorizationToken token =
			new AuthorizationToken(
				CODE,
				ACCESS_TOKEN,
				REFRESH_TOKEN,
				CREATION_TIME,
				EXPIRATION_TIME);
		Assert.assertEquals(REFRESH_TOKEN, token.getRefreshToken());
	}

	/**
	 * Test that a new authorization token from an authorization code
	 * verification has an expiration time sometime in the future.
	 */
	@Test
	public void testGetExpirationInBrandNewToken() {
		AuthorizationToken token = new AuthorizationToken(VERIFICATION);
		Assert.assertTrue(0L < token.getExpirationIn());
	}

	/**
	 * Test that a new authorization token based off of an existing
	 * authorization token has an expiration time sometime in the future.
	 */
	@Test
	public void testGetExpirationInNewToken() {
		AuthorizationToken originalToken =
			new AuthorizationToken(VERIFICATION);
		AuthorizationToken newToken = new AuthorizationToken(originalToken);
		Assert.assertTrue(0L < newToken.getExpirationIn());
	}
}