package com.ey.advisory.gstnapi;

public class ExecErrorCodes {

	private ExecErrorCodes() {}
	
	public static final String INVOCATION_REQ_LOAD_FAILURE = "IRLF";
	public static final String HANDLER_VERIFICATION_FAILURE = "HVF";	
	public static final String LOAD_RESPONSE_IDS_FAILURE = "LRIDF";
	public static final String API_EXEC_NETWORK_FAILURE = "AENF";
	public static final String API_EXEC_PROVIDER_FAILURE = "AEPF";
	public static final String SUCCESS_HNDLR_INVOC_FAILURE = "SHIF";
	public static final String FAILURE_HNDLR_INVOC_FAILURE = "FHIF";
	public static final String NEW_INVOC_CREATION_FAILURE = "NICF";	
	public static final String API_RESP_PERSIST_FAILURE = "ARPF";
	public static final String INVOC_EXISTENCE_CHK_FAILURE = "IECF";
	public static final String UNKNOWN_FAILURE = "UNKNOWN";
	public static final String INVOC_STATUS_UPDATE_FAILURE = "ISUF";

}
