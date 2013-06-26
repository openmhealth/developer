package org.openmhealth.reference.data.sql;

/**
 * <p>
 * The interface for all SQL DAO objects.
 * </p>
 *
 * @author John Jenkins
 */
public interface SqlDaoInterface {
	/**
	 * <p>
	 * Returns the SQL that will create the table for this DAO object.
	 * </p>
	 * 
	 * <p>
	 * The SQL generated here may only conform to MySQL. It *should* be more
	 * abstract to work with any SQL system for some SQL standard; however,
	 * that is not the case at this time.
	 * </p>
	 * 
	 * @return The SQL that will create the table for this DAO object.
	 */
	public String getSqlTableDefinition();
}