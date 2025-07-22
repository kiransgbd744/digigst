package com.ey.advisory.app.services.jobs.gstr1A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2clHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2claHeaderEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2clGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2claGstnRepository;
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

@Service("Gstr1AB2clB2claSuccessHandler")
@Slf4j
public class Gstr1AB2clB2claSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1AB2clB2claDataParserImpl")
	private Gstr1AB2clB2claDataParser gstr1AB2clB2claDataParser;

	@Autowired
	@Qualifier("Gstr1AGetB2claGstnRepository")
	private Gstr1AGetB2claGstnRepository gstr1AGetB2claGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetB2clGstnRepository")
	private Gstr1AGetB2clGstnRepository gstr1AGetB2clGstnRepository;

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
				LOGGER.debug("GET Call is Success for the batchId {} inside Gstr1B2clB2claSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
		
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.B2CL.equalsIgnoreCase(dto.getType())) {
					gstr1AGetB2clGstnRepository.softlyDeleteB2clHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1AGetB2claGstnRepository.softlyDeleteB2claHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				
				if (APIConstants.B2CL.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1AB2clHeaderEntity> b2clEntities = gstr1AB2clB2claDataParser.parseB2clData(dto, apiResp);
					if (b2clEntities != null && !b2clEntities.isEmpty()) {
                    gstr1AGetB2clGstnRepository.saveAll(b2clEntities);
					}
				} else {
					List<GetGstr1AB2claHeaderEntity> b2claEntities = gstr1AB2clB2claDataParser.parseB2claData(dto,
							apiResp);
					if (b2claEntities != null && !b2claEntities.isEmpty()) {
                   gstr1AGetB2claGstnRepository.saveAll(b2claEntities);
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
