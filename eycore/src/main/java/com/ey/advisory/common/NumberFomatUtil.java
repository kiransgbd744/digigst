package com.ey.advisory.common;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberFomatUtil {
	private NumberFomatUtil() {
	}

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
				Long.parseLong((String) obj);
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
				Double.parseDouble((String) obj);
				return true;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
		return false;
	}
	public static BigDecimal getBigDecimal( Object value ) {
        BigDecimal ret = null;
        if( value != null ) {
            if( value instanceof BigDecimal ) {
                ret = (BigDecimal) value;
            } else if( value instanceof BigInteger ) {
                ret = new BigDecimal( (BigInteger) value );
            } else if( value instanceof Number ) {
                ret = new BigDecimal( ((Number)value).doubleValue() );
            }
            else if( value instanceof String ) {
                ret = new BigDecimal( (String) value );
            } 
            else {
                throw new ClassCastException
                ("Not possible to Create ["+value+"] "
                		+ "from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }

	/**
	 * @param obj
	 * @return
	 */
	public static boolean isValidDec(Object obj) {
		BigDecimal check = new BigDecimal("9999999999999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}

	/**
	 * @param obj
	 * @return
	 */
	public static boolean is3digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("999999999999.999");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	public static boolean is133digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("9999999999.999");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	public static boolean is132digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("9999999999999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	
	public static boolean is73digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("9999999.999");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	
	public static boolean is72digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("99999999999999999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	public static boolean is102digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("9999999999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	public static boolean roundOffValidDec(Object obj) {
		BigDecimal check = new BigDecimal("9999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	
	public static boolean is2And2digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("99.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	
	public static boolean is14And2digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("99999999999999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	
	public static boolean is3And3digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("999.999");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	
	public static boolean is12And2digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("999999999999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	
	public static boolean is10And3digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("9999999999.999");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	
	public static boolean is12And3digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("999999999999.999");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	public static boolean is7And3digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("9999999.999");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	public static boolean is17And2digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("99999999999999999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	public static boolean is15And2digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("999999999999999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	public static boolean is8And2digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("99999999.99");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
	public static boolean is4And4digValidDec(Object obj) {
		BigDecimal check = new BigDecimal("9999.9999");
		BigDecimal checkObjVal = new BigDecimal(obj.toString());
		
		return check.compareTo(checkObjVal) >= 0 ? true : false;
	}
}
