package com.ey.advisory.app.data.daos.client;
/**
 * @author Balakrishna.S
 */

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.caches.ehcache.Ehcachegstin;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ARRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ATARepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1vs3BComputeRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsRespDto;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EinvProcessedRecordsCommonUtil {

	private static final String NOT_INITIATED = "NOT_INITIATED";

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;


	@Autowired
	@Qualifier("GstnSubmitRepository")
	private GstnSubmitRepository gstnSubmitRepository;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("Gstr1Vs3bStatusRepository") private Gstr1Vs3bStatusRepository
	 * gstr1Vs3bStatusRepository;
	 */
	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository getAnx1BatchRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("GstinAPIAuthInfoRepository")
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository gstr1BatchRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwarddocRepository;

	@Autowired
	@Qualifier("Gstr1B2CSRepository")
	private Gstr1B2CSRepository gstr1B2CSRepository;

	@Autowired
	@Qualifier("Gstr1ARRepository")
	private Gstr1ARRepository gstr1ARRepository;

	@Autowired
	@Qualifier("Gstr1ATARepository")
	private Gstr1ATARepository gstr1ATARepository;

	@Autowired
	@Qualifier("Ehcachegstin")
	private Ehcachegstin ehcachegstin;

	@Autowired
	@Qualifier("Gstr1vs3BComputeRepository")
	Gstr1vs3BComputeRepository gstr1vs3bRepo;

	public List<Gstr1ProcessedRecordsRespDto> convertEinvRecordsIntoObject(
			List<Object[]> outDataArray) throws Exception {
		List<Gstr1ProcessedRecordsRespDto> outList = new ArrayList<Gstr1ProcessedRecordsRespDto>();

		if (!outDataArray.isEmpty()) {
			for (Object obj[] : outDataArray) {
				Gstr1ProcessedRecordsRespDto dto = new Gstr1ProcessedRecordsRespDto();

				String GSTIN = String.valueOf(obj[0]);
				// String returnPeriod = String.valueOf(obj[1]);
				dto.setGstin(GSTIN);
				
				int returnPeriod = (int) obj[1];
				
				String tabStatus = getTabStatus(GSTIN,returnPeriod);
				
				dto.setStatus(tabStatus);
				
				String loadSaveGstnStatus = loadSaveGstnStatus(GSTIN,returnPeriod);
				
				if(loadSaveGstnStatus != null){
					
					dto.setTimeStamp(loadSaveGstnStatus);
				}
				
				// dto.setReturnPeriod(returnPeriod);
				// dto.setDocType(String.valueOf(obj[3]));
				 dto.setTaxDocType(String.valueOf(obj[2]));
				String stateCode = GSTIN.substring(0, 2);
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				dto.setState(stateName);
				List<String> regName = gSTNDetailRepository
						.findRegTypeByGstin(GSTIN);
				if (regName != null && regName.size() > 0) {
					String regTypeName = regName.get(0);
					if (regTypeName == null
							|| regTypeName.equalsIgnoreCase("normal")
							|| regTypeName.equalsIgnoreCase("regular")) {
						dto.setRegType("");
					} else {
						dto.setRegType(regTypeName.toUpperCase());
					}
				} else {
					dto.setRegType("");
				}

				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(GSTIN);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						dto.setAuthToken("Active");
					} else {
						dto.setAuthToken("Inactive");
					}
				} else {
					dto.setAuthToken("Inactive");
				}

				BigInteger integer = GenUtil.getBigInteger(obj[3]);
				dto.setCount(integer);
				dto.setSupplies((BigDecimal) obj[4]);
				dto.setIgst((BigDecimal) obj[5]);
				dto.setCgst((BigDecimal) obj[6]);
				dto.setSgst((BigDecimal) obj[7]);
				dto.setCess((BigDecimal) obj[8]);
				/*
				 * int totalCount, savedCount, errorCount, notSentCount,
				 * notSavedCount = 0; notSentCount = ((BigInteger)
				 * obj[8]).intValue(); savedCount = ((BigInteger)
				 * obj[9]).intValue(); notSavedCount = ((BigInteger)
				 * obj[10]).intValue(); errorCount = ((BigInteger)
				 * obj[11]).intValue(); totalCount = ((BigInteger)
				 * obj[12]).intValue(); dto.setTotalCount(totalCount);
				 * dto.setNotSavedCount(notSavedCount);
				 * dto.setNotSentCount(notSentCount);
				 * dto.setSavedCount(savedCount); dto.setErrorCount(errorCount);
				 */
				/*
				 * List<String> findStatusP = gstnSubmitRepository
				 * .findStatusP(GSTIN, returnPeriod); if
				 * (!findStatusP.isEmpty()) { dto.setStatus("SUBMITTED"); } else
				 * { dto.setStatus(""); } if (obj[14] == null || obj[14] ==
				 * "null") { dto.setTimeStamp(""); } else { Timestamp date =
				 * (Timestamp) obj[14]; LocalDateTime dt =
				 * date.toLocalDateTime(); LocalDateTime dateTimeFormatter =
				 * EYDateUtil .toISTDateTimeFromUTC(dt); DateTimeFormatter
				 * FOMATTER = DateTimeFormatter
				 * .ofPattern("dd-MM-yyyy : HH:mm:ss"); String newdate =
				 * FOMATTER.format(dateTimeFormatter);
				 * 
				 * dto.setTimeStamp(newdate); }
				 */ outList.add(dto);
			}
		}
		return outList;

	}

	private static String deriveStatusByTotSavedErrorCount(int totalCount,
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

	public List<Gstr1ProcessedRecordsRespDto> convertCalcuDataToResp(
			List<Gstr1ProcessedRecordsRespDto> sortedGstinDtoList) {
		List<Gstr1ProcessedRecordsRespDto> finalRespDtos = new ArrayList<>();
		sortedGstinDtoList.stream().forEach(dto -> {
			Gstr1ProcessedRecordsRespDto respDto = new Gstr1ProcessedRecordsRespDto();
			respDto.setState(dto.getState());
			respDto.setGstin(dto.getGstin());
			respDto.setReturnPeriod(dto.getReturnPeriod());
			respDto.setRegType(dto.getRegType());
			respDto.setAuthToken(dto.getAuthToken());
			respDto.setCount(dto.getCount());
			respDto.setSupplies(dto.getSupplies());
			respDto.setIgst(dto.getIgst());
			respDto.setCgst(dto.getCgst());
			respDto.setSgst(dto.getSgst());
			respDto.setCess(dto.getCess());
			respDto.setStatus(dto.getStatus());
			respDto.setTimeStamp(dto.getTimeStamp());
		
			String tabStatus = "";
			if (respDto.getStatus() == null && respDto.getStatus().isEmpty()) {
				 tabStatus = getTabStatus(dto.getGstin(),GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
				 respDto.setStatus(tabStatus);
			}
			if ("NOT INITIATED".equalsIgnoreCase(respDto.getStatus())) {

				respDto.setTimeStamp("");
			} else {
				respDto.setTimeStamp(dto.getTimeStamp());
			}

		//	String loadSaveGstnStatus = loadSaveGstnStatus(dto.getGstin(),GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		//	respDto.setTimeStamp(loadSaveGstnStatus);
		
			finalRespDtos.add(respDto);
		});
		return finalRespDtos;
	}
	
	
	public void createMapByGstinBasedOnType(
			Map<String, List<Gstr1ProcessedRecordsRespDto>> outMap,
			List<Gstr1ProcessedRecordsRespDto> outRespDtoList) {
		outRespDtoList.stream().forEach(dto -> {
			outMap.put(dto.getGstin(),
					outRespDtoList.stream().filter(
							resp -> resp.getGstin().equals(dto.getGstin()))
							.collect(Collectors.toList()));
		});

	}

	public void calculateDataByDocType(
			Map<String, List<Gstr1ProcessedRecordsRespDto>> outMap,
			List<Gstr1ProcessedRecordsRespDto> outDistList) {
		outMap.keySet().forEach(gstinKey -> {
			List<Gstr1ProcessedRecordsRespDto> dtoList = outMap.get(gstinKey);
			Gstr1ProcessedRecordsRespDto respDto = new Gstr1ProcessedRecordsRespDto();
			String state = "";
			String gstins = "";
			String retPeriod = "";
			String regType = "";
			String taxDocType = "";
			String authToken = "";
			BigInteger count = BigInteger.ZERO;
			BigDecimal outSupplies = BigDecimal.ZERO;
			BigDecimal igst = BigDecimal.ZERO;
			BigDecimal cgst = BigDecimal.ZERO;
			BigDecimal sgst = BigDecimal.ZERO;
			BigDecimal cess = BigDecimal.ZERO;
			String timeStamp = "";
			String status = "";

		/*	int totalCount = 0;
			int savedCount = 0;
			int errorCount = 0;
			int notSentCount = 0;
			int notSavedCount = 0;
*/
			for (Gstr1ProcessedRecordsRespDto dto : dtoList) {
				state = dto.getState();
				gstins = dto.getGstin();
				retPeriod = dto.getReturnPeriod();
				regType = dto.getRegType();
				authToken = dto.getAuthToken();
				timeStamp = dto.getTimeStamp();
				status = dto.getStatus();
				taxDocType = dto.getTaxDocType();

				/*totalCount = totalCount + dto.getTotalCount();
				savedCount = savedCount + dto.getSavedCount();
				errorCount = errorCount + dto.getErrorCount();
				notSentCount = notSentCount + dto.getNotSentCount();
				notSavedCount = notSavedCount + dto.getNotSavedCount();*/
				count = count.add((dto.getCount() != null
						&& dto.getCount().intValue() > 0) ? dto.getCount()
								: BigInteger.ZERO);
				if (taxDocType != null && taxDocType.equals("ADV ADJ")) {
					outSupplies = outSupplies
							.subtract((dto.getSupplies() != null
									&& dto.getSupplies().intValue() > 0)
											? dto.getSupplies()
											: BigDecimal.ZERO);
					cess = cess.subtract((dto.getCess() != null
							&& dto.getCess().intValue() > 0) ? dto.getCess()
									: BigDecimal.ZERO);
					igst = igst.subtract((dto.getIgst() != null
							&& dto.getIgst().intValue() > 0) ? dto.getIgst()
									: BigDecimal.ZERO);
					cgst = cgst.subtract((dto.getCgst() != null
							&& dto.getCgst().intValue() > 0) ? dto.getCgst()
									: BigDecimal.ZERO);
					sgst = sgst.subtract((dto.getSgst() != null
							&& dto.getSgst().intValue() > 0) ? dto.getSgst()
									: BigDecimal.ZERO);
				} else {
					outSupplies = outSupplies.add((dto.getSupplies() != null)
							? dto.getSupplies() : BigDecimal.ZERO);
					cess = cess.add((dto.getCess() != null) ? dto.getCess()
							: BigDecimal.ZERO);
					igst = igst.add((dto.getIgst() != null) ? dto.getIgst()
							: BigDecimal.ZERO);
					cgst = cgst.add((dto.getCgst() != null) ? dto.getCgst()
							: BigDecimal.ZERO);
					sgst = sgst.add((dto.getSgst() != null) ? dto.getSgst()
							: BigDecimal.ZERO);
				}

			}

			respDto.setState(state);
			respDto.setGstin(gstins);
			respDto.setReturnPeriod(retPeriod);
			respDto.setRegType(regType);
			respDto.setAuthToken(authToken);
			respDto.setCount(count);
			respDto.setSupplies(outSupplies);
			respDto.setIgst(igst);
			respDto.setCgst(cgst);
			respDto.setSgst(sgst);
			respDto.setTimeStamp(timeStamp);
			respDto.setCess(cess);
			respDto.setStatus(status);
		/*	respDto.setTotalCount(totalCount);
			respDto.setNotSavedCount(notSavedCount);
			respDto.setNotSentCount(notSentCount);
			respDto.setSavedCount(savedCount);
			respDto.setErrorCount(errorCount);*/

			outDistList.add(respDto);
		});
	}

	public void fillTheDataFromDataSecAttr(
			List<Gstr1ProcessedRecordsRespDto> dataDtoList,
			List<String> gstinList, String taxPeriodFrom, String taxPeriodTo) {

	//	List<String> thetaxPeriods = getThetaxPeriods(taxPeriodFrom,taxPeriodTo);
		
		List<String> dataGstinList = new ArrayList<>();
		dataDtoList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		for (String gstin : gstinList) {
			// int count = checkTheCountByGstinTaxperiod(gstin, taxPeriod);
			if (!dataGstinList.contains(gstin)) {
				Gstr1ProcessedRecordsRespDto dummy = new Gstr1ProcessedRecordsRespDto();
				dummy.setGstin(gstin);
			//	String status = "NOT INITIATED";
			//	dummy.setStatus(status);
				
				String tabStatus = getTabStatus(gstin,GenUtil.convertTaxPeriodToInt(taxPeriodFrom),GenUtil.convertTaxPeriodToInt(taxPeriodTo));
				
				
					dummy.setStatus(tabStatus);
	
				
				String loadSaveGstnStatus = loadSaveGstnStatus(gstin,GenUtil.convertTaxPeriodToInt(taxPeriodFrom),GenUtil.convertTaxPeriodToInt(taxPeriodTo));
				
				if(loadSaveGstnStatus != null){
					
					dummy.setTimeStamp(loadSaveGstnStatus);
				}
				
				dummy.setCount(new BigInteger("0"));
				dummy.setSupplies(new BigDecimal("0.0"));
				dummy.setIgst(new BigDecimal("0.0"));
				dummy.setCgst(new BigDecimal("0.0"));
				dummy.setSgst(new BigDecimal("0.0"));
				dummy.setCess(new BigDecimal("0.0"));
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

	private int checkTheCountByGstinTaxperiod(String gstin, String taxPeriod) {
		int count = 0;
		int docCount = docRepository.gstinCount(gstin, taxPeriod);
		int b2csCount = gstr1B2CSRepository.gstinCount(gstin, taxPeriod);
		int arCount = gstr1ARRepository.gstinCount(gstin, taxPeriod);
		int ataCount = gstr1ATARepository.gstinCount(gstin, taxPeriod);
		count = count + docCount + b2csCount + arCount + ataCount;
		return count;
	}

/**
 * 
 * @param gstn
 * @param taxPeriodFrom
 * @param taxPeriodTo
 * @return
 */
	
	public String getTabStatus(String gstinList, int taxPeriod) {

		StringBuilder queryBuilder = new StringBuilder();

		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			queryBuilder.append(" AND GSTIN = :gstinList ");
		}

		String buildQuery = queryBuilder.toString();

		String queryStr = "SELECT STATUS FROM (SELECT GSTIN,RETURN_PERIOD,DENSE_RANK() "
				+ "OVER(PARTITION BY GSTIN,GET_TYPE ORDER BY START_TIME DESC) "
				+ "AS NUM, GET_TYPE, START_TIME, STATUS "
				+ "FROM GETANX1_BATCH_TABLE GBT WHERE "
				+ "API_SECTION='GSTR1_EINV' AND GET_TYPE IN ('B2B','CDNR','CDNUR','EXP','WEB_UPLOAD') "
				+ buildQuery + " ) A WHERE NUM = 1 ";
			//	+ "ORDER BY GET_TYPE, START_TIME DESC ";

		Query Q = entityManager.createNativeQuery(queryStr);
		if (taxPeriod != 0) {
			Q.setParameter("taxPeriod", taxPeriod);
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			Q.setParameter("gstinList", gstinList);
		}

		@SuppressWarnings("unchecked")
		List<String> unqueSatusList = Q.getResultList();

	/*	List<String> statusList = new ArrayList<>();
		Map<String, List<String>> gstinsMap = Maps.newHashMap();
*//*
		List<String> unqueSatusList = resultSet.stream()
				.filter(status1 -> status1 != null)
				.collect(Collectors.toList());*/

		String finalStatus = "NOT INITIATED";

		if (unqueSatusList != null && unqueSatusList.size() > 0) {
			if (CollectionUtils.isNotEmpty(unqueSatusList)) {
				if (unqueSatusList.size() >0 ) {
					if (unqueSatusList.contains("INITIATED")) {
						finalStatus = "INITIATED";
					} else if (unqueSatusList.contains("INPROGRESS")) {
						finalStatus = "INPROGRESS";
					} else if (unqueSatusList.contains("SUCCESS") 
							&& unqueSatusList.contains("FAILED") 
							&& unqueSatusList
									.contains("SUCCESS_WITH_NO_DATA")) {
						finalStatus = "PARTIALLY_SUCCESS";
					} else if (unqueSatusList.contains("FAILED") 
							&& unqueSatusList.contains("SUCCESS_WITH_NO_DATA")) {
						finalStatus = "PARTIALLY_SUCCESS";
					} else if (unqueSatusList.contains("FAILED")
							&& unqueSatusList.contains("SUCCESS")) {
						finalStatus = "PARTIALLY_SUCCESS";
					}else if (unqueSatusList.contains("SUCCESS")
							&& unqueSatusList.contains("SUCCESS_WITH_NO_DATA")) {
						finalStatus = "SUCCESS";
					}else if (unqueSatusList.contains("SUCCESS")) {
						finalStatus = "SUCCESS";
					}
					else if (unqueSatusList.contains("SUCCESS_WITH_NO_DATA")) {
						finalStatus = "SUCCESS";
					}
					 else if (unqueSatusList.contains("FAILED")) {
							finalStatus = "FAILED";
						}
				} else {
					finalStatus = "PARTIALLY_SUCCESS";
				}
			}
		}

		return finalStatus;

	}
	
	/**
	 * Finding the Max(START_TIME) 
	 * @param request
	 * @return
	 */
	
	public String loadSaveGstnStatus(String gstinList, int taxPeriod) {
		// TODO Auto-generated method stub

		String lastUpdatedDate = "";
		StringBuilder queryBuilder = new StringBuilder();

		if (taxPeriod != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD = :taxPeriod ");
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			queryBuilder.append(" AND GSTIN = :gstinList ");
		}

		String buildQuery = queryBuilder.toString();
		
		
		String queryStr = "select MAX(START_TIME) FROM "
				+ "GETANX1_BATCH_TABLE GBT WHERE "
				+ "API_SECTION='GSTR1_EINV' AND GET_TYPE IN "
				+ "('B2B','CDNR','CDNUR','EXP','WEB_UPLOAD') "
				+ buildQuery ;
		
		Query Q = entityManager.createNativeQuery(queryStr);
		
		if (taxPeriod != 0) {
			Q.setParameter("taxPeriod", taxPeriod);
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			Q.setParameter("gstinList", gstinList);
		}
		
		try{
			@SuppressWarnings("unchecked")
			List<Object> list = Q.getResultList();
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ResultList data Converting to Dto");
			}

			if (list != null && !list.isEmpty() && list.get(0) != null) {

				// LocalDateTime localdateTime = (LocalDateTime) list.get(0);
				// Timestamp t = (Timestamp) list.get(0);
				LocalDateTime localdateTime = ((Timestamp) list.get(0))
						.toLocalDateTime();
				if (localdateTime != null) {

					LocalDateTime istDateTimeFromUTC = EYDateUtil
							.toISTDateTimeFromUTC(localdateTime);

					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");

					lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);

				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
		}
		return lastUpdatedDate;
	}
	
	
	/**
	 * This is for Status For Default Values
	 */
	
	private static List<String> getThetaxPeriods(String fromTaxPeriod,String toTaxPeriod){

		List<String> taxPeriodList = new LinkedList<>();
		LocalDate startDate = LocalDate.of(Integer.parseInt(fromTaxPeriod.substring(2)),
				Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
		LocalDate endDate = LocalDate.of(Integer.parseInt(toTaxPeriod.substring(2)),
				Integer.parseInt(toTaxPeriod.substring(0, 2)), 01);
		long numOfMonths = ChronoUnit.MONTHS.between(startDate, endDate) + 1;
		if (numOfMonths > 0) {
			List<LocalDate> listOfDates = Stream.iterate(startDate, date -> date.plusMonths(1)).limit(numOfMonths)
					.collect(Collectors.toList());
			listOfDates.forEach(localDate -> taxPeriodList
					.add(localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() + "" + localDate.getYear()
							: localDate.getMonthValue() + "" + localDate.getYear()));
	}

		return taxPeriodList;
		
	}
	
	
	/**
	 * 
	 */
	public String getTabStatus(String gstinList, int taxPeriodFrom,int taxPeriodTo) {

		StringBuilder queryBuilder = new StringBuilder();

		if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD  BETWEEN :taxPeriodFrom AND :taxPeriodTo  ");
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			queryBuilder.append(" AND GSTIN = :gstinList ");
		}

		String buildQuery = queryBuilder.toString();

		String queryStr = "SELECT STATUS FROM (SELECT GSTIN,RETURN_PERIOD,DENSE_RANK() "
				+ "OVER(PARTITION BY GSTIN,GET_TYPE ORDER BY START_TIME DESC) "
				+ "AS NUM, GET_TYPE, START_TIME, STATUS "
				+ "FROM GETANX1_BATCH_TABLE GBT WHERE "
				+ "API_SECTION='GSTR1_EINV' AND GET_TYPE IN ('B2B','CDNR','CDNUR','EXP') "
				+ buildQuery + " ) A WHERE NUM = 1 "
				+ "ORDER BY GET_TYPE, START_TIME DESC ";

		Query Q = entityManager.createNativeQuery(queryStr);
		if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
			Q.setParameter("taxPeriodFrom", taxPeriodFrom);
			Q.setParameter("taxPeriodTo", taxPeriodTo);
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			Q.setParameter("gstinList", gstinList);
		}

		@SuppressWarnings("unchecked")
		List<String> unqueSatusList = Q.getResultList();

	/*	List<String> statusList = new ArrayList<>();
		Map<String, List<String>> gstinsMap = Maps.newHashMap();
*//*
		List<String> unqueSatusList = resultSet.stream()
				.filter(status1 -> status1 != null)
				.collect(Collectors.toList());*/

		String finalStatus = "NOT INITIATED";

		if (unqueSatusList != null && unqueSatusList.size() > 0) {
			if (CollectionUtils.isNotEmpty(unqueSatusList)) {
				if (unqueSatusList.size() >0 ) {
					if (unqueSatusList.contains("INITIATED")) {
						finalStatus = "INITIATED";
					} else if (unqueSatusList.contains("INPROGRESS")) {
						finalStatus = "INPROGRESS";
					} else if (unqueSatusList.contains("SUCCESS")
							|| unqueSatusList
									.contains("SUCCESS_WITH_NO_DATA")) {
						finalStatus = "SUCCESS";
					} else if (unqueSatusList.contains("FAILED")) {
						finalStatus = "FAILED";
					} else if (!unqueSatusList.contains("FAILED")
							&& unqueSatusList.contains("SUCCESS")
							&& unqueSatusList.contains("SUCCESS_WITH_NO_DATA")) {
						finalStatus = "PARTIALLY_SUCCESS";
					}
				} else {
					finalStatus = "PARTIALLY_SUCCESS";
				}
			}
		}

		return finalStatus;

	}
	/**
	 * 
	 */

	/**
	 * Finding the Max(START_TIME) 
	 * @param request
	 * @return
	 */
	
	public String loadSaveGstnStatus(String gstinList, int taxPeriodFrom,int taxPeriodTo) {
		// TODO Auto-generated method stub

		String lastUpdatedDate = "";
		StringBuilder queryBuilder = new StringBuilder();

		if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
			queryBuilder.append(" AND DERIVED_RET_PERIOD  BETWEEN  :taxPeriodFrom AND :taxPeriodTo ");
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			queryBuilder.append(" AND GSTIN = :gstinList ");
		}

		String buildQuery = queryBuilder.toString();
		
		
		String queryStr = "select MAX(START_TIME) FROM "
				+ "GETANX1_BATCH_TABLE GBT WHERE "
				+ "API_SECTION='GSTR1_EINV' AND GET_TYPE IN "
				+ "('B2B','CDNR','CDNUR','EXP') "
				+ buildQuery ;
		
		Query Q = entityManager.createNativeQuery(queryStr);
		
		if (taxPeriodFrom != 0 && taxPeriodTo != 0) {
			Q.setParameter("taxPeriodFrom", taxPeriodFrom);
			Q.setParameter("taxPeriodTo", taxPeriodTo);
		}
		if (gstinList != null && !gstinList.isEmpty()) {
			Q.setParameter("gstinList", gstinList);
		}
		
		try{
			@SuppressWarnings("unchecked")
			List<Object> list = Q.getResultList();
			if(LOGGER.isDebugEnabled()){
			LOGGER.debug("ResultList data Converting to Dto");
			}

			if (list != null && !list.isEmpty() && list.get(0) != null) {

				// LocalDateTime localdateTime = (LocalDateTime) list.get(0);
				// Timestamp t = (Timestamp) list.get(0);
				LocalDateTime localdateTime = ((Timestamp) list.get(0))
						.toLocalDateTime();
				if (localdateTime != null) {

					LocalDateTime istDateTimeFromUTC = EYDateUtil
							.toISTDateTimeFromUTC(localdateTime);

					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("yyyy-MM-dd HH:mm:ss");

					lastUpdatedDate = FOMATTER.format(istDateTimeFromUTC);

				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception", e);
		}
		return lastUpdatedDate;
	}


	
	
	
}


