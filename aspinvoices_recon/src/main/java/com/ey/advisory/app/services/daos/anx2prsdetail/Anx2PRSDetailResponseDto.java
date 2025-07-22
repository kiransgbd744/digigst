package com.ey.advisory.app.services.daos.anx2prsdetail;

import java.math.BigDecimal;

public class Anx2PRSDetailResponseDto {

	private String table;

	private String invType;

	private String taxDocType;
	
	private int count;

	private BigDecimal invValue = BigDecimal.ZERO;

	private BigDecimal taxableValue = BigDecimal.ZERO;

	private BigDecimal totalTaxPayable = BigDecimal.ZERO;

	private BigDecimal IGST = BigDecimal.ZERO;

	private BigDecimal CGST = BigDecimal.ZERO;

	private BigDecimal SGST = BigDecimal.ZERO;

	private BigDecimal Cess = BigDecimal.ZERO;

	private BigDecimal totalCreditEligible = BigDecimal.ZERO;
	
	private BigDecimal ceIGST = BigDecimal.ZERO;
	
	private BigDecimal ceCGST = BigDecimal.ZERO;
	
	private BigDecimal ceSGST = BigDecimal.ZERO;
	
	private BigDecimal ceCess = BigDecimal.ZERO;

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

	public String getTaxDocType() {
		return taxDocType;
	}

	public void setTaxDocType(String taxDocType) {
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

	@Override
	public String toString() {
		return "Anx2PRSDetailResponseDto [table=" + table + ", invType="
				+ invType + ", taxDocType=" + taxDocType + ", count=" + count
				+ ", invValue=" + invValue + ", taxableValue=" + taxableValue
				+ ", totalTaxPayable=" + totalTaxPayable + ", IGST=" + IGST
				+ ", CGST=" + CGST + ", SGST=" + SGST + ", Cess=" + Cess
				+ ", totalCreditEligible=" + totalCreditEligible + ", ceIGST="
				+ ceIGST + ", ceCGST=" + ceCGST + ", ceSGST=" + ceSGST
				+ ", ceCess=" + ceCess + "]";
	}



}