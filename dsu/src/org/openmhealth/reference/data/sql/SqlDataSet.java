package org.openmhealth.reference.data.sql;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openmhealth.reference.data.DataSet;
import org.openmhealth.reference.data.Registry;
import org.openmhealth.reference.data.UserBin;
import org.openmhealth.reference.domain.ColumnList;
import org.openmhealth.reference.domain.Data;
import org.openmhealth.reference.domain.MetaData;
import org.openmhealth.reference.domain.MultiValueResult;
import org.openmhealth.reference.domain.Schema;
import org.openmhealth.reference.domain.User;
import org.openmhealth.reference.domain.sql.SqlMultiValueResult;
import org.openmhealth.reference.exception.OmhException;
import org.openmhealth.reference.util.ISOW3CDateTimeFormat;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * The SQL implementation of the interface to the database-backed collection of
 * {@link Data}.
 * </p>
 *
 * @author John Jenkins
 */
public class SqlDataSet extends DataSet implements SqlDaoInterface {
	/**
	 * A standard mapping factory for converting POJOs to JSON and visa versa.
	 */
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	
	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.DataSet#setData(java.util.List)
	 */
	@Override
	public void storeData(final List<Data> data) {
		// Validate the parameter.
		if(data == null) {
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
		transactionDefinition.setName("Adding a data point.");
		
		// Create the new transaction.
		TransactionStatus transactionStatus =
			transactionManager.getTransaction(transactionDefinition);
		
		// Get the JDBC template.
		JdbcTemplate jdbcTemplate = dao.getJdbcTemplate();
		
		// Create the list of points to be inserted into the database.
		List<Object[]> points = new ArrayList<Object[]>(data.size());
		
		// Add each of the points to the array.
		try {
			for(Data point : data) {
				Object[] pointData = new Object[6];
				pointData[0] = point.getOwner();
				pointData[1] = point.getSchemaId();
				pointData[2] = point.getSchemaVersion();
				
				MetaData metaData = point.getMetaData();
				if(metaData == null) {
					pointData[3] = null;
					pointData[4] = null;
				}
				else {
					pointData[3] = metaData.getId();
					pointData[4] =
						ISOW3CDateTimeFormat
							.any()
							.print(metaData.getTimestamp());
				}
				
				pointData[5] = JSON_MAPPER.writeValueAsString(point.getData());
				
				points.add(pointData);
			}
		}
		catch(JsonProcessingException e) {
			throw new OmhException("Could not convert some data to JSON.", e);
		}
		
		// Add the data.
		try {
			jdbcTemplate
				.batchUpdate(
					"INSERT INTO " + DataSet.DB_NAME + " (" +
							UserBin.DB_NAME + "_id" + ", " +
							Registry.DB_NAME + "_id" + ", " +
							Data.JSON_KEY_METADATA + "_" + 
								MetaData.JSON_KEY_ID + ", " +
							Data.JSON_KEY_METADATA + "_" +
								MetaData.JSON_KEY_TIMESTAMP + ", " +
							Data.JSON_KEY_DATA + " " +
						") VALUES (" +
							"(" +
								"SELECT " + SqlDao.KEY_DATABASE_ID + " " +
								"FROM " + UserBin.DB_NAME + " " +
								"WHERE " + User.JSON_KEY_USERNAME + " = ?" +
							"), " +
							"(" +
								"SELECT " + SqlDao.KEY_DATABASE_ID + " " +
								"FROM " + Registry.DB_NAME + " " +
								"WHERE " + Schema.JSON_KEY_ID + " = ? " +
								"AND " + Schema.JSON_KEY_VERSION + " = ?" +
							"), " +
							"?, " +
							"?, " +
							"?" +
						")",
					points);
			
			// Commit the transaction.
			transactionManager.commit(transactionStatus);
		}
		catch(DataAccessException e) {
			transactionManager.rollback(transactionStatus);
			throw new OmhException("There was a problem storing the data.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.openmhealth.reference.data.DataSet#getData(java.lang.String, java.lang.String, long, org.openmhealth.reference.domain.ColumnList, java.lang.Long, java.lang.Long)
	 */
	@Override
	public MultiValueResult<Data> getData(
		final String owner,
		final String schemaId,
		final long version,
		final ColumnList columnList,
		final long numToSkip,
		final long numToReturn) {
		
		// Validate the parameters.
		if(owner == null) {
			throw new OmhException("The data is null.");
		}
		else if(schemaId == null) {
			throw new OmhException("The schema ID is null.");
		}
		
		// Retrieve the list of results.
		List<Data> list;
		try {
			list =
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.query(
						// Get the required columns to build the object.
						"SELECT " +
								User.JSON_KEY_USERNAME + ", " +
								Schema.JSON_KEY_ID + ", " +
								Schema.JSON_KEY_VERSION + ", " +
								Data.JSON_KEY_METADATA + "_" +
									MetaData.JSON_KEY_ID + ", " +
								Data.JSON_KEY_METADATA + "_" +
									MetaData.JSON_KEY_TIMESTAMP + ", " +
								Data.JSON_KEY_DATA + " " +
							// Include all of the required tables.
							"FROM " +
								UserBin.DB_NAME + ", " +
								Registry.DB_NAME + ", " +
								DataSet.DB_NAME + " " +
							// Link the user table to the data table.
							"WHERE " +
									UserBin.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									DataSet.DB_NAME + 
									"." +
									UserBin.DB_NAME + "_id " +
							// Limit the results based on the required
							// username.
							"AND " + User.JSON_KEY_USERNAME + " = ? " +
							// Link the registry table to the data table.
							"AND " +
									Registry.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									DataSet.DB_NAME + 
									"." +
									Registry.DB_NAME + "_id " +
							// Limit the results based on the required schema
							// ID and version.
							"AND " + Schema.JSON_KEY_ID + " = ? " +
							"AND " + Schema.JSON_KEY_VERSION + " = ? " +
							"LIMIT ?, ?",
						new Object[] {
							owner,
							schemaId,
							version,
							numToSkip,
							numToReturn },
						new RowMapper<Data>() {
							/**
							 * Maps the row to a {@link Data} object.
							 */
							@Override
							public Data mapRow(
								final ResultSet resultSet,
								final int rowNum)
								throws SQLException {
								
								// Get the username.
								String username =
									resultSet
										.getString(User.JSON_KEY_USERNAME);
								// Get the schema's ID.
								String id =
									resultSet.getString(Schema.JSON_KEY_ID);
								// Get the stream's version.
								long version =
									resultSet.getLong(Schema.JSON_KEY_VERSION);
								
								// Build the meta-data.
								MetaData.Builder metaDataBuilder =
									new MetaData.Builder();
								// Get and set the ID, even if it is null.
								metaDataBuilder
									.setId(
										resultSet
											.getString(
												Data.JSON_KEY_METADATA +
													"_" +
													MetaData.JSON_KEY_ID));
								// Get the timestamp.
								String metaDataTimestampString =
									resultSet
										.getString(
											Data.JSON_KEY_METADATA +
												"_" +
												MetaData.JSON_KEY_TIMESTAMP);
								// If the timestamp is not null, decode it and
								// set it.
								if(metaDataTimestampString != null) {
									metaDataBuilder
										.setTimestamp(
											ISOW3CDateTimeFormat
												.any()
												.parseDateTime(
													metaDataTimestampString));
								}
								// If the builder has no non-null members,
								// create a MetaData object; otherwise, just
								// leave it as null.
								MetaData metaData =
									((metaDataBuilder.isNull()) ?
										null :
										metaDataBuilder.build());
								
								// Get the data.
								JsonNode data;
								try {
									data =
										JSON_MAPPER
											.readTree(
												resultSet
													.getString(
														Data.JSON_KEY_DATA));
								}
								catch(IOException e) {
									throw
										new SQLException(
											"Error decoding the data.",
											e);
								}
								// FIXME: Apply the column list.
								
								// Create a Data object and return it.
								return
									new Data(
										username,
										id,
										version,
										metaData,
										data);
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
		
		// Retrieve the total count of results.
		int count;
		try {
			count =
				SqlDao
					.getInstance()
					.getJdbcTemplate()
					.queryForInt(
						"SELECT COUNT(1) " +
							// Include all of the required tables.
							"FROM " +
								UserBin.DB_NAME + ", " +
								Registry.DB_NAME + ", " +
								DataSet.DB_NAME + " " +
							// Link the user table to the data table.
							"WHERE " +
									UserBin.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									DataSet.DB_NAME + 
									"." +
									UserBin.DB_NAME + "_id " +
							// Limit the results based on the required
							// username.
							"AND " + User.JSON_KEY_USERNAME + " = ? " +
							// Link the registry table to the data table.
							"AND " +
									Registry.DB_NAME + 
									"." +
									SqlDao.KEY_DATABASE_ID +
								" = " +
									DataSet.DB_NAME + 
									"." +
									Registry.DB_NAME + "_id " +
							// Limit the results based on the required schema
							// ID and version.
							"AND " + Schema.JSON_KEY_ID + " = ? " +
							"AND " + Schema.JSON_KEY_VERSION + " = ? ",
						new Object[] { owner, schemaId, version });
		}
		// For all issues, we simply propagate the exception.
		catch(DataAccessException e) {
			throw
				new OmhException(
					"There was an error querying for schemas count.",
					e);
		}
		
		return new SqlMultiValueResult<Data>(list, count);
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
				DataSet.DB_NAME + "(" +
					// Add the database ID.
					SqlDao.KEY_DATABASE_ID + 
						" int unsigned NOT NULL auto_increment, " +
					// Add the reference to the user table.
					UserBin.DB_NAME + "_id int unsigned NOT NULL, " +
					// Add the reference to the registry table.
					Registry.DB_NAME + "_id int unsigned NOT NULL, " +
					// Add the meta-data's ID field.
					Data.JSON_KEY_METADATA + "_" +
						MetaData.JSON_KEY_ID + " varchar(36), " +
					// Add the meta-data's timestamp field.
					Data.JSON_KEY_METADATA + "_" +
						MetaData.JSON_KEY_TIMESTAMP + " varchar(255), " +
					// Add the data field.
					Data.JSON_KEY_DATA + " text NOT NULL, " +
					// Create the primary key.
					"PRIMARY KEY (" + SqlDao.KEY_DATABASE_ID + "), " +
					// Create an index on the ID.
					"INDEX " +
						"`" +
							DataSet.DB_NAME + 
								"_index_" +
								Data.JSON_KEY_METADATA +
								"_" +
								MetaData.JSON_KEY_ID +
						"` " +
						"(" +
							Data.JSON_KEY_METADATA +
							"_" +
							MetaData.JSON_KEY_ID +
						"), " +
					// Create an index on the timestamp.
					"INDEX " +
						"`" +
							DataSet.DB_NAME + 
								"_index_" +
								Data.JSON_KEY_METADATA +
								"_" +
								MetaData.JSON_KEY_TIMESTAMP +
						"` " +
						"(" +
							Data.JSON_KEY_METADATA +
							"_" +
							MetaData.JSON_KEY_TIMESTAMP +
						"), " +
					// Link to the user table.
					"CONSTRAINT " +
						"`" +
							DataSet.DB_NAME + 
								"_fk_" +
								UserBin.DB_NAME + "_id" +
						"` " +
						"FOREIGN KEY " +
						"`" +
							DataSet.DB_NAME + 
								"_index_" +
								UserBin.DB_NAME + "_id" +
						"` " +
						"(" + UserBin.DB_NAME + "_id) " +
						"REFERENCES " + 
							UserBin.DB_NAME + " " + 
								"(" + SqlDao.KEY_DATABASE_ID + ") " +
								"ON UPDATE CASCADE " +
								"ON DELETE CASCADE, " +
					// Link to the registry.
					"CONSTRAINT " +
						"`" +
							DataSet.DB_NAME + 
								"_fk_" +
								Registry.DB_NAME + "_id" +
						"` " +
						"FOREIGN KEY " +
						"`" +
							DataSet.DB_NAME + 
								"_index_" +
								Registry.DB_NAME + "_id" +
						"` " +
						"(" + Registry.DB_NAME + "_id) " +
						"REFERENCES " + 
							Registry.DB_NAME + " " + 
								"(" + SqlDao.KEY_DATABASE_ID + ") " +
								"ON UPDATE CASCADE " +
								"ON DELETE CASCADE" +
				")";
	}
}