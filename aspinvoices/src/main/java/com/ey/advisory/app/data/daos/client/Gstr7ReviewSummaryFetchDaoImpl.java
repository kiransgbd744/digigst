package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr7ReviewSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Gstr7ReviewSummaryFetchDaoImpl")
public class Gstr7ReviewSummaryFetchDaoImpl {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr7ReviewSummaryFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	
//commeented data security filters because of SAIL issue.
	public List<Gstr7ReviewSummaryRespDto> loadGstr7ReviewSummary(
			Gstr2AProcessedRecordsReqDto reqDto) {

		List<Long> entityId = reqDto.getEntityId();
		String taxPeriod = reqDto.getRetunPeriod();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		
		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null, sgstin = null;
		String cgstin = null;

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
		
        String returnType = reqDto.getReturnType();
		
		if (returnType == null) {
			returnType = "GSTR7";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ReturnType -->" + returnType);
		}
		
		StringBuilder queryStr = new StringBuilder();

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				/*if (key.equalsIgnoreCase(OnboardingConstant.PC)) {
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
				}*/
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}

		int taxPeriod1 = 0;
		if (taxPeriod != null) {
			taxPeriod1 = GenUtil.convertTaxPeriodToInt(taxPeriod);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GstinList -->" + gstinList);
			LOGGER.debug("TaxPeriod -->" + taxPeriod);
			LOGGER.debug("TaxPeriod1 -->" + taxPeriod1);
		}

		if("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)){
			queryStr = createGSTR7TransQueryString(entityId, gstinList,
					taxPeriod1, taxPeriod, dataSecAttrs, profitCenter, sgstin,
					cgstin, plant, division, location, sales, distChannel, ud1, ud2,
					ud3, ud4, ud5, ud6, pcList, plantList, salesList, divisionList,
					locationList, distList, ud1List, ud2List, ud3List, ud4List,
					ud5List, ud6List);
		}else{
			queryStr = createQueryString(entityId, gstinList,
					taxPeriod1, taxPeriod, dataSecAttrs, profitCenter, sgstin,
					cgstin, plant, division, location, sales, distChannel, ud1, ud2,
					ud3, ud4, ud5, ud6, pcList, plantList, salesList, divisionList,
					locationList, distList, ud1List, ud2List, ud3List, ud4List,
					ud5List, ud6List);
		}

		LOGGER.debug("outQueryStr-->" + queryStr);

