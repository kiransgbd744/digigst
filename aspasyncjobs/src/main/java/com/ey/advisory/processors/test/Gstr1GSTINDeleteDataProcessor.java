package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.gstr1.einv.Gstr1GSTINDeleteDataService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr1GSTINDeleteDataProcessor")
public class Gstr1GSTINDeleteDataProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1GSTINDeleteDataServiceImpl")
	private Gstr1GSTINDeleteDataService gstr1GSTINDeleteDataService;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		LOGGER.debug("Begin Gstr1GSTINDeleteDataProcessor job");

		try {
			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr1GSTINDeleteDataProcessor User Upload Job"
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
					"GSTIN Delete response upload File status is in progress ");
			gstr1GSTINDeleteDataService.validateandSaveFileData(fileId,
					fileName, folderName);
		} catch (Exception e) {
			String msg = "Error Occurred in GSTIN Delete Processor.";
			LOGGER.error(msg, e);
			throw new AppException(msg, e);

		}
	}

}
