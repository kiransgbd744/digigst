package com.ey.advisory.app.itcmatching.vendorupload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VendorMasterUtil {
	private static Map<String, String> APPROVAL_STATUS_MAP = null;
	private static Map<String, String> VENDOR_RISK_CATEG_MAP = null;
	static {
		Map<String, String> map = new HashMap<>(5);
		Map<String, String> riskMap = new HashMap<>(3);
		map.put("FULL", "Approved for full payment");
		map.put("GST", "Approved for part payment (only GST)");
		map.put("PRIN", "Approved for part payment (only principal)");
		map.put("PART", "Approved for part payment");
		map.put("NO", "Not approved for payment");

		riskMap.put("high", "High");
		riskMap.put("medium", "Medium");
		riskMap.put("low", "Low");

		APPROVAL_STATUS_MAP = Collections.unmodifiableMap(map);
		VENDOR_RISK_CATEG_MAP = Collections.unmodifiableMap(riskMap);
	}

	public static String getApprovalStatus(String approvalStatus) {
		return APPROVAL_STATUS_MAP.get(approvalStatus);
	}

	public static boolean isApprovalStatus(String approvalStatus) {
		return APPROVAL_STATUS_MAP.containsKey(approvalStatus);
	}

	public static String getVendorRiskCategory(String vendorRiskCateg) {
		return VENDOR_RISK_CATEG_MAP.get(vendorRiskCateg);
	}

	public static boolean isVendorRiskCategory(String vendorRiskCateg) {
		return VENDOR_RISK_CATEG_MAP.containsKey(vendorRiskCateg);
	}
}
