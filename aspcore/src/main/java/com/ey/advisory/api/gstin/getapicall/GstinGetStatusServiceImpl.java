package com.ey.advisory.api.gstin.getapicall;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.ZipGenStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DashboardCommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.GSTNAPIUtil;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.GstinGetStatusService;
import com.ey.advisory.gstnapi.SuccessResult;
import com.ey.advisory.gstnapi.repositories.client.APIInvocationReqRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("GstinGetStatusServiceImpl")
@Slf4j
public class GstinGetStatusServiceImpl implements GstinGetStatusService {

	@Autowired
	@Qualifier("APIInvocationReqRepository")
	APIInvocationReqRepository apiInvocationReqRepo;

	@Autowired
	@Qualifier("GstinGetStatusRepository")
	GstinGetStatusRepository gstinGetStatusRepo;

	@Autowired
	ZipGenStatusRepository zipGenStatusRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final ImmutableList<String> DATA_DELETABLE_RETURNS = ImmutableList
			.of("GSTR1", "GSTR7", "GSTR6", "ITC04", "GSTR8","GSTR1A");

	@Override
	public void saveOrUpdateGSTNGetStatus(String apiParams, String status,
			String errorDesc) {

		JsonObject inpJson = (new JsonParser()).parse(apiParams)
				.getAsJsonObject();

		String returnTypeandSection = inpJson.get("id").getAsString();

		String section = null;
		String returnType = null;

		if (GSTNAPIUtil.gstr1GetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR1";

			section = GSTNAPIUtil.gstr1GetAPI.get(returnTypeandSection);

		} else if (GSTNAPIUtil.gstr2AGetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR2A";

			section = GSTNAPIUtil.gstr2AGetAPI.get(returnTypeandSection);
		} else if (GSTNAPIUtil.gstr2XGetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR2X";

			section = GSTNAPIUtil.gstr2XGetAPI.get(returnTypeandSection);
		} else if (GSTNAPIUtil.itc04GetAPI.containsKey(returnTypeandSection)) {

			returnType = "ITC04";

			section = GSTNAPIUtil.itc04GetAPI.get(returnTypeandSection);

		} else if (GSTNAPIUtil.gstr7GetAPI.containsKey(returnTypeandSection)) {
			LOGGER.debug("Inside GstinGetStatusServiceImpl for Return Type {}",
					returnTypeandSection);

			returnType = "GSTR7";

			section = GSTNAPIUtil.gstr7GetAPI.get(returnTypeandSection);

		} else if (GSTNAPIUtil.gstr6GetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR6";
			section = GSTNAPIUtil.gstr6GetAPI.get(returnTypeandSection);
		} else if (GSTNAPIUtil.gstr8GetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR8";

			section = GSTNAPIUtil.gstr8GetAPI.get(returnTypeandSection);

		}else if (GSTNAPIUtil.gstr1AGetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR1A";

			section = GSTNAPIUtil.gstr1AGetAPI.get(returnTypeandSection);

		}  
		
		else {
			String errMsg = String.format(
					"currently the given returnType and section %s is not supported, "
							+ "Hence GET status is not saved/updated",
					returnTypeandSection);
			LOGGER.error(errMsg);
			return;

		}

		String gstin = inpJson.getAsJsonObject("params").get("gstin:H")
				.getAsString();

		String taxPeriod = inpJson.getAsJsonObject("params").get("ret_period:H")
				.getAsString();

		GstnGetStatusEntity gstnGetApiStatus = gstinGetStatusRepo
				.findByGstinAndTaxPeriodAndReturnTypeAndSection(gstin,
						taxPeriod, returnType, section);

		Integer derivedTaxPeriod = Integer.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));

		if (gstnGetApiStatus == null) {
			gstnGetApiStatus = new GstnGetStatusEntity();
			gstnGetApiStatus.setGstin(gstin);
			gstnGetApiStatus.setReturnType(returnType);
			gstnGetApiStatus.setTaxPeriod(taxPeriod);
			gstnGetApiStatus.setSection(section);
			gstnGetApiStatus.setCsvGenerationFlag(false);
			gstnGetApiStatus.setDerivedTaxPeriod(derivedTaxPeriod);
			gstnGetApiStatus.setCreatedOn(LocalDateTime.now());

		} else {
			gstnGetApiStatus.setUpdatedOn(LocalDateTime.now());
			zipGenStatusRepo.updateGstr9ZipFilePathIfNotNull(returnType, gstin,
					taxPeriod, null, LocalDateTime.now(), null);

		}

		if (APIConstants.SUCCESS_WITH_NO_DATA.equalsIgnoreCase(status)) {
			gstnGetApiStatus.setCsvGenerationFlag(true);
			gstnGetApiStatus.setErrorDescription(errorDesc);
			gstnGetApiStatus.setIsdbLoad(true);
			deleteExistingCSVFileFromRepo(returnType, section, taxPeriod,
					gstin);
		} else {
			gstnGetApiStatus.setErrorDescription(errorDesc);
			gstnGetApiStatus.setCsvGenerationFlag(false);
			gstnGetApiStatus.setIsdbLoad(false);
		}

		gstnGetApiStatus.setStatus(status);
		gstinGetStatusRepo.save(gstnGetApiStatus);
	}

	@Override
	public void uploadJsonFileToRepoForGSTN(SuccessResult result,
			String apiParams) {

		List<Long> resultIds = result.getSuccessIds();

		JsonObject inpJson = JsonParser.parseString(apiParams)
				.getAsJsonObject();

		String gstin = inpJson.getAsJsonObject("apiParams")
				.getAsJsonObject("params").get("gstin:H").getAsString();

		String taxPeriod = inpJson.getAsJsonObject("apiParams")
				.getAsJsonObject("params").get("ret_period:H").getAsString();

		String returnTypeandSection = inpJson.getAsJsonObject("apiParams")
				.get("id").getAsString();

		String section = null;
		String returnType = null;

		if (GSTNAPIUtil.gstr1GetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR1";

			section = GSTNAPIUtil.gstr1GetAPI.get(returnTypeandSection);

		} else if (GSTNAPIUtil.itc04GetAPI.containsKey(returnTypeandSection)) {

			returnType = "ITC04";

			section = GSTNAPIUtil.itc04GetAPI.get(returnTypeandSection);

		} else if (GSTNAPIUtil.gstr2XGetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR2X";

			section = GSTNAPIUtil.gstr2XGetAPI.get(returnTypeandSection);

		} else if (GSTNAPIUtil.gstr7GetAPI.containsKey(returnTypeandSection)) {
			
			String type = inpJson.getAsJsonObject("ctxParams").get("type")
					.getAsString();
			returnType = "GSTR7";
			
			if (type.equalsIgnoreCase(APIConstants.TDS)) {
				section = GSTNAPIUtil.gstr7GetAPI
						.get(returnTypeandSection);
			} else {
				section = APIConstants.TRANSACTIONAL_DETAILS;
			}

		} else if (GSTNAPIUtil.gstr6GetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR6";
			section = GSTNAPIUtil.gstr6GetAPI.get(returnTypeandSection);
		} else if (GSTNAPIUtil.gstr8GetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR8";
			section = GSTNAPIUtil.gstr8GetAPI.get(returnTypeandSection);
		}else if (GSTNAPIUtil.gstr1AGetAPI.containsKey(returnTypeandSection)) {

			returnType = "GSTR1A";

			section = GSTNAPIUtil.gstr1AGetAPI.get(returnTypeandSection);

		}
		/*
		 * else if (GSTNAPIUtil.gstr2AGetAPI.containsKey(returnTypeandSection))
		 * {
		 * 
		 * returnType = "GSTR2A";
		 * 
		 * section = GSTNAPIUtil.gstr2AGetAPI.get(returnTypeandSection); }
		 */ else {
			String errMsg = String.format(
					"currently the given returnType and section %s is not supported,"
							+ " Hence skiping the json file uploaded",
					returnTypeandSection);
			LOGGER.error(errMsg);
			return;

		}

		uploadJsonFileToRepo(gstin, taxPeriod, returnType, section, resultIds);

		JsonObject jobParams = new JsonObject();
		jobParams.addProperty("gstin", gstin);
		jobParams.addProperty("taxPeriod", taxPeriod);
		jobParams.addProperty("returnType", returnType);
		jobParams.addProperty("invoiceType", section);

		asyncJobsService.createJob(TenantContext.getTenantId(),
				"ConvertJsonToCsv", jobParams.toString(), "SYSTEM", 1L, null,
				null);
	}

	private void uploadJsonFileToRepo(String gstin, String taxPeriod,
			String returnType, String section, List<Long> resultIds) {

		File tempDir = null;
		try {
			tempDir = Files.createTempDirectory("GSTRJsonReports").toFile();

			String folderName = DashboardCommonUtility.getDashboardFolderName(
					returnType, JobStatusConstants.JSON_FILE);

			for (int fileCount = 0; fileCount < resultIds.size(); fileCount++) {
				String apiResp = APIInvokerUtil
						.getResultById(resultIds.get(fileCount));

				String jsonFileName = returnType + "_" + gstin + "_" + taxPeriod
						+ "_" + section + "_" + fileCount + ".json";

				String filePath = tempDir.getAbsolutePath() + File.separator
						+ jsonFileName;

				try (FileWriter writer = new FileWriter(filePath);
						BufferedWriter bw = new BufferedWriter(writer)) {
					bw.write(apiResp);
					bw.flush();
					DocumentUtility.uploadFileWithFileName(new File(filePath),
							folderName, jsonFileName);

				} catch (Exception e) {

					String msg = String.format(
							"Exception occured while uploading json file %s",
							jsonFileName);
					LOGGER.error(msg, e);
					throw new AppException(msg);
				}

			}

			gstinGetStatusRepo.updateStatusAndFilePath(gstin, taxPeriod,
					returnType, section, APIConstants.SUCCESS,
					LocalDateTime.now());
		} catch (IOException ex) {
			String msg = "failed to create temporary directory";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}
	}

	private void deleteExistingCSVFileFromRepo(String returnType,
			String section, String taxPeriod, String gstin) {
		if (!DATA_DELETABLE_RETURNS.contains(returnType)) {
			return;
		}
		try {
			String folderName = DashboardCommonUtility.getDashboardFolderName(
					returnType, JobStatusConstants.CSV_FILE);

			String fileNameToBeDeleted = returnType + "_" + section + "_"
					+ taxPeriod + "_" + gstin + ".csv";
			DocumentUtility.deleteDocument(fileNameToBeDeleted, folderName);
		} catch (Exception e) {
			String message = "Exception occured while deleting document "
					+ "from csv folder";
			LOGGER.error(message);
		}
	}
}
