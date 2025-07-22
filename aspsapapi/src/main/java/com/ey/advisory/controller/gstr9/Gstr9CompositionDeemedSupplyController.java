package com.ey.advisory.controller.gstr9;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.gstr9.Gstr9CompositionDeemedSupplyService;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9CompositionDeemedSupplyDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9CompositionDeemedSupplySaveUserInputDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@RestController
@Slf4j
public class Gstr9CompositionDeemedSupplyController {

	@Autowired
	@Qualifier("Gstr9CompositionDeemedSupplyServiceImpl")
	private Gstr9CompositionDeemedSupplyService service;

	@PostMapping(value = "/ui/getCompositionDeemedSupply", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr9TaxPaidData(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE Gstr9CompositionDeemedSupplyController");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String gstin = requestObject.get("gstin").getAsString();
			String fyOld = requestObject.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			String taxPeriod = GenUtil
					.getFinancialPeriodFromFY(formattedFy);

			if (Strings.isNullOrEmpty(gstin)
					|| Strings.isNullOrEmpty(taxPeriod)) {
				String msg = "Gstin and taxPeriod is mandatory Data";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			List<Gstr9CompositionDeemedSupplyDto> respData = service
					.getGstr9CompositionDeemedSupplyDetails(gstin, taxPeriod, formattedFy);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respData);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr9 "
					+ "DemandsAndRefunds Data";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		}

	}

	@PostMapping(value = "/ui/saveGstr9CompDeemedSuppUserInputData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr9CompositionDeemedSupplyUserInputData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"INSIDE saveGstr9CompositionDeemedSupplyUserInputData");
			}
			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();

			Gstr9CompositionDeemedSupplySaveUserInputDto userInput = gson
					.fromJson(reqJson,
							Gstr9CompositionDeemedSupplySaveUserInputDto.class);

			String gstin = userInput.getGstin();
			String fyOld = userInput.getFy();
			String formattedFy = GenUtil.getFormattedFy(fyOld);			
			String status = userInput.getStatus();

			if (Strings.isNullOrEmpty(gstin) || Strings.isNullOrEmpty(formattedFy)) {
				String msg = "Gstin and Financial year is mandatory to save "
						+ "Gstn9user input Data";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			String resp = service.saveGstr9CompositionDeemedSupplyUserInputData(
					gstin, formattedFy, status, userInput.getUserInputList());

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving Gstr9 Composition "
					+ "Deemed Supply Data ";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		}

	}
}
