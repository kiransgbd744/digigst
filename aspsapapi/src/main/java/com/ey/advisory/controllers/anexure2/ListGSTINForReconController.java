package com.ey.advisory.controllers.anexure2;

import java.util.List;

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
import com.ey.advisory.app.docs.dto.anx2.Anx2ListGSTINForReconReq;
import com.ey.advisory.app.services.daos.listgstinforrecon.Anx2ListGstinsForReconService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@RestController
public class ListGSTINForReconController {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository entityRepository;

	@Autowired
	@Qualifier("Anx2ListGstinsForReconServiceImpl")
	private Anx2ListGstinsForReconService objAnx2ListGstinsForReconService;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ListGSTINForReconController.class);

	@RequestMapping(value = "/ui/listGSTINForRecon", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getInitiateRecon(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		// Execute the service method and get the result.
		try {
			Anx2ListGSTINForReconReq criteria = gson.fromJson(json,
					Anx2ListGSTINForReconReq.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getCgstins();
				if (gstins == null || gstins.size() == 0) {
					gstins = entityRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setCgstins(gstins);
				}
			}
			List<Object> recResponse = objAnx2ListGstinsForReconService
						.getAnx2ListGstinsForRecon(criteria);
			
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
