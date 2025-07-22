package com.ey.advisory.controller.gstr7;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.daos.client.simplified.UpdatedMofifiedDateFetchDaoImpl;
import com.ey.advisory.app.data.services.anx1.Gstr7ReviewSummaryFetchService;
import com.ey.advisory.app.data.views.client.Gstr7DiffSummaryRespDto;
import com.ey.advisory.app.data.views.client.Gstr7ReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.anx2.Gstr7SummaryAtGstnImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sasidhar
 *
 */
@Slf4j
@RestController
public class Gstr7ReviewSummaryController {

	@Autowired
	@Qualifier("Gstr7SummaryAtGstnImpl")
	private Gstr7SummaryAtGstnImpl gstr7SummaryAtGstn;

	@Autowired
	@Qualifier("UpdatedMofifiedDateFetchDaoImpl")
	UpdatedMofifiedDateFetchDaoImpl updatedMofifiedDateFetchDaoImpl;

	@Autowired
	@Qualifier("Gstr7ReviewSummaryFetchService")
	Gstr7ReviewSummaryFetchService gstr7ReviewSummaryFetchService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@RequestMapping(value = "/ui/getGstr7ReviewSummary", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		Anx2GetInvoicesReqDto dto = new Anx2GetInvoicesReqDto();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("The selected criteria for processed records is:->"
				+ requestObject.get("req"));
		// Execute the service method and get the result.
		try {

			Gstr2AProcessedRecordsReqDto reqDto = gson.fromJson(json,
					Gstr2AProcessedRecordsReqDto.class);

			if ("UPDATEGSTIN".equalsIgnoreCase(reqDto.getAction())) {
				String gstin = null;
				List<String> gstinList = null;
				Map<String, List<String>> dataSecAttrs = reqDto
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

					String msg = " Auth Token is InActive, Please Active ";
					// LOGGER.error(msg, ex);

					JsonObject resp = new JsonObject();
					resp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", msg)));

					return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
				} else {
					String groupCode = TenantContext.getTenantId();

					for (String gstn : gstinList) {

						dto.setReturnPeriod(reqDto.getRetunPeriod());
						dto.setGstin(gstn);
						ResponseEntity<String> gstr6Summary = gstr7SummaryAtGstn
								.getGstr7Summary(dto, groupCode);

					}

				}

			}

			List<Gstr7ReviewSummaryRespDto> response = gstr7ReviewSummaryFetchService
					.getReviewSummary(reqDto);

			String lastUpdatedDate = updatedMofifiedDateFetchDaoImpl
					.loadgst7BasicSummarySection(dto);

			JsonElement updatedDateRespbody = gson.toJsonTree(lastUpdatedDate);
			JsonElement responseListBody = gson.toJsonTree(response);
			Map<String, JsonElement> combinedMap = new HashMap<>();
			// lastUpdatedDate
			combinedMap.put("lastUpdatedDate", updatedDateRespbody);
			combinedMap.put("response", responseListBody);

			JsonElement summaryRespbody = gson.toJsonTree(combinedMap);
			JsonElement respBody = gson.toJsonTree(summaryRespbody);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			LOGGER.debug(
					"Response data for given criteria for processed records is :->"
							+ resp.toString());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr1 processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/ui/getGstr7ReviewDiff", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr7ReviewDiff(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("The selected criteria for processed records is:->"
				+ requestObject.get("req"));
		// Execute the service method and get the result.
		try {

			Gstr2AProcessedRecordsReqDto reqDto = gson.fromJson(json,
					Gstr2AProcessedRecordsReqDto.class);

			List<Gstr7DiffSummaryRespDto> response = gstr7ReviewSummaryFetchService
					.getDiffenceSummary(reqDto);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(response));

			LOGGER.debug(
					"Response data for given criteria for processed records is :->"
							+ resp.toString());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr1 processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
