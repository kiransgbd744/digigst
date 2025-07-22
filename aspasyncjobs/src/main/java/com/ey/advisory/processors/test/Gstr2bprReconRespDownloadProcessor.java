/**
 * This Processor is responsible to create PSD, ERROR and INFO Async Report 
 * for GSTR2B Recon Response Upload 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.gstr2bpr.reconresponse.upload.GSTR2bprPsdErrInfoReportDwnldService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Component("Gstr2bprReconRespDownloadProcessor")
public class Gstr2bprReconRespDownloadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GSTR2bprPsdErrInfoReportDwnldServiceImpl")
	private GSTR2bprPsdErrInfoReportDwnldService reportDwnldService;
	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr2bprReconRespDownloadProcessor Upload Job"
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
			
			if (reportName.equalsIgnoreCase("Error Reports")) {
				 filePath = reportDwnldService
						.getErrorData(Long.valueOf(id));

			} else if (reportName.equalsIgnoreCase("Processed Reports")) { 
				filePath = reportDwnldService
						.getPsdData(Long.valueOf(id));
				
			} else if (reportName.equalsIgnoreCase("Information Reports")) {
				filePath = reportDwnldService
						.getInfoData(Long.valueOf(id));

			}
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("updating filePath {} in REPORT_DOWNLOAD_REQUEST"
						+ " table :", filePath );
			}
			
			if(filePath!=null)
			{
			fileStatusDownloadReportRepo.updateStatus(id, "REPORT_GENERATED", 
					filePath.getValue0(), LocalDateTime.now(),filePath.getValue1());
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("updated filePath");
			}

		} catch (AppException e) {
			LOGGER.error("Exception in Gstr2bprReconRespDownloadProcessor ",
					e.getMessage());
			fileStatusDownloadReportRepo.updateStatus(id, 
					"REPORT_GENERATION_FAILED ", null, LocalDateTime.now());
			throw new AppException(e);
		}
	}
}
