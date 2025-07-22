/**
 * 
 */
package com.ey.advisory.gstnapi;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("TestSuccessHandler1")
@Slf4j
public class TestSuccessHandler1 implements SuccessHandler,FailureHandler{

	@Override
	public void handleFailure(FailureResult result, String apiParams) {		
	}

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		List<Long> resultIds = result.getSuccessIds();
		resultIds.forEach(id -> {
			String response = APIInvokerUtil.getResultById(id);
			JsonObject jsonObject = new JsonParser().parse(apiParams)
					.getAsJsonObject();
			
			/*List<GetGstr1B2bInvoicesEntity> b2bEnties = gstr1B2bB2baDataParser
					.parseB2bB2baData(dto, apiResp, type);
			if (!b2bEnties.isEmpty()) {
				TenantContext.setTenantId(groupCode);
				gstr1GetB2bB2baAtGstnRepository.saveAll(b2bEnties);
			}*/
		});	
		
	}

}
