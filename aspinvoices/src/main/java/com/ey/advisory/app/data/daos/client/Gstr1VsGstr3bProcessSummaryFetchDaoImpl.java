package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1Vs3bStatusRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessSummaryInnerDto;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Component("Gstr1VsGstr3bProcessSummaryFetchDaoImpl")
@Slf4j
public class Gstr1VsGstr3bProcessSummaryFetchDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository getAnx1BatchRepository;

	@Autowired
	private Gstr1ProcessedRecordsCommonUtil gstr1ProcessedRecordsCommonUtil;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("Gstr1Vs3bStatusRepository")
	private Gstr1Vs3bStatusRepository gstr1Vs3bStatusRepository;

	public List<Gstr1VsGstr3bProcessSummaryRespDto> fetchGstr1VsGstr3bRecords(Gstr1VsGstr3bProcessSummaryReqDto reqDto,
			Map<String, String> statusMap, Map<String, String> gstr3bStatusMap,Map<String,String> gstr1aStatusMap) {

		List<Long> entityId = reqDto.getEntityId();
		String taxPeriodFrom = reqDto.getTaxPeriodFrom();
		String taxPeriodTo = reqDto.getTaxPeriodTo();
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1VsGstr3bProcessSummaryFetchDaoImpl->" + "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {}, dataSecAttrs -> {}", reqDto);
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

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty() && dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					sgstin = key;
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}

		StringBuilder queryStr = createQueryString(entityId, gstinList, taxPeriodFrom, taxPeriodTo, dataSecAttrs,
				profitCenter, sgstin, cgstin, plant, division, location, sales, distChannel, ud1, ud2, ud3, ud4, ud5,
				ud6, pcList, plantList, salesList, divisionList, locationList, distList, ud1List, ud2List, ud3List,
				ud4List, ud5List, ud6List);
		
		StringBuilder queryDiffStr = createDiffQueryString(entityId, gstinList, taxPeriodFrom, taxPeriodTo, dataSecAttrs,
				profitCenter, sgstin, cgstin, plant, division, location, sales, distChannel, ud1, ud2, ud3, ud4, ud5,
				ud6, pcList, plantList, salesList, divisionList, locationList, distList, ud1List, ud2List, ud3List,
				ud4List, ud5List, ud6List);

		LOGGER.debug("outQueryStr-->" + queryStr);
		LOGGER.debug("queryDiffStr-->" + queryDiffStr);

		List<Gstr1VsGstr3bProcessSummaryRespDto> finalDtoList = new ArrayList<>();
		try {

			Query Q = entityManager.createNativeQuery(queryStr.toString());
			if (StringUtils.isNotEmpty(taxPeriodFrom) && StringUtils.isNotEmpty(taxPeriodTo)) {
				Q.setParameter("taxPeriodFrom", GenUtil.convertTaxPeriodToInt(taxPeriodFrom));
				Q.setParameter("taxPeriodTo", GenUtil.convertTaxPeriodToInt(taxPeriodTo));
			}
			if (gstinList != null && gstinList.size() > 0 && !gstinList.contains("")) {
				Q.setParameter("sgstin", gstinList);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> Qlist = Q.getResultList();
			LOGGER.error("bufferString-------------------------->" + Qlist);
			
			
			Query q1 = entityManager.createNativeQuery(queryDiffStr.toString());
			if (StringUtils.isNotEmpty(taxPeriodFrom) && StringUtils.isNotEmpty(taxPeriodTo)) {
				q1.setParameter("taxPeriodFrom", GenUtil.convertTaxPeriodToInt(taxPeriodFrom));
				q1.setParameter("taxPeriodTo", GenUtil.convertTaxPeriodToInt(taxPeriodTo));
			}
			if (gstinList != null && gstinList.size() > 0 && !gstinList.contains("")) {
				q1.setParameter("sgstin", gstinList);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> q1List = q1.getResultList();
			LOGGER.error("bufferString-------------------------->" + q1List);
			
			//create a map of <GSTIN, DTO>
			
			Map<String, List<Gstr1VsGstr3bProcessSummaryInnerDto>> diffMap = new HashMap<>();

			for (Object[] row : q1List) {
			    String gstin = row[0] != null ? row[0].toString() : null;

			    Gstr1VsGstr3bProcessSummaryInnerDto dto = new Gstr1VsGstr3bProcessSummaryInnerDto();
			    dto.setGstin(gstin);
			    dto.setDiffTaxableValue(row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO);
			    dto.setDiffTotalTax(row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO);
			    
			    // Add dto to map
			    diffMap.computeIfAbsent(gstin, k -> new ArrayList<>()).add(dto);
			}

			
			finalDtoList = convertGstr1vs3bRecordsIntoObject(Qlist, reqDto, taxPeriodFrom, taxPeriodTo, diffMap);

			gstr1ProcessedRecordsCommonUtil.fillGstr1vs3BTheDataFromDataSecAttr(finalDtoList, gstinList, taxPeriodFrom,
					taxPeriodTo, reqDto, statusMap, gstr3bStatusMap,gstr1aStatusMap);

			LOGGER.debug("Data list from database is-->" + Qlist);

		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}
		return finalDtoList;
	}

	private List<Gstr1VsGstr3bProcessSummaryRespDto> convertGstr1vs3bRecordsIntoObject(List<Object[]> savedDataList,
			Gstr1VsGstr3bProcessSummaryReqDto reqDto, String taxPeriodFrom, String taxPeriodTo,
			Map<String, List<Gstr1VsGstr3bProcessSummaryInnerDto>> diffMap) throws Exception {
		List<Gstr1VsGstr3bProcessSummaryRespDto> summaryList = new ArrayList<Gstr1VsGstr3bProcessSummaryRespDto>();
		if (!savedDataList.isEmpty() && savedDataList.size() > 0) {
			for (Object[] data : savedDataList) {
				Gstr1VsGstr3bProcessSummaryRespDto dto = new Gstr1VsGstr3bProcessSummaryRespDto();
				String gstin = String.valueOf(data[2]);
				dto.setGstin(gstin);

				String gstintoken = defaultGSTNAuthTokenService.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						dto.setAuthToken("Active");
					} else {
						dto.setAuthToken("Inactive");
					}
				} else {
					dto.setAuthToken("Inactive");
				}
				String stateCode = gstin.substring(0, 2);
				String stateName = statecodeRepository.findStateNameByCode(stateCode);
				dto.setState(stateName);
				int derivedRetPerFrom = GenUtil.convertTaxPeriodToInt(reqDto.getTaxPeriodFrom());
				int derivedRetPerTo = GenUtil.convertTaxPeriodToInt(reqDto.getTaxPeriodTo());

				List<Object[]> statusArray = gstr1Vs3bStatusRepository.findStatusByGstin(gstin, derivedRetPerFrom,
						derivedRetPerTo);
				if (statusArray == null || statusArray.isEmpty()) {
					dto.setReconStatus("NOT_INITIATED");
				} else {
					Object status[] = statusArray.stream().findFirst().get();
					String reconstatuses = String.valueOf(status[0]);
					if (status[0] == null || status[0] == "null") {
						dto.setReconStatus("");
					} else {
						dto.setReconStatus(reconstatuses);
					}
					if (status[1] == null || status[1] == "null") {
						dto.setReconDateTime("");
					} else {
						LocalDateTime dt = (LocalDateTime) status[1];
						LocalDateTime dateTimeFormatter = EYDateUtil.toISTDateTimeFromUTC(dt);
						DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(dateTimeFormatter);

						dto.setReconDateTime(newdate);
					}
				}
				dto.setGstr1TaxableValue((BigDecimal) data[3]);
				dto.setGstr1TotalTax((BigDecimal) data[4]);
				dto.setGstr3bTaxableValue((BigDecimal) data[5]);
				dto.setGstr3bTotalTax((BigDecimal) data[6]);
			
				 // Set differences from diffMap
	            if (diffMap != null && diffMap.containsKey(gstin)) {
	                List<Gstr1VsGstr3bProcessSummaryInnerDto> innerList = diffMap.get(gstin);
	                if (innerList != null && !innerList.isEmpty()) {
	                    Gstr1VsGstr3bProcessSummaryInnerDto diffDto = innerList.get(0);
	                    dto.setDiffTaxableValue(diffDto.getDiffTaxableValue());
	                    dto.setDiffTotalTax(diffDto.getDiffTotalTax());
	                } else {
	                    dto.setDiffTaxableValue(BigDecimal.ZERO);
	                    dto.setDiffTotalTax(BigDecimal.ZERO);
	                }
	            } else {
	                dto.setDiffTaxableValue(BigDecimal.ZERO);
	                dto.setDiffTotalTax(BigDecimal.ZERO);
	            }
				
				summaryList.add(dto);
			}
		}
		return summaryList;

	}

	public StringBuilder createQueryString(List<Long> entityId, List<String> gstinList, String taxPeriodFrom,
			String taxPeriodTo, Map<String, List<String>> dataSecAttrs, String profitCenter, String sgstin,
			String cgstin, String plant, String division, String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6, List<String> pcList, List<String> plantList,
			List<String> salesList, List<String> divisionList, List<String> locationList, List<String> distList,
			List<String> ud1List, List<String> ud2List, List<String> ud3List, List<String> ud4List,
			List<String> ud5List, List<String> ud6List) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty() && dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilder = new StringBuilder();
		if (StringUtils.isNotEmpty(taxPeriodFrom) && StringUtils.isNotEmpty(taxPeriodTo)) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom " + " AND :taxPeriodTo");
		}

		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND GSTIN IN :sgstin");
		}

		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString.append(" select STATUS,created_on,GSTIN, IFNULL(sum"
				+ "(TAXABLE_VALUE_GSTR1),0) as TAXABLEVALUE_GSTR1,"
				+ "IFNULL(sum(TOTAL_TAX_GSTR1),0) as TOTALTAX_GSTR1 , "
				+ "IFNULL(sum(TAXABLE_VALUE_GSTR3B),0) as TAXABLEVALUE_GST3B,"
				+ "IFNULL(sum(TOTAL_TAX_GSTR3B),0) as TOTALTAX_GSTR3B from "
				+ "( select '' AS STATUS,'' as created_on,GSTIN, " + "sum(TAXABLE_VALUE) AS TAXABLE_VALUE_GSTR3B,"
				+ " SUM(IFNULL(IGST_AMT,0) + IFNULL(CGST_AMT,0) +IFNULL"
				+ "(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX_GSTR3B,"
				+ " 0 AS TAXABLE_VALUE_GSTR1,0 AS TOTAL_TAX_GSTR1 FROM "
				+ "GSTR1_VS_3B_COMPUTE WHERE SUB_SECTION_NAME IN('A','D1','D2','H') " + "AND IS_ACTIVE = TRUE  ");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" GROUP BY GSTIN union all select '' AS STATUS,'' as"
				+ " created_on,GSTIN, 0 AS TAXABLE_VALUE_GSTR3B, 0 as "
				+ "TOTAL_TAX_GSTR3B, sum(TAXABLE_VALUE) AS TAXABLE_VALUE_GSTR1,"
				+ " SUM(IFNULL(IGST_AMT,0) + IFNULL(CGST_AMT,0) +IFNULL"
				+ "(SGST_AMT,0)+IFNULL(CESS_AMT,0)) AS TOTAL_TAX_GSTR1 "
				+ "FROM GSTR1_VS_3B_COMPUTE WHERE SUB_SECTION_NAME " + "IN('B','E','I') AND IS_ACTIVE = TRUE ");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" GROUP BY GSTIN ) GROUP BY GSTIN,STATUS,created_on ");
		LOGGER.debug("Gstr1vs3b Query from database is-->" + bufferString);

		LOGGER.error("bufferString-------------------------->" + bufferString);
		return bufferString;
	}

	
	public StringBuilder createDiffQueryString(List<Long> entityId, List<String> gstinList, String taxPeriodFrom,
			String taxPeriodTo, Map<String, List<String>> dataSecAttrs, String profitCenter, String sgstin,
			String cgstin, String plant, String division, String location, String sales, String distChannel, String ud1,
			String ud2, String ud3, String ud4, String ud5, String ud6, List<String> pcList, List<String> plantList,
			List<String> salesList, List<String> divisionList, List<String> locationList, List<String> distList,
			List<String> ud1List, List<String> ud2List, List<String> ud3List, List<String> ud4List,
			List<String> ud5List, List<String> ud6List) {

		if (dataSecAttrs != null && !dataSecAttrs.isEmpty() && dataSecAttrs.size() > 0) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase("GSTIN")) {
					sgstin = key;
				}
			}
		}

		StringBuilder queryBuilder = new StringBuilder();
		if (StringUtils.isNotEmpty(taxPeriodFrom) && StringUtils.isNotEmpty(taxPeriodTo)) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD BETWEEN :taxPeriodFrom " + " AND :taxPeriodTo");
		}

		if (gstinList != null && gstinList.size() > 0) {
			queryBuilder.append(" AND GSTIN IN :sgstin");
		}

		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString.append(" select GSTIN, SUM(IFNULL (TAXABLE_VALUE, 0)) AS TOTAL_TAXABLE_VALUE_DIFF, " + 
				"  SUM(IFNULL (IGST_AMT,0) + IFNULL(CGST_AMT,0) +IFNULL(SGST_AMT,0) + IFNULL(CESS_AMT,0)) AS TOTAL_TAX_DIFF FROM "
				+ "GSTR1_VS_3B_COMPUTE WHERE SUB_SECTION_NAME IN('K') " + "AND IS_ACTIVE = TRUE  ");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		
		bufferString.append(" GROUP BY GSTIN ");
		LOGGER.debug("Gstr1vs3b Diff Query from database is-->" + bufferString);

		return bufferString;
	}
	

}
