package com.ey.advisory.common;


/**
 * The base exception class for all EY applications. An additional error code
 * field is added to this exception class. Usually the exceptions raised
 * within internal applications will be displayed to the user. So,
 * instead of creating separate exceptions by sub-classing this, we can 
 * have a comprehensive list of error codes that the application can 
 * throw. Whenever an error is raised from within our components, create the
 * exception with the appropriate error code. But, if we are developing 
 * reusable components, where the user of the component will be different 
 * from the application for which the component is developed, then have 
 * another exception derived from this exception class to denote the 
 * specific scenarios, so that the calling application can catch that 
 * exception and assign that application specific error codes.
 *
 */
public class AppException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Adding an error code to the exception. Usually the exceptions raised
	 * within internal applications will usually be displayed to the user. So,
	 * instead of creating separate exceptions by sub-classing this, we can 
	 * have a comprehensive list of error codes that the application can 
	 * throw. Whenever an error is raised from within our components, create the
	 * exception with the appropriate error code. But, if we are developing 
	 * reusable components, where the user of the component will be different 
	 * from the application for which the component is developed, then have 
	 * another exception derived from this exception class to denote the 
	 * specific scenarios, so that the calling application can catch that 
	 * exception and assign that application specific error codes.
	 */
	private String errCode = ErrorConstants.UNKNOWN_ERROR;
	
	public AppException() {}

	public AppException(String message) {
		super(message);
	}

	/**
	 * Not overloading this method with the one that takes an int errCode and
	 * a Throwable cause. This is to enforce that the caller always gives a 
	 * message when an exception is thrown.
	 * @param cause
	 */
	public AppException(Throwable cause) {
		super(cause);	
	}
	
	public AppException(String message, Throwable cause) {
		super(message, cause);	
	}
		
	public AppException(String message, String errCode) {
		super(message);			
		this.errCode = errCode;
	}	
	
	public AppException(Throwable cause, String errCode) {
		super(AppException.enhanceErrMsg(
				cause.getMessage(), 
				cause.getCause(), 
				ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION), 
			cause.getCause());		
		
		this.errCode = errCode;
	}	
	
	public AppException(String message, Throwable cause, String errCode) {
		super(AppException.enhanceErrMsg(
				message, cause, 
				ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION), 
				cause);	
		this.errCode = errCode;
	}	
		
	/**
	 * This constructor alters and enhances the input error message by finding 
	 * the appropriate nested cause, extracting that error message and attaching
	 * it to the initial message. It also takes care of trimming the length of
	 * the message to ensure that the error message is only 
	 * ErrorConstants.ERR_MSG_STR_LEN long. This is to ensure that such a value
	 * can be stored in relational DB columns where the VARCHAR length is 
	 * configured to be this value.
	 * 
	 * @param message The input error message passed by the developer.
	 * @param cause The actual error that was the reason for 
	 * 				creating this exception
	 * @param strategy The Error message Enhancement Strategy to use for
	 * 		locating the error message.
	 */
	public AppException(String message, 
				Throwable cause, ErrMsgEnhancementStrategy strategy) {
		super(AppException.enhanceErrMsg(message, cause, strategy), cause);	
	}
	
	public AppException(Throwable cause, ErrMsgEnhancementStrategy strategy) {
		super(AppException.enhanceErrMsg(cause.getMessage(), 
					cause.getCause(), strategy), cause.getCause());			
	}		
	
	public AppException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	/**
	 * Exception 
	 * 
	 * @param cause
	 * @param errCode
	 * @param strategy
	 */
	public AppException(Throwable cause, String errCode, 
				ErrMsgEnhancementStrategy strategy) {
		super(AppException.enhanceErrMsg(cause.getMessage(), 
					cause.getCause(), strategy), cause.getCause());			
		this.errCode = errCode;
	}				

	public AppException(String message, Throwable cause, String errCode, 
			ErrMsgEnhancementStrategy strategy) {
		super(AppException.enhanceErrMsg(message, cause, strategy), cause);			
		this.errCode = errCode;
	}			
	
	
	/**
	 * This static method is required as we cannot include these lines within 
	 * the AppException constructor, before the super call (as super should be
	 * the first call within the constructor) 
	 * 
	 * @param errMsg The input error message passed by the developer.
	 * @param cause The actual error that was the reason for 
	 * 				creating this exception
	 * @param strategy The Error message Enhancement Strategy to use for
	 * 		locating the error message.
	 * 
	 * @return The enhanced error message string.
	 */
	private static String enhanceErrMsg(String errMsg, Throwable cause, 
			ErrMsgEnhancementStrategy strategy) {
		ErrMsgEnhancer enhancer = ErrMsgEnhancerFactory.getErrMsgEnhancer(
						strategy);
		return enhancer.enhanceErrorMessage(errMsg, cause, strategy);
	}
	
	/**
	 * Return the Error Code within the exception.
	 * 
	 * @return the error code
	 */
	public String getErrCode() {
		return this.errCode;
	}
	
	/**
	 * Returns true if and only if a valid error code is set to this
	 * exception. 
	 * 
	 * @return
	 */
	public boolean hasErrCode() {
		return (this.errCode != ErrorConstants.UNKNOWN_ERROR);
	}
}
