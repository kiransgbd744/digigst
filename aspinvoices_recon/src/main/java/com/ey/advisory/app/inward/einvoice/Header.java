/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
@XmlRootElement(name = "HEADER")
@XmlAccessorType(XmlAccessType.FIELD)
public class Header {

	@XmlElement(name = "DOCUMENTTYPE")
	private String documentType;
	
	@XmlElement(name = "SUPPLIERGSTIN")
	private String supplierGSTIN;
	
	@XmlElement(name = "DOCUMENTNUMBER")
	private String documentNumber;
	
	@XmlElement(name = "DOCUMENTDATE")
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
	
	@XmlElement(name = "CANCELREASON")
	private String cancellationReason;
	
	@XmlElement(name = "CANCELREMARKS")
	private String cancellationRemarks;
	
	@XmlElement(name = "REVCHARGEFLAG")
	private String revChargeFlag;
	
	@XmlElement(name = "SUPPLIERTNAME")
	private String supplierTradeName;
	
	@XmlElement(name = "SUPPLIERLNAME")
	private String supplierLegalName;
	
	@XmlElement(name = "SUPPLIERADDRESS1")
	private String supplierAddress1;
	
	@XmlElement(name = "SUPPLIERADDRESS2")
	private String supplierAddress2;
	
	@XmlElement(name = "SUPPLIERLOCATION")
	private String supplierLocation;
	
	@XmlElement(name = "SUPPLIERPINCODE")
	private String supplierPincode;
	
	@XmlElement(name = "SUPPLIERSTATCODE")
	private String supplierStateCode;
	
	@XmlElement(name = "SUPPLIERPHONE")
	private String supplierPhone;
	
	@XmlElement(name = "SUPPLIEREMAIL")
	private String supplierEmail;
	
	@XmlElement(name = "CUSTOMERGSTIN")
	private String customerGSTIN;
	
	@XmlElement(name = "CUSTOMERTNAME")
	private String customerTradeName;
	
	@XmlElement(name = "CUSTOMERLNAME")
	private String customerLegalName;
	
	@XmlElement(name = "CUSTOMERADDRESS1")
	private String customerAddress1;
	
	@XmlElement(name = "CUSTOMERADDRESS2")
	private String customerAddress2;
	
	@XmlElement(name = "CUSTOMERLOCATION")
	private String customerLocation;
	
	@XmlElement(name = "CUSTOMERPINCODE")
	private String customerPincode;
	
	@XmlElement(name = "CUSTOMERSTATCODE")
	private String customerStateCode;
	
	@XmlElement(name = "BILLINGPOS")
	private String billingPOS;
	
	@XmlElement(name = "CUSTOMERPHONE")
	private String customerPhone;
	
	@XmlElement(name = "CUSTOMEREMAIL")
	private String customerEmail;
	
	@XmlElement(name = "DISPATCHERTNAME")
	private String dispatcherTradeName;
	
	@XmlElement(name = "DISPATCHADDRESS1")
	private String dispatcherAddress1;
	
	@XmlElement(name = "DISPATCHADDRESS2")
	private String dispatcherAddress2;
	
	@XmlElement(name = "DISPATCHLOCATION")
	private String dispatcherLocation;
	
	@XmlElement(name = "DISPATCHPINCODE")
	private String dispatcherPincode;
	
	@XmlElement(name = "DISPATCHSTATCODE")
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
	
	@XmlElement(name = "INV_OTHERCHARGES")
	private BigDecimal invoiceOtherCharges;
	
	@XmlElement(name = "INVASSESSABLEAMT")
	private BigDecimal invoiceAssessableAmount;
	
	@XmlElement(name = "INVIGSTAMOUNT")
	private BigDecimal invoiceIGSTAmount;
	
	@XmlElement(name = "INVCGSTAMOUNT")
	private BigDecimal invoiceCGSTAmount;
	
	@XmlElement(name = "INVSGSTAMOUNT")
	private BigDecimal invoiceSGSTAmount;
	
	@XmlElement(name = "INVADVALOREMAMT")
	private BigDecimal invoiceCessAdValoremAmount;
	
	@XmlElement(name = "INVSPECIFICAMT")
	private BigDecimal invoiceCessSpecificAmount;
	
	@XmlElement(name = "INVSTCESSADVAMT")
	private BigDecimal invoiceStateCessAdValoremAmt;
	
	@XmlElement(name = "INVSTCESSSPEAMT")
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
	
	@XmlElement(name = "SHIPPINGBILLNO")
	private String shippingBillNumber;
	
	@XmlElement(name = "SHIPPINGBILLDATE")
	private String shippingBillDate;
	
	@XmlElement(name = "INVOICEREMARKS")
	private String invoiceRemarks;
	
	@XmlElement(name = "INVPERIODSTARTDT")
	private String invoicePeriodStartDate;
	
	@XmlElement(name = "INVPERIODENDDATE")
	private String invoicePeriodEndDate;
	
	@XmlElement(name = "PAYEENAME")
	private String payeeName;
	
	@XmlElement(name = "MODEOFPAYMENT")
	private String modeOfPayment;
	
	@XmlElement(name = "BRANCHORIFSCCODE")
	private String branchOrIFSCCode;
	
	@XmlElement(name = "PAYMENTTERMS")
	private String paymentTerms;
	
	@XmlElement(name = "PAYINSTRUCTION")
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
	
	@XmlElement(name = "IRNGENPRD")
	private String irnGenerationPeriod;
	
	@XmlElement(name = "RIDT")
	private String rivIntDate;
	
	@XmlElement(name = "PAYLOAD_ID")
	private String payloadId;
	
	@XmlElement(name = "SIGNEDQRCODE")
	private String signedQrCode;
	
}
