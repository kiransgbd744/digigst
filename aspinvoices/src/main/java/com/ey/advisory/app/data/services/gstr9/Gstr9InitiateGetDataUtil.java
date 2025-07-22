package com.ey.advisory.app.data.services.gstr9;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr9.Gstr9GetSummaryEntity;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9GetSummaryRepository;
import com.ey.advisory.app.data.services.common.GstnCommonServiceUtil;
import com.ey.advisory.app.docs.dto.gstr9.GetDetailsForGstr9ReqDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DashboardCommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GSTR9Constants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("Gstr9InitiateGetDataUtil")
public class Gstr9InitiateGetDataUtil {

	private static final String RETSUM = "RETSUM";

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr9EntityLevelServiceImpl")
	private Gstr9EntityLevelService gstr9EntityLevelService;

	@Autowired
	GstnCommonServiceUtil gstnCommonUtil;

	@Autowired
	Gstr9PopulateTblDataUtil gstr9PopTblDataUtil;

	@Autowired
	@Qualifier("Gstr9GetSummaryRepository")
	private Gstr9GetSummaryRepository gstr9GetSummaryRepo;

	@Autowired
	@Qualifier("GstinGetStatusRepository")
	GstinGetStatusRepository gstinGetStatusRepo;

	public APIResponse getGstnCall(String gstin, String fy, String taxPeriod) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";
		try {

			gstnCommonUtil.saveOrUpdateGstnStatus(gstin, taxPeriod, "RETSUM",
					GSTR9Constants.GSTR9);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside getGstnCall for Gstin {} and Fy {} ",
						gstin, fy);
			}
			Integer derviedTaxPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);
			APIResponse apiResponse = gstr9EntityLevelService
					.getGstr9Details(gstin, taxPeriod);

			if (apiResponse.isSuccess()) {

				String getGstnData = apiResponse.getResponse();

				String groupCode = TenantContext.getTenantId();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("After invoking GetUpdated Gstin"
							+ ".getGstr9Details() method " + "json response : "
							+ apiResponse);
				}

				// upload json file
				uploadJsonFileToRepo(getGstnData, gstin, taxPeriod);

				// check gstn user request table
				gstnCommonUtil.saveOrUpdateGstnUserRequest(gstin, taxPeriod,
						getGstnData, GSTR9Constants.GSTR9);

				List<Gstr9GetSummaryEntity> listOfGetSummaryEntities = new ArrayList<>();
				GetDetailsForGstr9ReqDto gstr9GetReqDto = gson.fromJson(
						apiResponse.getResponse(),
						GetDetailsForGstr9ReqDto.class);

				// Below Method will Populate the Data from Gstr9 Get API and
				// Create a List of Entities
				gstr9PopTblDataUtil.populateTbl4Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl5Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl6Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl7Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl8Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl9Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl10Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl14Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl15Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl16Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl17Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				gstr9PopTblDataUtil.populateTbl18Data(gstr9GetReqDto, gstin, fy,
						taxPeriod, derviedTaxPeriod, listOfGetSummaryEntities,
						userName);
				if (listOfGetSummaryEntities.isEmpty()) {
					String msg = "Not Able to Update Gstin Data";
					throw new AppException(msg);
				}

				int updatedRecords = gstr9GetSummaryRepo
						.updateActiveExistingRecords(gstin, taxPeriod,
								userName);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							" Updated Existing Active Records {} for"
									+ " Gstin  {}  and Fy {} in Gstr9GetSummaryEntity",
							updatedRecords, gstin, fy);
				}
				gstr9GetSummaryRepo.saveAll(listOfGetSummaryEntities);

				JsonObject jobParams = new JsonObject();
				jobParams.addProperty("gstin", gstin);
				jobParams.addProperty("taxPeriod", taxPeriod);
				jobParams.addProperty("returnType", GSTR9Constants.GSTR9);
				jobParams.addProperty("invoiceType", RETSUM);

				gstinGetStatusRepo.updateGetGstnStatus(false, "SUCCESS",
						LocalDateTime.now(), null, gstin, taxPeriod,
						GSTR9Constants.GSTR9, RETSUM);

				// creating job to convert json to csv
				asyncJobsService.createJob(groupCode,
						JobConstants.JSON_CSV_CONVERTOR, jobParams.toString(),
						userName, 1L, null, null);

			} else {
				String errMsg = String.format(
						"GSTN has Returned Error Response %s for Gstin %s and Fy %s",
						apiResponse.getError().getErrorDesc(), gstin, fy);
				gstinGetStatusRepo.updateGetGstnStatus(false, "FAILED",
						LocalDateTime.now(),
						apiResponse.getError().getErrorDesc(), gstin, taxPeriod,
						GSTR9Constants.GSTR9, RETSUM);
				LOGGER.error(errMsg);
			}

			return apiResponse;

		} catch (Exception e) {
			String msg = String.format(
					"Exception occured in Gstr9 , while Calling the "
							+ "get api response for Gstin %s and Fy %s",
					gstin, fy);
			LOGGER.error(msg, e);
			gstinGetStatusRepo.updateGetGstnStatus(false, "FAILED",
					LocalDateTime.now(), e.getMessage(), gstin, taxPeriod,
					GSTR9Constants.GSTR9, "RETSUM");
			throw new AppException(e.getMessage(), e);
		}

	}

	private void uploadJsonFileToRepo(String jsonResp, String gstin,
			String taxPeriod) throws IOException {

		String fileName = GSTR9Constants.GSTR9 + "_" + gstin + "_" + taxPeriod
				+ "_" + RETSUM + "_0.json";

		File tempDir = Files.createTempDirectory("GSTR9JsonReports").toFile();

		String filePath = tempDir.getAbsolutePath() + File.separator + fileName;

		try (FileWriter writer = new FileWriter(filePath);
				BufferedWriter bw = new BufferedWriter(writer)) {
			bw.write(jsonResp);
			bw.flush();

			String folderName = DashboardCommonUtility.getDashboardFolderName(
					GSTR9Constants.GSTR9, JobStatusConstants.JSON_FILE);

			DocumentUtility.uploadFileWithFileName(new File(filePath),
					folderName, fileName);

		} catch (Exception e) {

			String msg = "Error occured while uploading json file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		} finally {
			GenUtil.deleteTempDir(tempDir);
		}

	}

}
