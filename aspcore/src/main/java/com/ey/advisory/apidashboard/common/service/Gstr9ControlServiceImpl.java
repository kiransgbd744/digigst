package com.ey.advisory.apidashboard.common.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;

@Component
@Transactional(value = "clientTransactionManager",
readOnly = false, propagation = Propagation.REQUIRED)
public class Gstr9ControlServiceImpl implements Gstr9ControlService {

	@Autowired
	private GstinGetStatusRepository gstinStatusRepository;

	@Override
	public int updateWorkflowStatus(String jobStatus, LocalDateTime updatedDate,
			String gstinId, String taxPeriod, String returnType,
			String apiCall) {
		return gstinStatusRepository.updateWorkflowStatus(jobStatus, updatedDate,
				gstinId, taxPeriod, returnType, apiCall);

	}

}
