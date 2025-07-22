/**
 * 
 */
package com.ey.advisory.processors.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2InitiateReconFetchReportDetails;
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Gstr2ToleranceValueDto;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRPatternEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvoiceReconErrorLogEntity;
import com.ey.advisory.app.data.entities.client.asprecon.InwardEinvoiceReconProcedureEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRPatternRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconProcedureRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceReconErrorLogsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.InwardEinvoiceReconProcedureRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */

@Slf4j
@Component("InwardEinvoiceInitiateReconMatchingProcessor")
public class InwardEinvoiceInitiateReconMatchingProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Autowired
	@Qualifier("Gstr2ReconAddlReportsRepository")
	Gstr2ReconAddlReportsRepository addlReportRepo;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("Gstr2ReconProcedureRepository")
	Gstr2ReconProcedureRepository procRepo;


	@Autowired
	@Qualifier("InwardEinvoiceReconProcedureRepository")
	InwardEinvoiceReconProcedureRepository procInwardEinvoiceRRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRPatternRepository")
	Gstr2Recon2BPRPatternRepository pattern2BPRRepo;

	@Autowired
	@Qualifier("InwardEinvoiceReconAddlReportsRepository")
	InwardEinvoiceReconAddlReportsRepository addlInwardEinvReportRepo;

	@Autowired
	@Qualifier("Gstr2InitiateReconFetchReportDetailsImpl")
	Gstr2InitiateReconFetchReportDetails fetchReportDetails;

	@Autowired
	@Qualifier("InwardEinvoiceReconErrorLogsRepository")
	InwardEinvoiceReconErrorLogsRepository errorRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	private static final String CONF_KEY1 = "gstr2.recon.report.pattern.length.size";
	private static final String CONF_CATEG = "RECON_REPORTS";

	private static final String CONF_KEY2 = "gstr2.recon.report.pattern.count";

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin InwardEinvoiceInitiateReconMatchingProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();

		Optional<Gstr2ReconConfigEntity> entity = reconConfigRepo
				.findById(configId);

		entity.ifPresent(configEntity -> {
		    Long entityId = configEntity.getEntityId();
		    executeInwardEinvoiceVsPr(configId, entityId);
		});

		
	}

	private void executeInwardEinvoiceVsPr(Long configId, Long entityId) {

		reconConfigRepo.updateReconConfigStatusAndReportName(
				ReconStatusConstants.RECON_INPROGRESS, null,
				LocalDateTime.now(), configId);
		
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
		// fetching addln 2BPR Reports

		try {

			List<String> addnReportList = addlInwardEinvReportRepo
					.getAddlnReportTypeList(configId);

			List<Long> entityIds = new ArrayList<>();
			entityIds.add(entityId);
			
			//Inward Einvoice Check
			List<Long> optedEntities = entityConfigPemtRepo
					.findEntitiesOptedInwardEinvoice(entityIds);

			boolean flag = false;
			if (optedEntities != null && !optedEntities.isEmpty()) {

				flag = true;
			}

			List<InwardEinvoiceReconProcedureEntity> allProcList = procInwardEinvoiceRRepo
						.findAll();
				
				allProcList = allProcList.stream().filter(o -> o.getIsActive())
						.collect(Collectors.toList());
	
				Integer patternLengthSize = getPatternLengthSize();

				if (allProcList.isEmpty()) {

					String msg = String.format("Procedure Table is empty : "
							+ "Config Id is '%s' Hence updating to Failed",
							configId.toString());
					LOGGER.error(msg);

					reconConfigRepo.updateReconConfigStatusAndReportName(
							ReconStatusConstants.RECON_FAILED, null,
							LocalDateTime.now(), configId);

					InwardEinvoiceReconErrorLogEntity obj = new InwardEinvoiceReconErrorLogEntity();

					obj.setConfigId(configId);
					obj.setProcName(null);
					obj.setErrMessage(GenUtil.convertStringToClob(msg));
					obj.setCreatedDate(LocalDateTime.now());
					errorRepo.save(obj);

					throw new AppException(msg);

				}

				Map<Integer, InwardEinvoiceReconProcedureEntity> reportMap = new TreeMap<>();

				reportMap = allProcList.stream()
						.collect(Collectors.toMap(o -> o.getSeqId(), o -> o));

				String reportName = null;
				String procName = null;
				try {
					for (Integer k : reportMap.keySet()) {
						InwardEinvoiceReconProcedureEntity procedureTestEntity = reportMap
								.get(k);
						reportName = procedureTestEntity.getReportName();
						// checking for optional 3 reports
						if ((!reportName.equalsIgnoreCase("DOC_NUM_MISMATCH_2")
								|| (reportName
										.equalsIgnoreCase("DOC_NUM_MISMATCH_2")
										&& addnReportList.contains(
												"Doc No Mismatch II")))
								&& (!reportName.equalsIgnoreCase("POTENTIAL_2")
										|| (reportName
												.equalsIgnoreCase("POTENTIAL_2")
												&& addnReportList.contains(
														"Potential-II")))
								&& (!reportName.equalsIgnoreCase("LOGICAL")
										|| (reportName
												.equalsIgnoreCase("LOGICAL")
												&& addnReportList.contains(
														"Logical Match")))
								&& (!reportName.equalsIgnoreCase("POTENTIAL_1")
										|| (reportName
												.equalsIgnoreCase("POTENTIAL_1")
												&& addnReportList.contains(
														"Potential-I")))
								&& (!reportName.equalsIgnoreCase(
										"Doc No & Doc Date Mismatch")
										|| (reportName.equalsIgnoreCase(
												"Doc No & Doc Date Mismatch")
												&& addnReportList.contains(
														"Doc No & Doc Date Mismatch")))
								
								) {
							
							StoredProcedureQuery storedProc = null;
							if (reportName.equalsIgnoreCase("LOGICAL") 
									|| reportName.equalsIgnoreCase("ISD:LOGICAL")) {
								
								procName = logicalReconExecution(configId,
										cess, cgst, igst, sgst, taxableVal,
										totalTax, patternLengthSize,
										procedureTestEntity);
							} 
							else {
								procName = procedureTestEntity.getProcName();
								if (LOGGER.isDebugEnabled()) {
									String msg = String.format(
											"Proc Execution Started for : "
													+ procName);
									LOGGER.debug(msg);
								}
								storedProc = procCall(cess, cgst, igst, sgst,
										taxableVal, totalTax, configId,
										procName, null);
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
							+ "from RECON_MASTER SP %s did not "
							+ "return success," + " Hence updating to Failed",
							configId.toString(), procName);
					LOGGER.error(msg, e);

					reconConfigRepo.updateReconConfigStatusAndReportName(
							ReconStatusConstants.RECON_FAILED, null,
							LocalDateTime.now(), configId);

					// Deleteing Temp data
					callDeleteTempDataProc(configId);
					
					InwardEinvoiceReconErrorLogEntity obj = new InwardEinvoiceReconErrorLogEntity();

					obj.setConfigId(configId);
					obj.setProcName(procName);
					obj.setErrMessage(GenUtil.convertStringToClob(
							msg + " ----> " + e.toString()));
					obj.setCreatedDate(LocalDateTime.now());
					errorRepo.save(obj);
					throw new AppException(e);
				}

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();

			asyncJobsService.createJob(groupCode,
					JobConstants.EINV_RECON_REPORT_GENERATE,
					jsonParams.toString(), "SYSTEM", 50L, null, null);

		} catch (Exception ex) {

			LOGGER.error("Exception occured during 2BPR initiate Recon in proc",
					ex);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), configId);

			// Deleteing Temp data
			callDeleteTempDataProc(configId);
			InwardEinvoiceReconErrorLogEntity obj = new InwardEinvoiceReconErrorLogEntity();

			obj.setConfigId(configId);
			obj.setProcName(null);
			obj.setErrMessage(GenUtil.convertStringToClob(ex.toString()));
			obj.setCreatedDate(LocalDateTime.now());
			errorRepo.save(obj);
			throw new AppException(ex);

		}
	}

	/**
	 * @param configId
	 * @param cess
	 * @param cgst
	 * @param igst
	 * @param sgst
	 * @param taxableVal
	 * @param totalTax
	 * @param patternLengthSize
	 * @param procedureTestEntity
	 * @return
	 */
	private String logicalReconExecution(Long configId, BigDecimal cess,
			BigDecimal cgst, BigDecimal igst, BigDecimal sgst,
			BigDecimal taxableVal, BigDecimal totalTax,
			Integer patternLengthSize,
			InwardEinvoiceReconProcedureEntity procedureTestEntity) {
		String procName;
		StoredProcedureQuery storedProc;
		if (procedureTestEntity.getReportType()
				.equalsIgnoreCase("LOGICAL_INSERT")) {
			procName = procedureTestEntity
					.getProcName();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Proc Execution Started for : "
								+ procName
								+ " Report : "
								+ procedureTestEntity
										.getReportType());
				LOGGER.debug(msg);
			}
			storedProc = procCall(cess, cgst, igst,
					sgst, taxableVal, totalTax,
					configId, procName, null);
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
		} else {
			String logicalPattern = null;
			procName = procedureTestEntity
					.getProcName();
			List<Gstr2Recon2BPRPatternEntity> pattern2BPRAllEntityList = pattern2BPRRepo
					.fnByPatCntAndIsDelete(
							getPatternCount());

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"pattern2BPRAllEntityList: "
								+ pattern2BPRAllEntityList);
				LOGGER.debug(msg);
			}
			List<Gstr2Recon2BPRPatternEntity> pattern2BPREntityList = pattern2BPRAllEntityList
					.stream()
					.filter(s -> s.getPattern()
							.length() > patternLengthSize)
					.collect(Collectors.toList());
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"pattern2BPREntityList - after length filter: "
								+ pattern2BPREntityList);
				LOGGER.debug(msg);
			}

			if (procedureTestEntity.getReportType()
					.equalsIgnoreCase(
							"LOGICAL_PATTERN")) {
				for (Gstr2Recon2BPRPatternEntity entity : pattern2BPREntityList) {
					if (entity.getFilterType()
							.equalsIgnoreCase("Y")) {
						logicalPattern = entity
								.getPattern();
						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Proc Execution Started for: "
											+ procName
											+ " Report:"
											+ procedureTestEntity
													.getReportType(),
									"Logical Patten -> "
											+ logicalPattern);
							LOGGER.debug(msg);
						}
						storedProc = procCall(cess,
								cgst, igst, sgst,
								taxableVal, totalTax,
								configId, procName,
								logicalPattern);
						ProcExecution(storedProc,
								procName, configId);
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
			} else if (procedureTestEntity
					.getReportType().equalsIgnoreCase(
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
						storedProc = procCall(cess,
								cgst, igst, sgst,
								taxableVal, totalTax,
								configId, procName,
								logicalPattern);
						ProcExecution(storedProc,
								procName, configId);
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
		return procName;
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

				// Deleteing Temp data
				callDeleteTempDataProc(configId);

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

			// Deleteing Temp data
			callDeleteTempDataProc(configId);

			InwardEinvoiceReconErrorLogEntity obj = new InwardEinvoiceReconErrorLogEntity();

			obj.setConfigId(configId);
			obj.setProcName(procName);
			obj.setErrMessage(GenUtil
					.convertStringToClob(msg + " ----> " + e.toString()));
			obj.setCreatedDate(LocalDateTime.now());
			errorRepo.save(obj);

			throw new AppException(e);
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
			BigDecimal totalTax, Long configId, String procName,
			String logicalPattern) {

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
		if (logicalPattern != null) {
			storedProc.registerStoredProcedureParameter("VAR_PATTERN",
					String.class, ParameterMode.IN);

			storedProc.setParameter("VAR_PATTERN", logicalPattern);
		}

		return storedProc;
	}

	private static List<Gstr2ToleranceValueDto> getToleranceValue(
			EntityManager entityManager, Long cofigId) {
		
		List<Gstr2ToleranceValueDto> retList = null;
		
		try{

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
			LOGGER.debug("executing query to getToleranceValue :: configId "
					+ cofigId);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();
		 retList = list.stream()
				.map(o -> convertUserInput(o))
				.collect(Collectors.toCollection(ArrayList::new));
		
	} catch (Exception ex) {
		String msg = String.format("Error occured in "
				+ "Gstr2InitiateReconMatchingProcessor"
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

	private void callDeleteTempDataProc(Long configId) {
		//TODO proc name change
		try {
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_RECON_EINVPR_DELETE_TEMP_DATA");

			storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);
			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			String status = (String) storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"invoked SP USP_RECON_EINVPR_DELETE_TEMP_DATA "
								+ "with configId  %d,  Status %s ",
						configId, status);
				LOGGER.debug(msg);

			}

		} catch (Exception e) {
			String msg = "Exception occured while invoking USP_RECON_EINVPR_DELETE_TEMP_DATA";
			LOGGER.error(msg);
			throw new AppException(msg, e);
		}
	}

	private Long getPatternCount() {

		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY2);
		String chunkSize = config.getValue();
		return (Long.valueOf(chunkSize));
	}

	private Integer getPatternLengthSize() {

		Config config = configManager.getConfig(CONF_CATEG, CONF_KEY1);
		String chunkSize = config.getValue();
		return (Integer.valueOf(chunkSize));
	}

}
