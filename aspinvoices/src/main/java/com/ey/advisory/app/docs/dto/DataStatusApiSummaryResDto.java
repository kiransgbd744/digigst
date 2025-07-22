package com.ey.advisory.app.docs.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataStatusApiSummaryResDto {
	@Expose
	@SerializedName("date")
	private String date;
	@Expose
	@SerializedName("gstin")
	private String gstin;
	@Expose
	@SerializedName("returnPeriod")
	private String returnPeriod;
	@Expose
	@SerializedName("returnType")
	private String returnType;
	@Expose
	@SerializedName("returnSection")
	private String returnSection;
	@Expose
	@SerializedName("count")
	private BigInteger count;
	@Expose
	@SerializedName("taxableValue")
	private BigDecimal taxableValue = BigDecimal.ZERO;
	@Expose
	@SerializedName("totalTaxes")
	private BigDecimal totalTaxes = BigDecimal.ZERO;
	@Expose
	@SerializedName("igst")
	private BigDecimal igst = BigDecimal.ZERO;
	@Expose
	@SerializedName("cgst")
	private BigDecimal cgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("sgst")
	private BigDecimal sgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("cess")
	private BigDecimal cess = BigDecimal.ZERO;
	@Expose
	@SerializedName("reviewStatus")
	private String reviewStatus;
	@Expose
	@SerializedName("saveStatus")
	private String saveStatus;

	@Expose
	@SerializedName("authToken")
	private String authToken;

	@Expose
	@SerializedName("items")
	private List<String> items;

	private String docType;

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getReturnSection() {
		return returnSection;
	}

	public void setReturnSection(String returnSection) {
		this.returnSection = returnSection;
	}

	public BigInteger getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getTotalTaxes() {
		return totalTaxes;
	}

	public void setTotalTaxes(BigDecimal totalTaxes) {
		this.totalTaxes = totalTaxes;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public String getSaveStatus() {
		return saveStatus;
	}

	public void setSaveStatus(String saveStatus) {
		this.saveStatus = saveStatus;
	}

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "DataStatusApiSummaryResDto [date=" + date + ", gstin=" + gstin
				+ ", returnPeriod=" + returnPeriod + ", returnType="
				+ returnType + ", returnSection=" + returnSection + ", count="
				+ count + ", taxableValue=" + taxableValue + ", totalTaxes="
				+ totalTaxes + ", igst=" + igst + ", cgst=" + cgst + ", sgst="
				+ sgst + ", cess=" + cess + ", reviewStatus=" + reviewStatus
				+ ", saveStatus=" + saveStatus + ", authToken=" + authToken
				+ ", items=" + items + ", docType=" + docType + "]";
	}

}
