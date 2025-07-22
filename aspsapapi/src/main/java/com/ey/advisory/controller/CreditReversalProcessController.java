package com.ey.advisory.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Gstr3bItcStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3bItcStatusRepository;
import com.ey.advisory.app.gstr3b.Gstr3bRatioUserInputDto;
import com.ey.advisory.app.services.credit.reversal.CreditRevForUserInputService;
import com.ey.advisory.app.services.credit.reversal.CreditReveSummaryDto;
import com.ey.advisory.app.services.credit.reversal.CreditReversalDto;
import com.ey.advisory.app.services.credit.reversal.CreditReversalFinalDto;
import com.ey.advisory.app.services.credit.reversal.CreditReversalProcessDto;
import com.ey.advisory.app.services.credit.reversal.CreditReversalProcessServiceImpl;
import com.ey.advisory.app.services.credit.reversal.CreditTurnOverDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Umesha.M
 *
 */
@RestController
public class CreditReversalProcessController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(CreditReversalProcessController.class);

	@Autowired
	@Qualifier("CreditReversalProcessServiceImpl")
	private CreditReversalProcessServiceImpl credRevsalProcServImpl;
	
	@Autowired
	@Qualifier("CreditRevForUserInputServiceImpl")
	private CreditRevForUserInputService credRevUserInputServ;

	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr3bItcStatusRepository")
	private Gstr3bItcStatusRepository gstr3bItcStatusRepository;

	/**
	 * 
	 * @param Save
	 *            the Reversal records and Proc Call
	 * @return
	 */
	@PostMapping(value = "/ui/proceCallComputeReversal", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> proceCallComputeReversal(
			@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController proceCallComputeReversal() Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		String groupCode = TenantContext.getTenantId();

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		try {
			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			Long entityId = reqObj.get("entityId").getAsLong();
			List<JsonObject> responseList = new ArrayList<>();
			
			List<String> gstinList = getDataSecurity(reqDto);
			if (gstinList != null && !gstinList.isEmpty()) {
				for (String gstinvalue : gstinList) {
					int derivedRetPer = GenUtil
							.convertTaxPeriodToInt(reqDto.getTaxPeriod());
					Gstr3bItcStatusEntity entity = new Gstr3bItcStatusEntity();
					Gstr3bItcStatusEntity get3btcStatus = gstr3bItcStatusRepository
							.get3btcStatus(gstinvalue, derivedRetPer);
					JsonObject response = new JsonObject();
					
					if (get3btcStatus != null && (get3btcStatus.getStatus()
							.equalsIgnoreCase(APIConstants.INITIATED)
							|| get3btcStatus.getStatus().equalsIgnoreCase(
									APIConstants.INPROGRESS))) {

						String msg = String.format(
								"Compute Already in Progress %s", gstinvalue);
						LOGGER.error(msg);
						response.addProperty("gstin", gstinvalue);
						response.addProperty("msg", msg);
				} else {
						entity.setStatus(APIConstants.INITIATED);

						entity.setGstin(gstinvalue);
						entity.setDeriverdRetPeriod(derivedRetPer);
						entity.setCreatedOn(LocalDateTime.now());
						User users = SecurityContext.getUser();
						entity.setCreatedBy(users.getUserPrincipalName());
						gstr3bItcStatusRepository.gstr3bItcInActiveUpdate(
								gstinvalue, derivedRetPer);
						gstr3bItcStatusRepository.save(entity);

						JsonObject jobParams = new JsonObject();
						jobParams.addProperty("entityId", entityId);
						jobParams.addProperty("id", entity.getId());

						asyncJobsService.createJob(groupCode,
								JobConstants.COMMON_CREDIT_COMPUTE,
								jobParams.toString(), userName, 1L, null, null);
						// JsonElement respBody = gson.toJsonTree(jobParams);
						String msg = String.format(
								"Reversal Compute is Initiated Successfully %s",
								gstinvalue);
						response.addProperty("gstin", gstinvalue);
						response.addProperty("msg", msg);
						
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"CreditReversalProcessController getCredRevrProcess2() End");
						}
					}
					
					responseList.add(response);
				}
			}
			
			JsonElement respBody = gson.toJsonTree(responseList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {

			String msg = "Exception Occured in CreditReversalProcessController";
			LOGGER.error(msg, e);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(msg));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	/**
	 * 
	 * @param get
	 *            Credit Reversal Process
	 * @return
	 */
	@PostMapping(value = "/ui/getCredRevrProcess", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCredRevrProcess(@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredRevrProcess() Begin");
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			Annexure1SummaryReqDto anx1SumReqDto = basicCommonSecParam
					.setOutwardSumDataSecuritySearchParams(reqDto);

			List<CreditReversalProcessDto> credRevProcDtos = credRevsalProcServImpl
					.getCredRevrProcess(anx1SumReqDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			JsonElement respBody = gson.toJsonTree(credRevProcDtos);
			resp.add("resp", respBody);

		} catch (Exception e) {
			LOGGER.error("Exception Occured", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredRevrProcess() End");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * 
	 * @param Get
	 *            Credit Reversal
	 * @return
	 */
	@PostMapping(value = "/ui/getCredReversal", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCredReversal(@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredReversal() Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {

			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);

			CreditReversalFinalDto revFinalDto = new CreditReversalFinalDto();
			CreditReveSummaryDto credRevSumDto = credRevsalProcServImpl
					.getCredRevSummary(reqDto);

			List<CreditReversalDto> reversTurnOverDtos = credRevsalProcServImpl
					.getCredReversal(reqDto);

			revFinalDto.setSumDto(credRevSumDto);
			revFinalDto.setReversTurnOverDtos(reversTurnOverDtos);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(revFinalDto));
		} catch (Exception e) {
			LOGGER.error("Exception Occured", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredReversal() End");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * 
	 * @param get
	 *            Credit Turn Over Records
	 * @return
	 */
	@PostMapping(value = "/ui/getCredTurnOverPartA", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCredTurnOverPartA(
			@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredTurnOver() Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {

			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			List<CreditTurnOverDto> reversTurnOverDtos = credRevsalProcServImpl
					.getCredTurnOverPartA(reqDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(reversTurnOverDtos));
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredTurnOver() End");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	/**
	 * 
	 * @param get
	 *            Credit Turn Over Records
	 * @return
	 */
	@PostMapping(value = "/ui/getCredTurnOverPartB", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getCredTurnOverPartB(
			@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredTurnOver() Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {

			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(reqObj,
					Annexure1SummaryReqDto.class);
			List<CreditTurnOverDto> reversTurnOverDtos = credRevsalProcServImpl
					.getCredTurnOverPartB(reqDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(reversTurnOverDtos));
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"CreditReversalProcessController getCredTurnOver() End");
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/pushToGstr3BCredRevRatio", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> pushToGstr3BCredRevRatio(
			@RequestBody String reqJson) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		try {
			JsonObject jsonReq = (new JsonParser()).parse(reqJson)
					.getAsJsonObject().get("req").getAsJsonObject();
			Annexure1SummaryReqDto reqDto = gson.fromJson(jsonReq,
					Annexure1SummaryReqDto.class);

			String msg = credRevsalProcServImpl
					.pushToGstr3BCredRevRatio(reqDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gson.toJsonTree(msg));
		} catch (Exception e) {
			LOGGER.error("Exception Occured: ", e);
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	private List<String> getDataSecurity(final Annexure1SummaryReqDto reqDto) {
		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)
						&& dataSecAttrs.get(OnboardingConstant.GSTIN) != null
						&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
								.isEmpty()) {
					gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
				}
			}
		}
		return gstinList;
	}
	
	@PostMapping(value = "/ui/saveUserInputCredReversal", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveUserInputCredReversal(@RequestBody String jsonString) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("CreditReversalProcessController saveUserInputCredReversal() Begin");
		}
		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			Gstr3bRatioUserInputDto reqDto = gson.fromJson(reqJson, Gstr3bRatioUserInputDto.class);

			credRevUserInputServ.saveCredRevUserInputSummary(reqDto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("CreditReversalProcessController saveUserInputCredReversal() End");
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Error while saveUserInputCredReversal() method execution";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PostMapping(value = "/ui/moveToUserInputCredReversal", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> moveToUserInputCredReversal(@RequestBody String jsonString) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("CreditReversalProcessController moveToUserInputCredReversal() Begin");
		}
		JsonObject requestObject = JsonParser.parseString(jsonString).getAsJsonObject();

		JsonObject reqJson = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			Gstr3bRatioUserInputDto reqDto = gson.fromJson(reqJson, Gstr3bRatioUserInputDto.class);

			credRevUserInputServ.moveToCredRevUserInputSummary(reqDto);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("CreditReversalProcessController moveToUserInputCredReversal() End");
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Error while moveToUserInputCredReversal() method execution";
			LOGGER.error(msg, e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
