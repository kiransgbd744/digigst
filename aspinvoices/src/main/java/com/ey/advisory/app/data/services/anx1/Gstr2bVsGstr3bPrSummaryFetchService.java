package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessStatusRespDto;
import com.ey.advisory.app.docs.dto.Gstr2aVsGstr3bProcessSummaryFinalRespDto;
import com.ey.advisory.app.services.daos.get2a.Gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl;
import com.ey.advisory.app.services.daos.get2a.Gstr2bvsGstr3bGetGstr1StatusFetchDaoImpl;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2bVsGstr3bPrSummaryFetchService")
public class Gstr2bVsGstr3bPrSummaryFetchService {

	private static final String NOT_INITIATED = "NOT_INITIATED";
	private static final String SUCCESS_WITH_NO_DATA = "SUCCESS_WITH_NO_DATA";
	private static final String INPROGRESS = "INPROGRESS";
	private static final String SUCCESS = "SUCCESS";
	private static final String FAILED = "FAILED";
	private static final String INITIATED = "INITIATED";
	private static final String PARTIALLY_SUCCESS = "PARTIALLY_SUCCESS";

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	@Qualifier("Gstr2bVsGstr3bPrSummaryFetchDaoImpl")
	private Gstr2bVsGstr3bPrSummaryFetchDaoImpl gstr2bVsGstr3bPrSummaryFetchDaoImpl;

	@Autowired
	@Qualifier("Gstr2bvsGstr3bGetGstr1StatusFetchDaoImpl")
	private Gstr2bvsGstr3bGetGstr1StatusFetchDaoImpl gstr2bvsGstr3bGetGstr1StatusFetchDao;

	@Autowired
	@Qualifier("Gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl")
	private Gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl;

	public List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> fetchResponse(
			Gstr1VsGstr3bProcessSummaryReqDto req) throws Exception {
		List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> finalResp = Lists
				.newLinkedList();
		Gstr1VsGstr3bProcessSummaryReqDto reqDto = processedRecordsCommonSecParam
				.setGstr1VsGstr3bDataSecuritySearchParams(req);
		List<Gstr1VsGstr3bProcessStatusRespDto> itemsList = gstr2bvsGstr3bGetGstr1StatusFetchDao
				.getDataUploadedStatusDetails(reqDto);
		LOGGER.error("itemsList {}", itemsList);
		Map<String, String> statusMap = calculateStatusByGstin(itemsList);
		LOGGER.error("statusMap {}", statusMap);
		List<Gstr1VsGstr3bProcessStatusRespDto> gstr3bstatusList = gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl
				.getDataUploadedStatusDetails(reqDto);
		LOGGER.error("gstr3bstatusList {}", gstr3bstatusList);
		Map<String, String> gstr3bStatusMap = calculateGstr3bStatusByGstin(
				gstr3bstatusList);
		LOGGER.error("gstr3bStatusMap {}", gstr3bStatusMap);
		List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> processedRespDtos = gstr2bVsGstr3bPrSummaryFetchDaoImpl
				.fetchGstr2aVsGstr3bRecords(reqDto);
		LOGGER.error("processedRespDtos {}", processedRespDtos);
		if (CollectionUtils.isNotEmpty(processedRespDtos)) {
			processedRespDtos.forEach(dto -> {
				String gstin = dto.getGstin();
				if (statusMap.containsKey(gstin)) {
					String value[] = statusMap.get(gstin).split("__");
					if (value[0] != null && !value[0].contains("null")) {
						dto.setGstr2aStatus(value[0]);
					}
					String s = value[1];
					if (value[1] != null && !s.contains("null")) {
						dto.setGstr2aTimestamp(value[1]);
					}
				} else {
					dto.setGstr2aStatus(NOT_INITIATED);
				}
				if (gstr3bStatusMap.containsKey(gstin)) {
					String value[] = gstr3bStatusMap.get(gstin).split("__");
					if (value[0] != null && !value[0].contains("null")) {
						dto.setGetGstr3BStatus(value[0]);
					}
					String s = value[1];
					if (value[1] != null && !s.contains("null")) {
						dto.setGetGstr3BTimestamp(value[1]);
					}
				} else {
					dto.setGetGstr3BStatus(NOT_INITIATED);
				}
				String stateCode = gstin.substring(0, 2);
				String stateName = statecodeRepository
						.findStateNameByCode(stateCode);
				dto.setState(stateName);
				List<String> regName = gSTNDetailRepository
						.findRegTypeByGstin(gstin);
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

				finalResp.add(dto);
			});
		}

		List<String> gstins = req.getDataSecAttrs().get("GSTIN");
		List<String> gstinsList = new ArrayList<String>();
		List<String> respGstins = finalResp.stream()
				.map(Gstr2aVsGstr3bProcessSummaryFinalRespDto::getGstin)
				.collect(Collectors.toList());
		for (String gstin : gstins) {
			if (respGstins.contains(gstin))
				continue;
			gstinsList.add(gstin);
		}
		fillTheDataFromDataSecAttr(gstinsList, finalResp, statusMap,
				gstr3bStatusMap);
        Collections.sort(finalResp, Comparator.comparing(Gstr2aVsGstr3bProcessSummaryFinalRespDto::getGstin));

		
		return finalResp;
	}

