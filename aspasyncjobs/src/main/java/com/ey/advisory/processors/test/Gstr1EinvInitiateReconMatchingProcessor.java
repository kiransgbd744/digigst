package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.EinvReconProcedureEntity;
import com.ey.advisory.app.data.entities.client.Gstr1EInvReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconProcedureRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconGstinDetailsRepository;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvInitiateReconFetchReportDetails;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr1EinvInitiateReconMatchingProcessor")
public class Gstr1EinvInitiateReconMatchingProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("EinvReconProcedureRepository")
	EinvReconProcedureRepository procRepo;

	@Autowired
	@Qualifier("Gstr1EInvReconConfigRepository")
	Gstr1EInvReconConfigRepository gstr1InvReconConfigRepo;

	@Autowired
	@Qualifier("Gstr1EInvReconGstinDetailsRepository")
	Gstr1EInvReconGstinDetailsRepository gstr1ReconGstin;

	@Autowired
	@Qualifier("Gstr1EinvInitiateReconFetchReportDetailsImpl")
	Gstr1EinvInitiateReconFetchReportDetails reportFetchService;

	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin Gstr1EinvInitiateReconMatchingProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long configId = json.get("configId").getAsLong();

		List<EinvReconProcedureEntity> allProcList = procRepo.findAll();

		Map<Integer, String> procMap = new TreeMap<>();

		try {
			procMap = allProcList.stream().collect(
					Collectors.toMap(o -> o.getSeqId(), o -> o.getProcName()));

			List<String> gstinsDetails = gstr1ReconGstin
					.findGstinsByReconConfigId(configId);

			Optional<Gstr1EInvReconConfigEntity> configData = gstr1InvReconConfigRepo
					.findById(configId);
			if (!configData.isPresent()) {
			    LOGGER.error("Config data not found for configId: {}", configId);
			    throw new AppException("Config data not found for configId: " + configId);
			}
			Integer derivedTaxPeriod = configData.get().getDerivedTaxPeriod();

			String procName = null;
			String response = null;

			for (String gstin : gstinsDetails) {

				for (Integer k : procMap.keySet()) {
					procName = procMap.get(k);

					StoredProcedureQuery storedProc = entityManager
							.createStoredProcedureQuery(procName);

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"About to execute Recon SP with "
										+ "ConfigId :%s and gstin :%s",
								configId.toString(), gstin);
						LOGGER.debug(msg);
					}

					storedProc.registerStoredProcedureParameter(
							"P_RECON_CONFIG_ID", Long.class, ParameterMode.IN);
					storedProc.setParameter("P_RECON_CONFIG_ID", configId);

					storedProc.registerStoredProcedureParameter("P_GSTIN",
							String.class, ParameterMode.IN);
					storedProc.setParameter("P_GSTIN", gstin);

					storedProc.registerStoredProcedureParameter(
							"P_RETURN_PERIOD", Integer.class, ParameterMode.IN);
					storedProc.setParameter("P_RETURN_PERIOD",
							derivedTaxPeriod);

					response = (String) storedProc.getSingleResult();

					if (LOGGER.isDebugEnabled()) {
						String msg = String.format(
								"Config ID is '%s', Executed Recon SP, "
										+ "Response %s from RECON_MASTER "
										+ "storeProc: %s, gsting :%s, taxPeriod :%d",
								configId.toString(), response, procName, gstin,
								derivedTaxPeriod);
						LOGGER.debug(msg);
					}

					if (!ReconStatusConstants.SUCCESS
							.equalsIgnoreCase(response)) {

						String msg = String.format(
								"Config Id is '%s', gstin :%s Response "
										+ "from RECON_MASTER SP %s ,"
										+ "taxPeriod :%d did not return success,"
										+ " Hence updating to Failed",
								configId.toString(), gstin, procName,
								derivedTaxPeriod);
						LOGGER.debug(msg);

						gstr1InvReconConfigRepo
								.updateReconConfigStatusAndReportName(
										ReconStatusConstants.REPORT_GENERATION_FAILED,
										null, EYDateUtil.toUTCDateTimeFromLocal(
												LocalDateTime.now()),
										configId);
						throw new AppException(msg);

					}

				}

			}
			gstr1InvReconConfigRepo
			.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_COMPLETED,
					null, EYDateUtil.toUTCDateTimeFromLocal(
							LocalDateTime.now()),
					configId);
			reportFetchService.getReconReportData(configId);

		} catch (Exception ex) {
			String msg = String.format(
					"Config Id is '%s', Exception occured during initiate recon",
					configId.toString());
			LOGGER.debug(msg);

			LOGGER.error(msg, ex);

			gstr1InvReconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					configId);

			return;

		}

	}

}
