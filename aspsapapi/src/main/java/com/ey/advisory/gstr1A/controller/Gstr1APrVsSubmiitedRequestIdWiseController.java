package com.ey.advisory.gstr1A.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
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

import com.ey.advisory.app.gstr1.einv.Gstr1EinvRequesIdWiseDownloadTabDto;
import com.ey.advisory.app.gstr1.einv.Gstr1PrVsSubmReconReportRequestStatusDto;
import com.ey.advisory.app.gstr1a.einv.Gstr1AReportDownloadRequestIdWiseService;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@Slf4j
@RestController
public class Gstr1APrVsSubmiitedRequestIdWiseController {

	@Autowired
	@Qualifier("Gstr1AReportDownloadRequestIdWiseServiceImpl")
	private Gstr1AReportDownloadRequestIdWiseService reportStatus;

	@PostMapping(value = "/ui/gstr1APrVsSubmittedRequestIdWise", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr1APrVsSubmReportStatus(
			@RequestBody String jsonString) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr1APrVsSubmiitedRequestIdWiseController"
						+ ".getGstr1APrVsSubmReportStatus ,Parsing Input request";
				LOGGER.debug(msg);
			}

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			JsonObject request = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject json = request.get("req").getAsJsonObject();

			Gstr2InitiateReconReqDto reqDto = gson.fromJson(json,
					Gstr2InitiateReconReqDto.class);

			List<Gstr1PrVsSubmReconReportRequestStatusDto> status = reportStatus
					.getPrSubReportRequestStatus(reqDto, userName);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr1APrVsSubmiitedRequestIdWiseController"
						+ ".getGstr1APrVsSubmReportStatus, before returning response";
				LOGGER.debug(msg);
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
			String msg = " Unexpected error occured";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/ui/gstr1APrVsSubmDownloadIdWise", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getPrVsSubmdownloadIdWiseforRecon(
			@RequestBody String jsonString) {

		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			String configId = json.get("configId").getAsString();

			LOGGER.debug(
					"Inside Gstr1EinvRequesIdWiseDownloadTabController"
							+ ".getPrVsSubmdownloadIdWiseforRecon() method and config id : %s ",
					configId);

			List<Gstr1EinvRequesIdWiseDownloadTabDto> resp = reportStatus
					.getPrVsSubmDownloadData(Long.valueOf(configId));
			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(resp);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/ui/gstr1APrVsSubmittedRequestReport")
	public void gstr1PrVsSubmfileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		JsonObject errorResp = new JsonObject();

		LOGGER.debug("inside fileDownloads method and file type is {} ",
				"Gstr1ReconReports");

		try {
			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();

			String fileName = json.get("filePath").getAsString();
			String fileFolder = "PrVsSubmittedReport";
			
			String[] spliedName = fileName.split("_");

			Document document = DocumentUtility.downloadDocument(fileName,
					fileFolder);

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];
			
			String fname =  "GSTR1A Processed DigiGST vs Submitted GSTN"
					+ "_" + spliedName[1] + "_" + spliedName[2] + "_"
					+ spliedName[3];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename =" + fname));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (Exception ex) {
			String msg = "Error occured while downloading Gstr1PR vs Submitted report ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			response.flushBuffer();
		}
	}
}
