package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.service.ims.ImsRespUploadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Component("IMSResponseUploadProcessor")
public class IMSResponseUploadProcessor implements TaskProcessor {

	/*
	 * @Autowired
	 * 
	 * @Qualifier("GSTR2bprReconRespUploadServiceImpl") private
	 * GSTR2bprReconRespUploadService respValidateservice;
	 */

	@Autowired
	@Qualifier("ImsRespUploadServiceImpl")
	private ImsRespUploadService respValidateservice;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin IMSResponseUploadProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"IMSResponseUploadProcessor Upload Job"
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

			LOGGER.debug("IMS Response Upload File is in progress ");

			respValidateservice.validateResponse(fileId, fileName, folderName);
		} catch (Exception ex) {
			String msg = String.format(
					"Error occured in IMSResponseUploadProcessor for %s ",
					jsonString);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);

		}

	}
}
