package com.ey.advisory.statecode.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.statecode.dto.Gstr6DistributionStateCodeDetailsDto;
import com.ey.advisory.app.data.statecode.service.Gstr6DistStateCodeDetailsService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Dibyakanta.S
 *
 */
@Slf4j
@RestController
public class Gstr6DistributionStateCodeDetailsController {

	@Autowired
	@Qualifier("Gstr6DistStateCodeDetailsServiceImpl")
	private Gstr6DistStateCodeDetailsService gstr6DistStateCodeDetailsService;

	@PostMapping(value = "/ui/getStates", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getStates() {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin StateCodeDetailsServiceImpl"
						+ ".getAllStates ";
				LOGGER.debug(msg);
			}
			List<Gstr6DistributionStateCodeDetailsDto> statesList = gstr6DistStateCodeDetailsService
					.findStates();
			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr6DistributionSummaryStateCodeDetailsServiceImpl"
						+ ".getAllStates Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonElement respBody = gson.toJsonTree(statesList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("states", respBody);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr6DistributionSummaryStateCodeDetailsServiceImpl"
						+ ".getAllEntities ,before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception ex) {
			String msg = " Unexpected error occured ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
