package com.ey.advisory.processors.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.docs.CommonCreditReversalFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component("CommonCreditReversalFileArrivalProcesser")
public class CommonCreditReversalFileArrivalProcesser implements TaskProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CommonCreditReversalFileArrivalProcesser.class);

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("CommonCreditReversalFileArrivalHandler")
	private CommonCreditReversalFileArrivalHandler commonCreditReversalFileArrivalHandler;


	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		// TODO Auto-generated method stub

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		String docId = json.get("docId").getAsString();
		LOGGER.debug("Inward GSTR6 Distribution file Arrived");
		commonCreditReversalFileArrivalHandler.processProductFile(message, context,docId);
		
		LOGGER.debug("Gstr6 Distribution file Arrival processed");
	}

}
