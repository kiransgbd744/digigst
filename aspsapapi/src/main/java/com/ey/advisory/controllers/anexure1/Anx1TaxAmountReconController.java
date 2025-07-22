package com.ey.advisory.controllers.anexure1;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.anx1.taxamountrecon.TaxAmountReconDto;
import com.ey.advisory.app.anx1.taxamountrecon.TaxAmountReconRequestDto;
import com.ey.advisory.app.anx1.taxamountrecon.TaxAmountReconRet1Dto;
import com.ey.advisory.app.anx1.taxamountrecon.TaxAmountReconServiceImpl;
import com.ey.advisory.app.dto.compute.Annexure2ComputeDtoList;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;


/**
 * @author Arun KA
 *
 **/

@Slf4j
@RestController
public class Anx1TaxAmountReconController {

	@Autowired
	@Qualifier("TaxAmountReconService")
	TaxAmountReconServiceImpl taxAmountReconService;

	
	@PostMapping(value = "/ui/getTaxAmountReconInfo", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTaxAmountRecon(
			@RequestBody String jsonString) {

		try {
			
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Anx1TaxAmountReconController"
						+ ".getTaxAmountRecon ,Parsing Input request";
				LOGGER.debug(msg);
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			TaxAmountReconRequestDto  reqDto= gson.fromJson(json,
					TaxAmountReconRequestDto.class);

			
			validateInput(reqDto.getTaxPeriod());
			Pair<List<TaxAmountReconDto>,TaxAmountReconRet1Dto> infoList = 
					taxAmountReconService.getAllTaxAmountRecon(reqDto);
			
			JsonObject resp = new JsonObject();
			JsonObject detResp = new JsonObject();
			detResp.add("det", gson.toJsonTree(infoList.getValue0()));
			detResp.add("ret1", gson.toJsonTree(infoList.getValue1()));
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", detResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Anx1TaxAmountReconController"
						+ ".getTaxAmountRecon, before returning response";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			
			return InputValidationUtil.createJsonErrResponse(ex);
			
		}
	}
	
	private void validateInput(String taxPeriod) {

		String msg = null;

		if (StringUtils.isBlank(taxPeriod)) {
			msg = "Tax period cannot be empty.";
		}

		if (msg != null) {
			throw new AppException(msg);
		}

	}

}
