/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GETGSTR1_NIL_RATED_SUPPLIES_DETAILS")
public class GetGstr1NilDetailsInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String taxPayerOfGstn;

	@Column(name = "RET_PERIOD")
	private String ret_period;

	@Column(name = "FLAG")
	private String flag;

	@Column(name = "CHKSUM")
	private String checkSum;

	@Column(name = "NIL_AMT")
	private BigDecimal totalNilRatedOutwordSup;

	@Column(name = "EXPT_AMT")
	private BigDecimal totalExemptedOutwordSup;

	@Column(name = "NGSUP_AMT")
	private BigDecimal totalNonGstOutwordSup;

	@Column(name = "SUPPLY_TYPE")
	private String natureOfSupType;

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
	 * @return the taxPayerOfGstn
	 *//*
	public String getTaxPayerOfGstn() {
		return taxPayerOfGstn;
	}

	*//**
	 * @param taxPayerOfGstn
	 *            the taxPayerOfGstn to set
	 *//*
	public void setTaxPayerOfGstn(String taxPayerOfGstn) {
		this.taxPayerOfGstn = taxPayerOfGstn;
	}

	*//**
	 * @return the ret_period
	 *//*
	public String getRet_period() {
		return ret_period;
	}

	*//**
	 * @param ret_period
	 *            the ret_period to set
	 *//*
	public void setRet_period(String ret_period) {
		this.ret_period = ret_period;
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
	 * @return the checkSum
	 *//*
	public String getCheckSum() {
		return checkSum;
	}

	*//**
	 * @param checkSum
	 *            the checkSum to set
	 *//*
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	*//**
	 * @return the totalNilRatedOutwordSup
	 *//*
	public BigDecimal getTotalNilRatedOutwordSup() {
		return totalNilRatedOutwordSup;
	}

	*//**
	 * @param totalNilRatedOutwordSup
	 *            the totalNilRatedOutwordSup to set
	 *//*
	public void setTotalNilRatedOutwordSup(BigDecimal totalNilRatedOutwordSup) {
		this.totalNilRatedOutwordSup = totalNilRatedOutwordSup;
	}

	*//**
	 * @return the totalExemptedOutwordSup
	 *//*
	public BigDecimal getTotalExemptedOutwordSup() {
		return totalExemptedOutwordSup;
	}

	*//**
	 * @param totalExemptedOutwordSup
	 *            the totalExemptedOutwordSup to set
	 *//*
	public void setTotalExemptedOutwordSup(BigDecimal totalExemptedOutwordSup) {
		this.totalExemptedOutwordSup = totalExemptedOutwordSup;
	}

	*//**
	 * @return the totalNonGstOutwordSup
	 *//*
	public BigDecimal getTotalNonGstOutwordSup() {
		return totalNonGstOutwordSup;
	}

	*//**
	 * @param totalNonGstOutwordSup
	 *            the totalNonGstOutwordSup to set
	 *//*
	public void setTotalNonGstOutwordSup(BigDecimal totalNonGstOutwordSup) {
		this.totalNonGstOutwordSup = totalNonGstOutwordSup;
	}

	*//**
	 * @return the natureOfSupType
	 *//*
	public String getNatureOfSupType() {
		return natureOfSupType;
	}

	*//**
	 * @param natureOfSupType
	 *            the natureOfSupType to set
	 *//*
	public void setNatureOfSupType(String natureOfSupType) {
		this.natureOfSupType = natureOfSupType;
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
		return "GetGstr1NilDetailsInvoicesEntity [id=" + id
				+ ", taxPayerOfGstn=" + taxPayerOfGstn + ", ret_period="
				+ ret_period + ", flag=" + flag + ", checkSum=" + checkSum
				+ ", totalNilRatedOutwordSup=" + totalNilRatedOutwordSup
				+ ", totalExemptedOutwordSup=" + totalExemptedOutwordSup
				+ ", totalNonGstOutwordSup=" + totalNonGstOutwordSup
				+ ", natureOfSupType=" + natureOfSupType + ", isDelete="
				+ isDelete + "]";
	}

}
*/