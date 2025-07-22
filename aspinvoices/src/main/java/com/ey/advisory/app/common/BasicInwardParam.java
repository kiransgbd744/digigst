package com.ey.advisory.app.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Anx2InwardErrorRequestDto;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;

@Component("BasicInwardParam")
public class BasicInwardParam {
	
	public Anx2InwardErrorRequestDto setDataSecuritySearchParams(
			Anx2InwardErrorRequestDto searchParams) {
		
		
		List<Long> entityIds = searchParams.getEntityId();
		Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();

		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						inwardSecurityAttributeMap);
		if (searchParams.getDataSecAttrs() == null
				|| searchParams.getDataSecAttrs().isEmpty()) {
			searchParams.setDataSecAttrs(dataSecurityAttrMap);
		} else {
			Map<String, List<String>> dataSecReqMap = searchParams
					.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> poList = dataSecReqMap.get(OnboardingConstant.PO);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap
					.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap
					.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap
					.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap
					.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap
					.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap
					.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (poList == null || poList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				searchParams.setDataSecAttrs(dataSecurityAttrMap);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.GSTIN,
							gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.PLANT,
							plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.DIVISION,
							divList);
				}

				if ((poList != null && !poList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.PO, poList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.LOCATION,
							locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD1,
							ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD2,
							ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD3,
							ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD4,
							ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD5,
							ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD6,
							ud6List);
				}

				searchParams.setDataSecAttrs(dataSecurityAttrMap);
			}
		}
		return searchParams;
	}

	}



