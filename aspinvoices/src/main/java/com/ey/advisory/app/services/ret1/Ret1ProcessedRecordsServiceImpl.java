package com.ey.advisory.app.services.ret1;

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
import com.ey.advisory.app.data.daos.client.Ret1ProcessedRecordsDao;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.api.APIConstants;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Ret1ProcessedRecordsServiceImpl")
public class Ret1ProcessedRecordsServiceImpl
		implements Ret1ProcessedRecordsService {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(Ret1ProcessedRecordsServiceImpl.class);

	@Autowired
	@Qualifier("Ret1ProcessedRecordsDaoImpl")
	private Ret1ProcessedRecordsDao ret1ProcessedRecordsDao;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	public List<Ret1ProcessedRecordsResponseDto> fetchProcessedRecords(
			Ret1ProcessedRecordsRequestDto processedRecordsRequestDto)
			throws Exception {
		List<Ret1ProcessedRecordsResponseDto> ret1ProcessedRecordsResponseDtos = new ArrayList<>();

		List<GSTNDetailEntity> gstnDetailEntities = gstnDetailRepository
				.findByEntityIds(processedRecordsRequestDto.getEntityId());

		List<GSTNDetailEntity> filteredGstinEntites = new ArrayList<>();
		applyDataSecAtributesOnGstinList(gstnDetailEntities,
				processedRecordsRequestDto, filteredGstinEntites);

		List<String> gstins = filteredGstinEntites.stream()
				.map(gstin -> gstin.getGstin()).collect(Collectors.toList());

		if (!gstins.isEmpty() && gstins.size() > 0) {
			ret1ProcessedRecordsResponseDtos.addAll(
					ret1ProcessedRecordsDao.fetchRet1ProcessedRecords(gstins,
							processedRecordsRequestDto));
		}

		checkDataIfNotPresentAddByGstins(gstins,
				ret1ProcessedRecordsResponseDtos);
		Comparator<Ret1ProcessedRecordsResponseDto> stateNameComparator = (
				Ret1ProcessedRecordsResponseDto o1,
				Ret1ProcessedRecordsResponseDto o2) -> o1.getState()
						.compareTo(o2.getState());

		Collections.sort(ret1ProcessedRecordsResponseDtos, stateNameComparator);
		return ret1ProcessedRecordsResponseDtos;
	}

	private void checkDataIfNotPresentAddByGstins(List<String> gstins,
			List<Ret1ProcessedRecordsResponseDto> ret1ProcessedRecordsResponseDtos) {
		if (ret1ProcessedRecordsResponseDtos.isEmpty()
				&& ret1ProcessedRecordsResponseDtos.size() == 0) {
			if (!gstins.isEmpty() && gstins.size() > 0) {
				gstins.forEach(gstin -> {
					Ret1ProcessedRecordsResponseDto dto = addGstinDataToResponseDtos(
							gstin, APIConstants.EMPTY);
					ret1ProcessedRecordsDao.addStateAndAuthTokenData(dto,
							gstin);
					ret1ProcessedRecordsResponseDtos.add(dto);
				});
			} else {
				ret1ProcessedRecordsResponseDtos.add(addGstinDataToResponseDtos(
						APIConstants.EMPTY, APIConstants.EMPTY));
			}
		} else if (!ret1ProcessedRecordsResponseDtos.isEmpty()
				&& ret1ProcessedRecordsResponseDtos.size() >= 1) {
			List<String> dataGstins = ret1ProcessedRecordsResponseDtos.stream()
					.map(gstin -> gstin.getGstin())
					.collect(Collectors.toList());
			List<String> finalList = gstins.stream()
					.filter(st -> !dataGstins.contains(st))
					.collect(Collectors.toList());
			finalList.forEach(gstin -> {
				Ret1ProcessedRecordsResponseDto dto = addGstinDataToResponseDtos(
						gstin, APIConstants.EMPTY);
				ret1ProcessedRecordsDao.addStateAndAuthTokenData(dto, gstin);
				ret1ProcessedRecordsResponseDtos.add(dto);
			});
		}

	}

	private Ret1ProcessedRecordsResponseDto addGstinDataToResponseDtos(
			String gstin, String state) {
		Ret1ProcessedRecordsResponseDto dto = new Ret1ProcessedRecordsResponseDto();
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
		dto.setTds(BigDecimal.ZERO);
		dto.setTcs(BigDecimal.ZERO);
		return dto;
	}

	private List<GSTNDetailEntity> applyDataSecAtributesOnGstinList(
			List<GSTNDetailEntity> gstnDetailEntities,
			Ret1ProcessedRecordsRequestDto processedRecordsRequestDto,
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
