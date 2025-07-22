package com.ey.advisory.common;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ey.advisory.common.GsonLocalDateConverter;
import com.ey.advisory.common.GsonLocalDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

	private GsonUtil() {
	}

	public static Gson newSAPGsonInstance() {
		return new GsonBuilder()
				.registerTypeAdapter(LocalDate.class,
						new GsonLocalDateConverter())
				.registerTypeAdapter(LocalDateTime.class,
						new GsonLocalDateTimeConverter())
				.create();
	}

	public static Gson newSAPGsonInstanceWithDateFmt() {
		return new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	}

	public static Gson gsonInstanceWithExpose() {
		return new GsonBuilder()
				.registerTypeAdapter(LocalDate.class,
						new GsonLocalDateConverter())
				.registerTypeAdapter(LocalDateTime.class,
						new GsonLocalDateTimeConverter())
				.excludeFieldsWithoutExposeAnnotation().create();
	}

	public static Gson gsonInstanceWithEWBDateFormat() {
		return new GsonBuilder()
				.registerTypeAdapter(LocalDate.class,
						new GsonEWBLocalDateConverter())
				.registerTypeAdapter(LocalDateTime.class,
						new GsonEWBLocalDateTimeConverter())
				.excludeFieldsWithoutExposeAnnotation().create();
	}

	public static Gson newSAPGsonInstanceWithEWBDateFmt() {
		return new GsonBuilder()
				.registerTypeAdapter(LocalDate.class,
						new GsonEWBLocalDateConverter())
				.excludeFieldsWithoutExposeAnnotation().create();
	}

	public static Gson gsonInstanceWithExposeAndNull() {
		return new GsonBuilder()
				.registerTypeAdapter(LocalDate.class,
						new GsonLocalDateConverter())
				.registerTypeAdapter(LocalDateTime.class,
						new GsonLocalDateTimeConverter())
				.excludeFieldsWithoutExposeAnnotation().serializeNulls()
				.create();
	}

	public static Gson gsonInstanceWithEWB24HRFormat() {
		return new GsonBuilder()
				.registerTypeAdapter(LocalDate.class,
						new GsonEWBLocalDateConverter())
				.registerTypeAdapter(LocalDateTime.class,
						new GsonEWBLocalDateTime24HourConverter())
				.excludeFieldsWithoutExposeAnnotation().create();
	}

	public static Gson gsonWithDisableHtmlEscpaing() {
		return new GsonBuilder()
				.registerTypeAdapter(LocalDate.class,
						new GsonLocalDateConverter())
				.registerTypeAdapter(LocalDateTime.class,
						new GsonLocalDateTimeConverter())
				.disableHtmlEscaping().create();
	}

	public static Gson gsonInstanceBcapi() {
		return new GsonBuilder()
				.registerTypeAdapter(
						LocalDate.class, new GsonEWBLocalDateConverter())
				.registerTypeAdapter(
						LocalDateTime.class, new GsonLocalDateTime24HourConverter())
				.disableHtmlEscaping().setPrettyPrinting().create();
	}

}
