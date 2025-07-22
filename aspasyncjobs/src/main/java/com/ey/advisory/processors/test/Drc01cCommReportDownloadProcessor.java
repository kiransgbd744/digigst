package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.drc.Drc01AutoGetCallDetailsRepository;
import com.ey.advisory.app.data.repositories.client.drc.Drc01RequestCommDetailsRepository;
import com.ey.advisory.app.services.search.filestatussearch.Drc01cCommDownloadSummaryServiceImpl;
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



@Slf4j
@Component("Drc01cCommReportDownloadProcessor")
public class Drc01cCommReportDownloadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Drc01RequestCommDetailsRepository")
	private Drc01RequestCommDetailsRepository drc01RequestCommDetailsRepo;

	@Autowired
	@Qualifier("Drc01cCommDownloadSummaryServiceImpl")
	private Drc01cCommDownloadSummaryServiceImpl asyncReportDownloadService;
	
	@Autowired
	@Qualifier("Drc01AutoGetCallDetailsRepository")
	private Drc01AutoGetCallDetailsRepository drc01AutoGetCallDetailsRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Long requestId = json.get("requestId").getAsLong();
		Long id = json.get("id").getAsLong();
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Async Report Download processor with Report requestId : %d",
						requestId);
				LOGGER.debug(msg);
			}
			drc01RequestCommDetailsRepo.updateStatus(requestId,
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			drc01AutoGetCallDetailsRepo.updateStatus(
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, id,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			
			asyncReportDownloadService.generateReports(requestId,id);				
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured during Drc01b comm request report generation.");
			LOGGER.error(msg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}
}
