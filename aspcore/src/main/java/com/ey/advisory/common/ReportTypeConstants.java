package com.ey.advisory.common;

public class ReportTypeConstants {

	public static final String ERROR_BV = "errorBv";
	public static final String TOTAL_RECORDS = "totalrecords";
	public static final String PROCESSED_RECORDS = "processed";
	public static final String INFORMATION_RECORDS = "information";
	public static final String ERROR_SV = "errorSv";
	public static final String TOTAL_ERRORS = "errorTotal";
	public static final String ERROR = "error";
	public static final String AS_UPLOADED = "asUploaded";
	public static final String ASPERROR = "aspError";
	public static final String GSTNERROR = "gstnError";
	public static final String GSTR2PROCESS = "gstr2Process";
	public static final String GSTR6PROCESS = "gstr6Process";
	public static final String GSTR1ENVPROCESS = "gstr1EInvProcess";
	public static final String EINVOICE_RECON = "Einvoice_Recon";
	public static final String ISD_RECON = "ISD_Recon";
	public static final String EINVPROCESS = "EinvProcess";
	public static final String EINVSUMMARY = "EinvSummary";
	public static final String EINVCONSOLIDATED = "Einv Consolidated";
	public static final String REV_RESP180_PROCESSED = "180DaysRespProcessed";
	public static final String REV_RESP180_TOTAL = "180DaysRespTotal";
	public static final String REV_RESP180_ERROR = "180DaysRespError";

	public static final String GSTR1ENTITYLEVEL = "entityLevel";

	// Report Category
	public static final String FILE_STATUS = "FileStatus";
	public static final String DATA_STATUS = "DataStatus";
	public static final String SFTP = "SFTP";
	public static final String PROCESSED_SUMMARY = "ProcessedSummary";
	public static final String RATE_LEVEL_REPORT = "RateLevelReport";
	public static final String RATE_LEVEL_DATA = "RateLevelData";
	public static final String HSN_SUMMARY = "HSNSummaryReport";
	public static final String GET_GSTR1_EINVOICE = "Get GSTR1-Einvoice";
	public static final String REVIEW_SUMMARY = "ReviewSummary";
	public static final String GSTR3B_REPORT = "3B Report";
	public static final String GSTR3B_ENTITY_REPORT = "3B Report";
	public static final String GSTR3B_SAVESUBMIT_REPORT = "SaveSubmit";
	public static final String INWARD = "INWARD";
	public static final String OUTWARD = "OUTWARD";
	public static final String VENDOR_COMM = "VendorComm";
	public static final String VENDOR_COMM_2BPR = "Vendor_Comm_2BPR";

	public static final String GSTR1GETCALL = "GSTR1 API Call";
	public static final String GSTR3BGETCALL = "GSTR3B API Call";
	public static final String NON_COMPLAINT_COM = "NonComplaintVendorCom";
	public static final String COMPLAIN_HISTORY = "ComplainceHistory";
	public static final String GSTR8GETCALL = "GSTR8 API Call";

	// Einvoice
	public static final String EINV_NOT_APPLICABLE = "Not Applicable";
	public static final String EINV_APPLICABLE = "Applicable";
	public static final String EINV_IRN_GENERATE = "IRN Generated";
	public static final String EINV_IRN_CANCELED = "IRN Cancelled";
	public static final String EINV_ERROR = "Error DigiGST IRP";
	public static final String EINV_ERROR_FROM_IRP = "Error From IRP";

	// Outward File Status Ret
	public static final String RET_NOT_APPLICABLE = "RET N";
	public static final String RET_APPLICABLE = "RET A";
	public static final String RET_PROCESS = "RET P";
	public static final String RET_ERROR = "RET E";
	public static final String RET_INFO = "RET I";

	// Outward File Status Ewb
	public static final String EWB_NOT_APPLICABLE = "EWB N";
	public static final String EWB_APPLICABLE = "EWB A";
	public static final String EWB_CANCEL = "EWB C";
	public static final String EWB_ERROR = "EWB E";
	public static final String EWB_GENERATED = "EWB G";
	public static final String EWB_ERROR_FROM_NIC = "EWB EN";

	// API call DashBoard
	public static final String GSTR2X_API_CALL = "GSTR2X API Call";
	public static final String GSTR8A_API_CALL = "Table-8A API Call";
	public static final String GSTR9_API_CALL = "GSTR9 API Call";
	public static final String ITC04_API_CALL = "ITC04 API Call";
	public static final String GSTR7_API_CALL = "GSTR7 API Call";
	public static final String GSTR6_API_CALL = "GSTR6 API Call";
	public static final String GSTR1A_API_CALL = "GSTR1A API Call";

	// GSTR9
	public static final String GSTR9_DUMP_REPORTS = "GSTR9 Dump Reports";
	public static final String GSTR9_TRANSLVL_GSTR1 = "Transactional_Level_GSTR1";
	public static final String GSTR9_B2C_B2CSA = "B2CS_B2CSA";
	public static final String GSTR9_ADVREC_AMD = "Advance_Recd_Amendment";
	public static final String GSTR9_ADVADJ_AMD = "Advance_Adjusted_Amendment";
	public static final String GSTR9_NIL_NON_EXT = "NIL_NON_EXT";
	public static final String GSTR9_HSN_SUMMARY = "HSN_Summary";
	public static final String GSTR9_GSTR3B_SUMMARY = "GSTR3B_Summary";
	public static final String GSTR9_GSTR3B_TXPAID = "GSTR3B_Tax_Paid";
	public static final String GSTR9_DIGIGST_COMPUTE = "DIGIGST Compute";

