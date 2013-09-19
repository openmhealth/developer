package org.openmhealth.reference.domain;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Test everything about the {@link AuthorizationCode} class.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthorizationCodeTest {
	/**
	 * The username for these tests.
	 */
	public static final String USERNAME = UserTest.USERNAME;
	/**
	 * The password for these tests.
	 */
	public static final String PASSWORD = UserTest.PASSWORD;
	/**
	 * The email address for these tests.
	 */
	public static final String EMAIL = UserTest.EMAIL_STRING;
	/**
	 * A valid user for these tests.
	 */
	public static final User USER =
		new User(USERNAME, PASSWORD, EMAIL, null, null, null);
	
	/**
	 * A valid third-party ID for these tests.
	 */
	public static final String THIRD_PARTY_ID = ThirdPartyTest.ID;
	/**
	 * A valid name for the testing third-party.
	 */
	public static final String THIRD_PARTY_NAME = ThirdPartyTest.NAME;
	/**
	 * A valid description for the testing third-party.
	 */
	public static final String THIRD_PARTY_DESCRIPTION =
		ThirdPartyTest.DESCRIPTION;
	/**
	 * A valid URI for the testing third-party.
	 */
	public static final URI THIRD_PARTY_URI = ThirdPartyTest.URI;
	/**
	 * A valid third-party for these tests.
	 */
	public static final ThirdParty THIRD_PARTY =
		new ThirdParty(
			USER,
			THIRD_PARTY_NAME,
			THIRD_PARTY_DESCRIPTION,
			THIRD_PARTY_URI);
	
	/**
	 * A valid code for these tests.
	 */
	public static final String CODE = UUID.randomUUID().toString();
	
	/**
	 * A valid creation time for these tests.
	 */
	public static final long CREATION_TIME = System.currentTimeMillis();
	
	/**
	 * A valid expiration time for these tests.
	 */
	public static final long EXPIRATION_TIME = CREATION_TIME + 1;
	
	/**
	 * A valid set of scopes for these tests.
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
	 * Test that an exception is thrown when the third-party is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeThirdPartyScopesStateThirdPartyNull() {
		new AuthorizationCode(null, SCOPES, STATE);
	}

	/**
	 * Test that an exception is thrown when the scopes are null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeThirdPartyScopesStateScopesNull() {
		new AuthorizationCode(THIRD_PARTY, null, STATE);
	}

	/**
	 * Test that an exception is thrown when the scopes are empty.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeThirdPartyScopesStateScopesEmpty() {
		new AuthorizationCode(
			THIRD_PARTY,
			Collections.<String>emptySet(),
			STATE);
	}

	/**
	 * Test that it is valid to have the state be null.
	 */
	@Test()
	public void testAuthorizationCodeThirdPartyScopesStateStateNull() {
		new AuthorizationCode(THIRD_PARTY, SCOPES, null);
	}

	/**
	 * Test that it is valid to have the state be empty.
	 */
	@Test()
	public void testAuthorizationCodeThirdPartyScopesStateStateEmpty() {
		new AuthorizationCode(THIRD_PARTY, SCOPES, "");
	}

	/**
	 * Test that a valid authorization code can be built.
	 */
	@Test()
	public void testAuthorizationCodeThirdPartyScopesState() {
		new AuthorizationCode(THIRD_PARTY, SCOPES, STATE);
	}

	/**
	 * Test that an exception is thrown when the third-party is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeThirdPartyCodeCreationTimeExpirationTimeScopesStateThirdPartyNull() {
		new AuthorizationCode(
			null,
			CODE,
			CREATION_TIME,
			EXPIRATION_TIME,
			SCOPES,
			STATE);
	}

	/**
	 * Test that an exception is thrown when the code is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeThirdPartyCodeCreationTimeExpirationTimeScopesStateCodeNull() {
		new AuthorizationCode(
			THIRD_PARTY_ID,
			null,
			CREATION_TIME,
			EXPIRATION_TIME,
			SCOPES,
			STATE);
	}

	/**
	 * Test that an exception is thrown when the creation time is in the
	 * future.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeThirdPartyCodeCreationTimeExpirationTimeScopesStateCreationTimeFuture() {
		new AuthorizationCode(
			THIRD_PARTY_ID,
			CODE,
			System.currentTimeMillis() + 60000L,
			EXPIRATION_TIME,
			SCOPES,
			STATE);
	}

	/**
	 * Test that an exception is thrown when the expiration time is before the
	 * creation time.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeThirdPartyCodeCreationTimeExpirationTimeScopesStateExpirationTimeBeforeCreationTime() {
		new AuthorizationCode(
			THIRD_PARTY_ID,
			CODE,
			CREATION_TIME,
			CREATION_TIME - 1,
			SCOPES,
			STATE);
	}

	/**
	 * Test that an exception is thrown when the scope is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeThirdPartyCodeCreationTimeExpirationTimeScopesStateScopesNull() {
		new AuthorizationCode(
			THIRD_PARTY_ID,
			CODE,
			CREATION_TIME,
			EXPIRATION_TIME,
			null,
			STATE);
	}

	/**
	 * Test that an exception is thrown when the scope is empty.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeThirdPartyCodeCreationTimeExpirationTimeScopesStateScopesEmpty() {
		new AuthorizationCode(
			THIRD_PARTY_ID,
			CODE,
			CREATION_TIME,
			EXPIRATION_TIME,
			Collections.<String>emptySet(),
			STATE);
	}

	/**
	 * Test that it is valid to have a null state.
	 */
	@Test
	public void testAuthorizationCodeThirdPartyCodeCreationTimeExpirationTimeScopesStateStateNull() {
		new AuthorizationCode(
			THIRD_PARTY_ID,
			CODE,
			CREATION_TIME,
			EXPIRATION_TIME,
			SCOPES,
			null);
	}

	/**
	 * Test that it is valid to have an empty state.
	 */
	@Test
	public void testAuthorizationCodeThirdPartyCodeCreationTimeExpirationTimeScopesStateStateEmpty() {
		new AuthorizationCode(
			THIRD_PARTY_ID,
			CODE,
			CREATION_TIME,
			EXPIRATION_TIME,
			SCOPES,
			"");
	}

	/**
	 * Test that a valid authorization code can be built.
	 */
	@Test
	public void testAuthorizationCodeThirdPartyCodeCreationTimeExpirationTimeScopesState() {
		new AuthorizationCode(
			THIRD_PARTY_ID,
			CODE,
			CREATION_TIME,
			EXPIRATION_TIME,
			SCOPES,
			STATE);
	}

	/**
	 * Test that, when creating a new authorization code, that the code is not
	 * null.
	 */
	@Test
	public void testGetCodeNotNull() {
		AuthorizationCode code =
			new AuthorizationCode(THIRD_PARTY, SCOPES, STATE);
		Assert.assertNotNull(code.getCode());
	}

	/**
	 * Test that, when creating an authorization code from existing data, that
	 * the returned authorization code is the same as the one that was given.
	 */
	@Test
	public void testGetCodeMatches() {
		AuthorizationCode code =
			new AuthorizationCode(
				THIRD_PARTY_ID,
				CODE,
				CREATION_TIME,
				EXPIRATION_TIME,
				SCOPES,
				STATE);
		Assert.assertEquals(CODE, code.getCode());
	}

	/**
	 * Test that the scopes given when building a new authorization code are
	 * the same as the ones returned from the getter.
	 */
	@Test
	public void testGetScopesNewCode() {
		AuthorizationCode code =
			new AuthorizationCode(THIRD_PARTY, SCOPES, STATE);
		Set<String> scopes = code.getScopes();
		Assert.assertNotNull(scopes);
		Assert.assertEquals(SCOPES, scopes);
	}

	/**
	 * Test that the scopes given when reconstructing an old authorization code
	 * are the same as the ones returned from the getter.
	 */
	@Test
	public void testGetScopesOldCode() {
		AuthorizationCode code =
			new AuthorizationCode(
				THIRD_PARTY_ID,
				CODE,
				CREATION_TIME,
				EXPIRATION_TIME,
				SCOPES,
				STATE);
		Set<String> scopes = code.getScopes();
		Assert.assertNotNull(scopes);
		Assert.assertEquals(SCOPES, scopes);
	}

	/**
	 * Test that the set of scopes returned from the getter are unmodifiable.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testGetScopesUnmodifiable() {
		AuthorizationCode code =
			new AuthorizationCode(THIRD_PARTY, SCOPES, STATE);
		code.getScopes().add(null);
	}
	
	/**
	 * Test that the creation time given when reconstructing an old
	 * authorization code is the same as the one returned from the getter.
	 */
	@Test
	public void testGetCreationTimeOldCode() {
		AuthorizationCode code =
			new AuthorizationCode(
				THIRD_PARTY_ID,
				CODE,
				CREATION_TIME,
				EXPIRATION_TIME,
				SCOPES,
				STATE);
		Assert.assertEquals(CREATION_TIME, code.getCreationTime());
	}

	/**
	 * Test that the expiration time for a new code is equal to the creation
	 * time plus the default duration.
	 */
	@Test
	public void testGetExpirationTimeNewCode() {
		AuthorizationCode code =
			new AuthorizationCode(THIRD_PARTY, SCOPES, STATE);
		Assert
			.assertEquals(
				code.getCreationTime() + 
					AuthorizationCode.DEFAULT_CODE_LIFETIME_MILLIS, 
				code.getExpirationTime());
	}

	/**
	 * Test that the expiration time for a reconstructed code is equal to the
	 * given time.
	 */
	@Test
	public void testGetExpirationTimeOldCode() {
		AuthorizationCode code =
			new AuthorizationCode(
				THIRD_PARTY_ID,
				CODE,
				CREATION_TIME,
				EXPIRATION_TIME,
				SCOPES,
				STATE);
		Assert.assertEquals(EXPIRATION_TIME, code.getExpirationTime());
	}

	/**
	 * Test that the given state when creating a new authorization code is the
	 * same as the one returned from the getter.
	 */
	@Test
	public void testGetStateNewCode() {
		AuthorizationCode code =
			new AuthorizationCode(THIRD_PARTY, SCOPES, STATE);
		Assert.assertEquals(STATE, code.getState());
	}

	/**
	 * Test that the given state when reconstructing an old code is the same as
	 * the one returned from the getter.
	 */
	@Test
	public void testGetStateOldCode() {
		AuthorizationCode code =
			new AuthorizationCode(
				THIRD_PARTY_ID,
				CODE,
				CREATION_TIME,
				EXPIRATION_TIME,
				SCOPES,
				STATE);
		Assert.assertEquals(STATE, code.getState());
	}
}