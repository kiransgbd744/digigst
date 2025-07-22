package com.ey.advisory.processors.test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.simplified.ProcessSbmitPorcDaoImpl;
import com.ey.advisory.app.data.entities.client.Gstr1PrVsSubmittedReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconProcedureRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1PRvsSubReconGstinDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1SubmittedReconConfigRepository;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvInitiateReconFetchReportDetails;
import com.ey.advisory.app.gstr1.einv.Gstr1PrVsSubmInitiateReconFetchReportDetails;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran
 *
 */

@Slf4j
@Component("Gstr1PrSubmittedInitiateReconProcessor")
public class Gstr1PrSubmittedInitiateReconProcessor implements TaskProcessor {

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
	@Qualifier("Gstr1SubmittedReconConfigRepository")
	Gstr1SubmittedReconConfigRepository gstr1PrSubmiReconConfigRepo;

	@Autowired
	@Qualifier("Gstr1PRvsSubReconGstinDetailsRepository")
	Gstr1PRvsSubReconGstinDetailsRepository gstr1ReconGstinRepo;

	@Autowired
	@Qualifier("Gstr1EinvInitiateReconFetchReportDetailsImpl")
	Gstr1EinvInitiateReconFetchReportDetails reportFetchService;

	@Autowired
	@Qualifier("Gstr1PrVsSubmReconFetchReportDetailsImpl")
	Gstr1PrVsSubmInitiateReconFetchReportDetails prVsSubReportFetchService;

	@Autowired
	@Qualifier("ProcessSbmitPorcDaoImpl")
	ProcessSbmitPorcDaoImpl procCallImpl;


	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin Gstr1PRvsSubReconMatchingProcessor :%s", message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();

		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

		Long configId = json.get("configId").getAsLong();

		try {

			List<String> gstinsList = gstr1ReconGstinRepo.findGstinsByReconConfigId(configId);

			Optional<Gstr1PrVsSubmittedReconConfigEntity> configData = gstr1PrSubmiReconConfigRepo.findById(configId);
			if (!configData.isPresent()) {
			    LOGGER.error("Config data not found for configId: {}", configId);
			    throw new AppException("Config data not found for configId: " + configId);
			}
			Integer fromDerivedTaxPeriod = configData.get().getFromDerivedTaxPeriod();
			Integer toDerivedTaxPeriod = configData.get().getToDerivedTaxPeriod();

			int response = procCallImpl.getProcubmitProcCall(gstinsList, fromDerivedTaxPeriod, toDerivedTaxPeriod);
			if (!(response > 0)) {

				String msg = String.format("Config Id is '%s', gstin :%s Response " + " did not return success,"
						+ " Hence updating to Failed", configId.toString(), gstinsList);
				LOGGER.debug(msg);
				gstr1PrSubmiReconConfigRepo.updateReconConfigStatusAndReportName(ReconStatusConstants.RECON_FAILED,
						null, LocalDateTime.now(), configId);
				throw new AppException(msg);

			}
			gstr1PrSubmiReconConfigRepo.updateReconConfigStatusAndReportName(ReconStatusConstants.RECON_COMPLETED, null,
					LocalDateTime.now(), configId);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Config ID is '%s', Executed PR vs sub Recon SP, "
								+ "Recon completed preparing for report generation " + " response :%s",
						configId.toString(), response);
				LOGGER.debug(msg);
			}
			try {
				prVsSubReportFetchService.getPrVSSubmiReconReportData(configId);
			} catch (Exception e) {
				
				String msg = String.format(
						"Exception while generating report. ");
				LOGGER.error(msg, e);
				gstr1PrSubmiReconConfigRepo.updateReconConfigStatusAndReportName(
						ReconStatusConstants.REPORT_GENERATION_FAILED, null,
						LocalDateTime.now(),
						configId);
			}
			

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Config ID is '%s', Executed PR vs sub Recon SP, "
						+ "report generation completed " + "response: %s", configId.toString(), response);
				LOGGER.debug(msg);
			}
		} catch (Exception ex) {
			String msg = String.format("Config Id is '%s', Exception occured during report download",
					configId.toString());
			LOGGER.debug(msg);
			LOGGER.error(msg, ex);
			
			/*gstr1PrSubmiReconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now(),
					configId);*/
			gstr1PrSubmiReconConfigRepo.updateReconConfigStatusAndReportName(ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), configId);

			return;

		}

	}

}
