package com.ey.advisory.app.services.jobs.gstr1A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1CdnrHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1CdnraHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnrHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ACdnraHeaderEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetCdnrGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetCdnraGstnRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnrGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetCdnraGstnRepository;
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

@Service("Gstr1ACdnrCdnraSuccessHandler")
@Slf4j
public class Gstr1ACdnrCdnraSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1ACdnrCdnraDataParserImpl")
	private Gstr1ACdnrCdnraDataParser gstr1ACdnrCdnraDataParser;

	@Autowired
	@Qualifier("Gstr1AGetCdnrGstnRepository")
	private Gstr1AGetCdnrGstnRepository gstr1AGetCdnrGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetCdnraGstnRepository")
	private Gstr1AGetCdnraGstnRepository gstr1AGetCdnraGstnRepository;

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
				LOGGER.debug("GET Call is Success for the batchId {} inside Gstr1CdnrCdnraSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			
			TenantContext.setTenantId(dto.getGroupcode());

			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.CDNR.equalsIgnoreCase(dto.getType())) {
					gstr1AGetCdnrGstnRepository.softlyDeleteCdnrHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1AGetCdnraGstnRepository.softlyDeleteCdnraHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				// Long batchId = null;
				if (APIConstants.CDNR.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1ACdnrHeaderEntity> cdnrEntities = gstr1ACdnrCdnraDataParser.parseCdnrData(dto, apiResp);
					if (cdnrEntities != null && !cdnrEntities.isEmpty()) {
						gstr1AGetCdnrGstnRepository.saveAll(cdnrEntities);
					}
				} else {
					List<GetGstr1ACdnraHeaderEntity> cdnraEntities = gstr1ACdnrCdnraDataParser.parseCdnraData(dto,
							apiResp);
					if (cdnraEntities != null && !cdnraEntities.isEmpty()) {
						gstr1AGetCdnraGstnRepository.saveAll(cdnraEntities);
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
