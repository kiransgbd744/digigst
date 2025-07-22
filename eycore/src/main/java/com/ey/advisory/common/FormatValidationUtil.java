package com.ey.advisory.common;

import java.math.BigDecimal;
import java.math.BigInteger;

public class FormatValidationUtil {
	
	private FormatValidationUtil() {}
	
	public static boolean isNumber(Object obj) {
		return isDecimal(obj) || isInteger(obj);
	}

	public static boolean isInteger(Object obj) {
		if (obj == null)
			return true;
		// We are eliminating the Double and Float data types here.
		// But, there may be cases where the data can be sent as a Double
		// or a float, but with 0 after the decimal places. Currently,
		// we're not considering this.
		if (obj instanceof Long || obj instanceof Short
				|| obj instanceof Integer)
			return true;

		// We're ignoring BigDecimal even if the decimal places contain
		// zero.
		if (obj instanceof BigInteger)
			return true;
		if (obj instanceof String) {
			try {
				Long.parseLong(obj.toString());
				return true;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return false;
	}

	public static boolean isDecimal(Object obj) {
		if (obj == null)
			return true;
		if (obj instanceof Number)
			return true;
		if (obj instanceof BigDecimal)
			return true;
		if (obj instanceof BigInteger)
			return true;
		if (obj instanceof String) {
			try {
				Double.parseDouble(obj.toString());
				return true;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return false;
	}
	
	public static boolean isPresent(Object obj) {
		
		if (obj == null) return false;
		if (obj instanceof String) {
			return !(obj.toString()).trim().isEmpty();
		}
		return true;
	}
	
}
