package com.ey.advisory.app.docs.dto.gstr2;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2PRDataDto {
	
	@Expose
	@SerializedName("date")
	private LocalDate date;
	@Expose
	@SerializedName("uploadBy")
	private String uploadBy;
	@Expose
	@SerializedName("fileType")
	private String fileType;
	@Expose
	@SerializedName("fileName")
	private String fileName;
	@Expose
	@SerializedName("fileStatus")
	private String fileStatus;
	@Expose
	@SerializedName("total")
	private Integer total;
	@Expose
	@SerializedName("processASP")
	private Integer processASP;
	@Expose
	@SerializedName("errorASP")
	private Integer errorASP;
	@Expose
	@SerializedName("info")
	private Integer info;
	@Expose
	@SerializedName("status")
	private String status;
	@Expose
	@SerializedName("processGSTN")
	private Integer processGSTN;
	@Expose
	@SerializedName("errorGSTN")
	private Integer errorGSTN;
	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	/**
	 * @return the uploadBy
	 */
	public String getUploadBy() {
		return uploadBy;
	}
	/**
	 * @param uploadBy the uploadBy to set
	 */
	public void setUploadBy(String uploadBy) {
		this.uploadBy = uploadBy;
	}
	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}
	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the fileStatus
	 */
	public String getFileStatus() {
		return fileStatus;
	}
	/**
	 * @param fileStatus the fileStatus to set
	 */
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	/**
	 * @return the total
	 */
	public Integer getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(Integer total) {
		this.total = total;
	}
	/**
	 * @return the processASP
	 */
	public Integer getProcessASP() {
		return processASP;
	}
	/**
	 * @param processASP the processASP to set
	 */
	public void setProcessASP(Integer processASP) {
		this.processASP = processASP;
	}
	/**
	 * @return the errorASP
	 */
	public Integer getErrorASP() {
		return errorASP;
	}
	/**
	 * @param errorASP the errorASP to set
	 */
	public void setErrorASP(Integer errorASP) {
		this.errorASP = errorASP;
	}
	/**
	 * @return the info
	 */
	public Integer getInfo() {
		return info;
	}
	/**
	 * @param info the info to set
	 */
	public void setInfo(Integer info) {
		this.info = info;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the processGSTN
	 */
	public Integer getProcessGSTN() {
		return processGSTN;
	}
	/**
	 * @param processGSTN the processGSTN to set
	 */
	public void setProcessGSTN(Integer processGSTN) {
		this.processGSTN = processGSTN;
	}
	/**
	 * @return the errorGSTN
	 */
	public Integer getErrorGSTN() {
		return errorGSTN;
	}
	/**
	 * @param errorGSTN the errorGSTN to set
	 */
	public void setErrorGSTN(Integer errorGSTN) {
		this.errorGSTN = errorGSTN;
	}
	
	

}
