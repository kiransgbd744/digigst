package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.data.returns.compliance.service.ComplainceHistoryRevIntFinalDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplainceHistoryRevIntItemDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

import lombok.extern.slf4j.Slf4j;

@Service("ComplainceHistoryRevIntHandler")
@Slf4j
public class ComplainceHistoryRevIntHandler {

	@Autowired
	private DestinationConnectivity connectivity;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entiyInfoDetRepo;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodRepo;

	@Autowired
	private AnxErpBatchHandler anxErpBatchHandler;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository scePermRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	@Qualifier("ComplainceHistoryRevIntServiceImpl")
	private ComplainceHistoryRevIntServiceImpl compHistRevIntSevImpl;

	@Autowired
	@Qualifier("ErpScenarioMasterRepository")
	private ErpScenarioMasterRepository erpScenarioMasterRepository;

	public Integer complainceHistoryReqToErp(
			RevIntegrationScenarioTriggerDto dto) {

		Integer responseCode = 0;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ComplainceHistoryRevIntHandler Begin");
		}
		try {
			String groupCode = dto.getGroupcode();
			String gstin = dto.getGstin();
			String scenarioName = dto.getScenarioName();
			// String erpSourceId = dto.getSourceId();
			Long scenarioId = null;
			// Long erpId = dto.getErpId() != null ? dto.getErpId() : 1;
			// Find Source Id
			// Long erpId = erpInfoRepo.getErpId(erpSourceId);

			// Find scenario name
			scenarioId = erpScenarioMasterRepository
					.findSceIdOnScenarioName(scenarioName);

			String finYear = dto.getFinYear();
			String section = dto.getSection();

			// Find Active GSTIN Id Based on GSTIN
			GSTNDetailEntity gstnDetailsEntity = gstinDetailRepo
					.findByGstinAndIsDeleteFalse(gstin);
			Long entityId = gstnDetailsEntity.getEntityId();

			// Find the active Entity Details using Entity Id
			EntityInfoEntity entity = entiyInfoDetRepo
					.findByIdAndIsDeleteFalse(entityId);
			String entityName = null;
			String entityPan = null;
			if (entity != null) {
				entityName = entity.getEntityName();
				entityPan = entity.getPan();
			}
			String regType = gstnDetailsEntity.getRegistrationType();
			if (regType != null) {
				regType = regType.substring(0, 1).toUpperCase()
						+ regType.substring(1).toLowerCase();
			}
			List<ComplainceHistoryRevIntItemDto> compHistRevIntItemDtos = compHistRevIntSevImpl
					.getComplainceHistory(entityName, gstin, entityPan,
							entityId, regType, finYear, section);
			ComplainceHistoryRevIntFinalDto finalDto = new ComplainceHistoryRevIntFinalDto();
			List<ErpEventsScenarioPermissionEntity> permEntities = scePermRepo
					.getErpEventsScenarioPerms(scenarioId);

			for (ErpEventsScenarioPermissionEntity permEntity : permEntities) {
				if (permEntity != null) {
					long currentBatchSize = compHistRevIntItemDtos.size();
					AnxErpBatchEntity batchEntity = anxErpBatchHandler
							.createErpBatch(groupCode, entityId, gstin,
									permEntity.getDestName(), scenarioId,
									currentBatchSize, null,
									ERPConstants.EVENT_BASED_JOB,
									permEntity.getErpId(), null,
									APIConstants.SYSTEM.toUpperCase());
					finalDto.setItems(compHistRevIntItemDtos);
					responseCode = connectivity.pushToErp(finalDto,
							"ComplainceHistoryRevIntFinalDto", batchEntity);

					if (responseCode == 200) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug(
									"Destination {} -> Response code is {} ",
									permEntity.getDestName(), responseCode);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
			throw new AppException(e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ComplainceHistoryRevIntHandler End Response Code:{}",
					responseCode);
		}
		return responseCode;
	}

}
