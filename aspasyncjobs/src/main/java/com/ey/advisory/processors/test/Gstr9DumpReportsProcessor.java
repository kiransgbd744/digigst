package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("Gstr9DumpReportsProcessor")
public class Gstr9DumpReportsProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("Gstr9DumpAsyncReportDownloadServiceImpl")
	AsyncReportDownloadService asyncReportDownloadService;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String jsonString = message.getParamsJson();
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(jsonString);

			Long id = json.get("id").getAsLong();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside GStr9 Dump Async Report Download processor with Report id : %d",
						id);
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
					LocalDateTime.now());

			asyncReportDownloadService.generateReports(id);
		} catch (Exception ex) {
			String msg = "Exception occured while downloading csv report";
			LOGGER.error(msg,ex);
			throw new AppException(msg, ex);
		}
	}

}
