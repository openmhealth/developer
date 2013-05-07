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

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openmhealth.reference.data.Dao;
import org.openmhealth.reference.mongodb.data.MongoDao;

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
	
	private Dao dao = null;
	
	/**
	 * Default constructor.
	 */
	public DatabaseSetup() {
		// Do nothing.
	}

	/**
	 * <p>
	 * Setup the connection to the database.
	 * </p>
	 * 
	 * <p>
	 * To replace the default MongoDB backedn with another backend, edit this
	 * function to setup the proper DAO.
	 * </p>
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		LOGGER.info("Setting up the DAO.");
		dao = new MongoDao(ConfigurationFileImport.getCustomProperties());
	}

	/**
	 * Closes the connection to the database.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		LOGGER.info("Shutting down the DAO.");
		dao.shutdown();
	}
}
