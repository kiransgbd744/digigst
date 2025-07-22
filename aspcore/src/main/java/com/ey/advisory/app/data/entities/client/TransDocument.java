package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.ey.advisory.common.Document;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

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
public abstract class TransDocument extends Document {

	/* The KEY fields of a financial document */
	@Expose
	@SerializedName("docNo")
	@Column(name = "DOC_NUM")
	protected String docNo;

	@Expose
	@SerializedName("docDate")
	@Column(name = "DOC_DATE")
	protected LocalDate docDate;

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
	@Expose
	@SerializedName("docKey")
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
	@SerializedName("suppGstin")
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
	@SerializedName("custOrSupCode")
	@Column(name = "CUST_SUPP_CODE")
	protected String custOrSuppCode;

	@Expose
	@SerializedName("custOrSupType")
	@Column(name = "CUST_SUPP_TYPE")
	protected String custOrSuppType;

	@Expose
	@SerializedName("sec7OfIgstFlag")
	@Column(name = "SECTION7_OF_IGST_FLAG")
	protected String section7OfIgstFlag;

	/**
	 * For Outward invoices, it's called customer name and for Inward Invoices
	 * it's called supplier name.
	 */
	@Expose
	@SerializedName("custOrSupName")
	@Column(name = "CUST_SUPP_NAME")
	protected String custOrSuppName;

	@Expose
	@SerializedName("custOrSupAddr1")
	@Column(name = "CUST_SUPP_ADDRESS1")
	protected String custOrSuppAddress1;

	@Expose
	@SerializedName("custOrSupAddr2")
	@Column(name = "CUST_SUPP_ADDRESS2")
	protected String custOrSuppAddress2;

	@Expose
	@SerializedName("custOrSupAddr3")
	@Column(name = "CUST_SUPP_ADDRESS3")
	protected String custOrSuppAddress3;

	@Expose
	@SerializedName("custOrSupAddr4")
	@Column(name = "CUST_SUPP_ADDRESS4")
	protected String custOrSuppAddress4;

	@Expose
	@SerializedName("claimRefundFlag")
	@Column(name = "CLAIM_REFUND_FLAG")
	protected String claimRefundFlag;

	@Expose
	@SerializedName("stateApplyingCess")
	@Column(name = "STATE_APPLYING_CESS")
	protected String stateApplyingCess;

	@Expose
	@SerializedName("autoPopToRefundFlag")
	@Column(name = "AUTOPOPULATE_TO_REFUND")
	protected String autoPopToRefundFlag;

	@Expose
	@SerializedName("userId")
	@Column(name = "USER_ID")
	protected String userId;

	@Expose
	@SerializedName("srcFileName")
	@Column(name = "SOURCE_FILENAME")
	protected String sourceFileName;

	@Expose
	@SerializedName("srcIdentifier")
	@Column(name = "SOURCE_IDENTIFIER")
	protected String sourceIdentifier;

	/* Amendment details */

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private boolean isError;

	/**
	 * Sets if the document has information messages
	 */
	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	protected boolean isInfo;

	@Expose
	@SerializedName("docAmt")
	@Column(name = "DOC_AMT")
	protected BigDecimal docAmount; // Total Invoice/Document

	@Expose
	@SerializedName("stateCessAmt")
	@Column(name = "STATECESS_AMT")
	protected BigDecimal stateCessAmount;

	@Expose
	@SerializedName("otherValues")
	@Column(name = "OTHER_VALUES")
	protected BigDecimal otherValues;

	@Expose
	@SerializedName("taxableValue")
	@Column(name = "TAXABLE_VALUE")
	protected BigDecimal taxableValue; // Total Taxable Value.

	@Expose
	@SerializedName("isProcessed")
	@Column(name = "IS_PROCESSED")
	private boolean isProcessed;

	@Transient
	protected String status;

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
	@SerializedName("profitCentre1")
	@Column(name = "PROFIT_CENTRE")
	protected String profitCentre;

	@Expose
	@SerializedName("crDrPreGst")
	@Column(name = "CRDR_PRE_GST")
	protected String crDrPreGst;

