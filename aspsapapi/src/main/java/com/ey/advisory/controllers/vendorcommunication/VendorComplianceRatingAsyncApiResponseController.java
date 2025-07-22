package com.ey.advisory.controllers.vendorcommunication;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.ey.advisory.common.BigDecimalPlainAdapter;
import java.math.BigDecimal;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.asprecon.VendorComplianceRatingAsyncApiResponseEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendoComplianceAsyncApiRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorApiRequestDto;
import com.ey.advisory.app.data.services.compliancerating.VendorAsyncApiStatusDto;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceRatingAsyncReportDto;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@RestController
@Slf4j
public class VendorComplianceRatingAsyncApiResponseController {

	@Autowired
	@Qualifier("VendoComplianceAsyncApiRepository")
	private VendoComplianceAsyncApiRepository repo;

	private static final String FAILED = "Failed";
	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@PostMapping(value = "/api/getVendorAsyncApiResponse", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorsReturnFileStatus(
			@RequestBody String jsonString) {
		Gson gson = new GsonBuilder()
		        .disableHtmlEscaping() // prevents escaping characters like >
		        .registerTypeAdapter(BigDecimal.class, new BigDecimalPlainAdapter()) // fixes 1E+1
		        .create();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String reqJson = obj.get("req").getAsJsonObject().toString();

			VendorApiRequestDto dto = gson.fromJson(reqJson,
					VendorApiRequestDto.class);
			String refId = dto.getRefId();
			Optional<VendorComplianceRatingAsyncApiResponseEntity> entity = repo
					.findByReferenceId(refId);
			JsonObject resps = new JsonObject();
			JsonElement respBody;
			Type type = new TypeToken<List<VendorComplianceRatingAsyncReportDto>>() {
			}.getType();
			List<VendorComplianceRatingAsyncReportDto> dtoList = gson
					.fromJson(GenUtil.convertClobtoString(
							entity.get().getResponsePayload()), type);
			if (entity.get().getStatus().equalsIgnoreCase("Completed")) {
				respBody = gson.toJsonTree(dtoList);
			} else {
				respBody = gson.toJsonTree(entity.get().getStatus());
			}
			resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			resp.add("resp", respBody);
			LOGGER.error("exception while intiating vendor api: ", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	@PostMapping(value = "/ui/getVendorAsyncApiStatus", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getVendorsStatus(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String entityId = obj.get("entityId").toString();
			List<VendorComplianceRatingAsyncApiResponseEntity> entities = repo
					.findByEntityIdOrderByIdDesc(Long.valueOf(entityId));
			JsonObject resps = new JsonObject();
			List<VendorAsyncApiStatusDto> dtoList = new ArrayList<>();
			for (VendorComplianceRatingAsyncApiResponseEntity entity : entities) {
				VendorAsyncApiStatusDto dto = new VendorAsyncApiStatusDto();
				if (entity.getCreatedOn() != null) {
					dto.setCreatedOn(EYDateUtil
							.toISTDateTimeFromUTC(LocalDateTime
									.parse(entity.getCreatedOn().toString()))
							.format(formatter));
				}
				dto.setFy(entity.getFy());
				if (entity.getGstinCount() != null) {
					dto.setNoOfGstin(entity.getGstinCount().toString());
				}
				dto.setRequestId(entity.getReferenceId());
				dto.setUploadedBy(entity.getCreatedBy());
				dto.setStatus(entity.getStatus());
				dto.setUploadMode(entity.getUploadMode());
				if (entity.getModifiedOn() != null) {
					dto.setCompletionOn(EYDateUtil
							.toISTDateTimeFromUTC(LocalDateTime
									.parse(entity.getModifiedOn().toString()))
							.format(formatter));
				}
				dtoList.add(dto);
			}
			JsonElement respBody = gson.toJsonTree(dtoList);
			resps = new JsonObject();
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto(FAILED, e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", e.getMessage())));
			resp.add("resp", respBody);
			LOGGER.error("exception while intiating vendor api: ", e);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}
}
