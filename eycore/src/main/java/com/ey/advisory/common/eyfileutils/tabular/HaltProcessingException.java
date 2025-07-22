package com.ey.advisory.common.eyfileutils.tabular;

import com.ey.advisory.common.AppException;

public class HaltProcessingException extends AppException {

	private static final long serialVersionUID = 1L;

	public HaltProcessingException() {}

	public HaltProcessingException(String message) {
		super(message);
	}

	public HaltProcessingException(Exception e) {
		super(e);
	}

	public HaltProcessingException(String message, Exception e) {
		super(message, e);
	}

}
