package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.Gstr2AVsGstr3bReviewSumFetchDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr2AVssGstr3bReviewSummaryRespDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
/**
 * @author siva K
 * 
 */
@Component("Gstr2AVsGstr3bDownloadReviewSummaryFetchService")
@Slf4j
public class Gstr2AVsGstr3bDownloadReviewSummaryFetchService {

	@Autowired
	@Qualifier("Gstr2AVsGstr3bReviewSumFetchDaoImpl")
	private Gstr2AVsGstr3bReviewSumFetchDaoImpl gstr2aVsGstr3bReviewSummaryFetchDaoImpl;
	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	private LinkedList<String> buildSupplies() {
		LinkedList<String> supplies = Lists.newLinkedList();
		supplies.add("A");
		supplies.add("A1");
		supplies.add("A2");
		supplies.add("A3");
		supplies.add("B");
		supplies.add("B1");
		supplies.add("B2");
		supplies.add("B3");
		supplies.add("B4");
		supplies.add("B5");
		supplies.add("B6");
		supplies.add("B7");
		supplies.add("B8");
		supplies.add("C=A-B");
		return supplies;
	}

	public List<Gstr2AVssGstr3bReviewSummaryRespDto> loadDataByCriteria(
			Gstr1VsGstr3bProcessSummaryReqDto req) {
		List<Gstr2AVssGstr3bReviewSummaryRespDto> respDtos = Lists.newArrayList();

		Gstr1VsGstr3bProcessSummaryReqDto reqDto = processedRecordsCommonSecParam
				.setGstr2aVsGstr3bDataSecuritySearchParams(req);
		List<Gstr2AVssGstr3bReviewSummaryRespDto> processedRespDtos = gstr2aVsGstr3bReviewSummaryFetchDaoImpl
				.fetchGstr1VsGstr3bRecords(reqDto);

		Map<String, List<Gstr2AVssGstr3bReviewSummaryRespDto>> gstinMap = processedRespDtos
				.stream().collect(Collectors.groupingBy(
						Gstr2AVssGstr3bReviewSummaryRespDto::getGstin));
		Map<String, List<Gstr2AVssGstr3bReviewSummaryRespDto>> taxPeriodMap1 = processedRespDtos
				.stream().collect(Collectors.groupingBy(
						Gstr2AVssGstr3bReviewSummaryRespDto::getTaxPeriod));
		for (String taxPeriod : taxPeriodMap1.keySet()) {
			for (String gstin : gstinMap.keySet()) {
				List<Gstr2AVssGstr3bReviewSummaryRespDto> list = gstinMap.get(gstin);
				Map<String, List<Gstr2AVssGstr3bReviewSummaryRespDto>> suppliesMap = list.stream()
						.collect(Collectors.groupingBy(Gstr2AVssGstr3bReviewSummaryRespDto::getSupplies));
				buildSupplies().forEach(suppl -> {
					List<Gstr2AVssGstr3bReviewSummaryRespDto> suppBaselist = suppliesMap.get(suppl);
					if (suppBaselist ==null || suppBaselist.size() <= 0) {
						processedRespDtos.add(
								new Gstr2AVssGstr3bReviewSummaryRespDto(suppl, 
										suppl, BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, BigDecimal.ZERO, gstin, 
										taxPeriod));

					}
					if (suppBaselist !=null && suppBaselist.size() > 0) {
						Map<String, List<Gstr2AVssGstr3bReviewSummaryRespDto>> taxPeriodMap2 = suppBaselist
								.stream().collect(Collectors.groupingBy(
										Gstr2AVssGstr3bReviewSummaryRespDto::getTaxPeriod));

						if (taxPeriodMap2.get(taxPeriod) ==null || taxPeriodMap2.get(taxPeriod).size() <= 0) {
							processedRespDtos.add(
									new Gstr2AVssGstr3bReviewSummaryRespDto(suppl, 
											suppl, BigDecimal.ZERO, BigDecimal.ZERO,
											BigDecimal.ZERO, BigDecimal.ZERO, gstin, 
											taxPeriod));
						}
					}
				});

			}
		}
		
		if (CollectionUtils.isNotEmpty(processedRespDtos)) {
			// taxPeriod, data
			Map<String, List<Gstr2AVssGstr3bReviewSummaryRespDto>> taxPeriodMap = processedRespDtos
					.stream().collect(Collectors.groupingBy(
							Gstr2AVssGstr3bReviewSummaryRespDto::getTaxPeriod));
			for (String taxPeriod : taxPeriodMap.keySet()) {
				List<Gstr2AVssGstr3bReviewSummaryRespDto> taxPeriodDataList = taxPeriodMap
						.get(taxPeriod);
				if (CollectionUtils.isNotEmpty(taxPeriodDataList)) {
					// supply, suppliesList
					Map<String, List<Gstr2AVssGstr3bReviewSummaryRespDto>> suppliesMap = Maps
							.newLinkedHashMap();

					buildSupplies().forEach(suppl -> {
						suppliesMap.put(suppl, processedRespDtos.stream()
								.filter(dto -> (dto.getSupplies().equals(suppl)
										&& dto.getTaxPeriod()
												.equals(taxPeriod)))
								.collect(Collectors.toList()));
					});
					respDtos.addAll(
							calculateSuppliesAndReturnList(suppliesMap,
									reqDto.getDataSecAttrs().get("GSTIN")
											.stream().findFirst().get(),
									taxPeriod));
				}
			}
		}
		LOGGER.error("respDtos ---> {}",respDtos);
		return respDtos;
	}

