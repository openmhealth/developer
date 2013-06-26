package org.openmhealth.reference.data.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmhealth.reference.data.AuthorizationCodeBin;
import org.openmhealth.reference.data.AuthorizationCodeResponseBin;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.AuthorizationCode;
import org.openmhealth.reference.domain.AuthorizationCodeResponse;
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
 * {@link AuthorizationCodeResponse}s.
 * </p>
 *
 * @author John Jenkins
 */
public class SqlAuthorizationCodeResponseBin
	extends AuthorizationCodeResponseBin
	implements SqlDaoInterface {

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeResponseBin#storeVerification(org.openmhealth.reference.domain.AuthorizationCodeResponse)
	 */
	@Override
	public void storeVerification(
		final AuthorizationCodeResponse response)
		throws OmhException {

		// Validate the parameter.
		if(response == null) {
			throw new OmhException("The response is null.");
		}
		
		// Get the DAO.
		SqlDao dao = SqlDao.getInstance();

		// Get the transaction manager.
		PlatformTransactionManager transactionManager =
			dao.getTransactionManager();
		
		// Create a new transaction definition and name it.
		DefaultTransactionDefinition transactionDefinition =
			new DefaultTransactionDefinition();
		transactionDefinition
			.setName("Adding an authorization code response.");
		
		// Create the new transaction.
		TransactionStatus transactionStatus =
			transactionManager.getTransaction(transactionDefinition);
		
		// Get the JDBC template.
		JdbcTemplate jdbcTemplate = dao.getJdbcTemplate();
		
		// Add the authorization code response.
		try {
			jdbcTemplate
				.update(
					"INSERT INTO " + AuthorizationCodeResponseBin.DB_NAME +
						" (" +
							UserBin.DB_NAME + "_id" + ", " +
							AuthorizationCodeBin.DB_NAME + "_id" + ", " +
							AuthorizationCodeResponse.JSON_KEY_GRANTED + " " +
						") " +
						"VALUES" +
						" (" +
							"(" +
								"SELECT " + SqlDao.KEY_DATABASE_ID + " " +
								"FROM " + UserBin.DB_NAME + " " +
								"WHERE " + User.JSON_KEY_USERNAME + " = ?" +
							"), " +
							"(" +
								"SELECT " + SqlDao.KEY_DATABASE_ID + " " +
								"FROM " + AuthorizationCodeBin.DB_NAME + " " +
								"WHERE " + 
									AuthorizationCode.JSON_KEY_CODE + " = ?" +
							"), " +
							"?" +
						")",
					new Object[] {
						response.getOwnerUsername(),
						response.getAuthorizationCode(),
						response.getGranted()
						}
					);
		}
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw
				new OmhException(
					"There was a problem storing the authorization code " +
						"response.",
					e);
		}
		
		// Commit the transaction.
		try {
			transactionManager.commit(transactionStatus);
		}
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw
				new OmhException(
					"There was a problem storing the authorization code " +
						"response.",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeResponseBin#getVerification(java.lang.String)
	 */
	@Override
	public AuthorizationCodeResponse getResponse(
		final String code)
		throws OmhException {

		// Validate the parameter.
		if(code == null) {
			throw new OmhException("The authorization code is null.");
		}
		
		try {
			return
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForObject(
						"SELECT " +
								User.JSON_KEY_USERNAME + ", " +
								AuthorizationCode.JSON_KEY_CODE + ", " +
								AuthorizationCodeResponse.JSON_KEY_GRANTED + 
									" " +
							"FROM " +
								UserBin.DB_NAME + ", " +
								AuthorizationCodeBin.DB_NAME + ", " +
								AuthorizationCodeResponseBin.DB_NAME + " " +
							"WHERE " +
									UserBin.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									AuthorizationCodeResponseBin.DB_NAME + 
									"." +
									UserBin.DB_NAME + "_id " +
							"AND " +
									AuthorizationCodeBin.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									AuthorizationCodeResponseBin.DB_NAME + 
									"." +
									AuthorizationCodeBin.DB_NAME + "_id " +
							"AND " +
								AuthorizationCode.JSON_KEY_CODE + " = ?",
						new Object[] { code },
						new RowMapper<AuthorizationCodeResponse>() {
							/**
							 * Maps the row to an
							 * {@link AuthorizationCodeResponse} object. 
							 */
							@Override
							public AuthorizationCodeResponse mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								return
									new AuthorizationCodeResponse(
										resultSet
											.getString(
												AuthorizationCode
													.JSON_KEY_CODE),
										resultSet
											.getString(User.JSON_KEY_USERNAME),
										resultSet
											.getBoolean(
												AuthorizationCodeResponse
													.JSON_KEY_GRANTED));
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
					"Multiple authorization code responses have the same " +
						"code value.",
					e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"Multiple authorization code responses have the same " +
						"code value.",
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
				AuthorizationCodeResponseBin.DB_NAME + "(" +
					// Add the database ID.
					SqlDao.KEY_DATABASE_ID + 
						" int unsigned NOT NULL auto_increment, " +
					// Add the reference to the user.
					UserBin.DB_NAME +
						"_id int unsigned NOT NULL, " +
					// Add the reference to the authorization code that backs
					// this response.
					AuthorizationCodeBin.DB_NAME + 
						"_id int unsigned NOT NULL, " +
					// Add the granted flag.
					AuthorizationCodeResponse.JSON_KEY_GRANTED +
						" bit NOT NULL, " +
					// Create the primary key.
					"PRIMARY KEY (" + SqlDao.KEY_DATABASE_ID + "), " +
					// Add the foreign key to the user table.
					"CONSTRAINT " +
						"`" +
							AuthorizationCodeResponseBin.DB_NAME + 
								"_fk_" +
								UserBin.DB_NAME + "_id" +
						"` " +
						"FOREIGN KEY " +
						"`" +
							AuthorizationCodeResponseBin.DB_NAME + 
								"_index_" +
								UserBin.DB_NAME + "_id" +
						"` " +
						"(" + UserBin.DB_NAME + "_id) " +
						"REFERENCES " + 
							UserBin.DB_NAME + " " + 
								"(" + SqlDao.KEY_DATABASE_ID + ") " +
								"ON UPDATE CASCADE " +
								"ON DELETE CASCADE, " +
					// Add the foreign key to the authorization code.
					"CONSTRAINT " +
						"`" +
							AuthorizationCodeResponseBin.DB_NAME + 
								"_fk_" +
								AuthorizationCodeBin.DB_NAME + "_id" +
						"` " +
						"FOREIGN KEY " +
						"`" +
							AuthorizationCodeResponseBin.DB_NAME + 
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