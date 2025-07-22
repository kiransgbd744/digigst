/**
 * 
 */
package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ARRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ATARepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetAtaGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2bGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2baGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2clGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2claGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2csGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2csaGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnrGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnraGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnurGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnuraGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetExpGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetExpaGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetNilRatedGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetTxpGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetTxpaGstnRepository;
import com.ey.advisory.app.docs.dto.GetProcessedVsSubmittedStatusRespDto;
import com.ey.advisory.app.docs.dto.ProcessedVsSubmittedResponseDto;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ProcessedVsSubmittedRequestDto;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component
@Slf4j
public class ProcessedVsSubmittedRecordsCommonUtil {

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("GstnSubmitRepository")
	private GstnSubmitRepository gstnSubmitRepository;

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
	@Qualifier("GetProcessedVsSubmittedStatusService")
	private GetProcessedVsSubmittedStatusService getProcessedVsSubmittedStatusService;

	@Autowired
	private CommonUtility commonUtility;

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
	@Qualifier("Gstr1GetAtGstnRepository")
	private Gstr1GetAtGstnRepository gstr1GetAtGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetAtaGstnRepository")
	private Gstr1GetAtaGstnRepository gstr1GetAtaGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetTxpGstnRepository")
	private Gstr1GetTxpGstnRepository gstr1GetTxpGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetTxpaGstnRepository")
	private Gstr1GetTxpaGstnRepository gstr1GetTxpaGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2bGstnRepository")
	private Gstr1GetB2bGstnRepository gstr1GetB2bGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2baGstnRepository")
	private Gstr1GetB2baGstnRepository gstr1GetB2baGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2clGstnRepository")
	private Gstr1GetB2clGstnRepository gstr1GetB2clGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2claGstnRepository")
	private Gstr1GetB2claGstnRepository gstr1GetB2claGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2csGstnRepository")
	private Gstr1GetB2csGstnRepository gstr1GetB2csGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetB2csaGstnRepository")
	private Gstr1GetB2csaGstnRepository gstr1GetB2csaGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetCdnrGstnRepository")
	private Gstr1GetCdnrGstnRepository gstr1GetCdnrGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetCdnraGstnRepository")
	private Gstr1GetCdnraGstnRepository gstr1GetCdnraGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetCdnurGstnRepository")
	private Gstr1GetCdnurGstnRepository gstr1GetCdnurGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetCdnuraGstnRepository")
	private Gstr1GetCdnuraGstnRepository gstr1GetCdnuraGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetExpGstnRepository")
	private Gstr1GetExpGstnRepository gstr1GetExpGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetExpaGstnRepository")
	private Gstr1GetExpaGstnRepository gstr1GetExpaGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetNilRatedGstnRepository")
	private Gstr1GetNilRatedGstnRepository gstr1GetNilRatedGstnRepository;

	@Autowired
	DefaultStateCache defaultStateCache;

	private static final String NOT_INITIATED = "NOT_INITIATED";
	private static final String SUCCESS_WITH_NO_DATA = "SUCCESS_WITH_NO_DATA";
	private static final String INPROGRESS = "INPROGRESS";
	private static final String SUCCESS = "SUCCESS";
	private static final String FAILED = "FAILED";
	private static final String INITIATED = "INITIATED";
	private static final String PARTIALLY_SUCCESS = "PARTIALLY_SUCCESS";

