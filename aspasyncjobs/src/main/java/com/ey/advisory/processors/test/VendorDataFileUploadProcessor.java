package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.itcmatching.vendorupload.ItcMatchingVendorDataFileUpld;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Rajesh N K
 *
 */
@Slf4j
@Component("VendorDataFileUploadProcessor")
public class VendorDataFileUploadProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ItcMatchingVendorDataFileUpld")
	private ItcMatchingVendorDataFileUpld vendorDataFileUpload;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository fileStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {

		try {
			LOGGER.debug("Begin VendorDataFileUploadProcessor job");

			String groupCode = message.getGroupCode();
			String jsonString = message.getParamsJson();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Vendor DataFile Upload Job"
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

			LOGGER.debug(" Vendor DataFile Upload status is in progress ");

			vendorDataFileUpload.validateDataFileUpload(fileId, fileName,
					folderName);
		} catch (Exception e) {
			String msg = "VendorDataFileUploadProcessor got interrupted. "
					+ "Jobs might not be completed. Marking as 'Failed'";
			LOGGER.error(msg);
			throw new AppException(msg, e);
		}
	}
}
