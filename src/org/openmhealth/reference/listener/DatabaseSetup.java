package org.openmhealth.reference.listener;

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
	 * Default constructor.
	 */
	public DatabaseSetup() {
		// Do nothing.
	}

	/**
	 * Setup the connection to the database.
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		LOGGER.info("Setting up the DAO.");
		Dao.setup(ConfigurationFileImport.getCustomProperties());
	}

	/**
	 * Closes the connection to the database.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		LOGGER.info("Shutting down the DAO.");
		Dao.shutdown();
	}
}