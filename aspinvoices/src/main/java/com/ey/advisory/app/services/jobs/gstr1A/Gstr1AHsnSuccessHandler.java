package com.ey.advisory.app.services.jobs.gstr1A;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1HSNOrSACInvoicesEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AHSNOrSACInvoicesEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetHSNOrSacGstnRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetHSNOrSacGstnRepository;
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

@Service("Gstr1AHsnSuccessHandler")
@Slf4j
public class Gstr1AHsnSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1AHSNSummaryDataParserImpl")
	private Gstr1AHSNSummaryDataParser gstr1AHSNSummaryDataParser;

	@Autowired
	@Qualifier("Gstr1AGetHSNOrSacGstnRepository")
	private Gstr1AGetHSNOrSacGstnRepository gstr1AGetHSNOrSacGstnRepository;

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
						"GET Call is Success for the batchId {} inside Gstr1HsnSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			
			TenantContext.setTenantId(dto.getGroupcode());
			if (resultIds != null && !resultIds.isEmpty()) {
				gstr1AGetHSNOrSacGstnRepository.softlyDeleteHsnHeader(
						dto.getGstin(), dto.getReturnPeriod());
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				// Long batchId = null;
				List<GetGstr1AHSNOrSACInvoicesEntity> hsnEnties = gstr1AHSNSummaryDataParser
						.gstr1HSNSummaryDataParser(dto, apiResp);
				if (!hsnEnties.isEmpty()) {
					
					gstr1AGetHSNOrSacGstnRepository.saveAll(hsnEnties);
				}
			});
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId,
					APIConstants.SUCCESS, null, null, isTokenResp);
			
			gstinGetStatusRepo.updateDBLoad(dto.getGstin(), "HSN_SUMMARY", 
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
