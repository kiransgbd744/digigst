package com.ey.advisory.app.services.jobs.gstr1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1NilRatedEntity;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetNilRatedGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Service("Gstr1NilRatedSuccessHandler")
@Slf4j
public class Gstr1NilRatedSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1NilRatedSupDataParserImpl")
	private GSTR1NilRatedSupDataParser gSTR1NilRatedSupDataParser;

	@Autowired
	@Qualifier("Gstr1GetNilRatedGstnRepository")
	private Gstr1GetNilRatedGstnRepository gstr1GetNilRatedGstnRepository;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;
	
	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj, Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			String apiSection = dto.getApiSection();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside GSTR1NilRatedSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			
			TenantContext.setTenantId(dto.getGroupcode());
			if (resultIds != null && !resultIds.isEmpty()) {
				gstr1GetNilRatedGstnRepository.softlyDeleteNilHeader(
						dto.getGstin(), dto.getReturnPeriod());
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				// Long batchId = null;
				List<GetGstr1NilRatedEntity> nilEnties = gSTR1NilRatedSupDataParser
						.gstr1NilRatedSupDataParser(dto, apiResp);
				if (!nilEnties.isEmpty()) {
					
					gstr1GetNilRatedGstnRepository.saveAll(nilEnties);
				}
			});
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId,
					APIConstants.SUCCESS, null, null, isTokenResp);
			
			gstinGetStatusRepo.updateDBLoad(dto.getGstin(), "NIL_RATED", 
					dto.getReturnPeriod(), "GSTR1", true);

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			e.printStackTrace();
			throw new APIException(e.getLocalizedMessage());
		}

	}

}
