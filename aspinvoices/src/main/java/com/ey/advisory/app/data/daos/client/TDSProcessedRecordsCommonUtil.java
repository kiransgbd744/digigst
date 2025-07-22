package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.docs.dto.simplified.TDSProcessSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.TDSSummaryRespDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
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
@Component()
public class TDSProcessedRecordsCommonUtil {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	public void fillTheDataFromDataSecAttr(
			List<TDSProcessSummaryRespDto> dataDtoList, List<String> gstinList,int taxPeriod,String action) {

		List<String> dataGstinList = new ArrayList<>();
		dataDtoList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		for (String gstin : gstinList) {
			 int count = checkTheCountByGstinTaxperiod(gstin,taxPeriod,action);
			if (count == 0 || !dataGstinList.contains(gstin)) {
				TDSProcessSummaryRespDto dummy = new TDSProcessSummaryRespDto();
				dummy.setGstin(gstin);
				String status = "NOT INITIATED";
				dummy.setGetTdsStatus(status);
				// dummy.setCount(new BigInteger("0"));
				dummy.setTaxableValue(new BigDecimal("0.0"));
				dummy.setIntigratedTax(new BigDecimal("0.0"));
				dummy.setCentralTax(new BigDecimal("0.0"));
				dummy.setStateUtTax(new BigDecimal("0.0"));

				String stateCode = gstin.substring(0, 2);
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				dummy.setState(stateName);
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						dummy.setAuthToken("Active");
					} else {
						dummy.setAuthToken("Inactive");
					}
				} else {
					dummy.setAuthToken("Inactive");
				}

				List<String> regName = gSTNDetailRepository
						.findRegTypeByGstin(gstin);
				if (regName != null && regName.size() > 0) {
					String regTypeName = regName.get(0);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")
							|| regTypeName.equalsIgnoreCase("regular")) {
						dummy.setRegType("");
					} else {
						dummy.setRegType(regTypeName.toUpperCase());
					}
				} else {
					dummy.setRegType("");
				}

