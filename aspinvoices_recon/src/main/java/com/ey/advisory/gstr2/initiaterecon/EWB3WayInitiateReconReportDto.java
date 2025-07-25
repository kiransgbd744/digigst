/**
 * 
 */
package com.ey.advisory.gstr2.initiaterecon;

import lombok.Getter;
import lombok.Setter;

/**
 * @author sakshi.jain
 *
 */

@Getter
@Setter
public class EWB3WayInitiateReconReportDto {

	private String eINV_EWB_GSTR1_Status;
	private String reconReportType;
	private String reasonForMismatch;
	private String taxPeriod_EINV;
	private String taxPeriod_EWB;
	private String taxPeriod_DigiGST_GSTR1;
	private String supplierGSTIN_EINV;
	private String supplierGSTIN_EWB;
	private String supplierGSTIN_DigiGST_GSTR1;
	private String customerGSTIN_EINV;
	private String customerGSTIN_EWB;
	private String customerGSTIN_DigiGST_GSTR1;
	private String customerName_DigiGST_GSTR1;
	private String docType_EINV;
	private String docType_EWB;
	private String docType_DigiGST_GSTR1;
	private String supplyType_EINV;
	private String supplyType_EWB;
	private String supplyType_DigiGST_GSTR1;
	private String documentNumber_EINV;
	private String documentNumber_EWB;
	private String documentNumber_DigiGST_GSTR1;
	private String documentDate_EINV;
	private String documentDate_EWB;
	private String documentDate_DigiGST_GSTR1;
	private String taxableValue_EINV;
	private String taxableValue_EWB;
	private String taxableValue_DigiGST_GSTR1;
	private String iGST_EINV;
	private String iGST_EWB;
	private String iGST_DigiGST_GSTR1;
	private String cGST_EINV;
	private String cGST_EWB;
	private String cGST_DigiGST_GSTR1;
	private String sGST_EINV;
	private String sGST_EWB;
	private String sGST_DigiGST_GSTR1;
	private String cess_EINV;
	private String cess_EWB;
	private String cess_DigiGST_GSTR1;
	private String totalTax_EINV;
	private String totalTax_EWB;
	private String totalTax_DigiGST_GSTR1;
	private String invoiceValue_EINV;
	private String invoiceValue_EWB;
	private String invoiceValue_DigiGST_GSTR1;
	private String pOS_EINV;
	private String pOS_DigiGST_GSTR1;
	private String reverseChargeFlag_EINV;
	private String reverseChargeFlag_EWB;
	private String reverseChargeFlag_DigiGST_GSTR1;
	private String portCode_EINV;
	private String portCode_EWB;
	private String portCode_DigiGST_GSTR1;
	private String shippingBillNumber_EINV;
	private String shippingBillNumber_EWB;
	private String shippingBillNumber_DigiGST_GSTR1;
	private String shippingBillDate_EINV;
	private String shippingBillDate_EWB;
	private String shippingBillDate_DigiGST_GSTR1;
	private String iRN_EINV;
	private String iRN_DigiGST_GSTR1;
	private String iRNGenDate_EINV;
	private String iRNGenDate_DigiGST_GSTR1;
	private String status_EINV;
	private String autoDraftstatus_EINV;
	private String autoDraftedDate_EINV;
	private String errorCode_EINV;
	private String errorMessage_EINV;
	private String tableType_EINV;
	private String tableType_DigiGST_GSTR1;
	private String eWBNumber_EWB;
	private String eWBNumber_DigiGST_GSTR1;
	private String eWBDate_EWB;
	private String eWBDate_DigiGST_GSTR1;
	private String eWBValidupto_EWB;
	private String eWBValidupto_DigiGST_GSTR1;
	private String eWBRejectStatus_EWB;
	private String eWBExtendedtimes_EWB;
	private String transactionType_EWB;
	private String transactionType_DigiGST_GSTR1;
	private String docCategory_EWB;
	private String docCategory_DigiGST_GSTR1;
	private String supplierTradeName_EWB;
	private String supplierTradeName_DigiGST_GSTR1;
	private String supplierAddress1_EWB;
	private String supplierAddress1_DigiGST_GSTR1;
	private String supplierAddress2_EWB;
	private String supplierAddress2_DigiGST_GSTR1;
	private String supplierLocation_EWB;
	private String supplierLocation_DigiGST_GSTR1;
	private String supplierPincode_EWB;
	private String supplierPincode_DigiGST_GSTR1;
	private String supplierStateCode_EWB;
	private String supplierStateCode_DigiGST_GSTR1;
	private String customerTradeName_EWB;
	private String customerTradeName_DigiGST_GSTR1;
	private String customerAddress1_EWB;
	private String customerAddress1_DigiGST_GSTR1;
	private String customerAddress2_EWB;
	private String customerAddress2_DigiGST_GSTR1;
	private String customerLocation_EWB;
	private String customerLocation_DigiGST_GSTR1;
	private String customerPincode_EWB;
	private String customerPincode_DigiGST_GSTR1;
	private String customerStateCode_EWB;
	private String customerStateCode_DigiGST_GSTR1;
	private String dispatcherTradeName_EWB;
	private String dispatcherTradeName_DigiGST_GSTR1;
	private String dispatcherAddress1_EWB;
	private String dispatcherAddress1_DigiGST_GSTR1;
	private String dispatcherAddress2_EWB;
	private String dispatcherAddress2_DigiGST_GSTR1;
	private String dispatcherLocation_EWB;
	private String dispatcherLocation_DigiGST_GSTR1;
	private String dispatcherPincode_EWB;
	private String dispatcherPincode_DigiGST_GSTR1;
	private String dispatcherStateCode_EWB;
	private String dispatcherStateCode_DigiGST_GSTR1;
	private String shipToTradeName_EWB;
	private String shipToTradeName_DigiGST_GSTR1;
	private String shipToAddress1_EWB;
	private String shipToAddress1_DigiGST_GSTR1;
	private String shipToAddress2_EWB;
	private String shipToAddress2_DigiGST_GSTR1;
	private String shipToLocation_EWB;
	private String shipToLocation_DigiGST_GSTR1;
	private String shipToPincode_EWB;
	private String shipToPincode_DigiGST_GSTR1;
	private String shipToStateCode_EWB;
	private String shipToStateCode_DigiGST_GSTR1;
	private String modeofTransport_EWB;
	private String modeofTransport_DigiGST_GSTR1;
	private String transporterID_EWB;
	private String transporterID_DigiGST_GSTR1;
	private String transporterName_EWB;
	private String transporterName_DigiGST_GSTR1;
	private String transportDocNo_EWB;
	private String transportDocNo_DigiGST_GSTR1;
	private String transportDocDate_EWB;
	private String transportDocDate_DigiGST_GSTR1;
	private String distance_EWB;
	private String distance_DigiGST_GSTR1;
	private String vehicleNo_EWB;
	private String vehicleNo_DigiGST_GSTR1;
	private String vehicleType_EWB;
	private String vehicleType_DigiGST_GSTR1;
	private String customerType_DigiGST_GSTR1;
	private String customerCode_DigiGST_GSTR1;
	private String accountingVoucherNumber_DigiGST_GSTR1;
	private String accountingVoucherDate_DigiGST_GSTR1;
	private String companyCode_DigiGST_GSTR1;
	private String recordStatus_DigiGST_GSTR1;
	private String e_InvoiceGetCallDate;
	private String e_InvoiceGetCallTime;
	private String eWBGetCallDate;
	private String eWBGetCallTime;
	private String processingDateofDigiGST_GSTR1;
	private String processingTimeofDigiGST_GSTR1;
	private String source_EINV;
	private String source_EWB;
	private String source_DigiGST_GSTR1;
	private String eINV_StatusatDigiGST;
	private String eWB_StatusatDigiGST;
	private String gSTR1_StatusatDigiGST;
	private String reconID;
	private String reconDate;
	private String reconTime;
}
