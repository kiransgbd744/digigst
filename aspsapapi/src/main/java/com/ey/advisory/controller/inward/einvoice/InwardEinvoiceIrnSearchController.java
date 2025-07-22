package com.ey.advisory.controller.inward.einvoice;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.inward.einvoice.GetInwardIrnSearchDetailsService;
import com.ey.advisory.app.inward.einvoice.IrnSearchDetailsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class InwardEinvoiceIrnSearchController {
	private static final String INVALID_IRN = "Incorrect IRN";

	@Autowired
	@Qualifier("GetInwardIrnSearchDetailsHandler")
	GetInwardIrnSearchDetailsService getIrnDtlsService;
	
	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@PostMapping(value = "/ui/getInwardIrnSearchData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getInwardIrnDetailedData(
			@RequestBody String jsonString) throws IOException {
		String groupCode = TenantContext.getTenantId();

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		String irnNum = json.get("irn").getAsString();
		String gstin = json.get("gstin").getAsString();

		String msg1 = String
				.format("Inside InwardEinvoiceIrnSearchController.getInwardIrnSearchData() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg1);

		try {
			if (irnNum.length() != 64 || !irnNum.matches("[A-Za-z0-9]+")
					|| irnNum.matches("[A-Za-z]+")
					|| irnNum.matches("[0-9]+")) {
				throw new AppException(INVALID_IRN);
			}
			IrnSearchDetailsDto apiResp = null;
			/*Map<String, String> retMap = gSTNAuthTokenService
					.getAuthTokenStatusForGroup();

			List<String> activeGstins = retMap.entrySet().stream()
					.filter(entry -> "A".equals(entry.getValue()))
					.map(Map.Entry::getKey).collect(Collectors.toList());*/
			/*List<String> activeGstin = gstinDetailRepo
					.getActiveGstin(gstin);*/
			String authStatus = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			
			if (authStatus.equals("I")) {
				throw new AppException(
						"Auth Token for selected GSTIN is Inactive. Kindly activate");
			}else{
			
				apiResp = getIrnDtlsService.getIrnDtls(irnNum, groupCode,
						gstin);
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Irn Search "
							+ " Response %s from irnNum  %s :", apiResp, irnNum);
					LOGGER.debug(msg);
				}
			}

			JsonElement respBody = gson.toJsonTree(apiResp);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			jsonObj.add("resp", respBody);

			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed", e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Error Occured in Controller";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}
}
