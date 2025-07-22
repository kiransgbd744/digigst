/**
 * 
 */
package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.gstr8A.Gstr8AGetCallServiceImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun.KA
 *
 */

@Component("Gstr8aInitiateGetCallProcessor")
@Slf4j
public class Gstr8aInitiateGetCallProcessor implements TaskProcessor {
	
	@Autowired
	Gstr8AGetCallServiceImpl gstr8AGetCallServiceImpl;

	@Override
	public void execute(Message message, AppExecContext context) {
		
		try{
			String jsonString = message.getParamsJson();
			JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
	        String gstin=null;
	        String fy=null;
	        if(jsonObject.has("gstin") && !jsonObject.get("gstin").isJsonNull()){
	             gstin = jsonObject.get("gstin").getAsString();
	        }

	        if(jsonObject.has("fy") && !jsonObject.get("fy").isJsonNull()){
	             fy = jsonObject.get("fy").getAsString();
	        }
	        
	        if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("Inside gstr8a initiate get call processor. GSTIN: {}, FY: {}", gstin, fy);
	        }
	        
	        if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("Inside gstr8a initiate get call processor. jsonString: {} and jsonObject: {}",
	                    jsonString, jsonObject);
	        }

		/*String gstin = json.get("gstin").getAsString();
		String fy = json.get("fy").getAsString();*/
		
		gstr8AGetCallServiceImpl.getCall(gstin, fy, message.getUserName());
		}catch(Exception e){
			String msg = "Exception occured while invoking GSTR8A get call";
			LOGGER.error(msg);
			throw new AppException(msg, e);
		}	
	}
}
