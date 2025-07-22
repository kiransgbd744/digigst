package com.ey.advisory.controller;

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

import com.ey.advisory.app.gstr3b.Gstr2bvs3bProcProcessServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@RestController
public class Gstr2bvs3bProcessedSummaryProcController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2bvs3bProcessedSummaryProcController.class);

	@Autowired
	@Qualifier("Gstr2bvs3bProcProcessServiceImpl")
	private Gstr2bvs3bProcProcessServiceImpl gstr2bvs3bProcProcessServiceImpl;

	@PostMapping(value = "/ui/gstr2bvs3bproceCall", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> proceCallComputeReversal(
			@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2bvs3bProcessedSummaryProcController proceCall Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Gstr1VsGstr3bProcessSummaryReqDto reqDto = gson.fromJson(reqObj,
					Gstr1VsGstr3bProcessSummaryReqDto.class);
			String msg = gstr2bvs3bProcProcessServiceImpl
					.fetchgstr1vs3bProc(reqDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(msg));
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2bvs3bProcessedSummaryProcController ProcCall End");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

}
