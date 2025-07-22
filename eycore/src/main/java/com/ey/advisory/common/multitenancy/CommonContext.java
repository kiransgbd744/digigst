package com.ey.advisory.common.multitenancy;

import org.springframework.stereotype.Component;

/**
 * This object holds the common variables across the request
 * 
 * @author Sai.Pakanati
 *
 */
@Component
public class CommonContext {

	/**
	 * Make the class non-instantiable.
	 */
	private CommonContext() {
	}

	private static final ThreadLocal<String> gstinContext = new ThreadLocal<>();

	private static final ThreadLocal<String> taxPeriodContext = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> delinkingFlagContext = new ThreadLocal<>();

	private static final ThreadLocal<String> returnTypeContext = new ThreadLocal<>();

	public static void setGstin(String gstin) {
		gstinContext.set(gstin);
	}

	public static String getGstin() {

		return gstinContext.get();
	}

	public static void setTaxPeriod(String taxPeriod) {
		taxPeriodContext.set(taxPeriod);
	}

	public static String getTaxPeriod() {
		return taxPeriodContext.get();
	}
	
	public static void setReturnType(String returnType) {
		returnTypeContext.set(returnType);
	}

	public static String getReturnType() {

		return returnTypeContext.get();
	}
	public static void setDelinkingFlagContext(Boolean delinkingFlag) {
		delinkingFlagContext.set(delinkingFlag);
	}

	public static Boolean getDelinkingFlagContext() {

		return delinkingFlagContext.get();
	}

	public static void clearContext() {
		gstinContext.remove();
		taxPeriodContext.remove();
		delinkingFlagContext.remove();
		returnTypeContext.remove();
	}

}
