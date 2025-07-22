package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import com.ey.advisory.app.docs.dto.erp.Ret1HeaderItemProcessReviewSummaryReqDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ProcessDataRequestItemDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ProcessReviewSummItemRequestDto;
import com.ey.advisory.app.docs.dto.erp.Ret1ReviewSummaryRequestItemDto;
import com.ey.advisory.app.docs.dto.erp.Ret1SummaryDataRequestItemDto;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Ret1ProcessReviewSummaryHandler")
public class Ret1ProcessReviewSummaryHandler {

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("Ret1ReviewSummaryRequestDocsImpl")
	private Ret1ReviewSummaryRequestDocs ret1RevSumReqDocs;

	private static Logger LOGGER = LoggerFactory
			.getLogger(Ret1ProcessReviewSummaryHandler.class);

	public Integer processReviewSummryToErp(
			RevIntegrationScenarioTriggerDto dto, String type) {
		Integer responseCode = 0;
		AnxErpBatchEntity batch = new AnxErpBatchEntity();
		try {
			String destinationName = dto.getDestinationName();
			Long scenarioId = dto.getScenarioId();
			String groupcode = dto.getGroupcode();
			String gstin = dto.getGstin();
			Long entityId = dto.getEntityId();
			String stateCode = gstin.substring(0, 2);

			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			Optional<EntityInfoEntity> entity = entityRepo.findById(entityId);
			String entityName = null;
			String entityPan = null;
			String companyCode = null;
			if (entity.isPresent()) {
				entityName = entity.get().getEntityName();
				entityPan = entity.get().getPan();
				companyCode = entity.get().getCompanyHq();
			}
			Ret1HeaderItemProcessReviewSummaryReqDto reqDto = processSummary(
					gstin, entityPan, stateName, entityName, companyCode);

			long currentBatchSize = 0;
			if (reqDto.getHeaderProcessSummDto() != null) {
				currentBatchSize = currentBatchSize
						+ reqDto.getHeaderProcessSummDto().size();
			}

			batch = setErpBatch(groupcode, entityId, gstin, destinationName,
					scenarioId, currentBatchSize, null);
			// Erp Batch Id forming
			batch = batchRepo.save(batch);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Batch is created as {} ", batch);
			}
			if (reqDto != null && destinationName != null) {
				responseCode = ret1RevSumReqDocs.pushToErp(reqDto,
						destinationName, batch);
			}
			responseCode = responseCode != null ? responseCode : 0;
			/*if (responseCode == 200) {
				batch.setSuccess(true);
				batch.setStatus(APIConstants.SUCCESS);
			} else {
				batch.setSuccess(false);
				batch.setStatus(APIConstants.FAILED);
			}
			// Erp Batch updation
			batchRepo.save(batch);*/
		} catch (Exception e) {
			LOGGER.error("Occured Exception:{} ", e);
		}
		return responseCode;
	}

	// This code represents the process data
	private Ret1HeaderItemProcessReviewSummaryReqDto processSummary(
			String gstin, String entityPan, String stateName, String entityName,
			String companyCode) {

		Ret1HeaderItemProcessReviewSummaryReqDto procRevSumReqDto = new Ret1HeaderItemProcessReviewSummaryReqDto();

		List<Ret1ProcessDataRequestItemDto> processDataReqItemDtos = new ArrayList<>();

		List<Ret1ReviewSummaryRequestItemDto> itemProcessDtos = new ArrayList<>();
		ret1RevSumReqDocs.itemProcessSummary(gstin, entityPan, stateName,
				entityName, companyCode, itemProcessDtos);
		itemProcessDtos.forEach(reqProcessDto -> {
			Ret1ProcessDataRequestItemDto processDataReqItemDto = new Ret1ProcessDataRequestItemDto();
			processDataReqItemDto.setEntityName(entityName);
			processDataReqItemDto.setEntityPan(entityPan);
			processDataReqItemDto.setCompanyCode(companyCode);
			processDataReqItemDto.setGstin(reqProcessDto.getGstinNum());
			processDataReqItemDto.setReturnPerod(reqProcessDto.getRetPer());

			// Process Summary
			Ret1ProcessReviewSummItemRequestDto processReviewSumDto = new Ret1ProcessReviewSummItemRequestDto();
			Ret1ReviewSummaryRequestItemDto processData = new Ret1ReviewSummaryRequestItemDto();
			processData.add(reqProcessDto);
			processReviewSumDto.setProcessData(processData);

			// Review Summary
			Ret1SummaryDataRequestItemDto headerReviewSummDto = new Ret1SummaryDataRequestItemDto();
			List<Ret1ReviewSummaryRequestItemDto> summReqItemDtos = new ArrayList<>();
			ret1RevSumReqDocs.getReviewSummary(reqProcessDto.getGstinNum(),
					reqProcessDto.getRetPer(), entityPan, entityName,
					companyCode, summReqItemDtos);
			headerReviewSummDto.setSummaryData(summReqItemDtos);
			processDataReqItemDto.setHeaderReviewSummDto(headerReviewSummDto);

			processDataReqItemDtos.add(processDataReqItemDto);
		});

		return procRevSumReqDto;
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
