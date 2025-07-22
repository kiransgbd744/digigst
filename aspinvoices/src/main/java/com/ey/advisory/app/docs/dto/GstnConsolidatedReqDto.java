/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Laxmi.Salukuti
 *
 */
public class GstnConsolidatedReqDto extends SearchCriteria {

	public GstnConsolidatedReqDto() {
		super(SearchTypeConstants.REPORTS_SEARCH);
	}

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;
	
	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;

	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;

	@Expose
	@SerializedName("gstnRefId")
	private String gstnRefId;

	@Expose
	@SerializedName("answer")
	private Integer answer;

	@Expose
	@SerializedName("entityId")
	private String entityId;

	@Expose
	@SerializedName("gstr1aSections")
	private List<String> gstr1aSections;

	@Expose
	@SerializedName("gstr2aSections")
	private List<String> gstr2aSections;

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	/* Gstr1-Get EInvoices */
	@Expose
	@SerializedName("status")
	private String status;

	@Expose
	@SerializedName("gstr1EinvSections")
	private List<String> gstr1EinvSections;
	
	@Expose
	@SerializedName("tableType")
	private List<String> tableType = new ArrayList<>();
	
	@Expose
	@SerializedName("returnType")
	private String returnType;
	

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}

	/**
	 * @param gstin
	 *            the gstin to set
	 */
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	/**
	 * @return the taxPeriod
	 */
	public String getTaxPeriod() {
		return taxPeriod;
	}

	/**
	 * @param taxPeriod
	 *            the taxPeriod to set
	 */
	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
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

	/**
	 * @return the answer
	 */
	public Integer getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(Integer answer) {
		this.answer = answer;
	}

	public List<String> getGstr1aSections() {
		return gstr1aSections;
	}

	public void setGstr7aSections(List<String> gstr1aSections) {
		this.gstr1aSections = gstr1aSections;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public List<String> getGstr2aSections() {
		return gstr2aSections;
	}

	public void setGstr2aSections(List<String> gstr2aSections) {
		this.gstr2aSections = gstr2aSections;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getGstr1EinvSections() {
		return gstr1EinvSections;
	}

	public void setGstr1EinvSections(List<String> gstr1EinvSections) {
		this.gstr1EinvSections = gstr1EinvSections;
	}

	public void setGstr1aSections(List<String> gstr1aSections) {
		this.gstr1aSections = gstr1aSections;
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
	
	

	public List<String> getTableType() {
		return tableType;
	}

	public void setTableType(List<String> tableType) {
		this.tableType = tableType;
	}

	@Override
	public String toString() {
		return "GstnConsolidatedReqDto [gstin=" + gstin + ", taxPeriod="
				+ taxPeriod + ", taxPeriodFrom=" + taxPeriodFrom
				+ ", taxPeriodTo=" + taxPeriodTo + ", gstnRefId=" + gstnRefId
				+ ", answer=" + answer + ", entityId=" + entityId
				+ ", gstr1aSections=" + gstr1aSections + ", gstr2aSections="
				+ gstr2aSections + ", dataSecAttrs=" + dataSecAttrs
				+ ", status=" + status + ", gstr1EinvSections="
				+ gstr1EinvSections + ", tableType=" + tableType
				+ ", reconType=" + returnType + "]";
	}

}
