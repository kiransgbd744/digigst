/**
 * 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoReconRequestRepository;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.Gstr2aAutoReconImsGenerateReportServiceImpl;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.Gstr2aGenerateReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReportStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Component("Gstr2aGenerateReportProcessor")
public class Gstr2aGenerateReportProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2aGenerateReportServiceImpl")
	Gstr2aGenerateReportService gstr2aGenerateReportservice;
	
	@Autowired
	AutoReconRequestRepository autoReconRequestRepo;
	
	
	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;
	
	@Autowired
	@Qualifier("Gstr2aAutoReconImsGenerateReportServiceImpl")
	Gstr2aAutoReconImsGenerateReportServiceImpl gstr2aAutoReconImsGenerateReportServiceImpl;
	
	@Override
	public void execute(Message message, AppExecContext context) {
		
	LOGGER.debug("Begin GSTR2A generate report job");

	String groupCode = message.getGroupCode();
	String jsonString = message.getParamsJson();
	if (LOGGER.isDebugEnabled()) {
		String msg = String.format(
				"GSTR2A generate report Job"
						+ " executing for groupcode %s and params %s",
				groupCode, jsonString);
		LOGGER.debug(msg);
	}
	JsonParser parser = new JsonParser();

	JsonObject json = (JsonObject) parser.parse(jsonString);
	Long requestId = json.get("id").getAsLong();
	
	autoReconRequestRepo.updateStatus("INITIATED", LocalDateTime.now(), requestId, null);
	
	try {
		//Invoking for AIM +IMS report based on user selection
		
		String grpCode = TenantContext.getTenantId();
		String onbrdAnswer = onbrdConsolidated2BvsPROptionOpted(grpCode);
	 
			if (onbrdAnswer != null && onbrdAnswer.contains("GR4")) {
				gstr2aAutoReconImsGenerateReportServiceImpl.generateReport(requestId);
			} else {
				gstr2aGenerateReportservice.generateReport(requestId);
			}
		
	} catch (Exception e) {
		
		LOGGER.error("Error occured while generating auto cloud report", e);
		
		autoReconRequestRepo.updateStatus(
				ReportStatusConstants.REPORT_GENERATION_FAILED,
				LocalDateTime.now(), requestId, null);
		throw new AppException(e);
	}
	
		
	}
	
	public String onbrdConsolidated2BvsPROptionOpted(String grpCode) {

		HashMap<String, String> imsReconReportPermMap = new HashMap<String, String>();

		imsReconReportPermMap.put("A", "GR2");
		imsReconReportPermMap.put("B", "GR3");
		imsReconReportPermMap.put("C", "GR4");
		imsReconReportPermMap.put("A*B", "GR2,GR3");
		imsReconReportPermMap.put("B*A", "GR3,GR2");
		imsReconReportPermMap.put("B*C", "GR3,GR4");
		imsReconReportPermMap.put("C*B", "GR4,GR3");
		imsReconReportPermMap.put("C*A", "GR4,GR2");
		imsReconReportPermMap.put("A*C", "GR2,GR4");
		imsReconReportPermMap.put("A*B*C", "GR2,GR3,GR4");

		// "Which recon report is required to be enhanced alongwith IMS
		// columns?"
		String optAns = groupConfigPrmtRepository
				.findIms2BvsPROptionOpted(grpCode);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("optAns " + optAns);
		}
		if (optAns != null & optAns != "") {
			return imsReconReportPermMap.get(optAns);
		}
		return "";

	}

}
