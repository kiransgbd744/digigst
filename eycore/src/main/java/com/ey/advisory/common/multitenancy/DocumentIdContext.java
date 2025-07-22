package com.ey.advisory.common.multitenancy;

import org.springframework.stereotype.Component;

/**
 * Class copied form ASP tenant service.
 * 
 * @author Sai.Pakanati
 *
 */
@Component
public class DocumentIdContext {
	
	/**
	 * Make the class non-instantiable.
	 */
	private DocumentIdContext() {}
	
	private static final ThreadLocal<String> context = new ThreadLocal<>();	
	
	public static void setDocumentId(String tenantId) {
		context.set(tenantId);
	}

	public static String getDocumentId() {

		return context.get();
	}
	

	public static void clearTenant() {
		context.remove();
	}
	
	
}
