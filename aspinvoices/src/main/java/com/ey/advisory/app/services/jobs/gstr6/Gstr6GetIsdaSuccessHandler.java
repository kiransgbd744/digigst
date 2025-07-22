package com.ey.advisory.app.services.jobs.gstr6;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6.GetGstr6IsdaDetailsEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6GetIsdaGstnRepository;
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
@Service("Gstr6GetIsdaSuccessHandler")
@Slf4j
public class Gstr6GetIsdaSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr6IsdaDataParserImpl")
	private Gstr6IsdaGetDataParser gstr6IsdaGetDataParser;

	@Autowired
	@Qualifier("Gstr6GetIsdaGstnRepository")
	private Gstr6GetIsdaGstnRepository isdaHeaderRepo;

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
				LOGGER.debug("GET Call is Success for the batchId {} inside Gstr6GetIsdaSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}

			
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {

				isdaHeaderRepo.softlyDeleteIsdaHeader(dto.getGstin(), dto.getReturnPeriod(), now);

			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				List<GetGstr6IsdaDetailsEntity> isdEntities = gstr6IsdaGetDataParser.parseIsdaGetData(dto, apiResp,
						dto.getType(), ctxParamsObj.get("batchId").getAsLong(), now);
				if (isdEntities != null && !isdEntities.isEmpty()) {
					isdaHeaderRepo.saveAll(isdEntities);
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
