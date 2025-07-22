package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.ledger.LiabilityLedgerReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran
 *
 */
@Slf4j
@Component("liabilityLedgerReportProcessor")
public class liabilityLedgerReportProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("LiabilityLedgerReportServiceImpl")
	private LiabilityLedgerReportService liabilityLedgerReport;

	@Autowired
	FileStatusDownloadReportRepository downloadRepository;

	@Override
	public void execute(Message message, AppExecContext context) {
		Long id=null;
		try {
			
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin liabilityLedgerReportProcessor processor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		Gson gson = new Gson();
		 id = json.get("id").getAsLong();
		 JsonArray gstinsArray = json.getAsJsonArray("gstins");
		 
		 if (LOGGER.isDebugEnabled()) {
			    String msg = String.format(
			            "Inside processor with gstinsArray: %s",
			            gstinsArray);
			    LOGGER.debug(msg);
			}
		 
	        List<String> activeGstnList = new ArrayList<>();
	        for (JsonElement element : gstinsArray) {
	        	activeGstnList.add(element.getAsString());
	        }        
	        if (LOGGER.isDebugEnabled()) {
	            String msg = String.format(
	                    "Inside processor with activeGstnList: %s",
	                    activeGstnList);
	            LOGGER.debug(msg);
	        }
		
			Optional<FileStatusDownloadReportEntity> fileStatusEntity = downloadRepository
					.findById(id);
			
			if (fileStatusEntity.isPresent()) {
				String reqPayload = fileStatusEntity.get().getReqPayload();
				json = JsonParser.parseString(reqPayload).getAsJsonObject();
			} else {
				LOGGER.error("No FileStatusDownloadReportEntity found for ID: {}", id);
				throw new EntityNotFoundException("FileStatusDownloadReportEntity not found for ID: " + id);
			}
			String fromReturnPeriod = json.get("from_ret_period").getAsString();
			String toReturnPeriod = json.get("to_ret_period").getAsString();

			 
			if (LOGGER.isDebugEnabled()) {
			    String msg = String.format(
			            "Inside processor with fromReturnPeriod: %s, toReturnPeriod: %s",
			            fromReturnPeriod, toReturnPeriod);
			    LOGGER.debug(msg);
			}

		
			
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

			liabilityLedgerReport.getLiabilityLedgerReport(id,fromReturnPeriod,toReturnPeriod,activeGstnList);


		} catch (Exception ex) {
			String msg = String.format(
					"Exception occured during report generation for Liablity ledger");
			downloadRepository.updateStatus(id,
					"REPORT_GENERATION_FAILED", null, LocalDateTime.now());
			LOGGER.error(msg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}
}
