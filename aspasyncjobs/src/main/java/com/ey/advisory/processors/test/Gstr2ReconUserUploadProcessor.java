package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.asprecon.gstr2.reconresponse.upload.ResponseFileArrival;
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
@Component("Gstr2ReconUserUploadProcessor")
public class Gstr2ReconUserUploadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ResponseFileArrival")
	private ResponseFileArrival getReconResponseFile;
	
	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;
	
	@Override
	public void execute(Message message, AppExecContext context) {
		
		LOGGER.debug("Begin Gstr2ReconUserUploadProcessor job");
		
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Recon User Upload Job"
				+ " executing for groupcode %s and params %s", 
				groupCode, jsonString);
			LOGGER.debug(msg);
		}
		JsonParser parser = new JsonParser();
		JsonObject json = (JsonObject) parser.parse(jsonString);
		
		Long fileId = json.get("fileId").getAsLong();
		String fileName =  json.get("fileName").getAsString();
		String folderName = json.get("folderName").getAsString();
		fileStatusRepository.updateFileStatus(fileId, "InProgress");
		
		LOGGER.debug("Recon user response upload File status is in progress ");
		
		getReconResponseFile.validateReconResponse(fileId, fileName, folderName);
		
		
	}

}
