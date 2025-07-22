package com.ey.advisory.processors.test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnListingRepository;
import com.ey.advisory.app.inward.einvoice.GetInwardIrnDetailSectionHandler;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

@Service("GetIrnDtlProcessor")
@Slf4j
public class GetIrnDtlProcessor implements TaskProcessor {

	@Autowired
	private GetInwardIrnDetailSectionHandler getIrnDtlGetCall;
	
	@Autowired
	private GetIrnListingRepository getIrnListingRepo;

	@Override
	public void execute(Message message, AppExecContext context) {
		String groupCode = message.getGroupCode();
		TenantContext.setTenantId(groupCode);
		String jsonString = message.getParamsJson();
try{
		Gson gson = new Gson();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Get IRN Details Data Execute method with "
							+ "groupcode {}",
					groupCode);
		}
		
       
		JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

		// Extract batchId directly
		Long batchId = jsonObject.has("batchId") && !jsonObject.get("batchId").isJsonNull()
		        ? jsonObject.get("batchId").getAsLong()
		        : null;

		// Extract and deserialize the dtoList
		JsonArray dtoJsonArray = jsonObject.getAsJsonArray("dtoList");
		Type listType = new TypeToken<List<Gstr1GetInvoicesReqDto>>() {}.getType();
		List<Gstr1GetInvoicesReqDto> dtoList = gson.fromJson(dtoJsonArray, listType);

		// Extract IRNs
		List<String> irnList = new ArrayList<>();
		for (Gstr1GetInvoicesReqDto dto : dtoList) {
		    if (dto.getIrn() != null) {
		        irnList.add(dto.getIrn());
		    }
		}
		
			if(LOGGER.isDebugEnabled())
			{
			LOGGER.debug("Extracted IRNs: {}", irnList.size());
			}
			getIrnDtlGetCall.getIrnJsonDtl(irnList, dtoList, groupCode,batchId);

}catch(Exception ex)
{
LOGGER.error(" error in parsing the irn {} ",ex);
throw new AppException(ex);
}

		// below are exixting old code
		/*
		 * try { String groupCode = message.getGroupCode();
		 * TenantContext.setTenantId(groupCode);
		 * 
		 * String jsonString = message.getParamsJson(); if
		 * (LOGGER.isDebugEnabled()) { LOGGER.debug(
		 * "Get IRN Details Data Execute method with " +
		 * "groupcode {} and params {}", groupCode, jsonString); }
		 * 
		 * Gstr1GetInvoicesReqDto dto = new Gstr1GetInvoicesReqDto();
		 * 
		 * 
		 * 
		 * getIrnListingRepo.updateGetDtlSts(dto.getIrn(), "Initiated",
		 * dto.getIrnSts());
		 * 
		 * if (jsonString != null && groupCode != null) { JsonObject obj =
		 * JsonParser.parseString(jsonString).getAsJsonObject();
		 * 
		 * Gson gson = new Gson();
		 * 
		 * dto = gson.fromJson(obj, Gstr1GetInvoicesReqDto.class);
		 * 
		 * getIrnDtlGetCall.getIrnDtl(dto, groupCode);
		 * 
		 * if (LOGGER.isDebugEnabled()) {
		 * LOGGER.debug("Get IRN Details Gstn Processed with args {} ",
		 * jsonString); } } catch (Exception ex) { String msg =
		 * "GetIrnDtlProcessor got interrupted. " +
		 * "Jobs might not be completed. Marking as 'Failed'";
		 * 
		 * LOGGER.error(msg); getIrnListingRepo.updateGetDtlSts(dto.getIrn(),
		 * "FAILED", dto.getIrnSts()); throw new AppException(msg, ex); }
		 */
	}
}
