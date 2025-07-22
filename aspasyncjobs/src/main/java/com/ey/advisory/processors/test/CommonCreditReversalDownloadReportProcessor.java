package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.credit.reversal.ExceptionalTaggingDownloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component("CommonCreditReversalDownloadReportProcessor")
public class CommonCreditReversalDownloadReportProcessor implements TaskProcessor {
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("ExceptionalTaggingDownloadServiceImpl")
	private ExceptionalTaggingDownloadService reportDwnldService;
	
	@Override
	public void execute(Message message, AppExecContext context) {
	
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonElement jsonElement = JsonParser.parseString(jsonString);
		//JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"CommonCreditReversalDownloadReportProcessor Download Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		JsonObject json = jsonElement.getAsJsonObject();
		//JsonObject json = (JsonObject) parser.parse(jsonString);

		Long id = json.get("id").getAsLong();

		try {
			
			String reportName = json.get("reportType").getAsString();
			
			fileStatusDownloadReportRepo.updateStatus(id,
					"REPORT_GENERATION_INPROGRESS", null, LocalDateTime.now());

		    reportDwnldService.getData(Long.valueOf(id),reportName);

		} catch (AppException e) {
			LOGGER.error("Exception in CommonCreditReversalDownloadReportProcessors ",
					e.getMessage());
			fileStatusDownloadReportRepo.updateStatus(id, 
					"REPORT_GENERATION_FAILED ", null, LocalDateTime.now());
			throw new AppException(e);
		}
	}
}

