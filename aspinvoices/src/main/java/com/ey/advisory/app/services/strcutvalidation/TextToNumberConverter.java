package com.ey.advisory.app.services.strcutvalidation;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component("TextToNumberConverter")
public class TextToNumberConverter implements Converter{

	@Override
	public Number convert(Object value) {
		BigDecimal result = new BigDecimal((String) value);
		return result;
	}

}
