package com.ey.advisory.controllers.anexure2;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.services.daos.initiaterecon.Anx2ReviewSummeryService;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Siva.Nandam
 *
 */
@RestController
public class GetAnx2PRReviewSummaryController {
	@Autowired
	@Qualifier("Anx2ReviewSummeryService")
	private Anx2ReviewSummeryService anx2ReviewSummeryService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository entityRepository;

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(GetAnx2PRReviewSummaryController.class);

	@RequestMapping(value = "/ui/getanx2prReviewSummary", method = RequestMethod.POST,
			produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getanx2reviewSummary(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		// Execute the service method and get the result.
		try {

			Anx2PRSProcessedRequestDto criteria = gson.fromJson(json,
					Anx2PRSProcessedRequestDto.class);

			List<Long> entityIds = criteria.getEntityId();
			Map<String, String> inwardSecurityAttributeMap = DataSecurityAttributeUtil
					.getInwardSecurityAttributeMap();
			Map<String, List<String>> dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(entityIds,
							inwardSecurityAttributeMap);
			if (criteria.getDataSecAttrs() == null
					|| criteria.getDataSecAttrs().isEmpty()) {
				criteria.setDataSecAttrs(dataSecurityAttrMap);
			}
			
			List<Object> recResponse=anx2ReviewSummeryService.find(criteria);
			
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(recResponse);
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
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
