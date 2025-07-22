package com.ey.advisory.common;

public class EWBException extends AppException {

	private static final long serialVersionUID = 1L;
	
	protected String errCode;
	
	public EWBException() {}

	public EWBException(String message) {
		super(message);
	}
	
	public EWBException(String message, String errCode) {
		super(message);
		this.errCode = errCode;
	}	
	
	public EWBException(Throwable cause) {
		super(cause);	
	}
	
	public EWBException(String message, Throwable cause) {
		super(message, cause);	
	}
	
	public EWBException(String message, String errCode, Throwable cause) {
		super(message, cause);
		this.errCode = errCode;
	}

	public EWBException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EWBException(String message, String errCode, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.errCode = errCode;
	}
	
	/**
	 * Returns true if and only if a valid error code is set to this
	 * exception. 
	 * 
	 * @return
	 */
	public boolean hasErrCode() {
		return (this.errCode != null) && !this.errCode.trim().isEmpty();
	}
	
	public String getErrCode() {
		return this.errCode;
	}
}
