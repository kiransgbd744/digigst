package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.GSTR2AAutoReconRespUploadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Vishal.Verma
 *
 */
@Slf4j
@Component("GSTR2aprImsResponseUploadProcessor")
public class GSTR2aprImsResponseUploadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("GSTR2aprImsReconRespUploadServiceImpl")
	private GSTR2AAutoReconRespUploadService service;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin GSTR2aprImsResponseUploadProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR2aprImsResponseUploadProcessor Upload Job"
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

			LOGGER.debug("GSTR2A IMS Response Upload File is in progress ");

			service.validateResponse(fileId, fileName,
			 folderName);
		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in GSTR2aprImsResponseUploadProcessor for %s ",
					jsonString);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

		}

	}
}
