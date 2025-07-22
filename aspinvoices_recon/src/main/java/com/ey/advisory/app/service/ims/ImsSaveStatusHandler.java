/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.ImsProcessedInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ImsSaveJobQueueRepository;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("ImsSaveStatusHandler")
public class ImsSaveStatusHandler {

	@Autowired
	private ImsProcessedInvoiceRepository psdRepo;

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;
	
	@Autowired
	private ImsSaveJobQueueRepository queueRepo;

	public void callImsSaveStatusApi(String gstin, String tableType,
			String refId, String groupCode) {
		try {

			APIResponse resp = gstnServer.imsPoolingApiCall(groupCode, refId,
					gstin);

			if (!resp.isSuccess()) {

				String errResp = resp.getError().toString();
				Clob errRespClob = GenUtil.convertStringToClob(errResp);

				// update
				batchRepo.updateStatusAndReponseForRefId(
						APIConstants.POLLING_FAILED, null, errRespClob, refId,
						LocalDateTime.now());

				return;
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Ims Save STATUS API call response -> '%s'",
						resp.getResponse());
				LOGGER.debug(msg);
			}

			String status = null;
			String errorCode = null;
			String errorMessage = null;
			String psdCount = null;
			String errCount = null;
			Integer errorCount = 0;

			Gson gson = GsonUtil.newSAPGsonInstance();
			ImsPoolingResponse response = null;
			try {
				response = gson.fromJson(resp.getResponse(),
						ImsPoolingResponse.class);

				status = response.getStatus_cd();

				errorCode = response.getErr_cd();

				errorMessage = response.getErr_msg();

				psdCount = response.getProc_cnt();

				errCount = response.getErr_cnt();

				if (errCount != null && !errCount.isEmpty()) {
					errorCount = Integer.parseInt(errCount);
				}

			} catch (JsonSyntaxException e) {
				LOGGER.error(
						"Exception occured while parsing IMS pooling resp {} :",
						e);
				throw new AppException(e);
			}

			String successResp = resp.getResponse();
			Clob successRespClob = GenUtil.convertStringToClob(successResp);
			Long batchId = batchRepo.getBatchId(refId);
			if ("ER".equalsIgnoreCase(status)) {
				batchRepo.updateStatusAndReponseAndErrorsForRefId(
						APIConstants.POLLING_COMPLETED, status, successRespClob,
						refId, LocalDateTime.now(), errorCode, errorMessage,
						errorCount);

				// update psd status as well
				// psdRepo.updateSaveFlagByRefId(refId, false, false);
				// parse and save Error
				parseErrorResp(gstin, tableType, response, refId);
				queueRepo.updateStatus("Success",batchId.toString());
				return;

			} else if ("P".equalsIgnoreCase(status)) {
				batchRepo.updateStatusAndReponseAndErrorsForRefId(
						APIConstants.POLLING_COMPLETED, status, successRespClob,
						refId, LocalDateTime.now(), null, null, null);

				// update psd status as well
				psdRepo.updateSaveFlagByRefId(refId, true, true);
				queueRepo.updateStatus("Success",batchId.toString());
				return;
			} else if ("PE".equalsIgnoreCase(status)) {
				batchRepo.updateStatusAndReponseAndErrorsForRefId(
						APIConstants.POLLING_COMPLETED, status, successRespClob,
						refId, LocalDateTime.now(), errorCode, errorMessage,
						errorCount);

				// parse and save Error
				parseErrorResp(gstin, tableType, response, refId);
				queueRepo.updateStatus("Success",batchId.toString());
				return;
			}
			//handling PE/RCE case
			else {
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("IMS pooling inside else block"
							+ " - status {} ", status);
				}
				batchRepo.updateStatusAndReponseAndErrorsForRefId(
						APIConstants.POLLING_FAILED, status, successRespClob,
						refId, LocalDateTime.now(), errorCode, errorMessage,
						errorCount);
			}

