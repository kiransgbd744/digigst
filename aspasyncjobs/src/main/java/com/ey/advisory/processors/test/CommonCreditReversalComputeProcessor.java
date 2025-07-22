package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr3bItcStatusEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3bItcStatusRepository;
import com.ey.advisory.app.services.credit.reversal.CreditReversalProcessDaoImpl;
import com.ey.advisory.app.services.credit.reversal.CreditReversalProcessServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("CommonCreditReversalComputeProcessor")
public class CommonCreditReversalComputeProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("CreditReversalProcessServiceImpl")
	private CreditReversalProcessServiceImpl credRevsalProcServImpl;

	@Autowired
	@Qualifier("Gstr3bItcStatusRepository")
	private Gstr3bItcStatusRepository gstr3bItcStatusRepository;

	@Autowired
	@Qualifier(value = "CreditReversalProcessDaoImpl")
	private CreditReversalProcessDaoImpl daoImpl;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin VendorBulkEmailProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}
		String msg = null;
		Long id = null;
		Long entityId = null;
		try {
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			id = json.get("id").getAsLong();
			entityId = json.get("entityId").getAsLong();
			Gstr3bItcStatusEntity entity = gstr3bItcStatusRepository
					.getDetailsById(id);
			gstr3bItcStatusRepository
					.gstr3bItcStatusUpdate(APIConstants.INPROGRESS, id);
			int count = daoImpl.proceCallForComputeReversal(entity.getGstin(),
					entity.getDeriverdRetPeriod());
			LOGGER.debug("Count {} ", count);
			gstr3bItcStatusRepository.gstr3bItcStatusUpdate(
					count > 0 ? APIConstants.SUCCESS : APIConstants.FAILED, id);
		} catch (Exception ex) {
			msg = String.format(
					"Error while Executing CommonCreditReversalComputeProcessor to call "
							+ " proc for id :%s group code :%s and entityId :%s",
					id, message.getGroupCode(), entityId);
			gstr3bItcStatusRepository.gstr3bItcStatusUpdate(APIConstants.FAILED,
					id);
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
	}

}
