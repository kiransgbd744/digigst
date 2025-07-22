package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1RevStructure;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("annexure1RevRespHandler")
public class Annexure1RevRespHandler {

	@Autowired
	@Qualifier("anx1RevStructure")
	private Anx1RevStructure anx1RevStructure;

	public JsonElement handleRevResp(
			List<Annexure1SummaryResp1Dto> revSummaryRespList) {

		JsonElement revChRespBody = anx1RevStructure
				.anx1RevResp(revSummaryRespList);

		return revChRespBody;
	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumRevResp(
			List<Annexure1SummaryResp1Dto> revSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto revChRespBody = anx1RevStructure
				.anx1SumRevResp(revSummaryRespList,gstnResult);

		return revChRespBody;
	}

}
