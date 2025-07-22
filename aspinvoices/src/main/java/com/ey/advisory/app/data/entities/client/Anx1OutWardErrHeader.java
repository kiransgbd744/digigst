package com.ey.advisory.app.data.entities.client;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "ANX_OUTWARD_ERR_HEADER")
@Setter
@Getter
@ToString
@Slf4j
public class Anx1OutWardErrHeader extends TransErrorDocument {

	@Expose
	@SerializedName("svErrDocId")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "ANX_OUTWARD_ERR_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("salesOrg")
	@Column(name = "SALES_ORGANIZATION")
	protected String salesOrgnization;

	@Expose
	@SerializedName("errCodes")
	@Column(name = "ERROR_CODES")
	private String errCodes;

	@Expose
	@SerializedName("distChannel")
	@Column(name = "DISTRIBUTION_CHANNEL")
	protected String distributionChannel;

	@Expose
	@SerializedName("orgCgstin")
	@Column(name = "ORIGINAL_CUST_GSTIN")
	protected String origCgstin;

	@Expose
	@SerializedName("billToState")
	@Column(name = "BILL_TO_STATE")
	protected String billToState;

	@Expose
	@SerializedName("shipToState")
	@Column(name = "SHIP_TO_STATE")
	protected String shipToState;

	@Expose
	@SerializedName("shippingBillNo")
	@Column(name = "SHIP_BILL_NUM")
	protected String shippingBillNo;

	@Expose
	@SerializedName("shippingBillDate")
	@Column(name = "SHIP_BILL_DATE")
	protected String shippingBillDate;

	@Expose
	@SerializedName("tcsFlag")
	@Column(name = "TCS_FLAG")
	protected String tcsFlag;

	@Expose
	@SerializedName("accVoucherNo")
	@Column(name = "ACCOUNTING_VOUCHER_NUM")
	protected String accountingVoucherNumber;

	@Expose
	@SerializedName("accVoucherDate")
	@Column(name = "ACCOUNTING_VOUCHER_DATE")
	protected String accountingVoucherDate;

	/**
	 * EGSTIN will be present only for those transactions happening through an
	 * E-commerce supplier.
	 * 
	 */
	@Expose
	@SerializedName("ecomGSTIN")
	@Column(name = "ECOM_GSTIN")
	protected String egstin;

	/**
	 * Table type represents the table in the GSTR-1 form, where this document
	 * can be placed. E.g. 4A, 4B, 4C etc.
	 */

	/**
	 * This field represents the classification of invoices according to return
	 * filing rules by GSTN. Some of the possible values are B2B, B2CL, B2CS,
	 * EXP etc.
	 * 
	 */
	@Expose
	@SerializedName("igstAmt")
	@Column(name = "IGST_AMT")
	protected String igstAmount;

	@Expose
	@SerializedName("cgstAmt")
	@Column(name = "CGST_AMT")
	protected String cgstAmount;

	@Expose
	@SerializedName("sgstAmt")
	@Column(name = "SGST_AMT")
	protected String sgstAmount;

	@Expose
	@SerializedName("cessAmtSpecfic")
	@Column(name = "CESS_AMT_SPECIFIC")
	protected String cessAmountSpecific;

	@Expose
	@SerializedName("cessAmtAdvalorem")
	@Column(name = "CESS_AMT_ADVALOREM")
	protected String cessAmountAdvalorem;

	@Expose
	@SerializedName("irn")
	@Column(name = "IRN")
	protected String irn;

	@Expose
	@SerializedName("irnDate")
	@Column(name = "IRN_DATE")
	protected String irnDate;

	@Expose
	@SerializedName("taxScheme")
	@Column(name = "TAX_SCHEME")
	protected String taxScheme;

	@Expose
	@SerializedName("docCat")
	@Column(name = "DOC_CATEGORY")
	protected String docCategory;

	@Expose
	@SerializedName("supTradeName")
	@Column(name = "SUPP_TRADE_NAME")
	protected String supplierTradeName;

	@Expose
	@SerializedName("supLegalName")
	@Column(name = "SUPP_LEGAL_NAME")
	protected String supplierLegalName;

	@Expose
	@SerializedName("supBuildingNo")
	@Column(name = "SUPP_BUILDING_NUM")
	protected String supplierBuildingNumber;

	@Expose
	@SerializedName("supBuildingName")
	@Column(name = "SUPP_BUILDING_NAME")
	protected String supplierBuildingName;

	@Expose
	@SerializedName("supLocation")
	@Column(name = "SUPP_LOCATION")
	protected String supplierLocation;

