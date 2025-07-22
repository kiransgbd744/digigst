package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;

public class InitiateReconLineItemDto {

	private String section;

	private int anx2Count;

	private BigDecimal anx2TaxableValue = BigDecimal.ZERO;

	private BigDecimal anx2IGST = BigDecimal.ZERO;

	private BigDecimal anx2CGST = BigDecimal.ZERO;

	private BigDecimal anx2SGST = BigDecimal.ZERO;

	private BigDecimal anx2Cess = BigDecimal.ZERO;

	private int prCount;

	private BigDecimal prTaxableValue = BigDecimal.ZERO;

	private BigDecimal prIGST = BigDecimal.ZERO;

	private BigDecimal prCGST = BigDecimal.ZERO;

	private BigDecimal prSGST = BigDecimal.ZERO;

	private BigDecimal prCess = BigDecimal.ZERO;

	private BigDecimal avIGST = BigDecimal.ZERO;

	private BigDecimal avCGST = BigDecimal.ZERO;

	private BigDecimal avSGST = BigDecimal.ZERO;

	private BigDecimal avCess = BigDecimal.ZERO;

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

	public int getPrCount() {
		return prCount;
	}

	public void setPrCount(int prCount) {
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

	public BigDecimal getAvIGST() {
		return avIGST;
	}

	public void setAvIGST(BigDecimal avIGST) {
		this.avIGST = avIGST;
	}

	public BigDecimal getAvCGST() {
		return avCGST;
	}

	public void setAvCGST(BigDecimal avCGST) {
		this.avCGST = avCGST;
	}

	public BigDecimal getAvSGST() {
		return avSGST;
	}

	public void setAvSGST(BigDecimal avSGST) {
		this.avSGST = avSGST;
	}

	public BigDecimal getAvCess() {
		return avCess;
	}

	public void setAvCess(BigDecimal avCess) {
		this.avCess = avCess;
	}

	@Override
	public String toString() {
		return "InitiateReconLineItemDto [section=" + section + ", anx2Count="
				+ anx2Count + ", anx2TaxableValue=" + anx2TaxableValue
				+ ", anx2IGST=" + anx2IGST + ", anx2CGST=" + anx2CGST
				+ ", anx2SGST=" + anx2SGST + ", anx2Cess=" + anx2Cess
				+ ", prCount=" + prCount + ", prTaxableValue=" + prTaxableValue
				+ ", prIGST=" + prIGST + ", prCGST=" + prCGST + ", prSGST="
				+ prSGST + ", prCess=" + prCess + ", avIGST=" + avIGST
				+ ", avCGST=" + avCGST + ", avSGST=" + avSGST + ", avCess="
				+ avCess + "]";
	}

}
