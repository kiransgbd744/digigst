package com.ey.advisory.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
	
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	
	private static final String DEFAULT_DATETIME_FORMAT = 
					"yyyy-MM-dd'T'HH:mm:ss";
	
	/**
	 * Make the class non-instantiable. Only static methods should be included
	 * in this class.
	 */
	private JsonUtil() {}
	
	/**
	 * Create and return a new Gson Instance. There are some issues
	 * recorded against the shared use of a Gson object in a multi-threaded
	 * environment. The documentation does not say anything regarding thread
	 * safety. Once this is addressed, we can use a shared static instance
	 * of Gson instead of a newInstance() factory  method.
	 * 
	 * @return
	 */
	public static Gson newGsonInstanceWithTime() {
		return 	new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.serializeNulls()
				.setDateFormat(DEFAULT_DATETIME_FORMAT)
				.create();
	}
	
	public static Gson newGsonInstanceWithoutTime() {
		return 	new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.serializeNulls()
				.setDateFormat(DEFAULT_DATE_FORMAT)
				.create();
	}
	
	public static Gson newGsonInstance(boolean withTime) {
		return withTime ? newGsonInstanceWithTime() : 
				newGsonInstanceWithoutTime();
	}
	
	public static Gson newGsonInstance() {
		return new GsonBuilder()
				.serializeNulls()
				.excludeFieldsWithoutExposeAnnotation()
				.create();
	}
	
	public static Gson newGsonInstanceWithDateFormat(String format) {
		return 	new GsonBuilder()
				.excludeFieldsWithoutExposeAnnotation()
				.serializeNulls()
				.setDateFormat(format)
				.create();
	}
	
}
