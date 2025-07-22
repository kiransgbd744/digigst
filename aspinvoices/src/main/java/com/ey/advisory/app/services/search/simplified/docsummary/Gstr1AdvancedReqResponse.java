package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1AdvancedVerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Sasidhar
 *
 */
@Component("Gstr1AdvancedReqResponse")
public class Gstr1AdvancedReqResponse {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1AdvancedReqResponse.class);

	@Autowired
	@Qualifier("Gstr1AdvancedSummaryService")
	Gstr1AdvancedSummaryService service;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	Gson gson = GsonUtil.newSAPGsonInstance();

	public JsonElement gstr1ReqRespDetails(Annexure1SummaryReqDto req) {

		basicCommonSecParam
				.setOutwardSumDataSecuritySearchParams(req);

		List<Ret1AspVerticalSummaryDto> summaryDetails = service
				.findSummaryDetails(req);
		
		List<Ret1AspVerticalSummaryDto> gstnDetails = service
				.findgstnDetails(req);
		
		List<Gstr1AdvancedVerticalSummaryRespDto> VerticalDetails = service
				.findVerticalDetails(req);
		
		JsonElement summaryRespBody = gson.toJsonTree(summaryDetails);
		JsonElement gstnRespBody = gson.toJsonTree(gstnDetails);
		JsonElement vertRespBody = gson.toJsonTree(VerticalDetails);
		Map<String, JsonElement> combinedMap = new HashMap<>();

		combinedMap.put("summary", summaryRespBody);
		combinedMap.put("gstnView", gstnRespBody);
		combinedMap.put("verticalData", vertRespBody);
		JsonElement respBody = gson.toJsonTree(combinedMap);

		return respBody;
	}

}
