package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.google.gson.JsonElement;

@Service("Annexure1ExpwtRespHandler")
public class Annexure1ExpwtRespHandler {
	
	
	@Autowired
	@Qualifier("anx1EXPWTStructure")
	private Anx1EXPWTStructure anx1EXPWTStructure;

	public JsonElement handleExpwtResp(
			List<Annexure1SummaryResp1Dto> expwtSummaryRespList) {

		JsonElement expwtRespBody = anx1EXPWTStructure
				.anx1ExpwtResp(expwtSummaryRespList);

		return expwtRespBody;
	}
	
	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumExpwtResp(
			List<Annexure1SummaryResp1Dto> expwtSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto expwtRespBody = anx1EXPWTStructure
				.anx1SumExpwtResp(expwtSummaryRespList,gstnResult);

		return expwtRespBody;
	}

}
