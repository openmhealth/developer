/*
 *  Copyright 2012 John Jenkins
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.openmhealth.reference.util;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimeParserBucket;
import org.joda.time.format.ISODateTimeFormat;

/**
 * <p>Factory that creates instances of DateTimeFormatter for the W3C's
 * profile of the ISO 8601 standard.</p>
 * 
 * <p>ISO8601 is the international standard for data interchange and 
 * defines many formats for representing date and time information. W3C has
 * created a shorter, more specific list of formats.</p>
 * 
 * <p>One major difference between this and the ISODateTimeFormatter is
 * that this will always parse non-zone'd values, e.g. year, year-month,
 * and year-month-day, with a default zone of UTC as opposed to the
 * ISODateTimeFormatter which will give them JodaTime's configured default
 * time zone.</p>
 *
 * @author John Jenkins
 */
public class ISOW3CDateTimeFormat {
	/**
	 * <p>A DateTimeParser that implements the W3C profile of the ISO 8601
	 * representation of dates and times
	 * ({@link http://www.w3.org/TR/NOTE-datetime}).</p>
	 * 
	 * <p>The most common use-case of this library is through the
	 * ISOW3CDateTimeFormat, which will create an instance of this parser.
	 * <pre>
	 * ISOW3CDateTimeFormat.dateTime();
	 * </pre>
	 * For date-only formats, the zone is set to UTC. Like all DateTimeParsers,
	 * the zone information is overridden with JodaTime's default zone. If you
	 * are creating this parser yourself and would like to preserve the zone,
	 * be sure to call 'withOffsetParsed()' on the resulting parser.
	 * <pre>
	 * (new ISOW3CDateTimeParser()).withOffsetParsed();
	 * </pre>
	 * </p>
	 *
	 * @author John Jenkins
	 */
	public static class ISOW3CDateTimeParser implements DateTimeParser {
		/**
		 * The date-time formatter for the W3C's specifications for date-only
		 * values.
		 */
		private static final DateTimeParser ISO_W3C_DATE_PARSER;
		static {
			// The W3C defines 3 formats as valid ISO date values.
			DateTimeParser[] parsers = new DateTimeParser[3];
			
			// Just the year.
			parsers[0] = ISOW3CDateTimeFormat.year().getParser();
			
			// The year and month.
			parsers[1] = ISOW3CDateTimeFormat.yearMonth().getParser();
			
			// The year, month, and day.
			parsers[2] = 
				ISOW3CDateTimeFormat.yearMonthDay().getParser();
			
			// Build the parser with the 3 sub-parsers.
			DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
			builder.append(null, parsers);
			ISO_W3C_DATE_PARSER = builder.toParser();
		}
		