	@Expose
	@SerializedName("portCode")
	@Column(name = "SHIP_PORT_CODE")
	protected String portCode;

	/**
	 * diffPercent specifies flag and accepts either 'L65' or 'N' if it is
	 * blank, it is considered as 'N'
	 */
	@Expose
	@SerializedName("diffPercent")
	@Column(name = "DIFF_PERCENT")
	protected String diffPercent;

	@Expose
	@SerializedName("udf1")
	@Column(name = "USERDEFINED_FIELD1")
	protected String userdefinedfield1;

	@Expose
	@SerializedName("udf2")
	@Column(name = "USERDEFINED_FIELD2")
	protected String userdefinedfield2;

	@Expose
	@SerializedName("udf3")
	@Column(name = "USERDEFINED_FIELD3")
	protected String userdefinedfield3;

	@Expose
	@SerializedName("udf4")
	@Column(name = "USERDEFINED_FIELD4")
	protected String userDefinedField4;

	@Expose
	@SerializedName("profitCentre3")
	@Column(name = "USERACCESS1")
	protected String userAccess1;

	@Expose
	@SerializedName("profitCentre4")
	@Column(name = "USERACCESS2")
	protected String userAccess2;

	@Expose
	@SerializedName("profitCentre5")
	@Column(name = "USERACCESS3")
	protected String userAccess3;

	@Expose
	@SerializedName("profitCentre6")
	@Column(name = "USERACCESS4")
	protected String userAccess4;

	@Expose
	@SerializedName("profitCentre7")
	@Column(name = "USERACCESS5")
	protected String userAccess5;

	@Expose
	@SerializedName("profitCentre8")
	@Column(name = "USERACCESS6")
	protected String userAccess6;

	@Expose
	@SerializedName("ewbNo")
	@Column(name = "EWAY_BILL_NUM")
	protected String eWayBillNo;

	@Expose
	@SerializedName("taxPayable")
	@Column(name = "TAX_PAYABLE")
	protected BigDecimal taxPayable;

	@Expose
	@SerializedName("extractedBatchId")
	@Column(name = "EXTRACTED_BATCH_ID")
	protected Long extractedBatchId; // ERP Extracted Batch Id

	@Expose
	@SerializedName("extractedOn")
	@Transient
	protected LocalDateTime extractedOn;// ERP Extracted Date

	@Expose
	@SerializedName("extractedDate")
	@Column(name = "EXTRACTED_DATE")
	protected LocalDate extractedDate;// ERP Extracted Date - Derived

	@Expose
	@SerializedName("initiatedOn")
	@Column(name = "INITIATED_ON")
	protected LocalDateTime initiatedOn;

	@Expose
	@SerializedName("hciReceivedOn")
	@Column(name = "HCI_RECEIVED_ON")
	protected LocalDateTime hciReceivedOn;

	@Expose
	@SerializedName("javaReqReceivedOn")
	@Column(name = "JAVA_REQ_REC_ON")
	protected LocalDateTime reqReceivedOn;

	@Expose
	@SerializedName("javaBeforeSavingOn")
	@Column(name = "JAVA_BEFORE_SAVING_ON")
	protected LocalDateTime beforeSavingOn;

	@Expose
	@SerializedName("dataOriginTypeCode")
	@Column(name = "DATAORIGINTYPECODE")
	protected String dataOriginTypeCode;

	@Expose
	@SerializedName("isSaved")
	@Column(name = "IS_SAVED_TO_GSTN")
	protected boolean isSaved;

	@Expose
	@SerializedName("isSent")
	@Column(name = "IS_SENT_TO_GSTN")
	protected boolean isSent;

	@Expose
	@SerializedName("sentToGSTNDate")
	@Column(name = "SENT_TO_GSTN_DATE")
	protected LocalDate sentToGSTNDate;

	@Expose
	@SerializedName("savedToGSTNDate")
	@Column(name = "SAVED_TO_GSTN_DATE")
	protected LocalDate savedToGSTNDate;

