package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.services.compliancerating.VendorDueDateUploadService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * This Processor handles two request,SAPAPI and ADMINAPI
 * 
 * @author Jithendra.B
 *
 */

@Slf4j
@Component("VendorDueDateUploadProcessor")
public class VendorDueDateUploadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Autowired
	@Qualifier("VendorDueDateUploadServiceImpl")
	private VendorDueDateUploadService uploadService;

	@Override
	public void execute(Message message, AppExecContext context) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begin VendorDueDateUploadProcessor job");
		}
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		JsonParser parser = new JsonParser();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"VendorDueDateUploadProcessor Upload Job"
							+ " executing for groupcode %s and params %s",
					groupCode, jsonString);
			LOGGER.debug(msg);
		}

		try {
			JsonObject json = (JsonObject) parser.parse(jsonString);

			Long fileId = json.get("fileId").getAsLong();
			String fileName = json.get("fileName").getAsString();
			String folderName = json.get("folderName").getAsString();
			/*
			 * If entityId is null request is from ADMINAPI,otherwise SAPAPI
			 */
			Long entityId = json.get("entityId") != null
					? json.get("entityId").getAsLong() : null;

			fileStatusRepository.updateFileStatus(fileId, "InProgress");

			LOGGER.debug("Vendor Due Date Upload File is in progress ");

			uploadService.validateVendorDueDateFile(fileId, fileName,
					folderName, entityId);

		} catch (Exception ex) {
			String msg = String.format(
					"Error occured VendorDueDateUploadProcessor for %s ",
					jsonString);
			LOGGER.error(msg, ex);
			throw new AppException(ex);

		}

	}

}
