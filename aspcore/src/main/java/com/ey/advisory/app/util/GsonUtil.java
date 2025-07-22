package com.ey.advisory.app.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ey.advisory.common.GsonLocalDateConverter;
import com.ey.advisory.common.GsonLocalDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Umesha.M
 *
 */
public class GsonUtil {

	private GsonUtil() {}
	
	/**
	 * @return
	 */
	public static Gson newSAPGsonInstance() {
		return new GsonBuilder()
				.registerTypeAdapter(
						LocalDate.class, new GsonLocalDateConverter())
				.registerTypeAdapter(
						LocalDateTime.class, new GsonLocalDateTimeConverter())
				.create();
	}
	
	/**
	 * @return
	 */
	public static Gson gsonInstanceWithExpose() {
		return new GsonBuilder()
				.registerTypeAdapter(
						LocalDate.class, new GsonLocalDateConverter())
				.registerTypeAdapter(
						LocalDateTime.class, new GsonLocalDateTimeConverter())
				.excludeFieldsWithoutExposeAnnotation()
				.create();
	}
}	
