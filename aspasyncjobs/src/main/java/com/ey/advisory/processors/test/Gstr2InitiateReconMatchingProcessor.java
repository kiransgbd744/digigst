/**
 * 
 */
package com.ey.advisory.processors.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.ey.advisory.app.asprecon.gstr2.initiaterecon.Recon2BPRErrorLogEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRPatternEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRProcedureEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconProcedureEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRPatternRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRProcedureIsdTestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2Recon2BPRProcedureRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconAddlReportsRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconProcedureRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Recon2BPRErrorLogsRepository;
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
import jakarta.persistence.EntityNotFoundException;
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
@Component("Gstr2InitiateReconMatchingProcessor")
public class Gstr2InitiateReconMatchingProcessor implements TaskProcessor {

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
	@Qualifier("Gstr2Recon2BPRProcedureIsdTestRepository")
	Gstr2Recon2BPRProcedureIsdTestRepository proc2BPRIsdTestRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRProcedureRepository")
	Gstr2Recon2BPRProcedureRepository proc2BPRRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRPatternRepository")
	Gstr2Recon2BPRPatternRepository pattern2BPRRepo;

	@Autowired
	@Qualifier("Gstr2Recon2BPRAddlReportsRepository")
	Gstr2Recon2BPRAddlReportsRepository addl2BPRReportRepo;

	/*
	 * @Autowired
	 * 
	 * @Qualifier("Gstr2InitiateRecon2BPRFetchReportDetailsImpl")
	 * Gstr2InitiateRecon2BPRFetchReportDetailsImpl fetch2BPRReportDetails;
	 */

	@Autowired
	@Qualifier("Gstr2InitiateReconFetchReportDetailsImpl")
	Gstr2InitiateReconFetchReportDetails fetchReportDetails;

	@Autowired
	@Qualifier("Recon2BPRErrorLogsRepository")
	Recon2BPRErrorLogsRepository errorRepo;

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
					"Begin Gstr2InitiateReconMatchingProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();

		Optional<Gstr2ReconConfigEntity> optEntity = reconConfigRepo
				.findById(configId);

