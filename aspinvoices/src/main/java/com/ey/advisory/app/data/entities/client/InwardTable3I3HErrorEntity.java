package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Mahesh.Golla
 *
 */


@Entity
@Table(name = "ANX_ERROR_3H_3I")
public class InwardTable3I3HErrorEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("recipientGstin")
	@Column(name = "RECIPIENT_GSTIN")
	protected String recipientGstin;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RET_PERIOD")
	protected String returnPeriod;

	@Expose
	@SerializedName("returnType")
	@Column(name = "RET_TYPE")
	protected String returnType;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;

	@Expose
	@SerializedName("sec70fIGSTFLAG")
	@Column(name = "SEC7_OF_IGST_FLAG")
	protected String sec70fIGSTFLAG;

	@Expose
	@SerializedName("autoPopulateToRefund")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopulateToRefund;

	@Expose
	@SerializedName("transactionFlag")
	@Column(name = "TRAN_FLAG")
	protected String transactionFlag;

	@Expose
	@SerializedName("supplierGSTINorpan")
	@Column(name = "SUPPLIER_GSTIN/PAN")
	protected String supplierGSTINorpan;

	@Expose
	@SerializedName("supplierName")
	@Column(name = "SUPPLIER_NAME")
	protected String supplierName;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos;

	@Expose
	@SerializedName("hsn")
	@Column(name = "HSN")
	private String hsn;

	@Expose
	@SerializedName("taxableValue")
	@Column(name = "TAXABLE_VALUE")
	protected String taxableValue;

	@Expose
	@SerializedName("integratedTaxAmount")
	@Column(name = "IGST_AMT")
	protected String integratedTaxAmount;

	@Expose
	@SerializedName("centralTaxAmount")
	@Column(name = "CGST_AMT")
	protected String centralTaxAmount;

	@Expose
	@SerializedName("stateUTTaxAmount")
	@Column(name = "SGST_AMT")
	protected String stateUTTaxAmount;

	@Expose
	@SerializedName("cessAmount")
	@Column(name = "CESS_AMT")
	protected String cessAmount;

	@Expose
	@SerializedName("eligibilityIndicator")
	@Column(name = "ELGBL_INDICATOR")
	protected String eligibilityIndicator;

	@Expose
	@SerializedName("availableIGST")
	@Column(name = "AVAIL_IGST")
	protected String availableIGST;

	@Expose
	@SerializedName("availableCGST")
	@Column(name = "AVAIL_CGST")
	protected String availableCGST;

	@Expose
	@SerializedName("availableSGST")
	@Column(name = "AVAIL_SGST")
	protected String availableSGST;

	@Expose
	@SerializedName("availableCess")
	@Column(name = "AVAIL_CESS")
	protected String availableCess;

	@Expose
	@SerializedName("rate")
	@Column(name = "RATE")
	protected String rate;

	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;

	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTER")
	protected String profitCentre;

	@Expose
	@SerializedName("plant")
	@Column(name = "PLANT")
	protected String plant;

	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;

	@Expose
	@SerializedName("purchaseOrganisation")
	@Column(name = "PURCHAGE_ORG")
	protected String purchaseOrganisation;

	@Expose
	@SerializedName("userDefined1")
	@Column(name = "USERDEFINED1")
	protected String userDefined1;

	@Expose
	@SerializedName("userDefined2")
	@Column(name = "USERDEFINED2")
	protected String userDefined2;

	@Expose
	@SerializedName("userDefined3")
	@Column(name = "USERDEFINED3")
	protected String userDefined3;

	@Expose
	@SerializedName("userAccess1")
	@Column(name = "USER_ACCESS1")
	protected String userAccess1;

	@Expose
	@SerializedName("userAccess2")
	@Column(name = "USER_ACCESS2")
	protected String userAccess2;

	@Expose
	@SerializedName("userAccess3")
	@Column(name = "USER_ACCESS3")
	protected String userAccess3;

	@Expose
	@SerializedName("userAccess4")
	@Column(name = "USER_ACCESS4")
	protected String userAccess4;

	@Expose
	@SerializedName("userAccess5")
	@Column(name = "USER_ACCESS5")
	protected String userAccess5;

	@Expose
	@SerializedName("userAccess6")
	@Column(name = "USER_ACCESS6")
	protected String userAccess6;

	@Expose
	@Column(name = "TABLE_3H3I_KEY")
	protected String table3H3Ikey;
	
	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	protected Long fileId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRecipientGstin() {
		return recipientGstin;
	}

	public void setRecipientGstin(String recipientGstin) {
		this.recipientGstin = recipientGstin;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(String diffPercent) {
		this.diffPercent = diffPercent;
	}

	public String getSec70fIGSTFLAG() {
		return sec70fIGSTFLAG;
	}

	public void setSec70fIGSTFLAG(String sec70fIGSTFLAG) {
		this.sec70fIGSTFLAG = sec70fIGSTFLAG;
	}

	public String getAutoPopulateToRefund() {
		return autoPopulateToRefund;
	}

	public void setAutoPopulateToRefund(String autoPopulateToRefund) {
		this.autoPopulateToRefund = autoPopulateToRefund;
	}

	public String getTransactionFlag() {
		return transactionFlag;
	}

	public void setTransactionFlag(String transactionFlag) {
		this.transactionFlag = transactionFlag;
	}

	public String getSupplierGSTINorpan() {
		return supplierGSTINorpan;
	}

	public void setSupplierGSTINorpan(String supplierGSTINorpan) {
		this.supplierGSTINorpan = supplierGSTINorpan;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getHsn() {
		return hsn;
	}

	public void setHsn(String hsn) {
		this.hsn = hsn;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
	}

	public String getIntegratedTaxAmount() {
		return integratedTaxAmount;
	}

	public void setIntegratedTaxAmount(String integratedTaxAmount) {
		this.integratedTaxAmount = integratedTaxAmount;
	}

	public String getCentralTaxAmount() {
		return centralTaxAmount;
	}

	public void setCentralTaxAmount(String centralTaxAmount) {
		this.centralTaxAmount = centralTaxAmount;
	}

	public String getStateUTTaxAmount() {
		return stateUTTaxAmount;
	}

	public void setStateUTTaxAmount(String stateUTTaxAmount) {
		this.stateUTTaxAmount = stateUTTaxAmount;
	}

	public String getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(String cessAmount) {
		this.cessAmount = cessAmount;
	}

	public String getEligibilityIndicator() {
		return eligibilityIndicator;
	}

	public void setEligibilityIndicator(String eligibilityIndicator) {
		this.eligibilityIndicator = eligibilityIndicator;
	}

	public String getAvailableIGST() {
		return availableIGST;
	}

	public void setAvailableIGST(String availableIGST) {
		this.availableIGST = availableIGST;
	}

	public String getAvailableCGST() {
		return availableCGST;
	}

	public void setAvailableCGST(String availableCGST) {
		this.availableCGST = availableCGST;
	}

	public String getAvailableSGST() {
		return availableSGST;
	}

	public void setAvailableSGST(String availableSGST) {
		this.availableSGST = availableSGST;
	}

	public String getAvailableCess() {
		return availableCess;
	}

	public void setAvailableCess(String availableCess) {
		this.availableCess = availableCess;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getProfitCentre() {
		return profitCentre;
	}

	public void setProfitCentre(String profitCentre) {
		this.profitCentre = profitCentre;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPurchaseOrganisation() {
		return purchaseOrganisation;
	}

	public void setPurchaseOrganisation(String purchaseOrganisation) {
		this.purchaseOrganisation = purchaseOrganisation;
	}

	public String getUserDefined1() {
		return userDefined1;
	}

	public void setUserDefined1(String userDefined1) {
		this.userDefined1 = userDefined1;
	}

	public String getUserDefined2() {
		return userDefined2;
	}

	public void setUserDefined2(String userDefined2) {
		this.userDefined2 = userDefined2;
	}

	public String getUserDefined3() {
		return userDefined3;
	}

	public void setUserDefined3(String userDefined3) {
		this.userDefined3 = userDefined3;
	}

	public String getUserAccess1() {
		return userAccess1;
	}

	public void setUserAccess1(String userAccess1) {
		this.userAccess1 = userAccess1;
	}

	public String getUserAccess2() {
		return userAccess2;
	}

	public void setUserAccess2(String userAccess2) {
		this.userAccess2 = userAccess2;
	}

	public String getUserAccess3() {
		return userAccess3;
	}

	public void setUserAccess3(String userAccess3) {
		this.userAccess3 = userAccess3;
	}

	public String getUserAccess4() {
		return userAccess4;
	}

	public void setUserAccess4(String userAccess4) {
		this.userAccess4 = userAccess4;
	}

	public String getUserAccess5() {
		return userAccess5;
	}

	public void setUserAccess5(String userAccess5) {
		this.userAccess5 = userAccess5;
	}

	public String getUserAccess6() {
		return userAccess6;
	}

	public void setUserAccess6(String userAccess6) {
		this.userAccess6 = userAccess6;
	}

	public String getTable3H3Ikey() {
		return table3H3Ikey;
	}

	public void setTable3H3Ikey(String table3h3Ikey) {
		table3H3Ikey = table3h3Ikey;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	@Override
	public String toString() {
		return "InwardTable3I3HErrorEntity [id=" + id + ", recipientGstin="
				+ recipientGstin + ", returnPeriod=" + returnPeriod
				+ ", returnType=" + returnType + ", docType=" + docType
				+ ", diffPercent=" + diffPercent + ", sec70fIGSTFLAG="
				+ sec70fIGSTFLAG + ", autoPopulateToRefund="
				+ autoPopulateToRefund + ", transactionFlag=" + transactionFlag
				+ ", supplierGSTINorpan=" + supplierGSTINorpan
				+ ", supplierName=" + supplierName + ", pos=" + pos + ", hsn="
				+ hsn + ", taxableValue=" + taxableValue
				+ ", integratedTaxAmount=" + integratedTaxAmount
				+ ", centralTaxAmount=" + centralTaxAmount
				+ ", stateUTTaxAmount=" + stateUTTaxAmount + ", cessAmount="
				+ cessAmount + ", eligibilityIndicator=" + eligibilityIndicator
				+ ", availableIGST=" + availableIGST + ", availableCGST="
				+ availableCGST + ", availableSGST=" + availableSGST
				+ ", availableCess=" + availableCess + ", rate=" + rate
				+ ", division=" + division + ", profitCentre=" + profitCentre
				+ ", plant=" + plant + ", location=" + location
				+ ", purchaseOrganisation=" + purchaseOrganisation
				+ ", userDefined1=" + userDefined1 + ", userDefined2="
				+ userDefined2 + ", userDefined3=" + userDefined3
				+ ", userAccess1=" + userAccess1 + ", userAccess2="
				+ userAccess2 + ", userAccess3=" + userAccess3
				+ ", userAccess4=" + userAccess4 + ", userAccess5="
				+ userAccess5 + ", userAccess6=" + userAccess6
				+ ", table3H3Ikey=" + table3H3Ikey + ", fileId="
				+ fileId + "]";
	}
	
}