
package com.ey.advisory.controller.gstr6;

import java.util.List;

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

import com.ey.advisory.app.docs.dto.gstr1.Gstr1SummarySaveStatusRespDto;
import com.ey.advisory.app.docs.service.gstr6.Gstr6SummarySaveStatusServiceImpl;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1SummarySaveStatusReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RestController
public class Gstr6SummarySaveStatusController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6SummarySaveStatusController.class);

	@Autowired
	@Qualifier("Gstr6SummarySaveStatusServiceImpl")
	private Gstr6SummarySaveStatusServiceImpl gstr6SummarySaveStatusService;

	@PostMapping(value = "/ui/gstr6SummarySaveStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1SummarySaveStatus(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resps = new JsonObject();
		// Execute the service method and get the result.
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gstr1SummarySaveStatusReqDto criteria = gson.fromJson(json,
					Gstr1SummarySaveStatusReqDto.class);
			List<Gstr1SummarySaveStatusRespDto> summaryList = gstr6SummarySaveStatusService
					.findByCriteria(criteria);
			JsonElement respBody = gson.toJsonTree(summaryList);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			
		} catch (Exception ex) {
			String msg = "Error while parsing the input Json payload";
			LOGGER.error(msg, ex);
			return new ResponseEntity<>(resps.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
	}

}
