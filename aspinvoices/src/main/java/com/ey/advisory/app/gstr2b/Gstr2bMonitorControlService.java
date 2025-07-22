package com.ey.advisory.app.gstr2b;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.MonitorGstnGetStatusEntity;

@Service("Gstr2bMonitorControlService")
public interface Gstr2bMonitorControlService {

	void monitorControlEntries(
			List<MonitorGstnGetStatusEntity> monitorEntities);
}
