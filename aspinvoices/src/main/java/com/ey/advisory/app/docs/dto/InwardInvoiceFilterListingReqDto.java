/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component
@Data
public class InwardInvoiceFilterListingReqDto extends SearchCriteria {

	public InwardInvoiceFilterListingReqDto() {
		super(SearchTypeConstants.DOC_SEARCH);
	}

	// This flag is to show data based on the clicked Tab (ASP/GSTN) in Invoice
	// Management Screen
	@Expose
	@SerializedName("showGstnData")
	protected boolean showGstnData;

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
	@SerializedName("docNums")
	private List<String> docNums;
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;

	@Expose
	@SerializedName("processingStatus")
	private String processingStatus; // This field accepts P/E/I. P-Processed,
										// E-Error, I-Info

	@Expose
	@SerializedName("fileId")
	private Long fileId;

	@Expose
	@SerializedName("dataOriginTypeCode")
	private String dataOriginTypeCode; // A-API, E-Excel Upload

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("docType")
	private List<String> docTypes;

	@Expose
	@SerializedName("counterPartyGstin")
	private String counterPartyGstins;

	@Expose
	@SerializedName("refId")
	private String gstnRefId;// GSTN Batch Id
	
	@Expose
	@SerializedName("gstReturnsStatus")
	private List<String> gstReturnsStatus; // GSTReturns Status - NotApplicable,
	                                        //AspError,AspprocessedNotSaved,
	                                         // AspprocessedSaved,GstnError
	@Expose
	@SerializedName("supplyType")
	private List<String> supplyType;
	
	@Expose
	@SerializedName("postingDate")
	private LocalDate postingDate;
	
	@Expose
	@SerializedName("gstReturn")        //gstr2/gstr6
	private String gstReturn;

	@Expose
	@SerializedName("suppGstin")
	private String suppGstin ;
	
