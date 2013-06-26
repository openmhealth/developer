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
package org.openmhealth.reference.listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openmhealth.reference.data.Dao;

/**
 * <p>
 * Sets up the database.
 * </p>
 * 
 * <p>
 * This must be called after the {@link ConfigurationFileImport} listener to
 * ensure that specialized configuration options have been accounted for.
 * </p>
 * 
 * @author John Jenkins
 */
public class DatabaseSetup implements ServletContextListener {
	/**
	 * A {@link Logger} for this class.
	 */
	private static final Logger LOGGER =
		Logger.getLogger(DatabaseSetup.class.getName());
	
	/**
	 * The key that denotes which DAO class to use.
	 */
	public static final String PROPERTY_KEY_DATABASE_CLASS = "db.class";
	
	/**
	 * The DAO object to use to control the connection to the database.
	 */
	private Dao dao = null;
	
	/**
	 * Default constructor.
	 */
	public DatabaseSetup() {
		// Do nothing.
	}

	/**
	 * Setup the connection to the database. See the configuration file to
	 * change which database is setup.
	 */
	@Override
	public void contextInitialized(final ServletContextEvent event) {
		LOGGER.info("Setting up the DAO.");
		
		// Get the properties.
		Properties properties = ConfigurationFileImport.getCustomProperties();
		// If the database class property is missing, this is a critical error.
		if(! properties.containsKey(PROPERTY_KEY_DATABASE_CLASS)) {
			LOGGER
				.log(
					Level.SEVERE,
					"The database class is missing from the properties: " +
						PROPERTY_KEY_DATABASE_CLASS);
			throw
				new IllegalStateException(
					"The database class is missing from the properties: " +
						PROPERTY_KEY_DATABASE_CLASS);
		}
		
		// Get the class string.
		String daoClassString =
			properties.getProperty(PROPERTY_KEY_DATABASE_CLASS);

		// Create and store the DAO.
		try {
			dao =
				(Dao) Class
					.forName(daoClassString)
					.getConstructor(Properties.class)
					.newInstance(properties);
		}
		catch(
			ClassNotFoundException |
			SecurityException |
			NoSuchMethodException |
			IllegalArgumentException |
			InstantiationException |
			IllegalAccessException |
			InvocationTargetException
			e) {
			
			LOGGER
				.log(
					Level.SEVERE,
					"The DAO could not be created.: " + daoClassString,
					e);
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Closes the connection to the database.
	 */
	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		if(dao != null) {
			LOGGER.info("Shutting down the DAO.");
			dao.shutdown();
		}
	}
}