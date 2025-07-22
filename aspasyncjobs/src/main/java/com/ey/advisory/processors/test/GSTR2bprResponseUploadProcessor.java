package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.gstr2bpr.reconresponse.upload.GSTR2bprReconRespUploadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jithendra.B
 *
 */
@Slf4j
@Component("GSTR2bprResponseUploadProcessor")
public class GSTR2bprResponseUploadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("GSTR2bprReconRespUploadServiceImpl")
	private GSTR2bprReconRespUploadService respValidateservice;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin GSTR2BPRResponseUploadProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"GSTR2BPRResponseUploadProcessor Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}

		try {
			JsonObject json = (JsonObject) parser.parse(jsonString);

			Long fileId = json.get("fileId").getAsLong();
			String fileName = json.get("fileName").getAsString();
			String folderName = json.get("filePath").getAsString();
			Long entityId = json.get("entityId").getAsLong();

			fileStatusRepository.updateFileStatus(fileId, "InProgress");

			LOGGER.debug("GSTR2B_PR Response Upload File is in progress ");

			respValidateservice.validateResponse(fileId, fileName, folderName,
					entityId);
		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in GSTR2BPRResponseUploadProcessor for %s ",
					jsonString);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

		}

	}
}
