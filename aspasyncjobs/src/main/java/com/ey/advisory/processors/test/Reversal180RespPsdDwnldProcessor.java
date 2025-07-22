package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.service.days.revarsal180.Reversal180DaysResponseDwnldService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Service("Reversal180RespPsdDwnldProcessor")
public class Reversal180RespPsdDwnldProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("Reversal180DaysResponseDwnldServiceImpl")
	Reversal180DaysResponseDwnldService service;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long id = json.get("id").getAsLong();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside Revarsal180RespPsdDwnldProcessor with Report id : %d",
					id);
			LOGGER.debug(msg);
		}

		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
				LocalDateTime.now());

		if (LOGGER.isDebugEnabled()) {
			String msg = "Updated file status as 'REPORT_GENERATION_INPROGRESS'";
			LOGGER.debug(msg);
		}

		try {
			service.generateReports(id);
		} catch (Exception ex) {
			String msg = "Exception occured while downloading csv report";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}
}
