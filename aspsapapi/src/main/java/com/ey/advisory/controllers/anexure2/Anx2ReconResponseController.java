package com.ey.advisory.controllers.anexure2;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.anx2.initiaterecon.Anx2ReconResponseDTO;
import com.ey.advisory.app.anx2.initiaterecon.Anx2ReconResponseService;
import com.ey.advisory.app.docs.dto.Anx2ReconResponseReqDTO;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.BasicCommonSecParam;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Anx2ReconResponseController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2ReconResponseController.class);

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("Anx2ReconResponseServiceImpl")
	Anx2ReconResponseService anx2ReconResponseService;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	Gstr1FileStatusRepository fileStatusRepository;
	
	@Autowired
	@Qualifier("BasicCommonSecParam")
	private BasicCommonSecParam basicCommonSecParam;

	@PostMapping(value = "/ui/anx2UserRespFileUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> anx2UserRespFileUpload(
			@RequestParam("file") MultipartFile file) throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			User user = SecurityContext.getUser();
			String userName = user.getUserPrincipalName();
			LOGGER.debug(
					"inside ReconUserResponseUploads and file type is {}  "
							+ "foldername is {} ",
					"Anx2UserResponse",
					ConfigConstants.ANX2USERRESPONSEUPLOADS);
			LoadOptions options = new LoadOptions(FileFormatType.CSV);
			CommonUtility.setAsposeLicense();
			Workbook workbook = new Workbook(file.getInputStream(), options);
			workbook.setFileName(file.getOriginalFilename());
			String groupCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", groupCode);
			String uniqueFileName = DocumentUtility.uploadDocument(workbook,
					ConfigConstants.ANX2USERRESPONSEUPLOADS, "CSV");

			Gstr1FileStatusEntity fileStatus = new Gstr1FileStatusEntity();

			LocalDateTime localDate = LocalDateTime.now();
			fileStatus.setFileName(uniqueFileName);
			fileStatus.setFileType("Recon Response");
			fileStatus.setUpdatedBy(userName);
			fileStatus.setUpdatedOn(localDate);
			fileStatus.setFileStatus(JobStatusConstants.UPLOADED);
			fileStatus.setReceivedDate(localDate.toLocalDate());
			fileStatus.setSource(JobStatusConstants.WEB_UPLOAD);
			fileStatus.setDataType("Inward");
			fileStatus = fileStatusRepository.save(fileStatus);

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("fileId", fileStatus.getId());
			jsonParams.addProperty("fileName", uniqueFileName);
			jsonParams.addProperty("folderName",
					ConfigConstants.ANX2USERRESPONSEUPLOADS);

			asyncJobsService.createJob(groupCode, JobConstants.UPLOAD_RESPONSE,
					jsonParams.toString(), userName, 1L, null, null);

			APIRespDto dto = new APIRespDto("Sucess",
					"File has been successfully uploaded. "
							+ "Please check the file details in File Status");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Begining from uploadDocuments:{} ");
			}
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"File uploaded Failed" + e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while uploading  files";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/ui/getReconRespDetails", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getReconRespDetails(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject req = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			Anx2ReconResponseReqDTO dto = gson.fromJson(req,
					Anx2ReconResponseReqDTO.class);
			
			Annexure1SummaryReqDto dataSec = new 
					Annexure1SummaryReqDto();
			List<Long> entityList = new ArrayList<>();
			entityList.add(Long.valueOf(dto.getEntityId()));
			dataSec.setEntityId(entityList);
			dataSec.setTaxPeriod(dto.getTaxPeriod());
			
			dataSec = basicCommonSecParam
					.setInwardSumDataSecuritySearchParams(dataSec);

			List<String> gstins =( dto.getGstin() == null || 
					dto.getGstin().isEmpty()) ? dataSec.getDataSecAttrs().
					get(OnboardingConstant.GSTIN):dto.getGstin();
			
			
			if (gstins == null || gstins.isEmpty()) {
				gstins = entityService
						.getGSTINsForEntity(Long.valueOf(dto.getEntityId()));
			}

			Anx2ReconResponseDTO response = anx2ReconResponseService
					.getReconResponse(gstins, dto.getTaxPeriod(),
							dto.getTableType(), dto.getDocType());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Anx2ReconResponseController getReconRespDetails");
			}

			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(response);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@PostMapping(value = "/ui/generateErrorReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateErrorReport(
			@RequestBody String jsonString) {
		JsonObject errorResp = new JsonObject();
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();

			JsonObject req = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			String fileName = req.get("fileName").getAsString();
			String errorFolder = ConfigConstants.ERROR_UPLOAD_FOLDER_NAME;
			DocumentUtility.downloadDocument(fileName, errorFolder);
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree("success");
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.CREATED);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(errorResp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@GetMapping(value = "/ui/generateReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void generateErrorReport(HttpServletRequest request,
			HttpServletResponse response) {
		JsonObject errorResp = new JsonObject();
		try {

			Long fileId = Long.valueOf(request.getParameter("fileId"));
			String fileType = request.getParameter("fileType");
			String folderName = "";
			String fileName = "";
			Gstr1FileStatusEntity fileEntity = fileStatusRepository
					.findById(fileId)
					.orElseThrow(() -> new AppException(
							"No entry in file status table with"
									+ " Corresponding file"));
			if (fileType.equalsIgnoreCase("Error")) {
				folderName = ConfigConstants.ERROR_UPLOAD_FOLDER_NAME;
				fileName = fileEntity.getErrorFileName();
			} else if (fileType.equalsIgnoreCase("Total")) {
				folderName = ConfigConstants.ANX2USERRESPONSEUPLOADS;
				fileName = fileEntity.getFileName();
			} else
				throw new AppException("Wrong fileType Provided");
			Document document = DocumentUtility.downloadDocument(fileName,
					folderName);

			if (document == null) {
				throw new AppException("Error Occured");
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Recon User Response download from Folder Name : %s , "
								+ "file name : %s , file type : %s",
						folderName, fileName, fileType);
				LOGGER.debug(msg);
			}

			Workbook workbook = null;
			if (fileType.equalsIgnoreCase("Error")) {

				LOGGER.debug("Downloading recon response erro file");

				InputStream inputStream = document.getContentStream()
						.getStream();
				workbook = new Workbook(inputStream);
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = "
								+ fileEntity.getErrorFileName()));
			} else {

				LOGGER.debug("Downloading recon response user upload file");

				InputStream inputStream = document.getContentStream()
						.getStream();
				LoadOptions options = new LoadOptions(FileFormatType.CSV);
				CommonUtility.setAsposeLicense();
				workbook = new Workbook(inputStream, options);
				response.setHeader("Content-Disposition", String.format(
						"attachment; filename = " + fileEntity.getFileName()));
			}

			response.setContentType("APPLICATION/OCTET-STREAM");
			workbook.save(response.getOutputStream(), SaveFormat.CSV);

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			errorResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));

		}
	}
}
