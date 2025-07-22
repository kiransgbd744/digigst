package com.ey.advisory.app.gstr2b;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Get2BErpConfigRequestRepository;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.app.data.repositories.client.GstinApiCallRepository;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Service("Gstr2BGetSuccessHandler")
public class Gstr2BGetSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr2BbatchUtil")
	private Gstr2BbatchUtil batchUtil;

	@Autowired
	@Qualifier("GstinApiCallRepository")
	private GstinApiCallRepository gstnStatusRepo;

	@Autowired
	@Qualifier("DefaultGSTR2BResponseProcessor")
	private Gstr2BGetJsonProcessor defaultFactoryImpl;

	@Autowired
	Get2BErpConfigRequestRepository get2BErpConfigRequestRepo;
	
	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	@Autowired
	AsyncJobsService asyncJobsService;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {

		String gstin = null;
		String taxPeriod = null;
		boolean isDataChanged = false;
		boolean isTokenResp = false;

		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Long invocationId = result.getTransactionId();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GET Call is in Gstr2BGetSuccessHandler"
						+ " with {} reqIds", resultIds.size());
			}
			gstin = ctxParamsObj.get("gstin").getAsString();
			taxPeriod = ctxParamsObj.get("ret_period").getAsString();
			isDataChanged = ctxParamsObj.has("isDataChanged")
			        ? ctxParamsObj.get("isDataChanged").getAsBoolean() : false;
			isTokenResp = ctxParamsObj.has("isTokenResp")
			        ? ctxParamsObj.get("isTokenResp").getAsBoolean() : false;
			String groupCode = ctxParamsObj.has("groupCode")
					? ctxParamsObj.get("groupCode").isJsonNull() ? null
							: ctxParamsObj.get("groupCode").getAsString()
					: null;
			LOGGER.debug("groupCode from ctx {} ",groupCode);

			if (!isTokenResp && resultIds.size() == 1) {
				String prevGenDt = ctxParamsObj.has("prevGenDt")
				        ? ctxParamsObj.get("prevGenDt").isJsonNull() ? null
				                : ctxParamsObj.get("prevGenDt").getAsString()
				        : null;
				String prevCheckSum = ctxParamsObj.has("prevCheckSum")
				        ? ctxParamsObj.get("prevCheckSum").isJsonNull() ? null
				                : ctxParamsObj.get("prevCheckSum").getAsString()
				        : null;
				                isDataChanged = checkIsMatch(prevGenDt, prevCheckSum,
				        resultIds.get(0));
			}
			if (isDataChanged) {
			LOGGER.debug("isDataChanged {}", isDataChanged);
			batchRepo.updateStatus(APIConstants.SUCCESS,
					APIConstants.GSTR2B_GET_ALL, APIConstants.GSTR2B, gstin,
					taxPeriod, LocalDateTime.now(), isTokenResp, null,
					null);
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("gstin", gstin);
			jsonParams.addProperty("taxPeriod", taxPeriod);
			jsonParams.addProperty("invocationId", invocationId);
			
			if(Strings.isNullOrEmpty(groupCode)) {
				groupCode = TenantContext.getTenantId();
				LOGGER.debug("groupCode from tenant {} ",groupCode);
			}
			String userName = "SYSTEM";
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2B_GET_CSV_REPORT,
					jsonParams.toString(), userName, 1L, null, null);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"GET2B Success Handler, posted Report Job - {}",
						groupCode);
			gstnStatusRepo.updateStatus(gstin, taxPeriod,
					APIConstants.GSTR2B, APIConstants.SUCCESS, null,
					null, LocalDateTime.now());
			} else {

			Gstr2BGetJsonResponseProcessor processorType = defaultFactoryImpl
					.getProcessorType(resultIds);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Calling processorType::{}",
						processorType.getClass().getName());
			}
			processorType.processJsonResponse(gstin, taxPeriod, invocationId,
					resultIds, false);
			}
		} catch (Exception e) {
			LOGGER.error("Exception while handling success "
					+ "Gstr2BGetSuccessHandler", e);
			gstnStatusRepo.updateStatus(gstin, taxPeriod, APIConstants.GSTR2B,
					APIConstants.FAILED, null, null, LocalDateTime.now());
			throw new APIException(e.getLocalizedMessage());
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
