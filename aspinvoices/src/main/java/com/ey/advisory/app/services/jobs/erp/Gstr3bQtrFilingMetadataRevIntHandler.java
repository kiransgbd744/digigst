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
import com.ey.advisory.app.docs.dto.erp.Gstr3bQtrFilingPayloadMetaDataDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Service(value = "Gstr3bQtrFilingMetadataRevIntHandler")
public class Gstr3bQtrFilingMetadataRevIntHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3bQtrFilingMetadataRevIntHandler.class);

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("Gstr3bQtrFilingMetadataRevIntServiceImpl")
	private Gstr3bQtrFilingMetadataRevIntService serviceImpl;

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
					"Gstr3bQtrFilingMetadataRevIntHandler getPayloadMetaDataDetails Begin");
		}
		Integer responseCode = 0;
		try {

			String destName = null;
			Long scenarioId = null;
			String groupCode = dto.getGroupcode();
			String payloadId = dto.getPayloadId();
			String scenarioName = dto.getScenarioName();
			String sourceId = dto.getSourceId();
			Long erpId = null;

			TenantContext.setTenantId(groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Tenant Id: {}", TenantContext.getTenantId());
			}
//			if (sourceId != null) {
//				erpId = erpInfoRepo.getErpId(sourceId);
//			}

			if (scenarioName != null) {
				scenarioId = erpScenarioMasterRepository
						.findSceIdOnScenarioName(scenarioName);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Scenario Id: {}", scenarioId);
			}
			List<ErpEventsScenarioPermissionEntity> permEntities = erpEventsScenPermiRep
					.getErpEventsScenarioPerms(scenarioId);
			Gstr3bQtrFilingPayloadMetaDataDto payloadMetaDataDto = serviceImpl
					.payloadErrorInfoMsg(null, payloadId);
			
			long currentBatchSize = 0;
			if (payloadMetaDataDto != null
					&& payloadMetaDataDto.getGstinDetailDto() != null
					&& payloadMetaDataDto.getGstinDetailDto().getGstinDetailDto() != null) {
				currentBatchSize = payloadMetaDataDto.getGstinDetailDto()
						.getGstinDetailDto().size();
				
			} else {
				currentBatchSize = 0;
			}
			
			if (!permEntities.isEmpty()) {
				ErpEventsScenarioPermissionEntity permEntity = permEntities.get(0);
				AnxErpBatchEntity batchEntity = erpBatchHandler.createErpBatch(
						groupCode, null, null, permEntity.getDestName(),
						scenarioId, currentBatchSize, "CFS3B_VENDOR_STATUS",
						ERPConstants.EVENT_BASED_JOB, permEntity.getErpId(),
						payloadId, APIConstants.SYSTEM.toUpperCase());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Batch is created as {} ", batchEntity);
				}
				responseCode = destinationConn.pushToErp(payloadMetaDataDto,
						"Gstr3bQtrFilingPayloadMetaDataDto", batchEntity);
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
					"Gstr3bQtrFilingMetadataRevIntHandler getGstinValidatorMetaPayload End");
		}
		return responseCode;
	}
}
