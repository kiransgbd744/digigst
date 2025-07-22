package com.ey.advisory.common;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class GSTConstants {

	private GSTConstants() {
	}

	/* ITC - 04 constants */
	public static final String GSTR1_POPUP_ASPUI_NILEXTNON = "GSTR1_POPUP_ASPUI_NILEXTNON";
	public static final String GSTR1_POPUP_ASPUI_HSNSAC = "GSTR1_POPUP_ASPUI_HSNSAC";
	public static final String DUP_RECORD = "DuplicateRecords";
	public static final String TABLE_NUMBER = "tableNumber";
	public static final String FY = "FY";
	public static final String DELIVERY_CHALLAN_NUMBER = "deliveryChallanNumber";
	public static final String DELIVERY_CHALLAN_DATE = "deliveryChallanDate";
	public static final String JW_DELIVERY_CHALLAN_NUMBER = "JwDeliveryChallanNumber";
	public static final String JW_DELIVERY_CHALLAN_DATE = "JwDeliveryChallanDate";
	public static final String GOODS_RECEIVE_DATE = "goodsReceivingDate";
	public static final String INVOICE_NUMBER = "invoiceNumber";
	public static final String INVOICE_DATE = "invoiceDate";
	public static final String JOB_WORKER_GSTIN = "jobWorkerGstin";
	public static final String JOB_WORKER_STATE_CODE = "jobWorkerStateCode";
	public static final String JOB_WORKER_TYPE = "jobWorkerType";
	public static final String JOB_WORKER_ID = "jobWorkerId";
	public static final String JOB_WORKER_NAME = "jobWorkerName";
	public static final String TYPES_OF_GOODS = "typesOfGoods";
	public static final String ITEM_SERIAL_NUMBER = "itemSerialNumber";
	public static final String PRODUCT_DES = "productDescription";
	public static final String LOSSESS_QNT = "lossessQuantity";
	public static final String SPECIFIC_CESS_RATE_STATE = "stateCessSpecificRate";
	public static final String SPECIFIC_CESS_AMOUNT_STATE = "stateCessSpecificAmount";
	public static final String TABLE_NUMBER_4 = "4";
	public static final String TABLE_NUMBER_5A_5B = "5A,5B";
	public static final String TABLE_NUMBER_5C = "5C";
	public static final String TABLE_NUMBER_5A = "5A";
	public static final String TABLE_NUMBER_5B = "5B";
	public static final String NATURE_OF_JW = "natureOfJw";
	public static final String NATURE_OF_SUPPLIES = "natureOfSupplies";

	public static final String FROM_PLACE = "fromPlace";
	public static final String FROM_STATE = "fromState";

	public static final String Apr_Jun = "Apr-Jun";
	public static final String Jul_Sep = "Jul-Sep";
	public static final String Oct_Dec = "Oct-Dec";
	public static final String Jan_Mar = "Jan-Mar";

	/* ITC - 04 constants */
	public static final String desriptionGstr3B = "ANYOTHERLIABILITY";
	public static final String DOC_REF_NUMBER = "DOC_REF_NUMBER";
	public static final String CUSTOMER_PAN_ADHAR = "CUSTOMER_PAN_ADHAR";
	public static final String CUSTOMER_EMAIL = "Customer Email";
	public static final String CUST_CODE = "customerCode";
	public static final String ORIG_CUST_GSTIN = "OriginalCustomerGstin";
	public static final String PRODUCT_CODE = "PRODUCT_CODE";
	public static final String ORDER_LINE_REF = "Order Line Reference";
	public static final String CATEOGRY_OF_PRODUCT = "CATEOGRY_OF_PRODUCT";
	public static final String RECEIPIENT_ADV_REF_DATE = "RECEIPIENT_ADV_REF_DATE";
	public static final String REASON_FOR_CRDR_NOTE = "REASON_FOR_CRDR_NOTE";
	public static final String TRANSCTION_TYPE = "TRANSCTION_TYPE";
	public static final String TOTAL_INV_VALUE = "TOTAL_INV_VALUE";
	public static final String TCS_AMOUNT_INCOME_TAX = "TCS_AMOUNT_INCOME_TAX";
	public static final String INVOICE_STATE_CESS_ADVALOREM_AMOUNT = "INVOICE_STATE_CESS_ADVALOREM_AMOUNT";
	public static final String TCS_RATE_INCOME = "TCS_RATE_INCOME_TAX";
	public static final String ADVVALOREM_CESS_RATE_STATE = "stateCessAdvaloremRate";
	public static final String SPE_CESS_RATE_STATE = "stateCessSpecificRt";
	public static final String IN_CESS_RATE_ADV = "cessRtAdvalorem";
	public static final String ADVVALOREM_CESS_AMOUNT_STATE = "stateCessAdvaloremAmount";
	public static final String CAN_REASON = "canReason";
	public static final String REMARK = "invRemarks";

	public static final String CAN_REMARKS = "Cancellation Remarks";
	public static final String TAX_SCHEME = "TAX_SCHEME";
	public static final String BRANCH_OR_IFSC_CODE = "BRANCH_OR_IFSC_CODE";
	public static final String ATT_NAME = "Attribute Name";
	public static final String ATT_VALUE = "Attribute Value";
	public static final String TCS_FLAG_INCOME_TAX = "TCS_FLAG_INCOME_TAX";
	public static final String SPE_CHAR = "'";
	public static final String ER15167 = "ER15167";
	public static final String ER15171 = "ER15171";
	public static final String ER1272 = "ER1272";
	public static final String ER1273 = "ER1273";
	/**
	 * E-Invoice Details of constants
	 */
	public static final Integer SIXTYFOUR_LENGTH = 64;
	public static final String IRN = "IRN";
	public static final String IRN_DATE = "IRN_DATE";
	// public static final String DOC_CATEGORY ="DOC_CATEGORY";
	public static final String SUPPLIER_TRADE_NAME = "SUPPLIER_TRADE_NAME";
	public static final String SUPPLIER_LEGAL_NAME = "supLegalName";
	public static final String SUPPLIER_LOCATION = "supLocation";
	public static final String CUST_SUP_ADDER4 = "custOrSupAddr4";
	public static final String SUPPLIER_PINCODE = "supPincode";
	// public static final String SUPPLIER_STATE_CODE ="SUPPLIER_STATE_CODE";
	public static final String SUPPLIER_PHONE = "SUPPLIER_PHONE";
	public static final String CUSTO_TRADE_NAME = "CUSTOMER_TRADE_NAME";
	public static final String CUSTOMER_LEGAL_NAME = "custOrSupName";
	public static final String CUSTOMER_LOCATION = "custOrSupAddr4";
	public static final String CUSTOMER_PINCODE = "custPincode";
	public static final String CUSTOMER_STATE_CODE = "CUSTOMER_STATE_CODE";
	public static final String CUSTOMER_PHONE = "CUSTOMER_PHONE";
	public static final String DISPATCHER_GSTIN = "DISPATCHER_GSTIN";
	public static final String DISPATCHER_TRADE_NAME = "DISPATCHER_TRADE_NAME";
	public static final String DISPATCHER_LOCATION = "dispatcherLocation";
	public static final String DISPATCHER_PINCODE = "dispatcherPincode";
	public static final String DISPATCHER_STATE_CODE = "dispatcherStateCode";
	public static final String DISPATCHER_PHONE = "DISPATCHER_PHONE";
	public static final String SHIP_TO_GSTIN = "SHIP_TO_GSTIN";
	public static final String SHIP_TO_TRADE_NAME = "SHIP_TO_TRADE_NAME";
	public static final String SHIP_TO_LOCATION = "shipToLocation";
	public static final String SHIP_TO_PINCODE = "SHIP_TO_PINCODE";
	public static final String SHIP_TO_STATE_CODE = "shipToState";
	public static final String SHIP_TO_PHONE = "SHIP_TO_PHONE";
	public static final String SERIAL_NUMBER2 = "SERIAL_NUMBER||";
	public static final String PRODUCT_NAME = "PRODUCT_NAME";
	public static final String IS_SERVICE = "isService";
	public static final String BATCH_EX_DATE = "BATCH_EXPIRY_DATE";
	public static final String WARRANTY_DATE = "WARRANTY_DATE";
	// public static final String ORIGIN_COUNTRY ="ORIGIN_COUNTRY";
	public static final String FREE_QUANTITY = "FREE_QUANTITY";
	public static final String UNIT_PRICE = "unitPrice";
	public static final String ITEM_AMOUNT = "itemAmt";
	public static final String ITEM_DISCOUNT = "ITEM_DISCOUNT";
	public static final String ITEM_OTHER_CHARGES = "ITEM_OTHER_CHARGES";
	public static final String ITEM_ASSESSABLE_AMOUNT = "ITEM_ASSESSABLE_AMOUNT";
	public static final String PRE_TAX_AMOUNT = "PRE_TAX_AMOUNT";
	public static final String INVOICE_LINE_NET_AMOUNT = "INVOICE_LINE_NET_AMOUNT";
	public static final String TOTAL_ITEM_AMOUNT = "totalItemAmt";
	// public static final String ITEM_TOTAL = "ITEM_TOTAL";
	public static final String PRE_TAX_PARTICULARS = "PRE_TAX_PARTICULARS";
	public static final String TAX_ON = "TAX_ON";
	// public static final String AMOUNT = "AMOUNT";
	public static final String INVOICE_DISCOUNT = "INVOICE_DISCOUNT";
	public static final String INVOICE_OTHER_CHARGES = "INVOICE_OTHER_CHARGES";
	public static final String INVOICE_ALLOWANCES_OR_CHARGES = "INVOICE_ALLOWANCES_OR_CHARGES";
	public static final String SUM_OF_INVOICE_LINE_NET_AMOUNT = "SUM_OF_INVOICE_LINE_NET_AMOUNT";
	public static final String SUM_OF_ALLOWANCES_ONDOCUMENT_LEVEL = "SUM_OF_ALLOWANCES_ONDOCUMENT_LEVEL";
	public static final String SUM_OF_CHARGES_ONDOCUMENT_LEVEL = "SUM_OF_CHARGES_ONDOCUMENT_LEVEL";
	public static final String FREIGHT_AMOUNT = "FREIGHT_AMOUNT";
	public static final String INSURANCE_AMOUNT = "INSURANCE_AMOUNT";
	public static final String PACKAGING_AND_FORWARDING_CHARGES = "PACKAGING_AND_FORWARDING_CHARGES";
	public static final String INVOICE_ASSESSABLE_AMOUNT = "invAssessableAmt";
	public static final String INVOICE_IGST_AMOUNT = "INVOICE_IGST_AMOUNT";
	public static final String INVOICE_CGST_AMOUNT = "INVOICE_CGST_AMOUNT";
	public static final String INVOICE_SGST_AMOUNT = "INVOICE_SGST_AMOUNT";
	public static final String INVOICE_CESS_ADVALOREM_AMOUNT = "INVOICE_CESS_ADVALOREM_AMOUNT";
	public static final String INVOICE_CESS_SPECIFIC_AMOUNT = "INVOICE_CESS_SPECIFIC_AMOUNT";
	public static final String INVOICE_STATE_CESS_AMOUNT = "INVOICE_STATE_CESS_AMOUNT";
	public static final String TAX_TOTAL = "TAX_TOTAL";
	public static final String FOREIGN_CURRENCY = "FOREIGN_CURRENCY";
	// public static final String COUNTRY_CODE = "COUNTRY_CODE";
	public static final String INVOICE_VALUEFC = "INVOICE_VALUEFC";
	public static final String INVOICE_PERIOD_STARTDATE = "INVOICE_PERIOD_STARTDATE";
	public static final String INVOICE_PERIOD_ENDDATE = "INVOICE_PERIOD_ENDDATE";
	public static final String ORIGINAL_RETURN_PERIOD = "ORIGINAL_RETURN_PERIOD";
	public static final String ORIGINAL_TAXABLE_VALUE = "ORIGINAL_TAXABLE_VALUE";
	public static final String ORIGINAL_INVOICE_VALUE = "ORIGINAL_INVOICE_VALUE";
	public static final String ORIGINAL_INV_DATE = "ORIGINAL_INV_DATE";
	public static final String ORIGINAL_INV_NUMBER = "ORIGINAL_INV_NUMBER";
	public static final String PRECEEDING_INV_DATE = "preceedingInvDate";
	public static final String PRECEEDING_INV_NUMBER = "preceedingInvNo";
	public static final String RECEIPT_ADVICE_REFERENCE = "RECEIPT_ADVICE_REFERENCE";
	public static final String TENDER_REFERENCE = "TENDER_REFERENCE";
	public static final String CONTRACT_REFERENCE = "CONTRACT_REFERENCE";
	public static final String EXTERNAL_REFERENCE = "EXTERNAL_REFERENCE";
	public static final String PROJECT_REFERENCE = "PROJECT_REFERENCE";
	public static final String CUSTOMER_PO_REFERENCE_NUMBER = "CUSTOMER_PO_REFERENCE_NUMBER";
	public static final String CUSTOMER_PO_REFERENCE_DATE = "CUSTOMER_PO_REFERENCE_DATE";
	public static final String MODE_OF_PAYMENT = "modeOfPayment";
	public static final String CREDIT_DAYS = "CREDIT_DAYS";
	public static final String PAID_AMOUNT = "PAID_AMOUNT";
	public static final String BALANCE_AMOUNT = "BALANCE_AMOUNT";
	public static final String PAYMENT_DUE_DATE = "PAYMENT_DUE_DATE";
	public static final String ACCOUNT_DETAIL = "ACCOUNT_DETAIL";
	public static final String ACCOUNTING_VOCHAR_NUM = "accountingVocharNumber";
	public static final String ECOM_TRANSACTION = "ECOM_TRANSACTION";
	public static final String ECOM_POS = "ECOM_POS";
	public static final String TDS_FLAG = "TDS_FLAG";
	// public static final String SUB_SUPPLY_TYPE = "SUB_SUPPLY_TYPE";
	public static final String TRANS_PORTER_ID = "transporterID";
	public static final String TRANS_PORT_MODE = "transportMode";
	public static final String TRANS_PORT_DOCNO = "transportDocNo";
	public static final String TRANS_PORT_DOCDATE = "transportDocDate";
	public static final String DISTANCE = "distance";
	public static final String VEHICLE_NO = "vehicleNo";
	// public static final String VEHICLE_TYPE = "VEHICLE_TYPE";
	public static final String EXCHANGE_RATE = "exchangeRt";
	public static final String TCS_CGST_AMOUNT = "TCS_CGST_AMOUNT";
	public static final String TCS_SGST_AMOUNT = "TCS_SGST_AMOUNT";
	public static final String TCS_IGST_AMOUNT = "TCS_IGST_AMOUNT";
	public static final String TDS_IGST_AMOUNT = "TDS_IGST_AMOUNT";
	public static final String TDS_CGST_AMOUNT = "TDS_CGST_AMOUNT";
	public static final String TDS_SGST_AMOUNT = "TDS_SGST_AMOUNT";
	public static final String GL_POSTING_DATE = "GL_POSTING_DATE";
	public static final String CUSTOMER_TAN = "CUSTOMER_TAN";
	public static final String COMMON_SUPPLY_INDICATOR = "COMMON_SUPPLY_INDICATOR";
	public static final String ROUND_OFF = "roundOff";

	/*
	 * Annexure1 files uploads
	 */
	public static final String ANN1_FOLDER_NAME = "Annexure1Webuploads";
	public static final String ANN1_B2C_FILE_NAME = "ANN1_B2C_FILE_NAME";
	public static final String ANN1_TABLE4_FILE_NAME = "ANN1_TABLE4_FILE_NAME";
	public static final String B2C = "B2C";
	public static final String TABLE4 = "TABLE4";
	public static final String SHIPPINGBILL = "SHIPPINGBILL";
	public static final String ANN1_SHIPPING_BILL_FILE_NAME = "ANN1_SHIPPING_BILL_FILE_NAME";
	public static final String TABLE3H3I = "TABLE3H3I";
	public static final String STRUCTURAL_VALIDATIONS = "SV";
	public static final String BUSINESS_VALIDATIONS = "BV";
	public static final String OUTWARD = "OUTWARD";
	public static final String INWARD = "INWARD";
	public static final String SYSTEM = "SYSTEM";
	public static final String CROSS_ITC = "CROSS_ITC";

	/*
	 * Master Uploads Details
	 */
	public static final String MASTER = "MASTER";
	public static final String CUSTOMER = "CUSTOMER";
	public static final String MASTER_FOLDER_NAME = "MasterUploads";
	public static final String MASTER_CUSTOMER_FILE_NAME = "MASTER_CUSTOMER_FILE_NAME";
	public static final String PRODUCT = "PRODUCT";
	public static final String PRODUCT_FOLDER_NAME = "ProductUploads";
	public static final String MASTER_PRODUCT_FILE_NAME = "MASTER_PRODUCT_FILE_NAME";
	public static final String VENDOR_FOLDER_NAME = "vendorWebUpload";
	public static final String VENDOR_RAW_FILE_NAME = "VENDOR_RAW_FILE_NAME";
	public static final String VENDOR = "VENDOR";
	public static final String ITEM = "ITEM";
	public static final String ITEM_FOLDER_NAME = "ItemUploads";
	public static final String MASTER_ITEM_FILE_NAME = "MASTER_ITEM_FILE_NAME";
	public static final String SLASH = "-";
	public static final List<String> OUT_WARDS_RET_TABLE = ImmutableList
			.of("3A8", "3C3", "3C4", "3C5", "3D1", "3D2", "3D3", "3D4");
	public static final List<String> IN_WARDS_RET_TABLE = ImmutableList.of(
			"4A4", "4A10", "4A11", "4B2", "4B3", "4B4", "4B5", "4E1", "4E2");

	// GSTR6 Distribution Upload

	public static final String GSTR6 = "GSTR6";
	public static final String GSTR6ISDANNEX = "GSTR6";
	public static final String DISTRIBUTION = "DISTRIBUTION";
	public static final String GSTR6_FOLDER_NAME = "GSTR6Uploads";
	public static final String GSTR6_RAW_FILE_NAME = "GSTR6_RAW_FILE_NAME";
	// GSTR2a Web Upload
	public static final String GSTR2A = "GSTR2A";
	public static final String GSTR2 = "GSTR2";
	// Gstr8 File Upload
	public static final String GSTR8_FOLDER_NAME = "GSTR8Uploads";
	public static final String GSTR8 = "GSTR8";

	// Common Credit Reversal
	public static final String CREDIT_REVERSAL_FOLDER_NAME = "CreditReversalFileUploads";

	// RET - 1 AND 1A WEB Uploads
	public static final String RETURN_TABLE = "RETURN_TABLE";
	public static final String WEB_UPLOAD_KEY = "|";

	/**
	 * RET Vertical uploads
	 */
	public static final String RET = "RET";
	public static final String RET_1 = "RET-1";
	public static final String RET_1A = "RET-1A";
	public static final String RET_FOLDER_NAME = "RetWebuploads";
	public static final String RET_FILE_NAME = "RET_FILE_NAME";
	public static final String RET1AND1A = "RET1AND1A";
	public static final String INTEREST_FILE_NAME = "INTEREST_FILE_NAME";
	public static final String INTEREST = "INTEREST";
	public static final String INTEREST_IGST = "INTEREST_IGST";
	public static final String INTEREST_CGST = "INTEREST_CGST";
	public static final String INTEREST_SGST = "INTEREST_SGST";
	public static final String INTEREST_CESS = "INTEREST_CESS";
	public static final String LATEFEE_SGST = "LATEFEE_SGST";
	public static final String LATEFEE_CGST = "LATEFEE_CGST";
	public static final String REFUNDS_FILE_NAME = "REFUNDS_FILE_NAME";
	public static final String REFUNDS = "REFUNDS";
	public static final String SETOFFANDUTIL_FILE_NAME = "SETOFFANDUTIL_FILE_NAME";
	public static final String SETOFFANDUTIL = "SETOFFANDUTIL";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String PENALTY = "PENALTY";
	public static final String FEE = "FEE";
	public static final String VALUE = "VALUE";
	public static final String TAX_PAYABLE_REV_CHARGE = "TAX_PAYABLE_REV_CHARGE";
	public static final String TAX_PAYABLE_OTH_REV_CHARGE = "TAX_PAYABLE_OTH_REV_CHARGE";
	public static final String TAX_ALREADY_PAID_REV_CHARGE = "TAX_ALREADY_PAID_REV_CHARGE";
	public static final String TAX_ALREADY_OTH_PAID_REV_CHARGE = "TAX_ALREADY_OTH_PAID_REV_CHARGE";
	public static final String ADJ_REV_CHARGE = "ADJ_REV_CHARGE";
	public static final String ADJ_REV_OTH_CHARGE = "ADJ_REV_OTH_CHARGE";
	public static final String PAID_THROUGH_ITC_IGST = "PAID_THROUGH_ITC_IGST";
	public static final String PAID_THROUGH_ITC_CGST = "PAID_THROUGH_ITC_CGST";
	public static final String PAID_THROUGH_ITC_SGST = "PAID_THROUGH_ITC_SGST";
	public static final String PAID_THROUGH_ITC_CESS = "PAID_THROUGH_ITC_CESS";
	public static final String PAID_IN_CASH_INTEREST = "PAID_IN_CASH_INTEREST";
	public static final String PAID_IN_CASH_CESS = "PAID_IN_CASH_CESS";
	public static final String PAID_IN_CASH_LATE_FEE = "PAID_IN_CASH_LATE_FEE";
	public static final String INTEREST_LATEFEE = "INTEREST_LATEFEE";
	public static final String LATE_FEE = "LATE_FEE";
	public static final String USERINPUTS = "USERINPUTS";

	/**
	 * For Anx1 Data type Inward File uploads
	 */

	public static final String TABLE3H3I_FOLDER_NAME = "Anx1WebUploads";
	public static final String TABLE3H3I_FILE_NAME = "TABLE3H3I_FILE_NAME";

	/*
	 * Annexure2 files uploads
	 */
	public static final String ANN2_FOLDER_NAME = "Annexure2Webuploads";
	public static final String ANN2_RAW_FILE_NAME = "ANN2_RAW_FILE_NAME";
	public static final String ANN2_TABLE4_FILE_NAME = "ANN2_TABLE4_FILE_NAME";
	public static final String GET_ANX2_FOLDER_NAME = "GetAnnexure2Fileuploads";
	public static final String GET_ANX2_FILE_NAME = "GET_ANX2_FILE_NAME";
	public static final String GET_ANX2 = "GET_ANX2";
	public static final String COMPREHENSIVE_INWARD_RAW = "COMPREHENSIVE_INWARD_RAW";

	/* For file uploads */
	public static final String FOLDER_NAME = "Gstr1WebUploads";
	public static final String OLD_NIL_FILE_NAME = "OLD_NIL_FILE_NAME";
	public static final String GSTR_3B_FOLDER_NAME = "Gstr3BWebUploads";
	public static final String CEWB_FOLDER_NAME = "CewbWebUploads";
	public static final String ITC04_FOLDER_NAME = "Itc04bWebUploads";

	public static final String RAW_FILE_NAME = "RAW_FILE_NAME";
	public static final String B2CS_FILE_NAME = "B2CS_FILE_NAME";
	public static final String AT_FILE_NAME = "AT_FILE_NAME";
	public static final String ATA_FILE_NAME = "ATA_FILE_NAME";
	public static final String INVOICE_FILE_NAME = "INVOICE_FILE_NAME";
	public static final String NIL_FILE_NAME = "NIL_FILE_NAME";
	public static final String HSN_FILE_NAME = "HSN_FILE_NAME";
	public static final String B2CS_FILE_NAME_1A = "B2CS_FILE_NAME_1A";
	public static final String AT_FILE_NAME_1A = "AT_FILE_NAME_1A";
	public static final String ATA_FILE_NAME_1A = "ATA_FILE_NAME_1A";
	public static final String INVOICE_FILE_NAME_1A = "INVOICE_FILE_NAME_1A";
	public static final String NIL_FILE_NAME_1A = "NIL_FILE_NAME_1A";
	public static final String HSN_FILE_NAME_1A = "HSN_FILE_NAME_1A";
	public static final String GSTIN_VALIDATOR_UPLOAD_FOLDER = "GstinValidatorFileUpload";
	public static final String GSTR3B_FILE_NAME = "GSTR3B_FILE_NAME";
	public static final String CEWB_FILE_NAME = "CEWB_FILE_NAME";
	public static final String EINVOICE_RECON_FOLDER = "EinvoiceReconWebUploads";
	public static final String EINVOICE_RECON_FILE_NAME = "EINVOICE_RECON_FILE_NAME";
	public static final String GSTR2A_USERRESPONSE_UPLOADS = "Gstr2UserResponseUploads";
	public static final String GSTR2A_USERRESPONSE_FILE_NAME = "Gstr2UserResponse";
	public static final String GSTR2A_SFTP_USERRESPONSE_FILE = "Gstr2aSftpUserResponse";
	public static final String GSTR2A_SFTP_FOLDER = "Gstr2aSftpUploads";
	public static final String GSTR2A_ERP_USERRESPONSE_FILE_NAME = "Gstr2ERPUserResponse";
	/*
	 * Anand3.M For GSTR2 File Upload
	 */

	public static final String GSTR2_FOLDER_NAME = "Gstr2WebUploads";
	public static final String GSTR2_RAW_FILE_NAME = "GSTR2_RAW_FILE_NAME";
	public static final String GSTR2_B2CS_FILE_NAME = "GSTR2_B2CS_FILE_NAME";
	public static final String GSTR2_AT_FILE_NAME = "GSTR2_AT_FILE_NAME";
	public static final String GSTR2_ATA_FILE_NAME = "GSTR2_ATA_FILE_NAME";
	public static final String GSTR2_INVOICE_FILE_NAME = "GSTR2_INVOICE_FILE_NAME";
	public static final String GSTR2_NIL_FILE_NAME = "GSTR2_NIL_FILE_NAME";

	/*
	 * Anand3.M For GSTR2A File Upload
	 */
	public static final String GSTR2A_FOLDER_NAME = "Gstr2aWebUploads";
	public static final String GSTR2A_B2B_FILE_NAME = "GSTR2A_B2B_FILE_NAME";
	public static final String GSTR2A_B2B = "GSTR2A_B2B";
	public static final String GSTR2A_B2BA_FILE_NAME = "GSTR2A_B2BA_FILE_NAME";
	public static final String GSTR2A_B2BA = "GSTR2A_B2BA";
	
	public static final String GSTR8A_FOLDER_NAME = "Gstr8aWebUploads";
	public static final String GSTR8A_FILE_NAME = "GSTR8A_FILE_NAME";
	public static final String GSTR8A = "GSTR8A";

	public static final String GSTR6A_FOLDER_NAME = "Gstr6aWebUploads";
	public static final String GSTR6A_FILE_NAME = "GSTR6A_FILE_NAME";
	public static final String GSTR6A = "GSTR6A";

	/*
	 * Anand3.M For GSTR1 File Upload
	 */
	public static final String GSTR1_FOLDER_NAME = "Gstr1WebUploads";
	public static final String GSTR1_FILE_NAME = "GSTR1_FILE_NAME";
	public static final String EINV = "EINV";

	/*
	 * Anand3.M For GSTR7 File Upload
	 */
	public static final String GSTR7_FOLDER_NAME = "Gstr7WebUploads";
	public static final String GSTR7_FILE_NAME = "GSTR7_FILE_NAME";
	public static final String GSTR7_TRANS_FILE_NAME = "GSTR7_FILE_NAME";
	public static final String GSTR7_TRANSACTIONAL = "GSTR7_TRANSACTIONAL";
	public static final String GSTR7_TDS = "GSTR7_TDS";
	public static final String OTHERS = "OTHERS";

	/*
	 * Anand3.M For GSTR9 Hsn File Upload
	 */
	public static final String GSTR9 = "GSTR9";
	public static final String GSTR9_FOLDER_NAME_INOUT = "Gstr9InwardOutwardWebuploads";
	public static final String INWARD_OUTWARD = "GSTR9INWARDOUTWARD";
	public static final String GSTR9_FOLDER_NAME = "Gstr9HsnWebUploads";
	public static final String GSTR9_FILE_NAME = "GSTR9_FILE_NAME";
	public static final String GSTR9_HSN = "GSTR9_HSN";

	public static final String SupplyType = "supplyType";
	public static final String SERIAL_NUMBER = "serialNumber";
	public static final String RECORD_TYPE = "recordType";
	public static final String TOTAL_NUM = "totalNumber";
	public static final String NET_NUM = "netNumber";
	public static final String RAW = "RAW";
	public static final String COMPREHENSIVE_RAW = "COMPREHENSIVE_RAW";
	public static final String EXT = "EXT";
	public static final String HSN = "hsnsacCode";
	public static final String HSNUPLOAD = "VerticalUploadHSN";
	public static final String GSTR3B = "GSTR3B";
	public static final String GSTR3B_ITC_4243 = "GSTR3B_ITC_4243";
	public static final String CEWB = "CEWB";
	public static final String EWB = "EWB";
	public static final String ITC04 = "ITC04";
	public static final String CEWB_FILE = "CEWB";
	public static final String ITC04_FILE = "ITC04_FILE";
	public static final String ITC04_FILE_NAME = "ITC04_FILE_NAME";
	public static final String INVOICE = "INVOICE";
	public static final String AT = "ADVANCE RECEIVED";
	public static final String TXPD = "ADVANCE ADJUSTMENT";
	public static final String NILNONEXMPT = "NILNONEXMPT";
	public static final String DOC_SERIES = "DOCSERIES";
	public static final String B2CS = "B2CS";
	public static final String GSTR1 = "GSTR1";
	public static final String GSTR1A = "GSTR1A";
	public static final String GSTR1_SALES_REGISTER_UPLOAD = "SalesRegisterUpload";
	public static final String EINVOICE = "EINVOICE";
	public static final String EINVOICEFILEUPLOAD = "EINVOICEFILEUPLOAD";
	public static final String GSTR1AEINVOICEFILEUPLOAD = "GSTR1AEINVOICEFILEUPLOAD";
	public static final String ALL = "ALL";
	public static final String ORGUOM = "ORG_UOM";
	public static final String EINVOICE_RECON = "EINVOICE_RECON";
	public static final String DELETE_GSTN = "DELETE_GSTN";
	public static final String EXCLUSIVE_SAVE_FILE = "EXCLUSIVE_SAVE_FILE";

	public static final String NIL_SUPPLY_TYPE = "NIL";
	public static final String NON_SUPPLY_TYPE = "NON";
	public static final String EXMPT_SUPPLY_TYPE = "EXT";

	public static final String NIL_INTERSTATE_REG = "nilInterStateReg";
	public static final String NIL_INTRASTATE_REG = "nilIntraStateReg";
	public static final String NIL_INTERSTATE_UN_REG = "nilInterStateUnReg";
	public static final String NIL_INTRASTATE_UN_REG = "nilIntraStateUnReg";

	public static final String NON_INTERSTATE_REG = "nonInterStateReg";
	public static final String NON_INTRASTATE_REG = "nonIntraStateReg";
	public static final String NON_INTERSTATE_UN_REG = "nonInterStateUnReg";
	public static final String NON_INTRASTATE_UN_REG = "nonIntraStateUnReg";

	public static final String EXT_INTERSTATE_REG = "exmtInterStateReg";
	public static final String EXT_INTRASTATE_REG = "exmtIntraStateReg";
	public static final String EXT_INTERSTATE_UN_REG = "exmtInterStateUnReg";
	public static final String EXT_INTRASTATE_UN_REG = "exmtIntraStateUnReg";

	public static final String GSTIN_ON_BOARD_FILE_NAME = "GSTIN_ON_BOARD_FILE_NAME";
	public static final String GSTIN_ONBOARD_FOLDER_NAME = "GstinOnboardWebUploads";
	public static final String RETURN_FILING_FILE_NAME = "ReturnFilingFileUpload";
	/* For file uploads */

	/* Locations where the validations are performed */
	public static final String APP_VALIDATION = "APP";
	public static final String GSTN_VALIDATION = "GSTN";

	public static final String IM = "I";
	public static final String UQC = "itemUqc";
	public static final String CONCESSIONAL = "ConcessionalRateFlag";
	public static final String ExportDuty = "exportDuty";
	public static final String FOB = "fob";
	public static final String PRE_GST = "crDrPreGst";
	public static final String NatureOfDocument = "NatureOfDocument";
	public static final String GSTR_2X_GET_RECORDS = "Gstr2xGetRecordNotAvailable";
	public static final String RETURN_PREIOD = "returnPeriod";
	public static final String RETURN_TYPE = "returnType";
	public static final String QUANTITY = "itemQty";
	public static final String NEW_QUANTITY = "newquantity";
	public static final String UOM = "unitOfMeasure";
	public static final String NEW_UOM = "newunitOfMeasure";
	public static final String RFC = "RFV";
	public static final String SUPPLY_TYPE = "supplyType";
	public static final String LINE_NO = "itemNo";
	public static final String ARFC = "ARFV";
	public static final String INVOICE_VALUE = "lineItemAmt";
	public static final String PORT_CODE = "portCode";
	public static final String SUB_DIVISION = "subDivision";
	public static final String DIVISION = "division";
	public static final String IGST_AMOUNT = "igstAmt";
	public static final String CGST_AMOUNT = "cgstAmt";
	public static final String SGST_AMOUNT = "sgstAmt";
	public static final String IGST_RATE = "igstRt";
	public static final String CGST_RATE = "cgstRt";
	public static final String SGST_RATE = "sgstRt";
	public static final String CESS_AMT_ADV = "cessAmtAdvalorem";
	public static final String CESS_RATE_ADV = "cessRateAdvalorem";
	public static final String CESS_AMT_SPECIFIC = "cessAmtSpecfic";
	public static final String CESS_RATE_SPECIFIC = "cessRateSpecific";
	public static final String DOC_DATE = "docDate";
	public static final String DOC_NO = "docNo";
	public static final String TAX_RATE = "TAX_RATE";
	public static final String TAX_AMOUNT = "taxableVal";
	public static final String TOTAL_VALUE = "TOTAL_VALUE";
	public static final String TOTAL = "TotalQuantity";
	public static final String DOC_TYPE = "docType";
	public static final String CATEGORY = "Category";
	public static final String ORGDOC_TYPE = "originalDocType";
	public static final String SEC7_IGST_FLAG = "Section7ofIGSTFlag";
	public static final String AUTO_POP_REFUND = "AutoPopulateToRefund";
	public static final String ORIGINAL_DOC_DATE = "originalDocDate";
	public static final String DIFF_PER_FLAG = "diffPercent";
	public static final String SHIPPING_BILL_NO = "shippingBillNo";
	public static final String SHIPPING_BILL_DATE = "shippingBillDate";
	public static final String POS = "pos";
	public static final String ZERO = "0";
	public static final String FROM = "From";
	public static final String TO = "to";
	public static final String HSNORSAC = "hsnsacCode";
	public static final String NET_SUPPLIES = "netSupplies";
	public static final String TaxableValue = "taxableVal";
	public static final String ITCReversalIdentifier = "itcReversalIdentifier";
	public static final String commomSupplyIndicator = "commonSupplyIndicator";
	public static final String igst = "igst";
	public static final String cgst = "cgst";
	public static final String sgst = "sgst";
	public static final String cess = "cess";
	public static final String igstamount = "igst";
	public static final String CIF_VALUE = "cifvalue";
	public static final String CustomDuty = "CustomDuty";
	public static final String cgstamount = "cgst";
	public static final String Availableigstamount = "availableIgst";
	public static final String AvailableCgstamount = "availableCgst";
	public static final String AvailableSgstamount = "availableSgst";
	public static final String AvailableCess = "availableCess";
	public static final String sgstamount = "sgst";
	public static final String eligibiltIndicator = "eligibilityIndicator";
	public static final String RecipientGSTIN = "custGstin";
	public static final String RECIPIENTGSTNORPAN = "RECIPIENTGSTNORPAN";
	public static final String GSTINORPAN = "GSTINORPAN";
	public static final String LEGALNAME = "LEGALNAME";
	public static final String TRADENAME = "TRADENAME";
	public static final String EMAILID = "EMAILID";
	public static final String MOBILENUMBER = "MOBILENUMBER";
	public static final String RECIPIENTCODE = "RECIPIENTCODE";
	public static final String RECIPIENTTYPE = "custOrSupType";
	public static final String OUTSIDEINDIA = "OUTSIDEINDIA";

	public static final String IGST_USED_AS_IGST = "igstUsedAsIgst";
	public static final String SGST_USED_AS_IGST = "sgstUsedAsIgst";
	public static final String CGST_USED_AS_IGST = "cgstUsedAsIgst";
	public static final String SGST_USED_AS_SGST = "sgstUsedAsSgst";

	public static final String IGST_USED_AS_SGST = "igstUsedAsSgst";
	public static final String CGST_USED_AS_CGST = "cgstUsedAsCgst";
	public static final String IGST_USED_AS_CGST = "igstUsedAsCgst";
	public static final String CESS_USED_AS_CESS = "cessUsedAsCess";

	public static final String ERROR = "ERROR";
	public static final String INFO = "INFO";

	public static final String DifferentialPercentageFlag = "diffPercent";
	public static final String PercentageOfEligibility = "PercentageOfEligibility";
	public static final String ADJREFDATE = "adjustmentRefDate";
	public static final String ACCVOCHDATE = "accountVoucherDate";
	public static final String OTHERVALUE = "otherValues";
	public static final String ADJREFNO = "adjustmentRefNo";
	public static final String NOTNO = "notificationNumber";
	public static final String NOTDATE = "notificationDate";
	public static final String CIRCULARDATE = "circularDate";
	public static final String EWay_BillDate = "eWayBillDate";
	public static final String EWay_BillNo = "eWayBillNo";
	public static final String TaxableValueAdjusted = "adjustedTaxableValue";
	public static final String AUTOPOPULATED = "autoPopToRefundFlag";
	public static final String CLAIMREFUNDFLAG = "claimRefundFlag";
	public static final String SECTION7OFIGSTFLAG = "sec7OfIgstFlag";
	public static final String STATEAPPLYINGCESS = "stateApplyingCess";
	public static final String CESS_AMT_STATE = "stateCessAmt";
	public static final String CESS_RATE_STATE = "stateCessRate";
	public static final String STATE_CESS_RATE = "stateCessRt";
	public static final String STATE_CESS_SPECIFIC_AMOUNT = "stateCessSpecificAmt";
	public static final String StateCessAmountAdjusted = "adjustedCessAmt";
	public static final String SpecificCessAmountAdjusted = "adjustedCessAmtSpecific";
	public static final String AdvaloremCessAmountAdjusted = "adjustedCessAmtAdvalorem";
	public static final String StateUTTaxAmountAdjusted = "adjustedSgstAmt";
	public static final String IntegratedTaxAmountAdjusted = "adjustedIgstAmt";
	public static final String CentralTaxAmountAdjusted = "adjustedCgstAmt";

	public static final String FYYEAR = "-";
	public static final String UINorComposition = "uinOrComposition";
	public static final String ORG_CGSTN = "orgCustGstin";
	public static final String ORIGINAL_DOC_NO = "originalDocNo";
	public static final String CGSTN = "custGstin";
	public static final String RSLF = "RSLF";
	public static final String SLF = "SLF";
	public static final String INA = "INA";
	public static final String TAX = "TAX";
	public static final String UNR = "UNR";
	public static final String IMP = "IMP";
	public static final String CR = "CR";
	public static final String RCR = "RCR";
	public static final String DR = "DR";
	public static final String COM = "COM";
	public static final String ISD = "ISD";
	public static final String ISDA = "ISDA";
	public static final String REGULAR = "REGULAR";
	public static final String TCS = "TCS";
	public static final String TCSA = "TCSA";
	public static final String URD = "URD";
	public static final String URDA = "URDA";
	public static final String ADG = "ADG";
	public static final String ADV = "ADV";
	public static final String ADJ = "ADJ";
	public static final String AT_STR = "AT";
	public static final String ATA_STR = "ADJ";
	public static final String TXP_STR = "TXP";
	public static final String TXPA_STR = "TXPA";
	public static final String RADG = "RADG";
	public static final String ADS = "ADS";
	public static final String RADS = "RADS";
	public static final String NA = "NA";
	public static final String SEZG = "SEZG";
	public static final String SEZS = "SEZS";
	public static final String DTA = "DTA";
	public static final String EXP = "EXP";
	public static final String DXP = "DXP";
	public static final String RFV = "RFV";
	public static final String RRFV = "RRFV";
	public static final String ARFV = "ARFV";
	public static final String RANV = "RANV";
	public static final String PV = "PV";
	public static final String AV = "AV";
	public static final String DLC = "DLC";
	public static final String NYS = "NYS";
	public static final String IMPS = "IMPS";
	public static final String RC = "RC";
	public static final String IMPG = "IMPG";
	public static final String CAN = "can";
	public static final String N = "N";
	public static final String Y = "Y";
	public static final String L65 = "L65";
	public static final String BillToState = "billToState";
	public static final String ShipToState = "shipToState";
	public static final String ORG_GSTIN = "origCgstin";
	public static final String CGSTIN = "custGstin";
	public static final String GSTIN = "gstin";
	public static final String RET_PERIOD = "returnPeriod";
	public static final String SGSTIN = "suppGstin";
	public static final String TAXPAYER_GSTIN = "TaxpayerGSTIN";
	public static final String SUP_ECOM = "Supecom";
	public static final String ECOM_SUP = "Ecomsup";
	public static final String GSTR1_B2B = "B2B";
	public static final String GSTR1_B2BA = "B2BA";
	public static final String GSTR1_B2CL = "B2CL";
	public static final String GSTR1_B2CLA = "B2CLA";
	public static final String GSTR1_CDNRA = "CDNRA";
	public static final String GSTR1_CDNURA = "CDNURA";
	public static final String GSTR1_EXP = "EXPORTS";
	public static final String GSTR1_EXPA = "EXPORTS-A";
	public static final String GSTR1_B2CS = "B2CS";
	public static final String GSTR1_B2CSA = "B2CSA";
	public static final String GSTR1_NIL = "NIL";
	// public static final String NIL_B2B = "NIL_B2B";
	public static final String NIL_EXT_NON = "NILEXTNON";
	public final static String CDNA = "CDNA";
	public final static String CDNURA = "CDNURA";
	public final static String CDNRA = "CDNRA";
	public final static String CDNUR = "CDNUR";
	public final static String CDNR = "CDNR";
	public final static String ECOM = "ECOM";
	public final static String ECOMA = "ECOMA";
	public final static String CDNUR_EXPORTS = "CDNUR-EXPORTS";
	public final static String CDNUR_B2CL = "CDNUR-B2CL";
	public final static String CDNUR_EXPT = "CDNUR_EXPT";
	public final static String CDNUR_EXPWT = "CDNUR_EXPWT";
	public final static String NewHsnOrSac = "NewHsnOrSac";
	public final static String newPOS = "newPOS";
	public final static String MONTH = "MONTH";
	public final static String OrgPOS = "OrgPOS";
	public final static String OrgHsnOrSac = "OrgHsnOrSac";
	public final static String ORGTAXABLE_VALUE = "ORGTAXABLE_VALUE";
	public final static String NEW_ORGTAXABLE_VALUE = "NEW_TAXABLE_VALUE";
	public final static String ORG_GROSS_ADVANCE_RECEIVED = "OrgGrossAdvanceReceived";
	public final static String NEW_GROSS_ADVANCE_RECEIVED = "NewGrossAdvanceReceived";
	public final static String ORG_GROSS_ADVANCE_ADJUSTMENT = "OrgGrossAdvanceAdjustment";
	public final static String NEW_GROSS_ADVANCE_ADJUSTMENT = "NewGrossAdvanceAdjustment";
	public final static String ORGECOM_SUP_VALUE = "ORGECOM_SUP_VALUE";
	public final static String NEWECOM_SUP_VALUE = "NEWECOM_SUP_VALUE";
	public final static String BillOfEntry = "billOfEntryNo";
	public final static String OrgRate = "OrgRate";
	public final static String NO = "NO";
	public final static String OrgSgstin = "OrgSgstin";
	public final static String OrgE_ComGstin = "OrgE_ComGstin";
	public final static String NEWE_COMGSTIN = "NEWE_COMGSTIN";
	public final static String TransactionType = "TransactionType";
	public static final String GSTR1_4A = "4A";
	public static final String GSTR1_4B = "4B";
	public static final String GSTR1_4C = "4C";
	public static final String GSTR1_5A = "5A";
	public static final String GSTR1_5B = "5B";
	public static final String GSTR1_6A = "6A";
	public static final String GSTR1_6B = "6B";
	public static final String GSTR1_6C = "6C";
	public static final String GSTR1_7A1 = "7A(1)";
	public static final String GSTR1_7A2 = "7A(2)";
	public static final String GSTR1_7B1 = "7B(1)";
	public static final String GSTR1_7B2 = "7B(2)";
	public static final String GSTR1_8A = "8A";
	public static final String GSTR1_8B = "8B";
	public static final String GSTR1_8C = "8C";
	public static final String GSTR1_8D = "8D";
	public static final String GSTR1_9A = "9A";
	public static final String GSTR1_9B = "9B";
	public static final String GSTR1_9C = "9C";
	public static final String GSTR1_10A = "10A";
	public static final String GSTR1_10B = "10B";
	public static final String GSTR1_14 = "14";
	public static final String GSTR1_14I = "14(i)";
	public static final String GSTR1_14II = "14(ii)";
	public static final String GSTR1_14A = "14A";
	public static final String GSTR1_14AI = "14A(i)";
	public static final String GSTR1_14AII = "14A(ii)";
	public static final String GSTR1_15 = "15";
	public static final String GSTR1_15I = "15(i)";
	public static final String GSTR1_15II = "15(ii)";
	public static final String GSTR1_15III = "15(iii)";
	public static final String GSTR1_15IV = "15(iv)";
	public static final String GSTR1_15AI = "15A(i)";
	public static final String GSTR1_15AIA = "15A.1.a";
	public static final String GSTR1_15AIB = "15A.1.b";
	public static final String GSTR1_15AII = "15A(ii)";
	public static final String GSTR1_15AIIA = "15A.2.a";
	public static final String GSTR1_15AIIB = "15A.2.b";
	public static final String COMMON_CREDIT_REVERSAL = "COMMON_CREDIT_REVERSAL";

	public static final String I_RC = "I";
	public static final String GSTR2_7B = "7B";
	public static final String GSTR2_7A = "7A";
	public final static String GSTR2_CDN = "CDN";
	public final static String GSTR2_CDNA = "CDNA";
	public final static String GSTR2_IMPG = "IMPG";
	public final static String GSTR2_B2BA = "B2BA";
	public final static String GSTR2_SEZGA = "SEZGA";
	public final static String GSTR2_IMPGA = "IMPGA";
	public final static String GSTR2_SEZSA = "GSTR2_SEZSA";
	public final static String GSTR2_B2BURA = "B2BURA";
	public final static String GSTR2_B2BUR = "B2BUR";
	public final static String GSTR2_IMPS = "IMPS";
	public final static String GSTR2_RCM = "RCM";
	public final static String GSTR2_B2B = "B2B";
	public final static String GSTR2_SEZG = "SEZG";
	public final static String GSTR2_CDNRA = "CDNRA";
	public final static String GSTR2_CDNURA = "CDNURA";
	public final static String GSTR2_IMPSA = "IMPSA";
	public final static String GSTR2_5B = "5B";
	public final static String GSTR2_4A = "4A";
	public final static String GSTR2_4B = "4B";
	public final static String GSTR2_4C = "4C";
	public final static String GSTR2_5A = "5A";
	public final static String GSTR2_6D = "6D";
	public static final String GSTR2_6C = "6C";
	public static final String GSTR2_6A = "6A";
	public static final String GSTR2_6B = "6B";
	public static final String GSTR2_3 = "3";

	public static final String SERVICES_CODE = "99";
	public static final String POS25 = "25";

	public static final String L = "L";
	public static final String UL = "UL";
	public static final String CL = "CL";
	public static final String U = "U";
	public static final String C = "C";

	public static final double ZERO_DOUBLE = 0.0;
	public static final String E_ComGstin = "ecomGSTIN";
	public static final String HSNSAC = "99";
	public static final String STATE_UTTAX_AMOUNT = "StateUttaxAmount";
	public static final String CESS_AMT_ADVALOREM = "cessAmtAdvalorem";
	public static final String ReverseCharge = "reverseCharge";
	public static final String TAXABLE_VALUE = "taxableVal";
	public static final String ORG_INVOICE_VALUE = "invoiceVal";
	public static final String TCSFlag = "tcsFlag";
	public static final String TDSFlag = "tdsFlag";
	public static final String TCSAMT = "tcsAmount";
	public static final String ItcFlag = "itcFlag";
	public static final String VocherDate = "VocherDate";
	public static final String ReasonForCDebitNote = "reasonForCrDrNote";
	public static final String Total_Taxable_value = "totalTaxablevalue";
	public static final String InterStateRegistered = "InterStateRegistered";

	public static final String IntraStateRegistered = "IntraStateRegistered";

	public static final String InterStateUnRegistered = "InterStateUnRegistered";

	public static final String IntraStateUnRegistered = "IntraStateUnRegistered";

	public static final String BillOfEntryDate = "billOfEntryDate";

	public static final String SEZ = "SEZ";

	public static final String RNV = "RNV";

	public static final String RDR = "RDR";

	public static final String EXPT = "EXPT";

	public static final String EXPWT = "EXPWT";

	public static final String INV = "INV";

	public static final String RDLC = "RDLC";

	public static final String NON = "NON";

	public static final String NIL = "NIL";

	public static final String ISDIE = "ISDIE";

	public static final String ISD8 = "ISD8";

	public static final String NSY = "NSY";

	public static final String SOA = "SOA";

	public static final String LGAS = "LGAS";

	public static final String SEZT = "SEZT";

	public static final String SEZWT = "SEZWT";

	public static final String ISIE8 = "ISIE8";

	public static final String B2B = "B2B";
	public static final String B2BA = "B2BA";
	public static final String B2G = "B2G";
	public static final String SEZWP = "SEZWP";
	public static final String SEZWOP = "SEZWOP";
	public static final String CREDIT_NOTE = "Credit Note";
	public static final String DEBIT_NOTE = "Debit Note";
	public static final String INVOICE_NAME = "Invoice";
	public static final String CREDIT_NOTE_A = "Credit Note-Amendment";
	public static final String DEBIT_NOTE_A = "Debit Note-Amendment";
	public static final String INVOICE_A = "Invoice-Amendment";

	public static final String PROFITCENTRE = "profitCentre";
	public static final String PROFITCENTRE1 = "profitCentre1";
	public static final String PROFITCENTRE2 = "profitCentre2";
	public static final String PLANT = "plantCode";
	public static final String LOCATION = "location";
	public static final String SALESORG = "salesOrg";
	public static final String PURCHASEORG = "PurchaseOrg";
	public static final String DISTRIBUTIONCHAN = "distChannel";
	public static final String USERACCESS1 = "profitCentre3";
	public static final String USERACCESS2 = "profitCentre4";
	public static final String USERACCESS3 = "profitCentre5";
	public static final String USERACCESS4 = "profitCentre6";
	public static final String USERACCESS5 = "profitCentre7";
	public static final String USERACCESS6 = "profitCentre8";
	public static final Integer ATT_LENGTH = 100;

	// Table3H-3I file process constants

	public static final String RECIPIENT_GSTIN = "RECIPIENT_GSTIN";
	public static final String RETURN_PERIOD = "returnPeriod";
	public static final String DOCUMENT_TYPE = "DOCUMENT_TYPE";
	public static final String TRANSACTION_FLAG = "TRANSACTION_FLAG";
	public static final String SUPPLIER_GSTIN_OR_PAN = "SUPPLIER_GSTIN_OR_PAN";
	public static final String SUPPLIER_NAME = "SUPPLIER_NAME ";
	public static final String DIFFERENTIAL_PERCENTAGE_FLAG = "DIFFERENTIAL_PERCENTAGE_FLAG";
	public static final String SECTION7OF_IGST_FLAG = "SECTION7OF_IGST_FLAG";
	public static final String AUTO_POPULATETO_REFUND = "AUTO_POPULATETO_REFUND";
	public static final String RATE = "rate";
	public static final String NEW_RATE = "newRate";
	public static final String INTEGRATED_TAX_AMOUNT = "NTEGRATED_TAX_AMOUNT";
	public static final String CENTRAL_TAX_AMOUNT = "CENTRAL_TAX_AMOUNT";
	public static final String STATEUT_TAX_AMOUNT = "STATEUT_TAX_AMOUNT";
	public static final String CESS_AMOUNT = "cessAmt";
	public static final String ELIGIBILITY_INDICATOR = "eligibilityIndicator";
	public static final String AVAILABLE_IGST = "AVAILABLE_IGST ";
	public static final String AVAILABLE_CGST = "AVAILABLE_CGST ";
	public static final String AVAILABLE_SGST = "AVAILABLE_SGST ";
	public static final String AVAILABLE_CESS = "AVAILABLE_CESS ";
	public static final String PROFIT_CENTRE = "PROFIT_CENTRE";

	public static final String DIVISON = "divison";
	public static final String PURCHASE_ORGANISATION = "PURCHASE_ORGANISATION";
	public static final String USER_ACCESS1 = "USER_ACCESS1";

	public static final String USER_ACCESS2 = "USER_ACCESS2";
	public static final String USER_ACCESS3 = "USER_ACCESS3";
	public static final String USER_ACCESS4 = "USER_ACCESS4";
	public static final String USER_ACCESS5 = "USER_ACCESS5";
	public static final String USER_ACCESS6 = "USER_ACCESS6";
	public static final String SUPPLIER_TYPE = "supplierType";
	public static final String CONTRACT_VALUE = "contractValue";
	public static final String CONTRACT_DATE = "contractDate";
	public static final String PAYMENT_DATE = "paymentDate";
	public static final String POSTING_DATE = "postingDate";
	public static final String PURCHASE_DATE = "purchaseVoucherDate";
	public static final String ITC_REVERSAL_IDENTIFIER = "ITCReversalIdentifier";
	public static final String IDENTIFIER = "Identifier";
	public static final String ITC_ENTITLEMENT = "itcEntitlement";
	public static final String USER_DEFINED_FIELD1 = "USER_DEFINED_FIELD1 ";
	public static final String USER_DEFINED_FIELD2 = "USER_DEFINED_FIELD2 ";
	public static final String USER_DEFINED_FIELD3 = "USER_DEFINED_FIELD3 ";
	public static final String USER_DEFINED28 = "USER_DEFINED28";
	public static final String DIFF_PERCENT_L65 = "L65";
	public static final String DIFF_PERCENT_L65_VAL = "0.65";
	public static final String BIGDECIMAL_100 = "100";
	public static final String DATE_FORMAT1 = "yyyy-MM-dd";

	public static final String EXCEPTION_APP = "Exception while saving the records";
	public static final String EXCEPTIONS = "Exception Occured: {}";
	public static final String G10 = "G10";
	public static final String ddMMyyyy = "ddMMyyyy";
	public static final String DATE01072017 = "01072017";
	public static final String E = "E";
	public static final String F = "F";
	public static final String UN = "UN";
	public static final String ON = "ON";
	// onBoarding questions
	public static final String O2 = "O2";
	public static final String O3 = "O3";
	public static final String O6 = "O6";
	public static final String O8 = "O8";
	public static final String O9 = "O9";
	public static final String O43 = "O43";

	public static final String I2 = "I2";
	public static final String I3 = "I3";
	public static final String I4 = "I4";
	public static final String O4 = "O4";
	public static final String I5 = "I5";
	public static final String I6 = "I6";
	public static final String I8 = "I8";
	public static final String I9 = "I9";
	public static final String I10 = "I10";
	public static final String I11 = "I11";
	public static final String I12 = "I12";
	public static final String I15 = "I15";
	public static final String I37 = "I37";
	public static final String I53 = "I53";
	public static final String A = "A";
	public static final String B = "B";
	public static final String D = "D";
	public static final String ERR = "ERR";
	public static final String OTH = "OTH";
	public static final String I = "I";
	public static final String ITEMAMUNT = "itemAmount";
	public static final String TOTALITEMAMT = "totalItemAmount";
	public static final String TAXTOTAL = "taxTotal";
	public static final String INVSTATECESSAMOUNT = "invStateCessAmount";
	public static final String INVSPECESSAMOUNT = "invCessSpecificAmount";
	public static final String INVASSVAL = "invoiceAssessableAmount";
	public static final String INV_SGST_AMOUNT = "invoiceSgstAmount";
	public static final String INV_CGST_AMOUNT = "invoiceCgstAmount";
	public static final String INV_IGST_AMOUNT = "invoiceIgstAmount";
	public static final String INV_ADV_CESSAMOUNT = "invoiceCessAdvaloremAmount";
	public static final String RAV = "RAV";
	public static final String DIS = "DIS";
	public static final String CMB = "CMB";
	public static final String SHP = "SHP";
	public static final String DGSTIN = "dispatcherGstin";
	public static final String DTNAME = "dispatcherTradeName";
	public static final String DTLOCATION = "dispatcherLocation";
	public static final String DPINCODE = "dispatcherPincode";
	public static final String DPSTATECODE = "dispatcherStateCode";
	public static final String SHPITOGSTIN = "shipToGstin";
	public static final String SHIP_TO_TRADE = "shipToTradeName";
	public static final String SHIP_TO_LOC = "shipToLocation";
	public static final String SHIP_TO_PIN = "shipToPincode";
	public static final String SHIP_TO_ADDRESS1 = "shipToBuildingNo";
	public static final String SHIP_TO_ADDRESS2 = "shipToBuildingName";
	public static final String OTH_SUP_DESC = "otherSupplyTypeDesc";
	public static final String IS = "IS";
	public static final String FOREIGNCURRENCY = "foreignCurrency";
	public static final String INV_VALUE_FC = "invoiceValueFc";
	public static final String COUNTRY_CODE = "countryCode";
	public static final String E_COM_TRANS = "ecomTransaction";
	public static final String E_COM_POS = "ecomPos";
	public static final String AMOUNT = "amount";
	public static final String TAXON = "taxOn";
	public static final String ITEM_TOTAL = "itemTotal";
	public static final String invoiceLineNetAmount = "lineItemAmt";
	public static final String preTaxAmount = "preTaxAmount";
	public static final String itemDiscount = "itemDiscount";
	public static final String packagingAndForwardingCharges = "packagingAndForwardingCharges";
	public static final String insuranceAmount = "insuranceAmount";
	public static final String freightAmount = "freightAmount";
	public static final String sumOfChargesOnDocumentLevel = "sumOfChargesOnDocumentLevel";
	public static final String sumOfInvoiceLineNetAmount = "sumOfInvoiceLineNetAmount";
	public static final String invoiceAllowancesOrCharges = "invoiceAllowancesOrCharges";
	public static final String invoiceOtherCharges = "invoiceOtherCharges";
	public static final String invoiceDiscount = "invoiceDiscount";
	public static final String tdsSgstAmount = "tdsSgstAmount";
	public static final String tdsCgstAmount = "tdsCgstAmount";
	public static final String tdsIgstAmount = "tdsIgstAmount";
	public static final String tcsSgstAmount = "tcsSgstAmount";
	public static final String tcsCgstAmount = "tcsCgstAmount";
	public static final String R = "R";
	public static final String O = "O";
	public static final String SEZD = "SEZD";
	public static final String SEZU = "SEZU";
	public static final String VEHICLE_TYPE = "vehicleType";
	public static final String TRANSACTION_TYPE = "tranType";
	public static final String DOC_CATEGORY = "docCat";
	public static final String SUB_SUPPLY_TYPE = "subsupplyType";
	public static final String ORIGIN_COUNTRY = "originCountry";
	public static final String SUPPLIER_STATE_CODE = "supStateCode";
	public static final String SEDWP = "SEDWP";
	public static final String SEDWOP = "SEDWOP";
	public static final String SCH3 = "SCH3";
	public static final String IG = "IG";
	public static final String CG = "CG";
	public static final String OTH_SUPTYPE_DESC = "otherSupplyTypeDesc";
	public static final String PRE_INV_NO = "preceedingInvNo";
	public static final String PRE_INV_DATE = "preceedingInvDate";
	public static final String ORG_INV_NO = "preceedingInvoiceNumber";
	public static final String ORG_INV_DATE = "preceedingInvoiceDate";
	public static final String TRANS_DOC_NO = "transportDocNo";
	public static final String TRANS_DOC_DATE = "transportDocDate";
	public static final String TAX_SCHEMA = "taxScheme";
	public static final String ROAD = "ROAD";
	public static final String CASH = "CASH";
	public static final String EPAY = "EPAY";
	public static final String DIRDBT = "DIRDBT";
	public static final String GST = "GST";
	public static final String RAIL = "RAIL";
	public static final String AIR = "AIR";
	public static final String SHIP = "SHIP";
	public static final String RRV = "RRV";
	public static final String RV = "RV";
	public static final String T1 = "T1";
	public static final String T2 = "T2";
	public static final String T3 = "T3";
	public static final String T4 = "T4";
	public static final String G = "G";
	public static final String ACCVOCHNUM = "ACCVOCHNUM";
	public static final String SUPP_EMAIL = "SupplierEmail";
	public static final String CUST_ADDER2 = "custOrSupAddr2";
	public static final String CUST_ADDER1 = "custOrSupAddr1";
	public static final String DIS_PATCHER_ADDR1 = "dispatcherBuildingNo";
	public static final String DIS_PATCHER_ADDR2 = "dispatcherBuildingName";
	public static final String SHIP_ADDR1 = "ShipToAddress1";
	public static final String SHIP_ADDR2 = "ShipToAddress2";
	public static final String INV_REMARKS = "InvoiceRemarks";
	public static final String PAYEE_NAME = "PayeeName";
	public static final String PAYMENT_TERMS = "PaymentTerms";
	public static final String PAYMENT_INNSTRUCTIONS = "PaymentInstruction";
	public static final String CREDIT_TRANSFER = "CreditTransfer";
	public static final String TRANSPOTER_NAME = "TransporterName";
	public static final String SalesOrderNumber = "SalesOrderNumber";
	public static final String GLStateCessSpecific = "GLStateCessSpecific";
	public static final String GLStateCessAdvalorem = "GLStateCessAdvalorem";
	public static final String GLSpecificCess = "GLSpecificCess";
	public static final String GLAdvaloremCess = "GLAdvaloremCess";
	public static final String GLSGST = "GLSGST";
	public static final String GLCGST = "GLCGST";
	public static final String GLIGST = "GLIGST";
	public static final String SourceFileName = "SourceFileName";
	public static final String SourceIdentifier = "SourceIdentifier";
	public static final String CompanyCode = "CompanyCode";
	public static final String UserID = "UserID";
	public static final String TDSFlagGST = "TDSFlagGST";
	public static final String SupplierAddress2 = "supBuildingName";
	public static final String SupplierAddress1 = "supBuildingNo";
	// Gstr2 Recon

	public static final String DRA = "DRA";
	public static final String CRA = "CRA";
	public static final String INVA = "INVA";
	public static final String IE = "IE";

	// Ewb status
	public static final String BOS = "BOS";
	public static final String BOE = "BOE";
	public static final String JWK = "JWK";
	public static final String SKD = "SKD";
	public static final String UNK = "UNK";
	public static final String FOU = "FOU";
	public static final String EXB = "EXB";
	public static final String LNS = "LNS";
	public static final String JWR = "JWR";
	public static final String SR = "SR";
	public static final String ORG_CREDIT_NOTE_NUM = "OriginalCreditNoteNumber";
	public static final String ORG_CREDIT_NOTE_DATE = "OriginalCreditNoteDate";
	public static final String PROFIT_CENTRE8 = "PROFIT_CENTRE8";
	public static final String PROFITCENTRE7 = "PROFITCENTRE7";
	public static final String PROFITCENTRE6 = "PROFITCENTRE6";
	public static final String PROFITCENTRE5 = "PROFITCENTRE5";
	public static final String PROFITCENTRE4 = "PROFITCENTRE4";
	public static final String PROFITCENTRE3 = "PROFITCENTRE3";

	// Gstr7 constants
	public static final String GROSS_AMOUNT = "grossAmount";
	public static final String ORG_GROSS_AMOUNT = "orgGrossAmount";
	public static final String PAYMENT_ADVICE_DATE = "paymentAdviceDate";
	public static final String DOCUMENT_DATE = "documentDate";
	public static final String ACTION_TYPE = "actionType";
	public static final String TDS_DEDUCTOR_GSTIN = "tdsDeductorGSTIN";
	public static final String ORG_TDS_DEDUCTEE_GSTIN = "orgTdsDeducteeGSTIN";
	public static final String ORG_RETURN_PERIOD = "originalReturnPeriod";
	public static final String TDS_DEDUCTEE_GSTIN = "tdsDeducteeGSTIN";
	public static final String TDS_IGST = "tdsIgst";
	public static final String TDS_CGST = "tdsCgst";
	public static final String TDS_SGST = "tdsSgst";
	public static final String TDS = "TDS";
	public static final String TDSA = "TDSA";
	public static final String TAX_PAY = "TAX_PAY";
	public static final String TAX_PAID = "TAX_PAID";

	// Gstr7 constants
	public static final String HSN_GSTIN = "GSTIN";

	public static final String NEINV = "NEINV";
	public static final String NEWB = "NEWB";
	public static final String NBOTH = "NBOTH";
	public static final String NRET = "NRET";
	public static final String NRETEWB = "NRETEWB";
	public static final String NRETEINV = "NRETEINV";
	// Gstr2 Json Upload
	public static final String GSTR2_JSON_FOLDER_NAME = "Gstr2JsonFiles";
	public static final String GSTR2_JSON_FILE = "GSTR2_JSON_FILE";
	public static final String URP = "URP";
	public static final String REG = "REG";
	// Gstr6 get Summery Contants start
	public static final String EISDAT = "EISDAT";
	public static final String IEISDAT = "IEISDAT";
	public static final String EISDCNT = "EISDCNT";
	public static final String EISDURAT = "EISDURAT";
	public static final String IEISDCNAT = "IEISDCNAT";
	public static final String EISDCNAT = "EISDCNAT";
	public static final String IEISDURAT = "IEISDURAT";
	public static final String IEISDCNURAT = "IEISDCNURAT";
	public static final String EISDCNURAT = "EISDCNURAT";
	public static final String IEISDCNURT = "IEISDCNURT";
	public static final String EISDURT = "EISDURT";
	public static final String IEISDT = "IEISDT";
	public static final String EISDCNURT = "EISDCNURT";
	public static final String IEISDURT = "IEISDURT";
	public static final String CDNAT = "CDNAT";
	public static final String CDNT = "CDNT";
	public static final String B2BT = "B2BT";
	public static final String B2BAT = "B2BAT";
	public static final String SUPPLIER_MAIL = "SUPPLIER_MAIL";
	public static final String SHIP_TO_LEGAL_NAME = "SHIP_TO_LEGAL_NAME";
	public static final String BARCODE = "Barcode";
	public static final String BATCHNAME = "BatchName";

	public static final String SUP_TO_REG_BUYER = "SuppliesToRegisteredBuyers";
	public static final String SUP_RET_BY_REG_BUYER = "SuppliesReturnedbyRegisteredBuyers";
	public static final String SUP_TO_UN_REG_BUYER = "SuppliestoURbuyers";
	public static final String SUP_RET_BY_UN_REG_BUYER = "SuppliesReturnedbyURbuyers";
	public static final String ACTIONSAVEDATGSTN = "ActionSavedatGSTN";
	public static final String ACTIONSAVEDATDIGIGST = "ActionSavedatDigiGST";

	public static final String MON_OF_DEDUCTOR = "MonthofDeductor";
	public static final String ORG_MON_OF_DEDUCTOR = "OriginalMonthofDeductor";

	public static final String GSTR2X_FOLDER_NAME = "GSTR2xWebUploads";

	// 180_days_reversal
	public static final String R180_DAYS_REVERSAL = "180_days_Reversal";
	public static final String REVERSAL_180_DAYS_RESPONSE_FOLDER = "Reversal180ResponseWebUploads";
	public static final String REVERSAL_180_DAYS_RESPONSE = "REV_180DAYS_RESPONSE";
	public static final String REVERSAL_180_DAYS_RESPONSE_FILE_NAME = "REVERSAL_180DAYS_RESPONSE";
	public static final String GSTR3B_Form = "3BFORM";

	public static final String GSTR1_SALES_REGISTER_FOLDER_NAME = "Gstr1SalesRegisterWebUploads";

	// gstr1a changes
	public static final String COMPREHENSIVE_RAW_1A = "COMPREHENSIVE_RAW_1A";
	public static final String OUTWARD_1A = "OUTWARD_1A";

	// Gstr6 get Summery Contants end
	public enum GSTNStatus {
		SAVED("Saved"), NOTSAVED("NotSaved"), ERROR("Error"), SUBMITTED(
				"Submitted");

		private String gstnStatus;

		GSTNStatus(String status) {
			this.gstnStatus = status;
		}

		/**
		 * @return the gstnStatus
		 */
		public String getGstnStatus() {
			return gstnStatus;
		}

		/**
		 * @param gstnStatus
		 *            the gstnStatus to set
		 */
		public void setGstnStatus(String gstnStatus) {
			this.gstnStatus = gstnStatus;
		}
	}

	public enum ProcessingStatus {
		PROCESSED("P"), ERROR("E"), INFORMATION("I");

		private String status;

		ProcessingStatus(String processingStatus) {
			this.status = processingStatus;
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
	}

	public enum DataOriginTypeCodes {
		ERP_API("A"), ERP_API_INV_MANAGMENT_CORR("AI"), EXCL_UPLOAD(
				"E"), EXCL_UPLOAD_INV_MANAGMENT_CORR(
						"EI"), SFTP("S"), SFTP_INV_MANAGMENT_CORR("SI"), CSV(
								"C"), CSV_INV_MANAGMENT_CORR("CI"), BC_API(
										"B"), BC_API_INV_MANAGMENT_CORR("BI");
		String dataOriginTypeCode;

		DataOriginTypeCodes(String code) {
			this.dataOriginTypeCode = code;
		}

		/**
		 * @return the dataOriginTypeCode
		 */
		public String getDataOriginTypeCode() {
			return dataOriginTypeCode;
		}
	}

	public enum INWARD_ERR_CODES {
		ER1304("Invalid Multiple Supply type Combination");

		String desc;

		INWARD_ERR_CODES(String errDesc) {
			this.desc = errDesc;
		}

		/**
		 * @return the desc
		 */
		public String getDesc() {
			return desc;
		}

	}

	public enum OUTWARD_ERR_CODES {
		ER0503("Invalid Multiple Supply type Combination");

		String desc;

		OUTWARD_ERR_CODES(String errDesc) {
			this.desc = errDesc;
		}

		/**
		 * @return the desc
		 */
		public String getDesc() {
			return desc;
		}
	}

	public enum GSTReturns {

		NOTAPPLICABLE("NotApplicable"), ASPERROR(
				"AspError"), ASP_P_NOT_SAVED_GSTN(
						"AspprocessedNotSaved"), ASP_P_SAVED_GSTN(
								"AspprocessedSaved"), GSTNERROR("GstnError");

		private String gstnReturnStatus;

		GSTReturns(String gstnReturnStatus) {
			this.gstnReturnStatus = gstnReturnStatus;
		}

		public String getGstnReturnStatus() {
			return gstnReturnStatus;
		}

		public void setGstnReturnStatus(String gstnReturnStatus) {
			this.gstnReturnStatus = gstnReturnStatus;
		}
	}

	/*
	 * Ravindra For GSTR2B File Upload
	 */
	public static final String GSTR2B_FOLDER_NAME = "Gstr2bWebUploads";
	public static final String GSTR2B_B2B_FILE_NAME = "GSTR2B_B2B_FILE_NAME";
	public static final String GSTR2B_B2B = "GSTR2B_B2B";
	public static final String GSTR2B_B2BA_FILE_NAME = "GSTR2B_B2BA_FILE_NAME";
	public static final String GSTR2B_B2BA = "GSTR2B_B2BA";

	public static final String GSTR2B_PR_USERRESPONSE_FILE_NAME = "Gstr2bprUserResponse";

	public static final String Apr_Sep = "Apr-Sep";
	public static final String Oct_Mar = "Oct-Mar";

	// Ewb Reports
	public static final String EWB_FOLDER_NAME = "EWB_FOLDER";

	// EWB File Upload
	public static final String EWB_FOLDER = "EwbFileUploads";

	// IMPGSEZ in pre-recon summary
	public static final String IMPGS = "IMPGS";

	public static final String ELIGIBILE_INDICATOR = "eligibleIndicator";
	public static final String ORIGINAL_DOCU_NO = "orgDocNum";

	/*
	 * For Inward Einvoice File Upload
	 */
	public static final String INWARD_EINVOICE_FOLDER_NAME = "InwardEinvoiceWebUploads";
	public static final String INWARD_EINVOICE_FILE_NAME = "GSTR2B_B2B_FILE_NAME";
	public static final String INWARD_EINVOICE = "INWARD_EINVOICE";
	public static final String IMS_USERRESPONSE_FILE_NAME = "IMSUserResponse";
	
	public static final String GSTR2B_LINKING_INPROGRESS = "Linking In-Progress";
	public static final String GSTR2B_LINKING_SUCCESS = "Linking Success";
	public static final String GSTR2B_LINKING_FAILED = "Linking Failed";
	public static final String GSTR2B_LINKING_INITIATED = "Linking Initiated";
	
	
	public static final String GSTR2B_PR_IMS_USERRESPONSE_FILE_NAME = "Gstr2bprImsUserResponse";
	
	public static final String IMS_FOLDER_NAME = "ImsWebUploads";
	public static final String IMS_FILE_NAME = "IMS_FILE_NAME";
	public static final String IMS = "IMS";

	
	public static final String GSTR2A_PR_IMS_USERRESPONSE_FILE_NAME = "Gstr2aprImsUserResponse";
	
	public static final String DMS_FILE_UPLOAD = "DMSFileUpload";
	
	// GL Dump
	public static final String FISCAL_YEAR = "FiscalYear";
	public static final String GL_ACCOUNT = "GlAccount";
	public static final String ACCOUNTING_VOUCHER_DATE = "AccountingVoucherDate";
	public static final String CLEARING_DATE = "ClearingDate";
	public static final String CURRENCY = "CURRENCY";
	public static final String MIGO_DATE = "MigoDate";
	public static final String MIRO_DATE = "MiroDate";
	public static final String ENTRY_DATE = "EntryDate";

	public static final String GL_DUMP_FILE_NAME = "GL_DUMP_FILE_NAME";
	
	public static final String GL_MASTER_FOLDER_NAME = "GlMasterWebUploads";
	public static final String GL_MASTER_FILE_NAME = "GL_MASTER_FILE_NAME";
	public static final String GL_MASTER = "GL_MASTER";

public static final String DIGIGST_REMARK = "DigiGSTRemarks";
	
	public static final String DIGIGST_COMMENT = "DigiGSTComment";
	
	public static final String SUPPLIES_COLLECTED = "SuppliesCollected";

	public static final String SUPPLIES_RETURNED = "SuppliesReturned";

	public static final String DOCUMENT_NUMBER = "DocumentNumber";

	public static final String DOCU_DATE = "DocumentDate";

	public static final String ORG_DOCUMENT_NUMBER = "OriginalDocumentNo";

	public static final String ORG_DOCUMENT_DATE = "OriginalDocumentDate";
	
	public static final String NET_SUPPLY = "TaxableValue/NetSupplies";
	
	public static final String INVOICEVALUE = "InvoiceValue";
	
	public static final String ORIGINALTAXABLEVALUE = "OriginalTaxableValue";
	
	public static final String  ORIGINAL_INVOICE_VAL = "OriginalInvoiceValue";

	public static final String TDS_POS = "POS";
	
	public static final String GSTNACTION = "GSTNAction";
	
	public static final String GSTN_REMARKS = "GSTNRemarks";
	
	public static final String GSTN_COMMENT = "GSTNComment";

	public static final String DIGIGST_ACTION = "DigiGSTAction";
	
	public static final String TDS_GSTIN = "GSTIN";
	
	public static final String TYPE = "Type";
	
	public static final String TAX_PERIOD = "Taxperiod";
	
	public static final String TDS_MONTH = "Month";
	
	public static final String IGST = "IGST";
	public static final String CGST = "CGST";
	public static final String SGST = "SGST";
	
	public static final String CHECKSUM = "Chksum";
}
