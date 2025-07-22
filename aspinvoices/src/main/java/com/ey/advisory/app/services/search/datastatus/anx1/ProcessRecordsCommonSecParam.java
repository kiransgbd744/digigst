package com.ey.advisory.app.services.search.datastatus.anx1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.DashboardReqDto;
import com.ey.advisory.core.dto.DataStatusApiSummaryReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.dto.Gstr1SummarySaveStatusReqDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.ITC04RequestDto;

@Component("ProcessRecordsCommonSecParam")
public class ProcessRecordsCommonSecParam {

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String TDS = "TDS";

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	public Anx1ProcessedRecordsReqDto setDataSecuritySearchParams(
			Anx1ProcessedRecordsReqDto processedRecordsReqDto) {

		List<Long> entityIds = processedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> outDataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		if (processedRecordsReqDto.getDataSecAttrs() == null
				|| processedRecordsReqDto.getDataSecAttrs().isEmpty()) {
			processedRecordsReqDto.setOutwardDataSecAttrs(outDataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = processedRecordsReqDto
					.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				processedRecordsReqDto.setOutwardDataSecAttrs(outDataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				processedRecordsReqDto.setOutwardDataSecAttrs(outDataSecAttrs);
			}
		}
		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		Map<String, List<String>> inDataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						inwardSecurityAttributeMap);

		if (processedRecordsReqDto.getDataSecAttrs() == null
				|| processedRecordsReqDto.getDataSecAttrs().isEmpty()) {
			processedRecordsReqDto.setInwardDataSecAttrs(inDataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = processedRecordsReqDto
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
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
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
				processedRecordsReqDto.setInwardDataSecAttrs(inDataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((poList != null && !poList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.PO, poList);
				}

				if ((locList != null && !locList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				processedRecordsReqDto.setInwardDataSecAttrs(inDataSecAttrs);
			}
		}
		return processedRecordsReqDto;

	}

	public DataStatusApiSummaryReqDto setDataStatusSearchParams(
			DataStatusApiSummaryReqDto processedRecordsReqDto) {
		List<Long> entityIds = processedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		if (processedRecordsReqDto.getDataSecAttrs() == null
				|| processedRecordsReqDto.getDataSecAttrs().isEmpty()) {
			processedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = processedRecordsReqDto
					.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> poList = dataSecReqMap.get(OnboardingConstant.PO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (poList == null || poList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				processedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DC, dcList);
				}
				if ((poList != null && !poList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PO, poList);
				}
				if ((locList != null && !locList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				processedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
			}
		}
		return processedRecordsReqDto;
	}

	public Gstr1ProcessedRecordsReqDto setGstr1DataSecuritySearchParams(
			Gstr1ProcessedRecordsReqDto gstr1ProcessedRecordsReqDto) {
		List<Long> entityIds = gstr1ProcessedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ);
		ttlGstinList = gstNDetailRepository
				.filterGstinBasedByRegType(ttlGstinList, regTypeList);
		dataSecAttrs.put(OnboardingConstant.GSTIN, ttlGstinList);

		if (gstr1ProcessedRecordsReqDto.getDataSecAttrs() == null
				|| gstr1ProcessedRecordsReqDto.getDataSecAttrs().isEmpty()) {
			gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = gstr1ProcessedRecordsReqDto
					.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
			}
		}
		return gstr1ProcessedRecordsReqDto;
	}

	public DashboardReqDto setDashboardDataSecurityParams(DashboardReqDto dto) {

		List<Long> entityIds = new ArrayList<>();
		entityIds.add(dto.getEntityId());
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> outDataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		if (dto.getDataSecAttrs() == null || dto.getDataSecAttrs().isEmpty()) {
			dto.setOutwardDataSecAttrs(outDataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = dto.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				dto.setOutwardDataSecAttrs(outDataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				dto.setOutwardDataSecAttrs(outDataSecAttrs);
			}
		}
		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		Map<String, List<String>> inDataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						inwardSecurityAttributeMap);

		if (dto.getDataSecAttrs() == null || dto.getDataSecAttrs().isEmpty()) {
			dto.setInwardDataSecAttrs(inDataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = dto.getDataSecAttrs();
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
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
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
				dto.setInwardDataSecAttrs(inDataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((poList != null && !poList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.PO, poList);
				}

				if ((locList != null && !locList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				dto.setInwardDataSecAttrs(inDataSecAttrs);
			}
		}
		return dto;

	}

	public Gstr1ReviwSummReportsReqDto setDataSecuritySearchParams(
			Gstr1ReviwSummReportsReqDto processedRecordsReqDto) {

		List<Long> entityIds = processedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> outDataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		if (processedRecordsReqDto.getDataSecAttrs() == null
				|| processedRecordsReqDto.getDataSecAttrs().isEmpty()) {
			processedRecordsReqDto.setDataSecAttrs(outDataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = processedRecordsReqDto
					.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				processedRecordsReqDto.setDataSecAttrs(outDataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					outDataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				processedRecordsReqDto.setDataSecAttrs(outDataSecAttrs);
			}
		}
		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		Map<String, List<String>> inDataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						inwardSecurityAttributeMap);

		if (processedRecordsReqDto.getDataSecAttrs() == null
				|| processedRecordsReqDto.getDataSecAttrs().isEmpty()) {
			processedRecordsReqDto.setDataSecAttrs(inDataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = processedRecordsReqDto
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
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
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
				processedRecordsReqDto.setDataSecAttrs(inDataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((poList != null && !poList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.PO, poList);
				}

				if ((locList != null && !locList.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					inDataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				processedRecordsReqDto.setDataSecAttrs(inDataSecAttrs);
			}
		}
		return processedRecordsReqDto;

	}

	public Gstr1SummarySaveStatusReqDto setgstr2DataSecuritySearchParams(
			Gstr1SummarySaveStatusReqDto gstr1ProcessedRecordsReqDto) {
		List<Long> entityIds = new ArrayList<>();
		Long list = Long.parseLong(gstr1ProcessedRecordsReqDto.getEntityId());
		entityIds.add(list);
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		if (gstr1ProcessedRecordsReqDto.getDataSecAttrs() == null
				|| gstr1ProcessedRecordsReqDto.getDataSecAttrs().isEmpty()) {
			gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = gstr1ProcessedRecordsReqDto
					.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
			}
		}
		return gstr1ProcessedRecordsReqDto;
	}

	public Gstr2ProcessedRecordsReqDto setGstr2DataSecuritySearchParams(
			Gstr2ProcessedRecordsReqDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		if (req.getDataSecAttrs() == null || req.getDataSecAttrs().isEmpty()) {
			req.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = req.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				req.setDataSecAttrs(dataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				req.setDataSecAttrs(dataSecAttrs);
			}
		}
		return req;
	}

	public Gstr2AProcessedRecordsReqDto setGstr2aDataSecuritySearchParams(
			Gstr2AProcessedRecordsReqDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		if (req.getDataSecAttrs() == null || req.getDataSecAttrs().isEmpty()) {
			req.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = req.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				req.setDataSecAttrs(dataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				req.setDataSecAttrs(dataSecAttrs);
			}
		}
		return req;
	}

	public Gstr1ProcessedRecordsReqDto setGstr7DataSecuritySearchParams(
			Gstr1ProcessedRecordsReqDto gstr1ProcessedRecordsReqDto) {
		List<Long> entityIds = gstr1ProcessedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(TDS);
		ttlGstinList = gstNDetailRepository
				.filterTdsGstinBasedByRegType(ttlGstinList, regTypeList);
		dataSecAttrs.put(OnboardingConstant.GSTIN, ttlGstinList);

		if (gstr1ProcessedRecordsReqDto.getDataSecAttrs() == null
				|| gstr1ProcessedRecordsReqDto.getDataSecAttrs().isEmpty()) {
			gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = gstr1ProcessedRecordsReqDto
					.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
			}
		}
		return gstr1ProcessedRecordsReqDto;
	}
	public Gstr1SummarySaveStatusReqDto setgstr1SummaryDataSecuritySearchParams(
            Gstr1SummarySaveStatusReqDto gstr1ProcessedRecordsReqDto) {
        List<Long> entityIds = new ArrayList<>();
        Long list = Long.parseLong(gstr1ProcessedRecordsReqDto.getEntityId());
        entityIds.add(list);
        Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
                .getOutwardSecurityAttributeMap();
        Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
                .dataSecurityAttrMapForQuery(entityIds,
                        outwardSecurityAttributeMap);
        if (gstr1ProcessedRecordsReqDto.getDataSecAttrs() == null
                || gstr1ProcessedRecordsReqDto.getDataSecAttrs().isEmpty()) {
            gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
        } else {
            Map<String, List<String>> dataSecReqMap = gstr1ProcessedRecordsReqDto
                    .getDataSecAttrs();
            List<String> gstinList = dataSecReqMap
                    .get(OnboardingConstant.GSTIN);
            List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
            List<String> plantList = dataSecReqMap
                    .get(OnboardingConstant.PLANT);
            List<String> divList = dataSecReqMap
                    .get(OnboardingConstant.DIVISION);
            List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
            List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
            List<String> locList = dataSecReqMap
                    .get(OnboardingConstant.LOCATION);
            List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
            List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
            List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
            List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
            List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
            List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
            if ((gstinList == null || gstinList.isEmpty())
                    && (pcList == null || pcList.isEmpty())
                    && (plantList == null || plantList.isEmpty())
                    && (divList == null || divList.isEmpty())
                    && (soList == null || soList.isEmpty())
                    && (dcList == null || dcList.isEmpty())
                    && (locList == null || locList.isEmpty())
                    && (ud1List == null || ud1List.isEmpty())
                    && (ud2List == null || ud2List.isEmpty())
                    && (ud3List == null || ud3List.isEmpty())
                    && (ud4List == null || ud4List.isEmpty())
                    && (ud5List == null || ud5List.isEmpty())
                    && (ud6List == null || ud6List.isEmpty())) {
                gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
            } else {
                if ((gstinList != null && !gstinList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
                }

 

                if ((pcList != null && !pcList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.PC, pcList);
                }

 

                if ((plantList != null && !plantList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
                }

 

                if ((divList != null && !divList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
                }

 

                if ((soList != null && !soList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.SO, soList);
                }

 

                if ((dcList != null && !dcList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.DC, dcList);
                }

 

                if ((locList != null && !locList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
                }

 

                if ((ud1List != null && !ud1List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
                }

 

                if ((ud2List != null && !ud2List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
                }

 

                if ((ud3List != null && !ud3List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
                }

 

                if ((ud4List != null && !ud4List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
                }

 

                if ((ud5List != null && !ud5List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
                }

 

                if ((ud6List != null && !ud6List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
                }

 

                gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
            }
        }
        return gstr1ProcessedRecordsReqDto;
    }
	
	public ITC04RequestDto setItc04DataSecuritySearchParams(
			ITC04RequestDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		
		   List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
	        List<String> regTypeList = Arrays.asList(REGULAR, SEZ);
	        ttlGstinList = gstNDetailRepository
	                .filterGstinBasedByRegType(ttlGstinList, regTypeList);
	        dataSecAttrs.put(OnboardingConstant.GSTIN, ttlGstinList);
		
		if (req.getDataSecAttrs() == null || req.getDataSecAttrs().isEmpty()) {
			req.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = req.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				req.setDataSecAttrs(dataSecAttrs);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
				}

				req.setDataSecAttrs(dataSecAttrs);
			}
		}
		return req;
	}
 
}
