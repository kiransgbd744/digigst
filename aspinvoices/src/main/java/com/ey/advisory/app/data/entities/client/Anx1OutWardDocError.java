package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class is the base class for all financial transaction documents (like
 * invoices, invoice amendments, credit notes, debit notes, export docs etc).
 * This document contains the complete transactional information, including all
 * the line items (i.e. this does not represent rolled up information)
 * 
 * This can be used to represent all transactional financial documents that we
 * accept at an invoice level - B2B, B2BA, B2CL, B2CLA, B2CS (at invoicel
 * level), B2CSA (at invoice level), CDNR, CDNRA, CDNUR, CDNURA. Some of the
 * above types of documents can be accepted at a rolled up format, in which case
 * this class cannot be used to represent it.
 * 
 * @author Umesha M
 *
 */
@MappedSuperclass
public abstract class Anx1OutWardDocError extends Anx1DocumentError {

	/* The KEY fields of a financial document */
	@Expose
	@SerializedName("docNo")
	@Column(name = "DOC_NUM")
	protected String docNo;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected String docDate;

	@Expose
	@SerializedName("docType")
	@Column(name = "DOC_TYPE")
	protected String docType;

	/**
	 * The financial year in the format 1819, 1920, 2021 etc. This can be used
	 * for validating the invoices for duplicates. According to government
	 * rules, two document numbers belonging to the same type cannot be the
	 * same, within the same financial year.
	 */

	@Expose
	@SerializedName("fiYear")
	@Column(name = "FI_YEAR")
	protected String finYear;

	/**
	 * A unique string by which we can identify the financial document. The key
	 * can be different for inward and outward documents.
	 * 
	 */
	@Column(name = "DOC_KEY")
	protected String docKey;

	/* End of the KEY fields of the financial document */

	@Expose
	@SerializedName("plantCode")
	@Column(name = "PLANT_CODE")
	protected String plantCode;

	/**
	 * SGSTIN is mandatory for outward documents, but can be null for inward
	 * documents.
	 */

	@Expose
	@SerializedName("supplierGstin")
	@Column(name = "SUPPLIER_GSTIN")
	protected String sgstin;

	/**
	 * CGSTIN is mandatory for inward documents, but can be null for outward
	 * documents.
	 */
	@Expose
	@SerializedName("custGstin")
	@Column(name = "CUST_GSTIN")
	protected String cgstin;

	/**
	 * EGSTIN will be present only for those transactions happening through an
	 * E-commerce supplier.
	 * 
	 */
	@Expose
	@SerializedName("ecomCustGSTIN")
	@Column(name = "ECOM_CUST_GSTIN")
	protected String egstin;

	@Expose
	@SerializedName("supplyType")
	@Column(name = "SUPPLY_TYPE")
	protected String supplyType;

	@Expose
	@SerializedName("reverseCharge")
	@Column(name = "REVERSE_CHARGE")
	protected String reverseCharge;

	@Expose
	@SerializedName("pos")
	@Column(name = "POS")
	protected String pos; // State code

	/**
	 * For Outward invoices, it's called customer code and for Inward Invoices
	 * it's called supplier code.
	 */
	@Expose
	@SerializedName("custOrSuppCode")
	@Column(name = "CUST_SUPP_CODE")
	protected String custOrSuppCode;
	
	@Expose
	@SerializedName("custOrSuppType")
	@Column(name = "CUST_SUPP_TYPE")
	protected String custOrSuppType;
	
	@Expose
	@SerializedName("sec7OfIgstFlag")
	@Column(name = "SECTION7_OF_IGST_FLAG")
	protected String section7OfIgstFlag;
	
	@Expose
	@SerializedName("claimRefundFlag")
	@Column(name = "CLAIM_REFUND_FLAG")
	protected String claimRefundFlag;
	
