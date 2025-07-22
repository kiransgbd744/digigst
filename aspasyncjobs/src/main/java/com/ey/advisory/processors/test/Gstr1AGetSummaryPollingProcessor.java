package com.ey.advisory.processors.test;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.services.search.simplified.docsummary.gstr1A.Gstr1ASimpleDocGstnSummarySearchService;
import com.ey.advisory.app.util.HitGstnServer;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1AGetSummaryPollingProcessor")
public class Gstr1AGetSummaryPollingProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("hitGstnServer")
	private HitGstnServer gstnServer;

	@Autowired
	@Qualifier("Gstr1ASimpleDocGstnSummarySearchService")
	Gstr1ASimpleDocGstnSummarySearchService gstr1ATableSearchService;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository saveStatusRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Starting Gstr1GetSummaryPollingProcessor Job for"
							+ " -> params: '%s', GroupCode: '%s'",
					jsonString, groupCode);
			LOGGER.debug(msg);
		}
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		String gstin = requestObject.get(APIConstants.GSTIN).getAsString();
		String taxPeriod = requestObject.get(APIConstants.TAXPERIOD)
				.getAsString();
		String refId = requestObject.get(APIConstants.REFID).getAsString();
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("GSTR RETURN STATUS API call for -> GSTIN: '%s', "
							+ "Taxperiod: '%s'", gstin, taxPeriod);
			LOGGER.debug(msg);
		}
		callGstrReturnStatusApi(gstin, taxPeriod, refId, groupCode);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"3B Transaction polling is done -> GSTIN: '%s', "
							+ "Taxperiod: '%s', refId : '%s'",
					gstin, taxPeriod, refId);
			LOGGER.debug(msg);
		}

	}

	private void callGstrReturnStatusApi(String gstin, String taxPeriod,
			String refId, String groupCode) {
		try {

			APIResponse resp = gstnServer.poolingApiCallForGstr1A(groupCode, refId,
					gstin, taxPeriod);

			if (!resp.isSuccess()) {

				String errResp = resp.getError().toString();
				Clob errRespClob = GenUtil.convertStringToClob(errResp);

				// update
				saveStatusRepo.updateStatusAndReponseForRefId(
						APIConstants.POLLING_FAILED, null, errRespClob, refId,
						LocalDateTime.now());

				return;
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"GSTR RETURN STATUS API call response -> '%s'",
						resp.getResponse());
				LOGGER.debug(msg);
			}
			JsonObject respObj = JsonParser.parseString(resp.getResponse())
					.getAsJsonObject();

			String status = respObj.get("status_cd").toString().replace("\"",
					"");

			String successResp = resp.getResponse();
			Clob successRespClob = GenUtil.convertStringToClob(successResp);

			if ("ER".equalsIgnoreCase(status) || "P".equalsIgnoreCase(status)) {
				saveStatusRepo.updateStatusAndReponseForRefId(
						APIConstants.POLLING_COMPLETED, status, successRespClob,
						refId, LocalDateTime.now());

				Annexure1SummaryReqDto annexure1SummaryRequest = new Annexure1SummaryReqDto();
				annexure1SummaryRequest.setAction("UPDATEGSTIN");
				annexure1SummaryRequest.setTaxPeriod(taxPeriod);

				Map<String, List<String>> dataSecAttrs = new HashMap<>();
				dataSecAttrs.put(OnboardingConstant.GSTIN,
						Arrays.asList(gstin));
				annexure1SummaryRequest.setDataSecAttrs(dataSecAttrs);

				LOGGER.debug("GSTN Data Summary Execution BEGIN ");
				@SuppressWarnings("unused")
				SearchResult<Gstr1CompleteSummaryDto> gstnSumryResult = (SearchResult<Gstr1CompleteSummaryDto>) 
				gstr1ATableSearchService
						.find(annexure1SummaryRequest, null,
								Gstr1CompleteSummaryDto.class);
				LOGGER.debug("OUTWARD GSTN Data Summary Execution END ");

			} else if ("REC".equalsIgnoreCase(status)
					|| "IP".equalsIgnoreCase(status)) {
				saveStatusRepo.updateStatusAndReponseForRefId(
						APIConstants.POLLING_INPROGRESS, status, null, refId,
						LocalDateTime.now());
			}

		} catch (Exception ex) {
			saveStatusRepo.updateStatusAndReponseForRefId(
					APIConstants.POLLING_FAILED, null, null, refId,
					LocalDateTime.now());
			String msg = "Exception while Transaction polling of GSTR1 GET Summary";
			LOGGER.error(msg, ex);
			throw new AppException(ex, msg);
		}
	}
}
