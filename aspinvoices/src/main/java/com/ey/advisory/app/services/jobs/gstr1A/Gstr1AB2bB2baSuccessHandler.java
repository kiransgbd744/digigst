package com.ey.advisory.app.services.jobs.gstr1A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2bHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2baHeaderEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2bGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2baGstnRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
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

@Service("Gstr1AB2bB2baSuccessHandler")
@Slf4j
public class Gstr1AB2bB2baSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1AB2bB2baDataParserImpl")
	private Gstr1AB2bB2baDataParser gstr1AB2bB2baDataParser;

	@Autowired
	@Qualifier("Gstr1AGetB2bGstnRepository")
	private Gstr1AGetB2bGstnRepository gstr1AGetB2bGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetB2baGstnRepository")
	private Gstr1AGetB2baGstnRepository gstr1AGetB2baGstnRepository;

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
				LOGGER.debug("GET Call is Success for the batchId {} inside Gstr1AB2bB2baSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
					gstr1AGetB2bGstnRepository.softlyDeleteB2bHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1AGetB2baGstnRepository.softlyDeleteB2baHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				
				if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1AB2bHeaderEntity> b2bEntities = gstr1AB2bB2baDataParser.parseB2bData(dto, apiResp);
					if (b2bEntities != null && !b2bEntities.isEmpty()) {
						gstr1AGetB2bGstnRepository.saveAll(b2bEntities);
					}
				} else {
					List<GetGstr1AB2baHeaderEntity> b2baEntities = gstr1AB2bB2baDataParser.parseB2baData(dto, apiResp);
					if (b2baEntities != null && !b2baEntities.isEmpty()) {
                       gstr1AGetB2baGstnRepository.saveAll(b2baEntities);
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
