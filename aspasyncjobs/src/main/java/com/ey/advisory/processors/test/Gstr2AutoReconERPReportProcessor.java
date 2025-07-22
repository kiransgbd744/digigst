/**
 * 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.asprecon.gstr2.ap.recon.Gstr2ApManualGenerateReportServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.service.asprecon.auto.recon.erp.report.Gstr2AutoReconErpImsReportFetchDetails;
import com.ey.advisory.service.asprecon.auto.recon.erp.report.Gstr2AutoReconErpReportFetchDetails;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2AutoReconERPReportProcessor")
public class Gstr2AutoReconERPReportProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2AutoReconErpReportFetchDetailsImpl")
	private Gstr2AutoReconErpReportFetchDetails service;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ApManualGenerateReportServiceImpl")
	Gstr2ApManualGenerateReportServiceImpl gstr2ApManualGenerateReportServiceImpl;

	@Autowired
	@Qualifier("Gstr2AutoReconErpImsReportFetchDetailsImpl")
	Gstr2AutoReconErpImsReportFetchDetails erpImsReport;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr2AutoReconERPReportProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		Long configId = null;
		Long entityId = null;

		try {
			String jsonString = message.getParamsJson();
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(jsonString);

			configId = json.get("configId").getAsLong();
			entityId = json.get("entityId").getAsLong();
		/*	// IMS changes in Hold
			String grpCode = TenantContext.getTenantId();

			String onbrdAnswer = gstr2ApManualGenerateReportServiceImpl
					.onbrdConsolidated2BvsPROptionOpted(grpCode);

			if (onbrdAnswer != null && onbrdAnswer.contains("GR3")) {
				erpImsReport.generateImsReport(configId, entityId);
			} else {
				service.generateReport(configId, entityId);
			}
*/
			service.generateReport(configId, entityId);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATED, null,
					LocalDateTime.now(), configId);

		} catch (Exception e) {

			String msg = String.format("Error occured in "
					+ "Gstr2AutoReconERPReportProcessor, ERP Report "
					+ "Generation failed");
			LOGGER.error(msg, e);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now(), configId);

			throw new AppException(msg);
		}

	}
}
