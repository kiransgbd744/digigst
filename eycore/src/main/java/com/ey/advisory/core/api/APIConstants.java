package com.ey.advisory.core.api;

public class APIConstants {

	private APIConstants() {
	}

	public static final String DEFAULT_NON_STUB_API_EXECUTOR = "DefaultNonStubExecutor";
	public static final String DEFAULT_STUB_API_EXECUTOR = "DefaultGSTNStubExecutor";

	public static final String STATIC_SALES_VALIDATOR_CHAIN = "SalesDocRulesValidatorChain";
	public static final String DYNAMIC_SALES_VALIDATOR_CHAIN = "SkipRulesValidatorChain";
	public static final String E_INVOICESTATIC_VALIDATOR_CHAIN = "EinvoiceSalesDocRulesValidatorChain";
	public static final String GSTR1A_E_INVOICESTATIC_VALIDATOR_CHAIN = "Gstr1AEinvoiceSalesDocRulesValidatorChain";
	// Default Gstin and Group for Public API
	public static final String DEFAULT_PUBLIC_API_GSTIN = "000000000000000";
	public static final String DEFAULT_PUBLIC_API_GROUP = "000000";

	/**
	 * Gstn Action constants
	 */
	public static final String D = "D";
	public static final String C = "C";
	public static final String R = "R";
	public static final String SAVE = "SAVE";
	public static final String GET = "GET";
	public static final String DELETE = "DELETE";
	public static final String DELETE_FILE_UPLOAD = "DELETE (FILE UPLOAD)";
	public static final String DELETE_FULL_DATA = "DELETE (FULL DATA)";
	public static final String DELETE_RESPONSE = "DELETE_RESPONSE";
	public static final String ASP = "ASP";
	public static final String GSTN = "GSTN";
	public static final String B2B = "b2b";
	public static final String B2BA = "b2ba";
	public static final String B2CL = "b2cl";
	public static final String B2CLA = "b2cla";
	public static final String B2CS = "b2cs";
	public static final String B2CSA = "b2csa";
	public static final String NIL = "nil";
	public static final String EXP = "exp";
	public static final String EXPA = "expa";
	public static final String AT = "at";
	public static final String ATA = "ata";
	public static final String TXP = "txp";
	public static final String TXPA = "txpa";
	public static final String SUPECOM = "supecom";
	public static final String SUPECO = "supeco";
	public static final String SUPECOAMD = "supecoa";
	public static final String ECOMAMD = "ecoma";
	public static final String ECOMSUP = "ECOM[15(I) & 15(III)]";
	public static final String ECOMSUPSUM = "ECOM[15(II) & 15(IV)]";
	public static final String HSNSUM = "hsnsum";
	public static final String CDNR = "cdnr";
	public static final String CDNRA = "cdnra";
	public static final String CDNUR = "cdnur";
	public static final String CDNURA = "cdnura";
	public static final String DOCISS = "dociss";
	public static final String CDN = "cdn";
	public static final String CDNA = "cdna";
	public static final String TDS = "tds";
	public static final String CHECKSUM = "checksum";
	public static final String TCS = "tcs";
	public static final String TCSANDTDS = "TCSANDTDS";
	public static final String TCSA = "tcsa";
	public static final String TDSA = "tdsa";
	public static final String ISD = "isd";
	public static final String ISDA = "isda";
	public static final String TXPY = "tx_py";
	public static final String PMTPD = "pmt08_pd";
	public static final String NEGLIAB = "neg_liab";
	public static final String LATEFILEPMT = "latefilepmt08";
	public static final String TXPAID = "tx_paid";
	public static final String NETTXPY = "nettx_py";
	public static final String ATEFILEPMT = "latefilepmt08";
	public static final String LATERETFILE = "lateretfile";
	public static final String LATEREPDOC = "laterepdoc";
	public static final String REJDOC = "rejdoc";
	public static final String AMENDDOC = "amenddoc";
	public static final String AMDHIST = "amdhist";
	public static final String URD = "urd";
	public static final String URDA = "urda";

	public static final String URP = "URP";
	public static final String SCH3 = "SCH3";

	// Doc_Type
	public static final String INV = "INV";
	public static final String CR = "CR";
	public static final String RNV = "RNV";
	public static final String RCR = "RCR";

	// Eligibilty_Indicator
	public static final String IS = "IS";
	public static final String E = "E";
	public static final String NO = "NO";
	public static final String IE = "IE";

	public static final String CAMTC = "camtc";
	public static final String CAMTI = "camti";

	/**
	 * Gstn Response constants
	 */
	public static final String TXPD = "TXPD";
	public static final String TXPDA = "TXPDA";
	public static final String HSN = "HSN";
	public static final String DOC_ISSUE = "DOC_ISSUE";
	public static final String LATEFEE = "latefee";
	public static final String GSTR1_EINV = "GSTR1_EINV";
	public static final String EINV_B2B = "B2B";
	public static final String EINV_CDNR = "CDNR";
	public static final String EINV_CDNUR = "CDNUR";
	public static final String EINV_EXP = "EXP";

	/**
	 * Gstr1 Procedure Constants
	 */

	public static final String ANX_IMPS_PROC = "ANX_REVSUM_IMPS_PROC";

	public static final String B2CS_PROC = "GSTR1_SAVE_B2CS_PROC";
	public static final String B2CS_AMD_PROC = "GSTR1_SAVE_B2CS_AMD_PROC";
	public static final String AT_PROC = "GSTR1_SAVE_AT_PROC";
	public static final String AT_AMD_PROC = "GSTR1_SAVE_AT_PROC_AMD";
	public static final String TXP_PROC = "GSTR1_SAVE_TXP_PROC";
	public static final String TXP_AMD_PROC = "GSTR1_SAVE_TXPA_PROC_AMD";
	public static final String HSN_PROC = "GSTR1_SAVE_HSN_PROC";
	public static final String NIL_PROC = "GSTR1_SAVE_NILNONEXMPT_PROC";
	public static final String SUPECOM_PROC = "GSTR1_SAVE_TABLE14_PROC";
	public static final String ECOMSUPSUM_PROC = "GSTR1_SAVE_TABLE15_PROC";
	public static final String HSN_RATE_PROC = "GSTR1_SAVE_HSN_RATE_PROC";

