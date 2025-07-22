package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

/**
 * @author kiran
 *
 */
public class APIFileNilRespDto {

	/**
	 * 
	 */
	public static final String SUCCESS_STATUS = "S";
	/**
	 * 
	 */
	public static final String ERROR_STATUS = "E";

	@Expose
	protected String status;

	@Expose
	protected String resp;

	/**
	 * @param status
	 * @param resp
	 */
	public APIFileNilRespDto(String status, String resp) {
		super();
		this.status = status;
		this.resp = resp;
	}

	/**
	 * @return
	 */
	public static APIFileNilRespDto createSuccessResp() {
		return new APIFileNilRespDto(SUCCESS_STATUS, null);
	}

	/**
	 * @return
	 */
	public static APIFileNilRespDto creatErrorResp() {
		return new APIFileNilRespDto(ERROR_STATUS, null);
	}

	/**
	 * @return the successStatus
	 */
	public static String getSuccessStatus() {
		return SUCCESS_STATUS;
	}

	/**
	 * @return the errorStatus
	 */
	public static String getErrorStatus() {
		return ERROR_STATUS;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the resp
	 */
	public String getresp() {
		return resp;
	}

}
