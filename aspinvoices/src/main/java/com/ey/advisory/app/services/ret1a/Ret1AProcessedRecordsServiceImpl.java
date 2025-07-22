package com.ey.advisory.app.services.ret1a;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.daos.client.Ret1AProcessedRecordsDao;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.api.APIConstants;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Ret1AProcessedRecordsServiceImpl")
public class Ret1AProcessedRecordsServiceImpl
		implements Ret1AProcessedRecordsService {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1AProcessedRecordsServiceImpl.class);

	@Autowired
	@Qualifier("Ret1AProcessedRecordsDaoImpl")
	private Ret1AProcessedRecordsDao ret1AProcessedRecordsDao;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	public List<Ret1AProcessedRecordsResponseDto> fetchProcessedRecords(
			Ret1AProcessedRecordsRequestDto processedRecordsRequestDto)
			throws Exception {
		List<Ret1AProcessedRecordsResponseDto> ret1AProcessedRecordsResponseDtos = new ArrayList<>();

		List<GSTNDetailEntity> gstnDetailEntities = gstnDetailRepository
				.findByEntityIds(processedRecordsRequestDto.getEntityId());

		List<GSTNDetailEntity> filteredGstinEntites = new ArrayList<>();
		applyDataSecAtributesOnGstinList(gstnDetailEntities,
				processedRecordsRequestDto, filteredGstinEntites);

		List<String> gstins = filteredGstinEntites.stream()
				.map(gstin -> gstin.getGstin()).collect(Collectors.toList());

		if (!gstins.isEmpty() && gstins.size() > 0) {
			ret1AProcessedRecordsResponseDtos.addAll(
					ret1AProcessedRecordsDao.fetchRet1AProcessedRecords(gstins,
							processedRecordsRequestDto));
		}

		checkDataIfNotPresentAddByGstins(gstins,
				ret1AProcessedRecordsResponseDtos);

		Comparator<Ret1AProcessedRecordsResponseDto> stateNameComparator = (
				Ret1AProcessedRecordsResponseDto o1,
				Ret1AProcessedRecordsResponseDto o2) -> o1.getState()
						.compareTo(o2.getState());

		Collections.sort(ret1AProcessedRecordsResponseDtos,
				stateNameComparator);
		return ret1AProcessedRecordsResponseDtos;
	}

	private void checkDataIfNotPresentAddByGstins(List<String> gstins,
			List<Ret1AProcessedRecordsResponseDto> ret1AProcessedRecordsResponseDtos) {
		if (ret1AProcessedRecordsResponseDtos.isEmpty()
				&& ret1AProcessedRecordsResponseDtos.size() == 0) {
			if (!gstins.isEmpty() && gstins.size() > 0) {
				gstins.forEach(gstin -> {
					Ret1AProcessedRecordsResponseDto dto = addGstinDataToResponseDtos(
							gstin, APIConstants.EMPTY);
					ret1AProcessedRecordsDao.addStateAndAuthTokenData(dto,
							gstin);
					ret1AProcessedRecordsResponseDtos.add(dto);
				});
			} else {
				ret1AProcessedRecordsResponseDtos
						.add(addGstinDataToResponseDtos(APIConstants.EMPTY,
								APIConstants.EMPTY));
			}
		} else if (!ret1AProcessedRecordsResponseDtos.isEmpty()
				&& ret1AProcessedRecordsResponseDtos.size() >= 1) {
			List<String> dataGstins = ret1AProcessedRecordsResponseDtos.stream()
					.map(gstin -> gstin.getGstin())
					.collect(Collectors.toList());
			List<String> finalList = gstins.stream()
					.filter(st -> !dataGstins.contains(st))
					.collect(Collectors.toList());
			finalList.forEach(gstin -> {
				Ret1AProcessedRecordsResponseDto dto = addGstinDataToResponseDtos(
						gstin, APIConstants.EMPTY);
				ret1AProcessedRecordsDao.addStateAndAuthTokenData(dto, gstin);
				ret1AProcessedRecordsResponseDtos.add(dto);
			});
		}

	}

	private Ret1AProcessedRecordsResponseDto addGstinDataToResponseDtos(
			String gstin, String state) {
		Ret1AProcessedRecordsResponseDto dto = new Ret1AProcessedRecordsResponseDto();
		dto.setState(state);
		dto.setGstin(gstin);
		dto.setRegType(APIConstants.EMPTY);
		dto.setAuthToken(APIConstants.EMPTY);
		dto.setStatus(APIConstants.EMPTY);
		dto.setTimestamp(APIConstants.EMPTY);
		dto.setLiability(BigDecimal.ZERO);
		dto.setRevCharge(BigDecimal.ZERO);
		dto.setOtherCharge(BigDecimal.ZERO);
		dto.setItc(BigDecimal.ZERO);

		return dto;
	}

	private List<GSTNDetailEntity> applyDataSecAtributesOnGstinList(
			List<GSTNDetailEntity> gstnDetailEntities,
			Ret1AProcessedRecordsRequestDto processedRecordsRequestDto,
			List<GSTNDetailEntity> filteredGstinEntites) {
		Map<String, List<String>> dataSecAttrs = processedRecordsRequestDto
				.getDataSecAttrs();

		String gstn = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstn = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		if (gstinList != null && !gstinList.isEmpty() && gstinList.size() > 0
				&& !gstinList.get(0).equals(APIConstants.EMPTY)) {
			for (GSTNDetailEntity entity : gstnDetailEntities) {
				if (gstinList.contains(entity.getGstin())) {
					filteredGstinEntites.add(entity);
				}
			}
		} else {
			filteredGstinEntites.addAll(gstnDetailEntities);
		}

		return filteredGstinEntites;

	}
}
