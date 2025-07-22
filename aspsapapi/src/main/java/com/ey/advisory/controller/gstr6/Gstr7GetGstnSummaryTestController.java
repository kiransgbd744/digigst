/**
 * 
 */
package com.ey.advisory.controller.gstr6;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.anx2.Gstr6SummaryDataParserImpl;
import com.ey.advisory.app.services.jobs.anx2.Gstr7SummaryAtGstnImpl;
import com.ey.advisory.app.services.jobs.anx2.Gstr7SummaryDataParserImpl;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Nandam
 *
 */
@RestController
@Slf4j
public class Gstr7GetGstnSummaryTestController {

	@Autowired
	@Qualifier("Gstr7SummaryDataParserImpl")
	Gstr7SummaryDataParserImpl gstr7SummaryDataParserImpl;

	@Autowired
	@Qualifier("Gstr7SummaryAtGstnImpl")
	private Gstr7SummaryAtGstnImpl gstr7SummaryAtGstn;
	
	@Autowired
	@Qualifier("Gstr6SummaryDataParserImpl")
	Gstr6SummaryDataParserImpl gstr6SummaryDataParserImpl;
	
	@PostMapping(value = "/ui/gstnGstr7Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAnx2GstnSumry(
			@RequestBody String jsonReq) {
		String groupCode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupCode);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getGstr7GstnSumry method called with args {}",
					jsonReq);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();

		Anx2GetInvoicesReqDto dto = gson.fromJson(jsonReq,
				Anx2GetInvoicesReqDto.class);
		ResponseEntity<String> gstr6Summary = gstr7SummaryAtGstn.getGstr7Summary(dto, groupCode);

		return gstr6Summary;
	}

	@PostMapping(value = "/ui/gstnGstr7SummaryTest", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getAnx2GstnSumryTest(
			@RequestBody String jsonReq) {
		String groupCode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupCode);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getGstr7GstnSumry method called with args {}",
					jsonReq);
		}

		gstr7SummaryDataParserImpl.parsegstr7SummaryData(null, jsonReq, 1234L);

		//return gstr6Summary;
	}
	
	@PostMapping(value = "/ui/gstnGstr6SummaryTest", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getGSTR6GstnSumryTest(
			@RequestBody String jsonReq) {
		String groupCode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupCode);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getGstr7GstnSumry method called with args {}",
					jsonReq);
		}

		gstr6SummaryDataParserImpl.parseAnx2SummaryData(null, jsonReq, 1234L);

		//return gstr6Summary;
	}
}