	/*public String getSuppGstin() {
		return suppGstin;
	}

	public void setSuppGstin(String suppGstin) {
		this.suppGstin = suppGstin;
	}

	public String getAccVoucherNo() {
		return accVoucherNo;
	}

	public void setAccVoucherNo(String accVoucherNo) {
		this.accVoucherNo = accVoucherNo;
	}
*/
	@Expose
	@SerializedName("accVoucherNo")
	private List<String> accVoucherNo;
}
	/**
	 * @return the showGstnData
	 *//*
	public boolean isShowGstnData() {
		return showGstnData;
	}

	*//**
	 * @param showGstnData the showGstnData to set
	 *//*
	public void setShowGstnData(boolean showGstnData) {
		this.showGstnData = showGstnData;
	}

	*//**
	 * @return the criteria
	 *//*
	public String getCriteria() {
		return criteria;
	}

	*//**
	 * @param criteria the criteria to set
	 *//*
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	*//**
	 * @return the receivFromDate
	 *//*
	public LocalDate getReceivFromDate() {
		return receivFromDate;
	}

	*//**
	 * @param receivFromDate the receivFromDate to set
	 *//*
	public void setReceivFromDate(LocalDate receivFromDate) {
		this.receivFromDate = receivFromDate;
	}

	*//**
	 * @return the receivToDate
	 *//*
	public LocalDate getReceivToDate() {
		return receivToDate;
	}

	*//**
	 * @param receivToDate the receivToDate to set
	 *//*
	public void setReceivToDate(LocalDate receivToDate) {
		this.receivToDate = receivToDate;
	}

	*//**
	 * @return the docFromDate
	 *//*
	public LocalDate getDocFromDate() {
		return docFromDate;
	}

	*//**
	 * @param docFromDate the docFromDate to set
	 *//*
	public void setDocFromDate(LocalDate docFromDate) {
		this.docFromDate = docFromDate;
	}

	*//**
	 * @return the docToDate
	 *//*
	public LocalDate getDocToDate() {
		return docToDate;
	}

	*//**
	 * @param docToDate the docToDate to set
	 *//*
	public void setDocToDate(LocalDate docToDate) {
		this.docToDate = docToDate;
	}

	*//**
	 * @return the returnFrom
	 *//*
	public String getReturnFrom() {
		return returnFrom;
	}

	*//**
	 * @param returnFrom the returnFrom to set
	 *//*
	public void setReturnFrom(String returnFrom) {
		this.returnFrom = returnFrom;
	}

	*//**
	 * @return the returnTo
	 *//*
	public String getReturnTo() {
		return returnTo;
	}

	*//**
	 * @param returnTo the returnTo to set
	 *//*
	public void setReturnTo(String returnTo) {
		this.returnTo = returnTo;
	}

	*//**
	 * @return the docNo
	 *//*
	public String getDocNo() {
		return docNo;
	}

	*//**
	 * @param docNo the docNo to set
	 *//*
	public void List<string> setDocNo(String docNo) {
		this.docNo = docNo;
	}

	*//**
	 * @return the entityId
	 *//*
	public List<Long> getEntityId() {
		return entityId;
	}

	*//**
	 * @param entityId the entityId to set
	 *//*
	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	*//**
	 * @return the processingStatus
	 *//*
	public String getProcessingStatus() {
		return processingStatus;
	}

	*//**
	 * @param processingStatus the processingStatus to set
	 *//*
	public void setProcessingStatus(String processingStatus) {
		this.processingStatus = processingStatus;
	}

	*//**
	 * @return the fileId
	 *//*
	public Long getFileId() {
		return fileId;
	}

	*//**
	 * @param fileId the fileId to set
	 *//*
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	*//**
	 * @return the dataOriginTypeCode
	 *//*
	public String getDataOriginTypeCode() {
		return dataOriginTypeCode;
	}

	*//**
	 * @param dataOriginTypeCode the dataOriginTypeCode to set
	 *//*
	public void setDataOriginTypeCode(String dataOriginTypeCode) {
		this.dataOriginTypeCode = dataOriginTypeCode;
	}

	*//**
	 * @return the dataSecAttrs
	 *//*
	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	*//**
	 * @param dataSecAttrs the dataSecAttrs to set
	 *//*
	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	*//**
	 * @return the docTypes
	 *//*
	public List<String> getDocTypes() {
		return docTypes;
	}

	*//**
	 * @param docTypes the docTypes to set
	 *//*
	public void setDocTypes(List<String> docTypes) {
		this.docTypes = docTypes;
	}

	*//**
	 * @return the counterPartyGstins
	 *//*
	public String getCounterPartyGstins() {
		return counterPartyGstins;
	}

	*//**
	 * @param counterPartyGstins the counterPartyGstins to set
	 *//*
	public void setCounterPartyGstins(String counterPartyGstins) {
		this.counterPartyGstins = counterPartyGstins;
	}

	*//**
	 * @return the gstnRefId
	 *//*
	public String getGstnRefId() {
		return gstnRefId;
	}

	*//**
	 * @param gstnRefId the gstnRefId to set
	 *//*
	public void setGstnRefId(String gstnRefId) {
		this.gstnRefId = gstnRefId;
	}

	*//**
	 * @return the gstReturnsStatus
	 *//*
	public List<String> getGstReturnsStatus() {
		return gstReturnsStatus;
	}

	*//**
	 * @param gstReturnsStatus the gstReturnsStatus to set
	 *//*
	public void setGstReturnsStatus(List<String> gstReturnsStatus) {
		this.gstReturnsStatus = gstReturnsStatus;
	}

	*//**
	 * @return the supplyType
	 *//*
	public List<String> getSupplyType() {
		return supplyType;
	}

	*//**
	 * @param supplyType the supplyType to set
	 *//*
	public void setSupplyType(List<String> supplyType) {
		this.supplyType = supplyType;
	}

	*//**
	 * @return the postingDate
	 *//*
	public LocalDate getPostingDate() {
		return postingDate;
	}

	*//**
	 * @param postingDate the postingDate to set
	 *//*
	public void setPostingDate(LocalDate postingDate) {
		this.postingDate = postingDate;
	}

	*//**
	 * @return the gstReturn
	 *//*
	public String getGstReturn() {
		return gstReturn;
	}

	*//**
	 * @param gstReturn the gstReturn to set
	 *//*
	public void setGstReturn(String gstReturn) {
		this.gstReturn = gstReturn;
	}
	
}

*/