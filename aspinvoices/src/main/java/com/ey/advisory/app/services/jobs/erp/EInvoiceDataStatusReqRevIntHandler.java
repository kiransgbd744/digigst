package com.ey.advisory.app.services.jobs.erp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDataHeaderDto;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDataSummaryDto;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDataSummaryItemDto;
import com.ey.advisory.app.docs.dto.erp.EInvoiceDataStatusRequestDto;
import com.ey.advisory.app.docs.dto.erp.EinvoiceDataStatusRequestItemDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

@Component("EInvoiceDataStatusReqRevIntHandler")
public class EInvoiceDataStatusReqRevIntHandler {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("EInvoiceDataStatusReqRevIntServiceImpl")
	private EInvoiceDataStatusReqRevIntServiceImpl eInvDataStatusReqRevIntService;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	private AnxErpBatchRepository batchRepo;

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
			.getLogger(EInvoiceDataStatusReqRevIntHandler.class);

	public Integer dataStatusToERP(RevIntegrationScenarioTriggerDto dto) {
		Integer responseCode = 0;
		try {
			String tenantId = TenantContext.getTenantId();
			String destinationName = dto.getDestinationName();
			Long scenarioId = dto.getScenarioId();
			String groupcode = dto.getGroupcode();
			String gstin = dto.getGstin();
			Long entityId = dto.getEntityId();
			String stateCode = gstin.substring(0, 2);

			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			String entityName = null;
			String entityPan = null;
			String companyCode = null;
			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();
				entityPan = entity.get().getPan();
				companyCode = entity.get().getCompanyHq();
			}
			// Get gstin based on gst name
			Long gstinId = gstnDetailRepo.findEntityIdByGstin(gstin);

			// Get erp id based on gstin id,scenariodId and entity Id
			ErpScenarioPermissionEntity permEntity = permRepository
					.findSceIdBasedScenIdAndGstinAndEntity(gstinId, scenarioId,
							entityId);
			List<EinvoiceDataStatusRequestItemDto> itemDtos = new ArrayList<>();
			eInvDataStatusReqRevIntService.getDataStatusProcessData(companyCode,
					entityName, entityId, entityPan,
					APIConstants.OUTWARD_SUMMARY, gstin, itemDtos);
			List<EInvoiceDataStatusRequestDataSummaryItemDto> dataSummItemDtos = new ArrayList<>();
			eInvDataStatusReqRevIntService.eInvoiceDataStatus(companyCode,
					entityName, entityPan, gstin, "OUTWARD", dataSummItemDtos);

			eInvDataStatusReqRevIntService.getDataStatusProcessData(companyCode,
					entityName, entityId, entityPan,
					APIConstants.INWARD_SUMMARY, gstin, itemDtos);

			eInvDataStatusReqRevIntService.eInvoiceDataStatus(companyCode,
					entityName, entityPan, gstin, "INWARD", dataSummItemDtos);

			EInvoiceDataStatusRequestDataHeaderDto headerDto = new EInvoiceDataStatusRequestDataHeaderDto();
			headerDto.setItems(itemDtos);

			EInvoiceDataStatusRequestDataSummaryDto summaryDto = new EInvoiceDataStatusRequestDataSummaryDto();
			summaryDto.setItems(dataSummItemDtos);

			long currentBatchSize = 0;
			if (!itemDtos.isEmpty()) {
				currentBatchSize = currentBatchSize + itemDtos.size();
			}

			if (!dataSummItemDtos.isEmpty()) {
				currentBatchSize = currentBatchSize + dataSummItemDtos.size();
			}

			AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(groupcode,
					entityId, gstin, destinationName, scenarioId,
					currentBatchSize, "Data Status",
					ERPConstants.BACKGROUND_BASED_JOB, permEntity.getErpId(),
					null, APIConstants.SYSTEM.toUpperCase());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Batch is created as {} ", batch);
			}

			EInvoiceDataStatusRequestDto reqDto = new EInvoiceDataStatusRequestDto();
			reqDto.setHeaderDto(headerDto);
			reqDto.setSummaryDto(summaryDto);

			responseCode = destinationConn.pushToErp(reqDto,
					"EInvoiceDataStatusRequestDto", batch);
			responseCode = responseCode != null ? responseCode : 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ",
						destinationName, responseCode);
			}

		} catch (Exception e) {
			LOGGER.error("Exception Occured:{}", e);
		}
		return responseCode;
	}
}
