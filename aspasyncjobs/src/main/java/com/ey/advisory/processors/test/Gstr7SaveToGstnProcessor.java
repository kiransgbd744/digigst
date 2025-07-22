package com.ey.advisory.processors.test;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.processors.handler.Gstr7SaveToGstnJobHandler;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */

@Slf4j
@Component("Gstr7SaveToGstnProcessor")
public class Gstr7SaveToGstnProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr7SaveToGstnJobHandler")
	private Gstr7SaveToGstnJobHandler gstr7JobHandler;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Gstr7 SaveToGstn Data Execute method is ON with "
							+ "groupcode {} and params {}",
					groupCode, jsonString);
		}
		if (jsonString != null && groupCode != null) {
			try {
				Map<String, Config> confiMap = configManager.getConfigs("GSTR7",
						"gstr7.transactional.cutOff", "DEFAULT");

				String cutOffRetPeriod = confiMap
						.get("gstr7.transactional.cutOff") == null ? "202506"
								: confiMap.get("gstr7.transactional.cutOff")
										.getValue();

				Integer cutOffRetPeriodInt = GenUtil
						.convertTaxPeriodToInt(cutOffRetPeriod);
				LOGGER.error("ReturnPeriod {} ", cutOffRetPeriod);
				JsonObject requestObject = JsonParser.parseString(jsonString)
						.getAsJsonObject();
				Gson gson = GsonUtil.newSAPGsonInstance();
				SaveToGstnReqDto dto = gson.fromJson(requestObject,
						SaveToGstnReqDto.class);
				String retPeriod = dto.getReturnPeriod();
				Integer currentRetPeriodInt = GenUtil
						.convertTaxPeriodToInt(retPeriod);
				
				ProcessingContext gstr7Context = new ProcessingContext();
				gstr7Context.seAttribute(APIConstants.TRANSACTIONAL,
						currentRetPeriodInt < cutOffRetPeriodInt ? false
								: true);
				gstr7JobHandler.saveCancelledInvoices(jsonString, groupCode,
						gstr7Context);
				gstr7JobHandler.saveActiveInvoices(jsonString, groupCode,
						gstr7Context);
				LOGGER.info("Save to Gstn Processed with args {} ", jsonString);
			} catch (Exception ex) {
				String msg = "Exception occurred in GSTR7 Save to Gstn.";
				LOGGER.error(msg, ex);
				throw new AppException(msg, ex);
			}
		}
	}

}
