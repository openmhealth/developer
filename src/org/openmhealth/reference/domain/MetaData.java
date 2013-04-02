package org.openmhealth.reference.domain;

import org.joda.time.DateTime;
import org.openmhealth.reference.exception.OmhException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the meta-data for a data stream. All fields are 
 * optional. This class is immutable and, therefore, thread-safe.
 *
 * @author John Jenkins
 */
public class MetaData implements OmhObject {
	/**
	 * The version of this classed used for serialization. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The JSON key for the ID.
	 */
	public static final String JSON_KEY_ID = "id";
	/**
	 * The JSON key for the timestamp.
	 */
	public static final String JSON_KEY_TIMESTAMP = "timestamp";
	
	/**
	 * This class is responsible for building new MetaData objects. This
	 * class is mutable and, therefore, not thread-safe.
	 *
	 * @author John Jenkins
	 */
	public static class Builder {
		private String id = null;
		private DateTime timestamp = null;
		
		/**
		 * Creates an empty builder.
		 */
		public Builder() {};
		
		/**
		 * Returns true if an ID has been set; false, otherwise.
		 * 
		 * @return True if an ID has been set; false, otherwise.
		 */
		public boolean hasId() {
			return id != null;
		}
		
		/**
		 * Sets the ID.
		 * 
		 * @param id The ID.
		 */
		public void setId(final String id) {
			this.id = id;
		}
		
		/**
		 * Returns true if a time stamp has been set; false, otherwise.
		 * 
		 * @return True if a time stamp has been set; false, otherwise.
		 */
		public boolean hasTimestamp() {
			return timestamp != null;
		}
		
		/**
		 * Sets the timestamp.
		 * 
		 * @param timetamp The timestamp.
		 */
		public void setTimestamp(final DateTime timestamp) {
			this.timestamp = timestamp;
		}
		
		/**
		 * Builds the MetaData object.
		 * 
		 * @return The MetaData object.
		 */
		public MetaData build() throws OmhException {
			return new MetaData(id, timestamp);
		}
	}
	
	/**
	 * The unique ID for the point.
	 */
	@JsonProperty(JSON_KEY_ID)
	@JsonInclude(Include.NON_NULL)
	private final String id;
	/**
	 * The timestamp for the point.
	 */
	@JsonProperty(JSON_KEY_TIMESTAMP)
	@JsonInclude(Include.NON_NULL)
	private final DateTime timestamp;
	
	/**
	 * Creates a new MetaData object.
	 * 
	 * @param timestamp The time stamp for this meta-data.
	 * 
	 * @param location The location for this meta-data.
	 */
	@JsonCreator
	public MetaData(
		@JsonProperty(JSON_KEY_ID)
		final String id,
		@JsonProperty(JSON_KEY_TIMESTAMP)
		final DateTime timestamp)
		throws OmhException {
		
		// Timestamps cannot be in the future.
		if((timestamp != null) && timestamp.isAfterNow()) {
			throw
				new OmhException(
					"The timestamp cannot be in the future.");
		}
		
		this.id = id;
		this.timestamp = timestamp;
	}
	
	/**
	 * Returns the ID.
	 * 
	 * @return The ID.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns timestamp.
	 *
	 * @return The timestamp.
	 */
	public DateTime getTimestamp() {
		return timestamp;
	}
}
