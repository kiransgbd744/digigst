package com.ey.advisory.controller.gstr9;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.gstr9.Gstr9InwardService;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9InwardDashboardDTO;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9InwardSaveUserInputData;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9InwardUserInputDTO;
import com.ey.advisory.app.docs.enums.gstr9.SectionType;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jithendra.B
 *
 */

@Slf4j
@RestController
public class Gstr9InwardController {

	@Autowired
	private Gstr9InwardService gstr9InwardService;

	@PostMapping(value = "/ui/getGstr9InwardDashboardData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr9InwardDashboardData(
			@RequestBody String jsonString,HttpServletRequest req) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE getGstr9Dashboard");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String gstin = requestObject.get("gstin").getAsString();

			String fy = requestObject.get("fy").getAsString();

			if (Strings.isNullOrEmpty(gstin) || Strings.isNullOrEmpty(fy)) {
				String msg = "Gstin and Financial year is mandatory to get Gstn Inward Data";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			Gstr9InwardDashboardDTO respData = gstr9InwardService
					.getgstr9InwardDashBoardData(gstin, fy);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respData);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr9 inward Data  ";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		}

	}

	@PostMapping(value = "/ui/saveGstr9UserInputData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr9UserInputData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE saveGstr9UserInputData");
			}
			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();

			Gstr9InwardSaveUserInputData gstr9InwardSaveUserInputDataDto = gson
					.fromJson(reqJson, Gstr9InwardSaveUserInputData.class);

			String gstin = gstr9InwardSaveUserInputDataDto.getGstin();
			String fy = gstr9InwardSaveUserInputDataDto.getFy();
			String status = gstr9InwardSaveUserInputDataDto.getStatus();

			if (Strings.isNullOrEmpty(gstin) || Strings.isNullOrEmpty(fy)) {
				String msg = "Gstin and Financial year is mandatory to save Gstn9Inward user input Data";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			gstr9InwardService.saveGstr9InwardUserInputData(gstin, fy, status,
					gstr9InwardSaveUserInputDataDto.getUserInputList());

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving Gstr9 inward Data  ";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		}

	}

	@PostMapping(value = "/ui/getGstr9Inward7HPopUpData", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr9Inward7HPopUpData(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE getGstr9Inward7HPopUpData");
			}
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject("req");

			String gstin = requestObject.get("gstin").getAsString();

			String fy = requestObject.get("fy").getAsString();

			if (Strings.isNullOrEmpty(gstin) || Strings.isNullOrEmpty(fy)) {
				String msg = "Gstin and Financial year is mandatory to get Gstn Inward Data";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			List<Gstr9InwardUserInputDTO> dtoList = gstr9InwardService
					.getGstr9Inward7HPopUpData(gstin, fy);

			JsonObject statusResp = new JsonObject();
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dtoList);
			statusResp.add("gstr9Inward7HData", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", statusResp);

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr9 inward Data  ";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(msg));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);

		}

	}

}