	public static final String GSTR1A_B2CS_PROC = "GSTR1A_SAVE_B2CS_PROC";
	public static final String GSTR1A_B2CS_AMD_PROC = "GSTR1A_SAVE_B2CS_AMD_PROC";
	public static final String GSTR1A_AT_PROC = "GSTR1A_SAVE_AT_PROC";
	public static final String GSTR1A_AT_AMD_PROC = "GSTR1A_SAVE_AT_PROC_AMD";
	public static final String GSTR1A_TXP_PROC = "GSTR1A_SAVE_TXP_PROC";
	public static final String GSTR1A_TXP_AMD_PROC = "GSTR1A_SAVE_TXPA_PROC_AMD";
	public static final String GSTR1A_HSN_PROC = "GSTR1A_SAVE_HSN_PROC";
	public static final String GSTR1A_NIL_PROC = "GSTR1A_SAVE_NILNONEXMPT_PROC";
	public static final String GSTR1A_SUPECOM_PROC = "GSTR1A_SAVE_TABLE14_PROC";
	public static final String GSTR1A_ECOMSUPSUM_PROC = "GSTR1A_SAVE_TABLE15_PROC";
	public static final String GSTR1A_HSN_RATE_PROC = "GSTR1A_SAVE_HSN_RATE_PROC";

	/**
	 * Anx1 Procedure Constants
	 */

	public static final String B2C_PROC = "ANXSAVE_B2C_PROC";
	public static final String REV_PROC = "ANXSAVE_RC_PROC";
	public static final String IMPS_PROC = "ANXSAVE_IMPS_PROC";
	public static final String ECOM_PROC = "ANXSAVE_ECOM_PROC";

	/**
	 * Anx2 Procedure Constants
	 */

	public static final String USP_ANX2SAVE_GSTN = "USP_ANX2SAVE_GSTN";

	/**
	 * RET1 Procedure Constants
	 */

	public static final String USP_RET1_SAVE_3A = "USP_RET1_SAVE_3A";
	public static final String USP_RET1_SAVE_3C = "USP_RET1_SAVE_3C";
	public static final String USP_RET1_SAVE_3D = "USP_RET1_SAVE_3D";
	public static final String USP_RET1_SAVE_4A = "USP_RET1_SAVE_4A";
	public static final String USP_RET1_SAVE_4B = "USP_RET1_SAVE_4B";
	public static final String USP_RET1_SAVE_4ITC = "USP_RET1_SAVE_4ITC";

	public static final String USP_GETANX1_B2C_DETAILED = "USP_GETANX1_B2C_DETAILED";
	public static final String USP_GETANX1_B2B_DETAILED = "USP_GETANX1_B2B_DETAILED";
	public static final String USP_GETANX1_B2B_AMD_DETAILED = "USP_GETANX1_B2B_AMD_DETAILED";
	public static final String USP_GETANX1_EXPWP_DETAILED = "USP_GETANX1_EXPWP_DETAILED";
	public static final String USP_GETANX1_EXPWOP_DETAILED = "USP_GETANX1_EXPWOP_DETAILED";
	public static final String USP_GETANX1_SEZWP_DETAILED = "USP_GETANX1_SEZWP_DETAILED";
	public static final String USP_GETANX1_SEZWP_AMD_DETAILED = "USP_GETANX1_SEZWP_AMD_DETAILED";
	public static final String USP_GETANX1_SEZWOP_DETAILED = "USP_GETANX1_SEZWOP_DETAILED";
	public static final String USP_GETANX1_SEZWOPA_DETAILED = "USP_GETANX1_SEZWOPA_DETAILED";
	public static final String USP_GETANX1_DE_DETAILED = "USP_GETANX1_DE_DETAILED";
	public static final String USP_GETANX1_DEA_DETAILED = "USP_GETANX1_DEA_DETAILED";
	public static final String USP_GETANX1_REV_DETAILED = "USP_GETANX1_REV_DETAILED";
	public static final String USP_GETANX1_IMPS_DETAILED = "USP_GETANX1_IMPS_DETAILED";
	public static final String USP_GETANX1_IMPG_DETAILED = "USP_GETANX1_IMPG_DETAILED";
	public static final String USP_GETANX1_IMPGSEZ_DETAILED = "USP_GETANX1_IMPGSEZ_DETAILED";
	public static final String USP_GETANX1_ECOM_DETAILED = "USP_GETANX1_ECOM_DETAILED";

	/*********** Save To Gstn Constants *************/
	// public static final int MAX_NO_OF_INVOICES = 100;// 2500
	public static final String INV_TYPE_DE = "DE";
	public static final String INV_TYPE_SEZWOP = "SEZWOP";
	public static final String INV_TYPE_SEZWP = "SEZWP";
	public static final String INV_TYPE_R = "R";
	public static final String INV_TYPE_CBW = "CBW";
	public static final String SUP_TYPE_INTER = "INTER";
	public static final String SUP_TYPE_INTRA = "INTRA";
	public static final String SUP_TYPE_INTRB2B = "INTRB2B";
	public static final String SUP_TYPE_INTRB2C = "INTRB2C";
	public static final String SUP_TYPE_INTRAB2B = "INTRAB2B";
	public static final String SUP_TYPE_INTRAB2C = "INTRAB2C";
	public static final String EXP_TYPE_WOPAY = "WOPAY";
	public static final String EXP_TYPE_WPAY = "WPAY";
	public static final String OE = "OE";
	public static final String INV_TYPE_SEWP = "SEWP";
	public static final String INV_TYPE_SEWOP = "SEWOP";

