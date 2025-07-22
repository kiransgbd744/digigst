package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ANilRatedEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetNilRatedGstnRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
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

@Service("Gstr1ANilRatedSuccessHandler")
@Slf4j
public class Gstr1ANilRatedSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1ANilRatedSupDataParserImpl")
	private GSTR1ANilRatedSupDataParser gSTR1ANilRatedSupDataParser;

	@Autowired
	@Qualifier("Gstr1AGetNilRatedGstnRepository")
	private Gstr1AGetNilRatedGstnRepository gstr1AGetNilRatedGstnRepository;

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
				gstr1AGetNilRatedGstnRepository.softlyDeleteNilHeader(
						dto.getGstin(), dto.getReturnPeriod());
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				// Long batchId = null;
				List<GetGstr1ANilRatedEntity> nilEnties = gSTR1ANilRatedSupDataParser
						.gstr1NilRatedSupDataParser(dto, apiResp);
				if (!nilEnties.isEmpty()) {
					
					gstr1AGetNilRatedGstnRepository.saveAll(nilEnties);
				}
			});
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId,
					APIConstants.SUCCESS, null, null, isTokenResp);
			
			gstinGetStatusRepo.updateDBLoad(dto.getGstin(), "NIL_RATED", 
					dto.getReturnPeriod(), "GSTR1A", true);

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			e.printStackTrace();
			throw new APIException(e.getLocalizedMessage());
		}

	}

}
