package com.ey.advisory.gstnapi;

import com.ey.advisory.core.api.APIException;

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

}
