package com.ey.advisory.app.data.entities.client;


import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
@Table(name = "GSTR2_ADVANCE_ADJUSTMENT_DETAILS")
public class Gstr2AdvanceAdjustmentFileUploadEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGSTIN;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RET_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("transactionType")
	@Column(name = "TRAN_TYPE")
	private String transactionType;

	@Expose
	@SerializedName("month")
	@Column(name = "MONTH")
	private String month;

	@Expose
	@SerializedName("orgPos")
	@Column(name = "ORG_POS")
	private String orgPOS;

	@Expose
	@SerializedName("orgRate")
	@Column(name = "ORG_RATE")
	private BigDecimal orgRate = BigDecimal.ZERO;

	@Expose
	@SerializedName("orgGrossAdvanceAdjusted")
	@Column(name = "ORG_ADJUSTED")
	private BigDecimal orgGrossAdvanceAdjusted = BigDecimal.ZERO;;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	private String newPOS;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	private BigDecimal newRate = BigDecimal.ZERO;

	@Expose
	@SerializedName("newGrossAdvanceAdjusted")
	@Column(name = "NEW_ADJUSTED")
	private BigDecimal newGrossAdvanceAdjusted = BigDecimal.ZERO;;

	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	private BigDecimal integratedTaxAmount = BigDecimal.ZERO;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	private BigDecimal centralTaxAmount = BigDecimal.ZERO;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	private BigDecimal stateUTTaxAmount = BigDecimal.ZERO;

	@Expose
	@SerializedName("cessAmt")
	@Column(name = "CESS_AMT")
	private BigDecimal cessAmount = BigDecimal.ZERO;

	@Expose
	@SerializedName("ataKey")
	@Column(name = "ATA_KEY")
	private String ataKey;
	
	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getSupplierGSTIN() {
		return supplierGSTIN;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		this.supplierGSTIN = supplierGSTIN;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getOrgPOS() {
		return orgPOS;
	}

	public void setOrgPOS(String orgPOS) {
		this.orgPOS = orgPOS;
	}

	public BigDecimal getOrgRate() {
		return orgRate;
	}

	public void setOrgRate(BigDecimal orgRate) {
		this.orgRate = orgRate;
	}

	public BigDecimal getOrgGrossAdvanceAdjusted() {
		return orgGrossAdvanceAdjusted;
	}

	public void setOrgGrossAdvanceAdjusted(BigDecimal orgGrossAdvanceAdjusted) {
		this.orgGrossAdvanceAdjusted = orgGrossAdvanceAdjusted;
	}

	public String getNewPOS() {
		return newPOS;
	}

	public void setNewPOS(String newPOS) {
		this.newPOS = newPOS;
	}

	public BigDecimal getNewRate() {
		return newRate;
	}

	public void setNewRate(BigDecimal newRate) {
		this.newRate = newRate;
	}

	public BigDecimal getNewGrossAdvanceAdjusted() {
		return newGrossAdvanceAdjusted;
	}

	public void setNewGrossAdvanceAdjusted(BigDecimal newGrossAdvanceAdjusted) {
		this.newGrossAdvanceAdjusted = newGrossAdvanceAdjusted;
	}

	public BigDecimal getIntegratedTaxAmount() {
		return integratedTaxAmount;
	}

	public void setIntegratedTaxAmount(BigDecimal integratedTaxAmount) {
		this.integratedTaxAmount = integratedTaxAmount;
	}

	public BigDecimal getCentralTaxAmount() {
		return centralTaxAmount;
	}

	public void setCentralTaxAmount(BigDecimal centralTaxAmount) {
		this.centralTaxAmount = centralTaxAmount;
	}

	public BigDecimal getStateUTTaxAmount() {
		return stateUTTaxAmount;
	}

	public void setStateUTTaxAmount(BigDecimal stateUTTaxAmount) {
		this.stateUTTaxAmount = stateUTTaxAmount;
	}

	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

	public String getAtaKey() {
		return ataKey;
	}

	public void setAtaKey(String ataKey) {
		this.ataKey = ataKey;
	}

	public Integer getDerivedRetPeriod() {
		return derivedRetPeriod;
	}

	public void setDerivedRetPeriod(Integer derivedRetPeriod) {
		this.derivedRetPeriod = derivedRetPeriod;
	}

	@Override
	public String toString() {
		return "Gstr2AdvanceAdjustmentFileUploadEntity [id=" + id + ", fileId="
				+ fileId + ", supplierGSTIN=" + supplierGSTIN
				+ ", returnPeriod=" + returnPeriod + ", transactionType="
				+ transactionType + ", month=" + month + ", orgPOS=" + orgPOS
				+ ", orgRate=" + orgRate + ", orgGrossAdvanceAdjusted="
				+ orgGrossAdvanceAdjusted + ", newPOS=" + newPOS + ", newRate="
				+ newRate + ", newGrossAdvanceAdjusted="
				+ newGrossAdvanceAdjusted + ", integratedTaxAmount="
				+ integratedTaxAmount + ", centralTaxAmount=" + centralTaxAmount
				+ ", stateUTTaxAmount=" + stateUTTaxAmount + ", cessAmount="
				+ cessAmount + ", ataKey=" + ataKey + ", derivedRetPeriod="
				+ derivedRetPeriod + "]";
	}



}
