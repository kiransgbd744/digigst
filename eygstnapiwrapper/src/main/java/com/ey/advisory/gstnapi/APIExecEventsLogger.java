package com.ey.advisory.gstnapi;

/**
 * Logging of all intermediate events is one of the key aspects that should be
 * supported by the API wrapper framework. Since this framework shields the 
 * caller of the API from complexities like retries, tokens etc, it is necessary
 * to have a mechanism to keep the caller informed of the steps that are taken
 * by this framework to get the API executed and the events that arise thereby.
 * This interface is responsible to log either an error message or a success 
 * message to the log storage (which can be the DB). Implementations of this
 * interface can range from simple direct DB call to store the message to 
 * an asynchronous mechanism that accepts the messages in a queue and later 
 * dumps them to the DB as a background task. 
 * 
 * The callers of this API shouldn't assume this log call to be a part of their
 * transactions. Since the callers are supposed to log each and every minute 
 * details of execution, it should be expected that there is a huge number of
 * messages to be logged. Making the logger store the messages in a synchronous
 * mode can actually block the caller for a considerable amount of time, which
 * might not be desirable. Hence it's better to use an implementation that
 * stores the messages asynchronously.
 * 
 * The 'log' method of this interface will take care of attaching time stamps to
 * the submitted messages. 
 * 
 * @author Khalid1.Khan
 *
 */

@FunctionalInterface
public interface APIExecEventsLogger {
	
	public static final String MSGTYPE_ERR= "ERR";
	public static final String MSGTYPE_INFO = "INF";
	
	/**
	 * The core method responsible for logging the messages.
	 * @param reqId the invocation request id. Every message will be associated
	 * 		with an invocation request. 
	 * @param msgType the message type - ERR or INFO.
	 * @param msg the error message or the information message.
	 * @param errCode the error code if available. Certain errors like GSTN
	 * 		errors will have error codes associated with them. Storing these
	 * 		error codes will be useful for later analysis
	 */
	public void log(Long reqId, String msgType, String msg, 
			String errCode, Throwable th);
	
	/**
	 * This default method will invoke the log() method to store the messages.
	 * The message type will be set as ERR.
	 * 
	 * @param reqId the invocation request id. Every message will be associated
	 * 		with an invocation request.  
	 * @param msg the error message to be stored against the specified 
	 * 		invocation request id.
	 * @param errCode the error code if available. Certain errors like GSTN
	 * 		errors will have error codes associated with them. Storing these
	 * 		error codes will be useful for later analysis
	 */
	public default void logError(Long reqId, String errMsg, String errCode) {
		log(reqId, MSGTYPE_ERR, errMsg, errCode, null);
	}

	public default void logError(Long reqId, String errMsg, 
				String errCode, Exception ex) {
		log(reqId, MSGTYPE_ERR, errMsg, errCode, ex);
	}

	
	/**
	 * This default method will invoke the log() method to store the messages.
	 * The message type will be set to ERR. Use this method only if the
	 * invocation request Id itself is not available during processing. In this
	 * scenario, give as much detail as possible in the error message.
	 * 
	 * Note that if the invocation request id itself is not available, it 
	 * becomes extremely difficult to track the api execution flow. This 
	 * scenario usually happens when the serialized JSON is corrupted (e.g. 
	 * manual edits were made to the serialized JSON). 
	 * 
	 * In case the chain of API execution activities for a specific request 
	 * remains incomplete, then it is a good idea to check for error messages
	 * without a reqId.
	 * 
	 * @param errMsg The error message to be stored (without an invocation 
	 * 		request id)
	 * 
	 * @param errCode the error code if available. Certain errors like GSTN
	 * 		errors will have error codes associated with them. Storing these
	 * 		error codes will be useful for later analysis
	 */
	public default void logError(String errMsg, String errCode) {
		log(null, MSGTYPE_ERR, errMsg, errCode, null);
	}
	
	public default void logError(String errMsg, String errCode, Exception ex) {
		log(null, MSGTYPE_ERR, errMsg, errCode, ex);
	}
	
	/**
	 * This default method will invoke the log() method to store the messages.
	 * The message type will be set as INFO and the errCode is set as null.
	 * 
	 * @param reqId the invocation request id. Every message will be associated
	 * 		with an invocation request.
	 * @param msg the information message to be stored for the specified
	 * 		invocation request id.
	 */
	public default void logInfo(Long reqId, String infoMsg) {
		log(reqId, MSGTYPE_INFO, infoMsg, null, null);
	}
	
}
