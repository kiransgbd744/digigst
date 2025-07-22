/**
 * 
 */
package com.ey.advisory.admin.onboarding.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.Workbook;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.EYRegularConfig;
import com.ey.advisory.core.async.repositories.master.EYConfigRepository;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigConstants;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@RestController
public class EinvoiceApplicabilityFileUploadController {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	EYConfigRepository eyConfigRepo;

	@PostMapping(value = "/einvoiceApplicableFileUpload", produces = {
			MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> einvoiceApplicableFileUpload(
			@RequestParam("file") MultipartFile file) throws Exception {
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside Einvoice Applicable file upload with file type is {}  "
								+ "foldername is {} ",
						"EinvApplicableFileUpload",
						ConfigConstants.EINV_APPLICABLE_FILEUPLOAD);
			}

			LoadOptions options = new LoadOptions(FileFormatType.XLSX);
			CommonUtility.setAsposeLicense();
			Workbook workbook = new Workbook(file.getInputStream(), options);
			workbook.setFileName(file.getOriginalFilename());

			String groupCode = TenantContext.getTenantId();
			LOGGER.debug("Tenant Id Is {}", groupCode);
			String uniqueFileName = DocumentUtility.uploadDocument(workbook,
					ConfigConstants.EINV_APPLICABLE_FILEUPLOAD, "XLSX");

			if (uniqueFileName == null) {
				throw new AppException(
						"Unexpected " + "error while uploading  file");
			}

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("fileName", uniqueFileName);
			jsonParams.addProperty("folderName",
					ConfigConstants.EINV_APPLICABLE_FILEUPLOAD);

			asyncJobsService.createJob(groupCode,
					JobConstants.EINV_APPLICABLE_FILE_UPLOAD,
					jsonParams.toString(), userName, 1L, null, null);

			APIRespDto dto = new APIRespDto("Sucess",
					"File has been successfully uploaded. ");
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

	@PostMapping(value = "/saveEinvoiceDate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> saveEinvoiceDate(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();

			String date = requestObject.get("req").getAsJsonObject().get("date")
					.getAsString();

			if (date == null || date.isEmpty()) {

				APIRespDto dto = new APIRespDto("Error", "Please select date ");
				JsonObject resp = new JsonObject();
				JsonElement respBody = gson.toJsonTree(dto);
				resp.add("hdr", new Gson()
						.toJsonTree(new APIRespDto("E", "Please select date")));
				resp.add("resp", respBody);
				return new ResponseEntity<String>(resp.toString(),
						HttpStatus.OK);
			}
			String categ = "EINVAPP";
			Map<String, Config> configMap = configManager
					.getConfigs(categ, "einv.applicable.date");

			if (configMap.get("einv.applicable.date") == null) {

				EYRegularConfig topEntity = eyConfigRepo
						.findFirstByOrderByIdDesc();
				EYRegularConfig entity = new EYRegularConfig();
				entity.setId(topEntity.getId() + 1);
				entity.setCategory(categ);
				entity.setGroupCode(ConfigConstants.DEFAULT_GROUP);
				entity.setKey("einv.applicable.date");
				entity.setValue(date);

				eyConfigRepo.save(entity);

			} else {
				eyConfigRepo.updateDateValue("einv.applicable.date", date);
			}

			APIRespDto dto = new APIRespDto("Sucess",
					"Date submitted successfully ");
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			APIRespDto dto = new APIRespDto("Failed",
					"Date submit Failed" + e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			String msg = "Unexpected error while saving date";
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

	@PostMapping(value = "/getEinvoiceDate", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getEinvoiceDate(
			@RequestBody String jsonString) {

		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String categ = "EINVAPP";

		try {

			EYRegularConfig entity = eyConfigRepo.findByCategoryAndKey(
					categ, "einv.applicable.date");

			JsonObject jobParams = new JsonObject();
			
			if(entity == null)
				jobParams.addProperty("date", "");
			else
				jobParams.addProperty("date", entity.getValue());
			
			
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = "Unexpected error while getting einvoice date";
			APIRespDto dto = new APIRespDto("Failed",
					msg + e.getMessage());
			JsonObject resp = new JsonObject();
			JsonElement respBody = gson.toJsonTree(dto);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.add("resp", respBody);
			LOGGER.error(msg);
			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		}

	}

}
