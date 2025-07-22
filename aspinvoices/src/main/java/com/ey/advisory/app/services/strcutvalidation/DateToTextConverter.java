package com.ey.advisory.app.services.strcutvalidation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateToTextConverter implements Converter{
	
	private static DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");

	@Override
	public String convert(Object obj) {
		LocalDateTime now = (LocalDateTime) obj;
		return null;
	}

}