		if (optEntity.isPresent()) {
			Gstr2ReconConfigEntity entity = optEntity.get();
			String reconType = entity.getType();
			Long entityId = entity.getEntityId();

			if (reconType.equalsIgnoreCase("2APR")) {
				execute2APR(configId);
			} else {
				execute2BPR(configId, entityId);
			}
		} else {
			LOGGER.error("FileStatusDownloadReportEntity not found for ID: {}", configId);
			throw new EntityNotFoundException("FileStatusDownloadReportEntity not found for ID: " + configId);
		}
	}

	private void execute2BPR(Long configId, Long entityId) {

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

			List<String> addnReportList = addl2BPRReportRepo
					.getAddlnReportTypeList(configId);

			List<Long> entityIds = new ArrayList<>();
			entityIds.add(entityId);
			List<Long> optedEntities = entityConfigPemtRepo
					.getAllEntitiesOpted2B(entityIds, "I32");

			boolean flag = false;
			if (optedEntities != null && !optedEntities.isEmpty()) {

				flag = true;
			}

			// OnBoarding - DO You want to enable YDT 2BPR Recon?
			// if yes - {
			// call (new Proc )
			// report
			// }

			if (flag) {
				callYdtProc(configId);

			} else {

				List<Gstr2Recon2BPRProcedureEntity> allProcList = proc2BPRRepo
						.findAll();
				
				allProcList = allProcList.stream().filter(o -> o.getIsActive())
						.collect(Collectors.toList());

			/*	List<Gstr2Recon2BPRProcedureEntity> allProcList = proc2BPRIsdTestRepo
						.findAll();*/

				// check for isd selection
				if (!addnReportList.contains("ISD Matching Report")) {
					allProcList = allProcList.stream()
							.filter(o -> !o.getIsIsdRecon())
							.collect(Collectors.toList());
				}
				
				Integer patternLengthSize = getPatternLengthSize();

				if (allProcList.isEmpty()) {

					String msg = String.format("Procedure Table is empty : "
							+ "Config Id is '%s' Hence updating to Failed",
							configId.toString());
					LOGGER.error(msg);

					reconConfigRepo.updateReconConfigStatusAndReportName(
							ReconStatusConstants.RECON_FAILED, null,
							LocalDateTime.now(), configId);

					Recon2BPRErrorLogEntity obj = new Recon2BPRErrorLogEntity();

					obj.setConfigId(configId);
					obj.setProcName(null);
					obj.setErrMessage(GenUtil.convertStringToClob(msg));
					obj.setCreatedDate(LocalDateTime.now());
					errorRepo.save(obj);

					throw new AppException(msg);

				}

		//	Map<Integer, Gstr2Recon2BPRProcedureEntity> reportMap = new TreeMap<>();

				Map<Integer, Gstr2Recon2BPRProcedureEntity> reportMap = new TreeMap<>();

				reportMap = allProcList.stream()
						.collect(Collectors.toMap(o -> o.getSeqId(), o -> o));

				String reportName = null;
				String procName = null;
				try {
					for (Integer k : reportMap.keySet()) {
						Gstr2Recon2BPRProcedureEntity procedureTestEntity = reportMap
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
								
								&& (!reportName.startsWith("ISD:")
										|| (reportName
												.startsWith("ISD:")
												&& addnReportList.contains(
														"ISD Matching Report")))) {
							
							StoredProcedureQuery storedProc = null;
							if (reportName.equalsIgnoreCase("LOGICAL") 
									|| reportName.equalsIgnoreCase("ISD:LOGICAL")) {
								
								procName = logicalReconeExecution(configId,
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

					Recon2BPRErrorLogEntity obj = new Recon2BPRErrorLogEntity();

					obj.setConfigId(configId);
					obj.setProcName(procName);
					obj.setErrMessage(GenUtil.convertStringToClob(
							msg + " ----> " + e.toString()));
					obj.setCreatedDate(LocalDateTime.now());
					errorRepo.save(obj);

					throw new AppException(e);
				}

			}

			// Invoking 2BPR report seviceImpl
			// Gstr2InitiateRecon2BPRFetchReportDetailsImpl
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", configId);

			String groupCode = TenantContext.getTenantId();

			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_2BPR_RECON_REPORT_GENERATE,
					jsonParams.toString(), "SYSTEM", 50L, null, null);

			// fetch2BPRReportDetails.get2BPRReconReportData(configId);

		} catch (Exception ex) {

			LOGGER.error("Exception occured during 2BPR initiate Recon in proc",
					ex);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), configId);

			// Deleteing Temp data
			callDeleteTempDataProc(configId);
			Recon2BPRErrorLogEntity obj = new Recon2BPRErrorLogEntity();

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
	private String logicalReconeExecution(Long configId, BigDecimal cess,
			BigDecimal cgst, BigDecimal igst, BigDecimal sgst,
			BigDecimal taxableVal, BigDecimal totalTax,
			Integer patternLengthSize,
			Gstr2Recon2BPRProcedureEntity procedureTestEntity) {
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

			Recon2BPRErrorLogEntity obj = new Recon2BPRErrorLogEntity();

			obj.setConfigId(configId);
			obj.setProcName(procName);
			obj.setErrMessage(GenUtil
					.convertStringToClob(msg + " ----> " + e.toString()));
			obj.setCreatedDate(LocalDateTime.now());
			errorRepo.save(obj);

			throw new AppException(e);
		}
	}

	private void execute2APR(Long configId) {

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
					: null;
			cgst = toleValue.getCgst() != null
					? toleValue.getCess()
					: null;
			igst = toleValue.getIgst() != null
					? toleValue.getCess()
					: null;
			sgst = toleValue.getSgst() != null
					? toleValue.getCess()
					: null;
			taxableVal = toleValue.getTaxableVal() != null
					? toleValue.getCess()
					: null;
			totalTax = toleValue.getTotalTax() != null
					? toleValue.getCess()
					: null;

		}
		// fetching addln Reports

		List<String> addnReportList = addlReportRepo
				.getAddlnReportTypeList(configId);

		List<Gstr2ReconProcedureEntity> allProcList = procRepo.findAll();

		Map<Integer, String> procMap = new TreeMap<>();

		try {
			procMap = allProcList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			String procName = null;
			String response = null;

			try {
				for (Integer k : procMap.keySet()) {
					procName = procMap.get(k);

					// chcking for optional 3 reports
					if ((!procName.equalsIgnoreCase(
							"USP_RECON_2APR_10_DOC_NUM_MISMATCH_2")
							|| (procName.equalsIgnoreCase(
									"USP_RECON_2APR_10_DOC_NUM_MISMATCH_2")
									&& addnReportList
											.contains("Doc No Mismatch II")))
							&& (!procName.equalsIgnoreCase(
									"USP_RECON_2APR_11_POTENTIAL_2")
									|| (procName.equalsIgnoreCase(
											"USP_RECON_2APR_11_POTENTIAL_2")
											&& addnReportList
													.contains("Potential-II")))
							&& (!procName.equalsIgnoreCase(
									"USP_RECON_2APR_12_LOGICAL")
									|| (procName.equalsIgnoreCase(
											"USP_RECON_2APR_12_LOGICAL")
											&& addnReportList.contains(
													"Logical Match")))) {

						StoredProcedureQuery storedProc = procCall(cess, cgst,
								igst, sgst, taxableVal, totalTax, configId,
								procName, null);

						response = (String) storedProc.getSingleResult();

						LOGGER.error(procName + " :: " + response);

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
											null, LocalDateTime.now(),
											configId);

							throw new AppException(msg);
						}
					}
				}
			} catch (Exception e) {
				String msg = String.format("Config Id is '%s', Response "
						+ "from RECON_MASTER SP %s did not " + "return success,"
						+ " Hence updating to Failed", configId.toString(),
						procName);
				LOGGER.error(msg, e);

				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_FAILED, null,
						LocalDateTime.now(), configId);

				throw new AppException(e);
			}

			try {
				if (addnReportList.contains("Consolidated IMPG Report"))
					callImpgProc(configId, taxableVal, igst, cess);
			} catch (Exception ex) {

				LOGGER.error("Exception occured during impg Recon in proc ",
						ex);

				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_FAILED, null,
						LocalDateTime.now(), configId);

				throw new AppException(ex);

			}
			fetchReportDetails.getReconReportData(configId);

		} catch (Exception ex) {

			LOGGER.error("Exception occured during initiate Recon in proc ",
					ex);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), configId);

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

	private void callImpgProc(Long configId, BigDecimal taxbleValue,
			BigDecimal igst, BigDecimal cess) {
		String impgResponse = null;
		List<String> impgProcs = Arrays.asList(
				"USP_RECON_2APR_18_IMPG_EXACT_MATCH",
				"USP_RECON_2APR_19_IMPG_MISMATCH",
				"USP_RECON_2APR_20_IMPG_ADDITIONAL_ENTRIES");
		for (String procName : impgProcs) {

			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(procName);

			storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);
			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			storedProc.registerStoredProcedureParameter("P_TAXABLE_VALUE",
					BigDecimal.class, ParameterMode.IN);
			storedProc.setParameter("P_TAXABLE_VALUE", taxbleValue);

			storedProc.registerStoredProcedureParameter("P_IGST",
					BigDecimal.class, ParameterMode.IN);
			storedProc.setParameter("P_IGST", igst);

			storedProc.registerStoredProcedureParameter("P_CESS",
					BigDecimal.class, ParameterMode.IN);
			storedProc.setParameter("P_CESS", cess);

			impgResponse = (String) storedProc.getSingleResult();

			LOGGER.error(procName + " :: " + impgResponse);

			if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(impgResponse)) {

				String msg = String.format("Config Id is '%s', Response "
						+ "from Recon_IMPG SP %s did not " + "return success,"
						+ " Hence updating to Failed", configId.toString(),
						procName);
				LOGGER.error(msg);

				reconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.RECON_FAILED, null,
						LocalDateTime.now(), configId);

				throw new AppException(msg);
			}
		}

	}

	private void callDeleteTempDataProc(Long configId) {

		try {
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_RECON_2BPR_DELETE_TEMP_DATA");

			storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);
			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			String status = (String) storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"invoked SP USP_RECON_2BPR_DELETE_TEMP_DATA "
								+ "with configId  %d,  Status %s ",
						configId, status);
				LOGGER.debug(msg);

			}

		} catch (Exception e) {
			String msg = "Exception occured while invoking USP_RECON_2BPR_DELETE_TEMP_DATA";
			LOGGER.error(msg);
			throw new AppException(msg, e);
		}
	}

	private void callYdtProc(Long configId) {

		try {
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_2BPR_MASTER_YTD");

			storedProc.registerStoredProcedureParameter(
					"P_RECON_REPORT_CONFIG_ID", Long.class, ParameterMode.IN);
			storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

			String status = (String) storedProc.getSingleResult();

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"invoked SP USP_RECON_2BPR_MASTER_YTD "
								+ "with configId  %d,  Status %s ",
						configId, status);
				LOGGER.debug(msg);

			}

		} catch (Exception e) {
			String msg = "Exception occured while "
					+ "invoking USP_RECON_2BPR_MASTER_YTD";
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
