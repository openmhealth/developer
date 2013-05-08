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
 * Test everything about the {@link AuthorizationTokenVerification} class.
 * </p>
 *
 * @author John Jenkins
 */
public class AuthorizationCodeVerificationTest {
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
	 * A valid authorization code for these tests.
	 */
	public static final AuthorizationCode AUTHORIZATION_CODE =
		new AuthorizationCode(
			THIRD_PARTY_ID,
			CODE,
			CREATION_TIME,
			EXPIRATION_TIME,
			SCOPES,
			STATE);

	/**
	 * Test that an exception is thrown when the authorization code is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeVerificationAuthorizationCodeUserGrantedAuthorizationCodeNull() {
		new AuthorizationCodeVerification(null, USER, true);
	}

	/**
	 * Test that an exception is thrown when the user is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeVerificationAuthorizationCodeUserGrantedUserNull() {
		new AuthorizationCodeVerification(AUTHORIZATION_CODE, null, true);
	}

	/**
	 * Test that the verification can be created when it is granted.
	 */
	@Test
	public void testAuthorizationCodeVerificationAuthorizationCodeUserGrantedGrantedTrue() {
		new AuthorizationCodeVerification(AUTHORIZATION_CODE, USER, true);
	}

	/**
	 * Test that the verification can be created when it is not granted.
	 */
	@Test
	public void testAuthorizationCodeVerificationAuthorizationCodeUserGrantedGrantedFalse() {
		new AuthorizationCodeVerification(AUTHORIZATION_CODE, USER, false);
	}

	/**
	 * Test that an exception is thrown when the code is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeVerificationCodeOwnerGrantedCodeNull() {
		new AuthorizationCodeVerification(null, USERNAME, true);
	}

	/**
	 * Test that an exception is thrown when the owner is null.
	 */
	@Test(expected = OmhException.class)
	public void testAuthorizationCodeVerificationCodeOwnerGrantedOwnerNull() {
		new AuthorizationCodeVerification(CODE, null, true);
	}

	/**
	 * Test that the verification can be created when it is granted.
	 */
	@Test
	public void testAuthorizationCodeVerificationCodeOwnerGrantedGrantedTrue() {
		new AuthorizationCodeVerification(CODE, USERNAME, true);
	}

	/**
	 * Test that the verification can be created with it is not granted.
	 */
	@Test
	public void testAuthorizationCodeVerificationCodeOwnerGrantedGrantedFalse() {
		new AuthorizationCodeVerification(CODE, USERNAME, false);
	}

	/**
	 * Test that creating a new verification from an existing code causes the
	 * getter to return the same code with which the verification was built.
	 */
	@Test
	public void testGetAuthorizationCodeNewCode() {
		AuthorizationCodeVerification verification =
			new AuthorizationCodeVerification(AUTHORIZATION_CODE, USER, true);
		Assert.assertEquals(CODE, verification.getAuthorizationCode());
	}

	/**
	 * Test that reconstructing a verification from existing verification
	 * information causes the getter to return the same code.
	 */
	@Test
	public void testGetAuthorizationCodeOldCode() {
		AuthorizationCodeVerification verification =
			new AuthorizationCodeVerification(CODE, USERNAME, true);
		Assert.assertEquals(CODE, verification.getAuthorizationCode());
	}

	/**
	 * Test that, when creating a new verification that was granted, that the
	 * getter returns true.
	 */
	@Test
	public void testGetGrantedNewCodeGrantedTrue() {
		AuthorizationCodeVerification verification =
			new AuthorizationCodeVerification(AUTHORIZATION_CODE, USER, true);
		Assert.assertTrue(verification.getGranted());
	}

	/**
	 * Test that, when creating a new verification that was not granted, that 
	 * the getter returns false.
	 */
	@Test
	public void testGetGrantedNewCodeGrantedFalse() {
		AuthorizationCodeVerification verification =
			new AuthorizationCodeVerification(AUTHORIZATION_CODE, USER, false);
		Assert.assertFalse(verification.getGranted());
	}

	/**
	 * Test that, when reconstructing an old verification that was granted,
	 * that the getter returns true.
	 */
	@Test
	public void testGetGrantedOldCodeGrantedTrue() {
		AuthorizationCodeVerification verification =
			new AuthorizationCodeVerification(CODE, USERNAME, true);
		Assert.assertTrue(verification.getGranted());
	}

	/**
	 * Test that, when reconstructing an old verification that was not granted,
	 * that the getter returns false.
	 */
	@Test
	public void testGetGrantedOldCodeGrantedFalse() {
		AuthorizationCodeVerification verification =
			new AuthorizationCodeVerification(CODE, USERNAME, false);
		Assert.assertFalse(verification.getGranted());
	}
}