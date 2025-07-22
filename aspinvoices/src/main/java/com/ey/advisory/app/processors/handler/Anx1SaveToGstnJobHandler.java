package com.ey.advisory.app.processors.handler;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.anx1.Anx1SaveToGstnReqDto;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.Anx1CancelledInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.Anx1SaveInvicesIdentifier;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.ScreenDeciderAndExtractor;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIException;
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
@Service("Anx1SaveToGstnJobHandler")
@Slf4j
public class Anx1SaveToGstnJobHandler {

	private static final String LOG1 = "Anx1 {} SaveToGstn section has Started";
	private static final String LOG2 = "Anx1 {} SaveToGstn section has completed";

	@Autowired
	@Qualifier("anx1SaveInvicesIdentifierImpl")
	private Anx1SaveInvicesIdentifier saveData;

	@Autowired
	@Qualifier("anx1CancelledInvicesIdentifierImpl")
	private Anx1CancelledInvicesIdentifier gstnCancelData;
	
	@Autowired
	@Qualifier("screenDeciderAndExtractorImpl")
	private ScreenDeciderAndExtractor screenExtractor;
	
	public void saveCancelledInvoices(String jsonReq, String groupCode) {

		if(LOGGER.isDebugEnabled()) {
		LOGGER.debug("Anx1 CAN Processed Invoices SaveToGstn Job has Started {}",
				jsonReq);
		}
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			Anx1SaveToGstnReqDto requestDto = gson.fromJson(requestObject,
					Anx1SaveToGstnReqDto.class);
			
			List<Pair<String, String>> gstinRetPeriodPairs = 
					screenExtractor.getAnx1CombinationPairs(requestDto, groupCode);
			
			gstinRetPeriodPairs.forEach(pair -> {

				String gstin = pair.getValue0();
				String retPeriod = pair.getValue1();

				gstnCancelData.findCanInvoices(gstin, retPeriod, groupCode,
						SaveToGstnOprtnType.CAN);

			});
		} catch (Exception ex) {
			String msg = "Error while executing Anx1 SaveToGstn";
			LOGGER.error(msg, ex);
			throw new APIException(msg, ex);
		}
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Anx1 CAN Processed Invoices SaveToGstn Job has Finshed");
			}
	}

	public void saveActiveInvoices(String jsonReq, String groupCode) {

		LOGGER.debug("Anx1 Active Processed Invoices SaveToGstn Job has Started");
		try {
			
			JsonObject requestObject = (new JsonParser()).parse(jsonReq)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			Anx1SaveToGstnReqDto requestDto = gson.fromJson(requestObject,
					Anx1SaveToGstnReqDto.class);
			
			List<Pair<String, String>> gstinRetPeriodPairs = 
					screenExtractor.getAnx1CombinationPairs(requestDto, groupCode);
			
			gstinRetPeriodPairs.forEach(pair -> {
				
				String gstin = pair.getValue0();
				String retPeriod = pair.getValue1();
				
				requestDto.getTables().forEach(table -> {

					LOGGER.debug(LOG1, table);
					saveData.findAnx1SaveInvoices(gstin, retPeriod, groupCode,
							table, null, SaveToGstnOprtnType.SAVE);
					LOGGER.debug(LOG2, table);

				});
				
			});
			
			

			/*LOGGER.debug(LOG1, APIConstants.B2C);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.B2C,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.B2C);

			LOGGER.debug(LOG1, APIConstants.B2B);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.B2B,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.B2B);
			
			LOGGER.debug(LOG1, APIConstants.EXPWP);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.EXPWP,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.EXPWP);

			LOGGER.debug(LOG1, APIConstants.EXPWOP);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.EXPWOP,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.EXPWOP);

			LOGGER.debug(LOG1, APIConstants.SEZWP);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.SEZWP,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.SEZWP);

			LOGGER.debug(LOG1, APIConstants.SEZWOP);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.SEZWOP,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.SEZWOP);

			LOGGER.debug(LOG1, APIConstants.DE);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.DE, null,
					SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.DE);

			LOGGER.debug(LOG1, APIConstants.REV);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.REV,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.REV);

			LOGGER.debug(LOG1, APIConstants.IMPS);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.IMPS,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.IMPS);

			LOGGER.debug(LOG1, APIConstants.IMPG);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.IMPG,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.IMPG);

			LOGGER.debug(LOG1, APIConstants.IMPGSEZ);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.IMPGSEZ,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.IMPGSEZ);

			LOGGER.debug(LOG1, APIConstants.ECOM);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.ECOM,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.ECOM);
			
			LOGGER.debug(LOG1, APIConstants.MIS);
			saveData.findAnx1SaveInvoices(jsonReq, groupCode, APIConstants.MIS,
					null, SaveToGstnOprtnType.SAVE);
			LOGGER.debug(LOG2, APIConstants.MIS);*/

		} catch (Exception ex) {
			String msg = "Error while executing Anx1 SaveToGstn";
			LOGGER.error(msg, ex);
			throw new APIException(msg, ex);
		}
		if(LOGGER.isDebugEnabled()) {
		LOGGER.debug("Anx1 Active Processed Invoices SaveToGstn Job has Finshed");
		}
	}
}
