package com.ey.advisory.ewb.app.api;

public class AccessTokenExpiredException extends APIException {

	public AccessTokenExpiredException() {}

	public AccessTokenExpiredException(String message) {
		super(message);
	}

	public AccessTokenExpiredException(Throwable cause) {
		super(cause);
	}

	public AccessTokenExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

	public AccessTokenExpiredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
