package com.ey.advisory.common;

public class ErrorConstants {
	/**
	 * Unknown Error Code. This is the default value set to the error code
	 * if this class is instantiated without any errorCode.
	 */
	public static final String UNKNOWN_ERROR = "APP-9000";
	
	/**
	 * This is the max length of exception strings that can be stored in the DB.
	 * Any information that can be packed into this will allow the developer
	 * to monitor the exception without having to scan the logs.
	 */
	public static final int ERR_MSG_STR_LEN = 1024; 
}
