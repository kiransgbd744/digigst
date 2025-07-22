package com.ey.advisory.controllers.anexure1;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.Gstr1NilDetailsEntity;
import com.ey.advisory.app.data.repositories.client.Gstr1NilNonExtSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilRepository;
import com.ey.advisory.app.data.repositories.client.NilAndHsnProcedureCallRepository;
import com.ey.advisory.app.data.services.anx1.Gstr1NilExmpNonGstStautsService;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstSaveReqDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstSummaryStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstVerticalStatusRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonVerticalDelReqDto;
import com.ey.advisory.app.gstr3b.Gstr1NilNonExtProcServiceImpl;
import com.ey.advisory.app.services.validation.b2cs.ErrorDescriptionDto;
import com.ey.advisory.app.util.OnboardingConstant.CONFIG_PARAM_OUTWARD_QUE_KEY_ID;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1ProcessedRecordsReqDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * @author Sasidhar Reddy
 *
 */
@RestController
public class Gstr1NilExmpNonGstStautsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1NilExmpNonGstStautsController.class);

	private static final String UPDATE_SUCCESSFULLY = "Updated Successfully";
	private static final String DELETED_SUCCESSFULLY = "Deleted Successfully";
	private static final String RESP = "resp";

	@Autowired
	@Qualifier("Gstr1NilExmpNonGstStautsService")
	Gstr1NilExmpNonGstStautsService gstr1NilExmpNonGstStautsService;

	@Autowired
	@Qualifier("Gstr1NilRepository")
	Gstr1NilRepository gstr1NilRepository;

	@Autowired
	@Qualifier("Gstr1NilNonExtSummaryRepository")
	Gstr1NilNonExtSummaryRepository gstr1NilNonExtSummaryRepository;

	@Autowired
	@Qualifier("Gstr1NilNonExtProcServiceImpl")
	Gstr1NilNonExtProcServiceImpl Gstr1NilNonExtProcServiceImpl;

	@Autowired
	@Qualifier("NilAndHsnProcedureCallRepositoryImpl")
	private NilAndHsnProcedureCallRepository nilAndHsnProcedureCallRepository;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPrmtRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	private static final List<Integer> LENGTH1 = ImmutableList.of(4, 6, 8);
	private static final List<Integer> LENGTH2 = ImmutableList.of(6, 8);

	@RequestMapping(value = "/ui/gstr1NilExmpNonGstStauts", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		LOGGER.debug("The selected criteria for processed records is:->"
				+ requestObject.get("req"));
		// Execute the service method and get the result.
		try {

			Gstr1ProcessedRecordsReqDto reqDto = gson.fromJson(json,
					Gstr1ProcessedRecordsReqDto.class);

			List<Gstr1NilExmpNonGstStatusRespDto> userInputDetails = gstr1NilExmpNonGstStautsService
					.findNilExmpNonGstStauts(reqDto);
			List<Gstr1NilExmpNonGstSummaryStatusRespDto> summaryDetails = gstr1NilExmpNonGstStautsService
					.findNilExmpNonGstSummaryStauts(reqDto);
			
			summaryDetails.sort(Comparator.comparing(Gstr1NilExmpNonGstSummaryStatusRespDto::getOrder));
			
			LOGGER.debug(" summaryDetails {} ",summaryDetails.toString());
			
			List<Gstr1NilExmpNonGstVerticalStatusRespDto> vetricalDetails = gstr1NilExmpNonGstStautsService
					.findNilExmpNonGstVerticalStauts(reqDto);
			JsonElement userDataRespBody = gson.toJsonTree(userInputDetails);
			JsonElement summaryRespBody = gson.toJsonTree(summaryDetails);
			JsonElement vertRespBody = gson.toJsonTree(vetricalDetails);
			Map<String, JsonElement> combinedMap = new HashMap<>();

			combinedMap.put("summary", summaryRespBody);
			combinedMap.put("gstnView", userDataRespBody);
			combinedMap.put("verticalData", vertRespBody);
			JsonElement respBody = gson.toJsonTree(combinedMap);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			LOGGER.debug(
					"Response data for given criteria for processed records is :->"
							+ resp.toString());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Gstr1 processed records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/gstr1NilExmpNonGstSave", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1NilExmpNonGstSave(
			@RequestBody String jsonReq) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			JsonArray jsonReqArray = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonArray();
			@SuppressWarnings("serial")
			Type listType = new TypeToken<List<Gstr1NilExmpNonGstSaveReqDto>>() {
			}.getType();

			List<Gstr1NilExmpNonGstSaveReqDto> reqDtos = gson
					.fromJson(jsonReqArray, listType);

			gstr1NilExmpNonGstStautsService.saveNilExmpNonGstStauts(reqDtos);

			resp.add(RESP, gson.toJsonTree(new APIRespDto(
					APIRespDto.getSuccessStatus(), UPDATE_SUCCESSFULLY)));
			LOGGER.debug(
					"Response data for given criteria for processed records is :->"
							+ resp.toString());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

	}

	@PostMapping(value = "/ui/nilExmpNonVerticalDelete", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> nilExmpNonVerticalDelete(
			@RequestBody String jsonString) {
		String groupCode = TenantContext.getTenantId();
		LOGGER.debug(String.format("Group Code = '%s'", groupCode));
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();
			Gson gson = GsonUtil.newSAPGsonInstance();

			Gstr1NilExmpNonVerticalDelReqDto summaryRequest = gson.fromJson(
					reqJson.toString(), Gstr1NilExmpNonVerticalDelReqDto.class);

			int deleteRecord = gstr1NilRepository.UpdateListId(
					summaryRequest.getId(), summaryRequest.getGstin(),
					summaryRequest.getTaxPeriod(), summaryRequest.getDocKey());
			int deleteRecord1 = gstr1NilNonExtSummaryRepository.UpdateListId(
					summaryRequest.getId(), summaryRequest.getGstin(),
					summaryRequest.getTaxPeriod(), summaryRequest.getDocKey());

			JsonObject resp = new JsonObject();
			if (deleteRecord != 0 && deleteRecord1 != 0) {
				resp.add("hdr", gson.toJsonTree(new APIRespDto(
						APIRespDto.getSuccessStatus(), DELETED_SUCCESSFULLY)));
				LOGGER.debug(
						"Response data for given criteria for processed records is :->"
								+ resp.toString());

			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.getErrorStatus()));
			}

			// String sgstin = summaryRequest.getGstin() != null
			// && summaryRequest.getGstin().length() >= 15
			// ? summaryRequest.getGstin().substring(0, 15) : null;
			// nilAndHsnProcedureCallRepository.getNilNonProc(sgstin, GenUtil
			// .convertTaxPeriodToInt(summaryRequest.getTaxPeriod()));
			// nilAndHsnProcedureCallRepository.getHsnProc(sgstin, GenUtil
			// .convertTaxPeriodToInt(summaryRequest.getTaxPeriod()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while fecthing Annexure1 "
					+ "Summary Documents " + ex;
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/ui/gstr1NilExmpNonVerticalSave", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr1NilExmpNonVerticalSave(
			@RequestBody String jsonReq) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			JsonArray jsonReqArray = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject().get("req").getAsJsonArray();
			@SuppressWarnings("serial")
			Type listType = new TypeToken<List<Gstr1NilExmpNonGstVerticalStatusRespDto>>() {
			}.getType();

			List<Gstr1NilExmpNonGstVerticalStatusRespDto> reqDtos = gson
					.fromJson(jsonReqArray, listType);
			String groupCode = TenantContext.getTenantId();

			Long entityId = gSTNDetailRepository
					.findEntityIdByGstin(reqDtos.get(0).getGstin());
			EntityConfigPrmtEntity answerEntity = entityConfigPrmtRepository
					.findByGroupCodeAndEntityIdAndparamkryIdAndIsDeleteFalse(
							groupCode, entityId,
							CONFIG_PARAM_OUTWARD_QUE_KEY_ID.O34.name());
			String answer = null;
			if (answerEntity != null) {
				answer = answerEntity.getParamValue();
			}
			List<Gstr1NilExmpNonGstVerticalStatusRespDto> errorlist = new ArrayList<>();
			if (GSTConstants.B.equalsIgnoreCase(answer)) {
				errorlist = reqDtos.stream()
						.filter(dto -> !LENGTH2.contains(dto.getHsn().length()))
						.collect(Collectors.toList());
			} else {
				errorlist = reqDtos.stream()
						.filter(dto -> !LENGTH1.contains(dto.getHsn().length()))
						.collect(Collectors.toList());
			}
			if (!errorlist.isEmpty()) {
				errorlist.stream()
						.forEach(dto -> dto.setErrorList(deriveErrorDocs()));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("error records{}", errorlist);
				}
				reqDtos.removeAll(errorlist);

			}
			
			if (!reqDtos.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("processed records{}", reqDtos);
				}
				gstr1NilExmpNonGstStautsService
						.saveNilExmpNonVerticalStauts(reqDtos);

			}
			List<Gstr1NilExmpNonGstVerticalStatusRespDto> respList = new ArrayList<>();

			respList.addAll(errorlist);
			respList.addAll(reqDtos);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("respList {}", respList);
			}
			

			JsonElement respBody = gson.toJsonTree(respList);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			
			
			
			
			/*
			 * return resp; resp.add("hdr", gson.toJsonTree(new APIRespDto(
			 * APIRespDto.getSuccessStatus(), UPDATE_SUCCESSFULLY)));
			 */
			LOGGER.debug(
					"Response data for given criteria for processed records is :->"
							+ resp.toString());
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception e) {
			String msg = "Unexpected error while persisting entities";
			LOGGER.error(msg, e);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

	}

	private static List<ErrorDescriptionDto> deriveErrorDocs() {
		ErrorDescriptionDto errorDto = new ErrorDescriptionDto();
		errorDto.setErrorCode("ER1278");
		errorDto.setErrorDesc(
				"Length of entered HSN code is not valid as per AATO");
		errorDto.setErrorType("ERR");
		errorDto.setErrorField("hsn");
		return Arrays.asList(errorDto);

	}
}
