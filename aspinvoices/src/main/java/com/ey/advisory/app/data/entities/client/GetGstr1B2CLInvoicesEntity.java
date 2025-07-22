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
@Table(name = "GETGSTR1_B2CL_B2CLA_DETAILS")
public class GetGstr1B2CLInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "GSTIN")
	protected String sgstin;

	@Column(name = "RET_PERIOD")
	protected String returnPeriod;

	@Column(name = "STATE_CODE")
	protected String stateCode;

	@Column(name = "POS")
	protected String pos;

	@Column(name = "FLAG")
	protected String flag;

	@Column(name = "CHKSUM")
	protected String chkSum;

	@Column(name = "ORG_INV_NUM")
	protected String oinum;

	@Column(name = "ORG_INV_DATE")
	protected LocalDate oiDate;

	@Column(name = "SUPP_INV_NUM")
	protected String siNum;

	@Column(name = "SUPP_INV_DATE")
	protected LocalDate siDate;

	@Column(name = "SUPP_INV_VAL")
	protected BigDecimal siVal;

	@Column(name = "ECOM_GSTIN")
	protected String eGstin;

	@Column(name = "INV_TYPE")
	protected String invType;

	@Column(name = "DIFF_PERCENT")
	protected BigDecimal DiffPercent;

	@Column(name = "SERIAL_NUM")
	protected Integer sNum;

	@Column(name = "RATE")
	protected BigDecimal Rate;

	@Column(name = "TAX_VAL")
	protected BigDecimal TaxableValue;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal cessAmt;

	@Column(name = "TOKEN")
	protected String token;

	@Column(name = "EST_TIME")
	protected String eTime;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	*//**
	 * @return the siVal
	 *//*
	public BigDecimal getSiVal() {
		return siVal;
	}

	*//**
	 * @param siVal
	 *            the siVal to set
	 *//*
	public void setSiVal(BigDecimal siVal) {
		this.siVal = siVal;
	}

	public Long getId() {
		return id;
	}

	*//**
	 * @return the pos
	 *//*
	public String getPos() {
		return pos;
	}

	*//**
	 * @param pos
	 *            the pos to set
	 *//*
	public void setPos(String pos) {
		this.pos = pos;
	}

	*//**
	 * @return the chkSum
	 *//*
	public String getChkSum() {
		return chkSum;
	}

	public String getInvType() {
		return invType;
	}

	public BigDecimal getDiffPercent() {
		return DiffPercent;
	}

	public BigDecimal getTaxableValue() {
		return TaxableValue;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setChkSum(String chkSum) {
		this.chkSum = chkSum;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public void setDiffPercent(BigDecimal diffPercent) {
		DiffPercent = diffPercent;
	}

	public void setTaxableValue(BigDecimal taxableValue) {
		TaxableValue = taxableValue;
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

	public String getOinum() {
		return oinum;
	}

	public void setOinum(String oinum) {
		this.oinum = oinum;
	}

	*//**
	 * @return the stateCode
	 *//*
	public String getStateCode() {
		return stateCode;
	}

	*//**
	 * @param stateCode
	 *            the stateCode to set
	 *//*
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	*//**
	 * @return the flag
	 *//*
	public String getFlag() {
		return flag;
	}

	*//**
	 * @param flag
	 *            the flag to set
	 *//*
	public void setFlag(String flag) {
		this.flag = flag;
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
	 * @return the siNum
	 *//*
	public String getSiNum() {
		return siNum;
	}

	*//**
	 * @param siNum
	 *            the siNum to set
	 *//*
	public void setSiNum(String siNum) {
		this.siNum = siNum;
	}

	*//**
	 * @return the siDate
	 *//*
	public LocalDate getSiDate() {
		return siDate;
	}

	*//**
	 * @param siDate
	 *            the siDate to set
	 *//*
	public void setSiDate(LocalDate siDate) {
		this.siDate = siDate;
	}

	*//**
	 * @return the eGstin
	 *//*
	public String geteGstin() {
		return eGstin;
	}

	*//**
	 * @param eGstin
	 *            the eGstin to set
	 *//*
	public void seteGstin(String eGstin) {
		this.eGstin = eGstin;
	}

	*//**
	 * @return the sNum
	 *//*
	public Integer getsNum() {
		return sNum;
	}

	*//**
	 * @param sNum
	 *            the sNum to set
	 *//*
	public void setsNum(Integer sNum) {
		this.sNum = sNum;
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
	 * @return the eTime
	 *//*
	public String geteTime() {
		return eTime;
	}

	*//**
	 * @param eTime
	 *            the eTime to set
	 *//*
	public void seteTime(String eTime) {
		this.eTime = eTime;
	}

	*//**
	 * @return the token
	 *//*
	public String getToken() {
		return token;
	}

	*//**
	 * @param token
	 *            the token to set
	 *//*
	public void setToken(String token) {
		this.token = token;
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
		return "GetGstr1B2CLInvoicesEntity [id=" + id + ", sgstin=" + sgstin
				+ ", returnPeriod=" + returnPeriod + ", stateCode=" + stateCode
				+ ", pos=" + pos + ", flag=" + flag + ", chkSum=" + chkSum
				+ ", oinum=" + oinum + ", oiDate=" + oiDate + ", siNum=" + siNum
				+ ", siDate=" + siDate + ", siVal=" + siVal + ", eGstin="
				+ eGstin + ", invType=" + invType + ", DiffPercent="
				+ DiffPercent + ", sNum=" + sNum + ", Rate=" + Rate
				+ ", TaxableValue=" + TaxableValue + ", igstAmt=" + igstAmt
				+ ", cessAmt=" + cessAmt + ", token=" + token + ", eTime="
				+ eTime + ", isDelete=" + isDelete + "]";
	}
}
*/