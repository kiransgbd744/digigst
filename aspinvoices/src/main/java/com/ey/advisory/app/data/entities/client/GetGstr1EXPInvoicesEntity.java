/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GSTR1_EXP_EXPA_DETAILS")
public class GetGstr1EXPInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String sgstin;

	@Column(name = "RET_PERIOD")
	private String returnPeriod;

	@Column(name = "EXP_TYPE")
	private String exportType;

	@Column(name = "FLAG")
	private String invoiceStatus;

	@Column(name = "CHKSUM")
	private String chkSum;

	@Column(name = "SUP_INV_NUM")
	private String invoiceNum;

	@Column(name = "SUP_INV_DATE")
	private LocalDate invoiceDate;

	@Column(name = "SUP_INV_VAL")
	private BigDecimal invoiceValue;

	@Column(name = "SBP_CODE")
	private String shipBillPortCode;

	@Column(name = "SB_NUM")
	private String shipBillNum;

	@Column(name = "SB_DATE")
	private LocalDate shipBillDate;

	@Column(name = "COUN_GSTIN")
	private String couPartyGstn;

	@Column(name = "ORG_INV_NUM")
	private String oidNum;

	@Column(name = "ORG_INV_DATE")
	private LocalDate oiDate;

	@Column(name = "DIFF_PERCENT")
	private BigDecimal diffPercent;

	@Column(name = "RATE")
	private BigDecimal Rate;

	@Column(name = "TAX_VAL")
	private BigDecimal taxableValue;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	*//**
	 * @return the exportType
	 *//*
	public String getExportType() {
		return exportType;
	}

	*//**
	 * @param exportType
	 *            the exportType to set
	 *//*
	public void setExportType(String exportType) {
		this.exportType = exportType;
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
	 * @return the invoiceNum
	 *//*
	public String getInvoiceNum() {
		return invoiceNum;
	}

	*//**
	 * @param invoiceNum
	 *            the invoiceNum to set
	 *//*
	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}

	*//**
	 * @return the invoiceDate
	 *//*
	public LocalDate getInvoiceDate() {
		return invoiceDate;
	}

	*//**
	 * @param invoiceDate
	 *            the invoiceDate to set
	 *//*
	public void setInvoiceDate(LocalDate invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	*//**
	 * @return the invoiceValue
	 *//*
	public BigDecimal getInvoiceValue() {
		return invoiceValue;
	}

	*//**
	 * @param invoiceValue
	 *            the invoiceValue to set
	 *//*
	public void setInvoiceValue(BigDecimal invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	*//**
	 * @return the shipBillPortCode
	 *//*
	public String getShipBillPortCode() {
		return shipBillPortCode;
	}

	*//**
	 * @param shipBillPortCode
	 *            the shipBillPortCode to set
	 *//*
	public void setShipBillPortCode(String shipBillPortCode) {
		this.shipBillPortCode = shipBillPortCode;
	}

	*//**
	 * @return the shipBillNum
	 *//*
	public String getShipBillNum() {
		return shipBillNum;
	}

	*//**
	 * @param shipBillNum
	 *            the shipBillNum to set
	 *//*
	public void setShipBillNum(String shipBillNum) {
		this.shipBillNum = shipBillNum;
	}

	*//**
	 * @return the shipBillDate
	 *//*
	public LocalDate getShipBillDate() {
		return shipBillDate;
	}

	*//**
	 * @param shipBillDate
	 *            the shipBillDate to set
	 *//*
	public void setShipBillDate(LocalDate shipBillDate) {
		this.shipBillDate = shipBillDate;
	}

	*//**
	 * @return the couPartyGstn
	 *//*
	public String getCouPartyGstn() {
		return couPartyGstn;
	}

	*//**
	 * @param couPartyGstn
	 *            the couPartyGstn to set
	 *//*
	public void setCouPartyGstn(String couPartyGstn) {
		this.couPartyGstn = couPartyGstn;
	}

	public Long getId() {
		return id;
	}

	*//**
	 * @return the chkSum
	 *//*
	public String getChkSum() {
		return chkSum;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setChkSum(String chkSum) {
		this.chkSum = chkSum;
	}

	public String getSgstin() {
		return sgstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	*//**
	 * @return the rate
	 *//*
	public BigDecimal getRate() {
		return Rate;
	}

	*//**
	 * @param rate
	 *            the rate to set
	 *//*
	public void setRate(BigDecimal rate) {
		Rate = rate;
	}

	*//**
	 * @return the igstAmt
	 *//*
	public BigDecimal getIgstAmt() {
		return igstAmt;
	}

	*//**
	 * @param igstAmt
	 *            the igstAmt to set
	 *//*
	public void setIgstAmt(BigDecimal igstAmt) {
		this.igstAmt = igstAmt;
	}

	*//**
	 * @return the cessAmt
	 *//*
	public BigDecimal getCessAmt() {
		return cessAmt;
	}

	*//**
	 * @param cessAmt
	 *            the cessAmt to set
	 *//*
	public void setCessAmt(BigDecimal cessAmt) {
		this.cessAmt = cessAmt;
	}

	*//**
	 * @return the oidNum
	 *//*
	public String getOidNum() {
		return oidNum;
	}

	*//**
	 * @param oidNum
	 *            the oidNum to set
	 *//*
	public void setOidNum(String oidNum) {
		this.oidNum = oidNum;
	}

	*//**
	 * @return the oiDate
	 *//*
	public LocalDate getOiDate() {
		return oiDate;
	}

	*//**
	 * @param oiDate
	 *            the oiDate to set
	 *//*
	public void setOiDate(LocalDate oiDate) {
		this.oiDate = oiDate;
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
	 * @return the taxableValue
	 *//*
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	*//**
	 * @param taxableValue
	 *            the taxableValue to set
	 *//*
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;

	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;

	}

	@Override
	public String toString() {
		return "GetGstr1EXPInvoicesEntity [id=" + id + ", sgstin=" + sgstin
				+ ", returnPeriod=" + returnPeriod + ", exportType="
				+ exportType + ", invoiceStatus=" + invoiceStatus + ", chkSum="
				+ chkSum + ", invoiceNum=" + invoiceNum + ", invoiceDate="
				+ invoiceDate + ", invoiceValue=" + invoiceValue
				+ ", shipBillPortCode=" + shipBillPortCode + ", shipBillNum="
				+ shipBillNum + ", shipBillDate=" + shipBillDate
				+ ", couPartyGstn=" + couPartyGstn + ", oidNum=" + oidNum
				+ ", oiDate=" + oiDate + ", diffPercent=" + diffPercent
				+ ", Rate=" + Rate + ", taxableValue=" + taxableValue
				+ ", igstAmt=" + igstAmt + ", cessAmt=" + cessAmt
				+ ", isDelete=" + isDelete + "]";
	}

}
*/