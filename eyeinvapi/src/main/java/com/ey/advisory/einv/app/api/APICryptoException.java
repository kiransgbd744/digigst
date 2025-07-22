package com.ey.advisory.einv.app.api;

public class APICryptoException extends APIException {

	private static final long serialVersionUID = 1L;

	public APICryptoException() {}

	public APICryptoException(String message) {
		super(message);
	}

	public APICryptoException(Throwable cause) {
		super(cause);
	}

	public APICryptoException(String message, Throwable cause) {
		super(message, cause);
	}

	public APICryptoException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
