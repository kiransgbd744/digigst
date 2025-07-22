package com.ey.advisory.core.api;

/**
 * This class contains all the GSTN API identifiers. Each API is uniquely
 * identified using a String identifier. This is the key that we use to locate
 * all configuration information about the API. The caller code that invokes the
 * APIExecutor interface should specify one of these values as the API
 * Identifier parameter value, so that the execution can proceed by loading the
 * API configuration details for that particular API.
 * 
 * Note that these identifiers are determined by us. GSTN does not provide
 * unique identifiers for the APIs they expose.
 * 
 * @author Sai.Pakanati
 *
 */
public class APIIdentifiers {

	/**
	 * Making the class non-instantiable.
	 */
	private APIIdentifiers() {
	}

	/* AuthToken API */
	public static final String GET_OTP = "GET_OTP";
	public static final String GET_AUTH_TOKEN = "GET_AUTH_TOKEN";
	public static final String REFRESH_AUTH_TOKEN = "REFRESH_AUTH_TOKEN";
	public static final String GET_GSP_AUTH_TOKEN = "GET_GSP_AUTH_TOKEN";
	public static final String GSTN_TAXPAYER_LOGOUT = "TAXPAYER_LOGOUT";
	public static final String GSTN_PUBLICAPI_LOGOUT = "PUBLICAPI_LOGOUT";

	/* GSTR-1 Core APIs */
	public static final String GSTR1_SAVE = "GSTR1_SAVE";
	public static final String GSTR1_GET_SAVE_STATUS = "GSTR1_GET_SAVE_STATUS";
	public static final String GSTR1_SUBMIT = "GSTR1_SUBMIT";
	public static final String GSTR1_FILE = "GSTR1_FILE";
	public static final String GSTR6_CALCULATE_R6 = "GSTR6_CALCULATE_R6";
	public static final String GSTR6_SAVE_CROSS_ITC = "GSTR6_SAVE_CROSS_ITC";

	/* All Get APIs for GSTR-1 */
	public static final String GSTR1_GET_AT = "GSTR1_GET_AT";
	public static final String GSTR1_GET_ATA = "GSTR1_GET_ATA";
	public static final String GSTR1_GET_B2B = "GSTR1_GET_B2B";
	public static final String GSTR1_GET_B2BA = "GSTR1_GET_B2BA";
	public static final String GSTR1_GET_B2CL = "GSTR1_GET_B2CL";
	public static final String GSTR1_GET_B2CLA = "GSTR1_GET_B2CLA";
	public static final String GSTR1_GET_B2CS = "GSTR1_GET_B2CS";
	public static final String GSTR1_GET_B2CSA = "GSTR1_GET_B2CSA";
	public static final String GSTR1_GET_CDNR = "GSTR1_GET_CDNR";
	public static final String GSTR1_GET_CDNRA = "GSTR1_GET_CDNRA";
	public static final String GSTR1_GET_CDNUR = "GSTR1_GET_CDNUR";
	public static final String GSTR1_GET_CDNURA = "GSTR1_GET_CDNURA";
	public static final String GSTR1_GET_EXP = "GSTR1_GET_EXP";
	public static final String GSTR1_GET_EXPA = "GSTR1_GET_EXPA";
	public static final String GSTR1_GET_TXP = "GSTR1_GET_TXP";
	public static final String GSTR1_GET_TXPA = "GSTR1_GET_TXPA";
	public static final String GSTR1_GET_DOC_ISSUED = "GSTR1_GET_DOC_ISSUED";
	public static final String GSTR1_GET_SUMMARY = "GSTR1_GET_SUMMARY";
	public static final String GSTR1_GET_HSN_SUMMARY = "GSTR1_GET_HSN_SUMMARY";
	public static final String GSTR1_GET_NIL_RATED = "GSTR1_GET_NIL_RATED";
	public static final String GSTR1_GETSUM = "GSTR1_GETSUM";
	public static final String GSTR1_GET_EINV = "GSTR1_GET_EINV";

