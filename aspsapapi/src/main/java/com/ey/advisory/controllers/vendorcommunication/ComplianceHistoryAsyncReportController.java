/**
 * 
 */
package com.ey.advisory.controllers.vendorcommunication;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.repositories.client.asprecon.ReturnComplianceRequestRepository;
import com.ey.advisory.app.data.returns.compliance.service.ComplianceHistoryReportComService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 */

@RestController
@Slf4j
public class ComplianceHistoryAsyncReportController {

	@Autowired
	@Qualifier("ComplaintHistoryReportComServiceImpl")
	private ComplianceHistoryReportComService complianceHistoryReportComService;

	@Autowired
	private ReturnComplianceRequestRepository returnComplianceRequestRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private AsyncJobsService persistenceMngr;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	private Environment env;

	@PostMapping(value = "/ui/generatecomplianceReq", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateNonCompVendorReq(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"generatecompliance Request received from UI as {} ",
						jsonString);
			}

			JsonObject reqObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonObject reqJson = reqObject.get("req").getAsJsonObject();

			Gstr2aProcessedDataRecordsReqDto CompReportDto = gson
					.fromJson(reqJson, Gstr2aProcessedDataRecordsReqDto.class);
			String finYear = CompReportDto.getFinancialYear();
			String entityId = CompReportDto.getEntity();
			String returnType = CompReportDto.getReturnType();
			Map<String, List<String>> dataSecAttrs = CompReportDto
					.getDataSecAttrs();

			List<String> gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);

			Long requestID = complianceHistoryReportComService
					.createEntryComplainceComReq(Long.valueOf(gstinList.size()),
							finYear, Long.valueOf(entityId));

			if (gstinList.size() > 0) {
				for (String gstin : gstinList) {
					complianceHistoryReportComService
							.createEntryCompClientGstin(requestID, gstin,
									returnType);
				}
			}

			generateComplianceReportUploadAsync(requestID);

			LOGGER.info(
					"Compliance History Report Request has been submitted & RequestId is {}",
					requestID);

			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Compliance History Report Request has been submitted & RequestId is "
									+ requestID));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			LOGGER.error("Message", ex.getMessage());
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp",
					gson.toJsonTree(
							"Exception on Compliance History  Report Request "
									+ ex.getMessage()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}

	}

	@GetMapping(value = "/ui/downloadcomplianceReport")
	public void downloadCompReportUI(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long requestId = Long.valueOf(request.getParameter("requestId"));

		downloadcompReport(response, requestId);

	}

	private void downloadcompReport(HttpServletResponse response,
			Long requestId) throws Exception {
		LOGGER.debug("inside Async Report file Downloads");

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		String fileName = returnComplianceRequestRepository
				.getFilePath(requestId);

		String fileFolder = "ComplianceHistoryReport";

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Downloading Document with fileName : %s and Folder Name: %s",
					fileName, fileFolder);
			LOGGER.debug(msg);
		}
		Document document = DocumentUtility.downloadDocument(fileName,
				fileFolder);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Document to download : %s", document);
			LOGGER.debug(msg);
		}
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

	public void generateComplianceReportUploadAsync(Long requestId) {

		JsonObject jsonParams = new JsonObject();
		List<AsyncExecJob> CompReportAsync = new ArrayList<>();

		jsonParams.addProperty("id", requestId);
		CompReportAsync.add(asyncJobsService.createJob(
				TenantContext.getTenantId(), JobConstants.COMP_HISTORY_REPORT,
				jsonParams.toString(), "SYSTEM", 1L, null, null));

		if (!CompReportAsync.isEmpty()) {
			persistenceMngr.createJobs(CompReportAsync);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					" Complaint history report upload Job has been submitted for requestId ",
					requestId);
		}

	}
}
