package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.services.savetogstn.gstr8.Gstr8SaveInvicesIdentifier;
import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Slf4j
@Service("Gstr8SaveToGstnJobHandler")
public class Gstr8SaveToGstnJobHandler {
	private static final String LOG1 = "Gstr8 {} SaveToGstn section has Started";
	private static final String LOG2 = "Gstr8 {} SaveToGstn section has completed";

	@Autowired
	@Qualifier("Gstr8SaveInvicesIdentifierImpl")
	private Gstr8SaveInvicesIdentifier saveData;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	public void saveCancelledInvoices(String jsonReq, String groupCode) {
		try {
			JsonObject requestObject = JsonParser.parseString(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			SaveToGstnReqDto dto = gson.fromJson(requestObject,
					SaveToGstnReqDto.class);
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			Long userRequestId = dto.getUserRequestId();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TCS);
			saveData.findGstr8CanInvoices(gstin, retPeriod, groupCode,
					APIConstants.TCS.toUpperCase(), SaveToGstnOprtnType.CAN,
					userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TCSA);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TCSA);
			saveData.findGstr8CanInvoices(gstin, retPeriod, groupCode,
					APIConstants.TCSA.toUpperCase(), SaveToGstnOprtnType.CAN,
					userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TCSA);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.URD);
			saveData.findGstr8CanInvoices(gstin, retPeriod, groupCode,
					APIConstants.URD.toUpperCase(), SaveToGstnOprtnType.CAN,
					userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.URD);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.URDA);
			saveData.findGstr8CanInvoices(gstin, retPeriod, groupCode,
					APIConstants.URDA.toUpperCase(), SaveToGstnOprtnType.CAN,
					userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.URDA);

		} catch (Exception ex) {
			String msg = "UnExpected Error while Saving GSTR8.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public void saveActiveInvoices(String jsonReq, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr8 SaveToGstn Job has Started");
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
				LOGGER.debug(LOG1, APIConstants.TCS);
			saveData.findGstr8SaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.TCS.toUpperCase(), null,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TCS);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TCSA);
			saveData.findGstr8SaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.TCSA.toUpperCase(), null,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TCSA);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.URD);
			saveData.findGstr8SaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.URD.toUpperCase(), null,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.URD);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.URDA);
			saveData.findGstr8SaveInvoices(gstin, retPeriod, groupCode,
					APIConstants.URDA.toUpperCase(), null,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.URDA);

			// Allow next Save If there is now new data to SAVE immediately.
			gstnUserRequestUtil.allowNextSaveIfNoDataToSaveNow(gstin, retPeriod,
					APIConstants.SAVE.toUpperCase(),
					APIConstants.GSTR7.toUpperCase(), groupCode, userRequestId);

		} catch (Exception ex) {
			String msg = "UnExpected Error while Saving GSTR8.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr8 SaveToGstn Job has Finshed");
		}

	}

}
