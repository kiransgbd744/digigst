package com.ey.advisory.common.eyfileutils;

import com.ey.advisory.common.AppException;

public class FileProcessingException extends AppException {

	private static final long serialVersionUID = 1L;

	public FileProcessingException() {}

	public FileProcessingException(String message, String errCode) {
		super(message, errCode);
	}
	
	public FileProcessingException(String message) {
		super(message);
	}

	/**
	 * Not overloading this method with the one that takes an int errCode and
	 * a Throwable cause. This is to enforce that the caller always gives a 
	 * message when an exception is thrown.
	 * @param cause
	 */
	public FileProcessingException(Throwable cause) {
		super(cause);	
	}
	
	public FileProcessingException(String message, Throwable cause) {
		super(message, cause);	
	}
	
	public FileProcessingException(
			String message, Throwable cause, String errCode) {
		super(message, cause, errCode);	
	}	
}
