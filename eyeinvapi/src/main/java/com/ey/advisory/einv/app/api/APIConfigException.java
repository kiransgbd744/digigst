package com.ey.advisory.einv.app.api;

public class APIConfigException extends APIException {

	private static final long serialVersionUID = 1L;

	public APIConfigException() {}

	public APIConfigException(String message) {
		super(message);
	}

	public APIConfigException(Throwable cause) {
		super(cause);
	}

	public APIConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public APIConfigException(String message, Throwable cause, 
				boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