	@Expose
	@SerializedName("stateApplyingCess")
	@Column(name = "STATE_APPLYING_CESS")	
	protected String stateApplyingCess;
	
	@Expose
	@SerializedName("custOrSuppAddress1")
	@Column(name = "CUST_SUPP_ADDRESS1")
	protected String custOrSuppAddress1;
	
	@Expose
	@SerializedName("custOrSuppAddress2")
	@Column(name = "CUST_SUPP_ADDRESS2")
	protected String custOrSuppAddress2;

	/**
	 * For Outward invoices, it's called customer name and for Inward Invoices
	 * it's called supplier name.
	 */
	@Expose
	@SerializedName("custOrSuppName")
	@Column(name = "CUST_SUPP_NAME")
	protected String custOrSuppName;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;
	
	@Expose
	@SerializedName("sourceFileName")
	@Column(name = "SOURCE_FILENAME")	
	protected String sourceFileName;
	
	@Expose
	@SerializedName("sourceIdentifier")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;

	/* Amendment details */
	@Expose
	@SerializedName("originalDocNo")
	@Column(name = "ORIGINAL_DOC_NUM")
	protected String origDocNo;

	@Expose
	@SerializedName("originalDocDate")
	@Column(name = "ORIGINAL_DOC_DATE")
	protected String origDocDate;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private String isError;

	/**
	 * Sets if the document has information messages
	 */
	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected String isInfo;	
	
	@Expose
	@SerializedName("docAmount")
	@Column(name = "DOC_AMT")
	protected String docAmount; // Total Invoice/Document Amount
	
	@Column(name = "TAXABLE_VALUE")
	protected String taxableValue; // Total Taxable Value.
	
	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private String isProcessed;

	@Transient
	protected String status;
	
	@Expose
	@SerializedName("accountVoucherNo")
	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	protected String accountingVoucherNumber;
	
	@Expose
	@SerializedName("accountVoucherDate")
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	protected String accountingVoucherDate;

	/* Org Hierarchy Details */
	@Expose
	@SerializedName("division")
	@Column(name = "DIVISION")
	protected String division;
	
	@Expose
	@SerializedName("location")
	@Column(name = "LOCATION")
	protected String location;
	
	@Expose
	@SerializedName("subDivision")
	@Column(name = "SUBDIVISION")
	protected String subDivision;
	
	@Expose
	@SerializedName("profitCentre")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;
	
	// Required only if the doc is Credit/Debit Note.
	@Expose
	@SerializedName("crDrReason")
	@Column(name = "CRDR_REASON")
	protected String crDrReason;
	
	@Expose
	@SerializedName("crDrPreGst")
	@Column(name = "CRDR_PRE_GST")
	protected String isCrDrPreGst;
		
	@Expose
	@SerializedName("portCode")
	@Column(name = "SHIP_PORT_CODE")
	protected String portCode;
	
	@Expose
	@SerializedName("distChannel")
	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String distributionChannel;
	
	@Expose
	@SerializedName("userDefinedField1")
	@Column(name = "USERDEFINED_FIELD1")
	protected String userdefinedfield1;
	
	@Expose
	@SerializedName("userDefinedField2")
	@Column(name = "USERDEFINED_FIELD2")
	protected String userdefinedfield2;
	
	@Expose
	@SerializedName("userDefinedField3")
	@Column(name = "USERDEFINED_FIELD3")
	protected String userdefinedfield3;
	
	@Expose
	@SerializedName("userDefOnboard1")
	@Column(name = "USERDEFINED_ONBOARDING1")
	protected String userDefOnboarding1;
	
	@Expose
	@SerializedName("userDefOnboard2")
	@Column(name = "USERDEFINED_ONBOARDING2")
	protected String userDefOnboarding2;
	
	@Expose
	@SerializedName("userDefOnboard3")
	@Column(name = "USERDEFINED_ONBOARDING3")
	protected String userDefOnboarding3;
	
