package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InitiateReconHeaderDto {

	@Expose
	@SerializedName("Showing")
	protected String Showing;

	@Expose
	@SerializedName("section")
	protected String section;

	@Expose
	@SerializedName("anx2Count")
	protected int anx2Count;

	@Expose
	@SerializedName("anx2TaxableValue")
	protected BigDecimal anx2TaxableValue;

	@Expose
	@SerializedName("anx2IGST")
	protected BigDecimal anx2IGST;

	@Expose
	@SerializedName("anx2CGST")
	protected BigDecimal anx2CGST;

	@Expose
	@SerializedName("anx2SGST")
	protected BigDecimal anx2SGST;

	@Expose
	@SerializedName("anx2Cess")
	protected BigDecimal anx2Cess;

	@Expose
	@SerializedName("prCount")
	protected Integer prCount;

	@Expose
	@SerializedName("prTaxableValue")
	protected BigDecimal prTaxableValue;

	@Expose
	@SerializedName("prIGST")
	protected BigDecimal prIGST;

	@Expose
	@SerializedName("prCGST")
	protected BigDecimal prCGST;

	@Expose
	@SerializedName("prSGST")
	protected BigDecimal prSGST;

	@Expose
	@SerializedName("prCess")
	protected BigDecimal prCess;

	@Expose
	@SerializedName("avIGST")
	protected BigDecimal avIgst;

	@Expose
	@SerializedName("avCGST")
	protected BigDecimal avCgst;

	@Expose
	@SerializedName("avSGST")
	protected BigDecimal avSgst;

	@Expose
	@SerializedName("avCess")
	protected BigDecimal avCess;

	@Expose
	@SerializedName("items")
	private List<Object> lineItems;

	public String getShowing() {
		return Showing;
	}

	public void setShowing(String showing) {
		Showing = showing;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public int getAnx2Count() {
		return anx2Count;
	}

	public void setAnx2Count(int anx2Count) {
		this.anx2Count = anx2Count;
	}

	public BigDecimal getAnx2TaxableValue() {
		return anx2TaxableValue;
	}

	public void setAnx2TaxableValue(BigDecimal anx2TaxableValue) {
		this.anx2TaxableValue = anx2TaxableValue;
	}

	public BigDecimal getAnx2IGST() {
		return anx2IGST;
	}

	public void setAnx2IGST(BigDecimal anx2igst) {
		anx2IGST = anx2igst;
	}

	public BigDecimal getAnx2CGST() {
		return anx2CGST;
	}

	public void setAnx2CGST(BigDecimal anx2cgst) {
		anx2CGST = anx2cgst;
	}

	public BigDecimal getAnx2SGST() {
		return anx2SGST;
	}

	public void setAnx2SGST(BigDecimal anx2sgst) {
		anx2SGST = anx2sgst;
	}

	public BigDecimal getAnx2Cess() {
		return anx2Cess;
	}

	public void setAnx2Cess(BigDecimal anx2Cess) {
		this.anx2Cess = anx2Cess;
	}

	public Integer getPrCount() {
		return prCount;
	}

	public void setPrCount(Integer prCount) {
		this.prCount = prCount;
	}

	public BigDecimal getPrTaxableValue() {
		return prTaxableValue;
	}

	public void setPrTaxableValue(BigDecimal prTaxableValue) {
		this.prTaxableValue = prTaxableValue;
	}

	public BigDecimal getPrIGST() {
		return prIGST;
	}

	public void setPrIGST(BigDecimal prIGST) {
		this.prIGST = prIGST;
	}

	public BigDecimal getPrCGST() {
		return prCGST;
	}

	public void setPrCGST(BigDecimal prCGST) {
		this.prCGST = prCGST;
	}

	public BigDecimal getPrSGST() {
		return prSGST;
	}

	public void setPrSGST(BigDecimal prSGST) {
		this.prSGST = prSGST;
	}

	public BigDecimal getPrCess() {
		return prCess;
	}

	public void setPrCess(BigDecimal prCess) {
		this.prCess = prCess;
	}

	public BigDecimal getAvIgst() {
		return avIgst;
	}

	public void setAvIgst(BigDecimal avIgst) {
		this.avIgst = avIgst;
	}

	public BigDecimal getAvCgst() {
		return avCgst;
	}

	public void setAvCgst(BigDecimal avCgst) {
		this.avCgst = avCgst;
	}

	public BigDecimal getAvSgst() {
		return avSgst;
	}

	public void setAvSgst(BigDecimal avSgst) {
		this.avSgst = avSgst;
	}

	public BigDecimal getAvCess() {
		return avCess;
	}

	public void setAvCess(BigDecimal avCess) {
		this.avCess = avCess;
	}

	public List<Object> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<Object> lineItems) {
		this.lineItems = lineItems;
	}

	@Override
	public String toString() {
		return "InitiateReconHeaderDto [Showing=" + Showing + ", section="
				+ section + ", anx2Count=" + anx2Count + ", anx2TaxableValue="
				+ anx2TaxableValue + ", anx2IGST=" + anx2IGST + ", anx2CGST="
				+ anx2CGST + ", anx2SGST=" + anx2SGST + ", anx2Cess=" + anx2Cess
				+ ", prCount=" + prCount + ", prTaxableValue=" + prTaxableValue
				+ ", prIGST=" + prIGST + ", prCGST=" + prCGST + ", prSGST="
				+ prSGST + ", prCess=" + prCess + ", avIgst=" + avIgst
				+ ", avCgst=" + avCgst + ", avSgst=" + avSgst + ", avCess="
				+ avCess + ", lineItems=" + lineItems + "]";
	}

}