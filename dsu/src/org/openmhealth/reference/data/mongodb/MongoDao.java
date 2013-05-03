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
import java.util.Properties;

import org.openmhealth.reference.data.Dao;
import org.openmhealth.reference.exception.OmhException;

import com.mongodb.DB;
import com.mongodb.Mongo;

/**
 * <p>
 * The root class for all data access objects.
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
	private static final int DEFAULT_SERVER_PORT = 27017;
	
	/**
	 * The default name for the database.
	 */
	public static final String DEFAULT_DATABASE_NAME = "omh";
	
	/**
	 * The connection to the database.
	 */
	private final Mongo mongo;

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
			mongo = new Mongo(getDatabaseAddress(), getDatabasePort());
		}
		catch(UnknownHostException e) {
			throw new OmhException("The database could not setup.", e);
		}
		
		// Instantiate the specific components.
		new MongoAuthenticationTokenBin();
		new MongoAuthorizationCodeBin();
		new MongoAuthorizationCodeVerificationBin();
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
		return mongo.getDB(getDatabaseName());
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
	
	/**
	 * Shuts the DAO down.
	 */
	@Override
	public void shutdown() {
		mongo.close();
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