	@Expose
	@SerializedName("supPincode")
	@Column(name = "SUPP_PINCODE")
	protected String supplierPincode;

	@Expose
	@SerializedName("supStateCode")
	@Column(name = "SUPP_STATE_CODE")
	protected String supplierStateCode;

	@Expose
	@SerializedName("supPhone")
	@Column(name = "SUPP_PHONE")
	protected String supplierPhone;

	@Expose
	@SerializedName("supEmail")
	@Column(name = "SUPP_EMAIL")
	protected String supplierEmail;

	@Expose
	@SerializedName("custTradeName")
	@Column(name = "CUST_TRADE_NAME")
	protected String customerTradeName;

	@Expose
	@SerializedName("custPincode")
	@Column(name = "CUST_PINCODE")
	protected String customerPincode;

	@Expose
	@SerializedName("custPhone")
	@Column(name = "CUST_PHONE")
	protected String customerPhone;

	@Expose
	@SerializedName("custEmail")
	@Column(name = "CUST_EMAIL")
	protected String customerEmail;

	@Expose
	@SerializedName("dispatcherGstin")
	@Column(name = "DISPATCHER_GSTIN")
	protected String dispatcherGstin;

	@Expose
	@SerializedName("dispatcherTradeName")
	@Column(name = "DISPATCHER_TRADE_NAME")
	protected String dispatcherTradeName;

	@Expose
	@SerializedName("dispatcherBuildingNo")
	@Column(name = "DISPATCHER_BUILDING_NUM")
	protected String dispatcherBuildingNumber;

	@Expose
	@SerializedName("dispatcherBuildingName")
	@Column(name = "DISPATCHER_BUILDING_NAME")
	protected String dispatcherBuildingName;

	@Expose
	@SerializedName("dispatcherLocation")
	@Column(name = "DISPATCHER_LOCATION")
	protected String dispatcherLocation;

	@Expose
	@SerializedName("dispatcherPincode")
	@Column(name = "DISPATCHER_PINCODE")
	protected String dispatcherPincode;

	@Expose
	@SerializedName("dispatcherStateCode")
	@Column(name = "DISPATCHER_STATE_CODE")
	protected String dispatcherStateCode;

	@Expose
	@SerializedName("shipToGstin")
	@Column(name = "SHIP_TO_GSTIN")
	protected String shipToGstin;

	@Expose
	@SerializedName("shipToTradeName")
	@Column(name = "SHIP_TO_TRADE_NAME")
	protected String shipToTradeName;

	@Expose
	@SerializedName("shipToLegalName")
	@Column(name = "SHIP_TO_LEGAL_NAME")
	protected String shipToLegalName;

	@Expose
	@SerializedName("shipToBuildingNo")
	@Column(name = "SHIP_TO_BUILDING_NUM")
	protected String shipToBuildingNumber;

	@Expose
	@SerializedName("shipToBuildingName")
	@Column(name = "SHIP_TO_BUILDING_NAME")
	protected String shipToBuildingName;

	@Expose
	@SerializedName("shipToLocation")
	@Column(name = "SHIP_TO_LOCATION")
	protected String shipToLocation;

	@Expose
	@SerializedName("shipToPincode")
	@Column(name = "SHIP_TO_PINCODE")
	protected String shipToPincode;

	@Expose
	@SerializedName("invOtherCharges")
	@Column(name = "INV_OTHER_CHARGES")
	protected String invoiceOtherCharges;

	@Expose
	@SerializedName("invAssessableAmt")
	@Column(name = "INV_ASSESSABLE_AMT")
	protected String invoiceAssessableAmount;

	@Expose
	@SerializedName("invIgstAmt")
	@Column(name = "INV_IGST_AMT")
	protected String invoiceIgstAmount;

	@Expose
	@SerializedName("invCgstAmt")
	@Column(name = "INV_CGST_AMT")
	protected String invoiceCgstAmount;

	@Expose
	@SerializedName("invSgstAmt")
	@Column(name = "INV_SGST_AMT")
	protected String invoiceSgstAmount;

	@Expose
	@SerializedName("invCessAdvaloremAmt")
	@Column(name = "INV_CESS_ADVLRM_AMT")
	protected String invoiceCessAdvaloremAmount;

	@Expose
	@SerializedName("invCessSpecificAmt")
	@Column(name = "INV_CESS_SPECIFIC_AMT")
	protected String invoiceCessSpecificAmount;

