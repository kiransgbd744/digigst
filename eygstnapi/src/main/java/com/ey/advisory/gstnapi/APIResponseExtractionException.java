package com.ey.advisory.gstnapi;

import com.ey.advisory.core.api.APIException;

public class APIResponseExtractionException extends APIException {

	private static final long serialVersionUID = 1L;

	public APIResponseExtractionException() {}

	public APIResponseExtractionException(String message) {
		super(message);
	}

	public APIResponseExtractionException(Throwable cause) {
		super(cause);
	}

	public APIResponseExtractionException(String message, Throwable cause) {
		super(message, cause);
	}

}
