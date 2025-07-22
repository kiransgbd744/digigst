package com.ey.advisory.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Anx1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.DashboardReqDto;
import com.ey.advisory.core.dto.DataStatusApiSummaryReqDto;
import com.ey.advisory.core.dto.EinvoiceProcessedReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.ey.advisory.core.dto.Gstr1SummarySaveStatusReqDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.ey.advisory.core.dto.Gstr7TransReviwSummReportsReqDto;
import com.ey.advisory.core.dto.ITC04PopupReqDto;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.core.dto.ProcessedVsSubmittedRequestDto;
import com.google.common.collect.Lists;

@Component("ProcessedRecordsCommonSecParam")
public class ProcessedRecordsCommonSecParam {

	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String TDS = "TDS";
	private static final String TCS = "TCS";
	private static final String ISD = "ISD";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

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
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
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

	public Gstr1ProcessedRecordsReqDto setGstr1DataSecuritySearchParamsForOutward(
			Gstr1ProcessedRecordsReqDto gstr1ProcessedRecordsReqDto) {
		List<Long> entityIds = gstr1ProcessedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQueryOutward(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
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
		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForInwardQuery(entityIds,
						inwardSecurityAttributeMap);
		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

		List<String> regTypeListTds = Arrays.asList(TDS,TCS);
		if (CollectionUtils.isNotEmpty(ttlGstinList) 
				&& CollectionUtils.isNotEmpty(regTypeListTds)) {
			ttlGstinList = gstNDetailRepository.
					filter2aTDSISDGstinBasedByRegType(ttlGstinList, regTypeListTds);
		}

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
	
	public Gstr2ProcessedRecordsReqDto setInwardGstr2PRSumDataSecuritySearchParams(
			Gstr2ProcessedRecordsReqDto searchParams) {

		List<Long> entityIds = searchParams.getEntityId();
		Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		Map<String, List<String>> dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForInwardQuery(entityIds,
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
			// List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
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
					dataSecurityAttrMap.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD6, ud6List);
				}

				searchParams.setDataSecAttrs(dataSecurityAttrMap);
			}
		}
		return searchParams;

	}

	public Gstr2AProcessedRecordsReqDto setGstr2aDataSecuritySearchParams(
			Gstr2AProcessedRecordsReqDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		/*
		 * List<String> regTypeListTds = Arrays.asList(TDS); List<String>
		 * regTypeListIsd = Arrays.asList(TDS); ttlGstinList =
		 * gstNDetailRepository.filterTDSISDGstinBasedByRegType( ttlGstinList,
		 * regTypeListTds, regTypeListIsd);
		 */
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

	public Gstr1ProcessedRecordsReqDto setGstr8DataSecuritySearchParams(
			Gstr1ProcessedRecordsReqDto gstr1ProcessedRecordsReqDto) {
		List<Long> entityIds = gstr1ProcessedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getInwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(TCS);
		ttlGstinList = gstNDetailRepository
				.filterTcsGstinBasedByRegType(ttlGstinList, regTypeList);
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

	public Gstr1ReviwSummReportsReqDto setGstr7ReportDataSecuritySearchParams(
			Gstr1ReviwSummReportsReqDto gstr1ProcessedRecordsReqDto) {

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

	public Gstr1VsGstr3bProcessSummaryReqDto setGstr2aVsGstr3bDataSecuritySearchParams(
			Gstr1VsGstr3bProcessSummaryReqDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeListTds = Arrays.asList(TDS, ISD, TCS);
		ttlGstinList = gstNDetailRepository.filter2aTDSISDGstinBasedByRegType(
				ttlGstinList, regTypeListTds);
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

	public Gstr1VsGstr3bProcessSummaryReqDto setGstr1VsGstr3bDataSecuritySearchParams(
			Gstr1VsGstr3bProcessSummaryReqDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
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

	public ITC04RequestDto setItc04DataSecuritySearchParams(
			ITC04RequestDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		 List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
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
			List<String> pc2List = dataSecReqMap.get(OnboardingConstant.PC2);
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
					&& (pc2List == null || pc2List.isEmpty())
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
				if ((pc2List != null && !pc2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC2, pc2List);
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

	/* Itc04 popup data security attributes */
	public ITC04PopupReqDto setItc04DataSecuritySearchParams(
			ITC04PopupReqDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		// List<String> regTypeList = Arrays.asList(REGULAR, SEZ);
		/*
		 * ttlGstinList = gstNDetailRepository
		 * .filterGstinBasedByRegType(ttlGstinList, regTypeList);
		 */
		dataSecAttrs.put(OnboardingConstant.GSTIN, ttlGstinList);

		if (req.getDataSecAttrs() == null || req.getDataSecAttrs().isEmpty()) {
			req.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = req.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> pc2List = dataSecReqMap.get(OnboardingConstant.PC2);
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
					&& (pc2List == null || pc2List.isEmpty())
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
				if ((pc2List != null && !pc2List.isEmpty())) {
					dataSecAttrs.put(OnboardingConstant.PC2, pc2List);
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

	public ProcessedVsSubmittedRequestDto setProcVsSubDataSecuritySearchParams(
			ProcessedVsSubmittedRequestDto req) {
		List<Long> entityIds = req.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
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

	public Gstr2aProcessedDataRecordsReqDto setGstr2aProcessedDataSecuritySearchParams(
			Gstr2aProcessedDataRecordsReqDto req) {

		List<Long> entityIds = Lists.newArrayList();
		if (StringUtils.isNotBlank(req.getEntity())) {
			entityIds.add(Long.parseLong(req.getEntity()));
		}
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
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

	/**
	 * For Einvoice Processed Screen Req Data Security
	 * 
	 * @param gstr1ProcessedRecordsReqDto
	 * @return
	 */

	public EinvoiceProcessedReqDto setEinvDataSecuritySearchParams(
			EinvoiceProcessedReqDto gstr1ProcessedRecordsReqDto) {
		List<Long> entityIds = gstr1ProcessedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
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

	public Annexure1SummaryReqDto setGstr1DataSecuritySearchParams(
			Annexure1SummaryReqDto gstr1ProcessedRecordsReqDto) {
		List<Long> entityIds = gstr1ProcessedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
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

	public Gstr1ProcessedRecordsReqDto setGstr6DataSecuritySearchParams(
			Gstr1ProcessedRecordsReqDto gstr1ProcessedRecordsReqDto) {
		List<Long> entityIds = gstr1ProcessedRecordsReqDto.getEntityId();
		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);

		List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
		List<String> regTypeList = Arrays.asList(ISD);
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
	
	public Gstr7TransReviwSummReportsReqDto setGstr7TransReportDataSecuritySearchParams(
			Gstr7TransReviwSummReportsReqDto gstr7TransReviwSummReportsReqDto) {

		//Data Security Attribute (GSTR7) : GSTIN SO D PLANT UD1 UD2 UD3 UD4 UD5 UD6
		
		List<Long> entityIds = gstr7TransReviwSummReportsReqDto.getEntityId();
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

		if (gstr7TransReviwSummReportsReqDto.getDataSecAttrs() == null
				|| gstr7TransReviwSummReportsReqDto.getDataSecAttrs().isEmpty()) {
			gstr7TransReviwSummReportsReqDto.setDataSecAttrs(dataSecAttrs);
		} else {
			Map<String, List<String>> dataSecReqMap = gstr7TransReviwSummReportsReqDto
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
				gstr7TransReviwSummReportsReqDto.setDataSecAttrs(dataSecAttrs);
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

				gstr7TransReviwSummReportsReqDto.setDataSecAttrs(dataSecAttrs);
			}
		}
		return gstr7TransReviwSummReportsReqDto;
	}
	
	public void setGstr7TransDataSecurityAttribute(
			Gstr7TransReviwSummReportsReqDto request,
			FileStatusDownloadReportEntity entity) {

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		String ProfitCenter = null;
		String plant = null;
		String purchase = null;
		String division = null;
		String location = null;
		String ud1 = null;
		String ud2 = null;
		String ud3 = null;
		String ud4 = null;
		String ud5 = null;
		String ud6 = null;

		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> purchaseList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					ProfitCenter = key;
					if (!dataSecAttrs.get(OnboardingConstant.PC).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PC)
									.size() > 0) {
						pcList = dataSecAttrs.get(OnboardingConstant.PC);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {

					plant = key;
					if (!dataSecAttrs.get(OnboardingConstant.PLANT).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PLANT)
									.size() > 0) {
						plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					if (!dataSecAttrs.get(OnboardingConstant.DIVISION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.DIVISION)
									.size() > 0) {
						divisionList = dataSecAttrs
								.get(OnboardingConstant.DIVISION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					if (!dataSecAttrs.get(OnboardingConstant.LOCATION).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.LOCATION)
									.size() > 0) {
						locationList = dataSecAttrs
								.get(OnboardingConstant.LOCATION);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PO)) {
					purchase = key;
					if (!dataSecAttrs.get(OnboardingConstant.PO).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.PO)
									.size() > 0) {
						purchaseList = dataSecAttrs.get(OnboardingConstant.PO);
					}
				}

				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD1) != null
							&& dataSecAttrs.get(OnboardingConstant.UD1)
									.size() > 0) {
						ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD2) != null
							&& dataSecAttrs.get(OnboardingConstant.UD2)
									.size() > 0) {
						ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD3) != null
							&& dataSecAttrs.get(OnboardingConstant.UD3)
									.size() > 0) {
						ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD4) != null
							&& dataSecAttrs.get(OnboardingConstant.UD4)
									.size() > 0) {
						ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD5) != null
							&& dataSecAttrs.get(OnboardingConstant.UD5)
									.size() > 0) {
						ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
					}
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					if (dataSecAttrs.get(OnboardingConstant.UD6) != null
							&& dataSecAttrs.get(OnboardingConstant.UD6)
									.size() > 0) {
						ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
					}
				}
			}
		}

		if (ProfitCenter != null && !ProfitCenter.isEmpty()) {
			if (pcList != null && pcList.size() > 0) {
				entity.setProfitCenter(String.join(",", pcList));
			}
		}
		if (plant != null && !plant.isEmpty()) {
			if (plantList != null && plantList.size() > 0) {
				entity.setPlantCode(String.join(",", plantList));
			}
		}
		if (purchase != null && !purchase.isEmpty()) {
			if (purchaseList != null && purchaseList.size() > 0) {
				entity.setPurchaseOrg(String.join(",", purchaseList));
			}
		}
		if (division != null && !division.isEmpty()) {
			if (divisionList != null && divisionList.size() > 0) {
				entity.setDivision(String.join(",", divisionList));
			}
		}
		if (location != null && !location.isEmpty()) {
			if (locationList != null && locationList.size() > 0) {
				entity.setLocation(String.join(",", locationList));
			}
		}
		if (ud1 != null && !ud1.isEmpty()) {
			if (ud1List != null && ud1List.size() > 0) {
				entity.setUsrAcs1(String.join(",", ud1List));
			}
		}
		if (ud2 != null && !ud2.isEmpty()) {
			if (ud2List != null && ud2List.size() > 0) {
				entity.setUsrAcs2(String.join(",", ud2List));
			}
		}
		if (ud3 != null && !ud3.isEmpty()) {
			if (ud3List != null && ud3List.size() > 0) {
				entity.setUsrAcs3(String.join(",", ud3List));
			}
		}
		if (ud4 != null && !ud4.isEmpty()) {
			if (ud4List != null && ud4List.size() > 0) {
				entity.setUsrAcs4(String.join(",", ud4List));
			}
		}
		if (ud5 != null && !ud5.isEmpty()) {
			if (ud5List != null && ud5List.size() > 0) {
				entity.setUsrAcs5(String.join(",", ud5List));
			}
		}
		if (ud6 != null && !ud6.isEmpty()) {
			if (ud6List != null && ud6List.size() > 0) {
				entity.setUsrAcs6(String.join(",", ud6List));
			}
		}
	}
}
