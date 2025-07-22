package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.gstr2.reconresults.filter.Gstr2ReconResultRptDownloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("Gstr2ReconResultReportDwnldProcessor")
public class Gstr2ReconResultReportDwnldProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2APRReconResultsRptDownloadServiceImpl")
	private Gstr2ReconResultRptDownloadService report2APRService;

	@Autowired
	@Qualifier("Gstr2BPRReconResultsRptDownloadServiceImpl")
	private Gstr2ReconResultRptDownloadService report2BPRService;

	@Autowired
	@Qualifier("FileStatusDownloadReportRepository")
	FileStatusDownloadReportRepository fileStatusRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr2ReconResultReportDwnldProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long id = json.get("id").getAsLong();
		String reconType = json.get("reconType").getAsString();

		Gson gson = new Gson();
		String resp = null;

		try {
			
			fileStatusRepo.updateStatusAndCompltdOn(id,
					ReconStatusConstants.REPORT_GENERATION_INPROGRESS,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			if (LOGGER.isDebugEnabled()) {
				String msg ="Updated file status as 'REPORT_GENERATION_INPROGRESS'";
				LOGGER.debug(msg);
			}
			
			if ("2A_PR".equalsIgnoreCase(reconType)) {
				resp = report2APRService.generateReconResultReport(id);
			} else {
				resp = report2BPRService.generateReconResultReport(id);
			}

			if ("SUCCESS".equalsIgnoreCase(resp)) {
				fileStatusRepo.updateStatusAndCompltdOn(id,
						ReconStatusConstants.REPORT_GENERATED,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			}else if("NO_DATA".equalsIgnoreCase(resp))
			{
				fileStatusRepo.updateStatusAndCompltdOn(id,
						ReconStatusConstants.NO_DATA_FOUND,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			
			}

		} catch (Exception ex) {
			String msg = String.format(
					"Config Id is '%s', Exception occured during report generation for reconResultReport",
					id.toString());
			fileStatusRepo.updateStatusAndCompltdOn(id,
					ReconStatusConstants.REPORT_GENERATION_FAILED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			LOGGER.error(msg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}

	}

}
