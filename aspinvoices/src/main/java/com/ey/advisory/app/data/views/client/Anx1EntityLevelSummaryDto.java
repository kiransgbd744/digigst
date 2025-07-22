package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

/**
 * @author Sujith.Nanga
 *
 * 
 */

public class Anx1EntityLevelSummaryDto {

	private String GSTIN;
	private Integer AspCount;
	private BigDecimal AspTaxableValue = BigDecimal.ZERO;
	private String AspSuppliesReturned;
	private String AspNetValue;
	private BigDecimal AspIGST = BigDecimal.ZERO;
	private BigDecimal AspCGST = BigDecimal.ZERO;
	private BigDecimal AspSGST = BigDecimal.ZERO;
	private BigDecimal AspCess = BigDecimal.ZERO;
	private BigDecimal AspInvoiceValue = BigDecimal.ZERO;
	private String TableNo;
	private String TableDescription;
	private Integer MemoCount;
	private BigDecimal MemoTaxableValue = BigDecimal.ZERO;
	private BigDecimal MemoSuppliesReturned = BigDecimal.ZERO;
	private BigDecimal MemoNetValue = BigDecimal.ZERO;
	private BigDecimal MemoIGST = BigDecimal.ZERO;
	private BigDecimal MemoCGST = BigDecimal.ZERO;
	private BigDecimal MemoSGST = BigDecimal.ZERO;
	private BigDecimal MemoCess = BigDecimal.ZERO;
	private BigDecimal MemoInvoiceValue = BigDecimal.ZERO;
	private Integer AvailableCount;
	private BigDecimal AvailableTaxableValue = BigDecimal.ZERO;
	private BigDecimal AvailableSuppliesReturned = BigDecimal.ZERO;
	private BigDecimal AvailableNetValue = BigDecimal.ZERO;
	private BigDecimal AvailableIGST = BigDecimal.ZERO;
	private BigDecimal AvailableCGST = BigDecimal.ZERO;
	private BigDecimal AvailableSGST = BigDecimal.ZERO;
	private BigDecimal AvailableCess = BigDecimal.ZERO;
	private BigDecimal AvailableInvoiceValue = BigDecimal.ZERO;
	private Integer diffCount;
	private BigDecimal diffTaxableValue = BigDecimal.ZERO;
	private BigDecimal diffSuppliesReturned = BigDecimal.ZERO;
	private BigDecimal diffNetValue = BigDecimal.ZERO;
	private BigDecimal diffIGST = BigDecimal.ZERO;
	private BigDecimal diffCGST = BigDecimal.ZERO;
	private BigDecimal diffSGST = BigDecimal.ZERO;
	private BigDecimal diffCess = BigDecimal.ZERO;
	private BigDecimal diffInvoiceValue = BigDecimal.ZERO;

	public String getGSTIN() {
		return GSTIN;
	}