	@Expose
	@SerializedName("userDefOnboard4")
	@Column(name = "USERDEFINED_ONBOARDING4")
	protected String userDefOnboarding4;
	
	@Expose
	@SerializedName("userDefOnboard5")
	@Column(name = "USERDEFINED_ONBOARDING5")
	protected String userDefOnboarding5;
	
	@Expose
	@SerializedName("userDefOnboard6")
	@Column(name = "USERDEFINED_ONBOARDING6")
	protected String userDefOnboarding6;
	
	@Expose
	@SerializedName("userDefinedField4")
	@Column(name = "USERDEFINED_FIELD4")
	protected String userDefinedField4;
	
	@Expose
	@SerializedName("userDefinedField5")
	@Column(name = "USERDEFINED_FIELD5")
	protected String userDefinedField5;
	
	@Expose
	@SerializedName("userDefinedField6")
	@Column(name = "USERDEFINED_FIELD6")
	protected String userDefinedField6;
	
	@Expose
	@SerializedName("userDefinedField7")
	@Column(name = "USERDEFINED_FIELD7")
	protected String userDefinedField7;
	
	@Expose
	@SerializedName("userDefinedField8")
	@Column(name = "USERDEFINED_FIELD8")
	protected String userDefinedField8;
	
	@Expose
	@SerializedName("userDefinedField9")
	@Column(name = "USERDEFINED_FIELD9")
	protected String userDefinedField9;
	
	@Expose
	@SerializedName("userDefinedField10")
	@Column(name = "USERDEFINED_FIELD10")
	protected String userDefinedField10;
	
	@Expose
	@SerializedName("userDefinedField11")
	@Column(name = "USERDEFINED_FIELD11")
	protected String userDefinedField11;
	
	@Expose
	@SerializedName("userDefinedField12")
	@Column(name = "USERDEFINED_FIELD12")
	protected String userDefinedField12;
	
	@Expose
	@SerializedName("userDefinedField13")
	@Column(name = "USERDEFINED_FIELD13")
	protected String userDefinedField13;
	
	@Expose
	@SerializedName("userDefinedField14")
	@Column(name = "USERDEFINED_FIELD14")
	protected String userDefinedField14;
	
	@Expose
	@SerializedName("userDefinedField15")
	@Column(name = "USERDEFINED_FIELD15")
	protected String userDefinedField15;
	
	@Expose
	@SerializedName("eWayBillNo")
	@Column(name = "EWAY_BILL_NUM")
	protected String eWayBillNo;
	
	@Expose
	@SerializedName("eWayBillDate")
	@Column(name = "EWAY_BILL_DATE")
	protected String eWayBillDate;

	
	
	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the finYear
	 */
	public String getFinYear() {
		return finYear;
	}

	/**
	 * @param finYear
	 *            the finYear to set
	 */
	public void setFinYear(String finYear) {
		this.finYear = finYear;
	}


	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getCgstin() {
		return cgstin;
	}

	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	public String getEgstin() {
		return egstin;
	}

