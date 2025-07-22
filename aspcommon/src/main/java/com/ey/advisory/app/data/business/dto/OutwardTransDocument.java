package com.ey.advisory.app.data.business.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.EwbLocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * An Outward Document represents a concrete Financial Document like an Invoice
 * or a credit note.
 * 
 * @author Arun.KA
 *
 */

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OutwardTransDocument // extends TransDocument {
{

	@Expose
	@SerializedName("id")
	protected Long id;

	/* this is just added for soap request for ewbs */

	@XmlElement(name = "id-token")
	protected String idToken;

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	/* this is just added for soap request for ewbs */
	@Expose
	@XmlElement(name = "ack-date")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate ackDate;

	@Expose
	@SerializedName("isEinvoice")
	@XmlElement(name = "is-einvoice")
	private boolean isEinvoice;

	@Expose
	@SerializedName("isEwb")
	@XmlElement(name = "is-ewb")
	private boolean isEwb;

	@Expose
	@SerializedName("isCompliance")
	@XmlElement(name = "is-compliance")
	private boolean isCompliance;

	@Expose
	@SerializedName("derivedSupplyType")
	@XmlElement(name = "der-sup-type")
	private String derivedSupplyType;

	@Expose
	@SerializedName("eInvStatus")
	@XmlElement(name = "einv-status")
	private Integer eInvStatus;

	@Expose
	@SerializedName("eInvErrorCode")
	@XmlElement(name = "einv-err-code")
	private String eInvErrorCode;

	@Expose
	@SerializedName("eInvErrorDesc")
	@XmlElement(name = "einv-err-desc")
	private String eInvErrorDesc;

	@Expose
	@SerializedName("ewbStatus")
	@XmlElement(name = "ewb-status")
	private Integer ewbStatus;

	@Expose
	@SerializedName("ewbErrorCode")
	@XmlElement(name = "ewb-err-cd")
	private String ewbErrorCode;

	@Expose
	@SerializedName("ewbErrorDesc")
	@XmlElement(name = "ewb-err-desc")
	private String ewbErrorDesc;

	@Expose
	@SerializedName("salesOrg")
	@XmlElement(name = "sales-org")
	protected String salesOrgnization;

	@Expose
	@SerializedName("distChannel")
	@XmlElement(name = "dist-channel")
	protected String distributionChannel;

	@Expose
	@SerializedName("orgDocType")
	@XmlElement(name = "orig-doc-type")
	protected String origDocType;

	@Expose
	@SerializedName("orgCgstin")
	@XmlElement(name = "org-cgstin")
	protected String origCgstin;

	@Expose
	@SerializedName("billToState")
	@XmlElement(name = "bill-to-state")
	protected String billToState;

	@Expose
	@SerializedName("shipToState")
	@XmlElement(name = "shp-to-state")
	protected String shipToState;

	@Expose
	@SerializedName("shippingBillNo")
	@XmlElement(name = "shp-bill-no")
	protected String shippingBillNo;

	@Expose
	@SerializedName("shippingBillDate")
	@XmlElement(name = "shp-bill-date")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate shippingBillDate;

	@Expose
	@SerializedName("tcsFlag")
	@XmlElement(name = "tcs-flag")
	protected String tcsFlag;

	@Expose
	@SerializedName("accVoucherNo")
	@XmlElement(name = "acc-voucher-no")
	protected String accountingVoucherNumber;

	@Expose
	@SerializedName("accVoucherDate")
	@XmlElement(name = "acc-vouch-date")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate accountingVoucherDate;

	/**
	 * EGSTIN will be present only for those transactions happening through an
	 * E-commerce supplier.
	 * 
	 */
	@Expose
	@SerializedName("ecomGSTIN")
	protected String egstin;

	/**
	 * Table type represents the table in the GSTR-1 form, where this document
	 * can be placed. E.g. 4A, 4B, 4C etc.
	 */
	@Expose
	@SerializedName("gstr1TableNo")
	@XmlElement(name = "table-type")
	protected String tableType;

	/**
	 * This field represents the classification of invoices according to return
	 * filing rules by GSTN. Some of the possible values are B2B, B2CL, B2CS,
	 * EXP etc.
	 * 
	 */
	@Expose
	@SerializedName("gstr1SubCategory")
	@XmlElement(name = "gstr1-sub-categ")
	protected String gstnBifurcation;

	@Expose
	@SerializedName("an1Gstr1TableNo")
	@XmlElement(name = "an1-tabl-typ")
	protected String tableTypeNew;

	@Expose
	@SerializedName("an1Gstr1SubCategory")
	@XmlElement(name = "gstn-bifur-new")
	protected String gstnBifurcationNew;

	@Expose
	@SerializedName("returnType")
	@XmlElement(name = "ret-type")
	protected String returnType;

	@Expose
	@SerializedName("gstrReturnType")
	@XmlElement(name = "gstr-ret-type")
	protected String gstrReturnType;

	@Expose
	@SerializedName("erpBatchId")
	@XmlElement(name = "erp-batch-id")
	protected Long erpBatchId;

	@Expose
	@SerializedName("igstAmt")
	@XmlElement(name = "igst-amt")
	protected BigDecimal igstAmount;

	@Expose
	@SerializedName("cgstAmt")
	@XmlElement(name = "cgst-amt")
	protected BigDecimal cgstAmount;

	@Expose
	@SerializedName("sgstAmt")
	@XmlElement(name = "sgst-amt")
	protected BigDecimal sgstAmount;

	@Expose
	@SerializedName("cessAmtSpecfic")
	@XmlElement(name = "cess-amt-spec")
	protected BigDecimal cessAmountSpecific;

	@Expose
	@SerializedName("cessAmtAdvalorem")
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

	@Expose
	@SerializedName("irn")
	protected String irn;

	@Expose
	@SerializedName("irnDate")
	@XmlElement(name = "irn-date")
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	protected LocalDateTime irnDate;

	@Expose
	@SerializedName("taxScheme")
	@XmlElement(name = "tax-scheme")
	protected String taxScheme;

	@Expose
	@SerializedName("docCat")
	@XmlElement(name = "doc-categ")
	protected String docCategory;

	@Expose
	@SerializedName("supTradeName")
	@XmlElement(name = "supp-trd-name")
	protected String supplierTradeName;

	@Expose
	@SerializedName("supLegalName")
	@XmlElement(name = "supp-legal-nm")
	protected String supplierLegalName;

	@Expose
	@SerializedName("supBuildingNo")
	@XmlElement(name = "supp-bldng-no")
	protected String supplierBuildingNumber;

	@Expose
	@SerializedName("supBuildingName")
	@XmlElement(name = "supp-bldng-name")
	protected String supplierBuildingName;

	@Expose
	@SerializedName("supLocation")
	@XmlElement(name = "supp-location")
	protected String supplierLocation;

	@Expose
	@SerializedName("supPincode")
	@XmlElement(name = "supp-pin-cd")
	protected Integer supplierPincode;

	@Expose
	@SerializedName("supStateCode")
	@XmlElement(name = "supp-state-cd")
	protected String supplierStateCode;

	@Expose
	@SerializedName(value = "supPhone", alternate = { "suppPhone" })
	@XmlElement(name = "supp-phn")
	protected String supplierPhone;

	@Expose
	@SerializedName(value = "supEmail", alternate = { "suppEmail" })
	@XmlElement(name = "supp-email")
	protected String supplierEmail;

	@Expose
	@SerializedName("custTradeName")
	@XmlElement(name = "cust-trd-name")
	protected String customerTradeName;

	@Expose
	@SerializedName("custPincode")
	@XmlElement(name = "cust-pin-cd")
	protected Integer customerPincode;

	@Expose
	@SerializedName("custPhone")
	@XmlElement(name = "cust-phn")
	protected String customerPhone;

	@Expose
	@SerializedName("custEmail")
	@XmlElement(name = "cust-eml")
	protected String customerEmail;

	@Expose
	@SerializedName("dispatcherGstin")
	@XmlElement(name = "disp-gstin")
	protected String dispatcherGstin;

	@Expose
	@SerializedName("dispatcherTradeName")
	@XmlElement(name = "disp-trd-nm")
	protected String dispatcherTradeName;

	@Expose
	@SerializedName("dispatcherBuildingNo")
	@XmlElement(name = "disp-bldng-no")
	protected String dispatcherBuildingNumber;

	@Expose
	@SerializedName("dispatcherBuildingName")
	@XmlElement(name = "disp-bldng-nm")
	protected String dispatcherBuildingName;

	@Expose
	@SerializedName("dispatcherLocation")
	@XmlElement(name = "disp-loctn")
	protected String dispatcherLocation;

	@Expose
	@SerializedName("dispatcherPincode")
	@XmlElement(name = "disp-pin-cd")
	protected Integer dispatcherPincode;

	@Expose
	@SerializedName("dispatcherStateCode")
	@XmlElement(name = "disp-state-cd")
	protected String dispatcherStateCode;

	@Expose
	@SerializedName("shipToGstin")
	@XmlElement(name = "shp-to-gstin")
	protected String shipToGstin;

	@Expose
	@SerializedName("shipToTradeName")
	@XmlElement(name = "shp-to-trd-nm")
	protected String shipToTradeName;

	@Expose
	@SerializedName("shipToLegalName")
	@XmlElement(name = "shp-to-lgl-nm")
	protected String shipToLegalName;

	@Expose
	@SerializedName("shipToBuildingNo")
	@XmlElement(name = "shp-to-bl-no")
	protected String shipToBuildingNumber;

	@Expose
	@SerializedName("shipToBuildingName")
	@XmlElement(name = "shp-to-bl-nm")
	protected String shipToBuildingName;

	@Expose
	@SerializedName("shipToLocation")
	@XmlElement(name = "shp-to-loctn")
	protected String shipToLocation;

	@Expose
	@SerializedName("shipToPincode")
	@XmlElement(name = "shp-to-pin-cd")
	protected Integer shipToPincode;

	@Expose
	@SerializedName("invOtherCharges")
	@XmlElement(name = "inv-oth-chrgs")
	protected BigDecimal invoiceOtherCharges;

	@Expose
	@SerializedName("invAssessableAmt")
	@XmlElement(name = "inv-assess-amt")
	protected BigDecimal invoiceAssessableAmount;

	@Expose
	@SerializedName("invIgstAmt")
	@XmlElement(name = "inv-igst-amt")
	protected BigDecimal invoiceIgstAmount;

	@Expose
	@SerializedName("invCgstAmt")
	@XmlElement(name = "inv-cgst-amt")
	protected BigDecimal invoiceCgstAmount;

	@Expose
	@SerializedName("invSgstAmt")
	@XmlElement(name = "inv-sgst-amt")
	protected BigDecimal invoiceSgstAmount;

	@Expose
	@SerializedName("invCessAdvaloremAmt")
	@XmlElement(name = "inv-cess-adv-amt")
	protected BigDecimal invoiceCessAdvaloremAmount;

	@Expose
	@SerializedName("invCessSpecificAmt")
	@XmlElement(name = "inv-cess-spec-amt")
	protected BigDecimal invoiceCessSpecificAmount;

	@Expose
	@SerializedName("invStateCessAmt")
	@XmlElement(name = "inv-state-cess-amt")
	protected BigDecimal invoiceStateCessAmount;

	@Expose
	@SerializedName("roundOff")
	@XmlElement(name = "rnd-off")
	protected BigDecimal roundOff;

	@Expose
	@SerializedName("totalInvValueInWords")
	@XmlElement(name = "tot-inv-val-wrds")
	protected String totalInvoiceValueInWords;

	@Expose
	@SerializedName("foreignCurrency")
	@XmlElement(name = "forgn-curr")
	protected String foreignCurrency;

	@Expose
	@SerializedName("countryCode")
	@XmlElement(name = "cntry-cd")
	protected String countryCode;

	@Expose
	@SerializedName("invValueFc")
	@XmlElement(name = "inv-val-fc")
	protected BigDecimal invoiceValueFc;

	@Expose
	@SerializedName("invPeriodStartDate")
	@XmlElement(name = "inv-pd-strt-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate invoicePeriodStartDate;

	@Expose
	@SerializedName("invPeriodEndDate")
	@XmlElement(name = "inv-pd-end-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate invoicePeriodEndDate;

	@Expose
	@SerializedName("payeeName")
	@XmlElement(name = "payee-nm")
	protected String payeeName;

	@Expose
	@SerializedName("modeOfPayment")
	@XmlElement(name = "md-of-pmt")
	protected String modeOfPayment;

	@Expose
	@SerializedName("branchOrIfscCode")
	@XmlElement(name = "brnch-ifsc-cd")
	protected String branchOrIfscCode;

	@Expose
	@SerializedName("paymentTerms")
	@XmlElement(name = "pmt-terms")
	protected String paymentTerms;

	@Expose
	@SerializedName("paymentInstruction")
	@XmlElement(name = "pmt-inst")
	protected String paymentInstruction;

	@Expose
	@SerializedName("creditTransfer")
	@XmlElement(name = "credit-trnsfr")
	protected String creditTransfer;

	@Expose
	@SerializedName("directDebit")
	@XmlElement(name = "db-direct")
	protected String directDebit;

	@Expose
	@SerializedName("creditDays")
	@XmlElement(name = "credit-days")
	protected Integer creditDays;

	@Expose
	@SerializedName("paymentDueDate")
	@XmlElement(name = "pmt-due-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate paymentDueDate;

	@Expose
	@SerializedName("accDetail")
	@XmlElement(name = "acct-dtls")
	protected String accountDetail;

	@Expose
	@SerializedName("tdsFlag")
	@XmlElement(name = "tds-flg")
	protected String tdsFlag;

	@Expose
	@SerializedName("tranType")
	@XmlElement(name = "transctn-type")
	protected String transactionType;

	@Expose
	@SerializedName("subsupplyType")
	@XmlElement(name = "sub-supp-typ")
	protected String subSupplyType;

	@Expose
	@SerializedName("otherSupplyTypeDesc")
	@XmlElement(name = "oth-supp-type-desc")
	protected String otherSupplyTypeDescription;

	@Expose
	@SerializedName("transporterID")
	@XmlElement(name = "trnsprt-id")
	protected String transporterID;

	@Expose
	@SerializedName("transporterName")
	@XmlElement(name = "trnsprt-name")
	protected String transporterName;

	@Expose
	@SerializedName("transportMode")
	@XmlElement(name = "trnsprt-mode")
	protected String transportMode;

	@Expose
	@SerializedName("transportDocNo")
	@XmlElement(name = "trnsprt-doc-no")
	protected String transportDocNo;

	@Expose
	@SerializedName("transportDocDate")
	@XmlElement(name = "trnsprt-doc-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate transportDocDate;

	@Expose
	@SerializedName("distance")
	@XmlElement(name = "dist")
	protected Integer distance;

	@Expose
	@SerializedName("vehicleNo")
	@XmlElement(name = "veh-no")
	protected String vehicleNo;

	@Expose
	@SerializedName("vehicleType")
	@XmlElement(name = "veh-typ")
	protected String vehicleType;

	@Expose
	@SerializedName("exchangeRt")
	@XmlElement(name = "exchng-rt")
	protected String exchangeRate;

	@Expose
	@SerializedName("companyCode")
	@XmlElement(name = "cmpny-cd")
	protected String companyCode;

	@Expose
	@SerializedName("glPostingDate")
	@XmlElement(name = "gl-pstng-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	protected LocalDate glPostingDate;

	@Expose
	@SerializedName("salesOrderNo")
	@XmlElement(name = "sales-ordr-no")
	protected String salesOrderNumber;

	@Expose
	@SerializedName("custTan")
	@XmlElement(name = "cust-tan")
	protected String customerTan;

	@Expose
	@SerializedName("canReason")
	@XmlElement(name = "can-rsn")
	protected String cancellationReason;

	@Expose
	@SerializedName("canRemarks")
	@XmlElement(name = "can-rmrk")
	protected String cancellationRemarks;

	@Expose
	@SerializedName("invStateCessSpecificAmt")
	@XmlElement(name = "inv-st-cs-sp-amt")
	protected BigDecimal invStateCessSpecificAmt;

	@Expose
	@SerializedName("tcsFlagIncomeTax")
	@XmlElement(name = "tcs-flg-incm-tx")
	protected String tcsFlagIncomeTax;

	@Expose
	@SerializedName("custPANOrAadhaar")
	@XmlElement(name = "cust-pan-adhr")
	protected String customerPANOrAadhaar;

	@Expose
	@SerializedName("glStateCessSpecific")
	@XmlElement(name = "gl-st-cess-spec")
	protected String glStateCessSpecific;

	@Expose
	@SerializedName("originalInvoiceNumber")
	@XmlElement(name = "orig-inv-num")
	private String originalInvoiceNumber;

	@Expose
	@SerializedName("originalInvoiceDate")
	@XmlElement(name = "orig-inv-dt")
	@XmlJavaTypeAdapter(value = EwbLocalDateAdapter.class)
	private LocalDate originalInvoiceDate;

	@Expose
	@SerializedName("preDocDtls")
	@XmlElement(name = "prec-doc-dtls")
	private List<PreDocDetails> preDocDtls;

	@Expose
	@SerializedName("glCodeIgst")
	@XmlElement(name = "gl-cd-igst")
	protected String glCodeIgst;

	@Expose
	@SerializedName("glCodeCgst")
	@XmlElement(name = "gl-cd-cgst")
	protected String glCodeCgst;

	@Expose
	@SerializedName("glCodeSgst")
	@XmlElement(name = "gl-cd-sgst")
	protected String glCodeSgst;

	@Expose
	@SerializedName("glCodeAdvCess")
	@XmlElement(name = "gl-cd-adv-cess")
	protected String glCodeAdvCess;

	@Expose
	@SerializedName("glCodeSpCess")
	@XmlElement(name = "gl-cd-sp-cess")
	protected String glCodeSpCess;

	@Expose
	@SerializedName("glCodeStateCess")
	@XmlElement(name = "gl-cd-st-cess")
	protected String glCodeStateCess;

	@Expose
	@SerializedName("profitCentre2")
	@XmlElement(name = "prft-cntr2")
	protected String profitCentre2;

	@Expose
	@SerializedName("invRemarks")
	@XmlElement(name = "inv-rmrks")
	private String invoiceRemarks;

	@Expose
	@SerializedName("ecomTransactionId")
	@XmlElement(name = "ecm-trns-id")
	protected String ecomTransactionID;

	@Expose
	@SerializedName("contrDtls")
	@XmlElement(name = "cont-dtls")
	protected List<ContractDetails> contrDtls;

	@Expose
	@SerializedName("addlDocDtls")
	@XmlElement(name = "add-doc-dtls")
	protected List<AdditionalDocDetails> addlDocDtls;

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
	@SerializedName(value = "profitCentre1", alternate = { "profitCenter1" })
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
	@XmlElement(name = "diff-prcnt")
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
	@SerializedName("udf28")
	@XmlElement(name = "udf28")
	protected BigDecimal userDefinedField28;

	@Expose
	@SerializedName("udf29")
	@XmlElement(name = "udf29")
	protected String userDefinedField29;

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

	@Expose
	@SerializedName("fileId")
	@XmlElement(name = "file-id")
	protected Long acceptanceId;

	/**
	 * The tax period for which the document has to be filed. This period can be
	 * different from the actual period on which the transaction for this
	 * document took place.
	 */

	@Expose
	@SerializedName("returnPeriod")
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

	@Expose
	@SerializedName("derivedTaxperiod")
	@XmlElement(name = "dervd-tx-prd")
	protected Integer derivedTaxperiod;

	/**
	 * Logical delete flag for the entity.
	 */
	@Expose
	@SerializedName("isDeleted")
	@XmlElement(name = "is-delete")
	protected boolean isDeleted;

	/**
	 * Initialize an empty array list to hold the line items. The ITEM_INDEX
	 * column will make sure that the order of the line items are preserved in
	 * the database. This is the JPA way of indexing one to many collections.
	 */
	@Expose
	@SerializedName("lineItems")
	@XmlElementWrapper(name = "lineItems")
	@XmlElement(name = "items")
	protected List<OutwardTransDocLineItem> lineItems = new ArrayList<>();

	public LocalDate getAckDate() {
		return ackDate;
	}

	public void setAckDate(LocalDate ackDate) {
		this.ackDate = (createdDate != null) ? createdDate.toLocalDate() : null;
	}

	public void addPreDocDtls(List<PreDocDetails> preDocList) {

		List<PreDocDetails> filterList = preDocList.stream()
				.filter(o -> !PreDocDetails.isEmpty(o))
				.collect(Collectors.toList());
		if (!filterList.isEmpty()) {
			preDocDtls = new ArrayList<>();
			preDocDtls.addAll(filterList);
		}
	}

	public void addCntrDtls(List<ContractDetails> contDtlsList) {
		List<ContractDetails> filterList = contDtlsList.stream()
				.filter(o -> !ContractDetails.isEmpty(o))
				.collect(Collectors.toList());
		if (!filterList.isEmpty()) {
			contrDtls = new ArrayList<>();
			contrDtls.addAll(filterList);
		}
	}

	public void addAddlDocDtls(List<AdditionalDocDetails> addlDocDtlList) {
		List<AdditionalDocDetails> filterList = addlDocDtlList.stream()
				.filter(o -> !AdditionalDocDetails.isEmpty(o))
				.collect(Collectors.toList());
		if (!filterList.isEmpty()) {
			addlDocDtls = new ArrayList<>();
			addlDocDtls.addAll(filterList);
		}
	}

}
