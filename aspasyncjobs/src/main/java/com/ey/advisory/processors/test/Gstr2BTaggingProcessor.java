package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr2BMonitorTagging2ARepository;
import com.ey.advisory.app.gstr2b.Gstr2BTaggingServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Slf4j
@Component("Gstr2BTaggingProcessor")
public class Gstr2BTaggingProcessor implements TaskProcessor {

	@Autowired
	private Gstr2BMonitorTagging2ARepository monitorTaggingRepo;

	@Autowired
	private Gstr2BTaggingServiceImpl serviceImpl;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Gstr2B Get Call Job"
								+ " executing for groupcode {} and params {}",
						groupCode, jsonString);
			}
			JsonObject reqJson = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			Long id = reqJson.get("id").getAsLong();
			String gstin = reqJson.get("gstin").getAsString();
			String taxPeriod = reqJson.get("taxPeriod").getAsString();
			Long invocationId = reqJson.get("invocationId").getAsLong();
			String section = reqJson.get("section").getAsString();
			String source = reqJson.get("source").getAsString();
			// based on the id make update
			monitorTaggingRepo.updateStatus(APIConstants.INPROGRESS, null,id);
			if (APIConstants.GSTR2A.equalsIgnoreCase(source)) {
				serviceImpl.persistAndCallTagProcsForGstr2A(id, gstin,
						taxPeriod, invocationId, section, source);
			} else {
				serviceImpl.persistAndCallTagProcsForGstr2B(id, gstin, taxPeriod,
						invocationId, section, source);
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while initiating 2B Tagging", ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}

}
