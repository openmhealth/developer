package org.openmhealth.reference.domain;

import jbcrypt.BCrypt;

import org.junit.Assert;
import org.junit.Test;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Responsible for testing everything about the {@link User} class.
 * </p>
 *
 * @author John Jenkins
 */
public class UserTest {
	/**
	 * A valid username to use for testing.
	 */
	public static final String USERNAME = "Test.User";
	/**
	 * A valid, plain-text password to use for testing.
	 */
	public static final String PASSWORD_STRING = "Test.Password0";
	/**
	 * A valid, hashed password to use for testing.
	 */
	public static final String PASSWORD =
		BCrypt.hashpw(PASSWORD_STRING, BCrypt.gensalt());

	/**
	 * Test that an exception is thrown when the username is null.
	 */
	@Test(expected = OmhException.class)
	public void testUserUsernameNull() {
		new User(null, PASSWORD);
	}

	/**
	 * Test that an exception is thrown when the username is empty.
	 */
	@Test(expected = OmhException.class)
	public void testUserUsernameEmpty() {
		new User("", PASSWORD);
	}

	/**
	 * Test that an exception is thrown when the username is whitespace.
	 */
	@Test(expected = OmhException.class)
	public void testUserUsernameWhitespace() {
		new User("\t", PASSWORD);
	}

	/**
	 * Test that an exception is thrown when the password is null.
	 */
	@Test(expected = OmhException.class)
	public void testUserPasswordNull() {
		new User(USERNAME, null);
	}

	/**
	 * Test that an exception is thrown when the password is empty.
	 */
	@Test(expected = OmhException.class)
	public void testUserPasswordEmpty() {
		new User(USERNAME, "");
	}

	/**
	 * Test that an exception is thrown when the password is whitespace.
	 */
	@Test(expected = OmhException.class)
	public void testUserPasswordWhitespace() {
		new User(USERNAME, "\t");
	}

	/**
	 * Test that a User object can be made from valid parameters.
	 */
	@Test
	public void testUser() {
		new User(USERNAME, PASSWORD);
	}

	/**
	 * Test that the username is saved as the given value.
	 */
	@Test
	public void testGetUsername() {
		User user = new User(USERNAME, PASSWORD);
		Assert.assertEquals(USERNAME, user.getUsername());
	}

	/**
	 * Test that an exception is thrown if you try to check a null password.
	 */
	@Test(expected = OmhException.class)
	public void testCheckPasswordNull() {
		User user = new User(USERNAME, PASSWORD);
		user.checkPassword(null);
	}

	/**
	 * Test that an incorrect password is rejected.
	 */
	@Test
	public void testCheckPasswordInvalid() {
		User user = new User(USERNAME, PASSWORD);
		Assert.assertFalse(user.checkPassword(PASSWORD_STRING.substring(1)));
	}

	/**
	 * Test that the correct password is accepted.
	 */
	@Test
	public void testCheckPassword() {
		User user = new User(USERNAME, PASSWORD);
		Assert.assertTrue(user.checkPassword(PASSWORD_STRING));
	}

	/**
	 * Test that the username cannot be null.
	 */
	@Test(expected = OmhException.class)
	public void testValidateUsernameNull() {
		User.validateUsername(null);
	}

	/**
	 * Test that the username cannot be an empty string.
	 */
	@Test(expected = OmhException.class)
	public void testValidateUsernameEmpty() {
		User.validateUsername("");
	}

	/**
	 * Test that the username cannot be only whitespace.
	 */
	@Test(expected = OmhException.class)
	public void testValidateUsernameWhitespace() {
		User.validateUsername("\t");
	}

	/**
	 * Test that a valid username passes validation.
	 */
	@Test
	public void testValidateUsername() {
		User.validateUsername(USERNAME);
	}
}