			LOGGER.debug(" Polling Completed of IMS regeneration ");

		} catch (Exception ex) {
			batchRepo.updateStatusAndReponseForRefId(
					APIConstants.POLLING_FAILED, null, null, refId,
					LocalDateTime.now());

			String msg = "Exception while Transaction polling of IMS SAVE.";
			LOGGER.error(msg, ex);
			throw new AppException(ex, msg);
		}
	}

	/**
	 * @param gstin
	 * @param tableType
	 * @param response
	 */
	private void parseErrorResp(String gstin, String tableType,
			ImsPoolingResponse response, String refId) {
		ImsErrorReport error_report = response.getError_report();

		if (error_report != null) {

			Map<String, Pair<String, String>> invDtl = null;

			switch (tableType) {

			case "B2B":
				List<ErrorDetail> b2b = error_report.getB2b();
				invDtl = getInvDtl(b2b, gstin, tableType);

				break;

			case "B2BA":
				List<ErrorDetail> b2ba = error_report.getB2ba();
				invDtl = getInvDtl(b2ba, gstin, tableType);
				break;

			case "CN":
				List<ErrorDetail> cn = error_report.getCn();
				invDtl = getInvDtl(cn, gstin, tableType);
				break;

			case "DN":
				List<ErrorDetail> dn = error_report.getDn();
				invDtl = getInvDtl(dn, gstin, tableType);
				break;
			case "CNA":
				List<ErrorDetail> cna = error_report.getCna();
				invDtl = getInvDtl(cna, gstin, tableType);
				break;

			case "DNA":
				List<ErrorDetail> dna = error_report.getDna();
				invDtl = getInvDtl(dna, gstin, tableType);
				break;

			case "ECOM":
				List<ErrorDetail> ecom = error_report.getEcom();
					invDtl = getInvDtl(ecom, gstin, tableType);

				break;
			case "ECOMA":
				List<ErrorDetail> ecoma = error_report.getEcoma();

					invDtl = getInvDtl(ecoma, gstin, tableType);
				
				break;
			default:

				LOGGER.error("unknown TableType encountered"
						+ " while parsing pooling resp {} :", tableType);
			}

			List<String> docKeyList = new ArrayList<>();
			for (Map.Entry<String, Pair<String, String>> entry : invDtl
					.entrySet()) {
				String saveDocKey = entry.getKey();
				Pair<String, String> errorPair = entry.getValue();
				String errCode = errorPair.getValue0();
				String errorDesc = errorPair.getValue1();
				docKeyList.add(saveDocKey);

				psdRepo.updateErrorByGstnDocKey(saveDocKey, errCode, errorDesc);
				
			}
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Ims Save STATUS docKeyList for PE case-> '%s'",
						docKeyList);
				LOGGER.debug(msg);
			}
			
			psdRepo.updateSaveFlagByRefIdAndSaveDocKey(refId, true, true, docKeyList);

		}else {
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"else block -Ims Save STATUS refId for ER case-> '%s'",
						refId);
				LOGGER.debug(msg);
			}
			 psdRepo.updateSaveFlagByRefId(refId, false, false);
		}
	}

	private Map<String, Pair<String, String>> getInvDtl(List<ErrorDetail> obj,
			String rtin, String tableType) {
		Map<String, Pair<String, String>> resultMap = new HashMap<>();

		for (ErrorDetail errorDetail : obj) {
			String stin = errorDetail.getStin();
			String errCode = errorDetail.getError_cd();
			String errDesc = errorDetail.getError_msg();

			for (Invoice invoice : errorDetail.getInv()) {
				String taxPeriod = invoice.getRtnprd();
				String docNum = invoice.getInum();
				String docKey = stin + "|" + rtin + "|"
						+ docNum + "|" + taxPeriod;

				Pair<String, String> errorPair = Pair.with(errCode, errDesc);
				resultMap.put(docKey, errorPair);
			}
		}

		return resultMap;

	}

}
