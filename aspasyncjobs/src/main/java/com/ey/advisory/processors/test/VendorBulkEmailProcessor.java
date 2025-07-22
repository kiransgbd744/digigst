package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.VendorEmailStatusRepository;
import com.ey.advisory.app.services.vendorcomm.AsyncVendorBulkEmailService;
import com.ey.advisory.app.services.vendorcomm.VendorEmailCommunicationService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("VendorBulkEmailProcessor")
public class VendorBulkEmailProcessor implements TaskProcessor{
	
	@Autowired
	@Qualifier("VendorEmailStatusRepository")
	VendorEmailStatusRepository vdEmailStatusRepo;
	
	@Autowired
	@Qualifier("VendorEmailCommunicationServiceImpl")
	private VendorEmailCommunicationService vendorEmailCommunicationService;
	
	@Autowired
	@Qualifier("AsyncVendorBulkEmailServiceImpl")
	private AsyncVendorBulkEmailService asyncVendorBulkEmailService;


	
	@Override
	public void execute(Message message, AppExecContext context) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Begin VendorBulkEmailProcessor :%s",
					message.toString());
			LOGGER.debug(msg);
		}
		Long id = null;
		Long entityId = null;
		Long requestId = null;
		try {
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();

			id = json.get("id") != null
					? json.get("id").getAsLong() : null;
			entityId = json.get("entityId").getAsLong();
			requestId = json.get("requestID").getAsLong();

			vdEmailStatusRepo.updateStatus(id, "InProgress");
	      asyncVendorBulkEmailService.sendAll(id, entityId, requestId);
	      
	       vdEmailStatusRepo.updateStatus(id, "COMPLETED");
			 
		} catch (Exception ex) {
			String msg = String.format(
					"Error while Executing VendorBulkEmailProcessor to call "
							+ " proc for id :%s group code :%s and entityId :%s",
					id, message.getGroupCode(), entityId, requestId);
			vdEmailStatusRepo.updateStatus(id, "FAILED");
			LOGGER.error(msg, ex);
			throw new AppException(msg);

		}
	}

}
