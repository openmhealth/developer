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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

/**
 * <p>
 * Sets up the logging system.
 * </p>
 * 
 * <p>
 * This must be called after the {@link ConfigurationFileImport} listener to
 * ensure that specialized configuration options have been accounted for.
 * </p>
 *
 * @author John Jenkins
 */
public class LogSetup implements ServletContextListener {
	/**
	 * Default constructor.
	 */
	public LogSetup() {
		// Do nothing.
	}

	/**
	 * Sets up the logger.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		// Setup the logging.
		PropertyConfigurator
			.configure(ConfigurationFileImport.getCustomProperties());
	}

	/**
	 * Cleans up anything with the logger.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Do nothing.
	}
}
