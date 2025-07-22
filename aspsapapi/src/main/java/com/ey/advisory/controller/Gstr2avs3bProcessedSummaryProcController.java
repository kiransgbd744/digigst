package com.ey.advisory.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
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

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconReportRequestStatusService;
import com.ey.advisory.app.data.entities.client.Gstr2a2bVs3BbReqIdWiseDataDto;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvRequesIdWiseDownloadTabDto;
import com.ey.advisory.app.gstr3b.Gstr2avs3bProcProcessServiceImpl;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author Sasidhar reddy
 *
 */
@RestController
public class Gstr2avs3bProcessedSummaryProcController {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2avs3bProcessedSummaryProcController.class);

	@Autowired
	@Qualifier("Gstr2avs3bProcProcessServiceImpl")
	private Gstr2avs3bProcProcessServiceImpl gstr2avs3bProcProcessServiceImpl;
	
	@Autowired
	@Qualifier("Gstr2InitiateReconReportRequestStatusServiceImpl")
	private Gstr2InitiateReconReportRequestStatusService reportStatusService;

	/**
	 * 
	 * @param Proc
	 *            Call
	 */
	@PostMapping(value = "/ui/gstr2avs3bproceCall", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> proceCallComputeReversal(
			@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr2avs3bProcessedSummaryProcController proceCall Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Gstr1VsGstr3bProcessSummaryReqDto criteria = gson.fromJson(reqObj,
					Gstr1VsGstr3bProcessSummaryReqDto.class);
			if (criteria.getDataSecAttrs() == null
					|| criteria.getDataSecAttrs().isEmpty()) {
				throw new AppException("User Does not have any gstin");
			}
			String msg = gstr2avs3bProcProcessServiceImpl
					.fetchgstr2avs3bProc(criteria);
			if (msg.equalsIgnoreCase("SUCCESS")) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(
						"Please click on Request ID wise Link to download the Recon Reports"));
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2avs3bProcessedSummaryProcController  End");
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}

		return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	}

	@PostMapping(value = "/ui/GSTR2AVS3B2Bvs3bReqIdWise", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> gstr2A2Bvs3bReqIdWise(
			@RequestBody String jsonString) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin GSTR2AVS3B2Bvs3bReqIdWise"
						+ ".gstr2A2Bvs3bReqIdWise ,Parsing Input request";
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

			List<Gstr2a2bVs3BbReqIdWiseDataDto> get2a2bVs3BbReqIdWiseScreenData = gstr2avs3bProcProcessServiceImpl
					.get2a2bVs3BbReqIdWiseScreenData(reqDto, userName);

			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson
					.toJsonTree(get2a2bVs3BbReqIdWiseScreenData);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr6ComputeCredDistData "
						+ ".getReportRequestStatus, before returning response";
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

	@RequestMapping(value = "/ui/gstr2A2Bvs3BRptDwnldButton", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void gstr2A2Bvs3BRptDwnldButton(
			@RequestBody String jsonString, HttpServletResponse response) {
		Document document = null;
		String documentName = null;
		try {

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			String requestId = json.get("requestId").getAsString();

			LOGGER.debug(
					"Inside gstr 2a vs 3b report download controller"
							+ " requestId : %s ",
					requestId);

			List<Gstr1EinvRequesIdWiseDownloadTabDto> gstr2a2bVs3bData = gstr2avs3bProcProcessServiceImpl
					.getGstr2a2bVs3bData(Long.valueOf(requestId));

			String docId = gstr2a2bVs3bData.get(0).getDocId();

			if (docId != null && !docId.isEmpty()) {

				String msg = String.format(
						"Doc Id is available  and Doc Id %s",
						docId);
				LOGGER.debug(msg);
				document = DocumentUtility
						.downloadDocumentByDocId(docId);
				documentName = document.getName();
				if (LOGGER.isDebugEnabled()) {
					String str = String.format("file name is : %s",
							documentName);
					LOGGER.debug(str);
				}

			}

			if (document == null) {
				return;
			}

			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format(
								"attachment; filename = " + documentName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return;
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return;
		}

	}

	@PostMapping(value = "/ui/getGstr2a2bVS3bRequestIds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr2a2bVS3bRequestIds(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr2ReconRequestStatusController"
						+ ".getgstr2RequestId ,Parsing Input request";
				LOGGER.debug(msg);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			String entityId = null;

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			if (requestObject.has("entityId")) {
				entityId = requestObject.get("entityId").getAsString();

			}

			Gstr2InitiateReconReqDto reqDto = gson.fromJson(requestObject,
					Gstr2InitiateReconReqDto.class);

			List<Gstr2InitiateReconRequestIdsDto> status = gstr2avs3bProcProcessServiceImpl
					.getRequestIds(userName, Long.valueOf(entityId), reqDto);
			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr2avs3bProcessedSummaryProcController"
						+ ".getGstr2a2bVS3bRequestIds Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr2ReconRequestStatusController"
						+ ".getgstr2RequestIds, before returning response";
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
	
	@PostMapping(value = "/ui/getGSTR2AVS3B2bVS3bRequestIds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getgstr2RequestIds(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr2ReconRequestStatusController"
						+ ".getgstr2RequestId ,Parsing Input request";
				LOGGER.debug(msg);
			}
			Gson gson = GsonUtil.newSAPGsonInstance();
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			String entityId = null;

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			

			if (requestObject.has("entityId")) {
				entityId = requestObject.get("entityId").getAsString();

			}
			
			Gstr2InitiateReconReqDto reqDto = gson.fromJson(requestObject,
					Gstr2InitiateReconReqDto.class);
			
			List<Gstr2InitiateReconRequestIdsDto> status = reportStatusService
					.getRequestIds(userName, Long.valueOf(entityId), reqDto);
			if (LOGGER.isDebugEnabled()) {
				String msg = "Gstr2InitiateReconReportRequestStatusServiceImpl"
						+ ".getgstr2RequestIds Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr2ReconRequestStatusController"
						+ ".getgstr2RequestIds, before returning response";
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

	@PostMapping(value = "/ui/getinitiatedByIds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getinitiatedByIds(
			@RequestBody String jsonString) {

		try {
			
			Gson gson = GsonUtil.newSAPGsonInstance();
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			String entityId = null;

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			

			if (requestObject.has("entityId")) {
				entityId = requestObject.get("entityId").getAsString();

			}
			
			Gstr2InitiateReconReqDto reqDto = gson.fromJson(requestObject,
					Gstr2InitiateReconReqDto.class);
			
			List<Gstr2InitiateReconRequestIdsDto> status = reportStatusService
					.getUserId(userName, Long.valueOf(entityId), reqDto);
			
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
		
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
	
	@PostMapping(value = "/ui/getinitiatedByUserEmailIds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getinitiatedByUserEmailIds(
			@RequestBody String jsonString) {

		try {
			
			Gson gson = GsonUtil.newSAPGsonInstance();
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			String entityId = null;

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			

			if (requestObject.has("entityId")) {
				entityId = requestObject.get("entityId").getAsString();

			}
			
			Gstr2InitiateReconReqDto reqDto = gson.fromJson(requestObject,
					Gstr2InitiateReconReqDto.class);
			
			List<Gstr2InitiateReconRequestIdsDto> status = reportStatusService
					.getinitiatedByUserEmailIds(userName, Long.valueOf(entityId), reqDto);
			
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(status);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
		
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
	
}
