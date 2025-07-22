package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.beust.jcommander.internal.Maps;
import com.ey.advisory.app.docs.dto.simplified.ITC04PopupRespDto;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ITC04PopupReqDto;
import com.google.common.collect.Lists;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Component("ITC04PopupFetchDao")
public class ITC04PopupFetchDao {

	private static final String IG = "IG";
	private static final String CG = "CG";
	private static final String TABLE4 = "table4";
	private static final String TABLE5A = "table5A";
	private static final String TABLE5B = "table5B";
	private static final String TABLE5C = "table5C";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	private static Map<String, String> tableMap = Maps.newHashMap();

	static {
		tableMap.put(TABLE4, "Table 4 - M2JW");
		tableMap.put(TABLE5A, "Table 5A - JW2M");
		tableMap.put(TABLE5B, "Table 5B - Other JW2M");
		tableMap.put(TABLE5C, "Table 5C - Sold from JW");
		tableMap.put(IG, "IG");
		tableMap.put(CG, "CG");
	}

	public Collection<? extends ITC04PopupRespDto> findByCriteria(
			ITC04PopupReqDto dto, List<String> gstinList,
			List<String> divisionList, List<String> pcList,
			List<String> pc2List,List<String> plantList) {
		List<ITC04PopupRespDto> respDtos = Lists.newArrayList();
		String type = dto.getType();
		String taxPeriod = dto.getTaxPeriod();

		List<String> types = Lists.newLinkedList();
		if (type.contains("/")) {
			String typeArray[] = type.split("/");
			for (int i = 0; i < typeArray.length; i++) {
				types.add(typeArray[i]);
			}
		} else {
			types.add(type);
		}

		types.forEach(typeStr -> {
			switch (typeStr) {
			case TABLE4:
				List<ITC04PopupRespDto> dto4s = queryAndbuildTableData(
						taxPeriod, gstinList, divisionList, pcList, pc2List,plantList,
						createQueryStringForTable4(gstinList, taxPeriod, divisionList, pcList, pc2List,plantList));
				respDtos.add(buildData(dto4s, typeStr));
				break;
			case TABLE5A:
				List<ITC04PopupRespDto> dto5As = queryAndbuildTableData(
						taxPeriod, gstinList, divisionList, pcList, pc2List,plantList,
						createQueryStringForTable5A(gstinList, taxPeriod, divisionList, pcList, pc2List,plantList));
				respDtos.add(buildData(dto5As, typeStr));
				break;
			case TABLE5B:
				List<ITC04PopupRespDto> dto5Bs = queryAndbuildTableData(
						taxPeriod, gstinList, divisionList, pcList, pc2List,plantList,
						createQueryStringForTable5B(gstinList, taxPeriod, divisionList, pcList, pc2List,plantList));
				respDtos.add(buildData(dto5Bs, typeStr));
				break;
			case TABLE5C:
				List<ITC04PopupRespDto> dto5Cs = queryAndbuildTableData(
						taxPeriod, gstinList, divisionList, pcList, pc2List,plantList,
						createQueryStringForTable5C(gstinList, taxPeriod, divisionList, pcList, pc2List,plantList));
				respDtos.add(buildData(dto5Cs, typeStr));
				break;
			}
		});

		return respDtos;
	}

