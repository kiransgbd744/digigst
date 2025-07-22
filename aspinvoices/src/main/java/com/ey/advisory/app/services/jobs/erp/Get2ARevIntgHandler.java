package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr2aErpConsolidatedRepository;
import com.ey.advisory.app.docs.dto.erp.Get2AConsolidatedRevIntgDto;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntgHeaderDto;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntgItemDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

@Component("Get2ARevIntgHandler")
public class Get2ARevIntgHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get2ARevIntgHandler.class);

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;

	@Autowired
	@Qualifier("GetGstr2aErpConsolidatedRepository")
	private GetGstr2aErpConsolidatedRepository consolidatedRepository;

	@Autowired
	@Qualifier("Get2ARevIntgServiceImpl")
	private Get2ARevIntgServiceImpl get2ARevIntgService;

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
	private AnxErpBatchRepository batchRepo;

	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	public Integer get2AToErpPush(Get2AConsolidatedRevIntgDto dto) {

		Integer responseCode = 0;
		Long batchId = null;
		try {
			TenantContext.getTenantId();
			String destinationName = dto.getDestinationName();
			Long scenarioId = dto.getScenarioId();
			String groupCode = dto.getGroupcode();
			String gstin = dto.getGstin();
			// Long entityId = dto.getEntityId();
			Long erpId = dto.getErpId();
			batchId = dto.getBatchId();
			int chunkNo = 0;

			long currentBatchSize = 0;
			List<Integer> chunkIds = consolidatedRepository
					.getDistinctChunkIds(gstin, batchId);

			for (Integer chunkId : chunkIds) {

				chunkNo++;

				String chunkStatus = chunkNo + "/" + chunkIds.size();

				Get2ARevIntgHeaderDto headerDto = new Get2ARevIntgHeaderDto();
				List<Get2ARevIntgItemDto> itemDtos = get2ARevIntgService
						.get2ARevIntg(gstin, chunkId);
				headerDto.setItemDtos(itemDtos);
				if (headerDto.getItemDtos() != null
						&& !headerDto.getItemDtos().isEmpty()) {
					currentBatchSize = headerDto.getItemDtos().size();
				}

				// Get gstin based on gst name
				/*
				 * Long gstinId = gstnDetailRepo.findIdByGstin(gstin);
				 * 
				 * // Get erp id based on gstin id,scenariodId and entity Id
				 * ErpScenarioPermissionEntity permEntity = permRepository
				 * .findSceIdBasedScenIdAndGstinAndEntity(gstinId, scenarioId,
				 * entityId);
				 */

				/*
				 * ErpEventsScenarioPermissionEntity permEntity =
				 * erpEventsScenPermiRep
				 * .getEntityByScenarioIdAndErpId(scenarioId, erpId);
				 */

				// if (permEntity != null) {
				AnxErpBatchEntity batchEntity = erpBatchHandler.createErpBatch(
						groupCode, null, gstin, destinationName, scenarioId,
						currentBatchSize, null,
						ERPConstants.BACKGROUND_BASED_JOB, erpId, null,
						APIConstants.SYSTEM.toUpperCase(), "NALCO2A",
						dto.getJobId(), chunkStatus);

				batchEntity = batchRepo.save(batchEntity);
				if (currentBatchSize > 0) {
					responseCode = destinationConn.pushToErp(headerDto,
							"Get2ARevIntgHeaderDto", batchEntity);
				}
				if (responseCode == 200) {
					consolidatedRepository.updateErpBatchId(batchEntity.getId(),
							gstin);
					LocalDate now = LocalDate.now();
					consolidatedRepository.updateSentErp(now, gstin, chunkId);
				}
				// }
				responseCode = responseCode != null ? responseCode : 0;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Destination {} -> Response code is {} ",
							destinationName, responseCode);
				}
			}
		} catch (Exception e) {

			String errMsg = String.format(
					"Nalco Gstr Get2A Rev Integ Failed for batch id %s for group code %s",
					batchId, TenantContext.getTenantId());
			failedBatAltUtility.prepareAndTriggerAlert(String.valueOf(batchId),
					"Nalco Gstr Get2A Rev Integ",
					String.format("Exception is %s", e.getMessage()));
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg, e);
		}
		return responseCode;
	}
}
