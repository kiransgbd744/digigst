/**
 * 
 */
package com.ey.advisory.app.services.gstr3b.qtr.filing.apipush;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr3bQtrFilingApiPushRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bQtrFilingPayloadRepository;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Service("Gstr3bQtrFilingDetailsServiceImpl")
@Slf4j
public class Gstr3bQtrFilingDetailsServiceImpl {

	@Autowired
	@Qualifier("Gstr3bQtrFilingApiPushRepository")
	private Gstr3bQtrFilingApiPushRepository apiPushRepo;

	@Autowired
	@Qualifier("Gstr3bQtrFilingPayloadRepository")
	private Gstr3bQtrFilingPayloadRepository payloadRepo;

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private GstnReturnFilingStatus gstnReturnFiling;

	public void fetchGstr3bQtrFilingDetails(String payloadId,
			String groupCode) {

		try {
			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter formatter1 = DateTimeFormatter
					.ofPattern("dd-MM-yyyy HH:mm:ss");
			String formattedDateTime = currentDateTime.format(formatter1);
			LocalDateTime parsedDateTime = LocalDateTime
					.parse(formattedDateTime, formatter1);

			Gstr3bQtrFilingPayloadEntity payloadEntity = payloadRepo
					.getGstr3bQtrFilingPayload(payloadId);

			String jsonString = payloadEntity.getReqPlayload();

			Gson gson = GsonUtil.newSAPGsonInstance();

			Gstr3bQtrFilingReqDtoList dtoList = gson.fromJson(jsonString,

					Gstr3bQtrFilingReqDtoList.class);

			List<Gstr3bQtrFilingDetailApiPushEntity> gstinDetailsList = new ArrayList<>();

			int totalCount = 0;
			int errorCount = 0;

			for (Gstr3bQtrFilingReqDto dto : dtoList.getReqDtoList()) {

				if (LOGGER.isDebugEnabled())
					LOGGER.debug(
							"inside Gstr3bQtrFilingDetailsServiceImpl before calling api response");

				String financialYear = GenUtil
						.getFinancialYearByTaxperiod(dto.getReturnPeriod());
				try {
					List<ReturnFilingGstnResponseDto> retFilingList = gstnReturnFiling
							.callGstnApi(Arrays.asList(dto.getGstin()),
									financialYear, true);
					// gstnReturnFiling.persistReturnFillingStatus(retFilingList,
					// true);
					Boolean getStatus = false;
					for (ReturnFilingGstnResponseDto returnFilingDto : retFilingList) {
						if (returnFilingDto.getErrCode() == null) {
							if (returnFilingDto.getRetType()
									.equalsIgnoreCase("Gstr3B")
									&& getQuarter(
											returnFilingDto.getRetPeriod())
													.equalsIgnoreCase(
															dto.getQuarter())) {
								Gstr3bQtrFilingDetailApiPushEntity gstinResp = new Gstr3bQtrFilingDetailApiPushEntity();
								gstinResp.setGstin(dto.getGstin());
								gstinResp
										.setReturnPeriod(dto.getReturnPeriod());
								gstinResp.setQuarter(dto.getQuarter());
								gstinResp.setCreatedOn(parsedDateTime);
								gstinResp.setCreatedBy("SYSTEM");
								gstinResp.setIsFiled("Y");
								gstinResp.setPayloadId(payloadId);
								getStatus = true;
								gstinDetailsList.add(gstinResp);
							}
						} else {
							Gstr3bQtrFilingDetailApiPushEntity gstinResp = new Gstr3bQtrFilingDetailApiPushEntity();
							gstinResp.setGstin(dto.getGstin());
							gstinResp.setReturnPeriod(dto.getReturnPeriod());
							gstinResp.setQuarter(dto.getQuarter());
							gstinResp.setCreatedOn(parsedDateTime);
							gstinResp.setCreatedBy("SYSTEM");
							gstinResp.setIsFiled("N");
							gstinResp.setPayloadId(payloadId);
							gstinResp
									.setErrorCode(returnFilingDto.getErrCode());
							gstinResp.setErrorDiscription(
									returnFilingDto.getErrMsg());
							getStatus = true;
							gstinDetailsList.add(gstinResp);
						}
					}
					if (!getStatus) {
						Gstr3bQtrFilingDetailApiPushEntity gstinResp = new Gstr3bQtrFilingDetailApiPushEntity();
						gstinResp.setGstin(dto.getGstin());
						gstinResp.setReturnPeriod(dto.getReturnPeriod());
						gstinResp.setQuarter(dto.getQuarter());
						gstinResp.setCreatedOn(parsedDateTime);
						gstinResp.setCreatedBy("SYSTEM");
						gstinResp.setIsFiled("N");
						gstinResp.setPayloadId(payloadId);
						gstinDetailsList.add(gstinResp);
					}

				} catch (Exception e) {
					errorCount ++;
					LOGGER.error("");
				}
			}
			List<List<Gstr3bQtrFilingDetailApiPushEntity>> chunks = Lists
					.partition(gstinDetailsList, 2000);
			// saving all the values
			for (List<Gstr3bQtrFilingDetailApiPushEntity> chunk : chunks) {
				apiPushRepo.saveAll(chunk);
			}
			if (totalCount == errorCount) {
				payloadRepo.updateStatus(payloadId, "E", LocalDateTime.now());
			} else if (errorCount == 0) {
				payloadRepo.updateStatus(payloadId, "P", LocalDateTime.now());
			} else {
				payloadRepo.updateStatus(payloadId, "PE", LocalDateTime.now());

			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Posting reverse integration job for payloadId : {} ",
						payloadId);
			}

			String jobCategory = APIConstants.GSTR3B_QTR_FILING_PAYLOAD_METADATA_REV_INTG;
			JsonObject jobParams = new JsonObject();
			jobParams.addProperty("payloadId", payloadId);
			jobParams.addProperty("scenarioName",
					APIConstants.GSTR3B_QTR_FILING_PAYLOAD_METADATA_REV_INTG);

			asyncJobsService.createJob(groupCode, jobCategory,
					jobParams.toString(), "SYSTEM", JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);
		} catch (Exception ex) {
			LOGGER.error(
					" Error occured while fetching the venndor details {} ",
					ex);
			throw new AppException(ex);
		}
	}

	private String getQuarter(String taxPeriod) {
		Integer month = Integer.parseInt(taxPeriod.substring(0, 2));
		String quarter = "";
		if (month > 3 && month < 7)
			quarter = "Q1";
		else if (month > 6 && month < 10)
			quarter = "Q2";
		else if (month > 9 && month <= 12)
			quarter = "Q3";
		else
			quarter = "Q4";

		return quarter;
	}
}
