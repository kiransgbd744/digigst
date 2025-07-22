package com.ey.advisory.processors.test;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.common.GstnValidatorConstants;
import com.ey.advisory.app.data.repositories.client.ReturnFilingConfigRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.ReturnFilingFileArrivalHandler;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ReturnFilingCounterPartyProcessor")
public class ReturnFilingCounterPartyProcessor implements TaskProcessor {

	@Autowired
	ReturnFilingFileArrivalHandler rfFileArrivalHandler;

	@Autowired
	@Qualifier("ReturnFilingConfigRepository")
	ReturnFilingConfigRepository returnFilingConfigRepo;

	@Override
	public void execute(Message message, AppExecContext context) {

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Return Filing Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long requestId = json.get("requestID").getAsLong();
		String fileName = json.get("fileName").getAsString();
		String folderName = json.get("folderName").getAsString();
		String docId = json.get("docId").getAsString();
		String filingFrequency=json.get("filingFrequency").getAsString();
		try {

			returnFilingConfigRepo.updateReturnFilingStatus(
					GstnValidatorConstants.INITIATED_STATUS, requestId);

			Pair<String,String> gstinValidatorOnFile = rfFileArrivalHandler.gstinValidatorOnFile(requestId,
					fileName, folderName,docId, filingFrequency);
			String fName = gstinValidatorOnFile.getValue0();
		String fDocID=	gstinValidatorOnFile.getValue1();
			
			if (fName != null) {
				returnFilingConfigRepo.updateReturnFilingDetails(
						GstnValidatorConstants.GENERATED_STATUS, fName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						null, requestId,fDocID);
			} else {
				returnFilingConfigRepo.updateReturnFilingDetails(
						GstnValidatorConstants.FAILED_STATUS, fName,
						EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
						"NO GSTINS UPLOADED", requestId,null);
			}

		} catch (Exception ae) {
			LOGGER.error("Exception while processing the ReturnFilling Upload",
					ae);
			returnFilingConfigRepo.updateReturnFilingDetails(
					GstnValidatorConstants.FAILED_STATUS, null,
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()),
					StringUtils.abbreviate(ae.getMessage(),100), requestId,null);//.truncate as there
		}

	}

}
