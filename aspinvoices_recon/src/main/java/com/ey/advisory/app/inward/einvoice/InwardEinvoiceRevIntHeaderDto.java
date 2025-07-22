/**
 * 
 */
package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.util.Date;

import com.ey.advisory.common.JaxbXmlFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
@XmlRootElement(name = "IT_DATA")
@XmlAccessorType(XmlAccessType.FIELD)
public class InwardEinvoiceRevIntHeaderDto implements JaxbXmlFormatter{

	private String documentType;
	private String supplierGSTIN;
	private String documentNumber;
	private Date documentDate;
	private String IRN;
	private Date IRNDT;
	private String eInvoiceStatus;
	private Date IRNCANDT;
	private String ACKNO;
	private Date ACKDT;
	private String EWBNO;
	private Date EWBDT;
	private Date EWBValidTill;
	private String supplyType;
	private String taxScheme;
	private String cancellationReason;
	private String cancellationRemarks;
	private String revChargeFlag;
	private String supplierTradeName;
	private String supplierLegalName;
	private String supplierAddress1;
	private String supplierAddress2;
	private String supplierLocation;
	private String supplierPincode;
	private String supplierStateCode;
	private String supplierPhone;
	private String supplierEmail;
	private String customerGSTIN;
	private String customerTradeName;
	private String customerLegalName;
	private String customerAddress1;
	private String customerAddress2;
	private String customerLocation;
	private String customerPincode;
	private String customerStateCode;
	private String billingPOS;
	private String customerPhone;
	private String customerEmail;
	private String dispatcherTradeName;
	private String dispatcherAddress1;
	private String dispatcherAddress2;
	private String dispatcherLocation;
	private String dispatcherPincode;
	private String dispatcherStateCode;
	private String shipToGSTIN;
	private String shipToTradeName;
	private String shipToLegalName;
	private String shipToAddress1;
	private String shipToAddress2;
	private String shipToLocation;
	private String shipToPincode;
	private String shipToStateCode;
	private BigDecimal invoiceOtherCharges;
	private BigDecimal invoiceAssessableAmount;
	private BigDecimal invoiceIGSTAmount;
	private BigDecimal invoiceCGSTAmount;
	private BigDecimal invoiceSGSTAmount;
	private BigDecimal invoiceCessAdValoremAmount;
	private BigDecimal invoiceCessSpecificAmount;
	private BigDecimal invoiceStateCessAdValoremAmt;
	private BigDecimal invoiceStateCessSpecificAmount;
	private BigDecimal invoiceValue;
	private BigDecimal roundOff;
	private String currencyCode;
	private String countryCode;
	private BigDecimal invoiceValueFC;
	private String portCode;
	private String shippingBillNumber;
	private Date shippingBillDate;
	private String invoiceRemarks;
	private Date invoicePeriodStartDate;
	private Date invoicePeriodEndDate;
	private String payeeName;
	private String modeOfPayment;
	private String branchOrIFSCCode;
	private String paymentTerms;
	private String paymentInstruction;
	private boolean creditTransfer;
	private boolean directDebit;
	private int creditDays;
	private String accountDetail;
	private String ecomGSTIN;
	private String transporterId;
	private String transporterName;
	private String transportMode;
	private String transportDocNo;
	private Date transportDocDate;
	private double distance;
	private String vehicleNo;
	private String vehicleType;
	private boolean sec7IGSTFlag;
	private boolean claimRefundFlag;
	private BigDecimal invoiceDiscount;
	private int irnGenerationPeriod;
	private Date RIDT;
	private String payloadId;

}