	public void setGSTIN(String gSTIN) {
		GSTIN = gSTIN;
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

	public String getAspSuppliesReturned() {
		return AspSuppliesReturned;
	}

	public void setAspSuppliesReturned(String aspSuppliesReturned) {
		AspSuppliesReturned = aspSuppliesReturned;
	}

	public String getAspNetValue() {
		return AspNetValue;
	}

	public void setAspNetValue(String aspNetValue) {
		AspNetValue = aspNetValue;
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

	public String getTableNo() {
		return TableNo;
	}

	public void setTableNo(String tableNo) {
		TableNo = tableNo;
	}

	public String getTableDescription() {
		return TableDescription;
	}

	public void setTableDescription(String tableDescription) {
		TableDescription = tableDescription;
	}

	public Integer getMemoCount() {
		return MemoCount;
	}

	public void setMemoCount(Integer memoCount) {
		MemoCount = memoCount;
	}

	public BigDecimal getMemoTaxableValue() {
		return MemoTaxableValue;
	}

	public void setMemoTaxableValue(BigDecimal memoTaxableValue) {
		MemoTaxableValue = memoTaxableValue;
	}

	public BigDecimal getMemoSuppliesReturned() {
		return MemoSuppliesReturned;
	}

	public void setMemoSuppliesReturned(BigDecimal memoSuppliesReturned) {
		MemoSuppliesReturned = memoSuppliesReturned;
	}

	public BigDecimal getMemoNetValue() {
		return MemoNetValue;
	}

	public void setMemoNetValue(BigDecimal memoNetValue) {
		MemoNetValue = memoNetValue;
	}

	public BigDecimal getMemoIGST() {
		return MemoIGST;
	}

	public void setMemoIGST(BigDecimal memoIGST) {
		MemoIGST = memoIGST;
	}

	public BigDecimal getMemoCGST() {
		return MemoCGST;
	}

	public void setMemoCGST(BigDecimal memoCGST) {
		MemoCGST = memoCGST;
	}

	public BigDecimal getMemoSGST() {
		return MemoSGST;
	}

	public void setMemoSGST(BigDecimal memoSGST) {
		MemoSGST = memoSGST;
	}

	public BigDecimal getMemoCess() {
		return MemoCess;
	}

	public void setMemoCess(BigDecimal memoCess) {
		MemoCess = memoCess;
	}

	public BigDecimal getMemoInvoiceValue() {
		return MemoInvoiceValue;
	}

	public void setMemoInvoiceValue(BigDecimal memoInvoiceValue) {
		MemoInvoiceValue = memoInvoiceValue;
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

	public BigDecimal getAvailableSuppliesReturned() {
		return AvailableSuppliesReturned;
	}

	public void setAvailableSuppliesReturned(
			BigDecimal availableSuppliesReturned) {
		AvailableSuppliesReturned = availableSuppliesReturned;
	}

	public BigDecimal getAvailableNetValue() {
		return AvailableNetValue;
	}

	public void setAvailableNetValue(BigDecimal availableNetValue) {
		AvailableNetValue = availableNetValue;
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

	public BigDecimal getDiffSuppliesReturned() {
		return diffSuppliesReturned;
	}

	public void setDiffSuppliesReturned(BigDecimal diffSuppliesReturned) {
		this.diffSuppliesReturned = diffSuppliesReturned;
	}

	public BigDecimal getDiffNetValue() {
		return diffNetValue;
	}

	public void setDiffNetValue(BigDecimal diffNetValue) {
		this.diffNetValue = diffNetValue;
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

	@Override
	public String toString() {
		return "Anx1EntityLevelSummaryDto [GSTIN=" + GSTIN + ", AspCount="
				+ AspCount + ", AspTaxableValue=" + AspTaxableValue
				+ ", AspSuppliesReturned=" + AspSuppliesReturned
				+ ", AspNetValue=" + AspNetValue + ", AspIGST=" + AspIGST
				+ ", AspCGST=" + AspCGST + ", AspSGST=" + AspSGST + ", AspCess="
				+ AspCess + ", AspInvoiceValue=" + AspInvoiceValue
				+ ", TableNo=" + TableNo + ", TableDescription="
				+ TableDescription + ", MemoCount=" + MemoCount
				+ ", MemoTaxableValue=" + MemoTaxableValue
				+ ", MemoSuppliesReturned=" + MemoSuppliesReturned
				+ ", MemoNetValue=" + MemoNetValue + ", MemoIGST=" + MemoIGST
				+ ", MemoCGST=" + MemoCGST + ", MemoSGST=" + MemoSGST
				+ ", MemoCess=" + MemoCess + ", MemoInvoiceValue="
				+ MemoInvoiceValue + ", AvailableCount=" + AvailableCount
				+ ", AvailableTaxableValue=" + AvailableTaxableValue
				+ ", AvailableSuppliesReturned=" + AvailableSuppliesReturned
				+ ", AvailableNetValue=" + AvailableNetValue
				+ ", AvailableIGST=" + AvailableIGST + ", AvailableCGST="
				+ AvailableCGST + ", AvailableSGST=" + AvailableSGST
				+ ", AvailableCess=" + AvailableCess
				+ ", AvailableInvoiceValue=" + AvailableInvoiceValue
				+ ", diffCount=" + diffCount + ", diffTaxableValue="
				+ diffTaxableValue + ", diffSuppliesReturned="
				+ diffSuppliesReturned + ", diffNetValue=" + diffNetValue
				+ ", diffIGST=" + diffIGST + ", diffCGST=" + diffCGST
				+ ", diffSGST=" + diffSGST + ", diffCess=" + diffCess
				+ ", diffInvoiceValue=" + diffInvoiceValue + "]";
	}

}
