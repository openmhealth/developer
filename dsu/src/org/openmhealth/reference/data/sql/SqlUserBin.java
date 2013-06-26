package org.openmhealth.reference.data.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.exception.OmhException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

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
								User.JSON_KEY_PASSWORD + " " +
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
											.getString(
												User.JSON_KEY_PASSWORD));
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
				// Create the primary key.
				"PRIMARY KEY (" + SqlDao.KEY_DATABASE_ID + "), " +
				// Create an index on the username.
				"INDEX " +
					"`" +
						UserBin.DB_NAME + "_index_" + User.JSON_KEY_USERNAME +
					"` " +
					"(" + User.JSON_KEY_USERNAME + ")" +
			")";
	}
}