package com.ey.advisory.common.multitenancy;


public class TenantConstant {
	
	/**
	 * Making the class non-instantiable.
	 */
	private TenantConstant() {}
	
	public static final String REQUEST_TENANT_HEADER = "X-TENANT-ID";
	public static final String DB_NAME = "DB_NAME";
	public static final String DB_URL = "DB_URL";
	public static final String DB_PASSWORD = "DB_PASSWORD";
	public static final String DB_USER_NAME = "USER_NAME";
	public static final String DATASOURCE_CLASS = "DATASOURCE_CLASS";
}