	private Map<String, String> calculateGstr3bStatusByGstin(
			List<Gstr1VsGstr3bProcessStatusRespDto> gstr3bstatusList) {
		Map<String, String> gstinStatusMap = Maps.newHashMap();

		Map<String, List<Gstr1VsGstr3bProcessStatusRespDto>> statusMap = gstr3bstatusList
				.stream().collect(Collectors.groupingBy(
						Gstr1VsGstr3bProcessStatusRespDto::getGstin));

		Map<String, List<String>> statusesMap = Maps.newHashMap();
		Map<String, List<String>> timeStampMap = Maps.newHashMap();
		statusMap.keySet().forEach(gstin -> {
			List<Gstr1VsGstr3bProcessStatusRespDto> list = statusMap.get(gstin);
			statusesMap.put(gstin,
					list.stream()
							.map(Gstr1VsGstr3bProcessStatusRespDto::getStatus)
							.collect(Collectors.toList()));
			timeStampMap.put(gstin,
					list.stream()
							.map(Gstr1VsGstr3bProcessStatusRespDto::getLastUpdatedTime)
							.collect(Collectors.toList()));
		});

		statusesMap.keySet().forEach(gstin -> {
			String finalStatus = NOT_INITIATED;
			List<String> statusList = statusesMap.get(gstin);
			Set<String> uniqueStatuses = Sets.newHashSet(statusList);
			if (CollectionUtils.isNotEmpty(uniqueStatuses)) {
				finalStatus = deriveStatus(uniqueStatuses);
			}
			gstinStatusMap.put(gstin, finalStatus);
		});

		return updateTimeStampOnExisitngGstin(timeStampMap, gstinStatusMap);
	}

