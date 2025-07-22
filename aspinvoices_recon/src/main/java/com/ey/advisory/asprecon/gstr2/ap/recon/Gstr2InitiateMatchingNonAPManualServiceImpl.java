/**
 * 
 */
package com.ey.advisory.asprecon.gstr2.ap.recon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2ToleranceValueDto;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRPatternEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRPatternRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.services.asprecon.gstr2a.autorecon.AllProcsDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2InitiateMatchingNonAPManualServiceImpl")
public class Gstr2InitiateMatchingNonAPManualServiceImpl
		implements Gstr2InitiateMatchingNonAPManualService {

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2Recon2BPRPatternRepository")
	Gstr2Recon2BPRPatternRepository pattern2BPRRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	private static String msg = null;

	private static final String CONF_KEY1 = "gstr2.recon.report.pattern.length.size";
	private static final String CONF_CATEG1 = "RECON_REPORTS";

	private static final String CONF_KEY2 = "gstr2.recon.report.pattern.count";

	@Override
	public void executeNonAPManualRecon(Long configId, Boolean apFlag,
			List<String> gstins) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin, Gstr2InitiateMatchingNonAPManualServiceImpl. "
							+ "executeNonAPManualRecon() configId %s,"
							+ " gstins %s apFlag %s :",
					configId.toString(), gstins, apFlag);
			LOGGER.debug(msg);
		}

		BigDecimal cess = null;
		BigDecimal cgst = null;
		BigDecimal igst = null;
		BigDecimal sgst = null;
		BigDecimal taxableVal = null;
		BigDecimal totalTax = null;

		List<Gstr2ToleranceValueDto> toleranceValue = getToleranceValue(
				entityManager, configId);

		for (Gstr2ToleranceValueDto toleValue : toleranceValue) {
			cess = toleValue.getCess() != null
					? toleValue.getCess()
					: BigDecimal.TEN;
			cgst = toleValue.getCgst() != null
					? toleValue.getCgst()
					: BigDecimal.TEN;
			igst = toleValue.getIgst() != null
					? toleValue.getIgst()
					: BigDecimal.TEN;
			sgst = toleValue.getSgst() != null
					? toleValue.getSgst()
					: BigDecimal.TEN;
			taxableVal = toleValue.getTaxableVal() != null
					? toleValue.getTaxableVal()
					: BigDecimal.TEN;
			totalTax = toleValue.getTotalTax() != null
					? toleValue.getTotalTax()
					: BigDecimal.TEN;

		}

		if (LOGGER.isDebugEnabled()) {
			msg = String.format("Gstr2InitiateMatchingNonAPManualServiceImpl, "
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

		Optional<Gstr2ReconConfigEntity> entity = reconConfigRepo
				.findById(configId);

		Long entityId = entity.get().getEntityId();

		List<AllProcsDto> allProcList = getAllProcs();

		// check for isd selection
		if (!addnReportList.contains("ISD Matching Report")) {
			allProcList = allProcList.stream().filter(o -> !o.isIsdRecon())
					.collect(Collectors.toList());
		}

		List<AllProcsDto> procListWithGstinTrue = allProcList.stream()
				.filter(o -> o.isGstinLevel()).collect(Collectors.toList());

		List<AllProcsDto> procListWithGstinFalse = allProcList.stream()
				.filter(o -> !o.isGstinLevel()).collect(Collectors.toList());

		Map<Integer, AllProcsDto> procWithGstinTrueMap = null;
		Map<Integer, AllProcsDto> procWithGstinFalseMap = null;

		try {
			procWithGstinFalseMap = procListWithGstinFalse.stream()
					.collect(Collectors.toMap(o -> o.getSequence(), o -> o,
							(o1, o2) -> o2, TreeMap::new));

			procWithGstinTrueMap = procListWithGstinTrue.stream()
					.collect(Collectors.toMap(o -> o.getSequence(), o -> o,
							(o1, o2) -> o2, TreeMap::new));
			String procName = null;
			String response = null;

			try {
				for (String gstin : gstins) {

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("2APR NON AP manual, Gstin  {} ", gstin);
					}

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("2APR NON AP manual, keyset  {} ",
								procWithGstinTrueMap.keySet());
					}

					for (Integer k : procWithGstinTrueMap.keySet()) {
						AllProcsDto procedureTestEntity = procWithGstinTrueMap
								.get(k);
						procName = procedureTestEntity.getProcName();
						String reportName = procedureTestEntity.getReportName();
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("2APR NON AP manual, key {} ", k);
						}

						// List<Integer> ImpgSeqId = Arrays.asList(20, 21, 22,
						// 23, 24);

						// chcking for optional reports
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
									configId, procName, gstin);

							response = (String) storedProc.getSingleResult();

							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(
										"2APR NON AP manual, SP Name {}, "
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

								reconConfigRepo
										.updateReconConfigStatusAndReportName(
												ReconStatusConstants.RECON_FAILED,
												null,
												EYDateUtil
														.toUTCDateTimeFromLocal(
																LocalDateTime
																		.now()),
												configId);

								throw new AppException(msg);
							}

						}
					}
				}

				// isGstinLevel - False

				Integer patternLengthSize = getPatternLengthSize();

				for (Integer k : procWithGstinFalseMap.keySet()) {
					AllProcsDto procedureTestEntity = procWithGstinFalseMap
							.get(k);
					String reportName = procedureTestEntity.getReportName();

					// chcking for optional reports
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
								List<Gstr2Recon2BPRPatternEntity> 
								pattern2BPREntityAllList = pattern2BPRRepo
										// hardCoded 20
										.fnByPatCntAndIsDelete(
												getPatternCount());

								List<Gstr2Recon2BPRPatternEntity> 
								pattern2BPREntityList = pattern2BPREntityAllList
										.stream()
										.filter(s -> s.getPattern()
												.length() > patternLengthSize)
										.collect(Collectors.toList());

								if (procedureTestEntity.getReportType()
										.equalsIgnoreCase("LOGICAL_PATTERN")) {
									for (Gstr2Recon2BPRPatternEntity entity2bpr 
											: pattern2BPREntityList) {
										if (entity2bpr.getFilterType()
												.equalsIgnoreCase("Y")) {
											logicalPattern = entity2bpr
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
									for (Gstr2Recon2BPRPatternEntity 
											entity2bprDoc : pattern2BPREntityList) {
										if (entity2bprDoc.getFilterType()
												.equalsIgnoreCase("N")) {
											logicalPattern = entity2bprDoc
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
				msg = String.format("Config Id is '%s', Response "
						+ "from RECON_MASTER SP %s did not " + "return success,"
						+ " Hence updating to Failed", configId.toString(),
						procName);
				LOGGER.error(msg, e);

				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_FAILED, null,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						configId);

				throw new AppException(e);
			}

			// report Generation job submission -
			// Gstr2NonApManualGenerateReportServiceImpl

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);
			jsonParams.addProperty("apFlag", apFlag);
			jsonParams.addProperty("entityId", entityId);

			String groupCode = TenantContext.getTenantId();

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_RECON_REPORT_GENERATE,
					jsonParams.toString(), "SYSTEM", 50L, null, null);

			// fetchReportDetails.generateReport(configId);

		} catch (Exception ex) {

			ex.printStackTrace();

			LOGGER.error("Exception occured during initiate Recon in proc {}",
					ex);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId);

			throw new AppException(ex);

		}

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
			BigDecimal totalTax, Long configId, String procName, String gstin) {

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
		return storedProc;
	}

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

	private static List<Gstr2ToleranceValueDto> getToleranceValue(
			EntityManager entityManager, Long cofigId) {
		
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
			LOGGER.debug("executing query to getToleranceValue :: configId {}",
					cofigId);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		 retList = list.stream()
				.map(o -> convertUserInput(o))
				.collect(Collectors.toCollection(ArrayList::new));
		} catch (Exception ex) {
			String msg = String.format("Error occured in "
					+ "Gstr2InitiateMatchingNonAPManualServiceImpl"
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

	private List<AllProcsDto> getAllProcs() {
		try {
			String queryStr = "SELECT PROCEDURE_NAME,NON_AP_MANUAL_SEQUENCE, "
					+ " IS_GSTIN_LEVEL,REPORT_NAME,REPORT_TYPE, IS_ISD_RECON"
					+ " FROM TBL_AUTO_2APR_PROCEDURE WHERE "
					+ " NON_AP_MANUAL_SEQUENCE IS NOT NULL  "
					+ " ORDER BY NON_AP_MANUAL_SEQUENCE";
			Query q = entityManager.createNativeQuery(queryStr);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to getAllProcs {} : ", queryStr);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			List<AllProcsDto> retList = list.stream().map(o -> conertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			return retList;
		} catch (Exception ee) {
			String msg = String.format(
					"Exception while fetching proc names " + "and sequence %s",
					ee);
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
