/**
 */
package com.ey.advisory.processors.test;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.FileStatusDownloadReportRepository;
import com.ey.advisory.app.services.dashboard.fiori.Gstr3BDashboardReportDwnldDao;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("LiabilityPaymentDashboardDownloadProcessor")
public class LiabilityPaymentDashboardDownloadProcessor implements TaskProcessor {

	
	@Autowired
	FileStatusDownloadReportRepository fileStatusDownloadReportRepo;
	
	@Autowired
	@Qualifier("Gstr3BDashboardReportDwnldDaoImpl")
	private Gstr3BDashboardReportDwnldDao dashbrdRptDao;
	
	
	@Override
	public void execute(Message message, AppExecContext context) {
		
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"LiabilityPaymentDashboardDownloadProcessor Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		
		JsonObject json = (JsonObject) parser.parse(jsonString);
		Long id = json.get("id").getAsLong();

		try {
						
			fileStatusDownloadReportRepo.updateStatus(id,
					"REPORT_GENERATION_INPROGRESS", null, LocalDateTime.now());
			
			String uploadFileName = dashbrdRptDao.getDashbrdData(id);
			
			if(!Strings.isNullOrEmpty(uploadFileName))
			{
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("updating filePath {} in REPORT_DOWNLOAD_REQUEST"
						+ " table :", uploadFileName );
			}
			
			fileStatusDownloadReportRepo.updateStatus(id, "REPORT_GENERATED", 
					uploadFileName, LocalDateTime.now());
			
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("updated filePath");
			}

		} catch (AppException | IOException e) {
			LOGGER.error("Exception in LiabilityPaymentDashboardDownloadProcessor ",
					e.getMessage());
			fileStatusDownloadReportRepo.updateStatus(id, 
					"REPORT_GENERATION_FAILED ", null, LocalDateTime.now());
			throw new AppException(e);
		}
	}
}
