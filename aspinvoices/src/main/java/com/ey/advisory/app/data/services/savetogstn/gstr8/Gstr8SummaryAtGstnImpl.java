package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.services.common.GstnCommonServiceUtil;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.gstr1.GetBatchPayloadHandler;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DashboardCommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.JobStatusConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Service("Gstr8SummaryAtGstnImpl")
@Slf4j
public class Gstr8SummaryAtGstnImpl {

	@Autowired
	@Qualifier("Gstr8SummaryDataParserImpl")
	private Gstr8SummaryDataParserImpl gsr8SummaryDataParser;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private GetBatchPayloadHandler batchPayloadHelper;

	@Autowired
	GstnCommonServiceUtil gstnCommonUtil;

	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	public Pair<String, String> getGstr8Summary(Anx2GetInvoicesReqDto dto,
			String groupCode) {
		String msg = "";
		GetAnx1BatchEntity batch = null;
		String returnPeriod = dto.getReturnPeriod();
		String gstin = dto.getGstin();

		try {
			String type = APIIdentifiers.GSTR8_GETSUM;
			batch = makeBatch(dto, type);
			batchRepo.softlyDelete(type.toUpperCase(),
					APIConstants.GSTR8.toUpperCase(), dto.getGstin(),
					dto.getReturnPeriod());
			// Save new Batch
			batch = batchRepo.save(batch);
			APIResponse apiResp = gstr8Summary(dto, groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GSTR8 GSTN Response {} ", apiResp.getResponse());
			}

			if (apiResp.isSuccess()) {
				String getGstnData = apiResp.getResponse();
				saveJsonAsRecords(getGstnData, groupCode, dto, batch);
				// check get status table
				saveOrUpdateGetStatusResponse(gstin, returnPeriod,
						APIConstants.SUCCESS, null);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("GSTR8 Get Summary Fetched Successfully.");
				}
				return new Pair<String, String>("S",
						"GSTR8 Get Summary Fetched Successfully.");
			} else {
				String errCode = apiResp.getError().getErrorCode();
				String errDesc = apiResp.getError().getErrorDesc();
				String errMsg = String.format("%s-%s", errCode, errDesc);
				if ("RT-3BAS1009".equalsIgnoreCase(errCode)
						|| "RET13510".equalsIgnoreCase(errCode)) {
					saveOrUpdateGetStatusResponse(gstin, returnPeriod,
							APIConstants.SUCCESS_WITH_NO_DATA, errMsg);
				} else {
					saveOrUpdateGetStatusResponse(gstin, returnPeriod,
							APIConstants.FAILED, errMsg);
				}

				gstnUserRequestRepo.updateGstnResponse(
						new javax.sql.rowset.serial.SerialClob(
								errMsg.toCharArray()),
						0, gstin, returnPeriod,
						APIConstants.GSTR8.toUpperCase(), LocalDateTime.now());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Get Summary Call Returned Error Response.");
				}
				return new Pair<String, String>("E", errMsg);
			}

		} catch (Exception ex) {
			String errMsg = String.format(
					"Failed to parse the Response Gstr8 Get Summary for GSTIN %s and TaxPeriod %s",
					gstin, returnPeriod);
			batchUtil.updateById(batch.getId(), APIConstants.FAILED, null, null,
					false);
			LOGGER.error(errMsg, ex);
			throw new AppException(msg, ex);
		}
	}

	public void saveJsonAsRecords(String apiResp, String groupCode,
			Anx2GetInvoicesReqDto dto, GetAnx1BatchEntity batch) {
		if (apiResp != null && !apiResp.trim().isEmpty()) {
			batchPayloadHelper.dumpGetResponsePayload(groupCode, dto.getGstin(),
					dto.getReturnPeriod(), batch.getId(), apiResp,
					APIConstants.SYSTEM);
			gsr8SummaryDataParser.parsegstr8SummaryData(dto, apiResp,
					batch.getId());
			batchUtil.updateById(batch.getId(), APIConstants.SUCCESS, null,
					null, false);

		} else {
			batchUtil.updateById(batch.getId(),
					APIConstants.SUCCESS_WITH_NO_DATA, null, null, false);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No response from gstn");
			}
		}

	}

	public APIResponse gstr8Summary(Anx2GetInvoicesReqDto dto,
			String groupCode) {

		// Invoke the GSTN API - Get Return Status API and get the JSON.
		APIParam param1 = new APIParam("gstin", dto.getGstin());
		APIParam param2 = new APIParam("ret_period", dto.getReturnPeriod());

		APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
				APIIdentifiers.GSTR8_GETSUM, param1, param2);
		APIResponse resp = apiExecutor.execute(params, null);

		// String apiResp = resp.getResponse();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("gstr7 get summary api respons: {}",
					resp.getResponse());
		}
		return resp;
	}

	public GetAnx1BatchEntity makeBatch(Anx2GetInvoicesReqDto dto,
			String type) {
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		GetAnx1BatchEntity batch = new GetAnx1BatchEntity();
		batch.setApiSection(APIConstants.GSTR8.toUpperCase());
		batch.setSgstin(dto.getGstin());
		batch.setTaxPeriod(dto.getReturnPeriod());

		batch.setAction(dto.getAction());
		batch.setCtin(dto.getCtin());
		batch.setETin(dto.getEtin());

		batch.setFromTime(dto.getFromTime());
		/*
		 * batch.setFromTime( dto.getFromTime() != null &&
		 * !dto.getFromTime().isEmpty() ?
		 * DateUtil.stringToTime(dto.getFromTime(), DateUtil.DATE_FORMAT1) :
		 * null);
		 */
		batch.setDerTaxPeriod(
				GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		// batch.setProcessingStatus(APIConstants.PROCESSING_STATUS);
		batch.setType(type != null ? type.toUpperCase() : null);
		batch.setCreatedBy(APIConstants.SYSTEM.toUpperCase());
		batch.setCreatedOn(now);

		batch.setDelete(false);
		batch.setStartTime(now);
		batch.setStatus(APIConstants.INITIATED);

		// Extra column added only in ANX2 GET to Generate Request ID
		// batch.setRequestId(dto.getRequestId());
		return batch;
	}

	private void saveOrUpdateGetStatusResponse(String gstin, String taxPeriod,
			String status, String errDesc) {

		GstnGetStatusEntity gstnGetStatusEntity = gstinGetStatusRepo
				.findByGstinAndTaxPeriodAndReturnTypeAndSection(gstin,
						taxPeriod, APIConstants.GSTR8.toUpperCase(),
						APIConstants.GSTR7_SUMMARY_DETAILS);

		if (gstnGetStatusEntity == null) {

			gstnGetStatusEntity = new GstnGetStatusEntity();

			Integer derivedTaxPeriod = Integer.valueOf(
					taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));

			gstnGetStatusEntity.setGstin(gstin);
			gstnGetStatusEntity.setTaxPeriod(taxPeriod);
			gstnGetStatusEntity.setReturnType(APIConstants.GSTR8.toUpperCase());
			gstnGetStatusEntity.setSection(APIConstants.GSTR7_SUMMARY_DETAILS);
			gstnGetStatusEntity.setCreatedOn(LocalDateTime.now());
			gstnGetStatusEntity.setDerivedTaxPeriod(derivedTaxPeriod);
		}
		gstnGetStatusEntity.setErrorDescription(errDesc);
		gstnGetStatusEntity.setStatus(status);
		gstnGetStatusEntity.setUpdatedOn(LocalDateTime.now());

		boolean csvFlag = APIConstants.SUCCESS_WITH_NO_DATA
				.equalsIgnoreCase(status) ? true : false;

		gstnGetStatusEntity.setCsvGenerationFlag(csvFlag);

		gstinGetStatusRepo.save(gstnGetStatusEntity);
	}

	private void uploadJsonFileToRepo(String jsonResp, String gstin,
			String taxPeriod) throws IOException {

		String fileName = APIConstants.GSTR8.toUpperCase() + "_" + gstin + "_"
				+ taxPeriod + "_" + APIConstants.GSTR7_SUMMARY_DETAILS
				+ "_0.json";

		File tempFile = Files.createTempDirectory("GSTRJsonReports").toFile();

		String filePath = tempFile.getAbsolutePath() + File.separator
				+ fileName;

		try (FileWriter writer = new FileWriter(filePath);
				BufferedWriter bw = new BufferedWriter(writer)) {
			bw.write(jsonResp);
			bw.flush();

			String folderName = DashboardCommonUtility.getDashboardFolderName(
					APIConstants.GSTR8.toUpperCase(),
					JobStatusConstants.JSON_FILE);

			DocumentUtility.uploadFileWithFileName(new File(filePath),
					folderName, fileName);

		} catch (Exception e) {

			String msg = "Error occured while uploading json file";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);
		} finally {
			GenUtil.deleteTempDir(tempFile);
		}

	}

}
