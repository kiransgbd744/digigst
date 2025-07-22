package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.erp.Anx1AProcessReviewSummaryReqHeaderItemDto;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

@Service("Anx1AProcessAndReviewSummaryRevIntegHandler")
public class Anx1AProcessAndReviewSummaryRevIntegHandler {

	@Autowired
	@Qualifier("Anx1AProcessAndReviewSummaryRevIntegDocsImpl")
	private Anx1AProcessAndReviewSummaryRevIntegDocsImpl docsImpl;

	private static Logger LOGGER = LoggerFactory
			.getLogger(Anx1AProcessAndReviewSummaryRevIntegHandler.class);

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;
	@Autowired
	private AnxErpBatchRepository batchRepo;

	Integer respcode = 0;

	@Autowired
	private DestinationConnectivity destinationConn;

	public Integer processReviewSummryToErp(
			RevIntegrationScenarioTriggerDto dto) {

		try {
			TenantContext.setTenantId(dto.getGroupcode());
			String destinationName = dto.getDestinationName();
			String gstin = dto.getGstin();
			Long entityId = dto.getEntityId();
			String stateCode = gstin.substring(0, 2);
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			Long scenarioId = dto.getScenarioId();
			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			String entityName = null;
			String entityPan = null;
			String companyCode = null;
			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();
				entityPan = entity.get().getPan();
				companyCode = entity.get().getCompanyHq();
			}
			Anx1AProcessReviewSummaryReqHeaderItemDto headerDto = docsImpl
					.convertProcReviewSum(gstin, entityPan, stateName,
							entityName, companyCode);
			if (headerDto != null && headerDto.getDtos() != null) {
				long currentBatchSize = headerDto.getDtos().size();
				AnxErpBatchEntity batch = setErpBatch(dto.getGroupcode(),
						entityId, gstin, destinationName, scenarioId,
						currentBatchSize, null);
				batchRepo.save(batch);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Batch is created as {} ", batch);
				}
				respcode = destinationConn.pushToErp(headerDto,
						"Anx1AProcessReviewSummaryReqHeaderItemDto", batch);
				// respcode = docsImpl.pushToErp(headerDto, destinationName,
				// batch);

				respcode = respcode != null ? respcode : 0;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Destination {} -> Response code is {} ",
							destinationName, respcode);
				}
				/*
				 * if (respcode == 200) { batch.setSuccess(true);
				 * batch.setStatus(APIConstants.SUCCESS); } else {
				 * batch.setSuccess(false);
				 * batch.setStatus(APIConstants.FAILED); } // Erp Batch updation
				 * batchRepo.save(batch);
				 */
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return respcode;
	}

	private AnxErpBatchEntity setErpBatch(String groupcode, Long entityId,
			String gstin, String destinationName, Long scenarioId, Long size,
			String dataType) {
		AnxErpBatchEntity batch = new AnxErpBatchEntity();
		batch.setGroupcode(groupcode);
		batch.setEntityId(entityId);
		batch.setGstin(gstin);
		batch.setBatchSize(size);
		batch.setDestinationName(destinationName);
		batch.setScenarioId(scenarioId);
		batch.setDataType(dataType);
		batch.setHttpStatus(APIConstants.INITIATED);
		batch.setCreatedBy(APIConstants.SYSTEM);
		batch.setCreatedOn(LocalDateTime.now());
		batch.setDelete(false);
		return batch;
	}
}
