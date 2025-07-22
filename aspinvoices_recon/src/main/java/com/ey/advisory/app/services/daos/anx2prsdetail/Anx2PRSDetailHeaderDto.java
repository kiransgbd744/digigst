package com.ey.advisory.app.services.daos.anx2prsdetail;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Anx2PRSDetailHeaderDto {

	@Expose
	@SerializedName("table")
	protected String table;

	@Expose
	@SerializedName("invType")
	protected String invType;

	@Expose
	@SerializedName("taxDocType")
	protected BigDecimal taxDocType;
	
	@Expose
	@SerializedName("count")
	protected int count;

	@Expose
	@SerializedName("invValue")
	protected BigDecimal invValue;

	@Expose
	@SerializedName("taxableValue")
	protected BigDecimal taxableValue;

	@Expose
	@SerializedName("totalTaxPayable")
	protected BigDecimal totalTaxPayable;

	@Expose
	@SerializedName("IGST")
	protected BigDecimal IGST;

	@Expose
	@SerializedName("CGST")
	protected BigDecimal CGST;

	@Expose
	@SerializedName("SGST")
	protected BigDecimal SGST;

	@Expose
	@SerializedName("Cess")
	protected BigDecimal Cess;
	
	@Expose
	@SerializedName("totalCreditEligible")
	protected BigDecimal totalCreditEligible;
	
	@Expose
	@SerializedName("ceIGST")
	protected BigDecimal ceIGST;
	
	@Expose
	@SerializedName("ceCGST")
	protected BigDecimal ceCGST;
	
	@Expose
	@SerializedName("ceSGST")
	protected BigDecimal ceSGST;
	
	@Expose
	@SerializedName("ceCess")
	protected BigDecimal ceCess;
	
	@Expose
	@SerializedName("items")
	private List<Object> lineItems;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public BigDecimal getTaxDocType() {
		return taxDocType;
	}

	public void setTaxDocType(BigDecimal taxDocType) {
		this.taxDocType = taxDocType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BigDecimal getInvValue() {
		return invValue;
	}

	public void setInvValue(BigDecimal invValue) {
		this.invValue = invValue;
	}

	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	public BigDecimal getTotalTaxPayable() {
		return totalTaxPayable;
	}

	public void setTotalTaxPayable(BigDecimal totalTaxPayable) {
		this.totalTaxPayable = totalTaxPayable;
	}

	public BigDecimal getIGST() {
		return IGST;
	}

	public void setIGST(BigDecimal iGST) {
		IGST = iGST;
	}

	public BigDecimal getCGST() {
		return CGST;
	}

	public void setCGST(BigDecimal cGST) {
		CGST = cGST;
	}

	public BigDecimal getSGST() {
		return SGST;
	}

	public void setSGST(BigDecimal sGST) {
		SGST = sGST;
	}

	public BigDecimal getCess() {
		return Cess;
	}

	public void setCess(BigDecimal cess) {
		Cess = cess;
	}

	public BigDecimal getTotalCreditEligible() {
		return totalCreditEligible;
	}

	public void setTotalCreditEligible(BigDecimal totalCreditEligible) {
		this.totalCreditEligible = totalCreditEligible;
	}

	public BigDecimal getCeIGST() {
		return ceIGST;
	}

	public void setCeIGST(BigDecimal ceIGST) {
		this.ceIGST = ceIGST;
	}

	public BigDecimal getCeCGST() {
		return ceCGST;
	}

	public void setCeCGST(BigDecimal ceCGST) {
		this.ceCGST = ceCGST;
	}

	public BigDecimal getCeSGST() {
		return ceSGST;
	}

	public void setCeSGST(BigDecimal ceSGST) {
		this.ceSGST = ceSGST;
	}

	public BigDecimal getCeCess() {
		return ceCess;
	}

	public void setCeCess(BigDecimal ceCess) {
		this.ceCess = ceCess;
	}

	public List<Object> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<Object> lineItems) {
		this.lineItems = lineItems;
	}

	@Override
	public String toString() {
		return "Anx2PRSDetailHeaderDto [table=" + table + ", invType=" + invType
				+ ", taxDocType=" + taxDocType + ", count=" + count
				+ ", invValue=" + invValue + ", taxableValue=" + taxableValue
				+ ", totalTaxPayable=" + totalTaxPayable + ", IGST=" + IGST
				+ ", CGST=" + CGST + ", SGST=" + SGST + ", Cess=" + Cess
				+ ", totalCreditEligible=" + totalCreditEligible + ", ceIGST="
				+ ceIGST + ", ceCGST=" + ceCGST + ", ceSGST=" + ceSGST
				+ ", ceCess=" + ceCess + ", lineItems=" + lineItems + "]";
	}



}