package com.ey.advisory.processors.test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetGstinVendorMasterDetailRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorMasterUploadEntityRepository;
import com.ey.advisory.app.data.services.noncomplaintvendor.VendorInitiateGetCallService;
import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("InitiateGetVendorGstnDetailsProcessor")
@Slf4j
public class InitiateGetVendorGstnDetailsProcessor implements TaskProcessor {


	@Autowired
	@Qualifier("VendorInitiateGetCallServiceImpl")
	private VendorInitiateGetCallService vendorInitiateGetCallServiceImpl;
	
	@Autowired
	@Qualifier("entityInfoRepository")
	private EntityInfoRepository entityInfoRepository;
	
	@Autowired
	@Qualifier("VendorMasterUploadEntityRepository")
	private VendorMasterUploadEntityRepository vendorMasterUploadEntityRepository;
	
	@Autowired
	@Qualifier("GetGstinVendorMasterDetailRepository")
	private GetGstinVendorMasterDetailRepository getGstinVendorMasterDetailRepository;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
		List<String> vendorGstins = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		String entityId1 = json.get("entityId").getAsString();
		long entityId = Long.parseLong(entityId1);
		//Long entityId=json.get("entityId").getAsLong();
		JsonArray vendorGstinsArray = json.getAsJsonArray("vendorGstins");
		
		
		if (vendorGstinsArray!=null) {
			 if (LOGGER.isDebugEnabled()) {
				    String msg = String.format(
				            "Inside processor with vendorGstinsArray: %s",
				            vendorGstinsArray);
				    LOGGER.debug(msg);
				}
			 
		        for (JsonElement element : vendorGstinsArray) {
		        	vendorGstins.add(element.getAsString());
		        }        
		        if (LOGGER.isDebugEnabled()) {
		            String msg = String.format(
		                    "Inside processor with vendorGstins received from controller: %s",
		                    vendorGstins);
		            LOGGER.debug(msg);
		        }
			
		}else
		{

			List<String> recipientPanList = entityInfoRepository
					.findPanByEntityId(entityId);
			List<VendorMasterUploadEntity> masterEntities = vendorMasterUploadEntityRepository
					.findByIsDeleteFalseAndRecipientPANIn(recipientPanList);
			vendorGstins.addAll(masterEntities.stream()
					.map(VendorMasterUploadEntity::getVendorGstin)
					.distinct()
					.collect(Collectors.toList()));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("insidhe the method execute method  "
						+ " of controller InitiateGetVendorGstnDetailsProcessor and getting all the vendor master gstins: %d ",vendorGstins);
			}
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("before calling initiate get call");
		}
		
	/*	String lastGetCallStatus="In Progress";
		getGstinVendorMasterDetailRepository.updatingInitiateGetCallStatus(vendorGstins, lastGetCallStatus);
		*/
		vendorInitiateGetCallServiceImpl.initiateGetCallForSelectedGstin(vendorGstins,entityId);
		

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("after  calling initiate get call");
		}
		
		} catch (Exception ee) {
			String msg = "Error while fetching gstins from get call";
			LOGGER.error(msg, ee);
			throw new AppException(ee.getMessage());	
		}
	}

}
