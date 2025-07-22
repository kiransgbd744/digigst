package com.ey.advisory.app.services.jobs.erp;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetB2bGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetB2baGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetCdnGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetCdnaGstnRepository;
import com.ey.advisory.app.docs.dto.erp.Get2AConsoForSectionFinalDto;
import com.ey.advisory.app.docs.dto.erp.Get2ARevIntReqDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.core.api.APIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Service("Get6AConsoForSectionHandler")
public class Get6AConsoForSectionHandler {

	@Autowired
	@Qualifier("Get6AConsoForSectionServiceImpl")
	private Get6AConsoForSectionServiceImpl consoForSectionServiceImpl;

	@Autowired
	private DestinationConnectivity connectivity;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository erpEventsScenPermiRep;

	@Autowired
	@Qualifier("ErpScenarioMasterRepository")
	private ErpScenarioMasterRepository erpScenarioMasterRepository;

	@Autowired
	private AnxErpBatchHandler erpBatchHandler;
	
	@Autowired
	@Qualifier("Gstr6aGetB2bGstnRepository")
	private Gstr6aGetB2bGstnRepository b2bRepository;

	@Autowired
	@Qualifier("Gstr6aGetB2baGstnRepository")
	private Gstr6aGetB2baGstnRepository b2baRepository;

	@Autowired
	@Qualifier("Gstr6aGetCdnGstnRepository")
	private Gstr6aGetCdnGstnRepository cdnRepository;

	@Autowired
	@Qualifier("Gstr6aGetCdnaGstnRepository")
	private Gstr6aGetCdnaGstnRepository cdnaRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	private AnxErpBatchRepository batchRepo;

	private static final String B2B = "B2B";
	private static final String B2BA = "B2BA";
	private static final String CDN = "CDN";
	private static final String CDNA = "CDNA";

	/**
	 * Whenever calling procedure we are getting gstin,Return Period,
	 * section,batchId and erpId
	 * 
	 * @param dto
	 * @return
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Get6AConsoForSectionHandler.class);

	public Integer erpToGet6AConsoForSection(Get2ARevIntReqDto dto) {
		Integer respCode = 0;
		try {
			String groupCode = dto.getGroupCode();
			String scenarioName = dto.getScenarioName();
			String gstin = dto.getGstin();
			String retPeriod = dto.getRetPeriod();
			String section = dto.getSection();
			Long batchId = dto.getBatchId();
			Long erpId = dto.getErpId() != null ? dto.getErpId() : 1;

			/*
			 * Getting Scenario Id based scenario name
			 * 
			 */
			Long scenarioId = erpScenarioMasterRepository
					.findSceIdOnScenarioName(scenarioName);

			/**
			 * Getting destination name for based on Scenario Id and ERP Id
			 * 
			 */
			ErpEventsScenarioPermissionEntity permEntity = erpEventsScenPermiRep
					.getEntityByScenarioIdAndErpId(scenarioId, erpId);

			/*
			 * Procedure call for chunking the ERP Batch Id into header tables
			 * because chunking will be applied to result set with max volume
			 * 
			 */

			stampChunkId(groupCode, batchId, section);

			/**
			 * List of distinct ERP Batch IDs from header table based on
			 * GSTIN,Return Period, Section and Batch Id
			 */
			
			List<Long> erpBatchIds = consoForSectionServiceImpl
					.findChunkIds(gstin, retPeriod, section, batchId);
			/**
			 * Each ERP batch id/chunking id we need push the data to ERP.
			 */
			for (Long erpBatchId : erpBatchIds) {

				LOGGER.debug("Batch ID {} ", erpBatchId);
				LOGGER.debug("gstin {} ", gstin);
				LOGGER.debug("returnPeriod {} ", retPeriod);
				/**
				 * List of information for each section based GSTIN,Return
				 * Period, section
				 */
				Get2AConsoForSectionFinalDto finalDto = consoForSectionServiceImpl
						.findERPGet6AConsoForSection(gstin, retPeriod, section,
								batchId, erpBatchId);

				LOGGER.debug("finalDto {} ", finalDto);
				
				long currentBatchSize = 0;
				if (finalDto.getGet2AConsSecDtos() != null
						&& !finalDto.getGet2AConsSecDtos().isEmpty()) {
					currentBatchSize = finalDto.getGet2AConsSecDtos().size();
					AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(
							groupCode, null, gstin, permEntity.getDestName(),
							scenarioId, currentBatchSize, null,
							ERPConstants.EVENT_BASED_JOB, permEntity.getErpId(),
							null, APIConstants.SYSTEM.toUpperCase());

					batch.setId(erpBatchId);

					// http Status code assigningn 0 values
					batch.setHttpCode(0);

					// Setting batch
					batch.setSection(section.toUpperCase());

					// updating the batch table
					batch = batchRepo.save(batch);

					respCode = connectivity.pushToErp(finalDto,
							"Get2AConsoForSectionFinalDto", batch);

					LOGGER.debug("RespCode {} " , respCode);
					// update the Is Sent ERP Date column as a current date and
					// Is Sent ERP Flag as a true into header table for section.
					LocalDate date = LocalDate.now();
					updateSentERP(respCode, gstin, retPeriod, section, batchId,
							permEntity, erpBatchId, date);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured:", e);
			throw new AppException("Exception Occured:", e);
		}
		return respCode;
	}

	private void updateSentERP(Integer respCode, String gstin, String retPeriod,
			String section, Long batchId,
			ErpEventsScenarioPermissionEntity permEntity, Long erpBatchId,
			LocalDate date) {
		if (respCode == 200 && B2B.equalsIgnoreCase(section)) {
			b2bRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}

		if (respCode == 200 && B2BA.equalsIgnoreCase(section)) {
			b2baRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}

		if (respCode == 200 && CDN.equalsIgnoreCase(section)) {
			cdnRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}

		if (respCode == 200 && CDNA.equalsIgnoreCase(section)) {
			cdnaRepository.updateSentErp(gstin, batchId, retPeriod, date,
					erpBatchId);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Destination: {}", permEntity.getDestName());
			LOGGER.debug("Response Code:{}", respCode);
		}
	}

	private void stampChunkId(final String groupCode, final Long getBatchId,
			String section) {

		String procedureName = null;
		if (B2B.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR6A_B2B";
		} else if (B2BA.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR6A_B2BA";
		} else if (CDN.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR6A_CDN";
		} else if (CDNA.equalsIgnoreCase(section)) {
			procedureName = "USP_CHUNK_GETGSTR6A_CDNA";
		}

		StoredProcedureQuery storedProcQuery = entityManager
				.createStoredProcedureQuery(procedureName);
		storedProcQuery.registerStoredProcedureParameter("IP_GROUP_CODE",
				String.class, ParameterMode.IN);
		storedProcQuery.registerStoredProcedureParameter("IP_GET_BATCH_ID",
				Long.class, ParameterMode.IN);
		storedProcQuery.registerStoredProcedureParameter("IP_CHUNK_SPILIT_VAL",
				Integer.class, ParameterMode.IN);

		storedProcQuery.setParameter("IP_GROUP_CODE", groupCode);
		storedProcQuery.setParameter("IP_GET_BATCH_ID", getBatchId);
		storedProcQuery.setParameter("IP_CHUNK_SPILIT_VAL", 2000);
		storedProcQuery.execute();

	}
}
