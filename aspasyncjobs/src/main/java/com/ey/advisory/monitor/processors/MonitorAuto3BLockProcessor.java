/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.Gstr2AReconRespUploadProcEntity;
import com.ey.advisory.app.data.entities.client.asprecon.AIM3BLockStatusEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AIM3BLockStatusRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2AReconRespUploadProcRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.ConfigManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("MonitorAuto3BLockProcessor")
public class MonitorAuto3BLockProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	@Qualifier("AIM3BLockStatusRepository")
	AIM3BLockStatusRepository status3BRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository onboardingRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepo;
	
	@Autowired
	@Qualifier("Gstr2AReconRespUploadProcRepository")
	Gstr2AReconRespUploadProcRepository gstr2aProcRepo;
	
	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	private static final String AP_M_2APR = "AP_M_2APR";
	private static final String AUTO_2APR = "AUTO_2APR";

	private static List<String> type = Arrays.asList(AP_M_2APR, AUTO_2APR);

	private static List<String> statusList = Arrays.asList("RECON_INPROGRESS",
			"RECON_IN_QUEUE", "RECON_HALT", "INITIATED", "INPROGRESS");

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		Long batchId = null;
		List<Long> batchIds = new ArrayList<>();
		List<AIM3BLockStatusEntity> entityList = new ArrayList<>();
		try {

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"inside MonitorAuto3BLockProcessor "
								+ "group code {} : ",
						TenantContext.getTenantId());
			}

			List<Gstr2ReconConfigEntity> reconEntity = reconConfigRepo
					.findByTypeIn(type);

			Long count = reconEntity.stream()
					.filter(o -> statusList.contains(o.getStatus())).count();

			if (LOGGER.isDebugEnabled()) {

				LOGGER.debug(
						"inside MonitorAuto3BLockProcessor "
								+ "group code {}  and count {}: ",
						TenantContext.getTenantId(), count);
			}

			if (count != 0) {

				LOGGER.error(
						"inside MonitorAuto3BLockProcessor "
								+ "group code {}  and count {} Not eligible "
								+ "for Auto Lock hence returing: ",
						TenantContext.getTenantId(), count);
				return;
			}

			List<Long> entityIds = getAllAPEnabledEntity();

			for (Long entityId : entityIds) {

				batchId = generateCustomId(entityManager);// revisit
				AIM3BLockStatusEntity dto = new AIM3BLockStatusEntity();

				dto.setBatchId(batchId);
				// dto.setCompletedOn(LocalDateTime.now());
				dto.setCreatedOn(LocalDateTime.now());
				dto.setEntityId(entityId);
				dto.setStatus("INITIATED");
				entityList.add(dto);
				batchIds.add(batchId);
			} // 10 rows

			status3BRepo.saveAll(entityList);

			for (AIM3BLockStatusEntity entity : entityList) {

				status3BRepo.updateStatusAndResp(entity.getBatchId(),
						"INPROGRESS", null, null);
				procCall(entity.getBatchId(), entity.getEntityId());

			}

		} catch (Exception e) {

			String msg = String.format(
					"MonitorAuto3BLockProcessor"
							+ " - error occured group code  {}, ",
					TenantContext.getTenantId());

			LOGGER.error(msg, e);
			status3BRepo.updateStatus(batchIds, "FAILED");
			throw new AppException(e);

		}

	}

	private void procCall(Long batchId, Long entityId) {
		try {

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_AUTO_2APR_AIM_3BLOCK");

			storedProc.registerStoredProcedureParameter("VAR_BATCHID",
					Long.class, ParameterMode.IN);

			storedProc.registerStoredProcedureParameter("ENTITY_ID", Long.class,
					ParameterMode.IN);

			storedProc.setParameter("VAR_BATCHID", batchId);
			storedProc.setParameter("ENTITY_ID", entityId);

			String loadingProcResp = (String) storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executed Stored proc USP_AUTO_2APR_AIM_3BLOCK and "
								+ "got resp : %s",
						loadingProcResp);
				LOGGER.debug(msg);
			}

			if (!loadingProcResp.equalsIgnoreCase("SUCCESS")) {

				String msg = String
						.format("USP_AUTO_2APR_AIM_3BLOCK did not return "
								+ "success hence returing : EntityId %d ,"
								+ " batchId %d", entityId, batchId);
				LOGGER.error(msg);

				status3BRepo.updateStatusAndResp(batchId, "FAILED",
						loadingProcResp, null);
				return;
			}

			String failurePocName = "USP_RECON_RESP_PSD_FAILURE";
			try {
				Map<Integer, String> procs = fetchStoredGSTR2aProcedures();
				for (Integer indx : procs.keySet()) {
					String procName = procs.get(indx);
					try {
						String status = executeGstr2aProcedure(procName, Long.valueOf(batchId));
						if (!(ReconStatusConstants.SUCCESS.equalsIgnoreCase(status)
								|| "Success with Errors"
										.equalsIgnoreCase(status))) {
							String msg = String.format("Auto 3B Lock Batch Id is '%s', Response "
									+ "from PROC %s did not " + "return success,"
									+ " Hence updating to Failed", batchId,
									procName);
							LOGGER.error(msg);
							throw new AppException(msg);
						}
					} catch (Exception ex) {
						String msg = String.format(
								"Calling Auto 3B Lock  failure Proc '%s' for Batch Id is '%s'",
								failurePocName, batchId);
						LOGGER.debug(msg);
						executeGstr2aProcedure(failurePocName, Long.valueOf(batchId));
						
						status3BRepo.updateStatusAndResp(batchId, "FAILED", null,null);
						
						throw new AppException(ex);
					}
				}
				status3BRepo.updateStatusAndResp(batchId, "SUCCESS", null, null);
				
				LOGGER.debug("Auto 3B Lock File is Processed for fileId: {}", batchId);
				
			} catch (Exception e) {
				String msg = String
						.format("Exception Occure while fetching Proc List for Auto 3B Lock  from TBL :'%s'",failurePocName);
				LOGGER.error(msg, e);
				status3BRepo.updateStatusAndResp(batchId, "FAILED", null,null);
				throw new AppException(e);
			}

		} catch (Exception e) {

			String msg = String.format(
					"Exeption occured while Invoking "
							+ "Proc Call : EntityId %d , batchId %d",
					entityId, batchId);
			LOGGER.error(msg);

			status3BRepo.updateStatusAndResp(batchId, "FAILED", null, null);
		}
	}
	
	private String executeGstr2aProcedure(String procName, Long batchId)
			throws Exception {
		String status = null;
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Invking stor proc :%s , BatchId :%s", procName,
						Long.valueOf(batchId));
				LOGGER.debug(msg);
			}
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(procName);
			storedProc.registerStoredProcedureParameter("VAR_BATCHID",
					Long.class, ParameterMode.IN);
			storedProc.setParameter("VAR_BATCHID", batchId);
			long dbLoadStTime = System.currentTimeMillis();
			status = (String) storedProc.getSingleResult();
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to excecute the Proc :'%s'"
								+ " with staus :'%s' from DB is '%d' millisecs.",
						procName, status, dbLoadTimeDiff);
				LOGGER.debug(msg);
			}
		} catch (Exception e) {
			String msg = String.format(
					"Exception occured while calling proc :'%s' for Batch Id '%s'",
					procName, batchId);
			LOGGER.debug(msg);
			LOGGER.error(msg,e);
			if("USP_RECON_RESP_PSD_FAILURE".equalsIgnoreCase(procName))
			{
				failedBatAltUtility.prepareAndTriggerAlert(String.valueOf(batchId),
						"RECON_RESPONSE",String.format(" FAILURE PROCNAME : '%s' ",procName));
		
			}
			throw new AppException(msg);
		}
		return status;
	}

	private Map<Integer, String> fetchStoredGSTR2aProcedures() {
		Map<Integer, String> gstr2aProcMap = null;
		try {

			gstr2aProcMap = new TreeMap<>();
			List<Gstr2AReconRespUploadProcEntity> gstr2aProcList = gstr2aProcRepo
					.findAllActiveProc();

			gstr2aProcMap = gstr2aProcList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Getting alls GSTR 2APR recon response upload proc list '%s'",
						gstr2aProcMap.toString());
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			String msg = String
					.format("Exception while fetching GSTR 2APR recon "
							+ "response upload proc list from DB ");
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return gstr2aProcMap;
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT AIM_3BLOCK_SEQ.nextval FROM DUMMY";

		Query query = entityManager.createNativeQuery(queryStr);

		Long seqId = ((Long) query.getSingleResult());

		return seqId;
	}

	private static Long generateCustomId(EntityManager entityManager) {
		String reportId = "";
		String digits = "";
		Long nextSequencevalue = getNextSequencevalue(entityManager);

		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		String currentDate = currentYear
				+ (currentMonth < 10 ? ("0" + currentMonth)
						: String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}

	private List<Long> getAllAPEnabledEntity() {

		List<Long> entityIds = entityInfoRepo.findActiveEntityIds();

		List<Long> optedEntities = onboardingRepo
				.getAllEntitiesOpted2B(entityIds, "I27");

		return optedEntities;
	}

}