	/**
	 * ANX1 Constants
	 */
	public static final String B2C = "b2c";
	public static final String B2CA = "b2ca";
	public static final String EXPWP = "expwp";
	public static final String EXPWOP = "expwop";
	public static final String SEZWP = "sezwp";
	public static final String SEZWOP = "sezwop";
	public static final String DE = "de";
	public static final String REV = "rev";
	public static final String IMPS = "imps";
	public static final String IMPG = "impg";
	public static final String IMPGSEZ = "impgsez";
	public static final String MIS = "mis";
	public static final String ECOM = "ecom";
	public static final String ECOMA = "ecoma";
	public static final String ANX1_FROMTIME = "from_time";
	public static final String GSTR6_FROMTIME = "from_time";

	public static final String ANX1_DOCACTION = "doc_action";
	public static final String GSTR6_DOCACTION = "action_required";

	public static final String ANX1_CTIN = "ctin";
	public static final String GSTR6A_CTIN = "ctin";
	public static final String ANX1_ETIN = "etin";
	public static final String EXPLIAB = "expliab";
	public static final String PRIORLIAB = "priorliab";
	public static final String SUBTOTAL = "subtotal";
	public static final String DN = "dn";
	public static final String CN = "cn";
	public static final String ADVREC = "advrec";
	public static final String ADVADJ = "advadj";
	public static final String RDCTN = "rdctn";
	public static final String EXPNIL = "expnil";
	public static final String NONGST = "nongst";
	public static final String REVNT = "revnt";
	public static final String REJCN = "rejcn";
	public static final String PENCN = "pencn";
	public static final String ACCCN = "acccn";
	public static final String ELGCRDT = "elgcrdt";
	public static final String PROVCRDT = "provcrdt";
	public static final String ITCADJ = "itcadj";
	public static final String POSTRJCTDCREDITS = "postrjctdcredits";
	public static final String ITCREVINV = "itcrevinv";
	public static final String INELGCREDITS = "inelgcredits";
	public static final String ITCREV = "itcrev";
	public static final String ITCREVOTH = "itcrevoth";
	public static final String FIRSTMON = "firstmon";
	public static final String SECMON = "secmon";
	public static final String ITCCG = "itccg";
	public static final String ITCCS = "itccs";
	public static final String ITCAVL = "itcavl";
	public static final String NETITCAVL = "netitcavl";
	public static final String INTRLATEFILING = "intrlatefiling";
	public static final String INTRLATEREPORT = "intrlatereport";
	public static final String INTRREJA = "intrreja";
	public static final String INTRITCREV = "intritcrev";
	public static final String INTRREV = "intrrev";
	public static final String OTHRLIAB = "othrliab";
	public static final String ELGITC = "elgItc";
	public static final String INELGITC = "inelgItc";
	public static final String ISDITCCROSS = "isdItcCross";
	public static final String TOTALITC = "totalItc";

	public static final String P = "P";
	public static final String ER = "ER";
	public static final String G = "G";
	public static final String EMPTY = "";
	public static final String PE = "PE";
	public static final String IP = "IP";
	public static final String DUP = "DUP";
	public static final String E_CPI = "E_CPI";

	/**
	 * ANX2
	 */
	public static final String DEA = "dea";
	public static final String SEZWPA = "sezwpa";
	public static final String SEZWOPA = "sezwopa";
	public static final String SYSTEM = "system";
	public static final String PROCESSING_STATUS = "pending";
	public static final String Y = "Y";
	public static final String N = "N";
	// public static final String PROC_2A = "A2_BIGTABLE"; //old proc
	public static final String PROC_2A_B2B = "A2_B2B_BIGTABLE";
	public static final String PROC_2A_SEZWOP = "A2_SEZWOP_BIGTABLE";
	public static final String PROC_2A_SEZWP = "A2_SEZWP_BIGTABLE";
	public static final String PROC_2A_DE = "A2_DE_BIGTABLE";
	// public static final String PROC_PR = "PR_BIG_TABLE"; //old proc
	public static final String PROC_PR = "PR_INWARD_BIGTABLE";

	public static final String ISDC = "isdc";
	public static final String ITCSUM = "itcsum";

	/**
	 * Gstr types
	 */
	public static final String GSTR1 = "gstr1";
	public static final String GSTR6 = "gstr6";
	public static final String GSTR6A = "gstr6a";
	public static final String GSTR2A = "gstr2a";
	public static final String GSTR2X = "gstr2x";
	public static final String ANX1 = "anx1";
	public static final String ANX2 = "anx2";
	public static final String RET = "ret";
	public static final String GSTR2A_ERP = "gstr2a_erp";
	public static final String GSTR2A_UPLOAD = "gstr2a_upl";
	public static final String GSTR1_UPLOAD = "gstr1_upl";
	public static final String GSTR2B_UPLOAD = "gstr2b_upl";
	public static final String GSTR8 = "gstr8";
	public static final String GSTR6A_UPLOAD = "gstr6a_upl";
	public static final String VENDOR_COMPLIANCE = "Vendor Compliance";

	public static final String GSTR1A = "gstr1A";
	
	public static final String GSTR_1 = "GSTR-1_";
	public static final String GSTR_1A = "GSTR-1A_";
	
	/**
	 * Anx1 & Anx2 Get_Batch status
	 */
	public static final String INITIATED = "INITIATED";
	public static final String INPROGRESS = "INPROGRESS";
	public static final String FAILED = "FAILED";
	public static final String SUCCESS = "SUCCESS";
	public static final String TOKEN_RECEIVED = "TOKEN_RECEIVED";
	public static final String TOKEN_IN_PROGRESS = "TOKEN_IN_PROGRESS";
	public static final String SUCCESS_WITH_NO_DATA = "SUCCESS_WITH_NO_DATA";