	@Expose
	@SerializedName("invStateCessAmt")
	@Column(name = "INV_STATE_CESS_AMT")
	protected String invoiceStateCessAmount;

	@Expose
	@SerializedName("roundOff")
	@Column(name = "ROUND_OFF")
	protected String roundOff;

	@Expose
	@SerializedName("totalInvValueInWords")
	@Column(name = "TOT_INV_VAL_WORLDS")
	protected String totalInvoiceValueInWords;

	@Expose
	@SerializedName("foreignCurrency")
	@Column(name = "FOREIGN_CURRENCY")
	protected String foreignCurrency;

	@Expose
	@SerializedName("countryCode")
	@Column(name = "COUNTRY_CODE")
	protected String countryCode;

	@Expose
	@SerializedName("invValueFc")
	@Column(name = "INV_VAL_FC")
	protected String invoiceValueFc;

	@Expose
	@SerializedName("invPeriodStartDate")
	@Column(name = "INV_PERIOD_START_DATE")
	protected String invoicePeriodStartDate;

	@Expose
	@SerializedName("invPeriodEndDate")
	@Column(name = "INV_PERIOD_END_DATE")
	protected String invoicePeriodEndDate;

	@Expose
	@SerializedName("payeeName")
	@Column(name = "PAYEE_NAME")
	protected String payeeName;

	@Expose
	@SerializedName("modeOfPayment")
	@Column(name = "MODE_OF_PAYMENT")
	protected String modeOfPayment;

	@Expose
	@SerializedName("branchOrIfscCode")
	@Column(name = "BRANCH_IFSC_CODE")
	protected String branchOrIfscCode;

	@Expose
	@SerializedName("paymentTerms")
	@Column(name = "PAYMENT_TERMS")
	protected String paymentTerms;

	@Expose
	@SerializedName("paymentInstruction")
	@Column(name = "PAYMENT_INSTRUCTION")
	protected String paymentInstruction;

	@Expose
	@SerializedName("creditTransfer")
	@Column(name = "CR_TRANSFER")
	protected String creditTransfer;

	@Expose
	@SerializedName("directDebit")
	@Column(name = "DB_DIRECT")
	protected String directDebit;

	@Expose
	@SerializedName("creditDays")
	@Column(name = "CR_DAYS")
	protected String creditDays;

	@Expose
	@SerializedName("paymentDueDate")
	@Column(name = "PAYMENT_DUE_DATE")
	protected String paymentDueDate;

	@Expose
	@SerializedName("accDetail")
	@Column(name = "ACCOUNT_DETAIL")
	protected String accountDetail;

	@Expose
	@SerializedName("tdsFlag")
	@Column(name = "TDS_FLAG")
	protected String tdsFlag;

	@Expose
	@SerializedName("tranType")
	@Column(name = "TRANS_TYPE")
	protected String transactionType;

	@Expose
	@SerializedName("subsupplyType")
	@Column(name = "SUB_SUPP_TYPE")
	protected String subSupplyType;

	@Expose
	@SerializedName("otherSupplyTypeDesc")
	@Column(name = "OTHER_SUPP_TYPE_DESC")
	protected String otherSupplyTypeDescription;

	@Expose
	@SerializedName("transporterID")
	@Column(name = "TRANSPORTER_ID")
	protected String transporterID;

	@Expose
	@SerializedName("transporterName")
	@Column(name = "TRANSPORTER_NAME")
	protected String transporterName;

	@Expose
	@SerializedName("transportMode")
	@Column(name = "TRANSPORT_MODE")
	protected String transportMode;

	@Expose
	@SerializedName("transportDocNo")
	@Column(name = "TRANSPORT_DOC_NUM")
	protected String transportDocNo;

	@Expose
	@SerializedName("transportDocDate")
	@Column(name = "TRANSPORT_DOC_DATE")
	protected String transportDocDate;

	@Expose
	@SerializedName("distance")
	@Column(name = "DISTANCE")
	protected String distance;

	@Expose
	@SerializedName("vehicleNo")
	@Column(name = "VEHICLE_NUM")
	protected String vehicleNo;

	@Expose
	@SerializedName("vehicleType")
	@Column(name = "VEHICLE_TYPE")
	protected String vehicleType;

	@Expose
	@SerializedName("exchangeRt")
	@Column(name = "EXCHANGE_RATE")
	protected String exchangeRate;

	@Expose
	@SerializedName("companyCode")
	@Column(name = "COMPANY_CODE")
	protected String companyCode;

	@Expose
	@SerializedName("glPostingDate")
	@Column(name = "GL_POSTING_DATE")
	protected String glPostingDate;

