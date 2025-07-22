package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryAtGstn;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */

@Slf4j
@Component("Gstr1GetSummaryProcessor")
public class Gstr1GetSummaryProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("gstr1SummaryAtGstnImpl")
	Gstr1SummaryAtGstn gstr1SummaryGstnData;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Starting Gstr1GetSummaryProcessor Job for"
							+ " -> params: '%s', GroupCode: '%s'",
					jsonString, groupCode);
			LOGGER.debug(msg);
		}
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		String gstin = requestObject.get("gstin").getAsString();
		String retPeriod = requestObject.get("retPeriod").getAsString();
		gstr1GetCall(gstin, retPeriod, groupCode);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("GSTR1 GET Call is done -> GSTIN: '%s', "
					+ "Taxperiod: '%s'", gstin, retPeriod);
			LOGGER.debug(msg);
		}
	}

	private void gstr1GetCall(String gstin, String returnPeriod,
			String groupCode) {
		try {
			LOGGER.debug("GSTR1 GSTN Data Summary Execution BEGIN ");
			Gstr1GetInvoicesReqDto reqDto = new Gstr1GetInvoicesReqDto();
			reqDto.setGstin(gstin);
			reqDto.setReturnPeriod(returnPeriod);
			String getResp = gstr1SummaryGstnData.getGstr1Summary(reqDto,
					groupCode);
			LOGGER.debug("GSTR1 OUTWARD GSTN Data Summary Execution END ");
			if (!getResp.equals("SUCCESS")) {
				String msg = String.format(
						"Recieved error from GSTN for Gstr1"
								+ " summary Get call for gstin '%s' and taxperiod"
								+ " '%s' in Gstr1GetSummaryProcessor. Error: %s",
						gstin, returnPeriod, getResp);
				LOGGER.error(msg);
			} else {
				LOGGER.debug("GSTN GET Summary Completed");
			}
		} catch (Exception ex) {
			String msg = "Exception while GSTR1 GET Call from Gstr1GetSummaryProcessor";
			LOGGER.error(msg, ex);
			throw new AppException(ex, msg);
		}
	}

}
