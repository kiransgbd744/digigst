package com.ey.advisory.core.dto;

import com.google.gson.annotations.Expose;

/**
 * @author Umesha.M
 *
 */
public class APIRespDto {
	
	/**
	 * 
	 */
	public static final String SUCCESS_STATUS = "S";
	/**
	 * 
	 */
	public static final String ERROR_STATUS = "E";
	
	@Expose
	protected Integer totalCount;
	
	@Expose
	protected Integer pageNum;
	
	@Expose
	protected Integer pageSize;
	
	@Expose
	protected String status;
	
	@Expose
	protected String message;
	
	/**
	 * @param totalCount
	 * @param pageNum
	 * @param pageSize
	 * @param status
	 * @param message
	 */

	public APIRespDto(Integer totalCount, Integer pageNum, Integer pageSize,
			String status, String message) {
		super();
		this.totalCount = totalCount;
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.status = status;
		this.message = message;
	}

	/**
	 * @param status
	 * @param message
	 */
	public APIRespDto(String status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	
	/**
	 * @return
	 */
	public static APIRespDto createSuccessResp() {
		return new APIRespDto(SUCCESS_STATUS, null);
	}
	
	/**
	 * @param totalCount
	 * @param pageNum
	 * @param pageSize
	 * @param status
	 * @param message
	 * @return
	 */
	public static APIRespDto createSuccessResp(Integer totalCount,
			Integer pageNum, Integer pageSize, String status, String message) {
		return new APIRespDto(totalCount, pageNum, pageSize, SUCCESS_STATUS,
				null);
	}
	
	/**
	 * @return
	 */
	public static APIRespDto creatErrorResp() {
		return new APIRespDto(ERROR_STATUS, null);
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
	 * @return the pageNum
	 */
	public Integer getPageNum() {
		return pageNum;
	}

	/**
	 * @return the pageSize
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the totalCount
	 */
	public Integer getTotalCount() {
		return totalCount;
	}
	
}