	/* All Get APIs for GSTR-2A */
	public static final String GSTR2A_GET_B2B = "GSTR2A_GET_B2B";
	public static final String GSTR2A_GET_B2BA = "GSTR2A_GET_B2BA";
	public static final String GSTR2A_GET_CDN = "GSTR2A_GET_CDN";
	public static final String GSTR2A_GET_CDNA = "GSTR2A_GET_CDNA";
	public static final String GSTR2A_GET_ISD = "GSTR2A_GET_ISD";
	public static final String GSTR2A_GET_ISDA = "GSTR2A_GET_ISDA";
	public static final String GSTR2X_GET_TCS_TDS = "GSTR2X_GET_TCS_TDS";
	public static final String GSTR2A_GET_TDS = "GSTR2X_GET_TDS";
	public static final String GSTR2A_GET_TDSA = "GSTR2A_GET_TDSA";
	public static final String GSTR2A_GET_TCS = "GSTR2A_GET_TCS";
	public static final String GSTR2A_GET_TCSA = "GSTR2A_GET_TCSA";
	public static final String GSTR2A_GET_AMDHIST = "GSTR2A_GET_AMDHIST";
	public static final String GSTR2A_GET_IMPG = "GSTR2A_GET_IMPG";
	public static final String GSTR2A_GET_IMPGSEZ = "GSTR2A_GET_IMPGSEZ";

	/* All Get APIs for Ledger */
	public static final String LEDGER_GET_BAL = "LEDGER_GET_BAL";
	public static final String LEDGER_GET_CASH = "LEDGER_GET_CASH";
	public static final String LEDGER_GET_ITC = "LEDGER_GET_ITC";
	public static final String LEDGER_GET_TAX = "LEDGER_GET_TAX";
	public static final String LEDGER_GET_CRREV_RECLAIM = "LEDGER_GET_CRREV_RECLAIM";
	public static final String LEDGER_GET_CRREV_AND_RECLAIM_CASH = "LEDGER_GET_CRREV_AND_RECLAIM_CASH";
	public static final String LEDGER_GET_NRTN = "LEDGER_GET_NRTN";
	public static final String LEDGER_GET_TAXPAYABLE = "LEDGER_GET_TAXPAYABLE";
	public static final String LEDGER_GET_CRECAS_BAL = "LEDGER_GET_CRECAS_BAL";
	public static final String LEDGER_GET_LIAB_BAL = "LEDGER_GET_LIAB_BAL";
	
	public static final String LEDGER_GET_RCM = "LEDGER_GET_RCM";
	
	public static final String LEDGER_GET_RECLAIM = "LEDGER_GET_RECLAIM";
		
	public static final String LEDGER_GET_LIABILITY_RCM = "LEDGER_GET_LIABILITY_RCM";
		
	public static final String LEDGER_GET_LIABILITY_NEGATIVE = "LEDGER_GET__LIABILITY_NEGATIVE";
	
	public static final String LEDGER_GET_DETAILED_RCM = "LEDGER_GET_RCM";

		

	/* All Get APIs for ANX-2 */
	// public static final String ANX2_GET_GENSUM = "ANX2_GET_GENSUM";
	// public static final String ANX2_GET_GETSUM = "ANX2_GET_GETSUM";
	public static final String ANX2_GET_B2B = "ANX2_GET_B2B";
	public static final String ANX2_GET_DE = "ANX2_GET_DE";
	public static final String ANX2_GET_ISDC = "ANX2_GET_ISDC";
	public static final String ANX2_GET_ITCSUM = "ANX2_GET_ITCSUM";
	public static final String ANX2_GET_SEZWOP = "ANX2_GET_SEZWOP";
	public static final String ANX2_GET_SEZWP = "ANX2_GET_SEZWP";
	public static final String ANX2_GETSUM = "ANX2_GETSUM";
	public static final String ANX2_GENSUM = "ANX2_GENSUM";

	public static final String ANX2_GET_SAVEANX2 = "ANX2_GET_SAVEANX2";
	public static final String ANX2_GET_GETPMT = "ANX2_GET_GETPMT";
	public static final String ANX2_GET_SAVEPMT = "ANX2_GET_SAVEPMT";
	public static final String ANX2_GET_GETPROFILE = "ANX2_GET_GETPROFILE";
	public static final String ANX2_GET_SAVPROFILE = "ANX2_GET_SAVPROFILE";

	/* All Get APIs for ANX-1 */
	public static final String ANX1_GET_B2B = "ANX1_GET_B2B";
	public static final String ANX1_GET_B2BA = "ANX1_GET_B2BA";
	public static final String ANX1_GET_B2C = "ANX1_GET_B2C";
	public static final String ANX1_GET_REV = "ANX1_GET_REV";
	public static final String ANX1_GET_ECOM = "ANX1_GET_ECOM";
	public static final String ANX1_GET_DE = "ANX1_GET_DE";
	public static final String ANX1_GET_DEA = "ANX1_GET_DEA";
	public static final String ANX1_GET_MIS = "ANX1_GET_MIS";
	public static final String ANX1_GET_EXPWOP = "ANX1_GET_EXPWOP";
	public static final String ANX1_GET_EXPWP = "ANX1_GET_EXPWP";
	public static final String ANX1_GET_IMPGSEZ = "ANX1_GET_IMPGSEZ";
	public static final String ANX1_GET_IMPS = "ANX1_GET_IMPS";
	public static final String ANX1_GET_IMPG = "ANX1_GET_IMPG";
	public static final String ANX1_GET_SEZWP = "ANX1_GET_SEZWP";
	public static final String ANX1_GET_SEZWPA = "ANX1_GET_SEZWPA";
	public static final String ANX1_GET_SEZWOP = "ANX1_GET_SEZWOP";
	public static final String ANX1_GET_SEZWOPA = "ANX1_GET_SEZWOPA";
	public static final String ANX1_GENSUM = "ANX1_GENSUM";
	public static final String ANX1_GETSUM = "ANX1_GETSUM";

