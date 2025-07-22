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

import com.ey.advisory.app.data.daos.client.Gstr1VsGstr3bReviewSummaryFetchDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bReviewSummaryRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component("Gstr1VsGstr3bDownloadReviewSummaryFetchService")
public class Gstr1VsGstr3bDownloadReviewSummaryFetchService {
	@Autowired
	@Qualifier("Gstr1VsGstr3bReviewSummaryFetchDaoImpl")
	private Gstr1VsGstr3bReviewSummaryFetchDaoImpl gstr1VsGstr3bReviewSummaryFetchDaoImpl;

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
		supplies.add("B8-B9");
		supplies.add("B10");
		supplies.add("B11");
		supplies.add("B12");
		supplies.add("B13");
		supplies.add("B14");
		supplies.add("B15");
		supplies.add("B16-B17");
		supplies.add("B18");
		supplies.add("B19");
		supplies.add("C=A-B");
		supplies.add("D1");
		supplies.add("D2");
		supplies.add("E1");
		supplies.add("E2");
		supplies.add("E3");
		supplies.add("F=D1+D2-E");
		supplies.add("G=C+F");
		supplies.add("H");
		supplies.add("I");
		supplies.add("J=H-I");
		supplies.add("K=G+J");

		return supplies;
	}

	public List<Gstr1VsGstr3bReviewSummaryRespDto> loadDataByCriteria(
			Gstr1VsGstr3bProcessSummaryReqDto req) {
		List<Gstr1VsGstr3bReviewSummaryRespDto> respDtos = Lists.newArrayList();
		List<Gstr1VsGstr3bReviewSummaryRespDto> processedRespDtos = gstr1VsGstr3bReviewSummaryFetchDaoImpl
				.fetchGstr1VsGstr3bRecords(req);
		Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> gstinMap = processedRespDtos
				.stream().collect(Collectors.groupingBy(
						Gstr1VsGstr3bReviewSummaryRespDto::getGstin));
		Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> taxPeriodMap1 = processedRespDtos
				.stream().collect(Collectors.groupingBy(
						Gstr1VsGstr3bReviewSummaryRespDto::getTaxPeriod));
		for (String taxPeriod : taxPeriodMap1.keySet()) {
			for (String gstin : gstinMap.keySet()) {
				List<Gstr1VsGstr3bReviewSummaryRespDto> list = gstinMap.get(gstin);
				Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> suppliesMap = list.stream()
						.collect(Collectors.groupingBy(Gstr1VsGstr3bReviewSummaryRespDto::getSupplies));
				buildSupplies().forEach(suppl -> {
					List<Gstr1VsGstr3bReviewSummaryRespDto> suppBaselist = suppliesMap.get(suppl);
					if (suppBaselist ==null || suppBaselist.size() <= 0) {
						processedRespDtos.add(
								new Gstr1VsGstr3bReviewSummaryRespDto(suppl,
										suppl, BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, gstin, taxPeriod,
										Lists.newArrayList()));

					}
					if (suppBaselist !=null && suppBaselist.size() > 0) {
						Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> taxPeriodMap2 = suppBaselist
								.stream().collect(Collectors.groupingBy(
										Gstr1VsGstr3bReviewSummaryRespDto::getTaxPeriod));

						if (taxPeriodMap2.get(taxPeriod) ==null || taxPeriodMap2.get(taxPeriod).size() <= 0) {
							processedRespDtos.add(
									new Gstr1VsGstr3bReviewSummaryRespDto(suppl,
											suppl, BigDecimal.ZERO, BigDecimal.ZERO,
											BigDecimal.ZERO, BigDecimal.ZERO,
											BigDecimal.ZERO, gstin, taxPeriod,
											Lists.newArrayList()));
						}
					}
				});

			}
		}
		
		
		
		if (CollectionUtils.isNotEmpty(processedRespDtos)) {
			// taxPeriod, data
			Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> taxPeriodMap = processedRespDtos
					.stream().collect(Collectors.groupingBy(
							Gstr1VsGstr3bReviewSummaryRespDto::getTaxPeriod));

			for (String taxPeriod : taxPeriodMap.keySet()) {
				List<Gstr1VsGstr3bReviewSummaryRespDto> taxPeriodDataList = taxPeriodMap
						.get(taxPeriod);
				if (CollectionUtils.isNotEmpty(taxPeriodDataList)) {
					// supply, suppliesList
					Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> suppliesMap = Maps
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
									req.getDataSecAttrs().get("GSTIN")
											.stream().findFirst().get(),
									taxPeriod));
				}
			}
		} 

		return respDtos;
	}

	private List<Gstr1VsGstr3bReviewSummaryRespDto> calculateSuppliesAndReturnList(
			Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> suppliesMap,
			String gstin, String taxPeriod) {
		List<Gstr1VsGstr3bReviewSummaryRespDto> finalList = new ArrayList<>();
		for (String supp : suppliesMap.keySet()) {
			List<Gstr1VsGstr3bReviewSummaryRespDto> supplLsit = suppliesMap.get(supp);
			for (Gstr1VsGstr3bReviewSummaryRespDto dto : supplLsit) {
				if (dto.getSupplies() == "C=A-B") continue;
				if (dto.getSupplies() == "F=D1+D2-E") continue;
				if (dto.getSupplies() == "G=C+F") continue;
				if (dto.getSupplies() == "J=H-I") continue;
				if (dto.getSupplies() == "K=G+J") continue;
				Gstr1VsGstr3bReviewSummaryRespDto respDto = new Gstr1VsGstr3bReviewSummaryRespDto();
				respDto.setSupplies(dto.getSupplies());
				respDto.setFormula(dto.getFormula());
				respDto.setTaxableValue(dto.getTaxableValue());
				respDto.setIgst(dto.getIgst());
				respDto.setCgst(dto.getCgst());
				respDto.setSgst(dto.getSgst());
				respDto.setCess(dto.getCess());
				respDto.setGstin(dto.getGstin());
				respDto.setTaxPeriod(dto.getTaxPeriod());
				finalList.add(respDto);
			}
			
		}
		Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> gstinMap = finalList
				.stream().collect(Collectors.groupingBy(
						Gstr1VsGstr3bReviewSummaryRespDto::getGstin));
		for (String gstinkey : gstinMap.keySet()) {
			List<Gstr1VsGstr3bReviewSummaryRespDto> supplLsit = gstinMap.get(gstinkey);
			Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> supList = supplLsit.stream()
					.collect(Collectors.groupingBy(Gstr1VsGstr3bReviewSummaryRespDto::getSupplies));
			Gstr1VsGstr3bReviewSummaryRespDto a = supList.get("A").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto b = supList.get("B").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto d1 =supList.get("D1").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto d2 =supList.get("D2").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto E1 = supList.get("E1").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto E2 = supList.get("E2").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto E3 = supList.get("E3").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto dto3 = null;
			 	dto3 = new Gstr1VsGstr3bReviewSummaryRespDto();
		        dto3.setTaxableValue(E1.getTaxableValue().add(E2.getTaxableValue()).add(E3.getTaxableValue()));
		        dto3.setIgst(E1.getIgst().add(E2.getIgst()).add(E3.getIgst()));
		        dto3.setCgst(E1.getCgst().add(E2.getCgst()).add(E3.getCgst()));
		        dto3.setSgst(E1.getSgst().add(E2.getSgst()).add(E3.getSgst()));
		        dto3.setCess(E1.getCess().add(E2.getCess()).add(E3.getCess()));
		        dto3.setTaxPeriod(E1.getTaxPeriod());
			
			Gstr1VsGstr3bReviewSummaryRespDto h = supList.get("H").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto i = supList.get("I").get(0);
			getCValie(a, b, gstinkey, finalList);
			getFValie(d1, d2, dto3, gstinkey, finalList);
			getJvalue(h, i, gstinkey, finalList);
			Gstr1VsGstr3bReviewSummaryRespDto c = getGstinMap(finalList,gstinkey).get("C").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto f = getGstinMap(finalList,gstinkey).get("F").get(0);
			Gstr1VsGstr3bReviewSummaryRespDto j = getGstinMap(finalList,gstinkey).get("J").get(0);
			getGvalue(c, f, gstinkey, finalList);
			Gstr1VsGstr3bReviewSummaryRespDto g =getGstinMap(finalList,gstinkey).get("G").get(0);
			getKvalue(g, j, gstinkey, finalList);
		}
		
		finalList.forEach(dto -> dto
				.setSupplies(natureSuppliesMap().get(dto.getSupplies())));

		return finalList;
	}
