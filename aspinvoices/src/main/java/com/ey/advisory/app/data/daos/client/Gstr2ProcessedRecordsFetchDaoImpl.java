package com.ey.advisory.app.data.daos.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx1.Gstr2ProcessedRecordsFinalRespDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr2ProcessedRecordsReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Gstr2ProcessedRecordsFetchDaoImpl")
public class Gstr2ProcessedRecordsFetchDaoImpl
		implements Gstr2ProcessedRecordsFetchDao {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2ProcessedRecordsFetchDaoImpl.class);
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private Gstr2ProcessedRecordsCommonUtil gstr2ProcessedRecordsCommonUtil;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;
	
	@Autowired
	private CommonUtility commonUtility;

	public List<Gstr2ProcessedRecordsFinalRespDto> loadGstr2ProcessedRecords(
			Gstr2ProcessedRecordsReqDto gstr2ProcessedRecordsReqDto) {

		List<Long> entityId = gstr2ProcessedRecordsReqDto.getEntityId();
		LocalDate docRecvFrom = gstr2ProcessedRecordsReqDto.getDocRecvFrom();
		LocalDate docRecvTo = gstr2ProcessedRecordsReqDto.getDocRecvTo();
		String taxPeriodFromReq = gstr2ProcessedRecordsReqDto.getTaxPeriodFrom();
		String taxPeriodToReq = gstr2ProcessedRecordsReqDto.getTaxPeriodTo();
		
		int taxPeriodFrom = !Strings.isNullOrEmpty(taxPeriodFromReq)
				? GenUtil.convertTaxPeriodToInt(taxPeriodFromReq) : 0;
		int taxPeriodTo = !Strings.isNullOrEmpty(taxPeriodToReq)
				? GenUtil.convertTaxPeriodToInt(taxPeriodToReq) : 0;
				
		List<String> tableType = gstr2ProcessedRecordsReqDto.getTableType();
		List<String> tableTypeUpperCase = tableType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

		List<String> docType = gstr2ProcessedRecordsReqDto.getDocType();
		List<String> docTypeUpperCase = docType.stream()
				.map(String::toUpperCase).collect(Collectors.toList());

		List<String> dataCategory = gstr2ProcessedRecordsReqDto
				.getDocCategory();
		Map<String, List<String>> dataSecAttrs = gstr2ProcessedRecordsReqDto
				.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		String cgstin = null;

		String profitCenter = null, plant = null;
		String sales = null, division = null, location = null;
		String purchase = null, distChannel = null, ud1 = null;
		String ud2 = null, ud3 = null, ud4 = null, ud5 = null;
		String ud6 = null;

		List<String> pcList = null, plantList = null;
		List<String> salesList = null, divisionList = null;
		List<String> locationList = null, purcList = null;
		List<String> distList = null;
		List<String> ud1List = null, ud2List = null, ud3List = null;
		List<String> ud4List = null, ud5List = null, ud6List = null;

		List<Gstr2ProcessedRecordsFinalRespDto> apiSummaryResDtos = new ArrayList<Gstr2ProcessedRecordsFinalRespDto>();

		try {

			if (dataSecAttrs != null && !dataSecAttrs.isEmpty()
					&& dataSecAttrs.size() > 0) {
				for (String key : dataSecAttrs.keySet()) {

					if (key.equalsIgnoreCase("PC")) {
						profitCenter = key;
						pcList = dataSecAttrs.get("PC");
					}
					if (key.equalsIgnoreCase("Plant")) {
						plant = key;
						plantList = dataSecAttrs.get("Plant");
					}
					if (key.equalsIgnoreCase("D")) {
						division = key;
						divisionList = dataSecAttrs.get("D");
					}
					if (key.equalsIgnoreCase("L")) {
						location = key;
						locationList = dataSecAttrs.get("L");
					}
					/*if (key.equalsIgnoreCase("SO")) {
						sales = key;
						salesList = dataSecAttrs.get("SO");
					}*/
					if (key.equalsIgnoreCase("DC")) {
						distChannel = key;
						distList = dataSecAttrs.get("DC");
					}
					if (key.equalsIgnoreCase("PO")) {
						purchase = key;
						purcList = dataSecAttrs.get("PO");
					}
					if (key.equalsIgnoreCase("UD1")) {
						ud1 = key;
						ud1List = dataSecAttrs.get("UD1");
					}
					if (key.equalsIgnoreCase("UD2")) {
						ud2 = key;
						ud2List = dataSecAttrs.get("UD2");
					}
					if (key.equalsIgnoreCase("UD3")) {
						ud3 = key;
						ud3List = dataSecAttrs.get("UD3");
					}
					if (key.equalsIgnoreCase("UD4")) {
						ud4 = key;
						ud4List = dataSecAttrs.get("UD4");
					}
					if (key.equalsIgnoreCase("UD5")) {
						ud5 = key;
						ud5List = dataSecAttrs.get("UD5");
					}
					if (key.equalsIgnoreCase("UD6")) {
						ud6 = key;
						ud6List = dataSecAttrs.get("UD6");
					}
					if (key.equalsIgnoreCase("GSTIN")) {
						cgstin = key;
						gstinList = dataSecAttrs.get("GSTIN");
					}
				}
			}

			StringBuffer queryStr = createQueryString(entityId, gstinList,
					docRecvFrom, docRecvTo, taxPeriodFrom, taxPeriodTo, cgstin,
					tableType, tableTypeUpperCase, docType, docTypeUpperCase,
					dataCategory, dataSecAttrs, profitCenter, plant, division,
					location, sales, purchase, distChannel, ud1, ud2, ud3, ud4,
					ud5, ud6, pcList, plantList, salesList, divisionList,
					locationList, purcList, distList, ud1List, ud2List, ud3List,
					ud4List, ud5List, ud6List);

			Query outquery = entityManager
					.createNativeQuery(queryStr.toString());
			LOGGER.debug("outquery -->" + outquery);

			if (StringUtils.isNotBlank(cgstin)) {
				outquery.setParameter("gstinList", gstinList);
			}

			if (CollectionUtils.isNotEmpty(pcList)) {
				outquery.setParameter("pcList", pcList);
			}
			if (CollectionUtils.isNotEmpty(plantList)) {
				outquery.setParameter("plantList", plantList);
			}
			/*if (CollectionUtils.isNotEmpty(salesList)) {
				outquery.setParameter("salesList", salesList);
			}*/
			if (CollectionUtils.isNotEmpty(divisionList)) {
				outquery.setParameter("divisionList", divisionList);
			}
			if (CollectionUtils.isNotEmpty(locationList)) {
				outquery.setParameter("locationList", locationList);
			}
			if (CollectionUtils.isNotEmpty(purcList)) {
				outquery.setParameter("purcList", purcList);
			}
			if (CollectionUtils.isNotEmpty(distList)) {
				outquery.setParameter("distList", distList);
			}
			if (CollectionUtils.isNotEmpty(ud1List)) {
				outquery.setParameter("ud1List", ud1List);
			}
			if (CollectionUtils.isNotEmpty(ud2List)) {
				outquery.setParameter("ud2List", ud2List);
			}
			if (CollectionUtils.isNotEmpty(ud3List)) {
				outquery.setParameter("ud3List", ud3List);
			}
			if (CollectionUtils.isNotEmpty(ud4List)) {
				outquery.setParameter("ud4List", ud4List);
			}
			if (CollectionUtils.isNotEmpty(ud5List)) {
				outquery.setParameter("ud5List", ud5List);
			}
			if (CollectionUtils.isNotEmpty(ud6List)) {
				outquery.setParameter("ud6List", ud6List);
			}

			if (docRecvFrom != null && docRecvTo != null) {
				outquery.setParameter("docRecvFrom", docRecvFrom);
				outquery.setParameter("docRecvTo", docRecvTo);
			}
			if (CollectionUtils.isNotEmpty(docTypeUpperCase)
					&& CollectionUtils.isNotEmpty(dataCategory)) {
				outquery.setParameter("docTypeUpperCase", docTypeUpperCase);
				outquery.setParameter("dataCategory", dataCategory);
			}
			if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
				outquery.setParameter("taxPeriodFrom", taxPeriodFrom);
				outquery.setParameter("taxPeriodTo", taxPeriodTo);
			}

			if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
				outquery.setParameter("tableTypeUpperCase", tableTypeUpperCase);
			}
			if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
				outquery.setParameter("docTypeUpperCase", docTypeUpperCase);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = outquery.getResultList();
			
			Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
					.getGstnRegMap();
			
			List<Gstr2ProcessedRecordsFinalRespDto> outwardFinalList = gstr2ProcessedRecordsCommonUtil
					.convertGstr2RecordsIntoObject(list,gstnRegMap.getValue0(), gstnRegMap.getValue1());

			Map<String, List<Gstr2ProcessedRecordsFinalRespDto>> combinedDataMap = new HashMap<String, List<Gstr2ProcessedRecordsFinalRespDto>>();

			gstr2ProcessedRecordsCommonUtil.createMapByGstinBasedOnType(
					combinedDataMap, outwardFinalList);

			List<Gstr2ProcessedRecordsFinalRespDto> dataDtoList = new ArrayList<>();
			gstr2ProcessedRecordsCommonUtil
					.calculateDataByDocType(combinedDataMap, dataDtoList);

			gstr2ProcessedRecordsCommonUtil.fillTheDataFromDataSecAttr(
					dataDtoList, gstinList, docRecvFrom, docRecvTo,
					taxPeriodFromReq, taxPeriodToReq, gstnRegMap.getValue0(), gstnRegMap.getValue1());
			List<Gstr2ProcessedRecordsFinalRespDto> gstinDtoList = new ArrayList<Gstr2ProcessedRecordsFinalRespDto>();
			List<String> combinedGstinList = new ArrayList<>();
			if (cgstin != null && !cgstin.isEmpty() && gstinList != null
					&& gstinList.size() > 0) {
				combinedGstinList.addAll(gstinList);
			}
			if (!combinedGstinList.isEmpty() && combinedGstinList.size() > 0) {
				for (Gstr2ProcessedRecordsFinalRespDto processedDto : dataDtoList) {
					if (combinedGstinList.contains(processedDto.getGstin())) {
						gstinDtoList.add(processedDto);
					}
				}
				List<Gstr2ProcessedRecordsFinalRespDto> sortedGstinDtoList = gstinDtoList
						.stream()
						.sorted(Comparator.comparing(
								Gstr2ProcessedRecordsFinalRespDto::getGstin))
						.collect(Collectors.toList());
				return Gstr2ProcessedRecordsCommonUtil
						.convertCalcuDataToResp(sortedGstinDtoList);
			}

			List<Gstr2ProcessedRecordsFinalRespDto> sortedGstinDtoList = dataDtoList
					.stream()
					.sorted(Comparator.comparing(
							Gstr2ProcessedRecordsFinalRespDto::getGstin))
					.collect(Collectors.toList());
			apiSummaryResDtos.addAll(sortedGstinDtoList);
			LOGGER.debug("Final list from dao is ->" + apiSummaryResDtos);
		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}

		return Gstr2ProcessedRecordsCommonUtil
				.convertCalcuDataToResp(apiSummaryResDtos);

	}

	private StringBuffer createQueryString(List<Long> entityId,
			List<String> gstinList, LocalDate docRecvFrom, LocalDate docRecvTo,
			int taxPeriodFrom, int taxPeriodTo, String cgstin,
			List<String> tableType, List<String> tableTypeUpperCase,
			List<String> docType, List<String> docTypeUpperCase,
			List<String> dataCategory, Map<String, List<String>> dataSecAttrs,
			String profitCenter, String plant, String division, String location,
			String sales, String purchase, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6,
			List<String> pcList, List<String> plantList, List<String> salesList,
			List<String> divisionList, List<String> locationList,
			List<String> purcList, List<String> distList, List<String> ud1List,
			List<String> ud2List, List<String> ud3List, List<String> ud4List,
			List<String> ud5List, List<String> ud6List) {

		
		
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
				if (key.equalsIgnoreCase("PO")) {
					purchase = key;
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
					cgstin = key;
				}
			}
		}

		StringBuilder queryBuilder = new StringBuilder();

		if (CollectionUtils.isNotEmpty(gstinList)) {
			queryBuilder.append(" AND  CUST_GSTIN IN :gstinList ");
		}

		if (StringUtils.isNotBlank(profitCenter)
				&& CollectionUtils.isNotEmpty(pcList)) {
			queryBuilder.append(" AND  PROFIT_CENTRE IN :pcList");
		}
		if (StringUtils.isNotBlank(plant)
				&& CollectionUtils.isNotEmpty(plantList)) {
			queryBuilder.append(" AND  PLANT_CODE IN :plantList");
		}
		if (StringUtils.isNotBlank(division)
				&& CollectionUtils.isNotEmpty(divisionList)) {
			queryBuilder.append(" AND  DIVISION IN :divisionList");
		}
		if (StringUtils.isNotBlank(location)
				&& CollectionUtils.isNotEmpty(locationList)) {
			queryBuilder.append(" AND  LOCATION IN :locationList");
		}
		/*if (StringUtils.isNotBlank(sales)
				&& CollectionUtils.isNotEmpty(salesList)) {
			queryBuilder.append(" AND  SALES_ORGANIZATION IN :salesList");
		}*/
		if (StringUtils.isNotBlank(distChannel)
				&& CollectionUtils.isNotEmpty(distList)) {
			queryBuilder.append(" AND  DISTRIBUTION_CHANNEL IN :distList");
		}

		if (StringUtils.isNotBlank(purchase)
				&& CollectionUtils.isNotEmpty(purcList)) {
			queryBuilder.append(" AND   PURCHASE_ORGANIZATION IN :purcList ");
		}

		if (StringUtils.isNotBlank(ud1)
				&& CollectionUtils.isNotEmpty(ud1List)) {
			queryBuilder.append(" AND  USERACCESS1 IN :ud1List");
		}
		if (StringUtils.isNotBlank(ud2)
				&& CollectionUtils.isNotEmpty(ud2List)) {
			queryBuilder.append(" AND  USERACCESS2 IN :ud2List");
		}
		if (StringUtils.isNotBlank(ud3)
				&& CollectionUtils.isNotEmpty(ud3List)) {
			queryBuilder.append(" AND  USERACCESS3 IN :ud3List");
		}
		if (StringUtils.isNotBlank(ud4)
				&& CollectionUtils.isNotEmpty(ud4List)) {
			queryBuilder.append(" AND  USERACCESS4 IN :ud4List");
		}
		if (StringUtils.isNotBlank(ud5)
				&& CollectionUtils.isNotEmpty(ud5List)) {
			queryBuilder.append(" AND  USERACCESS5 IN :ud5List");
		}
		if (StringUtils.isNotBlank(ud6)
				&& CollectionUtils.isNotEmpty(ud6List)) {
			queryBuilder.append(" AND  USERACCESS6 IN :ud6List");
		}
		if (CollectionUtils.isNotEmpty(tableTypeUpperCase)) {
			queryBuilder.append(" AND  TAX_DOC_TYPE IN :tableTypeUpperCase");
		}
		if (CollectionUtils.isNotEmpty(docTypeUpperCase)) {
			queryBuilder.append(" AND  DOC_TYPE IN :docTypeUpperCase");
		}
		if (CollectionUtils.isNotEmpty(dataCategory)) {
			queryBuilder.append(" AND DATA_CATEGORY IN :dataCategory");
		}
		if (docRecvFrom != null && docRecvTo != null) {
			queryBuilder.append(
					" AND  DOC_DATE BETWEEN :docRecvFrom AND :docRecvTo  ");
		}
		if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
			queryBuilder.append(
					" AND  DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom AND :taxPeriodTo  ");
		}
		String condition = queryBuilder.toString();
		StringBuffer bufferString = new StringBuffer();
		bufferString
				.append( "SELECT "
						+ "  CUST_GSTIN, "
						+ "  MAX(LAST_UPDATED) LAST_UPDATED, "
						+ "  DOC_TYPE, "
						+ "  TAX_DOC_TYPE, "
						+ "  RETURN_PERIOD, "
						+ "  COUNT(DOC_KEY) COUNT, "
						+ "  SUM(INV_VALUE) INV_VALUE, "
						+ "  SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
						+ "  SUM(TOT_TAX) TOT_TAX, "
						+ "  SUM(TAX_PAYABLE_IGST) TAX_PAYABLE_IGST, "
						+ "  SUM(TAX_PAYABLE_CGST) TAX_PAYABLE_CGST, "
						+ "  SUM(TAX_PAYABLE_SGST) TAX_PAYABLE_SGST, "
						+ "  SUM(TAX_PAYABLE_CESS) TAX_PAYABLE_CESS, "
						+ "  SUM(TOT_CREDIT_ELIGIBLE) TOT_CREDIT_ELIGIBLE, "
						+ "  SUM(CR_ELG_IGST) CR_ELG_IGST, "
						+ "  SUM(CR_ELG_CGST) CR_ELG_CGST, "
						+ "  SUM(CR_ELG_SGST) CR_ELG_SGST, "
						+ "  SUM(CR_ELG_CESS) CR_ELG_CESS "
						+ "FROM "
						+ "  ( "
						+ "    SELECT "
						+ "      CUST_GSTIN AS CUST_GSTIN, "
						+ "      RETURN_PERIOD AS RETURN_PERIOD, "
						+ "      DOC_TYPE AS DOC_TYPE, "
						+ "      ( "
						+ "        CASE WHEN TAX_DOC_TYPE IN ('B2B') THEN 'B2B' WHEN TAX_DOC_TYPE IN ('B2BA') THEN 'B2BA' WHEN TAX_DOC_TYPE IN ('CDN') THEN 'CDN' WHEN TAX_DOC_TYPE IN ('CDNA') THEN 'CDNA' WHEN TAX_DOC_TYPE IN ('ISD') THEN 'ISD' WHEN TAX_DOC_TYPE IN ('ISDA') THEN 'ISDA' WHEN TAX_DOC_TYPE IN ('IMPS', 'IMPG', 'IMPGS') THEN 'IMP' WHEN TAX_DOC_TYPE IN ('IMPSA', 'IMPGA', 'IMPGSA') THEN 'IMPA' WHEN TAX_DOC_TYPE IN ('RCURD') THEN 'RCURD' WHEN TAX_DOC_TYPE IN ('RCURDA') THEN 'RCURDA'WHEN TAX_DOC_TYPE IN ('RCMADV') THEN 'RCMADV' END "
						+ "      ) AS TAX_DOC_TYPE, "
						+ "      MODIFIED_ON AS LAST_UPDATED, "
						+ "      DOC_KEY, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(DOC_AMT,0)) ELSE (IFNULL(DOC_AMT,0)) END AS INV_VALUE, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(TAXABLE_VALUE,0)) ELSE (IFNULL(TAXABLE_VALUE,0)) END AS TAXABLE_VALUE, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(IGST_AMT, 0)+ IFNULL (CGST_AMT,0)+ IFNULL(SGST_AMT, 0)+ IFNULL (CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) "
						+ "           ELSE (IFNULL(IGST_AMT, 0)+ IFNULL (CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+ IFNULL (CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) END AS TOT_TAX, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(IGST_AMT,0)) ELSE (IFNULL(IGST_AMT,0)) END AS TAX_PAYABLE_IGST, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(CGST_AMT,0)) ELSE (IFNULL(CGST_AMT,0)) END AS TAX_PAYABLE_CGST, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(SGST_AMT,0)) ELSE (IFNULL(SGST_AMT,0)) END AS TAX_PAYABLE_SGST, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) "
						+ "           ELSE (IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL (CESS_AMT_ADVALOREM, 0)) END AS TAX_PAYABLE_CESS, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_IGST, 0)+ IFNULL(AVAILABLE_CGST, 0)+ IFNULL (AVAILABLE_SGST, 0)+ IFNULL(AVAILABLE_CESS, 0)) "
						+ "           ELSE (IFNULL(AVAILABLE_IGST, 0)+ IFNULL(AVAILABLE_CGST, 0)+ IFNULL (AVAILABLE_SGST, 0)+ IFNULL(AVAILABLE_CESS, 0)) END AS TOT_CREDIT_ELIGIBLE, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_IGST,0)) ELSE (IFNULL(AVAILABLE_IGST,0)) END AS CR_ELG_IGST, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_CGST,0)) ELSE (IFNULL(AVAILABLE_CGST,0)) END AS CR_ELG_CGST, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_SGST,0)) ELSE (IFNULL(AVAILABLE_SGST,0)) END AS CR_ELG_SGST, "
						+ "      CASE WHEN DOC_TYPE IN ('CR', 'RCR','ADJ') THEN -1*(IFNULL(AVAILABLE_CESS,0)) ELSE (IFNULL(AVAILABLE_CESS,0)) END AS CR_ELG_CESS "
						+ "      FROM ANX_INWARD_DOC_HEADER "
						+ "      WHERE IS_PROCESSED= TRUE AND IS_DELETE=FALSE ");

		LOGGER.debug("Gstr2 Query from database is-->" + bufferString);
		if (StringUtils.isNotBlank(condition)) {
			bufferString.append(condition);
		}
		bufferString.append(" AND TAX_DOC_TYPE IN ('B2B','B2BA','CDN','CDNA','ISD','ISDA','IMPS', 'IMPSA','IMPG','IMPGA','IMPGS','IMPGSA','RCURD', 'RCURDA','RCMADV')) "
                          + " GROUP BY CUST_GSTIN,RETURN_PERIOD,TAX_DOC_TYPE,DOC_TYPE ");

		LOGGER.error("bufferString-------------------------->" + bufferString);
		return bufferString;
	}

}