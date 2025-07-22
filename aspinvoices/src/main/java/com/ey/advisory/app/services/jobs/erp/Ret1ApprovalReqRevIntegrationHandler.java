package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.daos.client.simplified.Ret1BasicDocSummarySectionDaoImpl;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.erp.ApprovalStatusReqDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ApprovalRequestDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ApprovalRequestHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ReviewSummaryRequestItemDto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

@Service("Ret1ApprovalReqRevIntegrationHandler")
public class Ret1ApprovalReqRevIntegrationHandler {

	@Autowired
	private DocRepository docRepo;

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("Ret1ApprovalReqRevDocsImpl")
	private Ret1ApprovalReqRevDocsImpl reqRevDocsImpl;

	Integer respCode = 0;

	@Autowired
	@Qualifier("Ret1BasicDocSummarySectionDaoImpl")
	private Ret1BasicDocSummarySectionDaoImpl ret1BasicDocSummarySectionDao;

	@Autowired
	@Qualifier("Ret1ReviewSummaryRequestDocsImpl")
	private Ret1ReviewSummaryRequestDocsImpl ret1ReviewSummaryRequestDocs;

	public Integer ret1ApprovalToErp(ApprovalStatusReqDto reqDto) {

		String destinationName = reqDto.getDestinationName();
		Long scenarioId = reqDto.getScenarioId();
		String groupCode = reqDto.getGroupcode();
		Map<Long, String> gstinIds = reqDto.getGstinIds();
		String returnPeriod = reqDto.getReturnPeriod();
		Long entityId = reqDto.getEntityId();
		TenantContext.setTenantId(groupCode);
		Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
		String entityName = null;
		String entityPan = null;
		String companyCode = null;
		if (entity.isPresent()) {
			entityName = entity.get().getEntityName();
			entityPan = entity.get().getPan();
			companyCode = entity.get().getCompanyHq();
		}
		Ret1ApprovalRequestHeaderDto headerDto = reqRevDocsImpl
				.convertDocsAsHeaderDtos(returnPeriod, gstinIds, entityName,
						companyCode);
		List<String> gstins = new ArrayList<>();
		gstinIds.forEach((ids, gstin) -> {
			gstins.add(gstin);
		});

		List<Ret1ReviewSummaryRequestItemDto> summReqItemDtos = new ArrayList<>();
		for (String gstin : gstins) {
			ret1ReviewSummaryRequestDocs.getReviewSummary(gstin, returnPeriod,
					entityPan, entityName, companyCode, summReqItemDtos);
		}

		Ret1ApprovalRequestDto reqRevDocs = new Ret1ApprovalRequestDto();
		reqRevDocs.setItemDto(summReqItemDtos);

		long currentBatchSize = reqRevDocs.getItemDto().size();

		List<AnxErpBatchEntity> batchs = setErpBatch(groupCode, entityId,
				gstins, scenarioId, destinationName, currentBatchSize);

		// Erp Batch id forming
		Iterable<AnxErpBatchEntity> bacths = batchRepo.saveAll(batchs);
		respCode = reqRevDocsImpl.pushToErp(headerDto, reqRevDocs,
				destinationName, null);

		bacths.forEach(batch -> {
			/*if (respCode == 200) {
				batch.setSuccess(true);
				batch.setStatus(APIConstants.SUCCESS);
			} else {
				batch.setSuccess(false);
				batch.setStatus(APIConstants.FAILED);
			}*/
			// Erp Batch updation
			docRepo.updateDocsWithErpWorkflowBatchId(returnPeriod,
					batch.getGstin(), batch.getId());
		});
		return respCode;
	}

	private List<AnxErpBatchEntity> setErpBatch(String groupcode, Long entityId,
			List<String> gstins, Long size, String destinationName,
			Long scenarioId) {
		List<AnxErpBatchEntity> batchs = new ArrayList<>();
		gstins.forEach(gstin -> {
			AnxErpBatchEntity batch = new AnxErpBatchEntity();
			batch.setGroupcode(groupcode);
			batch.setEntityId(entityId);
			batch.setDestinationName(destinationName);
			batch.setScenarioId(scenarioId);
			batch.setGstin(gstin);
			batch.setBatchSize(size);
			batch.setHttpStatus(APIConstants.INITIATED);
			batch.setCreatedBy(APIConstants.SYSTEM);
			batch.setCreatedOn(LocalDateTime.now());
			batchs.add(batch);
		});
		return batchs;
	}
}
