package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2ProcessedRecordsReqDto extends SearchCriteria {

	public Gstr2ProcessedRecordsReqDto(String searchType) {
		super(searchType);
		// TODO Auto-generated constructor stub
	}

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();

	@Expose
	@SerializedName("gstin")
	private List<String> gstin = new ArrayList<>();

	@Expose
	@SerializedName(value = "docRecvFrom", alternate = { "docDateFrom" })
	private LocalDate docRecvFrom;

	@Expose
	@SerializedName(value = "docRecvTo", alternate = { "docDateTo" })
	private LocalDate docRecvTo;

	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;

	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("tableType")
	private List<String> tableType = new ArrayList<>();

	@Expose
	@SerializedName("month")
	private List<String> month = new ArrayList<>();

	public List<String> getMonth() {
		return month;
	}

	public void setMonth(List<String> month) {
		this.month = month;
	}

	@Expose
	@SerializedName("docType")
	private List<String> docType = new ArrayList<>();

	@Expose
	@SerializedName("docCategory")
	private List<String> docCategory = new ArrayList<>();

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("type")
	private String type;

	@Expose
	@SerializedName("fy")
	private String fy;

	@Expose
	@SerializedName("financialYear")
	private String financialYear;

	@Expose
	@SerializedName("returnType")
	private String returnType;
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public List<String> getGstin() {
		return gstin;
	}

	public void setGstin(List<String> gstin) {
		this.gstin = gstin;
	}

	public LocalDate getDocRecvFrom() {
		return docRecvFrom;
	}

	public void setDocRecvFrom(LocalDate docRecvFrom) {
		this.docRecvFrom = docRecvFrom;
	}

	public LocalDate getDocRecvTo() {
		return docRecvTo;
	}

	public void setDocRecvTo(LocalDate docRecvTo) {
		this.docRecvTo = docRecvTo;
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

	public List<String> getDocType() {
		return docType;
	}

	public void setDocType(List<String> docType) {
		this.docType = docType;
	}

	public List<String> getDocCategory() {
		return docCategory;
	}

	public void setDocCategory(List<String> docCategory) {
		this.docCategory = docCategory;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public String getFy() {
		return fy;
	}

	public void setFy(String fy) {
		this.fy = fy;
	}

	/**
	 * @return the financialYear
	 */
	public String getFinancialYear() {
		return financialYear;
	}

	/**
	 * @param financialYear
	 *            the financialYear to set
	 */
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType
	 *            the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

}
