package com.ey.advisory.app.dashboard.apiCall;

import java.util.List;
import java.util.Map;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;

public interface GstinApiCallService {

	public List<GstnGetStatusEntity> fetchGetStatus(List<String> gstinDtoList,
			Integer derivedStartPeriod, Integer derivedEndPeriod,
			String returnType);

	public List<ApiGstinDetailsDto> getTaxPeriodDetails(
			List<GstnGetStatusEntity> getBatchEntityDetails,
			List<Object[]> listZipstatus, String returnType);

	public List<ApiFyGstinDetailsDto> getFyPeriodDetails(
			List<GstnGetStatusEntity> getBatchEntityDetails,
			Map<String, String> gstinRegMap);

}
