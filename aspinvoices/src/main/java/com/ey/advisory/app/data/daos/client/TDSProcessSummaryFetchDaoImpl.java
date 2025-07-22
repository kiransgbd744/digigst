package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.docs.dto.simplified.TDSProcessSummaryRespDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@Component("TDSProcessSummaryFetchDaoImpl")
public class TDSProcessSummaryFetchDaoImpl {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	private TDSProcessedRecordsCommonUtil tdsProcessedRecordsCommonUtil;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	public List<TDSProcessSummaryRespDto> fetchTdsPrRecords(
			ITC04RequestDto req) {

		List<Long> entityId = req.getEntityId();
		String taxPeriodReq = req.getTaxPeriod();
		String action = req.getAction();
		int taxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriodReq);

		List<TDSProcessSummaryRespDto> convertTDSCalcuDataToResp = null;
		List<TDSProcessSummaryRespDto> collectSortingList = new ArrayList<>();
		
		Map<String, List<String>> dataSecAttrs = req.getDataSecAttrs();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("TDSProcessSummaryFetchDaoImpl->"
					+ "and selected criteria are:: entityId-> {}, "
					+ "gstins-> {}, " + "taxPeriod-> {},"
					+ "dataSecAttrs -> {}", req);
		}
		String gstin = null;

		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}
		StringBuilder build = new StringBuilder();
	//	StringBuilder build2 = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			if (gstinList != null && gstinList.size() > 0) {
				build.append(" AND GSTIN IN (:gstinList) ");
			//	build2.append(" AND TCS.GSTIN IN :gstinList ");
			}
		}

		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD  = :taxPeriod ");
		//	build2.append(" AND TCS.DERIVED_RET_PERIOD  = :taxPeriod ");
		}

		String buildQuery = build.toString().substring(4);
	//	String buildQueryfilter = buildQuery.toString().substring(4);
	//	String buildTcs = build2.toString();

		String queryStr = createQueryString(buildQuery,action);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For ITC04 ProcessedSummary is {}"
					+ queryStr);
		}
		List<TDSProcessSummaryRespDto> retList = null;
		List<TDSProcessSummaryRespDto> listValue = new ArrayList<>();
		
		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				if (gstinList != null && !gstinList.isEmpty()
						&& gstinList.size() > 0) {
					q.setParameter("gstinList", gstinList);
					LOGGER.debug("GSTIN List is ---> {}", gstinList);
				}
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			List<Object[]> list = q.getResultList();
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ResultList data Converting to Dto",list);
			}
			retList = list.stream().map(o -> convert(o, action))
					.collect(Collectors.toCollection(ArrayList::new));

			Map<String,List<TDSProcessSummaryRespDto>> map = agregateMap(retList);
			
		
			map.forEach((key,value)->{
				
				BigDecimal taxable = BigDecimal.ZERO;
				BigDecimal intigratedTax = BigDecimal.ZERO;
				BigDecimal centralTax = BigDecimal.ZERO;
				BigDecimal stateUtTax = BigDecimal.ZERO;
			
				
				TDSProcessSummaryRespDto mapValue = new TDSProcessSummaryRespDto();
				
				List<TDSProcessSummaryRespDto> valueresults = map.get(key);
				
				for(TDSProcessSummaryRespDto result : valueresults)
				{
				/*valueresults.forEach(result ->{
				*/		
					taxable = taxable.add(result.getTaxableValue());
					intigratedTax = intigratedTax.add(result.getIntigratedTax());
					centralTax = centralTax.add(result.getCentralTax());
					stateUtTax = stateUtTax.add(result.getStateUtTax());	
					
					mapValue.setAuthToken(result.getAuthToken());
					mapValue.setState(result.getState());
					mapValue.setGstin(result.getGstin());
					mapValue.setRetPeriod(result.getRetPeriod());
									mapValue.setDocKey(result.getDocKey());
					mapValue.setFillingStatus(result.getFillingStatus());
					mapValue.setFillingtimeStamp(result.getFillingtimeStamp());
					
					
				}//);
				
				mapValue.setCentralTax(centralTax);
				mapValue.setStateUtTax(stateUtTax);
				mapValue.setIntigratedTax(intigratedTax);
				mapValue.setTaxableValue(taxable);

				listValue.add(mapValue);
				
				
			});
				
			
			
			
			
			// TDSProcessedRecordsCommonUtil.convertTDSCalcuDataToResp(retList);

			tdsProcessedRecordsCommonUtil.fillTheDataFromDataSecAttr(listValue,
					gstinList,taxPeriod,action);

			convertTDSCalcuDataToResp = tdsProcessedRecordsCommonUtil.convertTDSCalcuDataToResp(listValue,taxPeriod,
					action);

			
			 collectSortingList = convertTDSCalcuDataToResp
					.stream()
					.sorted(Comparator
							.comparing(TDSProcessSummaryRespDto::getGstin))
					.collect(Collectors.toList());
			
			// LOGGER.debug("After Execution getting the data ----->" +
			// retList);
			// return retList;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Executing TDS Query Issue -----> {}", e);
			LOGGER.error("Executing TDS Query Issue -----> {}" + action
					+ "Given Error {}", e);
		}
		return collectSortingList;
	}

	
	
	public Map<String,List<TDSProcessSummaryRespDto>> agregateMap(List<TDSProcessSummaryRespDto> dtos){
		
		Map<String,List<TDSProcessSummaryRespDto>> map = new HashMap<>();
		
		dtos.forEach(dto->{
			String docKeyPs = dto.getDocKey();
			if(map.containsKey(docKeyPs)){
				
				List<TDSProcessSummaryRespDto> keyList = map.get(docKeyPs);
				
				keyList.add(dto);
				map.put(docKeyPs, keyList);
				
			}else {
				
				List<TDSProcessSummaryRespDto> keyList = new ArrayList<>();
				
				keyList.add(dto);
				map.put(docKeyPs, keyList);
				
			}
			
		});
		
		return map;
		
		
		
	}
	
	@SuppressWarnings("static-access")
	private TDSProcessSummaryRespDto convert(Object[] arr, String action) {
		// ITC04ProcessSummaryRespDto obj = new ITC04ProcessSummaryRespDto();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Array data Setting to Dto");
		}
		TDSProcessSummaryRespDto dto = new TDSProcessSummaryRespDto();
		if (arr.length > 0) {

			String gstin = String.valueOf(arr[0]);
			dto.setGstin(gstin);
			String retPeriod = String.valueOf(arr[2]);
			dto.setTaxableValue((BigDecimal) arr[3]);
			dto.setIntigratedTax((BigDecimal) arr[4]);
			dto.setCentralTax((BigDecimal) arr[5]);
			dto.setStateUtTax((BigDecimal) arr[6]);
			String docKey = String.valueOf(arr[8]);
			dto.setDocKey(docKey);
			String docKeyPs = String.valueOf(arr[7]);
			dto.setDocKeyPs(docKeyPs);
			String gstintoken = defaultGSTNAuthTokenService
					.getAuthTokenStatusForGstin(gstin);
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
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			dto.setState(stateName);

			/**
			 * Calculating FilingStatus and FilingTimeStamp
			 */

			int taxPeriod = GenUtil.convertTaxPeriodToInt(retPeriod);
			List<TDSProcessSummaryRespDto> loadBasicSummarySection = tdsProcessedRecordsCommonUtil
					.loadBasicSummarySection(gstin, taxPeriod, action);

			int notSavedCount = 0;
			int savedCount = 0;
			int notSentCount = 0;
			int errorCount = 0;
			int totalCount = 0;
			String timeStamp = null;

			for (TDSProcessSummaryRespDto listDto : loadBasicSummarySection) {
				notSavedCount = listDto.getNotSavedCount();
				savedCount = listDto.getSavedCount();
				notSentCount = listDto.getNotSentCount();
				errorCount = listDto.getErrorCount();
				totalCount = listDto.getTotalCount();
				timeStamp = listDto.getFillingtimeStamp();
			}
			if (timeStamp != null) {
				dto.setFillingtimeStamp(timeStamp);
			}
			dto.setFillingStatus(tdsProcessedRecordsCommonUtil
					.deriveStatusByTotSavedErrorCount(totalCount, savedCount,
							errorCount, notSentCount, notSavedCount));

		}

		return dto;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery,String action) {
		// TODO Auto-generated method stub
		String queryStr = null;
		if ("TDS".equalsIgnoreCase(action)) {
			
			queryStr = "SELECT * FROM GSTR2X_PS_TDS WHERE " +buildQuery;
			
			/*queryStr = "SELECT GSTIN,DERIVED_RET_PERIOD,RET_PERIOD,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT,SUM(SGST_AMT)SGST_AMT "
					+ "FROM (SELECT GSTIN,DERIVED_RET_PERIOD,"
					+ "RET_PERIOD,IFNULL(SUM(TAXABLE_VALUE ),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT ),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT ),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT ),0) AS SGST_AMT,DOC_KEY "
					+ "FROM GETGSTR2X_TDS_TDSA "
					+ "WHERE IS_DELETE = FALSE AND RECORD_TYPE='TDS' "
					+ "GROUP BY GSTIN, DERIVED_RET_PERIOD,DOC_KEY,  RET_PERIOD "
					+ "UNION ALL "
					+ "SELECT GSTIN,DERIVED_RET_PERIOD,RET_PERIOD,"
					+ "IFNULL(SUM(TAXABLE_VALUE ),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT ),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT ),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT ),0) AS SGST_AMT,DOC_KEY "
					+ "FROM GSTR2X_TDSTCS_GET_FILUPD "
					+ "WHERE IS_DELETE = FALSE "
					+ "AND RECORD_TYPE='TDS' "
					+ "GROUP BY GSTIN,DERIVED_RET_PERIOD,DOC_KEY,RET_PERIOD) "
					+ "WHERE "
					+ buildQuery 
					+ "GROUP BY GSTIN,DERIVED_RET_PERIOD,RET_PERIOD";*/
			
		} else if ("TDSA".equalsIgnoreCase(action)) {
			
			queryStr = "SELECT * FROM GSTR2X_PS_TDSA WHERE " +buildQuery;
			
			/*queryStr = "SELECT GSTIN,DERIVED_RET_PERIOD,RET_PERIOD,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT,"
					+ "SUM(SGST_AMT)SGST_AMT FROM (SELECT GSTIN,"
					+ "DERIVED_RET_PERIOD,RET_PERIOD,"
					+ "IFNULL(SUM(TAXABLE_VALUE ),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT ),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT ),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT ),0) AS SGST_AMT,DOC_KEY "
					+ "FROM GETGSTR2X_TDS_TDSA "
					+ "WHERE IS_DELETE = FALSE AND RECORD_TYPE='TDSA' "
					+ "GROUP BY GSTIN,DERIVED_RET_PERIOD,DOC_KEY,RET_PERIOD "
					+ "UNION ALL "
					+ "SELECT GSTIN,DERIVED_RET_PERIOD,RET_PERIOD,"
					+ "IFNULL(SUM(TAXABLE_VALUE ),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT ),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT ),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT ),0) AS SGST_AMT,DOC_KEY "
					+ "FROM GSTR2X_TDSTCS_GET_FILUPD "
					+ "WHERE IS_DELETE = FALSE "
					+ "AND RECORD_TYPE='TDSA' "
					+ "GROUP BY GSTIN,DERIVED_RET_PERIOD,DOC_KEY,RET_PERIOD) "
					+ "WHERE "
					+ buildQuery
					+ "GROUP BY GSTIN, DERIVED_RET_PERIOD, RET_PERIOD";*/
		} else if ("TCS".equalsIgnoreCase(action)) {
		
			queryStr = "SELECT * FROM GSTR2X_PS_TCS WHERE " +buildQuery;
			
			/*queryStr = "SELECT GSTIN,DERIVED_RET_PERIOD,RET_PERIOD,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT,"
					+ "SUM(SGST_AMT)SGST_AMT "
					+ "FROM (SELECT GSTIN , DERIVED_RET_PERIOD ,RET_PERIOD,"
					+ "IFNULL(SUM(TAXABLE_VALUE ),0) AS TAXABLE_VALUE ,"
					+ "IFNULL(SUM(IGST_AMT ),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT ),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT ),0) AS SGST_AMT "
					+ "FROM GETGSTR2X_TCS_TCSA "
					+ "WHERE IS_DELETE = FALSE "
					+ "AND RECORD_TYPE='TCS' "
					+ "GROUP BY GSTIN,RET_PERIOD, DERIVED_RET_PERIOD "
					+ "UNION ALL "
					+ "SELECT GSTIN , DERIVED_RET_PERIOD ,RET_PERIOD,"
					+ "IFNULL(SUM(TAXABLE_VALUE ),0) AS TAXABLE_VALUE ,"
					+ "IFNULL(SUM(IGST_AMT ),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT ),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT ),0) AS SGST_AMT "
					+ "FROM GSTR2X_TDSTCS_GET_FILUPD "
					+ "WHERE IS_DELETE = FALSE "
					+ "AND RECORD_TYPE='TCS' "
					+ "GROUP BY GSTIN,RET_PERIOD, DERIVED_RET_PERIOD)"
					+ "WHERE "
					+ buildQuery 
					+ "GROUP BY GSTIN,RET_PERIOD, DERIVED_RET_PERIOD";*/
		} else if ("TCSA".equalsIgnoreCase(action)) {
	
			queryStr = "SELECT * FROM GSTR2X_PS_TCSA WHERE " +buildQuery;
			
			/*	queryStr = "SELECT GSTIN,DERIVED_RET_PERIOD,RET_PERIOD,"
					+ "SUM(TAXABLE_VALUE) TAXABLE_VALUE,"
					+ "SUM(IGST_AMT)IGST_AMT,SUM(CGST_AMT)CGST_AMT,"
					+ "SUM(SGST_AMT)SGST_AMT FROM ( "
					+ "SELECT GSTIN, DERIVED_RET_PERIOD,RET_PERIOD,"
					+ "IFNULL(SUM(TAXABLE_VALUE ),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT ),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT ),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT ),0) AS SGST_AMT "
					+ "FROM GETGSTR2X_TCS_TCSA "
					+ "WHERE IS_DELETE = FALSE AND RECORD_TYPE='TCSA' "
					+ "GROUP BY GSTIN,RET_PERIOD, DERIVED_RET_PERIOD "
					+ "UNION ALL "
					+ "SELECT GSTIN,DERIVED_RET_PERIOD,RET_PERIOD,"
					+ "IFNULL(SUM(TAXABLE_VALUE ),0) AS TAXABLE_VALUE,"
					+ "IFNULL(SUM(IGST_AMT ),0) AS IGST_AMT,"
					+ "IFNULL(SUM(CGST_AMT ),0) AS CGST_AMT,"
					+ "IFNULL(SUM(SGST_AMT ),0) AS SGST_AMT "
					+ "FROM GSTR2X_TDSTCS_GET_FILUPD "
					+ "WHERE IS_DELETE = FALSE AND RECORD_TYPE='TCSA' "
					+ "GROUP BY GSTIN,RET_PERIOD, DERIVED_RET_PERIOD) WHERE "
					+ buildQuery 
					+ "GROUP BY GSTIN,RET_PERIOD, DERIVED_RET_PERIOD";
*/		}
		// return queryStr;
		return queryStr;

	}

}
