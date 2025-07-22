package com.ey.advisory.controller;

import java.util.ArrayList;
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

import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.anx2.Itc04SummaryAtGstnImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Vishal.Verma
 *
 */
@Slf4j
@RestController
public class ITC04EntityUpdateGstnController {

	
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;
	
	@Autowired
	@Qualifier("Itc04SummaryAtGstnImpl")
	Itc04SummaryAtGstnImpl itc04SummaryAtGstnImpl;

	@PostMapping(value = "/ui/itc04EntityUpdateGstn", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> eyItc04Summary(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		
		JsonArray respBody = new JsonArray();
	
		String msg = null;
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			ITC04RequestDto itc04SummaryRequest = gson
					.fromJson(reqJson.toString(), ITC04RequestDto.class);

			if ("UPDATEGSTIN"
					.equalsIgnoreCase(itc04SummaryRequest.getAction())) {

				
				List<String> gstinList = null;
				Map<String, List<String>> dataSecAttrs = itc04SummaryRequest
						.getDataSecAttrs();
				if (!dataSecAttrs.isEmpty()) {
					for (String key : dataSecAttrs.keySet()) {
						if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
							String gstin = key;
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

					authStatus = authTokenStatusMap.get(sGstin);

					if (!"A".equalsIgnoreCase(authStatus)) {
						msg = String.format(" Auth Token is InActive, "
								+ "Please  Activate %s ", sGstin);
						JsonObject json = new JsonObject();
						inActiveGstins.add(sGstin);

						json.addProperty("gstin", sGstin);
						json.addProperty("msg", msg);
						respBody.add(json);
					}
				}

				gstinList.removeAll(inActiveGstins);

				if (gstinList != null && !gstinList.isEmpty()) {
					
					gstinList.forEach(gstin -> {
						Anx2GetInvoicesReqDto dto = new Anx2GetInvoicesReqDto();
						dto.setGstin(gstin);
						dto.setReturnPeriod(itc04SummaryRequest.getTaxPeriod());
						
						itc04SummaryAtGstnImpl.getGstr6Summary(dto, groupCode);

						itc04SummaryAtGstnImpl.gstr7Summary(dto, groupCode);

						String successMsg = String.format("Success,  %s ",
								gstin);
						JsonObject json = new JsonObject();
						json.addProperty("gstin", gstin);
						json.addProperty("msg", successMsg);
						respBody.add(json);

					});
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
