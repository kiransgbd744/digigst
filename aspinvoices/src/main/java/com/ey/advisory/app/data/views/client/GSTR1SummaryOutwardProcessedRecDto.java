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
public class GSTR1SummaryOutwardProcessedRecDto {

	private String tableType;
	private Integer aspCount;
	private BigDecimal aspInvoiceValue = BigDecimal.ZERO;
	private BigDecimal AspTaxableValue = BigDecimal.ZERO;
	private BigDecimal aspTotalTax = BigDecimal.ZERO;
	private BigDecimal aspIGST = BigDecimal.ZERO;
	private BigDecimal aspCGST = BigDecimal.ZERO;
	private BigDecimal aspSGST = BigDecimal.ZERO;
	private BigDecimal aspCess = BigDecimal.ZERO;
	private Integer availableCount;
	private BigDecimal availableInvoiceValue = BigDecimal.ZERO;
	private BigDecimal availableTaxableValue = BigDecimal.ZERO;
	private BigDecimal availableTotalTax = BigDecimal.ZERO;
	private BigDecimal availableIGST = BigDecimal.ZERO;
	private BigDecimal availableCGST = BigDecimal.ZERO;
	private BigDecimal availableSGST = BigDecimal.ZERO;
	private BigDecimal availableCess = BigDecimal.ZERO;
	private Integer diffCount;
	private BigDecimal diffInvoiceValue = BigDecimal.ZERO;
	private BigDecimal diffTaxableValue = BigDecimal.ZERO;
	private BigDecimal diffTotalTax = BigDecimal.ZERO;
	private BigDecimal diffIGST = BigDecimal.ZERO;
	private BigDecimal diffCGST = BigDecimal.ZERO;
	private BigDecimal diffSGST = BigDecimal.ZERO;
	private BigDecimal diffCess = BigDecimal.ZERO;

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public Integer getAspCount() {
		return aspCount;
	}

	public void setAspCount(Integer aspCount) {
		this.aspCount = aspCount;
	}

	public BigDecimal getAspInvoiceValue() {
		return aspInvoiceValue;
	}

	public void setAspInvoiceValue(BigDecimal aspInvoiceValue) {
		this.aspInvoiceValue = aspInvoiceValue;
	}

	public BigDecimal getAspTaxableValue() {
		return AspTaxableValue;
	}

	public void setAspTaxableValue(BigDecimal aspTaxableValue) {
		AspTaxableValue = aspTaxableValue;
	}

	public BigDecimal getAspTotalTax() {
		return aspTotalTax;
	}

	public void setAspTotalTax(BigDecimal aspTotalTax) {
		this.aspTotalTax = aspTotalTax;
	}

	public BigDecimal getAspIGST() {
		return aspIGST;
	}

	public void setAspIGST(BigDecimal aspIGST) {
		this.aspIGST = aspIGST;
	}

	public BigDecimal getAspCGST() {
		return aspCGST;
	}

	public void setAspCGST(BigDecimal aspCGST) {
		this.aspCGST = aspCGST;
	}

	public BigDecimal getAspSGST() {
		return aspSGST;
	}

	public void setAspSGST(BigDecimal aspSGST) {
		this.aspSGST = aspSGST;
	}

	public BigDecimal getAspCess() {
		return aspCess;
	}

	public void setAspCess(BigDecimal aspCess) {
		this.aspCess = aspCess;
	}

	public Integer getAvailableCount() {
		return availableCount;
	}

	public void setAvailableCount(Integer availableCount) {
		this.availableCount = availableCount;
	}

	public BigDecimal getAvailableInvoiceValue() {
		return availableInvoiceValue;
	}

	public void setAvailableInvoiceValue(BigDecimal availableInvoiceValue) {
		this.availableInvoiceValue = availableInvoiceValue;
	}

	public BigDecimal getAvailableTaxableValue() {
		return availableTaxableValue;
	}

	public void setAvailableTaxableValue(BigDecimal availableTaxableValue) {
		this.availableTaxableValue = availableTaxableValue;
	}

	public BigDecimal getAvailableTotalTax() {
		return availableTotalTax;
	}

	public void setAvailableTotalTax(BigDecimal availableTotalTax) {
		this.availableTotalTax = availableTotalTax;
	}

	public BigDecimal getAvailableIGST() {
		return availableIGST;
	}

	public void setAvailableIGST(BigDecimal availableIGST) {
		this.availableIGST = availableIGST;
	}

	public BigDecimal getAvailableCGST() {
		return availableCGST;
	}

	public void setAvailableCGST(BigDecimal availableCGST) {
		this.availableCGST = availableCGST;
	}

	public BigDecimal getAvailableSGST() {
		return availableSGST;
	}

	public void setAvailableSGST(BigDecimal availableSGST) {
		this.availableSGST = availableSGST;
	}

	public BigDecimal getAvailableCess() {
		return availableCess;
	}

	public void setAvailableCess(BigDecimal availableCess) {
		this.availableCess = availableCess;
	}

	public Integer getDiffCount() {
		return diffCount;
	}

	public void setDiffCount(Integer diffCount) {
		this.diffCount = diffCount;
	}

	public BigDecimal getDiffInvoiceValue() {
		return diffInvoiceValue;
	}

	public void setDiffInvoiceValue(BigDecimal diffInvoiceValue) {
		this.diffInvoiceValue = diffInvoiceValue;
	}

	public BigDecimal getDiffTaxableValue() {
		return diffTaxableValue;
	}

	public void setDiffTaxableValue(BigDecimal diffTaxableValue) {
		this.diffTaxableValue = diffTaxableValue;
	}

	public BigDecimal getDiffTotalTax() {
		return diffTotalTax;
	}

	public void setDiffTotalTax(BigDecimal diffTotalTax) {
		this.diffTotalTax = diffTotalTax;
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

	@Override
	public String toString() {
		return "GSTR1SummaryOutwardProcessedRecDto [tableType=" + tableType
				+ ", aspCount=" + aspCount + ", aspInvoiceValue="
				+ aspInvoiceValue + ", AspTaxableValue=" + AspTaxableValue
				+ ", aspTotalTax=" + aspTotalTax + ", aspIGST=" + aspIGST
				+ ", aspCGST=" + aspCGST + ", aspSGST=" + aspSGST + ", aspCess="
				+ aspCess + ", availableCount=" + availableCount
				+ ", availableInvoiceValue=" + availableInvoiceValue
				+ ", availableTaxableValue=" + availableTaxableValue
				+ ", availableTotalTax=" + availableTotalTax
				+ ", availableIGST=" + availableIGST + ", availableCGST="
				+ availableCGST + ", availableSGST=" + availableSGST
				+ ", availableCess=" + availableCess + ", diffCount="
				+ diffCount + ", diffInvoiceValue=" + diffInvoiceValue
				+ ", diffTaxableValue=" + diffTaxableValue + ", diffTotalTax="
				+ diffTotalTax + ", diffIGST=" + diffIGST + ", diffCGST="
				+ diffCGST + ", diffSGST=" + diffSGST + ", diffCess=" + diffCess
				+ "]";
	}

}