	/* All Get APIs for GSTR-6 */
	public static final String GSTR6_GET_B2B = "GSTR6_GET_B2B";
	public static final String GSTR6_GET_B2BA = "GSTR6_GET_B2BA";
	public static final String GSTR6_GET_CDN = "GSTR6_GET_CDN";
	public static final String GSTR6_GET_CDNA = "GSTR6_GET_CDNA";
	public static final String GSTR6_GET_ISD = "GSTR6_GET_ISD";
	public static final String GSTR6_GET_ISDA = "GSTR6_GET_ISDA";
	public static final String GSTR6_GET_ITC = "GSTR6_GET_ITC";
	public static final String GSTR6_GET_LATEFEE = "GSTR6_GET_LATEFEE";
	public static final String GSTR6_GETSUM = "GSTR6_GETSUM";
	public static final String GSTR7_GETSUM = "GSTR7_GETSUM";
	public static final String GSTR8_GETSUM = "GSTR8_GETSUM";
	public static final String ITC04_GETSUM = "ITC04_GETSUM";

	/* All Get APIs for GSTR-6A */
	public static final String GSTR6A_GET_B2B = "GSTR6A_GET_B2B";
	public static final String GSTR6A_GET_B2BA = "GSTR6A_GET_B2BA";
	public static final String GSTR6A_GET_CDN = "GSTR6A_GET_CDN";
	public static final String GSTR6A_GET_CDNA = "GSTR6A_GET_CDNA";

	public static final String RET_GET = "RET_GET_ALL";
	public static final String GSTR6_GET = "GSTR6_GET_ALL";

	/* All Get APIs for GSTR-8 */
	public static final String GSTR8_GET_TCS = "GSTR8_GET_TCS";
	public static final String GSTR8_GET_URD = "GSTR8_GET_URD";
	
	/* ANX-1 Core APIs */
	public static final String ANX1_SAVE = "ANX1_SAVE";
	public static final String ANX1_GET_SAVE_STATUS = "ANX1_GET_SAVE_STATUS";
	public static final String ANX1_SUBMIT = "ANX1_SUBMIT";
	public static final String ANX1_FILE = "ANX1_FILE";
	public static final String GET_GSTR3B = "ANX1_GETSUM";

	/* ANX-2 Core APIs */
	public static final String ANX2_SAVE = "ANX2_SAVE";
	public static final String ANX2_GET_SAVE_STATUS = "ANX2_GET_SAVE_STATUS";
	public static final String ANX2_SUBMIT = "ANX2_SUBMIT";
	public static final String ANX2_FILE = "ANX2_FILE";

	/* Common for Save API */
	public static final String GSTIN = "gstin";
	public static final String RET_PERIOD = "ret_period";
	public static final String VERSION_1_0 = "v1.0";
	public static final String VERSION_1_1 = "v1.1";
	public static final String VERSION_0_3 = "v0.3";
	public static final String REF_ID = "ref_id";
	public static final String REFERENECE_ID = "reference_id";
	public static final String GENERATE_SUMMARY = "generate_summary";
	public static final String ISNIL = "isnil";
	public static final String NIL_RETURN = "NIL_RETURN";
	/* getUrlList */
	public static final String GET_FILE_DETAILS = "GET_FILE_DETAILS";

	// GSTR3B
	public static final String GSTR3B_SAVE = "GSTR3B_SAVE";
	public static final String GET_GSTR3B_SUMMARY = "GET_GSTR3B";
	public static final String GSTR3B_SUBMIT = "GSTR3B_SUBMIT";
	public static final String GSTR3B_SAVE_OFFSET = "GSTR3B_SAVE_OFFSET";
	public static final String GSTR3B_GET_AUTO_CALC = "GSTR3B_GET_AUTO_CALC";
	public static final String GSTR3B_FILE = "GSTR3B_FILE";
	public static final String GSTR3B_RECOMPINT = "GSTR3B_RECOMPINT";
	public static final String GSTR3B_SAVEPAST_LIABILITY = "GSTR3B_SAVEPAST_LIABILITY";

