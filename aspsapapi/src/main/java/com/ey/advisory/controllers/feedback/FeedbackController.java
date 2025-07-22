package com.ey.advisory.controllers.feedback;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.services.feedback.FDSurveyGetRespDto;
import com.ey.advisory.app.services.feedback.FDSurveyRespDto;
import com.ey.advisory.app.services.feedback.FeedbackSurveyService;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.repositories.master.FeedbackUserConfigPrmtRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@RestController
public class FeedbackController {

	@Autowired
	@Qualifier("FeedbackSurveyServiceServiceImpl")
	private FeedbackSurveyService feedbackService;

	@Autowired
	FeedbackUserConfigPrmtRepository userPrmtRepo;

	@PostMapping(value = { "/ui/getFeedbackSvyDtls" }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fetchProcessedRecords(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		FDSurveyRespDto respDto = new FDSurveyRespDto();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			String userName = requestObject.get("userName").getAsString();

			List<FDSurveyGetRespDto> resultDto = feedbackService
					.fetchSurveyRecords(userName);

			respDto.setUserName(userName);
			respDto.setSubmitted(
					userPrmtRepo
							.findByGroupCodeAndUserNameAndIsDeleteFalse(
									TenantContext.getTenantId(), userName)
							.size() > 0 ? true : false);
			if (resultDto.isEmpty()) {
				String errMsg = String.format(
						"Unable to Fetch the Feedback for User %s", userName);
				respDto.setErrMsg(errMsg);
				JsonElement respBody = gson.toJsonTree(respDto);
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.add("resp", respBody);
				return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
			}
			respDto.setResults(resultDto);
			JsonElement respBody = gson.toJsonTree(respDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while fetching survey records.";
			LOGGER.error(msg, ex);
			String errMsg = String
					.format("Unable to Fetch the Feedback for Logged in User.");
			respDto.setErrMsg(errMsg);
			JsonElement respBody = gson.toJsonTree(respDto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/saveFeedbackSvyDtls", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveFeedbackDtls(
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "data", required = true) String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		File tempDir = Files.createTempDir();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			FDSurveyRespDto response = gson.fromJson(requestObject.get("req"),
					FDSurveyRespDto.class);

			String statusMsg = feedbackService.saveSurveyRecords(response, file,
					tempDir);

			if (statusMsg.equalsIgnoreCase("Success")) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.addProperty("msg", "Records Saved Successfully.");
			} else {
				resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
				resp.addProperty("msg", "Unable to Save the Records.");
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			String msg = "Unexpected error while saving survey records.";
			LOGGER.error(msg, ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.addProperty("msg", "Unable to Save the Records.");
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

}
