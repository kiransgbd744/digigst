package com.ey.advisory.controller.gstr2;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.gstr2.Gstr2SaveToGstnDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

@RestController
public class Gstr2SaveToGstinController {
	

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2SaveToGstinController.class);
	
	@RequestMapping(value = "/ui/gstr2SaveToGstin", method = 
			RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> getGstr2SaveToGstin(
			@RequestBody String jsonString) {
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			
			List<Gstr2SaveToGstnDto> respList = 
					new ArrayList<Gstr2SaveToGstnDto>();
			Gstr2SaveToGstnDto gstnDto = new Gstr2SaveToGstnDto();
			
			gstnDto.setGstin("33GSPTN0481G1ZA");
			gstnDto.setReturnPeriod("Jan 2018");
			gstnDto.setAspProcessed(60);
			gstnDto.setAspError(20);
			gstnDto.setAspInfo(10);
			gstnDto.setAspRectified(5);
			gstnDto.setGstinProcessed(50);
			gstnDto.setGstinError(25);
			gstnDto.setReturnStatus("Saved");
			gstnDto.setReviewStatus("Success");
			respList.add(gstnDto);
			
			Gstr2SaveToGstnDto gstnDto2 = new Gstr2SaveToGstnDto();
			
			gstnDto2.setGstin("33GSPTN0481G1ZA");
			gstnDto2.setReturnPeriod("Dec 2018");
			gstnDto2.setAspProcessed(70);
			gstnDto2.setAspError(30);
			gstnDto2.setAspInfo(20);
			gstnDto2.setAspRectified(5);
			gstnDto2.setGstinProcessed(50);
			gstnDto2.setGstinError(25);
			gstnDto2.setReturnStatus("Saved");
			gstnDto2.setReviewStatus("Success");
			respList.add(gstnDto2);
			
			
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(respList));

			return new ResponseEntity<>(resp.toString(),HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while getting PR Data  for Gstr2";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
