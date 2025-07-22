package com.ey.advisory.processors.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Clob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.gstr9.Gstr9SaveStatusRepository;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr9TransactionPollingProcessor")
public class Gstr9TransactionPollingProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	private Gstr9SaveStatusRepository gstr9SaveStatusRepository;

	// @Autowired
	// private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Posting Gstr9TransactionPollingProcessor Job for"
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
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("GSTR RETURN STATUS API call for -> GSTIN: '%s', "
								+ "Taxperiod: '%s'", gstin, taxPeriod);
				LOGGER.debug(msg);
			}
			callGstrReturnStatusApi(gstin, taxPeriod, refId, groupCode);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr9 Transaction polling is done -> GSTIN: '%s', "
								+ "Taxperiod: '%s', refId : '%s'",
						gstin, taxPeriod, refId);
				LOGGER.debug(msg);
			}
		}

	}

	private void callGstrReturnStatusApi(String gstin, String taxPeriod,
			String refId, String groupCode) {
		try {

			APIResponse resp = gstnServer.getStatusByRefId(groupCode, refId,
					gstin, taxPeriod);

			if (!resp.isSuccess()) {

				String errResp = resp.getError().toString();
				Clob errRespClob = new javax.sql.rowset.serial.SerialClob(
						errResp.toCharArray());

				gstr9SaveStatusRepository.updateStatusAndFilePath(refId,
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
				gstr9SaveStatusRepository.updateStatusAndFilePath(refId, status,
						gstin, taxPeriod, null, successRespClob);
				// gstrReturnStatusRepository.updateReturnStatus(
				// APIConstants.SAVED, gstin, taxPeriod,
				// APIConstants.GSTR9);
			} else if ("REC".equalsIgnoreCase(status)
					|| "IP".equalsIgnoreCase(status)) {

				gstr9SaveStatusRepository.updateStatusAndFilePath(refId,
						APIConstants.POLLING_INPROGRESS, gstin, taxPeriod, null,
						successRespClob);
			}

		} catch (Exception ex) {
			gstr9SaveStatusRepository.updateStatusAndFilePath(refId,
					APIConstants.POLLING_FAILED, gstin, taxPeriod, null, null);
			LOGGER.error("Exception while Transaction polling of Gstr9", ex);
			throw new AppException(ex,
					"{} error while Transaction polling of 3B");
		}
	}

	private void uploadErrorFileToRepo(JsonObject respObj, String gstin,
			String taxPeriod, String refId, String status,
			Clob successRespClob) {

		// JsonObject errObj = respObj.get("error_report").getAsJsonObject();
		String errorRep = respObj.toString();
		String file = "GSTR9ErrorReport_" + gstin + "_" + taxPeriod + "_"
				+ ".json";
		try (FileWriter writer = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(writer)) {
			bw.write(errorRep);
			bw.flush();
			String uploadedFileName = DocumentUtility.uploadZipFile(
					new File(file), APIConstants.GSTR9_SAVE_ERROR_FOLDER);
			gstr9SaveStatusRepository.updateStatusAndFilePath(refId, status,
					gstin, taxPeriod, uploadedFileName, successRespClob);
			// gstrReturnStatusRepository.updateReturnStatus(
			// APIConstants.SAVE_FAILED, gstin, taxPeriod,
			// APIConstants.GSTR9);
		} catch (Exception e) {
			LOGGER.error("Exception while creating the Save response file", e);
			throw new AppException(e,
					"{} error while creating the Save response file");
		}

	}
}
