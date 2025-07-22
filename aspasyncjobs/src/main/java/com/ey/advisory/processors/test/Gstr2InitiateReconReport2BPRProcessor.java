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

import com.ey.advisory.app.asprecon.gstr2.pr2b.reports.Gstr2InitiateRecon2BPRFetchReportDetailsImpl;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
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
@Component("Gstr2InitiateReconReport2BPRProcessor")
public class Gstr2InitiateReconReport2BPRProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2InitiateRecon2BPRFetchReportDetailsImpl")
	Gstr2InitiateRecon2BPRFetchReportDetailsImpl fetch2BPRReportDetails;
	
	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository addl2BPRReportRepo;
	
	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;
	
	/*
	 * List<String> mandatoryReportTypeList = Arrays.asList("Exact Match",
	 * "Match With Tolerance", "Value Mismatch", "POS Mismatch",
	 * "Doc Date Mismatch", "Doc Type Mismatch", "Doc No Mismatch I",
	 * "Multi-Mismatch", "Addition in PR",
	 * "Addition in 2B");//"Consolidated PR 2B Report"
	 */
	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr2InitiateReconReport2BPRProcessor :%s",
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
		
		try{

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2InitiateReconReport2BPRProcessor, Invoking "
								+ "executeAPManualRecon() configId %s,- : ",
						configId.toString());
				LOGGER.debug(msg);

			}

			List<String> addnReportList = addl2BPRReportRepo
					.getAddlnReportTypeList(configId);
			
			/*
			 * Gstr2ReconConfigEntity configEntity = reconConfigRepo
			 * .findByConfigId(configId);
			 * 
			 * if (!configEntity.getIsMandatory()) {
			 * 
			 * addl2BPRReportRepo.deleteByConfigIdAndReportTypeIn(configId,
			 * mandatoryReportTypeList);
			 * 
			 * if (LOGGER.isDebugEnabled()) { String msg = String.format(
			 * "Gstr2InitiateReconReport2BPRProcessor, deleted  " +
			 * "mandatoryReportTypeList() configId %s,- " + ": ",configId.toString());
			 * LOGGER.debug(msg);
			 * 
			 * } } long count = addnReportList.stream().filter(o -> o
			 * .equalsIgnoreCase("Consolidated PR 2B Report")).count(); if(count > 1) {
			 * addl2BPRReportRepo.deleteByConfigIdAndReportTypeAndReportTypeIdIsNull(
			 * configId, "Consolidated PR 2B Report"); }else {
			 * addl2BPRReportRepo.deleteByConfigIdAndReportType(configId,
			 * "Consolidated PR 2B Report"); }
			 * 
			 */			
			fetch2BPRReportDetails.get2BPRReconReportData(configId, addnReportList);
			
			
		} catch (Exception e) {
			LOGGER.error(
					"Error occured in Gstr2InitiateReconReport2BPRProcessor  "
					+ "configId {} ",
					configId.toString());
			reconConfigRepo.updateReconConfigStatusAndReportName(
					"REPORT_GENERATION_FAILED", null, LocalDateTime.now(),
					configId);
			throw new AppException(e);
		}
		
	}

}


