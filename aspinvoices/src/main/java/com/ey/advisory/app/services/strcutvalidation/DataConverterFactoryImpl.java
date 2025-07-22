package com.ey.advisory.app.services.strcutvalidation;


import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.TaskProcessor;

@Component("DataConverterFactoryImpl")
public class DataConverterFactoryImpl implements DataConverterFactory {

	@Override
	public Converter getConverter(Object srcObj, Class<?> expObj) {
		Converter converter = null;
		Class<?> srccls = srcObj.getClass();
		if (srccls == String.class && expObj == String.class) {
			converter = (Converter) StaticContextHolder
					.getBean("PRDBChunkProcessor", TaskProcessor.class);
		} else if (srccls == String.class && expObj == Number.class) {
			converter = (Converter) StaticContextHolder
					.getBean("PRDBChunkProcessor", TaskProcessor.class);
		} else if (srccls == String.class && expObj == Boolean.class) {
			converter = (Converter) StaticContextHolder
					.getBean("PRDBChunkProcessor", TaskProcessor.class);
		} else if (srccls == String.class && expObj == LocalDate.class) {
			converter = (Converter) StaticContextHolder
					.getBean("PRDBChunkProcessor", TaskProcessor.class);
		} else if (srccls == Number.class && expObj == String.class) {
			converter = (Converter) StaticContextHolder
					.getBean("PRDBChunkProcessor", TaskProcessor.class);
		} else if (srccls == LocalDate.class && expObj == String.class) {
			converter = (Converter) StaticContextHolder
					.getBean("PRDBChunkProcessor", TaskProcessor.class);
		} else if (srccls == Number.class && expObj == Number.class) {
			converter = (Converter) StaticContextHolder
					.getBean("PRDBChunkProcessor", TaskProcessor.class);
		} else if (srccls == LocalDate.class && expObj == LocalDate.class) {
			converter = (Converter) StaticContextHolder
					.getBean("PRDBChunkProcessor", TaskProcessor.class);
		}
		return converter;
	}

}
