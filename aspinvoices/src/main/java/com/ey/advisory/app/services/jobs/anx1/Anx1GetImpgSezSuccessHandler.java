package com.ey.advisory.app.services.jobs.anx1;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1ImpgSezInvoicesHeaderEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.GetAnx1ImpgSezInvoicesRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Anx1GetImpgSezSuccessHandler")
@Slf4j
public class Anx1GetImpgSezSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Anx1ImpgSezDataParserImpl")
	private Anx1ImpgSezDataParser anx1ImpgSezDataParser;

	@Autowired
	@Qualifier("GetAnx1ImpgSezInvoicesRepository")
	private GetAnx1ImpgSezInvoicesRepository headerRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	private DocRepository docRepository;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			/*
			 * JsonObject requestObject = ctxParamsObj.get("jsonReq")
			 * .getAsJsonObject();
			 */
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Anx1GetImpgSezSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			// String type = ctxParamsObj.get("type").getAsString();
			// JsonObject reqObject =
			// requestObject.get("req").getAsJsonObject();
			Anx1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Anx1GetInvoicesReqDto.class);
			TenantContext.setTenantId(dto.getGroupcode());
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				Set<GetAnx1ImpgSezInvoicesHeaderEntity> b2cEntities = anx1ImpgSezDataParser
						.parseImpgSezData(dto, apiResp);
				if (!b2cEntities.isEmpty()) {
					headerRepo.softlyDeleteImpgSezHeader(dto.getGstin(),
							dto.getReturnPeriod());

					headerRepo.saveAll(b2cEntities);
				}

			});

			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
					APIConstants.SUCCESS, null, null, isTokenResp);
			docRepository.getAnx1ProcCall(dto.getGstin(), dto.getReturnPeriod(),
					APIConstants.IMPGSEZ);

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw new APIException(e.getLocalizedMessage());
		}
	}

}
