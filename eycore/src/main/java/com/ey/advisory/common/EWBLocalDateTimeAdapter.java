/**
 * 
 */
package com.ey.advisory.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.base.Strings;

/**
 * @author Khalid1.Khan
 *
 */
public class EWBLocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter
			.ofPattern("dd/MM/yyyy hh:mm:ss a");
	private static final DateTimeFormatter FORMATTER3 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy HH:mm:ss");
	private static final DateTimeFormatter FORMATTER1 = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter FORMATTER4 = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter FORMATTER2 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter FORMATTER5 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");
	private static final DateTimeFormatter FORMATTER6 = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final DateTimeFormatter FORMATTER7 = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	private static final DateTimeFormatter FORMATTER8 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy hh:mm:ss");

	@Override
	public LocalDateTime unmarshal(String dateTime) throws Exception {

		if (Strings.isNullOrEmpty(dateTime))
			return null;

		try {
			return LocalDateTime.parse(dateTime, FORMATTER);
		} catch (Exception e) {

		}
		try {
			return LocalDateTime.parse(dateTime, FORMATTER3);
		} catch (Exception e) {

		}

		try {
			return LocalDateTime.parse(dateTime, FORMATTER1);
		} catch (Exception e) {

		}
		try {
			return LocalDate.parse(dateTime, FORMATTER2).atStartOfDay();
		} catch (Exception e) {

		}
		try {
			return LocalDate.parse(dateTime, FORMATTER4).atStartOfDay();
		} catch (Exception e) {

		}

		try {
			return LocalDate.parse(dateTime, FORMATTER5).atStartOfDay();
		} catch (Exception e) {
		}

		try {
			return LocalDateTime.parse(dateTime, FORMATTER6);
		} catch (Exception e) {

		}

		try {
			return LocalDateTime.parse(dateTime, FORMATTER7);
		} catch (Exception e) {

		}
		try {
			return LocalDateTime.parse(dateTime.trim(), FORMATTER8);
		} catch (Exception e) {

		}
		throw new AppException(
				"Exception Occured while deserializing date : " + dateTime);

	}

	@Override
	public String marshal(LocalDateTime dateTime) throws Exception {
		return dateTime.format(FORMATTER);
	}

}
