package com.ey.advisory.processors.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.services.jobs.erp.Gstr3BDetailsRevIntegerationHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.dto.RevIntegrationScenarioTriggerDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr3BDetailsRevIntegrationProcessor")
public class Gstr3BDetailsRevIntegrationProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr3BDetailsRevIntegerationHandler")
	private Gstr3BDetailsRevIntegerationHandler gstin3BRevIntegHandler;
	
	@Override
	public void execute(Message message, AppExecContext context) {
		String groupcode = message.getGroupCode();
		String json = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("GSTR3B RevIntegeration Job"
				+ " executing for goupcode %s and params %s", groupcode, json);
			LOGGER.debug(msg);
		}	
		Gson gson = GsonUtil.newSAPGsonInstance();
		
        /*
         * This Dto is Trigger and Dta we get from the Message Object as 
         * it contains JSON Param column that contains all details
         * like groupCode,Entity ID so we mapp that Parameters
         * to RevIntegrationScenarioTriggerDto
         */
		RevIntegrationScenarioTriggerDto revIntegerationDto = gson.fromJson(json,
				RevIntegrationScenarioTriggerDto.class);
		
		// Destination Name is Final Point were Data is to be pushed to ERP
		String destinationName = revIntegerationDto.getDestinationName();
		
		Long entityID = revIntegerationDto.getEntityId();
		
		
		String gstin = revIntegerationDto.getGstin();

		//The DestinationName,GroupCode and Entity Id is Mandatory
		if (groupcode != null && destinationName != null && entityID != null
				&& gstin != null) {
			Integer respcode = gstin3BRevIntegHandler
					.gstr3bDetailsToErp(revIntegerationDto);
		} else {
			LOGGER.debug("Partial Request Params groupcode, entityId, "
					+ "destinationName, returnPeriod and gstin are mandatory for"
					+ " 3B RevIntegeration"
					, revIntegerationDto);
            String msg = "Destination Name, Group Code,Gstin and "
            		+ "EntityID cannot be Empty for 3B RevIntegeration";
			LOGGER.error(msg);
			throw new AppException(msg);
		}
			
			
	}
		
}
