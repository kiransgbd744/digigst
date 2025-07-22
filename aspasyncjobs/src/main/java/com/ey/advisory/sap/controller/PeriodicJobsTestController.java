/**
 * 
 */
package com.ey.advisory.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.Message;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.monitor.processors.MonitorGstr1PollingReadinessProcessor;
import com.ey.advisory.monitor.processors.MonitorGstr6SaveOverallStatusProcessor;

/**
 * @author Hemasundar.J
 *
 */
@RestController
public class PeriodicJobsTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();

	@Autowired
	private MonitorGstr1PollingReadinessProcessor monitorGstr1;
	
	@Autowired
	private MonitorGstr6SaveOverallStatusProcessor monitorGstr6;

	@PostMapping(value = { "/monitorGstr1" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void monitorGstr1() {

		Group group = new Group();
		group.setGroupCode(GROUP_CODE);
		Message message = new Message();
		message.setId(1l);
		monitorGstr1.executeForGroup(group, message, null);
	}
	
	@PostMapping(value = { "/monitorGstr6" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void monitorGstr6() {

		Group group = new Group();
		group.setGroupCode(GROUP_CODE);
		Message message = new Message();
		message.setId(1l);
		monitorGstr6.executeForGroup(group, message, null);
	}
}
