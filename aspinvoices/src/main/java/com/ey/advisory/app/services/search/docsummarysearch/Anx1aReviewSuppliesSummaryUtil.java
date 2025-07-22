package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.app.docs.dto.anx1a.Anx1aSummarySuppliesRespDto;

public class Anx1aReviewSuppliesSummaryUtil {

	private static final String TABLE4 = "TABLE-4";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1aReviewSuppliesSummaryUtil.class);

	public static void convertObjectArrayIntoRespSuppliesList(
			List<Object[]> suppliesList,
			List<Anx1aSummarySuppliesRespDto> supplies, String param) {
		List<Anx1aSummarySuppliesRespDto> dbSuppliesList = new ArrayList<>();
		if (!suppliesList.isEmpty() && suppliesList.size() > 0) {
			for (Object obj[] : suppliesList) {
				Anx1aSummarySuppliesRespDto summaryScreenRespDto = new Anx1aSummarySuppliesRespDto();
				summaryScreenRespDto.setTable((String) obj[0]);
				summaryScreenRespDto.setDiffSupplyMade((BigDecimal) obj[1]);
				summaryScreenRespDto.setDiffSupplyReturn((BigDecimal) obj[2]);
				summaryScreenRespDto.setDiffNetSupply((BigDecimal) obj[3]);
				summaryScreenRespDto.setDiffIgst((BigDecimal) obj[4]);
				summaryScreenRespDto.setDiffCgst((BigDecimal) obj[5]);
				summaryScreenRespDto.setDiffSgst((BigDecimal) obj[6]);
				summaryScreenRespDto.setDiffCess((BigDecimal) obj[7]);
				summaryScreenRespDto.setDiffTaxPayble((BigDecimal) obj[8]);

				dbSuppliesList.add(summaryScreenRespDto);
			}
		}
		if (suppliesList.isEmpty()) {
			Anx1aSummarySuppliesRespDto respDto = new Anx1aSummarySuppliesRespDto();
			respDto.setAspSupplyMade(BigDecimal.ZERO);
			respDto.setAspSupplyReturn(BigDecimal.ZERO);
			respDto.setAspNetSupply(BigDecimal.ZERO);
			respDto.setTable(TABLE4);
			respDto.setAspTaxPayble(BigDecimal.ZERO);
			respDto.setAspIgst(BigDecimal.ZERO);
			respDto.setAspSgst(BigDecimal.ZERO);
			respDto.setAspCgst(BigDecimal.ZERO);
			respDto.setAspCess(BigDecimal.ZERO);
			respDto.setGstnSupplyMade(BigDecimal.ZERO);
			respDto.setGstnSupplyReturn(BigDecimal.ZERO);
			respDto.setGstnNetSupply(BigDecimal.ZERO);
			respDto.setGstnTaxPayble(BigDecimal.ZERO);
			respDto.setGstnIgst(BigDecimal.ZERO);
			respDto.setGstnSgst(BigDecimal.ZERO);
			respDto.setGstnCgst(BigDecimal.ZERO);
			respDto.setGstnCess(BigDecimal.ZERO);
			respDto.setDiffTaxPayble(BigDecimal.ZERO);
			respDto.setDiffIgst(BigDecimal.ZERO);
			respDto.setDiffSgst(BigDecimal.ZERO);
			respDto.setDiffCgst(BigDecimal.ZERO);
			respDto.setDiffCess(BigDecimal.ZERO);

			dbSuppliesList.add(respDto);
		}
		LOGGER.debug("Ecom supplies Final response --> ", dbSuppliesList);
		calculateDocTypesBySuppliesTransactions(dbSuppliesList, supplies);
	}

	private static void calculateDocTypesBySuppliesTransactions(
			List<Anx1aSummarySuppliesRespDto> dbSuppliesList,
			List<Anx1aSummarySuppliesRespDto> supplies) {
		Map<String, List<Anx1aSummarySuppliesRespDto>> returnSectionMap = createMapByTableSection(
				dbSuppliesList);
		returnSectionMap.keySet().forEach(key -> {
			List<Anx1aSummarySuppliesRespDto> dataList = returnSectionMap
					.get(key);
			if (!dataList.isEmpty() && dataList.size() > 0) {
				String docType = "";
				String tableSection = "";
				BigDecimal aspSupplyMade = BigDecimal.ZERO;
				BigDecimal aspSupplyReturn = BigDecimal.ZERO;
				BigDecimal aspNetSupply = BigDecimal.ZERO;
				BigDecimal aspTaxPayble = BigDecimal.ZERO;
				BigDecimal aspIgst = BigDecimal.ZERO;
				BigDecimal aspCgst = BigDecimal.ZERO;
				BigDecimal aspSgst = BigDecimal.ZERO;
				BigDecimal aspCess = BigDecimal.ZERO;

				BigDecimal gstnSupplyMade = BigDecimal.ZERO;
				BigDecimal gstnSupplyReturn = BigDecimal.ZERO;
				BigDecimal gstnNetSupply = BigDecimal.ZERO;
				BigDecimal gstnTaxPayble = BigDecimal.ZERO;
				BigDecimal gstnIgst = BigDecimal.ZERO;
				BigDecimal gstnCgst = BigDecimal.ZERO;
				BigDecimal gstnSgst = BigDecimal.ZERO;
				BigDecimal gstnCess = BigDecimal.ZERO;

				BigDecimal diffSupplyMade = BigDecimal.ZERO;
				BigDecimal diffSupplyReturn = BigDecimal.ZERO;
				BigDecimal diffNetSupply = BigDecimal.ZERO;
				BigDecimal diffTaxPayble = BigDecimal.ZERO;
				BigDecimal diffIgst = BigDecimal.ZERO;
				BigDecimal diffCgst = BigDecimal.ZERO;
				BigDecimal diffSgst = BigDecimal.ZERO;
				BigDecimal diffCess = BigDecimal.ZERO;

				for (Anx1aSummarySuppliesRespDto dto : dataList) {
					docType = dto.getTaxDocType();
					tableSection = dto.getTable();
					if (docType != null && (docType != null
							|| docType.equals("RCR") || docType.equals("CR")
							|| docType.equals("RFV")
							|| docType.equals("RRFV"))) {
						aspSupplyMade = aspSupplyMade
								.subtract((dto.getAspSupplyMade() != null && dto
										.getAspSupplyMade().intValue() > 0)
												? dto.getAspSupplyMade()
												: BigDecimal.ZERO);
						aspSupplyReturn = aspSupplyReturn.subtract(
								(dto.getAspSupplyReturn() != null && dto
										.getAspSupplyReturn().intValue() > 0)
												? dto.getAspSupplyReturn()
												: BigDecimal.ZERO);
						aspNetSupply = aspNetSupply
								.subtract((dto.getAspNetSupply() != null
										&& dto.getAspNetSupply().intValue() > 0)
												? dto.getAspNetSupply()
												: BigDecimal.ZERO);
						aspTaxPayble = aspTaxPayble
								.subtract((dto.getAspTaxPayble() != null
										&& dto.getAspTaxPayble().intValue() > 0)
												? dto.getAspTaxPayble()
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
						gstnSupplyMade = gstnSupplyMade.subtract(
								(dto.getGstnSupplyMade() != null && dto
										.getGstnSupplyMade().intValue() > 0)
												? dto.getGstnSupplyMade()
												: BigDecimal.ZERO);
						gstnSupplyReturn = gstnSupplyReturn.subtract(
								(dto.getGstnSupplyReturn() != null && dto
										.getGstnSupplyReturn().intValue() > 0)
												? dto.getGstnSupplyReturn()
												: BigDecimal.ZERO);
						gstnNetSupply = gstnNetSupply
								.subtract((dto.getGstnNetSupply() != null && dto
										.getGstnNetSupply().intValue() > 0)
												? dto.getGstnNetSupply()
												: BigDecimal.ZERO);
						gstnTaxPayble = gstnTaxPayble
								.subtract((dto.getGstnTaxPayble() != null && dto
										.getGstnTaxPayble().intValue() > 0)
												? dto.getGstnTaxPayble()
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
						diffSupplyMade = diffSupplyMade.subtract(
								(dto.getDiffSupplyMade() != null && dto
										.getDiffSupplyMade().intValue() > 0)
												? dto.getDiffSupplyMade()
												: BigDecimal.ZERO);
						diffSupplyReturn = diffSupplyReturn.subtract(
								(dto.getDiffSupplyReturn() != null && dto
										.getDiffSupplyReturn().intValue() > 0)
												? dto.getDiffSupplyReturn()
												: BigDecimal.ZERO);
						diffNetSupply = diffNetSupply
								.subtract((dto.getDiffNetSupply() != null && dto
										.getDiffNetSupply().intValue() > 0)
												? dto.getDiffNetSupply()
												: BigDecimal.ZERO);
						diffTaxPayble = diffTaxPayble
								.subtract((dto.getDiffTaxPayble() != null && dto
										.getDiffTaxPayble().intValue() > 0)
												? dto.getDiffTaxPayble()
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
					} else {
						aspSupplyMade = aspSupplyMade
								.add((dto.getAspSupplyMade() != null && dto
										.getAspSupplyMade().intValue() > 0)
												? dto.getAspSupplyMade()
												: BigDecimal.ZERO);
						aspSupplyReturn = aspSupplyReturn
								.add((dto.getAspSupplyReturn() != null && dto
										.getAspSupplyReturn().intValue() > 0)
												? dto.getAspSupplyReturn()
												: BigDecimal.ZERO);
						aspNetSupply = aspNetSupply
								.add((dto.getAspNetSupply() != null
										&& dto.getAspNetSupply().intValue() > 0)
												? dto.getAspNetSupply()
												: BigDecimal.ZERO);
						aspTaxPayble = aspTaxPayble
								.add((dto.getAspTaxPayble() != null
										&& dto.getAspTaxPayble().intValue() > 0)
												? dto.getAspTaxPayble()
												: BigDecimal.ZERO);
						aspCess = aspCess.add((dto.getAspCess() != null
								&& dto.getAspCess().intValue() > 0)
										? dto.getAspCess() : BigDecimal.ZERO);
						aspIgst = aspIgst.add((dto.getAspIgst() != null
								&& dto.getAspIgst().intValue() > 0)
										? dto.getAspIgst() : BigDecimal.ZERO);
						aspCgst = aspCgst.add((dto.getAspCgst() != null
								&& dto.getAspCgst().intValue() > 0)
										? dto.getAspCgst() : BigDecimal.ZERO);
						aspSgst = aspSgst.add((dto.getAspSgst() != null
								&& dto.getAspSgst().intValue() > 0)
										? dto.getAspSgst() : BigDecimal.ZERO);
						gstnSupplyMade = gstnSupplyMade
								.add((dto.getGstnSupplyMade() != null && dto
										.getGstnSupplyMade().intValue() > 0)
												? dto.getGstnSupplyMade()
												: BigDecimal.ZERO);
						gstnSupplyReturn = gstnSupplyReturn
								.add((dto.getGstnSupplyReturn() != null && dto
										.getGstnSupplyReturn().intValue() > 0)
												? dto.getGstnSupplyReturn()
												: BigDecimal.ZERO);
						gstnNetSupply = gstnNetSupply
								.add((dto.getGstnNetSupply() != null && dto
										.getGstnNetSupply().intValue() > 0)
												? dto.getGstnNetSupply()
												: BigDecimal.ZERO);
						gstnTaxPayble = gstnTaxPayble
								.add((dto.getGstnTaxPayble() != null && dto
										.getGstnTaxPayble().intValue() > 0)
												? dto.getGstnTaxPayble()
												: BigDecimal.ZERO);
						gstnCess = gstnCess.add((dto.getGstnCess() != null
								&& dto.getGstnCess().intValue() > 0)
										? dto.getGstnCess() : BigDecimal.ZERO);
						gstnIgst = gstnIgst.add((dto.getGstnIgst() != null
								&& dto.getGstnIgst().intValue() > 0)
										? dto.getGstnIgst() : BigDecimal.ZERO);
						gstnCgst = gstnCgst.add((dto.getGstnCgst() != null
								&& dto.getGstnCgst().intValue() > 0)
										? dto.getGstnCgst() : BigDecimal.ZERO);
						gstnCgst = gstnCgst.add((dto.getGstnSgst() != null
								&& dto.getGstnSgst().intValue() > 0)
										? dto.getGstnSgst() : BigDecimal.ZERO);
						diffSupplyMade = diffSupplyMade
								.add((dto.getDiffSupplyMade() != null && dto
										.getDiffSupplyMade().intValue() > 0)
												? dto.getDiffSupplyMade()
												: BigDecimal.ZERO);
						diffSupplyReturn = diffSupplyReturn
								.add((dto.getDiffSupplyReturn() != null && dto
										.getDiffSupplyReturn().intValue() > 0)
												? dto.getDiffSupplyReturn()
												: BigDecimal.ZERO);
						diffNetSupply = diffNetSupply
								.add((dto.getDiffNetSupply() != null && dto
										.getDiffNetSupply().intValue() > 0)
												? dto.getDiffNetSupply()
												: BigDecimal.ZERO);
						diffTaxPayble = diffTaxPayble
								.add((dto.getDiffTaxPayble() != null && dto
										.getDiffTaxPayble().intValue() > 0)
												? dto.getDiffTaxPayble()
												: BigDecimal.ZERO);
						diffCess = diffCess.add((dto.getDiffCess() != null
								&& dto.getDiffCess().intValue() > 0)
										? dto.getDiffCess() : BigDecimal.ZERO);
						diffIgst = diffIgst.add((dto.getDiffIgst() != null
								&& dto.getDiffIgst().intValue() > 0)
										? dto.getDiffIgst() : BigDecimal.ZERO);
						diffCgst = diffCgst.add((dto.getDiffCgst() != null
								&& dto.getDiffCgst().intValue() > 0)
										? dto.getDiffCgst() : BigDecimal.ZERO);
						diffSgst = diffSgst.add((dto.getDiffSgst() != null
								&& dto.getDiffSgst().intValue() > 0)
										? dto.getDiffSgst() : BigDecimal.ZERO);
					}

				}

				Anx1aSummarySuppliesRespDto respDto = new Anx1aSummarySuppliesRespDto();
				// data need to be set
				respDto.setAspSupplyMade(aspSupplyMade);
				respDto.setAspSupplyReturn(aspSupplyReturn);
				respDto.setAspNetSupply(aspNetSupply);
				respDto.setTable(tableSection);
				respDto.setAspTaxPayble(aspTaxPayble);
				respDto.setAspIgst(aspIgst);
				respDto.setAspSgst(aspSgst);
				respDto.setAspCgst(aspCgst);
				respDto.setAspCess(aspCess);
				respDto.setGstnSupplyMade(gstnSupplyMade);
				respDto.setGstnSupplyReturn(gstnSupplyReturn);
				respDto.setGstnNetSupply(gstnNetSupply);
				respDto.setGstnTaxPayble(gstnTaxPayble);
				respDto.setGstnIgst(gstnIgst);
				respDto.setGstnSgst(gstnSgst);
				respDto.setGstnCgst(gstnCgst);
				respDto.setGstnCess(gstnCess);
				respDto.setDiffTaxPayble(diffTaxPayble);
				respDto.setDiffIgst(diffIgst);
				respDto.setDiffSgst(diffSgst);
				respDto.setDiffCgst(diffCgst);
				respDto.setDiffCess(diffCess);
				LOGGER.debug("Ecom supplies Final response --> ", supplies);
				supplies.add(respDto);
			}
		});

	}

	private static Map<String, List<Anx1aSummarySuppliesRespDto>> createMapByTableSection(
			List<Anx1aSummarySuppliesRespDto> dbSuppliesList) {

		Map<String, List<Anx1aSummarySuppliesRespDto>> returnSectionMap = new HashMap<String, List<Anx1aSummarySuppliesRespDto>>();

		dbSuppliesList.forEach(dto -> {
			String dataKey = dto.getTable();
			if (returnSectionMap.containsKey(dataKey)) {
				List<Anx1aSummarySuppliesRespDto> dtos = returnSectionMap
						.get(dataKey);
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			} else {
				List<Anx1aSummarySuppliesRespDto> dtos = new ArrayList<>();
				dtos.add(dto);
				returnSectionMap.put(dataKey, dtos);
			}
		});
		return returnSectionMap;

	}

	public static void calculateDiffForSupplies(
			List<Anx1aSummarySuppliesRespDto> supplies) {
		if (!supplies.isEmpty() && supplies.size() > 0) {
			supplies.forEach(dto -> {
				dto.setDiffTaxPayble(
						dto.getAspTaxPayble().subtract(dto.getGstnTaxPayble()));
				dto.setDiffSupplyMade(dto.getAspSupplyMade()
						.subtract(dto.getGstnSupplyMade()));
				dto.setDiffSupplyReturn(dto.getAspTaxPayble()
						.subtract(dto.getGstnSupplyReturn()));
				dto.setDiffNetSupply(
						dto.getAspNetSupply().subtract(dto.getGstnNetSupply()));
				dto.setDiffIgst(dto.getAspIgst().subtract(dto.getGstnIgst()));
				dto.setDiffCgst(dto.getAspCgst().subtract(dto.getGstnCgst()));
				dto.setDiffSgst(dto.getAspSgst().subtract(dto.getGstnSgst()));
				dto.setDiffCess(dto.getAspCess().subtract(dto.getGstnCess()));
			});
		}

	}

}
