package com.ey.advisory.app.services.jobs.itc04;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Itc04InvoicesEntity;
import com.ey.advisory.app.data.repositories.client.Itc04GetInvoicesGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
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
@Service("Itc04GetInvoicesDetailsSuccessHandler")
@Slf4j
public class Itc04GetInvoicesDetailsSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Itc04InvoicesDataParserImpl")
	private Itc04InvoicesDataParser itc04GetDataParser;

	@Autowired
	@Qualifier("Itc04GetInvoicesGstnRepository")
	private Itc04GetInvoicesGstnRepository itc04HeaderRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			Itc04GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Itc04GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Itc04GetInvoicesDetailsSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}

			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				itc04HeaderRepo.softlyDeleteItc04Header(dto.getGstin(),
						dto.getReturnPeriod(), now);

			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				List<Itc04InvoicesEntity> itc04Entities = itc04GetDataParser
						.parseItc04GetData(dto, apiResp, dto.getType(),
								ctxParamsObj.get("batchId").getAsLong(), now);
				if (!itc04Entities.isEmpty()) {
					itc04HeaderRepo.saveAll(itc04Entities);
				}

			});

			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					isTokenResp);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			e.printStackTrace();
			throw new APIException(e.getLocalizedMessage());
		}
	}

}
