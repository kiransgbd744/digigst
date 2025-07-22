package com.ey.advisory.controllers.anexure1;

import java.util.HashMap;
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

import com.ey.advisory.app.data.entities.client.Anx1DataStatusEntity;
import com.ey.advisory.app.services.search.datastatus.anx1.Anx1DataStatusService;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * 
 * @author Balakrishna.S
 *
 */

@RestController
public class Anx1DataStatusController {

	@Autowired
	@Qualifier("Anx1DataStatusService")
	Anx1DataStatusService anx1DataStatusService;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1DataStatusController.class);

	@RequestMapping(value = "/ui/getAnx1DataStatus", 
			method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr1DataStatus(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		// Execute the service method and get the result.
		try {

			DataStatusSearchReqDto searchParams = gson.fromJson(json,
					DataStatusSearchReqDto.class);
			/**
			 * Start - Set Data Security Attributes
			 */

			LOGGER.debug("DataStatus Adapter Filters Setting to Request BEGIN");
			DataStatusSearchReqDto setDataSecurity = basicCommonSecParam
					.setDataSecuritySearchParams(searchParams);

			LOGGER.debug("DataStatus Adapter Filters Setting to Request END");
			/**
			 * End - Set Data Security Attributes
			 */

			SearchResult<Anx1DataStatusEntity> searchResult = anx1DataStatusService
					.find(setDataSecurity, null, Anx1DataStatusEntity.class);
			Map<String, String> combinedMap = new HashMap<>();
			if(searchResult.getResult() != null){
				LOGGER.debug("Datastatus Controller SearchResult  "
						+ "Data--> "+searchResult.getResult());
			}
			else{
				LOGGER.debug("Datastatus Controller SearchResult  have no Data");
			}
			
			combinedMap.put("withSAPTotal", "N");

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("hdr", gson.toJsonTree(combinedMap));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

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
