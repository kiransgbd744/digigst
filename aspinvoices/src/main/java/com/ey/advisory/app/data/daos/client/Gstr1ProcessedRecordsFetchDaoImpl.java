package com.ey.advisory.app.data.daos.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1ProcessedRecordsFetchDaoImpl")
public class Gstr1ProcessedRecordsFetchDaoImpl
		implements Gstr1ProcessedRecordsFetchDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	private Gstr1ProcessedRecordsCommonUtil gstr1ProcessedRecordsCommonUtil;

	public List<Gstr1ProcessedRecordsRespDto> loadGstr1ProcessedRecords(
			Gstr1ProcessedRecordsReqDto gstr1ProcessedRecordsReqDto) {

		List<Long> entityId = gstr1ProcessedRecordsReqDto.getEntityId();
		String taxPeriod = gstr1ProcessedRecordsReqDto.getRetunPeriod();
		List<String> tableType = gstr1ProcessedRecordsReqDto.getTableType();
		List<String> cdnrTableType = new ArrayList<>();
		for (String table : tableType) {
			if (table.equalsIgnoreCase("CDNUR")) {
				cdnrTableType.add("CDNUR");
				cdnrTableType.add("CDNUR-EXPORTS");
				cdnrTableType.add("CDNUR-B2CL");
			}
		}

		tableType.addAll(cdnrTableType);

		List<String> docType = gstr1ProcessedRecordsReqDto.getDocType();
		LocalDate docFromDate = gstr1ProcessedRecordsReqDto.getDocFromDate();
		LocalDate docToDate = gstr1ProcessedRecordsReqDto.getDocToDate();
		List<String> einvGenerated = gstr1ProcessedRecordsReqDto
				.getEINVGenerated();
		List<String> ewbGenerated = gstr1ProcessedRecordsReqDto
				.getEWBGenerated();

		Map<String, List<String>> dataSecAttrs = gstr1ProcessedRecordsReqDto
				.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1ProcessedRecordsFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
					gstr1ProcessedRecordsReqDto);
		}
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

		int taxPeriod1 = 0;
		if (taxPeriod != null) {
			taxPeriod1 = GenUtil.convertTaxPeriodToInt(taxPeriod);
		}

		List<Object[]> qList = new ArrayList<>();
		List<Gstr1ProcessedRecordsRespDto> finalDtoList = new ArrayList<>();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("DB Query Start-->" + LocalDateTime.now());
			}
			if (gstinList.isEmpty()) {
				return finalDtoList;
			}
			Integer tabStatus = getTabStatus(gstinList, taxPeriod1);

			String queryStr = createQueryString(entityId, gstinList, taxPeriod1,
					taxPeriod, dataSecAttrs, profitCenter, sgstin, cgstin,
					plant, division, location, sales, distChannel, ud1, ud2,
					ud3, ud4, ud5, ud6, pcList, plantList, salesList,
					divisionList, locationList, distList, ud1List, ud2List,
					ud3List, ud4List, ud5List, ud6List, tableType, docType,
					docFromDate, docToDate, einvGenerated, ewbGenerated,
					tabStatus);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("outQueryStr-->" + queryStr);
			}
			Query Q = entityManager.createNativeQuery(queryStr.toString());
			if (taxPeriod1 != 0) {
				Q.setParameter("taxPeriod", taxPeriod1);
			}
			if (gstinList != null && gstinList.size() > 0
					&& !gstinList.contains("")) {
				Q.setParameter("sgstin", gstinList);
			}
			if (tableType != null && tableType.size() > 0
					&& !tableType.contains("")) {
				Q.setParameter("tableType", tableType);
			}
			if (docType != null && docType.size() > 0
					&& !docType.contains("")) {
				Q.setParameter("docType", docType);
			}
			if (docFromDate != null && docToDate != null) {
				Q.setParameter("docFromDate", docFromDate);
				Q.setParameter("docToDate", docToDate);
			}
			if (profitCenter != null && !profitCenter.isEmpty()
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

			@SuppressWarnings("unchecked")
			List<Object[]> resultSet = Q.getResultList();
			qList.addAll(resultSet);

			Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
					.getGstnRegMap();
			List<Gstr1ProcessedRecordsRespDto> outwardFinalList = gstr1ProcessedRecordsCommonUtil
					.convertGstr1RecordsIntoObject(qList,
							gstnRegMap.getValue0(), gstnRegMap.getValue1());

			Map<String, List<Gstr1ProcessedRecordsRespDto>> combinedDataMap = new HashMap<String, List<Gstr1ProcessedRecordsRespDto>>();

			gstr1ProcessedRecordsCommonUtil.createMapByGstinBasedOnType(
					combinedDataMap, outwardFinalList);

			List<Gstr1ProcessedRecordsRespDto> dataDtoList = new ArrayList<>();
			gstr1ProcessedRecordsCommonUtil
					.calculateDataByDocType(combinedDataMap, dataDtoList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("DaTaDtoList List is ->{}" + dataDtoList.size());
			}

			gstr1ProcessedRecordsCommonUtil.fillTheDataFromDataSecAttr(
					dataDtoList, gstinList, taxPeriod, gstnRegMap.getValue0(),
					gstnRegMap.getValue1());
			List<Gstr1ProcessedRecordsRespDto> gstinDtoList = new ArrayList<Gstr1ProcessedRecordsRespDto>();
			List<String> combinedGstinList = new ArrayList<>();
			if (sgstin != null && !sgstin.isEmpty() && gstinList != null
					&& gstinList.size() > 0) {
				combinedGstinList.addAll(gstinList);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("combinedGstinList List is ->{}"
						+ combinedGstinList.size());
			}
			if (!combinedGstinList.isEmpty() && combinedGstinList.size() > 0) {
				for (Gstr1ProcessedRecordsRespDto processedDto : dataDtoList) {
					if (combinedGstinList.contains(processedDto.getGstin())) {
						gstinDtoList.add(processedDto);
					}
				}
				List<Gstr1ProcessedRecordsRespDto> sortedGstinDtoList = gstinDtoList
						.stream()
						.sorted(Comparator.comparing(
								Gstr1ProcessedRecordsRespDto::getGstin))
						.collect(Collectors.toList());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("sortedGstinDtoList List is ->{}"
							+ sortedGstinDtoList.size());
				}
				return gstr1ProcessedRecordsCommonUtil
						.convertCalcuDataToResp(sortedGstinDtoList);
			}

			List<Gstr1ProcessedRecordsRespDto> sortedGstinDtoList = dataDtoList
					.stream()
					.sorted(Comparator
							.comparing(Gstr1ProcessedRecordsRespDto::getGstin))
					.collect(Collectors.toList());
			finalDtoList.addAll(sortedGstinDtoList);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Final list from dao is ->" + finalDtoList);
			}
		} catch (

		Exception e) {
			throw new AppException("Error in data process ->", e);
		}

		return gstr1ProcessedRecordsCommonUtil
				.convertCalcuDataToResp(finalDtoList);

	}

	public String createQueryString(List<Long> entityId, List<String> gstinList,
			int taxPeriod, String taxPeriod1,
			Map<String, List<String>> dataSecAttrs, String profitCenter,
			String sgstin, String cgstin, String plant, String division,
			String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> distList, List<String> ud1List, List<String> ud2List,
			List<String> ud3List, List<String> ud4List, List<String> ud5List,
			List<String> ud6List, List<String> tableType, List<String> docType,
			LocalDate docFromDate, LocalDate docToDate,
			List<String> einvGenerated, List<String> ewbGenerated,
			Integer tabStatus) {

		String einvGen = null;
		if (einvGenerated != null && einvGenerated.size() > 0) {
			einvGen = einvGenerated.get(0);
		}

		String ewbResp = null;
		if (ewbGenerated != null && ewbGenerated.size() > 0) {
			ewbResp = ewbGenerated.get(0);
		}

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
		StringBuilder queryVertical = new StringBuilder();
		StringBuilder hdrQueryBuilder = new StringBuilder();
		StringBuilder queryBuilderGroupBy = new StringBuilder();
		StringBuilder nillQueryBuilder = new StringBuilder();
		StringBuilder userQueryBuilder = new StringBuilder();
		StringBuilder mulQueryBuilder = new StringBuilder();

		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			userQueryBuilder.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			queryVertical.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
			hdrQueryBuilder
					.append(" AND  HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			nillQueryBuilder
					.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod ");
			mulQueryBuilder.append(" DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (docFromDate != null && docToDate != null) {
			queryBuilder.append(
					" AND DOC_DATE BETWEEN :docFromDate AND :docToDate ");
			hdrQueryBuilder.append(
					" AND HDR.DOC_DATE BETWEEN :docFromDate AND :docToDate ");
		}

		if (ewbResp != null && ewbResp.equalsIgnoreCase("YES")) {
			queryBuilder.append(" AND EWB_NO_RESP IS NOT NULL ");
			hdrQueryBuilder.append(" AND HDR.EWB_NO_RESP IS NOT NULL ");
		}
		if (ewbResp != null && ewbResp.equalsIgnoreCase("NO")) {
			queryBuilder.append(" AND EWB_NO_RESP IS NULL ");
			hdrQueryBuilder.append(" AND HDR.EWB_NO_RESP IS NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("YES")) {
			queryBuilder.append(" AND IRN_RESPONSE IS NOT NULL ");
			hdrQueryBuilder.append(" AND HDR.IRN_RESPONSE IS NOT NULL ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("NO")) {
			queryBuilder.append(" AND IRN_RESPONSE IS NULL ");
			hdrQueryBuilder.append(" AND HDR.IRN_RESPONSE IS NULL ");
		}

		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND SUPPLIER_GSTIN IN (:sgstin) ");
			userQueryBuilder.append(" AND GSTIN IN (:sgstin)");
			queryVertical.append(" AND SUPPLIER_GSTIN IN (:sgstin) ");
			hdrQueryBuilder.append(" AND HDR.SUPPLIER_GSTIN IN (:sgstin) ");
			nillQueryBuilder.append(" AND HDR.SUPPLIER_GSTIN IN (:sgstin) ");
		}

		if (tableType != null && tableType.size() > 0) {
			queryBuilderGroupBy.append(" WHERE  TAX_DOC_TYPE IN (:tableType) ");
		}

		if (docType != null && docType.size() > 0) {
			queryBuilder.append(" AND DOC_TYPE IN (:docType) ");
			// queryVertical.append(" AND DOC_TYPE IN (:docType) ");
			hdrQueryBuilder.append(" AND HDR.DOC_TYPE IN (:docType) ");
			// nillQueryBuilder.append(" AND DOC_TYPE IN (:docType) ");
		}

		if (profitCenter != null && !profitCenter.isEmpty() && pcList != null
				&& pcList.size() > 0) {
			queryBuilder.append(" AND PROFIT_CENTRE IN (:pcList) ");
			queryVertical.append(" AND PROFIT_CENTRE IN (:pcList) ");
			hdrQueryBuilder.append(" AND HDR.PROFIT_CENTRE IN (:pcList) ");

		}
		if (plant != null && !plant.isEmpty() && plantList != null
				&& plantList.size() > 0) {
			queryBuilder.append(" AND PLANT_CODE IN (:plantList) ");
			queryVertical.append(" AND PLANT_CODE IN (:plantList) ");
			hdrQueryBuilder.append(" AND HDR.PLANT_CODE IN (:plantList) ");
		}
		if (sales != null && !sales.isEmpty() && salesList != null
				&& salesList.size() > 0) {
			queryBuilder.append(" AND SALES_ORGANIZATION IN (:salesList) ");
			queryVertical.append(" AND SALES_ORGANIZATION IN (:salesList) ");
			hdrQueryBuilder
					.append(" AND HDR.SALES_ORGANIZATION IN (:salesList) ");
		}
		if (distChannel != null && !distChannel.isEmpty() && distList != null
				&& distList.size() > 0) {
			queryBuilder.append(" AND DISTRIBUTION_CHANNEL IN (:distList) ");
			queryVertical.append(" AND DISTRIBUTION_CHANNEL IN (:distList) ");
			hdrQueryBuilder
					.append(" AND HDR.DISTRIBUTION_CHANNEL IN (:distList) ");
		}
		if (division != null && !division.isEmpty() && divisionList != null
				&& divisionList.size() > 0) {
			queryBuilder.append(" AND DIVISION IN (:divisionList) ");
			queryVertical.append(" AND DIVISION IN (:divisionList) ");
			hdrQueryBuilder.append(" AND HDR.DIVISION IN (:divisionList) ");
		}
		if (location != null && !location.isEmpty() && locationList != null
				&& locationList.size() > 0) {
			queryBuilder.append(" AND LOCATION IN (:locationList) ");
			queryVertical.append(" AND LOCATION IN (:locationList) ");
			hdrQueryBuilder.append(" AND HDR.LOCATION IN (:locationList) ");
		}
		if (ud1 != null && !ud1.isEmpty() && ud1List != null
				&& ud1List.size() > 0) {
			queryBuilder.append(" AND USERACCESS1 IN (:ud1List) ");
			queryVertical.append(" AND USERACCESS1 IN (:ud1List) ");
			hdrQueryBuilder.append(" AND HDR.USERACCESS1 IN (:ud1List) ");
		}
		if (ud2 != null && !ud2.isEmpty() && ud2List != null
				&& ud2List.size() > 0) {
			queryBuilder.append(" AND USERACCESS2 IN (:ud2List) ");
			queryVertical.append(" AND USERACCESS2 IN (:ud2List) ");
			hdrQueryBuilder.append(" AND HDR.USERACCESS2 IN (:ud2List) ");
		}
		if (ud3 != null && !ud3.isEmpty() && ud3List != null
				&& ud3List.size() > 0) {
			queryBuilder.append(" AND USERACCESS3 IN (:ud3List) ");
			queryVertical.append(" AND USERACCESS3 IN (:ud3List) ");
			hdrQueryBuilder.append(" AND HDR.USERACCESS3 IN (:ud3List) ");
		}
		if (ud4 != null && !ud4.isEmpty() && ud4List != null
				&& ud4List.size() > 0) {
			queryBuilder.append(" AND USERACCESS4 IN (:ud4List) ");
			queryVertical.append(" AND USERACCESS4 IN (:ud4List) ");
			hdrQueryBuilder.append(" AND HDR.USERACCESS4 IN (:ud4List) ");
		}
		if (ud5 != null && !ud5.isEmpty() && ud5List != null
				&& ud5List.size() > 0) {
			queryBuilder.append(" AND USERACCESS5 IN (:ud5List) ");
			queryVertical.append(" AND USERACCESS5 IN (:ud5List) ");
			hdrQueryBuilder.append(" AND HDR.USERACCESS5 IN (:ud5List) ");
		}
		if (ud6 != null && !ud6.isEmpty() && ud6List != null
				&& ud6List.size() > 0) {
			queryBuilder.append(" AND USERACCESS6 IN (:ud6List) ");
			queryVertical.append(" AND USERACCESS6 IN (:ud6List) ");
			hdrQueryBuilder.append(" AND HDR.USERACCESS6 IN (:ud6List) ");
		}

		String conditionVertical = queryVertical.toString();
		String hdrCondition = hdrQueryBuilder.toString();

		String groupByCondition = queryBuilderGroupBy.toString();
		String nillCondition = nillQueryBuilder.toString();
		// String userCompCondition = userQueryBuilder.toString();
		String mulCondition = mulQueryBuilder.toString();

		StringBuilder bufferString = new StringBuilder();

		String queryStr = null;
// old query backup--25-04-2024
		if (tabStatus != null && tabStatus.equals(1)) {

			queryStr = " WITH HDR "
					+ "AS ( "
					+ "	SELECT ID "
					+ "		,SUPPLIER_GSTIN "
					+ "		,RETURN_PERIOD "
					+ "		,TAX_DOC_TYPE "
					+ "		,DOC_TYPE "
					+ "		,DOC_KEY "
					+ "		,TAXABLE_VALUE "
					+ "		,IGST_AMT "
					+ "		,CGST_AMT "
					+ "		,SGST_AMT "
					+ "		,CESS_AMT_SPECIFIC "
					+ "		,CESS_AMT_ADVALOREM "
					+ "		,POS "
					+ "		,SUPPLY_TYPE "
					+ "		,DIFF_PERCENT "
					+ "		,ECOM_GSTIN "
					+ "		,IS_DELETE "
					+ "		,IS_PROCESSED "
					+ "		,IS_SENT_TO_GSTN "
					+ "		,IS_SAVED_TO_GSTN "
					+ "		,GSTN_ERROR "
					+ "		,MODIFIED_ON "
					+ "		,TCS_FLAG "
					+ "		,TABLE_SECTION "
					+ "		,IS_MULTI_SUPP_INV "
					+ "	FROM ANX_OUTWARD_DOC_HEADER HDR "
					+ "	WHERE HDR.ASP_INVOICE_STATUS = 2 "
					+ "		AND HDR.COMPLIANCE_APPLICABLE = TRUE "
					+ "		AND HDR.IS_DELETE = FALSE "
					+ "		AND ( "
					+ "			HDR.RETURN_TYPE = 'GSTR1' "
					+ "			AND HDR.TAX_DOC_TYPE IN ( "
					+ "				'B2B' "
					+ "				,'B2BA' "
					+ "				,'B2CL' "
					+ "				,'B2CLA' "
					+ "				,'EXPORTS' "
					+ "				,'EXPORTS-A' "
					+ "				,'CDNR' "
					+ "				,'CDNRA' "
					+ "				,'CDNUR' "
					+ "				,'CDNUR-EXPORTS' "
					+ "				,'CDNUR-B2CL' "
					+ "				,'CDNURA' "
					+ "				,'NILEXTNON' "
					+ "				,'AT' "
					+ "				,'TXP' "
					+ "				,'B2CS' "
					+ "				,'Supecom' "
					+ "				,'Ecomsup' "
					+ "				) "
					+ "			) "
					+ hdrCondition
					+ "	) "
					+ "	,ITM "
					+ "AS ( "
					+ "	SELECT DOC_HEADER_ID "
					+ "		,ITM.TAXABLE_VALUE "
					+ "		,ITM.IGST_AMT "
					+ "		,ITM.CGST_AMT "
					+ "		,ITM.SGST_AMT "
					+ "		,ITM.CESS_AMT_SPECIFIC "
					+ "		,ITM.CESS_AMT_ADVALOREM "
					+ "		,TAX_RATE "
					+ "		,ITM.ITM_TABLE_SECTION "
					+ "		,ITM.ITM_TAX_DOC_TYPE "
					+ "		,ITM.SUPPLY_TYPE "
					+ "		,ITM.DERIVED_RET_PERIOD "
					+ "	FROM HDR "
					+ "	INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON ITM.DOC_HEADER_ID = HDR.ID "
					+ "	) "
					+ "SELECT SUPPLIER_GSTIN "
					+ "	,RETURN_PERIOD "
					+ "	,TAX_DOC_TYPE "
					+ "	,IFNULL(SUM(SUPPLIES), 0) AS SUPPLIES "
					+ "	,IFNULL(SUM(IGST), 0) AS IGST "
					+ "	,IFNULL(SUM(CGST), 0) AS CGST "
					+ "	,IFNULL(SUM(SGST), 0) AS SGST "
					+ "	,IFNULL(SUM(CESS), 0) AS CESS "
					+ "	,IFNULL(SUM(GSTN_NOT_SENT_COUNT), 0) AS GSTN_NOT_SENT_COUNT "
					+ "	,IFNULL(SUM(GSTN_SAVED_COUNT), 0) AS GSTN_SAVED_COUNT "
					+ "	,IFNULL(SUM(GSTN_NOT_SAVED_COUNT), 0) AS GSTN_NOT_SAVED_COUNT "
					+ "	,IFNULL(SUM(GSTN_ERROR_COUNT), 0) AS GSTN_ERROR_COUNT "
					+ "	,IFNULL(SUM(TOTAL_COUNT_IN_ASP), 0) AS TOTAL_COUNT_IN_ASP "
					+ "	,CASE "
					+ "		WHEN TAX_DOC_TYPE = 'NILEXTNON' "
					+ "			THEN 0 "
					+ "		ELSE COUNT(DISTINCT KEY) "
					+ "		END AS TOT_COUNT "
					+ "	,MAX(MODIFIED_ON) MODIFIED_ON "
					+ "FROM ( "
					+ "	SELECT HDR.SUPPLIER_GSTIN "
					+ "		,HDR.RETURN_PERIOD "
					+ "		,CASE "
					+ "			WHEN ITM.ITM_TAX_DOC_TYPE = 'AT' "
					+ "				THEN 'ADV REC' "
					+ "			WHEN ITM.ITM_TAX_DOC_TYPE = 'TXP' "
					+ "				THEN 'ADV ADJ' "
					+ "			ELSE  ITM.ITM_TAX_DOC_TYPE END AS TAX_DOC_TYPE "
					+ "		,CASE "
					+ "			WHEN ITM.ITM_TAX_DOC_TYPE IN ( "
					+ "					'AT' "
					+ "					,'TXP' "
					+ "					) "
					+ "				THEN (Ifnull(HDR.SUPPLIER_GSTIN, '') || '|' || Ifnull(HDR.RETURN_PERIOD, '') || '|' || Ifnull(HDR.POS, 9999)) "
					+ "			ELSE HDR.DOC_KEY "
					+ "			END AS KEY "
					+ "		,( "
					+ "			Ifnull(( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE IN ( "
					+ "								'INV' "
					+ "								,'DR' "
					+ "								,'BOS' "
					+ "								) "
					+ "							AND ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "							AND ITM.SUPPLY_TYPE = 'NIL' "
					+ "							THEN  ITM.TAXABLE_VALUE "
					+ "						WHEN HDR.DOC_TYPE IN ( "
					+ "								'CR' "
					+ "								,'RCR' "
					+ "								) "
					+ "							AND HDR.TAX_DOC_TYPE IN ( "
					+ "								'CDNR' "
					+ "								,'CDNRA' "
					+ "								,'CDNUR' "
					+ "								,'CDNUR-EXPORTS' "
					+ "								,'CDNUR-B2CL' "
					+ "								,'CDNURA' "
					+ "								) "
					+ "							THEN - 1 *  ITM.TAXABLE_VALUE "
					+ "						WHEN HDR.DOC_TYPE IN ( "
					+ "								'DR' "
					+ "								,'RDR' "
					+ "								) "
					+ "							AND ITM.ITM_TAX_DOC_TYPE IN ( "
					+ "								'CDNR' "
					+ "								,'CDNRA' "
					+ "								,'CDNUR' "
					+ "								,'CDNUR-EXPORTS' "
					+ "								,'CDNUR-B2CL' "
					+ "								,'CDNURA' "
					+ "								) "
					+ "							THEN  ITM.TAXABLE_VALUE "
					+ "						WHEN ITM.ITM_TAX_DOC_TYPE IN ( "
					+ "								'B2B' "
					+ "								,'B2BA' "
					+ "								,'B2CL' "
					+ "								,'B2CLA' "
					+ "								,'EXPORTS' "
					+ "								,'EXPORTS-A' "
					+ "								,'AT' "
					+ "								,'TXP' "
					+ "								) "
					+ "							THEN  ITM.TAXABLE_VALUE "
					+ "						END "
					+ "					), 0) - Ifnull(( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE = 'CR' "
					+ "							AND ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "							AND ITM.SUPPLY_TYPE = 'NIL' "
					+ "							THEN  ITM.TAXABLE_VALUE "
					+ "						END "
					+ "					), 0) "
					+ "			) + ( "
					+ "			Ifnull(( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE IN ( "
					+ "								'INV' "
					+ "								,'DR' "
					+ "								,'BOS' "
					+ "								) "
					+ "							AND ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "							AND ITM.SUPPLY_TYPE = 'EXT' "
					+ "							THEN  ITM.TAXABLE_VALUE "
					+ "									 "
					+ "						END "
					+ "					), 0) - Ifnull(( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE = 'CR' "
					+ "							AND ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "							AND ITM.SUPPLY_TYPE = 'EXT' "
					+ "							THEN ITM.TAXABLE_VALUE "
					+ "						END "
					+ "					), 0) "
					+ "			) + ( "
					+ "			Ifnull(( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE IN ( "
					+ "								'INV' "
					+ "								,'DR' "
					+ "								,'BOS' "
					+ "								) "
					+ "							AND ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "							AND ITM.SUPPLY_TYPE IN ( "
					+ "								'NON' "
					+ "								,'SCH3' "
					+ "								) "
					+ "							THEN  ITM.TAXABLE_VALUE "
					+ "						END "
					+ "					), 0) - Ifnull(( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE = 'CR' "
					+ "							AND ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "							AND ITM.SUPPLY_TYPE IN ( "
					+ "								'NON' "
					+ "								,'SCH3' "
					+ "								) "
					+ "							THEN  ITM.TAXABLE_VALUE "
					+ "						END "
					+ "					), 0) "
					+ "			) AS SUPPLIES "
					+ "		,CASE "
					+ "			WHEN ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "				THEN 0 "
					+ "			ELSE ( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE IN ( "
					+ "								'C' "
					+ "								,'CR' "
					+ "								,'RCR' "
					+ "								) "
					+ "							THEN - 1 * IFNULL(ITM.IGST_AMT, 0) "
					+ "						ELSE 1 * IFNULL(ITM.IGST_AMT, 0) "
					+ "						END "
					+ "					) "
					+ "			END AS IGST "
					+ "		,CASE "
					+ "			WHEN ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "				THEN 0 "
					+ "			ELSE ( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE IN ( "
					+ "								'C' "
					+ "								,'CR' "
					+ "								,'RCR' "
					+ "								) "
					+ "							THEN - 1 * IFNULL(ITM.CGST_AMT, 0) "
					+ "						ELSE 1 * IFNULL(ITM.CGST_AMT, 0) "
					+ "						END "
					+ "					) "
					+ "			END AS CGST "
					+ "		,CASE "
					+ "			WHEN ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "				THEN 0 "
					+ "			ELSE ( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE IN ( "
					+ "								'C' "
					+ "								,'CR' "
					+ "								,'RCR' "
					+ "								) "
					+ "							THEN - 1 * IFNULL(ITM.SGST_AMT, 0) "
					+ "						ELSE 1 * IFNULL(ITM.SGST_AMT, 0) "
					+ "						END "
					+ "					) "
					+ "			END AS SGST "
					+ "		,CASE "
					+ "			WHEN ITM.ITM_TAX_DOC_TYPE = 'NILEXTNON' "
					+ "				THEN 0 "
					+ "			ELSE ( "
					+ "					CASE "
					+ "						WHEN HDR.DOC_TYPE IN ( "
					+ "								'C' "
					+ "								,'CR' "
					+ "								,'RCR' "
					+ "								) "
					+ "							THEN - 1 * (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0)) "
					+ "						ELSE 1 * IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
					+ "						END "
					+ "					) "
					+ "			END AS CESS "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SENT_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_NOT_SENT_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_NOT_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN GSTN_ERROR = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_ERROR_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) TOTAL_COUNT_IN_ASP "
					+ "		,HDR.MODIFIED_ON "
					+ "	FROM HDR "
					+ "	INNER JOIN "
					
					+ "	  ITM ON HDR.ID = ITM.DOC_HEADER_ID "
					+ "	WHERE  ITM.ITM_TAX_DOC_TYPE "
					+ "		 IN ( "
					+ "			'B2B' "
					+ "			,'B2BA' "
					+ "			,'B2CL' "
					+ "			,'B2CLA' "
					+ "			,'EXPORTS' "
					+ "			,'EXPORTS-A' "
					+ "			,'CDNR' "
					+ "			,'CDNRA' "
					+ "			,'CDNUR' "
					+ "			,'CDNUR-EXPORTS' "
					+ "			,'CDNUR-B2CL' "
					+ "			,'CDNURA' "
					+ "			,'NILEXTNON' "
					+ "			,'AT' "
					+ "			,'TXP' "
					+ "			) "
					+ "	 "
					+ "	UNION ALL "
					+ "	 "
					+ "	SELECT SUPPLIER_GSTIN "
					+ "		,RETURN_PERIOD "
					+ "		,CASE "
					+ "			WHEN IS_AMENDMENT = FALSE "
					+ "				THEN 'B2CS' "
					+ "			ELSE 'B2CSA' "
					+ "			END AS TAX_DOC_TYPE "
					+ "		,(Ifnull(SUPPLIER_GSTIN, '') || '|' || Ifnull(RETURN_PERIOD, '') || '|' || Ifnull(TRAN_TYPE, '') || '|' || Ifnull(NEW_POS, 9999) || '|' || Ifnull(NEW_ECOM_GSTIN, '') || '|' || Ifnull(NEW_RATE, 9999)) AS KEY "
					+ "		,Ifnull((NEW_TAXABLE_VALUE), 0) AS SUPPLIES "
					+ "		,Ifnull((IGST_AMT), 0) AS IGST "
					+ "		,Ifnull((CGST_AMT), 0) AS CGST "
					+ "		,Ifnull((SGST_AMT), 0) AS SGST "
					+ "		,Ifnull((CESS_AMT), 0) AS CESS "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SENT_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_NOT_SENT_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_NOT_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN GSTN_ERROR = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_ERROR_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) TOTAL_COUNT_IN_ASP "
					+ "		,MODIFIED_ON "
					+ "	FROM GSTR1_PROCESSED_B2CS "
					+ "	WHERE IS_DELETE = FALSE "
					+ conditionVertical
					+ "	UNION ALL "
					+ "	 "
					+ "	SELECT SUPPLIER_GSTIN "
					+ "		,HDR.RETURN_PERIOD "
					+ "		, ITM.ITM_TAX_DOC_TYPE AS TAX_DOC_TYPE "
					+ "		,(IFNULL(SUPPLIER_GSTIN, '') || '|' || IFNULL(HDR.RETURN_PERIOD, '') || '|' || IFNULL(HDR.DIFF_PERCENT, '') || '|' || IFNULL(HDR.POS, 9999) || '|' || IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(TAX_RATE, 9999)) KEY "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'CR' "
					+ "					,'RCR' "
					+ "					) "
					+ "				THEN - 1 * IFNULL(ITM.TAXABLE_VALUE, 0) "
					+ "			ELSE IFNULL(ITM.TAXABLE_VALUE, 0) "
					+ "			END AS SUPPLIES "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'C' "
					+ "					,'CR' "
					+ "					,'RCR' "
					+ "					) "
					+ "				THEN - 1 * IFNULL(ITM.IGST_AMT, 0) "
					+ "			ELSE 1 * IFNULL(ITM.IGST_AMT, 0) "
					+ "			END AS IGST "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'C' "
					+ "					,'CR' "
					+ "					,'RCR' "
					+ "					) "
					+ "				THEN - 1 * IFNULL(ITM.CGST_AMT, 0) "
					+ "			ELSE 1 * IFNULL(ITM.CGST_AMT, 0) "
					+ "			END AS CGST "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'C' "
					+ "					,'CR' "
					+ "					,'RCR' "
					+ "					) "
					+ "				THEN - 1 * IFNULL(ITM.SGST_AMT, 0) "
					+ "			ELSE 1 * IFNULL(ITM.SGST_AMT, 0) "
					+ "			END AS SGST "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'C' "
					+ "					,'CR' "
					+ "					,'RCR' "
					+ "					) "
					+ "				THEN - 1 * (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0)) "
					+ "			ELSE 1 * IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
					+ "			END AS CESS "
					+ "		,CASE "
					+ "			WHEN IS_SENT_TO_GSTN = FALSE "
					+ "				AND IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END AS GSTN_NOT_SENT_COUNT "
					+ "		,CASE "
					+ "			WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "				AND IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END AS GSTN_SAVED_COUNT "
					+ "		,CASE "
					+ "			WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "				AND IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END AS GSTN_NOT_SAVED_COUNT "
					+ "		,CASE "
					+ "			WHEN GSTN_ERROR = TRUE "
					+ "				AND IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END AS GSTN_ERROR_COUNT "
					+ "		,CASE "
					+ "			WHEN IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END AS TOTAL_COUNT_IN_ASP "
					+ "		,HDR.MODIFIED_ON AS MODIFIED_ON "
					+ "	FROM HDR "
					+ "	INNER JOIN ITM ON HDR.ID = ITM.DOC_HEADER_ID "
					+ "	WHERE  ITM.ITM_TAX_DOC_TYPE IN ('B2CS') "
					+ "	 "
					+ "	UNION ALL "
					+ "	 "
					+ "	SELECT SUPPLIER_GSTIN "
					+ "		,RETURN_PERIOD "
					+ "		,CASE "
					+ "			WHEN IS_AMENDMENT = FALSE "
					+ "				THEN 'ADV REC' "
					+ "			ELSE 'ADV REC-A' "
					+ "			END AS TAX_DOC_TYPE "
					+ "		,( "
					+ "			Ifnull(SUPPLIER_GSTIN, '') || '|' || Ifnull(RETURN_PERIOD, '') || '|' || ( "
					+ "				CASE "
					+ "					WHEN TRAN_TYPE IN ( "
					+ "							'ZL65' "
					+ "							,'L65' "
					+ "							,'zl65' "
					+ "							,'l65' "
					+ "							,'zL65' "
					+ "							,'Zl65' "
					+ "							) "
					+ "						THEN 'L65' "
					+ "					WHEN ( "
					+ "							TRAN_TYPE IN ( "
					+ "								'Z' "
					+ "								,'N' "
					+ "								,'' "
					+ "								,'z' "
					+ "								,'n' "
					+ "								) "
					+ "							OR TRAN_TYPE IS NULL "
					+ "							) "
					+ "						THEN 'N' "
					+ "					END "
					+ "				) || '|' || Ifnull(NEW_POS, 9999) "
					+ "			) AS KEY "
					+ "		,Ifnull((NEW_GROSS_ADV_RECEIVED), 0) AS SUPPLIES "
					+ "		,Ifnull((IGST_AMT), 0) AS IGST "
					+ "		,Ifnull((CGST_AMT), 0) AS CGST "
					+ "		,Ifnull((SGST_AMT), 0) AS SGST "
					+ "		,Ifnull((CESS_AMT), 0) AS CESS "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SENT_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_NOT_SENT_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_NOT_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN GSTN_ERROR = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_ERROR_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) TOTAL_COUNT_IN_ASP "
					+ "		,MODIFIED_ON MODIFIED_ON "
					+ "	FROM GSTR1_PROCESSED_ADV_RECEIVED "
					+ "	WHERE IS_DELETE = FALSE "
					+ conditionVertical
					+ "	UNION ALL "
					+ "	 "
					+ "	SELECT SUPPLIER_GSTIN "
					+ "		,RETURN_PERIOD "
					+ "		,CASE "
					+ "			WHEN IS_AMENDMENT = FALSE "
					+ "				THEN 'ADV ADJ' "
					+ "			ELSE 'ADV ADJ-A' "
					+ "			END AS TAX_DOC_TYPE "
					+ "		,( "
					+ "			Ifnull(SUPPLIER_GSTIN, '') || '|' || Ifnull(RETURN_PERIOD, '') || '|' || ( "
					+ "				CASE "
					+ "					WHEN TRAN_TYPE IN ( "
					+ "							'ZL65' "
					+ "							,'L65' "
					+ "							,'zl65' "
					+ "							,'l65' "
					+ "							,'zL65' "
					+ "							,'Zl65' "
					+ "							) "
					+ "						THEN 'L65' "
					+ "					WHEN ( "
					+ "							TRAN_TYPE IN ( "
					+ "								'Z' "
					+ "								,'N' "
					+ "								,'' "
					+ "								,'z' "
					+ "								,'n' "
					+ "								) "
					+ "							OR TRAN_TYPE IS NULL "
					+ "							) "
					+ "						THEN 'N' "
					+ "					END "
					+ "				) || '|' || Ifnull(NEW_POS, 9999) "
					+ "			) AS KEY "
					+ "		,Ifnull((NEW_GROSS_ADV_ADJUSTED), 0) AS SUPPLIES "
					+ "		,Ifnull((IGST_AMT), 0) AS IGST "
					+ "		,Ifnull((CGST_AMT), 0) AS CGST "
					+ "		,Ifnull((SGST_AMT), 0) AS SGST "
					+ "		,Ifnull((CESS_AMT), 0) AS CESS "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SENT_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_NOT_SENT_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_NOT_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN GSTN_ERROR = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) GSTN_ERROR_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE NULL "
					+ "				END "
					+ "			) TOTAL_COUNT_IN_ASP "
					+ "		,MODIFIED_ON "
					+ "	FROM GSTR1_PROCESSED_ADV_ADJUSTMENT "
					+ "	WHERE IS_DELETE = FALSE "
					+ conditionVertical
					+ "	UNION ALL "
					+ "	 "
					+ "	SELECT SUPPLIER_GSTIN "
					+ "		,RETURN_PERIOD "
					+ "		,'NILEXTNON' AS TAX_DOC_TYPE "
					+ "		,NULL AS KEY "
					+ "		,( "
					+ "			IFNULL(( "
					+ "					CASE "
					+ "						WHEN SUPPLY_TYPE = 'NIL' "
					+ "							THEN TAXABLE_VALUE "
					+ "						END "
					+ "					), 0) "
					+ "			) + ( "
					+ "			IFNULL(( "
					+ "					CASE "
					+ "						WHEN SUPPLY_TYPE = 'EXT' "
					+ "							THEN TAXABLE_VALUE "
					+ "						END "
					+ "					), 0) "
					+ "			) + ( "
					+ "			IFNULL(( "
					+ "					CASE "
					+ "						WHEN SUPPLY_TYPE IN ('NON') "
					+ "							THEN TAXABLE_VALUE "
					+ "						END "
					+ "					), 0) "
					+ "			) AS SUPPLIES "
					+ "		,0 AS IGST "
					+ "		,0 AS CGST "
					+ "		,0 AS SGST "
					+ "		,0 AS CESS "
					+ "		,CASE "
					+ "			WHEN IS_SENT_TO_GSTN = FALSE "
					+ "				AND HDR.IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END GSTN_NOT_SENT_COUNT "
					+ "		,CASE "
					+ "			WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "				AND HDR.IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END GSTN_SAVED_COUNT "
					+ "		,CASE "
					+ "			WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "				AND HDR.IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END GSTN_NOT_SAVED_COUNT "
					+ "		,CASE "
					+ "			WHEN GSTN_ERROR = TRUE "
					+ "				AND IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END GSTN_ERROR_COUNT "
					+ "		,CASE "
					+ "			WHEN HDR.IS_DELETE = FALSE "
					+ "				THEN 1 "
					+ "			ELSE NULL "
					+ "			END TOTAL_COUNT_IN_ASP "
					+ "		,HDR.MODIFIED_ON MODIFIED_ON "
					+ "	FROM GSTR1_SUMMARY_NILEXTNON HDR "
					+ "	WHERE IS_DELETE = FALSE "
					+ nillCondition
					+ "	UNION ALL "
					+ "	 "
					+ "	SELECT SUPPLIER_GSTIN "
					+ "		,HDR.RETURN_PERIOD "
					+ "		, ITM.ITM_TAX_DOC_TYPE AS TAX_DOC_TYPE "
					+ "		,HDR.ECOM_GSTIN AS KEY "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE = 'CR' "
					+ "				THEN - 1 * IFNULL(ITM.TAXABLE_VALUE, 0) "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'DR' "
					+ "					,'INV' "
					+ "					) "
					+ "				THEN IFNULL(ITM.TAXABLE_VALUE, 0) "
					+ "			END AS SUPPLIES "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE = 'CR' "
					+ "				THEN - 1 * IFNULL(ITM.IGST_AMT, 0) "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'DR' "
					+ "					,'INV' "
					+ "					) "
					+ "				THEN IFNULL(ITM.IGST_AMT, 0) "
					+ "			END AS IGST "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE = 'CR' "
					+ "				THEN - 1 * IFNULL(ITM.CGST_AMT, 0) "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'DR' "
					+ "					,'INV' "
					+ "					) "
					+ "				THEN IFNULL(ITM.CGST_AMT, 0) "
					+ "			END AS CGST "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE = 'CR' "
					+ "				THEN - 1 * IFNULL(ITM.SGST_AMT, 0) "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'DR' "
					+ "					,'INV' "
					+ "					) "
					+ "				THEN IFNULL(ITM.SGST_AMT, 0) "
					+ "			END AS SGST "
					+ "		,CASE "
					+ "			WHEN HDR.DOC_TYPE = 'CR' "
					+ "				THEN - 1 * (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0)) "
					+ "			WHEN HDR.DOC_TYPE IN ( "
					+ "					'DR' "
					+ "					,'INV' "
					+ "					) "
					+ "				THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
					+ "			END AS CESS "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SENT_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE 0 "
					+ "				END "
					+ "			) AS GSTN_NOT_SENT_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE 0 "
					+ "				END "
					+ "			) AS GSTN_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE 0 "
					+ "				END "
					+ "			) AS GSTN_NOT_SAVED_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN GSTN_ERROR = TRUE "
					+ "					AND IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE 0 "
					+ "				END "
					+ "			) AS GSTN_ERROR_COUNT "
					+ "		,( "
					+ "			CASE "
					+ "				WHEN IS_DELETE = FALSE "
					+ "					THEN 1 "
					+ "				ELSE 0 "
					+ "				END "
					+ "			) AS TOTAL_COUNT_IN_ASP "
					+ "		,MAX(HDR.MODIFIED_ON) OVER () AS MODIFIED_ON "
					+ "	FROM HDR "
					+ "	INNER JOIN ITM ON HDR.ID = ITM.DOC_HEADER_ID "
					+ "	WHERE HDR.SUPPLY_TYPE <> 'CAN' "
					+ "		AND IS_PROCESSED = TRUE "
					+ "		AND ( "
					+ "			 ITM.ITM_TABLE_SECTION IN ('14(ii)') "
					+ "			AND HDR.TCS_FLAG = 'E' "
					+ "			) "
					+ "	 "
					+ "	UNION ALL "
					+ "	 "
					+ "	SELECT SUPPLIER_GSTIN "
					+ "		,RETURN_PERIOD "
					+ "		,TAX_DOC_TYPE "
					+ "		,DOC_KEY_15 "
					+ "		,SUPPLIES "
					+ "		,IGST "
					+ "		,CGST "
					+ "		,SGST "
					+ "		,CESS "
					+ "		,GSTN_NOT_SENT_COUNT "
					+ "		,GSTN_SAVED_COUNT "
					+ "		,GSTN_NOT_SAVED_COUNT "
					+ "		,GSTN_ERROR_COUNT "
					+ "		,TOTAL_COUNT_IN_ASP "
					+ "		,MODIFIED_ON "
					+ "	FROM ( "
					+ "		SELECT HDR.SUPPLIER_GSTIN "
					+ "			,HDR.RETURN_PERIOD "
					+ "			, ITM.ITM_TAX_DOC_TYPE AS TAX_DOC_TYPE "
					+ "			, ITM.ITM_TABLE_SECTION AS TABLE_SECTION "
					+ "			,CASE "
					+ "				WHEN ITM.ITM_TABLE_SECTION IN ( "
					+ "						'15(i)' "
					+ "						,'15(iii)' "
					+ "						) "
					+ "					THEN DOC_KEY "
					+ "				WHEN ITM.ITM_TABLE_SECTION = '15(ii)' "
					+ "					THEN IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(HDR.POS, '') || '|' || IFNULL(ITM.TAX_RATE, 9999) "
					+ "				WHEN HDR.TABLE_SECTION = '15(iv)' "
					+ "					THEN IFNULL(HDR.POS, '') || '|' || IFNULL(ITM.TAX_RATE, 9999) "
					+ "				ELSE HDR.ECOM_GSTIN "
					+ "				END AS DOC_KEY_15 "
					+ "			,CASE "
					+ "				WHEN HDR.DOC_TYPE = 'CR' "
					+ "					THEN - 1 * IFNULL(ITM.TAXABLE_VALUE, 0) "
					+ "				WHEN HDR.DOC_TYPE IN ( "
					+ "						'DR' "
					+ "						,'INV' "
					+ "						) "
					+ "					THEN IFNULL(ITM.TAXABLE_VALUE, 0) "
					+ "				END AS SUPPLIES "
					+ "			,CASE "
					+ "				WHEN HDR.DOC_TYPE = 'CR' "
					+ "					THEN - 1 * IFNULL(ITM.IGST_AMT, 0) "
					+ "				WHEN HDR.DOC_TYPE IN ( "
					+ "						'DR' "
					+ "						,'INV' "
					+ "						) "
					+ "					THEN IFNULL(ITM.IGST_AMT, 0) "
					+ "				END AS IGST "
					+ "			,CASE "
					+ "				WHEN HDR.DOC_TYPE = 'CR' "
					+ "					THEN - 1 * IFNULL(ITM.CGST_AMT, 0) "
					+ "				WHEN HDR.DOC_TYPE IN ( "
					+ "						'DR' "
					+ "						,'INV' "
					+ "						) "
					+ "					THEN IFNULL(ITM.CGST_AMT, 0) "
					+ "				END AS CGST "
					+ "			,CASE "
					+ "				WHEN HDR.DOC_TYPE = 'CR' "
					+ "					THEN - 1 * IFNULL(ITM.SGST_AMT, 0) "
					+ "				WHEN HDR.DOC_TYPE IN ( "
					+ "						'DR' "
					+ "						,'INV' "
					+ "						) "
					+ "					THEN IFNULL(ITM.SGST_AMT, 0) "
					+ "				END AS SGST "
					+ "			,CASE "
					+ "				WHEN HDR.DOC_TYPE = 'CR' "
					+ "					THEN - 1 * (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0)) "
					+ "				WHEN HDR.DOC_TYPE IN ( "
					+ "						'DR' "
					+ "						,'INV' "
					+ "						) "
					+ "					THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
					+ "				END AS CESS "
					+ "			,( "
					+ "				CASE "
					+ "					WHEN IS_SENT_TO_GSTN = FALSE "
					+ "						AND IS_DELETE = FALSE "
					+ "						THEN 1 "
					+ "					ELSE 0 "
					+ "					END "
					+ "				) AS GSTN_NOT_SENT_COUNT "
					+ "			,( "
					+ "				CASE "
					+ "					WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "						AND IS_DELETE = FALSE "
					+ "						THEN 1 "
					+ "					ELSE 0 "
					+ "					END "
					+ "				) AS GSTN_SAVED_COUNT "
					+ "			,( "
					+ "				CASE "
					+ "					WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "						AND IS_DELETE = FALSE "
					+ "						THEN 1 "
					+ "					ELSE 0 "
					+ "					END "
					+ "				) AS GSTN_NOT_SAVED_COUNT "
					+ "			,( "
					+ "				CASE "
					+ "					WHEN GSTN_ERROR = TRUE "
					+ "						AND IS_DELETE = FALSE "
					+ "						THEN 1 "
					+ "					ELSE 0 "
					+ "					END "
					+ "				) AS GSTN_ERROR_COUNT "
					+ "			,( "
					+ "				CASE "
					+ "					WHEN IS_DELETE = FALSE "
					+ "						THEN 1 "
					+ "					ELSE 0 "
					+ "					END "
					+ "				) AS TOTAL_COUNT_IN_ASP "
					+ "			,MAX(HDR.MODIFIED_ON) OVER () AS MODIFIED_ON "
					+ "		FROM HDR "
					+ "		INNER JOIN ( "
					+ "			SELECT ITM_TABLE_SECTION "
					+ "				,SUPPLY_TYPE "
					+ "				,ITM_TAX_DOC_TYPE "
					+ "				,SUM(TAXABLE_VALUE) AS TAXABLE_VALUE "
					+ "				,DOC_HEADER_ID "
					+ "				,TAX_RATE "
					+ "				,SUM(IGST_AMT) AS IGST_AMT "
					+ "				,SUM(CGST_AMT) AS CGST_AMT "
					+ "				,SUM(SGST_AMT) AS SGST_AMT "
					+ "				,SUM(CESS_AMT_SPECIFIC) AS CESS_AMT_SPECIFIC "
					+ "				,SUM(CESS_AMT_ADVALOREM) AS CESS_AMT_ADVALOREM "
					+ "			FROM ITM "
					+ "			WHERE DERIVED_RET_PERIOD = :taxPeriod "
					+ "			GROUP BY DOC_HEADER_ID "
					+ "				,ITM_TABLE_SECTION "
					+ "				,SUPPLY_TYPE "
					+ "				,ITM_TAX_DOC_TYPE "
					+ "				,TAX_RATE "
					+ "			) ITM ON HDR.ID = ITM.DOC_HEADER_ID "
					+ "		WHERE IS_PROCESSED = TRUE "
					+ "			AND  ITM.ITM_TABLE_SECTION "
					+ "			 IN ( "
					+ "				'15(i)' "
					+ "				,'15(ii)' "
					+ "				,'15(iii)' "
					+ "				,'15(iv)' "
					+ "				) "
					+ "		) "
					+ "	) "
					+ groupByCondition
					+ " GROUP BY SUPPLIER_GSTIN "
					+ "	,RETURN_PERIOD "
					+ "	,TAX_DOC_TYPE "
					+ "ORDER BY LENGTH(TAX_DOC_TYPE)";
		
	} else {
			
			LOGGER.debug(" inside else ");
				queryStr = " WITH HDR "
						+ "AS ( "
						+ "	SELECT ID "
						+ "		,SUPPLIER_GSTIN "
						+ "		,RETURN_PERIOD "
						+ "		,TAX_DOC_TYPE "
						+ "		,DOC_TYPE "
						+ "		,DOC_KEY "
						+ "		,TAXABLE_VALUE "
						+ "		,IGST_AMT "
						+ "		,CGST_AMT "
						+ "		,SGST_AMT "
						+ "		,CESS_AMT_SPECIFIC "
						+ "		,CESS_AMT_ADVALOREM "
						+ "		,POS "
						+ "		,SUPPLY_TYPE "
						+ "		,DIFF_PERCENT "
						+ "		,ECOM_GSTIN "
						+ "		,IS_DELETE "
						+ "		,IS_PROCESSED "
						+ "		,IS_SENT_TO_GSTN "
						+ "		,IS_SAVED_TO_GSTN "
						+ "		,GSTN_ERROR "
						+ "		,MODIFIED_ON "
						+ "		,TCS_FLAG "
						+ "		,TABLE_SECTION "
						+ "		,IS_MULTI_SUPP_INV "
						+ "	FROM ANX_OUTWARD_DOC_HEADER HDR "
						+ "	WHERE HDR.ASP_INVOICE_STATUS = 2 "
						+ "		AND HDR.COMPLIANCE_APPLICABLE = TRUE "
						+ "		AND HDR.IS_DELETE = FALSE "
						+ "		AND ( "
						+ "			HDR.RETURN_TYPE = 'GSTR1' "
						+ "			AND HDR.TAX_DOC_TYPE IN ( "
						+ "				'B2B' "
						+ "				,'B2BA' "
						+ "				,'B2CL' "
						+ "				,'B2CLA' "
						+ "				,'EXPORTS' "
						+ "				,'EXPORTS-A' "
						+ "				,'CDNR' "
						+ "				,'CDNRA' "
						+ "				,'CDNUR' "
						+ "				,'CDNUR-EXPORTS' "
						+ "				,'CDNUR-B2CL' "
						+ "				,'CDNURA' "
						+ "				,'NILEXTNON' "
						+ "				,'AT' "
						+ "				,'TXP' "
						+ "				,'B2CS' "
						+ "				,'Supecom' "
						+ "				,'Ecomsup' "
						+ "				) "
						+ "			) "
						+ hdrCondition
						+ "	) "
						+ "	,ITM "
						+ "AS ( "
						+ "	SELECT DOC_HEADER_ID "
						+ "		,ITM.TAXABLE_VALUE "
						+ "		,ITM.IGST_AMT "
						+ "		,ITM.CGST_AMT "
						+ "		,ITM.SGST_AMT "
						+ "		,ITM.CESS_AMT_SPECIFIC "
						+ "		,ITM.CESS_AMT_ADVALOREM "
						+ "		,TAX_RATE "
						+ "		,ITM.ITM_TABLE_SECTION "
						+ "		,ITM.ITM_TAX_DOC_TYPE "
						+ "		,ITM.SUPPLY_TYPE "
						+ "		,DERIVED_RET_PERIOD "
						+ "	FROM HDR "
						+ "	INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON ITM.DOC_HEADER_ID = HDR.ID "
						+ "	) "
						+ "SELECT SUPPLIER_GSTIN "
						+ "	,RETURN_PERIOD "
						+ "	,TAX_DOC_TYPE "
						+ "	,SUM(SUPPLIES) SUPPLIES "
						+ "	,SUM(IGST) IGST "
						+ "	,SUM(CGST) CGST "
						+ "	,SUM(SGST) SGST "
						+ "	,SUM(CESS) CESS "
						+ "	,SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT "
						+ "	,SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT "
						+ "	,SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT "
						+ "	,SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT "
						+ "	,SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP "
						+ "	,CASE "
						+ "		WHEN TAX_DOC_TYPE = 'NILEXTNON' "
						+ "			THEN 0 "
						+ "		ELSE COUNT(DISTINCT KEY) "
						+ "		END AS TOT_COUNT "
						+ "	,MAX(MODIFIED_ON) MODIFIED_ON "
						+ "FROM ( "
						+ "	SELECT HDR.SUPPLIER_GSTIN "
						+ "		,HDR.RETURN_PERIOD "
						+ "		,CASE "
						+ "			WHEN ITM.ITM_TAX_DOC_TYPE = 'AT' "
						+ "				THEN 'ADV REC' "
						+ "			WHEN ITM.ITM_TAX_DOC_TYPE = 'TXP' "
						+ "				THEN 'ADV ADJ' "
						+ "			ELSE ITM.ITM_TAX_DOC_TYPE "
						+ "			END AS TAX_DOC_TYPE "
						+ "		,CASE "
						+ "			WHEN ITM.ITM_TAX_DOC_TYPE = 'B2CS' "
						+ "				THEN (Ifnull(HDR.SUPPLIER_GSTIN, '') || '|' || Ifnull(HDR.RETURN_PERIOD, '') || '|' || Ifnull(HDR.DIFF_PERCENT, '') || '|' || Ifnull(HDR.POS, 9999) || '|' || Ifnull(HDR.ECOM_GSTIN, '') || '|' || Ifnull(TAX_RATE, 9999)) "
						+ "			WHEN ITM.ITM_TAX_DOC_TYPE IN ( "
						+ "					'AT' "
						+ "					,'TXP' "
						+ "					) "
						+ "				THEN (Ifnull(HDR.SUPPLIER_GSTIN, '') || '|' || Ifnull(HDR.RETURN_PERIOD, '') || '|' || Ifnull(HDR.DIFF_PERCENT, '') || '|' || Ifnull(HDR.POS, 9999)) "
						+ "			ELSE HDR.DOC_KEY "
						+ "			END AS KEY "
						+ "		,CASE "
						+ "			WHEN DOC_TYPE IN ( "
						+ "					'CR' "
						+ "					,'C' "
						+ "					,'RCR' "
						+ "					) "
						+ "				THEN - 1 * IFNULL(ITM.TAXABLE_VALUE, 0) "
						+ "			ELSE IFNULL(ITM.TAXABLE_VALUE, 0) "
						+ "			END AS SUPPLIES "
						+ "		,CASE "
						+ "			WHEN HDR.DOC_TYPE IN ( "
						+ "					'CR' "
						+ "					,'C' "
						+ "					,'RCR' "
						+ "					) "
						+ "				THEN - 1 * IFNULL(ITM.IGST_AMT, 0) "
						+ "			ELSE IFNULL(ITM.IGST_AMT, 0) "
						+ "			END AS IGST "
						+ "		,CASE "
						+ "			WHEN HDR.DOC_TYPE IN ( "
						+ "					'CR' "
						+ "					,'C' "
						+ "					,'RCR' "
						+ "					) "
						+ "				THEN - 1 * IFNULL(ITM.CGST_AMT, 0) "
						+ "			ELSE IFNULL(ITM.CGST_AMT, 0) "
						+ "			END AS CGST "
						+ "		,CASE "
						+ "			WHEN HDR.DOC_TYPE IN ( "
						+ "					'CR' "
						+ "					,'C' "
						+ "					,'RCR' "
						+ "					) "
						+ "				THEN - 1 * IFNULL(ITM.SGST_AMT, 0) "
						+ "			ELSE IFNULL(ITM.SGST_AMT, 0) "
						+ "			END AS SGST "
						+ "		,CASE "
						+ "			WHEN HDR.DOC_TYPE IN ( "
						+ "					'CR' "
						+ "					,'C' "
						+ "					,'RCR' "
						+ "					) "
						+ "				THEN - 1 * (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + ifnull(ITM.CESS_AMT_ADVALOREM, 0)) "
						+ "			ELSE (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + ifnull(ITM.CESS_AMT_ADVALOREM, 0)) "
						+ "			END AS CESS "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SENT_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_NOT_SENT_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_NOT_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN GSTN_ERROR = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_ERROR_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) TOTAL_COUNT_IN_ASP "
						+ "		,HDR.MODIFIED_ON "
						+ "	FROM HDR "
						+ "	INNER JOIN ( "
						+ "		SELECT ITM_TABLE_SECTION "
						+ "			,SUPPLY_TYPE "
						+ "			,ITM_TAX_DOC_TYPE "
						+ "			,SUM(TAXABLE_VALUE) AS TAXABLE_VALUE "
						+ "			,DOC_HEADER_ID "
						+ "			,TAX_RATE "
						+ "			,SUM(IGST_AMT) AS IGST_AMT "
						+ "			,SUM(CGST_AMT) AS CGST_AMT "
						+ "			,SUM(SGST_AMT) AS SGST_AMT "
						+ "			,SUM(CESS_AMT_SPECIFIC) AS CESS_AMT_SPECIFIC "
						+ "			,SUM(CESS_AMT_ADVALOREM) AS CESS_AMT_ADVALOREM "
						+ "		FROM ITM WHERE "
						+ mulCondition
						+ "		GROUP BY DOC_HEADER_ID "
						+ "			,ITM_TABLE_SECTION "
						+ "			,SUPPLY_TYPE "
						+ "			,ITM_TAX_DOC_TYPE "
						+ "			,TAX_RATE "
						+ "		) ITM ON HDR.ID = ITM.DOC_HEADER_ID "
						+ "	WHERE ITM.ITM_TAX_DOC_TYPE "
						+ "		 IN ( "
						+ "			'B2B' "
						+ "			,'B2BA' "
						+ "			,'B2CL' "
						+ "			,'B2CLA' "
						+ "			,'EXPORTS' "
						+ "			,'EXPORTS-A' "
						+ "			,'CDNR' "
						+ "			,'CDNRA' "
						+ "			,'CDNUR' "
						+ "			,'CDNUR-EXPORTS' "
						+ "			,'CDNUR-B2CL' "
						+ "			,'CDNURA' "
						+ "			,'B2CS' "
						+ "			,'AT' "
						+ "			,'TXP' "
						+ "			) "
						+ "	 "
						+ "	UNION ALL "
						+ "	 "
						+ "	SELECT SUPPLIER_GSTIN "
						+ "		,RETURN_PERIOD "
						+ "		,CASE "
						+ "			WHEN IS_AMENDMENT = FALSE "
						+ "				THEN 'B2CS' "
						+ "			ELSE 'B2CSA' "
						+ "			END AS TAX_DOC_TYPE "
						+ "		,(Ifnull(SUPPLIER_GSTIN, '') || '|' || Ifnull(RETURN_PERIOD, '') || '|' || Ifnull(TRAN_TYPE, '') || '|' || Ifnull(NEW_POS, 9999) || '|' || Ifnull(NEW_ECOM_GSTIN, '') || '|' || Ifnull(NEW_RATE, 9999)) AS KEY "
						+ "		,Ifnull((NEW_TAXABLE_VALUE), 0) AS SUPPLIES "
						+ "		,Ifnull((IGST_AMT), 0) AS IGST "
						+ "		,Ifnull((CGST_AMT), 0) AS CGST "
						+ "		,Ifnull((SGST_AMT), 0) AS SGST "
						+ "		,Ifnull((CESS_AMT), 0) AS CESS "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SENT_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_NOT_SENT_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_NOT_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN GSTN_ERROR = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_ERROR_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) TOTAL_COUNT_IN_ASP "
						+ "		,MODIFIED_ON "
						+ "	FROM GSTR1_PROCESSED_B2CS "
						+ "	WHERE IS_DELETE = FALSE "
						+ conditionVertical
						+ "	UNION ALL "
						+ "	 "
						+ "	SELECT SUPPLIER_GSTIN "
						+ "		,RETURN_PERIOD "
						+ "		,CASE "
						+ "			WHEN IS_AMENDMENT = FALSE "
						+ "				THEN 'ADV REC' "
						+ "			ELSE 'ADV REC-A' "
						+ "			END AS TAX_DOC_TYPE "
						+ "		,( "
						+ "			Ifnull(SUPPLIER_GSTIN, '') || '|' || Ifnull(RETURN_PERIOD, '') || '|' || ( "
						+ "				CASE "
						+ "					WHEN TRAN_TYPE IN ( "
						+ "							'ZL65' "
						+ "							,'L65' "
						+ "							,'zl65' "
						+ "							,'l65' "
						+ "							,'zL65' "
						+ "							,'Zl65' "
						+ "							) "
						+ "						THEN 'L65' "
						+ "					WHEN ( "
						+ "							TRAN_TYPE IN ( "
						+ "								'Z' "
						+ "								,'N' "
						+ "								,'' "
						+ "								,'z' "
						+ "								,'n' "
						+ "								) "
						+ "							OR TRAN_TYPE IS NULL "
						+ "							) "
						+ "						THEN 'N' "
						+ "					END "
						+ "				) || '|' || Ifnull(NEW_POS, 9999) "
						+ "			) AS KEY "
						+ "		,Ifnull((NEW_GROSS_ADV_RECEIVED), 0) AS SUPPLIES "
						+ "		,Ifnull((IGST_AMT), 0) AS IGST "
						+ "		,Ifnull((CGST_AMT), 0) AS CGST "
						+ "		,Ifnull((SGST_AMT), 0) AS SGST "
						+ "		,Ifnull((CESS_AMT), 0) AS CESS "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SENT_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_NOT_SENT_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_NOT_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN GSTN_ERROR = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_ERROR_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) TOTAL_COUNT_IN_ASP "
						+ "		,MODIFIED_ON MODIFIED_ON "
						+ "	FROM GSTR1_PROCESSED_ADV_RECEIVED "
						+ "	WHERE IS_DELETE = FALSE "
						+ conditionVertical
						+ "	UNION ALL "
						+ "	 "
						+ "	SELECT SUPPLIER_GSTIN "
						+ "		,RETURN_PERIOD "
						+ "		,CASE "
						+ "			WHEN IS_AMENDMENT = FALSE "
						+ "				THEN 'ADV ADJ' "
						+ "			ELSE 'ADV ADJ-A' "
						+ "			END AS TAX_DOC_TYPE "
						+ "		,( "
						+ "			Ifnull(SUPPLIER_GSTIN, '') || '|' || Ifnull(RETURN_PERIOD, '') || '|' || ( "
						+ "				CASE "
						+ "					WHEN TRAN_TYPE IN ( "
						+ "							'ZL65' "
						+ "							,'L65' "
						+ "							,'zl65' "
						+ "							,'l65' "
						+ "							,'zL65' "
						+ "							,'Zl65' "
						+ "							) "
						+ "						THEN 'L65' "
						+ "					WHEN ( "
						+ "							TRAN_TYPE IN ( "
						+ "								'Z' "
						+ "								,'N' "
						+ "								,'' "
						+ "								,'z' "
						+ "								,'n' "
						+ "								) "
						+ "							OR TRAN_TYPE IS NULL "
						+ "							) "
						+ "						THEN 'N' "
						+ "					END "
						+ "				) || '|' || Ifnull(NEW_POS, 9999) "
						+ "			) AS KEY "
						+ "		,Ifnull((NEW_GROSS_ADV_ADJUSTED), 0) AS SUPPLIES "
						+ "		,Ifnull((IGST_AMT), 0) AS IGST "
						+ "		,Ifnull((CGST_AMT), 0) AS CGST "
						+ "		,Ifnull((SGST_AMT), 0) AS SGST "
						+ "		,Ifnull((CESS_AMT), 0) AS CESS "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SENT_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_NOT_SENT_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_NOT_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN GSTN_ERROR = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) GSTN_ERROR_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END "
						+ "			) TOTAL_COUNT_IN_ASP "
						+ "		,MODIFIED_ON "
						+ "	FROM GSTR1_PROCESSED_ADV_ADJUSTMENT "
						+ "	WHERE IS_DELETE = FALSE "
						+ conditionVertical
						+ "	UNION ALL "
						+ "	 "
						+ "	SELECT SUPPLIER_GSTIN "
						+ "		,RETURN_PERIOD "
						+ "		,'NILEXTNON' AS TAX_DOC_TYPE "
						+ "		,NULL AS KEY "
						+ "		,IFNULL(SUM(NIL_RATED_SUPPLIES), 0) + IFNULL(SUM(EXMPTED_SUPPLIES), 0) + IFNULL(SUM(NON_GST_SUPPLIES), 0) AS SUPPLIES "
						+ "		,0 AS IGST "
						+ "		,0 AS CGST "
						+ "		,0 AS SGST "
						+ "		,0 AS CESS "
						+ "		,COUNT(CASE "
						+ "				WHEN IS_SENT_TO_GSTN = FALSE "
						+ "					AND HDR.IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END) GSTN_NOT_SENT_COUNT "
						+ "		,COUNT(CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
						+ "					AND HDR.IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END) GSTN_SAVED_COUNT "
						+ "		,COUNT(CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
						+ "					AND HDR.IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END) GSTN_NOT_SAVED_COUNT "
						+ "		,COUNT(CASE "
						+ "				WHEN GSTN_ERROR = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END) GSTN_ERROR_COUNT "
						+ "		,COUNT(CASE "
						+ "				WHEN HDR.IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE NULL "
						+ "				END) TOTAL_COUNT_IN_ASP "
						+ "		,MAX(HDR.MODIFIED_ON) MODIFIED_ON "
						+ "	FROM GSTR1_USERINPUT_NILEXTNON HDR "
						+ "	WHERE IS_DELETE = FALSE "
						+ nillCondition
						+ "	GROUP BY SUPPLIER_GSTIN "
						+ "		,RETURN_PERIOD "
						+ "		,DOC_KEY "
						+ "	 "
						+ "	UNION ALL "
						+ "	 "
						+ "	SELECT SUPPLIER_GSTIN "
						+ "		,HDR.RETURN_PERIOD "
						+ "		, ITM.ITM_TAX_DOC_TYPE AS TAX_DOC_TYPE "
						+ "		,HDR.ECOM_GSTIN AS KEY "
						+ "		,CASE "
						+ "			WHEN HDR.DOC_TYPE = 'CR' "
						+ "				THEN - 1 * IFNULL(ITM.TAXABLE_VALUE, 0) "
						+ "			WHEN HDR.DOC_TYPE IN ( "
						+ "					'DR' "
						+ "					,'INV' "
						+ "					) "
						+ "				THEN IFNULL(ITM.TAXABLE_VALUE, 0) "
						+ "			END AS SUPPLIES "
						+ "		,CASE "
						+ "			WHEN HDR.DOC_TYPE = 'CR' "
						+ "				THEN - 1 * IFNULL(ITM.IGST_AMT, 0) "
						+ "			WHEN HDR.DOC_TYPE IN ( "
						+ "					'DR' "
						+ "					,'INV' "
						+ "					) "
						+ "				THEN IFNULL(ITM.IGST_AMT, 0) "
						+ "			END AS IGST "
						+ "		,CASE "
						+ "			WHEN HDR.DOC_TYPE = 'CR' "
						+ "				THEN - 1 * IFNULL(ITM.CGST_AMT, 0) "
						+ "			WHEN HDR.DOC_TYPE IN ( "
						+ "					'DR' "
						+ "					,'INV' "
						+ "					) "
						+ "				THEN IFNULL(ITM.CGST_AMT, 0) "
						+ "			END AS CGST "
						+ "		,CASE "
						+ "			WHEN HDR.DOC_TYPE = 'CR' "
						+ "				THEN - 1 * IFNULL(ITM.SGST_AMT, 0) "
						+ "			WHEN HDR.DOC_TYPE IN ( "
						+ "					'DR' "
						+ "					,'INV' "
						+ "					) "
						+ "				THEN IFNULL(ITM.SGST_AMT, 0) "
						+ "			END AS SGST "
						+ "		,CASE "
						+ "			WHEN HDR.DOC_TYPE = 'CR' "
						+ "				THEN - 1 * (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0)) "
						+ "			WHEN HDR.DOC_TYPE IN ( "
						+ "					'DR' "
						+ "					,'INV' "
						+ "					) "
						+ "				THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
						+ "			END AS CESS "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SENT_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE 0 "
						+ "				END "
						+ "			) AS GSTN_NOT_SENT_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE 0 "
						+ "				END "
						+ "			) AS GSTN_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_SAVED_TO_GSTN = FALSE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE 0 "
						+ "				END "
						+ "			) AS GSTN_NOT_SAVED_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN GSTN_ERROR = TRUE "
						+ "					AND IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE 0 "
						+ "				END "
						+ "			) AS GSTN_ERROR_COUNT "
						+ "		,( "
						+ "			CASE "
						+ "				WHEN IS_DELETE = FALSE "
						+ "					THEN 1 "
						+ "				ELSE 0 "
						+ "				END "
						+ "			) AS TOTAL_COUNT_IN_ASP "
						+ "		,MAX(HDR.MODIFIED_ON) OVER () AS MODIFIED_ON "
						+ "	FROM HDR "
						+ "	INNER JOIN ( "
						+ "		SELECT ITM_TABLE_SECTION "
						+ "			,SUPPLY_TYPE "
						+ "			,ITM_TAX_DOC_TYPE "
						+ "			,SUM(TAXABLE_VALUE) AS TAXABLE_VALUE "
						+ "			,DOC_HEADER_ID "
						+ "			,TAX_RATE "
						+ "			,SUM(IGST_AMT) AS IGST_AMT "
						+ "			,SUM(CGST_AMT) AS CGST_AMT "
						+ "			,SUM(SGST_AMT) AS SGST_AMT "
						+ "			,SUM(CESS_AMT_SPECIFIC) AS CESS_AMT_SPECIFIC "
						+ "			,SUM(CESS_AMT_ADVALOREM) AS CESS_AMT_ADVALOREM "
						+ "		FROM ITM "
						+ "		WHERE  "
						+ mulCondition
						+ "		GROUP BY DOC_HEADER_ID "
						+ "			,ITM_TABLE_SECTION "
						+ "			,SUPPLY_TYPE "
						+ "			,ITM_TAX_DOC_TYPE "
						+ "			,TAX_RATE "
						+ "		) ITM ON HDR.ID = ITM.DOC_HEADER_ID "
						+ "	WHERE HDR.SUPPLY_TYPE <> 'CAN' "
						+ "		AND IS_PROCESSED = TRUE "
						+ "		AND ( "
						+ "			 ITM.ITM_TABLE_SECTION IN ('14(ii)') "
						+ "			AND HDR.TCS_FLAG = 'E' "
						+ "			) "
						+ "	 "
						+ "	UNION ALL "
						+ "	 "
						+ "	SELECT SUPPLIER_GSTIN "
						+ "		,RETURN_PERIOD "
						+ "		,TAX_DOC_TYPE "
						+ "		,DOC_KEY_15 "
						+ "		,SUPPLIES "
						+ "		,IGST "
						+ "		,CGST "
						+ "		,SGST "
						+ "		,CESS "
						+ "		,GSTN_NOT_SENT_COUNT "
						+ "		,GSTN_SAVED_COUNT "
						+ "		,GSTN_NOT_SAVED_COUNT "
						+ "		,GSTN_ERROR_COUNT "
						+ "		,TOTAL_COUNT_IN_ASP "
						+ "		,MODIFIED_ON "
						+ "	FROM ( "
						+ "		SELECT HDR.SUPPLIER_GSTIN "
						+ "			,HDR.RETURN_PERIOD "
						+ "			,ITM.ITM_TAX_DOC_TYPE AS TAX_DOC_TYPE "
						+ "			, ITM.ITM_TABLE_SECTION AS TABLE_SECTION "
						+ "			,CASE "
						+ "				WHEN ITM.ITM_TABLE_SECTION IN ( "
						+ "						'15(i)' "
						+ "						,'15(iii)' "
						+ "						) "
						+ "					THEN DOC_KEY "
						+ "				WHEN ITM.ITM_TABLE_SECTION = '15(ii)' "
						+ "					THEN IFNULL(HDR.ECOM_GSTIN, '') || '|' || IFNULL(HDR.POS, '') || '|' || IFNULL(ITM.TAX_RATE, 9999) "
						+ "				WHEN HDR.TABLE_SECTION = '15(iv)' "
						+ "					THEN IFNULL(HDR.POS, '') || '|' || IFNULL(ITM.TAX_RATE, 9999) "
						+ "				ELSE HDR.ECOM_GSTIN "
						+ "				END AS DOC_KEY_15 "
						+ "			,CASE "
						+ "				WHEN HDR.DOC_TYPE = 'CR' "
						+ "					THEN - 1 * IFNULL(ITM.TAXABLE_VALUE, 0) "
						+ "				WHEN HDR.DOC_TYPE IN ( "
						+ "						'DR' "
						+ "						,'INV' "
						+ "						) "
						+ "					THEN IFNULL(ITM.TAXABLE_VALUE, 0) "
						+ "				END AS SUPPLIES "
						+ "			,CASE "
						+ "				WHEN HDR.DOC_TYPE = 'CR' "
						+ "					THEN - 1 * IFNULL(ITM.IGST_AMT, 0) "
						+ "				WHEN HDR.DOC_TYPE IN ( "
						+ "						'DR' "
						+ "						,'INV' "
						+ "						) "
						+ "					THEN IFNULL(ITM.IGST_AMT, 0) "
						+ "				END AS IGST "
						+ "			,CASE "
						+ "				WHEN HDR.DOC_TYPE = 'CR' "
						+ "					THEN - 1 * IFNULL(ITM.CGST_AMT, 0) "
						+ "				WHEN HDR.DOC_TYPE IN ( "
						+ "						'DR' "
						+ "						,'INV' "
						+ "						) "
						+ "					THEN IFNULL(ITM.CGST_AMT, 0) "
						+ "				END AS CGST "
						+ "			,CASE "
						+ "				WHEN HDR.DOC_TYPE = 'CR' "
						+ "					THEN - 1 * IFNULL(ITM.SGST_AMT, 0) "
						+ "				WHEN HDR.DOC_TYPE IN ( "
						+ "						'DR' "
						+ "						,'INV' "
						+ "						) "
						+ "					THEN IFNULL(ITM.SGST_AMT, 0) "
						+ "				END AS SGST "
						+ "			,CASE "
						+ "				WHEN HDR.DOC_TYPE = 'CR' "
						+ "					THEN - 1 * (IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0)) "
						+ "				WHEN HDR.DOC_TYPE IN ( "
						+ "						'DR' "
						+ "						,'INV' "
						+ "						) "
						+ "					THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0) + IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
						+ "				END AS CESS "
						+ "			,( "
						+ "				CASE "
						+ "					WHEN IS_SENT_TO_GSTN = FALSE "
						+ "						AND IS_DELETE = FALSE "
						+ "						THEN 1 "
						+ "					ELSE 0 "
						+ "					END "
						+ "				) AS GSTN_NOT_SENT_COUNT "
						+ "			,( "
						+ "				CASE "
						+ "					WHEN IS_SAVED_TO_GSTN = TRUE "
						+ "						AND IS_DELETE = FALSE "
						+ "						THEN 1 "
						+ "					ELSE 0 "
						+ "					END "
						+ "				) AS GSTN_SAVED_COUNT "
						+ "			,( "
						+ "				CASE "
						+ "					WHEN IS_SAVED_TO_GSTN = FALSE "
						+ "						AND IS_DELETE = FALSE "
						+ "						THEN 1 "
						+ "					ELSE 0 "
						+ "					END "
						+ "				) AS GSTN_NOT_SAVED_COUNT "
						+ "			,( "
						+ "				CASE "
						+ "					WHEN GSTN_ERROR = TRUE "
						+ "						AND IS_DELETE = FALSE "
						+ "						THEN 1 "
						+ "					ELSE 0 "
						+ "					END "
						+ "				) AS GSTN_ERROR_COUNT "
						+ "			,( "
						+ "				CASE "
						+ "					WHEN IS_DELETE = FALSE "
						+ "						THEN 1 "
						+ "					ELSE 0 "
						+ "					END "
						+ "				) AS TOTAL_COUNT_IN_ASP "
						+ "			,MAX(HDR.MODIFIED_ON) OVER () AS MODIFIED_ON "
						+ "		FROM HDR "
						+ "		INNER JOIN ( "
						+ "			SELECT ITM_TABLE_SECTION "
						+ "				,SUPPLY_TYPE "
						+ "				,ITM_TAX_DOC_TYPE "
						+ "				,SUM(TAXABLE_VALUE) AS TAXABLE_VALUE "
						+ "				,DOC_HEADER_ID "
						+ "				,TAX_RATE "
						+ "				,SUM(IGST_AMT) AS IGST_AMT "
						+ "				,SUM(CGST_AMT) AS CGST_AMT "
						+ "				,SUM(SGST_AMT) AS SGST_AMT "
						+ "				,SUM(CESS_AMT_SPECIFIC) AS CESS_AMT_SPECIFIC "
						+ "				,SUM(CESS_AMT_ADVALOREM) AS CESS_AMT_ADVALOREM "
						+ "			FROM ITM WHERE "
						+ mulCondition
						+ "			GROUP BY DOC_HEADER_ID "
						+ "				,ITM_TABLE_SECTION "
						+ "				,SUPPLY_TYPE "
						+ "				,ITM_TAX_DOC_TYPE "
						+ "				,TAX_RATE "
						+ "			) ITM ON HDR.ID = ITM.DOC_HEADER_ID "
						+ "		WHERE IS_PROCESSED = TRUE "
						+ "			AND  ITM.ITM_TABLE_SECTION "
						+ "			 IN ( "
						+ "				'15(i)' "
						+ "				,'15(ii)' "
						+ "				,'15(iii)' "
						+ "				,'15(iv)' "
						+ "				) "
						+ "		) "
						+ "	) "
						+ groupByCondition
						+ " GROUP BY SUPPLIER_GSTIN "
						+ "	,RETURN_PERIOD "
						+ "	,TAX_DOC_TYPE "
						+ "ORDER BY LENGTH(TAX_DOC_TYPE)";
				
			
			

		}
		
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 Query 6 from database is-->" + bufferString);
			LOGGER.debug("Outward query from database is-->" + bufferString);
		}
		return queryStr;
	}

	public Integer getTabStatus(List<String> gstinList, int taxPeriod) {

		StringBuilder queryBuilder = new StringBuilder();
		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			queryBuilder.append(" AND GSTIN IN (:gstinList) ");
		}
		String buildQuery = queryBuilder.toString();

		String queryStr = "SELECT (CASE WHEN IS_NIL_UI = FALSE THEN 1 ELSE 2 END) "
				+ "FROM GSTN_USER_REQUEST WHERE RETURN_TYPE='GSTR1' "
				+ "AND REQUEST_TYPE='SAVE' " + buildQuery
				+ " ORDER BY ID DESC LIMIT 1";

		Query Q = entityManager.createNativeQuery(queryStr);
		if (taxPeriod != 0) {
			Q.setParameter("taxPeriod", taxPeriod);
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			Q.setParameter("gstinList", gstinList);
		}

		@SuppressWarnings("unchecked")
		List<Object> resultSet = Q.getResultList();

		Integer status = 1;
		if (resultSet != null && !resultSet.isEmpty()) {
			status = (Integer) resultSet.get(0);
		}
		return status;

	}

}
