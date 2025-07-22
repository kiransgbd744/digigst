package com.ey.advisory.app.docs.dto.gstr2;

import java.time.LocalDate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2AspErrorDto {
	
	@Expose
	@SerializedName("supplier GSTIN")
	private String gstin;
	@Expose
	@SerializedName("docType")
	private String docType;
	@Expose
	@SerializedName("docNum")
	private String docNum;
	@Expose
	@SerializedName("docDate")
	private LocalDate docDate;
	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;
	@Expose
	@SerializedName("errorCode")
	private String errorCode;
	
	@Expose
	@SerializedName("recipientGStin")
	private String recipientGStin;
	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}
	/**
	 * @param gstin the gstin to set
	 */
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}
	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}
	/**
	 * @return the docNum
	 */
	public String getDocNum() {
		return docNum;
	}
	/**
	 * @param docNum the docNum to set
	 */
	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}
	/**
	 * @return the docDate
	 */
	public LocalDate getDocDate() {
		return docDate;
	}
	/**
	 * @param docDate the docDate to set
	 */
	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}
	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return returnPeriod;
	}
	/**
	 * @param returnPeriod the returnPeriod to set
	 */
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the recipientGStin
	 */
	public String getRecipientGStin() {
		return recipientGStin;
	}
	/**
	 * @param recipientGStin the recipientGStin to set
	 */
	public void setRecipientGStin(String recipientGStin) {
		this.recipientGStin = recipientGStin;
	}
	
	
	
	

}
