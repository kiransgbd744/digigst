package com.ey.advisory.app.data.business.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
 * @author Sai.Pakanati
 *
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class TransDocument extends Document {

	/* The KEY fields of a financial document */
	@Expose
	@SerializedName("docNo")
	@XmlElement(name = "doc-no")
	protected String docNo;

	@Expose
	@SerializedName("docDate")
	@XmlElement(name = "doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate docDate;

	@Expose
	@SerializedName("docType")
	@XmlElement(name = "doc-type")
	protected String docType;

	/**
	 * The financial year in the format 1819, 1920, 2021 etc. This can be used
	 * for validating the invoices for duplicates. According to government
	 * rules, two document numbers belonging to the same type cannot be the
	 * same, within the same financial year.
	 */

	@Expose
	@SerializedName("fiYear")
	@XmlElement(name = "fin-yr")
	protected String finYear;

	/**
	 * A unique string by which we can identify the financial document. The key
	 * can be different for inward and outward documents.
	 * 
	 */
	@XmlElement(name = "doc-key")
	protected String docKey;

	/* End of the KEY fields of the financial document */

	@Expose
	@SerializedName("plantCode")
	@XmlElement(name = "plnt-code")
	protected String plantCode;

	/**
	 * SGSTIN is mandatory for outward documents, but can be null for inward
	 * documents.
	 */

	@Expose
	@SerializedName("suppGstin")
	@XmlElement(name = "sgstin")
	protected String sgstin;

	/**
	 * CGSTIN is mandatory for inward documents, but can be null for outward
	 * documents.
	 */
	@Expose
	@SerializedName("custGstin")
	protected String cgstin;

	@Expose
	@SerializedName("supplyType")
	@XmlElement(name = "supp-type")
	protected String supplyType;

	@Expose
	@SerializedName("reverseCharge")
	@XmlElement(name = "rev-chrg")
	protected String reverseCharge;

	@Expose
	@SerializedName("pos")
	protected String pos; // State code

	/**
	 * For Outward invoices, it's called customer code and for Inward Invoices
	 * it's called supplier code.
	 */
	@Expose
	@SerializedName("custOrSupCode")
	@XmlElement(name = "cst-sup-cd")
	protected String custOrSuppCode;

	@Expose
	@SerializedName("custOrSupType")
	@XmlElement(name = "cust-supp-typ")
	protected String custOrSuppType;

	@Expose
	@SerializedName("sec7OfIgstFlag")
	@XmlElement(name = "sec70-igst-flg")
	protected String section7OfIgstFlag;

	/**
	 * For Outward invoices, it's called customer name and for Inward Invoices
	 * it's called supplier name.
	 */
	@Expose
	@SerializedName("custOrSupName")
	@XmlElement(name = "cust-supp-name")
	protected String custOrSuppName;

	@Expose
	@SerializedName("custOrSupAddr1")
	@XmlElement(name = "cust-sup-add1")
	protected String custOrSuppAddress1;

	@Expose
	@SerializedName("custOrSupAddr2")
	@XmlElement(name = "cust-supp-add2")
	protected String custOrSuppAddress2;

	@Expose
	@SerializedName("custOrSupAddr3")
	@XmlElement(name = "cust-supp-add3")
	protected String custOrSuppAddress3;

	@Expose
	@SerializedName("custOrSupAddr4")
	@XmlElement(name = "cust-supp-add4")
	protected String custOrSuppAddress4;

	@Expose
	@SerializedName("claimRefundFlag")
	@XmlElement(name = "clm-refund-flg")
	protected String claimRefundFlag;

	@Expose
	@SerializedName("stateApplyingCess")
	@XmlElement(name = "state-app-cess")
	protected String stateApplyingCess;

	@Expose
	@SerializedName("autoPopToRefundFlag")
	@XmlElement(name = "auto-pop-ref-flg")
	protected String autoPopToRefundFlag;

	@Expose
	@SerializedName("userId")
	@XmlElement(name = "user-id")
	protected String userId;

	@Expose
	@SerializedName("srcFileName")
	@XmlElement(name = "src-file-nm")
	protected String sourceFileName;

	@Expose
	@SerializedName("srcIdentifier")
	@XmlElement(name = "src-idntfr")
	protected String sourceIdentifier;

	/* Amendment details */
	/*
	 * @Expose
	 * 
	 * @SerializedName("originalDocNo")
	 * 
	 * name = "ORIGINAL_DOC_NUM") protected String origDocNo;
	 * 
	 * @Expose
	 * 
	 * @SerializedName("originalDocDate")
	 * 
	 * name = "ORIGINAL_DOC_DATE") protected LocalDate origDocDate;
	 */
	@Expose
	@SerializedName("isError")
	@XmlElement(name = "is-err")
	private boolean isError;

	/**
	 * Sets if the document has information messages
	 */
	@Expose
	@SerializedName("isInfo")
	@XmlElement(name = "is-info")
	protected boolean isInfo;

	@Expose
	@SerializedName("docAmt")
	@XmlElement(name = "doc-amt")
	protected BigDecimal docAmount; // Total Invoice/Document
									// Amount

	@Transient
	@XmlElement(name = "oth-val")
	protected BigDecimal otherValues;

	@Transient
	@XmlElement(name = "st-cess-amt")
	protected BigDecimal stateCessAmount;

	@Expose
	@SerializedName("taxableValue")
	@XmlElement(name = "tx-val")
	protected BigDecimal taxableValue; // Total Taxable Value.

	@Expose
	@SerializedName("isProcessed")
	@XmlElement(name = "is-procss")
	private boolean isProcessed;

	@Transient
	protected String status;

	/* Org Hierarchy Details */
	@Expose
	@SerializedName("division")
	protected String division;

	@Expose
	@SerializedName("location")
	protected String location;

	@Expose
	@SerializedName("subDivision")
	@XmlElement(name = "sub-division")
	protected String subDivision;

	@Expose
	@SerializedName("profitCentre1")
	@XmlElement(name = "prft-centr1")
	protected String profitCentre;

	@Expose
	@SerializedName("crDrPreGst")
	@XmlElement(name = "crdr-pre-gst")
	protected String crDrPreGst;

	@Expose
	@SerializedName("portCode")
	@XmlElement(name = "port-cd")
	protected String portCode;

	/**
	 * diffPercent specifies flag and accepts either 'L65' or 'N' if it is
	 * blank, it is considered as 'N'
	 */
	@Expose
	@SerializedName("diffPercent")
	@XmlElement(name= "diff-prcnt")
	protected String diffPercent;

	@Expose
	@SerializedName("udf1")
	@XmlElement(name = "ud-field1")
	protected String userdefinedfield1;

	@Expose
	@SerializedName("udf2")
	@XmlElement(name = "ud-field2")
	protected String userdefinedfield2;

	@Expose
	@SerializedName("udf3")
	@XmlElement(name = "ud-field3")
	protected String userdefinedfield3;

	@Expose
	@SerializedName("udf4")
	@XmlElement(name = "ud-field4")
	protected String userDefinedField4;

	@Expose
	@SerializedName("profitCentre3")
	@XmlElement(name = "profit-cntr3")
	protected String userAccess1;

	@Expose
	@SerializedName("profitCentre4")
	@XmlElement(name = "profit-cntr4")
	protected String userAccess2;

	@Expose
	@SerializedName("profitCentre5")
	@XmlElement(name = "profit-cntr5")
	protected String userAccess3;

	@Expose
	@SerializedName("profitCentre6")
	@XmlElement(name = "profit-cntr6")
	protected String userAccess4;

	@Expose
	@SerializedName("profitCentre7")
	@XmlElement(name = "profit-cntr7")
	protected String userAccess5;

	@Expose
	@SerializedName("profitCentre8")
	@XmlElement(name = "profit-cntr8")
	protected String userAccess6;

	@Expose
	@SerializedName("ewbNo")
	@XmlElement(name = "ewb-no")
	protected String eWayBillNo;

	@Expose
	@SerializedName("ewbDate")
	@XmlElement(name = "ewb-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate eWayBillDate;

	@Expose
	@SerializedName("taxPayable")
	@XmlElement(name = "tx-paybl")
	protected BigDecimal taxPayable;

	@Expose
	@SerializedName("extractedBatchId")
	@XmlElement(name = "extr-batch-id")
	protected Long extractedBatchId; // ERP Extracted Batch Id

	@Expose
	@SerializedName("extractedOn")
	@Transient
	@XmlElement(name = "extr-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime extractedOn;// ERP Extracted Date

	@Expose
	@SerializedName("extractedDate")
	@XmlElement(name = "extr-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate extractedDate;// ERP Extracted Date - Derived

	@Expose
	@SerializedName("initiatedOn")
	@XmlElement(name = "init-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime initiatedOn;

	@Expose
	@SerializedName("hciReceivedOn")
	@XmlElement(name = "hci-rec-dt")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime hciReceivedOn;

	@Expose
	@SerializedName("javaReqReceivedOn")
	@XmlElement(name = "req-rec-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime reqReceivedOn;

	@Expose
	@SerializedName("javaBeforeSavingOn")
	@XmlElement(name = "bfr-sav-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime beforeSavingOn;

	@Expose
	@SerializedName("dataOriginTypeCode")
	@XmlElement(name = "data-org-typ-cd")
	protected String dataOriginTypeCode;

	@Expose
	@SerializedName("isSaved")
	@XmlElement(name = "is-saved")
	protected boolean isSaved;

	@Expose
	@SerializedName("isSent")
	@XmlElement(name = "is-sent")
	protected boolean isSent;

	@Expose
	@SerializedName("sentToGSTNDate")
	@XmlElement(name = "sent-to-gstn-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate sentToGSTNDate;

	@Expose
	@SerializedName("savedToGSTNDate")
	@XmlElement(name = "sav-gstn-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate savedToGSTNDate;

	@Expose
	@SerializedName("isGstnError")
	@XmlElement(name = "is-gstn-err")
	protected boolean isGstnError;

	@Expose
	@SerializedName("isSubmitted")
	@XmlElement(name = "is-submitted")
	protected boolean isSubmitted;

	@Expose
	@SerializedName("gstnBatchId")
	@XmlElement(name = "gstn-btch-id")
	protected Long gstnBatchId;

	@Transient
	@XmlElement(name = "entity-id")
	private Long entityId;

	@Transient
	@XmlElement(name = "grp-id")
	private Long groupId;


	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo
	 *            the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the docDate
	 */
	public LocalDate getDocDate() {
		return docDate;
	}

	/**
	 * @param docDate
	 *            the docDate to set
	 */
	public void setDocDate(LocalDate docDate) {
		this.docDate = docDate;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType
	 *            the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
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

	/**
	 * @return the docKey
	 */
	public String getDocKey() {
		return docKey;
	}

	/**
	 * @param docKey
	 *            the docKey to set
	 */
	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}

	/**
	 * @return the plantCode
	 */
	public String getPlantCode() {
		return plantCode;
	}

	/**
	 * @param plantCode
	 *            the plantCode to set
	 */
	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	/**
	 * @return the sgstin
	 */
	public String getSgstin() {
		return sgstin;
	}

	/**
	 * @param sgstin
	 *            the sgstin to set
	 */
	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	/**
	 * @return the cgstin
	 */
	public String getCgstin() {
		return cgstin;
	}

	/**
	 * @param cgstin
	 *            the cgstin to set
	 */
	public void setCgstin(String cgstin) {
		this.cgstin = cgstin;
	}

	/**
	 * @return the supplyType
	 */
	public String getSupplyType() {
		return supplyType;
	}

	/**
	 * @param supplyType
	 *            the supplyType to set
	 */
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
	 * @param reverseCharge
	 *            the reverseCharge to set
	 */
	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	/**
	 * @return the pos
	 */
	public String getPos() {
		return pos;
	}

	/**
	 * @param pos
	 *            the pos to set
	 */
	public void setPos(String pos) {
		this.pos = pos;
	}

	/**
	 * @return the custOrSuppCode
	 */
	public String getCustOrSuppCode() {
		return custOrSuppCode;
	}

	/**
	 * @param custOrSuppCode
	 *            the custOrSuppCode to set
	 */
	public void setCustOrSuppCode(String custOrSuppCode) {
		this.custOrSuppCode = custOrSuppCode;
	}

	/**
	 * @return the custOrSuppType
	 */
	public String getCustOrSuppType() {
		return custOrSuppType;
	}

	/**
	 * @param custOrSuppType
	 *            the custOrSuppType to set
	 */
	public void setCustOrSuppType(String custOrSuppType) {
		this.custOrSuppType = custOrSuppType;
	}

	/**
	 * @return the section7OfIgstFlag
	 */
	public String getSection7OfIgstFlag() {
		return section7OfIgstFlag;
	}

	/**
	 * @param section7OfIgstFlag
	 *            the section7OfIgstFlag to set
	 */
	public void setSection7OfIgstFlag(String section7OfIgstFlag) {
		this.section7OfIgstFlag = section7OfIgstFlag;
	}

	/**
	 * @return the custOrSuppName
	 */
	public String getCustOrSuppName() {
		return custOrSuppName;
	}

	/**
	 * @param custOrSuppName
	 *            the custOrSuppName to set
	 */
	public void setCustOrSuppName(String custOrSuppName) {
		this.custOrSuppName = custOrSuppName;
	}

	/**
	 * @return the custOrSuppAddress1
	 */
	public String getCustOrSuppAddress1() {
		return custOrSuppAddress1;
	}

	/**
	 * @param custOrSuppAddress1
	 *            the custOrSuppAddress1 to set
	 */
	public void setCustOrSuppAddress1(String custOrSuppAddress1) {
		this.custOrSuppAddress1 = custOrSuppAddress1;
	}

	/**
	 * @return the custOrSuppAddress2
	 */
	public String getCustOrSuppAddress2() {
		return custOrSuppAddress2;
	}

	/**
	 * @param custOrSuppAddress2
	 *            the custOrSuppAddress2 to set
	 */
	public void setCustOrSuppAddress2(String custOrSuppAddress2) {
		this.custOrSuppAddress2 = custOrSuppAddress2;
	}

	/**
	 * @return the custOrSuppAddress3
	 */
	public String getCustOrSuppAddress3() {
		return custOrSuppAddress3;
	}

	/**
	 * @param custOrSuppAddress3
	 *            the custOrSuppAddress3 to set
	 */
	public void setCustOrSuppAddress3(String custOrSuppAddress3) {
		this.custOrSuppAddress3 = custOrSuppAddress3;
	}

	/**
	 * @return the custOrSuppAddress4
	 */
	public String getCustOrSuppAddress4() {
		return custOrSuppAddress4;
	}

	/**
	 * @param custOrSuppAddress4
	 *            the custOrSuppAddress4 to set
	 */
	public void setCustOrSuppAddress4(String custOrSuppAddress4) {
		this.custOrSuppAddress4 = custOrSuppAddress4;
	}

	/**
	 * @return the claimRefundFlag
	 */
	public String getClaimRefundFlag() {
		return claimRefundFlag;
	}

	/**
	 * @param claimRefundFlag
	 *            the claimRefundFlag to set
	 */
	public void setClaimRefundFlag(String claimRefundFlag) {
		this.claimRefundFlag = claimRefundFlag;
	}

	/**
	 * @return the stateApplyingCess
	 */
	public String getStateApplyingCess() {
		return stateApplyingCess;
	}

	/**
	 * @param stateApplyingCess
	 *            the stateApplyingCess to set
	 */
	public void setStateApplyingCess(String stateApplyingCess) {
		this.stateApplyingCess = stateApplyingCess;
	}

	/**
	 * @return the autoPopToRefundFlag
	 */
	public String getAutoPopToRefundFlag() {
		return autoPopToRefundFlag;
	}

	/**
	 * @param autoPopToRefundFlag
	 *            the autoPopToRefundFlag to set
	 */
	public void setAutoPopToRefundFlag(String autoPopToRefundFlag) {
		this.autoPopToRefundFlag = autoPopToRefundFlag;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the sourceFileName
	 */
	public String getSourceFileName() {
		return sourceFileName;
	}

	/**
	 * @param sourceFileName
	 *            the sourceFileName to set
	 */
	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	/**
	 * @return the sourceIdentifier
	 */
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}

	/**
	 * @param sourceIdentifier
	 *            the sourceIdentifier to set
	 */
	public void setSourceIdentifier(String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}

	/**
	 * @return the docAmount
	 */
	public BigDecimal getDocAmount() {
		return docAmount;
	}

	/**
	 * @param docAmount
	 *            the docAmount to set
	 */
	public void setDocAmount(BigDecimal docAmount) {
		this.docAmount = docAmount;
	}

	/**
	 * @return the taxableValue
	 */
	public BigDecimal getTaxableValue() {
		return taxableValue;
	}

	/**
	 * @param taxableValue
	 *            the taxableValue to set
	 */
	public void setTaxableValue(BigDecimal taxableValue) {
		this.taxableValue = taxableValue;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the division
	 */
	public String getDivision() {
		return division;
	}

	/**
	 * @param division
	 *            the division to set
	 */
	public void setDivision(String division) {
		this.division = division;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the subDivision
	 */
	public String getSubDivision() {
		return subDivision;
	}

	/**
	 * @param subDivision
	 *            the subDivision to set
	 */
	public void setSubDivision(String subDivision) {
		this.subDivision = subDivision;
	}

	/**
	 * @return the profitCentre
	 */
	public String getProfitCentre() {
		return profitCentre;
	}

	/**
	 * @param profitCentre
	 *            the profitCentre to set
	 */
	public void setProfitCentre(String profitCentre) {
		this.profitCentre = profitCentre;
	}

	/**
	 * @return the crDrPreGst
	 */
	public String getCrDrPreGst() {
		return crDrPreGst;
	}

	/**
	 * @param crDrPreGst
	 *            the crDrPreGst to set
	 */
	public void setCrDrPreGst(String crDrPreGst) {
		this.crDrPreGst = crDrPreGst;
	}

	/**
	 * @return the portCode
	 */
	public String getPortCode() {
		return portCode;
	}

	/**
	 * @param portCode
	 *            the portCode to set
	 */
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	/**
	 * @return the diffPercent
	 */
	public String getDiffPercent() {
		return diffPercent;
	}

	/**
	 * @param diffPercent
	 *            the diffPercent to set
	 */
	public void setDiffPercent(String diffPercent) {
		this.diffPercent = diffPercent;
	}

	/**
	 * @return the userdefinedfield1
	 */
	public String getUserdefinedfield1() {
		return userdefinedfield1;
	}

	/**
	 * @param userdefinedfield1
	 *            the userdefinedfield1 to set
	 */
	public void setUserdefinedfield1(String userdefinedfield1) {
		this.userdefinedfield1 = userdefinedfield1;
	}

	/**
	 * @return the userdefinedfield2
	 */
	public String getUserdefinedfield2() {
		return userdefinedfield2;
	}

	/**
	 * @param userdefinedfield2
	 *            the userdefinedfield2 to set
	 */
	public void setUserdefinedfield2(String userdefinedfield2) {
		this.userdefinedfield2 = userdefinedfield2;
	}

	/**
	 * @return the userdefinedfield3
	 */
	public String getUserdefinedfield3() {
		return userdefinedfield3;
	}

	/**
	 * @param userdefinedfield3
	 *            the userdefinedfield3 to set
	 */
	public void setUserdefinedfield3(String userdefinedfield3) {
		this.userdefinedfield3 = userdefinedfield3;
	}

	/**
	 * @return the userDefinedField4
	 */
	public String getUserDefinedField4() {
		return userDefinedField4;
	}

	/**
	 * @param userDefinedField4
	 *            the userDefinedField4 to set
	 */
	public void setUserDefinedField4(String userDefinedField4) {
		this.userDefinedField4 = userDefinedField4;
	}

	/**
	 * @return the userAccess1
	 */
	public String getUserAccess1() {
		return userAccess1;
	}

	/**
	 * @param userAccess1
	 *            the userAccess1 to set
	 */
	public void setUserAccess1(String userAccess1) {
		this.userAccess1 = userAccess1;
	}

	/**
	 * @return the userAccess2
	 */
	public String getUserAccess2() {
		return userAccess2;
	}

	/**
	 * @param userAccess2
	 *            the userAccess2 to set
	 */
	public void setUserAccess2(String userAccess2) {
		this.userAccess2 = userAccess2;
	}

	/**
	 * @return the userAccess3
	 */
	public String getUserAccess3() {
		return userAccess3;
	}

	/**
	 * @param userAccess3
	 *            the userAccess3 to set
	 */
	public void setUserAccess3(String userAccess3) {
		this.userAccess3 = userAccess3;
	}

	/**
	 * @return the userAccess4
	 */
	public String getUserAccess4() {
		return userAccess4;
	}

	/**
	 * @param userAccess4
	 *            the userAccess4 to set
	 */
	public void setUserAccess4(String userAccess4) {
		this.userAccess4 = userAccess4;
	}

	/**
	 * @return the userAccess5
	 */
	public String getUserAccess5() {
		return userAccess5;
	}

	/**
	 * @param userAccess5
	 *            the userAccess5 to set
	 */
	public void setUserAccess5(String userAccess5) {
		this.userAccess5 = userAccess5;
	}

	/**
	 * @return the userAccess6
	 */
	public String getUserAccess6() {
		return userAccess6;
	}

	/**
	 * @param userAccess6
	 *            the userAccess6 to set
	 */
	public void setUserAccess6(String userAccess6) {
		this.userAccess6 = userAccess6;
	}

	/**
	 * @return the eWayBillNo
	 */
	public String geteWayBillNo() {
		return eWayBillNo;
	}

	/**
	 * @param eWayBillNo
	 *            the eWayBillNo to set
	 */
	public void seteWayBillNo(String eWayBillNo) {
		this.eWayBillNo = eWayBillNo;
	}

	/**
	 * @return the eWayBillDate
	 */
	public LocalDate geteWayBillDate() {
		return eWayBillDate;
	}

	/**
	 * @param eWayBillDate
	 *            the eWayBillDate to set
	 */
	public void seteWayBillDate(LocalDate eWayBillDate) {
		this.eWayBillDate = eWayBillDate;
	}

	/**
	 * @return the taxPayable
	 */
	public BigDecimal getTaxPayable() {
		return taxPayable;
	}

	/**
	 * @param taxPayable
	 *            the taxPayable to set
	 */
	public void setTaxPayable(BigDecimal taxPayable) {
		this.taxPayable = taxPayable;
	}

	/**
	 * @return the initiatedOn
	 */
	public LocalDateTime getInitiatedOn() {
		return initiatedOn;
	}

	/**
	 * @param initiatedOn
	 *            the initiatedOn to set
	 */
	public void setInitiatedOn(LocalDateTime initiatedOn) {
		this.initiatedOn = initiatedOn;
	}

	/**
	 * @return the hciReceivedOn
	 */
	public LocalDateTime getHciReceivedOn() {
		return hciReceivedOn;
	}

	/**
	 * @param hciReceivedOn
	 *            the hciReceivedOn to set
	 */
	public void setHciReceivedOn(LocalDateTime hciReceivedOn) {
		this.hciReceivedOn = hciReceivedOn;
	}

	/**
	 * @return the reqReceivedOn
	 */
	public LocalDateTime getReqReceivedOn() {
		return reqReceivedOn;
	}

	/**
	 * @param reqReceivedOn
	 *            the reqReceivedOn to set
	 */
	public void setReqReceivedOn(LocalDateTime reqReceivedOn) {
		this.reqReceivedOn = reqReceivedOn;
	}

	/**
	 * @return the beforeSavingOn
	 */
	public LocalDateTime getBeforeSavingOn() {
		return beforeSavingOn;
	}

	/**
	 * @param beforeSavingOn
	 *            the beforeSavingOn to set
	 */
	public void setBeforeSavingOn(LocalDateTime beforeSavingOn) {
		this.beforeSavingOn = beforeSavingOn;
	}

	/**
	 * @return the isError
	 */
	public boolean getIsError() {
		return isError;
	}

	/**
	 * @param isError
	 *            the isError to set
	 */
	public void setIsError(boolean isError) {
		this.isError = isError;
	}

	/**
	 * @return the isInfo
	 */
	public boolean getIsInfo() {
		return isInfo;
	}

	/**
	 * @param isInfo
	 *            the isInfo to set
	 */
	public void setIsInfo(boolean isInfo) {
		this.isInfo = isInfo;
	}

	/**
	 * @return the isProcessed
	 */
	public boolean getIsProcessed() {
		return isProcessed;
	}

	/**
	 * @param isProcessed
	 *            the isProcessed to set
	 */
	public void setIsProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	/**
	 * @return the dataOriginTypeCode
	 */
	public String getDataOriginTypeCode() {
		return dataOriginTypeCode;
	}

	/**
	 * @param dataOriginTypeCode
	 *            the dataOriginTypeCode to set
	 */
	public void setDataOriginTypeCode(String dataOriginTypeCode) {
		this.dataOriginTypeCode = dataOriginTypeCode;
	}

	public BigDecimal getOtherValues() {
		return otherValues;
	}

	public void setOtherValues(BigDecimal otherValues) {
		this.otherValues = otherValues;
	}

	public BigDecimal getStateCessAmount() {
		return stateCessAmount;
	}

	public void setStateCessAmount(BigDecimal stateCessAmount) {
		this.stateCessAmount = stateCessAmount;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	

	/**
	 * @return the isGstnError
	 */
	public boolean isGstnError() {
		return isGstnError;
	}

	/**
	 * @param isGstnError
	 *            the isGstnError to set
	 */
	public void setGstnError(boolean isGstnError) {
		this.isGstnError = isGstnError;
	}

	/**
	 * @return the isSent
	 */
	public boolean isSent() {
		return isSent;
	}

	/**
	 * @param isSent
	 *            the isSent to set
	 */
	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	/**
	 * @return the sentToGSTNDate
	 */
	public LocalDate getSentToGSTNDate() {
		return sentToGSTNDate;
	}

	/**
	 * @param sentToGSTNDate
	 *            the sentToGSTNDate to set
	 */
	public void setSentToGSTNDate(LocalDate sentToGSTNDate) {
		this.sentToGSTNDate = sentToGSTNDate;
	}

	/**
	 * @return the savedToGSTNDate
	 */
	public LocalDate getSavedToGSTNDate() {
		return savedToGSTNDate;
	}

	/**
	 * @param savedToGSTNDate
	 *            the savedToGSTNDate to set
	 */
	public void setSavedToGSTNDate(LocalDate savedToGSTNDate) {
		this.savedToGSTNDate = savedToGSTNDate;
	}

	/**
	 * @return the isSubmitted
	 */
	public boolean isSubmitted() {
		return isSubmitted;
	}

	/**
	 * @param isSubmitted
	 *            the isSubmitted to set
	 */
	public void setSubmitted(boolean isSubmitted) {
		this.isSubmitted = isSubmitted;
	}

	/**
	 * @return the gstnBatchId
	 */
	public Long getGstnBatchId() {
		return gstnBatchId;
	}

	/**
	 * @param gstnBatchId
	 *            the gstnBatchId to set
	 */
	public void setGstnBatchId(Long gstnBatchId) {
		this.gstnBatchId = gstnBatchId;
	}

	/**
	 * @return the isSaved
	 */
	public boolean isSaved() {
		return isSaved;
	}

	/**
	 * @param isSaved
	 *            the isSaved to set
	 */
	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}

	/**
	 * @return the extractedBatchId
	 */
	public Long getExtractedBatchId() {
		return extractedBatchId;
	}

	/**
	 * @param extractedBatchId
	 *            the extractedBatchId to set
	 */
	public void setExtractedBatchId(Long extractedBatchId) {
		this.extractedBatchId = extractedBatchId;
	}

	/**
	 * @return the extractedOn
	 */
	public LocalDateTime getExtractedOn() {
		return extractedOn;
	}

	/**
	 * @param extractedOn
	 *            the extractedOn to set
	 */
	public void setExtractedOn(LocalDateTime extractedOn) {
		this.extractedOn = extractedOn;
	}

	/**
	 * @return the extractedDate
	 */
	public LocalDate getExtractedDate() {
		return extractedDate;
	}

	/**
	 * @param extractedDate
	 *            the extractedDate to set
	 */
	public void setExtractedDate(LocalDate extractedDate) {
		this.extractedDate = extractedDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"TransDocument [docNo=%s, docDate=%s, docType=%s, finYear=%s, "
						+ "docKey=%s, plantCode=%s, sgstin=%s, cgstin=%s, "
						+ "supplyType=%s, reverseCharge=%s, pos=%s, "
						+ "custOrSuppCode=%s, custOrSuppType=%s, "
						+ "section7OfIgstFlag=%s, custOrSuppName=%s, "
						+ "custOrSuppAddress1=%s, custOrSuppAddress2=%s, "
						+ "custOrSuppAddress3=%s, custOrSuppAddress4=%s, "
						+ "claimRefundFlag=%s, stateApplyingCess=%s, "
						+ "autoPopToRefundFlag=%s, userId=%s, "
						+ "sourceFileName=%s, "
						+ "sourceIdentifier=%s, origDocNo=%s, origDocDate=%s, "
						+ "isError=%s, isInfo=%s, docAmount=%s, "
						+ "otherValues=%s, "
						+ "stateCessAmount=%s, taxableValue=%s, "
						+ "isProcessed=%s, "
						+ "status=%s, division=%s, location=%s, "
						+ "subDivision=%s, "
						+ "profitCentre=%s, crDrPreGst=%s, portCode=%s, "
						+ "diffPercent=%s, userdefinedfield1=%s, "
						+ "userdefinedfield2=%s, userdefinedfield3=%s, "
						+ "userDefinedField4=%s, userAccess1=%s, "
						+ "userAccess2=%s, "
						+ "userAccess3=%s, userAccess4=%s, userAccess5=%s, "
						+ "userAccess6=%s, eWayBillNo=%s, eWayBillDate=%s, "
						+ "taxPayable=%s, extractedBatchId=%s, extractedOn=%s, "
						+ "extractedDate=%s, initiatedOn=%s, hciReceivedOn=%s,"
						+ "reqReceivedOn=%s, beforeSavingOn=%s, "
						+ "dataOriginTypeCode=%s, isSaved=%s, isSent=%s, "
						+ "sentToGSTNDate=%s, savedToGSTNDate=%s, "
						+ "isGstnError=%s, "
						+ "isSubmitted=%s, gstnBatchId=%s, entityId=%s, "
						+ "groupId=%s, "
						+ "entityAtValMap=%s, entityAtConfMap=%s, "
						+ "entityConfigParamMap=%s]",
				docNo, docDate, docType, finYear, docKey, plantCode, sgstin,
				cgstin, supplyType, reverseCharge, pos, custOrSuppCode,
				custOrSuppType, section7OfIgstFlag, custOrSuppName,
				custOrSuppAddress1, custOrSuppAddress2, custOrSuppAddress3,
				custOrSuppAddress4, claimRefundFlag, stateApplyingCess,
				autoPopToRefundFlag, userId, sourceFileName, sourceIdentifier,
				isError, isInfo, docAmount, otherValues, stateCessAmount,
				taxableValue, isProcessed, status, division, location,
				subDivision, profitCentre, crDrPreGst, portCode, diffPercent,
				userdefinedfield1, userdefinedfield2, userdefinedfield3,
				userDefinedField4, userAccess1, userAccess2, userAccess3,
				userAccess4, userAccess5, userAccess6, eWayBillNo, eWayBillDate,
				taxPayable, extractedBatchId, extractedOn, extractedDate,
				initiatedOn, hciReceivedOn, reqReceivedOn, beforeSavingOn,
				dataOriginTypeCode, isSaved, isSent, sentToGSTNDate,
				savedToGSTNDate, isGstnError, isSubmitted, gstnBatchId,
				entityId, groupId);
	}

}
