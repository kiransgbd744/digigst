/**
 * 
 */
package com.ey.advisory.processors.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.asprecon.EwbVsItc04ProcedureRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbvsItc04ConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbvsItc04ErrorRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.EwbvsItco4DownloadReconReportsRepository;
import com.ey.advisory.app.reconewbvsitc04.ErrorLogItemEntity;
import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04ConfigEntity;
import com.ey.advisory.app.reconewbvsitc04.EwbVsItc04ReconProcedureEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CombineAndZipCsvFiles;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ravindra V S
 *
 */

@Slf4j
@Component("EwbVsItc04InitiateReconProcessor")
public class EwbVsItc04InitiateReconProcessor implements TaskProcessor {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	CommonUtility commonUtility;

	@Autowired
	@Qualifier("EwbvsItc04ConfigRepository")
	EwbvsItc04ConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("EwbvsItco4DownloadReconReportsRepository")
	private EwbvsItco4DownloadReconReportsRepository reportRepo;

	@Autowired
	CombineAndZipCsvFiles combineAndZipCsvFiles;

	@Autowired
	@Qualifier("EwbVsItc04ProcedureRepository")
	EwbVsItc04ProcedureRepository procRepo;

	@Autowired
	@Qualifier("EwbvsItc04ErrorRepository")
	EwbvsItc04ErrorRepository errorRepo;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Override
	public void execute(Message message, AppExecContext context) {

		String jsonString = message.getParamsJson();

		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

		Long configId = json.get("configId").getAsLong();

		List<EwbVsItc04ConfigEntity> entityList = reconConfigRepo
				.findByConfigId(configId);
		entityList.forEach(entity -> {
			Long entityId = entity.getEntityId();
			String gstin = entity.getGstin();

			// Executing Recon Procs
			executeRecon(configId, entityId, gstin);
		});
		

		// Invoking Reports Processor Job
		JsonObject jsonParams = new JsonObject();
		jsonParams.addProperty("configId", configId);

		String groupCode = TenantContext.getTenantId();

		asyncJobsService.createJob(groupCode,
				JobConstants.INITIATE_RECON_REPORTS_EWBVSITC04,
				jsonParams.toString(), "SYSTEM", 50L, null, null);

	}

	private void executeRecon(Long configId, Long entityId, String gstin) {

		BigDecimal cess = BigDecimal.TEN;
		BigDecimal cgst = BigDecimal.TEN;
		BigDecimal igst = BigDecimal.TEN;
		BigDecimal sgst = BigDecimal.TEN;
		BigDecimal taxableVal = BigDecimal.TEN;

	/*	List<Gstr2ToleranceValueDto> toleranceValue = new ArrayList<>();

		for (Gstr2ToleranceValueDto toleValue : toleranceValue) {
			cess = toleValue.getCess() != null
					? new BigDecimal(toleValue.getCess()) : BigDecimal.TEN;
			cgst = toleValue.getCgst() != null
					? new BigDecimal(toleValue.getCgst()) : BigDecimal.TEN;
			igst = toleValue.getIgst() != null
					? new BigDecimal(toleValue.getIgst()) : BigDecimal.TEN;
			sgst = toleValue.getSgst() != null
					? new BigDecimal(toleValue.getSgst()) : BigDecimal.TEN;
			taxableVal = toleValue.getTaxableVal() != null
					? new BigDecimal(toleValue.getTaxableVal()) : BigDecimal.TEN;

		}*/

		List<EwbVsItc04ReconProcedureEntity> allProcList = procRepo.findAll();

		Map<Integer, String> procMap = new TreeMap<>();

		procMap = allProcList.stream().collect(
				Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));
		String procName = null;
		String response = null;
		try {
					for (Integer k : procMap.keySet()) {
						procName = procMap.get(k);

						// Executing List of Proc's

							StoredProcedureQuery storedProc = procCall(cess, cgst, igst,
									sgst, taxableVal, configId, procName, gstin);

							response = (String) storedProc.getSingleResult();

							LOGGER.debug(procName + " :: " + response);

							if (!ReconStatusConstants.SUCCESS
									.equalsIgnoreCase(response) && !ReconStatusConstants.FAILED
									.equalsIgnoreCase(response)) {

								String msg = String.format(
										"Config Id is '%s', Response "
												+ "from EWBVSITC04RECON_MASTER SP %s did not "
												+ "return success,"
												+ " Hence updating to Failed",
										configId.toString(), procName);
								LOGGER.error(msg);

								reconConfigRepo.updateReconConfigStatusAndReportName(
										ReconStatusConstants.RECON_FAILED,
										EYDateUtil.toUTCDateTimeFromLocal(
												LocalDateTime.now()),
										configId);
								
								throw new AppException(msg);
							} else if(ReconStatusConstants.FAILED
									.equalsIgnoreCase(response)){
								String msg = String.format(
										"Config Id is '%s', Response "
												+ "from EWBVSITC04RECON_MASTER SP %s did not "
												+ "return success,"
												+ " Hence updating to Failed",
										configId.toString(), procName);
								LOGGER.error(msg);

								reconConfigRepo.updateReconConfigStatusAndReportName(
										ReconStatusConstants.RECON_FAILED,
										EYDateUtil.toUTCDateTimeFromLocal(
												LocalDateTime.now()),
										configId);
								break;
		
							}
						}
				
		} catch (Exception e) {
			String msg = String.format(
					"Config Id is '%s', Response "
							+ "from EWBVSITC04RECON_MASTER SP %s did not "
							+ "return success," + " Hence updating to Failed",
					configId.toString(), procName);
			LOGGER.error(msg, e);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId);

			ErrorLogItemEntity obj = new ErrorLogItemEntity();

			obj.setProcedureName(procName);
			obj.setErrorMessage((msg + " ----> " + e.toString()));
			obj.setStartTime(LocalDateTime.now());
			obj.setInputParams("P_RECON_REPORT_CONFIG_ID:" + configId
					+ "P_GSTIN:" + gstin);
			errorRepo.save(obj);

			throw new AppException(e);
		}
	}

	private StoredProcedureQuery procCall(BigDecimal cess, BigDecimal cgst,
			BigDecimal igst, BigDecimal sgst, BigDecimal taxableVal,
			Long configId, String procName, String gstin) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Recon EwbVsItc04 Proc with ConfigId :%s",
					configId.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("P_RECON_REPORT_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("P_RECON_REPORT_CONFIG_ID", configId);

		storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("P_GSTIN", gstin);

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

		return storedProc;
	}

}
