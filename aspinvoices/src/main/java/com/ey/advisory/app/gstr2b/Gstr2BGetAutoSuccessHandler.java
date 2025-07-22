/**
 * 
 */
package com.ey.advisory.app.gstr2b;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2bAutoCommRepository;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */
@Slf4j
@Service("Gstr2BGetAutoSuccessHandler")
public class Gstr2BGetAutoSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr2bAutoCommRepository")
	private Gstr2bAutoCommRepository gstr2bAutoCommRepo;

	@Autowired
	@Qualifier("DefaultGSTR2BResponseProcessor")
	private Gstr2BGetJsonProcessor defaultFactoryImpl;
	
	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	


	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		List<Long> resultIds = result.getSuccessIds();
		String ctxParams = result.getCtxParams();
		boolean isDataChanged = false;
		boolean isTokenResp = false;

		Long invocationId = result.getTransactionId();
		JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
				.getAsJsonObject();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GET Call is in Gstr2BGetSuccessHandler"
					+ " with {} reqIds", resultIds.size());
		}
		String gstin = ctxParamsObj.get("gstin").getAsString();
		String taxPeriod = ctxParamsObj.get("ret_period").getAsString();
		isDataChanged = ctxParamsObj.has("isDataChanged")
				? ctxParamsObj.get("isDataChanged").getAsBoolean() : false;
		isTokenResp = ctxParamsObj.has("isTokenResp")
				? ctxParamsObj.get("isTokenResp").getAsBoolean() : false;

		if (!isTokenResp && resultIds.size() == 0) {
			String prevGenDt = ctxParamsObj.has("prevGenDt")
					? ctxParamsObj.get("prevGenDt").isJsonNull() ? null
							: ctxParamsObj.get("prevGenDt").getAsString()
					: null;
			String prevCheckSum = ctxParamsObj.has("prevCheckSum")
					? ctxParamsObj.get("prevCheckSum").isJsonNull() ? null
							: ctxParamsObj.get("prevCheckSum").getAsString()
					: null;
							isDataChanged = checkIsMatch(prevGenDt, prevCheckSum, resultIds.get(0));
		}
		if (isDataChanged) {

			batchRepo.updateStatus(APIConstants.SUCCESS,
					APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
					taxPeriod, LocalDateTime.now(), isTokenResp, null, null);
			gstr2bAutoCommRepo.updateGstr2bAutoStatus(gstin, taxPeriod,
					APIConstants.GSTR2B, APIConstants.SUCCESS, "");
			gstnStatusRepo.updateStatus(gstin, taxPeriod, APIConstants.GSTR2B,
					APIConstants.SUCCESS, null, null, LocalDateTime.now());
		} else {


		Gstr2BGetJsonResponseProcessor processorType = defaultFactoryImpl
				.getProcessorType(resultIds);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Calling processorType::{}",
					processorType.getClass().getName());
		}
		processorType.processJsonResponse(gstin, taxPeriod, invocationId,
				resultIds, true);
		}
	}
	
	private boolean checkIsMatch(String prevGenDt, String prevCheckSum,
			Long id) {
		Gson gson = GsonUtil.newSAPGsonInstance();

		String apiResp = APIInvokerUtil.getResultById(id);
		String genDt = "";

		Gstr2BGETDataDto reqDto = gson.fromJson(apiResp,
				Gstr2BGETDataDto.class);
		String checkSum = reqDto.getChecksum();

		if (!Strings.isNullOrEmpty(reqDto.getDetailedData().getGenDate())) {
			genDt = reqDto.getDetailedData().getGenDate();
		}

		boolean isGenDtEqual = false;
		if (prevGenDt != null && genDt != null) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				LocalDate prevDate = LocalDate.parse(prevGenDt, formatter);
				LocalDate currentDate = LocalDate.parse(genDt, formatter);
				isGenDtEqual = prevDate.equals(currentDate);
			} catch (DateTimeParseException e) {
				LOGGER.error(" Incorrect gen date format ", e);
				isGenDtEqual = false;
			}
		}

		boolean isCheckSumEqual = (prevCheckSum != null
				&& prevCheckSum.equals(checkSum));
		if (isCheckSumEqual && isGenDtEqual) {
			return true;
		}
		return false;

	}
}
