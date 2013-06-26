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

/**
 * <p>
 * Tests everything about the {@link MetaData.Builder} class.
 * </p>
 *
 * @author John Jenkins
 */
public class MetaDataBuilderTest {
	/**
	 * Test that a default {@link MetaData.Builder} can be built.
	 */
	@Test
	public void testMetaDataBuiler() {
		new MetaData.Builder();
	}
	
	/**
	 * Test that the default ID is null.
	 */
	@Test
	public void testMetaDataBuilerDefaultId() {
		MetaData.Builder builder = new MetaData.Builder();
		Assert.assertEquals(null, builder.getId());
	}
	
	/**
	 * Test that the default timestamp is null.
	 */
	@Test
	public void testMetaDataBuilerDefaultTimestamp() {
		MetaData.Builder builder = new MetaData.Builder();
		Assert.assertEquals(null, builder.getTimestamp());
	}
	
	/**
	 * Test that the ID can be set to null.
	 */
	@Test
	public void testSetIdNull() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(null);
		Assert.assertEquals(null, builder.getId());
	}
	
	/**
	 * Test that the ID can be set to a valid value.
	 */
	@Test
	public void testSetIdValid() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(MetaDataTest.ID_TEST);
		Assert.assertEquals(MetaDataTest.ID_TEST, builder.getId());
	}
	
	/**
	 * Test that checking for an ID, when none exists, returns false.
	 */
	@Test
	public void testHasIdFalse() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(null);
		Assert.assertFalse(builder.hasId());
	}
	
	/**
	 * Test that checking for an ID, when one does exist, returns true.
	 */
	@Test
	public void testHasIdTrue() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(MetaDataTest.ID_TEST);
		Assert.assertTrue(builder.hasId());
	}
	
	/**
	 * Test that the timestamp can be set to null.
	 */
	@Test
	public void testSetTimestampNull() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setTimestamp(null);
		Assert.assertEquals(null, builder.getTimestamp());
	}
	
	/**
	 * Test that the timestamp can be set to a valid value.
	 */
	@Test
	public void testSetTimestampValid() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setTimestamp(MetaDataTest.TIMESTAMP_TEST);
		Assert
			.assertEquals(MetaDataTest.TIMESTAMP_TEST, builder.getTimestamp());
	}
	
	/**
	 * Test that the timestamp can be set to an invalid value. It should be
	 * caught later at creation time.
	 */
	@Test
	public void testSetTimestampValidFuture() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setTimestamp(MetaDataTest.TIMESTAMP_FUTURE);
		Assert
			.assertEquals(
				MetaDataTest.TIMESTAMP_FUTURE,
				builder.getTimestamp());
	}
	
	/**
	 * Test that checking for a timestamp, when none exists, returns false.
	 */
	@Test
	public void testHasTimestampFalse() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setTimestamp(null);
		Assert.assertFalse(builder.hasTimestamp());
	}
	
	/**
	 * Test that checking for a timestamp, when one does exist, returns true.
	 */
	@Test
	public void testHasTimestampTrue() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setTimestamp(MetaDataTest.TIMESTAMP_TEST);
		Assert.assertTrue(builder.hasTimestamp());
	}
	
	/**
	 * Test that meta-data can be built with valid values and that the
	 * resulting object is what we set it to.
	 */
	@Test
	public void testBuildValid() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(MetaDataTest.ID_TEST);
		builder.setTimestamp(MetaDataTest.TIMESTAMP_TEST);
		MetaData metaData = builder.build();

		Assert.assertNotNull(metaData);
		Assert.assertEquals(MetaDataTest.ID_TEST, metaData.getId());
		Assert
			.assertEquals(
				MetaDataTest.TIMESTAMP_TEST,
				metaData.getTimestamp());
	}
	
	/**
	 * Test that the meta-data can be built with a null ID and that the
	 * resulting object is what we set it to.
	 */
	@Test
	public void testBuildValidIdNull() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(null);
		builder.setTimestamp(MetaDataTest.TIMESTAMP_TEST);
		MetaData metaData = builder.build();

		Assert.assertNotNull(metaData);
		Assert.assertEquals(null, metaData.getId());
		Assert
			.assertEquals(
				MetaDataTest.TIMESTAMP_TEST,
				metaData.getTimestamp());
	}
	
	/**
	 * Test that the meta-data can be built with a null timestamp and that the
	 * resulting object is what we set it to.
	 */
	@Test
	public void testBuildValidTimestampNull() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(MetaDataTest.ID_TEST);
		builder.setTimestamp(null);
		MetaData metaData = builder.build();

		Assert.assertNotNull(metaData);
		Assert.assertEquals(MetaDataTest.ID_TEST, metaData.getId());
		Assert.assertEquals(null, metaData.getTimestamp());
	}

	/**
	 * Test that the meta-data can be built with a null ID and timestamp and
	 * that the resulting object is what we set it to.
	 */
	@Test
	public void testBuildValidAllNull() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(null);
		builder.setTimestamp(null);
		MetaData metaData = builder.build();

		Assert.assertNotNull(metaData);
		Assert.assertEquals(null, metaData.getId());
		Assert.assertEquals(null, metaData.getTimestamp());
	}
	
	/**
	 * Test that the builder reports not null when no field is null.
	 */
	@Test
	public void testIsNullValid() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(MetaDataTest.ID_TEST);
		builder.setTimestamp(MetaDataTest.TIMESTAMP_TEST);
		Assert.assertFalse(builder.isNull());
	}
	
	/**
	 * Test that the builder reports not null when the ID is null.
	 */
	@Test
	public void testIsNullIdNull() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(null);
		builder.setTimestamp(MetaDataTest.TIMESTAMP_TEST);
		Assert.assertFalse(builder.isNull());
	}
	
	/**
	 * Test that the builder reports not null when the timestamp is null.
	 */
	@Test
	public void testIsNullTimestampNull() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(MetaDataTest.ID_TEST);
		builder.setTimestamp(null);
		Assert.assertFalse(builder.isNull());
	}

	/**
	 * Test that the builder reports null when all fields are null.
	 */
	@Test
	public void testIsNullAllNull() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(null);
		builder.setTimestamp(null);
		Assert.assertTrue(builder.isNull());
	}
}