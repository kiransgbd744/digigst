package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AProcReviewHeaderSummaryRequestDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AProcessAndReviewItemSummaryDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AProcessHeaderSummaryDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AProcessItemSummaryDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AProcessSummaryRespDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AReviewHeaderSummaryDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AReviewItemSummaryDto;
import com.ey.advisory.app.docs.dto.erp.gstr6.Gstr6AReviewSummaryRespDto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;

@Component("Gstr6AProcAndRevSumRevHandler")
public class Gstr6AProcAndRevSumRevHandler {

	@Autowired
	@Qualifier("Gstr6AProcAndRevSumRevServiceImpl")
	private Gstr6AProcAndRevSumRevServiceImpl gstr6AProcAndRevSumRevService;

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6AProcAndRevSumRevHandler.class);

	public Integer processReviewSummaryToERP(
			RevIntegrationScenarioTriggerDto dto) {
		Integer respCode = null;
		try {
			TenantContext.getTenantId();
			Long entityId = dto.getEntityId();
			String gstin = dto.getGstin();
			String destName = dto.getDestinationName();
			String groupCode = dto.getGroupcode();
			String entityName = null;
			String panNo = null;
			String companyCode = null;
			String stateCode = gstin.substring(0, 2);
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			Long scenarioId = dto.getScenarioId();
			EntityInfoEntity entity = entityRepo.findEntityByEntityId(entityId);
			if (entity != null) {
				entityName = entity.getEntityName();
				panNo = entity.getPan();
				companyCode = entity.getCompanyHq();
			}
			Gstr6AProcReviewHeaderSummaryRequestDto sumReqDto = getProcessReviewSummary(
					gstin, entityName, panNo, companyCode, stateName);

			long currentBatchsize = 0;
			if (sumReqDto.getGstr6ProcAndReviItemSumDtos() != null
					&& !sumReqDto.getGstr6ProcAndReviItemSumDtos().isEmpty()) {
				currentBatchsize = currentBatchsize
						+ sumReqDto.getGstr6ProcAndReviItemSumDtos().size();
			}
			AnxErpBatchEntity batch = setErpBatch(groupCode, entityId, gstin,
					destName, scenarioId, currentBatchsize, "Gstr6a");
			batch = batchRepo.save(batch);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Batch is created as {} ", batch);
			}
			respCode = gstr6AProcAndRevSumRevService.pushToErp(sumReqDto,
					destName, batch);
			respCode = respCode != null ? respCode : 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ", destName,
						respCode);
			}
			if (respCode == 200) {
				batch.setSuccess(true);
				batch.setHttpCode(respCode);
			} else {
				batch.setSuccess(false);
				batch.setHttpCode(respCode);
			}
			// Erp Batch updation
			batchRepo.save(batch);
		} catch (Exception e) {
			LOGGER.debug("Exception Occred:{}", e);
		}
		return respCode;
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

	private Gstr6AProcReviewHeaderSummaryRequestDto getProcessReviewSummary(
			String gstin, String entityName, String panNo, String companyCode,
			String stateName) throws Exception {
		Gstr6AProcReviewHeaderSummaryRequestDto revHeadrSuReqDto = new Gstr6AProcReviewHeaderSummaryRequestDto();

		List<Gstr6AProcessAndReviewItemSummaryDto> gstr6ProcAndReviItemSumDtos = new ArrayList<>();
		List<Gstr6AProcessSummaryRespDto> proceSummRespDtos = gstr6AProcAndRevSumRevService
				.convertProcessSummary(gstin);
		proceSummRespDtos.forEach(proceSummRespDto -> {
			if (proceSummRespDto != null && proceSummRespDto.getGstin() != null
					&& !proceSummRespDto.getGstin().isEmpty()
					&& proceSummRespDto.getRetPeriod() != null
					&& !proceSummRespDto.getRetPeriod().isEmpty()) {
				Gstr6AProcessAndReviewItemSummaryDto gstr6ProcAndReviItemSumDto = new Gstr6AProcessAndReviewItemSummaryDto();
				gstr6ProcAndReviItemSumDto.setEntityName(entityName);
				gstr6ProcAndReviItemSumDto.setEntityPan(panNo);
				gstr6ProcAndReviItemSumDto.setCompanyCode(companyCode);
				gstr6ProcAndReviItemSumDto.setGstinNumber(gstin);
				gstr6ProcAndReviItemSumDto
						.setReturnPeriod(proceSummRespDto.getRetPeriod());

				// Process Summary
				Gstr6AProcessHeaderSummaryDto processSummary = new Gstr6AProcessHeaderSummaryDto();
				List<Gstr6AProcessItemSummaryDto> processSumItems = convertProcessSummary(
						proceSummRespDto);
				processSummary.setItems(processSumItems);
				gstr6ProcAndReviItemSumDto.setProcessSummary(processSummary);

				// Review Summary
				Gstr6AReviewHeaderSummaryDto reviewSummary = new Gstr6AReviewHeaderSummaryDto();

				List<Gstr6AReviewItemSummaryDto> reviewSumItems = convertReviewSummary(
						gstin, proceSummRespDto.getRetPeriod());
				reviewSummary.setItems(reviewSumItems);
				gstr6ProcAndReviItemSumDto.setReviewSummary(reviewSummary);

				gstr6ProcAndReviItemSumDtos.add(gstr6ProcAndReviItemSumDto);
			}
		});
		return revHeadrSuReqDto;
	}

	private List<Gstr6AProcessItemSummaryDto> convertProcessSummary(
			Gstr6AProcessSummaryRespDto proceSummRespDto) {
		List<Gstr6AProcessItemSummaryDto> processItemSumDtos = new ArrayList<>();
		Gstr6AProcessItemSummaryDto processItemSumDto = new Gstr6AProcessItemSummaryDto();
		processItemSumDto.setTableType("Gstr6");
		processItemSumDto.setDocumentType("Gstr6");
		processItemSumDto.setTaxableValue(proceSummRespDto.getTaxableValue());
		processItemSumDto.setGetStatus(proceSummRespDto.getStatus());
		processItemSumDto.setTotalTax(proceSummRespDto.getTotalTax());
		processItemSumDto.setDocCount(proceSummRespDto.getCount());
		processItemSumDto.setIgst(proceSummRespDto.getIgst());
		processItemSumDto.setSgst(proceSummRespDto.getSgst());
		processItemSumDto.setCgst(proceSummRespDto.getCgst());
		processItemSumDto.setCess(proceSummRespDto.getCess());
		processItemSumDto.setInvoiceValue(proceSummRespDto.getInVoiceVal());
		processItemSumDtos.add(processItemSumDto);
		return processItemSumDtos;
	}

	private List<Gstr6AReviewItemSummaryDto> convertReviewSummary(String gstin,
			String retPeriod) {
		List<Gstr6AReviewItemSummaryDto> reviewSumItemDtos = new ArrayList<>();
		List<Gstr6AReviewSummaryRespDto> reviewSumRespDtos = gstr6AProcAndRevSumRevService
				.convertReviewSummary(gstin, retPeriod);
		reviewSumRespDtos.forEach(reviewSumRespDto -> {
			Gstr6AReviewItemSummaryDto reviewSumItemDto = new Gstr6AReviewItemSummaryDto();
			reviewSumItemDto.setTableType(reviewSumRespDto.getTableType());
			reviewSumItemDto
					.setDocumentType(reviewSumRespDto.getDocumentType());
			reviewSumItemDto.setDocCount(reviewSumRespDto.getDocCount());
			reviewSumItemDto
					.setTaxableValue(reviewSumRespDto.getTaxableValue());
			reviewSumItemDto.setTotalTax(reviewSumRespDto.getTotalTax());
			reviewSumItemDto
					.setInvoiceValue(reviewSumRespDto.getInvoiceValue());
			reviewSumItemDto.setIgst(reviewSumRespDto.getIgst());
			reviewSumItemDto.setSgst(reviewSumRespDto.getSgst());
			reviewSumItemDto.setCgst(reviewSumRespDto.getCgst());
			reviewSumItemDto.setCess(reviewSumRespDto.getCess());
			reviewSumItemDtos.add(reviewSumItemDto);
		});
		return reviewSumItemDtos;
	}
}