	private Map<String, String> calculateStatusByGstin(
			List<Gstr1VsGstr3bProcessStatusRespDto> itemsList) {
		Map<String, String> gstinStatusMap = Maps.newHashMap();

		Map<String, List<Gstr1VsGstr3bProcessStatusRespDto>> statusMap = itemsList
				.stream().collect(Collectors.groupingBy(
						Gstr1VsGstr3bProcessStatusRespDto::getGstin));

		Map<String, List<String>> statusesMap = Maps.newHashMap();
		Map<String, List<String>> timeStampMap = Maps.newHashMap();
		statusMap.keySet().forEach(gstin -> {
			List<Gstr1VsGstr3bProcessStatusRespDto> list = statusMap.get(gstin);
			statusesMap.put(gstin,
					list.stream()
							.map(Gstr1VsGstr3bProcessStatusRespDto::getStatus)
							.collect(Collectors.toList()));
			timeStampMap.put(gstin,
					list.stream()
							.map(Gstr1VsGstr3bProcessStatusRespDto::getLastUpdatedTime)
							.collect(Collectors.toList()));
		});

		statusesMap.keySet().forEach(gstin -> {
			String finalStatus = NOT_INITIATED;
			List<String> statusList = statusesMap.get(gstin);
			Set<String> uniqueStatuses = Sets.newHashSet(statusList);
			if (CollectionUtils.isNotEmpty(uniqueStatuses)) {
				if (uniqueStatuses.size() > 0) {
					/*
					 * if ((uniqueStatuses.contains(FAILED) ||
					 * uniqueStatuses.contains(INPROGRESS) ||
					 * uniqueStatuses.contains(INITIATED)) &&
					 * (uniqueStatuses.contains(SUCCESS) || uniqueStatuses
					 * .contains(SUCCESS_WITH_NO_DATA))) { finalStatus =
					 * PARTIALLY_SUCCESS; } else if
					 * (uniqueStatuses.contains(FAILED) ||
					 * uniqueStatuses.contains(INITIATED)) { finalStatus =
					 * PARTIALLY_SUCCESS; } else if
					 * (uniqueStatuses.contains(INPROGRESS)) { finalStatus =
					 * INPROGRESS; } else if
					 * (uniqueStatuses.contains(INITIATED)) { finalStatus =
					 * INITIATED; } else if (uniqueStatuses.contains(SUCCESS) ||
					 * uniqueStatuses.contains(SUCCESS_WITH_NO_DATA)) {
					 * finalStatus = SUCCESS; } else if
					 * (uniqueStatuses.contains(FAILED)) { finalStatus = FAILED;
					 * }
					 */
					// new logic
					if (uniqueStatuses.contains(INITIATED)) {
						finalStatus = INITIATED;
					} else if (uniqueStatuses.contains(INPROGRESS)) {
						finalStatus = INPROGRESS;
					} else if (uniqueStatuses.contains(FAILED)
							&& (uniqueStatuses.contains(SUCCESS)
									|| uniqueStatuses
											.contains(SUCCESS_WITH_NO_DATA))) {
						finalStatus = PARTIALLY_SUCCESS;
					} else if (uniqueStatuses.contains(SUCCESS)
							|| uniqueStatuses.contains(SUCCESS_WITH_NO_DATA)) {
						finalStatus = SUCCESS;
					} else if (uniqueStatuses.contains(FAILED)) {
						finalStatus = FAILED;
					}

				} /*
					 * else { finalStatus = PARTIALLY_SUCCESS; }
					 */
			}
			gstinStatusMap.put(gstin, finalStatus);
		});

		return updateTimeStampOnExisitngGstin(timeStampMap, gstinStatusMap);
	}

	private Map<String, String> updateTimeStampOnExisitngGstin(
			Map<String, List<String>> statusMap,
			Map<String, String> gstinStatusMap) {
		Map<String, String> finalMap = Maps.newHashMap();
		gstinStatusMap.keySet().forEach(gstin -> {
			List<String> timestampList = statusMap.get(gstin);
			finalMap.put(gstin, gstinStatusMap.get(gstin) + "__"
					+ getTimestamp(timestampList));
		});

		return finalMap;
	}

