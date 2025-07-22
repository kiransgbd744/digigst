package com.ey.advisory.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EYDateUtil {

	public static final ZoneId ZONE_ID_UTC = ZoneOffset.UTC;
	public static final ZoneId ZONE_ID_GERMANY = ZoneId.of("Europe/Berlin");
	public static final ZoneId ZONE_ID_INDIA = ZoneId.of("Asia/Kolkata");

	private EYDateUtil() {
	}

	/**
	 * Return a LocalDate object corresponding to the Instant represented by the
	 * specified java.util.Date. This method returns the LocalDate for this
	 * Instant, for the specified time zone.
	 *
	 * @param date
	 *            the java.util.Date to be converted.
	 *
	 * @return the LocalDate instance as per the specified time zone, for the
	 *         specified java.util.Date
	 */
	public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
		if (date == null)
			return null;
		return LocalDateTime.ofInstant(date.toInstant(), zoneId).toLocalDate();
	}

	/**
	 * Return a LocalDate object corresponding to the Instant represented by the
	 * specified java.util.Date. This method returns the LocalDate for this
	 * Instant, for the system time zone.
	 *
	 * @param date
	 *            the java.util.Date to be converted.
	 *
	 * @return the LocalDate instance as per the system time zone, for the
	 *         specified java.util.Date
	 */
	public static LocalDate toLocalDate(Date date) {
		if (date == null)
			return null;
		return toLocalDate(date, ZoneId.systemDefault());
	}

	/**
	 * Return a LocalDateTime object corresponding to the Instant represented by
	 * the specified java.util.Date. This method returns the LocalDateTime for
	 * this Instant, for the specified time zone.
	 *
	 * @param date
	 *            the java.util.Date to be converted.
	 *
	 * @return the LocalDateTime instance as per the specified time zone, for
	 *         the specified java.util.Date
	 */
	public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
		if (date == null)
			return null;
		return LocalDateTime.ofInstant(date.toInstant(), zoneId);
	}

	/**
	 * Return a LocalDateTime object corresponding to the Instant represented by
	 * the specified java.util.Date. This method returns the LocalDateTime for
	 * this Instant, for the system time zone.
	 *
	 * @param date
	 *            the java.util.Date to be converted.
	 *
	 * @return the LocalDate instance as per the system time zone, for the
	 *         specified java.util.Date
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		if (date == null)
			return null;
		return toLocalDateTime(date, ZoneId.systemDefault());
	}

	/**
	 * Return a ZonedDateTime object corresponding to the Instant represented by
	 * the specified java.util.Date. This method returns the ZonedDateTime for
	 * this Instant, for the specified time zone.
	 *
	 * @param date
	 *            the java.util.Date to be converted.
	 *
	 * @return the ZonedDateTime instance as per the specified time zone, for
	 *         the specified java.util.Date
	 */
	public static ZonedDateTime toZonedDateTime(Date date, ZoneId zoneId) {
		if (date == null)
			return null;
		return ZonedDateTime.ofInstant(date.toInstant(), zoneId);
	}

	/**
	 * Return a ZonedDateTime object corresponding to the Instant represented by
	 * the specified java.util.Date. This method returns the ZonedDateTime for
	 * this Instant, for the system time zone.
	 *
	 * @param date
	 *            the java.util.Date to be converted.
	 *
	 * @return the ZonedDateTime instance as per the system time zone, for the
	 *         specified java.util.Date
	 */
	public static ZonedDateTime toZonedDateTime(Date date) {
		if (date == null)
			return null;
		return toZonedDateTime(date, ZoneId.systemDefault());
	}

	/**
	 * Return a ZonedDateTime object corresponding to the Instant represented by
	 * the specified java.util.Calendar. This method returns the ZonedDateTime
	 * for this Instant, for the time zone represented by the Calendar instance.
	 *
	 * @param cal
	 *            the java.util.Calendar to be converted.
	 *
	 * @return the ZonedDateTime instance as per the date time and the time zone
	 *         of the Calendar instance
	 */
	public static ZonedDateTime toZonedDateTime(Calendar cal) {
		if (cal == null)
			return null;
		if (cal instanceof GregorianCalendar) {
			return ((GregorianCalendar) cal).toZonedDateTime();
		}

		return ZonedDateTime.ofInstant(cal.toInstant(),
				cal.getTimeZone().toZoneId());
	}

	/**
	 * Return the java.util.Date object at the start of the day (with all time
	 * components set to zero), for the specified local date at the specified
	 * time zone.
	 *
	 * @param ld
	 *            the LocalDate object to be converted.
	 * @param zoneId
	 *            the time zone to which the LocalDate belongs.
	 * @return the java.util.Date instance with all the time components set to
	 *         zero. This date represents the start of the day, for the
	 *         specified LocalDate at the specified time zone.
	 */
	public static Date toDate(LocalDate ld, ZoneId zoneId) {
		if (ld == null)
			return null;
		return Date.from(ld.atStartOfDay(zoneId).toInstant());
	}

	/**
	 * Return the java.util.Date object at the start of the day (with all time
	 * components set to zero), for the specified local date at the system time
	 * zone.
	 *
	 * @param ld
	 *            the LocalDate object to be converted.
	 * @return the java.util.Date instance with all the time components set to
	 *         zero. This date represents the start of the day, for the
	 *         specified LocalDate at the system time zone.
	 */
	public static Date toDate(LocalDate ld) {
		if (ld == null)
			return null;
		return toDate(ld, ZoneId.systemDefault());
	}

	/**
	 * Return the most accurate representation of the java.util.Date object for
	 * the specified LocalDateTime at the specified time zone. The specified
	 * LocalDateTime may be adjusted internally depending on whether the
	 * LocalDateTime and ZoneId combination results in a Gap or an Overlap (as
	 * specified in the ZonedDateTime documentation of the java doc).
	 *
	 * @param ldt
	 *            the LocalDateTime object to be converted.
	 * @param zoneId
	 *            the time zone to which the LocalDateTime belongs.
	 * @return the java.util.Date instance for the specified LocalDateTime at
	 *         the specified time zone.
	 */
	public static Date toDate(LocalDateTime ldt, ZoneId zoneId) {
		if (ldt == null)
			return null;
		return Date.from(ZonedDateTime.of(ldt, zoneId).toInstant());
	}

	/**
	 * Return the most accurate representation of the java.util.Date object for
	 * the specified LocalDateTime at the system time zone. The specified
	 * LocalDateTime may be adjusted internally depending on whether the
	 * LocalDateTime and System Time Zone combination results in a Gap or an
	 * Overlap (as specified in the ZonedDateTime documentation of the java
	 * doc).
	 *
	 * @param ldt
	 *            the LocalDateTime object to be converted.
	 * @return the java.util.Date instance for the specified LocalDateTime at
	 *         the system time zone.
	 */
	public static Date toDate(LocalDateTime ldt) {
		if (ldt == null)
			return null;
		return toDate(ldt, ZoneId.systemDefault());
	}

	/**
	 * Return the most accurate representation of the java.util.Date object for
	 * the specified ZonedDateTime object.
	 *
	 * @param zdt
	 *            the ZonedDateTime object to be converted.
	 * @return the java.util.Date instance for the specified ZonedDateTime.
	 *
	 */
	public static Date toDate(ZonedDateTime zdt) {
		if (zdt == null)
			return null;
		return Date.from(zdt.toInstant());
	}

	/**
	 * Return a java.util.Calendar instance for the start of the day specified
	 * by the LocalDate, for the specified time zone. The returned Calendar
	 * instance will have all time components set to 0.
	 *
	 * @param ld
	 *            the LocalDate object to the converted.
	 * @param zoneId
	 *            the time zone that represents the LocalDate
	 * @return the Calendar object that represents the start of the day for the
	 *         specified Local Date at the specified time zone.
	 */
	public static Calendar toCalendar(LocalDate ld, ZoneId zoneId) {
		if (ld == null)
			return null;
		return GregorianCalendar.from(ld.atStartOfDay(zoneId));
	}

	/**
	 * Return a java.util.Calendar instance for the start of the day specified
	 * by the LocalDate, for the system time zone. The returned Calendar
	 * instance will have all time components set to 0.
	 *
	 * @param ld
	 *            the LocalDate object to the converted.
	 * @return the Calendar object that represents the start of the day for the
	 *         specified Local Date at the system time zone.
	 */
	public static Calendar toCalendar(LocalDate ld) {
		if (ld == null)
			return null;
		return toCalendar(ld, ZoneId.systemDefault());
	}

	/**
	 * Return a java.util.Calendar instance for the specified LocalDateTime, for
	 * the specified time zone.
	 *
	 * @param ldt
	 *            the LocalDateTime object to the converted.
	 * @param zoneId
	 *            the time zone that represents the LocalDateTime
	 * @return the Calendar object that represents specified LocalDateTime at
	 *         the specified time zone.
	 */
	public static Calendar toCalendar(LocalDateTime ldt, ZoneId zoneId) {
		return GregorianCalendar.from(ZonedDateTime.of(ldt, zoneId));
	}

	/**
	 * Return a java.util.Calendar instance for the specified LocalDateTime, for
	 * the system time zone.
	 *
	 * @param ldt
	 *            the LocalDateTime object to the converted.
	 * @return the Calendar object that represents specified LocalDateTime at
	 *         the system time zone.
	 */
	public static Calendar toCalendar(LocalDateTime ldt) {
		if (ldt == null)
			return null;
		return toCalendar(ldt, ZoneId.systemDefault());
	}

	/**
	 * Return a java.util.Calendar instance for the specified ZonedDateTime.
	 * This is a direct one to one mapping.
	 *
	 * @param zdt
	 *            the specified ZonedDateTime.
	 * @return the calendar object that represents the specified ZonedDateTime.
	 */
	public static Calendar toCalendar(ZonedDateTime zdt) {
		if (zdt == null)
			return null;
		return GregorianCalendar.from(zdt);
	}

	public static String fmt(LocalDateTime ldt) {
		if (ldt == null)
			return null;
		DateTimeFormatter f = DateTimeFormatter
				.ofPattern("dd/MMM/yyyy hh:mm:ss a");
		return f.format(ldt);
	}

	public static String fmtLocalDate(LocalDate ldt) {

		LocalDate dateTimeFormatter = EYDateUtil.toISTDateTimeFromUTC(ldt);
		DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return FOMATTER.format(dateTimeFormatter);
	}

	public static String fmtDate(LocalDateTime ldt) {
		if (ldt == null)
			return null;
		DateTimeFormatter f = DateTimeFormatter
				.ofPattern("dd-MM-yyyy : HH:mm:ss");
		return f.format(ldt);
	}

	public static String fmtDate2(LocalDateTime ldt) {
		if (ldt == null)
			return null;
		DateTimeFormatter f = DateTimeFormatter
				.ofPattern("yyyy-MM-dd : HH:mm:ss");
		return f.format(ldt);
	}

	public static String fmtDate3(LocalDateTime ldt) {
		if (ldt == null)
			return null;
		DateTimeFormatter f = DateTimeFormatter
				.ofPattern("dd-MM-yyyy HH:mm:ss");
		return f.format(ldt);
	}

	public static String fmtDate4(LocalDateTime ldt) {
		if (ldt == null)
			return null;
		DateTimeFormatter f = DateTimeFormatter
				.ofPattern("dd/MM/yyyy HH:mm:ss");
		return f.format(ldt);
	}
	public static String fmtDateOnly(LocalDateTime ldt) {
		if (ldt == null)
			return null;
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return f.format(ldt);
	}
	

	public static String fmtDateOnly(LocalDate ldt, DateTimeFormatter fmt) {
		if (ldt == null)
			return null;
		return fmt.format(ldt);
	}

	public static String fmtDateOnly(String inpDate, DateTimeFormatter inpFmt,
			DateTimeFormatter outFmt) {
		try {
			if (inpDate == null)
				return null;

			LocalDate date = LocalDate.parse(inpDate, inpFmt);

			String formattedDate = date.format(outFmt);
			return formattedDate;
		} catch (Exception e) {
			return inpDate;
		}
	}

	public static String fmtDateOnlyIst(String inpDate,
			DateTimeFormatter inpFmt, DateTimeFormatter outFmt) {
		if (inpDate == null)
			return null;

		LocalDateTime date = EYDateUtil
				.toISTDateTimeFromUTC(LocalDateTime.parse(inpDate, inpFmt));
		 // Check if date is null
	    if (date == null) {
	        return null;
	    }
		String formattedDate = date.format(outFmt);
		return formattedDate;
	}

	public static String fmtTimeOnly(LocalDateTime ldt) {
		if (ldt == null)
			return null;
		DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm:ss");
		return f.format(ldt);
	}

	public static LocalDateTime toZone2DateTimeFromZone1(LocalDateTime ldt,
			ZoneId fromZone, ZoneId toZone) {
		if (ldt == null)
			return null;
		Instant instant = ldt.atZone(fromZone).toInstant();
		return LocalDateTime.ofInstant(instant, toZone);
	}

	public static LocalDateTime toLocalDateTimeFromUTC(LocalDateTime utcDateTime) {
	    if (utcDateTime == null) {
	        return null;
	    }
	    
	    ZoneId curZoneId = ZoneId.systemDefault();
	    LocalDateTime convertedDateTime = toZone2DateTimeFromZone1(utcDateTime, ZONE_ID_UTC, curZoneId);

	    // Check if the conversion resulted in a null value
	    if (convertedDateTime == null) {
	        return null;
	    }

	    return convertedDateTime;
	}


	public static LocalDateTime toUTCDateTimeFromLocal(
			LocalDateTime localDateTime) {
		if (localDateTime == null)
			return null;
		ZoneId curZoneId = ZoneId.systemDefault();
		return toZone2DateTimeFromZone1(localDateTime, curZoneId, ZONE_ID_UTC);
	}

	public static LocalDateTime toISTDateTimeFromUTC(
			LocalDateTime utcDateTime) {
		if (utcDateTime == null)
			return null;
		return toZone2DateTimeFromZone1(utcDateTime, ZONE_ID_UTC,
				ZONE_ID_INDIA);
	}

	public static LocalDate toLocalDateTimeFromUTC(LocalDate utcDate) {
	    if (utcDate == null) {
	        return null;
	    }
	    LocalDateTime utcDateTime = utcDate.atStartOfDay();
	    LocalDateTime convertedDateTime = toZone2DateTimeFromZone1(utcDateTime, ZONE_ID_UTC, ZoneId.systemDefault());
	    if (convertedDateTime == null) {
	        return null;
	    }
	    return convertedDateTime.toLocalDate();
	}

