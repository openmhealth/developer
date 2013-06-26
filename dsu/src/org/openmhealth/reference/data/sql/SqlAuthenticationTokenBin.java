package org.openmhealth.reference.data.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmhealth.reference.data.AuthenticationTokenBin;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.AuthenticationToken;
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
 * the {@link AuthenticationToken}s.
 * </p>
 *
 * @author John Jenkins
 */
public class SqlAuthenticationTokenBin
	extends AuthenticationTokenBin
	implements SqlDaoInterface {
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthenticationTokenBin#storeToken(org.openmhealth.reference.domain.AuthenticationToken)
	 */
	@Override
	public void storeToken(
		final AuthenticationToken token)
		throws OmhException {
		
		// Validate the parameter.
		if(token == null) {
			throw new OmhException("The token is null.");
		}
		
		// Get the DAO.
		SqlDao dao = SqlDao.getInstance();

		// Get the transaction manager.
		PlatformTransactionManager transactionManager =
			dao.getTransactionManager();
		
		// Create a new transaction definition and name it.
		DefaultTransactionDefinition transactionDefinition =
			new DefaultTransactionDefinition();
		transactionDefinition.setName("Adding an authentication token.");
		
		// Create the new transaction.
		TransactionStatus transactionStatus =
			transactionManager.getTransaction(transactionDefinition);
		
		// Get the JDBC template.
		JdbcTemplate jdbcTemplate = dao.getJdbcTemplate();
		
		// Add the authentication token.
		try {
			jdbcTemplate
				.update(
					"INSERT INTO " + AuthenticationTokenBin.DB_NAME +
						" (" +
							UserBin.DB_NAME + "_id" + ", " +
							AuthenticationToken.JSON_KEY_TOKEN + ", " +
							AuthenticationToken.JSON_KEY_GRANTED + ", " +
							AuthenticationToken.JSON_KEY_EXPIRES + " " +
						") " +
						"VALUES" +
						" (" +
							"(" +
								"SELECT " + SqlDao.KEY_DATABASE_ID + " " +
								"FROM " + UserBin.DB_NAME + " " +
								"WHERE " + User.JSON_KEY_USERNAME + " = ?" +
							"), " +
							"?, " +
							"?, " +
							"?" +
						")",
					new Object[] {
						token.getUsername(),
						token.getToken(),
						token.getGranted(),
						token.getExpires()
						}
					);
			
			// Commit the transaction.
			transactionManager.commit(transactionStatus);
		}
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw
				new OmhException(
					"There was a problem storing the authentication token.",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthenticationTokenBin#getToken(java.lang.String)
	 */
	@Override
	public AuthenticationToken getToken(
		final String token)
		throws OmhException {
		
		// Validate the parameter.
		if(token == null) {
			throw new OmhException("The token is null.");
		}
		
		try {
			return
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForObject(
						"SELECT " +
								User.JSON_KEY_USERNAME + ", " +
								AuthenticationToken.JSON_KEY_TOKEN + ", " +
								AuthenticationToken.JSON_KEY_GRANTED + ", " +
								AuthenticationToken.JSON_KEY_EXPIRES + " " +
							"FROM " +
								UserBin.DB_NAME + ", " +
								AuthenticationTokenBin.DB_NAME + " " +
							"WHERE " +
									UserBin.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									AuthenticationTokenBin.DB_NAME + 
									"." +
									UserBin.DB_NAME + "_id " +
							"AND " +
								AuthenticationToken.JSON_KEY_TOKEN + " = ?",
						new Object[] { token },
						new RowMapper<AuthenticationToken>() {
							/**
							 * Maps the row to an {@link AuthenticationToken}
							 * object. 
							 */
							@Override
							public AuthenticationToken mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								return
									new AuthenticationToken(
										resultSet
											.getString(
												AuthenticationToken
													.JSON_KEY_TOKEN),
										resultSet
											.getString(User.JSON_KEY_USERNAME),
										resultSet
											.getLong(
												AuthenticationToken
													.JSON_KEY_GRANTED),
										resultSet
											.getLong(
												AuthenticationToken
													.JSON_KEY_EXPIRES));
							}
						});
		}
		// If the problem is that the number of results isn't what we expected,
		// we may still be alright.
		catch(IncorrectResultSizeDataAccessException e) {
			// If there weren't any tokens with the given token value, then we
			// simply return null.
			if(e.getActualSize() == 0) {
				return null;
			}
			
			// Otherwise, we throw an exception.
			throw
				new OmhException(
					"Multiple authentication tokens have the same token " +
						"value.",
					e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for an authentication token.",
					e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.sql.SqlDaoInterface#getSqlTableDefinition()
	 */
	@Override
	public String getSqlTableDefinition() {
		return 
			// Create the table if it does not already exist.
			"CREATE TABLE IF NOT EXISTS " +
				AuthenticationTokenBin.DB_NAME + "(" +
					// Add the database ID.
					SqlDao.KEY_DATABASE_ID + 
						" int unsigned NOT NULL auto_increment, " +
					// Add the reference to the user table.
					UserBin.DB_NAME + "_id int unsigned NOT NULL, " +
					// Add the token field.
					AuthenticationToken.JSON_KEY_TOKEN +
						" varchar(36) NOT NULL, " +
					// Add the granted time field.
					AuthenticationToken.JSON_KEY_GRANTED +
						" bigint NOT NULL, " +
					// Add the expires time field.
					AuthenticationToken.JSON_KEY_EXPIRES +
						" bigint NOT NULL, " +
					// Create the primary key.
					"PRIMARY KEY (" + SqlDao.KEY_DATABASE_ID + "), " +
					// Create a unique index on the token field.
					"UNIQUE INDEX " +
						"`" +
							AuthenticationTokenBin.DB_NAME + 
								"_unique_index_" +
								AuthenticationToken.JSON_KEY_TOKEN +
						"` " +
						"(" + AuthenticationToken.JSON_KEY_TOKEN + "), " +
					// Create an index on the expires field.
					"INDEX " +
						"`" +
							AuthenticationTokenBin.DB_NAME + 
								"_index_" +
								AuthenticationToken.JSON_KEY_EXPIRES +
						"` " +
						"(" + AuthenticationToken.JSON_KEY_EXPIRES + "), " +
					// Link to the user table.
					"CONSTRAINT " +
						"`" +
							AuthenticationTokenBin.DB_NAME + 
								"_fk_" +
								UserBin.DB_NAME + "_id" +
						"` " +
						"FOREIGN KEY " +
						"`" +
							AuthenticationTokenBin.DB_NAME + 
								"_index_" +
								UserBin.DB_NAME + "_id" +
						"` " +
						"(" + UserBin.DB_NAME + "_id) " +
						"REFERENCES " + 
							UserBin.DB_NAME + " " + 
								"(" + SqlDao.KEY_DATABASE_ID + ") " +
								"ON UPDATE CASCADE " +
								"ON DELETE CASCADE" +
				")";
	}
}