	private ITC04PopupRespDto buildData(List<ITC04PopupRespDto> dtos,
			String typeStr) {
		if (CollectionUtils.isNotEmpty(dtos)) {
			if (!typeStr.equals(TABLE4)) {
				Optional<ITC04PopupRespDto> dto = dtos.stream().findFirst();
				if (dto.isPresent()) {
					return dto.get();
				} else {
					return dummayData(typeStr);
				}
			} else {
				ITC04PopupRespDto table4dto = new ITC04PopupRespDto();
				List<ITC04PopupRespDto> mjDtos = dtos.stream().filter(
						dto -> dto.getType().equals(tableMap.get(typeStr)))
						.collect(Collectors.toList());
				List<ITC04PopupRespDto> mjDtoItms = dtos.stream().filter(
						dto -> !dto.getType().equals(tableMap.get(typeStr)))
						.collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(mjDtos)) {
					Optional<ITC04PopupRespDto> mjDto = mjDtos.stream()
							.findFirst();
					if (mjDto.isPresent()) {
						table4dto = mjDto.get();
					} else {
						table4dto = dummayData(typeStr);
					}
				}
				List<ITC04PopupRespDto> mjItems = Lists.newArrayList();
				if (CollectionUtils.isNotEmpty(mjDtoItms)) {
					mjDtoItms.forEach(mjItm -> {
						mjItems.add(mjItm);
					});
					List<String> mjITypes = mjItems.stream()
							.map(ITC04PopupRespDto::getType)
							.collect(Collectors.toList());
					if (!mjITypes.contains(IG)) {
						mjItems.add(dummayData(IG));
					} else if (!mjITypes.contains(CG)) {
						mjItems.add(dummayData(CG));
					}
				} else {
					mjItems.add(dummayData(IG));
					mjItems.add(dummayData(CG));
				}
				table4dto.setItems(mjItems);
				return table4dto;
			}
		}
		return null;
	}

	private ITC04PopupRespDto dummayData(String typeStr) {
		ITC04PopupRespDto respDto = new ITC04PopupRespDto();
		respDto.setType(tableMap.get(typeStr));
		respDto.setCount(0);
		respDto.setQuantity(BigDecimal.ZERO);
		respDto.setLossesQuantity(BigDecimal.ZERO);
		respDto.setTaxableValue(BigDecimal.ZERO);
		respDto.setIgst(BigDecimal.ZERO);
		respDto.setCgst(BigDecimal.ZERO);
		respDto.setSgst(BigDecimal.ZERO);
		respDto.setCess(BigDecimal.ZERO);
		respDto.setStateCess(BigDecimal.ZERO);
		respDto.setTotalValue(BigDecimal.ZERO);
		return respDto;
	}

	private StringBuilder createQueryStringForTable5C(List<String> gstinList,
			String taxPeriod, List<String> divisionList, List<String> pcList,
			List<String> pc2List,List<String> plantList) {
		StringBuilder bufferString = new StringBuilder();
		String condition = conditionsString(gstinList, taxPeriod, divisionList, pcList, pc2List, plantList).toString();

		bufferString.append(
				"SELECT 'Table 5C - Sold from JW' AS GOODS_TYPE, COUNT (DISTINCT DOC_KEY )AS COUNT_KEY, ");
		bufferString.append(
				"SUM(IFNULL(ITM.ITM_QTY,0)) AS ITM_QTY, SUM(IFNULL(ITM.LOSSES_QTY,0)) AS LOSSES_QTY, ");
		bufferString.append(
				"SUM(IFNULL(ITM.TAXABLE_VALUE,0)) AS TAXABLE_VALUE, SUM(IFNULL(ITM.IGST_AMT,0)) AS IGST_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.CGST_AMT,0)) AS CGST_AMT, SUM(IFNULL(ITM.SGST_AMT,0))AS SGST_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0)) + SUM(IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS CESS_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.STATE_CESS_AMT_SPECIFIC,0))+ SUM(IFNULL(ITM.STATE_CESS_AMT_ADVALOREM,0)) ");
		bufferString.append(
				"AS STATECESS_AMT, SUM(IFNULL(ITM.TOTAL_VALUE,0)) AS TOTAL_VALUE ");
		bufferString
				.append("FROM ITC04_HEADER HDR INNER JOIN ITC04_ITEM ITM ON ");
		bufferString.append(
				"HDR.ID=ITM.DOC_HEADER_ID AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD WHERE IS_DELETE=FALSE ");
		bufferString.append(
				"AND IS_PROCESSED=TRUE AND (ACTION_TYPE IS NULL OR ACTION_TYPE <>'CAN') ");

		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append(" AND TABLE_NUMBER='5C' ");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("bufferString--------------------------> {} ",
					bufferString);
		}

		return bufferString;
	}

	private StringBuilder createQueryStringForTable5B(List<String> gstinList,
			String taxPeriod, List<String> divisionList, List<String> pcList,
			List<String> pc2List, List<String> plantList) {
		StringBuilder bufferString = new StringBuilder();
		String condition = conditionsString(gstinList, taxPeriod, divisionList, pcList, pc2List, plantList).toString();

		bufferString.append(
				"SELECT 'Table 5B - Other JW2M' AS GOODS_TYPE, COUNT (DISTINCT DOC_KEY )AS COUNT_KEY, ");
		bufferString.append(
				"SUM(IFNULL(ITM.ITM_QTY,0)) AS ITM_QTY, SUM(IFNULL(ITM.LOSSES_QTY,0)) AS LOSSES_QTY, ");
		bufferString.append(
				"SUM(IFNULL(ITM.TAXABLE_VALUE,0)) AS TAXABLE_VALUE, SUM(IFNULL(ITM.IGST_AMT,0)) AS IGST_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.CGST_AMT,0)) AS CGST_AMT, SUM(IFNULL(ITM.SGST_AMT,0))AS SGST_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0)) + SUM(IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS CESS_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.STATE_CESS_AMT_SPECIFIC,0))+ SUM(IFNULL(ITM.STATE_CESS_AMT_ADVALOREM,0)) ");
		bufferString.append(
				"AS STATECESS_AMT, SUM(IFNULL(ITM.TOTAL_VALUE,0)) AS TOTAL_VALUE ");
		bufferString.append(
				"FROM ITC04_HEADER HDR INNER JOIN ITC04_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID ");
		bufferString.append(
				"AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD WHERE IS_DELETE=FALSE AND IS_PROCESSED=TRUE ");
		bufferString
				.append("AND (ACTION_TYPE IS NULL OR ACTION_TYPE <>'CAN') ");
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append(" AND TABLE_NUMBER='5B' ");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("bufferString--------------------------> {} ",
					bufferString);
		}

		return bufferString;
	}

	private StringBuilder createQueryStringForTable5A(List<String> gstinList,
			String taxPeriod, List<String> divisionList, List<String> pcList,
			List<String> pc2List,List<String> plantList) {
		StringBuilder bufferString = new StringBuilder();
		String condition = conditionsString(gstinList, taxPeriod, divisionList, pcList, pc2List, plantList).toString();

		bufferString.append(
				"SELECT 'Table 5A - JW2M' AS GOODS_TYPE, COUNT (DISTINCT DOC_KEY )AS COUNT_KEY, ");
		bufferString.append(
				"SUM(IFNULL(ITM.ITM_QTY,0)) AS ITM_QTY, SUM(IFNULL(ITM.LOSSES_QTY,0)) AS LOSSES_QTY, ");
		bufferString.append(
				"SUM(IFNULL(ITM.TAXABLE_VALUE,0)) AS TAXABLE_VALUE, SUM(IFNULL(ITM.IGST_AMT,0)) AS IGST_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.CGST_AMT,0)) AS CGST_AMT, SUM(IFNULL(ITM.SGST_AMT,0))AS SGST_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0)) + SUM(IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS CESS_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.STATE_CESS_AMT_SPECIFIC,0))+ SUM(IFNULL(ITM.STATE_CESS_AMT_ADVALOREM,0)) ");
		bufferString.append(
				"AS STATECESS_AMT, SUM(IFNULL(ITM.TOTAL_VALUE,0)) AS TOTAL_VALUE ");
		bufferString.append(
				"FROM ITC04_HEADER HDR INNER JOIN ITC04_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID ");
		bufferString.append(
				"AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD WHERE IS_DELETE=FALSE AND IS_PROCESSED=TRUE ");
		bufferString
				.append("AND (ACTION_TYPE IS NULL OR ACTION_TYPE <>'CAN') ");
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append(" AND TABLE_NUMBER='5A' ");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("bufferString--------------------------> {} ",
					bufferString);
		}

		return bufferString;
	}

	private StringBuilder createQueryStringForTable4(List<String> gstinList,
			String taxPeriod, List<String> divisionList, List<String> pcList,
			List<String> pc2List,List<String> plantList) {
		StringBuilder bufferString = new StringBuilder();
		String condition = conditionsString(gstinList, taxPeriod, divisionList, pcList, pc2List, plantList).toString();

		bufferString.append(
				"SELECT 'Table 4 - M2JW' AS GOODS_TYPE, COUNT (DISTINCT DOC_KEY )AS COUNT_KEY, SUM(IFNULL(ITM.ITM_QTY,0)) ");
		bufferString.append(
				"AS ITM_QTY, SUM(IFNULL(ITM.LOSSES_QTY,0)) AS LOSSES_QTY, SUM(IFNULL(ITM.TAXABLE_VALUE,0)) ");
		bufferString.append(
				"AS TAXABLE_VALUE, SUM(IFNULL(ITM.IGST_AMT,0)) AS IGST_AMT, SUM(IFNULL(ITM.CGST_AMT,0)) AS CGST_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.SGST_AMT,0))AS SGST_AMT, SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0)) + ");
		bufferString.append(
				"SUM(IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS CESS_AMT, SUM(IFNULL(ITM.STATE_CESS_AMT_SPECIFIC,0))+ ");
		bufferString.append(
				"SUM(IFNULL(ITM.STATE_CESS_AMT_ADVALOREM,0)) AS STATECESS_AMT, SUM(IFNULL(ITM.TOTAL_VALUE,0)) ");
		bufferString.append("AS TOTAL_VALUE FROM ITC04_HEADER HDR ");
		bufferString.append(
				"INNER JOIN ITC04_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD ");
		bufferString.append(
				"WHERE IS_DELETE=FALSE AND IS_PROCESSED=TRUE AND (ACTION_TYPE IS NULL OR ACTION_TYPE <>'CAN') ");
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append("AND TABLE_NUMBER='4' ");
		bufferString.append(
				"UNION ALL SELECT GOODS_TYPE, COUNT (DISTINCT DOC_KEY )AS COUNT_KEY, SUM(IFNULL(ITM.ITM_QTY,0)) ");
		bufferString.append(
				"AS ITM_QTY, SUM(IFNULL(ITM.LOSSES_QTY,0)) AS LOSSES_QTY, SUM(IFNULL(ITM.TAXABLE_VALUE,0)) AS ");
		bufferString.append(
				"TAXABLE_VALUE, SUM(IFNULL(ITM.IGST_AMT,0)) AS IGST_AMT, SUM(IFNULL(ITM.CGST_AMT,0)) AS CGST_AMT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.SGST_AMT,0))AS SGST_AMT, SUM(IFNULL(ITM.CESS_AMT_SPECIFIC,0)) + ");
		bufferString.append(
				"SUM(IFNULL(ITM.CESS_AMT_ADVALOREM,0)) AS CESS_AMT, SUM(IFNULL(ITM.STATE_CESS_AMT_SPECIFIC,0))+ ");
		bufferString.append(
				"SUM(IFNULL(ITM.STATE_CESS_AMT_ADVALOREM,0)) AS STATECESS_AMT, SUM(IFNULL(ITM.TOTAL_VALUE,0)) ");
		bufferString.append("AS TOTAL_VALUE FROM ITC04_HEADER HDR INNER JOIN ");
		bufferString.append(
				"ITC04_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID AND HDR.RETURN_PERIOD=ITM.RETURN_PERIOD ");
		bufferString.append(
				"WHERE IS_DELETE=FALSE AND IS_PROCESSED=TRUE AND (ACTION_TYPE IS NULL OR ACTION_TYPE <>'CAN') ");

		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append(" AND TABLE_NUMBER='4' GROUP BY GOODS_TYPE");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("bufferString--------------------------> {} ",
					bufferString);
		}

		return bufferString;
	}

	private List<ITC04PopupRespDto> queryAndbuildTableData(String taxPeriod,
			List<String> gstinList, List<String> divisionList, List<String> pcList,
			List<String> pc2List,List<String> plantList, StringBuilder queryStr) {
		List<ITC04PopupRespDto> respDtos = Lists.newArrayList();
		Query popupQuery = entityManager.createNativeQuery(queryStr.toString());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("popupQuery --> {}", popupQuery);
		}

		if (StringUtils.isNotBlank(taxPeriod)) {
			popupQuery.setParameter("taxPeriod", taxPeriod);
		}

		if (CollectionUtils.isNotEmpty(gstinList)) {
			popupQuery.setParameter("gstinList", gstinList);
		}
		
		if (CollectionUtils.isNotEmpty(divisionList)) {
			popupQuery.setParameter("divisionList", divisionList);
		}
		
		if (CollectionUtils.isNotEmpty(pcList)) {
			popupQuery.setParameter("pcList", pcList);
		}
		
		if (CollectionUtils.isNotEmpty(pc2List)) {
			popupQuery.setParameter("pc2List", pc2List);
		}
		
		if (CollectionUtils.isNotEmpty(plantList)) {
			popupQuery.setParameter("plantList", plantList);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> list = popupQuery.getResultList();
		respDtos.addAll(convert(list));
		return respDtos;
	}

	private StringBuilder conditionsString(List<String> gstinList,
			String taxPeriod, List<String> divisionList, List<String> pcList,
			List<String> pc2List,List<String> plantList) {
		StringBuilder conditionalBuilder = new StringBuilder();

		if (StringUtils.isNotBlank(taxPeriod)) {
			conditionalBuilder
					.append(" AND HDR.QRETURN_PERIOD IN (:taxPeriod) ");
		}

		if (CollectionUtils.isNotEmpty(gstinList)) {
			conditionalBuilder.append(" AND SUPPLIER_GSTIN IN (:gstinList) ");
		}
		
		if (CollectionUtils.isNotEmpty(divisionList)) {
			conditionalBuilder.append(" AND DIVISION IN (:divisionList) ");
		}
		
		if (CollectionUtils.isNotEmpty(pcList)) {
			conditionalBuilder.append(" AND PROFIT_CENTRE1 IN (:pcList) ");
		}
		
		if (CollectionUtils.isNotEmpty(pc2List)) {
			conditionalBuilder.append(" AND PROFIT_CENTRE2 IN (:pc2List) ");
		}
		
		if (CollectionUtils.isNotEmpty(plantList)) {
			conditionalBuilder.append(" AND PLANT_CODE IN (:plantList) ");
		}

		String condition = conditionalBuilder.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"condition for applying filter--------------------------> {}",
					condition);
		}
		return conditionalBuilder;
	}

	private Collection<? extends ITC04PopupRespDto> convert(
			List<Object[]> list) {
		List<ITC04PopupRespDto> respDtos = Lists.newArrayList();
		list.forEach(obj -> {
			ITC04PopupRespDto dto = new ITC04PopupRespDto();
			dto.setType((String) obj[0]);
			dto.setCount(obj[1] != null ? (GenUtil.getBigInteger(obj[1])).intValue() : 0);
			dto.setQuantity(
					obj[2] != null ? (BigDecimal) obj[2] : BigDecimal.ZERO);
			dto.setLossesQuantity(
					obj[3] != null ? (BigDecimal) obj[3] : BigDecimal.ZERO);
			dto.setTaxableValue(
					obj[4] != null ? (BigDecimal) obj[4] : BigDecimal.ZERO);
			dto.setIgst(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
			// dto.setCgst((BigDecimal) obj[9]);

			dto.setCgst(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);

			dto.setSgst(obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO);
			dto.setCess(obj[8] != null ? (BigDecimal) obj[8] : BigDecimal.ZERO);
			dto.setStateCess(
					obj[9] != null ? (BigDecimal) obj[9] : BigDecimal.ZERO);
			dto.setTotalValue(
					obj[10] != null ? (BigDecimal) obj[10] : BigDecimal.ZERO);

			respDtos.add(dto);
		});

		return respDtos;
	}

}
