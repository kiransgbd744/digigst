/**
 * This Processor is responsible to create ERROR Async Report 
 * for Recon Result 
 */
package com.ey.advisory.processors.test;

import java.io.IOException;
import java.time.LocalDateTime;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.ReportDao;
import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.gstr2aprIms.reconresponse.upload.GSTR2aprImsPsdErrInfoReportDwnldService;
import com.ey.advisory.app.gstr2bpr.reconresponse.upload.GSTR2bprPsdErrInfoReportDwnldService;
import com.ey.advisory.app.gstr2bprIms.reconresponse.upload.GSTR2bprImsPsdErrInfoReportDwnldService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("Gstr2reconResultErrorDownloadProcessor")
public class Gstr2reconResultErrorDownloadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ReportDaoImpl")
	private ReportDao reportDwnldService;
	
	@Autowired
	@Qualifier("GSTR2bprPsdErrInfoReportDwnldServiceImpl")
	private GSTR2bprPsdErrInfoReportDwnldService error2BPRDao;

	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	@Autowired
	@Qualifier("GSTR2bprImsPsdErrInfoReportDwnldServiceImpl")
	private GSTR2bprImsPsdErrInfoReportDwnldService imsReportDwnldService;
	
	@Autowired
	@Qualifier("GSTR2aprImsPsdErrInfoReportDwnldServiceImpl")
	private GSTR2aprImsPsdErrInfoReportDwnldService gstr2aprReportDwnldService;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr2reconResultErrorDownloadProcessor Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		Pair<String,String>  filePath = null;
		
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long id = json.get("id").getAsLong();

		try {
			
			String reportName = json.get("reportType").getAsString();
			
			fileStatusDownloadReportRepo.updateStatus(id,
					"REPORT_GENERATION_INPROGRESS", null, LocalDateTime.now());
			
			if (reportName.equalsIgnoreCase("Error 2A-6AvsPR Report (Recon Result)")) {
				 filePath = reportDwnldService
						.getErrorData(Long.valueOf(id));

			} else if (reportName.equalsIgnoreCase("Error 2BvsPR Report (Recon Result)")) { 
				filePath = error2BPRDao
						.getErrorData(Long.valueOf(id));
			}else if (reportName.equalsIgnoreCase("IMS Action Error 2BvsPR (Recon Result)")) {
				filePath = imsReportDwnldService.getErrorData(Long.valueOf(id));

			} else if (reportName.equalsIgnoreCase("IMS Action Error 2AvsPR (Recon Result)")) {
				 filePath = gstr2aprReportDwnldService
							.getErrorData(Long.valueOf(id));

			} 
					
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("updating filePath {} in REPORT_DOWNLOAD_REQUEST"
						+ " table :", filePath );
			}
			
			if (filePath != null && filePath.getValue0() != null && filePath.getValue1() != null) {
				fileStatusDownloadReportRepo.updateStatus(id, "REPORT_GENERATED", filePath.getValue0(),
						LocalDateTime.now(), filePath.getValue1());
			} else {

				LOGGER.error("filePath or its components (Value0, Value1) are null. Unable to update status.");
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("updated filePath");
			}

		} catch (AppException | IOException e) {
			LOGGER.error("Exception in Gstr2reconResultErrorDownloadProcessor ",
					e.getMessage());
			fileStatusDownloadReportRepo.updateStatus(id, 
					"REPORT_GENERATION_FAILED ", null, LocalDateTime.now());
			throw new AppException(e);
		}
	}
}
