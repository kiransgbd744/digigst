/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data

public class HeaderItemDto {

	@XmlElement(name = "DOCUMENTTYPE")
	private String documentType;
	
	@XmlElement(name = "SUPPLIERGSTIN")
	private String supplierGSTIN;
	
	@XmlElement(name = "DOCUMENTNUMBER")
	private String documentNumber;
	
	@XmlElement(name = "DOCUMENTString")
	private String documentDate;
	
	@XmlElement(name = "IRN")
	private String IRN;
	
	@XmlElement(name = "IRNDT")
	private String IRNDT;
	
	@XmlElement(name = "EINVOICESTATUS")
	private String eInvoiceStatus;
	
	@XmlElement(name = "IRNCANDT")
	private String IRNCANDT;
	
	@XmlElement(name = "ACKNO")
	private String ACKNO;
	
	@XmlElement(name = "ACKDT")
	private String ACKDT;
	
	@XmlElement(name = "EWBNO")
	private String EWBNO;
	
	@XmlElement(name = "EWBDT")
	private String EWBDT;
	
	@XmlElement(name = "EWBVALIDTILL")
	private String EWBValidTill;
	
	@XmlElement(name = "SUPPLYTYPE")
	private String supplyType;
	
	@XmlElement(name = "TAXSCHEME")
	private String taxScheme;
	
	@XmlElement(name = "CANCELLATIONREASON")
	private String cancellationReason;
	
	@XmlElement(name = "CANCELLATIONREMARKS")
	private String cancellationRemarks;
	
	@XmlElement(name = "REVCHARGEFLAG")
	private String revChargeFlag;
	
	@XmlElement(name = "SUPPLIERTRADENAME")
	private String supplierTradeName;
	
	@XmlElement(name = "SUPPLIERLEGALNAME")
	private String supplierLegalName;
	
	@XmlElement(name = "SUPPLIERADDRESS1")
	private String supplierAddress1;
	
	@XmlElement(name = "SUPPLIERADDRESS2")
	private String supplierAddress2;
	
	@XmlElement(name = "SUPPLIERLOCATION")
	private String supplierLocation;
	
	@XmlElement(name = "SUPPLIERPINCODE")
	private String supplierPincode;
	
	@XmlElement(name = "SUPPLIERSTATECODE")
	private String supplierStateCode;
	
	@XmlElement(name = "SUPPLIERPHONE")
	private String supplierPhone;
	
	@XmlElement(name = "SUPPLIEREMAIL")
	private String supplierEmail;
	
	@XmlElement(name = "CUSTOMERGSTIN")
	private String customerGSTIN;
	
	@XmlElement(name = "CUSTOMERTRADENAME")
	private String customerTradeName;
	
	@XmlElement(name = "CUSTOMERLEGALNAME")
	private String customerLegalName;
	
	@XmlElement(name = "CUSTOMERADDRESS1")
	private String customerAddress1;
	
	@XmlElement(name = "CUSTOMERADDRESS2")
	private String customerAddress2;
	
	@XmlElement(name = "CUSTOMERLOCATION")
	private String customerLocation;
	
	@XmlElement(name = "CUSTOMERPINCODE")
	private String customerPincode;
	
	@XmlElement(name = "CUSTOMERSTATECODE")
	private String customerStateCode;
	
	@XmlElement(name = "BILLINGPOS")
	private String billingPOS;
	
	@XmlElement(name = "CUSTOMERPHONE")
	private String customerPhone;
	
	@XmlElement(name = "CUSTOMEREMAIL")
	private String customerEmail;
	
	@XmlElement(name = "DISPATCHERTRADENAME")
	private String dispatcherTradeName;
	
	@XmlElement(name = "DISPATCHERADDRESS1")
	private String dispatcherAddress1;
	
	@XmlElement(name = "DISPATCHERADDRESS2")
	private String dispatcherAddress2;
	
	@XmlElement(name = "DISPATCHERLOCATION")
	private String dispatcherLocation;
	
	@XmlElement(name = "DISPATCHERPINCODE")
	private String dispatcherPincode;
	
	@XmlElement(name = "DISPATCHERSTATECODE")
	private String dispatcherStateCode;
	
	@XmlElement(name = "SHIPTOGSTIN")
	private String shipToGSTIN;
	
	@XmlElement(name = "SHIPTOTRADENAME")
	private String shipToTradeName;
	
