/**
 * 
 */
package com.ey.advisory.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Khalid1.Khan
 *
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

	private static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
	
	@Override
	public LocalDateTime unmarshal(String dateTime) throws Exception {
		return LocalDateTime.parse(dateTime, formatter);
	}
	
	@Override
	public String marshal(LocalDateTime dateTime) throws Exception {
		return dateTime.format(formatter);
	}

}
