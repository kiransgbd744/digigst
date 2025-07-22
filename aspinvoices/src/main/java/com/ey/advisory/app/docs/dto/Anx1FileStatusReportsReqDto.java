package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anx1FileStatusReportsReqDto extends SearchCriteria {

	public Anx1FileStatusReportsReqDto() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	@Expose
	@SerializedName("uploadOn")
	private LocalDate uploadOn;
	@Expose
	@SerializedName("dataType")
	private String dataType;
	@Expose
	@SerializedName("fileType")
	private String fileType;
	@Expose
	@SerializedName("fileId")
	private Long fileId;
	@Expose
	@SerializedName("fileName")
	private String fileName;
	@Expose
	@SerializedName("type")
	private String type;
	@Expose
	@SerializedName("status")
	private String status;
	@Expose
	@SerializedName("errorType")
	private String errorType;
	@Expose
	@SerializedName("answer")
	private Integer answer;

	/**
	 * @return the entityId
	 */
	public List<Long> getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId
	 *            the entityId to set
	 */
	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the uploadOn
	 */
	public LocalDate getUploadOn() {
		return uploadOn;
	}

	/**
	 * @param uploadOn
	 *            the uploadOn to set
	 */
	public void setUploadOn(LocalDate uploadOn) {
		this.uploadOn = uploadOn;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType
	 *            the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the fileId
	 */
	public Long getFileId() {
		return fileId;
	}

	/**
	 * @param fileId
	 *            the fileId to set
	 */
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the errorType
	 */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType
	 *            the errorType to set
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * @return the answer
	 */
	public Integer getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(Integer answer) {
		this.answer = answer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Anx1FileStatusReportsReqDto [entityId=" + entityId
				+ ", uploadOn=" + uploadOn + ", dataType=" + dataType
				+ ", fileType=" + fileType + ", fileId=" + fileId
				+ ", fileName=" + fileName + ", type=" + type + ", status="
				+ status + ", errorType=" + errorType + ", answer=" + answer
				+ "]";
	}

}