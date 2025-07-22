package com.ey.advisory.app.processors.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr7.Gstr7CancelledInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr7.Gstr7SaveInvicesIdentifier;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.AppException;
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
 * @author SriBhavya
 *
 */

@Slf4j
@Service("Gstr7SaveToGstnJobHandler")
public class Gstr7SaveToGstnJobHandler {
	private static final String LOG1 = "Gstr7 {} SaveToGstn section has Started";
	private static final String LOG2 = "Gstr7 {} SaveToGstn section has completed";

	@Autowired
	@Qualifier("Gstr7SaveInvicesIdentifierImpl")
	private Gstr7SaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("Gstr7CancelledInvicesIdentifierImpl")
	private Gstr7CancelledInvicesIdentifier gstnCancelData;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	public void saveCancelledInvoices(String jsonReq, String groupCode,
			ProcessingContext gstr7context) {
		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			SaveToGstnReqDto dto = gson.fromJson(requestObject,
					SaveToGstnReqDto.class);
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			Long userRequestId = dto.getUserRequestId();

			boolean isTransactional = (boolean) gstr7context
					.getAttribute(APIConstants.TRANSACTIONAL);
			if (isTransactional) {
				Map<String, Map<Long, Long>> map = gstnCancelData
						.findOrgCanInvoicesMap(gstin, retPeriod, groupCode,
								SaveToGstnOprtnType.CAN, userRequestId,
								gstr7context);

				gstnCancelData.findGstr7TransCanInvoices(gstin, retPeriod,
						groupCode, map, userRequestId, gstr7context);

			} else {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG1, APIConstants.TDSA);
				gstnCancelData.findGstr7CanInvoices(gstin, retPeriod, groupCode,
						APIConstants.TDSA, SaveToGstnOprtnType.CAN,
						userRequestId, gstr7context);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG2, APIConstants.TDSA);

				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG1, APIConstants.TDS);
				gstnCancelData.findGstr7CanInvoices(gstin, retPeriod, groupCode,
						APIConstants.TDS, SaveToGstnOprtnType.CAN,
						userRequestId, gstr7context);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG2, APIConstants.TDS);
			}

		} catch (Exception ex) {
			String msg = "Unexpcted Error";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public void saveActiveInvoices(String jsonReq, String groupCode,
			ProcessingContext gstr7context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr6 SaveToGstn Job has Started");
		}
		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			SaveToGstnReqDto dto = gson.fromJson(requestObject,
					SaveToGstnReqDto.class);

			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			Long userRequestId = dto.getUserRequestId();

			// Invoices Data
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TDS);
			saveData.findGstr7SaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.TDS, null, SaveToGstnOprtnType.SAVE,
					userRequestId, gstr7context, null);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TDS);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TDSA);
			saveData.findGstr7SaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.TDSA, null, SaveToGstnOprtnType.SAVE,
					userRequestId, gstr7context, null);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TDSA);

			// Allow next Save If there is now new data to SAVE immediately.
			gstnUserRequestUtil.allowNextSaveIfNoDataToSaveNow(gstin, retPeriod,
					APIConstants.SAVE.toUpperCase(),
					APIConstants.GSTR7.toUpperCase(), groupCode, userRequestId);

		} catch (Exception ex) {
			String msg = "Unexpcted Error";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr7 SaveToGstn Job has Finshed");
		}

	}

}
