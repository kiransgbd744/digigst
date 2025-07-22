package com.ey.advisory.app.processors.handler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveToGstnSectionWiseReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.itc04.Itc04SaveInvicesIdentifier;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
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
@Service("Itc04SaveToGstnJobHandler")
public class Itc04SaveToGstnJobHandler {

	@Autowired
	@Qualifier("Itc04SaveInvicesIdentifierImpl")
	private Itc04SaveInvicesIdentifier saveData;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	private static final String LOG1 = "ITC04 {} SaveToGstn section has Started";
	private static final String LOG2 = "ITC04 {} SaveToGstn section has completed";

	private static final String LOG3 = "ITC04 {} Can SaveToGstn section has Started";
	private static final String LOG4 = "ITC04 {} Can SaveToGstn section has completed";

	public void saveCancelledInvoices(String jsonReq, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Itc04 Can SaveToGstn Job has Started");
		}
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq).getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			SaveToGstnSectionWiseReqDto dto = gson.fromJson(requestObject, SaveToGstnSectionWiseReqDto.class);
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			Long userRequestId = dto.getUserRequestId();
			List<String> sections = dto.getSections();

			if (sections.contains(GSTConstants.TABLE_NUMBER_5A)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG3, APIConstants.TABLE5A);
				saveData.findItc04CanInvoices(gstin, retPeriod, groupCode, APIConstants.TABLE5A, null,
						SaveToGstnOprtnType.CAN, userRequestId);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG4, APIConstants.TABLE5A);
			}

			if (sections.contains(GSTConstants.TABLE_NUMBER_5B)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG3, APIConstants.TABLE5B);
				saveData.findItc04CanInvoices(gstin, retPeriod, groupCode, APIConstants.TABLE5B, null,
						SaveToGstnOprtnType.CAN, userRequestId);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG4, APIConstants.TABLE5B);
			}

			if (sections.contains(GSTConstants.TABLE_NUMBER_5C)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG3, APIConstants.TABLE5C);
				saveData.findItc04CanInvoices(gstin, retPeriod, groupCode, APIConstants.TABLE5C, null,
						SaveToGstnOprtnType.CAN, userRequestId);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG4, APIConstants.TABLE5C);
			}

			if (sections.contains(GSTConstants.TABLE_NUMBER_4)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG3, APIConstants.M2JW);
				saveData.findItc04CanInvoices(gstin, retPeriod, groupCode, APIConstants.M2JW, null,
						SaveToGstnOprtnType.CAN, userRequestId);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG4, APIConstants.M2JW);
			}

		} catch (Exception ex) {
			String msg = "Unexpcted Error";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ITC04 Can SaveToGstn Job has Finshed");
		}

	}

	public void saveActiveInvoices(String jsonReq, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Itc04 SaveToGstn Job has Started");
		}
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq).getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			SaveToGstnSectionWiseReqDto dto = gson.fromJson(requestObject, SaveToGstnSectionWiseReqDto.class);
			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			Long userRequestId = dto.getUserRequestId();
			List<String> sections = dto.getSections();

			if (sections.contains(GSTConstants.TABLE_NUMBER_4)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG1, APIConstants.M2JW);
				saveData.findItc04SaveInvoices(gstin, retPeriod, groupCode, APIConstants.M2JW, null,
						SaveToGstnOprtnType.SAVE, userRequestId);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG2, APIConstants.M2JW);
			}

			if (sections.contains(GSTConstants.TABLE_NUMBER_5A)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG1, APIConstants.TABLE5A);
				saveData.findItc04SaveInvoices(gstin, retPeriod, groupCode, APIConstants.TABLE5A, null,
						SaveToGstnOprtnType.SAVE, userRequestId);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG2, APIConstants.TABLE5A);
			}

			if (sections.contains(GSTConstants.TABLE_NUMBER_5B)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG1, APIConstants.TABLE5B);
				saveData.findItc04SaveInvoices(gstin, retPeriod, groupCode, APIConstants.TABLE5B, null,
						SaveToGstnOprtnType.SAVE, userRequestId);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG2, APIConstants.TABLE5B);
			}

			if (sections.contains(GSTConstants.TABLE_NUMBER_5C)) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG1, APIConstants.TABLE5C);
				saveData.findItc04SaveInvoices(gstin, retPeriod, groupCode, APIConstants.TABLE5C, null,
						SaveToGstnOprtnType.SAVE, userRequestId);
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(LOG2, APIConstants.TABLE5C);
			}
			// Allow next Save If there is now new data to SAVE immediately.
			gstnUserRequestUtil.allowNextSaveIfNoDataToSaveNowForItc04(gstin, retPeriod,
					APIConstants.SAVE.toUpperCase(), APIConstants.ITC04.toUpperCase(), groupCode, userRequestId);

		} catch (Exception ex) {
			String msg = "Unexpcted Error";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ITC04 SaveToGstn Job has Finshed");
		}

	}

}
