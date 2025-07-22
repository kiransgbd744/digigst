package com.ey.advisory.controller;

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
import com.ey.advisory.app.data.entities.client.DataStatusEntity;
import com.ey.advisory.app.services.search.datastatussearch.
											DataStatusnewService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.DataStatusSearchReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@RestController
public class DataStatusController {

	/*@Autowired
	@Qualifier("DataStatusSearchService")
	DataStatusSearchService dataStatusSearchService;*/
	
	@Autowired
	@Qualifier("DataStatusnewService")
	DataStatusnewService dataStatusnewService;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository dataStatusRepository;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataStatusController.class);

	@RequestMapping(value = "/ui/getGstr1DataStatus", method = RequestMethod.POST, 
			produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getGstr1DataStatus(
			@RequestBody String jsonString) {

		
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
					
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		// Execute the service method and get the result.
		try {
			
			DataStatusSearchReqDto criteria = gson.fromJson(json,
					DataStatusSearchReqDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getSgstins();
				if (gstins == null || gstins.size() == 0) {
					gstins = dataStatusRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setSgstins(gstins);
				}
			}

			SearchResult<DataStatusEntity> searchResult = dataStatusnewService
								.find(criteria, null, DataStatusEntity.class);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		}  catch (JsonParseException ex) {
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
