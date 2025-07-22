/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author kiran
 */
public class Gstr1PrVsSubmDownloadReportDto extends SearchCriteria {

	public Gstr1PrVsSubmDownloadReportDto() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId;
	
	@Expose
	@SerializedName("Gstin")
	private String Gstin;
	
	@Expose
	@SerializedName("docDate")
	private String docDate;
	
	@Expose
	@SerializedName("docType1")
	private String docType1;
	
	@Expose
	@SerializedName("taxPeriod")
	private String taxperiod;

	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;

	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;

	@Expose
	@SerializedName("taxDocType")
	private String taxDocType;
	
	@Expose
	@SerializedName("docDateFrom")
	private LocalDate docDateFrom;
	
	@Expose
	@SerializedName("docDateTo")
	private LocalDate docDateTo;
	
	@Expose
	@SerializedName("eInvGenerated")
	private List<String> eInvGenerated = new ArrayList<>();
	
	@Expose
	@SerializedName("eWbGenerated")
	private List<String> eWbGenerated = new ArrayList<>();
	
	@Expose
	@SerializedName("tableType")
	private List<String> tableType = new ArrayList<>();
	@Expose
	@SerializedName("docType")
	private List<String> docType = new ArrayList<>();

	@Expose
	@SerializedName("type")
	private String type;

	@Expose
	@SerializedName("dataType")
	private String dataType;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("gstnUploadDate")
	private String gstnUploadDate;

	@Expose
	@SerializedName("gstin")
	private List<String> gstins;

	@Expose
	@SerializedName("gstnRefId")
	private String gstnRefId;
	
	@Expose
	@SerializedName("refId")
	private String refId;

	@Expose
	@SerializedName("genOfTimeAndDate")
	private String genOfTimeAndDate;
	
	@Expose
	@SerializedName("isNilUserInput")
	private String isNilUserInput;
	
	@Expose
	@SerializedName("docNum")
	private String docNum;
	
	@Expose
	@SerializedName("report")
	List<Gstr1ProcessedRecordsRespDto> report= new ArrayList<>();
	
	

	public List<Gstr1ProcessedRecordsRespDto> getReport() {
		return report;
	}

	public void setReport(List<Gstr1ProcessedRecordsRespDto> report) {
		this.report = report;
	}

	/**
	 * @return the entityId
	 */
	public List<Long> getEntityId() {
		return entityId;
	}

	public String getGenOfTimeAndDate() {
		return genOfTimeAndDate;
	}

	public void setGenOfTimeAndDate(String genOfTimeAndDate) {
		this.genOfTimeAndDate = genOfTimeAndDate;
	}

	/**
	 * @param entityId
	 *            the entityId to set
	 */
	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the taxperiod
	 */
	public String getTaxperiod() {
		return taxperiod;
	}

	/**
	 * @param taxperiod
	 *            the taxperiod to set
	 */
	public void setTaxperiod(String taxperiod) {
		this.taxperiod = taxperiod;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the dataSecAttrs
	 */
	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	/**
	 * @param dataSecAttrs
	 *            the dataSecAttrs to set
	 */
	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public String getGstnUploadDate() {
		return gstnUploadDate;
	}

	public void setGstnUploadDate(String gstnUploadDate) {
		this.gstnUploadDate = gstnUploadDate;
	}

	public List<String> getGstins() {
		return gstins;
	}

	public void setGstins(List<String> gstins) {
		this.gstins = gstins;
	}

	public String getGstnRefId() {
		return gstnRefId;
	}

	public void setGstnRefId(String gstnRefId) {
		this.gstnRefId = gstnRefId;
	}

	public String getTaxPeriodFrom() {
		return taxPeriodFrom;
	}

	public void setTaxPeriodFrom(String taxPeriodFrom) {
		this.taxPeriodFrom = taxPeriodFrom;
	}

	public String getTaxPeriodTo() {
		return taxPeriodTo;
	}

	public void setTaxPeriodTo(String taxPeriodTo) {
		this.taxPeriodTo = taxPeriodTo;
	}

	public String getTaxDocType() {
		return taxDocType;
	}

	public void setTaxDocType(String taxDocType) {
		this.taxDocType = taxDocType;
	}

	public String getIsNilUserInput() {
		return isNilUserInput;
	}

	public void setIsNilUserInput(String isNilUserInput) {
		this.isNilUserInput = isNilUserInput;
	}
	
	/**
	 * @return the docDateFrom
	 */
	public LocalDate getDocDateFrom() {
		return docDateFrom;
	}

	/**
	 * @param docDateFrom the docDateFrom to set
	 */
	public void setDocDateFrom(LocalDate docDateFrom) {
		this.docDateFrom = docDateFrom;
	}

	/**
	 * @return the docDateTo
	 */
	public LocalDate getDocDateTo() {
		return docDateTo;
	}

	/**
	 * @param docDateTo the docDateTo to set
	 */
	public void setDocDateTo(LocalDate docDateTo) {
		this.docDateTo = docDateTo;
	}

	/**
	 * @return the eInvGenerated
	 */
	public List<String> geteInvGenerated() {
		return eInvGenerated;
	}

	/**
	 * @param eInvGenerated the eInvGenerated to set
	 */
	public void seteInvGenerated(List<String> eInvGenerated) {
		this.eInvGenerated = eInvGenerated;
	}

	/**
	 * @return the eWbGenerated
	 */
	public List<String> geteWbGenerated() {
		return eWbGenerated;
	}

	/**
	 * @param eWbGenerated the eWbGenerated to set
	 */
	public void seteWbGenerated(List<String> eWbGenerated) {
		this.eWbGenerated = eWbGenerated;
	}

	public List<String> getTableType() {
		return tableType;
	}

	public void setTableType(List<String> tableType) {
		this.tableType = tableType;
	}

	public List<String> getDocType() {
		return docType;
	}

	public void setDocType(List<String> docType) {
		this.docType = docType;
	}

	/**
	 * @return the refId
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @param refId the refId to set
	 */
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	

	public String getDocNum() {
		return docNum;
	}

	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}


