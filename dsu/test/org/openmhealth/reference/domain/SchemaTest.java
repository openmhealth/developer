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

import name.jenkins.paul.john.concordia.schema.ObjectSchema;
import name.jenkins.paul.john.concordia.validator.ValidationController;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.openmhealth.reference.concordia.OmhValidationController;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <p>
 * Responsible for testing everything about the {@link Schema} class.
 * </p>
 *
 * @author John Jenkins
 */
public class SchemaTest {
	/**
	 * A valid ID to use for testing.
	 */
	public static final String ID = "abc123";
	/**
	 * A valid version to use for testing.
	 */
	public static final long VERSION = 1;
	/**
	 * A valid chunk size to use for testing.
	 */
	public static final long CHUNK_SIZE = 2;
	/**
	 * A valid time authoritative value to use for testing.
	 */
	public static final boolean TIME_AUTHORITATIVE = true;
	/**
	 * A valid time-zone authoritative value to use for testing.
	 */
	public static final boolean TIME_ZONE_AUTHORITATIVE = true;
	/**
	 * A valid schema to use for testing.
	 */
	public static final JsonNode SCHEMA;
	static {
		// Build the schema as an object type with no fields.
		ObjectNode schemaRoot = new ObjectNode(JsonNodeFactory.instance);
		schemaRoot
			.put(
				name.jenkins.paul.john.concordia.schema.Schema.JSON_KEY_TYPE,
				ObjectSchema.TYPE_ID);
		schemaRoot
			.put(
				ObjectSchema.JSON_KEY_FIELDS,
				new ArrayNode(JsonNodeFactory.instance));
		// Save the schema.
		SCHEMA = schemaRoot;
	}
	/**
	 * A mirror of The Open mHealth validation controller to use for testing. 
	 */
	public static final ValidationController CONTROLLER =
		OmhValidationController.VALIDATION_CONTROLLER;
	/**
	 * The owner username to use for testing.
	 */
	public static final String OWNER = "Test.User";
	/**
	 * The {@link MetaData} to use for testing.
	 */
	public static final MetaData META_DATA;
	static {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId("metaDataId");
		builder.setTimestamp(new DateTime(0, DateTimeZone.UTC));
		META_DATA = builder.build();
	}
	/**
	 * The data that conforms to the {@link #SCHEMA} to use for testing.
	 */
	public static final JsonNode DATA =
		new ObjectNode(JsonNodeFactory.instance);

	/**
	 * Test that an exception is thrown when the ID is null.
	 */
	@Test(expected = OmhException.class)
	public void testSchemaIdNull() {
		new Schema(
			null,
			VERSION,
			CHUNK_SIZE,
			TIME_AUTHORITATIVE,
			TIME_ZONE_AUTHORITATIVE,
			SCHEMA,
			CONTROLLER);
	}

	/**
	 * Test that an exception is thrown when the ID is an empty string.
	 */
	@Test(expected = OmhException.class)
	public void testSchemaIdEmpty() {
		new Schema(
			"",
			VERSION,
			CHUNK_SIZE,
			TIME_AUTHORITATIVE,
			TIME_ZONE_AUTHORITATIVE,
			SCHEMA,
			CONTROLLER);
	}

	/**
	 * Test that an exception is thrown when the ID is only whitespace.
	 */
	@Test(expected = OmhException.class)
	public void testSchemaIdWhitespace() {
		new Schema(
			"\t",
			VERSION,
			CHUNK_SIZE,
			TIME_AUTHORITATIVE,
			TIME_ZONE_AUTHORITATIVE,
			SCHEMA,
			CONTROLLER);
	}

	/**
	 * Test that an exception is thrown when the schema is null.
	 */
	@Test(expected = OmhException.class)
	public void testSchemaSchemaNull() {
		new Schema(
			ID,
			VERSION,
			CHUNK_SIZE,
			TIME_AUTHORITATIVE,
			TIME_ZONE_AUTHORITATIVE,
			null,
			CONTROLLER);
	}

	/**
	 * Test that an exception is thrown when the schema is not a valid schema.
	 */
	@Test(expected = OmhException.class)
	public void testSchemaSchemaInvalid() {
		new Schema(
			ID,
			VERSION,
			CHUNK_SIZE,
			TIME_AUTHORITATIVE,
			TIME_ZONE_AUTHORITATIVE,
			BooleanNode.TRUE,
			CONTROLLER);
	}

	/**
	 * Test that an exception is thrown when the controller is null.
	 */
	@Test(expected = OmhException.class)
	public void testSchemaControllerNull() {
		new Schema(
			ID,
			VERSION,
			CHUNK_SIZE,
			TIME_AUTHORITATIVE,
			TIME_ZONE_AUTHORITATIVE,
			SCHEMA,
			null);
	}

	/**
	 * Test that a {@link Schema} object can be created when the parameters are
	 * valid.
	 */
	@Test
	public void testSchema() {
		new Schema(
			ID,
			VERSION,
			CHUNK_SIZE,
			TIME_AUTHORITATIVE,
			TIME_ZONE_AUTHORITATIVE,
			SCHEMA,
			CONTROLLER);
	}