		/**
		 * The date-time formatter for the W3C's specifications for date-time 
		 * values.
		 */
		private static final DateTimeParser ISO_W3C_DATE_TIME_PARSER;
		static {
			// The W3C defines 3 formats as valid ISO date-time values.
			DateTimeParser[] parsers = new DateTimeParser[3];
			
			// The year, month, day, hour, minute, and time zone.
			// Build the parser from the existing ISODateTimeParser for the 
			// year, month, day, hour, and minute and add the time zone.
			parsers[0] = ISOW3CDateTimeFormat.dateHourMinuteZone().getParser();
			
			// The year, month, day, hour, minute, second, and time zone.
			parsers[1] = 
				ISOW3CDateTimeFormat.dateHourMinuteSecondZone().getParser();
			
			// The year, month, day, hour, minute, and time zone.
			// Build the parser from the existing ISODateTimeParser for the 
			// year, month, day, hour, and minute and add the time zone.
			parsers[2] = ISOW3CDateTimeFormat.dateTime().getParser();
			
			// Build the parser with the 3 sub-parsers.
			DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
			builder.append(null, parsers);
			ISO_W3C_DATE_TIME_PARSER = builder.toParser();
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.joda.time.format.DateTimeParser#estimateParsedLength()
		 */
		@Override
		public int estimateParsedLength() {
			return 
				Math.max(
					ISO_W3C_DATE_PARSER.estimateParsedLength(),
					ISO_W3C_DATE_TIME_PARSER.estimateParsedLength());
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.joda.time.format.DateTimeParser#parseInto(org.joda.time.format.DateTimeParserBucket, java.lang.String, int)
		 */
		@Override
		public int parseInto(
				final DateTimeParserBucket bucket,
				final String text,
				final int position) {
			
			// Save the current state of the bucket.
			Object bucketState = bucket.saveState();
			
			// Attempt to parse the text with the date-only parser.
			int newPosition =
				ISO_W3C_DATE_PARSER.parseInto(bucket, text, position);
			
			// If the parser returned a negative number, that represents an
			// error and should be propagated.
			if(newPosition < 0) {
				return newPosition;
			}
			// If the new position is the length of the text, then the entire
			// string was parsed. We must set the time zone to UTC for these
			// values.
			else if(newPosition == text.length()) {
				bucket.setZone(DateTimeZone.UTC);
				return newPosition;
			}
			// Otherwise, this parser is inadequate and we must reset the
			// bucket and use the other parser.
			else {
				// FIXME: This shouldn't be necessary, and we should be able to
				// just continue parsing where we left off. If that were the 
				// case, then this Parser could be removed as could the any()
				// function in the ISOW3CDateTimeFormat function, and users
				// could create any combination of the ISOW3CDateTimeFormats.
				bucket.restoreState(bucketState);
				return 
					ISO_W3C_DATE_TIME_PARSER.parseInto(bucket, text, position);
			}
		}
	}
	
	// Lazily instantiate the internal formatters.
	private static DateTimeFormatter
		year,
		yearMonth,
		yearMonthDay,
		yearMonthDayHourMinuteZone,
		yearMonthDayHourMinuteSecondZone,
		yearMonthDayHourMinuteSecondMillisZone,
		any;

	/**
	 * Default constructor. Does nothing.
	 */
	protected ISOW3CDateTimeFormat() {
		super();
	}
	
	/**
	 * Returns a formatter for the four digit year with a time zone of UTC.
	 * 
	 * @return A formatter for the year with a time zone of UTC.
	 */
	public static DateTimeFormatter year() {
		if(year == null) {
			year = ISODateTimeFormat.year().withZoneUTC();
		}
		return year;
	}
	
	/**
	 * Returns a formatter for the four digit year and two digit month of
	 * the year with a time zone of UTC.
	 * 
	 * @return A formatter for the year and month with a time zone of UTC.
	 */
	public static DateTimeFormatter yearMonth() {
		if(yearMonth == null) {
			yearMonth = ISODateTimeFormat.yearMonth().withZoneUTC();
		}
		return yearMonth;
	}
	
	/**
	 * Returns a formatter for the four digit year, two digit month of the
	 * year, and two digit day of the month with a time zone of UTC.
	 * 
	 * @return A formatter for the year, month, and day with a time zone of
	 * 		   UTC.
	 */
	public static DateTimeFormatter yearMonthDay() {
		if(yearMonthDay == null) {
			yearMonthDay = ISODateTimeFormat.yearMonthDay().withZoneUTC();
		}
		return yearMonthDay;
	}
	
	/**
	 * Returns a formatter for the four digit year, two digit month of the
	 * year, and two digit day of the month with a time zone of UTC.
	 * 
	 * @return A formatter for the year, month, and day with a time zone of
	 * 		   UTC.
	 */
	public static DateTimeFormatter date() {
		return yearMonthDay();
	}
	
	/**
	 * Returns a formatter that combines a full date, two digit hour of the
	 * day, two digit minute of the hour and a time zone.
	 * 
	 * @return A formatter for the date, hour, minute, and time zone.
	 */
	public static DateTimeFormatter dateHourMinuteZone() {
		if(yearMonthDayHourMinuteZone == null) {
			DateTimeFormatterBuilder dateHourMinuteTimezone =
				new DateTimeFormatterBuilder();
			dateHourMinuteTimezone
				.append(ISODateTimeFormat.dateHourMinute());
			dateHourMinuteTimezone.append(DateTimeFormat.forPattern("ZZ"));
			yearMonthDayHourMinuteZone =
				dateHourMinuteTimezone.toFormatter().withOffsetParsed();
		}
		return yearMonthDayHourMinuteZone;
	}
	
	/**
	 * Returns a formatter that combines a full date, two digit hour of the
	 * day, two digit minute of the hour, two digit second of the minute
	 * and a time zone.
	 * 
	 * @return A formatter for the date, hour, minute, second, and time
	 * 		   zone.
	 */
	public static DateTimeFormatter dateHourMinuteSecondZone() {
		if(yearMonthDayHourMinuteSecondZone == null) {
			yearMonthDayHourMinuteSecondZone =
				ISODateTimeFormat.dateTimeNoMillis().withOffsetParsed();
		}
		return yearMonthDayHourMinuteSecondZone;
	}
	
	/**
	 * Returns a formatter that combines a full date, two digit hour of the
	 * day, two digit minute of the hour, two digit second of the minute
	 * and a time zone.
	 * 
	 * @return A formatter for the date, hour, minute, second, and time
	 * 		   zone.
	 */
	public static DateTimeFormatter dateTimeNoMillis() {
		return dateHourMinuteSecondZone();
	}
	
	/**
	 * Returns a formatter that combines a full date, two digit hour of the
	 * day, two digit minute of the hour, two digit second of the minute,
	 * three digit milliseconds of the second and a time zone.
	 * 
	 * @return A formatter for the date, hour, minute, second, millisecond,
	 * 		   and time zone.
	 */
	public static DateTimeFormatter dateHourMinuteSecondMillisZone() {
		if(yearMonthDayHourMinuteSecondMillisZone == null) {
			yearMonthDayHourMinuteSecondMillisZone =
				ISODateTimeFormat.dateTime().withOffsetParsed();
		}
		return yearMonthDayHourMinuteSecondMillisZone;
	}
	
	/**
	 * Returns a formatter that combines a full date, two digit hour of the
	 * day, two digit minute of the hour, two digit second of the minute,
	 * three digit milliseconds of the second and a time zone.
	 * 
	 * @return A formatter for the date, hour, minute, second, millisecond,
	 * 		   and time zone.
	 */
	public static DateTimeFormatter dateTime() {
		return dateHourMinuteSecondMillisZone();
	}
	
	/**
	 * Returns a formatter that combines all ISOW3CDateTimeFormats. This
	 * formatter will correctly parse any of the other formatters and will only
	 * fail if the value doesn't match any of them.
	 * 
	 * @return A universal DateTimeFormatter for all ISO W3C date-time formats.
	 * 
	 * @see #year()
	 * @see #yearMonth()
	 * @see #yearMonthDay()
	 * @see #dateHourMinuteZone()
	 * @see #dateHourMinuteSecondZone()
	 * @see #dateHourMinuteSecondMillisZone()
	 */
	public static DateTimeFormatter any() {
		if(any == null) {
			any =
				new DateTimeFormatter(
						ISODateTimeFormat.dateTime().getPrinter(),
						new ISOW3CDateTimeParser())
					.withOffsetParsed();
		}
		return any;
	}
}
