package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1ImpgSezStructure;
import com.ey.advisory.app.services.search.docsummarysearch.Anx1ImpsStructure;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("annexure1ImpsRespHandler")
public class Annexure1ImpsRespHandler {

	@Autowired
	@Qualifier("anx1ImpsStructure")
	private Anx1ImpsStructure anx1ImpsStructure;

	public JsonElement handleImpsResp(
			List<Annexure1SummaryResp1Dto> impsISummaryRespList) {

		JsonElement impsIRespBody = anx1ImpsStructure
				.anx1ImpsResp(impsISummaryRespList);

		return impsIRespBody;
	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto handleSumImpsResp(
			List<Annexure1SummaryResp1Dto> impsISummaryRespList,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummaryResp1Dto impsIRespBody = anx1ImpsStructure
				.anx1SumImpsResp(impsISummaryRespList,gstnResult);

		return impsIRespBody;
	}

}
