package com.ey.advisory.common;

public class DefaultErrMsgEnchancer implements ErrMsgEnhancer {

	@Override
	public String enhanceErrorMessage(String errMsg, Throwable cause,
				ErrMsgEnhancementStrategy strategy) {
		
		if (strategy == ErrMsgEnhancementStrategy.NO_CHANGE) {
			// Trim the exception to maxLen characters and return.
			return trimMessage(errMsg);
		}
		
		boolean appendCause = (strategy != ErrMsgEnhancementStrategy.NO_CHANGE);
		
		return frameMessage(
				errMsg, cause, appendCause, true);
	}
	
	private Throwable findFirstValidNestedCause(Throwable ex, 
			boolean ignoreNestedAppExceptions) {
	
		// If the exception itself is null, then return null and terminate the
		// process.
		if (ex == null) return null;
		
		// If the specified exception is null or if it is already a non-app
		// exception break the recursive call stack by returning the same 
		// exception.
		if (!(ex instanceof AppException)) {
			return ex;
		}
		
		// Get the cause of the exception.
		Throwable cause = ex.getCause();
		if(cause == null) {
			// If the cause of this exception is null, it implies that this
			// exception was thrown without any nested Throwables. Probably, 
			// this exception was raised by the handling module itself.
			// In this scenario, return the last AppException itself as the
			// cause.
			return ex;
		}
				
		// Recursively locate the non-app exception. This will most probably
		// contain the root cause of why the exception occurred in the first
		// place.
		return findFirstValidNestedCause(
				ex.getCause(), ignoreNestedAppExceptions);
	}

	private String frameMessage(
			String message, Throwable ex, boolean appendCause, 
			boolean ignoreNestedAppExceptions) {
		
		// If the caller explicitly mentions not to append anything to the
		// input message, then return the input message itself.
		if (!appendCause) return message;
		
		// Otherwise, find the first non AppException instance from the nested
		// throwables, if any.
		Throwable t = findFirstValidNestedCause(ex, ignoreNestedAppExceptions);
		
		// If we're unable to locate a non-app exception, then return the
		// input message.
		if (t == null) return message;
	
		String msg = t.getMessage();
		
		// Not sure if an exception message can be null. If not, we need to
		// remove this line.
		if (msg == null) return message;
				
		// Concatenate the input message and the message of the first 
		// non-app nested exception. 
		String errMsg =  String.format("%s Nested Cause => [%s] %s", 
				(message == null) ? "" : message.trim(), 
						t.getClass().getSimpleName(), t.getMessage());	
						
		// If the final error message has more than the maximum characters for
		// an error message, then trim it to 1024 characters.
		return trimMessage(errMsg);
	}	

	/**
	 * Trim the error message  
	 * 
	 * @param errMsg
	 * @return
	 */
	private String trimMessage(String errMsg) {
		return errMsg.length() > ErrorConstants.ERR_MSG_STR_LEN ? 
				errMsg.substring(0, ErrorConstants.ERR_MSG_STR_LEN) : errMsg;		
	}
}
