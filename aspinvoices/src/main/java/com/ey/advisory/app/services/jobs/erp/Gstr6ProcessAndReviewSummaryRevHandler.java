package com.ey.advisory.app.services.jobs.erp;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.ErpCompanyCodeMappingRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.erp.Gstr6RevIntProcAndReviewSumFinalDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr6ProcessAndReviewSummaryRevHandler")
public class Gstr6ProcessAndReviewSummaryRevHandler {

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("Gstr6ProcessAndReviewSummaryRevServiceImpl")
	private Gstr6ProcessAndReviewSummaryRevServiceImpl gstr6ProcAndRevSumRevService;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

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
	
	@Autowired
	@Qualifier("ErpCompanyCodeMappingRepository")
	private ErpCompanyCodeMappingRepository companyCodeRepo;


	public Integer getProcessAndReviewSev(
			RevIntegrationScenarioTriggerDto dto) {
		Integer respcode = 0;
		try {
			String destinationName = dto.getDestinationName();
			Long scenarioId = dto.getScenarioId();
			String groupCode = dto.getGroupcode();
			String gstin = dto.getGstin();
			Long entityId = dto.getEntityId();
			Long erpId = dto.getErpId();
			String entityName = null;
			String entityPan = null;
			String companyCode = companyCodeRepo.getCompanyCode(erpId, entityId);
			String stateCode = gstin.substring(0, 2);
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();
			//	entityName = entityName.length() <= 25 ? entityName : entityName.substring(0, 25);
				entityPan = entity.get().getPan();
			}

			// Get gstin based on gst name
			Long gstinId = gstnDetailRepo.findIdByGstin(gstin);

			// Get erp id based on gstin id,scenariodId and entity Id
			
			ErpScenarioPermissionEntity permEntity = permRepository
					.findByScenarioIdAndGstinIdAndErpIdAndEntityIdAndIsDeleteFalse(dto.getScenarioId(),gstinId,erpId,entityId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Scenario Permission is configured for Scenario Id %s and Entity Id %s",
						scenarioId, entityId);
				LOGGER.debug(msg);
			}

			Gstr6RevIntProcAndReviewSumFinalDto sumFinalDto = gstr6ProcAndRevSumRevService
					.getGstr6RevIntProcAnReview(gstin, entityPan, stateName,
							entityName, companyCode);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"FinalDto is %s",
						sumFinalDto);
				LOGGER.debug(msg);
			}
			
			long currentBatchSize = 0;
			if (sumFinalDto.getGstr6RevIntProcAndReviewSumDtos() != null
					&& !sumFinalDto.getGstr6RevIntProcAndReviewSumDtos()
							.isEmpty()) {
				currentBatchSize = currentBatchSize + sumFinalDto
						.getGstr6RevIntProcAndReviewSumDtos().size();
			}

			AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(groupCode,
					entityId, gstin, destinationName, scenarioId,
					currentBatchSize, "Gstr6 Process Review Summary",
					ERPConstants.BACKGROUND_BASED_JOB,
					permEntity != null ? permEntity.getErpId() : null, null,
					APIConstants.SYSTEM.toUpperCase());
			respcode = destinationConn.pushToErp(sumFinalDto,
					"Gstr6RevIntProcAndReviewSumFinalDto", batch);

			respcode = respcode != null ? respcode : 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ",
						destinationName, respcode);
			}
			if (respcode == 200) {
				batch.setSuccess(true);
				batch.setHttpCode(respcode);
			} else {
				batch.setSuccess(false);
				batch.setHttpCode(respcode);
			}
			// Erp Batch updation
			batchRepo.save(batch);
		} catch (Exception e) {
			LOGGER.error("Exception thorws while fectching Data", e);
			throw new AppException(e);
		}
		return respcode;
	}
}
