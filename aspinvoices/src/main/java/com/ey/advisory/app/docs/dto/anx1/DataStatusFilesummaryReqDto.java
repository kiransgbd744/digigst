package com.ey.advisory.app.docs.dto.anx1;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataStatusFilesummaryReqDto {

	@Expose
	@SerializedName("fileId")
	private List<Long> fileId = new ArrayList<>();

	@Expose
	@SerializedName("fileType")
	private String fileType;

	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();

	@Expose
	@SerializedName("dataType")
	private String dataType;

	@Expose
	@SerializedName("answer")
	private Integer answer;

	public Integer getAnswer() {
		return answer;
	}

	public void setAnswer(Integer answer) {
		this.answer = answer;
	}

	public List<Long> getFileId() {
		return fileId;
	}

	public void setFileId(List<Long> fileId) {
		this.fileId = fileId;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

}
