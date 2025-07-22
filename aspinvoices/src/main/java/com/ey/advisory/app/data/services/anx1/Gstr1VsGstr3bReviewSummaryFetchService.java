package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.Gstr1VsGstr3bReviewSummaryFetchDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bReviewSummaryItemsRespDto;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bReviewSummaryRespDto;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component("Gstr1VsGstr3bReviewSummaryFetchService")
public class Gstr1VsGstr3bReviewSummaryFetchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1VsGstr3bReviewSummaryFetchService.class);
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

	public List<Gstr1VsGstr3bReviewSummaryRespDto> response(
			Gstr1VsGstr3bProcessSummaryReqDto req) {
		Gstr1VsGstr3bProcessSummaryReqDto reqDto = processedRecordsCommonSecParam
				.setGstr1VsGstr3bDataSecuritySearchParams(req);
		List<Gstr1VsGstr3bReviewSummaryRespDto> processedRespDtos = gstr1VsGstr3bReviewSummaryFetchDaoImpl
				.fetchGstr1VsGstr3bRecords(reqDto);
		Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> suppliesMap = Maps
				.newLinkedHashMap();
		List<Gstr1VsGstr3bReviewSummaryRespDto> respDtos = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(processedRespDtos)) {
			buildSupplies().forEach(suppl -> {
				suppliesMap.put(suppl,
						processedRespDtos.stream()
								.filter(dto -> dto.getSupplies().equals(suppl))
								.collect(Collectors.toList()));
			});

			respDtos = calculateSuppliesAndReturnList(suppliesMap, reqDto
					.getDataSecAttrs().get("GSTIN").stream().findFirst().get());
		} else {
			respDtos = buildGstr1vs3bDummyData(reqDto);
		}
		return respDtos;
	}

	private List<Gstr1VsGstr3bReviewSummaryRespDto> buildGstr1vs3bDummyData(
			Gstr1VsGstr3bProcessSummaryReqDto reqDto) {
		List<Gstr1VsGstr3bReviewSummaryRespDto> respDtos = Lists
				.newLinkedList();
		Map<String, String> suppliesMap = natureSuppliesMap();
		String gstin = reqDto.getDataSecAttrs().get("GSTIN").stream()
				.findFirst().get();
		respDtos.addAll(buildParentChiladItems(Lists.newArrayList("A"),
				Lists.newArrayList("A1", "A2", "A3"), gstin, suppliesMap));
		respDtos.addAll(buildParentChiladItems(Lists.newArrayList("B"),
				Lists.newArrayList("B1", "B2", "B3", "B4", "B5", "B6", "B7",
						"B8-B9", "B10", "B11", "B12", "B13", "B14", "B15",
						"B16-B17", "B18", "B19"),
				gstin, suppliesMap));
		respDtos.addAll(buildParentChiladItems(
				Lists.newArrayList("C", "D1", "D2", "E1","E2","E3", "F", "G", "H", "I", "J", "K"),
				Lists.newArrayList(), gstin, suppliesMap));

		return respDtos;
	}

	private List<Gstr1VsGstr3bReviewSummaryRespDto> buildParentChiladItems(
			ArrayList<String> newArrayList, ArrayList<String> newArrayList2,
			String gstin, Map<String, String> suppliesMap) {
		List<Gstr1VsGstr3bReviewSummaryRespDto> respDtos = Lists
				.newLinkedList();
		newArrayList.forEach(string -> {
			Gstr1VsGstr3bReviewSummaryRespDto dto = new Gstr1VsGstr3bReviewSummaryRespDto();
			dto.setGstin(gstin);
			dto.setSupplies(suppliesMap.get(string));
			dto.setFormula(string);
			dto.setTaxableValue(BigDecimal.ZERO);
			dto.setIgst(BigDecimal.ZERO);
			dto.setCgst(BigDecimal.ZERO);
			dto.setSgst(BigDecimal.ZERO);
			dto.setCess(BigDecimal.ZERO);
			if (CollectionUtils.isNotEmpty(newArrayList2)) {
				List<Gstr1VsGstr3bReviewSummaryItemsRespDto> items = new ArrayList<Gstr1VsGstr3bReviewSummaryItemsRespDto>();
				newArrayList2.forEach(subItm -> {
					Gstr1VsGstr3bReviewSummaryItemsRespDto itemsRespDto = new Gstr1VsGstr3bReviewSummaryItemsRespDto();
					itemsRespDto.setGstin(gstin);
					itemsRespDto.setSupplies(suppliesMap.get(subItm));
					itemsRespDto.setFormula(subItm);
					itemsRespDto.setTaxableValue(BigDecimal.ZERO);
					itemsRespDto.setIgst(BigDecimal.ZERO);
					itemsRespDto.setCgst(BigDecimal.ZERO);
					itemsRespDto.setSgst(BigDecimal.ZERO);
					itemsRespDto.setCess(BigDecimal.ZERO);
					items.add(itemsRespDto);
				});
				dto.setItems(items);
			}
			respDtos.add(dto);

		});
		return respDtos;
	}

	private List<Gstr1VsGstr3bReviewSummaryRespDto> calculateSuppliesAndReturnList(
			Map<String, List<Gstr1VsGstr3bReviewSummaryRespDto>> suppliesMap,
			String gstin) {
		Map<String, Gstr1VsGstr3bReviewSummaryRespDto> calMap = Maps
				.newLinkedHashMap();
		suppliesMap.keySet().forEach(suppl -> {
			List<Gstr1VsGstr3bReviewSummaryRespDto> supplLsit = suppliesMap
					.get(suppl);
			Gstr1VsGstr3bReviewSummaryRespDto respDto = new Gstr1VsGstr3bReviewSummaryRespDto();

			String formula = null;
			String supplies = null;
			BigDecimal taxableValue = BigDecimal.ZERO;
			BigDecimal igst = BigDecimal.ZERO;
			BigDecimal cgst = BigDecimal.ZERO;
			BigDecimal sgst = BigDecimal.ZERO;
			BigDecimal cess = BigDecimal.ZERO;
			String gstins = null;
			String taxPeriod = null;

			for (Gstr1VsGstr3bReviewSummaryRespDto dto : supplLsit) {
				formula = dto.getFormula();
				supplies = dto.getSupplies();
				taxableValue = taxableValue.add(dto.getTaxableValue());
				igst = igst.add(dto.getIgst());
				sgst = sgst.add(dto.getSgst());
				cgst = cgst.add(dto.getCgst());
				cess = cess.add(dto.getCess());
				gstins = dto.getGstin();
				taxPeriod = dto.getTaxPeriod();

			}
			respDto.setSupplies(supplies);
			respDto.setFormula(formula);
			respDto.setTaxableValue(taxableValue);
			respDto.setIgst(igst);
			respDto.setCgst(cgst);
			respDto.setSgst(sgst);
			respDto.setCess(cess);
			respDto.setGstin(gstins);
			respDto.setTaxPeriod(taxPeriod);

			calMap.put(suppl, respDto);
		});

		buildSupplies().forEach(supplies -> {
			if (calMap.containsKey(supplies)) {
				Gstr1VsGstr3bReviewSummaryRespDto dto = calMap.get(supplies);
				System.out.println(
						"Supplies--->" + supplies + " :::: dto::::" + dto);
				if (dto.getSupplies() == null || dto.getFormula() == null) {
					calMap.put(supplies,
							new Gstr1VsGstr3bReviewSummaryRespDto(supplies,
									supplies, BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, gstin, null,
									Lists.newArrayList()));
				}
			}
		});

		List<Gstr1VsGstr3bReviewSummaryRespDto> finalList = Lists
				.newLinkedList();

		try {
			calMap.keySet().forEach(sup -> {
				if (!Lists.newArrayList("C", "F", "G", "J", "K")
						.contains(sup)) {
					Gstr1VsGstr3bReviewSummaryRespDto dto = calMap.get(sup);
					amendItemsToSupplies(sup, calMap, dto);
					calculateSuppliesByFormula(sup, dto, calMap);
					finalList.add(dto);
					calMap.put(dto.getSupplies(), dto);
					LOGGER.error("calMap:::" + calMap);
				}
			});
		} catch (ConcurrentModificationException e) {
			LOGGER.error(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		finalList.forEach(dto -> dto
				.setSupplies(natureSuppliesMap().get(dto.getSupplies())));

		return finalList;

	}

	private void calculateSuppliesByFormula(String sup,
			Gstr1VsGstr3bReviewSummaryRespDto dto,
			Map<String, Gstr1VsGstr3bReviewSummaryRespDto> calMap) {
		switch (sup) {
		case "C=A-B": {
			setDataByFormular("A", "B", dto, calMap, "sub", "C");
			break;
		}
		case "G=C+F": {
			setDataByFormular("C", "F", dto, calMap, "add", "G");
			break;
		}
		case "J=H-I": {
			setDataByFormular("H", "I", dto, calMap, "sub", "J");
			break;
		}
		case "K=G+J": {
			setDataByFormular("G", "J", dto, calMap, "add", "K");
			break;
		}
		case "F=D1+D2-E": {
			setDataByFormular("D1", "D2", "E1-E2-E3", dto, calMap, "sub", "F");
			break;
		}
		}

	}

	private void setDataByFormular(String string, String string2,
			Gstr1VsGstr3bReviewSummaryRespDto dto,
			Map<String, Gstr1VsGstr3bReviewSummaryRespDto> calMap,
			String operation, String outcomeSupplies) {
		Gstr1VsGstr3bReviewSummaryRespDto dto1 = calMap.get(string);
		Gstr1VsGstr3bReviewSummaryRespDto dto2 = calMap.get(string2);
		if (operation.equals("add")) {
			LOGGER.error(string);
			LOGGER.error(string2);
			LOGGER.error(operation);
			LOGGER.error("" + dto1);
			LOGGER.error("" + dto2);

			dto.setTaxableValue(
					dto1.getTaxableValue().add(dto2.getTaxableValue()));
			dto.setIgst(dto1.getIgst().add(dto2.getIgst()));
			dto.setCgst(dto1.getCgst().add(dto2.getCgst()));
			dto.setSgst(dto1.getSgst().add(dto2.getSgst()));
			dto.setCess(dto1.getCess().add(dto2.getCess()));
			dto.setTaxPeriod(dto1.getTaxPeriod());
		} else if (operation.equals("sub")) {
			dto.setTaxableValue(
					dto1.getTaxableValue().subtract(dto2.getTaxableValue()));
			dto.setIgst(dto1.getIgst().subtract(dto2.getIgst()));
			dto.setCgst(dto1.getCgst().subtract(dto2.getCgst()));
			dto.setSgst(dto1.getSgst().subtract(dto2.getSgst()));
			dto.setCess(dto1.getCess().subtract(dto2.getCess()));
			dto.setTaxPeriod(dto1.getTaxPeriod());
		}
	
		dto.setSupplies(outcomeSupplies);
	}
	
	private void setDataByFormular(String string, String string2,String string3,
			Gstr1VsGstr3bReviewSummaryRespDto dto,
			Map<String, Gstr1VsGstr3bReviewSummaryRespDto> calMap,
			String operation, String outcomeSupplies) {
			Gstr1VsGstr3bReviewSummaryRespDto dto1 = calMap.get(string);
			Gstr1VsGstr3bReviewSummaryRespDto dto2 = calMap.get(string2);
			Gstr1VsGstr3bReviewSummaryRespDto dto3 = null;
			if (string3.contains("-")) {
		        String[] elements = string3.split("-");
		        Gstr1VsGstr3bReviewSummaryRespDto E1 = calMap.get(elements[0]);
		        Gstr1VsGstr3bReviewSummaryRespDto E2 = calMap.get(elements[1]);
		        Gstr1VsGstr3bReviewSummaryRespDto E3 = calMap.get(elements[2]);
	
		        // Calculate E1 + E2 + E3 and store the result in dto3
		        dto3 = new Gstr1VsGstr3bReviewSummaryRespDto();
		        dto3.setTaxableValue(E1.getTaxableValue().add(E2.getTaxableValue()).add(E3.getTaxableValue()));
		        dto3.setIgst(E1.getIgst().add(E2.getIgst()).add(E3.getIgst()));
		        dto3.setCgst(E1.getCgst().add(E2.getCgst()).add(E3.getCgst()));
		        dto3.setSgst(E1.getSgst().add(E2.getSgst()).add(E3.getSgst()));
		        dto3.setCess(E1.getCess().add(E2.getCess()).add(E3.getCess()));
		        dto3.setTaxPeriod(E1.getTaxPeriod());
		    } else {
		        dto3 = calMap.get(string3);
		    }
		
		  {
			LOGGER.error(string);
			LOGGER.error(string2);
			LOGGER.error(string3);
			LOGGER.error(operation);
			LOGGER.error("" + dto1);
			LOGGER.error("" + dto2);
			LOGGER.error("" + dto3);
			dto.setTaxableValue((
					dto1.getTaxableValue().add(dto2.getTaxableValue())).subtract(dto3.getTaxableValue()));
			dto.setIgst((dto1.getIgst().add(dto2.getIgst())).subtract(dto3.getIgst()));
			dto.setCgst((dto1.getCgst().add(dto2.getCgst())).subtract(dto3.getCgst()));
			dto.setSgst((dto1.getSgst().add(dto2.getSgst())).subtract(dto3.getSgst()));
			dto.setCess((dto1.getCess().add(dto2.getCess())).subtract(dto3.getCess()));
			dto.setTaxPeriod(dto1.getTaxPeriod());
		}
		
		dto.setSupplies(outcomeSupplies);
		}

	private void amendItemsToSupplies(String sup,
			Map<String, Gstr1VsGstr3bReviewSummaryRespDto> calMap,
			Gstr1VsGstr3bReviewSummaryRespDto dto) {
		if (sup.equalsIgnoreCase("A")) {
			Lists.newArrayList("A1", "A2", "A3").forEach(suppl -> {
				Gstr1VsGstr3bReviewSummaryRespDto respDto = calMap.get(suppl);
				buildAndUpdateSubItems(respDto, dto);
				calMap.remove(suppl);
			});
		}
		if (sup.equalsIgnoreCase("B")) {
			Lists.newArrayList("B1", "B2", "B3", "B4", "B5", "B6", "B7",
					"B8-B9", "B10", "B11", "B12", "B13", "B14", "B15",
					"B16-B17","B18", "B19").forEach(suppl -> {
						Gstr1VsGstr3bReviewSummaryRespDto respDto = calMap
								.get(suppl);
						buildAndUpdateSubItems(respDto, dto);
						calMap.remove(suppl);
					});
		}
	}

	private void buildAndUpdateSubItems(
			Gstr1VsGstr3bReviewSummaryRespDto respDto,
			Gstr1VsGstr3bReviewSummaryRespDto dto) {
		List<Gstr1VsGstr3bReviewSummaryItemsRespDto> items = new ArrayList<Gstr1VsGstr3bReviewSummaryItemsRespDto>();
		Gstr1VsGstr3bReviewSummaryItemsRespDto itemRespDto = new Gstr1VsGstr3bReviewSummaryItemsRespDto();
		itemRespDto.setSupplies(respDto.getSupplies());
		itemRespDto.setFormula(respDto.getFormula());
		itemRespDto.setTaxableValue(respDto.getTaxableValue());
		itemRespDto.setIgst(respDto.getIgst());
		itemRespDto.setCgst(respDto.getCgst());
		itemRespDto.setSgst(respDto.getSgst());
		itemRespDto.setCess(respDto.getCess());
		itemRespDto.setGstin(respDto.getGstin());
		itemRespDto.setTaxPeriod(respDto.getTaxPeriod());

		items.add(itemRespDto);
		dto.getItems().addAll(items);

	}
//changes as part of US-146137-GSTR-1A | GSTR-1/1A vs GSTR-3B (UI Changes, Recon report)
	private Map<String, String> natureSuppliesMap() {
		Map<String, String> natureSuppliesMap = Maps.newHashMap();
		natureSuppliesMap.put("A", "Table 3.1 (a), 3.1 (b) & 3.1.1.a as per GSTR - 3B");
		natureSuppliesMap.put("A1",
				"Table 3.1(a) Outward Taxable Supplies (other than zero rated, nil rated and exempted)");
		natureSuppliesMap.put("A2",
				"Table 3.1(b) Outward taxable supplies (zero rated)");
		natureSuppliesMap.put("A3",
				"Table 3.1.1.a Taxable supplies on which electronic commerce operator pays tax u/s 9(5)");
		natureSuppliesMap.put("B", "Details of supplies reported in GSTR - 1 & 1A");
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
				"Difference of Table 3.1 (a),3.1 (b) & 3.1.1.a and GSTR-1 & 1A");
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
		
		natureSuppliesMap.put("F", "Difference of Table 3.1 (c) , 3.1.1.b and GSTR-1 & 1A");
		natureSuppliesMap.put("G", "Difference excluding NON-GST Supplies");
		natureSuppliesMap.put("H",
				"Table 3.1 (e) Non-GST outward supplies - GSTR - 3B");
		natureSuppliesMap.put("I",
				"Table 8 - Details of NON-GST Supplies reported in GSTR - 1 & 1A");
		natureSuppliesMap.put("J", "Difference of Table 3.1 (e) and GSTR-1 & 1A");
		natureSuppliesMap.put("K", "Net Difference of GSTR - 1 & 1A and GSTR - 3B");
		return natureSuppliesMap;
	}

}
