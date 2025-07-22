package com.ey.advisory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.anx1.SavestatusReqDto;
import com.ey.advisory.app.services.search.simplified.docsummary.DifferenceStatusCommonAnxService;
import com.ey.advisory.app.services.search.simplified.docsummary.DifferenceStatusCommonRetService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Sasidhar Reddy
 *
 */

@RestController
public class DifferenceStatusCommonController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DifferenceStatusCommonController.class);

	@Autowired
	@Qualifier("DifferenceStatusCommonAnxService")
	private DifferenceStatusCommonAnxService differenceStatusCommonAnxService;

	@Autowired
	@Qualifier("DifferenceStatusCommonRetService")
	private DifferenceStatusCommonRetService differenceStatusCommonRetService;

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/ui/getDiffStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1SummarySaveStatus(
			@RequestBody String jsonString) throws JsonParseException {

		// Execute the service method and get the result.
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			SavestatusReqDto criteria = gson.fromJson(reqJson.toString(),
					SavestatusReqDto.class);
			JsonElement respBody = null;

			if (criteria.getReturnType().equals("anx1")) {
				respBody = gson.toJsonTree(differenceStatusCommonAnxService
						.getDifferenceForAnx1(criteria, gson).values());
			} else if (criteria.getReturnType().equals("anx1a")) {
				respBody = gson.toJsonTree(differenceStatusCommonAnxService
						.getDifferenceForAnx1a(criteria).values());
			} else if (criteria.getReturnType().equals("ret1")) {
				respBody = gson.toJsonTree(differenceStatusCommonRetService
						.getDifferenceForRet1(criteria, gson).values());
			} else if (criteria.getReturnType().equals("ret1a")) {
				respBody = gson.toJsonTree(differenceStatusCommonRetService
						.getDifferenceForRet1a(criteria, gson).values());
			}

			JsonObject resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing  " + "Summary Data "
					+ ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
