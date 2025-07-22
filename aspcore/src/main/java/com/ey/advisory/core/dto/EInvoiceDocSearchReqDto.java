/**
 * 
 */
package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * DocErrorDetailDto class is responsible for transferring search data request
 * from Error Correction UI screen to screen
 *
 * @author Laxmi.Salukuti
 */
@Component
public class EInvoiceDocSearchReqDto extends SearchCriteria {

	public EInvoiceDocSearchReqDto() {
		super(SearchTypeConstants.DOC_SEARCH);
	}

	/*
	 * @Expose
	 * 
	 * @SerializedName("gstins") private List<String> gstins;
	 */

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
	@SerializedName("gstnStatus")
	private List<String> gstnStatus;// Saved,NotSaved,Error,Submitted

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
	@SerializedName("returnType")
	private List<String> returnTypes;

	@Expose
	@SerializedName("dataCategory")
	private List<String> dataCategoryList;

	@Expose
	@SerializedName("tableNumber")
	private List<String> tableNumberList;

	@Expose
	@SerializedName("counterPartyGstin")
	private String counterPartyGstins;

	@Expose
	@SerializedName("refId")
	private String gstnRefId;// GSTN Batch Id

	@Expose
	@SerializedName("dateEACKFrom")
	private LocalDate dateEACKFrom;

	@Expose
	@SerializedName("dateEACKTo")
	private LocalDate dateEACKTo;

	@Expose
	@SerializedName("counterPartyFlag")
	private boolean counterPartyFlag;

	@Expose
	@SerializedName("ewbNo")
	private Long ewbNo;

	@Expose
	@SerializedName("ewbDate")
	private LocalDateTime ewbDate;

	@Expose
	@SerializedName("gstReturnsStatus")
	private List<String> gstReturnsStatus; // GSTReturns Status - NotApplicable,
											// AspError,AspprocessedNotSaved,
											// AspprocessedSaved,GstnError

	@Expose
	@SerializedName("ewbStatus")
	private List<Long> ewbStatus;

	@Expose
	@SerializedName("ewbErrorPoint")
	private List<Long> ewbErrorPoint;

	@Expose
	@SerializedName("ewbCancellation")
	private List<String> ewbCancellation;

	@Expose
	@SerializedName("subSupplyType")
	private List<String> subSupplyType;

	@Expose
	@SerializedName("supplyType")
	private List<String> supplyType;

	@Expose
	@SerializedName("transType")
	private String transType;

	@Expose
	@SerializedName("postingDate")
	private LocalDate postingDate;

	@Expose
	@SerializedName("transporterID")
	private String transporterID;

	@Expose
	@SerializedName("ewbValidUpto")
	private LocalDateTime ewbValidUpto;

	@Expose
	@SerializedName("einvStatus")
	private List<Long> einvStatus;
	
	@Expose
	@SerializedName("accVoucherNum")
	private List<String> accVoucherNum;
	
	@Expose
	@SerializedName("irnNum")
	private String irnNum;
	
	@Expose
	@SerializedName("gstReturn")
	private String gstReturn;
	
	@Expose
	@SerializedName("type")
	private String type;
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGstReturn() {
		return gstReturn;
	}

	public void setGstReturn(String gstReturn) {
		this.gstReturn = gstReturn;
	}

	@Expose
	@SerializedName("suppGstin")
	private String suppGstin;
	
