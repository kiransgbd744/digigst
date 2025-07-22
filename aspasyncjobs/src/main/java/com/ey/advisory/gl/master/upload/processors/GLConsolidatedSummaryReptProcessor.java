/**
 * @author kiran s
 
 
 */
package com.ey.advisory.gl.master.upload.processors;

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
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;


	@Slf4j
	@Service("GLConsolidatedSummaryReptProcessor")
	public class GLConsolidatedSummaryReptProcessor implements TaskProcessor {

		@Autowired
		FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

		@Autowired
		@Qualifier("GLConsolidatedRptDownloadServiceImpl")
		AsyncReportDownloadService gLConsolidatedReportDownloadService;
		
		@Autowired
		@Qualifier("FileStatusDownloadReportRepository")
		FileStatusDownloadReportRepository fileStatusRepo;

		@Override
		public void execute(Message message, AppExecContext context) {

			String jsonString = message.getParamsJson();
			JsonElement jsonElement = JsonParser.parseString(jsonString);
			JsonObject json = jsonElement.getAsJsonObject();
			Long id = json.get("id").getAsLong();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside GLConsolidatedSummaryReptProcessor Download processor with Report id : %d",
						id);
				LOGGER.debug(msg);
			}

			fileStatusDownloadReportRepo.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			if (LOGGER.isDebugEnabled()) {
				String msg ="Updated file status as 'REPORT_GENERATION_INPROGRESS' in GLConsolidatedSummaryReptProcessor";
				LOGGER.debug(msg);
			}

			try {
				gLConsolidatedReportDownloadService.generateReports(id);
				
				
					fileStatusRepo.updateStatusAndCompltdOn(id,
							ReconStatusConstants.REPORT_GENERATED,
							EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				
			} catch (Exception ex) {
				String msg = "Exception occured while downloading csv report";
				LOGGER.error(msg);
				fileStatusRepo.updateStatusAndCompltdOn(id,
						ReconStatusConstants.REPORT_GENERATION_FAILED,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				throw new AppException(msg, ex);
			}

		}

	}



