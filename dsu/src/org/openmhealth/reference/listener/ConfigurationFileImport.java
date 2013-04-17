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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * <p>
 * Attempts to read the configuration file, if it exists, and stores it. Any
 * number of the configuration options may be chosen, and all users of this
 * class should have a default in place.
 * </p>
 * 
 * <p>
 * The file will be loaded when the web application starts. The main usage for
 * this class is the {@link #getCustomProperties()} function.
 * </p>
 *
 * @author John Jenkins
 */
public class ConfigurationFileImport implements ServletContextListener {
	/**
	 * The location of the default configuration file.
	 */
	private static final String CONFIG_FILE_DEFAULT = 
		"/WEB-INF/config/default.conf";
	/**
	 * The default location for the configuration file on Windows.
	 */
	private static final String CONFIG_FILE_DEFAULT_WINDOWS =
		"%PROGRAMDATA%\\OpenmHealth\\config\\omh.conf";
	/**
	 * The default location for the configuration file on POSIX-compliant
	 * systems.
	 */
	private static final String CONFIG_FILE_DEFAULT_POSIX = "/etc/omh.conf";
	
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER =
		Logger.getLogger(ConfigurationFileImport.class.getName());
	
	/**
	 * The custom properties.
	 */
	private static Properties customProperties;
	
	/**
	 * Default constructor.
	 */
	public ConfigurationFileImport() {
		// Do nothing.
	}

	/**
	 * Find the log file, if it exists, and add its properties to the system
	 * properties.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		// An empty Properties object that will first be populated with the
		// default configuration.
		Properties properties = new Properties();
		File defaultConfiguration = 
			new File(
				event.getServletContext().getRealPath("/") + 
					CONFIG_FILE_DEFAULT);
		try {
			properties.load(new FileReader(defaultConfiguration));
		}
		// The default properties file didn't exist, which is alarming.
		catch(FileNotFoundException e) {
			LOGGER
				.log(
					Level.WARNING, 
					"The default properties file is missing: " +
						defaultConfiguration.getAbsolutePath(),
					e);
		}
		// There was an error reading the default properties file.
		catch(IOException e) {
			LOGGER
				.log(
					Level.WARNING, 
					"There was an error reading the default properties " +
						"file: " +
						defaultConfiguration.getAbsolutePath(),
					e);
		}
		
		// Get a handler for the properties file based on the operating system.
		File propertiesFile;
		if(System.getProperty("os.name").contains("Windows")) {
			propertiesFile = new File(CONFIG_FILE_DEFAULT_WINDOWS);
		}
		else {
			propertiesFile = new File(CONFIG_FILE_DEFAULT_POSIX);
		}
		
		// Attempts to retrieve the custom configuration file and store it.
		try {
			properties.load(new FileReader(propertiesFile));
		}
		// The properties file didn't exist, which is fine.
		catch(FileNotFoundException e) {
			LOGGER
				.log(
					Level.INFO,
					"The properties file does not exist: " +
						propertiesFile.getAbsolutePath());
		}
		// There was a problem reading the properties.
		catch(IOException e) {
			LOGGER
				.log(
					Level.WARNING, 
					"There was an error reading the properties file: " +
						propertiesFile.getAbsolutePath(),
					e);
		}
		
		// Store the properties as a sub-object to the system properties.
		customProperties = properties;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Do nothing.
	}
	
	/**
	 * Returns the custom properties defined by the external configuration 
	 * file.
	 * 
	 * @return A valid {@link Properties} object, which may or may not contain
	 * 		   the desired property.
	 */
	public static Properties getCustomProperties() {
		return customProperties;
	}
}
