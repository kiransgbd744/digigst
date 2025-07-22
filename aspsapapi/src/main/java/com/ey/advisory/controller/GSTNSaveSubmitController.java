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
import com.ey.advisory.app.docs.dto.GstnSaveSubmitReqDto;
import com.ey.advisory.app.docs.dto.GstnSaveSubmitRespDto;
import com.ey.advisory.app.services.search.gstnsavesubmit.GstnSaveSubmitService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * This class is responsible for showing GSTN Saved and Submit Records on UI
 * screen
 * @author Mohana.Dasari
 *
 */
@RestController
public class GSTNSaveSubmitController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GSTNSaveSubmitController.class);
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;
	
	@Autowired
	@Qualifier("GstnSaveSubmitService")
	private GstnSaveSubmitService gstnSaveSubmitService;
	

	/**
	 * This method is responsible for retrieving all documents details 
	 * which are either saved or submitted to GSTN
	 * @param jsonString
	 * @return
	 */
	@RequestMapping(value = "/ui/getGstr1SaveSubmitGstins", 
			method = RequestMethod.POST, 
			produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstnSaveAndSubmitDocuments(
			@RequestBody String jsonString) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			GstnSaveSubmitReqDto criteria = gson.fromJson(json,
					GstnSaveSubmitReqDto.class);
			if (null != criteria.getEntityId()
					&& !criteria.getEntityId().isEmpty()) {
				List<String> gstins = criteria.getSgstins();
				if (gstins == null || gstins.size() == 0) {
					gstins = gstinInfoRepository
							.findByEntityId(criteria.getEntityId());
					criteria.setSgstins(gstins);
				}
			}
			
			SearchResult<GstnSaveSubmitRespDto> searchResult = 
					gstnSaveSubmitService
					.find(criteria, null, GstnSaveSubmitRespDto.class);

			//Response
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(searchResult.getResult()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}catch(JsonParseException ex){
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(Exception ex){
			String msg = "Unexpected error while retriving "
					+ "GSTN Save and Submit Documents ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	

}
