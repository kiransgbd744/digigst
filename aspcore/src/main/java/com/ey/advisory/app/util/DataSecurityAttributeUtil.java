package com.ey.advisory.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Quartet;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mohana.Dasari
 *
 */
@Slf4j
public class DataSecurityAttributeUtil {

	private DataSecurityAttributeUtil() {
	}

	/**
	 * For GSTIN attribute, sgstin is used for Outward Sales Organization is
	 * applicable for Outward
	 * 
	 * @return
	 */
	public static Map<String, String> getOutwardSecurityAttributeMap() {
		Map<String, String> map = new HashMap<>();
		map.put(OnboardingConstant.PC, "profitCentre");
		map.put(OnboardingConstant.PC2, "profitCentre2");
		map.put(OnboardingConstant.PLANT, "plantCode");
		map.put(OnboardingConstant.DIVISION, "division");
		map.put(OnboardingConstant.GSTIN, "sgstin");
		map.put(OnboardingConstant.SO, "salesOrgnization");
		map.put(OnboardingConstant.DC, "distributionChannel");
		map.put(OnboardingConstant.LOCATION, "location");
		map.put(OnboardingConstant.UD1, "userAccess1");
		map.put(OnboardingConstant.UD2, "userAccess2");
		map.put(OnboardingConstant.UD3, "userAccess3");
		map.put(OnboardingConstant.UD4, "userAccess4");
		map.put(OnboardingConstant.UD5, "userAccess5");
		map.put(OnboardingConstant.UD6, "userAccess6");
		return map;
	}

	/**
	 * For GSTIN attribute, cgstin is used for Inward Purchase Organization is
	 * applicable for Inward
	 * 
	 * @return
	 */
	public static Map<String, String> getInwardSecurityAttributeMap() {
		Map<String, String> map = new HashMap<>();
		map.put(OnboardingConstant.PC, "profitCentre");
		map.put(OnboardingConstant.PC2, "profitCentre2");
		map.put(OnboardingConstant.PLANT, "plantCode");
		map.put(OnboardingConstant.DIVISION, "division");
		map.put(OnboardingConstant.GSTIN, "cgstin");
		map.put(OnboardingConstant.PO, "purchaseOrganization");
		map.put(OnboardingConstant.LOCATION, "location");
		map.put(OnboardingConstant.UD1, "userAccess1");
		map.put(OnboardingConstant.UD2, "userAccess2");
		map.put(OnboardingConstant.UD3, "userAccess3");
		map.put(OnboardingConstant.UD4, "userAccess4");
		map.put(OnboardingConstant.UD5, "userAccess5");
		map.put(OnboardingConstant.UD6, "userAccess6");
		return map;
	}

	/**
	 * 
	 * @param entityIds
	 * @return
	 */
	public static Map<String, List<String>> dataSecurityAttrMapForQuery(
			List<Long> entityIds, Map<String, String> securityAtMap) {
		Map<String, List<String>> map = new HashMap<>();
		User user = SecurityContext.getUser();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityAttributeUtil.dataSecurityAttrMapForQuery() "
					+ "logged in  user {} :", user);
		}
		
		if (user == null)
			return map;
		entityIds.forEach(entityId -> {
			List<Quartet<String, String, String, String>> applicableAttrs = user
					.getApplicableAttrs(entityId);
			if (applicableAttrs != null && !applicableAttrs.isEmpty()) {
				for (Quartet<String, String, String, String> applicableAttr : applicableAttrs) {
					String attrCode = applicableAttr.getValue0();
					if (securityAtMap.containsKey(attrCode)) {
						List<Pair<Long, String>> attrValuesForAttrCode = user
								.getAttrValuesForAttrCode(entityId, attrCode);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("DataSecurityAttributeUtil.dataSecurityAttrMapForQuery() "
									+ "applicableAttrs {} :", applicableAttrs.toString());
						}
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("DataSecurityAttributeUtil.dataSecurityAttrMapForQuery() "
									+ "attrValuesForAttrCode {} :", attrValuesForAttrCode);
						}
						if (attrValuesForAttrCode != null) {
							// attrValList.clear();
							List<String> attrValList = new ArrayList<>();
							for (Pair<Long, String> attrValue : attrValuesForAttrCode) {
								String attrVal = attrValue.getValue1();
								attrValList.add(attrVal);
							}
							map.put(attrCode, attrValList);
						}
					}
				}
			}
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DataSecurityAttributeUtil.dataSecurityAttrMapForQuery() "
					+ "before Return map {} :", map.toString());
		}
		return map;
	}

	/**
	 * 
	 * @param entityIds
	 * @return
	 */
	public static Map<String, List<String>> dataSecurityAttrMapForQueryOutward(
			List<Long> entityIds, Map<String, String> securityAtMap) {
		Map<String, List<String>> map = new HashMap<>();
		User user = SecurityContext.getUser();
		if (user == null)
			return map;
		entityIds.forEach(entityId -> {
			List<Quartet<String, String, String, String>> applicableAttrs = user
					.getApplicableAttrs(entityId);
			if (applicableAttrs != null && !applicableAttrs.isEmpty()) {
				for (Quartet<String, String, String, String> applicableAttr : applicableAttrs) {
					if ("M".equals(applicableAttr.getValue2())) {
						String attrCode = applicableAttr.getValue0();
						if (securityAtMap.containsKey(attrCode)) {
							List<Pair<Long, String>> attrValuesForAttrCode = user
									.getAttrValuesForAttrCode(entityId,
											attrCode);
							if (attrValuesForAttrCode != null) {
								// attrValList.clear();
								List<String> attrValList = new ArrayList<>();
								for (Pair<Long, String> attrValue : attrValuesForAttrCode) {
									String attrVal = attrValue.getValue1();
									attrValList.add(attrVal);
								}
								map.put(attrCode, attrValList);
							}
						}
					}
				}
			}
		});
		return map;
	}

	/**
	 * 
	 * @param entityIds
	 * @return
	 */
	public static Map<String, List<String>> dataSecurityAttrMapForInwardQuery(
			List<Long> entityIds, Map<String, String> securityAtMap) {
		Map<String, List<String>> map = new HashMap<>();
		User user = SecurityContext.getUser();
		if (user == null)
			return map;
		entityIds.forEach(entityId -> {
			List<Quartet<String, String, String, String>> applicableAttrs = user
					.getApplicableAttrs(entityId);
			if (applicableAttrs != null && !applicableAttrs.isEmpty()) {
				for (Quartet<String, String, String, String> applicableAttr : applicableAttrs) {
					if ("M".equals(applicableAttr.getValue3())) {
						String attrCode = applicableAttr.getValue0();
						if (securityAtMap.containsKey(attrCode)) {
							List<Pair<Long, String>> attrValuesForAttrCode = user
									.getAttrValuesForAttrCode(entityId,
											attrCode);
							if (attrValuesForAttrCode != null) {
								// attrValList.clear();
								List<String> attrValList = new ArrayList<>();
								for (Pair<Long, String> attrValue : attrValuesForAttrCode) {
									String attrVal = attrValue.getValue1();
									attrValList.add(attrVal);
								}
								map.put(attrCode, attrValList);
							}
						}
					}
				}
			}
		});
		return map;
	}
}
