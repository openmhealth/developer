package org.openmhealth.reference.data.sql;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmhealth.reference.data.Dao;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * <p>
 * The {@link Dao} parent class for all SQL data access objects.
 * </p>
 *
 * @author John Jenkins
 */
public abstract class SqlDao extends Dao {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER =
		Logger.getLogger(SqlDao.class.getName());
	
	/**
	 * The default server address.
	 */
	public static final String DEFAULT_SERVER_ADDRESS = "localhost";
	
	/**
	 * The default name for the database.
	 */
	public static final String DEFAULT_DATABASE_NAME = "omh";
	
	/**
	 * The property key for the database driver class.
	 */
	public static final String KEY_PROPERTY_DATABASE_DRIVER =
		"db.sql.driverClass";
	
	/**
	 * The property key for the database driver class.
	 */
	public static final String KEY_PROPERTY_DATABASE_JDBC_URL =
		"db.sql.jdbcUrl";
	
	/**
	 * The database column name for the ID, which will be universal across all
	 * tables.
	 */
	public static final String KEY_DATABASE_ID = "id";
	
	/**
	 * The data source used to connect to the database.
	 */
	private final PooledDataSource dataSource;
	/**
	 * The JDBC template provided by SpringSource to make interacting with the
	 * database simpler.
	 */
	private final JdbcTemplate jdbcTemplate;

	/**
	 * Initializes this DAO.
	 * 
	 * @param properties The default and custom properties for this DAO.
	 */
	public SqlDao(final Properties properties) {
		super(properties);
		
		// Attempt to create the DataSource.
		ComboPooledDataSource comboPooledDataSource =
			new ComboPooledDataSource();
		
		// Set the properties from the properties file (the ones that begin
		// with "c3p0").
		comboPooledDataSource.setProperties(properties);

		// Attempt to load the driver to be used to connect to the database.
		if(properties.containsKey(KEY_PROPERTY_DATABASE_DRIVER)) {
			try {
				comboPooledDataSource
					.setDriverClass(
						properties.getProperty(KEY_PROPERTY_DATABASE_DRIVER));
			}
			catch(PropertyVetoException e) {
				LOGGER.log(Level.SEVERE, "The driver was rejected.", e);
				throw new IllegalStateException("The driver was rejected.", e);
			}
		}
		// Otherwise, we may error out.
		else {
			LOGGER
				.log(
					Level.SEVERE,
					"For SQL database connections, a driver must be " +
						"specified.");
		}
		
		// If the JDBC URL was given, use that.
		if(properties.containsKey(KEY_PROPERTY_DATABASE_JDBC_URL)) {
			comboPooledDataSource
				.setJdbcUrl(
					properties.getProperty(KEY_PROPERTY_DATABASE_JDBC_URL));
		}
		// Otherwise, ask the specific implementation for a default URL.
		else {
			comboPooledDataSource.setJdbcUrl(getJdbcUrl());
		}
		
		// Set the username and password.
		comboPooledDataSource.setUser(getDatabaseUsername());
		comboPooledDataSource.setPassword(getDatabasePassword());
		
		// Save the data source.
		dataSource = comboPooledDataSource;
		
		// Create the JDBC template from the data source.
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		// Initialize all of the components.
		initDaos(
			new SqlUserBin(),
			new SqlRegistry(),
			new SqlDataSet(),
			new SqlThirdPartyBin(),
			new SqlAuthenticationTokenBin(),
			new SqlAuthorizationCodeBin(),
			new SqlAuthorizationCodeResponseBin(),
			new SqlAuthorizationTokenBin());
	}
	
	/**
	 * Returns the JDBC template used to access the database more easily.
	 * 
	 * @return The JDBC template object for accessing the database.
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	/**
	 * Returns a transaction manager to be used to create transactions.
	 * 
	 * @return The transaction manager to be used to create transactions.
	 */
	public PlatformTransactionManager getTransactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Dao#shutdown()
	 */
	@Override
	public void shutdown() {
		try {
			dataSource.close();
		}
		catch(SQLException e) {
			LOGGER.log(
				Level.WARNING,
				"Failed to close the connection to the database",
				e);
		}
	}
	
	/**
	 * Returns the instance of this DAO as a MongoDao.
	 * 
	 * @return The instance of this DAO as a MongoDao.
	 * 
	 * @throws IllegalStateException
	 *         The DAO was not built with a MongoDao.
	 */
	public static SqlDao getInstance() {
		try {
			return (SqlDao) Dao.getInstance();
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
	 * @see org.openmhealth.reference.data.Dao#getDefaultDatabaseName()
	 */
	@Override
	protected String getDefaultDatabaseName() {
		return DEFAULT_DATABASE_NAME;
	}
	
	/**
	 * Creates and returns the JDBC URL to use to connect to the database.
	 * 
	 * @return The JDBC URL to use to connect to the database.
	 */
	protected abstract String getJdbcUrl();
	
	/**
	 * Initializes the DAOs' access to the database.
	 * 
	 * @param daoInterfaces
	 *        The collection of DAOs to initialize.
	 * 
	 * @throws IllegalStateException
	 *         There was a problem initializing one of the DAOs.
	 */
	private final void initDaos(final SqlDaoInterface... daoInterfaces) {
		// Create a transaction manager.
		PlatformTransactionManager transactionManager =
			new DataSourceTransactionManager(dataSource);
		
		// Create a new transaction definition and name it.
		DefaultTransactionDefinition transactionDefinition =
			new DefaultTransactionDefinition();
		transactionDefinition
			.setName("Initializing the Open mHealth DAO database tables.");
		
		// Create the new transaction.
		TransactionStatus transactionStatus =
			transactionManager.getTransaction(transactionDefinition);
		
		// Create the table if it does not exist.
		try {
			for(SqlDaoInterface daoInterface : daoInterfaces) {
				// Create the table if it does not exist.
				jdbcTemplate.execute(daoInterface.getSqlTableDefinition());
			}
		}
		// If creating the table fails, roll back the transaction and error
		// out.
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw
				new IllegalStateException(
					"There was an issue creating a DAO table definition.",
					e);
		}
		
		// TODO: This is where the DAO interface's update scripts would
		// be run.
		
		// Commit the transaction.
		try {
			transactionManager.commit(transactionStatus);
		}
		catch(TransactionException e) {
			transactionManager.rollback(transactionStatus);
			throw
				new IllegalStateException(
					"There was an error committing the transaction.",
					e);
		}
	}
}