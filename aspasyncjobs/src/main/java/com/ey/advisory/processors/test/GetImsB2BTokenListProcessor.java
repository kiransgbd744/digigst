package com.ey.advisory.processors.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.ims.handlers.ImsInvoicesDataParser;
import com.ey.advisory.app.ims.handlers.ImsInvoicesProcCallService;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *
 */
@Service("GetImsB2BTokenListProcessor")
@Slf4j
public class GetImsB2BTokenListProcessor implements TaskProcessor {

	@Autowired
	@Qualifier("ImsInvoicesDataParserImpl")
	private ImsInvoicesDataParser imsInvoiceParser;

	@Autowired
	@Qualifier("ImsInvoicesProcCallServiceImpl")
	private ImsInvoicesProcCallService imsProcCallInvoiceParser;

	
	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;
	
	@Override
	public void execute(Message message, AppExecContext context) {
		
		List<Long> resultIds = new ArrayList<>();
		Long batchId = null;
		try {
			String jsonParam = null;
			if (LOGGER.isDebugEnabled()) {
				String logMsg = String.format(
						"Executing "
								+ " ImsB2BTokenCaseProcessor for Group %s",
						TenantContext.getTenantId());
				LOGGER.debug(logMsg);
			}
			
			String jsonString = message.getParamsJson();
			JsonObject json = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			for (JsonElement element : json.get("resultIds").getAsJsonArray()) {
				Long resultId = element.getAsLong();
				resultIds.add(resultId);
			}
			String ctxParams = json.get("ctxParams").getAsString();
			Long transactionId = json.get("transactionId").getAsLong();
			
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();

			String apiSection = dto.getApiSection();
			if (APIConstants.IMS_INVOICE.equalsIgnoreCase(apiSection)) {
				imsInvoiceParser.parseImsInvoicesData(resultIds, dto, batchId,null);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"calling dup check proc and update for batchid {}  "
								+ " and section {}",
						batchId, dto.getType());
			}

			if (APIConstants.IMS_INVOICE.equalsIgnoreCase(apiSection)) {
				imsProcCallInvoiceParser.procCall(dto, batchId);
			}
			batchUtil.updateById(batchId,APIConstants.SUCCESS, null,null, false);
			
		} catch (Exception ex) {
			LOGGER.error("Error while parsing irn list {}",ex);
			batchUtil.updateById(batchId,
					APIConstants.FAILED, null,"Error while parsing irn list", false);
			
			throw new AppException();
		}
	}
}
