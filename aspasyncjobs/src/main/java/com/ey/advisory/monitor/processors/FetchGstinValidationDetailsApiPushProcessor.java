package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.services.gstinvalidator.apipush.GstinValidatorPayloadService;
import com.ey.advisory.app.services.vendor.master.apipush.FetchGstinDetailsServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("FetchGstinValidationDetailsApiPushProcessor")
public class FetchGstinValidationDetailsApiPushProcessor
		implements TaskProcessor {

	@Autowired
	@Qualifier("FetchGstinDetailsServiceImpl")
	private FetchGstinDetailsServiceImpl fetchGstinDetailsServiceImpl;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinRepo;

	@Autowired
	@Qualifier("GstinValidatorPayloadServiceImpl")
	private GstinValidatorPayloadService payloadService;

	@Override
	public void execute(Message message, AppExecContext ctx) {

		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		String payloadId = json.get("payloadId").getAsString();
		String sourceId = json.get("sourceId").getAsString();

		String groupCode = message.getGroupCode();

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("FetchGstinValidationDetailsApiPushProcessor Job"
								+ " executing for params %s", jsonString);
				LOGGER.debug(msg);
			}

			fetchGstinDetailsServiceImpl.fetchGstinTaxPayerDetails(payloadId,
					groupCode, sourceId);

			if (payloadId.isEmpty()) {
				LOGGER.info("Payload id is null ");
				throw new AppException();
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Exception while processing the vendor filing request:",
					ex);

			String errorCode = (ex instanceof AppException)
					? ((AppException) ex).getErrCode() : "ER8888";
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
	}
}
