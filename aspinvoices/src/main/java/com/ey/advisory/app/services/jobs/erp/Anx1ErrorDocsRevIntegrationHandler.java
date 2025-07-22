/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AnxErpBatchEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.entities.client.ErpScenarioMasterEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.entities.client.GroupInfoEntity;
import com.ey.advisory.admin.data.repositories.client.AnxErpBatchRepository;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GroupInfoDetailsRepository;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.erp.Anx1ErrorDocsRevIntegrationReqDto;
import com.ey.advisory.app.docs.dto.erp.OutwardErrorDocsDto;
import com.ey.advisory.common.AnxErpBatchHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DestinationConnectivity;
import com.ey.advisory.common.ERPConstants;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx1ErrorDocsRevIntegrationHandler")
@Slf4j
public class Anx1ErrorDocsRevIntegrationHandler {

	@Autowired
	@Qualifier("Anx1AspErrorDocsImpl")
	private Anx1AspErrorDocs anx1ErrorDocs;

	@Autowired
	private DocRepository outDocRepo;
	
	@Autowired
	private InwardTransDocRepository inDocRepo;
	
	@Autowired
	private AnxErpBatchRepository batchRepo;
	
	@Autowired
	private DestinationConnectivity destinationConn;
	
	@Autowired
	@Qualifier("groupInfoDetailsRepository")
	private GroupInfoDetailsRepository groupRepo;
	
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	private EntityInfoDetailsRepository entityRepo;
	
	@Autowired
	private GSTNDetailRepository gstinRepo;
	
	@Autowired
	@Qualifier("AnxErpScenarioControlHelperImpl")
	private AnxErpScenarioControlHelper erpScenarioControlHelper;
	
	@Autowired
	private ErpScenarioMasterRepository scenMasterRepo;
	
	@Autowired
	private ErpEventsScenarioPermissionRepository eventsScenPermissionRepo;
	
	@Autowired
	private AnxErpBatchHandler erpBatchHandler;