				dataGstinList.add(gstin);
				dataDtoList.add(dummy);
			}
		}

	}

	
	 private int checkTheCountByGstinTaxperiod(String gstin, int taxPeriod,String action)
	 { 
		 List<TDSProcessSummaryRespDto> loadBasicSummarySection =
				 loadBasicSummarySection(gstin,taxPeriod,action);
	  
	  int notSavedCount =0; int savedCount = 0; int notSentCount =0; int
	  errorCount = 0; int totalCount = 0;int totalResult=0;
	  
	  for(TDSProcessSummaryRespDto dto : loadBasicSummarySection){
	  notSavedCount = dto.getNotSavedCount(); savedCount = dto.getSavedCount();
	  notSentCount = dto.getNotSentCount(); errorCount = dto.getErrorCount();
	  totalCount = dto.getTotalCount();
	  totalResult = dto.getTotalResult();
	  }
	  
	  int total = notSavedCount+savedCount+notSentCount+errorCount+totalCount;
	  return totalResult; 
	  }
	 
	public List<TDSProcessSummaryRespDto> loadBasicSummarySection(String gstin,
			int taxPeriod, String action) {
		// TODO Auto-generated method stub

		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			build.append(" HDR.CTIN IN (:gstin) ");
		}
		if (taxPeriod != 0) {

			build.append(" AND HDR.DERIVED_RET_PERIOD = :taxPeriod  ");
		}

		String buildQuery = build.toString();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN");
		}

		String queryStr = createQueryString(buildQuery, action);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}
		List<TDSProcessSummaryRespDto> retList = new ArrayList<>();
		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				q.setParameter("gstin", gstin);
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ResultList data Converting to Dto");
			}
			retList = list.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));

			// return retList;
		} catch (Exception e) {
			LOGGER.error("Exception is when Calculating Counts", e);
		}
		return retList;

	}

	private TDSProcessSummaryRespDto convert(Object[] arr) {
		// ITC04ProcessSummaryRespDto obj = new ITC04ProcessSummaryRespDto();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Array data Setting to Dto");
		}
		TDSProcessSummaryRespDto dto = new TDSProcessSummaryRespDto();
		// BigInteger i = GenUtil.getBigInteger(arr[0]);
		dto.setNotSentCount((GenUtil.getBigInteger(arr[0])).intValue());
		dto.setSavedCount((GenUtil.getBigInteger(arr[1])).intValue());
		dto.setNotSavedCount((GenUtil.getBigInteger(arr[2])).intValue());
		dto.setErrorCount((GenUtil.getBigInteger(arr[3])).intValue());
		dto.setTotalCount((GenUtil.getBigInteger(arr[4])).intValue());
		dto.setTotalResult((GenUtil.getBigInteger(arr[5])).intValue());
		Timestamp date = arr[6] != null ? (Timestamp) arr[6] : null;

		// (Timestamp) arr[5];
		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			dto.setFillingtimeStamp(newdate);
		}

		return dto;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createQueryString(String buildQuery, String action) {
		// TODO Auto-generated method stub
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Outward Query Execution BEGIN ");
		}

		String queryStr = null;

		if ("TDS".equalsIgnoreCase(action)) {
			queryStr = "SELECT SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT,"
					+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT,"
					+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
					+ "SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
					+ "SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP,"
					+ "COUNT(TOTAL_COUNT_IN_ASP) TOTAL_RESULTSET,"
					+ "MAX(MODIFIED_ON) FILING_TIMESTAMP "
					+ "FROM ( SELECT COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT,"
					+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
					+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT,"
					+ "COUNT(CASE WHEN GSTN_ERROR = TRUE AND "
					+ "HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_ERROR_COUNT,"
					+ "COUNT(CASE WHEN HDR.IS_DELETE=FALSE THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP,"
					+ "MAX(HDR.MODIFIED_ON) MODIFIED_ON "
					+ "FROM GSTR2X_PROCESSED_TCS_TDS HDR "
					+ "LEFT OUTER JOIN GETGSTR2X_TDS_TDSA GT "
					+ "ON HDR.DOC_KEY=GT.DOC_KEY " + "WHERE " + buildQuery
					+ "AND HDR.IS_DELETE=FALSE AND GT.IS_DELETE=FALSE "
					+ "AND HDR.RECORD_TYPE IN ('TDS'))";

		} else if ("TDSA".equalsIgnoreCase(action)) {
			queryStr = "SELECT SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT,"
					+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT,"
					+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
					+ "SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
					+ "SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP,"
					+ "COUNT(TOTAL_COUNT_IN_ASP) TOTAL_RESULTSET,"
					+ "MAX(MODIFIED_ON) FILING_TIMESTAMP "
					+ "FROM ( SELECT COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT,"
					+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
					+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT,"
					+ "COUNT(CASE WHEN GSTN_ERROR = TRUE AND "
					+ "HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_ERROR_COUNT,"
					+ "COUNT(CASE WHEN HDR.IS_DELETE=FALSE THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP,"
					+ "MAX(HDR.MODIFIED_ON) MODIFIED_ON "
					+ "FROM GSTR2X_PROCESSED_TCS_TDS HDR "
					+ "LEFT OUTER JOIN GETGSTR2X_TDS_TDSA GT "
					+ "ON HDR.DOC_KEY=GT.DOC_KEY " + "WHERE " + buildQuery
					+ " AND HDR.IS_DELETE=FALSE AND GT.IS_DELETE=FALSE "
					+ "AND HDR.RECORD_TYPE IN ('TDSA'))";

		} else if ("TCS".equalsIgnoreCase(action)) {
			queryStr = "SELECT SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT,"
					+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT,"
					+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
					+ "SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
					+ "SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP,"
					+ "COUNT(TOTAL_COUNT_IN_ASP) TOTAL_RESULTSET,"
					+ "MAX(MODIFIED_ON) FILING_TIMESTAMP "
					+ "FROM ( SELECT COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT,"
					+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
					+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT,"
					+ "COUNT(CASE WHEN GSTN_ERROR = TRUE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_ERROR_COUNT,"
					+ "COUNT(CASE WHEN HDR.IS_DELETE=FALSE THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP,"
					+ "MAX(HDR.MODIFIED_ON) MODIFIED_ON "
					+ "FROM GSTR2X_PROCESSED_TCS_TDS HDR "
					+ "LEFT OUTER JOIN GETGSTR2X_TCS_TCSA GT "
					+ "ON HDR.DOC_KEY=GT.DOC_KEY "
					+ "WHERE HDR.IS_DELETE=FALSE AND GT.IS_DELETE=FALSE AND "
					+ buildQuery + "AND HDR.RECORD_TYPE IN ('TCS')) ";
		} else if ("TCSA".equalsIgnoreCase(action)) {
			queryStr = "SELECT SUM(GSTN_NOT_SENT_COUNT) GSTN_NOT_SENT_COUNT,"
					+ "SUM(GSTN_SAVED_COUNT) GSTN_SAVED_COUNT,"
					+ "SUM(GSTN_NOT_SAVED_COUNT) GSTN_NOT_SAVED_COUNT,"
					+ "SUM(GSTN_ERROR_COUNT) GSTN_ERROR_COUNT,"
					+ "SUM(TOTAL_COUNT_IN_ASP) TOTAL_COUNT_IN_ASP,"
					+ "COUNT(TOTAL_COUNT_IN_ASP) TOTAL_RESULTSET,"
					+ "MAX(MODIFIED_ON) FILING_TIMESTAMP "
					+ "FROM ( SELECT COUNT(CASE WHEN IS_SENT_TO_GSTN = FALSE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SENT_COUNT,"
					+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = TRUE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_SAVED_COUNT,"
					+ "COUNT(CASE WHEN IS_SAVED_TO_GSTN = FALSE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_NOT_SAVED_COUNT,"
					+ "COUNT(CASE WHEN GSTN_ERROR = TRUE "
					+ "AND HDR.IS_DELETE = FALSE THEN 1 ELSE NULL END) GSTN_ERROR_COUNT,"
					+ "COUNT(CASE WHEN HDR.IS_DELETE=FALSE THEN 1 ELSE NULL END) TOTAL_COUNT_IN_ASP,"
					+ "MAX(HDR.MODIFIED_ON) MODIFIED_ON "
					+ "FROM GSTR2X_PROCESSED_TCS_TDS HDR "
					+ "LEFT OUTER JOIN GETGSTR2X_TCS_TCSA GT "
					+ "ON HDR.DOC_KEY=GT.DOC_KEY "
					+ "WHERE HDR.IS_DELETE=FALSE AND GT.IS_DELETE=FALSE AND "
					+ buildQuery + "AND HDR.RECORD_TYPE IN ('TCSA')) ";
		}
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug(" Last Updated Date  Query Execution END ");
		}
		return queryStr;
	}

	public List<TDSProcessSummaryRespDto> convertTDSCalcuDataToResp(
			List<TDSProcessSummaryRespDto> sortedGstinDtoList,int taxPeriod, String action) {
		List<TDSProcessSummaryRespDto> finalRespDtos = new ArrayList<>();
		sortedGstinDtoList.stream().forEach(dto -> {
			TDSProcessSummaryRespDto respDto = new TDSProcessSummaryRespDto();
			respDto.setState(dto.getState());
			respDto.setGstin(dto.getGstin());
			respDto.setAuthToken(dto.getAuthToken());
			respDto.setTotalCount(dto.getTotalCount());
			respDto.setDocKey(dto.getDocKey());
			respDto.setRegType(dto.getRegType());
			respDto.setTaxableValue(dto.getTaxableValue());
			respDto.setIntigratedTax(dto.getIntigratedTax());
			respDto.setCentralTax(dto.getCentralTax());
			respDto.setStateUtTax(dto.getStateUtTax());
			respDto.setFillingStatus(dto.getFillingStatus());
			respDto.setFillingtimeStamp(dto.getFillingtimeStamp());
			respDto.setGetTdsStatus((dto.getGetTdsStatus()));
			List<TDSProcessSummaryRespDto> tdsStatus = loadBasicTDSStatusSummarySection(
					respDto.getGstin(), taxPeriod, action);
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("Fetching Status from table ## GetTDSStatus ############",tdsStatus);
			}
			// Calculating TdsStatus
			if(tdsStatus.size()>0 && tdsStatus.get(0).getGetTdsStatus() != null){
			for (TDSProcessSummaryRespDto status : tdsStatus) {
				respDto.setGetTdsStatus((status.getGetTdsStatus()));
				respDto.setGetTdstimeStamp(status.getGetTdstimeStamp());
			}
			}

			// respDto.setTimeStamp(dto.getTimeStamp());
			/*
			 * if (respDto.getGetTdsStatus() == null ||
			 * respDto.getGetTdsStatus().isEmpty()) { respDto.setGetTdsStatus(
			 * deriveStatusByTotSavedErrorCount(dto.getTotalCount(),
			 * dto.getSavedCount(), dto.getErrorCount(), dto.getNotSentCount(),
			 * dto.getNotSavedCount()));
			 * 
			 * }
			 */

			finalRespDtos.add(respDto);
		});
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Checking Status for GetTDSStatus ############",finalRespDtos);
		}
		return finalRespDtos;
	}

	static String deriveStatusByTotSavedErrorCount(int totalCount,
			int savedCount, int errorCount, int notSentCount,
			int notSavedCount) {

		if (totalCount != 0) {
			if (totalCount == notSentCount) {
				return "NOT INITIATED";
			} else if (totalCount == savedCount) {
				return "SAVED";
			} else if (totalCount == errorCount) {
				return "FAILED";
			} else {
				return "PARTIALLY SAVED";
			}
		} else {
			return "NOT INITIATED";
		}
	}

	/**
	 * TDS Review Summary
	 */
	public void fillTheDataFromDataSecAttrForTds(
			List<TDSSummaryRespDto> dataDtoList, List<String> gstinList,
			int taxPeriod, String action) {

		List<String> dataGstinList = new ArrayList<>();
		dataDtoList.forEach(dto -> dataGstinList.add(dto.getGstinDeductee()));
		for (String gstin : gstinList) {
			 int count = checkTheCountByGstinTaxperiod(gstin, taxPeriod,action);
			if (count == 0 || !dataGstinList.contains(gstin)) {
				TDSSummaryRespDto dummy = new TDSSummaryRespDto();
				if ("TDS".equalsIgnoreCase(action)) {
					// TDSSummaryRespDto dummy = new TDSSummaryRespDto();

					dummy.setGstinDeductee(gstin);
					dummy.setMofDeductorUpld("");
					dummy.setTotamount(new BigDecimal("0.0"));
					dummy.setAmountIgst(new BigDecimal("0.0"));
					dummy.setAmountCgst(new BigDecimal("0.0"));
					dummy.setAmountSgst(new BigDecimal("0.0"));
					dummy.setSavedAction("NOT INITIATED");
					dummy.setUserAction("");
					dataGstinList.add(gstin);
					dataDtoList.add(dummy);
				} else if ("TDSA".equalsIgnoreCase(action)) {
					// TDSSummaryRespDto dummy = new TDSSummaryRespDto();

					// gstin = String.valueOf(arr[0]);
					dummy.setGstinDeductee(gstin);

					dummy.setMofDeductorUpld("");
					dummy.setOrgMonOfDeductorUpld("");
					dummy.setTotamount(new BigDecimal("0.0"));
					dummy.setAmountIgst(new BigDecimal("0.0"));
					dummy.setAmountCgst(new BigDecimal("0.0"));
					dummy.setAmountSgst(new BigDecimal("0.0"));
					dummy.setUserAction("");
					dummy.setSavedAction("");
					dataGstinList.add(gstin);
					dataDtoList.add(dummy);
				} else if ("TCS".equalsIgnoreCase(action)) {
					// TDSSummaryRespDto dummy = new TDSSummaryRespDto();

					dummy.setGstinOfColectr(gstin);
					dummy.setMonOfcollectorUpld("");
					dummy.setTotamount(new BigDecimal("0.0"));
					dummy.setSuppliesToRegisteredBuyers(new BigDecimal("0.0"));
					dummy.setSuppliesReturnedbyRegisteredBuyers(new BigDecimal("0.0"));
					dummy.setSuppliestoURbuyers(new BigDecimal("0.0"));
					dummy.setSuppliesReturnedbyURbuyers(new BigDecimal("0.0"));
					dummy.setTotamount(new BigDecimal("0.0"));
					dummy.setAmountIgst(new BigDecimal("0.0"));
					dummy.setAmountCgst(new BigDecimal("0.0"));
					dummy.setAmountSgst(new BigDecimal("0.0"));
					dummy.setSavedAction("NOT INITIATED");
					dummy.setUserAction("");
					dataGstinList.add(gstin);
					dataDtoList.add(dummy);
				} else if ("TCSA".equalsIgnoreCase(action)) {
					// TDSSummaryRespDto dummy = new TDSSummaryRespDto();

					dummy.setGstinOfColectr(gstin);
					dummy.setMonOfcollectorUpld("");
					dummy.setOrgmonOfcollectorUpld("");
					dummy.setTotamount(new BigDecimal("0.0"));
					dummy.setSuppliesToRegisteredBuyers(new BigDecimal("0.0"));
					dummy.setSuppliesReturnedbyRegisteredBuyers(new BigDecimal("0.0"));
					dummy.setSuppliestoURbuyers(new BigDecimal("0.0"));
					dummy.setSuppliesReturnedbyURbuyers(new BigDecimal("0.0"));
					dummy.setTotamount(new BigDecimal("0.0"));
					dummy.setAmountIgst(new BigDecimal("0.0"));
					dummy.setAmountCgst(new BigDecimal("0.0"));
					dummy.setAmountSgst(new BigDecimal("0.0"));
					dummy.setSavedAction("NOT INITIATED");
					dummy.setUserAction("");
					dataGstinList.add(gstin);
					dataDtoList.add(dummy);
				}

			}
		}

	}

	/**
	 * Calculating TDS Status And Time Stamp
	 */

	public List<TDSProcessSummaryRespDto> loadBasicTDSStatusSummarySection(
			String gstin, int taxPeriod, String action) {
		// TODO Auto-generated method stub

		StringBuilder build = new StringBuilder();

		if (gstin != null && !gstin.isEmpty()) {
			build.append(" GSTIN IN (:gstin) ");
		}
		if (taxPeriod != 0) {

			build.append(" AND DERIVED_RET_PERIOD = :taxPeriod  ");
		}

		String buildQuery = build.toString();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug(
				"Prepared where Condition and apply in Outward Query BEGIN",buildQuery);
		}

		String queryStr = createTdsStatusQueryString(buildQuery);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Query For Sections is -->" + queryStr);
		}
		List<TDSProcessSummaryRespDto> retList = new ArrayList<>();
		try {
			Query q = entityManager.createNativeQuery(queryStr);

			if (gstin != null && !gstin.isEmpty()) {
				q.setParameter("gstin", gstin);
			}
			if (taxPeriod != 0) {
				q.setParameter("taxPeriod", taxPeriod);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ResultList data Converting to Dto",list);
			}
			retList = list.parallelStream().map(o -> convertTdsStatus(o))
					.collect(Collectors.toCollection(ArrayList::new));

			// return retList;
		} catch (Exception e) {
			LOGGER.error("Exception is when Calculating Counts", e);
		}
		return retList;

	}

	private TDSProcessSummaryRespDto convertTdsStatus(Object[] arr) {
		// ITC04ProcessSummaryRespDto obj = new ITC04ProcessSummaryRespDto();
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Array data Setting to Dto");
		}
		TDSProcessSummaryRespDto dto = new TDSProcessSummaryRespDto();

		dto.setGetTdsStatus((String) arr[0]);
		Timestamp date = arr[1] != null ? (Timestamp) arr[1] : null;

		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			dto.setGetTdstimeStamp(newdate);
		}

		return dto;
	}

	/**
	 * @param buildQuery
	 * @return
	 */
	private String createTdsStatusQueryString(String buildQuery) {
		// TODO Auto-generated method stub
		if(LOGGER.isDebugEnabled()){
		LOGGER.debug("Outward Query Execution BEGIN ");
		}

		String queryStr = "SELECT MAX(STATUS)STATUS,"
				+ "MAX(START_TIME)START_TIME FROM GETANX1_BATCH_TABLE WHERE "
				+ buildQuery + "AND API_SECTION = 'GSTR2X' "
				+ "AND GET_TYPE='TCSANDTDS' AND IS_DELETE = FALSE";

		LOGGER.debug(" Last Updated Date  Query Execution END ");
		return queryStr;
	}

}
