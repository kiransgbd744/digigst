package com.ey.advisory.app.services.jobs.gstr6;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6B2bHeaderEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6GetB2bGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6GetInvoicesReqDto;
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

@Service("Gstr6GetB2bSuccessHandler")
@Slf4j
public class Gstr6GetB2bSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr6B2bDataParserImpl")
	private Gstr6B2bGetDataParser gstr6B2bGetDataParser;

	@Autowired
	@Qualifier("Gstr6GetB2bGstnRepository")
	private Gstr6GetB2bGstnRepository b2bHeaderRepo;

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
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams).getAsJsonObject();
			Gstr6GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj, Gstr6GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			String apiSection = dto.getApiSection();
			

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GET Call is Success for the batchId {} inside Gstr6GetB2bSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}

		
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
					b2bHeaderRepo.softlyDeleteB2bHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
                 List<GetGstr6B2bHeaderEntity> b2bEntities = gstr6B2bGetDataParser.parseB2bGetData(dto, apiResp,
							dto.getType(), ctxParamsObj.get("batchId").getAsLong(), now);
					if (b2bEntities != null && !b2bEntities.isEmpty()) {
                    b2bHeaderRepo.saveAll(b2bEntities);
					}
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
