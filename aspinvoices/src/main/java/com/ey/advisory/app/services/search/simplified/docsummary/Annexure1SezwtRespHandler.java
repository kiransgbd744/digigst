package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.google.gson.JsonElement;

@Service("Annexure1SezwtRespHandler")
public class Annexure1SezwtRespHandler {
	

	@Autowired
	@Qualifier("anx1SEZWTStructure")
	private Anx1SEZWTStructure anx1SEZTStructure;

	public JsonElement handleSezwtResp(
			List<Annexure1SummaryResp1Dto> sezwtSummaryRespList) {

		JsonElement sezwtRespBody = anx1SEZTStructure
				.anx1SezwtResp(sezwtSummaryRespList);

		return sezwtRespBody;
	}
	
	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumSezwtResp(
			List<Annexure1SummaryResp1Dto> sezwtSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto sezwtRespBody = anx1SEZTStructure
				.anx1SumSezwtResp(sezwtSummaryRespList,gstnResult);

		return sezwtRespBody;
	}

}
