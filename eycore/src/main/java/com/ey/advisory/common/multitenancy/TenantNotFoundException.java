package com.ey.advisory.common.multitenancy;

import com.ey.advisory.common.AppException;

/**
 * 
 * Exception thrown when the tenant is not available in the DB
 * Copied from ASP Tenant Service.
 * 
 */
public class TenantNotFoundException extends AppException {

	private static final long serialVersionUID = 1L;
	public String message;


	public TenantNotFoundException(String message) {
		super(message);
	}

	public TenantNotFoundException(String message, Throwable ex) {
		 super(message, ex);
	}
	
}
