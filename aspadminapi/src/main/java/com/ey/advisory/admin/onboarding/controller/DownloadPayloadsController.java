package com.ey.advisory.admin.onboarding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.bcadmin.common.dto.DownloadPayloadsdto;
import com.ey.advisory.bcadmin.common.services.payload.DownloadNICPayloadsService;
import com.ey.advisory.bcadmin.common.services.payload.DownloadNICRespPayloadsService;
import com.ey.advisory.bcadmin.common.utility.BusinessAdminConstants;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class DownloadPayloadsController {

	@Autowired
	@Qualifier("DownloadNICPayloadsServiceImpl")
	private DownloadNICPayloadsService downloadNICPayloadsService;

	@Autowired
	@Qualifier("DownloadNICRespPayloadsServiceImpl")
	private DownloadNICRespPayloadsService downloadNICRespPayloadsService;

	@PostMapping(value = "/downloadNICReqPayloads", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> downloadNICReqPayloads(
			@RequestBody String jsonString, HttpServletResponse response) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject errorResp = new JsonObject();

		try {
			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			DownloadPayloadsdto req = gson.fromJson(reqObject.get("req"),
					DownloadPayloadsdto.class);

			if (req.getApiType() == null || req.getApiType().isEmpty()) {
				String msg = "ApiType is mandatory to generate Report";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			if (req.getApiType().equals(BusinessAdminConstants.GEN_EINV_API)
					|| req.getApiType()
							.equals(BusinessAdminConstants.GEN_EWB_API)) {
				if (req.getDocNo() == null || req.getDocNo().isEmpty()
						|| req.getSgstin() == null
						|| req.getSgstin().isEmpty()) {
					String msg = "DocNo and Sgstin is mandatory to generate Report";
					LOGGER.error(msg);
					throw new AppException(msg);
				}

			} else {
				if (req.getIrn() == null || req.getIrn().isEmpty()) {
					String msg = "IRN is mandatory to generate Report";
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}

			downloadNICPayloadsService.downloadPayloads(req.getDocNo(),
					req.getSgstin(), req.getIrn(), req.getApiType(), response);

			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Message", e);
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(e.getMessage()));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/downloadNICResPayloads", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })

	public ResponseEntity<String> downloadNICResPayloads(
			@RequestBody String jsonString, HttpServletResponse response) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject errorResp = new JsonObject();

		try {
			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			DownloadPayloadsdto req = gson.fromJson(reqObject.get("req"),
					DownloadPayloadsdto.class);

			if (req.getApiType() == null || req.getApiType().isEmpty()) {
				String msg = "ApiType is mandatory to generate Report";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			if (req.getApiType().equals(BusinessAdminConstants.GEN_EINV_API)
					|| req.getApiType()
							.equals(BusinessAdminConstants.GEN_EWB_API)) {
				if (req.getDocNo() == null || req.getDocNo().isEmpty()
						|| req.getSgstin() == null
						|| req.getSgstin().isEmpty()) {
					String msg = "DocNo and Sgstin is mandatory to generate Report";
					LOGGER.error(msg);
					throw new AppException(msg);
				}

			} else {
				if (req.getIrn() == null || req.getIrn().isEmpty()) {
					String msg = "IRN is mandatory to generate Report";
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}

			downloadNICRespPayloadsService.downloadRespPayloads(req.getDocNo(),
					req.getSgstin(), req.getIrn(), req.getApiType(), response);

			return new ResponseEntity<>(null, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Message", e);
			errorResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			errorResp.add("resp", gson.toJsonTree(e.getMessage()));
			return new ResponseEntity<>(errorResp.toString(), HttpStatus.OK);
		}

	}

}