	@Expose
	@SerializedName("salesOrderNo")
	@Column(name = "SALES_ORD_NUM")
	protected String salesOrderNumber;

	@Expose
	@SerializedName("custTan")
	@Column(name = "CUST_TAN")
	protected String customerTan;

	@Expose
	@SerializedName("canReason")
	@Column(name = "CANCEL_REASON")
	protected String cancellationReason;

	@Expose
	@SerializedName("canRemarks")
	@Column(name = "CANCEL_REMARKS")
	protected String cancellationRemarks;

	@Expose
	@SerializedName("invStateCessSpecificAmt")
	@Column(name = "INV_STATE_CESS_SPECIFIC_AMOUNT")
	protected String invStateCessSpecificAmt;

	@Expose
	@SerializedName("tcsFlagIncomeTax")
	@Column(name = "TCS_FLAG_INCOME_TAX")
	protected String tcsFlagIncomeTax;

	@Expose
	@SerializedName("custPANOrAadhaar")
	@Column(name = "CUSTOMER_PAN_OR_AADHAAR")
	protected String customerPANOrAadhaar;

	@Expose
	@SerializedName("glStateCessSpecific")
	@Column(name = "GL_STATE_CESS_SPECIFIC")
	protected String glStateCessSpecific;

	@Expose
	@SerializedName("originalInvoiceNumber")
	@Column(name = "ORG_INV_NUM")
	private String originalInvoiceNumber;

	@Expose
	@SerializedName("originalInvoiceDate")
	@Column(name = "ORG_INV_DATE")
	private String originalInvoiceDate;

	@Expose
	@SerializedName("preceedingInvNo")
	@Column(name = "PRECEEDING_INV_NUM")
	private String preceedingInvoiceNumber;

	@Expose
	@SerializedName("preceedingInvDate")
	@Column(name = "PRECEEDING_INV_DATE")
	private String preceedingInvoiceDate;

	@Expose
	@SerializedName("glCodeIgst")
	@Column(name = "GLCODE_IGST")
	protected String glCodeIgst;

	@Expose
	@SerializedName("glCodeCgst")
	@Column(name = "GLCODE_CGST")
	protected String glCodeCgst;

	@Expose
	@SerializedName("glCodeSgst")
	@Column(name = "GLCODE_SGST")
	protected String glCodeSgst;

	@Expose
	@SerializedName("glCodeAdvCess")
	@Column(name = "GLCODE_ADV_CESS")
	protected String glCodeAdvCess;

	@Expose
	@SerializedName("glCodeSpCess")
	@Column(name = "GLCODE_SP_CESS")
	protected String glCodeSpCess;

	@Expose
	@SerializedName("glCodeStateCess")
	@Column(name = "GLCODE_STATE_CESS")
	protected String glCodeStateCess;

	@Expose
	@SerializedName("profitCentre2")
	@Column(name = "PROFIT_CENTRE2")
	protected String profitCentre2;

	@Expose
	@SerializedName("invRemarks")
	@Column(name = "INV_REMARKS")
	private String invoiceRemarks;

	@Expose
	@SerializedName("ecomTransactionID")
	@Column(name = "ECOM_TRANS_ID")
	protected String ecomTransactionID;

	/**
	 * Initialize an empty array list to hold the line items. The ITEM_INDEX
	 * column will make sure that the order of the line items are preserved in
	 * the database. This is the JPA way of indexing one to many collections.
	 */
	@Expose
	@SerializedName("lineItems")
	@OneToMany(mappedBy = "document", fetch = FetchType.EAGER)
	@OrderColumn(name = "ITEM_INDEX")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<AnxOutwardTransDocLineItemError> lineItems = new ArrayList<>();

	public Integer getItemNoForIndex(Integer itemIndex) {

		int lineItemCount = this.lineItems.size();
		if (itemIndex < lineItemCount && itemIndex >= 0) {
			AnxOutwardTransDocLineItemError lineItem = lineItems.get(itemIndex);
			try {
				return Integer.valueOf(lineItem.getLineNo());
			} catch (Exception e) {
				if (lineItem.getLineNo() == null
						|| lineItem.getLineNo().trim().isEmpty()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("lineItem null or empty ");
					}
					LOGGER.error("Line item error " + e.getMessage());
					return null;
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("lineItem " + lineItem.getLineNo());
					}
					LOGGER.error("Line item error " + e.getMessage());
					return -1;
				}
			}
		}
		return null;
	}
}
