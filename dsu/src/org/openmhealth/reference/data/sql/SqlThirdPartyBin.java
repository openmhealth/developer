package org.openmhealth.reference.data.sql;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmhealth.reference.data.ThirdPartyBin;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.ThirdParty;
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
 * {@link ThirdParty} entities.
 * </p>
 *
 * @author John Jenkins
 */
public class SqlThirdPartyBin
	extends ThirdPartyBin
	implements SqlDaoInterface {
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.ThirdPartyBin#storeThirdParty(org.openmhealth.reference.domain.ThirdParty)
	 */
	@Override
	public void storeThirdParty(
		final ThirdParty thirdParty)
		throws OmhException {
		
		// Validate the parameter.
		if(thirdParty == null) {
			throw new OmhException("The third-party is null.");
		}
		
		// Get the DAO.
		SqlDao dao = SqlDao.getInstance();

		// Get the transaction manager.
		PlatformTransactionManager transactionManager =
			dao.getTransactionManager();
		
		// Create a new transaction definition and name it.
		DefaultTransactionDefinition transactionDefinition =
			new DefaultTransactionDefinition();
		transactionDefinition.setName("Adding a third-party.");
		
		// Create the new transaction.
		TransactionStatus transactionStatus =
			transactionManager.getTransaction(transactionDefinition);
		
		// Get the JDBC template.
		JdbcTemplate jdbcTemplate = dao.getJdbcTemplate();
		
		// Add the third-party.
		try {
			jdbcTemplate
				.update(
					"INSERT INTO " + ThirdPartyBin.DB_NAME + " (" +
							UserBin.DB_NAME + "_id" + ", " +
							ThirdParty.JSON_KEY_ID + ", " +
							ThirdParty.JSON_KEY_SHARED_SECRET + ", " +
							ThirdParty.JSON_KEY_NAME + ", " +
							ThirdParty.JSON_KEY_DESCRIPTION + ", " +
							ThirdParty.JSON_KEY_REDIRECT_URI + " " +
						") VALUES (" +
							"(" +
								"SELECT " + SqlDao.KEY_DATABASE_ID + " " +
								"FROM " + UserBin.DB_NAME + " " +
								"WHERE " + User.JSON_KEY_USERNAME + " = ?" +
							"), " +
							"?, " +
							"?, " +
							"?, " +
							"?, " +
							"?" +
						")",
					new Object[] {
							thirdParty.getOwner(),
							thirdParty.getId(),
							thirdParty.getSecret(),
							thirdParty.getName(),
							thirdParty.getDescription(),
							thirdParty.getRedirectUri().toString()
						}
					);
			
			// Commit the transaction.
			transactionManager.commit(transactionStatus);
		}
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw
				new OmhException(
					"There was a problem storing the third-party.",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.ThirdPartyBin#getThirdParty(java.lang.String)
	 */
	@Override
	public ThirdParty getThirdParty(
		final String thirdParty)
		throws OmhException {
		
		try {
			return
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForObject(
						"SELECT " +
								User.JSON_KEY_USERNAME + ", " +
								ThirdParty.JSON_KEY_ID + ", " +
								ThirdParty.JSON_KEY_SHARED_SECRET + ", " +
								ThirdParty.JSON_KEY_NAME + ", " +
								ThirdParty.JSON_KEY_DESCRIPTION + ", " +
								ThirdParty.JSON_KEY_REDIRECT_URI + " " +
							"FROM " +
								UserBin.DB_NAME + ", " +
								ThirdPartyBin.DB_NAME + " " +
							"WHERE " +
									UserBin.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									ThirdPartyBin.DB_NAME + 
									"." +
									UserBin.DB_NAME + "_id " +
							"AND " +
								ThirdParty.JSON_KEY_ID + " = ?",
						new String[] { thirdParty },
						new RowMapper<ThirdParty>() {
							/**
							 * Maps the row to a {@link ThirdParty} object. 
							 */
							@Override
							public ThirdParty mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								String username = 
									resultSet
										.getString(User.JSON_KEY_USERNAME);
								String id =
									resultSet
										.getString(ThirdParty.JSON_KEY_ID);
								String sharedSecret =
									resultSet
										.getString(
											ThirdParty.JSON_KEY_SHARED_SECRET);
								String name =
									resultSet
										.getString(ThirdParty.JSON_KEY_NAME);
								String description =
									resultSet
										.getString(
											ThirdParty.JSON_KEY_DESCRIPTION);
								URI uri;
								try {
									uri =
										new URI(
											resultSet
												.getString(
													ThirdParty
														.JSON_KEY_REDIRECT_URI));
								}
								catch(URISyntaxException e) {
									throw
										new SQLException(
											"The redirect URI is malformed.",
											e);
								}
								
								return
									new ThirdParty(
										username,
										id,
										sharedSecret,
										name,
										description,
										uri);
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
					"Multiple third-parties have the same ID: " + thirdParty,
					e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for a third-party.",
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
				ThirdPartyBin.DB_NAME + "(" +
					// Add the database ID, which should be the same value for
					// all tables.
					SqlDao.KEY_DATABASE_ID + 
						" int unsigned NOT NULL auto_increment, " +
					// Add a link to the user that owns this third-party.
					UserBin.DB_NAME + "_id int unsigned NOT NULL, " +
					// Add the unique identifier for this third-party.
					ThirdParty.JSON_KEY_ID + " varchar(36) NOT NULL, " +
					// Add the shared secret.
					ThirdParty.JSON_KEY_SHARED_SECRET + 
						" varchar(36) NOT NULL, " +
					// Add the user-friendly name.
					ThirdParty.JSON_KEY_NAME + 
						" varchar(255) NOT NULL, " +
					// Add the description.
					ThirdParty.JSON_KEY_DESCRIPTION + " text, " +
					// Add the redirect URI after a user does or doesn't grant
					// access.
					ThirdParty.JSON_KEY_REDIRECT_URI + 
						" text NOT NULL, " +
					// Set the primary key.
					"PRIMARY KEY (" + SqlDao.KEY_DATABASE_ID + "), " +
					// Add a unique index for unique identifier.
					"UNIQUE INDEX " +
						"`" +
							ThirdPartyBin.DB_NAME + 
								"_unique_index_" +
								ThirdParty.JSON_KEY_ID +
						"` " +
						"(" + ThirdParty.JSON_KEY_ID + "), " +
					// Link to the user table.
					"CONSTRAINT " +
						"`" +
							ThirdPartyBin.DB_NAME + 
								"_fk_" +
								UserBin.DB_NAME + "_id" +
						"` " +
						"FOREIGN KEY " +
						"`" +
							ThirdPartyBin.DB_NAME + 
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