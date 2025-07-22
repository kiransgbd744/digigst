package com.ey.advisory.gstnapi;

import com.ey.advisory.core.api.APIException;

public class AccessTokenExpiredException extends APIException {

	private static final long serialVersionUID = 1L;

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

}
