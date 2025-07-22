package com.ey.advisory.app.services.strcutvalidation;

public interface DataConverterFactory {
	
	public Converter getConverter(Object srcObj, Class<?> expObj);

}
