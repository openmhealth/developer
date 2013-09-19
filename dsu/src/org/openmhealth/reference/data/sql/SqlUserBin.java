package org.openmhealth.reference.data.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.OmhException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * <p>
 * The SQL implementation of the interface to the database-backed collection of
 * {@link User}s.
 * </p>
 *
 * @author John Jenkins
 */
public class SqlUserBin
	extends UserBin
	implements SqlDaoInterface {

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.UserBin#createUser(org.openmhealth.reference.domain.User)
	 */
	@Override
	public void createUser(final User user) throws OmhException {
		// Validate the parameter.
		if(user == null) {
			throw new OmhException("The user is null.");
		}
		
		// Get the DAO.
		SqlDao dao = SqlDao.getInstance();

		// Get the transaction manager.
		PlatformTransactionManager transactionManager =
			dao.getTransactionManager();
		
		// Create a new transaction definition and name it.
		DefaultTransactionDefinition transactionDefinition =
			new DefaultTransactionDefinition();
		transactionDefinition.setName("Adding a user.");
		
		// Create the new transaction.
		TransactionStatus transactionStatus =
			transactionManager.getTransaction(transactionDefinition);
		
		// Get the JDBC template.
		JdbcTemplate jdbcTemplate = dao.getJdbcTemplate();
		
		// Add the authentication token.
		try {
			jdbcTemplate
				.update(
					"INSERT INTO " + UserBin.DB_NAME +
						" (" +
							User.JSON_KEY_USERNAME + ", " +
							User.JSON_KEY_PASSWORD + ", " +
							User.JSON_KEY_EMAIL + ", " +
							User.JSON_KEY_REGISTRATION_KEY + ", " +
							User.JSON_KEY_DATE_REGISTERED + ", " +
							User.JSON_KEY_DATE_ACTIVATED + " " +
						") " +
						"VALUES" +
						" (" +
							"?, " +
							"?, " +
							"?, " +
							"?, " +
							"?, " +
							"?" +
						")",
					new Object[] {
						user.getUsername(),
						user.getPassword(),
						user.getEmail().toString(),
						user.getRegistratioKey(),
						user.getDateRegistered(),
						user.getDateActivated()
					}
				);
			
			// Commit the transaction.
			transactionManager.commit(transactionStatus);
		}
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw new OmhException("There was a problem storing the user.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.UserBin#getUser(java.lang.String)
	 */
	@Override
	public User getUser(final String username) throws OmhException {
		try {
			return
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForObject(
						"SELECT " +
								User.JSON_KEY_USERNAME + ", " +
								User.JSON_KEY_PASSWORD + ", " +
								User.JSON_KEY_EMAIL + ", " +
								User.JSON_KEY_REGISTRATION_KEY + ", " +
								User.JSON_KEY_DATE_REGISTERED + ", " +
								User.JSON_KEY_DATE_ACTIVATED + " " +
							"FROM " +
								UserBin.DB_NAME + " " +
							"WHERE " +
								User.JSON_KEY_USERNAME + " = ?",
						new String[] { username },
						new RowMapper<User>() {
							/**
							 * Maps the row to a {@link User} object.
							 */
							@Override
							public User mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								return
									new User(
										resultSet
											.getString(User.JSON_KEY_USERNAME),
										resultSet
											.getString(User.JSON_KEY_PASSWORD),
										resultSet
											.getString(User.JSON_KEY_EMAIL),
										resultSet
											.getString(
												User.JSON_KEY_REGISTRATION_KEY),
										resultSet
											.getLong(
												User.JSON_KEY_DATE_REGISTERED),
										resultSet
											.getLong(
												User.JSON_KEY_DATE_ACTIVATED));
							}
						});
		}
		// If the problem is that the number of results isn't what we expected,
		// we may still be alright.
		catch(IncorrectResultSizeDataAccessException e) {
			// If there weren't any users with the given username, then we
			// simply return null.
			if(e.getActualSize() == 0) {
				return null;
			}
			
			// Otherwise, we throw an exception.
			throw
				new OmhException("Multiple users have the same username.", e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException("There was an error querying for a user.", e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.UserBin#getUserFromRegistrationId(java.lang.String)
	 */
	@Override
	public User getUserFromRegistrationId(
		final String registrationId)
		throws OmhException {
		
		try {
			return
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForObject(
						"SELECT " +
								User.JSON_KEY_USERNAME + ", " +
								User.JSON_KEY_PASSWORD + ", " +
								User.JSON_KEY_EMAIL + ", " +
								User.JSON_KEY_REGISTRATION_KEY + ", " +
								User.JSON_KEY_DATE_REGISTERED + ", " +
								User.JSON_KEY_DATE_ACTIVATED + " " +
							"FROM " +
								UserBin.DB_NAME + " " +
							"WHERE " +
								User.JSON_KEY_REGISTRATION_KEY + " = ?",
						new String[] { registrationId },
						new RowMapper<User>() {
							/**
							 * Maps the row to a {@link User} object.
							 */
							@Override
							public User mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								// Get the date registered and check for null.
								Long dateRegistered =
									resultSet
										.getLong(
											User.JSON_KEY_DATE_REGISTERED);
								if(resultSet.wasNull()) {
									dateRegistered = null;
								}

								// Get the date activated and check for null.
								Long dateActivated =
									resultSet
										.getLong(User.JSON_KEY_DATE_ACTIVATED);
								if(resultSet.wasNull()) {
									dateActivated = null;
								}
								
								return
									new User(
										resultSet
											.getString(User.JSON_KEY_USERNAME),
										resultSet
											.getString(User.JSON_KEY_PASSWORD),
										resultSet
											.getString(User.JSON_KEY_EMAIL),
										resultSet
											.getString(
												User.JSON_KEY_REGISTRATION_KEY),
										dateRegistered,
										dateActivated);
							}
						});
		}
		// If the problem is that the number of results isn't what we expected,
		// we may still be alright.
		catch(IncorrectResultSizeDataAccessException e) {
			// If there weren't any users with the given username, then we
			// simply return null.
			if(e.getActualSize() == 0) {
				return null;
			}
			
			// Otherwise, we throw an exception.
			throw
				new OmhException(
					"Multiple users have the same registration ID.",
					e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException("There was an error querying for a user.", e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.UserBin#updateUser(org.openmhealth.reference.domain.User)
	 */
	@Override
	public void updateUser(final User user) throws OmhException {
		// Validate the parameter.
		if(user == null) {
			throw new OmhException("The user is null.");
		}
		
		// Get the DAO.
		SqlDao dao = SqlDao.getInstance();

		// Get the transaction manager.
		PlatformTransactionManager transactionManager =
			dao.getTransactionManager();
		
		// Create a new transaction definition and name it.
		DefaultTransactionDefinition transactionDefinition =
			new DefaultTransactionDefinition();
		transactionDefinition.setName("Adding a user.");
		
		// Create the new transaction.
		TransactionStatus transactionStatus =
			transactionManager.getTransaction(transactionDefinition);
		
		// Get the JDBC template.
		JdbcTemplate jdbcTemplate = dao.getJdbcTemplate();
		
		// Add the authentication token.
		try {
			jdbcTemplate
				.update(
					"UPDATE " + UserBin.DB_NAME + " " +
					"SET " +
						User.JSON_KEY_PASSWORD + " = ?, " +
						User.JSON_KEY_EMAIL + " = ?, " +
						User.JSON_KEY_REGISTRATION_KEY + " = ?, " +
						User.JSON_KEY_DATE_REGISTERED + " = ?, " +
						User.JSON_KEY_DATE_ACTIVATED + " = ? " +
					"WHERE " + User.JSON_KEY_USERNAME + " = ?",
					new Object[] {
						user.getPassword(),
						user.getEmail().toString(),
						user.getRegistratioKey(),
						user.getDateRegistered(),
						user.getDateActivated(),
						user.getUsername()
					}
				);
			
			// Commit the transaction.
			transactionManager.commit(transactionStatus);
		}
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw new OmhException("There was a problem storing the user.", e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.sql.SqlDaoInterface#getSqlTableDefinition()
	 */
	@Override
	public String getSqlTableDefinition() {
		return 
			// Create the table if it does not exist.
			"CREATE TABLE IF NOT EXISTS " + UserBin.DB_NAME + "(" +
				// Add the database ID.
				SqlDao.KEY_DATABASE_ID + 
					" int unsigned NOT NULL auto_increment, " +
				// Add the username.
				User.JSON_KEY_USERNAME + " varchar(36) NOT NULL, " +
				// Add the password.
				User.JSON_KEY_PASSWORD + " varchar(60) NOT NULL, " +
				// Add the email address.
				User.JSON_KEY_EMAIL + " varchar(255) NOT NULL, " +
				// Add the registration key.
				User.JSON_KEY_REGISTRATION_KEY + " varchar(255), " +
				// Add the date the account was registered.
				User.JSON_KEY_DATE_REGISTERED + " bigint, " +
				// Add the date the account was activated.
				User.JSON_KEY_DATE_ACTIVATED + " bigint, " +
				// Create the primary key.
				"PRIMARY KEY (" + SqlDao.KEY_DATABASE_ID + "), " +
				// Create an unique index on the username.
				"UNIQUE INDEX " +
					"`" +
						UserBin.DB_NAME +
							"_unique_index_" +
							User.JSON_KEY_USERNAME +
					"` " +
					"(" + User.JSON_KEY_USERNAME + "), " +
				// Create an unique index on the registration key.
				"UNIQUE INDEX " +
					"`" +
						UserBin.DB_NAME +
							"_unique_index_" +
							User.JSON_KEY_REGISTRATION_KEY +
					"` " +
					"(" + User.JSON_KEY_REGISTRATION_KEY + ")" +
			")";
	}
}