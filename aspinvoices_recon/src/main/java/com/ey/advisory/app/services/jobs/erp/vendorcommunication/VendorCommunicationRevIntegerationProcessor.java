package com.ey.advisory.app.services.jobs.erp.vendorcommunication;

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
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Service("VendorCommunicationRevIntegerationProcessor")
public class VendorCommunicationRevIntegerationProcessor
		implements TaskProcessor {

	@Autowired
	private DocRepository docRepo;

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	@Qualifier("VendorMismatchServiceImpl")
	VendorMismatchService venMissMatchService;

	@Autowired
	@Qualifier("VendorMismatchPushErpImpl")
	VendorMismatchPushErp vendorMismatcPush;

	Integer respcode = 0;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin Vendor Communication "
					+ " RevIntegeration Job executing for goupcode %s"
					+ " and params %s",
					groupcode, json);
			LOGGER.debug(msg);
		}
		Gson gson = GsonUtil.newSAPGsonInstance();

		/*
		 * This Dto is Trigger and Dta we get from the Message Object as it
		 * contains JSON Param column that contains all details like
		 * groupCode,Entity ID so we mapp that Parameters to
		 * RevIntegrationScenarioTriggerDto
		 */
		RevIntegrationScenarioTriggerDto revIntegerationDto = gson
				.fromJson(json, RevIntegrationScenarioTriggerDto.class);

		// Destination Name is Final Point were Data is to be pushed to ERP
		try {

			String destinationName = revIntegerationDto.getDestinationName();

			Long entityID = revIntegerationDto.getEntityId();

			String gstin = revIntegerationDto.getGstin();
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Destination Name %s, Group Code %s,"
						+ " Gstin %s, EntityID %s ", destinationName, 
						groupcode, gstin, entityID); 
				LOGGER.debug(msg);
			}

			// The DestinationName,GroupCode and Entity Id is Mandatory
			if (destinationName == null || groupcode == null || entityID == null
					|| gstin == null) {
				String msg = "Destination Name, Group Code,Gstin and "
						+ "EntityID cannot be Empty";
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			/*
			 * ProcessedRecReqDto contains all the attributes which are required
			 * for Performing DB Operations and is Passed to Service and Dao
			 * Layer
			 */

			// Calling the Service Layer to get Details for Records
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Invoking getVendorMismatchRecords()"
						+ " for Destination Name %s, Group Code %s,"
						+ " Gstin %s, EntityID %s ", destinationName, 
						groupcode, gstin, entityID); 
				LOGGER.debug(msg);
			}

			List<VendorMismatchDto> processRecords = venMissMatchService
					.getVendorMismatchRecords(revIntegerationDto);
			

			if (processRecords != null && !processRecords.isEmpty()) {

				Long batchSize = (long) processRecords.size();
				/*
				 * List<Pair<VendorMismatchRevRecordsDto, List<Long>>> pairs =
				 * venMissMatchService.getDocsAsDtosByChunking(processRecords);
				 * 
				 * pairs.forEach(pair -> { long currentBatchSize =
				 * pair.getValue1().size(); AnxErpBatchEntity batch =
				 * setErpBatch(groupcode, entityID, currentBatchSize); // Erp
				 * Batch Id forming batch = batchRepo.save(batch); if
				 * (LOGGER.isDebugEnabled()) {
				 * LOGGER.debug("Batch is created as {} ", batch); }
				 * 
				 * 
				 * // update selected invoices with batch id
				 * docRepo.updateDocsWithErpBatchId(pair.getValue1(),
				 * batch.getId()); // Push Payload to erp respcode =
				 * vendorMismatcPush.pushToErp(pair.getValue0(),
				 * destinationName); respcode = respcode != null ? respcode : 0;
				 * if (LOGGER.isDebugEnabled()) {
				 * LOGGER.debug("Destination {} -> Response code is {} "
				 * ,destinationName, respcode); } if (respcode == 200) {
				 * batch.setSuccess(true);
				 * batch.setStatus(APIConstants.SUCCESS); } else {
				 * batch.setSuccess(false);
				 * batch.setStatus(APIConstants.FAILED); } // Erp Batch updation
				 * batchRepo.save(batch); });
				 */

				LOGGER.debug("The Size of  Summarized Processed Records"
						+ "for particular GSTIN is :" + batchSize);
				
				VendorMismatchRevRecordsDto revProcessedRecords =
						new VendorMismatchRevRecordsDto();
				// All List of Data got from Database is Set To Parent Dto
				revProcessedRecords.setItem(processRecords);

				AnxErpBatchEntity batch = setErpBatch(groupcode, entityID,
						batchSize);
				// Erp Batch Id forming
				batch = batchRepo.save(batch);
				
				// Push Payload to erp
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Invoking pushToErp() method :"
							+ " destinationName %s", destinationName); 
					LOGGER.debug(msg);
				}
				Integer respcode = vendorMismatcPush
						.pushToErp(revProcessedRecords, destinationName, batch);
				if (respcode == 200) {
					batch.setSuccess(true);
				} else {
					batch.setSuccess(false);
				}

				// Erp Batch updation
				batchRepo.save(batch);
			} else {
				LOGGER.debug("No Data found to do Vendor Communication"
						+ " Reveserse integration.");
			}

		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
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
		return batch;
	}

}
