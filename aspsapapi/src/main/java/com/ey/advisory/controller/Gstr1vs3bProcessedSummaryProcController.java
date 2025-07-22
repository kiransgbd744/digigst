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
import com.ey.advisory.app.data.repositories.client.Gstr1Vs3bConfigRepository;
import com.ey.advisory.app.gstr3b.Gstr1Vs3BRequestStatusDto;
import com.ey.advisory.app.gstr3b.Gstr1vs3bProcProcessServiceImpl;
import com.ey.advisory.app.gstr3b.Gstr1vs3bRequestStatusService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.controllers.anexure1.InputValidationUtil;
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
public class Gstr1vs3bProcessedSummaryProcController {

	private static final String GSTR1VsGstr3BRepo = "Gstr1Vs3bDownloadRepo";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1vs3bProcessedSummaryProcController.class);

	@Autowired
	@Qualifier("Gstr1vs3bProcProcessServiceImpl")
	private Gstr1vs3bProcProcessServiceImpl gstr1vs3bProcProcessServiceImpl;

	@Autowired
	@Qualifier("Gstr1vs3bRequestStatusServiceImpl")
	private Gstr1vs3bRequestStatusService gstr1vs3bReqServiceImpl;

	@Autowired
	@Qualifier(value = "Gstr1Vs3bConfigRepository")
	private Gstr1Vs3bConfigRepository gstr1Vs3bconfigRepository;

	/**
	 * 
	 * @param Proc
	 *            Call
	 */
	@PostMapping(value = "/ui/gstr1vs3bproceCall", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> proceCallComputeReversal(
			@RequestBody String req) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr1vs3bProcessedSummaryProcController proceCall Begin");
		}
		Gson gson = GsonUtil.gsonInstanceWithExpose();
		JsonObject resp = new JsonObject();
		try {
			JsonObject reqObj = (new JsonParser()).parse(req).getAsJsonObject()
					.get("req").getAsJsonObject();
			Gstr1VsGstr3bProcessSummaryReqDto reqDto = gson.fromJson(reqObj,
					Gstr1VsGstr3bProcessSummaryReqDto.class);
			if (reqDto.getDataSecAttrs() == null
					|| reqDto.getDataSecAttrs().isEmpty()) {
				throw new AppException("User Does not have any gstin");
			}
			String msg = gstr1vs3bProcProcessServiceImpl
					.fetchgstr1vs3bProc(reqDto);

			if (msg.equalsIgnoreCase("SUCCESS")) {
				resp.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resp.add("resp", gson.toJsonTree(
						"Please click on Request ID wise Link to download the Recon Reports"));
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1vs3bProcessedSummaryProcController ProcCall End");
			}

			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1vs3bProcessedSummaryProcController ProcCall Ended with error");
			}
			return InputValidationUtil.createJsonErrResponse(e);
		}

	}

	@RequestMapping(value = "/ui/getgstr1vs3BReportRequestStatusFilter", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getgstr1vs3BReportRequestStatusNew(
			@RequestBody String jsonString) {
		
		try {
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside Gstr1vs3bProcessedSummaryProcController.getgstr1vs3BReportRequestStatusNew() "
								+ "method : %s {} ", jsonString);
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

			
			JsonObject resps = new JsonObject();

			List<Gstr1Vs3BRequestStatusDto> recResponse = gstr1vs3bReqServiceImpl
					.getRequestIdSummary(reqDto, userName);

			if (recResponse != null && !recResponse.isEmpty()) {
				JsonElement respBody = gson.toJsonTree(recResponse);
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
			} else {
				JsonElement respBody = gson.toJsonTree("No Data");
				resps.add("hdr",
						gson.toJsonTree(APIRespDto.createSuccessResp()));
				resps.add("resp", respBody);
			}

			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", new Gson().toJsonTree(ex));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";

			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();

			resp.add("hdr", new Gson().toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", new Gson().toJsonTree(ex));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(value = "/ui/downloadGstr1Vs3bReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void fileDownloads(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		LOGGER.debug("inside fileDownloads method for downloadGstr1Vs3bReport");

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			String fileName = json.get("rptDownldPath").getAsString();
			Long requestId = json.get("requestId").getAsLong();

			LOGGER.debug("inside fileDownloads for fileName -", fileName);

			Document document = DocumentUtility.downloadDocument(fileName,
					GSTR1VsGstr3BRepo);

			if (document == null) {
				LOGGER.error("empty");
				return;
			}

		/*	Optional<Gstr1vs3bConfigEntity> entity = gstr1Vs3bconfigRepository
					.findById(requestId);
			String derivedretFrom = entity.get().getDeriverdRetPeriodFrom()
					.toString();
			String deriveRetTo = entity.get().getDeriverdRetPeriodTo()
					.toString();

			String fileSplits[] = fileName.split("_");
			
			String newFileName = fileSplits[0] + "_" + fileSplits[1] + "_"
					+ derivedretFrom + "_" + deriveRetTo + "_" + fileSplits[4];
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr1vs3bProcessedSummaryProcController downloadFile with newFileName -"+newFileName);
			}*/
			InputStream inputStream = document.getContentStream().getStream();
			int read = 0;
			byte[] bytes = new byte[1024];

			if (document != null) {
				response.setHeader("Content-Disposition",
						String.format("attachment; filename =" + fileName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}

		} catch (Exception ex) {
			String msg = "Exception while downloading files ";
			LOGGER.error(msg, ex);
		}
	}

	@PostMapping(value = "/ui/getGstr1vs3BRequestId", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr1vs3BRequestId(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin Gstr1vs3bProcessedSummaryProcController"
						+ ".getGstr1vs3BRequestId ,Parsing Input request";
				LOGGER.debug(msg);
			}
			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}
			Long entityId = null;

			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			entityId = json.get("entityId").getAsLong();

			List<Gstr2InitiateReconRequestIdsDto> requestIds = gstr1vs3bReqServiceImpl
					.getRequestIds(userName, entityId);

			if (LOGGER.isDebugEnabled()) {
				String msg = "EWB3WayReportRequestStatusServiceImpl"
						+ ".getEWB3WayRequestIds Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(requestIds);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End Gstr1vs3bProcessedSummaryProcController"
						+ ".getGstr1vs3BRequestId, before returning response";
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

	public static void main(String args[]) {
		String fileName = "GSTR1vsGSTR3B_092022_092022_20221028212739.xlsx";
		String filenames[] = fileName.split("_");
		System.out.println(filenames[3]);
	}
}
