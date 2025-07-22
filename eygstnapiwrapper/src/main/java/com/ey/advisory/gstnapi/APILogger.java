package com.ey.advisory.gstnapi;

import com.ey.advisory.common.StaticContextHolder;

/**
 * Static class that uses the default API logger implementation. Currently,
 * the Logger implementation bean is looked up using a fixed name. Later on, 
 * we can make this class to look up the bean based on an external 
 * configuration.
 * 
 * @see APIExecEventsLogger
 * 
 * @author Khalid1.Khan
 *
 */
public class APILogger {
	
	private static final String API_LOGGER_IMPL = "DefaultAPIExecEventsLogger";
	
	private APILogger() {}
	
	/**
	 * Log the specified errCode and errMsg against the specified invocation
	 * request id.
	 * 
	 * @param reqId The invocation request id.
	 * @param errMsg the error message to be recorded against the invocation
	 * 		request id.
	 * @param errCode the error code. In some situations like failure while 
	 * 		executing a GSTN API, this makes sense.
	 */
	public static void logError(Long reqId, String errMsg, String errCode) {
		APIExecEventsLogger logger =  StaticContextHolder.getBean(
				API_LOGGER_IMPL, APIExecEventsLogger.class);
		logger.logError(reqId, errMsg, errCode);
	}
	
	public static void logError(Long reqId, String errMsg, String errCode,
			Exception e) {
		APIExecEventsLogger logger =  StaticContextHolder.getBean(
				API_LOGGER_IMPL, APIExecEventsLogger.class);
		logger.logError(reqId, errMsg, errCode, e);
	}
	
	/**
	 * Logs an error message against the invocation request id, without the 
	 * error code.  
	 * 
	 * @param reqId the invocation request id.
	 * @param errMsg the error message to be recorded against the invocation.
	 */
	public static void logError(Long reqId, String errMsg) {
		logError(reqId, errMsg, null);
	}
	
	
	/**
	 * Log the specified general error message. This should be used if an 
	 * invocation request id is not available. If an invocation request id
	 * is available, then the overloaded version with the reqId should be used.
	 * Usually, this is used only during the invocation request id.
	 * 
	 * @param errMsg The general error message to be logged.
	 * @param errCode the error code. In some situations like failure while 
	 * 		executing a GSTN API, this makes sense.
	 */
	public static void logError(String errMsg, String errCode) {
		APIExecEventsLogger logger =  StaticContextHolder.getBean(
				API_LOGGER_IMPL, APIExecEventsLogger.class);
		logger.logError(errMsg, errCode);
	}
	
	public static void logError(String errMsg, String errCode, Exception e) {
		APIExecEventsLogger logger =  StaticContextHolder.getBean(
				API_LOGGER_IMPL, APIExecEventsLogger.class);
		logger.logError(errMsg, errCode, e);
	}
	
	/**
	 * Log an information message against the invocation request id. 
	 * 
	 * @param reqId the invocation request id against which the information 
	 * 		message is to be recorded.
	 * @param infoMsg the information message.
	 */
	public static void logInfo(Long reqId, String infoMsg) {
		APIExecEventsLogger logger =  StaticContextHolder.getBean(
				API_LOGGER_IMPL, APIExecEventsLogger.class);
		logger.logInfo(reqId, infoMsg);	
	}
	
}
