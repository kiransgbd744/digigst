package com.ey.advisory.apidashboard.common.service;

import java.time.LocalDateTime;

public interface Gstr9ControlService {

	public int updateWorkflowStatus(String jobStatus, LocalDateTime updatedDate,
			String gstinId, String taxPeriod, String returnType,
			String apiCall);

}