		List<Gstr7ReviewSummaryRespDto> finalDtoList = new ArrayList<>();
		try {

			Query Q = entityManager.createNativeQuery(queryStr.toString());
			
            if("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)){
				
				if (gstinList != null && gstinList.size() > 0
						&& !gstinList.contains("")) {
					Q.setParameter("sgstin", gstinList);
				}
				
				if (taxPeriod1 != 0) {
					Q.setParameter("taxPeriod", taxPeriod1);
				}
				
			} else {
				if (taxPeriod1 != 0) {
					Q.setParameter("taxPeriod", taxPeriod1);
				}
				if (gstinList != null && gstinList.size() > 0
						&& !gstinList.contains("")) {
					Q.setParameter("sgstin", gstinList);
				}
			}
			/*if (profitCenter != null && !profitCenter.isEmpty()
					&& !profitCenter.isEmpty() && pcList != null
					&& pcList.size() > 0) {
				Q.setParameter("pcList", pcList);
			}
			if (plant != null && !plant.isEmpty() && !plant.isEmpty()
					&& plantList != null && plantList.size() > 0) {
				Q.setParameter("plantList", plantList);
			}
			if (sales != null && !sales.isEmpty() && salesList != null
					&& salesList.size() > 0) {
				Q.setParameter("salesList", salesList);
			}
			if (division != null && !division.isEmpty() && divisionList != null
					&& divisionList.size() > 0) {
				Q.setParameter("divisionList", divisionList);
			}
			if (location != null && !location.isEmpty() && locationList != null
					&& locationList.size() > 0) {
				Q.setParameter("locationList", locationList);
			}
			if (distChannel != null && !distChannel.isEmpty()
					&& distList != null && distList.size() > 0) {
				Q.setParameter("distList", distList);
			}
			if (ud1 != null && !ud1.isEmpty() && ud1List != null
					&& ud1List.size() > 0) {
				Q.setParameter("ud1List", ud1List);
			}
			if (ud2 != null && !ud2.isEmpty() && ud2List != null
					&& ud2List.size() > 0) {
				Q.setParameter("ud2List", ud2List);
			}
			if (ud3 != null && !ud3.isEmpty() && ud3List != null
					&& ud3List.size() > 0) {
				Q.setParameter("ud3List", ud3List);
			}
			if (ud4 != null && !ud4.isEmpty() && ud4List != null
					&& ud4List.size() > 0) {
				Q.setParameter("ud4List", ud4List);
			}
			if (ud5 != null && !ud5.isEmpty() && ud5List != null
					&& ud5List.size() > 0) {
				Q.setParameter("ud5List", ud5List);
			}
			if (ud6 != null && !ud6.isEmpty() && ud6List != null
					&& ud6List.size() > 0) {
				Q.setParameter("ud6List", ud6List);
			}
*/
			@SuppressWarnings("unchecked")
			List<Object[]> Qlist = Q.getResultList();
			finalDtoList = convertGstr1RecordsIntoObject(Qlist);

		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}
		return finalDtoList;
	}

	private List<Gstr7ReviewSummaryRespDto> convertGstr1RecordsIntoObject(
			List<Object[]> savedDataList) {
		List<Gstr7ReviewSummaryRespDto> summaryList = new ArrayList<Gstr7ReviewSummaryRespDto>();
		if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
			for (Object[] data : savedDataList) {
				Gstr7ReviewSummaryRespDto dto = new Gstr7ReviewSummaryRespDto();
				dto.setSection(String.valueOf(data[0]));
				Integer count = (GenUtil.getBigInteger(data[1])).intValue();
				dto.setAspCount(count);
				dto.setAspTotalAmount((BigDecimal) data[2]);
				dto.setAspIgst((BigDecimal) data[3]);
				dto.setAspCgst((BigDecimal) data[4]);
				dto.setAspSgst((BigDecimal) data[5]);
				dto.setGstnCount((Integer) data[6]);
				dto.setGstnTotalAmount((BigDecimal) data[7]);
				dto.setGstnIgst((BigDecimal) data[8]);
				dto.setGstnCgst((BigDecimal) data[9]);
				dto.setGstnSgst((BigDecimal) data[10]);

				summaryList.add(dto);
			}
		}
		return summaryList;

	}

	public StringBuilder createQueryString(List<Long> entityId,
			List<String> gstinList, int taxPeriod, String taxPeriod1,
			Map<String, List<String>> dataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> distList, List<String> ud1List, List<String> ud2List,
			List<String> ud3List, List<String> ud4List, List<String> ud5List,
			List<String> ud6List) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

			/*	if (key.equalsIgnoreCase("PC")) {
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
				}*/
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilder1 = new StringBuilder();

		StringBuilder queryBuilder = new StringBuilder();
		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod)");
			queryBuilder1.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod)");
		}

		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND TDS_DEDUCTOR_GSTIN IN :sgstin");
			queryBuilder1.append(" AND GSTIN IN :sgstin");
		}

		/*if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& pcList.size() > 0) {
			queryBuilder.append(" AND PROFIT_CENTRE1 IN :pcList");
			queryBuilder1.append(" AND PROFIT_CENTRE1 IN :pcList");
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& plantList.size() > 0) {
			queryBuilder.append(" AND PLANT_CODE IN :plantList");
			queryBuilder1.append(" AND PLANT_CODE IN :plantList");
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& salesList.size() > 0) {
			queryBuilder.append(" AND SALES_ORGANIZATION IN :salesList");
			queryBuilder1.append(" AND SALES_ORGANIZATION IN :salesList");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& distList.size() > 0) {
			queryBuilder.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			queryBuilder1.append(" AND DISTRIBUTION_CHANNEL IN :distList");
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& divisionList.size() > 0) {
			queryBuilder.append(" AND DIVISION IN :divisionList");
			queryBuilder1.append(" AND DIVISION IN :divisionList");
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& locationList.size() > 0) {
			queryBuilder.append(" AND LOCATION IN :locationList");
			queryBuilder1.append(" AND LOCATION IN :locationList");
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
		}*/

		String condition = queryBuilder.toString();
		String condition1 = queryBuilder1.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString.append(" SELECT CATEGORY, SUM (ASP_COUNT)ASP_COUNT, "
				+ "SUM(ASP_TOTAL_AMT)ASP_TOTAL_AMT,SUM(ASP_IGST_AMT)"
				+ "ASP_IGST_AMT,SUM(ASP_CGST_AMT)ASP_CGST_AMT,"
				+ "SUM(ASP_SGST_AMT)ASP_SGST_AMT, SUM(GSTN_COUNT)GSTN_COUNT,"
				+ "SUM(GSTN_TOTAL_AMT)GSTN_TOTAL_AMT,SUM(GSTN_IGST_AMT)"
				+ "GSTN_IGST_AMT,SUM(GSTN_CGST_AMT)GSTN_CGST_AMT,"
				+ "SUM(GSTN_SGST_AMT)GSTN_SGST_AMT FROM ( SELECT CATEGORY,"
				+ " ASP_COUNT,ASP_TOTAL_AMT,ASP_IGST_AMT,ASP_CGST_AMT,"
				+ "ASP_SGST_AMT, 0 GSTN_COUNT,0 GSTN_TOTAL_AMT,"
				+ "0 GSTN_IGST_AMT,0 GSTN_CGST_AMT,0 GSTN_SGST_AMT FROM"
				+ " ( SELECT CATEGORY,SUM(COUNT) ASP_COUNT,SUM(TOTAL_AMT)"
				+ "ASP_TOTAL_AMT,SUM(IGST_AMT) ASP_IGST_AMT,SUM(CGST_AMT)"
				+ " ASP_CGST_AMT, SUM(SGST_AMT) ASP_SGST_AMT FROM( SELECT "
				+ "CATEGORY,COUNT(DISTINCT KEY) COUNT, SUM(TOTAL_AMT)"
				+ "TOTAL_AMT,SUM(IGST_AMT) IGST_AMT,SUM(CGST_AMT) CGST_AMT,"
				+ " SUM(SGST_AMT) SGST_AMT FROM ( SELECT TABLE_NUM AS "
				+ "CATEGORY, (TDS_DEDUCTOR_GSTIN||'-'||RETURN_PERIOD||'-'||"
				+ "NEW_TDS_DEDUCTEE_GSTIN) AS KEY, IFNULL(SUM(NEW_GROSS_AMT),0)"
				+ " AS TOTAL_AMT, IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
				+ " IFNULL(SUM(CGST_AMT),0) AS CGST_AMT, IFNULL(SUM(SGST_AMT),"
				+ "0) AS SGST_AMT FROM GSTR7_PROCESSED_TDS WHERE "
				+ "IS_DELETE = FALSE AND TABLE_NUM='Table-3' "
				+ "AND ACTION_TYPE IS NULL");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString
				.append(" GROUP BY TABLE_NUM,TDS_DEDUCTOR_GSTIN,RETURN_PERIOD,"
						+ "NEW_TDS_DEDUCTEE_GSTIN) GROUP BY CATEGORY UNION ALL "
						+ "SELECT CATEGORY,COUNT(DISTINCT KEY) COUNT, "
						+ "SUM(TOTAL_AMT)TOTAL_AMT,SUM(IGST_AMT) IGST_AMT,"
						+ "SUM(CGST_AMT) CGST_AMT, SUM(SGST_AMT) SGST_AMT FROM "
						+ "( SELECT TABLE_NUM AS CATEGORY, (TDS_DEDUCTOR_GSTIN||'-'||"
						+ "RETURN_PERIOD||'-'||ORG_RETURN_PERIOD||'-'||"
						+ "NEW_TDS_DEDUCTEE_GSTIN) AS KEY, IFNULL(SUM(NEW_GROSS_AMT),0)"
						+ " AS TOTAL_AMT, IFNULL(SUM(IGST_AMT),0) AS IGST_AMT,"
						+ " IFNULL(SUM(CGST_AMT),0) AS CGST_AMT, IFNULL(SUM(SGST_AMT),0)"
						+ " AS SGST_AMT FROM GSTR7_PROCESSED_TDS WHERE "
						+ "IS_DELETE = FALSE AND TABLE_NUM='Table-4'"
						+ " AND ACTION_TYPE IS NULL ");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString
				.append(" GROUP BY TABLE_NUM,TDS_DEDUCTOR_GSTIN,RETURN_PERIOD,"
						+ "ORG_RETURN_PERIOD,NEW_TDS_DEDUCTEE_GSTIN) GROUP BY "
						+ "CATEGORY) GROUP BY CATEGORY) UNION ALL SELECT CATEGORY, "
						+ "0 ASP_COUNT,0 ASP_TOTAL_AMT,0 ASP_IGST_AMT,0 ASP_CGST_AMT,"
						+ "0 ASP_SGST_AMT, GSTN_COUNT, GSTN_TOTAL_AMT, GSTN_IGST_AMT, "
						+ "GSTN_CGST_AMT, GSTN_SGST_AMT FROM ( SELECT CATEGORY,"
						+ "SUM(RECORD_COUNT) GSTN_COUNT, SUM(TOTAL_AMT)GSTN_TOTAL_AMT,"
						+ "SUM(IGST_AMT) GSTN_IGST_AMT,SUM(CGST_AMT) GSTN_CGST_AMT,"
						+ " SUM(SGST_AMT) GSTN_SGST_AMT FROM ( SELECT CASE WHEN "
						+ "SECTION_NAME='TDS' THEN 'Table-3' WHEN SECTION_NAME='TDSA'"
						+ " THEN 'Table-4' END AS CATEGORY, IFNULL(SUM(TOT_RECORD),0)"
						+ " AS RECORD_COUNT, IFNULL(SUM(TOT_AMT_DED),0) AS TOTAL_AMT,"
						+ " IFNULL(SUM(TOT_IGST),0) AS IGST_AMT, IFNULL(SUM(TOT_CGST),"
						+ "0) AS CGST_AMT, IFNULL(SUM(TOT_SGST),0) AS SGST_AMT FROM  "
						+ "GETGSTR7_SECTIONWISE_SUMMARY_TDS WHERE IS_DELETE = FALSE AND "
						+ "SECTION_NAME IN ('TDS','TDSA') ");
		if (!condition1.equals("")) {
			bufferString.append(condition1);
		}

		bufferString
				.append(" GROUP BY SECTION_NAME) GROUP BY CATEGORY)) GROUP BY CATEGORY "
						+ " ORDER BY CATEGORY ");


		return bufferString;
	}
	
	public StringBuilder createGSTR7TransQueryString(List<Long> entityId,
			List<String> gstinList, int taxPeriod, String taxPeriod1,
			Map<String, List<String>> dataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> distList, List<String> ud1List, List<String> ud2List,
			List<String> ud3List, List<String> ud4List, List<String> ud5List,
			List<String> ud6List) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

			/*	if (key.equalsIgnoreCase("PC")) {
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
				}*/
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilder1 = new StringBuilder();

		StringBuilder queryBuilder = new StringBuilder();
		
		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND GSTIN IN :sgstin ");
			queryBuilder1.append(" AND DEDUCTOR_GSTIN IN :sgstin ");
		}
		
		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod)");
			queryBuilder1.append(" AND DERIVED_RET_PERIOD IN (:taxPeriod)");
		}
		
		

		/*if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& pcList.size() > 0) {
			queryBuilder.append(" AND PROFIT_CENTRE1 IN :pcList");
			queryBuilder1.append(" AND PROFIT_CENTRE1 IN :pcList");
		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& plantList.size() > 0) {
			queryBuilder.append(" AND PLANT_CODE IN :plantList");
			queryBuilder1.append(" AND PLANT_CODE IN :plantList");
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& salesList.size() > 0) {
			queryBuilder.append(" AND SALES_ORGANIZATION IN :salesList");
			queryBuilder1.append(" AND SALES_ORGANIZATION IN :salesList");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& distList.size() > 0) {
			queryBuilder.append(" AND DISTRIBUTION_CHANNEL IN :distList");
			queryBuilder1.append(" AND DISTRIBUTION_CHANNEL IN :distList");
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& divisionList.size() > 0) {
			queryBuilder.append(" AND DIVISION IN :divisionList");
			queryBuilder1.append(" AND DIVISION IN :divisionList");
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& locationList.size() > 0) {
			queryBuilder.append(" AND LOCATION IN :locationList");
			queryBuilder1.append(" AND LOCATION IN :locationList");
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
		}*/

		String condition = queryBuilder.toString();
		String condition1 = queryBuilder1.toString();
		StringBuilder bufferString = new StringBuilder();
		
		
		bufferString.append("SELECT CATEGORY, "
			    + "SUM (ASP_COUNT)    ASP_COUNT, "
			    + "SUM(ASP_TOTAL_AMT) ASP_TOTAL_AMT, "
			    + "SUM(ASP_IGST_AMT)  ASP_IGST_AMT, "
			    + "SUM(ASP_CGST_AMT)  ASP_CGST_AMT, "
			    + "SUM(ASP_SGST_AMT)  ASP_SGST_AMT, "
			    + "SUM(GSTN_COUNT)    GSTN_COUNT, "
			    + "SUM(GSTN_TOTAL_AMT)GSTN_TOTAL_AMT, "
			    + "SUM(GSTN_IGST_AMT) GSTN_IGST_AMT, "
			    + "SUM(GSTN_CGST_AMT) GSTN_CGST_AMT, "
			    + "SUM(GSTN_SGST_AMT) GSTN_SGST_AMT "
			    + "FROM ( "
			    + "SELECT CATEGORY, "
			    + "ASP_COUNT, "
			    + "ASP_TOTAL_AMT, "
			    + "ASP_IGST_AMT, "
			    + "ASP_CGST_AMT, "
			    + "ASP_SGST_AMT, "
			    + "0 GSTN_COUNT, "
			    + "0 GSTN_TOTAL_AMT, "
			    + "0 GSTN_IGST_AMT, "
			    + "0 GSTN_CGST_AMT, "
			    + "0 GSTN_SGST_AMT "
			    + "FROM ( "
			    + "SELECT CATEGORY, "
			    + "SUM(COUNT)    ASP_COUNT, "
			    + "SUM(TOTAL_AMT)ASP_TOTAL_AMT, "
			    + "SUM(IGST_AMT) ASP_IGST_AMT, "
			    + "SUM(CGST_AMT) ASP_CGST_AMT, "
			    + "SUM(SGST_AMT) ASP_SGST_AMT "
			    + "FROM ( "
			    + "SELECT CATEGORY, "
			    + "COUNT(DISTINCT KEY) COUNT, "
			    + "SUM(TOTAL_AMT)      TOTAL_AMT, "
			    + "SUM(IGST_AMT)       IGST_AMT, "
			    + "SUM(CGST_AMT)       CGST_AMT, "
			    + "SUM(SGST_AMT)       SGST_AMT "
			    + "FROM ( "
			    + "SELECT 'Table-3' AS CATEGORY, "
			    + " DOC_KEY AS KEY, "
			    + "IFNULL (SUM(TAXABLE_VALUE),0) AS TOTAL_AMT, "
			    + "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT, "
			    + "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT, "
			    + "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT "
			    + "FROM GSTR7_TRANS_DOC_HEADER TDS "
			    + "WHERE SECTION='TDS' "
			    + "AND SUPPLY_TYPE = 'TAX' "
			    + "AND IS_DELETE = FALSE ");
		if (!condition1.equals("")) {
			bufferString.append(condition1);
		}
		bufferString.append("GROUP BY SECTION, DEDUCTOR_GSTIN, RETURN_PERIOD, DOC_KEY "
			    + ") "
			    + "GROUP BY CATEGORY "
			    + "UNION ALL "
			    + "SELECT CATEGORY, "
			    + "COUNT(DISTINCT KEY) COUNT, "
			    + "SUM(TOTAL_AMT)      TOTAL_AMT, "
			    + "SUM(IGST_AMT)       IGST_AMT, "
			    + "SUM(CGST_AMT)       CGST_AMT, "
			    + "SUM(SGST_AMT)       SGST_AMT "
			    + "FROM ( "
			    + "SELECT 'Table-4' AS CATEGORY, "
			    + " DOC_KEY AS KEY, "
			    + "IFNULL (SUM(TAXABLE_VALUE),0) AS TOTAL_AMT, "
			    + "IFNULL(SUM(IGST_AMT),0) AS IGST_AMT, "
			    + "IFNULL(SUM(CGST_AMT),0) AS CGST_AMT, "
			    + "IFNULL(SUM(SGST_AMT),0) AS SGST_AMT "
			    + "FROM GSTR7_TRANS_DOC_HEADER TDS "
			    + "WHERE SECTION='TDSA' "
			    + "AND SUPPLY_TYPE = 'TAX' "
			    + "AND IS_DELETE = FALSE ");
		if (!condition1.equals("")) {
			  bufferString.append(condition1);
		}
		bufferString.append("GROUP BY SECTION, DEDUCTOR_GSTIN, RETURN_PERIOD, DOC_KEY "
			    + ") "
			    + "GROUP BY CATEGORY "
			    + ") "
			    + "GROUP BY CATEGORY "
			    + ") "
			    + "UNION ALL "
			    + "SELECT CATEGORY, "
			    + "0 ASP_COUNT, "
			    + "0 ASP_TOTAL_AMT, "
			    + "0 ASP_IGST_AMT, "
			    + "0 ASP_CGST_AMT, "
			    + "0 ASP_SGST_AMT, "
			    + "GSTN_COUNT, "
			    + "GSTN_TOTAL_AMT, "
			    + "GSTN_IGST_AMT, "
			    + "GSTN_CGST_AMT, "
			    + "GSTN_SGST_AMT "
			    + "FROM ( "
			    + "SELECT CATEGORY, "
			    + "SUM(RECORD_COUNT) GSTN_COUNT, "
			    + "SUM(TOTAL_AMT)    GSTN_TOTAL_AMT, "
			    + "SUM(IGST_AMT)     GSTN_IGST_AMT, "
			    + "SUM(CGST_AMT)     GSTN_CGST_AMT, "
			    + "SUM(SGST_AMT)     GSTN_SGST_AMT "
			    + "FROM ( "
			    + "SELECT CASE WHEN SECTION_NAME = 'TDS' THEN 'Table-3' "
			    + "WHEN SECTION_NAME = 'TDSA' THEN 'Table-4' "
			    + "END AS CATEGORY, "
			    + "IFNULL(SUM(TOT_RECORD), 0) AS RECORD_COUNT, "
			    + "IFNULL(SUM(TOT_AMT_DED), 0) AS TOTAL_AMT, "
			    + "IFNULL(SUM(TOT_IGST), 0) AS IGST_AMT, "
			    + "IFNULL(SUM(TOT_CGST), 0) AS CGST_AMT, "
			    + "IFNULL(SUM(TOT_SGST), 0) AS SGST_AMT "
			    + "FROM GETGSTR7_SECTIONWISE_SUMMARY_TDS "
			    + "WHERE IS_DELETE = FALSE "
			    + "AND SECTION_NAME IN ('TDS', 'TDSA') ");
		
		if (!condition.equals("")) {
			bufferString.append(condition);
	       }
	    bufferString.append(
			    "GROUP BY SECTION_NAME "
			    + ") "
			    + "GROUP BY CATEGORY "
			    + ") "
			    + ") "
			    + "GROUP BY CATEGORY "
			    + "ORDER BY CATEGORY ");
		
		return bufferString;
	}

	public static void main(String[] args) {
		
		List<Long> entityId = null;
		String taxPeriod1 = "133";
		Map<String, List<String>> dataSecAttrs = null;
		
		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null, sgstin = null;
		String cgstin = null;

		List<String> gstinList = Arrays.asList("GSTN");
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
		String returnType="GSTR7_TRANSACTIONAL";
		
		int taxPeriod = 2024;
		
		
		StringBuilder queryStr= null;
		
		if ("GSTR7_TRANSACTIONAL".equalsIgnoreCase(returnType)) {
			queryStr = new Gstr7ReviewSummaryFetchDaoImpl().createGSTR7TransQueryString(entityId, gstinList,
					taxPeriod, taxPeriod1, dataSecAttrs, profitCenter, sgstin,
					cgstin, plant, division, location, sales, distChannel, ud1, ud2,
					ud3, ud4, ud5, ud6, pcList, plantList, salesList, divisionList,
					locationList, distList, ud1List, ud2List, ud3List, ud4List,
					ud5List, ud6List);
		} else {
			queryStr = new Gstr7ReviewSummaryFetchDaoImpl().createQueryString(entityId, gstinList,
					taxPeriod, taxPeriod1, dataSecAttrs, profitCenter, sgstin,
					cgstin, plant, division, location, sales, distChannel, ud1, ud2,
					ud3, ud4, ud5, ud6, pcList, plantList, salesList, divisionList,
					locationList, distList, ud1List, ud2List, ud3List, ud4List,
					ud5List, ud6List);
		}
		
		System.out.println(queryStr);
	}

}
