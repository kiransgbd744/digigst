package com.ey.advisory.einv.app.api;

public class InvalidAPIResponseException extends APIException {

	private static final long serialVersionUID = 1L;

	public InvalidAPIResponseException() {}

	public InvalidAPIResponseException(String message) {
		super(message);
	}

	public InvalidAPIResponseException(Throwable cause) {
		super(cause);
	}

	public InvalidAPIResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidAPIResponseException(String message, 
			Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
