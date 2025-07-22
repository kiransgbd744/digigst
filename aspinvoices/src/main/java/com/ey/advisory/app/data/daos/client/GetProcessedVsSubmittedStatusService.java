/**
 * 
 */
package com.ey.advisory.app.data.daos.client;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.GetProcessedVsSubmittedStatusRespDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ProcessedVsSubmittedRequestDto;
import com.google.common.base.Strings;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("GetProcessedVsSubmittedStatusService")
public class GetProcessedVsSubmittedStatusService {

	@Autowired
	@Qualifier("GetProcessedVsSubmittedStatusDaoImpl")
	private GetProcessedVsSubmittedStatusDao getProcessedVsSubmittedStatusDao;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository anx1BatchRepository;

	public List<GetProcessedVsSubmittedStatusRespDto> findByCriteria(
			ProcessedVsSubmittedRequestDto criteria, List<String> gstnList)
			throws Exception {

		String taxPeriodFrom = criteria.getTaxPeriodFrom();
		String taxPeriodTo = criteria.getTaxPeriodTo();

		int derivedTaxPeriodFrom = 0;
		if (!Strings.isNullOrEmpty(taxPeriodFrom)) {
			derivedTaxPeriodFrom = GenUtil.convertTaxPeriodToInt(taxPeriodFrom);
		}
		int derivedTaxPeriodTo = 0;
		if (!Strings.isNullOrEmpty(taxPeriodTo)) {
			derivedTaxPeriodTo = GenUtil.convertTaxPeriodToInt(taxPeriodTo);
		}

		List<GetProcessedVsSubmittedStatusRespDto> summaryList = new ArrayList<GetProcessedVsSubmittedStatusRespDto>();
		List<Object[]> dataStatusList = getProcessedVsSubmittedStatusDao
				.findDataByCriteria(criteria, gstnList);

		if (CollectionUtils.isNotEmpty(dataStatusList)) {
			summaryList.addAll(dataStatusList.parallelStream()
					.map(obj -> convert(obj)).collect(Collectors.toList()));

		}
		fillTheDefaultData(summaryList, gstnList, derivedTaxPeriodFrom,
				derivedTaxPeriodTo);
		List<GetProcessedVsSubmittedStatusRespDto> finalList = segregateTheGstinByStatusList(
				summaryList);
		return finalList;
	}

	private void fillTheDefaultData(
			List<GetProcessedVsSubmittedStatusRespDto> summaryList,
			List<String> gstnList, int derivedTaxPeriodFrom,
			int derivedTaxPeriodTo) {

		List<String> dataGstinList = new ArrayList<>();
		summaryList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		for (String gstin : gstnList) {
			int count = anx1BatchRepository.gstinCountByTaxPerFromTo(gstin,
					derivedTaxPeriodFrom, derivedTaxPeriodTo);
			if (count == 0 || !dataGstinList.contains(gstin)) {
				GetProcessedVsSubmittedStatusRespDto dto = new GetProcessedVsSubmittedStatusRespDto();
				dto.setGstin(gstin);
				dto.setStatus("-");
				dto.setLastUpdatedTimeStamp("-");

				summaryList.add(dto);
			}
		}
	}

	private List<GetProcessedVsSubmittedStatusRespDto> segregateTheGstinByStatusList(
			List<GetProcessedVsSubmittedStatusRespDto> summaryList) {
		List<GetProcessedVsSubmittedStatusRespDto> finalList = new ArrayList<GetProcessedVsSubmittedStatusRespDto>();
		if (CollectionUtils.isNotEmpty(summaryList)) {
			Map<String, List<GetProcessedVsSubmittedStatusRespDto>> gstinMap = summaryList
					.stream().collect(Collectors.groupingBy(e -> e.getGstin()));

			gstinMap.keySet().forEach(gstin -> {

				String lastUpdatedTimeStamp = " - ";
				String status = " - ";

				List<GetProcessedVsSubmittedStatusRespDto> dtos = gstinMap
						.get(gstin);
				for (GetProcessedVsSubmittedStatusRespDto dto : dtos) {
					if (!StringUtils.isEmpty(dto.getStatus())) {
						status = dto.getStatus();
					}
					if (!StringUtils.isEmpty(dto.getLastUpdatedTimeStamp())) {
						lastUpdatedTimeStamp = dto.getLastUpdatedTimeStamp();
					}

					GetProcessedVsSubmittedStatusRespDto respDto = new GetProcessedVsSubmittedStatusRespDto();
					respDto.setGstin(gstin);
					respDto.setStatus(status);
					respDto.setLastUpdatedTimeStamp(lastUpdatedTimeStamp);

					finalList.add(respDto);
				}
			});
		}
		return finalList;
	}

	private GetProcessedVsSubmittedStatusRespDto convert(Object[] obj) {
		GetProcessedVsSubmittedStatusRespDto dto = new GetProcessedVsSubmittedStatusRespDto();

		dto.setGstin((String) obj[0]);
		dto.setGetType((String) obj[3]);
		dto.setStatus((String) obj[4]);

		if (obj[5] == null) {
			dto.setLastUpdatedTimeStamp(null);
		} else {
			Timestamp date = (Timestamp) obj[5];
			LocalDateTime dt = date.toLocalDateTime();
			LocalDateTime dateTimeFormatter = EYDateUtil
					.toISTDateTimeFromUTC(dt);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy : HH:mm:ss");
			String newdate = FOMATTER.format(dateTimeFormatter);

			dto.setLastUpdatedTimeStamp(newdate);
		}
		return dto;
	}
}
