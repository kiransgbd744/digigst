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
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.erp.Anx1ApprovalReqDto;
import com.ey.advisory.app.docs.dto.erp.Anx1ApprovalRequestHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Anx1ReviewSummaryRequestDto;
import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1ApprovalReqRevIntegrationHandler")
public class Anx1ApprovalReqRevIntegrationHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ErrorDocsRevIntegrationHandler.class);

	@Autowired
	@Qualifier("Anx1ApprovalRequestDocsImpl")
	private Anx1ApprovalRequestDocs approvalReqDocs;

	@Autowired
	@Qualifier("Anx1ReviewSummaryRequestDocsImpl")
	private Anx1ReviewSummaryRequestDocsImpl reqDocs;

	@Autowired
	private DocRepository docRepo;

	@Autowired
	private AnxErpBatchRepository batchRepo;

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

	public Integer approvalRequestToErp(ApprovalStatusReqDto erpDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("approvalRequestToErp method called with reqDto {} ",
					erpDto);
		}
		try {
			String destinationName = erpDto.getDestinationName();
			Long scenarioId = erpDto.getScenarioId();
			String groupcode = erpDto.getGroupcode();
			String gstin = erpDto.getGstin();
			String returnPeriod = erpDto.getReturnPeriod();
			Long entityId = erpDto.getEntityId();
			TenantContext.setTenantId(groupcode);
			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			String entityName = null;
			String companyCode = null;
			String pan = null;
			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();
				pan = entity.get().getPan();
				companyCode = entity.get().getCompanyHq();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("enity name {} ", entityName, pan, companyCode);
			}

			Anx1ApprovalRequestHeaderDto headerDto = approvalReqDocs
					.convertDocsAsHeaderDtos(returnPeriod, gstin, entityName,
							pan, companyCode);
			// Get gstin based on gst name
			Long gstinId = gstnDetailRepo.findEntityIdByGstin(gstin);

			// Get erp id based on gstin id,scenariodId and entity Id
			ErpScenarioPermissionEntity permEntity = permRepository
					.findSceIdBasedScenIdAndGstinAndEntity(gstinId, scenarioId,
							entityId);
			Anx1ReviewSummaryRequestDto reqDto = reqDocs.convertDocsAsDtos(
					gstin, entityName, pan, companyCode, returnPeriod);

			if (reqDto.getImItem() != null && !reqDto.getImItem().isEmpty()) {
				long currentBatchSize = reqDto.getImItem().size();
				AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(
						groupcode, entityId, gstin, destinationName, scenarioId,
						currentBatchSize, "Anx1 Approval",
						ERPConstants.BACKGROUND_BASED_JOB,
						permEntity.getErpId(), null,
						APIConstants.SYSTEM.toUpperCase());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Batch is created as {} ", batch);
				}
				// update selected invoices with batch id
				docRepo.updateDocsWithErpWorkflowBatchId(returnPeriod, gstin,
						batch.getId());

				Anx1ApprovalReqDto approvalReqDto = new Anx1ApprovalReqDto();
				approvalReqDto.setImHeader(headerDto);
				approvalReqDto.setImItem(reqDto);

				respcode = destinationConn.pushToErp(approvalReqDto,
						"Anx1ApprovalReqDto", batch);
				respcode = respcode != null ? respcode : 0;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Destination {} -> Response code is {} ",
							destinationName, respcode);
				}
			} else {
				LOGGER.debug(
						"No Data found to do approval request reverse integration");
			}
			return respcode;
		} catch (

		Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			return null;
		}
	}
}
