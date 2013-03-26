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