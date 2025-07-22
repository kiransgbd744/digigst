package com.ey.advisory.processors.test;

import static com.ey.advisory.core.api.APIIdentifiers.GSTR1_SUBMIT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.docs.dto.Gstr1SubmitGstnDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr3BSubmitToGstnProcessor")
public class Gstr3BSubmitToGstnProcessor implements TaskProcessor {


	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("gstrReturnStatusRepository")
	private GstrReturnStatusRepository gstrReturnStatusRepository;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		String jsonString = message.getParamsJson();
		try {
			LOGGER.info("About to Submit GSTR3B {} ", jsonString);
			submitToGstn(jsonString, groupCode);
			LOGGER.info("Completed Submit GSTR3B Job {} ", jsonString);
		} catch (Exception ex) {
			String errMsg = "Exception while GSTR3B Submit";
			LOGGER.error(errMsg, ex);
			throw new AppException(ex.getMessage());
		}

	}

	public void submitToGstn(String jsonString, String groupCode) {
		JsonObject requestObject = (new JsonParser()).parse(jsonString)
				.getAsJsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		APIResponse resp = null;
		try {
			Gstr1SubmitGstnDto dto = gson.fromJson(requestObject,
					Gstr1SubmitGstnDto.class);
			String gstin = dto.getGstin();
			String taxPeriod = dto.getRet_period();
			StringBuilder sb = new StringBuilder();
			sb.append("{'data':{'gstin':'");
			sb.append(gstin);
			sb.append("',");
			sb.append("'ret_period':'");
			sb.append(taxPeriod);
			sb.append("'}}");
			String data = sb.toString();
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param2 = new APIParam("ret_period", taxPeriod);
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					GSTR1_SUBMIT, param1, param2);
			resp = apiExecutor.execute(params, data);
			if (resp.isSuccess()) {
				String saveJsonResp = resp.getResponse();
				LOGGER.debug(
						"GSTR3B Submitted Successfully for params {} and Response is {}",
						jsonString, saveJsonResp);
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = (JsonObject) jsonParser
						.parse(saveJsonResp);
				String refId = jsonObject.get("reference_id").getAsString();
				gstrReturnStatusRepository.updateReturnStatusWithSubmitId(
						APIConstants.SUBMITTED, dto.getGstin(),
						dto.getRet_period(), refId);
			} else {
				LOGGER.error(
						"GSTR3B Submit Failed for params {} and Error Response is {}",
						jsonString, resp.getErrors());
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Error in Gstn API call.", ex);
		}
	}

}
