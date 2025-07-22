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
import com.ey.advisory.app.docs.dto.CheckStatusReqDto;
import com.ey.advisory.app.services.search.docsummarysearch.CheckStatusServiceImpl;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

@RestController
public class CheckStatusController {
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gstinInfoRepository;
	
	/*@Autowired
	@Qualifier("gstinInfoRepository")
	Gstr1BatchRepository gstinBatchRepository;*/
	
	@Autowired
	@Qualifier("CheckStatusServiceImpl")
	CheckStatusServiceImpl checkStatusServiceImpl;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CheckStatusController.class);

	@RequestMapping(value = "/ui/getCheckStatus", method = RequestMethod.POST, 
			produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getCheckStatus(
			@RequestBody String jsonString) {

		
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
					
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		// Execute the service method and get the result.
		try {
			
			CheckStatusReqDto criteria = gson.fromJson(json,
					CheckStatusReqDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getSgstins();
				if (gstins == null || gstins.size() == 0) {
					gstins = gstinInfoRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setSgstins(gstins);
				}
			}
			SearchResult<Gstr1SaveBatchEntity> searchResult = checkStatusServiceImpl
								.find(criteria, null, Gstr1SaveBatchEntity.class);

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
			String msg = "Unexpected error while checking Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 

	}

}
