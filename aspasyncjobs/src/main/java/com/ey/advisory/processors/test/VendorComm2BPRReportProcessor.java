package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorCommRequestRepository;
import com.ey.advisory.app.services.vendorcomm.AsyncVendorCommReportUploadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("VendorComm2BPRReportProcessor")
public class VendorComm2BPRReportProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("VendorCommRequestRepository")
	private VendorCommRequestRepository vendorCommRequestRepository;

	@Autowired
	@Qualifier("AsyncVendorComm2BPRReportUploadServiceImpl")
	private AsyncVendorCommReportUploadService asyncVendorCommReportUploadService;

	@Override
	public void execute(Message message, AppExecContext context) {
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		try {
			JsonObject json = (JsonObject) parser.parse(jsonString);

			Long id = json.get("id").getAsLong();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"VendorDataFileUploadProcessor  with Report id : %d",
						id);
				LOGGER.debug(msg);
			}

			vendorCommRequestRepository.updateStatus(id,
					ReportStatusConstants.REPORT_GENERATION_INPROGRESS, null,
					LocalDateTime.now());

			if (LOGGER.isDebugEnabled()) {
				String msg = "Updated file status as 'REPORT_GENERATION_INPROGRESS'";
				LOGGER.debug(msg);
			}

			asyncVendorCommReportUploadService.generateVendorCommReports(id,
					false);
		} catch (Exception ex) {
			String msg = "Exception occured while downloading csv report";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}
}
