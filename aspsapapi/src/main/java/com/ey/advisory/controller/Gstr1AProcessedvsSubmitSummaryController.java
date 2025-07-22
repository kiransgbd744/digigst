package com.ey.advisory.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.services.search.simplified.docsummary.gstr1A.Gstr1AProcsSubScreenDocNilReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.gstr1A.Gstr1AProcsSubmitScreenHSNReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.gstr1A.Gstr1AProcsSubmitScreenReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.gstr1A.Gstr1ASimpleDocGstnSummarySearchService;
import com.ey.advisory.app.services.search.simplified.docsummary.gstr1A.Gstr1ASummaryScreenAdvReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.gstr1A.Gstr1ASummaryScreenEcomSupReqRespHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@RestController
public class Gstr1AProcessedvsSubmitSummaryController {

	@Autowired
	@Qualifier("Gstr1AProcsSubmitScreenReqRespHandler")
	private Gstr1AProcsSubmitScreenReqRespHandler tranReqResp;

	@Autowired
	@Qualifier("Gstr1AProcsSubmitScreenHSNReqRespHandler")
	Gstr1AProcsSubmitScreenHSNReqRespHandler hsnReqRespHandler;

	@Autowired
	@Qualifier("Gstr1AProcsSubScreenDocNilReqRespHandler")
	Gstr1AProcsSubScreenDocNilReqRespHandler docNilReqRespHandler;

	@Autowired
	@Qualifier("Gstr1ASummaryScreenEcomSupReqRespHandler")
	Gstr1ASummaryScreenEcomSupReqRespHandler gstr1ecomSupReqRespHandler;

	@Autowired
	@Qualifier("Gstr1ASimpleDocGstnSummarySearchService")
	Gstr1ASimpleDocGstnSummarySearchService tableSearchService;

	@Autowired
	@Qualifier("Gstr1ASummaryScreenAdvReqRespHandler")
	Gstr1ASummaryScreenAdvReqRespHandler gstr1AdvReqRespHandler;

	@PostMapping(value = "/ui/gstr1AProcessVsSubmitScreen", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyGstr1Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Annexure1SummaryReqDto annexure1SummaryRequest = gson
					.fromJson(reqJson.toString(), Annexure1SummaryReqDto.class);

			Pair<List<Gstr1SummaryScreenRespDto>, List<Gstr1SummaryScreenRespDto>> response = tranReqResp
					.handleGstr1ReqAndResp(annexure1SummaryRequest);

			SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult = (SearchResult<Gstr1CompleteSummaryDto>) tableSearchService
					.find(annexure1SummaryRequest, null,
							Gstr1CompleteSummaryDto.class);

			List<? extends Gstr1CompleteSummaryDto> gstnResult = gstnSumryResult
					.getResult();

			JsonElement outwardSummaryRespBody = gson
					.toJsonTree(response.getValue0());

			JsonElement hsnSummaryRespBody = gson.toJsonTree(hsnReqRespHandler
					.handleGstr1HsnReqAndResp(annexure1SummaryRequest));

			/*
			 * JsonElement advSummaryRespBody = gson
			 * .toJsonTree(response.getValue1());
			 */

			JsonElement advSummaryRespBody = gson
					.toJsonTree(gstr1AdvReqRespHandler.handleGstr1AdvReqAndResp(
							annexure1SummaryRequest, gstnResult));
			JsonElement nilSummaryRespBody = gson
					.toJsonTree(docNilReqRespHandler
							.handleGstr1NilReqAndResp(annexure1SummaryRequest));

			JsonElement docSummaryRespBody = gson
					.toJsonTree(docNilReqRespHandler
							.handleGstr1DocReqAndResp(annexure1SummaryRequest));

			JsonElement ecomSupRespBody = gson.toJsonTree(
					gstr1ecomSupReqRespHandler.handleGstr1ReqAndResp(
							annexure1SummaryRequest, gstnResult));

			Map<String, JsonElement> combinedMap = new HashMap<>();
			combinedMap.put("outward", outwardSummaryRespBody);
			combinedMap.put("hsnSummary", hsnSummaryRespBody);
			combinedMap.put("Advances", advSummaryRespBody);
			combinedMap.put("nil", nilSummaryRespBody);
			combinedMap.put("docIssue", docSummaryRespBody);
			combinedMap.put("ecomSup", ecomSupRespBody);

			JsonElement summaryRespbody = gson.toJsonTree(combinedMap);
			JsonElement respBody = gson.toJsonTree(summaryRespbody);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
