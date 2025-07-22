package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1VerticalDocSeriesRespDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Gstr1DocSeriesReqResponse")
public class Gstr1DocSeriesReqResponse {

	@Autowired
	@Qualifier("Gstr1InvoiceseriesSummaryService")
	private Gstr1InvoiceseriesSummaryService service;

	Gson gson = GsonUtil.newSAPGsonInstance();

	public JsonElement gstr1ReqRespDetails(Annexure1SummaryReqDto req) {

		List<Gstr1VerticalDocSeriesRespDto> gstnDetails = service
				.findgstnDetails(req);
		JsonElement gstnRespBody = gson.toJsonTree(gstnDetails);
		Map<String, JsonElement> combinedMap = new HashMap<>();

		combinedMap.put("gstnView", gstnRespBody);
		JsonElement respBody = gson.toJsonTree(combinedMap);

		return respBody;
	}

}
