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
import org.junit.Test;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * <p>
 * Responsible for testing everything about the {@link Data} class.
 * </p>
 *
 * @author John Jenkins
 */
public class DataTest {
	/**
	 * The owner username to use for testing.
	 */
	public static final String OWNER = "Test.User";
	/**
	 * The {@link Schema} to use for testing.
	 */
	public static final Schema SCHEMA;
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
		// Create the schema.
		SCHEMA =
			new Schema(
				SchemaTest.ID,
				1,
				schemaRoot,
				ValidationController.BASIC_CONTROLLER);
	}
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
	 * Test that an exception is thrown when the owner is null.
	 */
	@Test(expected = OmhException.class)
	public void testDataStringSchemaMetaDataJsonNodeOwnerNull() {
		new Data(null, SCHEMA, META_DATA, DATA);
	}

	/**
	 * Test that an exception is thrown when the schema is null.
	 */
	@Test(expected = OmhException.class)
	public void testDataStringSchemaMetaDataJsonNodeSchemaNull() {
		new Data(OWNER, null, META_DATA, DATA);
	}

	/**
	 * Test that it is valid to create a Data object without meta-data.
	 */
	@Test
	public void testDataStringSchemaMetaDataJsonNodeMetaDataNull() {
		new Data(OWNER, SCHEMA, null, DATA);
	}

	/**
	 * Test that an exception is thrown when the data is null.
	 */
	@Test(expected = OmhException.class)
	public void testDataStringSchemaMetaDataJsonNodeDataNull() {
		new Data(OWNER, SCHEMA, META_DATA, null);
	}

	/**
	 * Test that a data object can be created from valid parameters.
	 */
	@Test
	public void testDataStringSchemaMetaDataJsonNode() {
		new Data(OWNER, SCHEMA, META_DATA, DATA);
	}

	/**
	 * Test that an exception is thrown when the owner is null.
	 */
	@Test(expected = OmhException.class)
	public void testDataStringStringLongMetaDataJsonNodeOwnerNull() {
		new Data(null, SCHEMA.getId(), SCHEMA.getVersion(), META_DATA, DATA);
	}

	/**
	 * Test that an exception is thrown when the schema ID is null.
	 */
	@Test(expected = OmhException.class)
	public void testDataStringStringLongMetaDataJsonNodeSchemaIdNull() {
		new Data(OWNER, null, SCHEMA.getVersion(), META_DATA, DATA);
	}

	/**
	 * Test that it is valid to create a Data object without meta-data.
	 */
	@Test
	public void testDataStringStringLongMetaDataJsonNodeMetaDataNull() {
		new Data(OWNER, SCHEMA.getId(), SCHEMA.getVersion(), null, DATA);
	}

	/**
	 * Test that an exception is thrown when the data is null.
	 */
	@Test(expected = OmhException.class)
	public void testDataStringStringLongMetaDataJsonNodeDataNull() {
		new Data(OWNER, SCHEMA.getId(), SCHEMA.getVersion(), META_DATA, null);
	}

	/**
	 * Test that a data object can be created from valid parameters.
	 */
	@Test
	public void testDataStringStringLongMetaDataJsonNode() {
		new Data(OWNER, SCHEMA.getId(), SCHEMA.getVersion(), META_DATA, DATA);
	}
}