	/* public Apis */ // GSTR3B
	public static final String GET_GSTIN_DETAILS = "GET_GSTIN_DETAILS";

	// Save GSTR6
	public static final String GSTR6_SAVE = "GSTR6_SAVE";

	public static final String GET_FILLING_STATUS = "GET_RETURN_FILING";

	// Save GSTR7
	public static final String GSTR7_SAVE = "GSTR7_SAVE";
	public static final String GSTR7_GET_TDS = "GSTR7_GET_TDS";
	public static final String GSTR7_GET_CHECKSUM = "GSTR7_GET_CHECKSUM";

	// Get ITC04
	public static final String ITC04_GET_INVOICES = "ITC04_GET_INVOICES";

	public static final String ITC04_SAVE = "ITC04_SAVE";

	// Gstr2B
	public static final String GSTR2B_GET = "GSTR2B_GET";

	public static final String GSTR6_SUBMIT = "GSTR6_SUBMIT";

	public static final String GSTR2X_SAVE = "GSTR2X_SAVE";

	// SiginFile
	public static final String GSTR7_FILE = "GSTR7_FILE";
	public static final String ITC04_FILE = "ITC04_FILE";

	public static final String GSTR1_RESET = "GSTR1_RESET";

	// Gstr9
	public static final String GSTR9_GETDETAILS = "GSTR9_GETDETAILS";
	public static final String GSTR9_AUTOCAlCDETAILS = "GSTR9_AUTOCALCDETAILS";
	public static final String GSTR9_SAVE = "GSTR9_SAVE";

	// Gstr8a
	public static final String GSTR8A_GETDETAILS = "GSTR8A_GETDETAILS";

	public static final String GSTR6_FILE = "GSTR6_FILE";

	public static final String GSTR6_PROCEED_TO_FILE = "GSTR6_PROCEED_TO_FILE";

	public static final String GSTR3B_GET_INTEREST_AUTO_CALC = "GSTR3B_GET_INTEREST_AUTO_CALC";

	// EVC SIGN AND FILE
	public static final String EVC_SIGN_FILE = "EVC_OTP";

	public static final String DRC = "DRC01B";

	public static final String DRC_GETRETCOMP_LIST = "DRC_GETRETCOMP_LIST";

	public static final String DRC_GETRETCOMP_SUMMARY = "DRC_GETRETCOMP_SUMMARY";

	public static final String DRC01B_SAVE = "DRC01B_SAVE";
	public static final String DRC_Form_Type = "DRC01B";
	public static final String DRC01B = "DRC01B";

	public static final String DRC01B_FILE = "DRC01B_FILE";
	public static final String DRC01C = "DRC01C";

	public static final String DRC01C_GETRETCOMP_SUMMARY = "DRC01C_GETRETCOMP_SUMMARY";

	public static final String DRC01C_GETRETCOMP_LIST = "DRC01C_GETRETCOMP_LIST";
	public static final String DRC01C_SAVE = "DRC01C_SAVE";
	public static final String DRC01C_Form_Type = "DRC01C";
	public static final String DRC01C_FILE = "DRC01C_FILE";

	public static final String GET_IRN_LIST = "INWARD_EINVOICE";
	public static final String GET_IRN_DTL = "GET_IRN_DTL";
	public static final String GET_FILE_LIST = "GET_FILE_LIST";

	public static final String GSTR1_GET_SUPECO = "GSTR1_GET_SUPECO";
	public static final String GSTR1_GET_SUPECO_AMD = "GSTR1_GET_SUPECO_AMD";
	public static final String GSTR1_GET_ECOM = "GSTR1_GET_ECOM";
	public static final String GSTR1_GET_ECOM_AMD = "GSTR1_GET_ECOM_AMD";
	
	public static final String GSTR3B_ITC_RECLAIM = "GSTR3B_ITC_RECLAIM";
	
	public static final String VENDOR_GET_PREFERENCE = "VENDOR_GET_PREFERENCE";

	public static final String GSTR8_SAVE = "GSTR8_SAVE";
	
	public static final String GSTR2A_GET_ECOM = "GSTR2A_GET_ECOM";
	public static final String GSTR2A_GET_ECOMA = "GSTR2A_GET_ECOMA";
	
	//gstr1A
	public static final String GSTR1A_RESET = "GSTR1A_RESET";
	

