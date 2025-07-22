/**
 * 
 */
package com.ey.advisory.processors.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.einvseries.GSTR1EinvSeriesCompService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 * 
 */
@Slf4j
@Component("EinvSeriesAutoReconCompProcessor")
public class EinvSeriesAutoReconCompProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("EinvSeriRecPattServiceImpl")
	private GSTR1EinvSeriesCompService invServiceImpl;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void execute(Message message, AppExecContext context) {

		JsonParser parser = new JsonParser();
		String jsonString = message.getParamsJson();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		String gstnDetails = json.get("gstins").getAsString();
		List<String> gstinList = new ArrayList<String>(
				Arrays.asList(gstnDetails.split(",")));
		String retPeriod = json.get("retPeriod").getAsString();
		try {
			for (String gstn : gstinList) {
				invServiceImpl.compandperstSeriesData(0L, gstn, retPeriod, "B");
			}
		} catch (Exception ex) {
			String errMsg = "Error occured while generating Einvoice Series.";
			LOGGER.error(errMsg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}
}
