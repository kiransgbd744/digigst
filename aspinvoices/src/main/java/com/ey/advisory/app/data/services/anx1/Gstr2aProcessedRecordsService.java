package com.ey.advisory.app.data.services.anx1;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.Gstr2aProcessedDataRecordsFetchDao;
import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedDataRecordsRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr2aProcessedRecordsService")
@Slf4j
public class Gstr2aProcessedRecordsService {

	@Autowired
	@Qualifier("Gstr2aProcessedDataRecordsFetchDaoImpl")
	private Gstr2aProcessedDataRecordsFetchDao gstr2aProcessedRecordsFetchDao;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	public List<Gstr2aProcessedDataRecordsRespDto> findGstr2aProcessedRecords(
			Gstr2aProcessedDataRecordsReqDto gstr2aProcessedDataRecordsReqDto) {
		
		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR_2A_PROCESS,
				"REQPREPARE_START", "Gstr2aProcessedRecordsService",
				"findGstr2aProcessedRecords", "");
		Gstr2aProcessedDataRecordsReqDto reqDto = processedRecordsCommonSecParam
				.setGstr2aProcessedDataSecuritySearchParams(
						gstr2aProcessedDataRecordsReqDto);
		
		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR_2A_PROCESS,
				"REQPREPARE_END", "Gstr2aProcessedRecordsService",
				"findGstr2aProcessedRecords", "");

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR_2A_PROCESS,
				"RESPDTO_START", "Gstr2aProcessedRecordsService",
				"findGstr2aProcessedRecords", "");
		
		List<Gstr2aProcessedDataRecordsRespDto> processedRecordsRespDtos = gstr2aProcessedRecordsFetchDao
				.loadGstr2aDataProcessedRecords(reqDto);

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR_2A_PROCESS,
				"RESPDTO_END", "Gstr2aProcessedRecordsService",
				"findGstr2aProcessedRecords", "");

		return filterDataByStatusBySectionAndGstin(processedRecordsRespDtos);
	}

	private List<Gstr2aProcessedDataRecordsRespDto> filterDataByStatusBySectionAndGstin(
			List<Gstr2aProcessedDataRecordsRespDto> processedRecordsRespDtos) {
		List<Gstr2aProcessedDataRecordsRespDto> finalRespDtos = Lists
				.newArrayList();
		if (CollectionUtils.isNotEmpty(processedRecordsRespDtos)) {
			Map<String, List<Gstr2aProcessedDataRecordsRespDto>> gstinsMap = processedRecordsRespDtos
					.stream().collect(Collectors.groupingBy(
							Gstr2aProcessedDataRecordsRespDto::getGstin));
			gstinsMap.keySet().forEach(gstin -> {
				Gstr2aProcessedDataRecordsRespDto finalDto = new Gstr2aProcessedDataRecordsRespDto();
				List<Gstr2aProcessedDataRecordsRespDto> dataList = gstinsMap
						.get(gstin);

				String state = null;
				String regType = null;
				String authToken = null;
				String janTimestamp = null;
				String febTimeStamp = null;
				String marchTimestamp = null;
				String apriltimestamp = null;
				String mayTimeStamp = null;
				String juneTimeStamp = null;
				String julyTimestamp = null;
				String augTimeStamp = null;
				String sepTimeStamp = null;
				String octTimestamp = null;
				String novTimeStamp = null;
				String decTimeStamp = null;

				finalDto.setGstin(gstin);
				for (Gstr2aProcessedDataRecordsRespDto dbDto : dataList) {
					state = dbDto.getState();
					regType = dbDto.getRegType();
					authToken = dbDto.getAuthToken();
					if (StringUtils.isEmpty(janTimestamp)) {
						janTimestamp = dbDto.getJanTimestamp() != null
								? convertTimeStampToString(
										dbDto.getJanTimestamp())
								: APIConstants.EMPTY;
					}

					if (StringUtils.isEmpty(febTimeStamp)) {
						febTimeStamp = dbDto.getFebTimeStamp() != null
								? convertTimeStampToString(
										dbDto.getFebTimeStamp())
								: APIConstants.EMPTY;
					}

					if (StringUtils.isEmpty(marchTimestamp)) {
						marchTimestamp = dbDto.getMarchTimestamp() != null
								? convertTimeStampToString(
										dbDto.getMarchTimestamp())
								: APIConstants.EMPTY;
					}

					if (StringUtils.isEmpty(apriltimestamp)) {
						apriltimestamp = dbDto.getApriltimestamp() != null
								? convertTimeStampToString(
										dbDto.getApriltimestamp())
								: APIConstants.EMPTY;
					}
					if (StringUtils.isEmpty(mayTimeStamp)) {
						mayTimeStamp = dbDto.getMayTimeStamp() != null
								? convertTimeStampToString(
										dbDto.getMayTimeStamp())
								: APIConstants.EMPTY;
					}
					if (StringUtils.isEmpty(juneTimeStamp)) {
						juneTimeStamp = dbDto.getJuneTimeStamp() != null
								? convertTimeStampToString(
										dbDto.getJuneTimeStamp())
								: APIConstants.EMPTY;
					}
					if (StringUtils.isEmpty(julyTimestamp)) {
						julyTimestamp = dbDto.getJulyTimestamp() != null
								? convertTimeStampToString(
										dbDto.getJulyTimestamp())
								: APIConstants.EMPTY;
					}
					if (StringUtils.isEmpty(augTimeStamp)) {
						augTimeStamp = dbDto.getAugTimeStamp() != null
								? convertTimeStampToString(
										dbDto.getAugTimeStamp())
								: APIConstants.EMPTY;
					}
					if (StringUtils.isEmpty(sepTimeStamp)) {
						sepTimeStamp = dbDto.getSepTimeStamp() != null
								? convertTimeStampToString(
										dbDto.getSepTimeStamp())
								: APIConstants.EMPTY;
					}
					if (StringUtils.isEmpty(octTimestamp)) {
						octTimestamp = dbDto.getOctTimestamp() != null
								? convertTimeStampToString(
										dbDto.getOctTimestamp())
								: APIConstants.EMPTY;
					}
					if (StringUtils.isEmpty(novTimeStamp)) {
						novTimeStamp = dbDto.getNovTimeStamp() != null
								? convertTimeStampToString(
										dbDto.getNovTimeStamp())
								: APIConstants.EMPTY;
					}
					if (StringUtils.isEmpty(decTimeStamp)) {
						decTimeStamp = dbDto.getDecTimestamp() != null
								? convertTimeStampToString(
										dbDto.getDecTimestamp())
								: APIConstants.EMPTY;
					}
				}

				finalDto.setState(state);
				finalDto.setRegType(regType);
				finalDto.setAuthToken(authToken);
				finalDto.setJanTimestamp(janTimestamp);
				finalDto.setFebTimeStamp(febTimeStamp);
				finalDto.setMarchTimestamp(marchTimestamp);
				finalDto.setApriltimestamp(apriltimestamp);
				finalDto.setMayTimeStamp(mayTimeStamp);
				finalDto.setJuneTimeStamp(juneTimeStamp);
				finalDto.setJulyTimestamp(julyTimestamp);
				finalDto.setAugTimeStamp(augTimeStamp);
				finalDto.setSepTimeStamp(sepTimeStamp);
				finalDto.setOctTimestamp(octTimestamp);
				finalDto.setNovTimeStamp(novTimeStamp);
				finalDto.setDecTimestamp(decTimeStamp);

				List<String> janStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getJanStatus)
						.collect(Collectors.toList());
				String janStatus = filterDataByStatus(janStatusList);
				finalDto.setJanStatus(janStatus);

				List<String> febStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getFebStatus)
						.collect(Collectors.toList());
				String febStatus = filterDataByStatus(febStatusList);
				finalDto.setFebStatus(febStatus);

				List<String> marStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getMarchStatus)
						.collect(Collectors.toList());
				String marStatus = filterDataByStatus(marStatusList);
				finalDto.setMarchStatus(marStatus);

				List<String> aprilStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getAprilStatus)
						.collect(Collectors.toList());
				String aprilStatus = filterDataByStatus(aprilStatusList);
				finalDto.setAprilStatus(aprilStatus);

				List<String> mayStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getMayStatus)
						.collect(Collectors.toList());
				String mayStatus = filterDataByStatus(mayStatusList);
				finalDto.setMayStatus(mayStatus);

				List<String> juneStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getJuneStatus)
						.collect(Collectors.toList());
				String juneStatus = filterDataByStatus(juneStatusList);
				finalDto.setJuneStatus(juneStatus);

				List<String> julyStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getJulyStatus)
						.collect(Collectors.toList());
				String julyStatus = filterDataByStatus(julyStatusList);
				finalDto.setJulyStatus(julyStatus);

				List<String> augStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getAugStatus)
						.collect(Collectors.toList());
				String augStatus = filterDataByStatus(augStatusList);
				finalDto.setAugStatus(augStatus);

				List<String> sepStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getSepStatus)
						.collect(Collectors.toList());
				String sepStatus = filterDataByStatus(sepStatusList);
				finalDto.setSepStatus(sepStatus);

				List<String> octStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getOctStatus)
						.collect(Collectors.toList());
				String octStatus = filterDataByStatus(octStatusList);
				finalDto.setOctStatus(octStatus);

				List<String> novStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getNovStatus)
						.collect(Collectors.toList());
				String novStatus = filterDataByStatus(novStatusList);
				finalDto.setNovStatus(novStatus);

				List<String> decStatusList = dataList.stream()
						.map(Gstr2aProcessedDataRecordsRespDto::getDecStatus)
						.collect(Collectors.toList());
				String decStatus = filterDataByStatus(decStatusList);
				finalDto.setDecStatus(decStatus);

				finalRespDtos.add(finalDto);
			});

		}
		if (CollectionUtils.isNotEmpty(finalRespDtos)) {
			return finalRespDtos.stream()
					.filter(dto -> !dto.getRegType().equalsIgnoreCase("ISD"))
					.collect(Collectors.toList());
		}
		return finalRespDtos;
	}

	private String convertTimeStampToString(String timestamp) {
		try {
			if (StringUtils.isNotBlank(timestamp)) {
				String[] strings = timestamp.split("T");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
				String format = sdf2.format(sdf.parse(strings[0]));
				String finalDateString = format + " : " + strings[1];
				finalDateString = finalDateString.substring(0,
						finalDateString.length() - 4);
				return finalDateString;
			}
		} catch (Exception ex) {
			String msg = "Parse exception";
			LOGGER.error(msg);
			throw new AppException(msg, ex);

		}
		return "";
	}

	private String filterDataByStatus(List<String> statusList) {
		String status = "";
		Set<String> statusSet = Sets.newHashSet(statusList);
		System.out.println(statusList);
		System.out.println(statusSet);

		if (statusSet.contains("INITIATED")) {
			status = "INITIATED";
		} else if (statusSet.contains("INPROGRESS")) {
			status = "INPROGRESS";
		} else if ((statusSet.contains("SUCCESS")
				&& statusSet.contains("FAILED")
				&& statusSet.contains("SUCCESS_WITH_NO_DATA"))) {
			status = "PARTIALLY_SUCCESS";
		} else if (statusSet.contains("FAILED")
				&& statusSet.contains("SUCCESS_WITH_NO_DATA")) {
			status = "PARTIALLY_SUCCESS";
		} else if (statusSet.contains("FAILED")
				&& statusSet.contains("SUCCESS")) {
			status = "PARTIALLY_SUCCESS";
		} else if ((statusSet.contains("SUCCESS")
				&& statusSet.contains("SUCCESS_WITH_NO_DATA"))) {
			status = "SUCCESS";
		} else if (statusSet.contains("SUCCESS")) {
			status = "SUCCESS";
		} else if (statusSet.contains("SUCCESS_WITH_NO_DATA")) {
			status = "SUCCESS";
		} else if (statusSet.contains("FAILED")) {
			status = "FAILED";

		}
		return status;
	}

}
