package com.ey.advisory.common;

import org.springframework.stereotype.Component;

/**
 * This Context carries an UUID 
 * 
 * @author Sai.Pakanati
 *
 */
@Component
public class UUIDContext {
	
	/**
	 * Make the class non-instantiable.
	 */
	private UUIDContext() {}
	
	private static final ThreadLocal<String> context = new ThreadLocal<>();	
	
	public static void setUniqueID(String uuid) {
		context.set(uuid);
	}

	public static String getUniqueID() {

		return context.get();
	}
	
	public static void clearTenant() {
		context.remove();
	}
	
}
