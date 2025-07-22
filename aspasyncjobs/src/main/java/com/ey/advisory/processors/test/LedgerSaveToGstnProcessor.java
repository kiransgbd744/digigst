package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.LedgerSaveToGstnRcmEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.LedgerSaveToGstnRcmRepository;
import com.ey.advisory.app.services.ledger.LedgerTotalBalanceSummService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("LedgerSaveToGstnProcessor")
@Slf4j
public class LedgerSaveToGstnProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	@Autowired
	@Qualifier("LedgerTotalBalanceSummServiceImpl")
	LedgerTotalBalanceSummService ledgerSaveToGstn;

	@Autowired
	@Qualifier("LedgerSaveToGstnRcmRepository")
	LedgerSaveToGstnRcmRepository ledgerSaveToGstnRcmRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin GSTR2aVs3BInitiateReconProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();

		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

		String gstin = json.get("gstin").getAsString();
		String ledgerType = json.get("ledgerType").getAsString();
		Long id = null;
		try {

			LedgerSaveToGstnRcmEntity entity = ledgerSaveToGstnRcmRepository
					.findByGstinAndIsActiveTrueAndLedgerTp(gstin, ledgerType);
			id = entity.getId();
			ledgerSaveToGstn.saveToGstnRcmLedger(entity, id);

		}

		catch (Exception ex) {
			LOGGER.error("Exception occured in  saveToGstnRcmLedger ", ex);
			/*ledgerSaveToGstnRcmRepository.updateStatusById("FAILED",
					LocalDateTime.now(), null, null,
					id);*/
			
			String errorMessage = ex.getMessage();
			if (errorMessage == null || errorMessage.isEmpty()) {
				errorMessage = "Unknown error occurred";
			}
			ledgerSaveToGstnRcmRepository.updateStatusById("FAILED",
					LocalDateTime.now(), null, errorMessage, id);
			throw new AppException(ex);
		}

	}

}