package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.qrcodevalidator.QRCodeValidatorService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */
@Slf4j
@Component("QrCodeValidatorProcessor")
public class QrCodeValidatorProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("QRCodeValidatorServiceImpl")
	QRCodeValidatorService qrCodeService;

	@Override
	public void execute(Message message, AppExecContext context) {

		try {
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			Long id = json.get("id").getAsLong();

			String fileType = json.get("fileType").getAsString();

			String optedAns = json.has("optedAns")
					? json.get("optedAns").getAsString() : null;
					
			String uploadType=json.get("uploadType").getAsString();
			
			String entityId = json.has("entityId") 
					? json.get("entityId").getAsString() : null;

			qrCodeService.saveAndPersistQRReports(id, fileType, optedAns, uploadType , entityId);

		} catch (Exception e) {
			String errMsg = "Error Occured in QrCodeValidatorProcessor";
			LOGGER.error(errMsg, e);

			throw new AppException(errMsg, e,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}

	}

}