	public String getSuppGstin() {
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

	@Expose
	@SerializedName("accVoucherNo")
	private String accVoucherNo;
	

	
	public String getIrnNum() {
		return irnNum;
	}

	public void setIrnNum(String irnNum) {
		this.irnNum = irnNum;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public LocalDate getReceivFromDate() {
		return receivFromDate;
	}

	public void setReceivFromDate(LocalDate receivFromDate) {
		this.receivFromDate = receivFromDate;
	}

	public LocalDate getReceivToDate() {
		return receivToDate;
	}

	public void setReceivToDate(LocalDate receivToDate) {
		this.receivToDate = receivToDate;
	}

	public LocalDate getDocFromDate() {
		return docFromDate;
	}

	public void setDocFromDate(LocalDate docFromDate) {
		this.docFromDate = docFromDate;
	}

	public LocalDate getDocToDate() {
		return docToDate;
	}

	public void setDocToDate(LocalDate docToDate) {
		this.docToDate = docToDate;
	}

	public String getReturnFrom() {
		return returnFrom;
	}

	public void setReturnFrom(String returnFrom) {
		this.returnFrom = returnFrom;
	}

	public String getReturnTo() {
		return returnTo;
	}

	public void setReturnTo(String returnTo) {
		this.returnTo = returnTo;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	
	public List<String> getDocNums() {
		return docNums;
	}

	public void setDocNums(List<String> docNums) {
		this.docNums = docNums;
	}

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public List<String> getGstnStatus() {
		return gstnStatus;
	}

	public void setGstnStatus(List<String> gstnStatus) {
		this.gstnStatus = gstnStatus;
	}

	public String getProcessingStatus() {
		return processingStatus;
	}

	public void setProcessingStatus(String processingStatus) {
		this.processingStatus = processingStatus;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getDataOriginTypeCode() {
		return dataOriginTypeCode;
	}

	public void setDataOriginTypeCode(String dataOriginTypeCode) {
		this.dataOriginTypeCode = dataOriginTypeCode;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public List<String> getDocTypes() {
		return docTypes;
	}

	public void setDocTypes(List<String> docTypes) {
		this.docTypes = docTypes;
	}

	public List<String> getReturnTypes() {
		return returnTypes;
	}

	public void setReturnTypes(List<String> returnTypes) {
		this.returnTypes = returnTypes;
	}

	public List<String> getDataCategoryList() {
		return dataCategoryList;
	}

	public void setDataCategoryList(List<String> dataCategoryList) {
		this.dataCategoryList = dataCategoryList;
	}

	public List<String> getTableNumberList() {
		return tableNumberList;
	}

	public void setTableNumberList(List<String> tableNumberList) {
		this.tableNumberList = tableNumberList;
	}

	public String getCounterPartyGstins() {
		return counterPartyGstins;
	}

	public void setCounterPartyGstins(String counterPartyGstins) {
		this.counterPartyGstins = counterPartyGstins;
	}

	public boolean isShowGstnData() {
		return showGstnData;
	}

	public void setShowGstnData(boolean showGstnData) {
		this.showGstnData = showGstnData;
	}

	public String getGstnRefId() {
		return gstnRefId;
	}

	public void setGstnRefId(String gstnRefId) {
		this.gstnRefId = gstnRefId;
	}

	public LocalDate getDateEACKFrom() {
		return dateEACKFrom;
	}

	public void setDateEACKFrom(LocalDate dateEACKFrom) {
		this.dateEACKFrom = dateEACKFrom;
	}

	public LocalDate getDateEACKTo() {
		return dateEACKTo;
	}

	public void setDateEACKTo(LocalDate dateEACKTo) {
		this.dateEACKTo = dateEACKTo;
	}

	public boolean isCounterPartyFlag() {
		return counterPartyFlag;
	}

	public void setCounterPartyFlag(boolean counterPartyFlag) {
		this.counterPartyFlag = counterPartyFlag;
	}

	public Long getEwbNo() {
		return ewbNo;
	}

	public void setEwbNo(Long ewbNo) {
		this.ewbNo = ewbNo;
	}

	public LocalDateTime getEwbDate() {
		return ewbDate;
	}

	public void setEwbDate(LocalDateTime ewbDate) {
		this.ewbDate = ewbDate;
	}

	public List<String> getGstReturnsStatus() {
		return gstReturnsStatus;
	}

	public void setGstReturnsStatus(List<String> gstReturnsStatus) {
		this.gstReturnsStatus = gstReturnsStatus;
	}

	public List<Long> getEwbStatus() {
		return ewbStatus;
	}

	public void setEwbStatus(List<Long> ewbStatus) {
		this.ewbStatus = ewbStatus;
	}

	public List<Long> getEwbErrorPoint() {
		return ewbErrorPoint;
	}

	public void setEwbErrorPoint(List<Long> ewbErrorPoint) {
		this.ewbErrorPoint = ewbErrorPoint;
	}

	public List<String> getEwbCancellation() {
		return ewbCancellation;
	}

	public void setEwbCancellation(List<String> ewbCancellation) {
		this.ewbCancellation = ewbCancellation;
	}

	public List<String> getSubSupplyType() {
		return subSupplyType;
	}

	public void setSubSupplyType(List<String> subSupplyType) {
		this.subSupplyType = subSupplyType;
	}

	public List<String> getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(List<String> supplyType) {
		this.supplyType = supplyType;
	}

	public LocalDate getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(LocalDate postingDate) {
		this.postingDate = postingDate;
	}

	public String getTransporterID() {
		return transporterID;
	}

	public void setTransporterID(String transporterID) {
		this.transporterID = transporterID;
	}

	/**
	 * @return the transType
	 */
	public String getTransType() {
		return transType;
	}

	/**
	 * @param transType
	 *            the transType to set
	 */
	public void setTransType(String transType) {
		this.transType = transType;
	}

	/**
	 * @return the ewbValidUpto
	 */
	public LocalDateTime getEwbValidUpto() {
		return ewbValidUpto;
	}

	/**
	 * @param ewbValidUpto
	 *            the ewbValidUpto to set
	 */
	public void setEwbValidUpto(LocalDateTime ewbValidUpto) {
		this.ewbValidUpto = ewbValidUpto;
	}

	/**
	 * @return the einvStatus
	 */
	public List<Long> getEinvStatus() {
		return einvStatus;
	}

	/**
	 * @param einvStatus
	 *            the einvStatus to set
	 */
	public void setEinvStatus(List<Long> einvStatus) {
		this.einvStatus = einvStatus;
	}

	public List<String> getAccVoucherNum() {
		return accVoucherNum;
	}

	public void setAccVoucherNum(List<String> accVoucherNum) {
		this.accVoucherNum = accVoucherNum;
	}

}
