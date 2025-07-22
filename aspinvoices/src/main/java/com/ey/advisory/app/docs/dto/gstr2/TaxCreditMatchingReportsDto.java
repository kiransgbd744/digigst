package com.ey.advisory.app.docs.dto.gstr2;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxCreditMatchingReportsDto {
	
	@Expose
	@SerializedName("sno")
	private Long sno;
	@Expose
	@SerializedName("requestId")
	private String requestId;
	@Expose
	@SerializedName("noOfGstins")
	private Integer noOfGstins;
	@Expose
	@SerializedName("fromTaxPeriod")
	private String fromTaxPeriod;
	@Expose
	@SerializedName("toTaxPeriod")
	private String toTaxPeriod;
	@Expose
	@SerializedName("initiation")
	private LocalDateTime initiation;
	@Expose
	@SerializedName("initiatedBy")
	private String initiatedBy;
	@Expose
	@SerializedName("completion")
	private LocalDateTime completion;
	/**
	 * @return the sno
	 */
	public Long getSno() {
		return sno;
	}
	/**
	 * @param sno the sno to set
	 */
	public void setSno(Long sno) {
		this.sno = sno;
	}
	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return requestId;
	}
	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	/**
	 * @return the noOfGstins
	 */
	public Integer getNoOfGstins() {
		return noOfGstins;
	}
	/**
	 * @param noOfGstins the noOfGstins to set
	 */
	public void setNoOfGstins(Integer noOfGstins) {
		this.noOfGstins = noOfGstins;
	}
	/**
	 * @return the fromTaxPeriod
	 */
	public String getFromTaxPeriod() {
		return fromTaxPeriod;
	}
	/**
	 * @param fromTaxPeriod the fromTaxPeriod to set
	 */
	public void setFromTaxPeriod(String fromTaxPeriod) {
		this.fromTaxPeriod = fromTaxPeriod;
	}
	/**
	 * @return the toTaxPeriod
	 */
	public String getToTaxPeriod() {
		return toTaxPeriod;
	}
	/**
	 * @param toTaxPeriod the toTaxPeriod to set
	 */
	public void setToTaxPeriod(String toTaxPeriod) {
		this.toTaxPeriod = toTaxPeriod;
	}
	/**
	 * @return the initiation
	 */
	public LocalDateTime getInitiation() {
		return initiation;
	}
	/**
	 * @param initiation the initiation to set
	 */
	public void setInitiation(LocalDateTime initiation) {
		this.initiation = initiation;
	}
	/**
	 * @return the initiatedBy
	 */
	public String getInitiatedBy() {
		return initiatedBy;
	}
	/**
	 * @param initiatedBy the initiatedBy to set
	 */
	public void setInitiatedBy(String initiatedBy) {
		this.initiatedBy = initiatedBy;
	}
	/**
	 * @return the completion
	 */
	public LocalDateTime getCompletion() {
		return completion;
	}
	/**
	 * @param completion the completion to set
	 */
	public void setCompletion(LocalDateTime completion) {
		this.completion = completion;
	}
	

}
