/**
 * 
 */
package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

/**
 * @author Sujith.Nanga
 *
 * 
 */
public class GSTR1EntityLevelSummaryDto {
	private String returnPeriod;
	private String GSTIN;
	private String TableNo;
	private String TableDescription;
	private String taxPeriod;
	private String taxDocType;
	private Integer AspCount = 0;
	private BigDecimal AspTaxableValue = BigDecimal.ZERO;
	private BigDecimal AspIGST = BigDecimal.ZERO;
	private BigDecimal AspCGST = BigDecimal.ZERO;
	private BigDecimal AspSGST = BigDecimal.ZERO;
	private BigDecimal AspCess = BigDecimal.ZERO;
	private BigDecimal AspInvoiceValue = BigDecimal.ZERO;
	private Integer AvailableCount;
	private BigDecimal AvailableTaxableValue = BigDecimal.ZERO;
	private BigDecimal AvailableIGST = BigDecimal.ZERO;
	private BigDecimal AvailableCGST = BigDecimal.ZERO;
	private BigDecimal AvailableSGST = BigDecimal.ZERO;
	private BigDecimal AvailableCess = BigDecimal.ZERO;
	private BigDecimal AvailableInvoiceValue = BigDecimal.ZERO;
	private Integer diffCount = 0;
	private BigDecimal diffTaxableValue = BigDecimal.ZERO;
	private BigDecimal diffIGST = BigDecimal.ZERO;
	private BigDecimal diffCGST = BigDecimal.ZERO;
	private BigDecimal diffSGST = BigDecimal.ZERO;
	private BigDecimal diffCess = BigDecimal.ZERO;
	private BigDecimal diffInvoiceValue = BigDecimal.ZERO;

	/**
	 * @return the returnPeriod
	 */
	public String getReturnPeriod() {
		return returnPeriod;
	}

	/**
	 * @param returnPeriod
	 *            the returnPeriod to set
	 */
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getTableNo() {
		return TableNo;
	}

