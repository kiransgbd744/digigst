package com.ey.advisory.app.services.asprecon.gstr2a.autorecon;

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

import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.admin.data.repositories.client.SftpScenarioPermissionRepository;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2ToleranceValueDto;
import com.ey.advisory.app.data.entities.client.asprecon.AutoRecon2APROnBoardingEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRPatternEntity;
import com.ey.advisory.app.data.repositories.client.AutoReconStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstinSourceInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2AERPRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.AutoRecon2APROnBoardingRepo;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRPatternRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("Gstr2aAutoInitiateReconPanLevelServiceImpl")
public class Gstr2aAutoInitiateReconPanLevelServiceImpl
		implements Gstr2aAutoInitiateReconPanLevelService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("AutoReconStatusRepository")
	private AutoReconStatusRepository autoReconStatusRepo;

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
	@Qualifier("Gstr2Recon2BPRPatternRepository")
	Gstr2Recon2BPRPatternRepository pattern2BPRRepo;

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
	
	@Override
	public void initiateAutoReconPanLevel(Long entityId,
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
						"Calling initiateAutoReconPanLevel for , entityId :{}"
								+ " and configId :%s",
					 entityId,configId);
				LOGGER.debug(msg);
			}
			// update AUTO_2A_2B_RECON_STATUS as initiated
			

			reconConfigRepo.updateReconConfigStatus("PAN_RECON_INPROGRESS", configId);
			
			List<Gstr2ToleranceValueDto> toleranceValue = getToleranceValue(configId);

			cess = checkAndSetDefaultValue(toleranceValue.get(0).getCess());
			cgst = checkAndSetDefaultValue(toleranceValue.get(0).getCgst());
			igst = checkAndSetDefaultValue(toleranceValue.get(0).getIgst());
			sgst = checkAndSetDefaultValue(toleranceValue.get(0).getSgst());
			taxableVal = checkAndSetDefaultValue(
					toleranceValue.get(0).getTaxableVal());
			totalTax = checkAndSetDefaultValue(
					toleranceValue.get(0).getTotalTax());

			// getall storedprocs
			List<AllProcsDto> allProcList = getAllProcs();

			
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.debug("allProcList pan level : {}" + allProcList);
			}
		
			List<AllProcsDto> procListWithGstinFalse = allProcList.stream()
					.filter(o -> !o.isGstinLevel()).collect(Collectors.toList());
			
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.debug("procListWithGstinFalse pan level : {}" + procListWithGstinFalse);
			}
			Map<Integer, AllProcsDto> procWithGstinFalseMap = procListWithGstinFalse.stream().collect(Collectors.toMap(o -> o.getSequence(), o -> o,
							(o1, o2) -> o2, TreeMap::new));
			
			if(LOGGER.isDebugEnabled())
			{
				LOGGER.debug("procWithGstinFalseMap pan level : {}" + procWithGstinFalseMap);
			}
			String procName = null;
			List<AutoRecon2APROnBoardingEntity> selfOnboardingEntities = autoRecon2APROnBoardingRepo
					.findByEntityIdAndIsActiveTrue(entityId.toString());

			List<String> addnReportList = new ArrayList<>();
			
			
			for (AutoRecon2APROnBoardingEntity selfOnboardingEntity 
					: selfOnboardingEntities) {
				if(LOGGER.isDebugEnabled())
				{
					LOGGER.debug("selfOnboardingEntity pan level : {}" + selfOnboardingEntity);
				}

				if ("Potential-II".equalsIgnoreCase(
						selfOnboardingEntity.getReportType())) {

					if (selfOnboardingEntity.getOptionalReportSected()) {
						addnReportList.add("Potential-II");
					}

				} if ("Potential-I".equalsIgnoreCase(
						selfOnboardingEntity.getReportType())) {

					if (selfOnboardingEntity.getOptionalReportSected()) {
						addnReportList.add("Potential-I");
					}

				} if ("Logical Match".equalsIgnoreCase(
						selfOnboardingEntity.getReportType())) {

					if (selfOnboardingEntity.getOptionalReportSected()) {
						addnReportList.add("Logical Match");
					}

				}
			}


			for (Integer k : procWithGstinFalseMap.keySet()) {
				AllProcsDto procedureTestEntity = procWithGstinFalseMap
						.get(k);
				
				if(LOGGER.isDebugEnabled())
				{
					LOGGER.debug("procedureTestEntity incide loop pan level : {}" + procedureTestEntity);
				}
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
							List<Gstr2Recon2BPRPatternEntity> pattern2BPREntityList = pattern2BPRRepo
									//hardCoded 20
									.fnByPatCntAndIsDelete(20L);
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
			
			reconConfigRepo.updateReconConfigStatus("PAN_RECON_COMPLETED", configId);

		} catch (Exception ex) {
	String msg = "Exception occured during Gstr2a auto pan level initiate Recon";
			LOGGER.error(msg, ex);
			
			reconConfigRepo.updateReconConfigStatus("PAN_RECON_FAILED", configId);
			/*
			 * autoReconStatusRepo.updateAutoReconStatusOnSuccessOrFailure(
			 * "FAILED", LocalDateTime.now(), autoReconId);
			 * reconGstinRepo.updateStatus(gstin, LocalDate.now(),
			 * "RECON_FAILED");
			 */ throw new AppException(msg, ex);
		}
	}

	private List<Gstr2ToleranceValueDto> getToleranceValue(Long configId) {
		try {
			String queryStr ="SELECT  SUM((IFNULL((CASE  "
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
					+ "     AND  RECON_REPORT_CONFIG_ID=:configId";
			Query q = entityManager.createNativeQuery(queryStr);

			q.setParameter("configId", configId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to getToleranceValue :: configId "
						+ configId);
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

	private BigDecimal checkAndSetDefaultValue(BigDecimal obj) {

		return (obj != null) ? obj : BigDecimal.TEN;
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

}