package com.ey.advisory.app.docs.dto;
/**
 * @author Laxmi.Salukuti
 *
 */

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anx1VerticalDownloadReportsReqDto extends SearchCriteria {

	public Anx1VerticalDownloadReportsReqDto() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("dataType")
	private String dataType;
	@Expose
	@SerializedName("fileId")
	private Long fileId;
	@Expose
	@SerializedName("type")
	private String type;
	@Expose
	@SerializedName("status")
	private String status;
	@Expose
	@SerializedName("fileType")
	private String fileType;


	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Override
	public String toString() {
		return "Anx1VerticalDownloadReportsReqDto [dataType=" + dataType
				+ ", fileId=" + fileId + ", type=" + type + ", status=" + status
				+ ", fileType=" + fileType + "]";
	}

	
}