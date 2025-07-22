package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.service.days.revarsal180.Reversal180DaysResponseUploadServiceImpl;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("Reversal180DaysResponseUploadProcessor")
public class Reversal180DaysResponseUploadProcessor implements TaskProcessor {
	
	@Autowired
	@Qualifier("Reversal180DaysResponseUploadServiceImpl")
	private Reversal180DaysResponseUploadServiceImpl responseImpl;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin Reversal180DaysResponseUploadProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Revarsal180DaysResponseUploadProcessor Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}

		try {
			JsonObject json = (JsonObject) parser.parse(jsonString);

			Long fileId = json.get("fileId").getAsLong();
			String fileName = json.get("fileName").getAsString();
			String folderName = json.get("filePath").getAsString();
			fileStatusRepository.updateFileStatus(fileId, "InProgress");

			LOGGER.debug(
					"Revarsal 180Days Response Upload File is in progress ");

			responseImpl.validateReversalResponseFile(fileId, fileName,
					folderName);
		} catch (Exception ex) {
			String msg = String.format(
					"Error occured Revarsal180DaysResponseUploadProcessor for %s ",
					jsonString);
			LOGGER.error(msg, ex);
			throw new AppException(ex);

		}

	}

}