	@Expose
	@SerializedName("isGstnError")
	@Column(name = "GSTN_ERROR")
	protected boolean isGstnError;

	@Expose
	@SerializedName("gstnBatchId")
	@Column(name = "BATCH_ID")
	protected Long gstnBatchId;

	@Expose
	@SerializedName("gstnErrorCode")
	@Column(name = "GSTN_ERROR_CODE")
	protected String gstnErrorCode;

	@Expose
	@SerializedName("gstnErrorDesc")
	@Column(name = "GSTN_ERROR_DESC")
	protected String gstnErrorDesc;

	@Expose
	@SerializedName("isSubmitted")
	@Column(name = "IS_SUBMITTED")
	protected boolean isSubmitted;

	@Expose
	@SerializedName("submittedDate")
	@Column(name = "SUBMITTED_DATE")
	protected LocalDate submittedDate;

	@Expose
	@SerializedName("isFiled")
	@Column(name = "IS_FILED")
	protected boolean isFiled;

	@Expose
	@SerializedName("filedDate")
	@Column(name = "FILED_DATE")
	protected LocalDate filedDate;

	@Expose
	@SerializedName("ewbDate")
	@Column(name = "EWAY_BILL_DATE")
	protected LocalDateTime eWayBillDate;

	@Expose
	@SerializedName("companyCode")
	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Expose
	@SerializedName("payloadId")
	@Column(name = "PAYLOAD_ID")
	protected String payloadId;

	@Expose
	@SerializedName("derivedSourceId")
	@Column(name = "DERIVED_SOURCE_ID")
	protected String derivedSourceId;

	@Expose
	@SerializedName("invOtherCharges")
	@Column(name = "INV_OTHER_CHARGES")
	protected BigDecimal invoiceOtherCharges;

	@Expose
	@SerializedName("invAssessableAmt")
	@Column(name = "INV_ASSESSABLE_AMT")
	protected BigDecimal invoiceAssessableAmount;

	@Expose
	@SerializedName("invIgstAmt")
	@Column(name = "INV_IGST_AMT")
	protected BigDecimal invoiceIgstAmount;

	@Expose
	@SerializedName("invCgstAmt")
	@Column(name = "INV_CGST_AMT")
	protected BigDecimal invoiceCgstAmount;

	@Expose
	@SerializedName("invSgstAmt")
	@Column(name = "INV_SGST_AMT")
	protected BigDecimal invoiceSgstAmount;

	@Expose
	@SerializedName("invCessAdvaloremAmt")
	@Column(name = "INV_CESS_ADVLRM_AMT")
	protected BigDecimal invoiceCessAdvaloremAmount;

	@Expose
	@SerializedName("invCessSpecificAmt")
	@Column(name = "INV_CESS_SPECIFIC_AMT")
	protected BigDecimal invoiceCessSpecificAmount;

	@Expose
	@SerializedName("invStateCessAmt")
	@Column(name = "INV_STATE_CESS_AMT")
	protected BigDecimal invoiceStateCessAmount;

	@Expose
	@SerializedName("invStateCessSpecificAmt")
	@Column(name = "INV_STATE_CESS_SPECIFIC_AMOUNT")
	protected BigDecimal invStateCessSpecificAmt;

	@Expose
	@SerializedName("udf28")
	@Column(name = "USERDEFINED_FIELD28")
	protected BigDecimal userDefinedField28;

	@Expose
	@SerializedName("irn")
	@Column(name = "IRN")
	protected String irn;

	@Expose
	@SerializedName("dispatcherGstin")
	@Column(name = "DISPATCHER_GSTIN")
	protected String dispatcherGstin;

	@Expose
	@SerializedName("shipToGstin")
	@Column(name = "SHIP_TO_GSTIN")
	protected String shipToGstin;

	@Expose
	@SerializedName("paymentDueDate")
	@Column(name = "PAYMENT_DUE_DATE")
	protected LocalDate paymentDueDate;
	/**
	 * EGSTIN will be present only for those transactions happening through an
	 * E-commerce supplier.
	 * 
	 */
	@Expose
	@SerializedName("ecomGSTIN")
	@Column(name = "ECOM_GSTIN")
	protected String egstin;

