package com.ey.advisory.monitor.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.services.gstr3b.qtr.filing.apipush.Gstr3bQtrFilingDetailsServiceImpl;
import com.ey.advisory.app.services.gstr3b.qtr.filing.apipush.Gstr3bQtrFilingPayloadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("Gstr3bQtrFilingApiPushProcessor")
public class Gstr3bQtrFilingApiPushProcessor
		implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr3bQtrFilingDetailsServiceImpl")
	private Gstr3bQtrFilingDetailsServiceImpl gstr3bQtrFilingServiceImpl;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinRepo;

	@Autowired
	@Qualifier("Gstr3bQtrFilingPayloadServiceImpl")
	private Gstr3bQtrFilingPayloadService payloadService;

	@Override
	public void execute(Message message, AppExecContext ctx) {

		String jsonString = message.getParamsJson();
		
		JsonObject requestObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();
		String payloadId = requestObject.get("payloadId").getAsString();
		String groupCode = message.getGroupCode();

		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("FetchGstinValidationDetailsApiPushProcessor Job"
								+ " executing for params %s", jsonString);
				LOGGER.debug(msg);
			}

			gstr3bQtrFilingServiceImpl.fetchGstr3bQtrFilingDetails(payloadId,
					groupCode);

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
