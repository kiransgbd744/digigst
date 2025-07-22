/**
 * 
 */
package com.ey.advisory.app.data.business.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.EwbLocalDateAdapter;

import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Khalid1.Khan
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class OtrdTransDocSoapHdrRq {
	@XmlTransient
	protected Long id;
	/* this is just added for soap request for ewbs */
	@XmlElement(name = "id-token")
	protected String idToken;
	/* this is just added for soap request for ewbs */

	@XmlElement(name = "ack-date")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate ackDate;
	@XmlElement(name = "is-einvoice")
	private boolean isEinvoice;
	@XmlElement(name = "is-ewb")
	private boolean isEwb;

	@XmlElement(name = "is-compliance")
	private boolean isCompliance;

	@XmlElement(name = "der-sup-type")
	private String derivedSupplyType;

	@XmlElement(name = "einv-status")
	private Integer eInvStatus;
	@XmlElement(name = "einv-err-code")
	private String eInvErrorCode;
	@XmlElement(name = "einv-err-desc")
	private String eInvErrorDesc;

	@XmlElement(name = "ewb-status")
	private Integer ewbStatus;
	@XmlElement(name = "ewb-err-cd")
	private String ewbErrorCode;
	@XmlElement(name = "ewb-err-desc")
	private String ewbErrorDesc;

	@XmlElement(name = "sales-org")
	protected String salesOrgnization;

	@XmlElement(name = "dist-channel")
	protected String distributionChannel;

	@XmlElement(name = "orig-doc-type")
	protected String origDocType;

	@XmlElement(name = "org-cgstin")
	protected String origCgstin;

	@XmlElement(name = "bill-to-state")
	protected String billToState;

	@XmlElement(name = "shp-to-state")
	protected String shipToState;

	@XmlElement(name = "shp-bill-no")
	protected String shippingBillNo;

	@XmlElement(name = "shp-bill-date")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate shippingBillDate;

	@XmlElement(name = "tcs-flag")
	protected String tcsFlag;
	@XmlElement(name = "acc-voucher-no")
	protected String accountingVoucherNumber;
	@XmlElement(name = "acc-vouch-date")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate accountingVoucherDate;
	/**
	 * EGSTIN will be present only for those transactions happening through an
	 * E-commerce supplier.
	 * 
	 */
	protected String egstin;
	/**
	 * Table type represents the table in the GSTR-1 form, where this document
	 * can be placed. E.g. 4A, 4B, 4C etc.
	 */
	@XmlElement(name = "table-type")
	protected String tableType;

	@XmlElement(name = "ud-field1")
	protected String userdefinedfield1;
	@XmlElement(name = "ud-field2")
	protected String userdefinedfield2;
	@XmlElement(name = "ud-field3")
	protected String userdefinedfield3;
	@XmlElement(name = "ud-field4")
	protected String userDefinedField4;

	@XmlElement(name = "udf28")
	protected BigDecimal userDefinedField28;

	@XmlElement(name = "udf29")
	protected String userDefinedField29;

	/**
	 * This field represents the classification of invoices according to return
	 * filing rules by GSTN. Some of the possible values are B2B, B2CL, B2CS,
	 * EXP etc.
	 * 
	 */
	@XmlElement(name = "gstr1-sub-categ")
	protected String gstnBifurcation;
	@XmlElement(name = "an1-tabl-typ")
	protected String tableTypeNew;
	@XmlElement(name = "gstn-bifur-new")
	protected String gstnBifurcationNew;
	@XmlElement(name = "ret-type")
	protected String returnType;
	@XmlElement(name = "gstr-ret-type")
	protected String gstrReturnType;

	@XmlTransient
	protected Long erpBatchId;
	@XmlElement(name = "igst-amt")
	protected BigDecimal igstAmount;
	@XmlElement(name = "cgst-amt")
	protected BigDecimal cgstAmount;
	@XmlElement(name = "sgst-amt")
	protected BigDecimal sgstAmount;
	@XmlElement(name = "cess-amt-spec")
	protected BigDecimal cessAmountSpecific;
	@XmlElement(name = "cess-amt-adv")
	protected BigDecimal cessAmountAdvalorem;

	@XmlElement(name = "memo-val-igst")
	protected BigDecimal memoValIgst;
	@XmlElement(name = "memo-val-cgst")
	protected BigDecimal memoValCgst;
	@XmlElement(name = "memo-val-sgst")
	protected BigDecimal memoValSgst;
	@XmlElement(name = "memo-val-cess")
	protected BigDecimal memoValCess;

	@XmlElement(name = "ret1-igst-imp")
	protected BigDecimal ret1IgstImpact;
	@XmlElement(name = "ret1-cgst-impct")
	protected BigDecimal ret1CgstImpact;
	@XmlElement(name = "ret1-sgst-impct")
	protected BigDecimal ret1SgstImpact;
	@XmlElement(name = "der-sgstin-pan")
	protected String derivedSgstinPan;
	@XmlElement(name = "der-cgstin-pan")
	protected String derivedCgstinPan;

	protected String irn;
	@XmlElement(name = "irn-date")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime irnDate;
	@XmlElement(name = "tax-scheme")
	protected String taxScheme;
	@XmlElement(name = "doc-categ")
	protected String docCategory;

	@XmlElement(name = "supp-trd-name")
	protected String supplierTradeName;
	@XmlElement(name = "supp-legal-nm")
	protected String supplierLegalName;
	@XmlElement(name = "supp-bldng-no")
	protected String supplierBuildingNumber;
	@XmlElement(name = "supp-bldng-name")
	protected String supplierBuildingName;
	@XmlElement(name = "supp-location")
	protected String supplierLocation;
	@XmlElement(name = "supp-pin-cd")
	protected Integer supplierPincode;
	@XmlElement(name = "supp-state-cd")
	protected String supplierStateCode;
	@XmlElement(name = "supp-phn")
	protected String supplierPhone;
	@XmlElement(name = "supp-email")
	protected String supplierEmail;
	@XmlElement(name = "cust-trd-name")
	protected String customerTradeName;
	@XmlElement(name = "cust-pin-cd")
	protected Integer customerPincode;
	@XmlElement(name = "cust-phn")
	protected String customerPhone;
	@XmlElement(name = "cust-eml")
	protected String customerEmail;
	@XmlElement(name = "disp-gstin")
	protected String dispatcherGstin;
	@XmlElement(name = "disp-trd-nm")
	protected String dispatcherTradeName;
	@XmlElement(name = "disp-bldng-no")
	protected String dispatcherBuildingNumber;
	@XmlElement(name = "disp-bldng-nm")
	protected String dispatcherBuildingName;
	@XmlElement(name = "disp-loctn")
	protected String dispatcherLocation;
	@XmlElement(name = "disp-pin-cd")
	protected Integer dispatcherPincode;
	@XmlElement(name = "disp-state-cd")
	protected String dispatcherStateCode;
	@XmlElement(name = "shp-to-gstin")
	protected String shipToGstin;
	@XmlElement(name = "shp-to-trd-nm")
	protected String shipToTradeName;
	@XmlElement(name = "shp-to-lgl-nm")
	protected String shipToLegalName;
	@XmlElement(name = "shp-to-bl-no")
	protected String shipToBuildingNumber;
	@XmlElement(name = "shp-to-bl-nm")
	protected String shipToBuildingName;
	@XmlElement(name = "shp-to-loctn")
	protected String shipToLocation;
	@XmlElement(name = "shp-to-pin-cd")
	protected Integer shipToPincode;
	@XmlElement(name = "inv-oth-chrgs")
	protected BigDecimal invoiceOtherCharges;
	@XmlElement(name = "inv-assess-amt")
	protected BigDecimal invoiceAssessableAmount;
	@XmlElement(name = "inv-igst-amt")
	protected BigDecimal invoiceIgstAmount;
	@XmlElement(name = "inv-cgst-amt")
	protected BigDecimal invoiceCgstAmount;
	@XmlElement(name = "inv-sgst-amt")
	protected BigDecimal invoiceSgstAmount;
	@XmlElement(name = "inv-cess-adv-amt")
	protected BigDecimal invoiceCessAdvaloremAmount;
	@XmlElement(name = "inv-ces-spec-at")
	protected BigDecimal invoiceCessSpecificAmount;

	@XmlElement(name = "inv-st-cess-amt")
	protected BigDecimal invoiceStateCessAmount;
	@XmlElement(name = "rnd-off")
	protected BigDecimal roundOff;
	@XmlElement(name = "tot-inv-val-wrds")
	protected String totalInvoiceValueInWords;
	@XmlElement(name = "forgn-curr")
	protected String foreignCurrency;
	@XmlElement(name = "cntry-cd")
	protected String countryCode;
	@XmlElement(name = "inv-val-fc")
	protected BigDecimal invoiceValueFc;
	@XmlElement(name = "inv-pd-strt-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate invoicePeriodStartDate;
	@XmlElement(name = "inv-pd-end-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate invoicePeriodEndDate;
	@XmlElement(name = "payee-nm")
	protected String payeeName;
	@XmlElement(name = "md-of-pmt")
	protected String modeOfPayment;
	@XmlElement(name = "brnch-ifsc-cd")
	protected String branchOrIfscCode;
	@XmlElement(name = "pmt-terms")
	protected String paymentTerms;
	@XmlElement(name = "pmt-inst")
	protected String paymentInstruction;
	@XmlElement(name = "credit-trnsfr")
	protected String creditTransfer;
	@XmlElement(name = "db-direct")
	protected String directDebit;
	@XmlElement(name = "credit-days")
	protected Integer creditDays;
	@XmlElement(name = "pmt-due-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate paymentDueDate;
	@XmlElement(name = "acct-dtls")
	protected String accountDetail;
	@XmlElement(name = "tds-flg")
	protected String tdsFlag;
	@XmlElement(name = "transctn-type")
	protected String transactionType;
	@XmlElement(name = "sub-supp-typ")
	protected String subSupplyType;
	@XmlElement(name = "oth-sup-typ-des")
	protected String otherSupplyTypeDescription;
	@XmlElement(name = "trnsprt-id")
	protected String transporterID;
	@XmlElement(name = "trnsprt-name")
	protected String transporterName;
	@XmlElement(name = "trnsprt-mode")
	protected String transportMode;
	@XmlElement(name = "trnsprt-doc-no")
	protected String transportDocNo;
	@XmlElement(name = "trnsprt-doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate transportDocDate;
	@XmlElement(name = "dist")
	protected Integer distance;
	@XmlElement(name = "veh-no")
	protected String vehicleNo;
	@XmlElement(name = "veh-typ")
	protected String vehicleType;
	@XmlElement(name = "exchng-rt")
	protected String exchangeRate;
	@XmlElement(name = "cmpny-cd")
	protected String companyCode;
	@XmlElement(name = "gl-pstng-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate glPostingDate;
	@XmlElement(name = "sales-ordr-no")
	protected String salesOrderNumber;
	@XmlElement(name = "cust-tan")
	protected String customerTan;
	@XmlElement(name = "can-rsn")
	protected String cancellationReason;
	@XmlElement(name = "can-rmrk")
	protected String cancellationRemarks;
	@XmlElement(name = "inv-st-cs-sp-amt")
	protected BigDecimal invStateCessSpecificAmt;
	@XmlElement(name = "tcs-flg-incm-tx")
	protected String tcsFlagIncomeTax;
	@XmlElement(name = "cust-pan-adhr")
	protected String customerPANOrAadhaar;
	@XmlElement(name = "gl-st-cess-spec")
	protected String glStateCessSpecific;
	@XmlElement(name = "orig-inv-num")
	private String originalInvoiceNumber;
	@XmlElement(name = "orig-inv-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate originalInvoiceDate;

	@XmlElement(name = "gl-cd-igst")
	protected String glCodeIgst;
	@XmlElement(name = "gl-cd-cgst")
	protected String glCodeCgst;
	@XmlElement(name = "gl-cd-sgst")
	protected String glCodeSgst;
	@XmlElement(name = "gl-cd-adv-cess")
	protected String glCodeAdvCess;
	@XmlElement(name = "gl-cd-sp-cess")
	protected String glCodeSpCess;
	@XmlElement(name = "gl-cd-st-cess")
	protected String glCodeStateCess;
	@XmlElement(name = "prft-cntr2")
	protected String profitCentre2;
	@XmlElement(name = "inv-rmrks")
	private String invoiceRemarks;
	@XmlElement(name = "ecm-trns-id")
	protected String ecomTransactionID;
	/**
	 * formReturnType - This field is to set GSTR1 or ANX1-1 to invoke business
	 * rules in business rules chain. value to this field is set based on tax
	 * period
	 */
	@Transient
	@XmlElement(name = "frm-ret-type")
	private String formReturnType;
	@Transient
	@XmlElement(name = "is-cgst-mst-cst")
	protected boolean isCgstInMasterCust;

	@XmlElement(name = "doc-no")
	protected String docNo;
	@XmlElement(name = "doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate docDate;
	@XmlElement(name = "doc-type")
	protected String docType;
	/**
	 * The financial year in the format 1819, 1920, 2021 etc. This can be used
	 * for validating the invoices for duplicates. According to government
	 * rules, two document numbers belonging to the same type cannot be the
	 * same, within the same financial year.
	 */
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
	@XmlElement(name = "plnt-code")
	protected String plantCode;
	/**
	 * SGSTIN is mandatory for outward documents, but can be null for inward
	 * documents.
	 */
	@XmlElement(name = "sgstin")
	protected String sgstin;
	/**
	 * CGSTIN is mandatory for inward documents, but can be null for outward
	 * documents.
	 */
	protected String cgstin;
	@XmlElement(name = "supp-type")
	protected String supplyType;
	@XmlElement(name = "rev-chrg")
	protected String reverseCharge;
	protected String pos; // State code
	/**
	 * For Outward invoices, it's called customer code and for Inward Invoices
	 * it's called supplier code.
	 */
	@XmlElement(name = "cst-sup-cd")
	protected String custOrSuppCode;
	@XmlElement(name = "cust-supp-typ")
	protected String custOrSuppType;
	@XmlElement(name = "sec70-igst-flg")
	protected String section7OfIgstFlag;
	/**
	 * For Outward invoices, it's called customer name and for Inward Invoices
	 * it's called supplier name.
	 */
	@XmlElement(name = "cust-supp-name")
	protected String custOrSuppName;
	@XmlElement(name = "cust-sup-add1")
	protected String custOrSuppAddress1;
	@XmlElement(name = "cust-supp-add2")
	protected String custOrSuppAddress2;
	@XmlElement(name = "cust-supp-add3")
	protected String custOrSuppAddress3;
	@XmlElement(name = "cust-supp-add4")
	protected String custOrSuppAddress4;
	@XmlElement(name = "clm-refund-flg")
	protected String claimRefundFlag;
	@XmlElement(name = "state-app-cess")
	protected String stateApplyingCess;
	@XmlElement(name = "auto-pop-ref-flg")
	protected String autoPopToRefundFlag;
	@XmlElement(name = "user-id")
	protected String userId;
	@XmlElement(name = "src-file-nm")
	protected String sourceFileName;
	@XmlElement(name = "src-idntfr")
	protected String sourceIdentifier;

	@XmlElement(name = "hdr-src-idntfr")
	protected String hdrSrcIdentifier;

	/* Amendment details */
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	@XmlElement(name = "is-err")
	private boolean isError;
	/**
	 * Sets if the document has information messages
	 */
	@XmlElement(name = "is-info")
	protected boolean isInfo;
	@XmlElement(name = "doc-amt")
	protected BigDecimal docAmount; // Total Invoice/Document
									// Amount
	@Transient
	@XmlElement(name = "oth-val")
	protected BigDecimal otherValues;
	@Transient
	@XmlElement(name = "st-cess-amt")
	protected BigDecimal stateCessAmount;
	@XmlElement(name = "tx-val")
	protected BigDecimal taxableValue; // Total Taxable Value.
	@XmlElement(name = "is-procss")
	private boolean isProcessed;
	@Transient
	protected String status;
	/* Org Hierarchy Details */
	protected String division;
	protected String location;
	@XmlElement(name = "sub-division")
	protected String subDivision;
	@XmlElement(name = "prft-centr1")
	protected String profitCentre;
	@XmlElement(name = "crdr-pre-gst")
	protected String crDrPreGst;
	@XmlElement(name = "port-cd")
	protected String portCode;
	/**
	 * diffPercent specifies flag and accepts either 'L65' or 'N' if it is
	 * blank, it is considered as 'N'
	 */
	@XmlElement(name = "diff-prcnt")
	protected String diffPercent;
	@XmlElement(name = "profit-cntr3")
	protected String userAccess1;
	@XmlElement(name = "profit-cntr4")
	protected String userAccess2;
	@XmlElement(name = "profit-cntr5")
	protected String userAccess3;
	@XmlElement(name = "profit-cntr6")
	protected String userAccess4;
	@XmlElement(name = "profit-cntr7")
	protected String userAccess5;
	@XmlElement(name = "profit-cntr8")
	protected String userAccess6;
	@XmlElement(name = "ewb-no")
	protected String eWayBillNo;
	@XmlElement(name = "ewb-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate eWayBillDate;
	@XmlElement(name = "tx-paybl")
	protected BigDecimal taxPayable;
	@XmlTransient
	protected Long extractedBatchId; // ERP Extracted Batch Id
	@Transient
	@XmlElement(name = "extr-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime extractedOn;// ERP Extracted Date
	@XmlElement(name = "extr-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate extractedDate;// ERP Extracted Date - Derived
	@XmlElement(name = "init-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime initiatedOn;
	@XmlElement(name = "hci-rec-dt")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime hciReceivedOn;
	@XmlElement(name = "req-rec-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime reqReceivedOn;
	@XmlElement(name = "bfr-sav-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime beforeSavingOn;
	@XmlElement(name = "data-org-typ-cd")
	protected String dataOriginTypeCode;
	@XmlElement(name = "is-saved")
	protected boolean isSaved;
	@XmlElement(name = "is-sent")
	protected boolean isSent;
	@XmlElement(name = "sent-to-gstn-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate sentToGSTNDate;
	@XmlElement(name = "sav-gstn-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate savedToGSTNDate;
	@XmlElement(name = "is-gstn-err")
	protected boolean isGstnError;
	@XmlElement(name = "is-submitted")
	protected boolean isSubmitted;
	@XmlTransient
	protected Long gstnBatchId;
	@Transient
	@XmlTransient
	private Long entityId;
	@Transient
	@XmlTransient
	private Long groupId;

	/**
	 * An acceptance Id is required when a batch of documents are submitted
	 * together for processing. The batch can arrive as part of a file OR a REST
	 * API call etc. An acceptance Id is issued when we accept the batch and
	 * dump it for later processing. The user can come back with the acceptance
	 * id later to find out the procesing status of the documents included
	 * inabstract the batch.
	 */
	@XmlTransient
	protected Long acceptanceId;
	/**
	 * The tax period for which the document has to be filed. This period can be
	 * different from the actual period on which the transaction for this
	 * document took place.
	 */
	@XmlElement(name = "tx-period")
	protected String taxperiod;
	/**
	 * The created Date of this document
	 */
	@XmlElement(name = "created-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime createdDate;
	/**
	 * The updated Date of this document.
	 */
	@XmlElement(name = "modfid-on")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime updatedDate;
	@XmlElement(name = "cretd-by")
	protected String createdBy;
	@XmlElement(name = "modfied-by")
	protected String modifiedBy;
	/**
	 * This is the date part of the createdDate. This is required because there
	 * are use cases where we need to search between two dates, without
	 * considering the time. So, either we need to have a 'Date only column' on
	 * we need to apply a function on the created date at the DB level to
	 * extract the date part. In the latter scenario, certain databases do not
	 * support function based indexes, which can lead to a full table scan at
	 * the DB level. Hence, adding a received date column. The value of this
	 * field should be set at the time of creation of this object and it should
	 * be exactly same as the date part of the creationDate.
	 */
	@XmlElement(name = "rec-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate receivedDate;
	@XmlElement(name = "dervd-tx-prd")
	protected Integer derivedTaxperiod;
	/**
	 * Logical delete flag for the entity.
	 */
	@XmlElement(name = "is-delete")
	protected boolean isDeleted;

	/**
	 * Initialize an empty array list to hold the line items. The ITEM_INDEX
	 * column will make sure that the order of the line items are preserved in
	 * the database. This is the JPA way of indexing one to many collections.
	 */
	@XmlElementWrapper(name = "lineItems")
	@XmlElement(name = "item")
	protected List<OtrdTrnsDocLnItemsReq> lineItems = new ArrayList<>();

	@XmlElementWrapper(name = "prec-doc-dtls")
	@XmlElement(name = "prec-doc-dtl")
	private List<PreDocDetails> preDocDtls = new ArrayList<>();

	@XmlElementWrapper(name = "add-doc-dtls")
	@XmlElement(name = "add-doc-dtl")
	protected List<AdditionalDocDetails> addlDocDtls = new ArrayList<>();

	@XmlElementWrapper(name = "contract-dtls")
	@XmlElement(name = "contract-dtl")
	protected List<ContractDetails> contrDtls = new ArrayList<>();

}