/*	public static LocalDate toUTCDateTimeFromLocal(LocalDate localDate) {
		if (localDate == null)
			return null;
		return toZone2DateTimeFromZone1(localDate.atStartOfDay(),
				ZoneId.systemDefault(), ZONE_ID_UTC).toLocalDate();
	}*/
	public static LocalDate toUTCDateTimeFromLocal(LocalDate localDate) {
	    if (localDate == null) {
	        return null;
	    }
	    LocalDateTime zonedDateTime = toZone2DateTimeFromZone1(
	            localDate.atStartOfDay(),
	            ZoneId.systemDefault(),
	            ZONE_ID_UTC
	    );
	    if (zonedDateTime == null) {
	        return null;
	    }

	    return zonedDateTime.toLocalDate();
	}

	public static LocalDateTime toLocalDateTimeFromUTC(Date utcDate) {
		if (utcDate == null)
			return null;
		Instant instant = utcDate.toInstant();
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	public static LocalDateTime toUTCDateTimeFromLocal(Date date) {
	    if (date == null) {
	        return null;
	    }

	    Instant instant = date.toInstant();
	    return LocalDateTime.ofInstant(instant, ZONE_ID_UTC);
	}


	
	
	public static LocalDateTime toUTCDateTimeFromIST(
			LocalDateTime istDateTime) {
		if (istDateTime == null)
			return null;
		return toZone2DateTimeFromZone1(istDateTime, ZONE_ID_INDIA,
				ZONE_ID_UTC);
	}

	public static LocalDate toISTDateTimeFromUTC(LocalDate utcDate) {
	    if (utcDate == null) {
	        return null;
	    }
	    LocalDateTime utcDateTime = utcDate.atStartOfDay();
	    LocalDateTime indiaDateTime = toZone2DateTimeFromZone1(utcDateTime, ZONE_ID_UTC, ZONE_ID_INDIA);
	    if (indiaDateTime == null) {
	        return null;
	    }
	    return indiaDateTime.toLocalDate();
	}

	/*public static LocalDate toUTCDateTimeFromIST(LocalDate istDate) {
		if (istDate == null)
			return null;
		return toZone2DateTimeFromZone1(istDate.atStartOfDay(), ZONE_ID_INDIA,
				ZONE_ID_UTC).toLocalDate();
	}*/

	public static LocalDate toUTCDateTimeFromIST(LocalDate istDate) {
	    if (istDate == null) {
	        return null;
	    }
	    LocalDateTime istDateTime = istDate.atStartOfDay();
	    LocalDateTime utcDateTime = toZone2DateTimeFromZone1(istDateTime, ZONE_ID_INDIA, ZONE_ID_UTC);

	    if (utcDateTime == null) {
	        return null;
	    }
	    return utcDateTime.toLocalDate();
	}

	public static LocalDateTime toISTDateTimeFromUTC(Date utcDate) {
		if (utcDate == null)
			return null;
		Instant instant = utcDate.toInstant();
		return LocalDateTime.ofInstant(instant, ZONE_ID_INDIA);
	}

	public static LocalDateTime toUTCDateTimeFromIST(Date istDate) {
		if (istDate == null)
			return null;
		Instant instant = istDate.toInstant();
		return LocalDateTime.ofInstant(instant, ZONE_ID_UTC);
	}
	
}
