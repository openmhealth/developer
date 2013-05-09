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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openmhealth.reference.exception.OmhException;

/**
 * <p>
 * A list of desired columns.
 * </p>
 * 
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author John Jenkins
 */
public class ColumnList {
	/**
	 * The separator for columns as defined by Open mHealth.
	 */
	public static final String COLUMN_SEPARATOR = ".";
	
	/**
	 * The map of children nodes.
	 */
	Map<String, ColumnList> children = new HashMap<String, ColumnList>();
	
	/**
	 * Converts a list of {@link ColumnList#COLUMN_SEPARATOR}-separated columns
	 * into a {@link ColumnList} object.
	 * 
	 * @param columns
	 *        The list of columns.
	 */
	public ColumnList(final List<String> columns) {
		// If the columns are null, then this should just be an empty object.
		if(columns == null) {
			return;
		}
		
		// Add each of the columns as children.
		for(String column : columns) {
			// Process the node.
			addChild(column);
		}
	}

	/**
	 * Creates a new {@link ColumnList} object from the column list.
	 * 
	 * @param subColumns
	 *        The string representing the sub-column and, possibly, its
	 *        sub-columns.
	 */
	private ColumnList(final String subColumns) {
		addChild(subColumns);
	}
	
	/**
	 * Returns the number of child elements at this depth.
	 * 
	 * @return The number of child elements at this depth.
	 */
	public long size() {
		return children.size();
	}
	
	/**
	 * Creates a list of {@link #COLUMN_SEPARATOR}-separated strings that
	 * represent this column list.
	 * 
	 * @return A list of {@link #COLUMN_SEPARATOR}-separated strings that
	 *         represent this list.
	 */
	public List<String> toList() {
		List<String> result = new LinkedList<String>();
		
		for(String childName : children.keySet()) {
			ColumnList child = children.get(childName);
			if(child == null) {
				result.add(childName);
			}
			else {
				for(String childList : child.toList()) {
					result.add(childName + COLUMN_SEPARATOR + childList);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Adds a new child to the current list of children.
	 * 
	 * @param column The string representing the child which may be composed of
	 * 				 multiple sub-columns.
	 * 
	 * @throws OmhException The column is null or an empty string.
	 */
	private void addChild(final String column) throws OmhException {
		if(column == null) {
			throw new OmhException("The column is null.");
		}
		if(column.trim().length() == 0) {
			throw new OmhException("The column is empty.");
		}
		
		String[] parts = column.split("\\" + COLUMN_SEPARATOR, 2);
		String childName = parts[0];
		
		if(parts.length == 1) {
			children.put(childName, null);
		}
		else if(children.containsKey(childName)) {
			ColumnList child = children.get(childName);
			if(child != null) {
				child.addChild(parts[1]);
			}
		}
		else {
			children.put(childName, new ColumnList(parts[1]));
		}
	}
}