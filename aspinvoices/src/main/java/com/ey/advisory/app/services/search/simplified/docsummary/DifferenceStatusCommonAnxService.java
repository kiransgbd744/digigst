package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.anx1.SavestatusReqDto;
import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummaryInwardOutwardRespDto;
import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummaryRespDto;
import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummarySuppliesRespDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryEcomResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.DifferenceStatusSummaryRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1aReviewSummaryService;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("DifferenceStatusCommonAnxService")
public class DifferenceStatusCommonAnxService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DifferenceStatusCommonAnxService.class);

	@Autowired
	@Qualifier("Anx1GetGstnDataSearchService")
	Anx1GetGstnDataSearchService gstnService;

	@Autowired
	@Qualifier("Annexure1ReqRespHandler")
	Annexure1ReqRespHandler annexure1ReqRespHandler;

	@Autowired
	@Qualifier("Anx1aReviewSummaryServiceImpl")
	Anx1aReviewSummaryService anx1aReviewSummaryService;

	public Map<String, DifferenceStatusSummaryRespDto> getDifferenceForAnx1(
			SavestatusReqDto criteria, Gson gson) {
		Map<String, DifferenceStatusSummaryRespDto> diffRespDtosMap = new HashMap<>();

		Annexure1SummaryReqDto annexure1SummaryRequest = new Annexure1SummaryReqDto();
		annexure1SummaryRequest.setTaxPeriod(criteria.getTaxPeriod());
		annexure1SummaryRequest.getEntityId()
				.add(Long.parseLong(criteria.getEntityId()));
		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put("GSTIN", Arrays.asList(criteria.getGstin()));

		annexure1SummaryRequest.setDataSecAttrs(dataSecAttrs);

		JsonElement outwardSummaryRespBody = annexure1ReqRespHandler
				.handleAnnexure1ReqAndResp(annexure1SummaryRequest);
		convertAnx1SummaryToDiffSummary(outwardSummaryRespBody, gson,
				diffRespDtosMap, Arrays.asList("b2c", "b2b", "expt", "expwt",
						"sezt", "sezwt", "deemExpt"));

		JsonElement InwardSummaryRespBody = annexure1ReqRespHandler
				.handleInwardAnnexure1ReqAndResp(annexure1SummaryRequest);
		convertAnx1SummaryToDiffSummary(InwardSummaryRespBody, gson,
				diffRespDtosMap,
				Arrays.asList("impg", "impgSez", "imps", "rev"));

		JsonElement ecomSummaryRespBody = annexure1ReqRespHandler
				.handleEcommAnnexure1ReqAndResp(annexure1SummaryRequest);
		convertAnx1SummaryToDiffSummary(ecomSummaryRespBody, gson,
				diffRespDtosMap, Arrays.asList("table4"));

		return diffRespDtosMap;
	}

	@SuppressWarnings("serial")
	private void convertAnx1SummaryToDiffSummary(
			JsonElement outwardSummaryRespBody, Gson gson,
			Map<String, DifferenceStatusSummaryRespDto> diffRespDtosMap,
			List<String> sectionList) {
		if (sectionList.get(0).equals("table4")) {
			java.lang.reflect.Type type = new TypeToken<Map<String, List<Annexure1SummaryEcomResp1Dto>>>() {
			}.getType();
			Map<String, List<Annexure1SummaryEcomResp1Dto>> summaryList = gson
					.fromJson(outwardSummaryRespBody, type);

			convertEcomSectionAndAddtoMapBySection(
					summaryList.get("table4").get(0), sectionList.get(0),
					diffRespDtosMap);
		} else {
			java.lang.reflect.Type type = new TypeToken<Map<String, List<Annexure1SummaryResp1Dto>>>() {
			}.getType();
			Map<String, List<Annexure1SummaryResp1Dto>> summaryList = gson
					.fromJson(outwardSummaryRespBody, type);
			sectionList.forEach(section -> {
				List<Annexure1SummaryResp1Dto> sectionDataList = summaryList
						.get(section);
				convertAndAddtoMapBySection(sectionDataList.get(0), section,
						diffRespDtosMap);
			});
		}
	}

	private void convertEcomSectionAndAddtoMapBySection(
			Annexure1SummaryEcomResp1Dto summaryDto, String section,
			Map<String, DifferenceStatusSummaryRespDto> diffRespDtosMap) {
		DifferenceStatusSummaryRespDto respDto = new DifferenceStatusSummaryRespDto();
		respDto.setSection(summaryDto.getTableSection());
		respDto.setCount(summaryDto.getDiffSupplyMade().intValue());
		respDto.setInvoiceValue(summaryDto.getDiffSupplyReturn());
		respDto.setTaxableValue(summaryDto.getDiffNetSupply());
		respDto.setTaxPayble(summaryDto.getDiffTaxPayble());
		respDto.setIgst(summaryDto.getDiffIgst());
		respDto.setCgst(summaryDto.getDiffCgst());
		respDto.setSgst(summaryDto.getDiffSgst());
		respDto.setCess(summaryDto.getDiffCess());

		diffRespDtosMap.put(section, respDto);

	}

	private void convertAndAddtoMapBySection(
			Annexure1SummaryResp1Dto summaryDto, String section,
			Map<String, DifferenceStatusSummaryRespDto> diffRespDtosMap) {

		DifferenceStatusSummaryRespDto respDto = new DifferenceStatusSummaryRespDto();
		respDto.setSection(summaryDto.getTableSection());
		respDto.setCount(summaryDto.getDiffCount());
		respDto.setInvoiceValue(summaryDto.getDiffInvoiceValue());
		respDto.setTaxableValue(summaryDto.getDiffTaxableValue());
		respDto.setTaxPayble(summaryDto.getDiffTaxPayble());
		respDto.setIgst(summaryDto.getDiffIgst());
		respDto.setCgst(summaryDto.getDiffCgst());
		respDto.setSgst(summaryDto.getDiffSgst());
		respDto.setCess(summaryDto.getDiffCess());

		diffRespDtosMap.put(section, respDto);
	}

	public Map<? extends String, ? extends DifferenceStatusSummaryRespDto> getDifferenceForAnx1a(
			SavestatusReqDto criteria) {
		Map<String, DifferenceStatusSummaryRespDto> diffRespDtosMap = new HashMap<>();
		Annexure1SummaryReqDto annexure1SummaryRequest = new Annexure1SummaryReqDto();
		annexure1SummaryRequest.setTaxPeriod(criteria.getTaxPeriod());
		annexure1SummaryRequest.getEntityId()
				.add(Long.parseLong(criteria.getEntityId()));

		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		dataSecAttrs.put("GSTIN", Arrays.asList(criteria.getGstin()));

		annexure1SummaryRequest.setDataSecAttrs(dataSecAttrs);

		Anx1aSummaryRespDto anx1aSummaryRespDto = anx1aReviewSummaryService
				.getAnx1aReviewSummary(annexure1SummaryRequest);
		List<Anx1aSummaryInwardOutwardRespDto> outwardList = anx1aSummaryRespDto
				.getOutward();
		convertToDiffSummaryBySectionType(outwardList, diffRespDtosMap);

		List<Anx1aSummaryInwardOutwardRespDto> inwardList = anx1aSummaryRespDto
				.getInward();
		convertToDiffSummaryBySectionType(inwardList, diffRespDtosMap);

		List<Anx1aSummarySuppliesRespDto> suplliesList = anx1aSummaryRespDto
				.getSupplies();
		convertSuppliesToDiffSummaryBySectionType(suplliesList,
				diffRespDtosMap);

		return diffRespDtosMap;
	}

	private void convertSuppliesToDiffSummaryBySectionType(
			List<Anx1aSummarySuppliesRespDto> suplliesList,
			Map<String, DifferenceStatusSummaryRespDto> diffRespDtosMap) {
		suplliesList.forEach(dto -> {
			DifferenceStatusSummaryRespDto respDto = new DifferenceStatusSummaryRespDto();
			respDto.setSection(dto.getTable());
			respDto.setCount(dto.getDiffSupplyMade().intValue());
			respDto.setTaxableValue(dto.getDiffNetSupply());
			respDto.setInvoiceValue(dto.getDiffSupplyReturn());
			respDto.setTaxPayble(dto.getDiffTaxPayble());
			respDto.setIgst(dto.getDiffIgst());
			respDto.setCgst(dto.getDiffCgst());
			respDto.setSgst(dto.getDiffSgst());
			respDto.setCess(dto.getDiffCess());

			diffRespDtosMap.put(dto.getTable(), respDto);
		});
	}

	private void convertToDiffSummaryBySectionType(
			List<Anx1aSummaryInwardOutwardRespDto> outwardList,
			Map<String, DifferenceStatusSummaryRespDto> diffRespDtosMap) {
		outwardList.forEach(dto -> {
			DifferenceStatusSummaryRespDto respDto = new DifferenceStatusSummaryRespDto();
			respDto.setSection(dto.getTable());
			respDto.setCount(dto.getDiffCount());
			respDto.setInvoiceValue(dto.getDiffInvoiceValue());
			respDto.setTaxableValue(dto.getDiffTaxableValue());
			respDto.setTaxPayble(dto.getDiffTaxPayble());
			respDto.setIgst(dto.getDiffIgst());
			respDto.setCgst(dto.getDiffCgst());
			respDto.setSgst(dto.getDiffSgst());
			respDto.setCess(dto.getDiffCess());
			diffRespDtosMap.put(dto.getTable(), respDto);
		});
	}
}
