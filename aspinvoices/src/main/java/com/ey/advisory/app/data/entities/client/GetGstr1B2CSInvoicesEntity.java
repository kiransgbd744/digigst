/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GETGSTR1_B2CS_B2CSA_DETAILS")
public class GetGstr1B2CSInvoicesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Column(name = "GSTIN")
	protected String sgstin;

	@Column(name = "RET_PERIOD")
	private String returnPeriod;

	@Column(name = "FLAG")
	protected String flag;

	@Column(name = "CHKSUM")
	protected String chkSum;

	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Column(name = "TAX_VAL")
	protected BigDecimal TaxableValue;

	@Column(name = "DIFF_PERCENT")
	protected BigDecimal DiffPercent;

	@Column(name = "RATE")
	protected BigDecimal Rate;

	@Column(name = "IGST_AMT")
	protected BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	protected BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	protected BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	protected BigDecimal csgstAmt;

	@Column(name = "ECOM_GSTIN")
	private String eGstin;

	@Column(name = "POS")
	protected String pos;

	@Column(name = "TYPE")
	protected String type;

	@Column(name = "ORG_MON_INV")
	private String orgMonInv;

	@Column(name = "OPOS")
	private String orgPlaceSupp;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

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
	 * @return the cgstAmt
	 *//*
	public BigDecimal getCgstAmt() {
		return cgstAmt;
	}

	*//**
	 * @param cgstAmt
	 *            the cgstAmt to set
	 *//*
	public void setCgstAmt(BigDecimal cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	*//**
	 * @return the sgstAmt
	 *//*
	public BigDecimal getSgstAmt() {
		return sgstAmt;
	}

	*//**
	 * @param sgstAmt
	 *            the sgstAmt to set
	 *//*
	public void setSgstAmt(BigDecimal sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	*//**
	 * @return the csgstAmt
	 *//*
	public BigDecimal getCsgstAmt() {
		return csgstAmt;
	}

	*//**
	 * @param csgstAmt
	 *            the csgstAmt to set
	 *//*
	public void setCsgstAmt(BigDecimal csgstAmt) {
		this.csgstAmt = csgstAmt;
	}

	*//**
	 * @return the type
	 *//*
	public String getType() {
		return type;
	}

	*//**
	 * @param type
	 *            the type to set
	 *//*
	public void setType(String type) {
		this.type = type;
	}

	*//**
	 * @return the orgMonInv
	 *//*
	public String getOrgMonInv() {
		return orgMonInv;
	}

	*//**
	 * @param orgMonInv
	 *            the orgMonInv to set
	 *//*
	public void setOrgMonInv(String orgMonInv) {
		this.orgMonInv = orgMonInv;
	}

	*//**
	 * @return the orgPlaceSupp
	 *//*
	public String getOrgPlaceSupp() {
		return orgPlaceSupp;
	}

	*//**
	 * @param orgPlaceSupp
	 *            the orgPlaceSupp to set
	 *//*
	public void setOrgPlaceSupp(String orgPlaceSupp) {
		this.orgPlaceSupp = orgPlaceSupp;
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
		return "GetGstr1B2CSInvoicesEntity [id=" + id + ", sgstin=" + sgstin
				+ ", returnPeriod=" + returnPeriod + ", flag=" + flag
				+ ", chkSum=" + chkSum + ", supplyType=" + supplyType
				+ ", TaxableValue=" + TaxableValue + ", DiffPercent="
				+ DiffPercent + ", Rate=" + Rate + ", igstAmt=" + igstAmt
				+ ", cgstAmt=" + cgstAmt + ", sgstAmt=" + sgstAmt
				+ ", csgstAmt=" + csgstAmt + ", eGstin=" + eGstin + ", pos="
				+ pos + ", type=" + type + ", orgMonInv=" + orgMonInv
				+ ", orgPlaceSupp=" + orgPlaceSupp + ", isDelete=" + isDelete
				+ "]";
	}

}
*/