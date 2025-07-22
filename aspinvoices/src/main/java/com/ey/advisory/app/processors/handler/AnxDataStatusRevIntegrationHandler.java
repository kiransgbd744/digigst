/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusReqDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestDataHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestDataSummaryHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusRequestHeaderDto;
import com.ey.advisory.app.docs.dto.erp.AnxDataStatusResultDto;
import com.ey.advisory.app.services.jobs.erp.AnxDataStatusDataFetcherForErp;
import com.ey.advisory.app.services.jobs.erp.AnxDataStatusDocsCountImpl;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.JsonParseException;

/**
 * 
 * @author Sasidhar Reddy
 *
 */
@Service("AnxDataStatusRevIntegrationHandler")
public class AnxDataStatusRevIntegrationHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AnxDataStatusRevIntegrationHandler.class);

	@Autowired
	@Qualifier("AnxDataStatusDataFetcherForErpImpl")
	private AnxDataStatusDataFetcherForErp anx1DataStatusService;

	@Autowired
	private AnxDataStatusDocsCountImpl erpAnxDataStatusService;

	@Autowired
	private DocRepository outwardDocRepo;

	@Autowired
	private AnxErpBatchRepository batchRepo;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	private Long entityId;
	private Integer respcode;

	public Integer dataStatusCountToErp(RevIntegrationScenarioTriggerDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataStatusCountToErp method called with reqDto {} ",
					dto);
		}
		String groupcode = dto.getGroupcode();
		String destinationName = dto.getDestinationName();
		Long scenarioId = dto.getScenarioId();
		entityId = dto.getEntityId();
		String gstin = dto.getGstin();
		TenantContext.setTenantId(groupcode);

		AnxDataStatusReqDto searchParams = setRequestDto("", gstin, dto);
		LOGGER.debug("scenarioId and destinationName -- >", destinationName,
				scenarioId, gstin, dto);

		// This will do the actual DataStatus screen service to fetch the counts
		try {

			Optional<EntityInfoEntity> entityInfo = entityRepo
					.findById(entityId);
			String entityName = entityInfo.get().getEntityName();
			String pan = entityInfo.get().getPan();
			String companyCode = entityInfo.get().getCompanyHq();
			LOGGER.debug("enity name {} and Pan {} ", entityName, pan,
					companyCode);
			respcode = formBatchAndPushToErp(pan, entityName, gstin,
					searchParams, groupcode, destinationName, scenarioId,
					entityId, companyCode);
			LOGGER.debug("dataStatusCountToErp method called -->  ", respcode,
					pan, entityName, gstin, searchParams, groupcode,
					destinationName, scenarioId, entityId, companyCode);

		} catch (JsonParseException ex) {
			String msg = "Error while parsing the input Json";
			LOGGER.error(msg, ex);
			ex.printStackTrace();

		} catch (Exception ex) {
			String msg = "Unexpected error while retriving Data Status ";
			LOGGER.error(msg, ex);
			ex.printStackTrace();
		}
		return null;

	}

	private Integer formBatchAndPushToErp(String entityPan, String entityName,
			String gstin, AnxDataStatusReqDto searchParams, String groupcode,
			String destinationName, Long scenarioId, Long entityId,
			String companyCode) {

		Integer resp = 0;
		AnxDataStatusRequestHeaderDto itemDto = new AnxDataStatusRequestHeaderDto();
		AnxDataStatusRequestDataHeaderDto dataHeaderDto = new AnxDataStatusRequestDataHeaderDto();
		AnxDataStatusRequestDataSummaryHeaderDto dataSummaryHeaderDto = new AnxDataStatusRequestDataSummaryHeaderDto();

		searchParams.setDataType(APIConstants.OUTWARD);
		List<Object[]> arrs = anx1DataStatusService
				.findDataStatusApiSummary(searchParams);
		List<AnxDataStatusResultDto> results = anx1DataStatusService
				.convertGstinWise(entityPan, entityName, arrs, companyCode);
		AnxDataStatusRequestDataHeaderDto dataHeader = erpAnxDataStatusService
				.convertDocsAsDtos(results, "outward");
		dataHeaderDto.getImItem().addAll(dataHeader.getImItem());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataHeaderDto  -- >> ", dataHeaderDto, results);
		}
		searchParams.setDataType(APIConstants.OUTWARD_SUMMARY);
		List<Object[]> outSumArrs = anx1DataStatusService
				.findDataStatusApiSummary(searchParams);
		AnxDataStatusRequestDataSummaryHeaderDto dataSummaryHeader = anx1DataStatusService
				.calculateDataByDocTypeAndReturnPeiod(anx1DataStatusService
						.convertDataToOutwarSummary(entityPan, entityName,
								outSumArrs, entityId, companyCode),
						entityId);
		dataSummaryHeaderDto.getImItem().addAll(dataSummaryHeader.getImItem());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataSummaryHeader  -- >> ", dataSummaryHeader);
		}
		searchParams.setDataType(APIConstants.INWARD);
		List<Object[]> inArrs = anx1DataStatusService
				.findDataStatusApiSummary(searchParams);
		List<AnxDataStatusResultDto> results1 = anx1DataStatusService
				.convertGstinWise(entityPan, entityName, inArrs, companyCode);
		AnxDataStatusRequestDataHeaderDto dataHeader1 = anx1DataStatusService
				.convertDataToInward(results1, "inward");
		dataHeaderDto.getImItem().addAll(dataHeader1.getImItem());

		searchParams.setDataType(APIConstants.INWARD_SUMMARY);
		List<Object[]> inSumArrs = anx1DataStatusService
				.findDataStatusApiSummary(searchParams);
		AnxDataStatusRequestDataSummaryHeaderDto dataSummaryHeader1 = anx1DataStatusService
				.calculateDataByDocTypeAndReturnPeiod(anx1DataStatusService
						.convertDataToInwardDataSummary(entityPan, entityName,
								inSumArrs, entityId, companyCode),
						entityId);
		dataSummaryHeaderDto.getImItem().addAll(dataSummaryHeader1.getImItem());

		itemDto.setDataHeaderDto(dataHeaderDto);
		itemDto.setDataSummaryHeaderDto(dataSummaryHeaderDto);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("dataHeaderDto and  dataSummaryHeaderDto -- >> ",
					dataHeaderDto, dataSummaryHeaderDto, results1, dataHeader1,
					dataSummaryHeader1);
		}
		long currentBatchSize = 0;
		if (itemDto.getDataHeaderDto().getImItem() != null
				&& !itemDto.getDataHeaderDto().getImItem().isEmpty()) {
			currentBatchSize = itemDto.getDataHeaderDto().getImItem().size();
		}
		if (itemDto.getDataSummaryHeaderDto().getImItem() != null
				&& !itemDto.getDataSummaryHeaderDto().getImItem().isEmpty()) {
			currentBatchSize = currentBatchSize
					+ itemDto.getDataSummaryHeaderDto().getImItem().size();
		}

		AnxErpBatchEntity batch = setErpBatch(groupcode, entityId, gstin,
				destinationName, scenarioId, currentBatchSize, "");
		// Erp Batch Id formings
		batch = batchRepo.save(batch);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Batch is created as {} ", batch);
		}
		// push to erp logic
		if ((searchParams.getDataRecvFrom() != null
				&& searchParams.getDataRecvFrom().lengthOfYear() > 0)
				&& (searchParams.getDataRecvTo() != null
						&& searchParams.getDataRecvTo().lengthOfYear() > 0)) {
			outwardDocRepo.updateDocsWithErpDataStatusBatchId(batch.getId(),
					searchParams.getDataRecvFrom(),
					searchParams.getDataRecvTo());
		}
		resp = erpAnxDataStatusService.pushToErp(itemDto, destinationName, batch);
		resp = resp != null ? resp : 0;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Destination {} -> Response code is {} ",
					destinationName, resp);
		}
		/*if (resp == 200) {
			batch.setSuccess(true);
			batch.setStatus(APIConstants.SUCCESS);
		} else {
			batch.setSuccess(false);
			batch.setStatus(APIConstants.FAILED);
		}
		// Erp Batch updation
		batchRepo.save(batch);*/

		return resp;
	}

	private AnxDataStatusReqDto setRequestDto(String dataType, String gstin,
			RevIntegrationScenarioTriggerDto dto) {

		AnxDataStatusReqDto searchParams = new AnxDataStatusReqDto();

		searchParams.setGstin(gstin);
		searchParams.setDataType(dataType);

		LocalDate recvFrom = LocalDate.now();
		LocalDate recvTo = LocalDate.now();

		searchParams.setDataRecvFrom(recvFrom);

		searchParams.setDataRecvTo(recvTo);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Rev Integration of Data Status with Request Dto {}",
					searchParams);
		}
		return searchParams;
	}

	private AnxErpBatchEntity setErpBatch(String groupcode, Long entityId,
			String gstin, String destinationName, Long scenarioId, Long size,
			String dataType) {
		AnxErpBatchEntity batch = new AnxErpBatchEntity();
		batch.setGroupcode(groupcode);
		batch.setEntityId(entityId);
		batch.setDestinationName(destinationName);
		batch.setScenarioId(scenarioId);
		batch.setGstin(gstin);
		batch.setBatchSize(size);
		batch.setDataType(dataType);
		batch.setHttpStatus(APIConstants.INITIATED);
		batch.setCreatedBy(APIConstants.SYSTEM);
		batch.setCreatedOn(LocalDateTime.now());
		batch.setDelete(false);
		LOGGER.debug("Rev Integration of data status erp batch data", batch);
		return batch;

	}

}
