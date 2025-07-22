package com.ey.advisory.core.api;

public class ApiCallLimitExceededException extends APIException {

	private static final long serialVersionUID = 1L;

	public ApiCallLimitExceededException(String message) {
		super(message);
	}

	public ApiCallLimitExceededException(Throwable cause) {
		super(cause);
	}

	public ApiCallLimitExceededException(String message, Throwable cause) {
		super(message, cause);
	}

}
