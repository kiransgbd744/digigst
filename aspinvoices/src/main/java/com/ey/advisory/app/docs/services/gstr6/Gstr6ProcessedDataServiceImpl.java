/**
 * 
 */
package com.ey.advisory.app.docs.services.gstr6;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.entities.gstr6.Gstr6ComputeDigiConfigStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6ComputeDigiConfigStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ProcessedSummResponseDto;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr6SummaryRequestDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */

@Slf4j
@Service("Gstr6ProcessedDataServiceImpl")
public class Gstr6ProcessedDataServiceImpl
		implements Gstr6ProcessedDataService {

	@Autowired
	@Qualifier("Gstr6ProcessedDataDaoImpl")
	private Gstr6ProcessedDataDao gstr6ProcessedDataDao;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("GstnSubmitRepository")
	private GstnSubmitRepository gstnSubmitRepository;

	@Autowired
	@Qualifier("Gstr6ComputeDigiConfigStatusRepository")
	Gstr6ComputeDigiConfigStatusRepository digiConfigStatusRepo;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository returnstatusRepo;

	private static final String ACTIVE = "Active";
	private static final String IN_ACTIVE = "Inactive";
	private static final String NOT_INITIATED = "NOT INITIATED";
	private static final String PARTIALLY_SAVED = "PARTIALLY SAVED";

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Override
	public List<Gstr6ProcessedSummResponseDto> getGstr6ProcessedRec(
			Gstr6SummaryRequestDto dto) {
		List<Object[]> objs = gstr6ProcessedDataDao.getGstr6ProcessedProcRecords(dto);
		Map<String, List<String>> dataSecAttrs = dto.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		List<Gstr6ProcessedSummResponseDto> processDatas = new ArrayList<>();

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
						&& dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstinList = gstnDetailRepository.getGstinRegTypeISD(
							dataSecAttrs.get(OnboardingConstant.GSTIN));
				}
			}

		}
		String retPer = dto.getTaxPeriod();
		if (objs != null && !objs.isEmpty()) {
			List<Gstr6ProcessedSummResponseDto> gstr6ProcSumRespDatas = convertProcessedData(
					objs);

			Map<String, List<Gstr6ProcessedSummResponseDto>> mapGstr6ProdSumResp = mapGstr6ProcessSumResp(
					gstr6ProcSumRespDatas);
			gstinList.forEach(gstin -> {
				List<Gstr6ProcessedSummResponseDto> gstr6ProcSumRespDtos = mapGstr6ProdSumResp
						.get(gstin);
				if (gstr6ProcSumRespDtos != null
						&& !gstr6ProcSumRespDtos.isEmpty()) {
					processDatas.addAll(gstr6ProcSumRespDtos);
				} else {

					convertDefaultProcGstinData(processDatas, gstin, retPer);
				}
			});
		} else {
			convertDefaultProcessData(processDatas, gstinList, retPer);
		}
		processDatas.sort(
				Comparator.comparing(Gstr6ProcessedSummResponseDto::getGstin));
		return processDatas;
	}

	@Override
	public List<Gstr6ProcessedSummResponseDto> getGstr6ProcessedRecForRevIntg(
			Gstr6SummaryRequestDto dto) {
		List<Object[]> objs = gstr6ProcessedDataDao.getGstr6ProcessedRec(dto);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Query Result %s", objs.size());
			LOGGER.debug(msg);
		}

		Map<String, List<String>> dataSecAttrs = dto.getDataSecAttrs();
		List<String> gstinList = new ArrayList<>();
		List<Gstr6ProcessedSummResponseDto> processDatas = new ArrayList<>();

		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
						&& dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstinList = gstnDetailRepository.getGstinRegTypeISD(
							dataSecAttrs.get(OnboardingConstant.GSTIN));
				}
			}

		}
		String retPer = dto.getTaxPeriod();
		if (objs != null && !objs.isEmpty()) {
			List<Gstr6ProcessedSummResponseDto> gstr6ProcSumRespDatas = convertProcessedDataForRevIntg(
					objs);

			Map<String, List<Gstr6ProcessedSummResponseDto>> mapGstr6ProdSumResp = mapGstr6ProcessSumResp(
					gstr6ProcSumRespDatas);
			gstinList.forEach(gstin -> {
				List<Gstr6ProcessedSummResponseDto> gstr6ProcSumRespDtos = mapGstr6ProdSumResp
						.get(gstin);
				if (gstr6ProcSumRespDtos != null
						&& !gstr6ProcSumRespDtos.isEmpty()) {
					processDatas.addAll(gstr6ProcSumRespDtos);
				} else {

					if (dto.getListOfretPer() != null
							&& !dto.getListOfretPer().isEmpty()) {
						convertDefaultProcGstinData(processDatas, gstin,
								dto.getListOfretPer());
					} else {
						convertDefaultProcGstinData(processDatas, gstin,
								Arrays.asList(retPer));
					}
				}
			});
		} else {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"QUERY results is empty,Hence creating default values");
				LOGGER.debug(msg);
			}
			if (dto.getListOfretPer() != null
					&& !dto.getListOfretPer().isEmpty()) {
				convertDefaultProcessDataForRevIntg(processDatas, gstinList,
						dto.getListOfretPer());

			} else {
				convertDefaultProcessDataForRevIntg(processDatas, gstinList,
						Arrays.asList(retPer));
			}
		}
		processDatas.sort(
				Comparator.comparing(Gstr6ProcessedSummResponseDto::getGstin));
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"End of GSTR6 Processed Values, with the size of %s",
					processDatas.size());
			LOGGER.debug(msg);
		}
		return processDatas;
	}

	
	private void convertDefaultProcessDataForRevIntg(
			List<Gstr6ProcessedSummResponseDto> processDatas,
			List<String> gstinList, List<String> retPerList) {
		if (gstinList != null && !gstinList.isEmpty()) {
			for (String gstin : gstinList) {

				for (String retPer : retPerList) {

					Gstr6ProcessedSummResponseDto obj = new Gstr6ProcessedSummResponseDto();
					String gstintoken = defaultGSTNAuthTokenService
							.getAuthTokenStatusForGstin(gstin);
					if (gstintoken != null) {
						if ("A".equalsIgnoreCase(gstintoken)) {
							obj.setAuthToken(ACTIVE);
						} else {
							obj.setAuthToken(IN_ACTIVE);
						}
					} else {
						obj.setAuthToken(IN_ACTIVE);
					}
					String regName = gstnDetailRepository
							.findIsdRegTypeByGstin(gstin);
					if (regName != null && "isd".equalsIgnoreCase(regName)) {
						obj.setRegType(regName.toUpperCase());
					}
					obj.setGstin(gstin);
					obj.setRetPer(retPer);
					obj.setState(statecodeRepository
							.findStateNameByCode(gstin.substring(0, 2)));
					GstrReturnStatusEntity signedStatus = returnstatusRepo
							.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
									gstin, retPer,
									APIConstants.GSTR6.toUpperCase());
					if (signedStatus != null && "FILED"
							.equalsIgnoreCase(signedStatus.getStatus())) {
						obj.setStatus("FILED");
						if (signedStatus != null
								&& "FILED".equalsIgnoreCase(signedStatus.getStatus())) {
							obj.setStatus("FILED");
							obj.setTimeStamp(EYDateUtil
									.fmtLocalDate(signedStatus.getFilingDate()));
						}
					} else {
						obj.setStatus(NOT_INITIATED);
					}

					processDatas.add(obj);
				}
			}
		}
	}
	
	private Map<String, List<Gstr6ProcessedSummResponseDto>> mapGstr6ProcessSumResp(
			List<Gstr6ProcessedSummResponseDto> gstr6ProSumRespDtos) {
		Map<String, List<Gstr6ProcessedSummResponseDto>> mapGstr6ProsSumResp = new HashMap<>();
		gstr6ProSumRespDtos.forEach(gstr6ProSumRespDto -> {
			String docKey = gstr6ProSumRespDto.getGstin();
			if (mapGstr6ProsSumResp.containsKey(docKey)) {
				List<Gstr6ProcessedSummResponseDto> gstr6ProcSumResps = mapGstr6ProsSumResp
						.get(docKey);
				gstr6ProcSumResps.add(gstr6ProSumRespDto);
				mapGstr6ProsSumResp.put(docKey, gstr6ProcSumResps);
			} else {
				List<Gstr6ProcessedSummResponseDto> gstr6ProcSumResps = new ArrayList<>();
				gstr6ProcSumResps.add(gstr6ProSumRespDto);
				mapGstr6ProsSumResp.put(docKey, gstr6ProcSumResps);
			}
		});
		return mapGstr6ProsSumResp;
	}

	private void convertDefaultProcGstinData(
			List<Gstr6ProcessedSummResponseDto> processDatas, String gstin,
			List<String> retPerList) {

		for (String retPerStr : retPerList) {

			Gstr6ProcessedSummResponseDto obj = new Gstr6ProcessedSummResponseDto();
			String gstintoken = defaultGSTNAuthTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (gstintoken != null) {
				if ("A".equalsIgnoreCase(gstintoken)) {
					obj.setAuthToken(ACTIVE);
				} else {
					obj.setAuthToken(IN_ACTIVE);
				}
			} else {
				obj.setAuthToken(IN_ACTIVE);
			}
			String regName = gstnDetailRepository.findIsdRegTypeByGstin(gstin);
			if (regName != null && "isd".equalsIgnoreCase(regName)) {
				obj.setRegType(regName.toUpperCase());
			}
			obj.setGstin(gstin);
			obj.setRetPer(retPerStr);
			obj.setState(statecodeRepository
					.findStateNameByCode(gstin.substring(0, 2)));
			GstrReturnStatusEntity signedStatus = returnstatusRepo
					.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
							gstin, retPerStr, APIConstants.GSTR6.toUpperCase());
			if (signedStatus != null
					&& "FILED".equalsIgnoreCase(signedStatus.getStatus())) {
				obj.setStatus("FILED");
				if (signedStatus != null
						&& "FILED".equalsIgnoreCase(signedStatus.getStatus())) {
					obj.setStatus("FILED");
					obj.setTimeStamp(EYDateUtil
							.fmtLocalDate(signedStatus.getFilingDate()));
				}
			} else {
				obj.setStatus(NOT_INITIATED);
			}

			processDatas.add(obj);
		}
	}
	
	private void convertDefaultProcessData(
			List<Gstr6ProcessedSummResponseDto> processDatas,
			List<String> gstinList, String retPer) {
		if (gstinList != null && !gstinList.isEmpty()) {
			for (String gstin : gstinList) {
				Gstr6ProcessedSummResponseDto obj = new Gstr6ProcessedSummResponseDto();
				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						obj.setAuthToken(ACTIVE);
					} else {
						obj.setAuthToken(IN_ACTIVE);
					}
				} else {
					obj.setAuthToken(IN_ACTIVE);
				}
				String regName = gstnDetailRepository
						.findIsdRegTypeByGstin(gstin);
				if (regName != null && "isd".equalsIgnoreCase(regName)) {
					obj.setRegType(regName.toUpperCase());
				}
				obj.setGstin(gstin);
				obj.setState(statecodeRepository
						.findStateNameByCode(gstin.substring(0, 2)));
				GstrReturnStatusEntity signedStatus = returnstatusRepo
						.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
								gstin, retPer,
								APIConstants.GSTR6.toUpperCase());
				if (signedStatus != null
						&& "FILED".equalsIgnoreCase(signedStatus.getStatus())) {
					obj.setStatus("FILED");
					obj.setTimeStamp(EYDateUtil
							.fmtLocalDate(signedStatus.getFilingDate()));
				} else {
					obj.setStatus(NOT_INITIATED);
				}

				String taxPeriod = GenUtil.getDerivedTaxPeriod(retPer).toString();
				Gstr6ComputeDigiConfigStatusEntity configEntity = digiConfigStatusRepo
						.findByGstinAndTaxPeriodAndIsActiveTrue(gstin,
								taxPeriod);
				if(configEntity != null){
					obj.setDigiStatus(configEntity.getStatus());
					if (configEntity.getUpdatedOn() != null) {
						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(EYDateUtil
								.toISTDateTimeFromUTC(configEntity.getUpdatedOn()));
						obj.setDigiUpdatedOn(newdate.toString());
					}
				} else {
					obj.setDigiStatus("Not_Initiated");
				}
				
				processDatas.add(obj);
			}
		}
	}

	private void convertDefaultProcGstinData(
			List<Gstr6ProcessedSummResponseDto> processDatas, String gstin,
			String retPer) {
		Gstr6ProcessedSummResponseDto obj = new Gstr6ProcessedSummResponseDto();
		String gstintoken = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstin(gstin);
		if (gstintoken != null) {
			if ("A".equalsIgnoreCase(gstintoken)) {
				obj.setAuthToken(ACTIVE);
			} else {
				obj.setAuthToken(IN_ACTIVE);
			}
		} else {
			obj.setAuthToken(IN_ACTIVE);
		}
		String regName = gstnDetailRepository.findIsdRegTypeByGstin(gstin);
		if (regName != null && "isd".equalsIgnoreCase(regName)) {
			obj.setRegType(regName.toUpperCase());
		}
		obj.setGstin(gstin);
		obj.setState(
				statecodeRepository.findStateNameByCode(gstin.substring(0, 2)));
		GstrReturnStatusEntity signedStatus = returnstatusRepo
				.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
						gstin, retPer, APIConstants.GSTR6.toUpperCase());
		if (signedStatus != null
				&& "FILED".equalsIgnoreCase(signedStatus.getStatus())) {
			obj.setStatus("FILED");
			obj.setTimeStamp(
					EYDateUtil.fmtLocalDate(signedStatus.getFilingDate()));
		} else {
			obj.setStatus(NOT_INITIATED);
		}
		processDatas.add(obj);
	}

	private List<Gstr6ProcessedSummResponseDto> convertProcessedData(
			List<Object[]> arrs) {
		List<Gstr6ProcessedSummResponseDto> objs = new ArrayList<>();
		if (arrs != null && !arrs.isEmpty()) {
			for (Object[] arr : arrs) {
				Gstr6ProcessedSummResponseDto obj = new Gstr6ProcessedSummResponseDto();

				String gstin = (String) arr[0];
				obj.setGstin(gstin);
				String retPer = arr[1] != null ? String.valueOf(arr[1]) : null;
				obj.setRetPer(retPer);
				int count = ((BigInteger) arr[2]).intValue();
				obj.setCount(count);
				obj.setInvoiceValue((BigDecimal) arr[3]);
				obj.setTaxableValue((BigDecimal) arr[4]);
				obj.setTotalTax((BigDecimal) arr[5]);
				obj.setTpIgst((BigDecimal) arr[6]);
				obj.setTpCgst((BigDecimal) arr[7]);
				obj.setTpSgst((BigDecimal) arr[8]);
				obj.setTpCess((BigDecimal) arr[9]);
				obj.setTotCreElig((BigDecimal) arr[10]);
				obj.setCeIgst((BigDecimal) arr[11]);
				obj.setCeCgst((BigDecimal) arr[12]);
				obj.setCeSgst((BigDecimal) arr[13]);
				obj.setCeCess((BigDecimal) arr[14]);
				obj.setState((String) arr[15]);

				BigInteger gstnNotSentCount = arr[16] != null
						? new BigInteger(String.valueOf(arr[16]))
						: BigInteger.ZERO;
				BigInteger savedCount = arr[17] != null
						? new BigInteger(String.valueOf(arr[17]))
						: BigInteger.ZERO;
				/*
				 * BigInteger notSavedCount = arr[18] != null ? new
				 * BigInteger(String.valueOf(arr[18])) : BigInteger.ZERO;
				 */
				BigInteger errorCount = arr[19] != null
						? new BigInteger(String.valueOf(arr[19]))
						: BigInteger.ZERO;
				BigInteger totalCount = arr[20] != null
						? new BigInteger(String.valueOf(arr[20]))
						: BigInteger.ZERO;
				Pair<String, LocalDate> status = null;

				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						obj.setAuthToken(ACTIVE);
					} else {
						obj.setAuthToken(IN_ACTIVE);
					}
				} else {
					obj.setAuthToken(IN_ACTIVE);
				}
				String regName = gstnDetailRepository
						.findIsdRegTypeByGstin(gstin);
				if (regName != null && "isd".equalsIgnoreCase(regName)) {
					obj.setRegType(regName.toUpperCase());
				}
				List<String> findStatusP = gstnSubmitRepository
						.findStatusPByGstinAndRetPeriodAndRetrunType(gstin,
								retPer, APIConstants.GSTR6.toUpperCase());
				if (!findStatusP.isEmpty()) {
					obj.setStatus("SUBMITTED");
				} else {
					status = deriveStatusByTotSavedErrorCount(totalCount,
							savedCount, errorCount, gstnNotSentCount, gstin,
							retPer, APIConstants.GSTR6,
							TenantContext.getTenantId());
					obj.setStatus(status.getValue0());
				}
				String taxPeriod = GenUtil.getDerivedTaxPeriod(retPer).toString();
				Gstr6ComputeDigiConfigStatusEntity configEntity = digiConfigStatusRepo
						.findByGstinAndTaxPeriodAndIsActiveTrue(gstin,
								taxPeriod);
				if(configEntity != null){
					obj.setDigiStatus(configEntity.getStatus());
					if (configEntity.getUpdatedOn() != null) {
						DateTimeFormatter FOMATTER = DateTimeFormatter
								.ofPattern("dd-MM-yyyy : HH:mm:ss");
						String newdate = FOMATTER.format(EYDateUtil
								.toISTDateTimeFromUTC(configEntity.getUpdatedOn()));
						obj.setDigiUpdatedOn(newdate.toString());
					}
				} else {
					obj.setDigiStatus("Not_Initiated");
				}

				if (status.getValue0().equals("FILED")) {
					obj.setTimeStamp(
							EYDateUtil.fmtLocalDate(status.getValue1()));
					objs.add(obj);
				} else {
					Timestamp date = arr[21] != null ? (Timestamp) arr[21]
							: null;
					String newDate = convertTimeFormat(date);

					obj.setTimeStamp(newDate);

					objs.add(obj);
				}
				
				
			}
		}
		return objs;
	}

	private Pair<String, LocalDate> deriveStatusByTotSavedErrorCount(
			BigInteger totalCount, BigInteger savedCount, BigInteger errorCount,
			BigInteger notSentCount, String gstin, String taxPeriod,
			String returnType, String groupCode) {

		if (gstin != null && taxPeriod != null && returnType != null
				&& groupCode != null) {

			GstrReturnStatusEntity signedStatusP = returnstatusRepo
					.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
							gstin, taxPeriod, returnType.toUpperCase());
			if (signedStatusP != null
					&& "FILED".equalsIgnoreCase(signedStatusP.getStatus())) {
				return new Pair<String, LocalDate>("FILED",
						signedStatusP.getFilingDate());
			}
			List<String> submittedStatusP = gstnSubmitRepository
					.findStatusPByGstinAndRetPeriodAndRetrunType(gstin,
							taxPeriod, returnType.toUpperCase());
			if (!submittedStatusP.isEmpty()) {
				return new Pair<String, LocalDate>("SUBMITTED", null);

			}
			if (!gstnUserRequestUtil.isNextSaveRequestEligible(gstin, taxPeriod,
					APIConstants.SAVE, returnType.toUpperCase(), groupCode)) {
				return new Pair<String, LocalDate>("IN PROGRESS", null);

			} else if (!gstnUserRequestUtil.isNextSaveRequestEligible(gstin,
					taxPeriod, APIConstants.DELETE_FULL_DATA,
					returnType.toUpperCase(), groupCode)) {
				return new Pair<String, LocalDate>("IN PROGRESS", null);
			} else if (!gstnUserRequestUtil.isNextSaveRequestEligible(gstin,
					taxPeriod, APIConstants.DELETE_FILE_UPLOAD,
					returnType.toUpperCase(), groupCode)) {
				return new Pair<String, LocalDate>("IN PROGRESS", null);
			}
		}

		if (!totalCount.equals(BigInteger.ZERO)) {
			if (totalCount.equals(notSentCount)) {
				return new Pair<String, LocalDate>(NOT_INITIATED, null);
			} else if (totalCount.equals(savedCount)) {
				return new Pair<String, LocalDate>("SAVED", null);
			} else if (totalCount.equals(errorCount)) {
				return new Pair<String, LocalDate>("FAILED", null);
			} else {
				return new Pair<String, LocalDate>(PARTIALLY_SAVED, null);
			}
		} else {
			return new Pair<String, LocalDate>(NOT_INITIATED, null);
		}
	}

	private String convertTimeFormat(Timestamp date) {
		String newdate = null;
		if (date != null) {
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			newdate = formatter.format(dateTimeFormatter);
		}
		return newdate;
	}
	
	private List<Gstr6ProcessedSummResponseDto> convertProcessedDataForRevIntg(
			List<Object[]> arrs) {
		List<Gstr6ProcessedSummResponseDto> objs = new ArrayList<>();
		if (arrs != null && !arrs.isEmpty()) {
			for (Object[] arr : arrs) {
				Gstr6ProcessedSummResponseDto obj = new Gstr6ProcessedSummResponseDto();

				String gstin = (String) arr[0];
				obj.setGstin(gstin);
				String retPer = arr[1] != null ? String.valueOf(arr[1]) : null;
				obj.setRetPer(retPer);
				int count = ((BigInteger) arr[2]).intValue();
				obj.setCount(count);
				obj.setInvoiceValue((BigDecimal) arr[3]);
				obj.setTaxableValue((BigDecimal) arr[4]);
				obj.setTotalTax((BigDecimal) arr[5]);
				obj.setTpIgst((BigDecimal) arr[6]);
				obj.setTpCgst((BigDecimal) arr[7]);
				obj.setTpSgst((BigDecimal) arr[8]);
				obj.setTpCess((BigDecimal) arr[9]);
				obj.setTotCreElig((BigDecimal) arr[10]);
				obj.setCeIgst((BigDecimal) arr[11]);
				obj.setCeCgst((BigDecimal) arr[12]);
				obj.setCeSgst((BigDecimal) arr[13]);
				obj.setCeCess((BigDecimal) arr[14]);
				obj.setState((String) arr[15]);

				BigInteger gstnNotSentCount = arr[16] != null
						? new BigInteger(String.valueOf(arr[16]))
						: BigInteger.ZERO;
				BigInteger savedCount = arr[17] != null
						? new BigInteger(String.valueOf(arr[17]))
						: BigInteger.ZERO;
				/*
				 * BigInteger notSavedCount = arr[18] != null ? new
				 * BigInteger(String.valueOf(arr[18])) : BigInteger.ZERO;
				 */
				BigInteger errorCount = arr[19] != null
						? new BigInteger(String.valueOf(arr[19]))
						: BigInteger.ZERO;
				BigInteger totalCount = arr[20] != null
						? new BigInteger(String.valueOf(arr[20]))
						: BigInteger.ZERO;
				Pair<String, LocalDate> status = null;

				String gstintoken = defaultGSTNAuthTokenService
						.getAuthTokenStatusForGstin(gstin);
				if (gstintoken != null) {
					if ("A".equalsIgnoreCase(gstintoken)) {
						obj.setAuthToken(ACTIVE);
					} else {
						obj.setAuthToken(IN_ACTIVE);
					}
				} else {
					obj.setAuthToken(IN_ACTIVE);
				}
				String regName = gstnDetailRepository
						.findIsdRegTypeByGstin(gstin);
				if (regName != null && "isd".equalsIgnoreCase(regName)) {
					obj.setRegType(regName.toUpperCase());
				}
				List<String> findStatusP = gstnSubmitRepository
						.findStatusPByGstinAndRetPeriodAndRetrunType(gstin,
								retPer, APIConstants.GSTR6.toUpperCase());
				if (!findStatusP.isEmpty()) {
					obj.setStatus("SUBMITTED");
				} else {
					status = deriveStatusByTotSavedErrorCount(totalCount,
							savedCount, errorCount, gstnNotSentCount, gstin,
							retPer, APIConstants.GSTR6,
							TenantContext.getTenantId());
					obj.setStatus(status.getValue0());
				}
				if (status.getValue0().equals("FILED")) {
					obj.setTimeStamp(
							EYDateUtil.fmtLocalDate(status.getValue1()));
					objs.add(obj);
				} else {
					Timestamp date = arr[21] != null ? (Timestamp) arr[21]
							: null;
					String newDate = convertTimeFormat(date);

				obj.setTimeStamp(newDate);

				objs.add(obj);
			}
		}
		}	
		return objs;
	}
}
