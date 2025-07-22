package com.ey.advisory.monitor.processors;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorValidatorPayloadRepository;
import com.ey.advisory.app.vendor.service.GstinDetailsServiceImpl;
import com.ey.advisory.app.vendor.service.VendorApiGetFilingFrequencyService;
import com.ey.advisory.app.vendor.service.VendorApiGstinValidatorPayloadService;
import com.ey.advisory.app.vendor.service.VendorDetailsApiHandler;
import com.ey.advisory.app.vendor.service.VendorReqDto;
import com.ey.advisory.app.vendor.service.VendorValidatorPayloadEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Service("VendorValidationDetailsApiPushProcessor")
public class VendorValidationDetailsApiPushProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GstinDetailsServiceImpl")
	private GstinDetailsServiceImpl fetchGstinDetailsServiceImpl;

	@Autowired
	@Qualifier("VendorApiGstinValidatorPayloadServiceImpl")
	private VendorApiGstinValidatorPayloadService payloadService;

	@Autowired
	@Qualifier("VendorValidatorPayloadRepository")
	private VendorValidatorPayloadRepository payloadRepo;

	@Autowired
	@Qualifier("VendorDetailsApiHandler")
	private VendorDetailsApiHandler fillingDeatilsService;

	@Autowired
	@Qualifier("VendorApiGetFilingFrequencyServiceImpl")
	private VendorApiGetFilingFrequencyService fillingFrequencyService;
	
	@Autowired
	private AsyncJobsService asyncJobsService;

	@Override
	public void execute(Message message, AppExecContext ctx) {

		Gson gson = new Gson();

		String jsonString = message.getParamsJson();
		JsonObject json = (JsonObject) new JsonParser().parse(jsonString);

		String payloadId = json.get("payloadId").getAsString();

		if (payloadId.isEmpty()) {
			LOGGER.info("Payload id is null ");
			throw new AppException();
		}

		String groupCode = message.getGroupCode();

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("VendorValidationDetailsApiPushProcessor Job"
								+ " executing for params %s", jsonString);
				LOGGER.debug(msg);
			}

			try {
				// gstin validator
				fetchGstinDetailsServiceImpl.fetchGstinTaxPayerDetails(
						payloadId, groupCode);

				// updating post gstin validator api call
				payloadRepo.updateGetGstinValidationStatus(payloadId);

			} catch (Exception ex) {
				LOGGER.error(
						"Exception while processing the vendor filing request:",
						ex);

				String errorCode = (ex instanceof AppException)
						? ((AppException) ex).getErrCode()
						: "ER8888";
				String errorMsg = "ERP (run time error) -" + ex.getMessage();
				errorCode = (errorCode == null) ? "ER8887" : errorCode;

				payloadService.updateErrorExc(payloadId, APIConstants.E,
						errorCode.length() > 500 ? errorCode.substring(0, 500)
								: errorCode,
						errorMsg.length() > 1000 ? errorMsg.substring(0, 1000)
								: errorMsg,
						9);
				throw new AppException(ex);

			}

			VendorValidatorPayloadEntity payloadEntity = payloadRepo
					.getGstinValidatorPayload(payloadId);

			String jsonInput = GenUtil
					.convertClobtoString(payloadEntity.getReqPlayload());

			JsonObject requestObj = (new JsonParser()).parse(jsonInput)
					.getAsJsonObject();

			JsonArray json1 = requestObj.get("req").getAsJsonArray();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request Json {}", json1);
			}

			Type listType = new TypeToken<List<VendorReqDto>>() {
			}.getType();

			List<VendorReqDto> reqDto = gson.fromJson(json1, listType);

			Map<String, List<String>> resultMap = reqDto.stream()
					.collect(Collectors.groupingBy(VendorReqDto::getFy,
							Collectors.mapping(VendorReqDto::getGstin,
									Collectors.toList())));

			// Filling Details
			resultMap.forEach((fy, gstins) -> fillingDeatilsService
					.fetchFillingDetails(payloadId, fy, gstins));

			// updating post filling Details api call
			payloadRepo.updateGetFillingDetailsStatus(payloadId);

			// filling frequency
			resultMap.forEach((fy, gstins) -> fillingFrequencyService
					.getFilingFrequency(fy, gstins, payloadId));

			// updating post filling Frequency api call
			payloadRepo.updateGetFillingFrequencyStatus(payloadId);
			
			// submiiting job for ReverseIntegration 
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"VendorValidationDetailsApiPushProcessor before"
						+ " revere Integration Job"
								+ " submission for params %s",
						jsonString);
				LOGGER.debug(msg);
			}
			
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("payloadId", payloadId);
			jsonParams.addProperty("groupCode", TenantContext.getTenantId());
			jsonParams.addProperty("scenarioName",
					APIConstants.VENDOR_VALIDATOR_API_REV_INTG);
			
			asyncJobsService.createJob(TenantContext.getTenantId(), 
					JobConstants.VENDOR_VALIDATOR_API_REV_INTG,
					jsonParams.toString(), "SYSTEM", JobConstants.PRIORITY,
					JobConstants.PARENT_JOB_ID,
					JobConstants.SCHEDULE_AFTER_IN_MINS);

		} catch (Exception ex) {
			LOGGER.error(
					"Exception while processing the vendor filing request:",
					ex);
			throw new AppException(ex);

		}
	}
}
