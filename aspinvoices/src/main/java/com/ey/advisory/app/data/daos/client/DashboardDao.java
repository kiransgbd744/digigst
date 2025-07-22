package com.ey.advisory.app.data.daos.client;

import java.util.List;

import com.ey.advisory.core.dto.DashboardReqDto;

public interface DashboardDao {

	List<Object[]> getGstrReturnStatus(List<String> gstin, String retPeriod,
			List<String> returnType, String status);

	List<Object[]> getSupplyDetails(Long entityId, DashboardReqDto reqDto);

}
