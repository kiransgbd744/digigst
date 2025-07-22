package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.ErpEventsScenarioPermissionEntity;
import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioMasterRepository;
import com.ey.advisory.admin.data.repositories.client.SftpScenarioPermissionRepository;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2ToleranceValueDto;
import com.ey.advisory.app.data.entities.client.asprecon.AutoRecon2APROnBoardingEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconFailedProcedureEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstinSourceInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2AERPRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2APROnBoardingRepo;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconFailedProcedureRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.FailedBatchAlertUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("Gstr2aAutoInitiateReconServiceImpl")
public class Gstr2aAutoInitiateReconServiceImpl
		implements Gstr2aAutoInitiateReconService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("AutoReconStatusRepository")
	private AutoReconStatusRepository autoReconStatusRepo;

	@Autowired
	private ErpEventsScenarioPermissionRepository erpEventsScenPermissionRepo;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	@Qualifier("SftpScenarioPermissionRepository")
	private SftpScenarioPermissionRepository sftpScenPermissionRepo;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository reconGstinRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("GstinSourceInfoRepository")
	private GstinSourceInfoRepository gstinSourceInfoRepository;

	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	@Qualifier("AutoRecon2APROnBoardingRepo")
	private AutoRecon2APROnBoardingRepo autoRecon2APROnBoardingRepo;

	@Autowired
	@Qualifier("AutoRecon2AERPRequestRepository")
	AutoRecon2AERPRequestRepository revIntCheckRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;
	
	@Autowired
	@Qualifier("Gstr2ReconFailedProcedureRepository")
	Gstr2ReconFailedProcedureRepository procRepo;

	private static final String CONF_KEY = "gstr2.recon.erp.sftp.revInt";
	private static final String CONF_CATEG = "Recon_Reverese_Push";

	@Override
	public void initiateAutoRecon(String gstin, Long entityId, Long autoReconId,
			Long configId, String retType) {
		try {
			BigDecimal cess = null;
			BigDecimal cgst = null;
			BigDecimal igst = null;
			BigDecimal sgst = null;
			BigDecimal taxableVal = null;
			BigDecimal totalTax = null;
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Calling initiateAutoRecon for gstin :%s, entityId :%s"
								+ " and configId :%s",
						gstin, entityId, autoReconId);
				LOGGER.debug(msg);
			}
			// update AUTO_2A_2B_RECON_STATUS as initiated
			autoReconStatusRepo.updateAutoReconStatus("INITIATED",
					LocalDateTime.now(), autoReconId);

			reconGstinRepo.updateAutoReconIdAndStatus(gstin, configId,
					autoReconId, "RECON_INITIATED");

			List<Gstr2ToleranceValueDto> toleranceValue = getToleranceValue(
					configId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"toleranceValue gstin level : {}" + toleranceValue);
			}
			cess = checkAndSetDefaultValue(toleranceValue.get(0).getCess());
			cgst = checkAndSetDefaultValue(toleranceValue.get(0).getCgst());
			igst = checkAndSetDefaultValue(toleranceValue.get(0).getIgst());
			sgst = checkAndSetDefaultValue(toleranceValue.get(0).getSgst());
			taxableVal = checkAndSetDefaultValue(
					toleranceValue.get(0).getTaxableVal());
			totalTax = checkAndSetDefaultValue(
					toleranceValue.get(0).getTotalTax());

			// force motor fix for from date

			LocalDateTime fromdate = fetch2aAutoReconCompletedDate(entityId,
					gstin, configId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2aAutoInitiateReconServiceImpl, Invoked "
								+ "fetch2aAutoReconCompletedDate() "
								+ "configId %s, " + "gstin %s fromdate %s :",
						configId.toString(), gstin, fromdate.toString());
				LOGGER.debug(msg);
			}

			reconGstinRepo.updateFromDateByGstinAndConfigId(gstin, configId,
					fromdate);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr2aAutoInitiateReconServiceImpl, updated "
								+ "updateFromDateByGstinAndConfigId() "
								+ "configId %s, " + "gstin %s fromdate %s :",
						configId.toString(), gstin, fromdate.toString());
				LOGGER.debug(msg);
			}

			// getall storedprocs
			List<AllProcsDto> allProcList = getAllProcs();

			Map<Integer, String> procMap = new TreeMap<>();
			procMap = allProcList.stream().collect(Collectors
					.toMap(o -> o.getSequence(), o -> o.getProcName()));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("allProcList gstin level : {}" + allProcList);
			}

			List<AllProcsDto> procListWithGstinTrue = allProcList.stream()
					.filter(o -> o.isGstinLevel()).collect(Collectors.toList());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("procListWithGstinTrue gstin level : {}"
						+ procListWithGstinTrue);
			}

			Map<Integer, AllProcsDto> procWithGstinTrueMap = procListWithGstinTrue
					.stream().collect(Collectors.toMap(o -> o.getSequence(),
							o -> o, (o1, o2) -> o2, TreeMap::new));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("procWithGstinTrueMap gstin level : {}"
						+ procWithGstinTrueMap);
			}
			// getting is gstin false procs into list
			List<String> isGstinFalseList = allProcList.stream()
					.filter(o -> !o.isGstinLevel()).map(o -> o.getProcName())
					.collect(Collectors.toList());

			String procName = null;
			String response = null;

			List<AutoRecon2APROnBoardingEntity> selfOnboardingEntities = autoRecon2APROnBoardingRepo
					.findByEntityIdAndIsActiveTrue(entityId.toString());

			List<String> optionalProcList = new ArrayList<>();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("selfOnboardingEntities gstin level : {}"
						+ selfOnboardingEntities);
			}

			for (AutoRecon2APROnBoardingEntity selfOnboardingEntity : selfOnboardingEntities) {

				if ("Doc No & Doc Date Mismatch".equalsIgnoreCase(
						selfOnboardingEntity.getReportType())) {

					if (selfOnboardingEntity.getOptionalReportSected()) {
						optionalProcList.add("Doc No & Doc Date Mismatch");
					}

				}

			}

			for (Integer k : procWithGstinTrueMap.keySet()) {
				procName = procMap.get(k);

				AllProcsDto procedureTestEntity = procWithGstinTrueMap.get(k);

				// USP_AUTO_2APR_16_POTENTIAL_1
				// USP_AUTO_2APR_17_POTENTIAL_2
				// USP_AUTO_2APR_18_LOGICAL

				// chcking for optional reports

				String reportName = procedureTestEntity.getReportName();

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("procedureTestEntity gstin level : {}"
							+ procedureTestEntity);
				}

				if ((!reportName.equalsIgnoreCase("Doc No & Doc Date Mismatch")
						|| (reportName
								.equalsIgnoreCase("Doc No & Doc Date Mismatch"))
								&& optionalProcList.contains(
										"Doc No & Doc Date Mismatch"))) {

					StoredProcedureQuery storedProc = procCall(cess, cgst, igst,
							sgst, taxableVal, totalTax, configId, gstin,
							procedureTestEntity.getProcName(),
							isGstinFalseList);

					try {
						response = (String) storedProc.getSingleResult();
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"ProcName :%s, Response :%s for gstin :%s"
											+ " , configId :%s and autoReconId :%s",
									procedureTestEntity.getProcName(), response,
									gstin, configId, autoReconId);
							LOGGER.debug(msg);
						}

						if (!ReconStatusConstants.SUCCESS
								.equalsIgnoreCase(response)) {
							throw new AppException();
						}

					} catch (Exception ee) {
						String msg = String.format(
								"Response from Auto"
										+ " RECON_MASTER SP %s did not return"
										+ " success for configId :%s,and autoReconId :%s"
										+ " Hence updating to Failed",
								procName, configId, autoReconId);
						LOGGER.error(msg, ee);
						throw new AppException(msg);
					}

				}
			}
			autoReconStatusRepo.updateAutoReconStatusOnSuccessOrFailure(
					"SUCCESS", LocalDateTime.now(), autoReconId);

			reconGstinRepo.updateReconCompletedStatus(gstin, configId,
					"RECON_COMPLETED", LocalDateTime.now());

			boolean isRevIntg = isErpRevIntSelected(
					TenantContext.getTenantId());

			if (!isRevIntg) {
				LOGGER.error(
						"GroupCode {}, Gstin {}, ConfigId {} and AutoReconId {} "
								+ "is not eligible for ERP reverse intg,"
								+ "hence trying SFTP push ",
						TenantContext.getTenantId(), gstin, configId,
						autoReconId);

				submitSFTPJob(configId, gstin, autoReconId, retType);
			}

		} catch (Exception ex) {
			String msg = "Exception occured during Gstr2a auto initiate Recon";
			LOGGER.error(msg, ex);
			
			autoReconStatusRepo.updateAutoReconStatusOnSuccessOrFailure(
					"FAILED", LocalDateTime.now(), autoReconId);
			reconGstinRepo.updateStatus(gstin, LocalDate.now(), "RECON_FAILED");
			
			callFailedProc(gstin, configId);

			throw new AppException(msg, ex);
		}
	}

	private List<Gstr2ToleranceValueDto> getToleranceValue(Long cofigId) {
		try {
			String queryStr = "SELECT  SUM((IFNULL((CASE  "
					+ "		 WHEN CONFG_QUESTION_ID=34 THEN CAST(ANSWER AS DECIMAL(15,2)) END),0.00) )) AS  "
					+ "		 TAXABLE_VALUE, SUM((IFNULL((CASE WHEN CONFG_QUESTION_ID=35  "
					+ "					THEN CAST(ANSWER AS DECIMAL(15,2)) END),0.00) )) AS IGST  "
					+ "				         , SUM((IFNULL((CASE WHEN CONFG_QUESTION_ID=36  "
					+ "					THEN CAST(ANSWER AS DECIMAL(15,2)) END),0.00) )) AS CGST  "
					+ "				         , SUM((IFNULL((CASE WHEN CONFG_QUESTION_ID=37  "
					+ "					THEN CAST(ANSWER AS DECIMAL(15,2)) END),0.00) )) AS SGST  "
					+ "				         , SUM((IFNULL((CASE WHEN CONFG_QUESTION_ID=38  "
					+ "					THEN CAST(ANSWER AS DECIMAL(15,2)) END),0.00) )) AS CESS  "
					+ "				         , SUM((IFNULL((CASE WHEN CONFG_QUESTION_ID=39  "
					+ "					THEN CAST(ANSWER AS DECIMAL(15,2)) END),0.00) )) AS TOTAL_TAX  "
					+ "				                    FROM ENTITY_CONFG_PRMTR P  "
					+ "				     INNER JOIN TBL_RECON_REPORT_CONFIG RC  "
					+ "     ON P.ENTITY_ID=RC.ENTITY_ID "
					+ "     WHERE QUESTION_CODE='I13' AND IS_DELETE=FALSE "
					+ "     AND  RECON_REPORT_CONFIG_ID=:cofigId";
			Query q = entityManager.createNativeQuery(queryStr);

			q.setParameter("cofigId", cofigId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to getToleranceValue :: cofigId "
						+ cofigId);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<Gstr2ToleranceValueDto> retList = list.stream()
					.map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ee) {
			String msg = "Exception while fetching tolerance value";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}

	}

	private Gstr2ToleranceValueDto convertUserInput(Object[] o) {

		Gstr2ToleranceValueDto dto = new Gstr2ToleranceValueDto();
		try {
			dto.setTaxableVal((BigDecimal) o[0]);
			dto.setIgst((BigDecimal) o[1]);
			dto.setCgst((BigDecimal) o[2]);
			dto.setSgst((BigDecimal) o[3]);
			dto.setCess((BigDecimal) o[4]);
			dto.setTotalTax((BigDecimal) o[5]);
		} catch (Exception ex) {
			String msg = String.format(
					"Error while fetching Tolerance Value {object}:: %s ", o);
			LOGGER.error(msg, ex);
		}
		return dto;
	}

	private List<AllProcsDto> getAllProcs() {
		try {
			String queryStr = "SELECT AUTO_SEQUENCE, PROCEDURE_NAME, IS_GSTIN_LEVEL, REPORT_NAME, REPORT_TYPE FROM"
					+ " TBL_AUTO_2APR_PROCEDURE WHERE AUTO_SEQUENCE"
					+ " IS NOT NULL AND IS_ACTIVE = TRUE";
			Query q = entityManager.createNativeQuery(queryStr);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to getAllProcs");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<AllProcsDto> retList = list.stream().map(o -> conertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ee) {
			String msg = "Exception while fetching proc names and sequence";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private AllProcsDto conertToDto(Object[] o) {
		AllProcsDto dto = new AllProcsDto();
		try {
			dto.setSequence((Integer) o[0]);
			dto.setProcName((String) o[1]);
			dto.setGstinLevel((boolean) o[2]);
			dto.setReportName((String) o[3]);
			dto.setReportType((String) o[4]);
		} catch (Exception ex) {
			String msg = String.format(
					"Error while setting Procedure name {object}:: %s ", o);
			LOGGER.error(msg, ex);
		}
		return dto;
	}

	/**
	 * @param cess
	 * @param cgst
	 * @param igst
	 * @param sgst
	 * @param taxableVal
	 * @param totalTax
	 * @param configId
	 * @param gstin
	 * @param procName
	 * @return
	 */
	private StoredProcedureQuery procCall(BigDecimal cess, BigDecimal cgst,
			BigDecimal igst, BigDecimal sgst, BigDecimal taxableVal,
			BigDecimal totalTax, Long configId, String gstin, String procName,
			List<String> isGstinFalseList) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Auto Recon SP with ConfigId :%s and"
							+ "gstin :%s, procName :%s",
					configId.toString(), gstin, procName);
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_RECON_REPORT_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

		if (!isGstinFalseList.contains(procName)) {

			storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
					ParameterMode.IN);

			storedProc.setParameter("P_GSTIN", gstin);
		}

		storedProc.registerStoredProcedureParameter("P_TAXABLE_VALUE",
				BigDecimal.class, ParameterMode.IN);

		storedProc.setParameter("P_TAXABLE_VALUE", taxableVal);

		storedProc.registerStoredProcedureParameter("P_CGST", BigDecimal.class,
				ParameterMode.IN);

		storedProc.setParameter("P_CGST", cgst);

		storedProc.registerStoredProcedureParameter("P_SGST", BigDecimal.class,
				ParameterMode.IN);

		storedProc.setParameter("P_SGST", sgst);

		storedProc.registerStoredProcedureParameter("P_IGST", BigDecimal.class,
				ParameterMode.IN);

		storedProc.setParameter("P_IGST", igst);

		storedProc.registerStoredProcedureParameter("P_CESS", BigDecimal.class,
				ParameterMode.IN);

		storedProc.setParameter("P_CESS", cess);

		storedProc.registerStoredProcedureParameter("P_TOTAL_TAX",
				BigDecimal.class, ParameterMode.IN);

		storedProc.setParameter("P_TOTAL_TAX", totalTax);

		return storedProc;
	}

	private BigDecimal checkAndSetDefaultValue(BigDecimal obj) {

		return (obj != null) ? obj : BigDecimal.TEN;
	}

	private boolean submitSFTPJob(Long configId, String gstin, Long autoReconId,
			String retType) {

		/*
		 * Check for SFTP Push on-board
		 */
		Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
				JobConstants.GSTR2A_AUTO_RECON_SFTP_PUSH);

		if (scenarioId == null) {

			LOGGER.error(
					"Scenario {} is not configured for group {},"
							+ "Hence reverse SFTP Push job is not posted for {}",
					JobConstants.GSTR2A_AUTO_RECON_SFTP_PUSH,
					TenantContext.getTenantId(), gstin);

			return false;
		}

		ErpEventsScenarioPermissionEntity scenarioPermision = erpEventsScenPermissionRepo
				.findByScenarioIdAndIsDeleteFalse(scenarioId);

		if (scenarioPermision == null) {

			LOGGER.error("SFTP permission {} is not configured for group {},"
					+ "Hence SFTP reverse push job is " + "not posted for {}",
					JobConstants.GSTR2A_AUTO_RECON_REV_INTG,
					TenantContext.getTenantId(), gstin);

			return false;
		}

		/*
		 * Submit the Async job only if the Reverse integration is on-boarded -
		 * Client level
		 */

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Submitting SFTP Push Job for  GSTIN :%s,"
							+ "and autoReconId :%d", gstin, configId);
			LOGGER.debug(msg);
		}

		autoReconStatusRepo.updateERPPushStatus("INITIATED", null, autoReconId);

		Gson gson = GsonUtil.newSAPGsonInstance();
		GSTR2aAutoReconRevIntgReqDto erpReqDto = new GSTR2aAutoReconRevIntgReqDto();
		erpReqDto.setGstin(gstin);
		erpReqDto.setConfigId(configId);
		erpReqDto.setAutoReconId(autoReconId);
		erpReqDto.setScenarioName(JobConstants.GSTR2A_AUTO_RECON_SFTP_PUSH);
		erpReqDto.setScenarioId(scenarioId);
		erpReqDto.setDestinationName(scenarioPermision.getDestName());
		erpReqDto.setErpId(scenarioPermision.getErpId());
		erpReqDto.setDestinationType("NONSAP");
		erpReqDto.setRetType(retType);
		String erpReqJson = gson.toJson(erpReqDto);

		asyncJobsService.createJob(TenantContext.getTenantId(),
				JobConstants.GSTR2A_AUTO_RECON_SFTP_PUSH, erpReqJson,
				JobConstants.SYSTEM, JobConstants.PRIORITY,
				JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);
		return true;

	}

	/*
	 * private boolean submitReverseIntgJob(Long configId, String gstin, Long
	 * autoReconId, String retType) {
	 * 
	 * try {
	 * 
	 * 
	 * Check for Reverse integration on-board
	 * 
	 * Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG);
	 * 
	 * if (scenarioId == null) {
	 * 
	 * LOGGER.error( "Scenario {} is not configured for group {}," +
	 * "Hence reverse integartion job is not posted for {} and configId {}",
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, TenantContext.getTenantId(),
	 * gstin, configId);
	 * 
	 * return false; }
	 * 
	 * // look up into new table with gstin , // if no record is there log the
	 * error and return // take source id, look up based on system_ID on
	 * erp_info table take // Id // pass this id instead of hardcoded 1L in next
	 * method.
	 * 
	 * String sourceId = gstinSourceInfoRepository.findByGstin(gstin);
	 * 
	 * if (sourceId == null) {
	 * 
	 * LOGGER.error( "sourceId {} is not configured for group {}," +
	 * "Hence reverse integartion job is not posted for {} and configId {}",
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, TenantContext.getTenantId(),
	 * gstin, configId);
	 * 
	 * return false; }
	 * 
	 * Long erpId = erpInfoEntityRepository.getErpId(sourceId);
	 * 
	 * if (erpId == null) {
	 * 
	 * LOGGER.error( "erpId {} is not configured for group {}," +
	 * "Hence reverse integartion job is not posted for {} and configId {}",
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, TenantContext.getTenantId(),
	 * gstin, configId);
	 * 
	 * return false; } GSTNDetailEntity gstinInfo = gstinDetailRepo
	 * .findByGstinAndIsDeleteFalse(gstin); Long gstinId = gstinInfo.getId();
	 * List<ErpScenarioPermissionEntity> scenarioPermisionList =
	 * erpScenPermissionRepo
	 * .findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId, gstinId); if
	 * (!scenarioPermisionList.isEmpty()) {
	 * 
	 * LOGGER.error( "Scenario permission {} is not configured for group {}," +
	 * "Hence reverse integartion job is " +
	 * "not posted for {} and configId {}",
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, TenantContext.getTenantId(),
	 * gstin, configId);
	 * 
	 * return false; }
	 * 
	 * 
	 * Submit the Async job only if the Reverse integration is on-boarded -
	 * Client level
	 * 
	 * 
	 * if (LOGGER.isDebugEnabled()) { String msg = String
	 * .format("Invokking reverse Intg Job method to check " +
	 * "eligibility  for  GSTIN :%s," + "and autoReconId :%d", gstin, configId);
	 * LOGGER.debug(msg); }
	 * 
	 * // check eligibility in TBL_AUTO_2APR_ERP_REQUEST // synchronized (gstin)
	 * { will check later
	 * 
	 * if (!isRevIntEligible(configId, gstin)) {
	 * 
	 * revIntCheckRepo.updateStatus(configId, gstin, "WAITING");
	 * 
	 * autoReconStatusRepo.updateERPPushStatus("WAITING", null, autoReconId);
	 * 
	 * String msg = String.format(
	 * "2APR Reverse Integration is not eligible for " +
	 * "gstin %s, configId %s : ", gstin, configId.toString());
	 * 
	 * LOGGER.error(msg); return true;
	 * 
	 * } // }
	 * 
	 * autoReconStatusRepo.updateERPPushStatus("INITIATED", null, autoReconId);
	 * if (!scenarioPermisionList.isEmpty()) { for (ErpScenarioPermissionEntity
	 * scenario : scenarioPermisionList) { Gson gson =
	 * GsonUtil.newSAPGsonInstance(); GSTR2aAutoReconRevIntgReqDto erpReqDto =
	 * new GSTR2aAutoReconRevIntgReqDto(); erpReqDto.setGstin(gstin);
	 * erpReqDto.setConfigId(configId); erpReqDto.setAutoReconId(autoReconId);
	 * erpReqDto.setScenarioName( JobConstants.GSTR2A_AUTO_RECON_REV_INTG);
	 * erpReqDto.setScenarioId(scenarioId);
	 * erpReqDto.setErpId(scenario.getErpId());
	 * erpReqDto.setDestinationName(scenario.getDestName());
	 * erpReqDto.setDestinationType("SAP"); erpReqDto.setRetType(retType);
	 * String erpReqJson = gson.toJson(erpReqDto);
	 * 
	 * asyncJobsService.createJob(TenantContext.getTenantId(),
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, erpReqJson, JobConstants.SYSTEM,
	 * JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
	 * JobConstants.SCHEDULE_AFTER_IN_MINS); } } return true; } catch (Exception
	 * ex) { String msg = String.format(
	 * "Exception while submitting reverse Intg Job for " +
	 * " GSTIN :%s,and autoReconId :%d", gstin, configId); LOGGER.error(msg,
	 * ex); throw new AppException(msg, ex); } }
	 */

	/*
	 * private boolean isRevIntEligible(Long configId, String gstin) {
	 * 
	 * List<AutoRecon2AERPRequestEntity> list = revIntCheckRepo
	 * .findByGstinAndReconConfigIDLessThanAndStatusIn(gstin, configId,
	 * statusList);
	 * 
	 * return list.isEmpty(); }
	 */

	private boolean isErpRevIntSelected(String groupCode) {
		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY,
				groupCode);
		if (config != null && config.getValue().equalsIgnoreCase("false")) {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("isErpRevIntSelected config :%s",
						config.getValue());
				LOGGER.debug(msg);
			}

			return false;
		} else {
			return true;
		}

	}

	/*
	 * private void callFailedProc(Long configId) {
	 * 
	 * StoredProcedureQuery storedProc = entityManager
	 * .createStoredProcedureQuery("USP_AUTO_2APR_RECON_FAILED");
	 * 
	 * if (LOGGER.isDebugEnabled()) { String msg = String.format(
	 * "About to execute failedProc " +
	 * "USP_AUTO_2APR_RECON_FAILED SP with ConfigId :%s", configId.toString());
	 * LOGGER.debug(msg); }
	 * 
	 * storedProc.registerStoredProcedureParameter("P_RECON_REPORT_CONFIG_ID",
	 * Long.class, ParameterMode.IN);
	 * 
	 * storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);
	 * 
	 * String response = (String) storedProc.getSingleResult();
	 * LOGGER.debug("USP_AUTO_2APR_RECON_FAILED {} ", response);
	 * 
	 * }
	 */

	private LocalDateTime fetch2aAutoReconCompletedDate(Long entityId,
			String gstin, Long configId) {
		try {
			LocalDateTime fromDate = LocalDateTime.now();

			String query = "SELECT MAX(GD.COMPLETED_ON) AS FROM_DATE "
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

			if (obj == null) {
				if ("DECEMBER"
						.equalsIgnoreCase(fromDate.getMonth().toString())) {
					return LocalDateTime.of(fromDate.getYear(), 04, 01, 0, 0,
							0);
				} else {
					return LocalDateTime.of(fromDate.getYear() - 1, 04, 01, 0,
							0, 0);
				}
				// return LocalDateTime.of(2021, 04, 01, 0, 0, 0); //
				// 01-Apr-2021
			}
			fromDate = ((Timestamp) obj).toLocalDateTime();
			return fromDate;

		} catch (Exception ee) {
			String msg = "Exception occured while fetching auto Recon completion status";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private void callFailedProc(String gstin, Long configId) {

		String procName = null;
		String response = null;
		try {
			List<Gstr2ReconFailedProcedureEntity> procList = procRepo.findProcedure();
	
			Map<Integer, String> procMap = new TreeMap<>();

			procMap = procList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Config Id is '%s', Procs going to "
								+ "Execute  are %s ",
						configId.toString(), procMap.toString());
				LOGGER.error(msg);
			}
			for (Integer k : procMap.keySet())

			{
				procName = procMap.get(k);

				StoredProcedureQuery storedProc = procCallReconFailed(configId, gstin,
						procName);

				response = (String) storedProc.getSingleResult();

				LOGGER.debug(procName + " :: " + response);

				if (!ReconStatusConstants.SUCCESS
						.equalsIgnoreCase(response)) {

					String msg = String.format(
							"Config Id is '%s', Response "
									+ "from RECON_FAILED PROC %s did not "
									+ "return success,"
									+ " Hence updating to Failed",
							configId.toString(), procName);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Config Id is '%s', After For loop "
								+ "Post proc Execution %s ",
						configId.toString(), procMap.toString());
				LOGGER.error(msg);
			}
			reconConfigRepo.updateReconConfigStatus("RECON_FAILED", configId);

		} catch (Exception ex) {
			LOGGER.error(
					" Failed in failure proc at gstin level for config id is {} with the exception {} ",
					configId, ex);

			reconConfigRepo.updateReconConfigStatus("RECON_HALT", configId);
			failedBatAltUtility.prepareAndTriggerAlert(String.valueOf(configId),
					"AUTO_RECON",String.format(" RECON_HALT FOUND AT PAN LEVEL"));
		}

	}
	
	private StoredProcedureQuery procCallReconFailed(Long configId, String gstin,
			String procName) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Recon Failed Proc with ConfigId :%s",
					configId.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_RECON_REPORT_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

		return storedProc;
	}

}