	public Integer erpErrorDocsToErp(Anx1ErrorDocsRevIntegrationReqDto dto) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH,
				PerfamanceEventConstants.ERP_DOCS_TO_ERP_START,
				PerfamanceEventConstants.Anx1ErrorDocsRevIntegrationHandler,
				PerfamanceEventConstants.erpErrorDocsToErp, null);

		int respcode = 0;
		Long entityId = 0l;
		Long batchId = 0l;
		String accountNo = null;
		String entityName = null;
		String entityPan = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ErrorDocsToErp method called with reqDto {} ", dto);
		}
		try {
			String scenarioName = dto.getScenarioName();
			// String destinationName = dto.getDestinationName();
			String groupcode = dto.getGroupcode();
			String gstin = dto.getGstin();
			Long erpId = dto.getErpId();
			TenantContext.setTenantId(groupcode);

			/*
			 * if (dto.getScenarioId() != null) { scenarioId =
			 * dto.getScenarioId(); }
			 */
			if (dto.getBatchId() != null) {
				batchId = dto.getBatchId();
			}

			PerfUtil.logEventToFile(
					PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH,
					PerfamanceEventConstants.ERP_DOCS_DB_FETCH_START,
					PerfamanceEventConstants.Anx1ErrorDocsRevIntegrationHandler,
					PerfamanceEventConstants.erpErrorDocsToErp, null);

			ErpEventsScenarioPermissionEntity scenPermission = null;
			GSTNDetailEntity gstinInfo = gstinRepo
					.findByGstinAndIsDeleteFalse(gstin);
			if (gstinInfo != null && gstinInfo.getEntityId() != null) {
				Optional<EntityInfoEntity> entityInfo = entityRepo
						.findById(gstinInfo.getEntityId());
				if (entityInfo.isPresent()) {
					if (entityInfo.get().getId() != null) {
						entityId = entityInfo.get().getId();
					}
					entityName = entityInfo.get().getEntityName();
					entityPan = entityInfo.get().getPan();
					// companyCode = entityInfo.get().getCompanyHq();
					Optional<GroupInfoEntity> groupInfo = groupRepo
							.findById(entityInfo.get().getGroupId());
					if (groupInfo.isPresent()) {
						accountNo = groupInfo.get().getAccountno();
					}
				}

				Optional<ErpScenarioMasterEntity> optionalScenMaster = scenMasterRepo
						.findByScenarioNameAndIsDeleteFalse(scenarioName);

				if (optionalScenMaster.isPresent()) {
					ErpScenarioMasterEntity ScenarioMaster = optionalScenMaster
							.get();

					if (ScenarioMaster != null) {
						scenPermission = eventsScenPermissionRepo
								.findByScenarioIdAndIsDeleteFalse(
										ScenarioMaster.getId());
					}
				}

			}

			if (scenPermission == null) {

				LOGGER.error(String.format(
						"No valid scenario found in EVENTS_SCENARIO_PERMISSION table for the erpId %d and scnerio Name %s ",
						erpId, scenarioName));

				return null;
			}

			String destinationName = scenPermission.getDestName();
			Long scenarioId = scenPermission.getScenarioId();
			//Long erpId = scenPermission.getErpId();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Rev Integ Destination {} call started.",
						destinationName);
			}
			String dataType = APIConstants.EMPTY;
			List<Object[]> objs = null;
			// fetching all the outward/inward error docs
			if (APIConstants.OUTWARD_ASP_ERP_PUSH
					.equalsIgnoreCase(scenarioName)) {
				dataType = APIConstants.ASP_OUTWARD;
				Pair<Long, Long> range = erpScenarioControlHelper.getRange(
						gstin, scenarioId, new OutwardTransDocument());
				if (range != null) {
					objs = outDocRepo.aspErrorDocsForRevIntegrationByGstin(
							gstin, range.getValue0(), range.getValue1());
				}
			} else if (APIConstants.INWARD_ASP_ERP_PUSH
					.equalsIgnoreCase(scenarioName)) {
				dataType = APIConstants.ASP_INWARD;
				Pair<Long, Long> range = erpScenarioControlHelper
						.getRange(gstin, scenarioId, new InwardTransDocument());
				if (range != null) {
					objs = inDocRepo.aspErrorDocsForRevIntegrationByGstin(gstin,
							range.getValue0(), range.getValue1());
				}
			} else if (APIConstants.OUTWARD_GSTN_ERP_PUSH
					.equalsIgnoreCase(scenarioName)) {
				dataType = APIConstants.GSTN_OUTWARD;
				batchId = erpScenarioControlHelper.getBatchId(gstin, scenarioId,
						batchId, new OutwardTransDocument());
				if (batchId != null) {
					objs = outDocRepo.gstnErrorDocsForRevIntegrationByGstin(
							gstin, batchId);
				}
			} else if (APIConstants.INWARD_GSTN_ERP_PUSH
					.equalsIgnoreCase(scenarioName)) {
				dataType = APIConstants.GSTN_INWARD;
				batchId = erpScenarioControlHelper.getBatchId(gstin, scenarioId,
						batchId, new InwardTransDocument());
				if (batchId != null) {
					objs = inDocRepo.gstnErrorDocsForRevIntegrationByGstin(
							gstin, batchId);
				}
			}

			PerfUtil.logEventToFile(
					PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH,
					PerfamanceEventConstants.ERP_DOCS_DB_FETCH_END,
					PerfamanceEventConstants.Anx1ErrorDocsRevIntegrationHandler,
					PerfamanceEventConstants.erpErrorDocsToErp, null);

			objs = objs == null ? new ArrayList<>() : objs;

			if (objs.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"No Docs found to do {} Error docs Rev integration.",
							destinationName);
				}
			}

			if (!objs.isEmpty()) {

				PerfUtil.logEventToFile(
						PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH,
						PerfamanceEventConstants.ERP_DOCS_JSON_FORMATION_START,
						PerfamanceEventConstants.Anx1ErrorDocsRevIntegrationHandler,
						PerfamanceEventConstants.erpErrorDocsToErp, null);

				List<Pair<OutwardErrorDocsDto, List<Long>>> pairs = anx1ErrorDocs
						.convertDocsAsDtosByChunking(objs, accountNo,
								/* companyCode, */ entityName, entityPan);

				for (Pair<OutwardErrorDocsDto, List<Long>> pair : pairs) {

					if(pair == null) {
						LOGGER.error("null pair of DTOs and list");
						continue;
					}
					List<Long> value1 = pair.getValue1();
					if(value1 == null) {
						continue;
					}
					long currentBatchSize = value1.size();
					AnxErpBatchEntity batch = erpBatchHandler.createErpBatch(
							groupcode, entityId, gstin, destinationName,
							scenarioId, currentBatchSize, dataType,
							ERPConstants.EVENT_BASED_JOB, erpId, null,
							APIConstants.SYSTEM.toUpperCase());

					if (APIConstants.OUTWARD_ASP_ERP_PUSH
							.equalsIgnoreCase(scenarioName)
							|| APIConstants.OUTWARD_GSTN_ERP_PUSH
									.equalsIgnoreCase(scenarioName)) {

						batch.setMinIdOut(value1.get(0));
						batch.setMaxIdOut(value1.get(value1.size() - 1));

					} else if (APIConstants.INWARD_ASP_ERP_PUSH
							.equalsIgnoreCase(scenarioName)
							|| APIConstants.INWARD_GSTN_ERP_PUSH
									.equalsIgnoreCase(scenarioName)) {

						batch.setMinIdIn(value1.get(0));
						batch.setMaxIdIn(value1.get(value1.size() - 1));

					}

					// Erp Batch Id forming
					batch = batchRepo.save(batch);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("{} Batch is created as {} ",
								destinationName, batch);
					}

					PerfUtil.logEventToFile(
							PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH,
							PerfamanceEventConstants.ERP_DOCS_JSON_FORMATION_END,
							PerfamanceEventConstants.Anx1ErrorDocsRevIntegrationHandler,
							PerfamanceEventConstants.erpErrorDocsToErp, null);

					/*respcode = destinationConn.pushToErp(pair.getValue0(),
							"OutwardErrorDocsDto", destinationName, batch,
							ERPConstants.INVOICE_STATUS_HEADER,
							ERPConstants.INVOICE_STATUS_FOOTER);*/
					respcode = destinationConn.pushToErp(pair.getValue0(),
							"OutwardErrorDocsDto",  batch);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Destination {} -> Response code is {} ",
								destinationName, respcode);
					}

				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"No Data found to do error docs reverse integration");
				}
			}
			PerfUtil.logEventToFile(
					PerfamanceEventConstants.ERROR_DOCS_ERP_PUSH,
					PerfamanceEventConstants.ERP_DOCS_TO_ERP_END,
					PerfamanceEventConstants.Anx1ErrorDocsRevIntegrationHandler,
					PerfamanceEventConstants.erpErrorDocsToErp, null);

			return respcode;
		} catch (Exception ex) {
			LOGGER.error("Unexpected Eror", ex);
			throw new AppException(ex.getMessage(), ex);
		}

	}

	

}
