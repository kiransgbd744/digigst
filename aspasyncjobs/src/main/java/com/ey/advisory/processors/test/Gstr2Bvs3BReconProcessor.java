/**
 * 
 */
package com.ey.advisory.processors.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr2bvs3bStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2bVs3bStatusRepository;
import com.ey.advisory.app.gstr3b.Gstr2bvs3bProcProcessDaoImpl;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("Gstr2Bvs3BReconProcessor")
public class Gstr2Bvs3BReconProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2bvs3bProcProcessDaoImpl")
	Gstr2bvs3bProcProcessDaoImpl daoImpl;

	@Autowired
	@Qualifier(value = "Gstr2bVs3bStatusRepository")
	private Gstr2bVs3bStatusRepository gstr2bVs3bStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin SalesRegisterInitiateReconProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long batchId = json.get("batchId").getAsLong();

		List<Gstr2bvs3bStatusEntity> entityList = gstr2bVs3bStatusRepository
				.getGstr2bvs3bData(batchId);
		for (Gstr2bvs3bStatusEntity entity : entityList) {
			daoImpl.proceCallForComputeReversal(batchId, entity.getGstin(),
					entity.getTaxPeriodFrom(), entity.getTaxPeriodTo());
			gstr2bVs3bStatusRepository.gstr2bvs3bUpdateSuccessStatus(batchId);
		}		
	}

}
