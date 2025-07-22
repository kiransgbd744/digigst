/**
 * 
 */
package com.ey.advisory.app.services.jobs.anx2;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx2IsdcInvoicesHeaderEntity;
import com.ey.advisory.app.data.repositories.client.gstr2.GetAnx2IsdcInvoicesRepository;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx2GetIsdcSuccessHandler")
@Slf4j
public class Anx2GetIsdcSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Anx2IsdcDataParserImpl")
	private Anx2IsdcDataParser anx2DataParser;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private GetAnx2IsdcInvoicesRepository headerRepo;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Anx2GetIsdcSuccessHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Anx2GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Anx2GetInvoicesReqDto.class);
			TenantContext.setTenantId(dto.getGroupcode());
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				Set<GetAnx2IsdcInvoicesHeaderEntity> invoices = anx2DataParser
						.parseIsdcData(dto, apiResp,
								ctxParamsObj.get("batchId").getAsLong());
				// Update Batch as success
				if (!invoices.isEmpty()) {
					// batch.setInvCount(invoices.size());
					// InActiveting Previous Header Records
					headerRepo.softlyDeleteIsdcHeader(dto.getGstin(),
							dto.getReturnPeriod());
					// Save new Header
					headerRepo.saveAll(invoices);

					LOGGER.debug(
							"Response is stored into the tables sucessfully.");
				}
			});

			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
					APIConstants.SUCCESS, null, null, isTokenResp);

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
	}

}