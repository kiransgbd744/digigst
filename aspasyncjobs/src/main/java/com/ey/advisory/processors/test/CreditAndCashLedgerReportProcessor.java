package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.ledger.CreditLedgerBulkDetailsDto;
import com.ey.advisory.app.services.ledger.CreditLedgerReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran
 *
 */
@Slf4j
@Component("CreditAndCashLedgerReportProcessor")
public class CreditAndCashLedgerReportProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("CreditLedgerReportServiceImpl")
	private CreditLedgerReportService creditLedgerReport;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Override
	public void execute(Message message, AppExecContext context) {
		Long id=null;
		
		try {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin CreditLedgerReport processor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Gson gson = new Gson();
		 id = json.get("id").getAsLong();
		
		
			Optional<FileStatusDownloadReportEntity> fileStatusEntity = downloadRepository
					.findById(id);
			String reqPayload = null;
			if(fileStatusEntity.isPresent()) {
				reqPayload = fileStatusEntity.get().getReqPayload();
			}

			json = JsonParser.parseString(reqPayload).getAsJsonObject();

			Type listType = new TypeToken<List<CreditLedgerBulkDetailsDto>>() {
			}.getType();

			List<CreditLedgerBulkDetailsDto> reqDto = gson
					.fromJson(json.get("ledgerDetails").getAsJsonArray(), listType);
			
			FileStatusDownloadReportEntity entity = null;
			String reportType = null;
			if (fileStatusEntity.isPresent()) {
				entity = fileStatusEntity.get();
				reportType = entity.getReportType();
			}
			 String fromDate = json.get("fromdate").getAsString();
			 String toDate = json.get("toDate").getAsString();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside Async Report Download processor with Report id : %d",
						id);
				LOGGER.debug(msg);
			}

			downloadRepository.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));

			if (LOGGER.isDebugEnabled()) {
				String msg = "Updated file status as 'REPORT_GENERATION_INPROGRESS'";
				LOGGER.debug(msg);
			}

			creditLedgerReport.getCreditCashAndCrRevReclaimReport(reqDto, id,
					fromDate,toDate,reportType);


		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured during report generation for credit ledger");
			downloadRepository.updateStatus(id,
					"REPORT_GENERATION_FAILED", null, LocalDateTime.now());
			LOGGER.error(msg, ex);
			throw new AppException(msg);

		}
	}
	
	
}
