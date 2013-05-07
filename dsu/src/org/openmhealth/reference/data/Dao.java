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
package org.openmhealth.reference.data;

import java.util.Properties;

/**
 * <p>
 * The parent class for all data access objects.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class Dao {
	/**
	 * The property key for the server address.
	 */
	public static final String PROPERTY_KEY_SERVER_ADDRESS =
		"db.server.address";
	
	/**
	 * The property key for the server port.
	 */
	public static final String PROPERTY_KEY_SERVER_PORT = "db.server.port";
	
	/**
	 * The property key for the name of the database.
	 */
	public static final String PROPERTY_KEY_DATABASE_NAME = "db.name";
	
	/**
	 * The address of the database server.
	 */
	private final String dbAddress;
	
	/**
	 * The port for the database server.
	 */
	private final int dbPort;
	
	/**
	 * The name of the database to use.
	 */
	private final String dbName;
	
	/**
	 * The singular instance of this DAO.
	 */
	private static Dao instance;

	/**
	 * Reads the configuration properties and extracts the necessary
	 * information.
	 * 
	 * @param properties
	 *        All of the additional properties given be the user.
	 */
	protected Dao(final Properties properties) {
		// Sanitize the properties parameter.
		Properties tProperties = properties;
		if(tProperties == null) {
			tProperties = new Properties();
		}
		
		// Get the server address.
		String tServerAddress =
			tProperties.getProperty(PROPERTY_KEY_SERVER_ADDRESS);
		if(tServerAddress == null) {
			tServerAddress = getDefaultServerAddress();
		}
		dbAddress = tServerAddress;
		
		// Get the server port.
		int tServerPort;
		String serverPortString = 
			tProperties.getProperty(PROPERTY_KEY_SERVER_PORT);
		if(serverPortString == null) {
			tServerPort = getDefaultServerPort();
		}
		else {
			try {
				tServerPort = Integer.decode(serverPortString);
			}
			catch(NumberFormatException e) {
				throw
					new IllegalArgumentException(
						"The server port is not a number.",
						e);
			}
		}
		dbPort = tServerPort;
		
		// Get the database name.
		String tDbName = tProperties.getProperty(PROPERTY_KEY_DATABASE_NAME);
		if(tDbName == null) {
			tDbName = getDefaultDatabaseName();
		}
		dbName = tDbName;
		
		// Set the instance of the DAO.
		instance = this;
	}
	
	/**
	 * Returns the database address.
	 * 
	 * @return The database address.
	 */
	public String getDatabaseAddress() {
		return dbAddress;
	}
	
	/**
	 * Returns the database port.
	 * 
	 * @return The database port.
	 */
	public int getDatabasePort() {
		return dbPort;
	}
	
	/**
	 * Returns the database name.
	 * 
	 * @return The database name.
	 */
	public String getDatabaseName() {
		return dbName;
	}
	
	/**
	 * Returns the instance of this DAO.
	 * 
	 * @return The instance of this DAO.
	 */
	public static Dao getInstance() {
		return instance;
	}

	/**
	 * Shut down the current instance, which man include closing database
	 * connections among other things.
	 */
	public abstract void shutdown();
	
	/**
	 * The default address to use for the database if one was not provided in
	 * the configuration files.
	 * 
	 * @return The default database server address.
	 */
	protected abstract String getDefaultServerAddress();
	
	/**
	 * The default port to use for the database if one was not provided in the
	 * configuration files.
	 *  
	 * @return The default database server port.
	 */
	protected abstract int getDefaultServerPort();
	
	/**
	 * The default name to use for the database when connecting to it.
	 * 
	 * @return The default database name.
	 */
	protected abstract String getDefaultDatabaseName();
}