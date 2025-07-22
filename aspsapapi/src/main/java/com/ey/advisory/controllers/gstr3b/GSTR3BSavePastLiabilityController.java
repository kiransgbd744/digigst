package com.ey.advisory.controllers.gstr3b;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSecDetailsDTO;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;
import com.ey.advisory.app.gstr3b.Gstr3BGstinDashboardDao;
import com.ey.advisory.app.gstr3b.Gstr3BSavePastLiabDto;
import com.ey.advisory.app.gstr3b.Gstr3BSavePastLibBrkUpDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Gstr3BConstants;
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
public class GSTR3BSavePastLiabilityController {

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("GSTR3BReComSavePstServiceImpl")
	GSTR3BReComSavePstService savePastLiaServ;

	@Autowired
	private Gstr3BGstinDashboardDao gstnDao;

	@PostMapping(value = "/ui/gstr3bSavePastLiab")
	public ResponseEntity<String> gstr3bSavePastLiab(
			@RequestBody String inputParams) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject obj = new JsonParser().parse(inputParams)
					.getAsJsonObject();
			JsonObject reqJson = obj.get("req").getAsJsonObject();
			String gstin = reqJson.get("gstin").getAsString();
			String taxPeriod = reqJson.get("taxPeriod").getAsString();

			String isGstinActive = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (!"A".equalsIgnoreCase(isGstinActive)) {
				String errMsg = "Auth Token is Inactive, Please Activate";
				throw new AppException(errMsg);
			}

			List<Gstr3BGstinAspUserInputDto> userInputDto = gstnDao
					.getUserInputDtoforSavePst(taxPeriod, gstin,
							Arrays.asList(Gstr3BConstants.Table7_1));

			Gstr3BSavePastLiabDto gstr3bSavePstDto = converttoGSTNdto(
					userInputDto, gstin, taxPeriod);

			String reqData = gson.toJson(gstr3bSavePstDto);

			Pair<String, String> isRefIdAvail = savePastLiaServ
					.gstr3bReComSavePstImp(APIConstants.GSTR3B_SAVEPSTLIAB,
							gstin, gstr3bSavePstDto.getRetPeriod(), reqData);

			String apiStatus = isRefIdAvail.getValue0();

			if ("Success".equalsIgnoreCase(apiStatus)) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("resp",
						"Save Past period liability is Successful");

			} else {
				String errMsg = String.format(
						"Error response from GSTIN, resp is %s for GSTIN %s and taxPeriod %s",
						isRefIdAvail.getValue1(), gstin,
						gstr3bSavePstDto.getRetPeriod());
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.addProperty("resp", errMsg);

			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = String
					.format("Error while doing Save Past period liability");
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("resp", e.getMessage());
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/api/gstr3bSavePastLiab")
	public ResponseEntity<String> testgstr3bSavePastLiab(
			@RequestBody String inputParams) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject obj = new JsonParser().parse(inputParams)
					.getAsJsonObject();
			JsonObject reqJson = obj.get("req").getAsJsonObject();
			String gstin = reqJson.get("gstin").getAsString();
			String taxPeriod = reqJson.get("taxPeriod").getAsString();

			String isGstinActive = authTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (!"A".equalsIgnoreCase(isGstinActive)) {
				String errMsg = "Auth Token is Inactive, Please Activate";
				throw new AppException(errMsg);
			}

			List<Gstr3BGstinAspUserInputDto> userInputDto = gstnDao
					.getUserInputDtoforSavePst(taxPeriod, gstin,
							Arrays.asList(Gstr3BConstants.Table7_1));

			Gstr3BSavePastLiabDto gstr3bSavePstDto = converttoGSTNdto(
					userInputDto, gstin, taxPeriod);

			String reqData = gson.toJson(gstr3bSavePstDto);

			Pair<String, String> isRefIdAvail = savePastLiaServ
					.gstr3bReComSavePstImp(APIConstants.GSTR3B_SAVEPSTLIAB,
							gstin, gstr3bSavePstDto.getRetPeriod(), reqData);

			String apiStatus = isRefIdAvail.getValue0();

			if ("Success".equalsIgnoreCase(apiStatus)) {
				JsonElement respBody = gson
						.toJsonTree("Save Past period liability is Successful");
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", respBody);

			} else {
				String errMsg = String.format(
						"Error response from GSTIN, resp is %s for GSTIN %s and taxPeriod %s",
						isRefIdAvail.getValue1(), gstin,
						gstr3bSavePstDto.getRetPeriod());
				JsonElement respBody = gson.toJsonTree(errMsg);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", respBody);

			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = String
					.format("Error while doing Save Past period liability");
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			LOGGER.error(msg, e);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	private Gstr3BSavePastLiabDto converttoGSTNdto(
			List<Gstr3BGstinAspUserInputDto> userInput, String gstin,
			String taxPeriod) {

		Gstr3BSavePastLiabDto gstnDto = new Gstr3BSavePastLiabDto();
		gstnDto.setGstin(gstin);
		gstnDto.setRetPeriod(taxPeriod);
		List<Gstr3BSavePastLibBrkUpDto> brkUp = new ArrayList<>();

		for (Gstr3BGstinAspUserInputDto dto : userInput) {
			Gstr3BSavePastLibBrkUpDto brkUpDto = new Gstr3BSavePastLibBrkUpDto();
			brkUpDto.setRetPeriod(dto.getUserRetPeriod());
			Gstr3BSecDetailsDTO liaDto = new Gstr3BSecDetailsDTO();
			liaDto.setCsamt(dto.getCess());
			liaDto.setIamt(dto.getIgst());
			liaDto.setCamt(dto.getCgst());
			liaDto.setSamt(dto.getSgst());
			brkUpDto.setLiability(liaDto);
			brkUp.add(brkUpDto);
		}
		gstnDto.setBreakUp(brkUp);
		return gstnDto;
	}

}
