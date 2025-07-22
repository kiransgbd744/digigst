/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GETGSTR1_TXP_TXPA_DETAILS")
public class GetGstr1TXPInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstinOfTheTaxpayer;

	@Column(name = "RET_PERIOD")
	private String retPeriod;

	@Column(name = "FLAG")
	private String invoiceStatus;

	@Column(name = "CHKSUM")
	private String invoiceCheckSum;

	@Column(name = "ORG_MONTH")
	private String originalMonth;

	@Column(name = "POS")
	private String placeOfSupply;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercent;

	@Column(name = "RATE")
	private BigDecimal rateOfInvoice;

	@Column(name = "AD_AMT")
	private BigDecimal advanceToBeAdjested;

	@Column(name = "IGST_AMT")
	private BigDecimal iGstAmount;

	@Column(name = "CGST_AMT")
	private BigDecimal cGstAmount;

	@Column(name = "SGST_AMT")
	private BigDecimal sGstAmount;

	@Column(name = "CESS_AMT")
	private BigDecimal csGstAmount;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	*//**
	 * @return the id
	 *//*
	public Long getId() {
		return id;
	}

	*//**
	 * @param id
	 *            the id to set
	 *//*
	public void setId(Long id) {
		this.id = id;
	}

	*//**
	 * @return the originalMonth
	 *//*
	public String getOriginalMonth() {
		return originalMonth;
	}

	*//**
	 * @param originalMonth
	 *            the originalMonth to set
	 *//*
	public void setOriginalMonth(String originalMonth) {
		this.originalMonth = originalMonth;
	}

	*//**
	 * @return the gstinOfTheTaxpayer
	 *//*
	public String getGstinOfTheTaxpayer() {
		return gstinOfTheTaxpayer;
	}

	*//**
	 * @param gstinOfTheTaxpayer
	 *            the gstinOfTheTaxpayer to set
	 *//*
	public void setGstinOfTheTaxpayer(String gstinOfTheTaxpayer) {
		this.gstinOfTheTaxpayer = gstinOfTheTaxpayer;
	}

	*//**
	 * @return the retPeriod
	 *//*
	public String getRetPeriod() {
		return retPeriod;
	}

	*//**
	 * @param retPeriod
	 *            the retPeriod to set
	 *//*
	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	*//**
	 * @return the invoiceStatus
	 *//*
	public String getInvoiceStatus() {
		return invoiceStatus;
	}

	*//**
	 * @param invoiceStatus
	 *            the invoiceStatus to set
	 *//*
	public void setInvoiceStatus(String invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}

	*//**
	 * @return the invoiceCheckSum
	 *//*
	public String getInvoiceCheckSum() {
		return invoiceCheckSum;
	}

	*//**
	 * @param invoiceCheckSum
	 *            the invoiceCheckSum to set
	 *//*
	public void setInvoiceCheckSum(String invoiceCheckSum) {
		this.invoiceCheckSum = invoiceCheckSum;
	}

	*//**
	 * @return the placeOfSupply
	 *//*
	public String getPlaceOfSupply() {
		return placeOfSupply;
	}

	*//**
	 * @param placeOfSupply
	 *            the placeOfSupply to set
	 *//*
	public void setPlaceOfSupply(String placeOfSupply) {
		this.placeOfSupply = placeOfSupply;
	}

	*//**
	 * @return the supplyType
	 *//*
	public String getSupplyType() {
		return supplyType;
	}

	*//**
	 * @param supplyType
	 *            the supplyType to set
	 *//*
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	*//**
	 * @return the diffPercent
	 *//*
	public BigDecimal getDiffPercent() {
		return diffPercent;
	}

	*//**
	 * @param diffPercent
	 *            the diffPercent to set
	 *//*
	public void setDiffPercent(BigDecimal diffPercent) {
		this.diffPercent = diffPercent;
	}

	*//**
	 * @return the rateOfInvoice
	 *//*
	public BigDecimal getRateOfInvoice() {
		return rateOfInvoice;
	}

	*//**
	 * @param rateOfInvoice
	 *            the rateOfInvoice to set
	 *//*
	public void setRateOfInvoice(BigDecimal rateOfInvoice) {
		this.rateOfInvoice = rateOfInvoice;
	}

	*//**
	 * @return the advanceToBeAdjested
	 *//*
	public BigDecimal getAdvanceToBeAdjested() {
		return advanceToBeAdjested;
	}

	*//**
	 * @param advanceToBeAdjested
	 *            the advanceToBeAdjested to set
	 *//*
	public void setAdvanceToBeAdjested(BigDecimal advanceToBeAdjested) {
		this.advanceToBeAdjested = advanceToBeAdjested;
	}

	*//**
	 * @return the iGstAmount
	 *//*
	public BigDecimal getiGstAmount() {
		return iGstAmount;
	}

	*//**
	 * @param iGstAmount
	 *            the iGstAmount to set
	 *//*
	public void setiGstAmount(BigDecimal iGstAmount) {
		this.iGstAmount = iGstAmount;
	}

	*//**
	 * @return the cGstAmount
	 *//*
	public BigDecimal getcGstAmount() {
		return cGstAmount;
	}

	*//**
	 * @param cGstAmount
	 *            the cGstAmount to set
	 *//*
	public void setcGstAmount(BigDecimal cGstAmount) {
		this.cGstAmount = cGstAmount;
	}

	*//**
	 * @return the sGstAmount
	 *//*
	public BigDecimal getsGstAmount() {
		return sGstAmount;
	}

	*//**
	 * @param sGstAmount
	 *            the sGstAmount to set
	 *//*
	public void setsGstAmount(BigDecimal sGstAmount) {
		this.sGstAmount = sGstAmount;
	}

	*//**
	 * @return the csGstAmount
	 *//*
	public BigDecimal getCsGstAmount() {
		return csGstAmount;
	}

	*//**
	 * @param csGstAmount
	 *            the csGstAmount to set
	 *//*
	public void setCsGstAmount(BigDecimal csGstAmount) {
		this.csGstAmount = csGstAmount;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 
	@Override
	public String toString() {
		return "GetGstr1TXPInvoicesEntity [id=" + id + ", gstinOfTheTaxpayer="
				+ gstinOfTheTaxpayer + ", retPeriod=" + retPeriod
				+ ", invoiceStatus=" + invoiceStatus + ", invoiceCheckSum="
				+ invoiceCheckSum + ", originalMonth=" + originalMonth
				+ ", placeOfSupply=" + placeOfSupply + ", supplyType="
				+ supplyType + ", diffPercent=" + diffPercent
				+ ", rateOfInvoice=" + rateOfInvoice + ", advanceToBeAdjested="
				+ advanceToBeAdjested + ", iGstAmount=" + iGstAmount
				+ ", cGstAmount=" + cGstAmount + ", sGstAmount=" + sGstAmount
				+ ", csGstAmount=" + csGstAmount + ", isDelete=" + isDelete
				+ "]";
	}

}
*/