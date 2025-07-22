package com.ey.advisory.core.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataStatusApiSummaryReqDto {
	@Expose
	@SerializedName("dataType")
	private String dataType;
	
	@Expose
	@SerializedName("entityId")
	private List<Long> entityId = new ArrayList<>();
	@Expose
	@SerializedName("GSTIN")
	private List<String> GSTIN = new ArrayList<>();
	@Expose
	@SerializedName("dataRecvFrom")
	private String dataRecvFrom;
	@Expose
	@SerializedName("dataRecvTo")
	private String dataRecvTo;
	@Expose
	@SerializedName("docFromDate")
	private String docFromDate;
	@Expose
	@SerializedName("docToDate")
	private String docToDate;
	@Expose
	@SerializedName("taxPeriodFrom")
	private String taxPeriodFrom;
	@Expose
	@SerializedName("taxPeriodTo")
	private String taxPeriodTo;
	@Expose
	@SerializedName("dates")
	private List<String> dates = new ArrayList<>();

	@Expose
	@SerializedName("dataSecAttrs")
	private Map<String, List<String>> dataSecAttrs;

	@Expose
	@SerializedName("outwardDataSecAttrs")
	private Map<String, List<String>> outwardDataSecAttrs;

	@Expose
	@SerializedName("inwardDataSecAttrs")
	private Map<String, List<String>> inwardDataSecAttrs;
	
	@Expose
	@SerializedName("documentDateFrom")
	private String documentDateFrom;

	@Expose
	@SerializedName("documentDateTo")
	private String documentDateTo;
	
	@Expose
	@SerializedName("accVoucherDateFrom")
	private String accVoucherDateFrom;
	
	@Expose
	@SerializedName("accVoucherDateTo")
	private String accVoucherDateTo;

	public Map<String, List<String>> getOutwardDataSecAttrs() {
		return outwardDataSecAttrs;
	}

	public void setOutwardDataSecAttrs(
			Map<String, List<String>> outwardDataSecAttrs) {
		this.outwardDataSecAttrs = outwardDataSecAttrs;
	}

	public Map<String, List<String>> getInwardDataSecAttrs() {
		return inwardDataSecAttrs;
	}

	public void setInwardDataSecAttrs(
			Map<String, List<String>> inwardDataSecAttrs) {
		this.inwardDataSecAttrs = inwardDataSecAttrs;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public List<String> getGstin() {
		return GSTIN;
	}

	public void setGstin(List<String> GSTIN) {
		this.GSTIN = GSTIN;
	}

	public String getDataRecvFrom() {
		return dataRecvFrom;
	}

	public void setDataRecvFrom(String dataRecvFrom) {
		this.dataRecvFrom = dataRecvFrom;
	}

	public String getDataRecvTo() {
		return dataRecvTo;
	}

	public void setDataRecvTo(String dataRecvTo) {
		this.dataRecvTo = dataRecvTo;
	}

	public String getDocFromDate() {
		return docFromDate;
	}

	public void setDocFromDate(String docFromDate) {
		this.docFromDate = docFromDate;
	}

	public String getDocToDate() {
		return docToDate;
	}

	public void setDocToDate(String docToDate) {
		this.docToDate = docToDate;
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

	public List<String> getDates() {
		return dates;
	}

	public void setDates(List<String> dates) {
		this.dates = dates;
	}
	
	public String getDocumentDateFrom() {
		return documentDateFrom;
	}

	public void setDocumentDateFrom(String documentDateFrom) {
		this.documentDateFrom = documentDateFrom;
	}

	public String getDocumentDateTo() {
		return documentDateTo;
	}

	public void setDocumentDateTo(String documentDateTo) {
		this.documentDateTo = documentDateTo;
	}

	public String getAccVoucherDateFrom() {
		return accVoucherDateFrom;
	}

	public void setAccVoucherDateFrom(String accVoucherDateFrom) {
		this.accVoucherDateFrom = accVoucherDateFrom;
	}

	public String getAccVoucherDateTo() {
		return accVoucherDateTo;
	}

	public void setAccVoucherDateTo(String accVoucherDateTo) {
		this.accVoucherDateTo = accVoucherDateTo;
	}

	@Override
	public String toString() {
		return "DataStatusApiSummaryReqDto [dataType=" + dataType
				+ ", entityId=" + entityId + ", GSTIN=" + GSTIN
				+ ", dataRecvFrom=" + dataRecvFrom + ", dataRecvTo="
				+ dataRecvTo + ", docFromDate=" + docFromDate + ", docToDate="
				+ docToDate + ", taxPeriodFrom=" + taxPeriodFrom
				+ ", taxPeriodTo=" + taxPeriodTo + ", dates=" + dates
				+ ", dataSecAttrs=" + dataSecAttrs + ", getEntityId()="
				+ getEntityId() + ", getDataSecAttrs()=" + getDataSecAttrs()
				+ ", getDataType()=" + getDataType() + ", getGstin()="
				+ getGstin() + ", getDataRecvFrom()=" + getDataRecvFrom()
				+ ", getDataRecvTo()=" + getDataRecvTo() + ", getDocFromDate()="
				+ getDocFromDate() + ", getDocToDate()=" + getDocToDate()
				+ ", getTaxPeriodFrom()=" + getTaxPeriodFrom()
				+ ", getTaxPeriodTo()=" + getTaxPeriodTo() + ", getDates()="
				+ getDates() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", getAccVoucherDateFrom()="
				+ getAccVoucherDateFrom() + ", getAccVoucherDateTo()="
				+ getAccVoucherDateTo() + ", getDocumentDateFrom()="
				+ getDocumentDateFrom() + ", getDocumentDateTo()="
				+ getDocumentDateTo() + ",toString()=" + super.toString() + "]";
	}
}