	private List<Gstr2AVssGstr3bReviewSummaryRespDto> calculateSuppliesAndReturnList(
			Map<String, List<Gstr2AVssGstr3bReviewSummaryRespDto>> suppliesMap,
			String gstin, String taxPeriod) {
		
		List<Gstr2AVssGstr3bReviewSummaryRespDto> finalList = new ArrayList<>();
				
		for (String supp : suppliesMap.keySet()) {
			List<Gstr2AVssGstr3bReviewSummaryRespDto> supplLsit = suppliesMap.get(supp);
			for (Gstr2AVssGstr3bReviewSummaryRespDto dto : supplLsit) {
				if (dto.getSupplies() == "C=A-B") continue;
				Gstr2AVssGstr3bReviewSummaryRespDto respDto = new Gstr2AVssGstr3bReviewSummaryRespDto();
				respDto.setSupplies(dto.getSupplies());
				respDto.setFormula(dto.getFormula());
				respDto.setIgst(dto.getIgst());
				respDto.setCgst(dto.getCgst());
				respDto.setSgst(dto.getSgst());
				respDto.setCess(dto.getCess());
				respDto.setGstin(dto.getGstin());
				respDto.setTaxPeriod(String.valueOf(GenUtil
						.convertDerivedTaxPeriodToTaxPeriod(Integer
								.parseInt(dto.getTaxPeriod()))));
				finalList.add(respDto);
			}
			
		}
		
		Map<String, List<Gstr2AVssGstr3bReviewSummaryRespDto>> gstinMap = finalList
				.stream().collect(Collectors.groupingBy(
						Gstr2AVssGstr3bReviewSummaryRespDto::getGstin));
		for (String gstinkey : gstinMap.keySet()) {
			List<Gstr2AVssGstr3bReviewSummaryRespDto> supplLsit = gstinMap.get(gstinkey);
			Map<String, List<Gstr2AVssGstr3bReviewSummaryRespDto>> supList = supplLsit.stream()
					.collect(Collectors.groupingBy(Gstr2AVssGstr3bReviewSummaryRespDto::getSupplies));
					Gstr2AVssGstr3bReviewSummaryRespDto respDto = new Gstr2AVssGstr3bReviewSummaryRespDto();
					Gstr2AVssGstr3bReviewSummaryRespDto dto1 = supList.get("A").get(0);
					Gstr2AVssGstr3bReviewSummaryRespDto dto2 = supList.get("B").get(0);
					respDto.setIgst(dto1.getIgst().subtract(dto2.getIgst()));
					respDto.setCgst(dto1.getCgst().subtract(dto2.getCgst()));
					respDto.setSgst(dto1.getSgst().subtract(dto2.getSgst()));
					respDto.setCess(dto1.getCess().subtract(dto2.getCess()));
					respDto.setTaxPeriod(dto1.getTaxPeriod());
					respDto.setGstin(gstinkey);
					respDto.setFormula("C=A-B");
					respDto.setSupplies("C");
					finalList.add(respDto);
				
		}
		
		finalList.forEach(dto -> dto
				.setSupplies(natureSuppliesMap().get(dto.getSupplies())));

		return finalList;
	}
	private Map<String, String> natureSuppliesMap() {
		Map<String, String> natureSuppliesMap = Maps.newHashMap();
		 natureSuppliesMap.put("A", "Taxes as per GSTR-2A ");
		natureSuppliesMap.put("A1",
				"Table 3 and 5* of GSTR-2A (* original invoice of RNV must "
				+ "be present in table 3 of GSTR-2A of previous months. "
				+ "Original document for RCR and RDR must be present in "
				+ "table 5 of GSTR-2A of previous months.)");
		  natureSuppliesMap.put("A2",
	                "Table 6 of GSTR-2A (ISD Credit eligible and in-eligible)");
	        natureSuppliesMap.put("A3",
	                "IMPG and IMPGSEZ of GSTR-2A");
	        natureSuppliesMap.put("B", "Taxes as per GSTR-3B    ");
	        natureSuppliesMap.put("B1",
	                "Table 4A (1) of GSTR-3B (Import of Goods)  ");
	        natureSuppliesMap.put("B2",
	                "Table 4A (2) of GSTR-3B (Import of Services)   ");
	        natureSuppliesMap.put("B3",
	                "Table 4A (3) of GSTR-3B (Inward supplies liable to reverse charge) ");
	        natureSuppliesMap.put("B4",
	                "Table 4A (4) of GSTR-3B (Inward supplies from ISD) ");
	        natureSuppliesMap.put("B5",
	                "Table 4A (5) of GSTR-3B (All other ITC)    ");
	        natureSuppliesMap.put("B6", "Table 4D of GSTR-3B (Ineligible ITC)   ");
	        natureSuppliesMap.put("B7",
	                "less: Table 3.1(d) of GSTR-3B (Inward supplies liable to reverse charge)   ");
	        natureSuppliesMap.put("B8",
	                "less: In-eligible credit on Import of Goods based on PR    ");
	        natureSuppliesMap.put("C", "Difference ");
		return natureSuppliesMap;
	}

}
