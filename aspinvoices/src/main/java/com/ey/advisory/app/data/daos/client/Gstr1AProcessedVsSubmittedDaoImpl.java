/**
 * 
 */
package com.ey.advisory.app.data.daos.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.ProcessedVsSubmittedResponseDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ProcessedVsSubmittedRequestDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1AProcessedVsSubmittedDaoImpl")
@Slf4j
public class Gstr1AProcessedVsSubmittedDaoImpl implements Gstr1AProcessedVsSubmittedDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private ProcessedVsSubmittedRecordsCommonUtil procVsSubCommonUtil;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Override
	public List<ProcessedVsSubmittedResponseDto> processedVsSubdRecords(
			ProcessedVsSubmittedRequestDto procesVsSubReqDto) {

		List<Long> entityId = procesVsSubReqDto.getEntityId();
		String taxPeriodFrom = procesVsSubReqDto.getTaxPeriodFrom();
		String taxPeriodTo = procesVsSubReqDto.getTaxPeriodTo();
		List<String> tableType = procesVsSubReqDto.getTableType();
		List<String> tableTypeUpperCase = tableType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

		Map<String, List<String>> dataSecAttrs = procesVsSubReqDto
				.getDataSecAttrs();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ProcessedVsSubmittedDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
					procesVsSubReqDto);
		}
		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null, sgstin = null;

		List<String> gstinList = null;
		List<String> pcList = null;
		List<String> plantList = null;
		List<String> divisionList = null;
		List<String> locationList = null;
		List<String> salesList = null;
		List<String> distList = null;
		List<String> ud1List = null;
		List<String> ud2List = null;
		List<String> ud3List = null;
		List<String> ud4List = null;
		List<String> ud5List = null;
		List<String> ud6List = null;

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
					profitCenter = key;
					pcList = dataSecAttrs.get(OnboardingConstant.PC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.PLANT)) {
					plant = key;
					plantList = dataSecAttrs.get(OnboardingConstant.PLANT);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DIVISION)) {
					division = key;
					divisionList = dataSecAttrs
							.get(OnboardingConstant.DIVISION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.LOCATION)) {
					location = key;
					locationList = dataSecAttrs
							.get(OnboardingConstant.LOCATION);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.SO)) {
					sales = key;
					salesList = dataSecAttrs.get(OnboardingConstant.SO);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.DC)) {
					distChannel = key;
					distList = dataSecAttrs.get(OnboardingConstant.DC);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD1)) {
					ud1 = key;
					ud1List = dataSecAttrs.get(OnboardingConstant.UD1);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD2)) {
					ud2 = key;
					ud2List = dataSecAttrs.get(OnboardingConstant.UD2);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD3)) {
					ud3 = key;
					ud3List = dataSecAttrs.get(OnboardingConstant.UD3);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD4)) {
					ud4 = key;
					ud4List = dataSecAttrs.get(OnboardingConstant.UD4);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD5)) {
					ud5 = key;
					ud5List = dataSecAttrs.get(OnboardingConstant.UD5);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.UD6)) {
					ud6 = key;
					ud6List = dataSecAttrs.get(OnboardingConstant.UD6);
				}
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}
		int derivedTaxPeriodFrom = 0;
		if (!Strings.isNullOrEmpty(taxPeriodFrom)) {
			derivedTaxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
		}
		int derivedTaxPeriodTo = 0;
		if (!Strings.isNullOrEmpty(taxPeriodTo)) {
			derivedTaxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
		}
		StringBuilder queryStr = createQueryString(entityId, gstinList,
				derivedTaxPeriodFrom, derivedTaxPeriodTo, dataSecAttrs,
				profitCenter, sgstin, plant, division, location, sales,
				distChannel, ud1, ud2, ud3, ud4, ud5, ud6, pcList, plantList,
				salesList, divisionList, locationList, distList, ud1List,
				ud2List, ud3List, ud4List, ud5List, ud6List,
				tableTypeUpperCase);

		LOGGER.debug("outQueryStr-->" + queryStr);

		List<ProcessedVsSubmittedResponseDto> finalDtoList = new ArrayList<>();
		try {

			Query Q = entityManager.createNativeQuery(queryStr.toString());

			if (derivedTaxPeriodFrom != 0 && derivedTaxPeriodTo != 0) {
				Q.setParameter("derivedTaxPeriodFrom", derivedTaxPeriodFrom);
				Q.setParameter("derivedTaxPeriodTo", derivedTaxPeriodTo);
			}
			if (gstinList != null && gstinList.size() > 0
					&& !gstinList.contains("")) {
				Q.setParameter("sgstin", gstinList);
			}
			if (!Strings.isNullOrEmpty(profitCenter) && !profitCenter.isEmpty()
					&& pcList != null && pcList.size() > 0) {
				Q.setParameter("pcList", pcList);
			}
			if (!Strings.isNullOrEmpty(plant) && plantList != null
					&& plantList.size() > 0) {
				Q.setParameter("plantList", plantList);
			}
			if (!Strings.isNullOrEmpty(sales) && salesList != null
					&& salesList.size() > 0) {
				Q.setParameter("salesList", salesList);
			}
			if (!Strings.isNullOrEmpty(division) && divisionList != null
					&& divisionList.size() > 0) {
				Q.setParameter("divisionList", divisionList);
			}
			if (!Strings.isNullOrEmpty(location) && locationList != null
					&& locationList.size() > 0) {
				Q.setParameter("locationList", locationList);
			}
			if (!Strings.isNullOrEmpty(distChannel) && distList != null
					&& distList.size() > 0) {
				Q.setParameter("distList", distList);
			}
			if (!Strings.isNullOrEmpty(ud1) && ud1List != null
					&& ud1List.size() > 0) {
				Q.setParameter("ud1List", ud1List);
			}
			if (!Strings.isNullOrEmpty(ud2) && ud2List != null
					&& ud2List.size() > 0) {
				Q.setParameter("ud2List", ud2List);
			}
			if (!Strings.isNullOrEmpty(ud3) && ud3List != null
					&& ud3List.size() > 0) {
				Q.setParameter("ud3List", ud3List);
			}
			if (!Strings.isNullOrEmpty(ud4) && ud4List != null
					&& ud4List.size() > 0) {
				Q.setParameter("ud4List", ud4List);
			}
			if (!Strings.isNullOrEmpty(ud5) && ud5List != null
					&& ud5List.size() > 0) {
				Q.setParameter("ud5List", ud5List);
			}
			if (!Strings.isNullOrEmpty(ud6) && ud6List != null
					&& ud6List.size() > 0) {
				Q.setParameter("ud6List", ud6List);
			}

			List<Object[]> Qlist = Q.getResultList();

			LOGGER.error("bufferString-------------------------->" + Qlist);

			LOGGER.debug("Outward data list from database is-->" + Qlist);

			List<ProcessedVsSubmittedResponseDto> outwardFinalList = procVsSubCommonUtil
					.convertGstr1RecordsIntoObject(Qlist, procesVsSubReqDto,
							gstinList);

			Map<String, List<ProcessedVsSubmittedResponseDto>> combinedDataMap = new HashMap<String, List<ProcessedVsSubmittedResponseDto>>();

			procVsSubCommonUtil.createMapByGstinBasedOnType(combinedDataMap,
					outwardFinalList);

			procVsSubCommonUtil.fillTheDataFromDataSecAttr(procesVsSubReqDto,outwardFinalList,
					gstinList, derivedTaxPeriodFrom, derivedTaxPeriodTo);
			List<ProcessedVsSubmittedResponseDto> gstinDtoList = new ArrayList<ProcessedVsSubmittedResponseDto>();
			List<String> combinedGstinList = new ArrayList<>();
			if (sgstin != null && !sgstin.isEmpty() && gstinList != null
					&& gstinList.size() > 0) {
				combinedGstinList.addAll(gstinList);
			}
			if (!combinedGstinList.isEmpty() && combinedGstinList.size() > 0) {
				for (ProcessedVsSubmittedResponseDto processedDto : outwardFinalList) {
					if (combinedGstinList.contains(processedDto.getGstin())) {
						gstinDtoList.add(processedDto);
					}
				}
				List<ProcessedVsSubmittedResponseDto> sortedGstinDtoList = gstinDtoList
						.stream()
						.sorted(Comparator.comparing(
								ProcessedVsSubmittedResponseDto::getGstin))
						.collect(Collectors.toList());
				return ProcessedVsSubmittedRecordsCommonUtil
						.convertCalcuDataToResp(sortedGstinDtoList);
			}

			List<ProcessedVsSubmittedResponseDto> sortedGstinDtoList = outwardFinalList
					.stream()
					.sorted(Comparator.comparing(
							ProcessedVsSubmittedResponseDto::getGstin))
					.collect(Collectors.toList());
			finalDtoList.addAll(sortedGstinDtoList);
			LOGGER.debug("Final list from dao is ->" + finalDtoList);
		} catch (Exception e) {
			throw new AppException("Error in data process ->", e);
		}

		return ProcessedVsSubmittedRecordsCommonUtil
				.convertCalcuDataToResp(finalDtoList);
	}

	public StringBuilder createQueryString(List<Long> entityId,
			List<String> gstinList, int derivedTaxPeriodFrom,
			int derivedTaxPeriodTo, Map<String, List<String>> dataSecAttrs,
			String profitCenter, String sgstin, String plant, String division,
			String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> distList, List<String> ud1List, List<String> ud2List,
			List<String> ud3List, List<String> ud4List, List<String> ud5List,
			List<String> ud6List, List<String> tableTypeUpperCase) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("PC")) {
					profitCenter = key;
				}

				if (key.equalsIgnoreCase("Plant")) {
					plant = key;
				}
				if (key.equalsIgnoreCase("D")) {
					division = key;
				}
				if (key.equalsIgnoreCase("L")) {
					location = key;
				}
				if (key.equalsIgnoreCase("SO")) {
					sales = key;
				}
				if (key.equalsIgnoreCase("DC")) {
					distChannel = key;
				}
				if (key.equalsIgnoreCase("UD1")) {
					ud1 = key;
				}
				if (key.equalsIgnoreCase("UD2")) {
					ud2 = key;
				}
				if (key.equalsIgnoreCase("UD3")) {
					ud3 = key;
				}
				if (key.equalsIgnoreCase("UD4")) {
					ud4 = key;
				}
				if (key.equalsIgnoreCase("UD5")) {
					ud5 = key;
				}
				if (key.equalsIgnoreCase("UD6")) {
					ud6 = key;
				}
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}
		StringBuilder queryBuilder = new StringBuilder();

		if (derivedTaxPeriodFrom != 0 && derivedTaxPeriodTo != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD BETWEEN "
					+ ":derivedTaxPeriodFrom AND :derivedTaxPeriodTo");
		}
		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND SUPPLIER_GSTIN IN :sgstin");
		}
		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& pcList.size() > 0) {
			queryBuilder.append(" AND PROFIT_CENTRE IN :pcList");
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& plantList.size() > 0) {
			queryBuilder.append(" AND PLANT_CODE IN :plantList");
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& salesList.size() > 0) {
			queryBuilder.append(" AND SALES_ORGANIZATION IN :salesList");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& distList.size() > 0) {
			queryBuilder.append(" AND DISTRIBUTION_CHANNEL IN :distList");
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& divisionList.size() > 0) {
			queryBuilder.append(" AND DIVISION IN :divisionList");
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& locationList.size() > 0) {
			queryBuilder.append(" AND LOCATION IN :locationList");
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& ud1List.size() > 0) {
			queryBuilder.append(" AND USERACCESS1 IN :ud1List");
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& ud2List.size() > 0) {
			queryBuilder.append(" AND USERACCESS2 IN :ud2List");
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& ud3List.size() > 0) {
			queryBuilder.append(" AND USERACCESS3 IN :ud3List");
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& ud4List.size() > 0) {
			queryBuilder.append(" AND USERACCESS4 IN :ud4List");
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& ud5List.size() > 0) {
			queryBuilder.append(" AND USERACCESS5 IN :ud5List");
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& ud6List.size() > 0) {
			queryBuilder.append(" AND USERACCESS6 IN :ud6List");
		}

		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();

		bufferString.append("SELECT SUPPLIER_GSTIN, "
				+ "SUM(TOTAL_COUNT_IN_ASP)ASP_COUNT, SUM(ASP_TAXABLE_VALUE)"
				+ "ASP_TAXABLE_VALUE, SUM(ASP_TOTAL_TAX)ASP_TOTAL_TAX, "
				+ "SUM(GSTN_NOT_SENT_COUNT)GSTN_NOT_SENT_COUNT, "
				+ "SUM(GSTN_SAVED_COUNT)GSTN_SAVED_COUNT, "
				+ "SUM(GSTN_NOT_SAVED_COUNT)GSTN_NOT_SAVED_COUNT, "
				+ "SUM(GSTN_ERROR_COUNT)GSTN_ERROR_COUNT, "
				+ "SUM(TOTAL_COUNT_IN_ASP)TOTAL_COUNT_IN_ASP, "
				+ "MAX(LAST_UPDATED)LAST_UPDATED , SUM(GSTN_COUNT)GSTN_COUNT, "
				+ "SUM(GSTN_TAXABLE_VALUE)GSTN_TAXABLE_VALUE, "
				+ "SUM(GSTN_TOTAL_TAX)GSTN_TOTAL_TAX FROM( SELECT "
				+ "SUPPLIER_GSTIN,RETURN_PERIOD, 0 ASP_COUNT,TAX_DOC_TYPE,"
				+ "ASP_TAXABLE_VALUE,ASP_TOTAL_TAX, GSTN_NOT_SENT_COUNT,"
				+ "GSTN_SAVED_COUNT, GSTN_NOT_SAVED_COUNT,GSTN_ERROR_COUNT,"
				+ "TOTAL_COUNT_IN_ASP,LAST_UPDATED, GSTN_COUNT,"
				+ "GSTN_TAXABLE_VALUE,GSTN_TOTAL_TAX FROM ( SELECT "
				+ "SUPPLIER_GSTIN,RETURN_PERIOD, TAX_DOC_TYPE, "
				+ "IFNULL(SUM(ASP_TAXABLE_VALUE),0) AS ASP_TAXABLE_VALUE, "
				+ "IFNULL(SUM(ASP_TOTAL_TAX),0) AS ASP_TOTAL_TAX, "
				+ "IFNULL(SUM(GSTN_NOT_SENT_COUNT),0) AS GSTN_NOT_SENT_COUNT, "
				+ "IFNULL(SUM(GSTN_SAVED_COUNT),0) AS GSTN_SAVED_COUNT, "
				+ "IFNULL(SUM(GSTN_NOT_SAVED_COUNT),0) AS GSTN_NOT_SAVED_COUNT, "
				+ "IFNULL(SUM(GSTN_ERROR_COUNT),0) AS GSTN_ERROR_COUNT, "
				+ "IFNULL(SUM(TOTAL_COUNT_IN_ASP),0) AS TOTAL_COUNT_IN_ASP,"
				+ " LAST_UPDATED, IFNULL(SUM(GSTN_COUNT),0) AS GSTN_COUNT, "
				+ "IFNULL(SUM(GSTN_TAXABLE_VALUE),0) AS GSTN_TAXABLE_VALUE, "
				+ "IFNULL(SUM(GSTN_TOTAL_TAX),0) AS GSTN_TOTAL_TAX FROM "
				+ "GSTR1A_SUBMITTED_PS WHERE IS_DELETE=FALSE AND TAX_DOC_TYPE "
				+ "NOT IN ('HSN','DOC_ISSUED') AND "
				+ "REPORT_TYPE IS NOT NULL  "
				+ condition
				+ " GROUP BY SUPPLIER_GSTIN,RETURN_PERIOD,TAX_DOC_TYPE,LAST_UPDATED) "
				+ " UNION ALL SELECT SUPPLIER_GSTIN,RETURN_PERIOD, ASP_COUNT, "
				+ "TAX_DOC_TYPE,0 ASP_TAXABLE_VALUE,0 ASP_TOTAL_TAX, "
				+ "0 GSTN_NOT_SENT_COUNT,0 GSTN_SAVED_COUNT, "
				+ "0 GSTN_NOT_SAVED_COUNT,0 GSTN_ERROR_COUNT,"
				+ "0 TOTAL_COUNT_IN_ASP,'' LAST_UPDATED, 0 GSTN_COUNT,"
				+ "0 GSTN_TAXABLE_VALUE,0 GSTN_TOTAL_TAX FROM (SELECT "
				+ "SUPPLIER_GSTIN,RETURN_PERIOD, IFNULL(SUM(ASP_COUNT),0) "
				+ "AS ASP_COUNT, TAX_DOC_TYPE FROM GSTR1A_SUBMITTED_PS"
				+ " WHERE IS_DELETE=FALSE AND TAX_DOC_TYPE "
				+ "NOT IN ('HSN','DOC_ISSUED') AND "
				+ "REPORT_TYPE IS NOT NULL  "
				+ condition
				+ " GROUP BY SUPPLIER_GSTIN,"
				+ "RETURN_PERIOD,TAX_DOC_TYPE,LAST_UPDATED)) GROUP BY "
				+ "SUPPLIER_GSTIN ");

		return bufferString;
	}

}