	/* All Get APIs for GSTR-1A */
	public static final String GSTR1A_GET_AT = "GSTR1A_GET_AT";
	public static final String GSTR1A_GET_ATA = "GSTR1A_GET_ATA";
	public static final String GSTR1A_GET_B2B = "GSTR1A_GET_B2B";
	public static final String GSTR1A_GET_B2BA = "GSTR1A_GET_B2BA";
	public static final String GSTR1A_GET_B2CL = "GSTR1A_GET_B2CL";
	public static final String GSTR1A_GET_B2CLA = "GSTR1A_GET_B2CLA";
	public static final String GSTR1A_GET_B2CS = "GSTR1A_GET_B2CS";
	public static final String GSTR1A_GET_B2CSA = "GSTR1A_GET_B2CSA";
	public static final String GSTR1A_GET_CDNR = "GSTR1A_GET_CDNR";
	public static final String GSTR1A_GET_CDNRA = "GSTR1A_GET_CDNRA";
	public static final String GSTR1A_GET_CDNUR = "GSTR1A_GET_CDNUR";
	public static final String GSTR1A_GET_CDNURA = "GSTR1A_GET_CDNURA";
	public static final String GSTR1A_GET_EXP = "GSTR1A_GET_EXP";
	public static final String GSTR1A_GET_EXPA = "GSTR1A_GET_EXPA";
	public static final String GSTR1A_GET_TXP = "GSTR1A_GET_TXP";
	public static final String GSTR1A_GET_TXPA = "GSTR1A_GET_TXPA";
	public static final String GSTR1A_GET_DOC_ISSUED = "GSTR1A_GET_DOC_ISSUED";
	public static final String GSTR1A_GET_SUMMARY = "GSTR1A_GET_SUMMARY";
	public static final String GSTR1A_GET_HSN_SUMMARY = "GSTR1A_GET_HSN_SUMMARY";
	public static final String GSTR1A_GET_NIL_RATED = "GSTR1A_GET_NIL_RATED";
	public static final String GSTR1A_GETSUM = "GSTR1A_GETSUM";
	public static final String GSTR1A_GET_EINV = "GSTR1A_GET_EINV";
	
	public static final String GSTR1A_GET_SUPECO = "GSTR1A_GET_SUPECO";
	public static final String GSTR1A_GET_SUPECO_AMD = "GSTR1A_GET_SUPECO_AMD";
	public static final String GSTR1A_GET_ECOM = "GSTR1A_GET_ECOM";
	public static final String GSTR1A_GET_ECOM_AMD = "GSTR1A_GET_ECOM_AMD";
	public static final String GSTR1A_SAVE = "GSTR1A_SAVE";
	public static final String GSTR1A_GET_SAVE_STATUS = "GSTR1A_GET_SAVE_STATUS";
	public static final String GSTR1A_PROCEED_TO_FILE = "GSTR1A_PROCEED_TO_FILE";
	public static final String GSTR1A_FILE = "GSTR1A_FILE";
	
	public static final String IMS_COUNT = "IMS_COUNT";
	public static final String IMS_INVOICE = "IMS_INVOICE";
	public static final String IMS_FILE_LIST = "IMS_FILE_LIST";
	
	public static final String GSTR2B_REGENERATE = "GSTR2B_REGENERATE";
	public static final String GSTR2B_REG_SAVE_STATUS = "GSTR2B_REG_SAVE_STATUS";
	
	public static final String IMS_SAVE = "IMS_SAVE";
	
	public static final String IMS_SAVE_STATUS = "IMS_SAVE_STATUS";
	
	//RCM save
	public static final String SAVE_TO_GSTN_RCM = "SAVE_TO_GSTN_RCM";
	public static final String SAVE_TO_GSTN_RECLAIM = "SAVE_TO_GSTN_RECLAIM";
	public static final String GET_RCM_LEDGER_DETAILS ="GET_RCM_LEDGER_DETAILS";
	public static final String NEGATIVE_LIABILITY ="NEGATIVE_LIABILITY";
	public static final String GSTR3B_NEGATIVE_LIABILITY ="GSTR3B_NEGATIVE_LIABILITY";

	public static final String IMS_RESET = "IMS_RESET";
	
	public static final String GET_IRN_JSON = "GET_IRN_JSON";
	
    public static final String SUPPLIER_IMS = "SUPPLIER_IMS";
	
	public static final String IMS_SUPPLIER_GET = "IMS_SUPPLIER_GET";
	
	//GST Notices
		public static final String GST_NOTICE_DTL = "GST_NOTICE";
		public static final String NOTICE_REFID_DTL = "NOTICE_REFID_DTL";
		public static final String NOTICE_DOCID_DTL = "NOTICE_DOCID_DTL";
		
	
	


}
