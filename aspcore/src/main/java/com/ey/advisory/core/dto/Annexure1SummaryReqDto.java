package com.ey.advisory.core.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ey.advisory.common.SearchTypeConstants;
import com.ey.advisory.core.search.SearchCriteria;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public class Annexure1SummaryReqDto extends SearchCriteria {

	@Expose
	@SerializedName("taxPeriod")
	private String taxPeriod;

	@Expose
	@SerializedName("docType")
	private String docType;

	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();

	@Expose
	@SerializedName("entity")
	private List<Long> entity = new ArrayList<>();

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs = new HashMap<>();

	@Expose
	@SerializedName("action")
	private String action;

	@Expose
	@SerializedName("ratio")
	private String ratio;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("gstinList")
	private List<String> gstinList;

	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;
	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;

	@Expose
	@SerializedName("docFromDate")
	private LocalDate docFromDate;
	@Expose
	@SerializedName("docToDate")
	private LocalDate docToDate;
	@Expose
	@SerializedName("EINVGenerated")
	private List<String> EINVGenerated = new ArrayList<>();
	@Expose
	@SerializedName("EWBGenerated")
	private List<String> EWBGenerated = new ArrayList<>();

	@Expose
	@SerializedName("isDigigst")
	private Boolean isDigigst;

	@Expose
	@SerializedName("isVerified")
	private String isVerified;

	@Expose
	@SerializedName("returnType")
	private String returnType;

	@Expose
	@SerializedName("docNatureId")
	private Integer docNatureId;

	@Expose
	@SerializedName("fromRetPeriod")
	private String fromRetPeriod;

	@Expose
	@SerializedName("toRetPeriod")
	private String toRetPeriod;
	
	@Expose
	@SerializedName("reportType")
	private String reportType;
	
	@Expose
	@SerializedName("dataType")
	private String dataType;
	
	@Expose
	@SerializedName("isGstr1a")
	private boolean isGstr1a;
	
	public boolean getIsGstr1a() {
		return isGstr1a;
	}

	public void setIsGstr1a(boolean isGstr1a) {
		this.isGstr1a = isGstr1a;
	}

	public String getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(String isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getIsDigigst() {
		return isDigigst;
	}

	public void setIsDigigst(Boolean isDigigst) {
		this.isDigigst = isDigigst;
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

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public Annexure1SummaryReqDto() {
		super(SearchTypeConstants.DOC_SUMMARY_SEARCH);
	}

	public List<Long> getEntityId() {
		return entityId;
	}

	public void setEntityId(List<Long> entityId) {
		this.entityId = entityId;
	}

	public Map<String, List<String>> getDataSecAttrs() {
		return dataSecAttrs;
	}

	public void setDataSecAttrs(Map<String, List<String>> dataSecAttrs) {
		this.dataSecAttrs = dataSecAttrs;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public Integer getDocNatureId() {
		return docNatureId;
	}

	public void setDocNatureId(Integer docNatureId) {
		this.docNatureId = docNatureId;
	}

	public String getRatio() {
		return ratio;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public LocalDate getDocFromDate() {
		return docFromDate;
	}

	public void setDocFromDate(LocalDate docFromDate) {
		this.docFromDate = docFromDate;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public LocalDate getDocToDate() {
		return docToDate;
	}

	public void setDocToDate(LocalDate docToDate) {
		this.docToDate = docToDate;
	}

	public List<String> getEINVGenerated() {
		return EINVGenerated;
	}

	public void setEINVGenerated(List<String> eINVGenerated) {
		EINVGenerated = eINVGenerated;
	}

	public List<String> getEWBGenerated() {
		return EWBGenerated;
	}

	public void setEWBGenerated(List<String> eWBGenerated) {
		EWBGenerated = eWBGenerated;
	}

	public String getFromRetPeriod() {
		return fromRetPeriod;
	}

	public void setFromRetPeriod(String fromRetPeriod) {
		this.fromRetPeriod = fromRetPeriod;
	}

	public String getToRetPeriod() {
		return toRetPeriod;
	}

	public void setToRetPeriod(String toRetPeriod) {
		this.toRetPeriod = toRetPeriod;
	}

	public List<Long> getEntity() {
		return entity;
	}

	public void setEntity(List<Long> entity) {
		this.entity = entity;
	}

	public List<String> getGstinList() {
		return gstinList;
	}

	public void setGstinList(List<String> gstinList) {
		this.gstinList = gstinList;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	
}
