package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.google.gson.JsonElement;

@Service("Annexure1DeemExptRespHandler")
public class Annexure1DeemExptRespHandler {
	

	@Autowired
	@Qualifier("anx1DExpStructure")
	private Anx1DExpStructure anx1DExpStructure;

	public JsonElement handleDExpResp(
			List<Annexure1SummaryResp1Dto> dexpSummaryRespList) {

		JsonElement detRespBody = anx1DExpStructure
				.anx1DexpResp(dexpSummaryRespList);

		return detRespBody;
	}
	
	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumDExpResp(
			List<Annexure1SummaryResp1Dto> dexpSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto sezwtRespBody = anx1DExpStructure
				.anx1SumDexpResp(dexpSummaryRespList,gstnResult);

		return sezwtRespBody;
	}



}
