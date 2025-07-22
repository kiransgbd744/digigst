package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GstnDocSearchReqDto extends SearchCriteria {
	
	
	public GstnDocSearchReqDto() {
		super(SearchTypeConstants.DATA_STATUS_SEARCH);
	}

	@Expose
	@SerializedName("gstins")
	private List<String> gstins;

	@Expose
	@SerializedName("criteria")
	private String criteria;

	@Expose
	@SerializedName("receivFromDate")
	private LocalDate receivFromDate;

	@Expose
	@SerializedName("receivToDate")
	private LocalDate receivToDate;

	@Expose
	@SerializedName("docFromDate")
	private LocalDate docFromDate;

	@Expose
	@SerializedName("docToDate")
	private LocalDate docToDate;

	@Expose
	@SerializedName("returnFromDate")
	private String returnFrom;

	@Expose
	@SerializedName("returnToDate")
	private String returnTo;

	@Expose
	@SerializedName("docNo")
	private String docNo;
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("processingStatus")
	private String processingStatus; // This field accepts P/E/I. P-Processed,
										// E-Error, I-Info

	/**
	 * @return the gstins
	 */
	public List<String> getGstins() {
		return gstins;
	}

	/**
	 * @param gstins the gstins to set
	 */
	public void setGstins(List<String> gstins) {
		this.gstins = gstins;
	}

	/**
	 * @return the criteria
	 */
	public String getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	/**
	 * @return the receivFromDate
	 */
	public LocalDate getReceivFromDate() {
		return receivFromDate;
	}

	/**
	 * @param receivFromDate the receivFromDate to set
	 */
	public void setReceivFromDate(LocalDate receivFromDate) {
		this.receivFromDate = receivFromDate;
	}

	/**
	 * @return the receivToDate
	 */
	public LocalDate getReceivToDate() {
		return receivToDate;
	}

	/**
	 * @param receivToDate the receivToDate to set
	 */
	public void setReceivToDate(LocalDate receivToDate) {
		this.receivToDate = receivToDate;
	}

	/**
	 * @return the docFromDate
	 */
	public LocalDate getDocFromDate() {
		return docFromDate;
	}

	/**
	 * @param docFromDate the docFromDate to set
	 */
	public void setDocFromDate(LocalDate docFromDate) {
		this.docFromDate = docFromDate;
	}

	/**
	 * @return the docToDate
	 */
	public LocalDate getDocToDate() {
		return docToDate;
	}

	/**
	 * @param docToDate the docToDate to set
	 */
	public void setDocToDate(LocalDate docToDate) {
		this.docToDate = docToDate;
	}

	/**
	 * @return the returnFrom
	 */
	public String getReturnFrom() {
		return returnFrom;
	}

	/**
	 * @param returnFrom the returnFrom to set
	 */
	public void setReturnFrom(String returnFrom) {
		this.returnFrom = returnFrom;
	}

	/**
	 * @return the returnTo
	 */
	public String getReturnTo() {
		return returnTo;
	}

	/**
	 * @param returnTo the returnTo to set
	 */
	public void setReturnTo(String returnTo) {
		this.returnTo = returnTo;
	}

	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the entityId
	 */
	public List<Long> getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the processingStatus
	 */
	public String getProcessingStatus() {
		return processingStatus;
	}

	/**
	 * @param processingStatus the processingStatus to set
	 */
	public void setProcessingStatus(String processingStatus) {
		this.processingStatus = processingStatus;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GstnDocSearchReqDto [gstins=" + gstins + ", criteria="
				+ criteria + ", receivFromDate=" + receivFromDate
				+ ", receivToDate=" + receivToDate + ", docFromDate="
				+ docFromDate + ", docToDate=" + docToDate + ", returnFrom="
				+ returnFrom + ", returnTo=" + returnTo + ", docNo=" + docNo
				+ ", entityId=" + entityId + ", processingStatus="
				+ processingStatus + "]";
	}

		
}
