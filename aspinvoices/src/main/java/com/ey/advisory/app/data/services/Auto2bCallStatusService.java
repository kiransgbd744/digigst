package com.ey.advisory.app.data.services;

import java.util.List;

import com.ey.advisory.admin.data.entities.client.Gstr2bAutoCommEntity;
import com.ey.advisory.app.dashboard.apiCall.ApiGstinDetailsDto;

public interface Auto2bCallStatusService {

	public List<Gstr2bAutoCommEntity> fetchGetStatus(List<String> gstinDtoList,
			Integer derivedStartPeriod, Integer derivedEndPeriod,
			String returnType);

	public List<ApiGstinDetailsDto> getTaxPeriodDetails(
			List<Gstr2bAutoCommEntity> getBatchEntityDetails,
			String returnType);

}