	public String getGstin() {
		return Gstin;
	}

	public void setGstin(String gstin) {
		Gstin = gstin;
	}

	public String getDocDate() {
		return docDate;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public String getDocType1() {
		return docType1;
	}

	public void setDocType1(String docType1) {
		this.docType1 = docType1;
	}

	@Override
	public String toString() {
		return "Gstr1ReviwSummReportsReqDto [entityId=" + entityId + ", Gstin="
				+ Gstin + ", docDate=" + docDate + ", docType1=" + docType1
				+ ", taxperiod=" + taxperiod + ", taxPeriodFrom="
				+ taxPeriodFrom + ", taxPeriodTo=" + taxPeriodTo
				+ ", taxDocType=" + taxDocType + ", docDateFrom=" + docDateFrom
				+ ", docDateTo=" + docDateTo + ", eInvGenerated="
				+ eInvGenerated + ", eWbGenerated=" + eWbGenerated
				+ ", tableType=" + tableType + ", docType=" + docType
				+ ", type=" + type + ", dataType=" + dataType
				+ ", dataSecAttrs=" + dataSecAttrs + ", gstnUploadDate="
				+ gstnUploadDate + ", gstins=" + gstins + ", gstnRefId="
				+ gstnRefId + ", refId=" + refId + ", genOfTimeAndDate="
				+ genOfTimeAndDate + ", isNilUserInput=" + isNilUserInput
				+ ", docNum=" + docNum + ", searchType=" + searchType
				+ ", getEntityId()=" + getEntityId()
				+ ", getGenOfTimeAndDate()=" + getGenOfTimeAndDate()
				+ ", getTaxperiod()=" + getTaxperiod() + ", getType()="
				+ getType() + ", getDataType()=" + getDataType()
				+ ", getDataSecAttrs()=" + getDataSecAttrs()
				+ ", getGstnUploadDate()=" + getGstnUploadDate()
				+ ", getGstins()=" + getGstins() + ", getGstnRefId()="
				+ getGstnRefId() + ", getTaxPeriodFrom()=" + getTaxPeriodFrom()
				+ ", getTaxPeriodTo()=" + getTaxPeriodTo()
				+ ", getTaxDocType()=" + getTaxDocType()
				+ ", getIsNilUserInput()=" + getIsNilUserInput()
				+ ", getDocDateFrom()=" + getDocDateFrom() + ", getDocDateTo()="
				+ getDocDateTo() + ", geteInvGenerated()=" + geteInvGenerated()
				+ ", geteWbGenerated()=" + geteWbGenerated()
				+ ", getTableType()=" + getTableType() + ", getDocType()="
				+ getDocType() + ", getRefId()=" + getRefId() + ", getDocNum()="
				+ getDocNum() + ", getGstin()=" + getGstin() + ", getDocDate()="
				+ getDocDate() + ", getDocType1()=" + getDocType1()
				+ ", getSearchType()=" + getSearchType() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

	}
