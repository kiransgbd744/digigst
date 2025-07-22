package com.ey.advisory.core.api;

import com.ey.advisory.common.AppException;

public class APIException extends AppException {

	private static final long serialVersionUID = 1L;

	public APIException() {}

	public APIException(String message) {
		super(message);
	}

	public APIException(Throwable cause) {
		super(cause);
	}

	public APIException(String message, Throwable cause) {
		super(message, cause);
	}

}
