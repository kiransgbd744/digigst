package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr6.Gstr6CanInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr6.Gstr6SaveInvicesIdentifier;
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
@Service("Gstr6SaveToGstnJobHandler")
public class Gstr6SaveToGstnJobHandler {
	private static final String LOG1 = "Gstr6 {} SaveToGstn section has Started";
	private static final String LOG2 = "Gstr6 {} SaveToGstn section has completed";

	private static final String LOG3 = "Gstr6 {} Can SaveToGstn section has Started";
	private static final String LOG4 = "Gstr6 {} Can SaveToGstn section has completed";

	@Autowired
	@Qualifier("Gstr6SaveInvicesIdentifierImpl")
	private Gstr6SaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("Gstr6CanInvicesIdentifierImpl")
	private Gstr6CanInvicesIdentifier saveCanData;
	
	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	public void saveCancelledInvoices(String jsonReq, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr6 Can SaveToGstn Job has Started");
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
				LOGGER.debug(LOG3, APIConstants.B2B);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.B2B, SaveToGstnOprtnType.CAN,
					userRequestId,null);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.B2B);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.B2BA);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.B2BA, SaveToGstnOprtnType.CAN,
					userRequestId,null);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.B2BA);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.CDN);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.CDN, SaveToGstnOprtnType.CAN,
					userRequestId,null);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.CDN);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.CDNA);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.CDNA, SaveToGstnOprtnType.CAN,
					userRequestId,null);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.CDNA);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.ISD);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.ISD, SaveToGstnOprtnType.CAN,
					userRequestId,APIConstants.CR);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.ISD);
			
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.ISD);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.ISD, SaveToGstnOprtnType.CAN,
					userRequestId,APIConstants.INV);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.ISD);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.ISDA);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.ISDA, SaveToGstnOprtnType.CAN,
					userRequestId,APIConstants.RCR);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.ISDA);
			
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG3, APIConstants.ISDA);
			saveCanData.findGstr6CanInvoices(gstin, retPeriod, groupCode, APIConstants.ISDA, SaveToGstnOprtnType.CAN,
					userRequestId,APIConstants.RNV);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG4, APIConstants.ISDA);
		} catch (Exception ex) {
			String msg = "Unexpcted Error";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr6 can SaveToGstn Job has Finshed");
		}

	}

	public void saveActiveInvoices(String jsonReq, String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr6 SaveToGstn Job has Started");
		}
		try {
			/*
			 * JsonObject requestObject = (new JsonParser()).parse(jsonReq)
			 * .getAsJsonObject(); Gson gson = GsonUtil.newSAPGsonInstance();
			 * Gstr1SaveToGstnReqDto requestDto = gson.fromJson(requestObject,
			 * Gstr1SaveToGstnReqDto.class);
			 * 
			 * List<Pair<String, String>> gstinRetPeriodPairs = screenExtractor
			 * .getTestGstr1CombinationPairs(requestDto, groupCode);
			 */

			JsonObject requestObject = (new JsonParser()).parse(jsonReq).getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			SaveToGstnReqDto dto = gson.fromJson(requestObject, SaveToGstnReqDto.class);

			/* gstinRetPeriodPairs.forEach(pair -> { */

			String gstin = dto.getGstin();
			String retPeriod = dto.getReturnPeriod();
			Long userRequestId = dto.getUserRequestId();

			// Invoices Data
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2B);
			saveData.findGstr6SaveInvoices(gstin, retPeriod, groupCode, APIConstants.B2B, null,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2B);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.B2BA);
			saveData.findGstr6SaveInvoices(gstin, retPeriod, groupCode, APIConstants.B2BA, null,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.B2BA);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDN);
			saveData.findGstr6SaveInvoices(gstin, retPeriod, groupCode, APIConstants.CDN, null,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDN);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.CDNA);
			saveData.findGstr6SaveInvoices(gstin, retPeriod, groupCode, APIConstants.CDNA, null,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.CDNA);			
				
				LOGGER.debug(LOG1, APIConstants.ISD);
			saveData.findGstr6SaveInvoices(gstin, retPeriod, groupCode, APIConstants.ISD, APIConstants.INV,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.ISD);
			
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.ISD);
			saveData.findGstr6SaveInvoices(gstin, retPeriod, groupCode, APIConstants.ISD, APIConstants.CR,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.ISD);
			if (LOGGER.isDebugEnabled())
			
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.ISDA);
			saveData.findGstr6SaveInvoices(gstin, retPeriod, groupCode, APIConstants.ISDA, APIConstants.RNV,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.ISDA);
			
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG1, APIConstants.ISDA);
			saveData.findGstr6SaveInvoices(gstin, retPeriod, groupCode, APIConstants.ISDA, APIConstants.RCR,
					SaveToGstnOprtnType.SAVE, userRequestId);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(LOG2, APIConstants.ISDA);
			/* }); */
			
			//Allow next Save If there is now new data to SAVE immediately.
			gstnUserRequestUtil.allowNextSaveIfNoDataToSaveNow(gstin, retPeriod,
					APIConstants.SAVE.toUpperCase(),
					APIConstants.GSTR6.toUpperCase(), groupCode, userRequestId);

		} catch (Exception ex) {
			String msg = "Unexpcted Error";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Gstr6 SaveToGstn Job has Finshed");
		}

	}

}
