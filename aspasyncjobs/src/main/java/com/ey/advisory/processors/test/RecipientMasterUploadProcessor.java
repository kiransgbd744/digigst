package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.recipientmasterupload.RecipientMasterUploadAsyncServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hema G M
 *
 */
@Slf4j
@Component("RecipientMasterUploadProcessor")
public class RecipientMasterUploadProcessor implements TaskProcessor {
	@Autowired
	@Qualifier("RecipientMasterUploadAsyncServiceImpl")
	private RecipientMasterUploadAsyncServiceImpl recipientDataFileUpload;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		try {
			LOGGER.debug("Begin RecipientMasterUploadProcessor job");

			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"RecipientMaster DataFile Upload Job"
								+ " executing for groupcode %s and params %s",
						groupCode, jsonString);
				LOGGER.debug(msg);
			}
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(jsonString);

			Long fileId = json.get("fileId").getAsLong();
			String fileName = json.get("fileName").getAsString();
			String folderName = json.get("folderName").getAsString();
		    

			fileStatusRepository.updateFileStatus(fileId, "InProgress");

			LOGGER.debug(
					" RecipientMaster DataFile Upload status is in progress ");

			recipientDataFileUpload.validateDataFileUpload(fileId, fileName,
					folderName);
		} catch (Exception e) {
			String msg = "RecipientMasterUploadProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, e);
		}
	}

}
