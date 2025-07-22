package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorComplianceAsyncReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Saif.S
 *
 */
@Slf4j
@Component("VendorComplianceRatingAsyncReportProcessor")
public class VendorComplianceRatingAsyncReportProcessor
		implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("VendorComplianceRatingAsyncReportServiceImpl")
	VendorComplianceAsyncReportService asyncReportDownloadService;

	@Override
	public void execute(Message message, AppExecContext context) {

		try {
			String jsonString = message.getParamsJson();
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(jsonString);

			Long id = json.get("id").getAsLong();
			String source = json.get("source").getAsString();
			Long entityId = json.get("entityId").getAsLong();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Vendor Compliance Rating Async Report "
								+ " Download processor with Report id : %d",
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

			asyncReportDownloadService.generateReports(id, source, entityId);
		} catch (Exception ex) {
			String msg = "Exception occured while downloading Vendor "
					+ " Compliance Rating Report";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

}
