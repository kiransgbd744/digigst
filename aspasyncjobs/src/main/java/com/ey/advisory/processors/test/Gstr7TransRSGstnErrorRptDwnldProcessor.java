package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.search.gstr7trans.Gstr7TransReportDownloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
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
@Component("Gstr7TransRSGstnErrorRptDwnldProcessor")
public class Gstr7TransRSGstnErrorRptDwnldProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("Gstr7TransRSGstnErrorRptDwnldServiceImpl")
	private Gstr7TransReportDownloadService reportDwnldService;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		
		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Long id = json.get("id").getAsLong();
		String reportType = json.get("reportType").getAsString();
		String groupCode = message.getGroupCode();

		try {
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr7TransRSGstnErrorRptDwnldProcessor Download Job"
								+ " executing for groupcode %s and params %s",
						groupCode, jsonString);
				LOGGER.debug(msg);
			}
			
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null, 
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			reportDwnldService.generateReports(Long.valueOf(id), reportType);
			
		} catch (AppException e) {
			
			String msg = String.format(
					"Exception in Gstr7TransRSGstnErrorRptDwnldProcessor");
			LOGGER.error(msg, e);
			
			fileStatusDownloadReportRepo.updateStatus(id,
					"REPORT_GENERATION_FAILED ", null, LocalDateTime.now());
			throw new AppException(e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}
}