	/**
	 * Test that the given ID is the same as the one stored in the object.
	 */
	@Test
	public void testGetId() {
		Schema schema =
			new Schema(
				ID,
				VERSION,
				CHUNK_SIZE,
				TIME_AUTHORITATIVE,
				TIME_ZONE_AUTHORITATIVE,
				SCHEMA,
				CONTROLLER);
		Assert.assertEquals(ID, schema.getId());
	}

	/**
	 * Test that the given version is the same as the one stored in the object.
	 */
	@Test
	public void testGetVersion() {
		Schema schema =
			new Schema(
				ID,
				VERSION,
				CHUNK_SIZE,
				TIME_AUTHORITATIVE,
				TIME_ZONE_AUTHORITATIVE,
				SCHEMA,
				CONTROLLER);
		Assert.assertEquals(VERSION, schema.getVersion());
	}

	/**
	 * Test that validating data with a null owner throws an exception.
	 */
	@Test(expected = OmhException.class)
	public void testValidateDataOwnerNull() {
		Schema schema =
			new Schema(
				ID,
				VERSION,
				CHUNK_SIZE,
				TIME_AUTHORITATIVE,
				TIME_ZONE_AUTHORITATIVE,
				SCHEMA,
				CONTROLLER);
		schema.validateData(null, META_DATA, DATA);
	}

	/**
	 * Test that validating data when the meta-data is null is valid.
	 */
	@Test
	public void testValidateDataMetaDataNull() {
		Schema schema =
			new Schema(
				ID,
				VERSION,
				CHUNK_SIZE,
				TIME_AUTHORITATIVE,
				TIME_ZONE_AUTHORITATIVE,
				SCHEMA,
				CONTROLLER);
		schema.validateData(OWNER, null, DATA);
	}

	/**
	 * Test that validating data when the data is null throws an exception.
	 */
	@Test(expected = OmhException.class)
	public void testValidateDataDataNull() {
		Schema schema =
			new Schema(
				ID,
				VERSION,
				CHUNK_SIZE,
				TIME_AUTHORITATIVE,
				TIME_ZONE_AUTHORITATIVE,
				SCHEMA,
				CONTROLLER);
		schema.validateData(OWNER, META_DATA, null);
	}

	/**
	 * Test that validating data when the data is invalid throws an exception.
	 */
	@Test(expected = OmhException.class)
	public void testValidateDataDataInvalid() {
		Schema schema =
			new Schema(
				ID,
				VERSION,
				CHUNK_SIZE,
				TIME_AUTHORITATIVE,
				TIME_ZONE_AUTHORITATIVE,
				SCHEMA,
				CONTROLLER);
		schema.validateData(OWNER, META_DATA, BooleanNode.TRUE);
	}

	/**
	 * Test that validating valid data does not throw an exception.
	 */
	@Test
	public void testValidateData() {
		Schema schema =
			new Schema(
				ID,
				VERSION,
				CHUNK_SIZE,
				TIME_AUTHORITATIVE,
				TIME_ZONE_AUTHORITATIVE,
				SCHEMA,
				CONTROLLER);
		schema.validateData(OWNER, META_DATA, DATA);
	}

	/**
	 * Test that null is not a valid ID.
	 */
	@Test(expected = OmhException.class)
	public void testValidateIdNull() {
		Schema.validateId(null);
	}

	/**
	 * Test that an empty string is not a valid ID.
	 */
	@Test(expected = OmhException.class)
	public void testValidateIdEmpty() {
		Schema.validateId("");
	}

	/**
	 * Test that whitespace is not a valid ID.
	 */
	@Test(expected = OmhException.class)
	public void testValidateIdWhitespace() {
		Schema.validateId("\t");
	}

	/**
	 * Test that a valid ID is valid.
	 */
	@Test
	public void testValidateId() {
		Schema.validateId(ID);
	}

	/**
	 * Test that a negative number is not a valid version.
	 */
	@Test(expected = OmhException.class)
	public void testValidateVersionNegative() {
		Schema.validateVersion(-1);
	}

	/**
	 * Test that zero is not a valid version.
	 */
	@Test(expected = OmhException.class)
	public void testValidateVersionZero() {
		Schema.validateVersion(0);
	}

	/**
	 * Test that a positive number is not a valid version.
	 */
	@Test
	public void testValidateVersionPositive() {
		Schema.validateVersion(1);
	}

	/**
	 * Test that a valid version is a valid version.
	 */
	@Test
	public void testValidateVersion() {
		Schema.validateVersion(VERSION);
	}

	/**
	 * Test that a negative number is not a valid chunk size.
	 */
	@Test(expected = OmhException.class)
	public void testValidateChunkSizeNegative() {
		Schema.validateChunkSize(-1);
	}

	/**
	 * Test that zero is not a valid chunk size.
	 */
	@Test(expected = OmhException.class)
	public void testValidateChunkSizeZero() {
		Schema.validateChunkSize(0);
	}

	/**
	 * Test that a positive number is not a valid chunk size.
	 */
	@Test
	public void testValidateChunkSizePositive() {
		Schema.validateChunkSize(1);
	}

	/**
	 * Test that a valid chunk size is valid.
	 */
	@Test
	public void testValidateChunkSize() {
		Schema.validateChunkSize(CHUNK_SIZE);
	}
}
