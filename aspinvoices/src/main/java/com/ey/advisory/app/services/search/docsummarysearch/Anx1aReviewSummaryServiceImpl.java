package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.anx1a.Anx1aReviewSummaryDao;
import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummaryInwardOutwardItemRespDto;
import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummaryInwardOutwardRespDto;
import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummaryRespDto;
import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummarySuppliesRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

@Service("Anx1aReviewSummaryServiceImpl")
public class Anx1aReviewSummaryServiceImpl
		implements Anx1aReviewSummaryService {

	private static final String outwardSections = "outwardSections";
	private static final String inwardSections = "inwardSections";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1aReviewSummaryServiceImpl.class);

	@Autowired
	@Qualifier("Anx1aReviewSummaryDaoImpl")
	private Anx1aReviewSummaryDao anx1aReviewSummaryDao;

	public Anx1aSummaryRespDto getAnx1aReviewSummary(
			Annexure1SummaryReqDto anx1aSummaryReqDto) {

		Anx1aSummaryRespDto anx1aSummaryRespDto = new Anx1aSummaryRespDto();

		List<Anx1aSummaryInwardOutwardRespDto> outward = new ArrayList<>();
		List<Anx1aSummaryInwardOutwardRespDto> inward = new ArrayList<>();
		List<Anx1aSummarySuppliesRespDto> supplies = new ArrayList<>();

		List<Object[]> outwardList = anx1aReviewSummaryDao
				.getOutwardAnx1aReviewSummary(anx1aSummaryReqDto);
		String outwardParam = "outwardSections";
		String inwardParam = "inwardSections";
		String table4Param = "TABLE-4";
		convertObjectArrayIntoRespList(outwardList, outward, outwardParam);
		LOGGER.debug("OutwardList with outwardParam section --> ",
				outwardList + outwardParam);
		List<Object[]> inwardList = anx1aReviewSummaryDao
				.getInwardAnx1aReviewSummary(anx1aSummaryReqDto);
		convertObjectArrayIntoRespList(inwardList, inward, inwardParam);
		LOGGER.debug("inwardList with inwardParam section --> ",
				inwardList + inwardParam);
		List<Object[]> suppliesList = anx1aReviewSummaryDao
				.getSuppliesAnx1aReviewSummary(anx1aSummaryReqDto);

		Anx1aReviewSuppliesSummaryUtil.convertObjectArrayIntoRespSuppliesList(
				suppliesList, supplies, table4Param);
		LOGGER.debug("suppliesList with table4Param section --> ",
				suppliesList + table4Param);
		calculateDiffForInwardOutward(outward);
		calculateDiffForInwardOutward(inward);
		Anx1aReviewSuppliesSummaryUtil.calculateDiffForSupplies(supplies);

		List<String> mandatorySections = Arrays.asList("RNV", "RCR", "RDR");
		appendDefaultDataWhenNotFound(outward, mandatorySections);
		anx1aSummaryRespDto.setOutward(outward);
		appendDefaultDataWhenNotFound(inward, mandatorySections);
		anx1aSummaryRespDto.setInward(inward);
		anx1aSummaryRespDto.setSupplies(supplies);
		return anx1aSummaryRespDto;
	}

	private void appendDefaultDataWhenNotFound(
			List<Anx1aSummaryInwardOutwardRespDto> outward,
			List<String> mandatorySections) {
		outward.forEach(outDto -> {
			List<Anx1aSummaryInwardOutwardItemRespDto> items = outDto
					.getItems();
			List<String> availabilitySections = items.stream()
					.map(Anx1aSummaryInwardOutwardItemRespDto::getTable)
					.collect(Collectors.toList());
			List<String> notPresent = mandatorySections.stream()
					.filter(st -> !availabilitySections.contains(st))
					.collect(Collectors.toList());
			items.addAll(addDefaultDataToItemList(notPresent));
		});
	}

	private List<Anx1aSummaryInwardOutwardItemRespDto> addDefaultDataToItemList(
			List<String> notPresent) {
		List<Anx1aSummaryInwardOutwardItemRespDto> dtos = new ArrayList<>();
		notPresent.forEach(section -> {
			Anx1aSummaryInwardOutwardItemRespDto dto = new Anx1aSummaryInwardOutwardItemRespDto();
			dto.setAspCount(0);
			dto.setTable(section);
			dto.setAspInvoiceValue(BigDecimal.ZERO);
			dto.setAspTaxableValue(BigDecimal.ZERO);
			dto.setAspTaxPayble(BigDecimal.ZERO);
			dto.setAspIgst(BigDecimal.ZERO);
			dto.setAspSgst(BigDecimal.ZERO);
			dto.setAspCgst(BigDecimal.ZERO);
			dto.setAspCess(BigDecimal.ZERO);
			dto.setGstnCount(0);
			dto.setGstnInvoiceValue(BigDecimal.ZERO);
			dto.setGstnTaxableValue(BigDecimal.ZERO);
			dto.setGstnTaxPayble(BigDecimal.ZERO);
			dto.setGstnIgst(BigDecimal.ZERO);
			dto.setGstnSgst(BigDecimal.ZERO);
			dto.setGstnCgst(BigDecimal.ZERO);
			dto.setGstnCess(BigDecimal.ZERO);
			dto.setDiffCount(0);
			dto.setDiffInvoiceValue(BigDecimal.ZERO);
			dto.setDiffTaxableValue(BigDecimal.ZERO);
			dto.setDiffTaxPayble(BigDecimal.ZERO);
			dto.setDiffIgst(BigDecimal.ZERO);
			dto.setDiffSgst(BigDecimal.ZERO);
			dto.setDiffCgst(BigDecimal.ZERO);
			dto.setDiffCess(BigDecimal.ZERO);
			dtos.add(dto);
		});

		return dtos;
	}

	private void calculateDiffForInwardOutward(
			List<Anx1aSummaryInwardOutwardRespDto> outward) {
		if (!outward.isEmpty() && outward.size() > 0) {
			outward.forEach(dto -> {
				dto.setDiffCount(dto.getAspCount() - dto.getGstnCount());
				dto.setDiffInvoiceValue(dto.getAspInvoiceValue()
						.subtract(dto.getGstnInvoiceValue()));
				dto.setDiffTaxableValue(dto.getAspTaxableValue()
						.subtract(dto.getGstnTaxableValue()));
				dto.setDiffTaxPayble(
						dto.getAspTaxPayble().subtract(dto.getGstnTaxPayble()));
				dto.setDiffIgst(dto.getAspIgst().subtract(dto.getGstnIgst()));
				dto.setDiffCgst(dto.getAspCgst().subtract(dto.getGstnIgst()));
				dto.setDiffSgst(dto.getAspSgst().subtract(dto.getGstnSgst()));
				dto.setDiffCess(dto.getAspCess().subtract(dto.getGstnCess()));
			});
		}

	}

	private void convertObjectArrayIntoRespList(List<Object[]> objectsList,
			List<Anx1aSummaryInwardOutwardRespDto> responseList, String param) {
		List<Anx1aSummaryInwardOutwardRespDto> dbResponseList = new ArrayList<>();
		List<String> sectionList = new ArrayList<>();
		if (!objectsList.isEmpty() && objectsList.size() > 0) {
			for (Object obj[] : objectsList) {
				Anx1aSummaryInwardOutwardRespDto summaryScreenRespDto = new Anx1aSummaryInwardOutwardRespDto();
				summaryScreenRespDto
						.setAspCount(Integer.parseInt(obj[0].toString()));
				summaryScreenRespDto.setTaxDocType((String) obj[1]);
				summaryScreenRespDto.setTable((String) obj[2]);
				summaryScreenRespDto.setAspInvoiceValue((BigDecimal) obj[3]);
				summaryScreenRespDto.setAspTaxableValue((BigDecimal) obj[4]);
				summaryScreenRespDto.setAspTaxPayble((BigDecimal) obj[5]);
				summaryScreenRespDto.setAspIgst((BigDecimal) obj[6]);
				summaryScreenRespDto.setAspCgst((BigDecimal) obj[7]);
				summaryScreenRespDto.setAspSgst((BigDecimal) obj[8]);
				summaryScreenRespDto.setAspCess((BigDecimal) obj[9]);
				sectionList.add((String) obj[2]);
				dbResponseList.add(summaryScreenRespDto);
			}
		}
		if (param.equalsIgnoreCase(outwardSections)) {
			if (objectsList.isEmpty() || !sectionList.contains("3A")) {
				Anx1aSummaryInwardOutwardRespDto dto = new Anx1aSummaryInwardOutwardRespDto();
				Anx1ASectionSummeryDefaultValuesUtil.DefaultValues(dto, "3A");
				dbResponseList.add(dto);
			}
			if (objectsList.isEmpty() || !sectionList.contains("3C")) {
				Anx1aSummaryInwardOutwardRespDto dto = new Anx1aSummaryInwardOutwardRespDto();
				Anx1ASectionSummeryDefaultValuesUtil.DefaultValues(dto, "3C");
				dbResponseList.add(dto);
			}
			if (objectsList.isEmpty() || !sectionList.contains("3D")) {
				Anx1aSummaryInwardOutwardRespDto dto = new Anx1aSummaryInwardOutwardRespDto();
				Anx1ASectionSummeryDefaultValuesUtil.DefaultValues(dto, "3D");
				dbResponseList.add(dto);
			}
		}
		if (param.equalsIgnoreCase(inwardSections)) {
			if (objectsList.isEmpty() || !sectionList.contains("3H")) {
				Anx1aSummaryInwardOutwardRespDto dto = new Anx1aSummaryInwardOutwardRespDto();
				Anx1ASectionSummeryDefaultValuesUtil.DefaultValues(dto, "3H");
				dbResponseList.add(dto);
			}
			if (objectsList.isEmpty() || !sectionList.contains("3I")) {
				Anx1aSummaryInwardOutwardRespDto dto = new Anx1aSummaryInwardOutwardRespDto();
				Anx1ASectionSummeryDefaultValuesUtil.DefaultValues(dto, "3I");
				dbResponseList.add(dto);
			}
			if (objectsList.isEmpty() || !sectionList.contains("3J")) {
				Anx1aSummaryInwardOutwardRespDto dto = new Anx1aSummaryInwardOutwardRespDto();
				Anx1ASectionSummeryDefaultValuesUtil.DefaultValues(dto, "3J");
				dbResponseList.add(dto);
			}
			if (objectsList.isEmpty() || !sectionList.contains("3K")) {
				Anx1aSummaryInwardOutwardRespDto dto = new Anx1aSummaryInwardOutwardRespDto();
				Anx1ASectionSummeryDefaultValuesUtil.DefaultValues(dto, "3K");
				dbResponseList.add(dto);
			}
		}

		calculateDocTypesByTransactions(dbResponseList, responseList);
	}

	private void calculateDocTypesByTransactions(
			List<Anx1aSummaryInwardOutwardRespDto> dbResponseList,
			List<Anx1aSummaryInwardOutwardRespDto> responseList) {
		Map<String, List<Anx1aSummaryInwardOutwardRespDto>> returnSectionMap = createMapByTableSection(
				dbResponseList);
		returnSectionMap.keySet().forEach(key -> {
			List<Anx1aSummaryInwardOutwardRespDto> dataList = returnSectionMap
					.get(key);
			if (!dataList.isEmpty() && dataList.size() > 0) {
				List<Anx1aSummaryInwardOutwardItemRespDto> items = new ArrayList<>();
				String docType = "";
				String tableSection = "";
				Integer aspCount = 0;
				BigDecimal aspInvoiceValue = BigDecimal.ZERO;
				BigDecimal aspTaxPayble = BigDecimal.ZERO;
				BigDecimal aspTaxableValue = BigDecimal.ZERO;
				BigDecimal aspIgst = BigDecimal.ZERO;
				BigDecimal aspCgst = BigDecimal.ZERO;
				BigDecimal aspSgst = BigDecimal.ZERO;
				BigDecimal aspCess = BigDecimal.ZERO;
				Integer gstnCount = 0;
				BigDecimal gstnInvoiceValue = BigDecimal.ZERO;
				BigDecimal gstnTaxableValue = BigDecimal.ZERO;
				BigDecimal gstnTaxPayble = BigDecimal.ZERO;
				BigDecimal gstnIgst = BigDecimal.ZERO;
				BigDecimal gstnCgst = BigDecimal.ZERO;
				BigDecimal gstnSgst = BigDecimal.ZERO;
				BigDecimal gstnCess = BigDecimal.ZERO;
				Integer diffCount = 0;
				BigDecimal diffInvoiceValue = BigDecimal.ZERO;
				BigDecimal diffTaxableValue = BigDecimal.ZERO;
				BigDecimal diffTaxPayble = BigDecimal.ZERO;
				BigDecimal diffIgst = BigDecimal.ZERO;
				BigDecimal diffCgst = BigDecimal.ZERO;
				BigDecimal diffSgst = BigDecimal.ZERO;
				BigDecimal diffCess = BigDecimal.ZERO;

				for (Anx1aSummaryInwardOutwardRespDto dto : dataList) {
					docType = dto.getTaxDocType();
					tableSection = dto.getTable();
					aspCount = aspCount + dto.getAspCount().intValue();
					gstnCount = dto.getGstnCount().intValue();
					diffCount = dto.getDiffCount().intValue();

					if (docType != null && !docType.equals("")
							&& (docType.equals("CR") || docType.equals("RFV")
									|| docType.equals("RCR"))) {
						aspTaxPayble = aspTaxPayble
								.subtract((dto.getAspTaxPayble() != null
										&& dto.getAspTaxPayble().intValue() > 0)
												? dto.getAspTaxPayble()
												: BigDecimal.ZERO);
						aspTaxableValue = aspTaxableValue.subtract(
								(dto.getAspTaxableValue() != null && dto
										.getAspTaxableValue().intValue() > 0)
												? dto.getAspTaxableValue()
												: BigDecimal.ZERO);

						aspIgst = aspIgst.subtract((dto.getAspIgst() != null
								&& dto.getAspIgst().intValue() > 0)
										? dto.getAspIgst() : BigDecimal.ZERO);
						aspCgst = aspCgst.subtract((dto.getAspCgst() != null
								&& dto.getAspCgst().intValue() > 0)
										? dto.getAspCgst() : BigDecimal.ZERO);
						aspSgst = aspSgst.subtract((dto.getAspSgst() != null
								&& dto.getAspSgst().intValue() > 0)
										? dto.getAspSgst() : BigDecimal.ZERO);
						aspCess = aspCess.subtract((dto.getAspCess() != null
								&& dto.getAspCess().intValue() > 0)
										? dto.getAspCess() : BigDecimal.ZERO);
						aspInvoiceValue = aspInvoiceValue.subtract(
								(dto.getAspInvoiceValue() != null && dto
										.getAspInvoiceValue().intValue() > 0)
												? dto.getAspInvoiceValue()
												: BigDecimal.ZERO);
						gstnTaxPayble = gstnTaxPayble
								.subtract((dto.getGstnTaxPayble() != null && dto
										.getGstnTaxPayble().intValue() > 0)
												? dto.getGstnTaxPayble()
												: BigDecimal.ZERO);
						gstnTaxableValue = gstnTaxableValue.subtract(
								(dto.getGstnTaxableValue() != null && dto
										.getGstnTaxableValue().intValue() > 0)
												? dto.getGstnTaxableValue()
												: BigDecimal.ZERO);

						gstnIgst = gstnIgst.subtract((dto.getGstnIgst() != null
								&& dto.getGstnIgst().intValue() > 0)
										? dto.getGstnIgst() : BigDecimal.ZERO);
						gstnCgst = gstnCgst.subtract((dto.getGstnCgst() != null
								&& dto.getGstnCgst().intValue() > 0)
										? dto.getGstnCgst() : BigDecimal.ZERO);
						gstnSgst = gstnSgst.subtract((dto.getGstnSgst() != null
								&& dto.getGstnSgst().intValue() > 0)
										? dto.getGstnSgst() : BigDecimal.ZERO);
						gstnCess = gstnCess.subtract((dto.getGstnCess() != null
								&& dto.getGstnCess().intValue() > 0)
										? dto.getGstnCess() : BigDecimal.ZERO);
						gstnInvoiceValue = gstnInvoiceValue.subtract(
								(dto.getGstnInvoiceValue() != null && dto
										.getGstnInvoiceValue().intValue() > 0)
												? dto.getGstnInvoiceValue()
												: BigDecimal.ZERO);
						diffTaxPayble = diffTaxPayble
								.subtract((dto.getDiffTaxPayble() != null && dto
										.getDiffTaxPayble().intValue() > 0)
												? dto.getDiffTaxPayble()
												: BigDecimal.ZERO);
						diffTaxableValue = diffTaxableValue.subtract(
								(dto.getDiffTaxableValue() != null && dto
										.getDiffTaxableValue().intValue() > 0)
												? dto.getDiffTaxableValue()
												: BigDecimal.ZERO);

						diffIgst = diffIgst.subtract((dto.getDiffIgst() != null
								&& dto.getDiffIgst().intValue() > 0)
										? dto.getDiffIgst() : BigDecimal.ZERO);
						diffCgst = diffCgst.subtract((dto.getDiffCgst() != null
								&& dto.getDiffCgst().intValue() > 0)
										? dto.getDiffCgst() : BigDecimal.ZERO);
						diffSgst = diffSgst.subtract((dto.getDiffSgst() != null
								&& dto.getDiffSgst().intValue() > 0)
										? dto.getDiffSgst() : BigDecimal.ZERO);
						diffCess = diffCess.subtract((dto.getDiffCess() != null
								&& dto.getDiffCess().intValue() > 0)
										? dto.getDiffCess() : BigDecimal.ZERO);
						diffInvoiceValue = diffInvoiceValue.subtract(
								(dto.getDiffInvoiceValue() != null && dto
										.getDiffInvoiceValue().intValue() > 0)
												? dto.getDiffInvoiceValue()
												: BigDecimal.ZERO);
					}
					if (dto.getTaxDocType() != null) {
						items.add(convertDtoIntoItems(dto));
					}
				}

				Anx1aSummaryInwardOutwardRespDto respDto = new Anx1aSummaryInwardOutwardRespDto();
				respDto.setAspCount(aspCount);
				respDto.setTable(tableSection);
				respDto.setAspInvoiceValue(aspInvoiceValue);
				respDto.setAspTaxableValue(aspTaxableValue);
				respDto.setAspTaxPayble(aspTaxPayble);
				respDto.setAspIgst(aspIgst);
				respDto.setAspSgst(aspSgst);
				respDto.setAspCgst(aspCgst);
				respDto.setAspCess(aspCess);
				respDto.setGstnCount(gstnCount);
				respDto.setGstnInvoiceValue(gstnInvoiceValue);
				respDto.setGstnTaxableValue(gstnTaxableValue);
				respDto.setGstnTaxPayble(gstnTaxPayble);
				respDto.setGstnIgst(gstnIgst);
				respDto.setGstnSgst(gstnSgst);
				respDto.setGstnCgst(gstnCgst);
				respDto.setGstnCess(gstnCess);
				respDto.setDiffCount(diffCount);
				respDto.setDiffInvoiceValue(diffInvoiceValue);
				respDto.setDiffTaxableValue(diffTaxableValue);
				respDto.setDiffTaxPayble(diffTaxPayble);
				respDto.setDiffIgst(diffIgst);
				respDto.setDiffSgst(diffSgst);
				respDto.setDiffCgst(diffCgst);
				respDto.setDiffCess(diffCess);
				respDto.setItems(items);

				LOGGER.debug("Final response --> ", responseList);
				responseList.add(respDto);
			}
		});

	}

	@SuppressWarnings("unused")
	private Anx1aSummaryInwardOutwardItemRespDto convertDtoIntoItems(
			Anx1aSummaryInwardOutwardRespDto dto) {
		Anx1aSummaryInwardOutwardItemRespDto respDto = new Anx1aSummaryInwardOutwardItemRespDto();
		respDto.setAspCount(dto.getAspCount());
		respDto.setTable(dto.getTaxDocType());
		respDto.setAspInvoiceValue(dto.getAspInvoiceValue());
		respDto.setAspTaxableValue(dto.getAspTaxableValue());
		respDto.setAspTaxPayble(dto.getAspTaxPayble());
		respDto.setAspIgst(dto.getAspIgst());
		respDto.setAspSgst(dto.getAspSgst());
		respDto.setAspCgst(dto.getAspCgst());
		respDto.setAspCess(dto.getAspCess());
		respDto.setGstnCount(dto.getGstnCount());
		respDto.setGstnInvoiceValue(dto.getGstnInvoiceValue());
		respDto.setGstnTaxableValue(dto.getGstnTaxableValue());
		respDto.setGstnTaxPayble(dto.getGstnTaxPayble());
		respDto.setGstnIgst(dto.getGstnIgst());
		respDto.setGstnSgst(dto.getGstnSgst());
		respDto.setGstnCgst(dto.getGstnCgst());
		respDto.setGstnCess(dto.getGstnCess());
		respDto.setDiffCount(dto.getDiffCount());
		respDto.setDiffInvoiceValue(dto.getDiffInvoiceValue());
		respDto.setDiffTaxableValue(dto.getDiffTaxableValue());
		respDto.setDiffTaxPayble(dto.getDiffTaxPayble());
		respDto.setDiffIgst(dto.getDiffIgst());
		respDto.setDiffSgst(dto.getDiffSgst());
		respDto.setDiffCgst(dto.getDiffCgst());
		respDto.setDiffCess(dto.getDiffCess());
		return respDto;
	}

	private Map<String, List<Anx1aSummaryInwardOutwardRespDto>> createMapByTableSection(
			List<Anx1aSummaryInwardOutwardRespDto> dbResponseList) {

		Map<String, List<Anx1aSummaryInwardOutwardRespDto>> returnSectionMap = new HashMap<String, List<Anx1aSummaryInwardOutwardRespDto>>();

		dbResponseList.forEach(dto -> {
			String dataKey = dto.getTable();
			if (returnSectionMap.containsKey(dataKey)) {
				List<Anx1aSummaryInwardOutwardRespDto> dtos = returnSectionMap
						.get(dataKey);
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			} else {
				List<Anx1aSummaryInwardOutwardRespDto> dtos = new ArrayList<>();
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			}
		});
		return returnSectionMap;
	}

}