	public void setTableNo(String tableNo) {
		TableNo = tableNo;
	}
	
	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}

	public String getTableDescription() {
		return TableDescription;
	}

	public void setTableDescription(String tableDescription) {
		TableDescription = tableDescription;
	}

	public Integer getAspCount() {
		return AspCount;
	}

	public void setAspCount(Integer aspCount) {
		AspCount = aspCount;
	}

	public BigDecimal getAspTaxableValue() {
		return AspTaxableValue;
	}

	public void setAspTaxableValue(BigDecimal aspTaxableValue) {
		AspTaxableValue = aspTaxableValue;
	}

	public BigDecimal getAspIGST() {
		return AspIGST;
	}

	public void setAspIGST(BigDecimal aspIGST) {
		AspIGST = aspIGST;
	}

	public BigDecimal getAspCGST() {
		return AspCGST;
	}

	public void setAspCGST(BigDecimal aspCGST) {
		AspCGST = aspCGST;
	}

	public BigDecimal getAspSGST() {
		return AspSGST;
	}

	public void setAspSGST(BigDecimal aspSGST) {
		AspSGST = aspSGST;
	}

	public BigDecimal getAspCess() {
		return AspCess;
	}

	public void setAspCess(BigDecimal aspCess) {
		AspCess = aspCess;
	}

	public BigDecimal getAspInvoiceValue() {
		return AspInvoiceValue;
	}

	public void setAspInvoiceValue(BigDecimal aspInvoiceValue) {
		AspInvoiceValue = aspInvoiceValue;
	}

	public Integer getAvailableCount() {
		return AvailableCount;
	}

	public void setAvailableCount(Integer availableCount) {
		AvailableCount = availableCount;
	}

	public BigDecimal getAvailableTaxableValue() {
		return AvailableTaxableValue;
	}

	public void setAvailableTaxableValue(BigDecimal availableTaxableValue) {
		AvailableTaxableValue = availableTaxableValue;
	}

	public BigDecimal getAvailableIGST() {
		return AvailableIGST;
	}

	public void setAvailableIGST(BigDecimal availableIGST) {
		AvailableIGST = availableIGST;
	}

	public BigDecimal getAvailableCGST() {
		return AvailableCGST;
	}

	public void setAvailableCGST(BigDecimal availableCGST) {
		AvailableCGST = availableCGST;
	}

	public BigDecimal getAvailableSGST() {
		return AvailableSGST;
	}

	public void setAvailableSGST(BigDecimal availableSGST) {
		AvailableSGST = availableSGST;
	}

	public BigDecimal getAvailableCess() {
		return AvailableCess;
	}

	public void setAvailableCess(BigDecimal availableCess) {
		AvailableCess = availableCess;
	}

	public BigDecimal getAvailableInvoiceValue() {
		return AvailableInvoiceValue;
	}

	public void setAvailableInvoiceValue(BigDecimal availableInvoiceValue) {
		AvailableInvoiceValue = availableInvoiceValue;
	}

	public Integer getDiffCount() {
		return diffCount;
	}

	public void setDiffCount(Integer diffCount) {
		this.diffCount = diffCount;
	}

	public BigDecimal getDiffTaxableValue() {
		return diffTaxableValue;
	}

	public void setDiffTaxableValue(BigDecimal diffTaxableValue) {
		this.diffTaxableValue = diffTaxableValue;
	}

	public BigDecimal getDiffIGST() {
		return diffIGST;
	}

	public void setDiffIGST(BigDecimal diffIGST) {
		this.diffIGST = diffIGST;
	}

	public BigDecimal getDiffCGST() {
		return diffCGST;
	}

	public void setDiffCGST(BigDecimal diffCGST) {
		this.diffCGST = diffCGST;
	}

	public BigDecimal getDiffSGST() {
		return diffSGST;
	}

	public void setDiffSGST(BigDecimal diffSGST) {
		this.diffSGST = diffSGST;
	}

	public BigDecimal getDiffCess() {
		return diffCess;
	}

	public void setDiffCess(BigDecimal diffCess) {
		this.diffCess = diffCess;
	}

	public BigDecimal getDiffInvoiceValue() {
		return diffInvoiceValue;
	}

	public void setDiffInvoiceValue(BigDecimal diffInvoiceValue) {
		this.diffInvoiceValue = diffInvoiceValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	/**
	 * @return the taxDocType
	 */
	public String getTaxDocType() {
		return taxDocType;
	}

	/**
	 * @param taxDocType
	 *            the taxDocType to set
	 */
	public void setTaxDocType(String taxDocType) {
		this.taxDocType = taxDocType;
	}

	/**
	 * @return the gSTIN
	 */
	public String getGSTIN() {
		return GSTIN;
	}

	/**
	 * @param gSTIN
	 *            the gSTIN to set
	 */
	public void setGSTIN(String gSTIN) {
		GSTIN = gSTIN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GSTR1EntityLevelSummaryDto [returnPeriod=" + returnPeriod
				+ ", GSTIN=" + GSTIN + ", TableNo=" + TableNo
				+ ", TableDescription=" + TableDescription + ", taxDocType="
				+ taxDocType + ", AspCount=" + AspCount + ", AspTaxableValue="
				+ AspTaxableValue + ", AspIGST=" + AspIGST + ", AspCGST="
				+ AspCGST + ", AspSGST=" + AspSGST + ", AspCess=" + AspCess
				+ ", AspInvoiceValue=" + AspInvoiceValue + ", AvailableCount="
				+ AvailableCount + ", AvailableTaxableValue="
				+ AvailableTaxableValue + ", AvailableIGST=" + AvailableIGST
				+ ", AvailableCGST=" + AvailableCGST + ", AvailableSGST="
				+ AvailableSGST + ", AvailableCess=" + AvailableCess
				+ ", AvailableInvoiceValue=" + AvailableInvoiceValue
				+ ", diffCount=" + diffCount + ", diffTaxableValue="
				+ diffTaxableValue + ", diffIGST=" + diffIGST + ", diffCGST="
				+ diffCGST + ", diffSGST=" + diffSGST + ", diffCess=" + diffCess
				+ ", diffInvoiceValue=" + diffInvoiceValue + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

}
