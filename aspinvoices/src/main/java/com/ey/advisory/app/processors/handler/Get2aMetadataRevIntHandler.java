package com.ey.advisory.app.processors.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.erp.Get2aMetaDataDto;
import com.ey.advisory.app.docs.dto.erp.Get2aMetadataRevIntReqDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Get2aMetadataRevIntHandler")
public class Get2aMetadataRevIntHandler {

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository getAnx1BatchRepo;
	
	@Autowired
	@Qualifier("ErpScenarioMasterRepository")
	private ErpScenarioMasterRepository erpScenarioMasterRepository;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository erpEventsScenPermiRep;

	@Autowired
	private DestinationConnectivity destinationConn;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	private ErpInfoEntityRepository erpInfoRepo;
	
	public Integer getGet2aMetadataDetails(Get2aMetadataRevIntReqDto dto) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get2aMetadataRevIntHandler getGet2aMetadataDetails Begin");
		}
		Long batchId = dto.getBatchId();
		String destName = null;
		Long scenarioId = null;
		String groupcode = dto.getGroupcode();
		String scenarioName = dto.getScenarioName() != null ? dto.getScenarioName() : "Get2aMetadataRevIntg";
		String sourceId = dto.getSourceId();
		Long erpId = null;
		
		TenantContext.setTenantId(groupcode);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tenant Id: {}", TenantContext.getTenantId());
		}
		if (sourceId != null) {
			erpId = erpInfoRepo.getErpId(sourceId);
		}
		if (scenarioName != null) {
			scenarioId = erpScenarioMasterRepository.findSceIdOnScenarioName(scenarioName);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Scenario Id: {}", scenarioId);
		}
		ErpEventsScenarioPermissionEntity permEntity = erpEventsScenPermiRep
				.getEntityByScenarioIdAndErpId(scenarioId, erpId);
		destName = permEntity.getDestName();
		
		GetAnx1BatchEntity entity = getAnx1BatchRepo.findByIdAndIsDeleteFalse(batchId);
		Get2aMetaDataDto metaDataDto = formPayload(entity);
		
		AnxErpBatchEntity batchEntity = erpBatchHandler.createErpBatch(groupcode, null, null,
				permEntity.getDestName(), scenarioId, 1l, null, ERPConstants.EVENT_BASED_JOB,
				permEntity.getErpId(), null, APIConstants.SYSTEM.toUpperCase(), entity.getType(), dto.getJobId(), null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Batch is created as {} ", batchEntity);
		}
		Integer responseCode = destinationConn.pushToErp(metaDataDto, "Get2aMetaDataDto", batchEntity);

		responseCode = responseCode != null ? responseCode : 0;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Destination {} -> Response code is {} ", destName, responseCode);
		}
		
		return responseCode;
	}
	
	private Get2aMetaDataDto formPayload(GetAnx1BatchEntity entity) {

		Get2aMetaDataDto dto = new Get2aMetaDataDto();

		dto.setGetBatchId(entity.getId());
		dto.setGstin(entity.getSgstin());
		dto.setTaxPeriod(entity.getTaxPeriod());
		dto.setSection(entity.getType());
		dto.setTotalCount(entity.getInvCount());
		dto.setDeltaNewCount(entity.getDeltaNewCount());
		dto.setDeltaModCount(entity.getDeltaModifiedCount());
		dto.setDeltaDelCount(entity.getDeltaDeletedCount());
		dto.setDeltaTotCount(entity.getDeltaTotalCount());

		return dto;

	}
}
