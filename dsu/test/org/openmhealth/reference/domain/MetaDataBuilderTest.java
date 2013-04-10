package org.openmhealth.reference.domain;

import org.junit.Assert;
import org.junit.Test;
import org.openmhealth.reference.exception.OmhException;

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
	 * Test that an invalid timestamp results in an exception being thrown on
	 * creation.
	 */
	@Test(expected = OmhException.class)
	public void testBuildValidTimestampFuture() {
		MetaData.Builder builder = new MetaData.Builder();
		builder.setId(MetaDataTest.ID_TEST);
		builder.setTimestamp(MetaDataTest.TIMESTAMP_FUTURE);
		builder.build();
	}
}