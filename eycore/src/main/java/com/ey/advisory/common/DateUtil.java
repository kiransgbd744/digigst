package com.ey.advisory.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for analyzing a String and converting it into a
 * date, if possible. If the string cannot be converted to a date, it 
 * returns null. 
 * 
 * @author V.Mule
 *
 */
public class DateUtil {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DateUtil.class);

	public static final DateTimeFormatter DATE_FORMAT = 
			DateTimeFormatter.ofPattern("yyyyMMdd");
	
	public static final DateTimeFormatter DATE_FORMAT1 = 
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT1 = 
			DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT2 = 
			DateTimeFormatter.ofPattern("dd-MM-yyyy");

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT3 = 
			DateTimeFormatter.ofPattern("yyyy/MM/dd");

	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT4 = 
			DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT5 = 
			DateTimeFormatter.ofPattern("MMyyyy");
	
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT6 = 
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT7 = 
			DateTimeFormatter.ofPattern("dd-MMM-yy");
	
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT8 = 
			DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT9 = 
			DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	
	public static final DateTimeFormatter SUPPORTED_DATE_FORMAT10 = 
			DateTimeFormatter.ofPattern("MMM-yy");
	
	
	
	private static final DateTimeFormatter[] FAST_DATE_FORMATS = {
			DATE_FORMAT,SUPPORTED_DATE_FORMAT1, SUPPORTED_DATE_FORMAT2,
			SUPPORTED_DATE_FORMAT3, SUPPORTED_DATE_FORMAT4,
			SUPPORTED_DATE_FORMAT5, SUPPORTED_DATE_FORMAT6, SUPPORTED_DATE_FORMAT7, 
			SUPPORTED_DATE_FORMAT8};
	
	private static final DateTimeFormatter[] TCS_TDS_DATE_FORMATS = {
			SUPPORTED_DATE_FORMAT1,SUPPORTED_DATE_FORMAT3};
	
	
	private DateUtil() {}

	public static LocalDate convertUtilDateToLocalDate(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime()); 
		LocalDateTime localDateTime = LocalDateTime.ofInstant(
					instant, ZoneId.systemDefault()); 
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
	public static void main(String[] args) {
		Object a="17.10.2017";
		System.out.println(parseObjToDate(a));
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
			return convertUtilDateToLocalDate((Date)docDate);
		}

		if (docDate instanceof String && 
				!((String) docDate).trim().equals("")) {		
			for (DateTimeFormatter format : FAST_DATE_FORMATS) {
				if(!((String) docDate).trim().isEmpty()){
					LocalDate date = tryConvertUsingFormat(
							((String) docDate).trim(), format);	
					if (date != null) return date;
				}
			}
			return null;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("docDate is not instance of String "
					+ "or Util Date Aspose Date");
		}
		
		return null;		
	}
	
	public static LocalDate tryConvertUsingFormat(String docDate,
			DateTimeFormatter dateFormat) {
		try {
			return LocalDate.parse(docDate, dateFormat);			
		} catch (Exception e) {
			return null;
		}
	}	
	
	public static LocalDateTime stringToTime(String dateTime,
			DateTimeFormatter formatter) {
		return LocalDateTime.parse(dateTime, formatter);
	}
public static LocalDate parseObjToDateTdsTcs(Object docDate) {
		
		
		if (docDate == null) {
			return null;
		}

		if (docDate instanceof com.aspose.cells.DateTime) {
			Date date = ((com.aspose.cells.DateTime) docDate).toDate();
			return convertUtilDateToLocalDate(date);
		}

		if (docDate instanceof Date) {
			return convertUtilDateToLocalDate((Date)docDate);
		}

		if (docDate instanceof String && 
				!((String) docDate).trim().equals("")) {		
			for (DateTimeFormatter format : TCS_TDS_DATE_FORMATS) {
				if(!((String) docDate).trim().isEmpty()){
					LocalDate date = tryConvertUsingFormat(
							((String) docDate).trim(), format);	
					if (date != null) return date;
				}
			}
			return null;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("docDate is not instance of String "
					+ "or Util Date Aspose Date");
		}
		
		return null;		
	}
}
