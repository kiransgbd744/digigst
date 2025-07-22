/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp.processedrecords;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 * This is Job for Summarizing Processed Records 
 *
 */

@Service("Anx1ProcessedRecordsRevIntegerationProcessor")
@Slf4j
public class Anx1ProcessedRecordsRevIntegerationProcessor
		implements TaskProcessor {
	
	@Autowired
	@Qualifier("ProcessRecordsServiceImpl")
	ProcessRecordsService processedRecServ;
	
	@Autowired
	@Qualifier("ProcessRecTaxPeriodsFinderImpl")
	ProcessRecTaxPeriodsFinder tpFinder;
	
	@Autowired
	private DocRepository docRepo;
	
	@Autowired
	private AnxErpBatchRepository batchRepo;
	
	@Autowired
	@Qualifier("ProcessedRecordsPushErpImpl")
	ProcessedRecordsPushErp pushDataToErp;
	

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("ProcessRecords RevIntegeration Job"
				+ " executing for goupcode %s and params %s", groupcode, json);
			LOGGER.debug(msg);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();
		
        /*
         * This Dto is Trigger and Dta we get from the Message Object as 
         * it contains JSON Param column that contains all details
         * like groupCode,Entity ID so we mapp that Parameters
         * to RevIntegrationScenarioTriggerDto
         */
		RevIntegrationScenarioTriggerDto revIntegerationDto = gson.fromJson(json,
				RevIntegrationScenarioTriggerDto.class);
		
		// Destination Name is Final Point were Data is to be pushed to ERP
		String destinationName = revIntegerationDto.getDestinationName();
		
		Long entityID = revIntegerationDto.getEntityId();
		
		
		String gstin = revIntegerationDto.getGstin();

		//The DestinationName,GroupCode and Entity Id is Mandatory
		if (destinationName == null || groupcode == null || entityID == null
				|| gstin == null) {
            String msg = "Destination Name, Group Code,Gstin and "
            		+ "EntityID cannot be Empty";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
		
		/*
		 * ProcessRecTaxPeriodsFinder class is used for TaxPeriod as currently
		 * we are not getting any request Parameter as taxPeriod
		 * so meanwhile,we are using this class to get Previous Month TaxPeriod
		 */
		List<String> taxPeriods = tpFinder.getApplicableTaxPeriods();
		
		/*
		 * ProcessedRecReqDto contains all the attributes which are required 
		 * for Performing DB Operations and is Passed to Service and Dao Layer
		 */
		
		
		// Calling the Service Layer to get Details for Records
		List<ProcessedRecDetForGstinTaxPeriodDto> processRecords =
		        processedRecServ.getProcessedRecords(revIntegerationDto,
		        		taxPeriods);
		
		if(processRecords != null && !processRecords.isEmpty()) {
		Long batchSize= (long) processRecords.size();

		LOGGER.debug("The Size of  Summarized Processed Records"
				+ "for particular GSTIN is :" + batchSize );
		Anx1ProcessedRevRecordsDto  revProcessedRecords = 
				new Anx1ProcessedRevRecordsDto();
		//All List of Data got from Database is Set To Parent Dto
		revProcessedRecords.setIM_DATA(processRecords);
		
		AnxErpBatchEntity batch = setErpBatch(groupcode, entityID, batchSize);
		// Erp Batch Id forming
		batch = batchRepo.save(batch);
		// Push Payload to erp
	    Integer respcode = pushDataToErp.pushToErp(revProcessedRecords, 
	    		destinationName, batch);
	    /*if (respcode == 200) {
			batch.setSuccess(true);
		} else {
			batch.setSuccess(false);
		}
	    
	    // Erp Batch updation
	 	batchRepo.save(batch);*/
		} else {
			LOGGER.debug("No Data found to do Processed Records"
					+ " Reveserse integration.");
		}
	}
	
	private AnxErpBatchEntity setErpBatch(String groupcode, Long entityId, 
			Long size) {
		AnxErpBatchEntity batch = new AnxErpBatchEntity();
		batch.setGroupcode(groupcode);
		batch.setEntityId(entityId);
		batch.setBatchSize(size);
		batch.setHttpStatus(APIConstants.INITIATED);
		batch.setCreatedBy(APIConstants.SYSTEM);
		batch.setCreatedOn(LocalDateTime.now());
		batch.setDelete(false);
		return batch ;
}

}
