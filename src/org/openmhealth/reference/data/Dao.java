package org.openmhealth.reference.data;

import java.net.UnknownHostException;
import java.util.Properties;

import org.openmhealth.reference.exception.OmhException;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class Dao {
	/**
	 * The property key for the server address.
	 */
	public static final String PROPERTY_KEY_SERVER_ADDRESS = 
		"db.server.address";
	/**
	 * The default server address.
	 */
	private static final String DEFAULT_SERVER_ADDRESS = "localhost";
	
	/**
	 * The property key for the server port.
	 */
	public static final String PROPERTY_KEY_SERVER_PORT = "db.server.port";
	/**
	 * The default server port.
	 */
	private static final int DEFAULT_SERVER_PORT = 27017;
	
	/**
	 * The property key for the name of the database.
	 */
	public static final String PROPERTY_KEY_DATABASE_NAME = "db.name";
	/**
	 * The default name for the database.
	 */
	public static final String DEFAULT_DATABASE_NAME = "omh";
	
	/**
	 * The singular instance of this class.
	 */
	private static Dao instance;
	
	/**
	 * The connection to the database.
	 */
	private Mongo mongo;
	
	/**
	 * The name of the database to use.
	 */
	private String dbName;

	/**
	 * Default constructor.
	 */
	private Dao(final Properties properties) {
		// Get the server address.
		String serverAddress =
			properties.getProperty(PROPERTY_KEY_SERVER_ADDRESS);
		if(serverAddress == null) {
			serverAddress = DEFAULT_SERVER_ADDRESS;
		}
		
		// Get the server port.
		int serverPort;
		String serverPortString = 
			properties.getProperty(PROPERTY_KEY_SERVER_PORT);
		if(serverPortString == null) {
			serverPort = DEFAULT_SERVER_PORT;
		}
		else {
			try {
				serverPort = Integer.decode(serverPortString);
			}
			catch(NumberFormatException e) {
				throw
					new IllegalArgumentException(
						"The server port is not a number.",
						e);
			}
		}
		
		// Create the singular Mongo instance.
		try {
			mongo = new Mongo(serverAddress, serverPort);
		}
		catch(UnknownHostException e) {
			throw new OmhException("The database could not setup.", e);
		}
		
		// Get the database name.
		dbName = properties.getProperty(PROPERTY_KEY_DATABASE_NAME);
		if(dbName == null) {
			dbName = DEFAULT_DATABASE_NAME;
		}
	}
	
	/**
	 * Returns the database connection to MongoDB.
	 * 
	 * @return The database to MongoDB.
	 */
	public DB getDb() {
		return mongo.getDB(dbName);
	}
	
	/**
	 * Sets up this DAO. This may only be called once, before any database
	 * access has been requested.
	 * 
	 * @param properties The properties to use to setup this DAO.
	 */
	public static void setup(final Properties properties) {
		if(instance != null) {
			throw new IllegalStateException("The DAO has already been setup.");
		}
		
		if(properties == null) {
			instance = new Dao(new Properties());
		}
		else {
			instance = new Dao(properties);
		}
	}
	
	/**
	 * Shuts the DAO down.
	 */
	public static void shutdown() {
		// If it wasn't running, then we are done.
		if(instance == null) {
			return;
		}
		
		instance.mongo.close();
		instance = null;
	}
	
	/**
	 * Returns the instance of the data access object.
	 * 
	 * @return The instance of the data access object.
	 */
	public static Dao getInstance() {
		// If the class has not been setup, attempt to initialize it with the
		// defaults.
		if(instance == null) {
			instance = new Dao(new Properties());
		}
		
		return instance;
	}
}