	/**
	 * Anx2 Request ID status
	 */
	public static final String NOT_INITIATED = "NOT_INITIATED";
	public static final String COMPLETED = "COMPLETED";

	/**
	 * Data Security Applicable Attributes
	 * 
	 */
	public static final String GSTIN = "gstin";
	public static final String PROFIT_CENTER = "profitCenter";
	public static final String PLANT = "plant";
	public static final String DIVISION = "division";
	public static final String LOCATION = "location";
	public static final String PURCHASE_ORG = "purchaseOrg";
	public static final String SALES_ORG = "salesOrg";
	public static final String DIST_CHANNEL = "distChannel";
	public static final String USER_ACCESS1 = "userAccess1";
	public static final String USER_ACCESS2 = "userAccess2";
	public static final String USER_ACCESS3 = "userAccess3";
	public static final String USER_ACCESS4 = "userAccess4";
	public static final String USER_ACCESS5 = "userAccess5";
	public static final String USER_ACCESS6 = "userAccess6";

	/**
	 * Data Status
	 */
	public static final String ASP_OUTWARD = "aspoutward";
	public static final String ASP_INWARD = "aspinward";
	public static final String GSTN_OUTWARD = "gstnoutward";
	public static final String GSTN_INWARD = "gstninward";
	public static final String OUTWARD = "outward";
	public static final String INWARD = "inward";
	public static final String TABLE4 = "TABLE4";
	public static final String OUTWARD_SUMMARY = "outwardSummary";
	public static final String INWARD_SUMMARY = "inwardSummary";

	public static final String REVIEW_SUMMARY = "ReviewSummary";
	public static final String PROCESS_SUMMARY = "ProcessSummary";
	public static final String SEZ_REVIEW_SUMMARY = "SEZ";
	public static final String DOC_ISSUE_REVIEW_SUMMARY = "DocIssue";

	public static final String NILEXTNON_REVIEW_SUMMARY = "NILExtNon";
	public static final String HSN_DETAILS_REVIEW_SUMMARY = "HsnDetails";
	public static final String ADVANCE_REVIEW_SUMMARY = "AdvanceDetails";

	public static final String OUTWARD_ASP_ERP_PUSH = "OUTWARD_ASP_ERP_PUSH";
	public static final String INWARD_ASP_ERP_PUSH = "INWARD_ASP_ERP_PUSH";
	public static final String OUTWARD_GSTN_ERP_PUSH = "OUTWARD_GSTN_ERP_PUSH";
	public static final String INWARD_GSTN_ERP_PUSH = "INWARD_GSTN_ERP_PUSH";
	public static final String OUTWARD_PAYLOAD_METADATA_REV_INTG = "OutwardPayloadMetadataRevIntg";
	public static final String INWARD_PAYLOAD_METADATA_REV_INTG = "InwardPayloadMetadataRevIntg";
	public static final String ITC04_PAYLOAD_METADATA_REV_INTG = "Itc04PayloadMetadataRevIntg";
	public static final String BC_API_PAYLOAD_REV_INT = "BCAPIPaylodRevInt";

	/**
	 * Detinations
	 */
	// These 4 destinations are depricated needs to be
	// removed(ASP_ERP_OUTWARD_ERROR,
	// ASP_ERP_INWARD_ERROR,GSTN_ERP_OUTWARD_ERROR,GSTN_ERP_INWARD_ERROR)
	public static final String ASP_ERP_OUTWARD_ERROR = "Asp_Error_Docs_Outward";
	public static final String ASP_ERP_INWARD_ERROR = "Asp_Error_Docs_Inward";
	public static final String GSTN_ERP_OUTWARD_ERROR = "Gstn_Error_Docs_Outward";
	public static final String GSTN_ERP_INWARD_ERROR = "Gstn_Error_Docs_Inward";

	public static final String ERP_WORKFLOW = "approvalworkflow";
	/*
	 * public static final String ERP_OUTWARD_ERROR = "Error_Docs_Outward";
	 * public static final String ERP_INWARD_ERROR = "Error_Docs_Inward";
	 */
	public static final String ERP_OUTWARD_INWARD_DATASTATUS = "DataStatus";
	public static final String ERP_ANX1_REVIEW_SUMMARY = "Anx1_Review_Sum";
	public static final String ERP_GSTR1_REVIEW_AND_PROCESSED_SUMMARY = "Gstr1_Review_And_Processed_Sum";
	public static final String ERP_GSTR3_REVIEW_AND_PROCESSED_SUMMARY = "Gstr3b_Review_And_Processed_Summery";
	public static final String ERP_GSTR7_REVIEW_AND_PROCESSED_SUMMARY = "Gstr7_Review_And_Processed_Summery";
	public static final String ERP_EINVOICE_DATA_STATUS = "EInvoice_Data_Status_Sum";
	public static final String ERP_GSTR1_WORKFLOW = "GSTR_Approval_req";
	public static final String ERP_ANX1A_PROC_REVIEW_SUM = "ANX1A_PROC_REVIEW_SUM";

	public static final String ERP_GSTR6_PROC_REVIEW_SUM = "Gstr6_Process_Review_sum";
	public static final String ERP_GSTR6_WORKFLOW = "GSTR6_Approval_req";

