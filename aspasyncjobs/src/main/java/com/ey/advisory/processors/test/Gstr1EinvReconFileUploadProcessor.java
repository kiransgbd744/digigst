package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.services.gstr1fileupload.Gstr1vsEinvReconFileUploadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1EinvReconFileUploadProcessor")
public class Gstr1EinvReconFileUploadProcessor implements TaskProcessor {

	@Autowired
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	Gstr1vsEinvReconFileUploadService gstr1vsEinvReconFileUploadService;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Executing Gstr1EinvReconFileUploadProcessor job");
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		try {

			JsonObject json = (JsonObject) parser.parse(jsonString);

			Long fileId = json.get("fileId").getAsLong();
			String fileName = json.get("fileName").getAsString();
			String folderName = json.get("filePath").getAsString();

			fileStatusRepository.updateFileStatus(fileId, "InProgress");

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"GSTR1 Einv Recon Upload Job"
								+ " executing for groupcode %s and params %s",
						groupCode, jsonString);
				LOGGER.debug(msg);
			}

			gstr1vsEinvReconFileUploadService
					.validateGstr1vsEinvReconFile(fileId, fileName, folderName);

			LOGGER.debug(" GSTR1 Einv Recon Upload status is Completed ");
		} catch (Exception e) {
			String msg = "Gstr1EinvReconFileUploadProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, e);
		}
	}
}
