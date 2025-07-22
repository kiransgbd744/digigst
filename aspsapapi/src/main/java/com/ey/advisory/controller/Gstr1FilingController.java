package com.ey.advisory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.FilingReqDto;
import com.ey.advisory.app.services.filing.FilingHandler;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Siva.Nandam
 *
 */
@RestController
public class Gstr1FilingController {

	@Autowired
	@Qualifier("FilingHandler")
   private FilingHandler filingHandler;
	
	
    @RequestMapping(value = "/ui/gstr1filling", method = RequestMethod.POST, produces = {
                 MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> setApprovalStatus(
                 @RequestBody String jsonString) {

          JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
          String reqJson = obj.get("req").getAsJsonObject().toString();
          Gson gson = GsonUtil.newSAPGsonInstance();
          FilingReqDto dto = gson.fromJson(reqJson, FilingReqDto.class);
          
          JsonElement filingObj = filingHandler
					.handleFiling(dto);
			
           JsonObject resp = new JsonObject();
          JsonElement respBody = gson.toJsonTree(filingObj);
          resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
          resp.add("resp", respBody);
          return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);
    }

}

