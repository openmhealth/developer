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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * Tests everything about the {@link ColumnList} class.
 * </p>
 *
 * @author John Jenkins
 */
public class ColumnListTest {
	/**
	 * A single element.
	 */
	public static final String ELEMENT_A = "a";
	/**
	 * A single element.
	 */
	public static final String ELEMENT_B = "b";
	/**
	 * An element with one child.
	 */
	public static final String ELEMENT_A_CHILD_A = "a.a";
	/**
	 * An element with one child.
	 */
	public static final String ELEMENT_A_CHILD_B = "a.b";
	
	/**
	 * Tests that passing null to the constructor doesn't throw an exception.
	 */
	@Test
	public void testColumnListNull() {
		new ColumnList(null);
	}
	
	/**
	 * Tests that passing an empty list to the constructor doesn't throw an
	 * exception.
	 */
	@Test
	public void testColumnListEmpty() {
		new ColumnList(Collections.<String>emptyList());
	}
	
	/**
	 * Tests that a column list of one no-child element can be created.
	 */
	@Test
	public void testColumnListOneElement() {
		List<String> element = new ArrayList<String>(1);
		element.add(ELEMENT_A);
		new ColumnList(element);
	}
	
	/**
	 * Tests that a column list with two no-child elements can be created. 
	 */
	@Test
	public void testColumnListMultipleElements() {
		List<String> element = new ArrayList<String>(2);
		element.add(ELEMENT_A);
		element.add(ELEMENT_B);
		new ColumnList(element);
	}
	
	/**
	 * Tests that a column list with one element that has one child can be
	 * created.
	 */
	@Test
	public void testColumnListOneChild() {
		List<String> element = new ArrayList<String>(2);
		element.add(ELEMENT_A_CHILD_A);
		new ColumnList(element);
	}
	
	/**
	 * Test that a column list with one parent that has two children can be
	 * created.
	 */
	@Test
	public void testColumnListTwoChildren() {
		List<String> element = new ArrayList<String>(2);
		element.add(ELEMENT_A_CHILD_A);
		element.add(ELEMENT_A_CHILD_B);
		new ColumnList(element);
	}
	
	/**
	 * Tests that a column list with one element that is the parent and another
	 * element that is the parent with a child can be created.
	 */
	@Test
	public void testColumnListParentAndChild() {
		List<String> element = new ArrayList<String>(2);
		element.add(ELEMENT_A);
		element.add(ELEMENT_A_CHILD_A);
		new ColumnList(element);
	}

	/**
	 * Tests that when a column list with a value of null is created, it has an
	 * appropriate size.
	 */
	@Test
	public void testSizeNull() {
		ColumnList columnList = new ColumnList(null);
		Assert.assertEquals(0, columnList.size());
	}

	/**
	 * Tests that when a column list with no children is created, it has an
	 * appropriate size.
	 */
	@Test
	public void testSizeEmpty() {
		ColumnList columnList =
			new ColumnList(Collections.<String>emptyList());
		Assert.assertEquals(0, columnList.size());
	}

	/**
	 * Tests that a column list with one element has a size of 1.
	 */
	@Test
	public void testSizeOneElement() {
		List<String> element = new ArrayList<String>(1);
		element.add(ELEMENT_A);
		ColumnList columnList = new ColumnList(element);
		Assert.assertEquals(1, columnList.size());
	}

	/**
	 * Tests that a column list made with multiple elements has the same size
	 * as the number of elements.
	 */
	@Test
	public void testSizeMultipleElements() {
		List<String> element = new ArrayList<String>(1);
		element.add(ELEMENT_A);
		element.add(ELEMENT_B);
		ColumnList columnList = new ColumnList(element);
		Assert.assertEquals(2, columnList.size());
	}

	/**
	 * Tests that when a column list with one element being just the parent and
	 * another element being a parent with a child has a size of 1 because the
	 * size is based on the number at that depth, not the entire tree.
	 */
	@Test
	public void testSizeParentAndChild() {
		List<String> element = new ArrayList<String>(1);
		element.add(ELEMENT_A);
		element.add(ELEMENT_A_CHILD_A);
		ColumnList columnList = new ColumnList(element);
		Assert.assertEquals(1, columnList.size());
	}

	/**
	 * Tests that a column list with multiple elements where each element has
	 * the same parent but multiple children returns a size of 1 because the
	 * size is based on the number at that depth, not the entire tree.
	 */
	@Test
	public void testSizeChildren() {
		List<String> element = new ArrayList<String>(1);
		element.add(ELEMENT_A_CHILD_A);
		element.add(ELEMENT_A_CHILD_B);
		ColumnList columnList = new ColumnList(element);
		Assert.assertEquals(1, columnList.size());
	}

	/**
	 * Tests that creating a complex list and then returning it to a list
	 * results in, effectively, the same list, although the elements may be
	 * reordered.
	 */
	@Test
	public void testToList() {
		List<String> element = new ArrayList<String>(1);
		element.add(ELEMENT_A_CHILD_A);
		element.add(ELEMENT_A_CHILD_B);
		element.add(ELEMENT_B);
		ColumnList columnList = new ColumnList(element);
		
		// First, get the result list.
		List<String> result = columnList.toList();
		
		// Then, check to be sure the lists are the same length.
		Assert.assertEquals(element.size(), result.size());
		
		// Finally, check that they contain the same elements.
		element.removeAll(result);
		Assert.assertEquals(0, element.size());
	}
}