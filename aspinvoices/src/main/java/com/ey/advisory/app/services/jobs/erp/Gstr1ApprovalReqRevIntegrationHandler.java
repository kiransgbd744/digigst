package com.ey.advisory.app.services.jobs.erp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ApprovalRequestDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ApprovalRequestHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ApprovalRequestItemDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ApproveRequestFinalDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ReviewSummaryRequestItemDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

@Service("Gstr1ApprovalReqRevIntegrationHandler")
public class Gstr1ApprovalReqRevIntegrationHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Anx1ErrorDocsRevIntegrationHandler.class);

	@Autowired
	private DocRepository docRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	Integer respcode = 0;

	@Autowired
	@Qualifier("Gstr1ApprovalRequestDocsImpl")
	private Gstr1ApprovalRequestDocs gstr1ApprovalRequestDocs;

	@Autowired
	@Qualifier("GstnSummaryErpSectionService")
	private GstnSummaryErpSectionService gstnSummaryErpSectionService;

	@Autowired
	@Qualifier("Gstr1ReviewSummaryRequestDocsImpl")
	private Gstr1ReviewSummaryRequestDocsImpl requestDocsImpl;

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

	public Integer gsrt1ApprovalRequestToErp(ApprovalStatusReqDto erpDto) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("approvalRequestToErp method called with reqDto {} ",
					erpDto);
		}
		try {
			String destinationName = erpDto.getDestinationName();
			Long scenarioId = erpDto.getScenarioId();
			String groupcode = erpDto.getGroupcode();
			Map<Long, String> gstinIds = erpDto.getGstinIds();
			String returnPeriod = erpDto.getReturnPeriod();
			Long entityId = erpDto.getEntityId();
			TenantContext.setTenantId(groupcode);
			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			String entityName = entity.get().getEntityName();
			String entityPan = entity.get().getPan();
			String companyCode = entity.get().getCompanyHq();

			gstinIds.forEach((id, gstin) -> {

				Gstr1ApprovalRequestDto approveRequestDto = new Gstr1ApprovalRequestDto();
				Gstr1ApprovalRequestHeaderDto headerDto = gstr1ApprovalRequestDocs
						.convertDocsAsHeaderDtos(returnPeriod, id, gstin,
								entityName, companyCode);
				List<Gstr1ApprovalRequestItemDto> reqApproItemDtos = getConvertReviewSummary(
						gstin, returnPeriod, entityName, entityPan, entityId,
						companyCode);
				approveRequestDto.setRequestItemDto(reqApproItemDtos);

				Gstr1ApproveRequestFinalDto dto = new Gstr1ApproveRequestFinalDto();
				dto.setHeaderDto(headerDto);
				dto.setApproveRequestDto(approveRequestDto);

				long currentBatchSize = approveRequestDto.getRequestItemDto()
						.size();
				// Get gstin based on gst name
				Long gstinId = gstnDetailRepo.findEntityIdByGstin(gstin);

				// Get erp id based on gstin id,scenariodId and entity Id
				ErpScenarioPermissionEntity permEntity = permRepository
						.findSceIdBasedScenIdAndGstinAndEntity(gstinId,
								scenarioId, entityId);
				AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(
						groupcode, entityId, gstin, destinationName, scenarioId,
						currentBatchSize, null, ERPConstants.EVENT_BASED_JOB,
						permEntity.getErpId(), null,
						APIConstants.SYSTEM.toUpperCase());
				try {
					respcode = destinationConn.pushToErp(dto,
							"Gstr1ApproveRequestFinalDto", batch);
				} catch (Exception e) {
					LOGGER.error("Unexpected Eror", e);
				}
				docRepo.updateDocsWithErpWorkflowBatchId(returnPeriod,
						batch.getGstin(), batch.getId());

			});
			return respcode;
		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			return null;
		}
	}

	private List<Gstr1ApprovalRequestItemDto> getConvertReviewSummary(
			String gstin, String returnPeriod, String entityName,
			String entityPan, Long entityId, String companyCode) {

		List<Gstr1ReviewSummaryRequestItemDto> requestItemDtos = requestDocsImpl
				.reviewSummary(gstin, returnPeriod, entityName, entityPan,
						entityId, companyCode);
		List<Gstr1ApprovalRequestItemDto> itemDtos = new ArrayList<>();
		requestItemDtos.forEach(revSumItemDto -> {
			Gstr1ApprovalRequestItemDto itemDto = new Gstr1ApprovalRequestItemDto();

			itemDto.setEntityName(entityName);
			itemDto.setGstinNum(gstin);
			itemDto.setRetPer(returnPeriod);
			itemDto.setEntityPan(entityPan);
			itemDto.setCompanyCode(companyCode);

			itemDto.setDataType(revSumItemDto.getDataType());
			itemDto.setTaxCate(revSumItemDto.getTaxCate());
			// Outward Review Summary
			itemDto.setDataType(revSumItemDto.getDataType());
			itemDto.setTaxCate(revSumItemDto.getTaxCate());
			itemDto.setAspCount(revSumItemDto.getAspCount());
			itemDto.setAspInval(revSumItemDto.getAspInval());
			itemDto.setAspTbval(revSumItemDto.getAspTbval());
			itemDto.setAspTxval(revSumItemDto.getAspTxval());
			itemDto.setAspIgstval(revSumItemDto.getAspIgstval());
			itemDto.setAspSgstval(revSumItemDto.getAspSgstval());
			itemDto.setAspCgstval(revSumItemDto.getAspCgstval());
			itemDto.setAspCessval(revSumItemDto.getAspCessval());

			itemDto.setGstnCount(revSumItemDto.getGstnCount());
			itemDto.setGstnInval(revSumItemDto.getGstnInval());
			itemDto.setGstnTbval(revSumItemDto.getGstnTbval());
			itemDto.setGstnTxval(revSumItemDto.getGstnTxval());
			itemDto.setGstnIgstval(revSumItemDto.getGstnIgstval());
			itemDto.setGstnSgstval(revSumItemDto.getGstnSgstval());
			itemDto.setGstnCgstval(revSumItemDto.getGstnCgstval());
			itemDto.setGstnCessval(revSumItemDto.getGstnCessval());

			itemDto.setDiffCount(revSumItemDto.getDiffCount());
			itemDto.setDiffInval(revSumItemDto.getDiffInval());
			itemDto.setDiffTbval(revSumItemDto.getDiffTbval());
			itemDto.setDiffTxval(revSumItemDto.getDiffTxval());
			itemDto.setDiffIgstval(revSumItemDto.getDiffIgstval());
			itemDto.setDiffSgstval(revSumItemDto.getDiffSgstval());
			itemDto.setDiffCgstval(revSumItemDto.getDiffCgstval());
			itemDto.setDiffCessval(revSumItemDto.getDiffCessval());

			// Nil Non Exmptention
			itemDto.setAspNilRsup(revSumItemDto.getAspNilRsup());
			itemDto.setAspExpSup(revSumItemDto.getAspExpSup());
			itemDto.setAspNonGsup(revSumItemDto.getAspNonGsup());
			itemDto.setAspCancel(revSumItemDto.getAspCancel());
			itemDto.setAspNetissue(revSumItemDto.getAspNetissue());

			itemDto.setGstNilRsup(revSumItemDto.getGstNilRsup());
			itemDto.setGstExpSup(revSumItemDto.getGstExpSup());
			itemDto.setGstNonGsup(revSumItemDto.getGstNonGsup());
			itemDto.setGstCancel(revSumItemDto.getGstCancel());
			itemDto.setGstNetissue(revSumItemDto.getGstNetissue());

			itemDto.setDiffNilRsup(revSumItemDto.getDiffNilRsup());
			itemDto.setDiffExpSup(revSumItemDto.getDiffExpSup());
			itemDto.setDiffNonGsup(revSumItemDto.getDiffNonGsup());
			itemDto.setDiffNonGsup(revSumItemDto.getDiffNonGsup());
			itemDto.setDiffNetissue(revSumItemDto.getDiffNetissue());

			itemDtos.add(itemDto);
		});
		return itemDtos;
	}
}
