package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.Gstr1DeleteInvicesIdentifier;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1DeleteGstnDataJobHandler")
public class Gstr1DeleteGstnDataJobHandler {

	private static final String LOG1 = "Gstr1 {} Delete SaveToGstn section has Started";
	private static final String LOG2 = "Gstr1 {} Delete SaveToGstn section has completed";

	@Autowired
	private Gstr1DeleteInvicesIdentifier saveData;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	public void saveAutoDraftedInvoices(String jsonReq, String groupCode,
			ProcessingContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 AutoDraft SaveToGstn Job has Started");
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		SaveToGstnReqDto dto = gson.fromJson(requestObject,
				SaveToGstnReqDto.class);

		String gstin = dto.getGstin();
		String retPeriod = dto.getReturnPeriod();
		Long userRequestId = dto.getUserRequestId();
		String userSelectedSec = dto.getSection();

		// Invoices Data
		/**
		 * Here SaveToGStnOprtnType.DELETE enum value is refering to
		 * APIConstans.DELETE_FILE_UPLOAD
		 * 
		 */
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2B)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2B);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.B2B, null, SaveToGstnOprtnType.DELETE,
					userRequestId, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2B);
		}

		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.EXP)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.EXP);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.EXP, null, SaveToGstnOprtnType.DELETE,
					userRequestId, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.EXP);
		}

		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNR)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDNR);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.CDNR, null, SaveToGstnOprtnType.DELETE,
					userRequestId, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDNR);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNUR)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDNUR);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.CDNUR, null, SaveToGstnOprtnType.DELETE,
					userRequestId, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDNUR);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 AutoDraft SaveToGstn Job has Finshed");
		}

		// Allow next Save If there is now new data to SAVE immediately.
		gstnUserRequestUtil.allowNextSaveIfNoDataToSaveNow(gstin, retPeriod,
				APIConstants.DELETE_FILE_UPLOAD.toUpperCase(),
				GenUtil.getReturnType(context), groupCode, userRequestId);

	}

	public void saveDeleteResponseInvoices(String jsonReq, String groupCode,
			ProcessingContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 DeleteResponse SaveToGstn Job has Started");
		}

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		SaveToGstnReqDto dto = gson.fromJson(requestObject,
				SaveToGstnReqDto.class);

		String gstin = dto.getGstin();
		String retPeriod = dto.getReturnPeriod();
		Long userRequestId = dto.getUserRequestId();
		String userSelectedSec = dto.getSection();

		// Invoices Data
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2B)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2B);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.B2B, null, SaveToGstnOprtnType.DELETE_RESPONSE,
					userRequestId, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2B);
		}

		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.EXP)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.EXP);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.EXP, null, SaveToGstnOprtnType.DELETE_RESPONSE,
					userRequestId, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.EXP);
		}

		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNR)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDNR);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.CDNR, null,
					SaveToGstnOprtnType.DELETE_RESPONSE, userRequestId,
					context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDNR);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNUR)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDNUR);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.CDNUR, null,
					SaveToGstnOprtnType.DELETE_RESPONSE, userRequestId,
					context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDNUR);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 DeleteResponse SaveToGstn Job has Finshed");
		}

		// Allow next Save If there is now new data to SAVE immediately.
		gstnUserRequestUtil.allowNextSaveIfNoDataToSaveNow(gstin, retPeriod,
				APIConstants.DELETE_RESPONSE.toUpperCase(),
				GenUtil.getReturnType(context), groupCode, userRequestId);

	}

}
