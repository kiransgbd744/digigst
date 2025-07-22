/**
 * 
 */
package com.ey.advisory.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.base.Strings;

/**
 * @author Siva.Reddy This adapter is created for trans doc date
 *
 */
public class GetEwbLocalDateAdapter extends XmlAdapter<String, LocalDate> {

	private static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");

	private static final DateTimeFormatter FORMATTER2 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	@Override
	public LocalDate unmarshal(String dateTime) throws Exception {
		if (Strings.isNullOrEmpty(dateTime))
			return null;

		try {
			return LocalDate.parse(dateTime, FORMATTER2);
		} catch (Exception e) {

		}

		try {
			return LocalDate.parse(dateTime, formatter);
		} catch (Exception e) {

		}

		throw new AppException(
				"Exception Occured while deserializing date : " + dateTime);
	}

	@Override
	public String marshal(LocalDate v) throws Exception {
		return v.format(FORMATTER2);
	}

}
