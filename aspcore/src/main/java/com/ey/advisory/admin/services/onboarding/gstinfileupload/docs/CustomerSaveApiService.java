package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.dto.MasterCustomerReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component("CustomerSaveApiService")
public class CustomerSaveApiService {
	private static final  Logger LOGGER = 
			LoggerFactory.getLogger(CustomerSaveApiService.class);
	

	public JsonObject saveCustomer(String jsonString) {
		LOGGER.debug("Enter in to Save Customer Method");
		JsonObject reqObject = null;
		try{
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			Gson gson = GsonUtil.newSAPGsonInstance();
			reqObject = requestObject.get("req").getAsJsonObject();
			MasterCustomerReqDto  reqDto = gson.fromJson(reqObject,
					                             MasterCustomerReqDto.class);
			
		}
		catch(Exception e){
			
		}
		
		return null;
	}

}
