package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.vendor.master.apipush.VendorMasterApiDetailsServiceImpl;
import com.ey.advisory.app.services.vendor.master.apipush.VendorMasterApiPayloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("VendorMasterApiPushProcessor")
public class VendorMasterApiPushProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("VendorMasterApiDetailsServiceImpl")
	private VendorMasterApiDetailsServiceImpl vendorMasterApiServiceImpl;

	@Autowired
	@Qualifier("VendorMasterApiPayloadServiceImpl")
	private VendorMasterApiPayloadService payloadService;

	@Override
	public void execute(Message message, AppExecContext ctx) {

		String jsonString = message.getParamsJson();

		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		String payloadId = requestObject.get("payloadId").getAsString();
		String sourceId = requestObject.get("sourceId").getAsString();
		String groupCode = message.getGroupCode();

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("FetchGstinValidationDetailsApiPushProcessor Job"
								+ " executing for params %s", jsonString);
				LOGGER.debug(msg);
			}

			vendorMasterApiServiceImpl.fetchVendorMasterApiDetails(payloadId,
					sourceId, groupCode);

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
