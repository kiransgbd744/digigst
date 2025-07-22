package com.ey.advisory.gstnapi;

import com.ey.advisory.core.api.APIException;

public class AuthTokenExpiredException extends APIException {

	private static final long serialVersionUID = 1L;

	public AuthTokenExpiredException() {}

	public AuthTokenExpiredException(String message) {
		super(message);
	}

	public AuthTokenExpiredException(Throwable cause) {
		super(cause);
	}

	public AuthTokenExpiredException(String message, Throwable cause) {
		super(message, cause);
	}

}
