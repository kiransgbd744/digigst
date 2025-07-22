package com.ey.advisory.core.dto;

import java.time.LocalDate;
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
 * @author Mohana.Dasari
 */
@Component
public class DocSearchReqDto extends SearchCriteria {

	public DocSearchReqDto() {
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
	@SerializedName("id")
	protected Long id;

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
	private List<String> counterPartyGstins;

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
	private String ewbNo;

	@Expose
	@SerializedName("ewbDate")
	private LocalDate ewbDate;

	@Expose
	@SerializedName("transType")
	private String transType;

	@Expose
	@SerializedName("docKey")
	private String docKey;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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

	public List<String> getCounterPartyGstins() {
		return counterPartyGstins;
	}

	public void setCounterPartyGstins(List<String> counterPartyGstins) {
		this.counterPartyGstins = counterPartyGstins;
	}

	public boolean isShowGstnData() {
		return showGstnData;
	}

	public void setShowGstnData(boolean showGstnData) {
		this.showGstnData = showGstnData;
	}

	/**
	 * @return the gstnRefId
	 */
	public String getGstnRefId() {
		return gstnRefId;
	}

	/**
	 * @param gstnRefId
	 *            the gstnRefId to set
	 */
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

	public String getEwbNo() {
		return ewbNo;
	}

	public void setEwbNo(String ewbNo) {
		this.ewbNo = ewbNo;
	}

	public LocalDate getEwbDate() {
		return ewbDate;
	}

	public void setEwbDate(LocalDate ewbDate) {
		this.ewbDate = ewbDate;
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

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

}
