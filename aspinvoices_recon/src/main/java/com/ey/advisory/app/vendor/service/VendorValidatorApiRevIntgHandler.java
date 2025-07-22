/**
 * 
 */
package com.ey.advisory.app.vendor.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorValidatorPayloadRepository;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.PayloadDocsRevIntegrationReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Service("VendorValidatorApiRevIntgHandler")
public class VendorValidatorApiRevIntgHandler {

	@Autowired
	@Qualifier("VendorValidatorPayloadRepository")
	private VendorValidatorPayloadRepository payloadRepo;

	@Autowired
	@Qualifier("VendorValidationRevIntDataServcie")
	private VendorValidationRevIntDataServcie service;

	@Autowired
	@Qualifier("ErpScenarioMasterRepository")
	private ErpScenarioMasterRepository erpScenarioMasterRepository;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository erpEventsScenPermiRep;

	@Autowired
	private DestinationConnectivity destinationConn;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;

	@Autowired
	private ErpInfoEntityRepository erpInfoRepo;

	public Integer getDataForErpPush(PayloadDocsRevIntegrationReqDto dto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("VendorValidatorApiRevIntgHandler"
					+ ".getDataForErpPush Begin");
		}
		Integer responseCode = 0;
		try {

			String destName = null;
			Long scenarioId = null;
			Long entityId = null;
			String groupCode = dto.getGroupcode();
			String payloadId = dto.getPayloadId();
			String scenarioName = dto.getScenarioName();

			TenantContext.setTenantId(groupCode);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Tenant Id: {}", TenantContext.getTenantId());
			}

			if (scenarioName != null) {
				scenarioId = erpScenarioMasterRepository
						.findSceIdOnScenarioName(scenarioName);
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Scenario Id: {}", scenarioId);
			}
			List<ErpEventsScenarioPermissionEntity> permEntity = erpEventsScenPermiRep
					.getErpEventsScenarioPerms(scenarioId);

			VendorValidationMetaDataDto payloadMetaDataDto = service
					.xmlPayloadData(payloadId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("VendorValidatorApiRevIntgHandler"
						+ ".getDataForErpPush invoked payloadMetaDataDto");
			}

			long currentBatchSize = 0;

			if (payloadMetaDataDto != null
					&& payloadMetaDataDto.getResponseDto() != null) {
				currentBatchSize = payloadMetaDataDto.getResponseDto()
						.getItems().size();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("VendorValidatorApiRevIntgHandler"
							+ ".getDataForErpPush invoked payloadMetaDataDto "
							+ "size = {} ", currentBatchSize);
				}

			} else {
				currentBatchSize = 0;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("VendorValidatorApiRevIntgHandler"
							+ ".getDataForErpPush invoked payloadMetaDataDto"
							+ " has no data " + " = {} ", currentBatchSize);
				}
			}
			if (permEntity != null) {
				AnxErpBatchEntity batchEntity = erpBatchHandler.createErpBatch(
						groupCode, entityId, null,
						permEntity.get(0).getDestName(), scenarioId,
						currentBatchSize, "VendorValidatorApiRevIntg",
						ERPConstants.EVENT_BASED_JOB,
						permEntity.get(0).getErpId(), payloadId,
						APIConstants.SYSTEM.toUpperCase());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Batch is created as {} ", batchEntity);
				}
				responseCode = destinationConn.pushToErp(payloadMetaDataDto,
						"VendorValidationMetaDataDto", batchEntity);
			}
			responseCode = responseCode != null ? responseCode : 0;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Destination {} -> Response code is {} ", destName,
						responseCode);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			throw new AppException(e);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("VendorValidatorApiRevIntgHandler"
					+ ".getDataForErpPush End");
		}
		return responseCode;
	}

}
