package com.ey.advisory.app.data.daos.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.EinvoiceProcessedReqDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EinvoicProcessedRecordsFetchDaoImpl")
public class EinvoicProcessedRecordsFetchDaoImpl {
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private EinvProcessedRecordsCommonUtil gstr1ProcessedRecordsCommonUtil;

	public List<Gstr1ProcessedRecordsRespDto> loadEinvProcessedRecords(
			EinvoiceProcessedReqDto gstr1ProcessedRecordsReqDto) {

		List<Long> entityId = gstr1ProcessedRecordsReqDto.getEntityId();
		String taxPeriodFrom = gstr1ProcessedRecordsReqDto.getTaxPeriodFrom();
		String taxPeriodTo = gstr1ProcessedRecordsReqDto.getTaxPeriodTo();
		List<String> gstinAsList = new ArrayList<>();

		List<String> tableType = gstr1ProcessedRecordsReqDto.getTableType();
		// List<String> docType = gstr1ProcessedRecordsReqDto.getDocType();

		LocalDate docFromDate1 = gstr1ProcessedRecordsReqDto.getDocFromDate();
		
		String docFromDate = null;
		if(docFromDate1 != null) {
		 docFromDate = docFromDate1.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
		
		/*LocalDate docFromDate = LocalDate.parse(docFromDate1.toString(),
                 DateUtil.SUPPORTED_DATE_FORMAT2);
		*/
	
		LocalDate docToDate1 = gstr1ProcessedRecordsReqDto.getDocToDate();
		
		String docToDate = null;
		if(docToDate1 != null) {
		 docToDate = docToDate1.format(DateUtil.SUPPORTED_DATE_FORMAT2);
		}
	/*	LocalDate docToDate = LocalDate.parse(docToDate1.toString(),
                DateUtil.SUPPORTED_DATE_FORMAT2);
	*/	
		List<String> einvGenerated = gstr1ProcessedRecordsReqDto
				.getEINVGenerated();
		List<String> ewbGenerated = gstr1ProcessedRecordsReqDto
				.getEWBGenerated();
		List<Gstr1ProcessedRecordsRespDto> sortedGstinDtoList = new ArrayList<>();

		List<String> draftedGSTN = gstr1ProcessedRecordsReqDto
				.getAutoDraftedGSTN();

		Map<String, List<String>> dataSecAttrs = gstr1ProcessedRecordsReqDto
				.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1ProcessedRecordsFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}",
					gstr1ProcessedRecordsReqDto);
		}

		String sgstin = null;
		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					gstinAsList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}

		int taxPeriod1 = 0;
		int taxPeriod2 = 0;
		if (taxPeriodFrom != null && taxPeriodTo != null) {
			taxPeriod1 = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
			taxPeriod2 = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
		}

		try {

			String queryStr = createQueryString(entityId, gstinAsList,
					taxPeriod1, taxPeriod2, dataSecAttrs, tableType,
					docFromDate, docToDate, einvGenerated, ewbGenerated,
					draftedGSTN);
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("outQueryStr-->" + queryStr);
			}

			Query Q = entityManager.createNativeQuery(queryStr.toString());
			if (taxPeriod1 != 0 && taxPeriod2 != 0) {
				Q.setParameter("taxPeriodFrom", taxPeriod1);
				Q.setParameter("taxPeriodTo", taxPeriod2);
			}
			if (gstinAsList != null && gstinAsList.size() > 0
					&& !gstinAsList.contains("")) {
				Q.setParameter("sgstin", gstinAsList);
			}
			if (tableType != null && tableType.size() > 0
					&& !tableType.contains("")) {
				Q.setParameter("tableType", tableType);
			}
			/*
			 * if (docType != null && docType.size() > 0 &&
			 * !docType.contains("")) { Q.setParameter("docType", docType); }
			 */
			if (docFromDate != null && docToDate != null) {
				Q.setParameter("docFromDate", docFromDate);
				Q.setParameter("docToDate", docToDate);
			}

			List<Object[]> resultSet = Q.getResultList();
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("bufferString-------------------------->" + resultSet);
			}

			List<Gstr1ProcessedRecordsRespDto> outwardFinalList = gstr1ProcessedRecordsCommonUtil
					.convertEinvRecordsIntoObject(resultSet);

			Map<String, List<Gstr1ProcessedRecordsRespDto>> combinedDataMap = new HashMap<String, List<Gstr1ProcessedRecordsRespDto>>();

			gstr1ProcessedRecordsCommonUtil.createMapByGstinBasedOnType(
					combinedDataMap, outwardFinalList);

			List<Gstr1ProcessedRecordsRespDto> dataDtoList = new ArrayList<>();
			gstr1ProcessedRecordsCommonUtil
					.calculateDataByDocType(combinedDataMap, dataDtoList);

			gstr1ProcessedRecordsCommonUtil.fillTheDataFromDataSecAttr(
					dataDtoList, gstinAsList, taxPeriodFrom, taxPeriodTo);
			List<Gstr1ProcessedRecordsRespDto> gstinDtoList = new ArrayList<Gstr1ProcessedRecordsRespDto>();
			List<String> combinedGstinList = new ArrayList<>();
			if (sgstin != null && !sgstin.isEmpty() && gstinAsList != null
					&& gstinAsList.size() > 0) {
				combinedGstinList.addAll(gstinAsList);
			}
			if (!combinedGstinList.isEmpty() && combinedGstinList.size() > 0) {
				for (Gstr1ProcessedRecordsRespDto processedDto : dataDtoList) {
					if (combinedGstinList.contains(processedDto.getGstin())) {
						gstinDtoList.add(processedDto);
					}
				}
				sortedGstinDtoList = gstinDtoList.stream()
						.sorted(Comparator.comparing(
								Gstr1ProcessedRecordsRespDto::getGstin))
						.collect(Collectors.toList());

			}

		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}
		return gstr1ProcessedRecordsCommonUtil
				.convertCalcuDataToResp(sortedGstinDtoList);
	}

	public String createQueryString(List<Long> entityId, List<String> gstinList,
			int taxPeriodFrom, int taxPeriodTo,
			Map<String, List<String>> dataSecAttrs, List<String> tableType,
			String docFromDate, String docToDate,
			List<String> einvGenerated, List<String> ewbGenerated,
			List<String> draftedGSTN) {

		String einvGen = null;
		if (einvGenerated != null && einvGenerated.size() > 0) {
			einvGen = einvGenerated.get(0);
		}

		String ewbResp = null;
		if (ewbGenerated != null && ewbGenerated.size() > 0) {
			ewbResp = ewbGenerated.get(0);
		}
		String draftGstn = null;
		if (draftedGSTN != null && draftedGSTN.size() > 0) {

			draftGstn = draftedGSTN.get(0);

		}

		String sgstin = null;
		if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
				&& dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilderForB2bExp = new StringBuilder();
		StringBuilder queryBuilderForCdnrCdnur = new StringBuilder();
		StringBuilder queryBuilderTaxDoc = new StringBuilder();
		if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
			queryBuilderForB2bExp.append(
					" AND HDR.DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");
			queryBuilderForCdnrCdnur.append(
					" AND HDR.DERIVED_RET_PERIOD BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");
		}
		if (docFromDate != null && docToDate != null) {
			queryBuilderForB2bExp.append(
					" AND INV_DATE BETWEEN :docFromDate AND :docToDate ");
			queryBuilderForCdnrCdnur.append(
					" AND NOTE_DATE BETWEEN :docFromDate AND :docToDate ");
		}

		if (gstinList != null && gstinList.size() > 0) {
			queryBuilderForB2bExp.append(" AND HDR.GSTIN IN (:sgstin) ");
			queryBuilderForCdnrCdnur.append(" AND HDR.GSTIN IN (:sgstin) ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("YES")) {
			queryBuilderForB2bExp.append(" AND EINV_STATUS IN ('ACT') ");
			queryBuilderForCdnrCdnur.append(" AND EINV_STATUS IN ('ACT') ");
		}
		if (einvGen != null && einvGen.equalsIgnoreCase("NO")) {
			queryBuilderForB2bExp.append(" AND (EINV_STATUS NOT IN ('ACT') OR EINV_STATUS IS NULL) ");
			queryBuilderForCdnrCdnur.append(" AND (EINV_STATUS NOT IN ('ACT') OR EINV_STATUS IS NULL) ");
		}
		if (draftGstn != null && draftGstn.equalsIgnoreCase("YES")) {
			queryBuilderForB2bExp.append("AND AUTODFT IN ('Auto-populated') ");
			queryBuilderForCdnrCdnur.append("AND AUTODFT IN ('Auto-populated') ");
		}
		if (draftGstn != null && draftGstn.equalsIgnoreCase("NO")) {
			queryBuilderForB2bExp.append(" AND (AUTODFT NOT IN ('Auto-populated') OR AUTODFT IS NULL) ");
			queryBuilderForCdnrCdnur.append(" AND (AUTODFT NOT IN ('Auto-populated') OR AUTODFT IS NULL) ");
		}
		if (tableType != null && tableType.size() > 0) {
		//	queryBuilder.append(" AND TAX_DOC_TYPE IN (:tableType) ");
			queryBuilderTaxDoc.append(" WHERE TAX_DOC_TYPE IN (:tableType) ");
		}

		String conditionb2bExp = queryBuilderForB2bExp.toString().substring(4);
		String conditioncdnrCdnur = queryBuilderForCdnrCdnur.toString().substring(4);
		String conditionTaxDoc = queryBuilderTaxDoc.toString();

		String queryStr = "SELECT SUPPLIER_GSTIN,DERIVED_RET_PERIOD,"
				+ "TAX_DOC_TYPE,SUM(COUNT)COUNT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN TAXABLE_VALUE END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN TAXABLE_VALUE END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN TAXABLE_VALUE END),0) AS TAXABLE_VALUE,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN IGST_AMT END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN IGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN IGST_AMT END),0) AS IGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN CGST_AMT END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN CGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN CGST_AMT END),0) AS CGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN SGST_AMT END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN SGST_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN SGST_AMT END),0) AS SGST_AMT,"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('INV') THEN CESS_AMT END),0)+"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('D') THEN CESS_AMT END),0)-"
				+ "IFNULL(SUM(CASE WHEN DOC_TYPE IN ('C') THEN CESS_AMT END),0) AS CESS_AMT FROM ( "
				+ "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,"
				+ "'B2B' AS TAX_DOC_TYPE,'INV' AS DOC_TYPE,INV_DATE,EINV_STATUS,AUTODFT,"
				+ "COUNT( DISTINCT HDR.ID) AS COUNT,"
				+ "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
				+ "IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,"
				+ "IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT,"
				+ "IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT,"
				+ "IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT "
				+ "FROM GETGSTR1_EINV_B2B_HEADER HDR "
				+ "INNER JOIN  GETGSTR1_EINV_B2B_ITEM ITM "
				+ "ON HDR.ID = ITM.HEADER_ID AND "
				+ "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
				+ "ON HDR.BATCH_ID = BT.ID "
				+ "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
				+ "AND BT.GET_TYPE IN ('B2B','WEB_UPLOAD') AND "
				+ conditionb2bExp
                + "GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,INV_DATE,EINV_STATUS,AUTODFT "
                + "UNION ALL SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,'EXPORTS' AS TAX_DOC_TYPE,'INV' AS DOC_TYPE,"
                + "INV_DATE,EINV_STATUS,AUTODFT,COUNT( DISTINCT HDR.ID) AS COUNT,"
                + "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
                + "IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,0 AS CGST_AMT,"
                + "0 AS SGST_AMT,IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT "
                + "FROM GETGSTR1_EINV_EXP_HEADER HDR "
                + "INNER JOIN  GETGSTR1_EINV_EXP_ITEM ITM "
                + "ON HDR.ID = ITM.HEADER_ID AND "
                + "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
                + "LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
                + "ON HDR.BATCH_ID = BT.ID "
                + "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
                + "AND BT.GET_TYPE IN ('EXP','WEB_UPLOAD') AND "
                + conditionb2bExp 
                + "GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,HDR.DERIVED_RET_PERIOD,"
                + "INV_DATE,EINV_STATUS,AUTODFT "
                + "UNION ALL "
                + "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,'CDNR' AS TAX_DOC_TYPE,"
                + "NOTE_TYPE AS DOC_TYPE,NOTE_DATE AS INV_DATE,EINV_STATUS,AUTODFT,"
                + "COUNT( DISTINCT HDR.ID) AS COUNT,"
                + "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
                + "IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,"
                + "IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT,"
                + "IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT,"
                + "IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT "
                + "FROM GETGSTR1_EINV_CDNR_HEADER HDR "
                + "INNER JOIN  GETGSTR1_EINV_CDNR_ITEM ITM "
                + "ON HDR.ID = ITM.HEADER_ID AND "
                + "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
                + "LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
                + "ON HDR.BATCH_ID = BT.ID "
                + "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
                + "AND BT.GET_TYPE  IN ('CDNR','WEB_UPLOAD') AND "
                + conditioncdnrCdnur 
                + "GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,NOTE_DATE,EINV_STATUS,AUTODFT ,NOTE_TYPE "
                + "UNION ALL  "
                + "SELECT HDR.GSTIN SUPPLIER_GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,'CDNUR' AS TAX_DOC_TYPE,"
                + "NOTE_TYPE AS DOC_TYPE,NOTE_DATE AS INV_DATE,EINV_STATUS,AUTODFT,"
                + "COUNT( DISTINCT HDR.ID) AS COUNT,"
                + "IFNULL(SUM(ITM.TAXABLE_VALUE),0) AS TAXABLE_VALUE,"
                + "IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT,0 AS CGST_AMT,"
                + "0 AS SGST_AMT,IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT "
                + "FROM GETGSTR1_EINV_CDNUR_HEADER HDR "
                + "INNER JOIN  GETGSTR1_EINV_CDNUR_ITEM ITM "
                + "ON HDR.ID = ITM.HEADER_ID AND "
                + "HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
                + "LEFT OUTER JOIN GETANX1_BATCH_TABLE BT "
                + "ON HDR.BATCH_ID = BT.ID "
                + "WHERE HDR.IS_DELETE = FALSE AND BT.IS_DELETE = FALSE "
                + "AND BT.GET_TYPE IN ('CDNUR','WEB_UPLOAD') AND "
                + conditioncdnrCdnur
                + "GROUP BY HDR.GSTIN,HDR.RETURN_PERIOD,"
                + "HDR.DERIVED_RET_PERIOD,NOTE_DATE,EINV_STATUS,AUTODFT ,NOTE_TYPE) "
                +  conditionTaxDoc 
                + "GROUP BY SUPPLIER_GSTIN,DERIVED_RET_PERIOD, TAX_DOC_TYPE";
		return queryStr;
	}

}