	private String getTimestamp(List<String> timestampList) {
		SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd : HH:mm:ss");
		String returnStamp = null;
		try {
			if (CollectionUtils.isNotEmpty(timestampList)) {
				returnStamp = timestampList.get(0);

				Date startValue = out.parse(timestampList.get(0));

				for (int i = 1; i < timestampList.size(); i++) {
					Date nextValue = out.parse(timestampList.get(i));
					if (nextValue.after(startValue)) {
						// startValue = nextValue;
						returnStamp = timestampList.get(i);
					}
				}

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return returnStamp;
	}

	private void fillTheDataFromDataSecAttr(List<String> gstins,
			List<Gstr2aVsGstr3bProcessSummaryFinalRespDto> finalResp,
			Map<String, String> statusMap,
			Map<String, String> gstr3bStatusMap) {
		gstins.forEach(gstin -> {
			Gstr2aVsGstr3bProcessSummaryFinalRespDto finalRespDto = new Gstr2aVsGstr3bProcessSummaryFinalRespDto();
			finalRespDto.setGstin(gstin);
			String stateCode = gstin.substring(0, 2);
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			finalRespDto.setState(stateName);
			List<String> regName = gSTNDetailRepository
					.findgstr2avs3bRegTypeByGstin(gstin);
			if (regName != null && regName.size() > 0) {
				String regTypeName = regName.get(0);
				if (regTypeName == null
						|| regTypeName.equalsIgnoreCase("normal")
						|| regTypeName.equalsIgnoreCase("regular")) {
					finalRespDto.setRegType("");
				} else {
					finalRespDto.setRegType(regTypeName.toUpperCase());
				}
			} else {
				finalRespDto.setRegType("");
			}

			String gstintoken = defaultGSTNAuthTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (gstintoken != null) {
				if ("A".equalsIgnoreCase(gstintoken)) {
					finalRespDto.setAuthToken("Active");
				} else {
					finalRespDto.setAuthToken("Inactive");
				}
			} else {
				finalRespDto.setAuthToken("Inactive");
			}
			if (statusMap.containsKey(gstin)) {
				String value[] = statusMap.get(gstin).split("__");
				if (value[0] != null && !value[0].contains("null")) {
					finalRespDto.setGstr2aStatus(value[0]);
				}
				String s = value[1];
				if (value[1] != null && !s.contains("null")) {
					finalRespDto.setGstr2aTimestamp(value[1]);
				}
			} else {
				finalRespDto.setGstr2aStatus(NOT_INITIATED);
			}
			if (gstr3bStatusMap.containsKey(gstin)) {
				String value[] = gstr3bStatusMap.get(gstin).split("__");
				if (value[0] != null && !value[0].contains("null")) {
					finalRespDto.setGetGstr3BStatus(value[0]);
				}
				String s = value[1];
				if (value[1] != null && !s.contains("null")) {
					finalRespDto.setGetGstr3BTimestamp(value[1]);
				}
			} else {
				finalRespDto.setGetGstr3BStatus(NOT_INITIATED);
			}
			finalRespDto.setReconStatus(NOT_INITIATED);
			finalRespDto.setGstr2AIgst(BigDecimal.ZERO);
			finalRespDto.setGstr2ACgst(BigDecimal.ZERO);
			finalRespDto.setGstr2ASgst(BigDecimal.ZERO);
			finalRespDto.setGstr2ACess(BigDecimal.ZERO);
			finalRespDto.setGstr3BIgst(BigDecimal.ZERO);
			finalRespDto.setGstr3BCgst(BigDecimal.ZERO);
			finalRespDto.setGstr3BSgst(BigDecimal.ZERO);
			finalRespDto.setGstr3BCess(BigDecimal.ZERO);
			finalRespDto.setDiffIgst(BigDecimal.ZERO);
			finalRespDto.setDiffCgst(BigDecimal.ZERO);
			finalRespDto.setDiffSgst(BigDecimal.ZERO);
			finalRespDto.setDiffCess(BigDecimal.ZERO);

			finalResp.add(finalRespDto);
		});
	}

	private static String deriveStatus(Set<String> uniqueStatuses) {

		if (uniqueStatuses.size() > 0) {
			// if (uniqueStatuses.size() > 1) {
			/*
			 * if (uniqueStatuses.contains(FAILED) &&
			 * uniqueStatuses.contains(SUCCESS)) { return PARTIALLY_SUCCESS; }
			 * if (uniqueStatuses.contains(SUCCESS)) return SUCCESS; if
			 * (uniqueStatuses.contains(FAILED)) return FAILED;
			 */
			// new logic to correct the status
			if (uniqueStatuses.contains(INITIATED)) {
				return INITIATED;
			} else if (uniqueStatuses.contains(INPROGRESS)) {
				return INPROGRESS;
			} else if ((uniqueStatuses.contains(SUCCESS)
					|| uniqueStatuses.contains(SUCCESS_WITH_NO_DATA))
					&& uniqueStatuses.contains(FAILED)) {
				return PARTIALLY_SUCCESS;
			} else if (uniqueStatuses.contains(SUCCESS)
					|| uniqueStatuses.contains(SUCCESS_WITH_NO_DATA)) {
				return SUCCESS;
			} else if (uniqueStatuses.contains(FAILED)) {
				return FAILED;
			}
			// }

		}

		return uniqueStatuses.stream().findFirst().get();

	}
}
