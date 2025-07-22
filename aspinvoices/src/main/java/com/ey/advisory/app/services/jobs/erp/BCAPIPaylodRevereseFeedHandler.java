package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.app.docs.dto.erp.BCAPIOutwardPayloadErrorIFinalDto;
import com.ey.advisory.app.docs.dto.erp.BCAPIOutwardPayloadErrorItemDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("BCAPIPaylodRevereseFeedHandler")
public class BCAPIPaylodRevereseFeedHandler {

	@Autowired
	@Qualifier("BCAPIPaylodRevereseFeedServiceImpl")
	private BCAPIPaylodRevereseFeedServiceImpl bcapiPayRevFeedServImpl;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

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

	public Integer bcapiPaylodToErp(PayloadDocsRevIntegrationReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"BCAPIPaylodRevereseFeedHandler bcapiPaylodToErp BEGIN");
		}

		Integer responseCode = 0;
		String destName = null;
		Long scenarioId = null;
		Long entityId = null;
		String groupCode = reqDto.getGroupcode();
		String payloadId = reqDto.getPayloadId();
		String scenarioName = reqDto.getScenarioName();
		String sourceId = reqDto.getSourceId();
		Long erpId = null;

		TenantContext.setTenantId(groupCode);
		try {
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
			List<BCAPIOutwardPayloadErrorItemDto> list = bcapiPayRevFeedServImpl
					.getObjectPayload(payloadId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"BCAPIPaylodRevereseFeedHandler bcapiPaylodToErp END");
			}
			long currentBatchSize = 0;

			if (permEntity != null && !list.isEmpty()) {
				currentBatchSize = list.size();
				AnxErpBatchEntity batchEntity = erpBatchHandler.createErpBatch(
						groupCode, entityId, null, permEntity.getDestName(),
						scenarioId, currentBatchSize, "OUTWARD",
						ERPConstants.EVENT_BASED_JOB, permEntity.getErpId(),
						payloadId, APIConstants.SYSTEM.toUpperCase());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Batch is created as {} ", batchEntity);
				}
				BCAPIOutwardPayloadErrorIFinalDto bcApiOutwardPayldto = new BCAPIOutwardPayloadErrorIFinalDto();
				bcApiOutwardPayldto.setItemDtos(list);
				responseCode = destinationConn.pushToErp(bcApiOutwardPayldto,
						"BCAPIOutwardPayloadErrorIFinalDto", batchEntity);
			}
			responseCode = responseCode != null ? responseCode : 0;
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Destination {} -> Response code is {} ", destName,
					responseCode);
		}
		return responseCode;
	}
}
