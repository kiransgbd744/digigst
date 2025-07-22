package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.gstr1.einv.Gstr1VsEInvEecSaveArrivalService;
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
@Component("Gstr1VsEInvRespUploadProcessor")
public class Gstr1VsEInvRespUploadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1VsEInvEecSaveArrivalService")
	private Gstr1VsEInvEecSaveArrivalService getFile;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin Gstr1VsEInvRespUploadProcessor job");

		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Recon User Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}
		JsonParser parser = new JsonParser();
		
		try{
		JsonObject json = (JsonObject) parser.parse(jsonString);

		Long fileId = json.get("fileId").getAsLong();
		String fileName = json.get("fileName").getAsString();
		String folderName = json.get("folderName").getAsString();
		fileStatusRepository.updateFileStatus(fileId, "InProgress");

		LOGGER.debug("GSTR1 Exclusive save upload File status is in progress ");

		getFile.validateResponseFile(fileId, fileName,
				folderName);
		}catch(Exception ex){
			String msg = String.format("Error occured while processesing the "
					+ "jsonString  %s ", jsonString);
			LOGGER.error(msg, ex);
			throw new AppException(ex);
			
		}

	}

}
