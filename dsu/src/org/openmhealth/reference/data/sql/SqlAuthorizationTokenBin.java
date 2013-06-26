package org.openmhealth.reference.data.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmhealth.reference.data.AuthorizationCodeBin;
import org.openmhealth.reference.data.AuthorizationTokenBin;
import org.openmhealth.reference.domain.AuthorizationCode;
import org.openmhealth.reference.domain.AuthorizationToken;
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
 * {@link AuthorizationToken}s.
 * </p>
 *
 * @author John Jenkins
 */
public class SqlAuthorizationTokenBin
	extends AuthorizationTokenBin
	implements SqlDaoInterface {

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationTokenBin#storeToken(org.openmhealth.reference.domain.AuthorizationToken)
	 */
	@Override
	public void storeToken(
		final AuthorizationToken token)
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
		transactionDefinition.setName("Adding an authorization token.");
		
		// Create the new transaction.
		TransactionStatus transactionStatus =
			transactionManager.getTransaction(transactionDefinition);
		
		// Get the JDBC template.
		JdbcTemplate jdbcTemplate = dao.getJdbcTemplate();
		
		// Add the authorization token.
		try {
			jdbcTemplate
				.update(
					"INSERT INTO " + AuthorizationTokenBin.DB_NAME +
						" (" +
							AuthorizationCodeBin.DB_NAME + "_id" + ", " +
							AuthorizationToken.JSON_KEY_ACCESS_TOKEN + ", " +
							AuthorizationToken.JSON_KEY_REFRESH_TOKEN + ", " +
							AuthorizationToken.JSON_KEY_CREATION_TIME + ", " +
							AuthorizationToken.JSON_KEY_EXPIRATION_TIME + " " +
						") " +
						"VALUES" +
						" (" +
							"(" +
								"SELECT " + SqlDao.KEY_DATABASE_ID + " " +
								"FROM " + AuthorizationCodeBin.DB_NAME + " " +
								"WHERE " +
									AuthorizationCode.JSON_KEY_CODE + 
									" = ?" +
							"), " +
							"?, " +
							"?, " +
							"?, " +
							"?" +
						")",
					new Object[] {
						token.getAuthorizationCodeString(),
						token.getAccessToken(),
						token.getRefreshToken(),
						token.getCreationTime(),
						token.getExpirationTime()
						}
					);
			
			// Commit the transaction.
			transactionManager.commit(transactionStatus);
		}
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw
				new OmhException(
					"There was a problem storing the authorization token.",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationTokenBin#getTokenFromAccessToken(java.lang.String)
	 */
	@Override
	public AuthorizationToken getTokenFromAccessToken(
		final String accessToken)
		throws OmhException {
		
		// Validate the parameter.
		if(accessToken == null) {
			throw new OmhException("The access token is null.");
		}
		
		try {
			return
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForObject(
						"SELECT " +
								AuthorizationCode.JSON_KEY_CODE + ", " +
								AuthorizationToken.JSON_KEY_ACCESS_TOKEN + 
									", " +
								AuthorizationToken.JSON_KEY_REFRESH_TOKEN +
									", " +
								AuthorizationTokenBin.DB_NAME + "." +
									AuthorizationToken.JSON_KEY_CREATION_TIME +
									", " +
								AuthorizationTokenBin.DB_NAME + "." +
									AuthorizationToken
										.JSON_KEY_EXPIRATION_TIME +
									" " +
							"FROM " +
								AuthorizationCodeBin.DB_NAME + ", " +
								AuthorizationTokenBin.DB_NAME + " " +
							"WHERE " +
									AuthorizationCodeBin.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									AuthorizationTokenBin.DB_NAME + 
									"." +
									AuthorizationCodeBin.DB_NAME + "_id " +
							"AND " +
								AuthorizationToken.JSON_KEY_ACCESS_TOKEN +
									" = ?",
						new Object[] { accessToken },
						new RowMapper<AuthorizationToken>() {
							/**
							 * Maps the row to an {@link AuthorizationToken}
							 * object. 
							 */
							@Override
							public AuthorizationToken mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								return
									new AuthorizationToken(
										resultSet
											.getString(
												AuthorizationCode
													.JSON_KEY_CODE),
										resultSet
											.getString(
												AuthorizationToken
													.JSON_KEY_ACCESS_TOKEN),
										resultSet
											.getString(
												AuthorizationToken
													.JSON_KEY_REFRESH_TOKEN),
										resultSet
											.getLong(
												AuthorizationToken
													.JSON_KEY_CREATION_TIME),
										resultSet
											.getLong(
												AuthorizationToken
													.JSON_KEY_EXPIRATION_TIME));
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
					"Multiple authorization tokens have the same access " +
						"token value.",
					e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for an authorization token.",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationTokenBin#getTokenFromRefreshToken(java.lang.String)
	 */
	@Override
	public AuthorizationToken getTokenFromRefreshToken(
		final String refreshToken)
		throws OmhException {
		
		// Validate the parameter.
		if(refreshToken == null) {
			throw new OmhException("The refresh token is null.");
		}
		
		try {
			return
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForObject(
						"SELECT " +
								AuthorizationCode.JSON_KEY_CODE + ", " +
								AuthorizationToken.JSON_KEY_ACCESS_TOKEN + 
									", " +
								AuthorizationToken.JSON_KEY_REFRESH_TOKEN +
									", " +
								AuthorizationTokenBin.DB_NAME + "." +
									AuthorizationToken.JSON_KEY_CREATION_TIME +
									", " +
								AuthorizationTokenBin.DB_NAME + "." +
									AuthorizationToken
										.JSON_KEY_EXPIRATION_TIME +
									" " +
							"FROM " +
								AuthorizationCodeBin.DB_NAME + ", " +
								AuthorizationTokenBin.DB_NAME + " " +
							"WHERE " +
									AuthorizationCodeBin.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									AuthorizationTokenBin.DB_NAME + 
									"." +
									AuthorizationCodeBin.DB_NAME + "_id " +
							"AND " +
								AuthorizationToken.JSON_KEY_REFRESH_TOKEN +
									" = ?",
						new Object[] { refreshToken },
						new RowMapper<AuthorizationToken>() {
							/**
							 * Maps the row to an {@link AuthorizationToken}
							 * object. 
							 */
							@Override
							public AuthorizationToken mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								return
									new AuthorizationToken(
										resultSet
											.getString(
												AuthorizationCode
													.JSON_KEY_CODE),
										resultSet
											.getString(
												AuthorizationToken
													.JSON_KEY_ACCESS_TOKEN),
										resultSet
											.getString(
												AuthorizationToken
													.JSON_KEY_REFRESH_TOKEN),
										resultSet
											.getLong(
												AuthorizationToken
													.JSON_KEY_CREATION_TIME),
										resultSet
											.getLong(
												AuthorizationToken
													.JSON_KEY_EXPIRATION_TIME));
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
					"Multiple authorization tokens have the same refresh " +
						"token value.",
					e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for an authorization token.",
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
			// Create the table if it does not exist.
			"CREATE TABLE IF NOT EXISTS " +
				AuthorizationTokenBin.DB_NAME + "(" +
					// Add the database ID column.
					SqlDao.KEY_DATABASE_ID + 
						" int unsigned NOT NULL auto_increment, " +
					// Add the link to the authorization code that backs this
					// authorization token.
					AuthorizationCodeBin.DB_NAME + 
						"_id int unsigned NOT NULL, " +
					// Add the access token.
					AuthorizationToken.JSON_KEY_ACCESS_TOKEN +
						" varchar(36) NOT NULL, " +
					// Add the refresh token.
					AuthorizationToken.JSON_KEY_REFRESH_TOKEN +
						" varchar(36) NOT NULL, " +
					// Add the creation time.
					AuthorizationToken.JSON_KEY_CREATION_TIME +
						" bigint NOT NULL, " +
					// Add the expiration time.
					AuthorizationToken.JSON_KEY_EXPIRATION_TIME +
						" bigint NOT NULL, " +
					// Create the primary key.
					"PRIMARY KEY (" + SqlDao.KEY_DATABASE_ID + "), " +
					// Create the unique index for the access token.
					"UNIQUE INDEX " +
						"`" +
							AuthorizationTokenBin.DB_NAME + 
								"_unique_index_" +
								AuthorizationToken.JSON_KEY_ACCESS_TOKEN +
						"` " +
						"(" + AuthorizationToken.JSON_KEY_ACCESS_TOKEN + ")" +
						", " +
					// Create the unique index for the refresh token.
					"UNIQUE INDEX " +
						"`" +
							AuthorizationTokenBin.DB_NAME + 
								"_unique_index_" +
								AuthorizationToken.JSON_KEY_REFRESH_TOKEN +
						"` " +
						"(" + AuthorizationToken.JSON_KEY_REFRESH_TOKEN + ")" +
						", " +
					// Link back to the authorization code.
					"CONSTRAINT " +
						"`" +
							AuthorizationTokenBin.DB_NAME + 
								"_fk_" +
								AuthorizationCodeBin.DB_NAME + "_id" +
						"` " +
						"FOREIGN KEY " +
						"`" +
							AuthorizationTokenBin.DB_NAME + 
								"_index_" +
								AuthorizationCodeBin.DB_NAME + "_id" +
						"` " +
						"(" + AuthorizationCodeBin.DB_NAME + "_id) " +
						"REFERENCES " + 
							AuthorizationCodeBin.DB_NAME + " " + 
								"(" + SqlDao.KEY_DATABASE_ID + ") " +
								"ON UPDATE CASCADE " +
								"ON DELETE CASCADE" +
				")";
	}
}