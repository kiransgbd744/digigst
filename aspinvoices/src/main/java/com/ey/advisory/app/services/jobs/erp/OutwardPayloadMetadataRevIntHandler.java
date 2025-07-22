package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.app.docs.dto.erp.PayloadErrorInfoMesgItemDto;
import com.ey.advisory.app.docs.dto.erp.PayloadMetaDataDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;

/**
 * 
 * @author Umesha.M
 *
 */
@Service(value = "OutwardPayloadMetadataRevIntHandler")
public class OutwardPayloadMetadataRevIntHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OutwardPayloadMetadataRevIntHandler.class);

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("OutwardPayloadMetadataRevIntServiceImpl")
	private OutwardPayloadMetadataRevIntServiceImpl serviceImpl;

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

	public Integer getPayloadMetadataDetails(
			PayloadDocsRevIntegrationReqDto dto, String type) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"OutwardPayloadMetadataRevIntHandler getOutwardPayloadMetadata Begin");
		}
		Integer responseCode = 0;
		try {

			String destName = null;
			Long scenarioId = null;
			Long entityId = null;
			String groupCode = dto.getGroupcode();
			String payloadId = dto.getPayloadId();
			String scenarioName = dto.getScenarioName();
			String sourceId = dto.getSourceId();
			Long erpId = null;

			TenantContext.setTenantId(groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Tenant Id: {}", TenantContext.getTenantId());
			}
			if (sourceId != null) {
				erpId = erpInfoRepo.getErpId(sourceId);
			}

			if (scenarioName != null) {
				scenarioId = erpScenarioMasterRepository
						.findSceIdOnScenarioName(scenarioName);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Scenario Id: {}", scenarioId);
			}
			ErpEventsScenarioPermissionEntity permEntity = erpEventsScenPermiRep
					.getEntityByScenarioIdAndErpId(scenarioId, erpId);
			PayloadMetaDataDto payloadMetaDataDto = serviceImpl
					.payloadErrorInfoMsg(type, payloadId);
			long currentBatchSize = 0;
			if (payloadMetaDataDto != null
					&& payloadMetaDataDto.getErrorMsgDto() != null
					&& payloadMetaDataDto.getErrorMsgDto().getItems() != null) {
				currentBatchSize = payloadMetaDataDto.getErrorMsgDto()
						.getItems().size();
				List<PayloadErrorInfoMesgItemDto> itemDtos = payloadMetaDataDto
						.getErrorMsgDto().getItems();
				entityId = entityRepo
						.findIdEntityId(itemDtos.get(0).getEntityName());

			} else {
				currentBatchSize = 0;
			}
			if (permEntity != null) {
				AnxErpBatchEntity batchEntity = erpBatchHandler.createErpBatch(
						groupCode, entityId, null, permEntity.getDestName(),
						scenarioId, currentBatchSize, type,
						ERPConstants.EVENT_BASED_JOB, permEntity.getErpId(),
						payloadId, APIConstants.SYSTEM.toUpperCase(), type, dto.getJobId(), null);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Batch is created as {} ", batchEntity);
				}
				responseCode = destinationConn.pushToErp(payloadMetaDataDto,
						"PayloadMetaDataDto", batchEntity);
			}
			responseCode = responseCode != null ? responseCode : 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ", destName,
						responseCode);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"OutwardPayloadMetadataRevIntHandler getOutwardPayloadMetadata End");
		}
		return responseCode;
	}
}
