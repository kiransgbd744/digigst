package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.gstr3b.Gstr3bSaveSubmitReportService;
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
 * @author Arun KA
 *
 */

@Slf4j
@Service("Gstr3bSaveSubmitReportProcessor")
public class Gstr3bSaveSubmitReportProcessor implements TaskProcessor{
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	@Autowired
	@Qualifier("Gstr3bSaveSubmitReportServiceImpl")
	Gstr3bSaveSubmitReportService gstr3bSaveSubmitReportService;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long id = json.get("id").getAsLong();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside GSTR3B save submit report download"
					+ " processor with Report id : %d",
					id);
			LOGGER.debug(msg);
		}

		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		if (LOGGER.isDebugEnabled()) {
			String msg ="Updated file status as 'REPORT_GENERATION_INPROGRESS'";
			LOGGER.debug(msg);
		}

		try {
			gstr3bSaveSubmitReportService.generateSaveSubmitReports(id);
		} catch (Exception ex) {
			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			String msg = "Exception occured while downloading csv report";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

		
	}

}