	// Gstr6A Process and Review Summary
	public static final String ERP_GSTR6A_PROC_REVIEW_SUM = "Gstr6A_Process_Review_sum";
	// Ret 1 Process and Review Summary
	public static final String ERP_RET1_REVIEW_AND_PROCESSED_SUMMARY = "RET1_Process_ReviewSumm";
	public static final String ERP_RET1A_REVIEW_AND_PROCESSED_SUMMARY = "Ret1A_Review_And_Processed_Sum";
	public static final String ERP_RET1_APPROVAL = "RET1_Approval_req";
	public static final String Compliance_Report = "Compliance Report";
	public static final String Compliance_Table_Report = "Compliance Table Report";
	/**
	 * GSTR3B SAVE GSTN
	 */
	public static final String GSTR3B = "GSTR3B";
	public static final String SUP_DETAILS = "sup_details";
	public static final String INTER_SUP = "inter_sup";
	public static final String ITC_ELG = "itc_elg";
	public static final String INWARD_SUP = "inward_sup";
	public static final String INTR_LTFEE = "intr_ltfee";
	public static final String OSUP_DET = "osup_det";
	public static final String OSUP_ZERO = "osup_zero";
	public static final String OSUP_NIL_EXMP = "osup_nil_exmp";
	public static final String ISUP_REV = "isup_rev";
	public static final String OSUP_NONGST = "osup_nongst";
	public static final String UNREG_DETAILS = "unreg_details";
	public static final String COMP_DETAILS = "comp_details";
	public static final String UIN_DETAILS = "uin_details";
	public static final String ITC_AVL = "itc_avl";
	public static final String ITC_REV = "itc_rev";
	public static final String ITC_NET = "itc_net";
	public static final String ITC_INELG = "itc_inelg";
	public static final String ISUP_DETAILS = "isup_details";
	public static final String INTR_DETAILS = "intr_details";
	public static final String TXVAL = "txval";
	public static final String IAMT = "iamt";
	public static final String CAMT = "camt";
	public static final String SAMT = "samt";
	public static final String CSAMT = "csamt";
	public static final String POS = "pos";
	public static final String TY = "ty";
	public static final String INTER = "inter";
	public static final String INTRA = "intra";
	public static final String TAXPERIOD = "taxPeriod";
	public static final String REFID = "refId";
	public static final String APIACTION = "apiAction";
	public static final String RETPERIOD = "retPeriod";
	public static final String GSTR3B_SAVE_ERROR_FOLDER = "GSTR3BSaveErrorJsons";
	public static final String GSTR3B_SAVE = "GSTR3B_SAVE";
	public static final String SAVE_FAILED = "SAVE_FAILED";
	public static final String POLLING_FAILED = "POLLING_FAILED";
	public static final String GSTIN_LIST = "gstinList";
	public static final String SAVED = "SAVED";
	public static final String FILED = "FILED";
	public static final String SAVE_INITIATED = "SAVE_INITIATED";
	public static final String RECOMPUTE_INITIATED = "RECOMPUTE_INITIATED";
	public static final String SAVEPSTLIAB_INITIATED = "SAVEPSTLIAB_INITIATED";
	public static final String GSTR3B_RECOMPUTE = "Re-compute";
	public static final String GSTR3B_SAVEPSTLIAB = "Past Period Liability";
	public static final String SAVE_INITIATION_FAILED = "SAVE_INITIATION_FAILED";
	public static final String SAVE_INPROGRESS = "SAVE_INPROGRESS";
	public static final String POLLING_INITIATED = "POLLING_INITIATED";
	public static final String POLLING_INPROGRESS = "POLLING_INPROGRESS";
	public static final String POLLING_COMPLETED = "POLLING_COMPLETED";
	public static final String SUBMITTED = "SUBMITTED";
	public static final String NOT_SUBMITTED = "NOT SUBMITTED";
	public static final String ALREADY_SUBMITTED = "already submitted";

	public static final String GET_INITIATED = "GET_INITIATED";
	public static final String SUBMIT_INITIATED = "SUBMIT_INITIATED";
	public static final String SUBMIT_INPROGRESS = "SUBMIT_INPROGRESS";
	public static final String SUBMIT_COMPLETED = "SUBMIT_COMPLETED";
	public static final String SUBMIT_FAILED = "SUBMIT_FAILED";

	public static final String RETURN_TYPE = "RETURN_TYPE";

	public static final String ANX1_RETURN_TYPE = "ANX1";
	public static final String ANX2_RETURN_TYPE = "ANX2";
	public static final String RET_RETURN_TYPE = "RET1";
	public static final String GSTR6_RETURN_TYPE = "GSTR6";
	public static final String GSTR6A_RETURN_TYPE = "GSTR6A";
	public static final String GSTR2A_RETURN_TYPE = "GSTR2A";
	public static final String GSTR2X_RETURN_TYPE = "GSTR2X";

	public static final String ERP_ANX2_PR_SUMMARY = "ANX2_PR_Summary";

	/**
	 * RET Constants
	 */
	public static final String TBL3A = "tbl3a";
	public static final String TBL3B = "tbl3b";
	public static final String TBL3C = "tbl3c";
	public static final String TBL3D = "tbl3d";
	public static final String TBL3E = "tbl3e";

	public static final String TBL4A = "tbl4A";
	public static final String TBL4B = "tbl4b";
	public static final String TBL4ITC = "tbl4itc";
	public static final String TBL5 = "tbl5";
	public static final String TBL6 = "tbl6";

	public static final String PAYMENTTAX = "paymenttax";
	public static final String INTALERT = "intalert";

	/**
	 * SAVE ANX2
	 */

	public static final String ACTIVE = "Active";
	public static final String IN_ACTIVE = "Inactive";
	public static final String TYPE_COUNT = "Count";
	public static final String TYPE_TAXABLE = "Taxable Value";
	public static final String TYPE_TOTALTAX = "Total Tax";

	/**
	 * GSTR6
	 */

	public static final String ITC = "ItcDetails";

	// json upload
	public static final String JSON_UPLOADED = "uploaded";
	public static final String JSON_PROCESSED = "Processed";
	public static final String JSON_FAILED = "failed";

	public static final String TAX = "TAX";