	public List<ProcessedVsSubmittedResponseDto> convertGstr1RecordsIntoObject(
			List<Object[]> outDataArray,
			ProcessedVsSubmittedRequestDto prVsSubReqDto,
			List<String> gstinList) throws Exception {
		List<ProcessedVsSubmittedResponseDto> outList = new ArrayList<ProcessedVsSubmittedResponseDto>();

		Map<String, String> gstinsStatusMap = getStatusByCriteria(prVsSubReqDto,
				gstinList);
		Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
				.getGstnRegMap();
		
		List<GSTNDetailEntity> gstinReqList = gSTNDetailRepository
				.findRegTypeByGstinList(gstinList);
		Map<String, GSTNDetailEntity> gstinReqMap = gstinReqList.stream()
				.collect(Collectors.toMap(GSTNDetailEntity::getGstin,
						Function.identity()));

		if (!outDataArray.isEmpty()) {
			for (Object obj[] : outDataArray) {
				ProcessedVsSubmittedResponseDto dto = new ProcessedVsSubmittedResponseDto();

				String GSTIN = String.valueOf(obj[0]);
				// String returnPeriod = String.valueOf(obj[1]);
				dto.setGstin(GSTIN);

				if (gstinsStatusMap.containsKey(GSTIN)) {
					String value[] = gstinsStatusMap.get(GSTIN).split("__");
					if (value[0] != null && !value[0].contains("null")) {
						dto.setGetCallStatus(value[0]);
					}
					String s = value[1];
					if (value[1] != null && !s.contains("null")) {
						dto.setGetCallDateTime(value[1]);
					}
				} else {
					dto.setGetCallStatus(NOT_INITIATED);
				}
				String stateCode = GSTIN.substring(0, 2);
				String stateName = defaultStateCache.getStateName(stateCode);
				dto.setState(stateName);
				String regName = gstinReqMap.get(GSTIN).getRegistrationType();

				if (regName != null && (!regName.equalsIgnoreCase("TDS")
						|| !regName.equalsIgnoreCase("ISD"))) {
					if (regName == null
							|| regName.equalsIgnoreCase("normal")
							|| regName.equalsIgnoreCase("regular")) {
						dto.setRegType("");
					} else {
						dto.setRegType(regName.toUpperCase());
					}
				} else {
					dto.setRegType("");
				}

				String gstintoken = gstnRegMap.getValue0().get(GSTIN);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						dto.setAuthToken("Active");
					} else {
						dto.setAuthToken("Inactive");
					}
				} else {
					dto.setAuthToken("Inactive");
				}

				BigInteger aprCount = GenUtil.getBigInteger(obj[1]);
				dto.setAprCount(aprCount);
				dto.setAprTaxableValue((BigDecimal) obj[2]);
				dto.setAprTotalTax((BigDecimal) obj[3]);
				BigInteger asrCount = GenUtil.getBigInteger(obj[10]);
				dto.setAsrCount(asrCount);
				dto.setAsrTaxableValue((BigDecimal) obj[11]);
				dto.setAsrTotalTax((BigDecimal) obj[12]);

				BigInteger totalCount, savedCount, errorCount, notSentCount,
						notSavedCount = new BigInteger("0");
				notSentCount = (GenUtil.getBigInteger(obj[4]));
				savedCount = (GenUtil.getBigInteger(obj[5]));
				notSavedCount = (GenUtil.getBigInteger(obj[6]));
				errorCount = (GenUtil.getBigInteger(obj[7]));
				totalCount = (GenUtil.getBigInteger(obj[8]));
				dto.setTotalCount(totalCount);
				dto.setNotSavedCount(notSavedCount);
				dto.setNotSentCount(notSentCount);
				dto.setSavedCount(savedCount);
				dto.setErrorCount(errorCount);
				/*
				 * List<String> findStatusP = gstnSubmitRepository
				 * .findStatus(GSTIN, returnPeriod);
				 */
				List<String> findStatusP = gstnSubmitRepository
						.findStatus(GSTIN);
				if (!findStatusP.isEmpty()) {
					dto.setFilingStatus("SUBMITTED");
				} else {
					dto.setFilingStatus("");
				}
				if (obj[9] == null || obj[9] == "null") {
					dto.setFilingDateTime("");
				} else {
					Timestamp date = (Timestamp) obj[9];
					LocalDateTime dt = date.toLocalDateTime();
					LocalDateTime dateTimeFormatter = EYDateUtil
							.toISTDateTimeFromUTC(dt);
					DateTimeFormatter FOMATTER = DateTimeFormatter
							.ofPattern("dd-MM-yyyy : HH:mm:ss");
					String newdate = FOMATTER.format(dateTimeFormatter);

					dto.setFilingDateTime(newdate);
				}
				outList.add(dto);
			}
		}
		return outList;
	}

	public static List<ProcessedVsSubmittedResponseDto> convertCalcuDataToResp(
			List<ProcessedVsSubmittedResponseDto> sortedGstinDtoList) {
		List<ProcessedVsSubmittedResponseDto> finalRespDtos = new ArrayList<>();
		sortedGstinDtoList.stream().forEach(dto -> {
			ProcessedVsSubmittedResponseDto respDto = new ProcessedVsSubmittedResponseDto();
			respDto.setState(dto.getState());
			respDto.setGstin(dto.getGstin());
			// respDto.setReturnPeriod(dto.getReturnPeriod());
			respDto.setRegType(dto.getRegType());
			respDto.setAuthToken(dto.getAuthToken());

			respDto.setAprCount(dto.getAprCount());
			respDto.setAprTaxableValue(dto.getAprTaxableValue());
			respDto.setAprTotalTax(dto.getAprTotalTax());
			respDto.setAsrCount(dto.getAsrCount());
			respDto.setAsrTaxableValue(dto.getAsrTaxableValue());
			respDto.setAsrTotalTax(dto.getAsrTotalTax());
			respDto.setFilingDateTime(dto.getFilingDateTime());
			if (respDto.getFilingStatus() == null
					|| respDto.getFilingStatus().isEmpty()) {
				respDto.setFilingStatus(
						deriveStatusByTotSavedErrorCount(dto.getTotalCount(),
								dto.getSavedCount(), dto.getErrorCount(),
								dto.getNotSentCount(), dto.getNotSavedCount()));
			}
			respDto.setGetCallStatus(dto.getGetCallStatus());
			respDto.setGetCallDateTime(dto.getGetCallDateTime());

			finalRespDtos.add(respDto);
		});
		return finalRespDtos;
	}

	private static String deriveStatusByTotSavedErrorCount(
			BigInteger totalCount, BigInteger savedCount, BigInteger errorCount,
			BigInteger notSentCount, BigInteger notSavedCount) {
		BigInteger zero = new BigInteger("0");

		if (totalCount != zero) {
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

	public void createMapByGstinBasedOnType(
			Map<String, List<ProcessedVsSubmittedResponseDto>> outMap,
			List<ProcessedVsSubmittedResponseDto> outRespDtoList) {
		outRespDtoList.stream().forEach(dto -> {
			outMap.put(dto.getGstin(),
					outRespDtoList.stream().filter(
							resp -> resp.getGstin().equals(dto.getGstin()))
							.collect(Collectors.toList()));
		});
	}

	public void fillTheDataFromDataSecAttr(
			ProcessedVsSubmittedRequestDto PRReqDto,
			List<ProcessedVsSubmittedResponseDto> dataDtoList,
			List<String> gstinList, int derivedTaxPeriodFrom,
			int derivedTaxPeriodTo) throws Exception {

		List<String> dataGstinList = new ArrayList<>();
		dataDtoList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		Map<String, String> gstinsStatusMap = getStatusByCriteria(PRReqDto,
				gstinList);

		Pair<Map<String, String>, Map<String, String>> gstnRegMap = commonUtility
				.getGstnRegMap();
		List<GSTNDetailEntity> gstinReqList = gSTNDetailRepository
				.findRegTypeByGstinList(gstinList);
		Map<String, GSTNDetailEntity> gstinReqMap = gstinReqList.stream()
				.collect(Collectors.toMap(GSTNDetailEntity::getGstin,
						Function.identity()));

		Map<String, Integer> countMap = checkTheCountByGstinTaxperiod(gstinList,
				derivedTaxPeriodFrom, derivedTaxPeriodTo);
		for (String gstin : gstinList) {

			int count = 0;
			count = (countMap != null && countMap.containsKey(gstin))
					? countMap.get(gstin) : 0;

			if (count == 0 || !dataGstinList.contains(gstin)) {
				ProcessedVsSubmittedResponseDto dummy = new ProcessedVsSubmittedResponseDto();
				dummy.setGstin(gstin);

				if (gstinsStatusMap.containsKey(gstin)) {
					String value[] = gstinsStatusMap.get(gstin).split("__");
					if (value[0] != null && !value[0].contains("null")) {
						dummy.setGetCallStatus(value[0]);
					}
					String s = value[1];
					if (s != null && !s.contains("null")) {
						dummy.setGetCallDateTime(value[1]);
					}
				} else {
					dummy.setGetCallStatus(NOT_INITIATED);
				}
				String status = "NOT INITIATED";
				dummy.setFilingStatus(status);
				dummy.setAprCount(new BigInteger("0"));
				dummy.setAprTaxableValue((new BigDecimal("0.0")));
				dummy.setAprTotalTax((new BigDecimal("0.0")));
				dummy.setAsrCount(new BigInteger("0"));
				dummy.setAsrTaxableValue((new BigDecimal("0.0")));
				dummy.setAsrTotalTax((new BigDecimal("0.0")));

				String stateCode = gstin.substring(0, 2);
				String stateName = defaultStateCache.getStateName(stateCode);
				dummy.setState(stateName);
				String gstintoken = gstnRegMap.getValue0().get(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						dummy.setAuthToken("Active");
					} else {
						dummy.setAuthToken("Inactive");
					}
				} else {
					dummy.setAuthToken("Inactive");
				}
				String regName = gstinReqMap.get(gstin).getRegistrationType();

				if (regName != null && (!regName.equalsIgnoreCase("TDS")
						|| !regName.equalsIgnoreCase("ISD"))) {

					if (regName == null || regName.equalsIgnoreCase("normal")
							|| regName.equalsIgnoreCase("regular")) {
						dummy.setRegType("");
					} else {
						dummy.setRegType(regName.toUpperCase());
					}
				} else {
					dummy.setRegType("");
				}
				dataGstinList.add(gstin);
				dataDtoList.add(dummy);
			}
		}
	}

	private Map<String, Integer> checkTheCountByGstinTaxperiod(
			List<String> gstinList, int derivedTaxPeriodFrom,
			int derivedTaxPeriodTo) {

		String sql = sqlQuery();

		Query q = entityManager.createNativeQuery(sql);
		q.setParameter("gstinList", gstinList);
		q.setParameter("fromTaxPeriod", derivedTaxPeriodFrom);
		q.setParameter("toTaxPeriod", derivedTaxPeriodTo);
		List<Object[]> list = q.getResultList();

		if (!list.isEmpty()) {
			Map<String, Integer> countMap = list.stream()
					.collect(Collectors.toMap(s -> String.valueOf(s[0]),
							s -> Integer.valueOf(String.valueOf(s[1]))));

			return countMap;
		} else
			return null;
		/*
		 * int count = 0;
		 * 
		 * int docCount = docRepository.gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int b2csCount =
		 * gstr1B2CSRepository.gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int atCount =
		 * gstr1ARRepository.gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int ataCount =
		 * gstr1ATARepository.gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int atHeaderCount =
		 * gstr1GetAtGstnRepository.gstinCountByRetPerFromTo( gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int ataHeaderCount =
		 * gstr1GetAtaGstnRepository.gstinCountByRetPerFromTo( gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int txpHeaderCount =
		 * gstr1GetTxpGstnRepository.gstinCountByRetPerFromTo( gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int txpaHeaderCount =
		 * gstr1GetTxpaGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int b2bHeaderCount =
		 * gstr1GetB2bGstnRepository.gstinCountByRetPerFromTo( gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int b2baHeaderCount =
		 * gstr1GetB2baGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int b2clHeaderCount =
		 * gstr1GetB2clGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int b2claHeaderCount =
		 * gstr1GetB2claGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int b2csHeaderCount =
		 * gstr1GetB2claGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int b2csaHeaderCount =
		 * gstr1GetB2csaGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int cdnrHeaderCount =
		 * gstr1GetCdnrGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int cdnraHeaderCount =
		 * gstr1GetCdnraGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int cdnurHeaderCount =
		 * gstr1GetCdnurGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int cdnuraHeaderCount =
		 * gstr1GetCdnuraGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int expHeaderCount =
		 * gstr1GetExpGstnRepository.gstinCountByRetPerFromTo( gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int expaHeaderCount =
		 * gstr1GetExpaGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo); int nilextnonCount =
		 * gstr1GetNilRatedGstnRepository .gstinCountByRetPerFromTo(gstin,
		 * derivedTaxPeriodFrom, derivedTaxPeriodTo);
		 * 
		 * count = count + docCount + b2csCount + atCount + ataCount +
		 * atHeaderCount + ataHeaderCount + txpHeaderCount + txpaHeaderCount +
		 * b2bHeaderCount + b2baHeaderCount + b2clHeaderCount + b2claHeaderCount
		 * + b2csHeaderCount + b2csaHeaderCount + cdnrHeaderCount +
		 * cdnraHeaderCount + cdnurHeaderCount + cdnuraHeaderCount +
		 * expHeaderCount + expaHeaderCount + nilextnonCount;
		 */
	}

	private Map<String, String> getStatusByCriteria(
			ProcessedVsSubmittedRequestDto criteria, List<String> gstinList)
			throws Exception {
		Map<String, String> gstinStatusMap = Maps.newHashMap();

		List<GetProcessedVsSubmittedStatusRespDto> dtos = getProcessedVsSubmittedStatusService
				.findByCriteria(criteria, gstinList);

		Map<String, List<GetProcessedVsSubmittedStatusRespDto>> gstinMap = dtos
				.stream().collect(Collectors.groupingBy(e -> e.getGstin()));

		Map<String, Set<String>> gstinsMap = Maps.newHashMap();
		Map<String, List<String>> timestampMap = Maps.newHashMap();

		gstinMap.keySet().forEach(sgstin -> {

			Set<String> statusList = Sets.newHashSet();
			List<String> timestampList = Lists.newArrayList();

			List<GetProcessedVsSubmittedStatusRespDto> dtoStatusList = gstinMap
					.get(sgstin);

			for (GetProcessedVsSubmittedStatusRespDto dto : dtoStatusList) {
				// sgstin = dto.getGstin();

				statusList.add(dto.getStatus());

				timestampList.add(dto.getLastUpdatedTimeStamp() != null
						? dto.getLastUpdatedTimeStamp() : null);
			}

			timestampMap.put(sgstin,
					timestampList.stream().filter(
							str -> (str != null && !str.trim().equals("-")))
							.collect(Collectors.toList()));
			gstinsMap.put(sgstin,
					statusList.stream().filter(
							str -> (str != null && !str.trim().equals("-")))
							.collect(Collectors.toSet()));

		});

		/*
		 * Map<String, Set<String>> gstinsMap = Maps.newHashMap(); Map<String,
		 * List<String>> timestampMap = Maps.newHashMap(); Set<String>
		 * statusList = Sets.newHashSet(); List<String> timestampList =
		 * Lists.newArrayList(); String sgstin = null; for
		 * (GetProcessedVsSubmittedStatusRespDto dto : dtos) { sgstin =
		 * dto.getGstin();
		 * 
		 * statusList.add(dto.getStatus());
		 * 
		 * timestampList.add(dto.getLastUpdatedTimeStamp() != null ?
		 * dto.getLastUpdatedTimeStamp() : null); } timestampMap.put(sgstin,
		 * timestampList.stream() .filter(str -> (str != null &&
		 * !str.trim().equals("-"))) .collect(Collectors.toList()));
		 * gstinsMap.put(sgstin, statusList.stream() .filter(str -> (str != null
		 * && !str.trim().equals("-"))) .collect(Collectors.toSet()));
		 */
		gstinsMap.keySet().forEach(gstin -> {
			String finalStatus = NOT_INITIATED;
			Set<String> finalStatusList = gstinsMap.get(gstin);
			List<String> unqueSatusList = finalStatusList.stream()
					.filter(status -> status != null)
					.collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(unqueSatusList)) {

				if (unqueSatusList.size() > 0) {
					if (unqueSatusList.contains(INITIATED)) {
						finalStatus = INITIATED;
					} else if (unqueSatusList.contains(INPROGRESS)) {
						finalStatus = INPROGRESS;
					} else if ((unqueSatusList.contains(SUCCESS)
							|| unqueSatusList.contains(SUCCESS_WITH_NO_DATA))
							&& unqueSatusList.contains(FAILED)) {
						finalStatus = PARTIALLY_SUCCESS;
					} else if ((unqueSatusList.contains(SUCCESS)
							|| unqueSatusList.contains(SUCCESS_WITH_NO_DATA))) {
						finalStatus = SUCCESS;
					} else if (unqueSatusList.contains(FAILED)) {
						finalStatus = FAILED;
					}
				}

				/*
				 * if (unqueSatusList.size() > 0) { if
				 * (unqueSatusList.contains(INPROGRESS)) { finalStatus =
				 * INPROGRESS; } else if (unqueSatusList.contains(INITIATED)) {
				 * finalStatus = INITIATED; } else if
				 * (unqueSatusList.contains(SUCCESS) ||
				 * unqueSatusList.contains(SUCCESS_WITH_NO_DATA)) { finalStatus
				 * = SUCCESS; } else if (unqueSatusList.contains(FAILED)) {
				 * finalStatus = FAILED; } else if
				 * ((!unqueSatusList.contains(FAILED) ||
				 * unqueSatusList.contains(INPROGRESS) ||
				 * unqueSatusList.contains(INITIATED)) &&
				 * (unqueSatusList.contains(SUCCESS) || unqueSatusList
				 * .contains(SUCCESS_WITH_NO_DATA))) { finalStatus =
				 * PARTIALLY_SUCCESS; } } else { finalStatus =
				 * PARTIALLY_SUCCESS; }
				 */
			}
			gstinStatusMap.put(gstin, finalStatus);
		});
		return updateTimeStampOnExisitngGstin(timestampMap, gstinStatusMap);
	}

	private Map<String, String> updateTimeStampOnExisitngGstin(
			Map<String, List<String>> timestampMap,
			Map<String, String> gstinStatusMap) {
		Map<String, String> finalMap = Maps.newHashMap();
		gstinStatusMap.keySet().forEach(gstin -> {
			List<String> timestampList = timestampMap.get(gstin);
			finalMap.put(gstin, gstinStatusMap.get(gstin) + "__"
					+ getTimestamp(timestampList));
		});
		return finalMap;
	}

	private static String getTimestamp(List<String> timestampList) {
		SimpleDateFormat out = new SimpleDateFormat("dd-MM-yyyy : HH:mm:ss");
		String returnStamp = null;
		try {
			if (CollectionUtils.isNotEmpty(timestampList)) {
				Date startValue = out.parse(timestampList.get(0));
				for (int i = 1; i < timestampList.size(); i++) {
					Date nextValue = out.parse(timestampList.get(i));
					if (nextValue.after(startValue)) {
						startValue = nextValue;
					}
				}
				returnStamp = out.format(startValue);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnStamp;
	}

	private String sqlQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append(" WITH CTE AS ("
				+ "  SELECT SUPPLIER_GSTIN AS GSTIN,COUNT(1) AS CNT FROM ANX_OUTWARD_DOC_HEADER "
				+ " WHERE SUPPLIER_GSTIN IN (:gstinList) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ " GROUP BY SUPPLIER_GSTIN" + " UNION ALL"
				+ " SELECT SUPPLIER_GSTIN,COUNT(1) FROM GSTR1_PROCESSED_B2CS"
				+ " WHERE SUPPLIER_GSTIN IN (:gstinList) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ " GROUP BY SUPPLIER_GSTIN" + " UNION ALL"
				+ " SELECT SUPPLIER_GSTIN,COUNT(1) FROM GSTR1_PROCESSED_ADV_RECEIVED"
				+ " WHERE SUPPLIER_GSTIN IN (:gstinList) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ " GROUP BY SUPPLIER_GSTIN" + " UNION ALL"
				+ " SELECT SUPPLIER_GSTIN,COUNT(1) FROM GSTR1_PROCESSED_ADV_ADJUSTMENT"
				+ " WHERE SUPPLIER_GSTIN IN (:gstinList) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ " GROUP BY SUPPLIER_GSTIN" + " UNION ALL"
				+ " SELECT GSTIN,COUNT(1) FROM GETGSTR1_AT_HEADER"
				+ " WHERE GSTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ " GROUP BY GSTIN UNION ALL"
				+ " SELECT GSTIN,COUNT(1) FROM GETGSTR1_ATA_HEADER"
				+ " WHERE GSTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ " GROUP BY GSTIN" + "  UNION ALL"
				+ "  SELECT GSTIN,COUNT(1) FROM GETGSTR1_TXP_HEADER"
				+ "  WHERE GSTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY GSTIN" + "  UNION ALL"
				+ "  SELECT GSTIN,COUNT(1) FROM GETGSTR1_TXPA_HEADER"
				+ "  WHERE GSTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY GSTIN" + "  UNION ALL"
				+ "  SELECT CTIN,COUNT(1) FROM GETGSTR1_B2B_HEADER"
				+ "  WHERE CTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY CTIN" + "  UNION ALL"
				+ "  SELECT CTIN,COUNT(1) FROM GETGSTR1_B2BA_HEADER"
				+ "  WHERE CTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY CTIN" + "  UNION ALL"
				+ "  SELECT GSTIN,COUNT(1) FROM GETGSTR1_B2CLA_HEADER"
				+ "  WHERE GSTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY GSTIN" + "  UNION ALL"
				+ "  SELECT ETIN,COUNT(1) FROM GETGSTR1_B2CL_HEADER"
				+ "  WHERE ETIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY ETIN" + "  UNION ALL"
				+ "  SELECT ETIN,COUNT(1) FROM GETGSTR1_B2CSA_HEADER"
				+ "  WHERE ETIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY ETIN" + "  UNION ALL"
				+ "  SELECT ETIN,COUNT(1) FROM GETGSTR1_B2CS_HEADER"
				+ "  WHERE ETIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY ETIN" + "  UNION ALL"
				+ "  SELECT CTIN,COUNT(1) FROM GETGSTR1_CDNR_HEADER"
				+ "  WHERE CTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY CTIN" + "  UNION ALL"
				+ "  SELECT CTIN,COUNT(1) FROM GETGSTR1_CDNRA_HEADER"
				+ "  WHERE CTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY CTIN" + "  UNION ALL"
				+ "  SELECT GSTIN,COUNT(1) FROM GETGSTR1_CDNUR_HEADER"
				+ "  WHERE GSTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY GSTIN" + "  UNION ALL"
				+ "  SELECT GSTIN,COUNT(1) FROM GETGSTR1_CDNURA_HEADER"
				+ "  WHERE GSTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY GSTIN" + "  UNION ALL"
				+ "  SELECT GSTIN,COUNT(1) FROM GETGSTR1_EXP_HEADER"
				+ "  WHERE GSTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY GSTIN" + "  UNION ALL"
				+ "  SELECT CTIN ,COUNT(1) FROM GETGSTR1_EXPA_HEADER"
				+ "  WHERE CTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY CTIN" + "  UNION ALL"
				+ "  SELECT GSTIN,COUNT(1) FROM GETGSTR1_NILEXTNON"
				+ "  WHERE GSTIN IN (:gstinList ) AND DERIVED_RET_PERIOD BETWEEN :fromTaxPeriod AND :toTaxPeriod"
				+ "  GROUP BY GSTIN" + "  )" + " SELECT GSTIN, IFNULL(SUM(CNT),0) AS CNT FROM CTE GROUP BY GSTIN ");

		return builder.toString();
	}
}
