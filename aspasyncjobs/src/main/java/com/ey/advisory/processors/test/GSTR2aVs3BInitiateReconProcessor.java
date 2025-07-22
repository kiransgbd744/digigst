package com.ey.advisory.processors.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr2Avs3bReconEntity;
import com.ey.advisory.app.data.repositories.client.Gstr2Avs3BReconRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr2Avs3BReconRequestDto;
import com.ey.advisory.app.gstr1.einv.GSTR2aVs3BInitiateReconServiceimpl;
import com.ey.advisory.app.services.reports.Gstr2Avs3bReviewSummaryReportHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kiran.s
 *
 */

@Slf4j
@Component("GSTR2aVs3BInitiateReconProcessor")
public class GSTR2aVs3BInitiateReconProcessor implements TaskProcessor {

	@Autowired
	CommonUtility commonUtility;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2Avs3bReviewSummaryReportHandler")
	private Gstr2Avs3bReviewSummaryReportHandler gstr2Avs3bReviewSummaryReportHandler;

	@Autowired
	@Qualifier("Gstr2Avs3BReconRepository")
	private Gstr2Avs3BReconRepository gstr2Avs3BReconRepository;
	
	@Autowired
	@Qualifier("GSTR2aVs3BInitiateReconServiceimpl")
	GSTR2aVs3BInitiateReconServiceimpl gstr2aVs3BInitiateReconServiceimpl;


	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin GSTR2aVs3BInitiateReconProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();

		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

		Long requestId = json.get("requestId").getAsLong();
		String response = null;
		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			Optional<Gstr2Avs3bReconEntity> reqData = gstr2Avs3BReconRepository
					.findById(requestId);
			if (!reqData.isPresent()) {
			    LOGGER.error("RequestId data not found for configId: {}", requestId);
			    throw new AppException("RequestId data not found for configId: " + requestId);
			}
			Gstr2Avs3BReconRequestDto criteria = new Gstr2Avs3BReconRequestDto();
			criteria.setEntityId(reqData.get().getEntityId());
			criteria.setTaxPeriodFrom(reqData.get().getFromTaxPeriod());
			criteria.setTaxPeriodTo(reqData.get().getToTaxPeriod());
			Clob clob = reqData.get().getGstins();
			int derivedFromTaxPeriod = reqData.get().getDerivedFromTaxPeriod();
			int derivedToTaxPeriod = reqData.get().getDerivedToTaxPeriod();
			List<String> gstinList = new ArrayList<>();
			try (Reader reader = clob.getCharacterStream();
					BufferedReader bufferedReader = new BufferedReader(
							reader)) {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					// Split the line on commas and add each GSTIN to the list
					String[] gstins = line.split(",");
					for (String gstin : gstins) {
						gstinList.add(gstin.trim()); // Trim to remove any extra
														// spaces
					}
				}
			} catch (SQLException | IOException e) {
				gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
						ReconStatusConstants.RECON_FAILED, null,
						LocalDateTime.now(), requestId, null);
			}

			criteria.setGstin(gstinList);
			LOGGER.debug("Response data begin");
			int count = 0;
			try {

				for (String gstin : gstinList) {

					count = gstr2aVs3BInitiateReconServiceimpl.proceCallForComputeReversal(
							gstin, derivedFromTaxPeriod, derivedToTaxPeriod);

					if (count >= 1) {

						String msg = String.format(
								"requestId is '%s', gstin :%s Response "
										+ "from COMPUTE_GSTR1_VS_3B  "
										+ "did not return success,"
										+ " Hence updating to Failed",
								requestId, gstin);
						LOGGER.debug(msg);

						gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
								ReconStatusConstants.RECON_COMPLETED, null,
								LocalDateTime.now(), requestId, null);

					} else {
						gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
								ReconStatusConstants.RECON_FAILED, null,
								LocalDateTime.now(), requestId, null);
						continue;
					}

				}

			} catch (Exception e) {
				gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
						ReconStatusConstants.RECON_FAILED, null,
						LocalDateTime.now(), requestId, null);

			}

			if (count >= 1) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Gstr2A vs 3b report download starts with requestid ",
							requestId);
				}

				if (reqData.isPresent()) {
					Gstr2Avs3bReconEntity reqData1 = reqData.get();
					try {
						
						gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
								ReconStatusConstants.REPORT_GENERATION_INPROGRESS, null,
								LocalDateTime.now(), requestId, null);
						// Download report
						gstr2aVs3BInitiateReconServiceimpl.gstr2aVS3bReportDownload(gson, reqData1, response,
								requestId);
					} catch (Exception e) {
						gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
								ReconStatusConstants.REPORT_GENERATION_FAILED,
								null,
								LocalDateTime.now(), requestId, null);
					}
				} else {
					// Handle the case where the Optional is empty
					LOGGER.error(
							"Gstr2Avs3bReconEntity not found for requestId: {}",
							requestId);
				}

			}
		}

		catch (Exception e) {
			gstr2Avs3BReconRepository.UpdateGstr2Avs3BRecon(
					ReconStatusConstants.RECON_FAILED, null,
					LocalDateTime.now(), requestId, null);
		}

	}

	
}
