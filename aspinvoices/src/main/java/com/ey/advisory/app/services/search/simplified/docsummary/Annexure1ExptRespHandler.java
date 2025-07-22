package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.google.gson.JsonElement;

@Service("Annexure1ExptRespHandler")
public class Annexure1ExptRespHandler {

	@Autowired
	@Qualifier("anx1EXPTStructure")
	private Anx1EXPTStructure anx1EXPTStructure;

	public JsonElement handleExptResp(
			List<Annexure1SummaryResp1Dto> exptSummaryRespList) {

		JsonElement exptRespBody = anx1EXPTStructure
				.anx1ExptResp(exptSummaryRespList);

		return exptRespBody;
	}
	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumExptResp(
			List<Annexure1SummaryResp1Dto> exptSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto exptRespBody = anx1EXPTStructure
				.anx1SumExptResp(exptSummaryRespList,gstnResult);

		return exptRespBody;
	}
}
