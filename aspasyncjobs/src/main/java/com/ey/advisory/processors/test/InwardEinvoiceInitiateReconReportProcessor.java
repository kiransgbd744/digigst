/**
 * 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceReconAddlReportsRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.inwardEinvoice.initiateRecon.InwardEinvoiceInitiateReconFetchReportDetailsImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("InwardEinvoiceInitiateReconReportProcessor")
public class InwardEinvoiceInitiateReconReportProcessor
		implements TaskProcessor {

	@Autowired
	@Qualifier("InwardEinvoiceInitiateReconFetchReportDetailsImpl")
	InwardEinvoiceInitiateReconFetchReportDetailsImpl fetchInwardEinvoiceReportDetails;

	@Autowired
	@Qualifier("InwardEinvoiceReconAddlReportsRepository")
	InwardEinvoiceReconAddlReportsRepository addlInwardEinvoiceReportRepo;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	List<String> mandatoryReportTypeList = Arrays.asList("Exact Match",
			"Match With Tolerance", "Value Mismatch", "POS Mismatch",
			"Doc Date Mismatch", "Doc Type Mismatch", "Doc No Mismatch I",
			"Multi-Mismatch", "Addition in PR", "Addition in Inward E-Inv",
			"Consolidated PR Inward E-Inv Report");

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin InwardEinvoiceInitiateReconReportProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();

		reconConfigRepo.updateReconConfigStatusAndReportName(
				"REPORT_GENERATION_INPROGRESS", null, LocalDateTime.now(),
				configId);

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"InwardEinvoiceInitiateReconReportProcessor, Invoking "
								+ "executeAPManualRecon() configId %s,- : ",
						configId.toString());
				LOGGER.debug(msg);

			}

			List<String> addnReportList = addlInwardEinvoiceReportRepo
					.getAddlnReportTypeList(configId);
			
			LOGGER.debug("addnReportList - {} ",addnReportList);
			
			Gstr2ReconConfigEntity configEntity = reconConfigRepo
					.findByConfigId(configId);

			if (!configEntity.getIsMandatory()) {

				addlInwardEinvoiceReportRepo.deleteByConfigIdAndReportTypeIn(
						configId, mandatoryReportTypeList);

				if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("InwardEinvoiceInitiateReconReportProcessor, deleted  "
									+ "mandatoryReportTypeList- %s, configId %s,- "
									+ ": ", mandatoryReportTypeList,configId.toString());
					LOGGER.debug(msg);

				}
			} else if (addnReportList
					.contains("Consolidated PR Inward E-Inv Report")) {
				// to remove extra entry of consolidated pr 
				
				LOGGER.debug("INSIDE deletion of consolidatd exta entry");
				
				addlInwardEinvoiceReportRepo.deleteByConfigIdAndReportTypeIn(
						configId,
						Arrays.asList("Consolidated PR Inward E-Inv Report"));

			}
			fetchInwardEinvoiceReportDetails
					.getInwardEinvoiceReconReportData(configId, addnReportList);

		} catch (Exception e) {
			LOGGER.error(
					"Error occured in InwardEinvoiceInitiateReconReportProcessor  "
							+ "configId {} ",
					configId.toString());
			reconConfigRepo.updateReconConfigStatusAndReportName(
					"REPORT_GENERATION_FAILED", null, LocalDateTime.now(),
					configId);
			throw new AppException(e);
		}

	}

}
