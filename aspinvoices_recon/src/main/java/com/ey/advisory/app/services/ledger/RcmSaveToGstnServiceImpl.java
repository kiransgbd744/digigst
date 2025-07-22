package com.ey.advisory.app.services.ledger;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSaveToGstnRcmRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("RcmSaveToGstnServiceImpl")
public class RcmSaveToGstnServiceImpl implements RcmSaveToGstnService {

	@Autowired
	@Qualifier("DefaultAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("LedgerSaveToGstnRcmRepository")
	LedgerSaveToGstnRcmRepository ledgerSaveToGstnRcmRepository;

	@Override
	public void rcmSaveToGstnApiCall(String data,
			String gstin, Long id) {
		String acknum = null;
		APIResponse resp = null;
		try {

			APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);

			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN,
					APIIdentifiers.SAVE_TO_GSTN_RCM, param1);
			resp = apiExecutor.execute(params, data);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("RCM Get call response is : "
						+ (resp != null ? resp.toString() : "null"));
			}

			if (resp != null && resp.isSuccess()) {
				String response = resp.getResponse();
				JsonObject jsonResponse = JsonParser.parseString(response)
						.getAsJsonObject();

				if (jsonResponse.has("acknum")) {
					acknum = jsonResponse.get("acknum").getAsString();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String
								.format("RCM acknum number  %s" + acknum));
					}
				}

				ledgerSaveToGstnRcmRepository.updateStatusById("SUCCESS",
						LocalDateTime.now(), acknum, null,
						id);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String
							.format("RCM GSTN Save response is %s" + response));
				}
			} else {
				LOGGER.error(
						"Received error response - {} while Saving RCM Ledger for gstin {}",
						resp.getErrors(), gstin);

				String errorDesc = "ERROR FROM GSTIN";
				if (resp != null && resp.getErrors() != null
						&& !resp.getErrors().isEmpty()) {
					APIError apiError = resp.getErrors().get(0); // Assuming you
																	// only want
																	// the first
																	// error
					errorDesc = apiError.getErrorDesc(); // Extract the error
															// description

					if (errorDesc.length() > 490) {
						errorDesc = errorDesc.substring(0, 490);
					}
				}

				ledgerSaveToGstnRcmRepository.updateStatusById(
						"FAILED", LocalDateTime.now(), null, errorDesc, id);

				/*
				 * ledgerSaveToGstnRcmRepository.updateStatusById("FAILED",
				 * LocalDateTime.now(), null, "ERROR FROM GSTIN", id);
				 */
			}
		} catch (Exception e) {
			LOGGER.error("Exception in the method rcmSaveToGstnApiCall ", e);

			ledgerSaveToGstnRcmRepository.updateStatusById("FAILED",
					LocalDateTime.now(), null, "ERROR FROM GSTIN", id);
			throw new AppException(e);
		}
		// return acknum;

	}

	@Override
	public void reclaimSaveToGstnApiCall(String data,
			String gstin, Long id) {
		String acknum = null;
		APIResponse resp = null;
		try {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						"reclaimSaveToGstnApiCall GSTN Save API Call Initiated."));
			}
			resp = new APIResponse();
			APIParam param1 = new APIParam(APIIdentifiers.GSTIN, gstin);

			APIParams params = new APIParams(TenantContext.getTenantId(),
					APIProviderEnum.GSTN,
					APIIdentifiers.SAVE_TO_GSTN_RECLAIM, param1);
			resp = apiExecutor.execute(params, data);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("RECLAIM Get call response is : "
						+ (resp != null ? resp.toString() : "null"));
			}

			if (resp != null && resp.isSuccess()) {
				String response = resp.getResponse();
				JsonObject jsonResponse = JsonParser.parseString(response)
						.getAsJsonObject();

				if (jsonResponse.has("acknum")) {
					acknum = jsonResponse.get("acknum").getAsString();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String
								.format("RCM acknum number  %s" + acknum));
					}
				}
				ledgerSaveToGstnRcmRepository.updateStatusById("SUCCESS",
						LocalDateTime.now(), acknum, null,
						id);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String
							.format("RCM GSTN Save response is %s" + response));
				}
			} else {
				LOGGER.error(
						"Received error response - {} while Saving RCM Ledger for gstin {}",
						"", gstin);
				String errorDesc = "ERROR FROM GSTIN";
				if (resp != null && resp.getErrors() != null
						&& !resp.getErrors().isEmpty()) {
					APIError apiError = resp.getErrors().get(0); 
					errorDesc = apiError.getErrorDesc(); 
					if (errorDesc.length() > 490) {
						errorDesc = errorDesc.substring(0, 490);
					}
				}

				ledgerSaveToGstnRcmRepository.updateStatusById(
						"FAILED", LocalDateTime.now(), null, errorDesc, id);

				/*
				 * ledgerSaveToGstnRcmRepository.updateStatusById("FAILED",
				 * LocalDateTime.now(), null, "ERROR FROM GSTIN", id);
				 */
			}

		} catch (Exception e) {
			ledgerSaveToGstnRcmRepository.updateStatusById("FAILED",
					LocalDateTime.now(), null, "ERROR FROM GSTIN", id);
			throw new AppException(e);
		}
		// return acknum;

	}

}