	// Vendor Rating
	public static final String VENDOR_COMPLIANCE_RATING = "Vendor Compliance Rating";
	public static final String VENDOR_COMPLIANCE_SUMMARY = "Vendor Compliance Summary";

	public static final String CUSTOMER_COMPLIANCE_RATING = "Customer Compliance Rating";
	public static final String CUSTOMER_COMPLIANCE_SUMMARY = "Customer Compliance Summary";

	public static final String MY_COMPLIANCE_RATING = "My Compliance Rating";
	public static final String MY_COMPLIANCE_SUMMARY = "My Compliance Summary";

	public static final String GSTR3B_180DAYS_REV_RESP = "180 days Reversal Response";
	public static final String GSTR3B_Table4_Transactional = "GSTR3B Table4 Transactional";

	// Stock Transfer
	public static final String STOCK_TRANSFER = "Stock Transfer";

	// Ewb
	public static final String EWB_PROCESSED_REPORTS = "Processed Reports";
	public static final String EWB_TOTAL_REPORTS = "Total Reports";
	public static final String EWB_ERROR_REPORTS = "Error Reports";

	// EWB download

	public static final String EWB_REPORT = "EWB_REPORT";
	public static final String EWB_DETAIL_REPORT = "EWB_DETAIL_REPORT";

	public static final String GSTR6_ISD_ANNEX = "ISDAnnexure";

	// Invoice Management Outward
	public static final String SHIPPING_BILL = "Shipping Bill";

	// Gstr6a Summary Report
	public static final String GSTR6A_SUMMARY = "Summary";

	// 180days Payement Reference Async
	public static final String DAYS180_TOTAL = "180DaysPaymentReference_totalrecords";
	public static final String DAYS180_ERROR = "180DaysPaymentReference_error";
	public static final String DAYS180_PROCESSED = "180DaysPaymentReference_processed";

	public static final String TOTAL_180DAYS = "totalrecords180days";
	public static final String ERROR_180DAYS = "error180days";
	public static final String PROCESSED_180DAYS = "processed180days";
	public static final String SALES_REGISTER = "SalesRegisterUpload";

	public static final String GSTR1_PDF = "GSTR 1 PDF";
	public static final String GSTR1A_PDF = "GSTR 1A PDF";
	public static final String HSNUPLOAD = "VerticalUploadHSN";
	public static final String GSTR3_PDF = "GSTR 3B PDF";
	public static final String GSTR6_PDF = "GSTR 6 PDF";
	public static final String GSTR7_PDF = "GSTR 7 PDF";
	public static final String GSTR8_PDF = "GSTR 8 PDF";

	public static final String ITC_REVERSAL_RULE_42 = "ITC Reversal Rule 42";

	public static final String DRC01B = "DRC01B";

	public static final String DRC01C = "DRC01C";

	public static final String CASH_LEDGER = "Cash Ledger";

	public static final String CREDIT_LEDGER = "Credit Ledger";

	public static final String LIABILITY_LEDGER = "Liability Ledger";

	public static final String INWARDEINVOICE_JSONS = "JSON Data";

	public static final String VENDOR_DOWNLOAD_REPORT = "Vendor E-Invoice Applicability Status";

	public static final String INWARD_EINVOICE_SUMMARY_REPORT = "Summary Report";

	public static final String INWARD_EINVOICE_DETAILED_REPORT = "Detailed Report (Invoice Level)";

	public static final String NESTED_ARRAY = "Preceding Document & Other details";

	public static final String STOCK_TRACKING_REPORT = "Stock Tracking Report";

	public static final String GSTR8_PROCESSED_REPORT = "Processed Records";
	
	public static final String GSTR8_ENTITY_REPORT = "Entity Level Summary";
	
	public static final String GSTR3B_ENTITY_SUMMARY_REPORT = "GSTR3B Entity Level Summary";

	public static final String Vendors_Compliance_Report = "Vendors Compliance Report";

	public static final String Consolidated_DigiGST_Error_Report = "Consolidated_DigiGST_Error_Report";
	
	public static final String Processed_Records_Recon_Tagging = "Processed_Records_Recon_Tagging";
	
	public static final String GSTR1A_AS_UPLOADED = "gstr1aAsUploaded";
	public static final String GSTR1A_ASPERROR = "gstr1aAspError";
	public static final String GSTR1A_GSTNERROR = "gstr1aGstnError";
	public static final String GSTR1AENTITYLEVEL = "gstr1aEntityLevel";
	public static final String GSTR1ASHIPPING_BILL = "GSTR1A Missing Shipping Bill";
	
	public static final String GL_PROCESSED_SUMMARY = "GlProcessedSummary";

	public static final String IMS_AMD_ORG_TRACK_REPORT = "IMS Amendment Original Track Report";

}