	// SAVEDOC API
	public static final String ERROR = "ERROR";
	public static final String NOCHECKSUM = "NOCHECKSUM";
	// GSTR7
	public static final String GSTR7_TDS_DETAILS = "TDS_DETAILS";
	public static final String GSTR7_SUMMARY_DETAILS = "SUMMARY_DETAILS";
	public static final String GSTR7_RETURN_TYPE = "GSTR7";
	public static final String GSTR7_RETSUM = "RETSUM";
	public static final String GSTR7_SUMMARY = "SUMMARY";
	public static final String GSTR7_FROMTIME = "from_time";
	public static final String GSTR7_REC_TYPE = "rec_type";
	public static final String GSTR7 = "gstr7";

	public static final String GSTR7_SAVE_TDSA_PROC = "GSTR7_SAVE_TDSA_PROC";
	public static final String GSTR7_SAVE_TDS_PROC = "GSTR7_SAVE_TDS_PROC";

	public static final String GSTR8_SAVE_GSTN_PROC = "USP_GSTR8_SAVE_PROC";
	public static final String GSTR8_SAVE_CAN_GSTN_PROC = "USP_GSTR8_CAN_PROC";

	// public static final String GSTR1_GSTN_API_VERSION = "v2.0";

	public static final String GSTR6A_GSTN_API_VERSION = "v1.0";
	public static final String GSTR6_GSTN_API_VERSION = "v1.0";

	public static final String ERP_GET2A = "Get2A_Sum";

	public static final String ERP_GET2A_CONSOLIDATED = "Get2A_Consolidated_Sum";

	// ----------------GSTR2A ERP GET PROCS ----------------------//

	public static final String USP_GETGSTR2A_B2B_ERP_CONSOLIDATED = "USP_GETGSTR2A_B2B_ERP_CONSOLIDATED";
	public static final String USP_GETGSTR2A_B2BA_ERP_CONSOLIDATED = "USP_GETGSTR2A_B2BA_ERP_CONSOLIDATED";
	public static final String USP_GETGSTR2A_CDN_ERP_CONSOLIDATED = "USP_GETGSTR2A_CDN_ERP_CONSOLIDATED";
	public static final String USP_GETGSTR2A_CDNA_ERP_CONSOLIDATED = "USP_GETGSTR2A_CDNA_ERP_CONSOLIDATED";
	public static final String USP_GETGSTR2A_ISD_ERP_CONSOLIDATED = "USP_GETGSTR2A_ISD_ERP_CONSOLIDATED";

	// ------- GSTR2A GET PROCS TO PUT DATA INTO ORIGINAL TABLES -------//

	public static final String USP_GETGSTR2A_B2B_DUP_CHK = "USP_GETGSTR2A_B2B_DUP_CHK";
	public static final String USP_GETGSTR2A_B2BA_DUP_CHK = "USP_GETGSTR2A_B2BA_DUP_CHK";
	public static final String USP_GETGSTR2A_CDN_DUP_CHK = "USP_GETGSTR2A_CDN_DUP_CHK";
	public static final String USP_GETGSTR2A_CDNA_DUP_CHK = "USP_GETGSTR2A_CDNA_DUP_CHK";
	public static final String USP_GETGSTR2A_ISD_DUP_CHK = "USP_GETGSTR2A_ISD_DUP_CHK";
	public static final String USP_GETGSTR2A_ISDA_DUP_CHK = "USP_GETGSTR2A_ISDA_DUP_CHK";
	public static final String USP_GETGSTR2A_AMDHIST_DUP_CHK = "USP_GETGSTR2A_AMDHIST_DUP_CHK";
	public static final String USP_GETGSTR2A_IMPG_DUP_CHK = "USP_GETGSTR2A_IMPG_DUP_CHK";
	public static final String USP_GETGSTR2A_IMPGSEZ_DUP_CHK = "USP_GETGSTR2A_IMPGSEZ_DUP_CHK";
	public static final String USP_GETGSTR2A_TDS_DUP_CHK = "USP_GETGSTR2A_TDS_DUP_CHK";
	public static final String USP_GETGSTR2A_TDSA_DUP_CHK = "USP_GETGSTR2A_TDSA_DUP_CHK";
	public static final String USP_GETGSTR2A_TCS_DUP_CHK = "USP_GETGSTR2A_TCS_DUP_CHK";
	public static final String USP_GETGSTR6A_B2B_DUP_CHK = "USP_GETGSTR6A_B2B_DUP_CHK";
	public static final String USP_GETGSTR6A_B2BA_DUP_CHK = "USP_GETGSTR6A_B2BA_DUP_CHK";
	public static final String USP_GETGSTR6A_CDN_DUP_CHK = "USP_GETGSTR6A_CDN_DUP_CHK";
	public static final String USP_GETGSTR6A_CDNA_DUP_CHK = "USP_GETGSTR6A_CDNA_DUP_CHK";
	public static final String USP_GETGSTR2A_ECOM_DUP_CHK = "USP_GETGSTR2A_ECOM_DUP_CHK";
	public static final String USP_GETGSTR2A_ECOMA_DUP_CHK = "USP_GETGSTR2A_ECOMA_DUP_CHK";

	public static final String GSTR7_CAN_TDSA_PROC = "USP_GSTR7_SAVE_TDSA_CAN";
	public static final String GSTR7_CAN_TDS_PROC = "USP_GSTR7_SAVE_TDS_CAN";

	public static final String SAVE_CROSS_ITC = "SAVE_CROSS_ITC";
	public static final String CALCULATE_R6 = "CALCULATE_R6";

	public static final String NO_DOCUMENT_FOUND = "RET13509";
	public static final String NO_DOCUMENT_FOUND1 = "RET11416";
	public static final String NO_DOCUMENT_FOUND2 = "RET11417";
	public static final String NO_DOCUMENT_FOUND3 = "RET13508";
	public static final String NO_DOCUMENT_FOUND4 = "RET13510";
	public static final String NO_DOCUMENT_FOUND5 = "RETWEB_04";

