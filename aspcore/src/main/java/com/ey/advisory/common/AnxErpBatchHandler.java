/**
 * 
 */
package com.ey.advisory.common;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("AnxErpBatchHandler")
public class AnxErpBatchHandler {

	
	@Autowired
	private AnxErpBatchRepository batchRepo;
	
	public AnxErpBatchEntity updateErpBatch(AnxErpBatchEntity batch,
			Integer httpCode, String httpStatus, Boolean isSuccess,
			String apiResponse, String exception) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside updateErpBatch with args {}, {}, {}", batch,
					httpCode, httpStatus);
		}
		if(batch == null) {
			return null;
		}
		batch.setSuccess(isSuccess);
		batch.setHttpStatus(httpStatus);
		batch.setHttpCode(httpCode != null ? httpCode : 0);
		//If apiResponse length is more than 5000
		apiResponse = apiResponse != null ? (apiResponse.length() > 5000
				? apiResponse.substring(0, 5000) : apiResponse) : apiResponse;
		batch.setApiResponse(apiResponse);
		batch.setException(exception);
		if (batch.getGroupcode() != null) {
			TenantContext.setTenantId(batch.getGroupcode());
		} else {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"NULL groupcode, Please set the groupcode to AnxErpBatchEntity");
			}
		}
		
		AnxErpBatchEntity saveBatch = batchRepo.save(batch);
		// if(saveBatch != null) {
		return saveBatch;

	}
	
	public AnxErpBatchEntity createErpBatch(String groupcode, Long entityId,
			String gstin, String destinationName, Long scenarioId, Long size, 
			String dataType, String jobType, Long erpId, String payloadId, 
			String user) {
		AnxErpBatchEntity batch = new AnxErpBatchEntity();
		batch.setGroupcode(groupcode);
		batch.setEntityId(entityId);
		batch.setGstin(gstin);
		batch.setBatchSize(size);
		batch.setDestinationName(destinationName);
		batch.setScenarioId(scenarioId);
		batch.setHttpStatus(APIConstants.INITIATED);
		batch.setCreatedBy(user);
		batch.setCreatedOn(LocalDateTime.now());
		batch.setDelete(false);
		batch.setDataType(dataType != null ? dataType.toUpperCase() : null);
		batch.setJobType(jobType != null ? jobType.toUpperCase() : null);
		batch.setErpId(erpId);
		batch.setPayloadId(payloadId);
		
		
		return batch;
	}
	
	public AnxErpBatchEntity createErpBatch(String groupcode, Long entityId,
			String gstin, String destinationName, Long scenarioId, Long size, 
			String dataType, String jobType, Long erpId, String payloadId, 
			String user, String section, Long jobId, String chunkStatus) {
		AnxErpBatchEntity batch = new AnxErpBatchEntity();
		batch.setGroupcode(groupcode);
		batch.setEntityId(entityId);
		batch.setGstin(gstin);
		batch.setBatchSize(size);
		batch.setDestinationName(destinationName);
		batch.setScenarioId(scenarioId);
		batch.setHttpStatus(APIConstants.INITIATED);
		batch.setCreatedBy(user);
		batch.setCreatedOn(LocalDateTime.now());
		batch.setDelete(false);
		batch.setDataType(dataType != null ? dataType.toUpperCase() : null);
		batch.setJobType(jobType != null ? jobType.toUpperCase() : null);
		batch.setErpId(erpId);
		batch.setPayloadId(payloadId);
		batch.setSection(section);
		batch.setJobId(jobId);
		batch.setChunkStatus(chunkStatus);
		return batch;
	}
}
