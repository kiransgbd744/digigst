/**
 * 
 */
package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.HsnSummaryConfigRepository;
import com.ey.advisory.app.data.repositories.client.HsnSummaryGstinDetailRepository;
import com.ey.advisory.app.data.repositories.client.HsnSummaryProcedureRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.service.hsn.summary.HsnSummaryInitiateReportDetails;
import com.ey.advisory.service.hsn.summary.HsnSummaryProcedureEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Shashikant.Shukla
 *
 */

@Slf4j
@Component("HsnSummaryInitiateReportProcessor")
public class HsnSummaryInitiateReportProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("HsnSummaryConfigRepository")
	HsnSummaryConfigRepository reconConfigRepo;

	@Autowired
	@Qualifier("HsnSummaryGstinDetailRepository")
	HsnSummaryGstinDetailRepository reconGstinDetailRepo;
	
	@Autowired
	@Qualifier("HsnSummaryProcedureRepository")
	HsnSummaryProcedureRepository procRepo;

	@Autowired
	@Qualifier("HsnSummaryInitiateReportDetailsmpl")
	HsnSummaryInitiateReportDetails fetchReportDetails;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin HsnSummaryInitiateReportProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();

		executeHsnSummaryRecon(configId);
	}

	private void executeHsnSummaryRecon(Long configId) {

		String procName = null;
		String response = null;
		String gstin = null;
		try {
			List<HsnSummaryProcedureEntity> procList = procRepo.findProcedure();
			if (procList == null || procList.isEmpty()) {
				String msg = String.format("No Data Found To Reconciliation %d",
						configId);
				LOGGER.error(msg);
				reconConfigRepo.updateReconConfigStatusAndReportName(
						"No Data Found",
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						configId);
				throw new AppException(msg);
			}

			Map<Integer, String> procMap = new TreeMap<>();

			procMap = procList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			List<String> gstins = reconGstinDetailRepo.findByConfigId(configId);

			for (String egstin : gstins) {
				gstin = egstin;
				for (Integer k : procMap.keySet())

				{
					procName = procMap.get(k);

					StoredProcedureQuery storedProc = procCall(configId, gstin,
							procName);

					response = (String) storedProc.getSingleResult();

					LOGGER.debug(procName + " :: " + response);

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
		} catch (Exception e) {
			String msg = String.format(
					"Config Id is '%s', Response "
							+ "from RECON_MASTER SP %s did not "
							+ "return success," + " Hence updating to Failed",
					configId.toString(), procName);
			LOGGER.error(msg, e);
			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_FAILED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId);

			throw new AppException(e);
		}

		try {

			fetchReportDetails.getInitiateReconReportData(configId);

		} catch (Exception ex) {
			String msg = String.format("Error in report generation" + ex,
					configId.toString());
			LOGGER.error(msg);

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATION_FAILED,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId);
			throw new AppException(msg, ex);
		}
	}

	private StoredProcedureQuery procCall(Long configId, String gstin,
			String procName) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Recon SP with ConfigId :%s",
					configId.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("RECON_CONFIG_ID",
				Long.class, ParameterMode.IN);

		storedProc.setParameter("RECON_CONFIG_ID", configId);

		storedProc.registerStoredProcedureParameter("GSTIN", String.class,
				ParameterMode.IN);

		storedProc.setParameter("GSTIN", gstin);

		return storedProc;
	}

}