private static Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>>  
getGstinMap(List<Gstr1VsGstr3bReviewSummaryRespDto> finalList,String gstin){
	Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> gstinMap = finalList
			.stream().collect(Collectors.groupingBy(
					Gstr1VsGstr3bReviewSummaryRespDto::getGstin));
	List<Gstr1VsGstr3bReviewSummaryRespDto> list = gstinMap.get(gstin);
	return list.stream()
			.collect(Collectors.groupingBy(Gstr1VsGstr3bReviewSummaryRespDto::getSupplies));
}
	

	private Map<String, String> natureSuppliesMap() {
		Map<String, String> natureSuppliesMap = Maps.newHashMap();
		natureSuppliesMap.put("A", "Table 3.1 (a), 3.1 (b) & 3.1.1.a as per GSTR - 3B");
		natureSuppliesMap.put("A1",
				"Table 3.1(a) Outward Taxable Supplies (other than zero rated, nil rated and exempted)");
		natureSuppliesMap.put("A2",
				"Table 3.1(b) Outward taxable supplies (zero rated)");
		natureSuppliesMap.put("A3",
				"Table 3.1.1.a Taxable supplies on which electronic commerce operator pays tax u/s 9(5)");
		natureSuppliesMap.put("B", "Details of supplies reported in GSTR - 1");
		natureSuppliesMap.put("B1",
				"Supplies made to registered person on forward charge (B2B - 4A, 6B, 6C)");
		natureSuppliesMap.put("B2",
				"Supplies made to registered person on reverse charge  (B2B - 4B)");
		natureSuppliesMap.put("B3",
				"Supplies made to unregistered person (B2CL - 5)");
		natureSuppliesMap.put("B4",
				"Supplies made to other unregistered person (B2CS - 7)");
		natureSuppliesMap.put("B5", "Exports (6A)");
		natureSuppliesMap.put("B6",
				"CR/DR issued against supplies reported in B1 & B2 (CDNR - 9B)");
		natureSuppliesMap.put("B7",
				"CR/DR issued against supplies reported in B3 & B5 (CDNUR - 9B)");
		natureSuppliesMap.put("B8-B9",
				"Advance Received Less Advance Adjusted (11)");
		natureSuppliesMap.put("B10",
				"Amendment of supplies reported in B1 & B2 of previous tax periods (B2BA - 9A)");
		natureSuppliesMap.put("B11",
				"Amendment of supplies reported in B3 of previous tax periods (B2CLA - 9A)");
		natureSuppliesMap.put("B12",
				"Amendment of supplies reported in B4 of previous tax periods (B2CSA - 10)");
		natureSuppliesMap.put("B13",
				"Amendment of supplies reported in B5 of previous tax periods (EXPA - 9A)");
		natureSuppliesMap.put("B14",
				"Amendment of supplies reported in B6 of previous tax periods (CDNRA - 9C)");
		natureSuppliesMap.put("B15",
				"Amendment of supplies reported in B7 of previous tax periods (CDNURA - 9C)");
		natureSuppliesMap.put("B16-B17",
				"Amendment of advance received reported in B8 of previous "
						+ "tax periods Less Amendment of advance adjusted reported in"
						+ " B9 of previous tax periods (11)");
		natureSuppliesMap.put("B18",
				"Supplies made to other unregistered person (ECOM - 15)");
		natureSuppliesMap.put("B19",
				"Amendment of Supplies reported in B18 of Previous tax periods (ECOMA-15)");
		natureSuppliesMap.put("C",
				"Difference of Table 3.1 (a),3.1 (b) & 3.1.1.a and GSTR-1");
		natureSuppliesMap.put("D1",
				"Table 3.1 (c) Other outward supplies (Nil rated, exempted) - GSTR - 3B");
		natureSuppliesMap.put("D2",
				"Table 3.1.1.b Taxable supplies made by a registered person through an electronic commerce operator, "
				+ "on which electronic commerce operator is required to pay tax u/s 9(5)");
		natureSuppliesMap.put("E1",
				"Table 8 - Details of NIL Rated & Exempt supplies reported in GSTR - 1 & 1A");
		natureSuppliesMap.put("E2",
				"Taxable supplies on which electronic commerce operator pays tax u/s 9(5) (SUPECOM - 14(ii))");
		natureSuppliesMap.put("E3",
				"Amendment of Supplies reported in E2 of Previous tax periods (SUPECOM-14A(ii))");
		natureSuppliesMap.put("F", "Difference of Table 3.1 (c) , 3.1.1.b and GSTR-1");
		natureSuppliesMap.put("G", "Difference excluding NON-GST Supplies");
		natureSuppliesMap.put("H",
				"Table 3.1 (e) Non-GST outward supplies - GSTR - 3B");
		natureSuppliesMap.put("I",
				"Table 8 - Details of NON-GST Supplies reported in GSTR - 1");
		natureSuppliesMap.put("J", "Difference of Table 3.1 (e) and GSTR-1");
		natureSuppliesMap.put("K", "Net Difference of GSTR - 1 and GSTR - 3B");
		return natureSuppliesMap;
	}
	private  static void getCValie(Gstr1VsGstr3bReviewSummaryRespDto a,
			Gstr1VsGstr3bReviewSummaryRespDto b,String gstin,
			List<Gstr1VsGstr3bReviewSummaryRespDto> finalList){
		Gstr1VsGstr3bReviewSummaryRespDto respDto = new Gstr1VsGstr3bReviewSummaryRespDto();
		respDto.setTaxableValue(a.getTaxableValue().subtract(b.getTaxableValue()));
		respDto.setIgst(a.getIgst().subtract(b.getIgst()));
		respDto.setCgst(a.getCgst().subtract(b.getCgst()));
		respDto.setSgst(a.getSgst().subtract(b.getSgst()));
		respDto.setCess(a.getCess().subtract(b.getCess()));
		respDto.setTaxPeriod(a.getTaxPeriod());
		respDto.setGstin(gstin);
		respDto.setFormula("C=A-B");
		respDto.setSupplies("C");
		finalList.add(respDto);
	}
	private void getFValie(Gstr1VsGstr3bReviewSummaryRespDto a, Gstr1VsGstr3bReviewSummaryRespDto b,  Gstr1VsGstr3bReviewSummaryRespDto c, String gstin,
			List<Gstr1VsGstr3bReviewSummaryRespDto> finalList) {
		Gstr1VsGstr3bReviewSummaryRespDto respDto = new Gstr1VsGstr3bReviewSummaryRespDto();
		respDto.setTaxableValue((a.getTaxableValue().add(b.getTaxableValue())).subtract(c.getTaxableValue()));
		respDto.setIgst((a.getIgst().add(b.getIgst())).subtract(c.getIgst()));
		respDto.setCgst((a.getCgst().add(b.getCgst())).subtract(c.getCgst()));
		respDto.setSgst((a.getSgst().add(b.getSgst())).subtract(c.getSgst()));
		respDto.setCess((a.getCess().add(b.getCess())).subtract(c.getCess()));
		respDto.setTaxPeriod(a.getTaxPeriod());
		respDto.setGstin(gstin);
		respDto.setFormula("F=D1+D2-E1-E2-E3");
		respDto.setSupplies("F");
		finalList.add(respDto);
	
	}
	private void getJvalue(Gstr1VsGstr3bReviewSummaryRespDto a, Gstr1VsGstr3bReviewSummaryRespDto b, String gstin,
			List<Gstr1VsGstr3bReviewSummaryRespDto> finalList) {
		Gstr1VsGstr3bReviewSummaryRespDto respDto = new Gstr1VsGstr3bReviewSummaryRespDto();
		respDto.setIgst(a.getIgst().subtract(b.getIgst()));
		respDto.setCgst(a.getCgst().subtract(b.getCgst()));
		respDto.setSgst(a.getSgst().subtract(b.getSgst()));
		respDto.setCess(a.getCess().subtract(b.getCess()));
		respDto.setTaxableValue(a.getTaxableValue().subtract(b.getTaxableValue()));
		respDto.setTaxPeriod(a.getTaxPeriod());
		respDto.setGstin(gstin);
		respDto.setFormula("J=H-I");
		respDto.setSupplies("J");
		finalList.add(respDto);
		
	}

	private void getGvalue(Gstr1VsGstr3bReviewSummaryRespDto a, Gstr1VsGstr3bReviewSummaryRespDto b, String gstin,
			List<Gstr1VsGstr3bReviewSummaryRespDto> finalList) {
		Gstr1VsGstr3bReviewSummaryRespDto respDto = new Gstr1VsGstr3bReviewSummaryRespDto();
		respDto.setIgst(a.getIgst().add(b.getIgst()));
		respDto.setCgst(a.getCgst().add(b.getCgst()));
		respDto.setSgst(a.getSgst().add(b.getSgst()));
		respDto.setCess(a.getCess().add(b.getCess()));
		respDto.setTaxPeriod(a.getTaxPeriod());
		respDto.setTaxableValue(a.getTaxableValue().add(b.getTaxableValue()));
		respDto.setGstin(gstin);
		respDto.setFormula("G=C+F");
		respDto.setSupplies("G");
		finalList.add(respDto);
	
	}
	private void getKvalue(Gstr1VsGstr3bReviewSummaryRespDto a, Gstr1VsGstr3bReviewSummaryRespDto b, String gstin,
			List<Gstr1VsGstr3bReviewSummaryRespDto> finalList) {
		Gstr1VsGstr3bReviewSummaryRespDto respDto = new Gstr1VsGstr3bReviewSummaryRespDto();
		respDto.setTaxableValue(a.getTaxableValue().add(b.getTaxableValue()));
		respDto.setIgst(a.getIgst().add(b.getIgst()));
		respDto.setCgst(a.getCgst().add(b.getCgst()));
		respDto.setSgst(a.getSgst().add(b.getSgst()));
		respDto.setCess(a.getCess().add(b.getCess()));
		respDto.setTaxPeriod(a.getTaxPeriod());
		respDto.setGstin(gstin);
		respDto.setFormula("K=G+J");
		respDto.setSupplies("K");
		finalList.add(respDto);
	
	}
}
