/**
 * 
 */
package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.math.BigDecimal;
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
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2ToleranceValueDto;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRPatternEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconFailedProcedureEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstinSourceInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRPatternRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconFailedProcedureRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AllProcsDto;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.GSTR2aAutoReconRevIntgReqDto;
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
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2InitiateMatchingAPManualServiceImpl")
public class Gstr2InitiateMatchingAPManualServiceImpl
		implements Gstr2InitiateMatchingAPManualService {

	@Autowired
	@Qualifier("AutoReconStatusRepository")
	private AutoReconStatusRepository autoReconStatusRepo;

	@Autowired
	@Qualifier("GstinSourceInfoRepository")
	private GstinSourceInfoRepository gstinSourceInfoRepository;

	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private ErpEventsScenarioPermissionRepository erpEventsScenPermissionRepo;

	@Autowired
	private ErpScenarioMasterRepository scenarioMasterRepo;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRPatternRepository")
	Gstr2Recon2BPRPatternRepository pattern2BPRRepo;

	@Autowired
	FailedBatchAlertUtility failedBatAltUtility;

	@Autowired
	@Qualifier("Gstr2ReconFailedProcedureRepository")
	Gstr2ReconFailedProcedureRepository procRepo;

	/*
	 * private static List<String> statusList = Arrays.asList("FAILED",
	 * "INITIATED", "WAITING");
	 */

	private static BigDecimal defaultTen = BigDecimal.TEN;

	private static final String CONF_KEY1 = "gstr2.recon.report.pattern.length.size";
	private static final String CONF_CATEG1 = "RECON_REPORTS";

	private static final String CONF_KEY2 = "gstr2.recon.report.pattern.count";

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static final String CONF_KEY = "gstr2.recon.erp.sftp.revInt";
	private static final String CONF_CATEG = "Recon_Reverese_Push";

	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository reconGstinRepo;

	@Override
	public void executeAPManualRecon(Long configId, List<String> gstins,
			Long entityId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Begin, Gstr2InitiateMatchingAPManualServiceImpl. "
							+ "executeAPManualRecon() configId %s,"
							+ " gstins %s ", configId.toString(), gstins);
			LOGGER.debug(msg);
		}

		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("configId", configId);
		jsonParams.addProperty("entityId", entityId);
		jsonParams.addProperty("apFlag", true);

		String groupCode = TenantContext.getTenantId();

		// String pan = gstins.get(0).substring(2, 12);

		List<Gstr2ToleranceValueDto> toleranceValue = getToleranceValue(
				configId);

		Gstr2ToleranceValueDto toleValue = toleranceValue.get(0);

		BigDecimal cess = toleValue.getCess() != null ? toleValue.getCess()
				: defaultTen;
		BigDecimal cgst = toleValue.getCgst() != null ? toleValue.getCgst()
				: defaultTen;
		BigDecimal igst = toleValue.getIgst() != null ? toleValue.getIgst()
				: defaultTen;
		BigDecimal sgst = toleValue.getSgst() != null ? toleValue.getSgst()
				: defaultTen;
		BigDecimal taxableVal = toleValue.getTaxableVal() != null
				? toleValue.getTaxableVal()
				: defaultTen;
		BigDecimal totalTax = toleValue.getTotalTax() != null
				? toleValue.getTotalTax()
				: defaultTen;

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr2InitiateMatchingAPManualServiceImpl, "
							+ "toleranceValues :: igst - %s, cgst - %s, sgst- %s, "
							+ "cess - %s, taxableVal - %s , totalTax - %s ",
					igst.toString(), cgst.toString(), sgst.toString(),
					cess.toString(), taxableVal.toString(),
					totalTax.toString());
			LOGGER.debug(msg);
		}

		// fetching addln Reports
		List<String> addnReportList = addlReportRepo
				.getAddlnReportTypeList(configId);

		List<AllProcsDto> allProcList = getAllAPReconProcs();

		if (allProcList.isEmpty()) {

			String msg = String.format(
					"Config Id is '%s, TBL_AUTO_2APR_PROCEDURE table is empty "
							+ ": Hence updating as Recon Failed ",
					configId.toString());

			LOGGER.error(msg);

			// update config status
			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), configId);

			// update individual gstin status
			reconGstinRepo.updateStatusByConfigIdAndStatus(LocalDateTime.now(),
					configId, ReconStatusConstants.RECON_FAILED);

			throw new AppException(msg);

		}

		// check for isd selection
		if (!addnReportList.contains("ISD Matching Report")) {
			allProcList = allProcList.stream().filter(o -> !o.isIsdRecon())
					.collect(Collectors.toList());
		}

		List<AllProcsDto> procListWithGstinTrue = allProcList.stream()
				.filter(o -> o.isGstinLevel()).collect(Collectors.toList());

		List<AllProcsDto> procListWithGstinFalse = allProcList.stream()
				.filter(o -> !o.isGstinLevel()).collect(Collectors.toList());

		if (procListWithGstinTrue.isEmpty()
				|| procListWithGstinFalse.isEmpty()) {

			String msg = String.format(
					"Config Id is '%s, Sequence Id in TBL_AUTO_2APR_PROCEDURE "
							+ "table is null : Hence updating as Recon Failed ",
					configId.toString());

			LOGGER.error(msg);

			// update config status
			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), configId);

			// update individual gstin status
			reconGstinRepo.updateStatusByConfigIdAndStatus(LocalDateTime.now(),
					configId, ReconStatusConstants.RECON_FAILED);

			throw new AppException(msg);

		}

		Map<Integer, AllProcsDto> procWithGstinTrueMap = null;
		Map<Integer, AllProcsDto> procWithGstinFalseMap = null;

		try {
			/*
			 * procWithGstinFalseMap = procListWithGstinFalse.stream()
			 * .collect(Collectors.toMap(o -> o.getSequence(), o ->
			 * o.getProcName(), (o1, o2) -> o2, TreeMap::new));
			 */

			procWithGstinFalseMap = procListWithGstinFalse.stream()
					.collect(Collectors.toMap(o -> o.getSequence(), o -> o,
							(o1, o2) -> o2, TreeMap::new));

			procWithGstinTrueMap = procListWithGstinTrue.stream()
					.collect(Collectors.toMap(o -> o.getSequence(), o -> o,
							(o1, o2) -> o2, TreeMap::new));

			String procName = null;
			String response = null;
			// String gstinForStatus = null;

			try {
				for (String gstin : gstins) {

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("2APR AP manual, Gstin  {} ", gstin);
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"2APR AP manual, "
										+ "procWithGstinTrueMap keyset  {} ",
								procWithGstinTrueMap.keySet());
					}

					for (Integer k : procWithGstinTrueMap.keySet()) {
						AllProcsDto procedureTestEntity = procWithGstinTrueMap
								.get(k);
						procName = procedureTestEntity.getProcName();
						// procName = procWithGstinTrueMap.get(k);
						String reportName = procedureTestEntity.getReportName();

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("2APR AP manual gstin true, key {} ",
									k);
						}

						// List<Integer> ImpgSeqId = Arrays.asList(22,23,24, 25,
						// 26, 27, 28);

						// gstinForStatus = gstin;

						// chcking for optional reports DOC_NUM_MISMATCH_2
						if ((!reportName.equalsIgnoreCase("DOC_NUM_MISMATCH_2")
								|| (reportName
										.equalsIgnoreCase("DOC_NUM_MISMATCH_2")
										&& addnReportList.contains(
												"Doc No Mismatch II")))
								&& (!reportName.equalsIgnoreCase(
										"Doc No & Doc Date Mismatch")
										|| (reportName.equalsIgnoreCase(
												"Doc No & Doc Date Mismatch")
												&& addnReportList.contains(
														"Doc No & Doc Date Mismatch")))) {

							StoredProcedureQuery storedProc = procCall(cess,
									cgst, igst, sgst, taxableVal, totalTax,
									configId, procName, gstin, null);

							response = (String) storedProc.getSingleResult();
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"2APR AP manual, SP Name {}, "
												+ "configId {} response {}",
										procName, configId, response);
							}

							if (!ReconStatusConstants.SUCCESS
									.equalsIgnoreCase(response)) {

								String msg = String.format(
										"Config Id is '%s', Response "
												+ "from RECON_MASTER SP %s did not "
												+ "return success,"
												+ " Hence updating to Failed",
										configId.toString(), procName);
								LOGGER.error(msg);

								throw new AppException(msg);
							}
						}
					}

					// updating gstinDatils table
					reconGstinRepo.updateReconCompletedStatus(gstin, configId,
							ReconStatusConstants.RECON_COMPLETED,
							LocalDateTime.now());

					/*
					 * // submitting ERP push JOB autoReconId // null
					 * 
					 * boolean isRevIntg = isErpRevIntSelected(groupCode);
					 * 
					 * if (isRevIntg) {
					 * 
					 * LOGGER.debug("GroupCode {}, Gstin {}, ConfigId {}  " +
					 * "has configured for ERP Reverse Integration," +
					 * "hence invoking submitReverseIntgJob method ",
					 * TenantContext.getTenantId(), gstin, configId);
					 * 
					 * submitReverseIntgJob(configId, gstin, null); }
					 * 
					 * else {
					 * LOGGER.error("GroupCode {}, Gstin {}, ConfigId {}  " +
					 * "is not eligible for ERP reverse intg," +
					 * "hence trying SFTP push ", TenantContext.getTenantId(),
					 * gstin, configId);
					 * 
					 * submitSFTPJob(configId, gstin, null); }
					 */
				}
			} catch (Exception e) {

				String msg = String.format("Config Id is '%s', Response "
						+ "from RECON_MASTER SP %s did not return success,"
						+ " Hence updating to Failed", configId.toString(),
						procName);
				LOGGER.error(msg, e);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Block 1");
				}
				// update individual gstin status
				reconGstinRepo.updateStatusByConfigIdAndStatus(
						LocalDateTime.now(), configId,
						ReconStatusConstants.RECON_FAILED);

				throw new AppException(msg);
			}

			try {

				Integer patternLengthSize = getPatternLengthSize();

				for (Integer k : procWithGstinFalseMap.keySet()) {
					AllProcsDto procedureTestEntity = procWithGstinFalseMap
							.get(k);
					String reportName = procedureTestEntity.getReportName();
					// checking for optional 3 reports
					if ((!reportName.equalsIgnoreCase("Potential_1")
							|| (reportName.equalsIgnoreCase("Potential_1")
									&& addnReportList.contains("Potential-I")))
							&& (!reportName.equalsIgnoreCase("Potential_2")
									|| (reportName
											.equalsIgnoreCase("Potential_2")
											&& addnReportList.contains(
													"Potential-II")))) {
						StoredProcedureQuery storedProc = null;
						if (reportName.equalsIgnoreCase("LOGICAL MATCH")) {
							if (procedureTestEntity.getReportType()
									.equalsIgnoreCase("LOGICAL_INSERT")) {
								procName = procedureTestEntity.getProcName();
								if (LOGGER.isDebugEnabled()) {
									String msg = String.format(
											"Proc Execution Started for : "
													+ procName + " Report : "
													+ procedureTestEntity
															.getReportType());
									LOGGER.debug(msg);
								}
								storedProc = procCall(cess, cgst, igst, sgst,
										taxableVal, totalTax, configId,
										procName, null, null);
								ProcExecution(storedProc, procName, configId);
								if (LOGGER.isDebugEnabled()) {
									String msg = String.format(
											"Proc Execution Completed for : "
													+ procName + " Report : "
													+ procedureTestEntity
															.getReportType());
									LOGGER.debug(msg);
								}
							} else {
								String logicalPattern = null;
								procName = procedureTestEntity.getProcName();
								List<Gstr2Recon2BPRPatternEntity> pattern2BPREntityAllList = pattern2BPRRepo
										// hardCoded 20
										.fnByPatCntAndIsDelete(
												getPatternCount());

								List<Gstr2Recon2BPRPatternEntity> pattern2BPREntityList = pattern2BPREntityAllList
										.stream()
										.filter(s -> s.getPattern()
												.length() > patternLengthSize)
										.collect(Collectors.toList());

								if (procedureTestEntity.getReportType()
										.equalsIgnoreCase("LOGICAL_PATTERN")) {
									for (Gstr2Recon2BPRPatternEntity entity : pattern2BPREntityList) {
										if (entity.getFilterType()
												.equalsIgnoreCase("Y")) {
											logicalPattern = entity
													.getPattern();
											if (LOGGER.isDebugEnabled()) {
												String msg = String.format(
														"Proc Execution Started for : "
																+ procName
																+ " Report : "
																+ procedureTestEntity
																		.getReportType());
												LOGGER.debug(msg);
											}
											storedProc = procCall(cess, cgst,
													igst, sgst, taxableVal,
													totalTax, configId,
													procName, null,
													logicalPattern);
											ProcExecution(storedProc, procName,
													configId);
											if (LOGGER.isDebugEnabled()) {
												String msg = String.format(
														"Proc Execution Completed for : "
																+ procName
																+ " Report : "
																+ procedureTestEntity
																		.getReportType());
												LOGGER.debug(msg);
											}
										}
									}
								} else if (procedureTestEntity.getReportType()
										.equalsIgnoreCase(
												"LOGICAL_DOC_COUNT")) {
									for (Gstr2Recon2BPRPatternEntity entity : pattern2BPREntityList) {
										if (entity.getFilterType()
												.equalsIgnoreCase("N")) {
											logicalPattern = entity
													.getPattern();
											if (LOGGER.isDebugEnabled()) {
												String msg = String.format(
														"Proc Execution Started for : "
																+ procName
																+ " Report : "
																+ procedureTestEntity
																		.getReportType());
												LOGGER.debug(msg);
											}
											storedProc = procCall(cess, cgst,
													igst, sgst, taxableVal,
													totalTax, configId,
													procName, null,
													logicalPattern);
											ProcExecution(storedProc, procName,
													configId);
											if (LOGGER.isDebugEnabled()) {
												String msg = String.format(
														"Proc Execution Completed for : "
																+ procName
																+ " Report : "
																+ procedureTestEntity
																		.getReportType());
												LOGGER.debug(msg);
											}
										}
									}
								}
							}
						} else {
							procName = procedureTestEntity.getProcName();
							if (LOGGER.isDebugEnabled()) {
								String msg = String
										.format("Proc Execution Started for : "
												+ procName);
								LOGGER.debug(msg);
							}
							storedProc = procCall(cess, cgst, igst, sgst,
									taxableVal, totalTax, configId, procName,
									null, null);
							ProcExecution(storedProc, procName, configId);
							if (LOGGER.isDebugEnabled()) {
								String msg = String.format(
										"Proc Execution Completed for : "
												+ procName);
								LOGGER.debug(msg);
							}
						}
					}
				}

			} catch (Exception e) {
				String msg = String.format("Config Id is '%s', Response "
						+ "from RECON_MASTER SP %s did not " + "return success,"
						+ " Hence updating to Failed", configId.toString(),
						procName);
				LOGGER.error(msg, e);

				// calling it for ERP reverse Integration fix
				// Task 116053
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Block 2");
				}
				//update all gstins status to failed 
				reconGstinRepo.updateStatusByConfigIdAndStatus(
						LocalDateTime.now(), configId,
						ReconStatusConstants.RECON_FAILED);

				throw new AppException(msg);

			}

			/**
			 * After All the proc execution need to call RevInt and SFTP for PAN
			 * level.
			 */
			for (String gstin : gstins) {

				// submitting ERP push JOB autoReconId // null

				boolean isRevIntg = isErpRevIntSelected(groupCode);

				if (!isRevIntg) {

					LOGGER.error(
							"GroupCode {}, Gstin {}, ConfigId {}  "
									+ "is not eligible for ERP reverse intg,"
									+ "hence trying SFTP push ",
							TenantContext.getTenantId(), gstin, configId);

					submitSFTPJob(configId, gstin, null);
				}

			}

			// report Generation
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_RECON_REPORT_GENERATE,
					jsonParams.toString(), "SYSTEM", 50L, null, null);
			// fetchReportDetails.generateReport(configId, true, entityId);

		} catch (Exception ex) {

			LOGGER.error("Exception occured during Iniiate Maching {} ", ex);

			// calling it for ERP reverse Integration fix
			// Task 116053-
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Block 3");
			}
			try {
				callFailedProc(configId);
			} catch (Exception e) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Failed Exception C3");
				}

				String msg1 = String.format(
						"Config Id is '%s', Failed Proc with exception '%s'",
						configId.toString(), ex);
				LOGGER.error(msg1, ex);

				// update config status
				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_HALT, null,
						LocalDateTime.now(), configId);

				failedBatAltUtility.prepareAndTriggerAlert(
						String.valueOf(configId), "RECON", String.format(
								" RECON_HALT FOUND AT PREVIOUS CONFIG LEVEL"));

			}

			// throw new AppException(ex);

		}

	}

	private List<AllProcsDto> getAllAPReconProcs() {
		try {
			String queryStr = "SELECT PROCEDURE_NAME,AP_MANUAL_SEQUENCE, "
					+ " IS_GSTIN_LEVEL,REPORT_NAME,REPORT_TYPE,IS_ISD_RECON"
					+ " FROM TBL_AUTO_2APR_PROCEDURE WHERE "
					+ " AP_MANUAL_SEQUENCE IS NOT NULL  "
					+ " ORDER BY AP_MANUAL_SEQUENCE";
			Query q = entityManager.createNativeQuery(queryStr);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to getAllAPReconProcs {}",
						queryStr);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<AllProcsDto> retList = list.stream().map(o -> conertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ee) {
			String msg = "Exception while fetching getAllAPReconProcs "
					+ "proc names and sequence";
			LOGGER.error(msg, ee);
			throw new AppException(msg);
		}
	}

	private AllProcsDto conertToDto(Object[] o) {
		AllProcsDto dto = new AllProcsDto();
		try {
			dto.setSequence((Integer) o[1]);
			dto.setProcName((String) o[0]);
			dto.setGstinLevel((boolean) o[2]);
			dto.setReportName((String) o[3]);
			dto.setReportType((String) o[4]);
			dto.setIsdRecon((boolean) o[5]);
		} catch (Exception ex) {
			String msg = String.format(
					"Error while setting Procedure name {object}:: %s ", o);
			LOGGER.error(msg, ex);
		}
		return dto;
	}

	private List<Gstr2ToleranceValueDto> getToleranceValue(Long cofigId) {
		List<Gstr2ToleranceValueDto> retList = null;
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
				LOGGER.debug(
						"executing query to getToleranceValue :: configId {}",
						cofigId);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			retList = list.stream().map(o -> convertUserInput(o))
					.collect(Collectors.toCollection(ArrayList::new));

		} catch (Exception ex) {
			String msg = String.format("Error occured in "
					+ "Gstr2InitiateMatchingAPManualServiceImpl"
					+ ".getToleranceValue", ex);
			LOGGER.error(msg);
		}
		return retList;
	}

	private static Gstr2ToleranceValueDto convertUserInput(Object[] o) {

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

	/**
	 * @param cess
	 * @param cgst
	 * @param igst
	 * @param sgst
	 * @param taxableVal
	 * @param totalTax
	 * @param configId
	 * @param procName
	 * @return
	 */
	private StoredProcedureQuery procCall(BigDecimal cess, BigDecimal cgst,
			BigDecimal igst, BigDecimal sgst, BigDecimal taxableVal,
			BigDecimal totalTax, Long configId, String procName, String gstin,
			String pattern) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Recon SP with ConfigId :%s",
					configId.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_RECON_REPORT_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

		if (gstin != null) {

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

		if (pattern != null) {
			storedProc.registerStoredProcedureParameter("VAR_PATTERN",
					String.class, ParameterMode.IN);

			storedProc.setParameter("VAR_PATTERN", pattern);
		}
		return storedProc;
	}

	private boolean submitSFTPJob(Long configId, String gstin,
			Long autoReconId) {

		/*
		 * Check for SFTP Push on-board
		 */
		Long scenarioId = scenarioMasterRepo.findSceIdOnScenarioName(
				JobConstants.GSTR2A_AUTO_RECON_SFTP_PUSH);

		if (scenarioId == null) {

			LOGGER.error("Scenario {} is not configured for group {},"
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
	 * autoReconId) {
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
	 * LOGGER.error("Scenario {} is not configured for group {}," +
	 * "Hence reverse integartion job is not posted for {}",
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, TenantContext.getTenantId(),
	 * gstin);
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
	 * LOGGER.error("sourceId {} is not configured for group {}," +
	 * "Hence reverse integartion job is not posted for {}",
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, TenantContext.getTenantId(),
	 * gstin);
	 * 
	 * return false; }
	 * 
	 * Long erpId = erpInfoEntityRepository.getErpId(sourceId);
	 * 
	 * if (erpId == null) {
	 * 
	 * LOGGER.error("erpId {} is not configured for group {}," +
	 * "Hence reverse integartion job is not posted for {}",
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, TenantContext.getTenantId(),
	 * gstin);
	 * 
	 * return false; }
	 * 
	 * GSTNDetailEntity gstinInfo = gstinDetailRepo
	 * .findByGstinAndIsDeleteFalse(gstin); Long gstinId = gstinInfo.getId();
	 * List<ErpScenarioPermissionEntity> scenarioPermisionList =
	 * erpScenPermissionRepo
	 * .findByScenarioIdAndGstinIdAndIsDeleteFalse(scenarioId, gstinId); if
	 * (!scenarioPermisionList.isEmpty()) {
	 * 
	 * LOGGER.error( "Scenario permission {} is not configured for group {}," +
	 * "Hence reverse integartion job is " + "not posted for {}",
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, TenantContext.getTenantId(),
	 * gstin);
	 * 
	 * return false; }
	 * 
	 * 
	 * Submit the Async job only if the Reverse integration is on-boarded -
	 * Client level
	 * 
	 * 
	 * if (LOGGER.isDebugEnabled()) { String msg = String
	 * .format("Submitting reverse Intg Job for  GSTIN :%s," +
	 * "and autoReconId :%d", gstin, configId); LOGGER.debug(msg); }
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
	 * erpReqDto.setDestinationType("SAP"); String erpReqJson =
	 * gson.toJson(erpReqDto);
	 * 
	 * asyncJobsService.createJob(TenantContext.getTenantId(),
	 * JobConstants.GSTR2A_AUTO_RECON_REV_INTG, erpReqJson, JobConstants.SYSTEM,
	 * JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
	 * JobConstants.SCHEDULE_AFTER_IN_MINS);
	 * 
	 * } } return true; } catch (Exception ex) { String msg = String.format(
	 * "Exception while submitting reverse Intg Job for " +
	 * " GSTIN :%s,and autoReconId :%d", gstin, configId); LOGGER.error(msg,
	 * ex); throw new AppException(msg, ex); } }
	 */

	private void callFailedProc(Long configId) {

		String procName = null;
		String response = null;
		try {
			List<Gstr2ReconFailedProcedureEntity> procList = procRepo
					.findProcedure();

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

				StoredProcedureQuery storedProc = procCallReconFailed(configId,
						procName);

				response = (String) storedProc.getSingleResult();

				LOGGER.debug(procName + " :: " + response);

				if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {

					String msg = String.format("Config Id is '%s', Response "
							+ "from RECON_FAILED PROC %s did not "
							+ "return success," + " Hence updating to Failed",
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

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), configId);

		} catch (Exception ex) {
			LOGGER.error(
					" Failed in failure proc at pan level for config id is {} with the exception {} ",
					configId, ex);

			throw new AppException(ex);

			/*
			 * reconConfigRepo.updateReconConfigStatus("RECON_HALT", configId);
			 * failedBatAltUtility.prepareAndTriggerAlert(String.valueOf(
			 * configId),
			 * "AUTO_RECON",String.format(" RECON_HALT FOUND AT PAN LEVEL"));
			 * 
			 * 
			 */ }

	}

	private StoredProcedureQuery procCallReconFailed(Long configId,
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

	private void ProcExecution(StoredProcedureQuery storedProc, String procName,
			Long configId) {
		try {
			String response = (String) storedProc.getSingleResult();
			LOGGER.debug(procName + " :: " + response);

			if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {

				String msg = String.format("Config Id is '%s', Response "
						+ "from RECON_MASTER SP %s did not " + "return success,"
						+ " Hence updating to Failed", configId.toString(),
						procName);
				LOGGER.error(msg);

				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_FAILED, null,
						LocalDateTime.now(), configId);

				throw new AppException(msg);
			}
		} catch (Exception e) {
			String msg = String.format(
					"Config Id is '%s', Response "
							+ "from RECON_MASTER SP %s did not "
							+ "return success," + " Hence updating to Failed",
					configId.toString(), procName);
			LOGGER.error(msg, e);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), configId);

			throw new AppException(e);
		}
	}

	private Long getPatternCount() {

		Config config = configManager.getConfig(CONF_CATEG1, CONF_KEY2);
		String chunkSize = config.getValue();
		return (Long.valueOf(chunkSize));
	}

	private Integer getPatternLengthSize() {

		Config config = configManager.getConfig(CONF_CATEG1, CONF_KEY1);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
	}
}
