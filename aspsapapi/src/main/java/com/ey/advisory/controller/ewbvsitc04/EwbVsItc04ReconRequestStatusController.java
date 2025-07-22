/**
 * 
 */
package com.ey.advisory.controller.ewbvsitc04;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbvsItco4DownloadReconReportsRepository;
import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04InitiateReconReportRequestStatusDto;
import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04InitiateReconReportRequestStatusService;
import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04RequesIdWiseDownloadTabService;
import com.ey.advisory.app.reconewbvsitc04.EwbvsItc04ReconDownloadReportsEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@RestController
public class EwbVsItc04ReconRequestStatusController {

	@Autowired
	@Qualifier("EwbVsItc04ReportRequestStatusServiceImpl")
	private EwbVsItc04InitiateReconReportRequestStatusService reportStatusService;

	@Autowired
	@Qualifier("EwbVsItc04RequesIdWiseDownloadTabServiceImpl")
	private EwbVsItc04RequesIdWiseDownloadTabService service;

	@Autowired
	@Qualifier("EwbvsItco4DownloadReconReportsRepository")
	EwbvsItco4DownloadReconReportsRepository reconDownlRepo;

	@PostMapping(value = "/ui/getEwbVsItc04RequestIds", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEwbVsItc04RequestIds(
			@RequestBody String jsonString) {

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = "Begin EwbVsItc04ReconRequestStatusController"
						+ ".getEwbVsItc04RequestIds ,Parsing Input request";
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

			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			entityId = requestObject.get("entityId").getAsLong();

			List<Gstr2InitiateReconRequestIdsDto> requestIds = reportStatusService
					.getRequestIds(userName, entityId);

			if (LOGGER.isDebugEnabled()) {
				String msg = "EwbVsItc04ReconRequestStatusController"
						+ ".getEwbVsItc04RequestIds Preparing Response Object";
				LOGGER.debug(msg);
			}
			JsonObject resp = new JsonObject();
			JsonObject gstinResp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(requestIds);
			gstinResp.add("requestDetails", respBody);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", gstinResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = "End EwbVsItc04ReconRequestStatusController"
						+ ".getEwbVsItc04RequestIds, before returning response";
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

	@PostMapping(value = "/ui/getEwbVsItc04ReportRequestStatusFilter", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEwbVsItc04ReportRequestStatusFilter(
			@RequestBody String jsonString) {

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		String msg = String
				.format("Inside EwbVsItc04ReconRequestStatusController.getEwbVsItc04ReportRequestStatusFilter() "
						+ "method : %s {} ", jsonString);
		LOGGER.debug(msg);

		try {
			Gstr2InitiateReconReqDto reqDto = gson.fromJson(json,
					Gstr2InitiateReconReqDto.class);

			String userName = null;
			User user = SecurityContext.getUser();

			if (user != null) {
				userName = user.getUserPrincipalName();
			} else {
				userName = "SYSTEM";
			}

			List<EwbVsItc04InitiateReconReportRequestStatusDto> recResponse = reportStatusService
					.getReportRequestData(reqDto, userName);

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(recResponse);
			resps.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resps.add("resp", respBody);
			return new ResponseEntity<>(resps.toString(), HttpStatus.OK);

		} catch (JsonParseException ex) {
			msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);

			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/ui/ewbvsItc04DownloadIdWise", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> ewbVsItc04DownloadIdWise(
			@RequestBody String jsonString) {

		LOGGER.debug("Inside EwbVsItc04ReconRequestStatusController"
				+ ".ewbVsItc04DownloadIdWise() method and file type is {} ");

		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			JsonObject json = requestObject.get("req").getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();

			String configId = json.get("configId").getAsString();

			List<Gstr2RequesIdWiseDownloadTabDto> resp = service
					.getDownloadData(Long.valueOf(configId));
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

	@GetMapping(value = "/ui/eWBVsItc04DownloadDocument")
	public void eWBVsItc04DownloadDocument(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		LOGGER.debug(
				"inside eWBVsItc04DownloadDocument method and file type is {} ",
				"EwbVsItc04ReconReport");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String configId = request.getParameter("configId");
		String reportType = request.getParameter("reportType");
		

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					" Downloading report with configId  %s"
							+ " and Report Type  %s and request is %s",
					configId, reportType, request);
			LOGGER.debug(msg);
		}

		Optional<EwbvsItc04ReconDownloadReportsEntity> configEntity = reconDownlRepo
				.findByConfigIdAndReportType(Long.valueOf(configId),
						reportType);

		if (!configEntity.isPresent()) {
			String msg = "No records found";
			throw new AppException(msg);
		}
		String fileName = configEntity.get().getPath();

		String fileFolder = "EwbVsItc04ReconReport";
		Document document = null;
		String docId = configEntity.get().getDocId();
		if (docId != null && !docId.isEmpty()) {
			 
			String msg = String.format(
					"Doc Id is available for File Name %s and Doc Id %s",
					fileName, docId);
			LOGGER.debug(msg);
			document = DocumentUtility
					.downloadDocumentByDocId(docId);

		} else {

			document = DocumentUtility.downloadDocument(fileName,
					fileFolder);
			if (fileName == null ) {
				throw new AppException("FileName is null");

			}
		}


		/*Document document = DocumentUtility.downloadDocument(fileName,
				fileFolder);*/

		
		
		if (document == null) {
			return;
		}

		InputStream inputStream = document.getContentStream().getStream();
		int read = 0;
		byte[] bytes = new byte[1024];

		if (document != null) {
			response.setHeader("Content-Disposition",
					String.format("attachment; filename = " + fileName));
			OutputStream outputStream = response.getOutputStream();
			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		}

	}

}
