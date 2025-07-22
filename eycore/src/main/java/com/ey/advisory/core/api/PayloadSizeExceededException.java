package com.ey.advisory.core.api;

public class PayloadSizeExceededException extends APIException {

	private static final long serialVersionUID = 1L;
	

	public PayloadSizeExceededException(String message) {
		super(message);
	}

	public PayloadSizeExceededException(Throwable cause) {
		super(cause);
	}

	public PayloadSizeExceededException(String message, Throwable cause) {
		super(message, cause);
	}

}
