package com.ey.advisory.services.days180.api.push;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Service(value = "PaymentReference180DaysPayloadMetadataRevIntHandler")
public class PaymentReference180DaysPayloadMetadataRevIntHandler {

	@Autowired
	@Qualifier("PaymentReference180DaysPayloadMetadataRevIntService")
	private PaymentReference180DaysPayloadMetadataRevIntService service;

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
			PayloadDocsRevIntegrationReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"PaymentReference180DaysPayloadMetadataRevIntService"
					+ ".getPayloadMetadataDetails Begin");
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
			PayloadMetaDataXMLDto payloadMetaDataDto = service
					.payloadErrorInfoMsg(payloadId);
			long currentBatchSize = 0;
			if (payloadMetaDataDto != null
					&& payloadMetaDataDto.getResp() != null) {
				currentBatchSize = payloadMetaDataDto.getResp()
						.getDockeyItems().size();
				
			} else {
				currentBatchSize = 0;
			}
			if (permEntity != null) {
				AnxErpBatchEntity batchEntity = erpBatchHandler.createErpBatch(
						groupCode, entityId, null, permEntity.getDestName(),
						scenarioId, currentBatchSize, "PaymentReference_180Days",
						ERPConstants.EVENT_BASED_JOB, permEntity.getErpId(),
						payloadId, APIConstants.SYSTEM.toUpperCase());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Batch is created as {} ", batchEntity);
				}
				responseCode = destinationConn.pushToErp(payloadMetaDataDto,
						"PayloadMetaDataXMLDto", batchEntity);
			}
			responseCode = responseCode != null ? responseCode : 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ", destName,
						responseCode);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			throw new AppException(e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"PaymentReference180DaysPayloadMetadataRevIntService"
					+ ".getPayloadMetadataDetails End");
		}
		return responseCode;
	}
}