	@XmlElement(name = "SHIPTOLEGALNAME")
	private String shipToLegalName;
	
	@XmlElement(name = "SHIPTOADDRESS1")
	private String shipToAddress1;
	
	@XmlElement(name = "SHIPTOADDRESS2")
	private String shipToAddress2;
	
	@XmlElement(name = "SHIPTOLOCATION")
	private String shipToLocation;
	
	@XmlElement(name = "SHIPTOPINCODE")
	private String shipToPincode;
	
	@XmlElement(name = "SHIPTOSTATECODE")
	private String shipToStateCode;
	
	@XmlElement(name = "INVOICEOTHERCHARGES")
	private BigDecimal invoiceOtherCharges;
	
	@XmlElement(name = "INVOICEASSESSABLEAMOUNT")
	private BigDecimal invoiceAssessableAmount;
	
	@XmlElement(name = "INVOICEIGSTAMOUNT")
	private BigDecimal invoiceIGSTAmount;
	
	@XmlElement(name = "INVOICECGSTAMOUNT")
	private BigDecimal invoiceCGSTAmount;
	
	@XmlElement(name = "INVOICESGSTAMOUNT")
	private BigDecimal invoiceSGSTAmount;
	
	@XmlElement(name = "INVOICECESSADVALOREMAMOUNT")
	private BigDecimal invoiceCessAdValoremAmount;
	
	@XmlElement(name = "INVOICECESSSPECIFICAMOUNT")
	private BigDecimal invoiceCessSpecificAmount;
	
	@XmlElement(name = "INVOICESTATECESSADVALOREMAMT")
	private BigDecimal invoiceStateCessAdValoremAmt;
	
	@XmlElement(name = "INVOICESTATECESSSPECIFICAMOUNT")
	private BigDecimal invoiceStateCessSpecificAmount;
	
	@XmlElement(name = "INVOICEVALUE")
	private BigDecimal invoiceValue;
	
	@XmlElement(name = "ROUNDOFF")
	private BigDecimal roundOff;
	
	@XmlElement(name = "CURRENCYCODE")
	private String currencyCode;
	
	@XmlElement(name = "COUNTRYCODE")
	private String countryCode;
	
	@XmlElement(name = "INVOICEVALUEFC")
	private BigDecimal invoiceValueFC;
	
	@XmlElement(name = "PORTCODE")
	private String portCode;
	
	@XmlElement(name = "SHIPPINGBILLNUMBER")
	private String shippingBillNumber;
	
	@XmlElement(name = "SHIPPINGBILLDATE")
	private String shippingBillDate;
	
	@XmlElement(name = "INVOICEREMARKS")
	private String invoiceRemarks;
	
	@XmlElement(name = "INVOICEPERIODSTARTDATE")
	private String invoicePeriodStartDate;
	
	@XmlElement(name = "INVOICEPERIODENDDATE")
	private String invoicePeriodEndDate;
	
	@XmlElement(name = "PAYEENAME")
	private String payeeName;
	
	@XmlElement(name = "MODEOFPAYMENT")
	private String modeOfPayment;
	
	@XmlElement(name = "BRANCHORIFSCCODE")
	private String branchOrIFSCCode;
	
	@XmlElement(name = "PAYMENTTERMS")
	private String paymentTerms;
	
	@XmlElement(name = "PAYMENTINSTRUCTION")
	private String paymentInstruction;
	
	@XmlElement(name = "CREDITTRANSFER")
	private String creditTransfer;
	
	@XmlElement(name = "DIRECTDEBIT")
	private String directDebit;
	
	@XmlElement(name = "CREDITDAYS")
	private Integer creditDays;
	
	@XmlElement(name = "ACCOUNTDETAIL")
	private String accountDetail;
	
	@XmlElement(name = "ECOMGSTIN")
	private String ecomGSTIN;
	
	@XmlElement(name = "TRANSPORTERID")
	private String transporterId;
	
	@XmlElement(name = "TRANSPORTERNAME")
	private String transporterName;
	
	@XmlElement(name = "TRANSPORTMODE")
	private String transportMode;
	
	@XmlElement(name = "TRANSPORTDOCNO")
	private String transportDocNo;
	
	@XmlElement(name = "TRANSPORTDOCDATE")
	private String transportDocDate;
	
	@XmlElement(name = "DISTANCE")
	private BigDecimal distance;
	
