package com.ey.advisory.controller.gstr9;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.services.gstr9.Gstr9OutwardTabService;
import com.ey.advisory.app.data.services.gstr9.Gstr9OutwardUtil;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9GstinInOutwardDashBoardDTO;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9OutwardDashboardDTO;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@RestController
@RequestMapping(value = "/ui/")
public class Gstr9OutwardTabController {

	@Autowired
	@Qualifier("Gstr9OutwardTabServiceImpl")
	private Gstr9OutwardTabService gstr9OutwardTabService;

	@Autowired
	Gstr9OutwardUtil gstr9OutwardUtil;

	@PostMapping(value = "getOutwardTabDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getOutwardTabDetails(
			@RequestBody String jsonString, HttpServletRequest req) {		
		return getOutwardGstr9Details(jsonString);
	}

	private ResponseEntity<String> getOutwardGstr9Details(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE getGstr9OutwardDashboard");
			}
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String fyOld = req.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			String taxPeriod = GenUtil
					.getFinancialPeriodFromFY(formattedFy);
			if (Strings.isNullOrEmpty(gstin)
					|| Strings.isNullOrEmpty(taxPeriod)) {
				String msg = "Gstin and taxPeriod is mandatory to get the Data";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			Gstr9OutwardDashboardDTO respData = gstr9OutwardTabService
					.getgstr9OutwardDashBoardData(gstin, taxPeriod,formattedFy);
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respData);

			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			LOGGER.error(msg, ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.addProperty("resp", ex.getMessage());
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "saveOutwardTabDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveGstr9OutwardTabDetails(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("INSIDE Outward saveGstr9UserInputData");
			}
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String fyOld = req.get("fy").getAsString();
			String formattedFy = GenUtil.getFormattedFy(fyOld);
			String taxPeriod = GenUtil
					.getFinancialPeriodFromFY(formattedFy);
			if (Strings.isNullOrEmpty(gstin)
					|| Strings.isNullOrEmpty(taxPeriod)) {
				String msg = "Gstin and taxPeriod is mandatory to Save the Data";
				LOGGER.error(msg);
				throw new AppException(msg);
			}
			Type list = new TypeToken<List<Gstr9GstinInOutwardDashBoardDTO>>() {
			}.getType();
			List<Gstr9GstinInOutwardDashBoardDTO> listReqDtos = gson.fromJson(
					req.get("gstr9OutwardDashboard").getAsJsonArray(), list);
			gstr9OutwardTabService.saveGstr9OutwardUserInputData(gstin,
					formattedFy, listReqDtos);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.addProperty("resp", "Data Saved Successfully");
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = "Unexpected error while saving Gstr9 OutWard Data";
			LOGGER.error(msg, ex);
			JsonObject errorResp = new JsonObject();
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.addProperty("resp", msg);
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);
		}
	}

}
