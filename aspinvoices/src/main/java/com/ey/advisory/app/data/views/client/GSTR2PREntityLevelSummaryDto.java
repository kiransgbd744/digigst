package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

/**
 * 
 * @author Balakrishna.S
 *
 */
public class GSTR2PREntityLevelSummaryDto {

	private String GSTIN;
	private String TableDescription;
	private Integer Count;
	private BigDecimal invoiceValue = BigDecimal.ZERO;
	private BigDecimal taxableValue = BigDecimal.ZERO;
	private BigDecimal totalTax = BigDecimal.ZERO;
	private BigDecimal IGSTTax = BigDecimal.ZERO;
	private BigDecimal CGSTTax = BigDecimal.ZERO;
	private BigDecimal SGSTTax = BigDecimal.ZERO;
	private BigDecimal CessTax = BigDecimal.ZERO;
	private BigDecimal TotalCreditEligible = BigDecimal.ZERO;
	private BigDecimal EligibleIGST = BigDecimal.ZERO;
	private BigDecimal EligibleCGST = BigDecimal.ZERO;
	private BigDecimal EligibleSGST = BigDecimal.ZERO;
	private BigDecimal EligibleCess = BigDecimal.ZERO;
	public String getGSTIN() {
		return GSTIN;
	}
	public void setGSTIN(String gSTIN) {
		GSTIN = gSTIN;
	}
	public Integer getCount() {
		return Count;
	}
	public void setCount(Integer count) {
		Count = count;
	}
	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}
	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}
	public BigDecimal getTotalTax() {
		return totalTax;
	}
	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}
	public BigDecimal getIGSTTax() {
		return IGSTTax;
	}
	public void setIGSTTax(BigDecimal iGSTTax) {
		IGSTTax = iGSTTax;
	}
	public BigDecimal getCGSTTax() {
		return CGSTTax;
	}
	public void setCGSTTax(BigDecimal cGSTTax) {
		CGSTTax = cGSTTax;
	}
	public BigDecimal getSGSTTax() {
		return SGSTTax;
	}
	public void setSGSTTax(BigDecimal sGSTTax) {
		SGSTTax = sGSTTax;
	}
	public BigDecimal getCessTax() {
		return CessTax;
	}
	public void setCessTax(BigDecimal cessTax) {
		CessTax = cessTax;
	}
	public BigDecimal getTotalCreditEligible() {
		return TotalCreditEligible;
	}
	public void setTotalCreditEligible(BigDecimal totalCreditEligible) {
		TotalCreditEligible = totalCreditEligible;
	}
	public BigDecimal getEligibleIGST() {
		return EligibleIGST;
	}
	public void setEligibleIGST(BigDecimal eligibleIGST) {
		EligibleIGST = eligibleIGST;
	}
	public BigDecimal getEligibleCGST() {
		return EligibleCGST;
	}
	public void setEligibleCGST(BigDecimal eligibleCGST) {
		EligibleCGST = eligibleCGST;
	}
	public BigDecimal getEligibleSGST() {
		return EligibleSGST;
	}
	public void setEligibleSGST(BigDecimal eligibleSGST) {
		EligibleSGST = eligibleSGST;
	}
	public BigDecimal getEligibleCess() {
		return EligibleCess;
	}
	public void setEligibleCess(BigDecimal eligibleCess) {
		EligibleCess = eligibleCess;
	}
	public String getTableDescription() {
		return TableDescription;
	}
	public void setTableDescription(String tableDescription) {
		TableDescription = tableDescription;
	}
	
	
	
	
	
}
