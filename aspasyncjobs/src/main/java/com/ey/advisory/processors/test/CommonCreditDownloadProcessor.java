/**
 */
package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.AsyncReportDownloadService;
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
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("CommonCreditDownloadProcessor")
public class CommonCreditDownloadProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("AsyncCommonCreditDownloadSummaryServiceImpl")
	private AsyncReportDownloadService asyncReportSummaryDownloadService;

	@Autowired
	@Qualifier("AsyncCommonCreditDownloadVerticalServiceImpl")
	private AsyncReportDownloadService asyncReportVerticalDownloadService;

	@Autowired
	@Qualifier("AsyncCommonCreditDownloadOutwardServiceImpl")
	private AsyncReportDownloadService asyncReportOutwardDownloadService;

	@Autowired
	@Qualifier("AsyncCommonCreditDownloadInwardServiceImpl")
	private AsyncReportDownloadService asyncReportInwardDownloadService;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Long id = json.get("id").getAsLong();
		String dataType = json.get("dataType").getAsString();

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Async Report Download processor with Report id : %d",
						id);
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			if ("Summary".equalsIgnoreCase(dataType)) {
				asyncReportSummaryDownloadService.generateReports(id);
			} else if ("Vertical".equalsIgnoreCase(dataType)) {
				asyncReportVerticalDownloadService.generateReports(id);
			} else if ("Outward".equalsIgnoreCase(dataType)) {
				asyncReportOutwardDownloadService.generateReports(id);
			} else if ("Inward".equalsIgnoreCase(dataType)) {
				asyncReportInwardDownloadService.generateReports(id);
			} else {
				String errMsg = String.format("Invalid Report Type %s",
						dataType);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured during Common Credit Summary report generation.");
			LOGGER.error(msg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}
}
