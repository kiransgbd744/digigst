package com.ey.advisory.common.multitenancy;

import org.springframework.stereotype.Component;

/**
 * Class copied form ASP tenant service.
 * 
 * @author Sai.Pakanati
 *
 */
@Component
public class TenantContext {
	
	/**
	 * Make the class non-instantiable.
	 */
	private TenantContext() {}
	
	private static final ThreadLocal<String> context = new ThreadLocal<>();	
	
	private static final ThreadLocal<String> errMsgContext = new ThreadLocal<>();	

	public static void setTenantId(String tenantId) {
		context.set(tenantId);
	}

	public static String getTenantId() {

		return context.get();
	}
	
	public static void setErrorMsg(String errorMsg) {
		errMsgContext.set(errorMsg);
	}
	
	public static String getErrorMsg() {
		return errMsgContext.get();
	}

	public static void clearTenant() {
		context.remove();
		errMsgContext.remove();
	}
	
	
}
