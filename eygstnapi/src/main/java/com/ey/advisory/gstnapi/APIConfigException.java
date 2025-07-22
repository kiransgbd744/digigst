package com.ey.advisory.gstnapi;

import com.ey.advisory.core.api.APIException;

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

}
