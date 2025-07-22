package com.ey.advisory.app.services.jobs.gstr1A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1B2csHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1B2csaHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2csHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1AB2csaHeaderEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2csGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetB2csaGstnRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2csGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetB2csaGstnRepository;
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

/**
 * 
 * @author Anand3.M
 *
 */
@Service("Gstr1AB2csB2csaSuccessHandler")
@Slf4j
public class Gstr1AB2csB2csaSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1AB2csB2csaDataParserImpl")
	private Gstr1AB2csB2csaDataParser gstr1AB2csB2csaDataParser;

	@Autowired
	@Qualifier("Gstr1AGetB2csaGstnRepository")
	private Gstr1AGetB2csaGstnRepository gstr1AGetB2csaGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetB2csGstnRepository")
	private Gstr1AGetB2csGstnRepository gstr1AGetB2csGstnRepository;

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
				LOGGER.debug("GET Call is Success for the batchId {} inside Gstr1AB2csB2csaSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.B2CS.equalsIgnoreCase(dto.getType())) {
					gstr1AGetB2csGstnRepository.softlyDeleteB2csHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1AGetB2csaGstnRepository.softlyDeleteB2csaHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (APIConstants.B2CS.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1AB2csHeaderEntity> b2csEntities = gstr1AB2csB2csaDataParser.parseB2csData(dto, apiResp);
					if (b2csEntities != null && !b2csEntities.isEmpty()) {
						gstr1AGetB2csGstnRepository.saveAll(b2csEntities);
					}
				} else {
					List<GetGstr1AB2csaHeaderEntity> b2csaEntities = gstr1AB2csB2csaDataParser.parseB2csaData(dto,
							apiResp);
					if (b2csaEntities != null && !b2csaEntities.isEmpty()) {
						gstr1AGetB2csaGstnRepository.saveAll(b2csaEntities);
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
