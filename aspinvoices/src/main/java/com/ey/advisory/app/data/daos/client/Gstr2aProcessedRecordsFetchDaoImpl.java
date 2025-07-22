package com.ey.advisory.app.data.daos.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedRecordsRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Gstr2aProcessedRecordsFetchDaoImpl")
public class Gstr2aProcessedRecordsFetchDaoImpl
		implements Gstr2aProcessedRecordsFetchDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2aProcessedRecordsFetchDaoImpl.class);
	private static final String GSTIN = "GSTIN";

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private Gstr2aProcessedRecordsCommonUtil gstr2aPRCommonUtil;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;
	
	  @Autowired
	  @Qualifier("GSTNDetailRepository")
	  private GSTNDetailRepository gSTNDetailRepository;

	public List<Gstr2aProcessedRecordsRespDto> loadGstr2aProcessedRecords(
			Gstr2AProcessedRecordsReqDto gstr2aPRReqDto) {

		String taxPeriod = gstr2aPRReqDto.getRetunPeriod();
		String fromPeriod = gstr2aPRReqDto.getFromPeriod();
		String toPeriod = gstr2aPRReqDto.getToPeriod();
		List<String> tableType = gstr2aPRReqDto.getTableType();
		List<String> tableTypeUpperCase = tableType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

		List<String> docType = gstr2aPRReqDto.getDocType();
		List<String> docTypeUpperCase = docType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());
		Map<String, List<String>> dataSecAttrs = gstr2aPRReqDto
				.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		String cgstin = null;
		List<String> regGstinList = new ArrayList<>();

		List<Gstr2aProcessedRecordsRespDto> apiSummaryResDtos = Lists
				.newArrayList();

		try {

			if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
					&& dataSecAttrs.size() > 0) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase(GSTIN)) {
						cgstin = key;
						gstinList = dataSecAttrs.get(GSTIN);
					}
				}
			}
			
			/*String gst="30ABOPS9546G1Z3";
			regGstinList.add(gst);*/
			regGstinList= gSTNDetailRepository.filterGstinBasedOnRegType(gstinList);
			StringBuilder queryStr = createQueryString(regGstinList, fromPeriod,
					toPeriod, tableTypeUpperCase, docTypeUpperCase);

			Query outquery = entityManager
					.createNativeQuery(queryStr.toString());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("outquery --> {}", outquery);
			}

			if (CollectionUtils.isNotEmpty(regGstinList)) {
				outquery.setParameter("gstinList", regGstinList);
			}

			if (StringUtils.isNotBlank(fromPeriod)
					&& StringUtils.isNotBlank(toPeriod)) {
				outquery.setParameter("fromPeriod",
						GenUtil.convertTaxPeriodToInt(fromPeriod));
				outquery.setParameter("toPeriod",
						GenUtil.convertTaxPeriodToInt(toPeriod));
			}

			if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
				outquery.setParameter("tableTypeUpperCase", tableTypeUpperCase);
			}
			if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
				outquery.setParameter("docTypeUpperCase", docTypeUpperCase);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = outquery.getResultList();
			List<Gstr2aProcessedRecordsRespDto> outwardFinalList = gstr2aPRCommonUtil
					.convertGstr2aRecordsIntoObjesct(list, gstr2aPRReqDto,
							regGstinList);

			Map<String, List<Gstr2aProcessedRecordsRespDto>> combinedDataMap = Maps
					.newHashMap();

			gstr2aPRCommonUtil.createMapByGstinBasedOnType(combinedDataMap,
					outwardFinalList);

			List<Gstr2aProcessedRecordsRespDto> dataDtoList = new ArrayList<>();
			gstr2aPRCommonUtil.calculateDataByDocType(combinedDataMap,
					dataDtoList);

			gstr2aPRCommonUtil.fillTheDataFromDataSecAttr(gstr2aPRReqDto,
					dataDtoList, regGstinList, taxPeriod);
			List<Gstr2aProcessedRecordsRespDto> gstinDtoList = Lists
					.newArrayList();
			List<String> combinedGstinList = new ArrayList<>();
			if (cgstin != null && !cgstin.isEmpty() && regGstinList != null
					&& regGstinList.size() > 0) {
				combinedGstinList.addAll(regGstinList);
			}
			if (!combinedGstinList.isEmpty()) {
				for (Gstr2aProcessedRecordsRespDto processedDto : dataDtoList) {
					if (combinedGstinList.contains(processedDto.getGstin())) {
						gstinDtoList.add(processedDto);
					}
				}
				List<Gstr2aProcessedRecordsRespDto> sortedGstinDtoList = gstinDtoList
						.stream()
						.sorted(Comparator.comparing(
								Gstr2aProcessedRecordsRespDto::getGstin))
						.collect(Collectors.toList());
				return Gstr2aProcessedRecordsCommonUtil
						.convertCalcuDataToResp(sortedGstinDtoList);
			}

			List<Gstr2aProcessedRecordsRespDto> sortedGstinDtoList = dataDtoList
					.stream()
					.sorted(Comparator
							.comparing(Gstr2aProcessedRecordsRespDto::getGstin))
					.collect(Collectors.toList());
			apiSummaryResDtos.addAll(sortedGstinDtoList);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Final list from dao is -> {}", apiSummaryResDtos);
			}
		} catch (Exception e) {
			throw new AppException("Error in data process -> {}", e);
		}

		return Gstr2aProcessedRecordsCommonUtil
				.convertCalcuDataToResp(apiSummaryResDtos);

	}

	private StringBuilder createQueryString(List<String> gstinList,
			String fromPeriod, String toPeriod, List<String> tableTypeUpperCase,
			List<String> docTypeUpperCase) {

		StringBuilder queryBuilder = new StringBuilder();
		
		StringBuilder queryBuilder1 = new StringBuilder();
		
		StringBuilder queryBuilder2 = new StringBuilder();
		
		StringBuilder queryBuilder3 = new StringBuilder();

		if (StringUtils.isNotBlank(fromPeriod)
				&& StringUtils.isNotBlank(toPeriod)) {
			queryBuilder.append(
					" WHERE DERIVED_RET_PERIOD BETWEEN :fromPeriod AND :toPeriod ");
			
			queryBuilder1.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN :fromPeriod AND :toPeriod ");
			queryBuilder2.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN :fromPeriod AND :toPeriod ");
			queryBuilder3.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN :fromPeriod AND :toPeriod ");
			

		}
		if (CollectionUtils.isNotEmpty(gstinList)) {
			queryBuilder.append(" AND CUST_GSTIN IN (:gstinList) ");
			queryBuilder1.append(" AND HDR.CGSTIN IN  (:gstinList) ");
			queryBuilder2.append(" AND HDR.CTIN IN  (:gstinList) ");
			queryBuilder3.append(" AND HDR.GSTIN IN  (:gstinList) ");
			
		}
		if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
			queryBuilder.append(" AND TAX_DOC_TYPE IN (:tableTypeUpperCase) ");

		}
		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			queryBuilder.append(" AND DOC_TYPE IN (:docTypeUpperCase) ");

		}

		String condition = queryBuilder.toString();
		String condition1 = queryBuilder1.toString();
		String condition2 = queryBuilder2.toString();
		String condition3 = queryBuilder3.toString();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"condition for applying filter--------------------------> {}",
					condition);
		}
		StringBuilder bufferString = new StringBuilder();
		bufferString.append(
				"SELECT CUST_GSTIN,MAX(LAST_UPDATED) LAST_UPDATED,DERIVED_RET_PERIOD, TAX_DOC_TYPE,DOC_TYPE, ");
		bufferString.append(
				"SUM(COUNT) COUNT,SUM(INV_VALUE) INV_VALUE,SUM(TAXABLE_VALUE)TAXABLE_VALUE,SUM(TOT_TAX) TOT_TAX, ");
		bufferString.append(
				"SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT, SUM(SGST_AMT)SGST_AMT,SUM(CESS_AMT)CESS_AMT ");
		bufferString.append(
				"FROM ( SELECT CGSTIN AS CUST_GSTIN, HDR.DERIVED_RET_PERIOD, 'B2B' AS TAX_DOC_TYPE, 'INV' ");
		bufferString.append(
				"AS DOC_TYPE, MAX(CREATED_ON) LAST_UPDATED, COUNT(DISTINCT HDR.ID) AS COUNT, ");
		bufferString.append(
				"SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)");
		bufferString.append(
				"+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE, IFNULL(SUM(ITM.TAX_VALUE ),0) AS TAXABLE_VALUE, ");
		bufferString.append(
				"(IFNULL(SUM(ITM.IGST_AMT),0)+ IFNULL(SUM(ITM.CGST_AMT),0) +IFNULL (SUM(ITM.SGST_AMT),0) ");
		bufferString.append(
				"+IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL (SUM(ITM.CESS_AMT),0) AS CESS_AMT FROM GETGSTR2A_B2B_HEADER HDR INNER JOIN ");
		bufferString.append(
				"GETGSTR2A_B2B_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND ");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "+ condition1);
		bufferString.append(
				" WHERE IS_DELETE=FALSE GROUP BY CGSTIN, HDR.DERIVED_RET_PERIOD UNION ALL SELECT CGSTIN AS CUST_GSTIN, HDR.DERIVED_RET_PERIOD, ");
		bufferString.append(
				"'B2BA' AS TAX_DOC_TYPE, 'RNV' AS DOC_TYPE, MAX(CREATED_ON) LAST_UPDATED, ");
		bufferString.append(
				"COUNT(DISTINCT HDR.ID) AS COUNT, SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) ");
		bufferString.append(
				"+IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE, ");
		bufferString.append(
				"IFNULL(SUM(ITM.TAX_VALUE ),0) AS TAXABLE_VALUE, (IFNULL(SUM(ITM.IGST_AMT),0) ");
		bufferString.append(
				"+IFNULL(SUM(ITM.CGST_AMT),0) +IFNULL (SUM(ITM.SGST_AMT),0) +IFNULL(SUM(ITM.CESS_AMT),0)) ");
		bufferString.append(
				"AS TOT_TAX, IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT ,IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT ,");
		bufferString.append(
				"IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT ");
		bufferString.append(
				"FROM GETGSTR2A_B2BA_HEADER HDR INNER JOIN GETGSTR2A_B2BA_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD " + condition1);
		bufferString.append(
				" WHERE IS_DELETE=FALSE GROUP BY CGSTIN,HDR.DERIVED_RET_PERIOD ");
		bufferString.append(" UNION ALL ");
		
		bufferString.append(" SELECT CGSTIN AS CUST_GSTIN, HDR.DERIVED_RET_PERIOD, 'ECOM' AS TAX_DOC_TYPE, 'INV' AS DOC_TYPE,  ");
		bufferString.append(" MAX(CREATED_ON) LAST_UPDATED, COUNT(DISTINCT HDR.ID) AS COUNT, ");
		bufferString.append(" SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE, ");
		bufferString.append(" IFNULL(SUM(ITM.TAX_VALUE ),0) AS TAXABLE_VALUE, ");
		bufferString.append(" (IFNULL(SUM(ITM.IGST_AMT),0)+ IFNULL(SUM(ITM.CGST_AMT),0) +IFNULL (SUM(ITM.SGST_AMT),0) +IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, ");
		bufferString.append(" IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, ");
		bufferString.append(" IFNULL (SUM(ITM.CESS_AMT),0) AS CESS_AMT  ");
		bufferString.append(" FROM GETGSTR2A_ECOM_HEADER HDR INNER JOIN GETGSTR2A_ECOM_ITEM ITM ON HDR.ID = ITM.HEADER_ID  ");
		bufferString.append(" AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD   " + condition1);
		//bufferString.append(" AND HDR.DERIVED_RET_PERIOD BETWEEN 202310 AND 202310  AND HDR.CGSTIN IN  ('29AAAPH9357H1A2') ");
		bufferString.append("  WHERE IS_DELETE=FALSE  ");
		bufferString.append(" GROUP BY CGSTIN, HDR.DERIVED_RET_PERIOD  ");
		bufferString.append(" UNION ALL  ");
		  
		bufferString.append( " SELECT CGSTIN AS CUST_GSTIN, HDR.DERIVED_RET_PERIOD, 'ECOMA' AS TAX_DOC_TYPE, 'RNV' AS DOC_TYPE, MAX(CREATED_ON) LAST_UPDATED,COUNT(DISTINCT HDR.ID) AS COUNT,  ");
		bufferString.append( " SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE, ");
		bufferString.append( " IFNULL(SUM(ITM.TAX_VALUE ),0) AS TAXABLE_VALUE, ");
		bufferString.append( " (IFNULL(SUM(ITM.IGST_AMT),0) +IFNULL(SUM(ITM.CGST_AMT),0) +IFNULL (SUM(ITM.SGST_AMT),0) +IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, ");
		bufferString.append( " IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT ,IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT ,IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT,  ");
		bufferString.append( " IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT  ");
		bufferString.append( " FROM GETGSTR2A_ECOMA_HEADER HDR INNER JOIN GETGSTR2A_ECOMA_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append( " AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD   " + condition1);
		//bufferString.append( " AND HDR.DERIVED_RET_PERIOD BETWEEN 202310 AND 202310  AND HDR.CGSTIN IN  ('29AAAPH9357H1A2') ");
		bufferString.append( "  WHERE IS_DELETE=FALSE  ");
		bufferString.append( " GROUP BY CGSTIN,HDR.DERIVED_RET_PERIOD  ");
		  
		bufferString.append( "UNION ALL  ");
				
		bufferString.append(" SELECT CTIN AS CUST_GSTIN, ");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD, 'CDN' AS TAX_DOC_TYPE, (CASE WHEN NOTE_TYPE='C' THEN 'CR' ");
		bufferString.append(
				"WHEN NOTE_TYPE='D' THEN 'DR'END) AS DOC_TYPE, MAX(CREATED_ON) LAST_UPDATED, ");
		bufferString.append(
				"COUNT(DISTINCT HDR.ID) AS COUNT, SUM(IFNULL(ITM.IGST_AMT,0)+IFNULL(ITM.CGST_AMT,0) ");
		bufferString.append(
				"+IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) INV_VALUE ,");
		bufferString.append(
				"IFNULL(SUM(HDR.TAXABLE_VALUE ),0) AS TAXABLE_VALUE, (IFNULL(SUM(ITM.IGST_AMT),0)+ ");
		bufferString.append(
				"IFNULL(SUM(ITM.CGST_AMT),0) +IFNULL(SUM(ITM.SGST_AMT),0)+ IFNULL(SUM(ITM.CESS_AMT),0)) ");
		bufferString.append(
				"AS TOT_TAX, IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT FROM ");
		bufferString.append(
				"GETGSTR2A_CDN_HEADER HDR INNER JOIN GETGSTR2A_CDN_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  " + condition2);
		bufferString.append(
				"WHERE IS_DELETE=FALSE AND NOTE_TYPE IN ('C','D') GROUP BY CTIN,HDR.DERIVED_RET_PERIOD,NOTE_TYPE ");
		bufferString.append(
				"UNION ALL SELECT CTIN AS CUST_GSTIN,HDR.DERIVED_RET_PERIOD, 'CDNA' AS TAX_DOC_TYPE, ");
		bufferString.append(
				"(CASE WHEN NOTE_TYPE='C' THEN 'RCR' WHEN NOTE_TYPE='D' THEN 'RDR'END) AS DOC_TYPE, ");
		bufferString.append(
				"MAX(CREATED_ON) LAST_UPDATED, COUNT(DISTINCT HDR.ID) AS COUNT ,SUM(IFNULL(ITM.IGST_AMT,0)");
		bufferString.append(
				"+IFNULL(ITM.CGST_AMT,0) +IFNULL(ITM.SGST_AMT,0)+IFNULL(ITM.CESS_AMT,0)+IFNULL(ITM.TAX_VALUE,0)) ");
		bufferString.append(
				"INV_VALUE ,IFNULL(SUM (ITM.TAX_VALUE ),0) AS TAXABLE_VALUE ,(IFNULL(SUM(ITM.IGST_AMT),0)+ ");
		bufferString.append(
				"IFNULL(SUM(ITM.CGST_AMT),0)+ IFNULL(SUM(ITM.SGST_AMT),0)+ IFNULL(SUM(ITM.CESS_AMT),0)) AS ");
		bufferString.append(
				"TOT_TAX, IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT FROM ");
		bufferString.append(
				"GETGSTR2A_CDNA_HEADER HDR INNER JOIN GETGSTR2A_CDNA_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND ");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD " + condition2);
		bufferString.append(
				"WHERE IS_DELETE=FALSE AND NOTE_TYPE IN ('C','D')  GROUP BY CTIN,HDR.DERIVED_RET_PERIOD,NOTE_TYPE UNION ALL SELECT CTIN AS CUST_GSTIN, ");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD, 'ISD' AS TAX_DOC_TYPE, (CASE WHEN ISD_DOC_TYPE='ISD' THEN 'INV' ");
		bufferString.append(
				"WHEN ISD_DOC_TYPE='ISDCN' THEN 'CR' END) AS DOC_TYPE, MAX(CREATED_ON) LAST_UPDATED, ");
		bufferString.append(
				"COUNT(DISTINCT HDR.ID) AS COUNT, 0 AS INV_VALUE, 0 AS TAXABLE_VALUE, ");
		bufferString.append(
				"(IFNULL(SUM(ITM.IGST_AMT), 0)+ IFNULL(SUM(ITM.CGST_AMT),0) +IFNULL(SUM(ITM.SGST_AMT),0) ");
		bufferString.append(
				"+IFNULL (SUM(ITM.CESS_AMT),0)) AS TOT_TAX, IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, IFNULL(SUM (ITM.SGST_AMT),0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT FROM GETGSTR2A_ISD_HEADER HDR INNER JOIN ");
		bufferString.append(
				"GETGSTR2A_ISD_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND ");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  AND "
				+ "HDR.DERIVED_RET_PERIOD BETWEEN :fromPeriod AND :toPeriod AND HDR.CTIN IN  (:gstinList)");
		bufferString.append(
				"WHERE IS_DELETE=FALSE AND ISD_DOC_TYPE IN ('ISD','ISDCN') GROUP BY CTIN,HDR.DERIVED_RET_PERIOD,ISD_DOC_TYPE UNION ALL SELECT CTIN ");
		bufferString.append(
				"AS CUST_GSTIN,HDR.DERIVED_RET_PERIOD, 'ISDA' AS TAX_DOC_TYPE, (CASE WHEN ISD_DOC_TYPE='ISD' ");
		bufferString.append(
				"THEN 'RNV' WHEN ISD_DOC_TYPE='ISDCN' THEN 'RCR' END) AS DOC_TYPE, MAX(CREATED_ON) LAST_UPDATED, ");
		bufferString.append(
				"COUNT(DISTINCT HDR.ID) AS COUNT, 0 AS INV_VALUE, 0 AS TAXABLE_VALUE, ");
		bufferString.append(
				"(IFNULL(SUM(ITM.IGST_AMT),0) + IFNULL(SUM(ITM.CGST_AMT),0)+ IFNULL(SUM(ITM.SGST_AMT),0) ");
		bufferString.append(
				"+IFNULL(SUM (ITM.CESS_AMT),0)) AS TOT_TAX, IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.CGST_AMT),0) AS CGST_AMT, IFNULL(SUM(ITM.SGST_AMT),0) AS SGST_AMT, ");
		bufferString.append(
				"IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT FROM GETGSTR2A_ISDA_HEADER HDR INNER JOIN ");
		bufferString
				.append("GETGSTR2A_ISDA_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD "
				+ "AND HDR.DERIVED_RET_PERIOD BETWEEN :fromPeriod AND :toPeriod AND HDR.CTIN IN  (:gstinList)");
		bufferString.append(
				" WHERE IS_DELETE=FALSE  AND ISD_DOC_TYPE IN ('ISD','ISDCN') GROUP BY CTIN,HDR.DERIVED_RET_PERIOD,ISD_DOC_TYPE ");
		bufferString.append(
				"UNION ALL SELECT GSTIN AS CUST_GSTIN, HDR.DERIVED_RET_PERIOD, 'IMPG' AS TAX_DOC_TYPE, ");
		bufferString.append(
				"'INV' AS DOC_TYPE, MAX(MODIFIED_ON) LAST_UPDATED, COUNT(DISTINCT HDR.ID) AS COUNT, ");
		bufferString.append(
				"(IFNULL(SUM(ITM.TAXABLE_VALUE),0) +IFNULL(SUM(ITM.IGST_AMT),0) + IFNULL(SUM(ITM.CESS_AMT),0)) ");
		bufferString.append(
				"AS INV_VALUE, IFNULL(SUM (ITM.TAXABLE_VALUE ),0) AS TAXABLE_VALUE , ");
		bufferString.append(
				"(IFNULL(SUM(ITM.IGST_AMT),0) +IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, ");
		bufferString.append(
				"IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, 0 CGST_AMT, 0 SGST_AMT, IFNULL(SUM(ITM.CESS_AMT),0) ");
		bufferString.append(
				"AS CESS_AMT FROM GETGSTR2A_IMPG_HEADER HDR INNER JOIN GETGSTR2A_IMPG_ITEM ITM ");
		bufferString.append(
				"ON HDR.ID = ITM.HEADER_ID AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD " + condition3);
		bufferString.append(
				"WHERE IS_DELETE=FALSE GROUP BY GSTIN,HDR.DERIVED_RET_PERIOD UNION ALL SELECT GSTIN ");
		bufferString.append(
				"AS CUST_GSTIN, HDR.DERIVED_RET_PERIOD , 'IMPGSEZ' AS TAX_DOC_TYPE, 'INV' AS DOC_TYPE, ");
		bufferString.append(
				"MAX(MODIFIED_ON) LAST_UPDATED, COUNT(DISTINCT HDR.ID) AS COUNT ,");
		bufferString.append(
				"(IFNULL(SUM(ITM.TAXABLE_VALUE),0)+ IFNULL(SUM(ITM.IGST_AMT),0) +IFNULL(SUM(ITM.CESS_AMT),0)) ");
		bufferString.append(
				"AS INV_VALUE , IFNULL(SUM(ITM.TAXABLE_VALUE ),0) AS TAXABLE_VALUE, (IFNULL(SUM(HDR.IGST_AMT),0) ");
		bufferString.append(
				"+IFNULL(SUM (HDR.CESS_AMT),0)) AS TOT_TAX, IFNULL(SUM(HDR.IGST_AMT),0) AS IGST_AMT, 0 CGST_AMT, ");
		bufferString.append(
				"0 SGST_AMT , IFNULL(SUM(HDR.CESS_AMT),0) AS CESS_AMT FROM GETGSTR2A_IMPGSEZ_HEADER HDR ");
		bufferString.append(
				"INNER JOIN GETGSTR2A_IMPGSEZ_ITEM ITM ON HDR.ID = ITM.HEADER_ID ");
		bufferString.append(
				"AND HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  " + condition3);
		bufferString.append(
				"WHERE IS_DELETE=FALSE GROUP BY GSTIN,HDR.DERIVED_RET_PERIOD UNION ALL SELECT GSTIN AS CUST_GSTIN, ");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD , (CASE WHEN HDR.AMD_TYPE='IMPG' THEN 'AMD_IMPG' ");
		bufferString.append(
				"WHEN HDR.AMD_TYPE='IMPGSEZ' THEN 'AMD_IMPGSEZ' END) TAX_DOC_TYPE, 'RNV' AS DOC_TYPE, ");
		bufferString.append(
				"MAX(MODIFIED_ON) LAST_UPDATED, COUNT(DISTINCT HDR.ID) AS COUNT, ");
		bufferString.append(
				"(IFNULL(SUM(ITM.TAXABLE_VALUE),0)+ IFNULL(SUM(ITM.IGST_AMT),0) +IFNULL(SUM(ITM.CESS_AMT),0)) ");
		bufferString.append(
				"AS INV_VALUE ,IFNULL(SUM(ITM.TAXABLE_VALUE ),0) AS TAXABLE_VALUE, (IFNULL(SUM(ITM.IGST_AMT),0)+ ");
		bufferString.append(
				"IFNULL(SUM(ITM.CESS_AMT),0)) AS TOT_TAX, IFNULL(SUM(ITM.IGST_AMT),0) AS IGST_AMT, 0 CGST_AMT, ");
		bufferString.append(
				"0 SGST_AMT, IFNULL(SUM(ITM.CESS_AMT),0) AS CESS_AMT FROM GETGSTR2A_AMDHIST_HEADER HDR ");
		bufferString.append(
				"INNER JOIN GETGSTR2A_AMDHIST_ITEM ITM ON HDR.ID = ITM.HEADER_ID AND ");
		bufferString.append(
				"HDR.DERIVED_RET_PERIOD = ITM.DERIVED_RET_PERIOD  "+ condition3);
		bufferString
				.append("WHERE IS_DELETE=FALSE GROUP BY GSTIN,HDR.DERIVED_RET_PERIOD,HDR.AMD_TYPE) ");

		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append(
				" GROUP BY CUST_GSTIN,DERIVED_RET_PERIOD,TAX_DOC_TYPE,DOC_TYPE ");
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("bufferString--------------------------> {} ",
					bufferString);
		}
		return bufferString;
	}

}
