/**
 * 
 */
package com.ey.advisory.sap.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.services.jobs.anx2.Gstr6SummaryAtGstnImpl;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Nandam
 *
 */
@RestController
@Slf4j
public class Gstr6GetGstnSummaryTestController {

	private static final String GROUP_CODE = TestController.staticTenantId();

	@Autowired
	@Qualifier("Gstr6SummaryAtGstnImpl")
	private Gstr6SummaryAtGstnImpl gstr6SummaryAtGstn;

	@PostMapping(value = "/ui/gstnGstr6Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnx2GstnSumry(
			@RequestBody String jsonReq) {
		
		TenantContext.setTenantId(GROUP_CODE);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getGstr6GstnSumry method called with args {}",
					jsonReq);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();

		Gstr6GetInvoicesReqDto dto = gson.fromJson(jsonReq,
				Gstr6GetInvoicesReqDto.class);
		ResponseEntity<String> gstr6Summary = gstr6SummaryAtGstn.getGstr6Summary(dto, GROUP_CODE);
		

		return gstr6Summary;
		
		
	}
	

	@RequestMapping(value = "/ui/gstGstr6Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnxGstnSumry(
			@RequestBody String jsonReq) {
		
		TenantContext.setTenantId(GROUP_CODE);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getGstr6GstnSumry method called with args {}",
					jsonReq);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();

		Gstr6GetInvoicesReqDto dto = gson.fromJson(jsonReq,
				Gstr6GetInvoicesReqDto.class);
		ResponseEntity<String> gstr6Summary = gstr6SummaryAtGstn.getGstr6ITCDetailsSummary(dto, GROUP_CODE);
		

		return gstr6Summary;
		
		
	}

}
