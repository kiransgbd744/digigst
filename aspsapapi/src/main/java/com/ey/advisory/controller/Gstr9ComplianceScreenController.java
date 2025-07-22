package com.ey.advisory.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.app.data.returns.compliance.service.ComplienceRespDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplienceSummeryRespDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplienceSummeryService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@RestController
@Slf4j
public class Gstr9ComplianceScreenController {

	private static final DateTimeFormatter format = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Autowired
	@Qualifier("Gstr9ComplianceServiceImpl")
	private ComplienceSummeryService complienceSummeryService;

	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty returnDataStorageStatusRepo;

	@PostMapping(value = "/ui/getGstr9Compliance", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> fetchProcessedRecords(
			@RequestBody String jsonString) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();

		JsonObject json = requestObject.get("req").getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr9 complienceHistory request{}");
		}

		try {

			Gstr2aProcessedDataRecordsReqDto gstr2AProcessedDataRecordsReqDto = gson
					.fromJson(json, Gstr2aProcessedDataRecordsReqDto.class);
			List<ComplienceSummeryRespDto> respDtos = complienceSummeryService
					.findcomplienceSummeryRecords(
							gstr2AProcessedDataRecordsReqDto);
			JsonObject resp = new JsonObject();
			if ((respDtos != null) && (!respDtos.isEmpty())) {
				String initiateStatus = respDtos.get(0).getInitiatestatus();
				String initiateTime = respDtos.get(0).getInitiateTime();
				ComplienceRespDto respd = new ComplienceRespDto();

				respd.setInitiateTime(initiateTime);

				respd.setInitiatestatus(initiateStatus);

				respd.setComplienceSummeryRespDto(respDtos);
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(respd));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Response data for given criteria for processed data records is{}");
				}
			}  else {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				String msg = String.format("No GSTIN is Onboarded for this Return Type");
				resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retrieving Gstr9 compliance records";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
