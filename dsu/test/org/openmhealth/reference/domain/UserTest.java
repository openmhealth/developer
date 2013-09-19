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

import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

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
	 * A valid user-name to use for testing.
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
	 * A valid email address to use for testing.
	 */
	public static final String EMAIL_STRING = "root@localhost.localdomain";
	/**
	 * A valid email address to use for testing validated as an InternetAddress
	 * object.
	 */
	public static final InternetAddress EMAIL;
	static {
		try {
			EMAIL = new InternetAddress(EMAIL_STRING);
		}
		catch(AddressException e) {
			throw
				new IllegalStateException(
					"The testing email address is invalid.",
					e);
		}
	}
	/**
	 * A valid, random registration ID for testing.
	 */
	public static final String REGISTRATION_ID = UUID.randomUUID().toString();
	/**
	 * A valid registration date for testing.
	 */
	public static final Long REGISTRATION_DATE = System.currentTimeMillis();
	/**
	 * A valid activation date for testing.
	 */
	public static final Long ACTIVATION_DATE =
		REGISTRATION_DATE + (1000 * 60 * 60 * 10);

	/**
	 * Test that an exception is thrown when the username is null.
	 */
	@Test(expected = OmhException.class)
	public void testUserUsernameNull() {
		new User(
			null,
			PASSWORD,
			EMAIL_STRING,
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when the username is empty.
	 */
	@Test(expected = OmhException.class)
	public void testUserUsernameEmpty() {
		new User(
			"",
			PASSWORD,
			EMAIL_STRING,
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when the username is whitespace.
	 */
	@Test(expected = OmhException.class)
	public void testUserUsernameWhitespace() {
		new User(
			"\t",
			PASSWORD,
			EMAIL_STRING,
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when the password is null.
	 */
	@Test(expected = OmhException.class)
	public void testUserPasswordNull() {
		new User(
			USERNAME,
			null,
			EMAIL_STRING,
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when the password is empty.
	 */
	@Test(expected = OmhException.class)
	public void testUserPasswordEmpty() {
		new User(
			USERNAME,
			"",
			EMAIL_STRING,
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that any password is valid, including those that are only
	 * whitespace.
	 */
	public void testUserPasswordWhitespace() {
		new User(
			USERNAME,
			" \t",
			EMAIL_STRING,
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when the email is null.
	 */
	@Test(expected = OmhException.class)
	public void testUserEmailNull() {
		new User(
			USERNAME,
			PASSWORD,
			null,
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when the email is empty.
	 */
	@Test(expected = OmhException.class)
	public void testUserEmailEmpty() {
		new User(
			USERNAME,
			PASSWORD,
			"",
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when the email is whitespace.
	 */
	@Test(expected = OmhException.class)
	public void testUserEmailWhitespace() {
		new User(
			USERNAME,
			PASSWORD,
			"\t",
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when the email is not a valid email
	 * address.
	 */
	@Test(expected = OmhException.class)
	public void testUserEmailInvalid() {
		new User(
			USERNAME,
			PASSWORD,
			"blah",
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when the registration ID is empty.
	 */
	@Test(expected = OmhException.class)
	public void testUserRegistrationIdWhitespace() {
		new User(
			USERNAME,
			PASSWORD,
			EMAIL_STRING,
			"",
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that an exception is thrown when a registration ID exists but no
	 * registration date exists.
	 */
	@Test(expected = OmhException.class)
	public void testUserRegistrationIdWithoutDate() {
		new User(
			USERNAME,
			PASSWORD,
			EMAIL_STRING,
			REGISTRATION_ID,
			null,
			null);
	}

	/**
	 * Test that an exception is thrown when the registration date is after the
	 * activation date.
	 */
	@Test(expected = OmhException.class)
	public void testUserRegistrationDateAfterActivationDate() {
		new User(
			USERNAME,
			PASSWORD,
			EMAIL_STRING,
			REGISTRATION_ID,
			ACTIVATION_DATE + 1,
			ACTIVATION_DATE);
	}

	/**
	 * Test that a User object can be made from valid parameters.
	 */
	@Test
	public void testUser() {
		new User(
			USERNAME,
			PASSWORD,
			EMAIL_STRING,
			REGISTRATION_ID,
			REGISTRATION_DATE,
			ACTIVATION_DATE);
	}

	/**
	 * Test that a User object can be made from valid parameters without
	 * registration or activation.
	 */
	@Test
	public void testUserNoRegistration() {
		new User(
			USERNAME,
			PASSWORD,
			EMAIL_STRING,
			null,
			null,
			null);
	}

	/**
	 * Test that a User object can be made from valid parameters with
	 * registration but no activation.
	 */
	@Test
	public void testUserNotActivated() {
		new User(
			USERNAME,
			PASSWORD,
			EMAIL_STRING,
			REGISTRATION_ID,
			REGISTRATION_DATE,
			null);
	}

	/**
	 * Test that a User object can be made from valid parameters where the
	 * activation date is recorded, but the user never registered the account.
	 */
	@Test
	public void testUserActivationWithoutRegistration() {
		new User(
			USERNAME,
			PASSWORD,
			EMAIL_STRING,
			null,
			null,
			ACTIVATION_DATE);
	}

	/**
	 * Test that the username is saved as the given value.
	 */
	@Test
	public void testGetUsername() {
		User user =
			new User(
				USERNAME,
				PASSWORD,
				EMAIL_STRING,
				REGISTRATION_ID,
				REGISTRATION_DATE,
				ACTIVATION_DATE);
		Assert.assertEquals(USERNAME, user.getUsername());
	}

	/**
	 * Test that an exception is thrown if you try to check a null password.
	 */
	@Test(expected = OmhException.class)
	public void testCheckPasswordNull() {
		User user =
			new User(
				USERNAME,
				PASSWORD,
				EMAIL_STRING,
				REGISTRATION_ID,
				REGISTRATION_DATE,
				ACTIVATION_DATE);
		user.checkPassword(null);
	}

	/**
	 * Test that an incorrect password is rejected.
	 */
	@Test
	public void testCheckPasswordInvalid() {
		User user =
			new User(
				USERNAME,
				PASSWORD,
				EMAIL_STRING,
				REGISTRATION_ID,
				REGISTRATION_DATE,
				ACTIVATION_DATE);
		Assert.assertFalse(user.checkPassword(PASSWORD_STRING.substring(1)));
	}

	/**
	 * Test that the correct password is accepted.
	 */
	@Test
	public void testCheckPassword() {
		User user =
			new User(
				USERNAME,
				PASSWORD,
				EMAIL_STRING,
				REGISTRATION_ID,
				REGISTRATION_DATE,
				ACTIVATION_DATE);
		Assert.assertTrue(user.checkPassword(PASSWORD_STRING));
	}

	/**
	 * Test that the username is saved as the given value.
	 */
	@Test
	public void testGetEmail() {
		User user =
			new User(
				USERNAME,
				PASSWORD,
				EMAIL_STRING,
				REGISTRATION_ID,
				REGISTRATION_DATE,
				ACTIVATION_DATE);
		Assert.assertEquals(EMAIL, user.getEmail());
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