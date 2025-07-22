/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.simplified.client.RequestIdWiseEntity;
import com.ey.advisory.app.data.repositories.client.RequestIdWiseRepository;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * @author Hemasundar.J
 *
 */
@Service("RequestIdWiseUtil")
public class RequestIdWiseUtil {
	
	@Autowired
	private RequestIdWiseRepository reqIdRepo;
	
	public RequestIdWiseEntity createReqIdEntity(String request, String groupCode) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = (new JsonParser()).parse(request)
				.getAsJsonObject();
		JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
		Type listType = new TypeToken<List<Anx2GetInvoicesReqDto>>() {
		}.getType();
		List<Anx2GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray, listType);
		Anx2GetInvoicesReqDto dto = dtos.get(0);
		RequestIdWiseEntity reqIdBatch = new RequestIdWiseEntity();

		reqIdBatch.setTaxPeriod(dto.getReturnPeriod());
		reqIdBatch.setStatus(APIConstants.NOT_INITIATED);
		reqIdBatch.setParams(request);
		reqIdBatch.setNoOfGstins(dtos.size());
		reqIdBatch.setInitiatedOn(LocalDateTime.now());
		reqIdBatch.setInitiatedBy(APIConstants.SYSTEM);
		
		TenantContext.setTenantId(groupCode);
		reqIdBatch = reqIdRepo.save(reqIdBatch);
		
		return reqIdBatch;
	}
	
	
	public RequestIdWiseEntity updateStatus(String request, String groupCode,
			String status) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject requestObject = (new JsonParser()).parse(request)
				.getAsJsonObject();
		JsonArray asJsonArray = requestObject.get("req").getAsJsonArray();
		Type listType = new TypeToken<List<Anx2GetInvoicesReqDto>>() {
		}.getType();
		List<Anx2GetInvoicesReqDto> dtos = gson.fromJson(asJsonArray, listType);
		Anx2GetInvoicesReqDto dto = dtos.get(0);

		RequestIdWiseEntity reqIdBatch = null;
		TenantContext.setTenantId(groupCode);

		Optional<RequestIdWiseEntity> optReqIdBatch = reqIdRepo
				.findById(dto.getRequestId());
		if (optReqIdBatch.isPresent()) {
			reqIdBatch = optReqIdBatch.get();
		}
		reqIdBatch.setStatus(status);
		reqIdRepo.save(reqIdBatch);
		
		return reqIdBatch;
	}
	
}
