package com.ey.advisory.gstnapi;

public class GSTNAPIParamNames {
	
	/**
	 * Make the class non-instantiable.
	 */
	private GSTNAPIParamNames() {}
	
	// Constants required in the HTTP Request header, for GSTN
	public static final String AUTH_TOKEN = "auth-token";
	public static final String STATE_CODE = "state-cd";	
	public static final String GSTN_USER_NAME = "username";
	public static final String IP_USER = "ip-usr";
	public static final String TXN_NO = "txn";
	public static final String CLIENT_ID = "clientid";
	public static final String CLIENT_SECRET = "client-secret";
	
	// Constants required in the HTTP Request header for GSP 
	public static final String DIGIGST_USER_NAME = "digigst_username";
	public static final String API_KEY = "api_key";
	public static final String API_SECRET = "";
	public static final String ACCESS_TOKEN = "access_token";
	
	//Constants required in the URL Params for GSTN
	public static final String ACTION = "action";
	public static final String GSTIN = "gstin";
	public static final String REF_ID = "ref_id";
	public static final String RET_PERIOD = "ret_period";
}
