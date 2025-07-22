package com.ey.advisory.app.services.strcutvalidation;

public class TextToBooleanConverter implements Converter{

	@Override
	public Boolean convert(Object obj) {
		String value = (String) obj;
		return value.equalsIgnoreCase("true");
	}

}