	public void setEgstin(String egstin) {
		this.egstin = egstin;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	/**
	 * @return the reverseCharge
	 */
	public String getReverseCharge() {
		return reverseCharge;
	}

	/**
	 * @param reverseCharge the reverseCharge to set
	 */
	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getCustOrSuppCode() {
		return custOrSuppCode;
	}

	public void setCustOrSuppCode(String custOrSuppCode) {
		this.custOrSuppCode = custOrSuppCode;
	}

	public String getCustOrSuppName() {
		return custOrSuppName;
	}

	public void setCustOrSuppName(String custOrSuppName) {
		this.custOrSuppName = custOrSuppName;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getOrigDocNo() {
		return origDocNo;
	}

	public void setOrigDocNo(String origDocNo) {
		this.origDocNo = origDocNo;
	}


	public String getDocDate() {
		return docDate;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getCustOrSuppType() {
		return custOrSuppType;
	}

	public void setCustOrSuppType(String custOrSuppType) {
		this.custOrSuppType = custOrSuppType;
	}

	public String getSection7OfIgstFlag() {
		return section7OfIgstFlag;
	}

	public void setSection7OfIgstFlag(String section7OfIgstFlag) {
		this.section7OfIgstFlag = section7OfIgstFlag;
	}

	public String getClaimRefundFlag() {
		return claimRefundFlag;
	}

	public void setClaimRefundFlag(String claimRefundFlag) {
		this.claimRefundFlag = claimRefundFlag;
	}

	public String getStateApplyingCess() {
		return stateApplyingCess;
	}

	public void setStateApplyingCess(String stateApplyingCess) {
		this.stateApplyingCess = stateApplyingCess;
	}

	public String getCustOrSuppAddress1() {
		return custOrSuppAddress1;
	}

	public void setCustOrSuppAddress1(String custOrSuppAddress1) {
		this.custOrSuppAddress1 = custOrSuppAddress1;
	}

	public String getCustOrSuppAddress2() {
		return custOrSuppAddress2;
	}

	public void setCustOrSuppAddress2(String custOrSuppAddress2) {
		this.custOrSuppAddress2 = custOrSuppAddress2;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSourceIdentifier() {
		return sourceIdentifier;
	}

	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}

	public String getOrigDocDate() {
		return origDocDate;
	}

	public void setOrigDocDate(String origDocDate) {
		this.origDocDate = origDocDate;
	}

	public String getIsError() {
		return isError;
	}

	public void setIsError(String isError) {
		this.isError = isError;
	}

	public String getIsInfo() {
		return isInfo;
	}

	public void setIsInfo(String isInfo) {
		this.isInfo = isInfo;
	}

	public String getDocAmount() {
		return docAmount;
	}

	public void setDocAmount(String docAmount) {
		this.docAmount = docAmount;
	}

	public String getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(String taxableValue) {
		this.taxableValue = taxableValue;
	}

	public String getIsProcessed() {
		return isProcessed;
	}

	public void setIsProcessed(String isProcessed) {
		this.isProcessed = isProcessed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAccountingVoucherNumber() {
		return accountingVoucherNumber;
	}

	public void setAccountingVoucherNumber(String accountingVoucherNumber) {
		this.accountingVoucherNumber = accountingVoucherNumber;
	}

	public String getAccountingVoucherDate() {
		return accountingVoucherDate;
	}

	public void setAccountingVoucherDate(String accountingVoucherDate) {
		this.accountingVoucherDate = accountingVoucherDate;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSubDivision() {
		return subDivision;
	}

	public void setSubDivision(String subDivision) {
		this.subDivision = subDivision;
	}

	public String getProfitCentre() {
		return profitCentre;
	}

	public void setProfitCentre(String profitCentre) {
		this.profitCentre = profitCentre;
	}

	public String getCrDrReason() {
		return crDrReason;
	}

	public void setCrDrReason(String crDrReason) {
		this.crDrReason = crDrReason;
	}

	public String getIsCrDrPreGst() {
		return isCrDrPreGst;
	}

	public void setIsCrDrPreGst(String isCrDrPreGst) {
		this.isCrDrPreGst = isCrDrPreGst;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getDistributionChannel() {
		return distributionChannel;
	}

	public void setDistributionChannel(String distributionChannel) {
		this.distributionChannel = distributionChannel;
	}

	public String getUserdefinedfield1() {
		return userdefinedfield1;
	}

	public void setUserdefinedfield1(String userdefinedfield1) {
		this.userdefinedfield1 = userdefinedfield1;
	}

	public String getUserdefinedfield2() {
		return userdefinedfield2;
	}

	public void setUserdefinedfield2(String userdefinedfield2) {
		this.userdefinedfield2 = userdefinedfield2;
	}

	public String getUserdefinedfield3() {
		return userdefinedfield3;
	}

	public void setUserdefinedfield3(String userdefinedfield3) {
		this.userdefinedfield3 = userdefinedfield3;
	}

	public String getUserDefOnboarding1() {
		return userDefOnboarding1;
	}

	public void setUserDefOnboarding1(String userDefOnboarding1) {
		this.userDefOnboarding1 = userDefOnboarding1;
	}

	public String getUserDefOnboarding2() {
		return userDefOnboarding2;
	}

	public void setUserDefOnboarding2(String userDefOnboarding2) {
		this.userDefOnboarding2 = userDefOnboarding2;
	}

	public String getUserDefOnboarding3() {
		return userDefOnboarding3;
	}

	public void setUserDefOnboarding3(String userDefOnboarding3) {
		this.userDefOnboarding3 = userDefOnboarding3;
	}

	public String getUserDefOnboarding4() {
		return userDefOnboarding4;
	}

	public void setUserDefOnboarding4(String userDefOnboarding4) {
		this.userDefOnboarding4 = userDefOnboarding4;
	}

	public String getUserDefOnboarding5() {
		return userDefOnboarding5;
	}

	public void setUserDefOnboarding5(String userDefOnboarding5) {
		this.userDefOnboarding5 = userDefOnboarding5;
	}

	public String getUserDefOnboarding6() {
		return userDefOnboarding6;
	}

	public void setUserDefOnboarding6(String userDefOnboarding6) {
		this.userDefOnboarding6 = userDefOnboarding6;
	}

	public String getUserDefinedField4() {
		return userDefinedField4;
	}

	public void setUserDefinedField4(String userDefinedField4) {
		this.userDefinedField4 = userDefinedField4;
	}

	public String getUserDefinedField5() {
		return userDefinedField5;
	}

	public void setUserDefinedField5(String userDefinedField5) {
		this.userDefinedField5 = userDefinedField5;
	}

	public String getUserDefinedField6() {
		return userDefinedField6;
	}

	public void setUserDefinedField6(String userDefinedField6) {
		this.userDefinedField6 = userDefinedField6;
	}

	public String getUserDefinedField7() {
		return userDefinedField7;
	}

	public void setUserDefinedField7(String userDefinedField7) {
		this.userDefinedField7 = userDefinedField7;
	}

	public String getUserDefinedField8() {
		return userDefinedField8;
	}

	public void setUserDefinedField8(String userDefinedField8) {
		this.userDefinedField8 = userDefinedField8;
	}

	public String getUserDefinedField9() {
		return userDefinedField9;
	}

	public void setUserDefinedField9(String userDefinedField9) {
		this.userDefinedField9 = userDefinedField9;
	}

	public String getUserDefinedField10() {
		return userDefinedField10;
	}

	public void setUserDefinedField10(String userDefinedField10) {
		this.userDefinedField10 = userDefinedField10;
	}

	public String getUserDefinedField11() {
		return userDefinedField11;
	}

	public void setUserDefinedField11(String userDefinedField11) {
		this.userDefinedField11 = userDefinedField11;
	}

	public String getUserDefinedField12() {
		return userDefinedField12;
	}

	public void setUserDefinedField12(String userDefinedField12) {
		this.userDefinedField12 = userDefinedField12;
	}

	public String getUserDefinedField13() {
		return userDefinedField13;
	}

	public void setUserDefinedField13(String userDefinedField13) {
		this.userDefinedField13 = userDefinedField13;
	}

	public String getUserDefinedField14() {
		return userDefinedField14;
	}

	public void setUserDefinedField14(String userDefinedField14) {
		this.userDefinedField14 = userDefinedField14;
	}

	public String getUserDefinedField15() {
		return userDefinedField15;
	}

	public void setUserDefinedField15(String userDefinedField15) {
		this.userDefinedField15 = userDefinedField15;
	}

	public String geteWayBillNo() {
		return eWayBillNo;
	}

	public void seteWayBillNo(String eWayBillNo) {
		this.eWayBillNo = eWayBillNo;
	}

	public String geteWayBillDate() {
		return eWayBillDate;
	}

	public void seteWayBillDate(String eWayBillDate) {
		this.eWayBillDate = eWayBillDate;
	}

	@Override
	public String toString() {
		return "TransDocument [docNo=" + docNo + ", docDate=" 
				+ docDate + ", docType=" + docType 
				+ ", finYear="+ finYear + ", docKey=" 
				+ docKey + ", plantCode=" + plantCode 
				+ ", sgstin=" + sgstin + ", cgstin="
				+ cgstin + ", egstin=" + egstin 
				+ ", supplyType=" + supplyType 
				+ ", reverseCharge=" + reverseCharge
				+ ", pos=" + pos + ", custOrSuppCode=" 
				+ custOrSuppCode + ", custOrSuppType=" 
				+ custOrSuppType + ", section7OfIgstFlag=" 
				+ section7OfIgstFlag + ", claimRefundFlag=" 
				+ claimRefundFlag + ", stateApplyingCess=" 
				+ stateApplyingCess + ", custOrSuppAddress1=" 
				+ custOrSuppAddress1 + ", custOrSuppAddress2=" 
				+ custOrSuppAddress2 + ", custOrSuppName=" 
				+ custOrSuppName + ", userId="	+ userId 
				+ ", sourceFileName=" + sourceFileName 
				+ ", sourceIdentifier=" + sourceIdentifier
				+ ", origDocNo=" + origDocNo + ", origDocDate=" 
				+ origDocDate + ", isError=" + isError 
				+ ", isInfo="+ isInfo + ", docAmount=" + docAmount 
				+ ", taxableValue=" + taxableValue + ", isProcessed="
				+ isProcessed + ", status=" + status 
				+ ", accountingVoucherNumber=" 
				+ accountingVoucherNumber + ", accountingVoucherDate=" 
				+ accountingVoucherDate + ", division=" + division 
				+ ", location="	+ location + ", subDivision=" 
				+ subDivision + ", profitCentre=" + profitCentre 
				+ ", crDrReason=" + crDrReason + ", isCrDrPreGst=" 
				+ isCrDrPreGst + ", portCode=" + portCode 
				+ ", distributionChannel=" + distributionChannel 
				+ ", userdefinedfield1=" + userdefinedfield1 
				+ ", userdefinedfield2=" + userdefinedfield2 
				+ ", userdefinedfield3=" + userdefinedfield3 
				+ ", userDefOnboarding1=" + userDefOnboarding1 
				+ ", userDefOnboarding2=" + userDefOnboarding2 
				+ ", userDefOnboarding3=" + userDefOnboarding3 
				+ ", userDefOnboarding4=" + userDefOnboarding4 
				+ ", userDefOnboarding5=" + userDefOnboarding5 
				+ ", userDefOnboarding6=" + userDefOnboarding6 
				+ ", userDefinedField4="  + userDefinedField4 
				+ ", userDefinedField5=" + userDefinedField5 
				+ ", userDefinedField6=" + userDefinedField6 
				+ ", userDefinedField7=" + userDefinedField7 
				+ ", userDefinedField8=" + userDefinedField8 
				+ ", userDefinedField9=" + userDefinedField9 
				+ ", userDefinedField10=" + userDefinedField10 
				+ ", userDefinedField11=" + userDefinedField11 
				+ ", userDefinedField12=" + userDefinedField12 
				+ ", userDefinedField13=" + userDefinedField13 
				+ ", userDefinedField14=" + userDefinedField14 
				+ ", userDefinedField15=" + userDefinedField15 
				+ ", eWayBillNo=" + eWayBillNo
				+ ", eWayBillDate=" + eWayBillDate + "]";
	}
	
}
