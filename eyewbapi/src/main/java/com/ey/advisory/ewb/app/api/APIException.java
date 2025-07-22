package com.ey.advisory.ewb.app.api;

import com.ey.advisory.common.EWBException;

public class APIException extends EWBException {

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

	public APIException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
