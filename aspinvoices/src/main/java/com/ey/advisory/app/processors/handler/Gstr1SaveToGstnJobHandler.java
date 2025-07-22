package com.ey.advisory.app.processors.handler;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.Gstr1CancelledInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.Gstr1SaveInvicesIdentifier;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
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
@Service("Gstr1SaveToGstnJobHandler")
public class Gstr1SaveToGstnJobHandler {

	private static final String LOG1 = "Gstr1 {} SaveToGstn section has Started";
	private static final String LOG2 = "Gstr1 {} SaveToGstn section has completed";

	@Autowired
	@Qualifier("gstr1SaveInvicesIdentifierImpl")
	private Gstr1SaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("gstr1CancelledInvicesIdentifierImpl")
	private Gstr1CancelledInvicesIdentifier gstnCancelData;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	public void saveCancelledInvoices(String jsonReq, String groupCode,
			ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAVE_CAN_INV_START,
				PerfamanceEventConstants.Gstr1SaveToGstnJobHandler,
				PerfamanceEventConstants.saveCancelledInvoices, null);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("saveCancelledInvoices method called with arg {}",
					jsonReq);
		}

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		SaveToGstnReqDto dto = gson.fromJson(requestObject,
				SaveToGstnReqDto.class);

		String gstin = dto.getGstin();
		String retPeriod = dto.getReturnPeriod();
		Long userRequestId = dto.getUserRequestId();
		String userSelectedSec = dto.getSection();
		List<Long> docIds = dto.getDocIds();
		Map<String, Map<Long, Long>> map = gstnCancelData.findOrgCanInvoicesMap(
				gstin, retPeriod, groupCode, SaveToGstnOprtnType.CAN, docIds,
				context);
		gstnCancelData.findCanInvoices(gstin, retPeriod, groupCode, map,
				userRequestId, userSelectedSec, docIds, context);

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAVE_CAN_INV_END,
				PerfamanceEventConstants.Gstr1SaveToGstnJobHandler,
				PerfamanceEventConstants.saveCancelledInvoices, null);
	}

	public void saveActiveInvoices(String jsonReq, String groupCode,
			ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAVE_ACT_INV_START,
				PerfamanceEventConstants.Gstr1SaveToGstnJobHandler,
				PerfamanceEventConstants.saveActiveInvoices, null);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 SaveToGstn Job has Started");
		}

		JsonObject requestObject = JsonParser.parseString(jsonReq)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();

		SaveToGstnReqDto dto = gson.fromJson(requestObject,
				SaveToGstnReqDto.class);

		String gstin = dto.getGstin();
		String retPeriod = dto.getReturnPeriod();
		Long userRequestId = dto.getUserRequestId();
		String userSelectedSec = dto.getSection();
		List<Long> docIds = dto.getDocIds();

		// Invoices Data
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2B)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2B);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.B2B, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2B);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2CL)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2CL);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.B2CL, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2CL);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.EXP)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.EXP);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.EXP, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.EXP);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.AT)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.AT);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.AT, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.AT);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.TXP)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TXP);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.TXP, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TXP);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNR)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDNR);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.CDNR, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDNR);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNUR)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDNUR);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.CDNUR, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDNUR);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2BA)) {
			// Ammendments

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2BA);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.B2BA, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2BA);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2CLA)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2CLA);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.B2CLA, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2CLA);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.EXPA)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.EXPA);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.EXPA, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.EXPA);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.ATA)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.ATA);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.ATA, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.ATA);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.TXPA)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TXPA);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.TXPA, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TXPA);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNRA)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDNRA);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.CDNRA, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDNRA);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.CDNURA)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDNURA);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.CDNURA, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDNURA);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.NIL)) {
			// summary data

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.NIL);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.NIL, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.NIL);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2CS)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2CS);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.B2CS, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2CS);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.B2CSA)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2CSA);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.B2CSA, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2CSA);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.HSNSUM)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.HSNSUM);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.HSNSUM, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.HSNSUM);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.DOCISS)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.DOCISS);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.DOCISS, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.DOCISS);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.SUPECOM)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.SUPECOM);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.SUPECOM, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.SUPECOM);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.ECOMSUPSUM)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.ECOMSUPSUM);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.ECOMSUPSUM, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.ECOMSUPSUM);
		}
		if (userSelectedSec == null
				|| userSelectedSec.equals(APIConstants.ECOMSUP)) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.ECOMSUP);
			saveData.findSaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.ECOMSUP, null, SaveToGstnOprtnType.SAVE,
					userRequestId, docIds, context);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.ECOMSUP);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr1 SaveToGstn Job has Finshed");
		}

		
		if (docIds != null && !docIds.isEmpty()) {
			LOGGER.debug("If Section {}", userSelectedSec.toUpperCase());
			gstnUserRequestUtil.allowNextSaveIfNoDataToSaveNow(gstin, retPeriod,
					APIConstants.SAVE.toUpperCase(),
					GenUtil.getReturnType(context), groupCode, userRequestId,
					userSelectedSec.toUpperCase());

		} else {
			LOGGER.debug("DocIds are Null");
			gstnUserRequestUtil.allowNextSaveIfNoDataToSaveNow(gstin, retPeriod,
					APIConstants.SAVE.toUpperCase(),
					GenUtil.getReturnType(context), groupCode, userRequestId);
		}
		// Allow next Save If there is now new data to SAVE immediately.

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.SAVE_ACT_INV_END,
				PerfamanceEventConstants.Gstr1SaveToGstnJobHandler,
				PerfamanceEventConstants.saveActiveInvoices, null);
	}
}
