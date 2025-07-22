package com.ey.advisory.controller.gstr2b;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.lang3.StringUtils;
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

import com.ey.advisory.app.data.entities.client.Gstr2BGenerateReportTypeEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2BGenerateReportTypeRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bGet2bRequestStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bGetCallRequestStatusRepository;
import com.ey.advisory.app.gstr2b.Gstr2bGet2bRequestStatusEntity;
import com.ey.advisory.app.gstr2b.Gstr2bReportRequestEntity;
import com.ey.advisory.app.services.search.docsearch.Gstr2BGenerateReportFileIdWiseService;
import com.ey.advisory.app.services.search.docsearch.Gstr2BReportDownloadDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.InitiateGetCallDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */
@RestController
@Slf4j
public class Gstr2BGetReportsController {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2bGetCallRequestStatusRepository")
	Gstr2bGetCallRequestStatusRepository configRepo;

	@Autowired
	@Qualifier("Gstr2bGet2bRequestStatusRepository")
	Gstr2bGet2bRequestStatusRepository requestRepo;

	@Autowired
	@Qualifier("Gstr2BGenerateReportFileIdWiseServiceImpl")
	private Gstr2BGenerateReportFileIdWiseService service;
	
	@Autowired
	private Gstr2BGenerateReportTypeRepository reportTypeRepo;

	@RequestMapping(value = "/ui/generateGstr2bDownloadIdWise", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getdownloadDataforRecon(
			@RequestBody String jsonString) {

		LOGGER.debug("Inside Gstr2BGenerateReportFileIdWiseController"
				+ ".reportFileDownloadIdWise() method and file type is {} ");

		try {
			JsonObject reqObj = (new JsonParser()).parse(jsonString)
					.getAsJsonObject().getAsJsonObject();

			JsonObject reqObject = reqObj.get("req").getAsJsonObject();
			Gson gson = new Gson();
			Gstr2BReportDownloadDto reqDto = gson.fromJson(reqObject,
					Gstr2BReportDownloadDto.class);

			List<Gstr2BReportDownloadDto> respList = service
					.getDownloadData(reqDto.getConfigId());

			JsonObject resps = new JsonObject();
			JsonElement respBody = gson.toJsonTree(respList);
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

	@PostMapping(value = "/ui/getGstr2BReports", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getGstr2BReports(
			@RequestBody String jsonString) {

		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		JsonObject errorResp = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String status = "SUBMITTED";
		try {
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			JsonObject requestObject = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			String groupCode = TenantContext.getTenantId();

			Gstr1GetInvoicesReqDto dto = gson.fromJson(requestObject.toString(),
					Gstr1GetInvoicesReqDto.class);
			List<InitiateGetCallDto> gstinTaxPeriodList = dto
					.getGstinTaxPeiordList();

			Long configId = generateCustomId(entityManager);
			List<Gstr2bReportRequestEntity> requestEntityList = new ArrayList<>();

			if (dto.getFinYear() != null) {
				// String fy = dto.getFinYear();

				gstinTaxPeriodList.forEach(o -> {
					o.getTaxPeriodList().forEach(tp -> {
						Gstr2bReportRequestEntity entity = new Gstr2bReportRequestEntity();
						entity.setGstin(o.getGstin());
						entity.setTaxPeriod(getRetPeriod(tp, dto.getFinYear()));
						entity.setReportType(dto.getReportType());
						entity.setReportDownloadId(configId);
						entity.setIsActive(true);
						entity.setCreatedBy(userName);
						entity.setCreatedOn(EYDateUtil
								.toUTCDateTimeFromLocal(LocalDateTime.now()));
						entity.setCompletedOn(null);

						requestEntityList.add(entity);
					});
				});
			}
			configRepo.saveAll(requestEntityList);

			Gstr2bGet2bRequestStatusEntity requestStatusEntity = new Gstr2bGet2bRequestStatusEntity();
			requestStatusEntity.setReportType(dto.getReportType());
			requestStatusEntity.setCreatedBy(userName);
			requestStatusEntity.setCreatedDate(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			requestStatusEntity.setReqId(configId);
			requestStatusEntity.setGstinCount((long) gstinTaxPeriodList.size());
			requestStatusEntity.setReportType(dto.getReportType());
			requestStatusEntity.setStatus(status);
			requestRepo.save(requestStatusEntity);

			jobParams.addProperty("id", configId);
			jobParams.addProperty("reportType", dto.getReportType());

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2B_GET_ALL_REPORT, jobParams.toString(),
					userName, 1L, null, null);

			LOGGER.debug("GET2B Get, posted Report Job - {}", groupCode);

			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (AppException ex) {
			String msg = ex.getMessage();
			LOGGER.error(ex.getMessage(), ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String currentDate = currentYear + (currentMonth < 10
				? ("0" + currentMonth) : String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval " + "FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	String getRetPeriod(String month, String finYear) {
		if (!StringUtils.isEmpty(month)) {
			if (month.equals("01") || month.equals("02")
					|| month.equals("03")) {
				month = month + finYear.substring(0, 2)
						+ finYear.substring(5, 7);
			} else {
				month = month + finYear.substring(0, 4);

			}

		}
		return month;
	}
	
	@GetMapping(value = "/ui/downloadGstr2BReport")
	public void downloadAutoReconReportUI(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Long requestId = Long.valueOf(request.getParameter("requestId"));

		downloadGstr2BReport(response, requestId);

	}

	private void downloadGstr2BReport(HttpServletResponse response,
			Long requestId) throws Exception {

		String tenantCode = TenantContext.getTenantId();
		LOGGER.debug("Tenant Id Is {}", tenantCode);

		Document document = null;
		String fileName = null;
		InputStream inputStream = null;
		// foldername
		String fileFolder = "Gstr2bGetReport";

		Optional<Gstr2BGenerateReportTypeEntity> chileEntity = reportTypeRepo
				.findById(requestId);

		if (chileEntity.isPresent()) {
			String docId = chileEntity.get().getDocId();
			fileName = chileEntity.get().getFilePath();

			LOGGER.debug("Inside doc id block ");
			document = DocumentUtility.downloadDocumentByDocId(docId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Document to download : %s",
						document);
				LOGGER.debug(msg);
			}
			if (document == null) {
				return;
			}
		} else {

			Gstr2bGet2bRequestStatusEntity entity = requestRepo
					.findByReqId(requestId);

			fileName = entity.getFilePath();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Downloading Document with fileName : %s and Folder Name: %s",
						fileName, fileFolder);
				LOGGER.debug(msg);
			}

			String docId = entity.getDocId();

			if (Strings.isNullOrEmpty(docId)) {
				document = DocumentUtility.downloadDocument(fileName,
						fileFolder);
			} else {
				LOGGER.debug("Inside doc id block ");
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Document to download : %s",
						document);
				LOGGER.debug(msg);
			}
			if (document == null) {
				return;
			}
		}
		if (document != null) {
			inputStream = document.getContentStream().getStream();
		}
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
