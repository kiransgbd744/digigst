package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.ClientFilingStatusEntity;
import com.ey.advisory.app.data.repositories.client.ClientFilingStatusRepositoty;
import com.ey.advisory.app.data.services.noncomplaintvendor.ComplaintClientCommunicationServiceImpl;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("InitiateGetClientFilingStatusProcessor")
@Slf4j
public class InitiateGetClientFilingStatusProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ComplaintClientCommunicationServiceImpl")
	private ComplaintClientCommunicationServiceImpl complaintClientServiceImpl;

	@Autowired
	@Qualifier("ClientFilingStatusRepositoty")
	private ClientFilingStatusRepositoty returnDataStorageStatusRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		JsonArray asJsonArray = json.get("requestIds").getAsJsonArray();
		for (JsonElement element : asJsonArray) {
			Long requestId = Long.parseLong(element.toString());
			Optional<ClientFilingStatusEntity> returnDataStatusEntity = Optional
					.empty();
			try {
				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("InitiateGetClientFilingStatusProcessor Job"
									+ " executing for params %s", jsonString);
					LOGGER.debug(msg);
				}

				String financialYear = json.get("financialYear").getAsString();
				returnDataStatusEntity = returnDataStorageStatusRepo
						.findById(requestId);
				if (returnDataStatusEntity.isPresent()) {
					returnDataStatusEntity.get().setStatus("InProgress");
					returnDataStatusEntity.get()
							.setModifiedOn(LocalDateTime.now());
					returnDataStorageStatusRepo
							.save(returnDataStatusEntity.get());
					
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.COMP_HISTORY_RET);
					complaintClientServiceImpl
							.persistGstnApiForSelectedFinancialYear(
									financialYear,
									returnDataStatusEntity.get().getGstin());
					returnDataStatusEntity.get().setStatus("Completed");
					returnDataStatusEntity.get()
							.setModifiedOn(LocalDateTime.now());
					returnDataStorageStatusRepo
							.save(returnDataStatusEntity.get());

					JsonObject jsonParams = new JsonObject();
					jsonParams.addProperty("gstin",
							returnDataStatusEntity.get().getGstin());
					jsonParams.addProperty("section",
							returnDataStatusEntity.get().getReturnType());
					jsonParams.addProperty("finYear",
                            returnDataStatusEntity.get().getFinancialYear());
					jsonParams.addProperty("scenarioName",
							JobConstants.COMP_HISTORY_REV);

					asyncJobsService.createJob(message.getGroupCode(),
							JobConstants.COMP_HISTORY_REV,
							jsonParams.toString(), "SYSTEM",
							JobConstants.PRIORITY, message.getId(),
							JobConstants.SCHEDULE_AFTER_IN_MINS);
				}

			} catch (Exception e) {
				LOGGER.error(
						"Exception while processing the vendor filing request:",
						e);
				if (returnDataStatusEntity.isPresent()) {
					returnDataStatusEntity.get().setStatus("Failed");
					returnDataStatusEntity.get()
							.setModifiedOn(LocalDateTime.now());
					returnDataStorageStatusRepo
							.save(returnDataStatusEntity.get());
				}
			}
		}
	}
}
