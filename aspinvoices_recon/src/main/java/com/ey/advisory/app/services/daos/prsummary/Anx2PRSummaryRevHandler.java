package com.ey.advisory.app.services.daos.prsummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.erp.Anx2PRSummaryDetailDto;
import com.ey.advisory.app.docs.dto.erp.Anx2PRSummaryDto;
import com.ey.advisory.app.docs.dto.erp.Anx2PRSummaryHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Anx2PRSummaryItemDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Anx2PRSProcessedRequestDto;
import com.ey.advisory.core.dto.Anx2PRSProcessedResponseDto;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

@Service("Anx2PRSummaryRevHandler")
public class Anx2PRSummaryRevHandler {

	@Autowired
	@Qualifier("Anx2PRSummaryRevServiceImpl")
	private Anx2PRSummaryRevServiceImpl anx2PRSummaryRevService;

	@Autowired
	@Qualifier("Anx2PRSProcessedDataServiceImpl")
	private Anx2PRSProcessedDataService anx2PRSProcessedDataService;

	Integer respCode = 0;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

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

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx2PRSummaryRevHandler.class);

	public Integer getAnx2PRSummary(
			final RevIntegrationScenarioTriggerDto dto) {

		try {
			String groupCode = TenantContext.getTenantId();
			String destName = dto.getDestinationName();
			Long entityId = dto.getEntityId();
			String gstin = dto.getGstin();
			String stateCode = gstin.substring(0, 2);
			String state = statecodeRepository.findStateNameByCode(stateCode);
			Long scenoId = dto.getScenarioId();
			String entityName = null;
			String entityPan = null;
			String companyCode = null;
			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();
				entityPan = entity.get().getPan();
				companyCode = entity.get().getCompanyHq();
			}

			// Get gstin based on gst name
			Long gstinId = gstnDetailRepo.findIdByGstin(gstin);

			// Get erp id based on gstin id,scenariodId and entity Id
			ErpScenarioPermissionEntity permEntity = permRepository
					.findSceIdBasedScenIdAndGstinAndEntity(gstinId, scenoId,
							entityId);
			Anx2PRSProcessedRequestDto criteria = new Anx2PRSProcessedRequestDto();

			List<String> gstins = new ArrayList<>();
			gstins.add(gstin);
			Map<String, List<String>> dataSecAttrs = new HashMap<>();
			dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
			criteria.setDataSecAttrs(dataSecAttrs);
			List<String> docType = new ArrayList<>();
			criteria.setDocType(docType);
			List<String> recoType = new ArrayList<>();
			criteria.setRecordType(recoType);
			List<Anx2PRSProcessedResponseDto> respDto = anx2PRSProcessedDataService
					.getAnx2PRSProcessedRecords(criteria);

			Anx2PRSummaryHeaderDto headerDto = anx2PRSummaryRevService
					.convertObjToHeader(entityName, entityPan, companyCode,
							state, respDto);
			Set<String> returnPeriod = new HashSet<>();
			if (!headerDto.getHeader().isEmpty()) {
				List<Anx2PRSummaryItemDto> headers = headerDto.getHeader();
				headers.forEach(header -> {
					returnPeriod.add(header.getRetPer());
				});
			}
			List<String> returnPeriods = new ArrayList<>();
			returnPeriods.addAll(returnPeriod);
			Anx2PRSummaryDetailDto detailsDto = new Anx2PRSummaryDetailDto();
			List<Anx2PRSummaryItemDto> itemDtos = new ArrayList<>();
			for (String returnPer : returnPeriods) {
				anx2PRSummaryRevService.convertObjToDetails(gstin, entityName,
						entityPan, companyCode, state, returnPer, itemDtos);
			}
			detailsDto.setItemDto(itemDtos);
			long batchSize = 0;
			if (detailsDto.getItemDto() != null) {
				batchSize = detailsDto.getItemDto().size();
				AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(
						groupCode, entityId, gstin, destName, scenoId,
						batchSize, "Anx2PRSummary",
						ERPConstants.BACKGROUND_BASED_JOB,
						permEntity.getErpId(), null,
						APIConstants.SYSTEM.toUpperCase());

				Anx2PRSummaryDto summaryDto = new Anx2PRSummaryDto();
				summaryDto.setHeaderDto(headerDto);
				summaryDto.setDetailDto(detailsDto);
				respCode = destinationConn.pushToErp(summaryDto,
						"Anx2PRSummaryDto", batch);

			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"No Data found to do review sumry reverse integration");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
		}
		return respCode;
	}
}
