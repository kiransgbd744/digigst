package com.ey.advisory.controller.recon3way;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.recon3way.Recon3WaySummaryStatusService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2ReconSummaryStatusDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@RestController
public class Recon3WayStatusController {

	@Autowired
	@Qualifier("Recon3WaySummaryStatusServiceImpl")
	private Recon3WaySummaryStatusService service;

	@PostMapping(value = "/ui/getDataForRecon3WaySummary", produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getDataForRecon3WaySummary(
			@RequestBody String reqJson) {

		JsonObject requestObject = (new JsonParser()).parse(reqJson)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		String criteria = null;
		String fromReturnPeriod = null;
		String toReturnPeriod = null;
		
		try
		{
			fromReturnPeriod = json.get("fromReturnPeriod").getAsString();
			toReturnPeriod = json.get("toReturnPeriod").getAsString();
			criteria = json.get("criteria").getAsString();
			Long entityId = json.get("entityId").getAsLong();
		
			if (fromReturnPeriod != null && toReturnPeriod != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"3 Way Recon Screen Search"
									+ "Parameters fromReturnPeriod %s To"
									+ " toReturnPeriod %s for criteria %s"
									+ ": ",
									fromReturnPeriod, toReturnPeriod, criteria);
					LOGGER.debug(msg);
				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(" 3 Way Recon Screen Search on"
							+ "Parameters fromReturnPeriod %s To toReturnPeriod %s "
							+ ": ", fromReturnPeriod, toReturnPeriod);
					LOGGER.debug(msg);
				}
			}

			List<Gstr2ReconSummaryStatusDto> respObj = service
					.getRecon3WayDetailSummaryStatus(entityId, fromReturnPeriod,
							toReturnPeriod, criteria);

			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respObj);
			detResp.add("det", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Recon3WayStatusController", e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error in Recon3WayStatusController";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}