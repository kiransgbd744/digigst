/**
 * 
 */
package com.ey.advisory.controller.gstr6;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.anx2.Itc04SummaryAtGstnImpl;
import com.ey.advisory.app.services.jobs.anx2.Itc04SummaryDataParserImpl;
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
public class Itc04GetGstnSummaryTestController {


	@Autowired
	@Qualifier("Itc04SummaryAtGstnImpl")
	private Itc04SummaryAtGstnImpl itc04SummaryAtGstn;
	
	@Autowired
	@Qualifier("Itc04SummaryDataParserImpl")
	Itc04SummaryDataParserImpl itc04SummaryDataParserImpl;
	
	@PostMapping(value = "/ui/gstnItc04Summary", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public String getAnx2GstnSumry(@RequestBody String jsonReq) {
		String groupCode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupCode);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getGstr7GstnSumry method called with args {}",
					jsonReq);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();

		Anx2GetInvoicesReqDto dto = gson.fromJson(jsonReq,
				Anx2GetInvoicesReqDto.class);

		return itc04SummaryAtGstn.getGstr6Summary(dto, groupCode);
	}

	@PostMapping(value = "/ui/gstnItc04SummaryTest", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void getAnx2GstnSumryTest(
			@RequestBody String jsonReq) {
		String groupCode = TenantContext.getTenantId();
		TenantContext.setTenantId(groupCode);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getGstr7GstnSumry method called with args {}",
					jsonReq);
		}
		
		Anx2GetInvoicesReqDto a=new Anx2GetInvoicesReqDto();
		a.setGstin("29AAAPH9357H000");
a.setReturnPeriod("102019");
		itc04SummaryDataParserImpl.itc04SummaryData(a, jsonReq, 1234L);

		//return gstr6Summary;
	}
	
	
}
