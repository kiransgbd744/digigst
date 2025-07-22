package com.ey.advisory.app.services.strcutvalidation;

public class NumberToTextConverter implements Converter {

	@Override
	public String convert(Object obj) {
		return String.valueOf(obj);
	}

}