	@Expose
	@SerializedName("tcsFlag")
	@Column(name = "TCS_FLAG")
	protected String tcsFlag;

	@Expose
	@SerializedName("tdsFlag")
	@Column(name = "TDS_FLAG")
	protected String tdsFlag;

	@Expose
	@SerializedName("supTradeName")
	@Column(name = "SUPP_TRADE_NAME")
	protected String supplierTradeName;

	@Expose
	@SerializedName("itemRowCount")
	@Column(name = "ITM_ROW_COUNT")
	protected Long itemRowCount;

	@Expose
	@SerializedName("gstr3bFilingStatus")
	@Transient
	protected Boolean gstr3bFilingStatus;
	
	@Expose
	@SerializedName("gstr1FilingStatus")
	@Transient
	protected Boolean gstr1FilingStatus;

	@Expose
	@SerializedName("gstr3bFilingDate")
	@Transient
	protected LocalDate gstr3bFilingDate;
	
	@Expose
	@SerializedName("gstr1FilingDate")
	@Transient
	protected LocalDate gstr1FilingDate;
	
	@Transient
	private Long entityId;

	@Transient
	private String serviceOption;

	@Transient
	private String ewbGenerateOption;

	@Transient
	private Long groupId;

	/**
	 * formReturnType - This field is to set GSTR1 or ANX1-1 to invoke business
	 * rules in business rules chain. value to this field is set based on tax
	 * period
	 */
	@Transient
	private String formReturnType;

	@Transient
	private Map<Long, List<Pair<String, String>>> entityAtValMap;

	@Transient
	private Map<EntityAtConfigKey, Map<Long, String>> entityAtConfMap;

	@Transient
	private Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap;

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

	public Map<Long, List<Pair<String, String>>> getEntityAtValMap() {
		return entityAtValMap;
	}

	public void setEntityAtValMap(
			Map<Long, List<Pair<String, String>>> entityAtValMap) {
		this.entityAtValMap = entityAtValMap;
	}

	public Map<Long, List<EntityConfigPrmtEntity>> getEntityConfigParamMap() {
		return entityConfigParamMap;
	}

	public void setEntityConfigParamMap(
			Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap) {
		this.entityConfigParamMap = entityConfigParamMap;
	}

	public Map<EntityAtConfigKey, Map<Long, String>> getEntityAtConfMap() {
		return entityAtConfMap;
	}

