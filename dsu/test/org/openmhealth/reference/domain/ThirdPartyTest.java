package org.openmhealth.reference.domain;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Test everything about the {@link ThirdParty} class.
 * </p>
 *
 * @author John Jenkins
 */
public class ThirdPartyTest {
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
	 * A valid ID for these tests.
	 */
	public static final String ID = UUID.randomUUID().toString();
	/**
	 * A valid shared secret for these tests.
	 */
	public static final String SHARED_SECRET = UUID.randomUUID().toString();
	/**
	 * A valid name for these tests.
	 */
	public static final String NAME = "ThirdPartyTestName";
	/**
	 * A valid description for these tests.
	 */
	public static final String DESCRIPTION = "ThirdPartyTestDescription";
	/**
	 * A valid URI for these tests.
	 */
	public static final URI URI;
	static {
		URI result = null;
		try {
			result = new URI("http://localhost:8080/");
		}
		catch(URISyntaxException e) {
			fail("Could not build the testing URI.");
		}
		URI = result;
	}

	/**
	 * Test that an exception is thrown when the user is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyUserNameDescriptionUriUserNull() {
		new ThirdParty(null, NAME, DESCRIPTION, URI);
	}

	/**
	 * Test that an exception is thrown when the name is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyUserNameDescriptionUriNameNull() {
		new ThirdParty(USER, null, DESCRIPTION, URI);
	}

	/**
	 * Test that an exception is thrown when the description is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyUserNameDescriptionUriDescriptionNull() {
		new ThirdParty(USER, NAME, null, URI);
	}

	/**
	 * Test that an exception is thrown when the URI is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyUserNameDescriptionUriUriNull() {
		new ThirdParty(USER, NAME, DESCRIPTION, null);
	}

	/**
	 * Test that a valid third-party can be built.
	 */
	@Test
	public void testThirdPartyUserNameDescriptionUri() {
		new ThirdParty(USER, NAME, DESCRIPTION, URI);
	}

	/**
	 * Test that an exception is thrown when the owner is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyOwnerIdSharedSecretNameDescriptionUriOwnerNull() {
		new ThirdParty(null, ID, SHARED_SECRET, NAME, DESCRIPTION, URI);
	}

	/**
	 * Test that an exception is thrown when the ID is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyOwnerIdSharedSecretNameDescriptionUriIdNull() {
		new ThirdParty(USERNAME, null, SHARED_SECRET, NAME, DESCRIPTION, URI);
	}

	/**
	 * Test that an exception is thrown when the shared secret is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyOwnerIdSharedSecretNameDescriptionUriSharedSecretNull() {
		new ThirdParty(USERNAME, ID, null, NAME, DESCRIPTION, URI);
	}

	/**
	 * Test that an exception is thrown when the name is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyOwnerIdSharedSecretNameDescriptionUriNameNull() {
		new ThirdParty(USERNAME, ID, SHARED_SECRET, null, DESCRIPTION, URI);
	}

	/**
	 * Test that an exception is thrown when the description is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyOwnerIdSharedSecretNameDescriptionUriDescriptionNull() {
		new ThirdParty(USERNAME, ID, SHARED_SECRET, NAME, null, URI);
	}

	/**
	 * Test that an exception is thrown when the URI is null.
	 */
	@Test(expected = OmhException.class)
	public void testThirdPartyOwnerIdSharedSecretNameDescriptionUriUriNull() {
		new ThirdParty(USERNAME, ID, SHARED_SECRET, NAME, DESCRIPTION, null);
	}

	/**
	 * Test that a valid third-party can be created.
	 */
	@Test
	public void testThirdPartyOwnerIdSharedSecretNameDescriptionUri() {
		new ThirdParty(USERNAME, ID, SHARED_SECRET, NAME, DESCRIPTION, URI);
	}

	/**
	 * Test that a new third-party has some, non-null, ID.
	 */
	@Test
	public void testGetIdNew() {
		ThirdParty thirdParty = new ThirdParty(USER, NAME, DESCRIPTION, URI);
		Assert.assertNotNull(thirdParty.getId());
	}

	/**
	 * Test that a reconstructed third-party has the same ID as it was given 
	 * when it was built.
	 */
	@Test
	public void testGetIdReconstructed() {
		ThirdParty thirdParty =
			new ThirdParty(
				USERNAME, 
				ID, 
				SHARED_SECRET, 
				NAME, 
				DESCRIPTION, 
				URI);
		Assert.assertEquals(ID, thirdParty.getId());
	}

	/**
	 * Test that a new third-party has some, non-null, secret.
	 */
	@Test
	public void testGetSecretNew() {
		ThirdParty thirdParty = new ThirdParty(USER, NAME, DESCRIPTION, URI);
		Assert.assertNotNull(thirdParty.getSecret());
	}

	/**
	 * Test that a reconstructed third-party has the same secret as it was
	 * given when it was built.
	 */
	@Test
	public void testGetSecretReconstructed() {
		ThirdParty thirdParty =
			new ThirdParty(
				USERNAME, 
				ID, 
				SHARED_SECRET, 
				NAME, 
				DESCRIPTION, 
				URI);
		Assert.assertEquals(SHARED_SECRET, thirdParty.getSecret());
	}

	/**
	 * Test that a new third-party has the same name as it was given when it
	 * was created.
	 */
	@Test
	public void testGetNameNew() {
		ThirdParty thirdParty = new ThirdParty(USER, NAME, DESCRIPTION, URI);
		Assert.assertEquals(NAME, thirdParty.getName());
	}

	/**
	 * Test that a reconstructed third-party has the same name as it was given
	 * when it was built.
	 */
	@Test
	public void testGetNameReconstructed() {
		ThirdParty thirdParty =
			new ThirdParty(
				USERNAME, 
				ID, 
				SHARED_SECRET, 
				NAME, 
				DESCRIPTION, 
				URI);
		Assert.assertEquals(NAME, thirdParty.getName());
	}

	/**
	 * Test that a new third-party has the same description as it was given
	 * when it was created.
	 */
	@Test
	public void testGetDescriptionNew() {
		ThirdParty thirdParty = new ThirdParty(USER, NAME, DESCRIPTION, URI);
		Assert.assertEquals(DESCRIPTION, thirdParty.getDescription());
	}

	/**
	 * Test that a reconstructed third-party has the same description as it was
	 * given when it was built.
	 */
	@Test
	public void testGetDescriptionReconstructed() {
		ThirdParty thirdParty =
			new ThirdParty(
				USERNAME, 
				ID, 
				SHARED_SECRET, 
				NAME, 
				DESCRIPTION, 
				URI);
		Assert.assertEquals(DESCRIPTION, thirdParty.getDescription());
	}

	/**
	 * Test that a new third-party has the same URI as it was given when it was
	 * created.
	 */
	@Test
	public void testGetRedirectUriNew() {
		ThirdParty thirdParty = new ThirdParty(USER, NAME, DESCRIPTION, URI);
		Assert.assertEquals(URI, thirdParty.getRedirectUri());
	}

	/**
	 * Test that a reconstructed third-party has the same URI as it was given
	 * when it was built.
	 */
	@Test
	public void testGetRedirectUriReconstructed() {
		ThirdParty thirdParty =
			new ThirdParty(
				USERNAME, 
				ID, 
				SHARED_SECRET, 
				NAME, 
				DESCRIPTION, 
				URI);
		Assert.assertEquals(URI, thirdParty.getRedirectUri());
	}
}