	public static final String ERP_OUTWARD_PAYLOAD = "OUTWARD_PAYLOAD";
	public static final String ERP_INWARD_PAYLOAD = "INWARD_PAYLOAD";

	// ----------------ITC04 ERP GET ----------------------//
	public static final String ITC04 = "itc04";
	public static final String INVOICES = "invoices";

	public static final String ITC04_RETURN_TYPE = "ITC04";
	public static final String GSTR1_RETURN_TYPE = "GSTR1";
	public static final String GSTR1A_RETURN_TYPE = "GSTR1A";
	public static final String GSTR8_RETURN_TYPE = "GSTR8";

	// Gstr2 ReconResult

	public static final String RESPONSE_3B = "3B-TaxPeriod";
	public static final String FORCE_MATCH = "Force Match";
	public static final String FORCE_MATCH_WITHOUT_3B = "Force Match (Without 3B Lock)";
	public static final String FORCE_MATCH_WITH_3B = "Force Match (With 3B Lock)";

	public static final String LOCK = "Lock";
	public static final String NO_ACTION = "No Action";
	public static final String forceMatch_GSTR3B = "ForceMatch/GSTR3B";
	public static final String UnLock = "UNLOCK";
	public static final String addition2A = "Addition in 2A";
	public static final String additionPR = "Addition in PR";
	public static final String ADDITION2B = "Addition in 2B";
	public static final String RESPONSE_B3 = "3B Response";
	public static final String ADDITION_2A_IMPG = "Addition in 2A IMPG";
	public static final String ADDITION_2B_IMPG = "Addition in 2B IMPG";
	public static final String ADDITION_PR_IMPG = "Addition in PR IMPG";
	public static final String EXACT_MATCH_IMPG = "Exact Match IMPG";
	public static final String MISMATCH_IMPG = "Mismatch IMPG";
	public static final String EXACT_MATCH = "Exact Match";
	public static final String MATCH_WITH_TOLERANCE = "Match With Tolerance";
	// ITC04 Constants
	public static final String M2JW = "m2jw";
	public static final String TABLE5A = "table5a";
	public static final String TABLE5B = "table5b";
	public static final String TABLE5C = "table5c";

	// GSTR2B Constant

	public static final String GSTR2B_GSTIN = "gstin";
	public static final String GSTR2B_TaxPERIOD = "rtnprd";
	public static final String GSTR2B_FILE_NUM = "file_num";
	public static final String GSTR2B = "GSTR2B";
	public static final String GSTR2B_GET_ALL = "GSTR2B_GET_ALL";

	public static final String GSTR2B_ITC_Summary = "itcsumm";
	public static final String GSTR2B_Supplier_Wise_Summary = "cpsumm";
	public static final String GSTR2B_Document_Data = "docdata";

	public static final String GSTR2B_ITC_Available = "itcavl";
	public static final String GSTR2B_ITC_UnAvailable = "itcunavl";
	public static final String GSTR2B_ITC_Rejected = "itcrejected";

	public static final String NonReverseChareSupplies = "nonrevsup";
	public static final String ReverseChareSupplies = "revsup";
	public static final String ISDSupplies = "isdsup";
	public static final String Imports = "imports";
	public static final String OtherSupplies = "othersup";

	public static final String GSTR2B_B2B = "b2b";
	public static final String GSTR2B_B2BA = "b2ba";
	public static final String GSTR2B_CDNR = "cdnr";
	public static final String GSTR2B_CDNRA = "cdnra";
	public static final String GSTR2B_ISD = "isd";
	public static final String GSTR2B_ISDA = "isda";
	public static final String GSTR2B_IMPG = "impg";
	public static final String GSTR2B_IMPGA = "impga";
	public static final String GSTR2B_IMPGSEZ = "impgsez";
	public static final String GSTR2B_IMPGASEZ = "impgasez";
	public static final String GSTR2B_ECOM = "ecom";
	public static final String GSTR2B_ECOMA = "ecoma";

	public static final String GSTR2B_CDNRREV = "cdnrrev";
	public static final String GSTR2B_CDNRAREV = "cdnrarev";

	public static final String COMPUTE_GSTR6_TURN_OVER_GSTIN_PROC = "COMPUTE_GSTR6_TURN_OVER_GSTIN";
	public static final String COMPUTE_GSTR6_TURN_OVER_DIGIGST_PROC = "COMPUTE_GSTR6_TURN_OVER_DIGIGST";
	public static final String COMPUTE_GSTR6_CREDIT_DISTRIBUTION_PROC = "COMPUTE_GSTR6_CREDIT_DISTRIBUTION";
	public static final String COMPUTE_GSTR6_TURN_OVER_USER_INPUT_PROC = "COMPUTE_GSTR6_TURN_OVER_USER_INPUT";
	public static final String COMPUTE_GSTR6_CREDIT_DISTRIBUTION_SPECIFIC_PROC = "COMPUTE_GSTR6_CREDIT_DISTRIBUTION_SPECIFIC";
	public static final String COMPUTE_GSTR6_CREDIT_DISTRIBUTION_LOCK = "COMPUTE_GSTR6_CREDIT_DISTRIBUTION_LOCK";
	public static final String COMPUTE_GSTR6_CREDIT_DISTRIBUTION_LOCK_SPECIFIC = "COMPUTE_GSTR6_CREDIT_DISTRIBUTION_LOCK_SPECIFIC";

	// SftpMonitoringOutward
	public static final String OUTWARD_SFTPFILE_RESPONSE = "OutwardSftpfileResponse";
	public static final String OUTWARD_SFTPFILE_MONITORING = "OutwardSftpfileMonitoring";

