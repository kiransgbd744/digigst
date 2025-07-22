package com.ey.advisory.processors.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.gstr3b.Gstr3bGenerate3BService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("Gstr3bDigiCalculateProcessor")
@Slf4j
public class Gstr3bDigiCalculateProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr3bGenerate3BServiceImpl")
	private Gstr3bGenerate3BService gstr3bGstinService;
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstinInfoRepository;

	@Override
	public void execute(Message message, AppExecContext context) {
		try {
			String groupCode = message.getGroupCode();
			JsonParser parser = new JsonParser();
			String jsonString = message.getParamsJson();
			JsonObject json = (JsonObject) parser.parse(jsonString);
			String gstnDetails = json.get("gstins").getAsString();
			List<String> gstinList = new ArrayList<String>(
					Arrays.asList(gstnDetails.split(",")));
			String taxPeriod = json.get("retPeriod").getAsString();
			
			GSTNDetailEntity gstinDetails = gstinInfoRepository.findByGstinAndIsDeleteFalse(gstinList.get(0));
			 
			
			for (String gstin : gstinList) {
				String respList = gstr3bGstinService
						.getGstr3bGenerateList(taxPeriod, gstin,gstinDetails.getEntityId());
			}

		} catch (Exception ex) {
			String errMsg = "Error occured while invoking GET Summary call.";
			LOGGER.error(errMsg, ex);
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);
		}
	}
}
