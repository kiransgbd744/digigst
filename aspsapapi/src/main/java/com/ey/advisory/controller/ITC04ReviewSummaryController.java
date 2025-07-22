package com.ey.advisory.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.daos.client.simplified.UpdatedMofifiedDateFetchDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.ITC04PopupRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.ITC04SummaryScreenReqRespHandler;
import com.ey.advisory.app.services.search.simplified.docsummary.Itc04PopupSummaryService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Slf4j
@RestController
public class ITC04ReviewSummaryController {

	
	@Autowired
	@Qualifier("ITC04SummaryScreenReqRespHandler")
	ITC04SummaryScreenReqRespHandler reqResp;
	
	@Autowired
	@Qualifier("UpdatedMofifiedDateFetchDaoImpl")
	UpdatedMofifiedDateFetchDaoImpl dateFetchDao;
	

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;
	
	@Autowired
	@Qualifier("Itc04PopupSummaryService")
	Itc04PopupSummaryService itc04Service;

	
	
	@SuppressWarnings("unused")
	@PostMapping(value = "/ui/itc04SummaryScreen", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyItc04Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			ITC04RequestDto itc04SummaryRequest = gson
					.fromJson(reqJson.toString(), ITC04RequestDto.class);

			if ("UPDATEGSTIN"
					.equalsIgnoreCase(itc04SummaryRequest.getAction())) {

				String gstin = null;
				List<String> gstinList = null;
				Map<String, List<String>> dataSecAttrs = itc04SummaryRequest
						.getDataSecAttrs();
				if (!dataSecAttrs.isEmpty()) {
					for (String key : dataSecAttrs.keySet()) {
						if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
							gstin = key;
							if (!dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()
									&& dataSecAttrs
											.get(OnboardingConstant.GSTIN)
											.size() > 0) {
								gstinList = dataSecAttrs
										.get(OnboardingConstant.GSTIN);
							}
						}
					}
				}

				String authStatus = authTokenService
						.getAuthTokenStatusForGstin(gstinList.get(0));

				// String authStatus = "A";

				if (!"A".equalsIgnoreCase(authStatus)) {

					String msg = "Auth Token is Inactive, Please Activate";
					// LOGGER.error(msg, ex);

					JsonObject resp = new JsonObject();
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));

					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				}
			}

		/*	LOGGER.debug("GSTN Data Summary Execution BEGIN ");
			SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult = (SearchResult<Gstr1CompleteSummaryDto>) tableSearchService
					.find(annexure1SummaryRequest, null,
							Gstr1CompleteSummaryDto.class);
			LOGGER.debug("OUTWARD GSTN Data Summary Execution END ");*/

		//	String lastUpdatedDate = dateFetchDao.getCreatedDate(itc04SummaryRequest);
			String lastUpdatedDate = dateFetchDao.loadBasicITC04SummarySection(itc04SummaryRequest);
			
	//		String lastUpdatedDate = null;
			/* String lastUpdatedDate =
			 loadBasicSummarySection.getLastUpdatedDate();*/

		/*	List<? extends Gstr1CompleteSummaryDto> gstnResult = gstnSumryResult
					.getResult();
*/
			JsonElement outwardSummaryRespBody = gson
					.toJsonTree(reqResp.handleItc04ReqAndResp(itc04SummaryRequest));

			
			JsonElement updatedDateRespbody = gson.toJsonTree(lastUpdatedDate);
			Map<String, JsonElement> combinedMap = new HashMap<>();
			// lastUpdatedDate
			combinedMap.put("lastUpdatedDate", updatedDateRespbody);
			combinedMap.put("itc04Records", outwardSummaryRespBody);
			
		//	JsonElement summaryRespbody = gson.toJsonTree(outwardSummaryRespBody);
			JsonElement respBody = gson.toJsonTree(combinedMap);

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
			String msg = "Unexpected error while fecthing ITC04  "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * ITC04 Popup Screen
	 */
	
	@PostMapping(value = "/ui/itc04PopupScreen", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyItc04PopupScreen(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			ITC04RequestDto itc04SummaryRequest = gson
					.fromJson(reqJson.toString(), ITC04RequestDto.class);

			
			List<ITC04PopupRespDto> itc04PopupRecords = itc04Service.itc04PopupRecords(itc04SummaryRequest);
			JsonElement respBody = gson.toJsonTree(itc04PopupRecords);

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
			String msg = "Unexpected error while fecthing ITC04  "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
