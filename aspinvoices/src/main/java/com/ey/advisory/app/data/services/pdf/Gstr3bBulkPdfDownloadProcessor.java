package com.ey.advisory.app.data.services.pdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.data.services.ewb.Gstr6IsdPdfService;
import com.ey.advisory.app.services.search.filestatussearch.AsyncReportDownloadServiceGstr3b;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleDocGstnSummarySearchService;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran
 *
 */
@Slf4j
@Component("Gstr3bBulkPdfDownloadProcessor")
public class Gstr3bBulkPdfDownloadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr6IsdPrintServiceImpl")
	private Gstr6IsdPdfService gstr6Service;

	@Autowired
	@Qualifier("ProcessedRecordsCommonSecParam")
	private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Autowired
	@Qualifier("Gstr1SimpleDocGstnSummarySearchService")
	Gstr1SimpleDocGstnSummarySearchService tableSearchService;

	@Autowired
	@Qualifier("Gstr1SummaryMultiPDFService")
	Gstr1SummaryMultiPDFService gstr1SummaryMultiPDFService;

	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	@Qualifier("Gstr3BSummaryPDFGenerationReportImpl")
	Gstr3BSummaryPDFGenerationReport gstr3BSummaryPDFGenerationReport;
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Autowired
	@Qualifier("AsyncGSTR3bPDFDownloadServiceImpl")
	AsyncReportDownloadServiceGstr3b asyncReportDownloadServiceGstr3b;
	
	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Long id = json.get("id").getAsLong();
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

			String response = asyncReportDownloadServiceGstr3b.generateReports(id);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"processor executed successfully with  id : %d",
						id);
				LOGGER.debug(msg);
			}
			
			if (!"SUCCESS".equalsIgnoreCase(response)) {
				
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"service impl didn't return success id : %d",
							id);
					LOGGER.debug(msg);
				}
				throw new AppException("service impl didn't return success");
			}
		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured during report generation for gstr1");
			LOGGER.error(msg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

	
}
