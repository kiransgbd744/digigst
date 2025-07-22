package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1B2BStructure;
import com.google.gson.JsonElement;
/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("Annexure1B2BRespHandler")
public class Annexure1B2BRespHandler {

	/*
	 * @Autowired
	 * 
	 * @Qualifier("Annexure1SummaryStructure") private Annexure1SummaryStructure
	 * annexure1SummaryStructure;
	 */
	@Autowired
	@Qualifier("anx1B2BStructure")
	private Anx1B2BStructure anx1B2BStructure;

	public JsonElement handleB2BResp(
			List<Annexure1SummaryResp1Dto> b2bSummaryRespList) {

		JsonElement b2bRespBody = anx1B2BStructure
				.anx1B2bResp(b2bSummaryRespList);

		return b2bRespBody;
	}
	// Anx1 Summary Total API Calculating Total
	public Annexure1SummaryResp1Dto handleSumB2BResp(
			List<Annexure1SummaryResp1Dto> b2bSummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto b2bRespBody = anx1B2BStructure
				.anx1SumB2bResp(b2bSummaryRespList,gstnResult);

		return b2bRespBody;
	}

}
