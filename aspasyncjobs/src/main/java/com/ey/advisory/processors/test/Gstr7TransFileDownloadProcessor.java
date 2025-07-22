package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.search.gstr7trans.Gstr7TransReportDownloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Akhilesh.Yadav
 *
 */

@Slf4j
@Component("Gstr7TransFileDownloadProcessor")
public class Gstr7TransFileDownloadProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("Gstr7TransReportDownloadServiceImpl")
	private Gstr7TransReportDownloadService reportDwnldService;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr7TransFileDownloadProcessor Download Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

		Long id = json.get("id").getAsLong();

		try {

			String reportType = json.get("reportType").getAsString();

			fileStatusDownloadReportRepo.updateStatus(id,
					"REPORT_GENERATION_INPROGRESS", null, LocalDateTime.now());

			reportDwnldService.generateReports(Long.valueOf(id), reportType);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstr7TransFileDownloadProcessor Executed.");
			}

		} catch (AppException e) {
			LOGGER.error("Exception in Gstr7TransFileDownloadProcessor ",
					e.getMessage());
			fileStatusDownloadReportRepo.updateStatus(id,
					"REPORT_GENERATION_FAILED ", null, LocalDateTime.now());
			throw new AppException(e);
		}
	}
}