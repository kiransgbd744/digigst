package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.search.gstr8.Gstr8ReportDownloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("Gstr8FileDownloadProcessor")
public class Gstr8FileDownloadProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("Gstr8ReportDownloadServiceImpl")
	private Gstr8ReportDownloadService reportDwnldService;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr8FileDownloadProcessor Download Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		String filePath = null;

		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long id = json.get("id").getAsLong();

		try {

			String reportType = json.get("reportType").getAsString();

			fileStatusDownloadReportRepo.updateStatus(id,
					"REPORT_GENERATION_INPROGRESS", null, LocalDateTime.now());

			reportDwnldService.generateReports(Long.valueOf(id), reportType);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("updating filePath {} in REPORT_DOWNLOAD_REQUEST"
						+ " table :", filePath);
			}

		} catch (AppException e) {
			LOGGER.error("Exception in Gstr8FileDownloadProcessor ",
					e.getMessage());
			fileStatusDownloadReportRepo.updateStatus(id,
					"REPORT_GENERATION_FAILED ", null, LocalDateTime.now());
			throw new AppException(e);
		}
	}
}