	// Gstr9
	public static final String GSTR9 = "GSTR9";
	public static final String GSTR9_SAVE_ERROR_FOLDER = "GSTR9SaveErrorJsons";

	public static final String ITC_REVESAL_180_FOLDER = "ITCReversal180Report";

	public static final String ERP_COMPLAINCE_HISTORY_SUMMARY_SUM = "ERP_COMPLAINCE_HISTORY_SUMMARY_SUM";

	public static final String GSTR1_SAVE_ERROR_JSON_FOLDER = "GSTR1SaveErrorJsons";

	public static final String GSTR1_CUST_INV_REPORT = "GSTR1_TRANS_LEVEL";

	public static final String GSTR2B_TRANSACT_REV_INT = "GSTR2BTransactionRevInt";
	public static final String GSTR2B_SUMMARY_REV_INT = "GSTR2BSummaryRevInt";
	public static final String IRP_BLANK_RESP = "IRP returned Blank Response";

	public static final String GSTR8A = "GSTR8A";
	public static final String NO_REF_ID = "No Ref ID";

	public static final String DRC01B = "drc01B";

	public static final String FRMTYPE = "frmtyp";
	public static final String DRC01C = "drc01C";

	public static final String INV_TYPE_DEXP = "DEXP";

	public static final String INV_TYPE_EXPWP = "EXPWP";

	public static final String INV_TYPE_EXPWOP = "EXPWOP";
	public static final String GET_IRN_LIST = "INWARD_EINVOICE";
	public static final String INV_TYPE_B2B = "B2B";

	public static final String GSTR1_GET_SUPECO = "GSTR1_GET_SUPECO";
	public static final String GSTR1_GET_SUPECO_AMD = "GSTR1_GET_SUPECO_AMD";

	public static final String GSTR3B_ITC_RECLAIM = "GSTR3B_ITC_RECLAIM";
	public static final String ITC_04 = "ITC-04";

	public static final String INV_TYPE_DXP = "DXP";
	public static final String INVMGMT = "INVMGMT";

	public static final String REVERSAL180DAYS_PAYLOAD_METADATA_REV_INTG = "PaymentReference180DaysMetadataRevInt";
	public static final String GSTIN_VALIDATOR_PAYLOAD_METADATA_REV_INTG = "GstinValidatorPayloadMetadataRevIntg";
	
	public static final String VENDOR_VALIDATOR_API_REV_INTG = "VendorValidatorApiRevIntg";
	public static final String GSTR3B_QTR_FILING_PAYLOAD_METADATA_REV_INTG = "Gstr3bQtrFilingPayloadMetadataRevIntg";
	public static final String VENDOR_MASTER_API_PAYLOAD_METADATA_REV_INTG = "VendorMasterApiMetaDataRevIntg";
	
	public static final String INWARD_EINVOICE_UPLOAD = "inward_einvoice_upl";
	
	public static final String RETURN_TYPE_STR = "returnType";

	public static final String GSTR8A_UPLOAD = "gstr8a_upl";
	
	//IMS Constants
	
	public static final String IMS_TYPE_B2B = "B2B";
	public static final String IMS_TYPE_B2BA = "B2BA";
	public static final String IMS_TYPE_CN = "CN";
	public static final String IMS_TYPE_CNA = "CNA";
	public static final String IMS_TYPE_DN = "DN";
	public static final String IMS_TYPE_DNA = "DNA";
	public static final String IMS_TYPE_ECOM = "ECOM";
	public static final String IMS_TYPE_ECOMA = "ECOMA";
	public static final String IMS_COUNT_TYPE_ALL_OTH = "ALL_OTH";
	public static final String IMS_COUNT = "IMS_COUNT";
	public static final String IMS_INVOICE = "IMS_INVOICE";
	public static final String IMS_COUNT_UPL ="IMS_COUNT_UPL";
	public static final String IMS_INVOICE_UPL ="IMS_INVOICE_UPL";
	
	public static final String IMS_TYPE_CDN = "CDN";
	public static final String IMS_TYPE_CDNA = "CDNA";
	
	public static final String IMS_COUNT_TYPE_INV_SUPP_ISD = "INV_SUPP_ISD";
	public static final String IMS_COUNT_TYPE_IMP_GDS = "IMP_GDS";
	
	//GSTr2b Regenerate
	public static final String GSTR2B_REGENERATE = "GSTR2B_REGENERATE";
	
	public static final String GET_IMS_LIST = "IMS_GET";
	
	public static final String GET_IMS_SAVE = "IMS_SAVE";
	
	public static final String IMS_UPLOAD = "ims_upl";
	
	public static final String GL_MASTER_UPLOAD = "gl_Recon_upl";
	
	public static final String SUPPLIER_IMS_INVOICE = "SUPPLIER_IMS";
	
	public static final String SUPPLIER_IMS = "SUPPLIER_IMS";
	
	public static final String IMS_TYPE_CDNR = "CDNR";
	public static final String IMS_TYPE_CDNRA = "CDNRA";
	
	public static final String IMS_RETURN_TYPE_GSTR1 = "GSTR1";
	public static final String IMS_RETURN_TYPE_GSTR1A = "GSTR1A";
	
	public static final String GSTR7_TRANS_VALIDATOR_CHAIN = "Gstr7TransDocRulesValidatorChain";
	
	// GSTR7 Txn
	public static final String TRANSACTIONAL = "TRANSACTIONAL";
	public static final String TRANSACTIONAL_DETAILS = "TRANSACTIONAL_DETAILS";
	public static final String GSTR7_TRANSACTIONAL = "GSTR7_TRANSACTIONAL";

	// GST Notices
		public static final String GST_NOTICE_DTL = "GST_NOTICE";
		public static final String NOTICE_REFID_DTL = "NOTICE_REFID_DTL";
		public static final String NOTICE_DOCID_DTL = "NOTICE_DOCID_DTL";
		
}