	@XmlElement(name = "VEHICLENO")
	private String vehicleNo;
	
	@XmlElement(name = "VEHICLETYPE")
	private String vehicleType;
	
	@XmlElement(name = "SEC7IGSTFLAG")
	private String sec7IGSTFlag;
	
	@XmlElement(name = "CLAIMREFUNDFLAG")
	private String claimRefundFlag;
	
	@XmlElement(name = "INVOICEDISCOUNT")
	private BigDecimal invoiceDiscount;
	
	@XmlElement(name = "IRNGENERATIONPERIOD")
	private String irnGenerationPeriod;
	
	@XmlElement(name = "RIDT")
	private String rivIntDate;
	
	@XmlElement(name = "PAYLOAD_ID")
	private String payloadId;

	@XmlElement(name = "LINENUMBER")
	private String lineNumber;
	
	@XmlElement(name = "PRODUCTSERIALNUMBER")
	private String productSerialNumber;
	
	@XmlElement(name = "PRODUCT_DESC")
	private String productDescription;
	
	@XmlElement(name = "ISSERVICE")
	private String isService;
	
	@XmlElement(name = "HSN")
	private String HSN;
	
	@XmlElement(name = "BARCODE")
	private String barcode;
	
	@XmlElement(name = "BATCHNAME")
	private String batchName;
	
	@XmlElement(name = "BATCHEXPIRYDATE")
	private String batchExpiryDate;
	
	@XmlElement(name = "WARRANTYDATE")
	private String warrantyDate;
	
	@XmlElement(name = "ORDERLINEREFERENCE")
	private String orderLineReference;
	
	@XmlElement(name = "ORIGINCOUNTRY")
	private String originCountry;
	
	@XmlElement(name = "UQC")
	private String UQC;
	
	@XmlElement(name = "QUANTITY")
	private BigDecimal quantity;
	
	@XmlElement(name = "FREEQUANTITY")
	private BigDecimal freeQuantity;
	
	@XmlElement(name = "UNITPRICE")
	private BigDecimal unitPrice;
	
	@XmlElement(name = "ITEMAMOUNT")
	private BigDecimal itemAmount;
	
	@XmlElement(name = "ITEMDISCOUNT")
	private BigDecimal itemDiscount;
	
	@XmlElement(name = "PRETAXAMOUNT")
	private BigDecimal preTaxAmount;
	
	@XmlElement(name = "ITEMASSESSABLEAMT")
	private BigDecimal itemAssessableAmount;
	
	@XmlElement(name = "IGSTRATE")
	private BigDecimal igstRate;
	
	@XmlElement(name = "IGSTAMOUNT")
	private BigDecimal igstAmount;
	
	@XmlElement(name = "CGSTRATE")
	private BigDecimal cgstRate;
	
	@XmlElement(name = "CGSTAMOUNT")
	private BigDecimal cgstAmount;
	
	@XmlElement(name = "SGSTRATE")
	private BigDecimal sgstRate;
	
	@XmlElement(name = "SGSTAMOUNT")
	private BigDecimal sgstAmount;
	
	@XmlElement(name = "CESSADVALOREMRATE")
	private BigDecimal cessAdValoremRate;
	
	@XmlElement(name = "CESSADVALOREMAMT")
	private BigDecimal cessAdValoremAmount;
	
	@XmlElement(name = "CESSSPECIFICAMT")
	private BigDecimal cessSpecificAmount;
	
	@XmlElement(name = "STATECESSADVALOREMRATE")
	private BigDecimal stateCessAdValoremRate;
	
	@XmlElement(name = "STATECESSADVALOREMAMOUNT")
	private BigDecimal stateCessAdValoremAmount;
	
	@XmlElement(name = "STATECESSAMOUNT")
	private BigDecimal stateCessAmount;
	
	@XmlElement(name = "ITEMOTHERCHARGES")
	private BigDecimal itemOtherCharges;
	
	@XmlElement(name = "TOTALITEMAMOUNT")
	private BigDecimal totalItemAmount;
	
	@XmlElement(name = "PAIDAMOUNT")
	private BigDecimal paidAmount;
	
	@XmlElement(name = "BALANCEAMOUNT")
	private BigDecimal balanceAmount;
	
	@XmlElement(name = "EXPORTDUTY")
	private BigDecimal exportDuty;

	@XmlElement(name = "SIGNEDQRCODE")
	private String signedQrCode;
	

}
