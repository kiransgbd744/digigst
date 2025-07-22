/**
 * 
 */
package com.ey.advisory.sap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.erp.Anx1ErrorDocsRevIntegrationReqDto;
import com.ey.advisory.app.services.jobs.erp.Anx1ErrorDocsRevIntegrationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Hemasundar.J
 *
 */
@RestController
public class Anx1ErrorDocsRevIntegrationTestController {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ErrorDocsRevIntegrationTestController.class);
	
	@Autowired
	private Anx1ErrorDocsRevIntegrationHandler errorHandler;

	@PostMapping(value = "/anx1ErrorDocsToErp", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> errorDocsToErp(
			@RequestBody String jsonString) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ErrorDocsToErp method called");
		}
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.gsonInstanceWithExpose();
			Anx1ErrorDocsRevIntegrationReqDto dto = gson.fromJson(reqJson,
					Anx1ErrorDocsRevIntegrationReqDto.class);
			// Overriding groupcode and destination for testing
			String groupcode = TestController.staticTenantId();
			dto.setGroupcode(groupcode);
			if (groupcode != null && dto.getGstin() != null) {
				String scenarioName = APIConstants.OUTWARD_ASP_ERP_PUSH;
				String destinationName = APIConstants.ASP_ERP_OUTWARD_ERROR;
				dto.setDestinationName(destinationName);
				dto.setScenarioName(scenarioName);
				Integer respcode1 = errorHandler.erpErrorDocsToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Outward response code is {}", respcode1);
				}
				scenarioName = APIConstants.INWARD_ASP_ERP_PUSH;
				destinationName = APIConstants.ASP_ERP_INWARD_ERROR;
				dto.setDestinationName(destinationName);
				dto.setScenarioName(scenarioName);
				Integer respcode2 = errorHandler.erpErrorDocsToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Inward response code is {}", respcode2);
				}
				scenarioName = APIConstants.OUTWARD_GSTN_ERP_PUSH;
				destinationName = APIConstants.GSTN_ERP_INWARD_ERROR;
				dto.setDestinationName(destinationName);
				dto.setScenarioName(scenarioName);
				Integer respcode3 = errorHandler.erpErrorDocsToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Outward response code is {}", respcode1);
				}
				scenarioName = APIConstants.INWARD_GSTN_ERP_PUSH;
				destinationName = APIConstants.GSTN_ERP_INWARD_ERROR;
				dto.setDestinationName(destinationName);
				dto.setScenarioName(scenarioName);
				Integer respcode4 = errorHandler.erpErrorDocsToErp(dto);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Inward response code is {}", respcode2);
				}
			}
			JsonObject resp = respObject(APIConstants.SUCCESS);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", "Unexpected Eror")));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public JsonObject respObject(Object msg) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		JsonElement respBody = gson.toJsonTree(msg);
		resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		resp.add("resp", respBody);
		return resp;
	}

}
