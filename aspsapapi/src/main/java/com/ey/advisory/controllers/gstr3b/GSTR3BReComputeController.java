package com.ey.advisory.controllers.gstr3b;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.service.interest.gstr3b.GSTR3BReComSavePstService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@RestController
@Slf4j
public class GSTR3BReComputeController {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GSTR3BReComSavePstServiceImpl")
	GSTR3BReComSavePstService reComputeInterest;

	@PostMapping(value = "/ui/gstr3bRecomputeInt")
	public ResponseEntity<String> gstr3bRecomputeInt(
			@RequestBody String inputParams) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject obj = new JsonParser().parse(inputParams)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String taxPeriod = req.get("taxPeriod").getAsString();
			req.addProperty("ret_period", taxPeriod);
			String isGstinActive = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (!"A".equalsIgnoreCase(isGstinActive)) {
				String errMsg = "Auth Token is Inactive, Please Activate";
				throw new AppException(errMsg);
			}

			String reqData = gson.toJson(req);

			Pair<String, String> isRefIdAvail = reComputeInterest
					.gstr3bReComSavePstImp(APIConstants.GSTR3B_RECOMPUTE, gstin,
							taxPeriod, reqData);

			String apiStatus = isRefIdAvail.getValue0();

			if ("Success".equalsIgnoreCase(apiStatus)) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("resp", "Re-Compute is Successful");

			} else {
				String errMsg = String.format(
						"Error response from GSTIN, resp is %s for GSTIN %s and taxPeriod %s",
						isRefIdAvail.getValue1(), gstin, taxPeriod);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.addProperty("resp", errMsg);

			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = String
					.format("Error while calling Re-Computing the Data");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", e.getMessage());
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/api/gstr3bRecomputeInt")
	public ResponseEntity<String> testgstr3bRecomputeInt(
			@RequestBody String inputParams) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject obj = new JsonParser().parse(inputParams)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String gstin = req.get("gstin").getAsString();
			String taxPeriod = req.get("taxPeriod").getAsString();
			req.addProperty("ret_period", taxPeriod);
			String isGstinActive = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (!"A".equalsIgnoreCase(isGstinActive)) {
				String errMsg = "Auth Token is Inactive, Please Activate";
				throw new AppException(errMsg);
			}

			String reqData = gson.toJson(req);

			Pair<String, String> isRefIdAvail = reComputeInterest
					.gstr3bReComSavePstImp(APIConstants.GSTR3B_RECOMPUTE, gstin,
							taxPeriod, reqData);

			String apiStatus = isRefIdAvail.getValue0();

			if ("Success".equalsIgnoreCase(apiStatus)) {

				JsonElement respBody = gson
						.toJsonTree("Re-Compute is Successful");
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);

			} else {
				String errMsg = String.format(
						"Error response from GSTIN, resp is %s for GSTIN %s and taxPeriod %s",
						isRefIdAvail.getValue1(), gstin, taxPeriod);
				JsonElement respBody = gson.toJsonTree(errMsg);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", respBody);

			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = String
					.format("Error while calling Re-Computing the Data");
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

}
