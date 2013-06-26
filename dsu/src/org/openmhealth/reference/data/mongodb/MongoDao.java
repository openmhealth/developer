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
package org.openmhealth.reference.data.mongodb;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.openmhealth.reference.data.Dao;
import org.openmhealth.reference.exception.OmhException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * <p>
 * The data access class for all MongoDB data access objects.
 * </p>
 *
 * @author John Jenkins
 */
public class MongoDao extends Dao {
	/**
	 * The default server address.
	 */
	public static final String DEFAULT_SERVER_ADDRESS = "localhost";
	
	/**
	 * The default server port.
	 */
	public static final int DEFAULT_SERVER_PORT = 27017;
	
	/**
	 * The default name for the database.
	 */
	public static final String DEFAULT_DATABASE_NAME = "omh";
	
	/**
	 * The connection to the database.
	 */
	private final MongoClient mongo;

	/**
	 * Default constructor, which will create the connection to the MongoDB.
	 * 
	 * @param properties
	 *        The user-defined properties to use to setup the connection.
	 * 
	 * @throws OmhException
	 *         There was a problem setting up the connection to the database.
	 */
	public MongoDao(final Properties properties) throws OmhException {
		super(properties);
		
		// Create the singular Mongo instance.
		try {
			// Create the empty list of credentials.
			List<MongoCredential> credentials =
				new LinkedList<MongoCredential>();
			
			// If a username and password were given, use them.
			if(
				(MongoDao.getInstance().getDatabaseUsername() != null) &&
				(MongoDao.getInstance().getDatabasePassword() != null)) {
				
				
				credentials
					.add(
						MongoCredential
							.createMongoCRCredential(
								MongoDao.getInstance().getDatabaseUsername(),
								MongoDao.getInstance().getDatabaseName(),
								MongoDao
									.getInstance()
									.getDatabaseUsername()
									.toCharArray()));
			}
			
			// Create the MongoClient.
			mongo =
				new MongoClient(
					new ServerAddress(getDatabaseAddress(), getDatabasePort()),
					credentials);
		}
		catch(UnknownHostException e) {
			throw new OmhException("The database could not setup.", e);
		}
		
		// Instantiate the specific components.
		new MongoAuthenticationTokenBin();
		new MongoAuthorizationCodeBin();
		new MongoAuthorizationCodeResponseBin();
		new MongoAuthorizationTokenBin();
		new MongoDataSet();
		new MongoRegistry();
		new MongoThirdPartyBin();
		new MongoUserBin();
	}
	
	/**
	 * Returns the database connection to MongoDB.
	 * 
	 * @return The database to MongoDB.
	 */
	public DB getDb() {
		// Get the connection to the database.
		return mongo.getDB(getDatabaseName());
	}
	
	/**
	 * Shuts the DAO down.
	 */
	@Override
	public void shutdown() {
		mongo.close();
	}
	
	/**
	 * Returns the instance of this DAO as a MongoDao.
	 * 
	 * @return The instance of this DAO as a MongoDao.
	 * 
	 * @throws IllegalStateException
	 *         The DAO was not built with a MongoDao.
	 */
	public static MongoDao getInstance() {
		try {
			return (MongoDao) Dao.getInstance();
		}
		catch(ClassCastException e) {
			throw new IllegalStateException("The DAO is not a MongoDB DAO.");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Dao#getDefaultServerAddress()
	 */
	@Override
	protected String getDefaultServerAddress() {
		return DEFAULT_SERVER_ADDRESS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Dao#getDefaultServerPort()
	 */
	@Override
	protected int getDefaultServerPort() {
		return DEFAULT_SERVER_PORT;
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Dao#getDefaultDatabaseName()
	 */
	@Override
	protected String getDefaultDatabaseName() {
		return DEFAULT_DATABASE_NAME;
	}
}