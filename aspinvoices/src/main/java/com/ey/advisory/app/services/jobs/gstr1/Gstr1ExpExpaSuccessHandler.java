package com.ey.advisory.app.services.jobs.gstr1;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1ExpHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1ExpaHeaderEntity;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetExpGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetExpaGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
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

@Service("Gstr1ExpExpaSuccessHandler")
@Slf4j
public class Gstr1ExpExpaSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1ExpExpaDataParserImpl")
	private Gstr1ExpExpaDataParser gstr1ExpExpaDataParser;

	@Autowired
	@Qualifier("Gstr1GetExpGstnRepository")
	private Gstr1GetExpGstnRepository gstr1GetExpGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetExpaGstnRepository")
	private Gstr1GetExpaGstnRepository gstr1GetExpaGstnRepository;

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
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams).getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj, Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			String apiSection = dto.getApiSection();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GET Call is Success for the batchId {} inside Gstr1ExpExpaSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.EXP.equalsIgnoreCase(dto.getType())) {
					gstr1GetExpGstnRepository.softlyDeleteExpHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1GetExpaGstnRepository.softlyDeleteExpaHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (APIConstants.EXP.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1ExpHeaderEntity> expEntities = gstr1ExpExpaDataParser.parseExpData(dto, apiResp);
					if (expEntities != null && !expEntities.isEmpty()) {
						gstr1GetExpGstnRepository.saveAll(expEntities);
					}
				} else {
					List<GetGstr1ExpaHeaderEntity> expaEntities = gstr1ExpExpaDataParser.parseExpaData(dto, apiResp);
					if (expaEntities != null && !expaEntities.isEmpty()) {
						gstr1GetExpaGstnRepository.saveAll(expaEntities);
					}
				}

			});
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					isTokenResp);
			
			gstinGetStatusRepo.updateDBLoad(dto.getGstin(), dto.getType().toUpperCase(), 
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
