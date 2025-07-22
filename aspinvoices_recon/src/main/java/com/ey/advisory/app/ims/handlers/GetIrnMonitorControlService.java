package com.ey.advisory.app.ims.handlers;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.MonitorGstnGetIrnStatusEntity;

@Service("GetIrnMonitorControlServiceImpl")
public interface GetIrnMonitorControlService {

	void monitorControlEntries(
			List<MonitorGstnGetIrnStatusEntity> monitorEntities);
}
