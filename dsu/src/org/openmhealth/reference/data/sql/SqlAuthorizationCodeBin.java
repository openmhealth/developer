package org.openmhealth.reference.data.sql;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.openmhealth.reference.data.AuthenticationTokenBin;
import org.openmhealth.reference.data.AuthorizationCodeBin;
import org.openmhealth.reference.data.ThirdPartyBin;
import org.openmhealth.reference.domain.AuthorizationCode;
import org.openmhealth.reference.domain.ThirdParty;
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
 * {@link AuthorizationCode}s.
 * </p>
 *
 * @author John Jenkins
 */
public class SqlAuthorizationCodeBin
	extends AuthorizationCodeBin
	implements SqlDaoInterface {

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeBin#storeCode(org.openmhealth.reference.domain.AuthorizationCode)
	 */
	@Override
	public void storeCode(final AuthorizationCode code) throws OmhException {
		// Validate the parameter.
		if(code == null) {
			throw new OmhException("The code is null.");
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
			.setName("Adding an authorization code.");
		
		// Create the new transaction.
		TransactionStatus transactionStatus =
			transactionManager.getTransaction(transactionDefinition);
		
		// Get the JDBC template.
		JdbcTemplate jdbcTemplate = dao.getJdbcTemplate();
		
		// Create the binary representation of the data.
		byte[] scopes;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(code.getScopes());
			scopes = baos.toByteArray();
		}
		catch(IOException e) {
			throw
				new OmhException(
					"There was an error creating the output stream.",
					e);
		}
		
		// Add the authorization code.
		try {
			jdbcTemplate
				.update(
					"INSERT INTO " + AuthorizationCodeBin.DB_NAME + 
						" (" +
							ThirdPartyBin.DB_NAME + "_id" + ", " +
							AuthorizationCode.JSON_KEY_CODE + ", " +
							AuthorizationCode.JSON_KEY_CREATION_TIME + ", " +
							AuthorizationCode.JSON_KEY_EXPIRATION_TIME + ", " +
							AuthorizationCode.JSON_KEY_SCOPES + ", " +
							AuthorizationCode.JSON_KEY_STATE + " " +
						") " +
						"VALUES" +
						" (" +
							"(" +
								"SELECT " + SqlDao.KEY_DATABASE_ID + " " +
								"FROM " + ThirdPartyBin.DB_NAME + " " +
								"WHERE " + ThirdParty.JSON_KEY_ID + " = ?" +
							"), " +
							"?, " +
							"?, " +
							"?, " +
							"?, " +
							"?" +
						")",
					new Object[] {
						code.getThirdPartyId(),
						code.getCode(),
						code.getCreationTime(),
						code.getExpirationTime(),
						scopes,
						code.getState()
						}
					);
			
			// Commit the transaction
			transactionManager.commit(transactionStatus);
		}
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw
				new OmhException(
					"There was a problem storing the authorization code.",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.AuthorizationCodeBin#getCode(java.lang.String)
	 */
	@Override
	public AuthorizationCode getCode(final String code) throws OmhException {
		// Validate the parameter.
		if(code == null) {
			throw new OmhException("The code is null.");
		}
		
		try {
			return
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForObject(
						"SELECT " +
								ThirdParty.JSON_KEY_ID + ", " +
								AuthorizationCode.JSON_KEY_CODE + ", " +
								AuthorizationCode.JSON_KEY_CREATION_TIME + 
									", " +
								AuthorizationCode.JSON_KEY_EXPIRATION_TIME + 
									", " +
								AuthorizationCode.JSON_KEY_SCOPES + ", " +
								AuthorizationCode.JSON_KEY_STATE + " " +
							"FROM " +
								ThirdPartyBin.DB_NAME  + ", " +
								AuthorizationCodeBin.DB_NAME + " " +
							"WHERE " +
									ThirdPartyBin.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									AuthorizationCodeBin.DB_NAME + 
									"." +
									ThirdPartyBin.DB_NAME  + "_id " +
							"AND " +
								AuthorizationCode.JSON_KEY_CODE + " = ?",
						new Object[] { code },
						new RowMapper<AuthorizationCode>() {
							/**
							 * Maps the row to an {@link AuthorizationCode}
							 * object. 
							 */
							@SuppressWarnings("unchecked")
							@Override
							public AuthorizationCode mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								// Decode the scopes byte array.
								Set<String> scopes;
								try {
									byte[] scopesArray =
										resultSet
											.getBytes(
												AuthorizationCode
													.JSON_KEY_SCOPES);
									ByteArrayInputStream bais =
										new ByteArrayInputStream(scopesArray);
									ObjectInputStream ois =
										new ObjectInputStream(bais);
									scopes = (Set<String>) ois.readObject();
								}
								catch(IOException e) {
									throw
										new SQLException(
											"The scopes object could not be " +
												"read or decoded.",
											e);
								}
								catch(ClassNotFoundException e) {
									throw
										new SQLException(
											"The Set class is unknown.",
											e);
								}
								
								return
									new AuthorizationCode(
										resultSet
											.getString(ThirdParty.JSON_KEY_ID),
										resultSet
											.getString(
												AuthorizationCode
													.JSON_KEY_CODE),
										resultSet
											.getLong(
												AuthorizationCode
													.JSON_KEY_CREATION_TIME),
										resultSet
											.getLong(
												AuthorizationCode
													.JSON_KEY_EXPIRATION_TIME),
										scopes,
										resultSet
											.getString(
												AuthorizationCode
													.JSON_KEY_STATE));
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
					"Multiple authorization codes have the same value.",
					e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for an authorization codes.",
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
			// Create the table if it doesn't exist.
			"CREATE TABLE IF NOT EXISTS " +
				AuthorizationCodeBin.DB_NAME + "(" +
					// Create the database ID.
					SqlDao.KEY_DATABASE_ID + 
						" int unsigned NOT NULL auto_increment, " +
					// Link it to the third-party table.
					ThirdPartyBin.DB_NAME + "_id int unsigned NOT NULL, " +
					// Store the code.
					AuthorizationCode.JSON_KEY_CODE +
						" varchar(36) NOT NULL, " +
					// Store the creation time.
					AuthorizationCode.JSON_KEY_CREATION_TIME +
						" bigint NOT NULL, " +
					// Store the expiration time.
					AuthorizationCode.JSON_KEY_EXPIRATION_TIME +
						" bigint NOT NULL, " +
					// Store the scope as a string that Java can easily encode
					// and decode.
					AuthorizationCode.JSON_KEY_SCOPES +
						" blob NOT NULL, " +
					// Store the state.
					// This is being saved as a VARCHAR, which may not be
					// sufficient, but it is more efficient than TEXT.
					AuthorizationCode.JSON_KEY_STATE +
						" varchar(255), " +
					// Create the primary key.
					"PRIMARY KEY (" + SqlDao.KEY_DATABASE_ID + "), " +
					// Ensure that all codes are unique.
					"UNIQUE INDEX " +
						"`" +
							AuthenticationTokenBin.DB_NAME + 
								"_unique_index_" +
								AuthorizationCode.JSON_KEY_CODE +
						"` " +
						"(" + AuthorizationCode.JSON_KEY_CODE + "), " +
					// Link to the third-party table.
					"CONSTRAINT " +
						"`" +
							AuthorizationCodeBin.DB_NAME + 
								"_fk_" +
								ThirdPartyBin.DB_NAME + "_id" +
						"` " +
						"FOREIGN KEY " +
						"`" +
							AuthorizationCodeBin.DB_NAME + 
								"_index_" +
								ThirdPartyBin.DB_NAME + "_id" +
						"` " +
						"(" + ThirdPartyBin.DB_NAME + "_id) " +
						"REFERENCES " + 
							ThirdPartyBin.DB_NAME + " " + 
								"(" + SqlDao.KEY_DATABASE_ID + ") " +
								"ON UPDATE CASCADE " +
								"ON DELETE CASCADE" +
				")";
	}
}