	public void setEntityAtConfMap(
			Map<EntityAtConfigKey, Map<Long, String>> entityAtConfMap) {
		this.entityAtConfMap = entityAtConfMap;
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

	/**
	 * @return the serviceOption
	 */
	public String getServiceOption() {
		return serviceOption;
	}

	/**
	 * @param serviceOption
	 *            the serviceOption to set
	 */
	public void setServiceOption(String serviceOption) {
		this.serviceOption = serviceOption;
	}

	/**
	 * @return the formReturnType
	 */
	public String getFormReturnType() {
		return formReturnType;
	}

	/**
	 * @param formReturnType
	 *            the formReturnType to set
	 */
	public void setFormReturnType(String formReturnType) {
		this.formReturnType = formReturnType;
	}

	/**
	 * @return the ewbGenerateOption
	 */
	public String getEwbGenerateOption() {
		return ewbGenerateOption;
	}

	/**
	 * @param ewbGenerateOption
	 *            the ewbGenerateOption to set
	 */
	public void setEwbGenerateOption(String ewbGenerateOption) {
		this.ewbGenerateOption = ewbGenerateOption;
	}

	/**
	 * @return the eWayBillDate
	 */
	public LocalDateTime geteWayBillDate() {
		return eWayBillDate;
	}

	/**
	 * @param eWayBillDate
	 *            the eWayBillDate to set
	 */
	public void seteWayBillDate(LocalDateTime eWayBillDate) {
		this.eWayBillDate = eWayBillDate;
	}

	/**
	 * @return the gstnErrorCode
	 */
	public String getGstnErrorCode() {
		return gstnErrorCode;
	}

	/**
	 * @param gstnErrorCode
	 *            the gstnErrorCode to set
	 */
	public void setGstnErrorCode(String gstnErrorCode) {
		this.gstnErrorCode = gstnErrorCode;
	}

	/**
	 * @return the gstnErrorDesc
	 */
	public String getGstnErrorDesc() {
		return gstnErrorDesc;
	}

	/**
	 * @param gstnErrorDesc
	 *            the gstnErrorDesc to set
	 */
	public void setGstnErrorDesc(String gstnErrorDesc) {
		this.gstnErrorDesc = gstnErrorDesc;
	}

	/**
	 * @return the submittedDate
	 */
	public LocalDate getSubmittedDate() {
		return submittedDate;
	}

	/**
	 * @param submittedDate
	 *            the submittedDate to set
	 */
	public void setSubmittedDate(LocalDate submittedDate) {
		this.submittedDate = submittedDate;
	}

	/**
	 * @return the isFiled
	 */
	public boolean isFiled() {
		return isFiled;
	}

	/**
	 * @param isFiled
	 *            the isFiled to set
	 */
	public void setFiled(boolean isFiled) {
		this.isFiled = isFiled;
	}

	/**
	 * @return the filedDate
	 */
	public LocalDate getFiledDate() {
		return filedDate;
	}

	/**
	 * @param filedDate
	 *            the filedDate to set
	 */
	public void setFiledDate(LocalDate filedDate) {
		this.filedDate = filedDate;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	/**
	 * @param companyCode
	 *            the companyCode to set
	 */
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	/**
	 * @return the payloadId
	 */
	public String getPayloadId() {
		return payloadId;
	}

	/**
	 * @param payloadId
	 *            the payloadId to set
	 */
	public void setPayloadId(String payloadId) {
		this.payloadId = payloadId;
	}

	/**
	 * @return the derivedSourceId
	 */
	public String getDerivedSourceId() {
		return derivedSourceId;
	}

	/**
	 * @param derivedSourceId
	 *            the derivedSourceId to set
	 */
	public void setDerivedSourceId(String derivedSourceId) {
		this.derivedSourceId = derivedSourceId;
	}

	/**
	 * @return the invoiceOtherCharges
	 */
	public BigDecimal getInvoiceOtherCharges() {
		return invoiceOtherCharges;
	}

	/**
	 * @param invoiceOtherCharges
	 *            the invoiceOtherCharges to set
	 */
	public void setInvoiceOtherCharges(BigDecimal invoiceOtherCharges) {
		this.invoiceOtherCharges = invoiceOtherCharges;
	}

	/**
	 * @return the invoiceAssessableAmount
	 */
	public BigDecimal getInvoiceAssessableAmount() {
		return invoiceAssessableAmount;
	}

	/**
	 * @param invoiceAssessableAmount
	 *            the invoiceAssessableAmount to set
	 */
	public void setInvoiceAssessableAmount(BigDecimal invoiceAssessableAmount) {
		this.invoiceAssessableAmount = invoiceAssessableAmount;
	}

	/**
	 * @return the invoiceIgstAmount
	 */
	public BigDecimal getInvoiceIgstAmount() {
		return invoiceIgstAmount;
	}

	/**
	 * @param invoiceIgstAmount
	 *            the invoiceIgstAmount to set
	 */
	public void setInvoiceIgstAmount(BigDecimal invoiceIgstAmount) {
		this.invoiceIgstAmount = invoiceIgstAmount;
	}

	/**
	 * @return the invoiceCgstAmount
	 */
	public BigDecimal getInvoiceCgstAmount() {
		return invoiceCgstAmount;
	}

	/**
	 * @param invoiceCgstAmount
	 *            the invoiceCgstAmount to set
	 */
	public void setInvoiceCgstAmount(BigDecimal invoiceCgstAmount) {
		this.invoiceCgstAmount = invoiceCgstAmount;
	}

	/**
	 * @return the invoiceSgstAmount
	 */
	public BigDecimal getInvoiceSgstAmount() {
		return invoiceSgstAmount;
	}

	/**
	 * @param invoiceSgstAmount
	 *            the invoiceSgstAmount to set
	 */
	public void setInvoiceSgstAmount(BigDecimal invoiceSgstAmount) {
		this.invoiceSgstAmount = invoiceSgstAmount;
	}

	/**
	 * @return the invoiceCessAdvaloremAmount
	 */
	public BigDecimal getInvoiceCessAdvaloremAmount() {
		return invoiceCessAdvaloremAmount;
	}

	/**
	 * @param invoiceCessAdvaloremAmount
	 *            the invoiceCessAdvaloremAmount to set
	 */
	public void setInvoiceCessAdvaloremAmount(
			BigDecimal invoiceCessAdvaloremAmount) {
		this.invoiceCessAdvaloremAmount = invoiceCessAdvaloremAmount;
	}

	/**
	 * @return the invoiceCessSpecificAmount
	 */
	public BigDecimal getInvoiceCessSpecificAmount() {
		return invoiceCessSpecificAmount;
	}

	/**
	 * @param invoiceCessSpecificAmount
	 *            the invoiceCessSpecificAmount to set
	 */
	public void setInvoiceCessSpecificAmount(
			BigDecimal invoiceCessSpecificAmount) {
		this.invoiceCessSpecificAmount = invoiceCessSpecificAmount;
	}

	/**
	 * @return the invoiceStateCessAmount
	 */
	public BigDecimal getInvoiceStateCessAmount() {
		return invoiceStateCessAmount;
	}

	/**
	 * @param invoiceStateCessAmount
	 *            the invoiceStateCessAmount to set
	 */
	public void setInvoiceStateCessAmount(BigDecimal invoiceStateCessAmount) {
		this.invoiceStateCessAmount = invoiceStateCessAmount;
	}

	/**
	 * @return the invStateCessSpecificAmt
	 */
	public BigDecimal getInvStateCessSpecificAmt() {
		return invStateCessSpecificAmt;
	}

	/**
	 * @param invStateCessSpecificAmt
	 *            the invStateCessSpecificAmt to set
	 */
	public void setInvStateCessSpecificAmt(BigDecimal invStateCessSpecificAmt) {
		this.invStateCessSpecificAmt = invStateCessSpecificAmt;
	}

	/**
	 * @return the userDefinedField28
	 */
	public BigDecimal getUserDefinedField28() {
		return userDefinedField28;
	}

	/**
	 * @param userDefinedField28
	 *            the userDefinedField28 to set
	 */
	public void setUserDefinedField28(BigDecimal userDefinedField28) {
		this.userDefinedField28 = userDefinedField28;
	}

	/**
	 * @return the irn
	 */
	public String getIrn() {
		return irn;
	}

	/**
	 * @param irn
	 *            the irn to set
	 */
	public void setIrn(String irn) {
		this.irn = irn;
	}

	/**
	 * @return the dispatcherGstin
	 */
	public String getDispatcherGstin() {
		return dispatcherGstin;
	}

	/**
	 * @param dispatcherGstin
	 *            the dispatcherGstin to set
	 */
	public void setDispatcherGstin(String dispatcherGstin) {
		this.dispatcherGstin = dispatcherGstin;
	}

	/**
	 * @return the shipToGstin
	 */
	public String getShipToGstin() {
		return shipToGstin;
	}

	/**
	 * @param shipToGstin
	 *            the shipToGstin to set
	 */
	public void setShipToGstin(String shipToGstin) {
		this.shipToGstin = shipToGstin;
	}

	/**
	 * @return the paymentDueDate
	 */
	public LocalDate getPaymentDueDate() {
		return paymentDueDate;
	}

	/**
	 * @param paymentDueDate
	 *            the paymentDueDate to set
	 */
	public void setPaymentDueDate(LocalDate paymentDueDate) {
		this.paymentDueDate = paymentDueDate;
	}

	/**
	 * @return the egstin
	 */
	public String getEgstin() {
		return egstin;
	}

	/**
	 * @param egstin
	 *            the egstin to set
	 */
	public void setEgstin(String egstin) {
		this.egstin = egstin;
	}

	/**
	 * @return the tcsFlag
	 */
	public String getTcsFlag() {
		return tcsFlag;
	}

	/**
	 * @param tcsFlag
	 *            the tcsFlag to set
	 */
	public void setTcsFlag(String tcsFlag) {
		this.tcsFlag = tcsFlag;
	}

	/**
	 * @return the tdsFlag
	 */
	public String getTdsFlag() {
		return tdsFlag;
	}

	/**
	 * @param tdsFlag
	 *            the tdsFlag to set
	 */
	public void setTdsFlag(String tdsFlag) {
		this.tdsFlag = tdsFlag;
	}

	/**
	 * @return the supplierTradeName
	 */
	public String getSupplierTradeName() {
		return supplierTradeName;
	}

	/**
	 * @param supplierTradeName
	 *            the supplierTradeName to set
	 */
	public void setSupplierTradeName(String supplierTradeName) {
		this.supplierTradeName = supplierTradeName;
	}

	/**
	 * @return the itemRowCount
	 */
	public Long getItemRowCount() {
		return itemRowCount;
	}

	/**
	 * @param itemRowCount
	 *            the itemRowCount to set
	 */
	public void setItemRowCount(Long itemRowCount) {
		this.itemRowCount = itemRowCount;
	}

	public void setGstr1FilingStatus(Boolean gstr1FilingStatus) {
		this.gstr1FilingStatus = gstr1FilingStatus;
	}

	/**
	 * @return the gstr1FilingStatus
	 */
	public Boolean getGstr1FilingStatus() {
		return gstr1FilingStatus;
	}
	
	public void setGstr3BFilingStatus(Boolean gstr3bFilingStatus) {
		this.gstr3bFilingStatus = gstr3bFilingStatus;
	}

	/**
	 * @return the gstr3BFilingStatus
	 */
	public Boolean getGstr3BFilingStatus() {
		return gstr3bFilingStatus;
	}
	
	public void setGstr1FilingDate(LocalDate gstr1FilingDate) {
		this.gstr1FilingDate = gstr1FilingDate;
	}

	/**
	 * @return the gstr1FilingDate
	 */
	public LocalDate getGstr1FilingDate() {
		return gstr1FilingDate;
	}
	
	public void setGst3BFilingDate(LocalDate gstr3bFilingDate) {
		this.gstr3bFilingDate = gstr3bFilingDate;
	}

	/**
	 * @return the gstr3bFilingDate
	 */
	public LocalDate getGstr3BFilingDate() {
		return gstr3bFilingDate;
	}
	@Override
	public String toString() {
		return "TransDocument [docNo=" + docNo + ", docDate=" + docDate
				+ ", docType=" + docType + ", finYear=" + finYear + ", docKey="
				+ docKey + ", plantCode=" + plantCode + ", sgstin=" + sgstin
				+ ", cgstin=" + cgstin + ", supplyType=" + supplyType
				+ ", reverseCharge=" + reverseCharge + ", pos=" + pos
				+ ", custOrSuppCode=" + custOrSuppCode + ", custOrSuppType="
				+ custOrSuppType + ", section7OfIgstFlag=" + section7OfIgstFlag
				+ ", custOrSuppName=" + custOrSuppName + ", custOrSuppAddress1="
				+ custOrSuppAddress1 + ", custOrSuppAddress2="
				+ custOrSuppAddress2 + ", custOrSuppAddress3="
				+ custOrSuppAddress3 + ", custOrSuppAddress4="
				+ custOrSuppAddress4 + ", claimRefundFlag=" + claimRefundFlag
				+ ", stateApplyingCess=" + stateApplyingCess
				+ ", autoPopToRefundFlag=" + autoPopToRefundFlag + ", userId="
				+ userId + ", sourceFileName=" + sourceFileName
				+ ", sourceIdentifier=" + sourceIdentifier + ", isError="
				+ isError + ", isInfo=" + isInfo + ", docAmount=" + docAmount
				+ ", stateCessAmount=" + stateCessAmount + ", otherValues="
				+ otherValues + ", taxableValue=" + taxableValue
				+ ", isProcessed=" + isProcessed + ", status=" + status
				+ ", division=" + division + ", location=" + location
				+ ", subDivision=" + subDivision + ", profitCentre="
				+ profitCentre + ", crDrPreGst=" + crDrPreGst + ", portCode="
				+ portCode + ", diffPercent=" + diffPercent
				+ ", userdefinedfield1=" + userdefinedfield1
				+ ", userdefinedfield2=" + userdefinedfield2
				+ ", userdefinedfield3=" + userdefinedfield3
				+ ", userDefinedField4=" + userDefinedField4 + ", userAccess1="
				+ userAccess1 + ", userAccess2=" + userAccess2
				+ ", userAccess3=" + userAccess3 + ", userAccess4="
				+ userAccess4 + ", userAccess5=" + userAccess5
				+ ", userAccess6=" + userAccess6 + ", eWayBillNo=" + eWayBillNo
				+ ", taxPayable=" + taxPayable + ", extractedBatchId="
				+ extractedBatchId + ", extractedOn=" + extractedOn
				+ ", extractedDate=" + extractedDate + ", initiatedOn="
				+ initiatedOn + ", hciReceivedOn=" + hciReceivedOn
				+ ", reqReceivedOn=" + reqReceivedOn + ", beforeSavingOn="
				+ beforeSavingOn + ", dataOriginTypeCode=" + dataOriginTypeCode
				+ ", isSaved=" + isSaved + ", isSent=" + isSent
				+ ", sentToGSTNDate=" + sentToGSTNDate + ", savedToGSTNDate="
				+ savedToGSTNDate + ", isGstnError=" + isGstnError
				+ ", gstnBatchId=" + gstnBatchId + ", gstnErrorCode="
				+ gstnErrorCode + ", gstnErrorDesc=" + gstnErrorDesc
				+ ", isSubmitted=" + isSubmitted + ", submittedDate="
				+ submittedDate + ", isFiled=" + isFiled + ", filedDate="
				+ filedDate + ", eWayBillDate=" + eWayBillDate
				+ ", companyCode=" + companyCode + ", payloadId=" + payloadId
				+ ", derivedSourceId=" + derivedSourceId
				+ ", invoiceOtherCharges=" + invoiceOtherCharges
				+ ", invoiceAssessableAmount=" + invoiceAssessableAmount
				+ ", invoiceIgstAmount=" + invoiceIgstAmount
				+ ", invoiceCgstAmount=" + invoiceCgstAmount
				+ ", invoiceSgstAmount=" + invoiceSgstAmount
				+ ", invoiceCessAdvaloremAmount=" + invoiceCessAdvaloremAmount
				+ ", invoiceCessSpecificAmount=" + invoiceCessSpecificAmount
				+ ", invoiceStateCessAmount=" + invoiceStateCessAmount
				+ ", invStateCessSpecificAmt=" + invStateCessSpecificAmt
				+ ", userDefinedField28=" + userDefinedField28 + ", irn=" + irn
				+ ", dispatcherGstin=" + dispatcherGstin + ", shipToGstin="
				+ shipToGstin + ", paymentDueDate=" + paymentDueDate
				+ ", egstin=" + egstin + ", tcsFlag=" + tcsFlag + ", tdsFlag="
				+ tdsFlag + ", supplierTradeName=" + supplierTradeName
				+ ", itemRowCount=" + itemRowCount + ", entityId=" + entityId
				+ ", serviceOption=" + serviceOption + ", ewbGenerateOption="
				+ ewbGenerateOption + ", groupId=" + groupId
				+ ", formReturnType=" + formReturnType + ", entityAtValMap="
				+ entityAtValMap + ", entityAtConfMap=" + entityAtConfMap
				+ ", entityConfigParamMap=" + entityConfigParamMap + "]";
	}

}
