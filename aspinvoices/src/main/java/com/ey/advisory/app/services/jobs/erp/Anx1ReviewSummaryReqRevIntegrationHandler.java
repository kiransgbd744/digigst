/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.docs.dto.erp.Anx1ReviewSummaryRequestDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

/**
 * @author Umesha M
 *
 */
@Service("Anx1ReviewSummaryReqRevIntegrationHandler")
public class Anx1ReviewSummaryReqRevIntegrationHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ErrorDocsRevIntegrationHandler.class);

	@Autowired
	@Qualifier("Anx1ReviewSummaryRequestDocsImpl")
	private Anx1ReviewSummaryRequestDocs anx1ReviewSummaryRequestDocs;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	Integer respcode = 0;

	@Autowired
	private DestinationConnectivity destinationConn;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	@Qualifier("ErpScenarioPermissionRepository")
	private ErpScenarioPermissionRepository permRepository;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepo;

	public Integer reviewSummaryRequestToErp(
			RevIntegrationScenarioTriggerDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"reviewSummaryRequestToErp method called with reqDto {}",
					dto);
		}
		try {
			String destinationName = dto.getDestinationName();
			Long scenarioId = dto.getScenarioId();
			String groupCode = dto.getGroupcode();
			String gstin = dto.getGstin();
			Long entityId = dto.getEntityId();
			TenantContext.setTenantId(groupCode);
			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			String pan = null;
			String entityName = null;
			String companyCode = null;
			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();
				pan = entity.get().getPan();
				companyCode = entity.get().getCompanyHq();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("enity name {} ", entityName);
			}
			Anx1ReviewSummaryRequestDto itemDto = anx1ReviewSummaryRequestDocs
					.convertDocsAsDtos(gstin, entityName, pan, companyCode,
							null);
			// Get gstin based on gst name
			Long gstinId = gstnDetailRepo.findEntityIdByGstin(gstin);

			// Get erp id based on gstin id,scenariodId and entity Id
			ErpScenarioPermissionEntity permEntity = permRepository
					.findSceIdBasedScenIdAndGstinAndEntity(gstinId, scenarioId,
							entityId);

			if (itemDto.getImItem() != null && !itemDto.getImItem().isEmpty()) {
				long currentBatchSize = itemDto.getImItem().size();
				AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(
						groupCode, entityId, gstin, destinationName, scenarioId,
						currentBatchSize, "Anx1 Review Sum",
						ERPConstants.BACKGROUND_BASED_JOB,
						permEntity.getErpId(), null,
						APIConstants.SYSTEM.toUpperCase());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Batch is created as {} ", batch);
				}

				respcode = destinationConn.pushToErp(itemDto,
						"Anx1ReviewSummaryRequestDto", batch);
				/*
				 * respcode = anx1ReviewSummaryRequestDocs.pushToErp(itemDto,
				 * destinationName, batch);
				 */
				respcode = respcode != null ? respcode : 0;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Destination {} -> Response code is {} ",
							destinationName, respcode);
				}

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"No Data found to do review sumry reverse integration");
				}
			}
			return respcode;
		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			return null;
		}
	}
}
