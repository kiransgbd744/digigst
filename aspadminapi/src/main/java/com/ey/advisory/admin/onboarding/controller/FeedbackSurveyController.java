package com.ey.advisory.admin.onboarding.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.services.feedback.FDSurveyRespDto;
import com.ey.advisory.app.services.feedback.FeedbackReportService;
import com.ey.advisory.app.services.feedback.FeedbackReqReportDto;
import com.ey.advisory.app.services.feedback.FeedbackSurveyService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.FeedbackUserConfigPrmtEntity;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.FeedbackUserConfigPrmtRepository;
import com.ey.advisory.core.async.repositories.master.GroupRepository;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@RestController
@Slf4j
public class FeedbackSurveyController {

	@Autowired
	FeedbackUserConfigPrmtRepository userPrmtRepo;

	@Autowired
	@Qualifier("FeedbackReportServiceImpl")
	private FeedbackReportService feedbackReportService;

	@Autowired
	@Qualifier("FeedbackSurveyServiceServiceImpl")
	private FeedbackSurveyService feedbackService;

	@Autowired
	private GroupRepository groupRepository;

	@GetMapping(value = "/ui/SurveyUserSuggDocument")
	public void fileDownloads(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			LOGGER.debug("inside Async Report file Downloads");

			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			String id = request.getParameter("confgPrmtId");
			Optional<FeedbackUserConfigPrmtEntity> entity = userPrmtRepo
					.findByIdAndIsDeleteFalse(Long.valueOf(id));
			String fileFolder = null;
			String fileName = null;
			if (entity.isPresent()) {
				fileName = entity.get().getFilePath();
				fileFolder = "FeedbackSurveyFiles";
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Downloading Document with fileName : %s and Folder Name: %s",
							fileName, fileFolder);
					LOGGER.debug(msg);
				}
			}
			if (fileName == null) {
				throw new AppException("FileName is null");
			}
			Document document = DocumentUtility.downloadDocument(fileName,
					fileFolder);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Document to download : %s",
						document);
				LOGGER.debug(msg);
			}

			if (document == null) {
				LOGGER.error("document is empty");
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

		} catch (Exception ex) {
			String msg = "Exception while downloading files ";
			LOGGER.error(msg, ex);
		}

	}

	@GetMapping(value = "/api/SurveyUserSuggDocument")
	public void apifileDownloads(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			LOGGER.debug("inside Async Report file Downloads");

			String tenantCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", tenantCode);
			String id = request.getParameter("confgPrmtId");
			Optional<FeedbackUserConfigPrmtEntity> entity = userPrmtRepo
					.findByIdAndIsDeleteFalse(Long.valueOf(id));
			String fileFolder = null;
			String fileName = null;
			if (entity.isPresent()) {
				fileName = entity.get().getFilePath();
				fileFolder = "FeedbackSurveyFiles";
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Downloading Document with fileName : %s and Folder Name: %s",
							fileName, fileFolder);
					LOGGER.debug(msg);
				}
			}
			if (fileName == null) {
				throw new AppException("FileName is null");
			}
			Document document = DocumentUtility.downloadDocument(fileName,
					fileFolder);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Document to download : %s",
						document);
				LOGGER.debug(msg);
			}

			if (document == null) {
				LOGGER.error("document is empty");
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

		} catch (Exception ex) {
			String msg = "Exception while downloading files ";
			LOGGER.error(msg, ex);
		}

	}

	@PostMapping(value = "/ui/getAllUserFeedbackSvyDtls", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getAllUserFeedbackSvyDtls(
			@RequestBody String jsonString) throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject resp = new JsonObject();
		FDSurveyRespDto respDto = new FDSurveyRespDto();
		JsonObject surveyResp = new JsonObject();
		try {
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			String groupCode = requestObject.get("groupCode").getAsString();
			List<FDSurveyRespDto> surveyDtls = feedbackService
					.getAllUserFeedbackSvyDtls(groupCode);
			JsonElement surveyDetails = gson.toJsonTree(surveyDtls);
			surveyResp.add("surveyDtls", surveyDetails);

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", surveyResp);
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

	@PostMapping(value = "/ui/downloadFeedbackSvyDtls", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public void downloadFeedbackReport(@RequestBody String jsonString,
			HttpServletResponse response) {

		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		JsonObject json = requestObject.get("req").getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();

		try {
			String fileName = null;
			Workbook workbook = null;
			FeedbackReqReportDto criteria = gson.fromJson(json,
					FeedbackReqReportDto.class);

			workbook = feedbackReportService.findReportDownload(criteria);
			String groupCode = TenantContext.getTenantId();
			Group group = groupRepository
					.findByGroupCodeAndIsActiveTrue(groupCode);
			String groupName = group.getGroupName();
			fileName = groupName + "_" + groupCode + "_VoC_Report";

			if (workbook == null) {
				workbook = new Workbook();
			}
			if (fileName != null) {
				response.setContentType("APPLICATION/OCTET-STREAM");
				response.setHeader("Content-Disposition", String
						.format("attachment; filename=" + fileName + ".xlsx"));
				workbook.save(response.getOutputStream(), SaveFormat.XLSX);
				response.getOutputStream().flush();
			}
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving "
					+ "Data from Report ";
			LOGGER.error(msg, ex);
			JsonObject resp = new JsonObject();
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
		}
	}
}
