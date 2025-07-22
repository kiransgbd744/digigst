package com.ey.advisory.controllers.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.statelist.StateListDto;
import com.ey.advisory.app.data.statelist.StateListService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Vishal.Verma
 *
 */

@Slf4j
@RestController
public class StateListController {

	@Autowired
	@Qualifier("StateListServiceImpl")
	private StateListService stateService;

	@PostMapping(value = "/ui/getAllState", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllState(@RequestBody String jsonString) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin StateListServiceImpl" + ".getAllState ";
				LOGGER.debug(msg);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			List<StateListDto> stateList = stateService.findStates();
			
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "StateListServiceImpl"
						+ ".getAllState Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject gstinResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(stateList);
			gstinResp.add("states", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End StateListServiceImpl"
						+ ".getAllEntities ,before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (Exception ex) {
			String msg = " Unexpected error occured ";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
