package com.ey.advisory.app.services.jobs.gstr1A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1ExpHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1ExpaHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AExpHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AExpaHeaderEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetExpGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetExpaGstnRepository;
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

@Service("Gstr1AExpExpaSuccessHandler")
@Slf4j
public class Gstr1AExpExpaSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1AExpExpaDataParserImpl")
	private Gstr1AExpExpaDataParser gstr1AExpExpaDataParser;

	@Autowired
	@Qualifier("Gstr1AGetExpGstnRepository")
	private Gstr1AGetExpGstnRepository gstr1AGetExpGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetExpaGstnRepository")
	private Gstr1AGetExpaGstnRepository gstr1AGetExpaGstnRepository;

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
					gstr1AGetExpGstnRepository.softlyDeleteExpHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1AGetExpaGstnRepository.softlyDeleteExpaHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (APIConstants.EXP.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1AExpHeaderEntity> expEntities = gstr1AExpExpaDataParser.parseExpData(dto, apiResp);
					if (expEntities != null && !expEntities.isEmpty()) {
						gstr1AGetExpGstnRepository.saveAll(expEntities);
					}
				} else {
					List<GetGstr1AExpaHeaderEntity> expaEntities = gstr1AExpExpaDataParser.parseExpaData(dto, apiResp);
					if (expaEntities != null && !expaEntities.isEmpty()) {
						gstr1AGetExpaGstnRepository.saveAll(expaEntities);
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
