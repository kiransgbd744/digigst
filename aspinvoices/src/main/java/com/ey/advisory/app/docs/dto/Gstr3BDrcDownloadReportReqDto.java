
package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Harsh
 *
 */
public class Gstr3BDrcDownloadReportReqDto extends SearchCriteria {

	public Gstr3BDrcDownloadReportReqDto() {
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
	@SerializedName("taxPeriod")
	private String taxperiod;

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
	
	@Expose
	@SerializedName("reportType")
	private String reportType;
	

	public List<Gstr1ProcessedRecordsRespDto> getReport() {
		return report;
	}
	
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	/**
	 * @return the dataType
	 */
	public String getReportType() {
		return reportType;
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

	

	public String getIsNilUserInput() {
		return isNilUserInput;
	}

	public void setIsNilUserInput(String isNilUserInput) {
		this.isNilUserInput = isNilUserInput;
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

	

	@Override
	public String toString() {
		return "Gstr1ReviwSummReportsReqDto [entityId=" + entityId + ", Gstin="
				+ Gstin + ", docDate=" + docDate
				+ ", taxperiod=" + taxperiod + ", docType=" + docType
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
				+ getGstnRefId()
				+ ", getIsNilUserInput()=" + getIsNilUserInput()
			    + ", getDocType()="
				+ getDocType() + ", getRefId()=" + getRefId() + ", getDocNum()="
				+ getDocNum() + ", getGstin()=" + getGstin() + ", getDocDate()="
				+ getDocDate()
				+ ", getSearchType()=" + getSearchType() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() +", getReportType=" + getReportType() +"]";
	}

	}
