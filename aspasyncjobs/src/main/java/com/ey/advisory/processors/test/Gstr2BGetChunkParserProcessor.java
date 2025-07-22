package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.ControlGstnGetStatusRepository;
import com.ey.advisory.app.gstr2b.Gstr2BGETDataDto;
import com.ey.advisory.app.gstr2b.Gstr2RespParser;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr2BGetChunkParserProcessor")
public class Gstr2BGetChunkParserProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("Gstr2RespParserImpl")
	private Gstr2RespParser parser;

	@Autowired
	@Qualifier("ControlGstnGetStatusRepository")
	private ControlGstnGetStatusRepository controlGstnGetStatusRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		Gson gson = new Gson();
		String jsonString = message.getParamsJson();

		Long controlId = null;
		List<Long> resultIds = null;
		Long invocationId = null;
		try {
			JsonObject requestObject = (new JsonParser()).parse(jsonString)
					.getAsJsonObject();
			JsonArray idArray = requestObject.get("jsonIds").getAsJsonArray();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Received request::{}", requestObject.toString());
			}

			Type listType = new TypeToken<ArrayList<Long>>() {
			}.getType();
			resultIds = gson.fromJson(idArray, listType);
			controlId = requestObject.get("controlId").getAsLong();
			invocationId = requestObject.get("invId").getAsLong();

			controlGstnGetStatusRepo.updateJobStatus(APIConstants.INPROGRESS,
					LocalDateTime.now(), controlId);

			for (Long id : resultIds) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Started parsing for id::{}", id);
				}
				String apiResp = APIInvokerUtil.getResultById(id);

				Gstr2BGETDataDto reqDto = gson.fromJson(apiResp,
						Gstr2BGETDataDto.class);

				parser.pasrseResp(false, reqDto, invocationId);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Completed parsing for id::{}", id);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Completed parsing for all ids, marking status as"
						+ " 'COMPLETED' in Control table");
			}

			controlGstnGetStatusRepo.updateJobStatus(APIConstants.COMPLETED,
					LocalDateTime.now(), controlId);
		} catch (Exception ee) {
			String msg = ee.getMessage().length() > 200
					? ee.getMessage().substring(0, 198) : ee.getMessage();
			controlGstnGetStatusRepo.updateJobStatusAndErrorDesc(
					APIConstants.FAILED, msg, LocalDateTime.now(), controlId);
			LOGGER.error("Error while parsing jsons ", ee);
			LOGGER.error("Result Ids FAILED: {}", resultIds.toString());
			throw new AppException(ee);
		}
	}
}
