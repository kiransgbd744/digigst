package com.ey.advisory.app.services.strcutvalidation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TextToDateConverter implements Converter {

	private static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	@Override
	public LocalDate convert(Object obj) {
		String value = (String) obj;
		LocalDate localDate = LocalDate.parse(value, formatter);
		return localDate;
	}

}
