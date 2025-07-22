package com.ey.advisory.einv.app.api;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

import com.ey.advisory.common.EWBException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NICDateUtil {
	
	private static final FastDateFormat TWELVE_HOUR_FORMAT = FastDateFormat
			.getInstance("dd/MM/yyyy hh:mm:ss a");

	private static final FastDateFormat TWELVE_HOUR_FORMAT_WITH_DOTS = 
				FastDateFormat.getInstance("dd/MM/yyyy hh.mm.ss a");

	
	// We are adding an 'a' at the end, because NIC sometimes returns
	// a 24 hour format with the AM/PM included.
	private static final FastDateFormat TWENTYFOUR_HOUR_FORMAT = FastDateFormat
			.getInstance("dd/MM/yyyy HH:mm:ss");

	private static final FastDateFormat TWENTYFOUR_HOUR_FORMAT_WITH_DOTS = 
			FastDateFormat.getInstance("dd/MM/yyyy HH.mm.ss");
	
	private static final FastDateFormat DATE_ONLY_FORMAT = FastDateFormat
			.getInstance("dd/MM/yyyy");
	
	
	
	//25/01/2019 10.11.00 AM
	
	
	private static FastDateFormat getDateFormat(
				boolean is24HrFmt, String timeSepChar) {
		// If 24 hour format is required, then get one of the 24 hour formats
		// available based on the time separator character specified.
		if (is24HrFmt) {
			return ".".equals(timeSepChar) ? TWENTYFOUR_HOUR_FORMAT_WITH_DOTS : 
					TWENTYFOUR_HOUR_FORMAT;
		}
		
		// Similar to the above logic, return the appropriate 12 hr format.
		return ".".equals(timeSepChar) ? TWELVE_HOUR_FORMAT_WITH_DOTS :
				TWELVE_HOUR_FORMAT;
	}

	/**
	 * Currently, NIC keeps changing the format of the date that they return
	 * which is causing our code to fail or process incorrect results.
	 * 
	 * @return
	 */
	public static Date parseNICDate(String dateString) {

		// Null inputs will be skipped.
		if (dateString == null)
			return null;

		// Error message to be returned on any Invalid date format
		String msg = "Invalid Date Format returned by NIC. " + "NIC Date = '"
				+ dateString + "'";

		String dateStr = dateString.trim();
		int len = dateStr.length();
		Date retDate = null;
		// Currently we are expecting only 2 formats to be returned by NIC
		// If any other date formats are encountered, throw the exception
		// right at this point.
		if (len != 10 && (len < 19 || len > 22)) {
			LOGGER.error(msg);
			throw new EWBException(msg);
		}

		// if the trimmed length of the date string obtained is 10, then
		// the date should be in dd/MM/yyyy format. Try parsing the date
		// using this format. If an exception occurs, then throw an error
		// at this point. 
		if (len == 10) {
			try {
				retDate = DATE_ONLY_FORMAT.parse(dateStr);
			} catch (ParseException ex) {
				LOGGER.error(msg);
				throw new EWBException(msg);
			}
			return retDate;
		}

		//25/01/2019 10.11.00 AM
		if(len == 21 && !dateStr.startsWith("0"))
			dateStr = "0" + dateStr;
		
		// Get the first 2 characters of the date string. Skip the Date part.
		// The time part starts at character 12
		// (hence start index = 12 -1 = 11).
		String firstTwoChars = dateStr.substring(11, 13);
		// Get the last 2 characters of the date string.
		String lastTwoChars = dateStr.substring(len - 2, len).toUpperCase();

		int hours = 0;

		try {
			hours = Integer.parseInt(firstTwoChars);
		} catch (NumberFormatException ex) {
			LOGGER.error(msg);
			throw new EWBException(msg);
		}

		// If the hours value does not fall in the valid range, then
		// throw an exception.
		if (hours < 0 || hours > 23) {
			LOGGER.error(msg);
			throw new EWBException(msg);
		}

		boolean isAmPmPresent = lastTwoChars.equals("AM")
				|| lastTwoChars.equals("PM");
		boolean isHoursLT13 = hours < 13; // is the hours value < 12.
		
		// Extract the date string (because the STUPID NIC API returns either
		// a dot or a colon). Let's hope they'll stop changing date formats!!!
		String timeSepChar = dateStr.substring(13, 14);
		
		try {
			// If it is in 24 hr format, parse it using the 24 hour format
			// parser. We don't care if AM/PM is attached to the string. This
			// format will take care of both the scenarios.
			if (!isHoursLT13) {
				retDate = getDateFormat(true, timeSepChar).parse(dateStr);
				return retDate;
			}

			if (isAmPmPresent && isHoursLT13) {
				retDate = getDateFormat(false, timeSepChar).parse(dateStr);
				return retDate;
			}

			// If the hours value is < 13, but no AM/PM is present.
			// Use the 24 hour parser.
			if (!isAmPmPresent && isHoursLT13) {
				retDate = getDateFormat(true, timeSepChar).parse(dateStr);
				return retDate;
			}
			
			
			// Throw exception for any other scenario.
			msg = "Illegal State. Unrecognized Date Format encountered.";
			LOGGER.error(msg);
			throw new IllegalStateException(msg);

		} catch (ParseException ex) {
			LOGGER.error(msg);
			throw new EWBException(msg);
		}

	}



}
