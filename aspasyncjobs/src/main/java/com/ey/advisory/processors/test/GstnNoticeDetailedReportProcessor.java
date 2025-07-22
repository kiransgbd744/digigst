package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.AsyncReportDownloadService;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("GstnNoticeDetailedReportProcessor")
public class GstnNoticeDetailedReportProcessor implements TaskProcessor {
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("GstnNoticeDetailedReportDwldServiceImpl")
	AsyncReportDownloadService asyncReportDownloadService;
	
	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		/*JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);*/
		JsonObject json = JsonParser.parseString(jsonString)
        		.getAsJsonObject();

		Long id = json.get("id").getAsLong();
		String reportType = json.get("reportType").getAsString();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside FileStatus Ims Async Report Download processor with Report id : %d",
					id);
			LOGGER.debug(msg);
		}

		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		if (LOGGER.isDebugEnabled()) {
			String msg = "Updated file status as 'REPORT_GENERATION_INPROGRESS'";
			LOGGER.debug(msg);
		}
		try {
				asyncReportDownloadService.generateReports(id);
			
		} catch (Exception ex) {
			String msg = "Exception occured while downloading csv report";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}


}
