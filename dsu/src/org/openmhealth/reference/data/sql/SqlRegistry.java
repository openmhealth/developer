package org.openmhealth.reference.data.sql;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.mongojack.internal.MongoJacksonMapperModule;
import org.openmhealth.reference.concordia.OmhValidationController;
import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.domain.MultiValueResult;
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.domain.sql.SqlMultiValueResult;
import org.openmhealth.reference.exception.OmhException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * The SQL implementation of the interface to the database-backed collection of
 * the {@link Registry}.
 * </p>
 *
 * @author John Jenkins
 */
public class SqlRegistry
	extends Registry
	implements SqlDaoInterface {
	
	/**
	 * The object mapper that should be used to parse {@link Schema}s.
	 */
	private static final ObjectMapper JSON_MAPPER;
	static {
		// Create the object mapper.
		ObjectMapper mapper = new ObjectMapper();
		
		// Add our custom validation controller as an injectable parameter to
		// the Schema's constructor.
		InjectableValues.Std injectableValues = new InjectableValues.Std();
		injectableValues
			.addValue(
				Schema.JSON_KEY_VALIDATION_CONTROLLER,
				OmhValidationController.VALIDATION_CONTROLLER);
		mapper.setInjectableValues(injectableValues);
		
		// Finally, we must configure the mapper to work with the MongoJack
		// configuration.
		JSON_MAPPER = MongoJacksonMapperModule.configure(mapper);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Registry#getSchemaIds()
	 */
	@Override
	public MultiValueResult<String> getSchemaIds(
		final long numToSkip,
		final long numToReturn) {
		
		// Retrieve the list of results.
		List<String> list;
		try {
			list =
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.query(
						"SELECT DISTINCT(" + Schema.JSON_KEY_ID + ") " +
							"FROM " + Registry.DB_NAME + " " +
							"ORDER BY " +
								Schema.JSON_KEY_ID + ", " +
								Schema.JSON_KEY_VERSION + " " + 
							"LIMIT ?, ?",
						new Object[] { numToSkip, numToReturn },
						new SingleColumnRowMapper<String>());
		}
		// For all issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for the schema IDs.",
					e);
		}
		
		// Retrieve the total count of results.
		int count;
		try {
			count =
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForInt(
						"SELECT COUNT(" + SqlDao.KEY_DATABASE_ID + ") " +
							"FROM " + Registry.DB_NAME);
		}
		// If the problem is that the number of results isn't what we expected,
		// we may still be alright.
		catch(IncorrectResultSizeDataAccessException e) {			
			// Otherwise, we throw an exception.
			throw
				new OmhException(
					"A count query returned more than one result.",
					e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for the schema IDs count.",
					e);
		}
		
		// Return the result
		return new SqlMultiValueResult<String>(list, count);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Registry#getSchemaIds()
	 */
	@Override
	public MultiValueResult<Long> getSchemaVersions(
		final String schemaId,
		final long numToSkip,
		final long numToReturn) {
		
		// Validate the schema ID.
		if(schemaId == null) {
			throw new OmhException("The schema ID is null.");
		}
		
		// Retrieve the list of results.
		List<Long> list;
		try {
			list =
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.query(
						"SELECT DISTINCT(" + Schema.JSON_KEY_VERSION + ") " +
							"FROM " + Registry.DB_NAME + " " +
							"WHERE " + Schema.JSON_KEY_ID + " = ? " +
							"ORDER BY " + Schema.JSON_KEY_VERSION + " " + 
							"LIMIT ?, ?",
						new Object[] { schemaId, numToSkip, numToReturn },
						new SingleColumnRowMapper<Long>());
		}
		// For all issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for the schema versions.",
					e);
		}
		
		// Retrieve the total count of results.
		int count;
		try {
			count =
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForInt(
						"SELECT COUNT(" + SqlDao.KEY_DATABASE_ID + ") " +
							"FROM " + Registry.DB_NAME + " " +
							"WHERE " + Schema.JSON_KEY_ID + " = ?",
						new Object[] { schemaId });
		}
		// For all issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for schema versions count.",
					e);
		}
		
		return new SqlMultiValueResult<Long>(list, count);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Registry#getSchema(java.lang.String, long)
	 */
	public Schema getSchema(final String schemaId, final long schemaVersion) {
		if(schemaId == null) {
			throw new OmhException("The schema ID is null.");
		}
		
		try {
			return
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForObject(
						"SELECT " +
								Schema.JSON_KEY_ID + ", " +
								Schema.JSON_KEY_VERSION + ", " +
								"`" + Schema.JSON_KEY_SCHEMA + "` " +
							"FROM " +
								Registry.DB_NAME + " " +
							"WHERE " + Schema.JSON_KEY_ID + " = ? " +
							"AND " + Schema.JSON_KEY_VERSION + " = ?",
						new Object[] { schemaId, schemaVersion },
						new RowMapper<Schema>() {
							/**
							 * Maps the row to an {@link Schema} object.
							 */
							@Override
							public Schema mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								String id =
									resultSet.getString(Schema.JSON_KEY_ID);
								long version =
									resultSet.getLong(Schema.JSON_KEY_VERSION);
								JsonNode schema;
								try {
									schema = JSON_MAPPER.
										readTree(
											resultSet
												.getString(
													Schema.JSON_KEY_SCHEMA));
								}
								catch(IOException e) {
									throw
										new SQLException(
											"Error reading the schema.",
											e);
								}
								
								return
									new Schema(
										id,
										version,
										schema,
										OmhValidationController
											.VALIDATION_CONTROLLER);
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
					"Multiple schemas have the same ID-version pair: " +
						schemaId + ", " +
						schemaVersion,
					e);
		}
		// For all other issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for a schema.",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.Registry#getSchemas(java.lang.String, java.lang.Long, long, long)
	 */
	@Override
	public MultiValueResult<? extends Schema> getSchemas(
		final String schemaId, 
		final Long schemaVersion,
		final long numToSkip,
		final long numToReturn) {
		
		// The SELECT portion of the SQL that is querying for the data.
		String querySelect =
			"SELECT " +
				Schema.JSON_KEY_ID + ", " +
				Schema.JSON_KEY_VERSION + ", " +
				"`" + Schema.JSON_KEY_SCHEMA + "` ";
		
		// The FROM portion used by both the data query and the count query.
		String sqlFrom =
			"FROM " +
				Registry.DB_NAME + " ";
		
		// Create the base SQL for the query for data.
		StringBuilder queryBuilder = new StringBuilder(querySelect);
		queryBuilder.append(sqlFrom);
		
		// Create the parameters list for the data query and the count query.
		List<Object> sqlParameters = new LinkedList<Object>();
		
		// Gather the WHERE clauses.
		List<String> whereClauses = new LinkedList<String>();
		
		// Add the schema ID, if given.
		if(schemaId != null) {
			whereClauses.add(Schema.JSON_KEY_ID + " = ?");
			sqlParameters.add(schemaId);
		}
		
		// Add the schema version, if given.
		if(schemaVersion != null) {
			whereClauses.add(Schema.JSON_KEY_VERSION + " = ?");
			sqlParameters.add(schemaVersion);
		}
		
		// Build the WHERE string.
		boolean firstPass = true;
		StringBuilder sqlWhereBuilder = null;
		for(String whereClause : whereClauses) {
			if(firstPass) {
				firstPass = false;
				sqlWhereBuilder = new StringBuilder("WHERE ");
			}
			else {
				sqlWhereBuilder.append("AND ");
			}
			
			sqlWhereBuilder.append(whereClause).append(" ");
		}
		String sqlWhere = null;
		if(sqlWhereBuilder != null) {
			sqlWhere = sqlWhereBuilder.toString();
		}
		
		// Add the WHERE clause to the query for data if it is not null.
		if(sqlWhere != null) {
			queryBuilder.append(sqlWhere);
		}
		
		// Add the ordering and paging.
		queryBuilder
			.append(
				"ORDER BY " +
					Schema.JSON_KEY_ID + ", " +
					Schema.JSON_KEY_VERSION + " " + 
				"LIMIT ?, ?");
		
		// Create the data-query parameter list from the SQL parameters with
		// the additional paging parameters.
		List<Object> queryParameters = new LinkedList<Object>(sqlParameters);
		queryParameters.add(numToSkip);
		queryParameters.add(numToReturn);
		
		// Retrieve the list of results.
		List<Schema> list;
		try {
			list =
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.query(
						queryBuilder.toString(),
						queryParameters.toArray(),
						new RowMapper<Schema>() {
							/**
							 * Maps the row to an {@link Schema} object.
							 */
							@Override
							public Schema mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								String id =
									resultSet.getString(Schema.JSON_KEY_ID);
								long version =
									resultSet.getLong(Schema.JSON_KEY_VERSION);
								JsonNode schema;
								try {
									schema =
										JSON_MAPPER.
											readTree(
												resultSet
													.getString(
														Schema
															.JSON_KEY_SCHEMA));
								}
								catch(IOException e) {
									throw
										new SQLException(
											"Error reading the schema.",
											e);
								}
								
								return
									new Schema(
										id,
										version,
										schema,
										OmhValidationController
											.VALIDATION_CONTROLLER);
							}
						});
		}
		// For all issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for schemas.",
					e);
		}
		
		// Create the SELECT portion of the count query.
		StringBuilder countBuilder =
			new StringBuilder("SELECT COUNT(1) ");
		
		// Add the shared FROM clause.
		countBuilder.append(sqlFrom);
		
		// Add the WHERE clause if it is not null.
		if(sqlWhere != null) {
			countBuilder.append(sqlWhere);
		}
		
		// Retrieve the total count of results.
		int count;
		try {
			count =
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForInt(
						countBuilder.toString(),
						sqlParameters.toArray());
		}
		// For all issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for schemas count.",
					e);
		}
		
		return new SqlMultiValueResult<Schema>(list, count);
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
				Registry.DB_NAME + "(" +
					// Add the database ID.
					SqlDao.KEY_DATABASE_ID + 
						" int unsigned NOT NULL auto_increment, " +
					// Add the schema ID.
					Schema.JSON_KEY_ID + " varchar(36) NOT NULL, " +
					// Add the schema version.
					Schema.JSON_KEY_VERSION + " bigint NOT NULL, " +
					// Add the schema.
					"`" + Schema.JSON_KEY_SCHEMA + "` text NOT NULL, " +
					// Create the primary key.
					"PRIMARY KEY (" + SqlDao.KEY_DATABASE_ID + "), " +
					// Create a unique index on the ID-version pair.
					"UNIQUE INDEX " +
						"`" +
							Registry.DB_NAME + 
								"_unique_index_" +
								Schema.JSON_KEY_ID +
								"_" +
								Schema.JSON_KEY_VERSION +
						"` " +
						"(" +
							Schema.JSON_KEY_ID +
								", " +
								Schema.JSON_KEY_VERSION +
						") " +
				")";
	}
}