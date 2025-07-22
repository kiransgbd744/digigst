package com.ey.advisory.app.services.jobs.gstr1A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnurHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnuraHeaderEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetCdnurGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetCdnuraGstnRepository;
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

@Service("Gstr1ACdnurCdnuraSuccessHandler")
@Slf4j
public class Gstr1ACdnurCdnuraSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1ACdnurCdnuraDataParserImpl")
	private Gstr1ACdnurCdnuraDataParser gstr1CdnurCdnuraDataParser;

	@Autowired
	@Qualifier("Gstr1AGetCdnurGstnRepository")
	private Gstr1AGetCdnurGstnRepository gstr1AGetCdnurGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetCdnuraGstnRepository")
	private Gstr1AGetCdnuraGstnRepository gstr1AGetCdnuraGstnRepository;

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
				LOGGER.debug("GET Call is Success for the batchId {} inside Gstr1CdnurCdnuraSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.CDNUR.equalsIgnoreCase(dto.getType())) {
					gstr1AGetCdnurGstnRepository.softlyDeleteCdnurHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1AGetCdnuraGstnRepository.softlyDeleteCdnuraHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				// Long batchId = null;
				if (APIConstants.CDNUR.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1ACdnurHeaderEntity> cdnurEntities = gstr1CdnurCdnuraDataParser.parseCdnurData(dto,
							apiResp);
					if (cdnurEntities != null && !cdnurEntities.isEmpty()) {
						gstr1AGetCdnurGstnRepository.saveAll(cdnurEntities);
					}
				} else {
					List<GetGstr1ACdnuraHeaderEntity> cdnuraEntities = gstr1CdnurCdnuraDataParser.parseCdnuraData(dto,
							apiResp);
					if (cdnuraEntities != null && !cdnuraEntities.isEmpty()) {
						gstr1AGetCdnuraGstnRepository.saveAll(cdnuraEntities);
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
