package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr2x.Gstr2XSaveInvicesIdentifier;
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
 * @author SriBhavya
 *
 */

@Slf4j
@Service("Gstr2XSaveToGstnJobHandler")
public class Gstr2XSaveToGstnJobHandler {
	private static final String LOG1 = "Gstr2X {} SaveToGstn section has Started";
	private static final String LOG2 = "Gstr2X {} SaveToGstn section has completed";

	private static final String LOG3 = "Gstr2X {} Can SaveToGstn section has Started";
	private static final String LOG4 = "Gstr2X {} Can SaveToGstn section has completed";
	
	@Autowired
	@Qualifier("Gstr2XSaveInvicesIdentifierImpl")
	private Gstr2XSaveInvicesIdentifier saveData;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	public void saveCancelledInvoices(String jsonReq, String groupCode) {
		/*if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2X Can SaveToGstn Job has Started");
		}
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq).getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			SaveToGstnReqDto dto = gson.fromJson(requestObject, SaveToGstnReqDto.class);
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			Long userRequestId = dto.getUserRequestId();

			// Invoices Data
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.TCS);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.TCS, SaveToGstnOprtnType.CAN,
					userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.TCS);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.TCSA);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.TCSA, SaveToGstnOprtnType.CAN,
					userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.TCSA);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.TDS);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.TDS, SaveToGstnOprtnType.CAN,
					userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.TDS);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.TDSA);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.TDSA, SaveToGstnOprtnType.CAN,
					userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.TDSA);
			
		} catch (Exception ex) {
			String msg = "Unexpcted Error";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr6 can SaveToGstn Job has Finshed");
		}*/

	}

	public void saveActiveInvoices(String jsonReq, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2X SaveToGstn Job has Started");
		}
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq).getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			SaveToGstnReqDto dto = gson.fromJson(requestObject, SaveToGstnReqDto.class);
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			Long userRequestId = dto.getUserRequestId();
			String section = dto.getSection();
			
			// Invoices Data
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, section);
			saveData.findGstr2XSaveInvoices(gstin, retPeriod, groupCode, section,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, section);

			/*if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TCSA);
			saveData.findGstr2XSaveInvoices(gstin, retPeriod, groupCode, APIConstants.TCSA,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TCSA);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TDS);
			saveData.findGstr2XSaveInvoices(gstin, retPeriod, groupCode, APIConstants.TDS,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TDS);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.TDSA);
			saveData.findGstr2XSaveInvoices(gstin, retPeriod, groupCode, APIConstants.TDSA,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.TDSA);
			
*/			//Allow next Save If there is now new data to SAVE immediately.
			gstnUserRequestUtil.allowNextSaveIfNoDataToSaveNow(gstin, retPeriod,
					APIConstants.SAVE.toUpperCase(),
					APIConstants.GSTR2X.toUpperCase(), groupCode, userRequestId);
			
		} catch (Exception ex) {
			String msg = "Unexpcted Error";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr2X SaveToGstn Job has Finshed");
		}

	}

}
