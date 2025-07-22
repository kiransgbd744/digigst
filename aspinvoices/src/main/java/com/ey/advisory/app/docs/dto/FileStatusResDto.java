package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;

public class FileStatusResDto {

	private LocalDate date;
	private String uploadBy;
	private String fileType;
	private String fileName;
	private String fileStatus;
	private Integer total;
	private Integer processed;
	private Integer aspError;
	private Integer Info;
	private String status;
	private Integer gstnProcessed;
	private Integer gstnError;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getUploadBy() {
		return uploadBy;
	}

	public void setUploadBy(String uploadBy) {
		this.uploadBy = uploadBy;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getProcessed() {
		return processed;
	}

	public void setProcessed(Integer processed) {
		this.processed = processed;
	}

	public Integer getAspError() {
		return aspError;
	}

	public void setAspError(Integer aspError) {
		this.aspError = aspError;
	}

	public Integer getInfo() {
		return Info;
	}

	public void setInfo(Integer info) {
		Info = info;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getGstnProcessed() {
		return gstnProcessed;
	}

	public void setGstnProcessed(Integer gstnProcessed) {
		this.gstnProcessed = gstnProcessed;
	}

	public Integer getGstnError() {
		return gstnError;
	}

	public void setGstnError(Integer gstnError) {
		this.gstnError = gstnError;
	}

	@Override
	public String toString() {
		return "FileStatusResDto [date=" + date + ", uploadBy=" + uploadBy
				+ ", fileType=" + fileType + ", fileName=" + fileName
				+ ", fileStatus=" + fileStatus + ", total=" + total
				+ ", processed=" + processed + ", aspError=" + aspError
				+ ", Info=" + Info + ", status=" + status + ", gstnProcessed="
				+ gstnProcessed + ", gstnError=" + gstnError + "]";
	}

}
