package com.ey.advisory.core.async;

public interface JobStatusConstants {
	public static final String WEB_UPLOAD = "WEB_UPLOAD";
	public static final String SFTP_WEB_UPLOAD = "SFTP_WEB_UPLOAD";
	public static final String ERP = "ERP";
	public static final String PROCESSED = "Processed";
	public static final String UPLOADED = "Uploaded";
	public static final String COMPLETED = "Completed";
	public static final String FAILED = "Failed";
	public static final String IN_PROGRESS = "InProgress";
	public static final String DUPLICATE = "Duplicate";
	public static final String PICKED = "Picked";
	public static final String SUBMITTED = "Submitted";
	public static final String INWARD_CHUNK_TYPE = "PRchunk";
	public static final String INWARD_SFTP_TYPE = "PRsftp";
	public static final String STAGING_TYPE = "staging";
	public static final String DB_CHUNK_TYPE = "DBChunk";
	public static final String XLSX_TYPE = "xlsx";
	public static final String XLSM_TYPE = "xlsm";
	public static final String CSV_TYPE = "csv";
	public static final String OUTWARD_CHUNK_TYPE = "SRchunk";
	public static final String OUTWARD_SFTP_TYPE = "SRsftp";
	public static final String GSTR9_TOKEN_JOBTYPE = "getGstr9Token";
	public static final String GSTR1_JSON_CSV_CONV = "Gstr1Json2CsvConv";
	public static final String B2B_TYPE = "b2b";
	public static final String B2BA_TYPE = "b2ba";
	public static final String B2CL_TYPE = "b2cl";
	public static final String B2CLA_TYPE = "b2cla";
	public static final String B2CS_TYPE = "b2cs";
	public static final String B2CSA_TYPE = "b2csa";
	public static final String CDNR_TYPE = "cdnr";
	public static final String CDNRA_TYPE = "cdnra";
	public static final String CDNUR_TYPE = "cdnur";
	public static final String CDNURA_TYPE = "cdnura";
	public static final String DOC_ISSUE_TYPE = "doc_issue";
	public static final String JSON_FOLDER = "GetCallJson";
	public static final String AT_TYPE = "at";
	public static final String ATA_TYPE = "ata";
	public static final String EXP_TYPE = "exp";
	public static final String EXPA_TYPE = "expa";
	public static final String TXP_TYPE = "txpd";
	public static final String TXPA_TYPE = "txpda";
	public static final String HSN_SUM_TYPE = "hsn";
	public static final String NIL_RATED_TYPE = "nil";
	public static final String GSTR2A_CDN_TYPE = "cdn";
	public static final String GSTR2A_CDNA_TYPE = "cdna";
	public static final String GSTR2A_B2BA_TYPE = "b2ba";
	public static final String GSTR2A_B2B_TYPE = "b2b";
	public static final String GSTR2A_ISDA_TYPE = "isda";
	public static final String GSTR2A_ISD_TYPE = "isd";
	public static final String GSTR3B_TYPE = "gstr3b";
	public static final String GSTR2A_TDS_TYPE = "tds";
	public static final String GSTR2A_TDSA_TYPE = "tdsa";
	public static final String SUP_DETAILS_TableType = "3.1";
	public static final String OSUP_DET_Type = "OSUP_DET";
	public static final String OSUP_ZERO = "OSUP_ZERO";
	public static final String OSUP_NIL_EXMPT_Type = "OSUP_NIL_EXMPT";
	public static final String ISUP_REV_Type = "ISUP_REV";
	public static final String OSUP_NONGST_Type = "OSUP_NONGST";
	public static final String INTER_SUP_DETAILS_TableType = "3.2";
	public static final String UNREG_DET_Type = "UNREG_DET";
	public static final String COMP_DET_Type = "COMP_DET";
	public static final String UIN_DET_Type = "UIN_DET";
	public static final String ITC_ELG_DETAILS_TableType = "4";
	public static final String ITC_AVL_Type = "ITC_AVAIL";
	public static final String ITC_REV_Type = "ITC_REV";
	public static final String ITC_NET_Type = "ITC_NET";
	public static final String ITC_INELIG_Type = "ITC_INELIG";
	public static final String INTR_LTFEE_TableType = "5.1";
	public static final String INTR_DET_Type = "INTR_DET";
	public static final String LATE_FEE_DET_Type = "LATE_FEE_DET";
	public static final String ISUP_DET_DETAILS_TableType = "5";
	public static final String ISUP_DET_Type = "ISUP_DET";
	public static final String TAX_PAYABLE_SECTION_TYPE = "TAX_PAYABLE";
	public static final String TAX_PAID_CASH_SECTION_TYPE = "TAX_PAID_CASH";
	public static final String TAX_PAID_ITC_SECTION_TYPE = "TAX_PAID_ITC";
	public static final String GSTR3B_SUMMARY_TYPE = "summary";
	public static final String GSTR3B_TAXPAYABLE_TYPE = "taxPayable";
	public static final String VENDOR_PROCESS = "vendorProcess";


	// logger related
	public static final String LOGGER_ENTERING = "Entering";
	public static final String LOGGER_EXITING = "Exiting";
	public static final String LOGGER_ERROR = "Error in";
	public static final String LOGGER_METHOD = "Method : ";
	
	public static final String CATEGORY_GET_GSTR9 = "getGstr9";
	
	
	
	public static final String CSV_STARTED = "CSV_STARTED";
	public static final String CSV_FAILED = "CSV_FAILED";
	public static final String CSV_END = "CSV_END";
	public static final String BLOB_DB_UPLOAD_JOB_POSTED = 
						"DBLOAD_JOB_POSTED";
	public static final String BLOB_UPLOAD_STARTED = "BLOB_UPLD_ST";
	public static final String BLOB_UPLOAD_FAILED = "BLOB_UPLD_FLD";
	public static final String BLOB_UPLOAD_END = "BLOB_UPLD_END";
	public static final String DB_UPLOAD_STARTED = "DB_UPLD_ST";
	public static final String DB_UPLOAD_FAILED = "DB_UPLD_FLD";
	public static final String DB_UPLOAD_END = "DB_UPLD_END";
	public static final String UNKNOWN_ERROR = "UNKNOWN_ERR";
	
	public static final String CSV_NA = "CSV_BLOB_UPLD_NA";
	
	public static final String COMPUTE_POSTED = "COMPUTE_POSTED";
	public static final String COMPUTE_STARTED = "COMPUTE_ST";
	public static final String COMPUTE_FAILED = "COMPUTE_FLD";
	public static final String COMPUTE_END = "COMPUTE_END";
	
	public static final String JSON_FILE = "json";
	public static final String CSV_FILE  = "csv";
	public static final String ZIP_FILE = "zip";
	
	

}
