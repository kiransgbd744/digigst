package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "GSTR1_ADVANCE_RECEVIED_DETAILS")
public class Gstr1AdvanceRecivedFileUploadEntity {
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
	private String sgstin;

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
	@SerializedName("orgGrossAdvanceReceived")
	@Column(name = "ORG_RECEIVED")
	private BigDecimal orgGrossAdvanceReceived = BigDecimal.ZERO;

	@Expose
	@SerializedName("newPos")
	@Column(name = "NEW_POS")
	private String newPOS;

	@Expose
	@SerializedName("newRate")
	@Column(name = "NEW_RATE")
	private BigDecimal newRate = BigDecimal.ZERO;

	@Expose
	@SerializedName("newGrossAdvanceReceived")
	@Column(name = "NEW_RECEIVED")
	private BigDecimal newGrossAdvanceReceived = BigDecimal.ZERO;;

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
	@SerializedName("atKey")
	@Column(name = "AT_KEY")
	private String atKey;
	
	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected Integer derivedRetPeriod;
	
	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

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
		return sgstin;
	}

	public void setSupplierGSTIN(String supplierGSTIN) {
		this.sgstin = supplierGSTIN;
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

	public BigDecimal getOrgGrossAdvanceReceived() {
		return orgGrossAdvanceReceived;
	}

	public void setOrgGrossAdvanceReceived(BigDecimal orgGrossAdvanceReceived) {
		this.orgGrossAdvanceReceived = orgGrossAdvanceReceived;
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

	public BigDecimal getNewGrossAdvanceReceived() {
		return newGrossAdvanceReceived;
	}

	public void setNewGrossAdvanceReceived(BigDecimal newGrossAdvanceReceived) {
		this.newGrossAdvanceReceived = newGrossAdvanceReceived;
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

	public String getAtKey() {
		return atKey;
	}

	public void setAtKey(String atKey) {
		this.atKey = atKey;
	}

	public Integer getDerivedRetPeriod() {
		return derivedRetPeriod;
	}

	public void setDerivedRetPeriod(Integer derivedRetPeriod) {
		this.derivedRetPeriod = derivedRetPeriod;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public String toString() {
		return "Gstr1AdvanceRecivedFileUploadEntity [id=" + id + ", fileId="
				+ fileId + ", sgstin=" + sgstin + ", returnPeriod="
				+ returnPeriod + ", transactionType=" + transactionType
				+ ", month=" + month + ", orgPOS=" + orgPOS + ", orgRate="
				+ orgRate + ", orgGrossAdvanceReceived="
				+ orgGrossAdvanceReceived + ", newPOS=" + newPOS + ", newRate="
				+ newRate + ", newGrossAdvanceReceived="
				+ newGrossAdvanceReceived + ", integratedTaxAmount="
				+ integratedTaxAmount + ", centralTaxAmount=" + centralTaxAmount
				+ ", stateUTTaxAmount=" + stateUTTaxAmount + ", cessAmount="
				+ cessAmount + ", atKey=" + atKey + ", derivedRetPeriod="
				+ derivedRetPeriod + ", isDelete=" + isDelete + "]";
	}

	public Gstr1AdvanceRecivedFileUploadEntity() {
	}
	public Gstr1AdvanceRecivedFileUploadEntity(String atKey) {
		this.atKey = atKey;
	}

	public Gstr1AdvanceRecivedFileUploadEntity(Long fileId, String sgstin,
			String returnPeriod, String transactionType, String month,
			String orgPOS, BigDecimal orgRate,
			BigDecimal orgGrossAdvanceReceived, String newPOS,
			BigDecimal newRate, BigDecimal newGrossAdvanceReceived,
			BigDecimal integratedTaxAmount, BigDecimal centralTaxAmount,
			BigDecimal stateUTTaxAmount, BigDecimal cessAmount, String atKey,
			Integer derivedRetPeriod) {
		this.fileId = fileId;
		this.sgstin = sgstin;
		this.returnPeriod = returnPeriod;
		this.transactionType = transactionType;
		this.month = month;
		this.orgPOS = orgPOS;
		this.orgRate = (orgRate != null) ? orgRate :BigDecimal.ZERO ;
		this.orgGrossAdvanceReceived =( orgGrossAdvanceReceived != null) ?
				                     orgGrossAdvanceReceived:BigDecimal.ZERO ;
		this.newPOS = newPOS;
		this.newRate = (newRate != null) ? newRate :BigDecimal.ZERO;
		this.newGrossAdvanceReceived =(newGrossAdvanceReceived != null) ?
				newGrossAdvanceReceived : BigDecimal.ZERO;
		this.integratedTaxAmount = (integratedTaxAmount != null) ?
				integratedTaxAmount:BigDecimal.ZERO;
		this.centralTaxAmount = (centralTaxAmount != null) ? 
				centralTaxAmount:BigDecimal.ZERO;
		this.stateUTTaxAmount = (stateUTTaxAmount != null) ?
				           stateUTTaxAmount:BigDecimal.ZERO;
		this.cessAmount = (cessAmount != null) ? cessAmount : BigDecimal.ZERO;
		this.atKey = atKey;
		this.derivedRetPeriod = derivedRetPeriod;
	}

	public Gstr1AdvanceRecivedFileUploadEntity add(
			Gstr1AdvanceRecivedFileUploadEntity newObj) {
		this.fileId = newObj.fileId;
		this.sgstin = newObj.sgstin;
		this.returnPeriod = newObj.returnPeriod;
		this.transactionType = newObj.transactionType;
		this.month = newObj.month;
		this.orgPOS = newObj.orgPOS;
		this.orgRate = this.orgRate.add(newObj.orgRate);
		this.orgGrossAdvanceReceived = 
			this.orgGrossAdvanceReceived.add(newObj.orgGrossAdvanceReceived);
		this.newPOS = newObj.newPOS;
		this.newRate = this.newRate.add(newObj.newRate);
		this.newGrossAdvanceReceived = this.newGrossAdvanceReceived.
				          add(newObj.newGrossAdvanceReceived);
		this.integratedTaxAmount = this.integratedTaxAmount.
				                 add(newObj.integratedTaxAmount);
		this.centralTaxAmount = 
				       this.centralTaxAmount.add(newObj.centralTaxAmount);
		this.stateUTTaxAmount = this.stateUTTaxAmount.
				add(newObj.stateUTTaxAmount);
		this.cessAmount = this.cessAmount.add(newObj.cessAmount);
		this.atKey = newObj.atKey;
		this.derivedRetPeriod = newObj.derivedRetPeriod;
		return this;
	}
}