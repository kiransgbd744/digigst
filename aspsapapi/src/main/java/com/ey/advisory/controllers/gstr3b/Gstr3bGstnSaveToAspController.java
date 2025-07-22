package com.ey.advisory.controllers.gstr3b;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.gstr3b.Gstr3BGstinDashboardService;
import com.ey.advisory.app.gstr3b.Gstr3BGstinsDto;
import com.ey.advisory.app.gstr3b.Gstr3bGstnSaveToAspService;
import com.ey.advisory.app.gstr3b.Gstr3bTaxPaymentDto;
import com.ey.advisory.app.gstr3b.Gstr3bUpdateGstnService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr3bGetInvoicesReqDto;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@RestController
@Slf4j
public class Gstr3bGstnSaveToAspController {

	@Autowired
	@Qualifier("Gstr3bGstnSaveToAspServiceImpl")
	private Gstr3bGstnSaveToAspService aspService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService authTokenService;

	@Autowired
	private Gstr3BGstinDashboardService dashBoardService;

	@Autowired
	@Qualifier("Gstr3bUpdateGstnServiceImpl")
	Gstr3bUpdateGstnService gstr3bUpdateGstnService;

	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;

	@PostMapping(value = "/ui/getGSTR3BSummaryFromGSTN", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGSTR3BSummaryFromGSTN(
			@RequestBody String jsonString) {

		JsonObject resp = new JsonObject();
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
			Type listType = new TypeToken<List<Gstr3bGetInvoicesReqDto>>() {
			}.getType();
			List<Gstr3bGetInvoicesReqDto> dtos = gson.fromJson(asJsonArray,
					listType);

			// Dto will contain only active GSTIN's and
			// respBody contains invalid GSTIN's list with messages
			JsonArray respBody = getAllActiveGstnList(dtos);
			APIRespDto apiResp = null;

			if (respBody != null && !respBody.equals(new JsonArray())) {
				apiResp = APIRespDto.creatErrorResp();
			}
			Set<String> uniqueGstins = new HashSet<>();
			for (Gstr3bGetInvoicesReqDto dto : dtos) {
				/**
				 * Extra Logic to support fromTime/toTime
				 */
				Integer fromPeriod = !StringUtils.isEmpty(dto.getFromPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getFromPeriod())
						: GenUtil.convertTaxPeriodToInt(dto.getTaxPeriod());
				Integer toPeriod = !StringUtils.isEmpty(dto.getToPeriod())
						? GenUtil.convertTaxPeriodToInt(dto.getToPeriod())
						: GenUtil.convertTaxPeriodToInt(dto.getTaxPeriod());

				if (fromPeriod == null || toPeriod == null) {
					apiResp = APIRespDto.creatErrorResp();
					JsonObject json = new JsonObject();
					json.addProperty("gstin", dto.getGstin());
					json.addProperty("msg", "ReturnPeriod can not be null");
					respBody.add(json);
					LOGGER.error("ReturnPeriod can not be null for {} ",
							dto.getGstin());
					break;
				}

				for (; fromPeriod <= toPeriod; fromPeriod++) {
					String taxPeriod = GenUtil
							.convertDerivedTaxPeriodToTaxPeriod(fromPeriod);
					if ("13".equals(taxPeriod.substring(0, 2))) {
						fromPeriod = fromPeriod + 88;
						taxPeriod = GenUtil
								.convertDerivedTaxPeriodToTaxPeriod(fromPeriod);
					}
					String gstin = dto.getGstin();
					// Invoking GSTN API
					if (LOGGER.isDebugEnabled()) {
						LOGGER.info(" Before Invoking getGSTR3BSummaryFromGSTN"
								+ ".getGSTR3BSummary() method TaxPeriod : "
								+ taxPeriod + " gstin : " + gstin);
					}

					APIResponse apiResponse = gstr3bUpdateGstnService
							.getGstnCall(gstin, taxPeriod);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.info("After invoking getGSTR3BSummaryFromGSTN"
								+ ".getGSTR3BSummary() method "
								+ "json response : " + apiResponse);
					}

					if (apiResponse == null) {

						gstnUserRequestRepo.updateGstnResponse(null, 0, gstin,
								taxPeriod, APIConstants.GSTR3B,
								LocalDateTime.now());
						String msg = "GSTIN has returned NULL response";
						apiResp = APIRespDto.creatErrorResp();
						JsonObject json = new JsonObject();
						json.addProperty("gstin", dto.getGstin());
						json.addProperty("msg", msg);
						respBody.add(json);
						LOGGER.error(msg);
						continue;
					}

					if (!apiResponse.isSuccess()) {
						gstnUserRequestRepo.updateGstnResponse(null, 0, gstin,
								taxPeriod, APIConstants.GSTR3B,
								LocalDateTime.now());
						APIError error = apiResponse.getError();
						String msg = error.getErrorDesc();
						apiResp = APIRespDto.creatErrorResp();
						JsonObject json = new JsonObject();
						json.addProperty("gstin", dto.getGstin());
						json.addProperty("msg", msg);
						respBody.add(json);
						LOGGER.error(msg);
						continue;
						/*
						 * resp.add("hdr", new Gson() .toJsonTree(new
						 * APIRespDto("E", msg))); return new
						 * ResponseEntity<>(resp.toString(),
						 * HttpStatus.INTERNAL_SERVER_ERROR);
						 */
					}

					List<Gstr3BGstinsDto> resultList = dashBoardService
							.getGstrDtoList(apiResponse);
					List<Gstr3bTaxPaymentDto> taxPaymentResult = dashBoardService
							.getTaxPayemntList(apiResponse);

					String getGstnData = apiResponse.getResponse();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.info("Before invoking saveGstnResponse() method "
								+ "resultList :" + resultList);
					}
					aspService.saveGstnResponse(gstin, taxPeriod, resultList,
							taxPaymentResult, getGstnData);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.info(
								"After invoking saveGstnResponse() method ");
					}
					if (!uniqueGstins.contains(gstin)) {
						uniqueGstins.add(gstin);
						JsonObject json = new JsonObject();
						json.addProperty("gstin", gstin);
						json.addProperty("msg",
								"Get Gstr3B  Request initiated Successfully");
						respBody.add(json);
					}
				}
			}
			if (apiResp == null) {
				apiResp = APIRespDto.createSuccessResp();
			}
			resp.add("hdr", gson.toJsonTree(apiResp));
			resp.add("resp", gson.toJsonTree(respBody));
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

		} catch (Exception ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private JsonArray getAllActiveGstnList(List<Gstr3bGetInvoicesReqDto> dtos) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting all the active GSTNs for GSTR1 GET");
		}

		String msg = "";
		List<Gstr3bGetInvoicesReqDto> inActiveGstinDtos = new ArrayList<>();
		JsonArray respBody = new JsonArray();
		if (dtos != null && !dtos.isEmpty()) {
			List<String> gstnList = dtos.stream().map(e -> e.getGstin())
					.collect(Collectors.toList());
			Map<String, String> gstinAuthMap = authTokenService
					.getAuthTokenStatusForGstins(gstnList);

			for (Gstr3bGetInvoicesReqDto dto : dtos) {
				String gstin = dto.getGstin();
				JsonObject json = new JsonObject();
				String authStatus = gstinAuthMap.get(gstin);
				if (!"A".equalsIgnoreCase(authStatus)) {
					inActiveGstinDtos.add(dto);
					msg = "Auth Token is Inactive, Please Activate";
					json.addProperty("gstin", gstin);
					json.addProperty("msg", msg);
					respBody.add(json);
				}
			}
			dtos.removeAll(inActiveGstinDtos);
		}
		return respBody;
	}

}
