package com.ey.advisory.app.services.gstr1;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.beust.jcommander.internal.Lists;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.daos.client.Gstr1PopUpRecordsDao;
import com.google.common.collect.Sets;

@Component("Gstr1PopUpRecordsServiceImpl")
public class Gstr1PopUpRecordsServiceImpl {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1PopUpRecordsServiceImpl.class);

	@Autowired
	@Qualifier("Gstr1PopUpRecordsDaoImpl")
	private Gstr1PopUpRecordsDao gstr1PopUpRecordsDao;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	public Gstr1PopScreenRecordsFinalResponseDto fetchRec(
			Gstr1PopScreenRecordsRequestDto dto) throws Exception {

		List<String> reqGstins = dto.getGstins();
		if (CollectionUtils.isNotEmpty(reqGstins)) {
			Gstr1PopScreenRecordsFinalResponseDto finalResponseDto = gstr1PopUpRecordsDao
					.fetchProcessStatusData(dto, Lists.newArrayList());
			return filterDataByGstins(dto.getGstins(), finalResponseDto);
		} else {
			List<String> gstins = gstnDetailRepository
					.findgstinByEntityId(Long.parseLong(dto.getEntityId()));
			Gstr1PopScreenRecordsFinalResponseDto finalResponseDto = gstr1PopUpRecordsDao
					.fetchProcessStatusData(dto, gstins);
			return filterDataByGstins(gstins, finalResponseDto);
		}
	}

	private Gstr1PopScreenRecordsFinalResponseDto filterDataByGstins(
			List<String> gstins,
			Gstr1PopScreenRecordsFinalResponseDto finalDto) {
		Set<String> gstinSet = Sets.newHashSet(gstins);
		Gstr1PopScreenRecordsFinalResponseDto finalResponseDto = new Gstr1PopScreenRecordsFinalResponseDto();
		if (!CollectionUtils.isEmpty(gstinSet)) {
			List<Gstr1PopScreenRecordsResponseDto> lastCallList = finalDto
					.getLastCall();
			List<Gstr1PopScreenRecordsResponseDto> lastSuccessList = finalDto
					.getLastSuccess();

			List<String> lastCallGstins = Lists.newArrayList();
			List<String> lastSuccessGstins = Lists.newArrayList();

			if (!CollectionUtils.isEmpty(lastCallList)) {
				lastCallList.stream().forEach(
						callDto -> lastCallGstins.add(callDto.getGstin()));
			}

			if (!CollectionUtils.isEmpty(lastSuccessList)) {
				lastSuccessList.stream().forEach(
						callDto -> lastSuccessGstins.add(callDto.getGstin()));
			}

			Set<String> successSet = Sets.newHashSet(gstins);

			lastCallList.forEach(dto -> gstinSet.remove(dto.getGstin()));
			lastSuccessList.forEach(dto -> successSet.remove(dto.getGstin()));

			gstinSet.forEach(gstin -> {
				lastCallList.add(buildDummyRespDto(gstin));
			});

			successSet.forEach(gstin -> {
				lastSuccessList.add(buildDummyRespDto(gstin));
			});

			finalResponseDto.setLastCall(lastCallList);
			finalResponseDto.setLastSuccess(lastSuccessList);
		}
		return finalResponseDto;
	}

	private Gstr1PopScreenRecordsResponseDto buildDummyRespDto(
			final String gstin) {
		Gstr1PopScreenRecordsResponseDto ret1ProcessedRecordsResponseDto = new Gstr1PopScreenRecordsResponseDto();

		ret1ProcessedRecordsResponseDto.setGstin(gstin);
		ret1ProcessedRecordsResponseDto.setB2bTimeStamp("");
		ret1ProcessedRecordsResponseDto.setB2bStatus("");
		ret1ProcessedRecordsResponseDto.setCdnrTimeStamp("");
		ret1ProcessedRecordsResponseDto.setCdnrStatus("");
		ret1ProcessedRecordsResponseDto.setCdnurTimeStamp("");
		ret1ProcessedRecordsResponseDto.setCdnurStatus("");
		ret1ProcessedRecordsResponseDto.setExportsTimestamp("");
		ret1ProcessedRecordsResponseDto.setExportStatus("");
		return ret1ProcessedRecordsResponseDto;
	}
}
