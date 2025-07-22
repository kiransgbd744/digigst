package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.reports.Gstr1B2BASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2BSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2CLASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2CLSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2CSASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1B2CSSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1CDNRASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1CDNRSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1CDNURASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1CDNURSectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1EXPASectionReportHandler;
import com.ey.advisory.app.services.reports.Gstr1EXPSectionReportHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.AsyncReportB2BDownloadService;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Service("GSTR1SectionWiseReportDownloadProcessor")
public class GSTR1SectionWiseReportDownloadProcessor implements TaskProcessor {

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("Gstr1B2BSectionReportHandler")
	AsyncReportB2BDownloadService asyncReportDownloadService;

	/*@Autowired
	@Qualifier("Gstr1B2BSectionReportHandler")
	private Gstr1B2BSectionReportHandler gstr1B2BSectionReportHandler;*/

	
	@Autowired
	@Qualifier("Gstr1B2BASectionReportHandler")
	private Gstr1B2BASectionReportHandler gstr1B2BASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1B2CLSectionReportHandler")
	private Gstr1B2CLSectionReportHandler gstr1B2CLSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1B2CLASectionReportHandler")
	private Gstr1B2CLASectionReportHandler gstr1B2CLASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1EXPSectionReportHandler")
	private Gstr1EXPSectionReportHandler gstr1EXPSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1EXPASectionReportHandler")
	private Gstr1EXPASectionReportHandler gstr1EXPASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1CDNRSectionReportHandler")
	private Gstr1CDNRSectionReportHandler gstr1CDNRSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1CDNRASectionReportHandler")
	private Gstr1CDNRASectionReportHandler gstr1CDNRASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1CDNURSectionReportHandler")
	private Gstr1CDNURSectionReportHandler gstr1CDNURSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1CDNURASectionReportHandler")
	private Gstr1CDNURASectionReportHandler gstr1CDNURASectionReportHandler;

	@Autowired
	@Qualifier("Gstr1B2CSSectionReportHandler")
	private Gstr1B2CSSectionReportHandler gstr1B2CSSectionReportHandler;

	@Autowired
	@Qualifier("Gstr1B2CSASectionReportHandler")
	private Gstr1B2CSASectionReportHandler gstr1B2CSASectionReportHandler;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

		Gson gson = GsonUtil.newSAPGsonInstance();

		Long id = json.get("id").getAsLong();
		String reportType = json.get("reportType").getAsString();
		String jsonStringCriteria = json.get("jsonString").getAsString();

		Gstr1ReviwSummReportsReqDto criteria = gson.fromJson(jsonStringCriteria,
				Gstr1ReviwSummReportsReqDto.class);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside GSTR1SectionWiseReportDownloadProcessor : %s",
					criteria.toString());
			LOGGER.debug(msg);
		}
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside GSTR1SectionWiseReportDownloadProcessor with Report id : %d",
					id);
			LOGGER.debug(msg);
		}

		fileStatusDownloadReportRepo.updateStatus(id,
				ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
				LocalDateTime.now());

		if (LOGGER.isDebugEnabled()) {
			String msg = "Updated file status as 'REPORT_GENERATION_INPROGRESS'";
			LOGGER.debug(msg);
		}

		try {

			if (reportType.equalsIgnoreCase("B2B")) {
				//gstr1B2BSectionReportHandler.downloadRSProcessedReport(criteria,id);
				asyncReportDownloadService.generateB2bReports(criteria,id);

			}

			if (reportType.equalsIgnoreCase("B2BA")) {
				gstr1B2BASectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}

			if (reportType.equalsIgnoreCase("B2CL")) {
				gstr1B2CLSectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}

			if (reportType.equalsIgnoreCase("B2CLA")) {
				gstr1B2CLASectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}

			if (reportType.equalsIgnoreCase("EXPORTS")) {
				gstr1EXPSectionReportHandler
				        .downloadRSProcessedReport(criteria,id);
			}

			if (reportType.equalsIgnoreCase("EXPORTA")) {
				gstr1EXPASectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}

			if (reportType.equalsIgnoreCase("CDNR")) {
				gstr1CDNRSectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}

			if (reportType.equalsIgnoreCase("CDNRA")) {
				gstr1CDNRASectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}

			if (reportType.equalsIgnoreCase("CDNUR")) {
				gstr1CDNURSectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}

			if (reportType.equalsIgnoreCase("CDNURA")) {
				gstr1CDNURASectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}
			if (reportType.equalsIgnoreCase("B2CS")) {
				gstr1B2CSSectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}

			if (reportType.equalsIgnoreCase("B2CSA")) {
				gstr1B2CSASectionReportHandler
						.downloadRSProcessedReport(criteria, id);
			}

		} catch (Exception ex) {
			String msg = "GSTR1SectionWiseReportDownloadProcessor - "
					+ "Exception occured while downloading csv report";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}

	}

}