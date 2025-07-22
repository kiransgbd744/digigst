/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.AutoReconStatusEntity;
import com.ey.advisory.admin.data.entities.client.ConfigQuestionEntity;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.ConfigQuestionRepository;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconAddlReportsEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconGstinEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Service("MonitorGstr6aAutoReconcileProcessor")
public class MonitorGstr6aAutoReconcileProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	AutoReconStatusRepository autoReconStatusRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	GetAnx1BatchRepository getAnx1BatchRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository reconGstinRepo;

	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepo;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@Autowired
	@Qualifier("ConfigQuestionRepository")
	ConfigQuestionRepository configQuestionRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final ImmutableList<String> SECTIONS = ImmutableList
			.of("B2B", "B2BA", "CDN", "CDNA");

	private static final ImmutableList<String> ERP_STATUS = ImmutableList.of(
			"PAN_RECON_JOB_POSTED", "PAN_RECON_FAILED", "PAN_RECON_COMPLETED",
			"ERP_JOB_POSTED", "REPORT_GENERATED", "REPORT_GENERATION_FAILED");

	private static final ImmutableList<String> STATUS = ImmutableList
			.of("SUCCESS", "SUCCESS_WITH_NO_DATA", "FAILED", "INPROGRESS");

	private static final ImmutableList<String> RECON_PROGRESS_STATUS = ImmutableList
			.of("RECON_SUBMITTED", "INITIATED");

	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("MMyyyy");

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"About to monitor GSTR6A reconcile for group - '%s' ",
						group.getGroupCode());
				LOGGER.debug(logMsg);
			}
			List<Long> entityIds = entityInfoRepo.findActiveEntityIds();
			if (entityIds.isEmpty()) {
				LOGGER.warn(
						"Currently there are No active entities onboarded,"
								+ " Hence Not monitoring Auto Recon group {}",
						group.getGroupCode());
				return;
			}
			createPreReconConfigInserts(entityIds);

			AutoReconStatusEntity isReconInProgress = autoReconStatusRepo
					.findByReconStatusInAndDate(RECON_PROGRESS_STATUS,
							EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
									.toLocalDate());

			if (isReconInProgress != null) {
				String msg = String.format(
						"Recon in progress for gstin : '%s', Hence skipping",
						isReconInProgress.getGstin());
				LOGGER.error(msg);
				return;
			}

			List<AutoReconStatusEntity> entities = autoReconStatusRepo
					.findByGet6aStatusAndReconStatusIsNullAndDate("INITIATED",
							EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
									.toLocalDate());

			if (entities.isEmpty()) {
				LOGGER.warn(
						"Either no record found or no entity is opted for Auto "
								+ "Recon for : group {}",
						group.getGroupCode());
				return;
			}
			AutoReconStatusEntity entity = validateEntity(entities);

			if (entity != null)
				createReconJob(entity);

		} catch (Exception e) {
			LOGGER.error("Exception occured while Auto reconcile", e);
			throw new AppException(e);
		}

	}

	private void createReconJob(AutoReconStatusEntity entity) {

		String groupCode = TenantContext.getTenantId();
		String gstin = entity.getGstin();

		// checking for incremental data before submitting recon.
		if (getIncrementalDataSummary(Arrays.asList(gstin))) {

			Gstr2ReconConfigEntity obj = reconConfigRepo
					.findByEntityIdAndAutoReconDate(entity.getEntityId(),
							EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
									.toLocalDate());

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("gstin", entity.getGstin());
			jobParams.addProperty("entityId", entity.getEntityId());
			jobParams.addProperty("id", entity.getId());
			jobParams.addProperty("configId", obj.getConfigId());
			jobParams.addProperty("returnType", "6A");

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2A_AUTO_RECON, jobParams.toString(),
					"SYSTEM", 50L, null, null);

			autoReconStatusRepo.updateGet6aAndReconStatus("COMPLETED",
					LocalDateTime.now(), "RECON_SUBMITTED", entity.getId());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Updated Auto recon table for Id - '%s' ",
						entity.getId());
				LOGGER.debug(msg);
			}

		} else {
			autoReconStatusRepo.updateGet6aAndReconStatus("COMPLETED",
					LocalDateTime.now(), "NO_DELTA_DATA", entity.getId());

			reconGstinRepo.updateStatus(entity.getGstin(), entity.getDate(),
					"NO_DELTA_DATA");

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"updating status in updateGet6aAndReconStatus "
								+ "NO_DELTA_DATA as there is no incremental "
								+ "data found for  groupcode :{} are gstin {}",
						TenantContext.getTenantId(), entity.getGstin());
			}
		}

	}

	private void createPreReconConfigInserts(List<Long> entityIds) {

		String groupCode = TenantContext.getTenantId();

		List<AutoReconStatusEntity> entities = autoReconStatusRepo
				.findByGet6aStatusAndReconStatusIsNullAndDate("INITIATED", EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
                        .toLocalDate());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"entities eligible for autorecon for groupcode :{} are {}",
					groupCode, entities);
		}

		ConfigQuestionEntity confQuestionEntity = configQuestionRepo
				.findByQuestionCodeAndQuestionType("I35", "R"); // check
		List<Long> optedEntities = entityConfigPemtRepo.getAllEntitiesOpted2A(
				entityIds, "I35", confQuestionEntity.getId());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Entities opted for Auto GSTR6A for groupcode :{} ,are {}",
					groupCode, optedEntities);
		}

		Set<String> gstins = entities.stream()
				.map(AutoReconStatusEntity::getGstin)
				.collect(Collectors.toSet());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"gstins eligible for autorecon for groupcode :{} are {}",
					groupCode, gstins);
		}

		for (Long entityId : optedEntities) {

			boolean isActiveGstins = false;

			List<String> optedGstins = gSTNDetailRepo
					.findgstinByEntityIdWithISD(Arrays.asList(entityId));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstins opted for GSTR6A are {}", optedGstins);
			}
			for (String gstin : optedGstins) {
				if (gstins.contains(gstin)) {
					isActiveGstins = true;
					break;
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Activegstins eligible for autorecon 6A for groupcode :{} are {}",
						groupCode, isActiveGstins);
			}

			Gstr2ReconConfigEntity entity = reconConfigRepo
					.findByEntityIdAndAutoReconDate(entityId,
							EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
									.toLocalDate());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"entry in config table for autorecon 6A for groupcode :{} are {}",
						groupCode, entity);
			}

			if (entity == null && isActiveGstins) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"About to insert into config table for autorecon 6A for groupcode :{}",
							groupCode);
				}
				Long configId = persistReconReportConfigDetails(entityId);

				persistReconReportGstinDetails(configId, optedGstins, entityId,
						gstins);
			} else if (entity != null
					&& (!(ERP_STATUS.contains(entity.getStatus())))) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside erpReport for autorecon for groupcode :{}",
							groupCode);
				}

				List<String> reconStatus = reconGstinRepo
						.findByConfigId(entity.getConfigId());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Reconstatus for erpReport for autorecon for groupcode :{} are {}",
							groupCode, reconStatus);
				}

				if ((reconStatus.contains("RECON_COMPLETED")
						|| reconStatus.contains("NO_DELTA_DATA"))
						&& !(reconStatus.contains("GET_INITIATED")
								|| reconStatus.contains("RECON_INITIATED"))) {

					JsonObject jobParams = new JsonObject();
					jobParams.addProperty("configId", entity.getConfigId());
					jobParams.addProperty("entityId", entityId);
					/*
					 * asyncJobsService.createJob(groupCode,
					 * JobConstants.Gstr2A_AUTO_RECON_ERP_REPORT,
					 * erpReportParams.toString(), "SYSTEM", 50L, null, null);
					 * 
					 * Gstr2ReconAddlReportsEntity setEntity = setEntity(
					 * entity.getConfigId(), "ERP_Report");
					 * 
					 * addlReportRepo.save(setEntity);
					 * 
					 * reconConfigRepo.updateReconConfigStatus("ERP_JOB_POSTED",
					 * entity.getConfigId());
					 */
					jobParams.addProperty("returnType", "6A");
					asyncJobsService.createJob(groupCode,
							JobConstants.Gstr2_Auto_Recon_Pan_Level,
							jobParams.toString(), "SYSTEM", 50L, null, null);

				}
			}
		}

	}

	private AutoReconStatusEntity validateEntity(
			List<AutoReconStatusEntity> entities) {

		AutoReconStatusEntity eligibleEntity = null;

		try {

			for (AutoReconStatusEntity entity : entities) {
				// TODO diferent in gstr2a
				List<String> prevTaxPeriodList = getEligibleTaxPeriods();

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Previous Taxperiods : '%s'",
							prevTaxPeriodList);
					LOGGER.debug(msg);
				}

				List<String> getAllStatus = getAnx1BatchRepo.findBatchForRecon(
						entity.getGstin(), prevTaxPeriodList, "GSTR6A",
						SECTIONS);

				if (getAllStatus.contains("FAILED")) {

					entity.setGet6aStatus("FAILED");
					entity.setGet6aCompletedOn(LocalDateTime.now());
					entity.setReconStatus(null);
					autoReconStatusRepo.save(entity);

					reconGstinRepo.updateStatus(entity.getGstin(),
							entity.getDate(), "GET_FAILED");

					continue;
				}

				if (getAllStatus.contains("INPROGRESS")
						|| getAllStatus.contains("INITIATED")) {
					continue;
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"No of Success or Success with no records for gstin : "
									+ "'%s' - '%s' ",
							entity.getGstin(), getAllStatus.size());
					LOGGER.debug(msg);
				}
			
				eligibleEntity = entity;
				break;

			}

		} catch (Exception e) {
			LOGGER.error("Exception occured while monitoring GSTR6A Auto Recon",
					e);
		}

		return eligibleEntity;

	}

	private Long persistReconReportConfigDetails(Long entityId) {
		try {

			Optional<EntityInfoEntity> entityInfo = entityInfoRepo
					.findById(entityId);

			Long configId = generateCustomId(entityManager);
			Gstr2ReconConfigEntity entity = new Gstr2ReconConfigEntity();
			entity.setEntityId(entityId);
			entity.setConfigId(configId);
			entity.setType("AUTO_2APR");
			entity.setStatus("RECON_SUBMITTED");
			entity.setCreatedDate(LocalDateTime.now());
			entity.setCreatedBy("SYSTEM");
			entityInfo.ifPresent(info->entity.setPan(info.getPan()));
			entity.setAutoReconDate(EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now()).toLocalDate());
			reconConfigRepo.save(entity);

			return configId;

		} catch (Exception ee) {
			String msg = String.format(
					"Exception occured while persisting"
							+ " recon report Config Entity for 6A " + "  :%s",
					LocalDate.now());
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}

	}

	private void persistReconReportGstinDetails(Long confId,
			List<String> gstinList, Long entityId,
			Set<String> autoReconGstins) {

		for (String gstin : gstinList) {

			try {
				Gstr2ReconGstinEntity reconGstinEntiy = new Gstr2ReconGstinEntity();
				reconGstinEntiy.setConfigId(confId);
				reconGstinEntiy.setGstin(gstin);
				reconGstinEntiy.setActive(true);
				reconGstinEntiy.setAutoReconDate(
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
								.toLocalDate());
				/*
				 * reconGstinEntiy.setFromDate(
				 * fetch2aAutoReconCompletedDate(entityId, gstin, confId));
				 */ reconGstinEntiy.setFromDate(null);
				reconGstinEntiy.setToDate(LocalDateTime.now());
				// TODO doubt
				String reconStatus = autoReconGstins.contains(gstin)
						? "GET_INITIATED" : "GET_NOT_INITIATED";
				reconGstinEntiy.setStatus(reconStatus);
				reconGstinEntiy.setCretaedOn(LocalDateTime.now());
				reconGstinRepo.save(reconGstinEntiy);

			} catch (Exception ee) {
				String msg = String.format("Exception occured while persisting"
						+ " recon report gstin details for configId :%s "
						+ " and gstin :%s", confId, gstin);
				LOGGER.error(msg, ee);
				throw new AppException(msg);
			}
		}
	}

	private static Long getNextSequencevalue(EntityManager entityManager) {

		String queryStr = "SELECT RECON_REPORT_SEQ.nextval " + "FROM DUMMY";

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
		String currentDate = currentYear + (currentMonth < 10
				? ("0" + currentMonth) : String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}

	private LocalDateTime fetch2aAutoReconCompletedDate(Long entityId,
			String gstin, Long configId) {
		try {
			LocalDateTime fromDate = LocalDateTime.now();

			String query = "SELECT IFNULL(MAX(GD.COMPLETED_ON),'2021-04-01') AS FROM_DATE "
					+ " FROM TBL_RECON_REPORT_GSTIN_DETAILS GD INNER JOIN TBL_RECON_REPORT_CONFIG C "
					+ " ON C.RECON_REPORT_CONFIG_ID=GD.RECON_REPORT_CONFIG_ID "
					+ " AND C.RECON_TYPE IN ('AUTO_2APR','AP_M_2APR') AND GD.STATUS='RECON_COMPLETED' "
					+ " WHERE GD.GSTIN = :gstin";

			Query q = entityManager.createNativeQuery(query);
			q.setParameter("gstin", gstin);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the recon complation date"
						+ " from AUTO_2A_2B_RECON_STATUS table for given entityID"
						+ +entityId + ", gstin " + gstin);
			}
			@SuppressWarnings("unchecked")
			Object obj = q.getSingleResult();

			if (obj == null)
				return LocalDateTime.of(2021, 04, 01, 0, 0, 0); // 01-Apr-2021
			fromDate = ((Timestamp) obj).toLocalDateTime();
			return fromDate;

		} catch (Exception ee) {
			String msg = "Exception occured while fetching auto Recon completion status";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private Gstr2ReconAddlReportsEntity setEntity(Long configId,
			String reportName) {

		Gstr2ReconAddlReportsEntity entity = new Gstr2ReconAddlReportsEntity();
		entity.setConfigId(configId);
		entity.setIsDownloadable(false);
		entity.setReportType(reportName);
		entity.setReportTypeId(23);

		return entity;
	}

	private List<String> getEligibleTaxPeriods() {

		/**
		 * If Fin Year GET is chosen by the client then the difference in the
		 * months calculation is written below based the functional requirement
		 * documentation as for Oct, Nov and Dec months Get call to initiate
		 * from current finyear and for rest of the months Get call to initiate
		 * from Previous finyear
		 */
		int numOfTaxPeriods = 0;
		int monthValue = LocalDate.now().getMonthValue();
		if (monthValue == 1) {
			numOfTaxPeriods = 10;
		} else if (monthValue == 2) {
			numOfTaxPeriods = 11;
		} else if (monthValue == 3) {
			numOfTaxPeriods = 12;
		} else if (monthValue == 4) {
			numOfTaxPeriods = 13;
		} else if (monthValue == 5) {
			numOfTaxPeriods = 14;
		} else if (monthValue == 6) {
			numOfTaxPeriods = 15;
		} else if (monthValue == 7) {
			numOfTaxPeriods = 16;
		} else if (monthValue == 8) {
			numOfTaxPeriods = 17;
		} else if (monthValue == 9) {
			numOfTaxPeriods = 18;
		} else if (monthValue == 10) {
			numOfTaxPeriods = 19;
		} else if (monthValue == 11) {
			numOfTaxPeriods = 20;
		} else if (monthValue == 12) {
			numOfTaxPeriods = 9;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");

		List<String> taxPeriods = new ArrayList<>();
		for (numOfTaxPeriods = numOfTaxPeriods
				- 1; numOfTaxPeriods >= 0; numOfTaxPeriods--) {

			LocalDate minusMonths = LocalDate.now()
					.minusMonths(numOfTaxPeriods);
			String taxPeriod = minusMonths.format(formatter);
			taxPeriods.add(taxPeriod);

		}
		return taxPeriods;

	}

	// method to check for incremental data
	private boolean getIncrementalDataSummary(List<String> recipientGstins) {
		String rGstins = "";
		try {
			if (recipientGstins != null && !recipientGstins.isEmpty()) {
				rGstins = String.join(",", recipientGstins);
			} else {
				rGstins = "";
			}

			StoredProcedureQuery storedProc = null;

			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Inside Gstr2InitiateReconMatchingAPProcessor"
								+ ".getIncrementalDataSummary():: "
								+ "Invoking USP_AUTO_2APR_PRE_RECON_SUMMARY_DELTA "
								+ "Stored Proc");
				LOGGER.debug(msg);
			}
			storedProc = entityManager.createStoredProcedureQuery(
					"USP_AUTO_2APR_PRE_RECON_SUMMARY_DELTA");

			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);
			storedProc.setParameter("P_GSTIN_LIST", rGstins);

			@SuppressWarnings("unchecked")
			String status = (String) storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						" auto 6A - 2APR AP manual, "
								+ "USP_AUTO_2APR_PRE_RECON_SUMMARY_DELTA response {}",
						status);
			}

			boolean reconFlag = ReconStatusConstants.SUCCESS
					.equalsIgnoreCase(status) ? true : false;
			return reconFlag;

		} catch (Exception ee) {
			String msg = String.format(
					"Error while Executing Stored Proc to get "
							+ " IncrementalDataSummary for recipientGstins :%s",
					recipientGstins);
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

}
