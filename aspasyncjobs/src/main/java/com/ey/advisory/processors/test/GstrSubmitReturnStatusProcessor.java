/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.services.refidpolling.submit.SubmitBatchIdPollingManager;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.PollingMessage;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("GstrSubmitReturnStatusProcessor")
public class GstrSubmitReturnStatusProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1SubmitBatchIdPollingManager")
	private SubmitBatchIdPollingManager batchManager;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"GSTR1_SUBMIT ReturnStatus Data Execute method is ON with message {} ",
					message);
		}
		String groupcode = message.getGroupCode();
		String jsonReq = message.getParamsJson();
		if (jsonReq != null && groupcode != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Pooling Gstr1 Submit ReturnStatus- Request is {} ",
						jsonReq);
			}
			Gson gson = new Gson();
			PollingMessage reqDto = gson.fromJson(jsonReq,
					PollingMessage.class);

			if (reqDto != null) {

				GstnSubmitEntity batch = new GstnSubmitEntity();
				batch.setId(reqDto.getBatchId());
				batch.setGstin(reqDto.getGstin());
				batch.setRetPeriod(reqDto.getTaxPeriod());
				batch.setRefId(reqDto.getRefId());
				batch.setReturnType(reqDto.getReturnType());
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Pooling Gstr1 Submit ReturnStatus- batch is {} ",
							batch);
				}

				batchManager.processBatch(groupcode, batch);
			}

		}

	}

}
