package com.ey.advisory.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Siva.Nandam
 *
 */
public class DateFormatForStructuralValidatons {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DateUtil.class);

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT1 = DateTimeFormatter
			.ofPattern("uuuu-MM-dd");

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT2 = DateTimeFormatter
			.ofPattern("dd-MM-uuuu");

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT3 = DateTimeFormatter
			.ofPattern("uuuu/MM/dd");

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT4 = DateTimeFormatter
			.ofPattern("dd/MM/uuuu");

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT5 = DateTimeFormatter
			.ofPattern("uuuu-MM-dd HH:mm:ss");
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT6 = DateTimeFormatter
			.ofPattern("uuuu-MM-dd'T'HH:mm:ss");
			
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT7 = DateTimeFormatter
			.ofPattern("uuuu.MM.dd");
	
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT8 = DateTimeFormatter
			.ofPattern("dd.MM.uuuu");
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT9 = DateTimeFormatter
			.ofPattern("dd-MM-uuuu HH:mm:ss");
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT10 = DateTimeFormatter
			.ofPattern("dd-MMM-uu");
			

	private static final DateTimeFormatter[] FAST_DATE_FORMATS = {
			SUPPORTED_DATE_FORMAT1, SUPPORTED_DATE_FORMAT2,
			SUPPORTED_DATE_FORMAT3, SUPPORTED_DATE_FORMAT4,
			SUPPORTED_DATE_FORMAT5, SUPPORTED_DATE_FORMAT6,
			//SUPPORTED_DATE_FORMAT7, SUPPORTED_DATE_FORMAT8, 
			SUPPORTED_DATE_FORMAT9,SUPPORTED_DATE_FORMAT10};

	private DateFormatForStructuralValidatons() {
	}

	public static LocalDate convertUtilDateToLocalDate(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant,
				ZoneId.systemDefault());
		return localDateTime.toLocalDate();
	}

	public static LocalDateTime convertUtilDateToLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime())
				.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static Date convertLocalDateToUtilDate(LocalDate date) {
		return java.util.Date.from(
				date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date convertLocalDateTimeToUtilDate(LocalDateTime date) {
		return java.util.Date
				.from(date.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate parseObjToDate(Object docDate) {

		if (docDate == null) {
			return null;
		}

		if (docDate instanceof com.aspose.cells.DateTime) {
			Date date = ((com.aspose.cells.DateTime) docDate).toDate();
			return convertUtilDateToLocalDate(date);
		}

		if (docDate instanceof Date) {
			return convertUtilDateToLocalDate((Date) docDate);
		}

		if (docDate instanceof String
				&& !((String) docDate).trim().equals("")) {
			//-- applies BC(nagetive) year
			if (((String) docDate).contains("--")
					|| ((String) docDate).contains("/-")
					|| ((String) docDate).startsWith("-")
					) {
				return null;
			}
			
			for (DateTimeFormatter format : FAST_DATE_FORMATS) {
				LocalDate date = tryConvertUsingFormat((String) docDate,
						format);
				if (date != null)
					return date;
			}
			return null;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("docDate is not instance of String "
					+ "or Util Date Aspose Date");
		}

		return null;
	}

	private static LocalDate tryConvertUsingFormat(String docDate,
			DateTimeFormatter dateFormat) {
		try {

			return LocalDate.parse(docDate,
					dateFormat.withResolverStyle(ResolverStyle.STRICT));
		} catch (Exception e) {
			return null;
		}
	}

	public static LocalDateTime stringToTime(String dateTime,
			DateTimeFormatter formatter) {
		return LocalDateTime.parse(dateTime, formatter);
	}


}
