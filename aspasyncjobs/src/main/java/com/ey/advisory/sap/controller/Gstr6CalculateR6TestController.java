/**
 * 
 */
package com.ey.advisory.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.processors.handler.Gstr6CalculateR6Handler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@RestController
public class Gstr6CalculateR6TestController {
	
	private static final String GROUP_CODE = TestController.staticTenantId();

	@Autowired
	private Gstr6CalculateR6Handler gstr6JobHandler;

	@PostMapping(value = "/gstr6CalculateR6",  produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void submitRefIdStatus(@RequestBody String jsonString) {

		String groupCode = GROUP_CODE;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr6CalculateR6Processor Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}

		if (jsonString != null && groupCode != null) {
			try {
				gstr6JobHandler.calculateR6(jsonString, groupCode, APIConstants.SYSTEM);
				LOGGER.info("Gstr6CalculateR6Processor Processed with args {} ",
						jsonString);

			} catch (Exception ex) {
				String msg = "App Exception";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}

	}
}
