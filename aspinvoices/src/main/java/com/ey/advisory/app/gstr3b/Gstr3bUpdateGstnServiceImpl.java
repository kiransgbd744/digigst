package com.ey.advisory.app.gstr3b;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;
import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BMonthlyTrendTaxAmountRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DashboardCommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */
@Slf4j
@Component("Gstr3bUpdateGstnServiceImpl")
public class Gstr3bUpdateGstnServiceImpl implements Gstr3bUpdateGstnService {

	@Autowired
	private Gstr3BGstinDashboardService dashBoardService;

	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;

	@Autowired
	Gstr3BMonthlyTrendTaxAmountRepository gstr3bMonthlyTaxRepo;

	@Autowired
	@Qualifier("Gstr3bGstnSaveToAspServiceImpl")
	private Gstr3bGstnSaveToAspService aspService;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Override
	public APIResponse getGstnCall(String gstin, String taxPeriod) {

		Gstr3bMonthlyTrendTaxAmountsEntity gstr3BEntity = new Gstr3bMonthlyTrendTaxAmountsEntity();
		try {

			gstr3BEntity.setSuppGstin(gstin);
			gstr3BEntity.setTaxPeriod(taxPeriod);
			gstr3BEntity.setCreateDate(LocalDateTime.now());

			// get gstn call
			APIResponse apiResponse = dashBoardService
					.getGSTR3BSummary(taxPeriod, gstin);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" api response gstr3b summary - "
						+ apiResponse.getResponse());
			}
			if (apiResponse.isSuccess()) {

				String getGstnData = apiResponse.getResponse();

				// check get status table
				saveOrUpdateGetStatusResponse(gstin, taxPeriod,
						APIConstants.SUCCESS, null);

				// check gstn user request table
				saveOrUpdateGstnUserRequest(gstin, taxPeriod, getGstnData);

				// upload json file
				uploadJsonFileToRepo(getGstnData, gstin, taxPeriod);

				String groupCode = TenantContext.getTenantId();
				/*
				 * User user = SecurityContext.getUser(); String userName =
				 * user.getUserPrincipalName();
				 */

				// saving to asp compute table

				List<Gstr3BGstinsDto> resultList = dashBoardService
						.getGstrDtoList(apiResponse);
				List<Gstr3bTaxPaymentDto> taxPaymentResult = dashBoardService
						.getTaxPayemntList(apiResponse);

				String getGstnDataResp = apiResponse.getResponse();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.info("Before invoking saveGstnResponse() method "
							+ "resultList :" + resultList);
				}
				aspService.saveGstnResponse(gstin, taxPeriod, resultList,
						taxPaymentResult, getGstnDataResp);

				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser()
								.getUserPrincipalName() != null)
										? SecurityContext.getUser()
												.getUserPrincipalName()
										: "SYSTEM";

				if (userName == null || userName.isEmpty()) {
					userName = "SYSTEM";
				}

				JsonObject jobParams = new JsonObject();
				jobParams.addProperty("gstin", gstin);
				jobParams.addProperty("taxPeriod", taxPeriod);
				jobParams.addProperty("returnType", Gstr3BConstants.GSTR3B);
				jobParams.addProperty("invoiceType", Gstr3BConstants.RETSUM);

				// update save status with is active as true
				gstr3bMonthlyTaxRepo.softDeleteByGstinandTaxPeriod(gstin,
						taxPeriod);
				gstr3BEntity.setStatus("Success");
				gstr3BEntity.setApiDateTime(LocalDateTime.now());

				// creating job to convert json to csv
				asyncJobsService.createJob(groupCode,
						JobConstants.JSON_CSV_CONVERTOR, jobParams.toString(),
						userName, 1L, null, null);
			} else {
				String errCode = apiResponse.getError().getErrorCode();
				String errDesc = apiResponse.getError().getErrorDesc();
				if ("RT-3BAS1009".equalsIgnoreCase(errCode)) {
					saveOrUpdateGetStatusResponse(gstin, taxPeriod,
							APIConstants.SUCCESS_WITH_NO_DATA,
							errCode + "-" + errDesc);

					gstr3bMonthlyTaxRepo.softDeleteByGstinandTaxPeriod(gstin,
							taxPeriod);
					gstr3BEntity.setStatus("Success with no Data");
					gstr3BEntity.setApiDateTime(LocalDateTime.now());

					// soft delete previous one
					// update save status with is active as true

				} else {
					saveOrUpdateGetStatusResponse(gstin, taxPeriod,
							APIConstants.FAILED, errCode + "-" + errDesc);

					gstr3bMonthlyTaxRepo.softDeleteByGstinandTaxPeriod(gstin,
							taxPeriod);
					gstr3BEntity.setStatus("Failed");
					gstr3BEntity.setApiDateTime(LocalDateTime.now());

					// soft delete previous one
					// update save status with is active as true

				}
				gstnUserRequestRepo.updateGstnResponse(null, 0, gstin,
						taxPeriod, Gstr3BConstants.GSTR3B, LocalDateTime.now());
			}
			gstr3BEntity.setIsActive(true);
			gstr3bMonthlyTaxRepo.save(gstr3BEntity);

			return apiResponse;

		} catch (Exception e) {

			String msg = "Exception occured in GSTR3B , while persisting the "
					+ "get api response in DB and doc repo";

			// soft delete previous one
			// update save status with is active as true

			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		}

	}

	private void saveOrUpdateGstnUserRequest(String gstin, String taxPeriod,
			String getGstnData) {

		GstnUserRequestEntity gstnUserRequestEntity = null;

		gstnUserRequestEntity = gstnUserRequestRepo
				.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
						Gstr3BConstants.GSTR3B);

		Clob reqClob;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(
					getGstnData.toCharArray());

			if (gstnUserRequestEntity == null) {

				gstnUserRequestEntity = new GstnUserRequestEntity();

				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser()
								.getUserPrincipalName() != null)
										? SecurityContext.getUser()
												.getUserPrincipalName()
										: "SYSTEM";

				if (userName == null || userName.isEmpty()) {
					userName = "SYSTEM";
				}

				gstnUserRequestEntity.setGstin(gstin);
				gstnUserRequestEntity.setTaxPeriod(taxPeriod);
				gstnUserRequestEntity.setReturnType(Gstr3BConstants.GSTR3B);
				gstnUserRequestEntity.setRequestType("GET");
				gstnUserRequestEntity.setRequestStatus(1);
				gstnUserRequestEntity.setCreatedBy(userName);
				gstnUserRequestEntity.setCreatedOn(LocalDateTime.now());
				gstnUserRequestEntity.setDelete(false);
				gstnUserRequestEntity.setGetResponsePayload(reqClob);
				gstnUserRequestEntity.setDerivedRetPeriod(
						Integer.valueOf(taxPeriod.substring(2)
								.concat(taxPeriod.substring(0, 2))));

				gstnUserRequestRepo.save(gstnUserRequestEntity);

			} else {
				gstnUserRequestRepo.updateGstnResponse(reqClob, 1, gstin,
						taxPeriod, Gstr3BConstants.GSTR3B, LocalDateTime.now());

			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting gstnUserRequest data";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}
	}

	private void saveOrUpdateGetStatusResponse(String gstin, String taxPeriod,
			String status, String errDesc) {

		GstnGetStatusEntity gstnGetStatusEntity = null;

		gstnGetStatusEntity = gstinGetStatusRepo
				.findByGstinAndTaxPeriodAndReturnTypeAndSection(gstin,
						taxPeriod, Gstr3BConstants.GSTR3B,
						Gstr3BConstants.RETSUM);

		if (gstnGetStatusEntity == null) {

			gstnGetStatusEntity = new GstnGetStatusEntity();

			Integer derivedTaxPeriod = Integer.valueOf(
					taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));

			gstnGetStatusEntity.setGstin(gstin);
			gstnGetStatusEntity.setTaxPeriod(taxPeriod);
			gstnGetStatusEntity.setReturnType(Gstr3BConstants.GSTR3B);
			gstnGetStatusEntity.setSection(Gstr3BConstants.RETSUM);
			gstnGetStatusEntity.setCreatedOn(LocalDateTime.now());
			gstnGetStatusEntity.setDerivedTaxPeriod(derivedTaxPeriod);
		}
		gstnGetStatusEntity.setErrorDescription(errDesc);
		gstnGetStatusEntity.setStatus(status);
		gstnGetStatusEntity.setUpdatedOn(LocalDateTime.now());

		boolean dbLoad = (APIConstants.SUCCESS.equalsIgnoreCase(status)
				|| APIConstants.SUCCESS_WITH_NO_DATA.equalsIgnoreCase(status))
						? true : false;

		boolean csvFlag = APIConstants.SUCCESS_WITH_NO_DATA
				.equalsIgnoreCase(status) ? true : false;

		gstnGetStatusEntity.setIsdbLoad(dbLoad);
		gstnGetStatusEntity.setCsvGenerationFlag(csvFlag);

		gstinGetStatusRepo.save(gstnGetStatusEntity);
	}

	private void uploadJsonFileToRepo(String jsonResp, String gstin,
			String taxPeriod) throws IOException {

		String fileName = Gstr3BConstants.GSTR3B + "_" + gstin + "_" + taxPeriod
				+ "_" + "RETSUM_0.json";

		File tempDir = Files.createTempDirectory("GSTR3BJsonReports").toFile();

		String filePath = tempDir.getAbsolutePath() + File.separator + fileName;

		try (FileWriter writer = new FileWriter(filePath);
				BufferedWriter bw = new BufferedWriter(writer)) {
			bw.write(jsonResp);
			bw.flush();

			String folderName = DashboardCommonUtility.getDashboardFolderName(
					Gstr3BConstants.GSTR3B, JobStatusConstants.JSON_FILE);

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
