/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr1EinvSeriesCompRepo;
import com.ey.advisory.app.data.services.einvseries.GSTR1EinvSeriesCompService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
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
@Component("GSTR1EinvSeriesAutoCompProcessor")
public class GSTR1EinvSeriesAutoCompProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GSTR1EinvSeriesCompServiceImpl")
	private GSTR1EinvSeriesCompService invServiceImpl;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private Gstr1EinvSeriesCompRepo gstr1EinvSeriesCompRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		JsonParser parser = new JsonParser();
		String jsonString = message.getParamsJson();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		String gstin = json.get("gstin").getAsString();
		String retPeriod = json.get("retPeriod").getAsString();
		Long configId = json.get("configId").getAsLong();
		String implType = json.get("implType").getAsString();
		try {
			gstr1EinvSeriesCompRepo.updateRequestStatus(configId,
					APIConstants.INPROGRESS, null, null);
			invServiceImpl.compandperstSeriesData(configId, gstin, retPeriod,
					implType);
		} catch (Exception ex) {
			String errMsg = "Error occured while invoking Auto Invoice Series.";
			LOGGER.error(errMsg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}
}
