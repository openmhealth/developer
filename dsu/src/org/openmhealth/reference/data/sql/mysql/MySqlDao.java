package org.openmhealth.reference.data.sql.mysql;

import java.util.Properties;

import org.openmhealth.reference.data.sql.SqlDao;

/**
 * <p>
 * The {@link Dao} for MySQL.
 * </p>
 *
 * @author John Jenkins
 */
public class MySqlDao extends SqlDao {
	/**
	 * The default server port.
	 */
	public static final int DEFAULT_SERVER_PORT = 3306;

	/**
	 * Initializes this DAO.
	 * 
	 * @param properties
	 *        The properties to use to configure this DAO.
	 */
	public MySqlDao(Properties properties) {
		super(properties);
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
	 * @see org.openmhealth.reference.data.sql.SqlDao#getJdbcUrl()
	 */
	@Override
	protected String getJdbcUrl() {
		return 
			"jdbc:mysql://" + 
				getDatabaseAddress() +
				":" + 
				getDatabasePort() +
				"/" +
				getDatabaseName();
	}
}