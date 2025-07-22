package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.service.report.gstr3b.Gstr3bReportDownloadService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 */
@Service("Gstr3bOutwardReportProcessor")
@Slf4j
public class Gstr3bOutwardReportProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	@Autowired
	@Qualifier("Gstr3bReportDownloadServiceImpl")
	Gstr3bReportDownloadService gstr3bReportDownloadService;

	@Override
	public void execute(Message message, AppExecContext context) {

		String inputJson = message.getParamsJson();
		JsonObject json = (new JsonParser()).parse(inputJson).getAsJsonObject();

		Long id = json.get("id").getAsLong();

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Inside Gstr3b Outward Report processor "
					+ "with request Id : %s", id.toString());
			LOGGER.debug(msg);
		}

		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

		try {
			gstr3bReportDownloadService.generateOutwardReport(id);
		} catch (Exception ex) {
			String msg = "Exception occured while generating GSTR3B outward report";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

}
