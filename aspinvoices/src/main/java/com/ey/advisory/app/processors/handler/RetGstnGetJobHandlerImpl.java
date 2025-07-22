/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.services.jobs.ret.RetInvoicesAtGstn;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.core.dto.RetGetInvoicesReqDto;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("RetGstnGetJobHandlerImpl")
@Slf4j
public class RetGstnGetJobHandlerImpl implements RetGstnGetJobHandler {

	@Autowired
	@Qualifier("RetGetInvoicesAtGstnImpl")
	private RetInvoicesAtGstn retInvoicesAtGstn;

	@Override
	public void retGstnGetCall(String jsonReq, String groupCode) {

		JsonObject requestObject = (new JsonParser()).parse(jsonReq)
				.getAsJsonObject();
		JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();

		Gson gson = GsonUtil.newSAPGsonInstance();
		Type listType = new TypeToken<List<RetGetInvoicesReqDto>>() {
		}.getType();
		List<RetGetInvoicesReqDto> dtos = gson.fromJson(asJsonArray, listType);

		dtos.forEach(dto -> {

			// Need to call return filling status
			// api(GstnReturnFilingStatus.java)
			// If it is submitted/filed then after submit/file, the very first
			// time
			// we need to call the GET API's and next time not required to call.

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("RET Get Call is Started with req.", dto);
			}
			retInvoicesAtGstn.findInvFromGstn(dtos.get(0), groupCode);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("RET Get Call is Ended with req.", dto);
			}

		});

	}
}
