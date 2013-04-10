package org.openmhealth.reference.domain;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * Tests everything about the {@link MetaData} class.
 * </p>
 *
 * @author John Jenkins
 */
public class MetaDataTest {
	/**
	 * An ID to use when testing.
	 */
	public static final String ID_TEST = "test";
	/**
	 * A timestamp to use when testing.
	 */
	public static final DateTime TIMESTAMP_TEST = new DateTime(0);
	/**
	 * A timestamp to use when testing that is in the future.
	 */
	public static final DateTime TIMESTAMP_FUTURE =
		(new DateTime()).plusDays(1);
	
	/**
	 * Test that a {@link MetaData} object can be built.
	 */
	@Test
	public void testMetaData() {
		new MetaData(ID_TEST, TIMESTAMP_TEST);
	}

	/**
	 * Test that the ID can be null.
	 */
	@Test
	public void testMetaDataIdNull() {
		new MetaData(null, TIMESTAMP_TEST);
	}
	
	/**
	 * Test that the timestamp can be null.
	 */
	@Test
	public void testMetaDataTimestampNull() {
		new MetaData(ID_TEST, null);
	}
	
	/**
	 * Test that the timestamp cannot be in the future.
	 */
	@Test(expected = OmhException.class)
	public void testMetaDataTimestampFuture() {
		new MetaData(ID_TEST, TIMESTAMP_FUTURE);
	}

	/**
	 * Test that getId() returns the same ID that it was built with.
	 */
	@Test
	public void testGetId() {
		MetaData metaData = new MetaData(ID_TEST, TIMESTAMP_TEST);
		Assert.assertEquals(ID_TEST, metaData.getId());
	}

	/**
	 * Test that getId() returns the same ID that it was built with even if it
	 * is null.
	 */
	@Test
	public void testGetIdNull() {
		MetaData metaData = new MetaData(null, TIMESTAMP_TEST);
		Assert.assertEquals(null, metaData.getId());
	}

	/**
	 * Test that getTimestamp() returns the same timestamp that it was built
	 * with.
	 */
	@Test
	public void testGetTimestamp() {
		MetaData metaData = new MetaData(ID_TEST, TIMESTAMP_TEST);
		Assert.assertEquals(TIMESTAMP_TEST, metaData.getTimestamp());
	}

	/**
	 * Test that getTimestamp() returns the same timestamp that it was built
	 * with even if it is null.
	 */
	@Test
	public void testGetTimestampNull() {
		MetaData metaData = new MetaData(ID_TEST, null);
		Assert.assertEquals(null, metaData.getTimestamp());
	}
}