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

import com.ey.advisory.app.data.daos.client.simplified.ProcessSbmitPorcDaoImpl;
import com.ey.advisory.app.data.entities.client.Gstr6ComputeCredDistDataEntity;
import com.ey.advisory.app.data.repositories.client.EinvReconProcedureRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1EInvReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1PRvsSubReconGstinDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1SubmittedReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6ComputeCredDistDataRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CalculateTurnOverRequestDto;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvInitiateReconFetchReportDetails;
import com.ey.advisory.app.gstr1.einv.Gstr6CompCredDistDataReportDetailsImpl;
import com.ey.advisory.app.services.daos.gstr6.Gstr6CalculateTurnOverGstnService;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
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
@Component("Gstr6CompCredDistDataProcessor")
public class Gstr6CompCredDistDataProcessor implements TaskProcessor {

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
	@Qualifier("Gstr6CompCredDistDataReportDetailsImpl")
	Gstr6CompCredDistDataReportDetailsImpl credDistdataReportdetimpl;

	@Autowired
	@Qualifier("ProcessSbmitPorcDaoImpl")
	ProcessSbmitPorcDaoImpl procCallImpl;
	
	@Autowired
	@Qualifier("Gstr6CalculateTurnOverGstnServiceImpl")
	private Gstr6CalculateTurnOverGstnService gstr6CalculateTurnOverGstnService;
	
	@Autowired
	private Gstr6ComputeCredDistDataRepository gstr6CompCredDistDataRepository;



	@Override
	public void execute(Message message, AppExecContext context) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Begin Gstr6CompCredDistDataProcessor :%s", message.toString());
			LOGGER.debug(msg);
		}

		String jsonString = message.getParamsJson();

		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();

		Long requestId = json.get("requestId").getAsLong();

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			Optional<Gstr6ComputeCredDistDataEntity> reqDataOpt = gstr6CompCredDistDataRepository.findById(requestId);
			if (!reqDataOpt.isPresent()) {
			    // Handle the case where reqData is not found
			    LOGGER.error("Request data not found for requestId: {}", requestId);
			    gstr6CompCredDistDataRepository.updateGstr6CredDistDataComp(
			        ReconStatusConstants.COMPUTE_FAILED, null, LocalDateTime.now(), requestId, null);
			    return; // Exit if no data found
			}
			Gstr6ComputeCredDistDataEntity reqData = reqDataOpt.get();
			Gstr6CalculateTurnOverRequestDto criteria=new Gstr6CalculateTurnOverRequestDto();
			criteria.setEntityId(reqData.getEntityId());
			criteria.setTaxPeriod(reqData.getTaxPeriod());
			//criteria.setGstin(reqData.getGstin());
			Clob clob = reqData.getGstins();
			List<String> gstinList = new ArrayList<>();
			try (Reader reader = clob.getCharacterStream();
			     BufferedReader bufferedReader = new BufferedReader(reader)) {
			    String line;
			    while ((line = bufferedReader.readLine()) != null) {
			        gstinList.add(line);
			    }
			} catch (SQLException | IOException e) {
				gstr6CompCredDistDataRepository.updateGstr6CredDistDataComp(
						ReconStatusConstants.COMPUTE_FAILED, null,
						LocalDateTime.now(), requestId,null);
			}

			criteria.setGstin(gstinList);
			LOGGER.debug("Response data begin");
			String gstr6ComputeStatus = gstr6CalculateTurnOverGstnService.getGstr6ComputeCreditDistributionData(criteria,requestId);

			if (gstr6ComputeStatus.equalsIgnoreCase("Success")) {
				
				try {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Gstr6 report download starts with requestid ",
								requestId);
					}
					//--download report
					credDistdataReportdetimpl.getGstr6CredDistReportData(requestId);
				} catch (Exception e) {
					
					gstr6CompCredDistDataRepository.updateGstr6CredDistDataComp(
							ReconStatusConstants.REPORT_GENERATION_FAILED, null,
							LocalDateTime.now(), requestId,null);
				}
				
			}
						
		
		} catch (Exception ex) {
		
			gstr6CompCredDistDataRepository.updateGstr6CredDistDataComp(
					ReconStatusConstants.REPORT_GENERATION_FAILED, null,
					LocalDateTime.now(), requestId,null);
			return;

		}

	}

}
