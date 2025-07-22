/*package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

*//**
 * 
 * @author Hemasundar.J
 *
 *//*
@Entity
@Table(name = "GETGSTR1_AT_ATA_DETAILS")
public class GetGstr1ATEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("flag")
	@Column(name = "FLAG")
	protected String flag;

	@Expose
	@SerializedName("chksum")
	@Column(name = "CHKSUM")
	protected String chksum;

	@Expose
	@SerializedName("omon")
	@Column(name = "OMON")
	protected LocalDate omon;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;

	@Expose
	@SerializedName("sply_ty")
	@Column(name = "SPLY_TY")
	protected String sply_ty;

	@Expose
	@SerializedName("diff_percent")
	@Column(name = "DIFF_PERCENT")
	protected BigDecimal diff_percent;

	@Expose
	@SerializedName("rt")
	@Column(name = "RT")
	protected BigDecimal rt;

	@Expose
	@SerializedName("ad_amt")
	@Column(name = "AD_AMT")
	protected BigDecimal ad_amt;

	@Expose
	@SerializedName("iamt")
	@Column(name = "IAMT")
	protected BigDecimal iamt;

	@Expose
	@SerializedName("camt")
	@Column(name = "CAMT")
	protected BigDecimal camt;

	@Expose
	@SerializedName("samt")
	@Column(name = "SAMT")
	protected BigDecimal samt;

	@Expose
	@SerializedName("csamt")
	@Column(name = "CSAMT")
	protected BigDecimal csamt;

	@Expose
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	@Expose
	@SerializedName("ret_period")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("derivedTaxperiod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedTaxperiod;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public LocalDate getOmon() {
		return omon;
	}

	public void setOmon(LocalDate omon) {
		this.omon = omon;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getSply_ty() {
		return sply_ty;
	}

	public void setSply_ty(String sply_ty) {
		this.sply_ty = sply_ty;
	}

	public BigDecimal getDiff_percent() {
		return diff_percent;
	}

	public void setDiff_percent(BigDecimal diff_percent) {
		this.diff_percent = diff_percent;
	}

	public BigDecimal getRt() {
		return rt;
	}

	public void setRt(BigDecimal rt) {
		this.rt = rt;
	}

	public BigDecimal getAd_amt() {
		return ad_amt;
	}

	public void setAd_amt(BigDecimal ad_amt) {
		this.ad_amt = ad_amt;
	}

	public BigDecimal getIamt() {
		return iamt;
	}

	public void setIamt(BigDecimal iamt) {
		this.iamt = iamt;
	}

	public BigDecimal getCamt() {
		return camt;
	}

	public void setCamt(BigDecimal camt) {
		this.camt = camt;
	}

	public BigDecimal getSamt() {
		return samt;
	}

	public void setSamt(BigDecimal samt) {
		this.samt = samt;
	}

	public BigDecimal getCsamt() {
		return csamt;
	}

	public void setCsamt(BigDecimal csamt) {
		this.csamt = csamt;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public Integer getDerivedTaxperiod() {
		return derivedTaxperiod;
	}

	public void setDerivedTaxperiod(Integer derivedTaxperiod) {
		this.derivedTaxperiod = derivedTaxperiod;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public String toString() {
		return "GetGstr1ATEntity [id=" + id + ", flag=" + flag + ", chksum="
				+ chksum + ", omon=" + omon + ", pos=" + pos + ", sply_ty="
				+ sply_ty + ", diff_percent=" + diff_percent + ", rt=" + rt
				+ ", ad_amt=" + ad_amt + ", iamt=" + iamt + ", camt=" + camt
				+ ", samt=" + samt + ", csamt=" + csamt + ", sgstin=" + sgstin
				+ ", returnPeriod=" + returnPeriod + ", derivedTaxperiod="
				+ derivedTaxperiod + ", isDelete=" + isDelete + "]";
	}

}
*/