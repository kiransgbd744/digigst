package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.sql.Clob;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
@Entity
@Table(name = "TBL_GETIRN_DEXP_HEADER")
public class GetIrnDexpHeaderEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_GETIRN_DEXP_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "IRN")
	private String irn;

	@Column(name = "IRN_DATE_TIME")
	private LocalDateTime irnDateTime;

	@Column(name = "IRN_DATE")
	private LocalDateTime irnDate;

	@Column(name = "IRN_STATUS")
	private String irnStatus;

	@Column(name = "ACK_NUM")
	private Long ackNum;

	@Column(name = "TAX_SCHEME")
	private String taxScheme;

	@Column(name = "IRN_CANCEL_DATE_TIME")
	private LocalDateTime irnCancelDateTime;

	@Column(name = "IRN_CANCEL_DATE")
	private LocalDateTime irnCancelDate;

	@Column(name = "CANCELLATION_REASON")
	private String cancellationReason;

	@Column(name = "CANCELLATION_REMARKS")
	private String cancellationRemarks;

	@Column(name = "SUPPLY_TYPE")
	private String supplyType;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOC_NUM")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDateTime docDate;

	@Column(name = "REVERSECHARGEFLAG")
	private String reverseChargeFlag;

	@Column(name = "SUPPLIER_GSTIN")
	private String supplierGSTIN;

	@Column(name = "SUPPLIER_TRADENAME")
	private String supplierTradeName;

	@Column(name = "SUPPLIER_LEGALNAME")
	private String supplierLegalName;

	@Column(name = "SUPPLIER_ADDRESS1")
	private String supplierAddress1;

	@Column(name = "SUPPLIER_ADDRESS2")
	private String supplierAddress2;

	@Column(name = "SUPPLIER_LOCATION")
	private String supplierLocation;

	@Column(name = "SUPPLIER_PINCODE")
	private Integer supplierPincode;

	@Column(name = "SUPPLIER_STATECODE")
	private Integer supplierStateCode;

	@Column(name = "SUPPLIER_PHONE")
	private String supplierPhone;

	@Column(name = "SUPPLIER_EMAIL")
	private String supplierEmail;

	@Column(name = "CUSTOMER_GSTIN")
	private String customerGSTIN;

	@Column(name = "CUSTOMER_TRADENAME")
	private String customerTradeName;

	@Column(name = "CUSTOMER_LEGALNAME")
	private String customerLegalName;

	@Column(name = "CUSTOMER_ADDRESS1")
	private String customerAddress1;

	@Column(name = "CUSTOMER_ADDRESS2")
	private String customerAddress2;

	@Column(name = "CUSTOMER_LOCATION")
	private String customerLocation;

	@Column(name = "CUSTOMER_PINCODE")
	private Integer customerPincode;

	@Column(name = "CUSTOMER_STATECODE")
	private Integer customerStateCode;

	@Column(name = "BILLING_POS")
	private String billingPOS;

	@Column(name = "CUSTOMER_PHONE")
	private String customerPhone;

	@Column(name = "CUSTOMER_EMAIL")
	private String customerEmail;

	@Column(name = "DISPATCHER_TRADENAME")
	private String dispatcherTradeName;

	@Column(name = "DISPATCHER_ADDRESS1")
	private String dispatcherAddress1;

	@Column(name = "DISPATCHER_ADDRESS2")
	private String dispatcherAddress2;

	@Column(name = "DISPATCHER_LOCATION")
	private String dispatcherLocation;

	@Column(name = "DISPATCHER_PINCODE")
	private Integer dispatcherPincode;

	@Column(name = "DISPATCHER_STATECODE")
	private Integer dispatcherStateCode;

	@Column(name = "SHIP_TO_GSTIN")
	private String shipToGSTIN;

	@Column(name = "SHIP_TO_TRADENAME")
	private String shipToTradeName;

	@Column(name = "SHIP_TO_LEGALNAME")
	private String shipToLegalName;

	@Column(name = "SHIP_TO_ADDRESS1")
	private String shipToAddress1;

	@Column(name = "SHIP_TO_ADDRESS2")
	private String shipToAddress2;

	@Column(name = "SHIP_TO_LOCATION")
	private String shipToLocation;

	@Column(name = "SHIP_TO_PINCODE")
	private Integer shipToPincode;

	@Column(name = "SHIP_TO_STATECODE")
	private Integer shipToStateCode;

	@Column(name = "INV_OTHER_CHARGES")
	private BigDecimal invOtherCharges;

	@Column(name = "INV_ASSESSABLE_AMT")
	private BigDecimal invAssessableAmt;

	@Column(name = "INV_IGST_AMT")
	private BigDecimal invIgstAmt;

	@Column(name = "INV_CGST_AMT")
	private BigDecimal invCgstAmt;

	@Column(name = "INV_SGST_AMT")
	private BigDecimal invSgstAmt;

	@Column(name = "INV_CESS_ADVALOREM_AMT")
	private BigDecimal invCessAdvaloremAmt;

	@Column(name = "INV_CESS_SPECIFIC_AMT")
	private BigDecimal invCessSpecificAmt;

	@Column(name = "INV_STATECESS_ADVALOREM_AMT")
	private BigDecimal invStateCessAdvaloremAmt;

	@Column(name = "INV_STATECESS_SPECIFIC_AMT")
	private BigDecimal invStateCessSpecificAmt;

	@Column(name = "INV_VALUE")
	private BigDecimal invValue;

	@Column(name = "ROUNDOFF")
	private BigDecimal roundOff;

	@Column(name = "CURRENCY_CODE")
	private String currencyCode;

	@Column(name = "COUNTRY_CODE")
	private String countryCode;

	@Column(name = "INVOICE_VALUE_FC")
	private BigDecimal invoiceValueFC;

	@Column(name = "PORT_CODE")
	private String portCode;

	@Column(name = "SHIPPING_BILL_NUMBER")
	private String shippingBillNumber;

	@Column(name = "SHIPPING_BILL_DATE")
	private LocalDateTime shippingBillDate;

	@Column(name = "INVOICE_REMARKS")
	private String invoiceRemarks;

	@Column(name = "INVOICE_PERIOD_START_DATE")
	private LocalDateTime invoicePeriodStartDate;

	@Column(name = "INVOICE_PERIOD_END_DATE")
	private LocalDateTime invoicePeriodEndDate;

	@Column(name = "PAYEE_NAME")
	private String payeeName;

	@Column(name = "MODE_OF_PAYMENT")
	private String modeOfPayment;

	@Column(name = "BRANCH_OR_IFSC_CODE")
	private String branchOrIFSCCode;

	@Column(name = "PAYMENT_TERMS")
	private String paymentTerms;

	@Column(name = "PAYMENT_INSTRUCTION")
	private String paymentInstruction;

	@Column(name = "CREDIT_TRANSFER")
	private String creditTransfer;

	@Column(name = "DIRECT_DEBIT")
	private String directDebit;

	@Column(name = "CREDIT_DAYS")
	private Integer creditDays;

	@Column(name = "ACCOUNT_DETAIL")
	private String accountDetail;

	@Column(name = "ECOM_GSTIN")
	private String ecomGSTIN;

	@Column(name = "TRANSPORTER_ID")
	private String transporterID;

	@Column(name = "TRANSPORTER_NAME")
	private String transporterName;

	@Column(name = "TRANSPORT_MODE")
	private String transportMode;

	@Column(name = "TRANSPORT_DOCNO")
	private String transportDocNo;

	@Column(name = "TRANSPORT_DOCDATE")
	private String transportDocDate;

	@Column(name = "DISTANCE")
	private Integer distance;

	@Column(name = "VEHICLENO")
	private String vehicleNo;

	@Column(name = "VEHICLE_TYPE")
	private String vehicleType;

	@Column(name = "SECTION7OFIGSTFLAG")
	private String section7OfIGSTFlag;

	@Column(name = "CLAIMREFUNDFLAG")
	private String claimRefundFlag;

	@Column(name = "USERDEFINEDFIELD28")
	private BigDecimal userDefinedField28;

	@Column(name = "IRN_GENERATION_PERIOD")
	private String irnGenerationPeriod;

	@Column(name = "REV_INTEG_PAYLOAD_ID")
	private Long revIntegrationPayloadId;

	@Column(name = "REV_INTEG_DTTM")
	private LocalDateTime revIntegrationDateTime;

	@Column(name = "EWAY_BILL_NUMBER")
	private Long eWayBillNumber;

	@Column(name = "EWAY_BILL_DATE")
	private LocalDateTime eWayBillDate;

	@Column(name = "VALID_UPTO")
	private LocalDateTime validUpto;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "PAID_AMOUNT")
	private BigDecimal paidAmount;

	@Column(name = "BALANCE_AMOUNT")
	private BigDecimal balanceAmount;
	
	@Column(name = "EXPORT_DUTY")
	private BigDecimal exportDuty;
	
	@Column(name = "DISCOUNT")
	private BigDecimal discount;
	
	@Column(name = "SIGNED_QR")
	private Clob signedQR;
	
	@Column(name = "IRP_NAME")
	private String issNme;

	/*@OneToMany(mappedBy = "header")
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetIrnDexpItemEntity> lineItems = new ArrayList<>();
*/
}
