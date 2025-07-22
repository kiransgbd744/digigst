/*package com.ey.advisory.app.services.jobs.gstr2a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.gstr2.GetAnx1BatchRepository;
import com.ey.advisory.app.docs.dto.anx1.Gstr1GetInvoicesReqDto;
import com.ey.advisory.app.services.jobs.anx2.Anx2GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

*//**
 * 
 * @author Santosh.Gururaj
 *
 *//*
@Slf4j
@Component("gstr2aIsdIsdaInvoicesAtGstnImpl")
public class Gstr2aIsdIsdaInvoicesAtGstnImpl
		implements Gstr2aInvoicesAtGstn {

	@Autowired
	@Qualifier("Gstr2aDataAtGstnImpl")
	private Gstr2aDataAtGstn gstr2aDataAtGstn;

	@Autowired
	@Qualifier("gstr2aIsdIsdaDataParserImpl")
	private Gstr2aIsdIsdaDataParser gstr2aIsdIsdaDataParser;

	@Autowired
	private Gstr2aGetIsdInvoicesAtGstnRepository repository;
	
	@Autowired
	@Qualifier("getAnx1BatchRepository")
	private GetAnx1BatchRepository batchRepo;
	
	@Autowired
	@Qualifier("Anx2GetBatchUtil")
	private Anx2GetBatchUtil batchUtil;

	@Override
	public Long findInvFromGstn(Gstr1GetInvoicesReqDto dto,
			final String groupCode, final String type,Long batchId) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("I am from findISDFromGstn Method type: {}", type);
		}
		Long reqId = null;
		Gson gson = GsonUtil.newSAPGsonInstance();
	//	GetAnx1BatchEntity batch;
		try {
			JsonObject requestObject = (new JsonParser().parse(jsonReq))
					.getAsJsonObject();
			JsonObject reqObject = requestObject.get("req").getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(reqObject,
					Gstr1GetInvoicesReqDto.class);
			
			batch = batchUtil.makeBatchForGstr2B2b(dto, type);
			TenantContext.setTenantId(groupCode);
			//InActiveting Previous Batch Records 
			batchRepo.softlyDelete(type.toUpperCase(), 
					APIConstants.GSTR2A.toUpperCase(),dto.getGstin(), 
					dto.getReturnPeriod());
			//Save new Batch
			batch = batchRepo.save(batch);
			if(dto.getGroupcode() == null || dto.getGroupcode().trim().length() == 0) {
				dto.setGroupcode(groupCode);
				}
				dto.setBatchId(batchId);
				dto.setType(type);
				String json = gson.toJson(dto);
			
				reqId = gstr2aDataAtGstn.findGstr2aDataAtGstn(dto,
					groupCode, type,json);
			if (apiResp != null) {
				List<GetGstr2aIsdInvoicesEntity> entities = gstr2aIsdIsdaDataParser
						.parseIsdData(dto, apiResp, type,json);
				if (!entities.isEmpty()) {
					TenantContext.setTenantId(groupCode);
					// InActiveting Previous Header Records
					repository.softlyDeleteIsdHeader(dto.getGstin(),
							dto.getReturnPeriod());
					repository.saveAll(entities);
				}
			}
			JsonElement respBody = gson.toJsonTree(apiResp);
			resp1.add("resp", respBody);
		} catch (Exception ex) {

	throw new AppException(ex);

}
return reqId;
	}

}
*/