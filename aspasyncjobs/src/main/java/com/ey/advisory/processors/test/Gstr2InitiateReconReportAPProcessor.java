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
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.asprecon.gstr2.ap.recon.Gstr2ApManualGenerateReportServiceImpl;
import com.ey.advisory.asprecon.gstr2.ap.recon.Gstr2NonApManualGenerateReportService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2InitiateReconReportAPProcessor")
public class Gstr2InitiateReconReportAPProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2NonApManualGenerateReportServiceImpl")
	Gstr2NonApManualGenerateReportService nonApFetchReportDetails;

	@Autowired
	@Qualifier("Gstr2ApManualGenerateReportServiceImpl")
	Gstr2ApManualGenerateReportServiceImpl apFetchReportDetails;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;
	
	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;
	
	/*
	 * List<String> mandatoryReportTypeList = Arrays.asList("Exact Match",
	 * "Match With Tolerance", "Value Mismatch", "POS Mismatch",
	 * "Doc Date Mismatch", "Doc Type Mismatch", "Doc No Mismatch I",
	 * "Multi-Mismatch", "Addition in PR",
	 * "Addition in 2A_6A");//"Consolidated PR 2A_6A Report"
	 */
	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr2InitiateReconReportAPProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();
		Boolean apFlag = json.get("apFlag").getAsBoolean();
		Long entityId = json.get("entityId").getAsLong();

		try {

			List<String> addnReportList = addlReportRepo
					.getAddlnReportTypeList(configId);
			
			/*
			 * Gstr2ReconConfigEntity configEntity = reconConfigRepo
			 * .findByConfigId(configId);
			 * 
			 * if (!configEntity.getIsMandatory()) {
			 * 
			 * addlReportRepo.deleteByConfigIdAndReportTypeIn(configId,
			 * mandatoryReportTypeList);
			 * 
			 * if (LOGGER.isDebugEnabled()) { String msg = String.format(
			 * "Gstr2InitiateReconReportAPProcessor, deleted  " +
			 * "mandatoryReportTypeList() configId %s,- " + ": ",configId.toString());
			 * LOGGER.debug(msg);
			 * 
			 * } } if(addnReportList.contains("Consolidated PR 2A/6A Report")) {
			 * 
			 * addlReportRepo.deleteByConfigIdAndReportType(configId,
			 * "Consolidated PR 2A/6A Report"); }else {
			 * addlReportRepo.deleteByConfigIdAndReportType(configId,
			 * "Consolidated PR 2A_6A Report"); }
			 * 
			 */			
			reconConfigRepo.updateReconConfigStatusAndReportName(
					"REPORT_GENERATION_INPROGRESS", null, LocalDateTime.now(),
					configId);

			if (!apFlag) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2InitiateReconReportAPProcessor, Invoking "
									+ "executeNonAPManualRecon() configId %s,  "
									+ "apFlag %s :",
							configId.toString(), apFlag);
					LOGGER.debug(msg);
				}

				nonApFetchReportDetails.generateReport(configId,
						addnReportList);

			} else if (apFlag) {

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Gstr2InitiateReconReportAPProcessor, Invoking "
									+ "executeAPManualRecon() configId %s,"
									+ "  apFlag %s :",
							configId.toString(), apFlag);
					LOGGER.debug(msg);
				}

				apFetchReportDetails.generateReport(configId, entityId,
						addnReportList);
			}
		} catch (Exception e) {
			LOGGER.error(
					"Error occured in Gstr2InitiateReconReportAPProcessor  "
							+ "configId {} ",
					configId.toString());
			reconConfigRepo.updateReconConfigStatusAndReportName(
					"REPORT_GENERATION_FAILED", null, LocalDateTime.now(),
					configId);

			throw new AppException(e);
		}

	}

}
