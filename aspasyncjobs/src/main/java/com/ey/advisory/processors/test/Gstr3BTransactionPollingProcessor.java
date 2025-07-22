package com.ey.advisory.processors.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Clob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.services.savetogstn.jobs.gstr3B.Gstr3BSaveStatusEntryHandler;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component("Gstr3BTransactionPollingProcessor")
public class Gstr3BTransactionPollingProcessor implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BTransactionPollingProcessor.class);

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	@Qualifier("gstr3BSaveStatusEntryHandlerImpl")
	Gstr3BSaveStatusEntryHandler gstr3BSaveStatusEntryHandlerImpl;

	@Autowired
	@Qualifier("gstr3BSaveStatusRepository")
	Gstr3BSaveStatusRepository gstr3BSaveStatusRepository;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private ErpScenarioMasterRepository masterRepo;

	@Autowired
	private GSTNDetailRepository gstnRepo;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository erpEventsScenPermiRep;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Posting Gstr3BTransactionPollingProcessor Job for"
							+ " -> params: '%s', GroupCode: '%s'",
					jsonString, groupCode);
			LOGGER.debug(msg);
		}
		if (jsonString != null && groupCode != null) {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			String gstin = requestObject.get(APIConstants.GSTIN).getAsString();
			String taxPeriod = requestObject.get(APIConstants.TAXPERIOD)
					.getAsString();
			String refId = requestObject.get(APIConstants.REFID).getAsString();
			String apiAction = requestObject.get(APIConstants.APIACTION)
					.getAsString();
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("GSTR RETURN STATUS API call for -> GSTIN: '%s', "
								+ "Taxperiod: '%s'", gstin, taxPeriod);
				LOGGER.debug(msg);
			}
			callGstrReturnStatusApi(gstin, taxPeriod, refId, groupCode,
					apiAction);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"3B Transaction polling is done -> GSTIN: '%s', "
								+ "Taxperiod: '%s', refId : '%s'",
						gstin, taxPeriod, refId);
				LOGGER.debug(msg);
			}
		}

	}

	private void callGstrReturnStatusApi(String gstin, String taxPeriod,
			String refId, String groupCode, String apiAction) {
		try {

			APIResponse resp = gstnServer.getStatusByRefId(groupCode, refId,
					gstin, taxPeriod);

			if (!resp.isSuccess()) {

				String errResp = resp.getError().toString();
				Clob errRespClob = new javax.sql.rowset.serial.SerialClob(
						errResp.toCharArray());

				gstr3BSaveStatusRepository.updateStatusAndFilePath(refId,
						APIConstants.POLLING_FAILED, gstin, taxPeriod, null,
						errRespClob);

				return;
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"GSTR RETURN STATUS API call response -> '%s'",
						resp.getResponse());
				LOGGER.debug(msg);
			}
			JsonObject respObj = (new JsonParser()).parse(resp.getResponse())
					.getAsJsonObject();

			String status = respObj.get("status_cd").toString().replace("\"",
					"");

			String successResp = resp.getResponse();
			Clob successRespClob = new javax.sql.rowset.serial.SerialClob(
					successResp.toCharArray());

			if ("PE".equalsIgnoreCase(status)
					|| "ER".equalsIgnoreCase(status)) {
				uploadErrorFileToRepo(respObj, gstin, taxPeriod, refId, status,
						successRespClob);
			} else if ("P".equalsIgnoreCase(status)) {
				gstr3BSaveStatusRepository.updateStatusAndFilePath(refId,
						status, gstin, taxPeriod, null, successRespClob);
				if (!APIConstants.GSTR3B_RECOMPUTE.equalsIgnoreCase(apiAction)
						|| !APIConstants.GSTR3B_SAVEPSTLIAB
								.equalsIgnoreCase(apiAction)) {
					gstrReturnStatusRepository.updateReturnStatus(
							APIConstants.SAVED, gstin, taxPeriod,
							APIConstants.GSTR3B);
				}

				// 3B RevInt
				/*
				 * try { postReverseIntegrationjob(groupCode, gstin, taxPeriod);
				 * } catch (Exception ex) {
				 * 
				 * LOGGER.error("Exception while RevereseIntigrating 3B data",
				 * ex); throw new AppException(ex,
				 * "{} Exception while RevereseIntigrating 3B data"); }
				 */

			} else if ("REC".equalsIgnoreCase(status)
					|| "IP".equalsIgnoreCase(status)) {

				gstr3BSaveStatusRepository.updateStatusAndFilePath(refId,
						APIConstants.POLLING_INPROGRESS, gstin, taxPeriod, null,
						successRespClob);
			}

		} catch (Exception ex) {
			gstr3BSaveStatusRepository.updateStatusAndFilePath(refId,
					APIConstants.POLLING_FAILED, gstin, taxPeriod, null, null);
			LOGGER.error("Exception while Transaction polling of 3B", ex);
			throw new AppException(ex,
					"{} error while Transaction polling of 3B");
		}
	}

	private void postReverseIntegrationjob(String groupCode, String gstin,
			String taxPeriod) {

		GSTNDetailEntity gstinEntity = gstnRepo
				.findByGstinAndIsDeleteFalse(gstin);

		Long entityId = gstinEntity.getEntityId();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR3B reverseIntegration entityId -> '%d'", entityId);
			LOGGER.debug(msg);
		}
		Long scenarioId = masterRepo
				.findSceIdOnScenarioName(JobConstants.GSTR3B_REV_INT);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR3B reverseIntegration scenarioId -> '%d'", scenarioId);
			LOGGER.debug(msg);
		}

		if (scenarioId == null) {

			LOGGER.error("GSTR3B reverseIntegration returning without data,"
					+ " No scenarioId found for  -> {} ", scenarioId);
			return;

		}
		ErpEventsScenarioPermissionEntity scenPermissionEntity = erpEventsScenPermiRep
				.findByScenarioIdAndIsDeleteFalse(scenarioId);

		String destName = scenPermissionEntity.getDestName();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR3B reverseIntegration scenPermissionEntity -> '%s'",
					scenPermissionEntity);
			LOGGER.debug(msg);
		}

		JsonObject jsonParams = new JsonObject();

		jsonParams.addProperty("groupcode", groupCode);
		jsonParams.addProperty("gstin", gstin);
		jsonParams.addProperty("finYear", taxPeriod);
		jsonParams.addProperty("entityId", entityId.toString());
		jsonParams.addProperty("scenarioId", scenarioId);
		jsonParams.addProperty("destinationName", destName);
		jsonParams.addProperty("source", " GSTR3B_SAVE");

		asyncJobsService.createJob(groupCode, JobConstants.GSTR3B_REV_INT,
				jsonParams.toString(), "SYSTEM", 1L, null, null);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR3B reverseIntegration submitted job "
							+ "from tasnaction polling  jsonParams -> '%s'",
					jsonParams.toString());
			LOGGER.debug(msg);
		}
	}

	private void uploadErrorFileToRepo(JsonObject respObj, String gstin,
			String taxPeriod, String refId, String status,
			Clob successRespClob) {

		// JsonObject errObj = respObj.get("error_report").getAsJsonObject();
		String errorRep = respObj.toString();
		String file = "Gstr3BErrorReport" + gstin + "_" + taxPeriod + "_"
				+ ".json";
		try (FileWriter writer = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(writer)) {
			bw.write(errorRep);
			bw.flush();
			String uploadedFileName = DocumentUtility.uploadZipFile(
					new File(file), APIConstants.GSTR3B_SAVE_ERROR_FOLDER);
			gstr3BSaveStatusRepository.updateStatusAndFilePath(refId, status,
					gstin, taxPeriod, uploadedFileName, successRespClob);
			gstrReturnStatusRepository.updateReturnStatus(
					APIConstants.SAVE_FAILED, gstin, taxPeriod,
					APIConstants.GSTR3B);
		} catch (Exception e) {
			LOGGER.error("Exception while creating the Save response file", e);
			throw new AppException(e,
					"{} error while creating the Save response file");
		}

	}

}
