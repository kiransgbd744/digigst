package com.ey.advisory.core.api;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class GSTNAPIUtil {

	/**
	 * Make the class non-instantiable.
	 */
	private GSTNAPIUtil() {
	}

	/**
	 * Depending on the type of request
	 * 
	 * @param identifier
	 * @return
	 */
	public static boolean isAuthRelatedRequest(String identifier) {
		return APIIdentifiers.GET_AUTH_TOKEN.equals(identifier)
				|| APIIdentifiers.GET_OTP.equals(identifier)
				|| APIIdentifiers.REFRESH_AUTH_TOKEN.equals(identifier)
				|| APIIdentifiers.GET_GSP_AUTH_TOKEN.equals(identifier)
				|| APIIdentifiers.GSTN_TAXPAYER_LOGOUT.equals(identifier)
				|| APIIdentifiers.GSTN_PUBLICAPI_LOGOUT.equals(identifier);
	}

	/**
	 * Depending on the type of request
	 * 
	 * @param identifier
	 * @return
	 */
	public static boolean isNewReturnApiRequest(String identifier) {
		return APIIdentifiers.ANX1_SAVE.equals(identifier)
				|| APIIdentifiers.ANX2_SAVE.equals(identifier)
				|| APIIdentifiers.ANX1_GET_B2C.equals(identifier)
				|| APIIdentifiers.ANX1_GET_B2B.equals(identifier)
				|| APIIdentifiers.ANX1_GET_EXPWP.equals(identifier)
				|| APIIdentifiers.ANX1_GET_EXPWOP.equals(identifier)
				|| APIIdentifiers.ANX1_GET_SEZWP.equals(identifier)
				|| APIIdentifiers.ANX1_GET_SEZWOP.equals(identifier)
				|| APIIdentifiers.ANX1_GET_DE.equals(identifier)
				|| APIIdentifiers.ANX1_GET_REV.equals(identifier)
				|| APIIdentifiers.ANX1_GET_IMPS.equals(identifier)
				|| APIIdentifiers.ANX1_GET_IMPG.equals(identifier)
				|| APIIdentifiers.ANX1_GET_IMPGSEZ.equals(identifier)
				|| APIIdentifiers.ANX1_GET_MIS.equals(identifier)
				|| APIIdentifiers.ANX1_GET_ECOM.equals(identifier)
				|| APIIdentifiers.ANX1_GET_SAVE_STATUS.equals(identifier)
				|| APIIdentifiers.ANX1_GETSUM.equals(identifier)
				|| APIIdentifiers.ANX1_GENSUM.equals(identifier)
				|| APIIdentifiers.RET_GET.equals(identifier)
				|| APIIdentifiers.ANX2_GETSUM.equals(identifier)
				|| APIIdentifiers.ANX2_GENSUM.equals(identifier)
				|| APIIdentifiers.ANX2_GET_B2B.equals(identifier)
				|| APIIdentifiers.ANX2_GET_DE.equals(identifier)
				|| APIIdentifiers.ANX2_GET_SEZWOP.equals(identifier)
				|| APIIdentifiers.ANX2_GET_SEZWP.equals(identifier)
				|| APIIdentifiers.ANX2_GET_ISDC.equals(identifier)
				|| APIIdentifiers.ANX2_GET_ITCSUM.equals(identifier)
				|| APIIdentifiers.GSTR6_GET_B2B.equals(identifier)
				|| APIIdentifiers.GSTR6_GET_B2BA.equals(identifier)
				|| APIIdentifiers.GSTR6_GET_CDN.equals(identifier)
				|| APIIdentifiers.GSTR6_GET_CDNA.equals(identifier)
				|| APIIdentifiers.GSTR6_GET_ISD.equals(identifier)
				|| APIIdentifiers.GSTR6_GET_ITC.equals(identifier)
				|| APIIdentifiers.GSTR6_GET_LATEFEE.equals(identifier)
				|| APIIdentifiers.GSTR6A_GET_B2B.equals(identifier)
				|| APIIdentifiers.GSTR6A_GET_B2BA.equals(identifier)
				|| APIIdentifiers.GSTR6A_GET_CDN.equals(identifier)
				|| APIIdentifiers.GSTR6A_GET_CDNA.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_B2B.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_B2BA.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_CDN.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_CDNA.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_ISD.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_ISDA.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_TCS.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_TDS.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_TDSA.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_IMPG.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_IMPGSEZ.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_AMDHIST.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_ECOM.equals(identifier)
				|| APIIdentifiers.GSTR2A_GET_ECOMA.equals(identifier)
				|| APIIdentifiers.GSTR7_SAVE.equals(identifier)
				|| APIIdentifiers.ITC04_SAVE.equals(identifier)
				|| APIIdentifiers.GSTR2X_GET_TCS_TDS.equals(identifier)
				|| APIIdentifiers.GSTR2X_SAVE.equals(identifier)
				|| APIIdentifiers.GSTR1_RESET.equals(identifier)
				|| APIIdentifiers.GSTR6_PROCEED_TO_FILE.equals(identifier)
				|| APIIdentifiers.GSTR9_SAVE.equals(identifier)
				|| APIIdentifiers.GSTR1A_PROCEED_TO_FILE.equals(identifier)
				|| APIIdentifiers.GSTR1A_RESET.equals(identifier)
				|| APIIdentifiers.GSTR2B_REGENERATE.equals(identifier)
				|| APIIdentifiers.IMS_SAVE.equals(identifier);
	}

	/**
	 * Depending on the type of request
	 * 
	 * @param identifier
	 * @return
	 */
	public static boolean isPublicApiRelatedRequest(String identifier) {
		return APIIdentifiers.GET_FILLING_STATUS.equals(identifier)
				|| APIIdentifiers.GET_GSP_AUTH_TOKEN.equals(identifier)
				|| APIIdentifiers.GET_GSTIN_DETAILS.equals(identifier)
				|| APIIdentifiers.VENDOR_GET_PREFERENCE.equalsIgnoreCase(identifier);
	}

	public static boolean isSignAndFileRequest(String identifier) {
		return APIIdentifiers.GSTR3B_FILE.equals(identifier)
				|| APIIdentifiers.GSTR1_FILE.equals(identifier)
				|| APIIdentifiers.GSTR6_FILE.equals(identifier)
				|| APIIdentifiers.ITC04_FILE.equals(identifier)
				|| APIIdentifiers.GSTR1A_FILE.equals(identifier);
	}

	public static boolean isSaveRequest(String identifier) {
		return APIIdentifiers.GSTR1_SAVE.equals(identifier)
				|| APIIdentifiers.ANX1_SAVE.equals(identifier)
				|| APIIdentifiers.ANX2_SAVE.equals(identifier)
				|| APIIdentifiers.GSTR6_SAVE.equals(identifier)
				|| APIIdentifiers.GSTR6_CALCULATE_R6.equals(identifier)
				|| APIIdentifiers.GSTR6_SAVE_CROSS_ITC.equals(identifier)
				|| APIIdentifiers.GSTR7_SAVE.equals(identifier)
				|| APIIdentifiers.ITC04_SAVE.equals(identifier)
				|| APIIdentifiers.GSTR2X_SAVE.equals(identifier)
				|| APIIdentifiers.GSTR1_RESET.equals(identifier)
		        || APIIdentifiers.GSTR1A_RESET.equals(identifier)
		        || APIIdentifiers.GSTR1A_SAVE.equals(identifier)
		        || APIIdentifiers.IMS_SAVE.equals(identifier)
		        || APIIdentifiers.SAVE_TO_GSTN_RCM.equals(identifier)
		        || APIIdentifiers.SAVE_TO_GSTN_RECLAIM.equals(identifier);
	}

	public static boolean isGetReturnStatusRequest(String identifier) {
		return APIIdentifiers.ANX1_GET_SAVE_STATUS.equals(identifier)
				|| APIIdentifiers.GSTR1_GET_SAVE_STATUS.equals(identifier)
				|| APIIdentifiers.GSTR1A_GET_SAVE_STATUS.equals(identifier);
	}

	// GSTR1
	public static Map<String, String> gstr1GetAPI = ImmutableMap
			.<String, String>builder().put(APIIdentifiers.GSTR1_GET_AT, "AT")
			.put(APIIdentifiers.GSTR1_GET_ATA, "ATA")
			.put(APIIdentifiers.GSTR1_GET_B2B, "B2B")
			.put(APIIdentifiers.GSTR1_GET_B2BA, "B2BA")
			.put(APIIdentifiers.GSTR1_GET_B2CL, "B2CL")
			.put(APIIdentifiers.GSTR1_GET_B2CLA, "B2CLA")
			.put(APIIdentifiers.GSTR1_GET_B2CS, "B2CS")
			.put(APIIdentifiers.GSTR1_GET_B2CSA, "B2CSA")
			.put(APIIdentifiers.GSTR1_GET_CDNR, "CDNR")
			.put(APIIdentifiers.GSTR1_GET_CDNRA, "CDNRA")
			.put(APIIdentifiers.GSTR1_GET_CDNUR, "CDNUR")
			.put(APIIdentifiers.GSTR1_GET_CDNURA, "CDNURA")
			.put(APIIdentifiers.GSTR1_GET_EXP, "EXP")
			.put(APIIdentifiers.GSTR1_GET_EXPA, "EXPA")
			.put(APIIdentifiers.GSTR1_GET_TXP, "TXP")
			.put(APIIdentifiers.GSTR1_GET_TXPA, "TXPA")
			.put(APIIdentifiers.GSTR1_GET_DOC_ISSUED, "DOC_ISSUE")
			.put(APIIdentifiers.GSTR1_GET_SUMMARY, "GSTR1_GET_SUMMARY")
			.put(APIIdentifiers.GSTR1_GET_HSN_SUMMARY, "HSN_SUMMARY")
			.put(APIIdentifiers.GSTR1_GET_NIL_RATED, "NIL_RATED")
			.put(APIIdentifiers.GSTR1_GETSUM, "GETSUM")
			.put(APIIdentifiers.GSTR1_GET_SUPECO,
					APIConstants.SUPECOM.toUpperCase())
			.put(APIIdentifiers.GSTR1_GET_SUPECO_AMD,
					APIConstants.SUPECOAMD.toUpperCase())
			.put(APIIdentifiers.GSTR1_GET_ECOM, "ECOM")
			.put(APIIdentifiers.GSTR1_GET_ECOM_AMD,
					APIConstants.ECOMAMD.toUpperCase())
			.build();

	// Gstr2A
	public static Map<String, String> gstr2AGetAPI = ImmutableMap
			.<String, String>builder().put(APIIdentifiers.GSTR2A_GET_B2B, "B2B")
			.put(APIIdentifiers.GSTR2A_GET_B2BA, "B2BA")
			.put(APIIdentifiers.GSTR2A_GET_CDN, "CDN")
			.put(APIIdentifiers.GSTR2A_GET_CDNA, "CDNA")
			.put(APIIdentifiers.GSTR2A_GET_ISD, "ISD")
			.put(APIIdentifiers.GSTR2A_GET_ISDA, "ISDA")
			.put(APIIdentifiers.GSTR2A_GET_TCS, "TCS")
			.put(APIIdentifiers.GSTR2A_GET_TDS, "TDS")
			.put(APIIdentifiers.GSTR2A_GET_TDSA, "TDSA")
			.put(APIIdentifiers.GSTR2A_GET_IMPG, "IMPG")
			.put(APIIdentifiers.GSTR2A_GET_IMPGSEZ, "IMPGSEZ")
			.put(APIIdentifiers.GSTR2A_GET_AMDHIST, "AMDHIST")
			.put("ECOM", APIIdentifiers.GSTR2A_GET_ECOM)
			.put("ECOMA", APIIdentifiers.GSTR2A_GET_ECOMA)
			.build();
	// Gstr2A
	public static Map<String, String> gstr2XGetAPI = ImmutableMap
			.<String, String>builder()
			.put(APIIdentifiers.GSTR2X_GET_TCS_TDS, APIConstants.TCSANDTDS)
			.build();

	public static Map<String, String> itc04GetAPI = ImmutableMap
			.<String, String>builder()
			.put(APIIdentifiers.ITC04_GET_INVOICES, APIConstants.GET).build();

	// Gstr7
	public static Map<String, String> gstr7GetAPI = ImmutableMap
			.<String, String>builder()
			.put(APIIdentifiers.GSTR7_GET_TDS, APIConstants.GSTR7_TDS_DETAILS)
			.build();

	public static Map<String, String> gstr6GetAPI = ImmutableMap
			.<String, String>builder().put(APIIdentifiers.GSTR6_GET_B2B, "B2B")
			.put(APIIdentifiers.GSTR6_GET_B2BA, "B2BA")
			.put(APIIdentifiers.GSTR6_GET_CDN, "CDN")
			.put(APIIdentifiers.GSTR6_GET_CDNA, "CDNA")
			.put(APIIdentifiers.GSTR6_GET_ISD, "ISD")
			.put(APIIdentifiers.GSTR6_GET_ISDA, "ISDA").build();

	// Gstr2A
	public static Map<String, String> gstr2AGetAPIIdentifier = ImmutableMap
			.<String, String>builder().put("B2B", APIIdentifiers.GSTR2A_GET_B2B)
			.put("B2BA", APIIdentifiers.GSTR2A_GET_B2BA)
			.put("CDN", APIIdentifiers.GSTR2A_GET_CDN)
			.put("CDNA", APIIdentifiers.GSTR2A_GET_CDNA)
			.put("ISD", APIIdentifiers.GSTR2A_GET_ISD)
			.put("ISDA", APIIdentifiers.GSTR2A_GET_ISDA)
			.put("TCS", APIIdentifiers.GSTR2A_GET_TCS)
			.put("TDS", APIIdentifiers.GSTR2A_GET_TDS)
			.put("TDSA", APIIdentifiers.GSTR2A_GET_TDSA)
			.put("IMPG", APIIdentifiers.GSTR2A_GET_IMPG)
			.put("IMPGSEZ", APIIdentifiers.GSTR2A_GET_IMPGSEZ)
			.put("AMDHIST", APIIdentifiers.GSTR2A_GET_AMDHIST)
			.put("ECOM", APIIdentifiers.GSTR2A_GET_ECOM)
			.put("ECOMA", APIIdentifiers.GSTR2A_GET_ECOMA)
			.build();

	//Gstr8
		public static Map<String, String> gstr8GetAPI = ImmutableMap
				.<String, String>builder().put(APIIdentifiers.GSTR8_GET_TCS, "TCS")
				.put(APIIdentifiers.GSTR8_GET_URD, "URD")
				.build();
		
		// GSTR1A
		public static Map<String, String> gstr1AGetAPI = ImmutableMap
				.<String, String>builder().put(APIIdentifiers.GSTR1A_GET_AT, "AT")
				.put(APIIdentifiers.GSTR1A_GET_ATA, "ATA")
				.put(APIIdentifiers.GSTR1A_GET_B2B, "B2B")
				.put(APIIdentifiers.GSTR1A_GET_B2BA, "B2BA")
				.put(APIIdentifiers.GSTR1A_GET_B2CL, "B2CL")
				.put(APIIdentifiers.GSTR1A_GET_B2CLA, "B2CLA")
				.put(APIIdentifiers.GSTR1A_GET_B2CS, "B2CS")
				.put(APIIdentifiers.GSTR1A_GET_B2CSA, "B2CSA")
				.put(APIIdentifiers.GSTR1A_GET_CDNR, "CDNR")
				.put(APIIdentifiers.GSTR1A_GET_CDNRA, "CDNRA")
				.put(APIIdentifiers.GSTR1A_GET_CDNUR, "CDNUR")
				.put(APIIdentifiers.GSTR1A_GET_CDNURA, "CDNURA")
				.put(APIIdentifiers.GSTR1A_GET_EXP, "EXP")
				.put(APIIdentifiers.GSTR1A_GET_EXPA, "EXPA")
				.put(APIIdentifiers.GSTR1A_GET_TXP, "TXP")
				.put(APIIdentifiers.GSTR1A_GET_TXPA, "TXPA")
				.put(APIIdentifiers.GSTR1A_GET_DOC_ISSUED, "DOC_ISSUE")
				.put(APIIdentifiers.GSTR1A_GET_SUMMARY, "GSTR1_GET_SUMMARY")
				.put(APIIdentifiers.GSTR1A_GET_HSN_SUMMARY, "HSN_SUMMARY")
				.put(APIIdentifiers.GSTR1A_GET_NIL_RATED, "NIL_RATED")
				.put(APIIdentifiers.GSTR1A_GETSUM, "GETSUM")
				.put(APIIdentifiers.GSTR1A_GET_SUPECO,
						APIConstants.SUPECOM.toUpperCase())
				.put(APIIdentifiers.GSTR1A_GET_SUPECO_AMD,
						APIConstants.SUPECOAMD.toUpperCase())
				.put(APIIdentifiers.GSTR1A_GET_ECOM, "ECOM")
				.put(APIIdentifiers.GSTR1A_GET_ECOM_AMD,
						APIConstants.ECOMAMD.toUpperCase())
				.build();
}
