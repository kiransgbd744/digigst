package com.ey.advisory.controller.gstr7;

import java.util.ArrayList;
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

import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.anx2.Gstr7SummaryAtGstnImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@RestController
public class Gstr7EntityUpdateGstnController {

	@Autowired
	@Qualifier("Gstr7SummaryAtGstnImpl")
	private Gstr7SummaryAtGstnImpl gstr7SummaryAtGstn;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@RequestMapping(value = "/ui/getGstr7EntityReviewSummary", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonArray respBody = new JsonArray();
		String msg = null;

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
				String authStatus = null;

				Map<String, String> authTokenStatusMap = authTokenService
						.getAuthTokenStatusForGstins(gstinList);

				List<String> inActiveGstins = new ArrayList<>();

				for (String sGstin : gstinList) {
					JsonObject jsonMsg = new JsonObject();
					authStatus = authTokenStatusMap.get(sGstin);

					if (!"A".equalsIgnoreCase(authStatus)) {
						msg = String.format(" Auth Token is InActive, "
								+ "Please  Activate %s ", sGstin);

						inActiveGstins.add(sGstin);

						jsonMsg.addProperty("gstin", sGstin);
						jsonMsg.addProperty("msg", msg);
						respBody.add(jsonMsg);
					}
				}

				gstinList.removeAll(inActiveGstins);

				if (gstinList != null && !gstinList.isEmpty()) {
					String groupCode = TenantContext.getTenantId();
					for (String gstn : gstinList) {
						JsonObject jsonMsg = new JsonObject();
						dto.setReturnPeriod(reqDto.getRetunPeriod());
						dto.setGstin(gstn);
						gstr7SummaryAtGstn.getGstr7Summary(dto, groupCode);

						String successMsg = String.format("Success,  %s ",
								gstin);

						jsonMsg.addProperty("gstin", gstin);
						jsonMsg.addProperty("msg", successMsg);
						respBody.add(jsonMsg);

					}
				}

			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respBody));

			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

		} catch (JsonParseException ex) {
			msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		catch (Exception ex) {
			msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
