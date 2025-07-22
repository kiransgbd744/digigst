package com.ey.advisory.app.services.strcutvalidation;

public class ValidatorUtil {

	public static boolean isValuePresent(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof String) {
			String value = (String) obj;
			return !value.trim().isEmpty();
		}
		return false;
	}

	/*
	 * public static boolean isConvertibleToString(Object srcObj) { if (srcObj
	 * instanceof String || srcObj instanceof Number || srcObj instanceof
	 * Boolean || srcObj instanceof Date) return true; return false; }
	 */

	public static boolean isEvenNumber(Integer num) {
		return num % 2 == 0;
	}

}
