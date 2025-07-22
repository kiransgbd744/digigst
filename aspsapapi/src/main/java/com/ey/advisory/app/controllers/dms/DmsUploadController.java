package com.ey.advisory.app.controllers.dms;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.dms.DmsFetchRulesApiServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.RuleAnsDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 *
 * 
 * @author ashutosh.kar
 *
 */
@RestController
public class DmsUploadController {
	
	private static final Logger LOGGER = LoggerFactory.
			getLogger(DmsUploadController.class);
	
	@Autowired
	@Qualifier("DmsFetchRulesApiServiceImpl")
	private DmsFetchRulesApiServiceImpl dmsFetchRulesApiServiceImpl;
	
	@GetMapping(value = "ui/getViewRulesInUpload.do", produces = {
	        MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getViewRules() {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			List<RuleAnsDto> rules = dmsFetchRulesApiServiceImpl.getViewRules();
			resp.add("rules", gson.toJsonTree(rules));
			resp.add("resp",
					gson.toJsonTree(new APIRespDto(APIRespDto.getSuccessStatus(), "Rules Fetched successfully")));
		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
