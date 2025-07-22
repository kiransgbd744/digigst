package com.ey.advisory.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.jobs.erp.SaveToGstnRI;
import com.ey.advisory.common.multitenancy.TenantContext;

/**
 * 
 * @author Hemasundar.J
 *
 */
@RestController
public class Gstr1SaveToGstnRevIntegrationTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();
	
	@Autowired
	@Qualifier("saveToGstnRIImpl")
	private SaveToGstnRI saveToGstnRI;

	@PostMapping(value = "/gstr1SaveToGstnDataToErp", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveToGstn() {
		TenantContext.setTenantId(GROUP_CODE);
		return saveToGstnRI.getTaxDocsForRevIntegration();
	}

}