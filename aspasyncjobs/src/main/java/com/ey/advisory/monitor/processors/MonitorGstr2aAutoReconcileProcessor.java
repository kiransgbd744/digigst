/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
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
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconGstinEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AIM3BLockStatusRepository;
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
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Service("MonitorGstr2aAutoReconcileProcessor")
public class MonitorGstr2aAutoReconcileProcessor
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
			.of("B2B", "B2BA", "CDN", "CDNA", "IMPG", "IMPGSEZ");

	private static final ImmutableList<String> SECTIONS_6A = ImmutableList
			.of("B2B", "B2BA", "CDN", "CDNA");

	private static final ImmutableList<String> ERP_STATUS = ImmutableList.of(
			"PAN_RECON_JOB_POSTED", "PAN_RECON_FAILED", "PAN_RECON_COMPLETED",
			"ERP_JOB_POSTED", "REPORT_GENERATED", "REPORT_GENERATION_FAILED",
			"RECON_FAILED", "PAN_RECON_INPROGRESS");

	private static final ImmutableList<String> RECON_PROGRESS_STATUS = ImmutableList
			.of("RECON_SUBMITTED", "INITIATED");

	private static List<String> ineligibleStatus = ImmutableList.of(
			ReconStatusConstants.RECON_INITIATED,
			ReconStatusConstants.RECON_INPROGRESS);

	
	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("MMyyyy");

	@Autowired
	@Qualifier("AIM3BLockStatusRepository")
	AIM3BLockStatusRepository status3BRepo;

	private static List<String> auto3BLockStatusList = Arrays
			.asList("INITIATED", "INPROGRESS");

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {

			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"About to monitor GSTR2A/6A reconcile for group - '%s' ",
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
			// Add check from TBL_AIM_3BLOCK_STATUS
			// return

			int auto3BstatusCount = status3BRepo
					.findByStatusIn(auto3BLockStatusList);

			if (auto3BstatusCount != 0) {
				String msg = String.format(
						"Auto 3B Locking is in progress, please initiate recon after sometime");
				LOGGER.error(msg);
				return;
			}

			createPreReconConfigInserts(entityIds);
			
			if(!isReconEligible())
			{
				LOGGER.error("Currently there are active recons. Hence skipping auto recon ");
				return;
			}

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
			List<AutoReconStatusEntity> entities = new ArrayList<>();

			List<AutoReconStatusEntity> entities2A = autoReconStatusRepo
					.findByGet2aStatusAndReconStatusIsNullAndDate("INITIATED",
							EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
									.toLocalDate());

			List<AutoReconStatusEntity> entities6A = autoReconStatusRepo
					.findByGet6aStatusAndReconStatusIsNullAndDate("INITIATED",
							EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
									.toLocalDate());
			entities.addAll(entities2A);
			entities.addAll(entities6A);

			// check for any recon hault entities
			List<Long> reconHaltEntities = new ArrayList<>();
			reconHaltEntities = reconConfigRepo.findReconHaultEntityId();

			if (entities.isEmpty()) {
				LOGGER.warn(
						"Either no record found or no entity is opted for Auto "
								+ "Recon for : group {}",
						group.getGroupCode());
				return;
			}

			List<Gstr2ReconConfigEntity> panInProgressConfigId = reconConfigRepo
					.findByAutoReconDateAndStatusIn(
							EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
									.toLocalDate(),
							Arrays.asList("PAN_RECON_JOB_POSTED",
									"PAN_RECON_INPROGRESS"));

			if (panInProgressConfigId != null
					&& !panInProgressConfigId.isEmpty()) {
				String msg = String.format(
						"PAN LEVEL Recon in progress , Hence skipping Gstin recon");
				LOGGER.error(msg);
				return;
			}

			// TODO check for any recon failure
			AutoReconStatusEntity entity = validateEntity(entities);

			if (entity != null) {
				Gstr2ReconConfigEntity obj = reconConfigRepo
						.findByEntityIdAndAutoReconDate(entity.getEntityId(),
								LocalDate.now());

				List<String> reconStatus = reconGstinRepo
						.findByConfigId(obj.getConfigId());

				String returnType = null;
				if (!Strings.isNullOrEmpty(entity.getGet2aStatus())) {
					returnType = "2A";
				} else {
					returnType = "6A";
				}

				if (!reconHaltEntities.isEmpty()
						&& reconHaltEntities.contains(entity.getEntityId())) {
					LOGGER.error(
							" recon Halt is detected. Hence not posting recon job ");

					if ("2A".equalsIgnoreCase(returnType)) {

						autoReconStatusRepo.updateGet2aAndReconRemarks(
								"COMPLETED", LocalDateTime.now(), null,
								entity.getId(), "Recon Halt found");
					} else {
						autoReconStatusRepo.updateGet6aAndReconRemarks(
								"COMPLETED", LocalDateTime.now(), null,
								entity.getId(), "Recon Halt found");
					}
				} else {
					if (reconStatus.contains("RECON_FAILED")) {
						LOGGER.error(
								" recon failure is detected. Hence not posting recon job ");
						if ("2A".equalsIgnoreCase(returnType)) {

							autoReconStatusRepo.updateGet2aAndReconRemarks(
									"COMPLETED", LocalDateTime.now(), null,
									entity.getId(), "Recon failure found");
						} else {
							autoReconStatusRepo.updateGet6aAndReconRemarks(
									"COMPLETED", LocalDateTime.now(), null,
									entity.getId(), "Recon failure found");
						}
					} else {
						createReconJob(entity, returnType);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception occured while Auto reconcile", e);
			throw new AppException(e);
		}
	}

	private void createReconJob(AutoReconStatusEntity entity,
			String returnType) {

		String gstin = entity.getGstin();
		Gstr2ReconConfigEntity obj = reconConfigRepo
				.findByEntityIdAndAutoReconDate(entity.getEntityId(),
						LocalDate.now());

		// checking for incremental data before submitting recon.
		if (getIncrementalDataSummary(Arrays.asList(gstin))) {

			String groupCode = TenantContext.getTenantId();

			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("gstin", entity.getGstin());
			jobParams.addProperty("entityId", entity.getEntityId());
			jobParams.addProperty("id", entity.getId());
			jobParams.addProperty("configId", obj.getConfigId());
			if ("2A".equalsIgnoreCase(returnType)) {

				jobParams.addProperty("returnType", "2A");
			} else {
				jobParams.addProperty("returnType", "6A");

			}

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2A_AUTO_RECON, jobParams.toString(),
					"SYSTEM", 50L, null, null);

			if ("2A".equalsIgnoreCase(returnType)) {

				autoReconStatusRepo.updateGet2aAndReconStatus("COMPLETED",
						LocalDateTime.now(), "RECON_SUBMITTED", entity.getId());

			} else {
				autoReconStatusRepo.updateGet6aAndReconStatus("COMPLETED",
						LocalDateTime.now(), "RECON_SUBMITTED", entity.getId());

			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Updated Auto recon table for Id - '%s' ",
						entity.getId());
				LOGGER.debug(msg);
			}
		} else {

			if ("2A".equalsIgnoreCase(returnType)) {

				autoReconStatusRepo.updateGet2aAndReconStatus("COMPLETED",
						LocalDateTime.now(), "NO_DELTA_DATA", entity.getId());

			} else {
				autoReconStatusRepo.updateGet6aAndReconStatus("COMPLETED",
						LocalDateTime.now(), "NO_DELTA_DATA", entity.getId());

			}

			reconGstinRepo.updateStatus(entity.getGstin(), entity.getDate(),
					"NO_DELTA_DATA");

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"updating status in updateGet2aAndReconStatus "
								+ "NO_DELTA_DATA as there is no incremental "
								+ "data found for  groupcode :{} are gstin {}",
						TenantContext.getTenantId(), entity.getGstin());
			}
		}

	}

	private void createPreReconConfigInserts(List<Long> entityIds) {

		String groupCode = TenantContext.getTenantId();

		List<AutoReconStatusEntity> entities = new ArrayList<>();

		List<AutoReconStatusEntity> entities2A = autoReconStatusRepo
				.findByGet2aStatusAndReconStatusIsNullAndDate("INITIATED",
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
								.toLocalDate());

		List<AutoReconStatusEntity> entities6A = autoReconStatusRepo
				.findByGet6aStatusAndReconStatusIsNullAndDate("INITIATED",
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
								.toLocalDate());

		entities.addAll(entities6A);
		entities.addAll(entities2A);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"entities eligible for autorecon for groupcode :{} are {}",
					groupCode, entities);
		}

		ConfigQuestionEntity confQuestionEntity = configQuestionRepo
				.findByQuestionCodeAndQuestionType("I27", "R");

		Set<Long> optedEntities = new HashSet<>();

		List<Long> optedEntities2A = entityConfigPemtRepo.getAllEntitiesOpted2A(
				entityIds, "I27", confQuestionEntity.getId());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Entities opted for Auto GSTR2A for groupcode :{} ,are {}",
					groupCode, optedEntities2A);
		}

		ConfigQuestionEntity confQuestion6AEntity = configQuestionRepo
				.findByQuestionCodeAndQuestionType("I35", "R"); // check
		List<Long> optedEntities6A = entityConfigPemtRepo.getAllEntitiesOpted2A(
				entityIds, "I35", confQuestion6AEntity.getId());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Entities opted for Auto GSTR6A for groupcode :{} ,are {}",
					groupCode, optedEntities6A);
		}

		Set<String> gstins = entities.stream()
				.map(AutoReconStatusEntity::getGstin)
				.collect(Collectors.toSet());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"gstins eligible for autorecon for groupcode :{} are {}",
					groupCode, gstins);
		}

		optedEntities.addAll(optedEntities2A);
		optedEntities.addAll(optedEntities6A);

		for (Long entityId : optedEntities) {

			boolean isActiveGstins = false;
			List<String> optedGstins = new ArrayList<>();

			List<String> optedGstins2A = gSTNDetailRepo
					.findRegGstinByEntityId(entityId);

			List<String> optedGstins6A = gSTNDetailRepo
					.findgstinByEntityIdWithISD(Arrays.asList(entityId));

			optedGstins.addAll(optedGstins6A);
			optedGstins.addAll(optedGstins2A);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Gstins opted for gstr2a or gstr6A are {}",
						optedGstins);
			}

			for (String gstin : optedGstins) {
				if (gstins.contains(gstin)) {
					isActiveGstins = true;
					break;
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Activegstins eligible for autorecon for groupcode :{} are {}",
						groupCode, isActiveGstins);
			}

			Gstr2ReconConfigEntity entity = reconConfigRepo
					.findByEntityIdAndAutoReconDate(entityId,
							EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now())
									.toLocalDate());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"entry in config table for autorecon for groupcode :{} are {}",
						groupCode, entity);
			}

			if (entity == null && isActiveGstins) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"About to insert into config table for autorecon for groupcode :{}",
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
								|| reconStatus.contains("RECON_INITIATED")
								|| reconStatus.contains(
										"RECON_FAILED"))) {/*
															 * 
															 * JsonObject
															 * erpReportParams =
															 * new JsonObject();
															 * erpReportParams
															 * .addProperty(
															 * "configId",
															 * entity.
															 * getConfigId() );
															 * erpReportParams
															 * .addProperty(
															 * "entityId",
															 * entityId);
															 * 
															 * asyncJobsService
															 * .createJob(
															 * groupCode,
															 * JobConstants.
															 * Gstr2A_AUTO_RECON_ERP_REPORT,
															 * erpReportParams
															 * .toString(),
															 * "SYSTEM", 50L,
															 * null, null);
															 * 
															 * Gstr2ReconAddlReportsEntity
															 * setEntity =
															 * setEntity(
															 * entity.
															 * getConfigId() ,
															 * "ERP_Report") ;
															 * 
															 * addlReportRepo
															 * .save(
															 * setEntity);
															 * 
															 * reconConfigRepo .
															 * updateReconConfigStatus
															 * (
															 * "ERP_JOB_POSTED",
															 * entity.
															 * getConfigId() );
															 */
					JsonObject jobParams = new JsonObject();
					// jobParams.addProperty("gstin", entity.getGstin());
					jobParams.addProperty("entityId", entityId);
					// jobParams.addProperty("id", entity.getId());
					jobParams.addProperty("configId", entity.getConfigId());
					jobParams.addProperty("returnType", "2A");

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

				List<String> prevTaxPeriodList = new ArrayList<>();

				int numOfTaxPeriods = entity.getNumOfTaxPeriods();
				for (numOfTaxPeriods = numOfTaxPeriods
						- 1; numOfTaxPeriods >= 0; numOfTaxPeriods--) {

					LocalDate minusMonths = EYDateUtil
							.toISTDateTimeFromUTC(LocalDateTime.now())
							.toLocalDate().minusMonths(numOfTaxPeriods);
					String taxPeriod = minusMonths.format(formatter);
					prevTaxPeriodList.add(taxPeriod);

				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("Previous Taxperiods : '%s'",
							prevTaxPeriodList);
					LOGGER.debug(msg);
				}

				List<String> getAllStatus = new ArrayList<>();

				if (!Strings.isNullOrEmpty(entity.getGet2aStatus())) {
					getAllStatus = getAnx1BatchRepo.findBatchForRecon(
							entity.getGstin(), prevTaxPeriodList, "GSTR2A",
							SECTIONS);

					if (getAllStatus.contains("FAILED")) {

						entity.setGet2aStatus("FAILED");
						entity.setGet2ACompletedOn(LocalDateTime.now());
						entity.setReconStatus(null);
						autoReconStatusRepo.save(entity);

						reconGstinRepo.updateStatus(entity.getGstin(),
								entity.getDate(), "GET_FAILED");

						continue;
					}

				} else {
					getAllStatus = getAnx1BatchRepo.findBatchForRecon(
							entity.getGstin(), prevTaxPeriodList, "GSTR6A",
							SECTIONS_6A);

					if (getAllStatus.contains("FAILED")) {

						entity.setGet6aStatus("FAILED");
						entity.setGet6aCompletedOn(LocalDateTime.now());
						entity.setReconStatus(null);
						autoReconStatusRepo.save(entity);

						reconGstinRepo.updateStatus(entity.getGstin(),
								entity.getDate(), "GET_FAILED");

						continue;
					}

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
			LOGGER.error("Exception occured while monitoring GSTR2A Auto Recon",
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
			entityInfo.ifPresent(info -> entity.setPan(info.getPan()));
			
			entity.setType("AUTO_2APR");
			entity.setStatus("RECON_SUBMITTED");
			entity.setCreatedDate(LocalDateTime.now());
			entity.setCreatedBy("SYSTEM");
			entity.setAutoReconDate(EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now()).toLocalDate());
			reconConfigRepo.save(entity);

			return configId;

		} catch (Exception ee) {
			String msg = String.format(
					"Exception occured while persisting"
							+ " recon report Config Entity for " + "  :%s",
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
				 */
				reconGstinEntiy.setFromDate(null);
				reconGstinEntiy.setToDate(LocalDateTime.now());
				String reconStatus = autoReconGstins.contains(gstin)
						? "GET_INITIATED"
						: "GET_NOT_INITIATED";
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
		String currentDate = currentYear
				+ (currentMonth < 10 ? ("0" + currentMonth)
						: String.valueOf(currentMonth));
		if (nextSequencevalue != null && nextSequencevalue > 0) {
			digits = String.format("%06d", nextSequencevalue);
			reportId = currentDate.concat(digits);
		}

		return Long.valueOf(reportId);
	}

	// force motor from date fix

	/*
	 * private LocalDateTime fetch2aAutoReconCompletedDate(Long entityId, String
	 * gstin, Long configId) { try { LocalDateTime fromDate =
	 * LocalDateTime.now();
	 * 
	 * String query = "SELECT MAX(GD.COMPLETED_ON) AS FROM_DATE " +
	 * " FROM TBL_RECON_REPORT_GSTIN_DETAILS GD INNER JOIN TBL_RECON_REPORT_CONFIG C "
	 * + " ON C.RECON_REPORT_CONFIG_ID=GD.RECON_REPORT_CONFIG_ID " +
	 * " AND C.RECON_TYPE IN ('AUTO_2APR','AP_M_2APR') AND GD.STATUS='RECON_COMPLETED' "
	 * + " WHERE GD.GSTIN = :gstin";
	 * 
	 * Query q = entityManager.createNativeQuery(query); q.setParameter("gstin",
	 * gstin);
	 * 
	 * if (LOGGER.isDebugEnabled()) {
	 * LOGGER.debug("executing query to get the recon complation date" +
	 * " from AUTO_2A_2B_RECON_STATUS table for given entityID" + +entityId +
	 * ", gstin " + gstin); }
	 * 
	 * @SuppressWarnings("unchecked") Object obj = q.getSingleResult();
	 * 
	 * if (obj == null) {
	 * if("DECEMBER".equalsIgnoreCase(fromDate.getMonth().toString())) { return
	 * LocalDateTime.of(fromDate.getYear(), 04, 01, 0, 0, 0); }else { return
	 * LocalDateTime.of(fromDate.getYear()-1, 04, 01, 0, 0, 0); } // return
	 * LocalDateTime.of(2021, 04, 01, 0, 0, 0); // 01-Apr-2021 } fromDate =
	 * ((Timestamp) obj).toLocalDateTime(); return fromDate;
	 * 
	 * } catch (Exception ee) { String msg =
	 * "Exception occured while fetching auto Recon completion status";
	 * LOGGER.error(msg, ee); throw new AppException(msg); } }
	 */
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
						.format("Inside MonitorGstr2aAutoReconcileProcessor"
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
				LOGGER.debug("2APR AP Auto, "
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
	
	private boolean isReconEligible() {

		List<Gstr2ReconConfigEntity> entityList = reconConfigRepo
				.findByStatusIn(ineligibleStatus);

		entityList = entityList.stream()
				.filter(o -> !o.getType().equalsIgnoreCase("EINVPR")).filter(o -> !o.getType().equalsIgnoreCase("AUTO_2APR"))
				.collect(Collectors.toList());

		if (!entityList.isEmpty()) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside MonitorInitiateMatchingQueueProcessor - "
								+ "isReconEligible() group code {} previous recon "
								+ "is already in Initiate / Inprogress status hence "
								+ "returing false ",
						TenantContext.getTenantId());
			}
			return false;
		}

		return true;
	}

}
