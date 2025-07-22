/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AEinvSeriesCompRepo;
import com.ey.advisory.app.data.services.Gstr1A.GSTR1AEinvSeriesCompService;
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
@Component("GSTR1AEinvSeriesAutoCompProcessor")
public class GSTR1AEinvSeriesAutoCompProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GSTR1AEinvSeriesCompServiceImpl")
	private GSTR1AEinvSeriesCompService invServiceImpl;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private Gstr1AEinvSeriesCompRepo gstr1AEinvSeriesCompRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		String gstin = json.get("gstin").getAsString();
		String retPeriod = json.get("retPeriod").getAsString();
		Long configId = json.get("configId").getAsLong();
		String implType = json.get("implType").getAsString();
		try {
			gstr1AEinvSeriesCompRepo.updateRequestStatus(configId,
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
