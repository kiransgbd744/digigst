package com.ey.advisory.app.services.jobs.gstr1A;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1SupEcomAmdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1SupEcomHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ASupEcomAmdHeaderEntity;
import com.ey.advisory.app.data.gstr1A.entities.client.GetGstr1ASupEcomHeaderEntity;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetSupEcomAmdGstnRepository;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AGetSupEcomGstnRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetSupEcomAmdGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetSupEcomGstnRepository;
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
 * @author Siva.Reddy
 *
 */
@Service("Gstr1ASupEcomSupEcomASuccessHandler")
@Slf4j
public class Gstr1ASupEcomSupEcomAmdSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr1ASupEcomSupEcomAmdDataParserImpl")
	private Gstr1ASupEcomSupEcomAmdDataParser gstr1ASupEcomSupEcomADataParser;

	@Autowired
	@Qualifier("Gstr1AGetSupEcomGstnRepository")
	private Gstr1AGetSupEcomGstnRepository gstr1AGetSupEcomGstnRepository;

	@Autowired
	@Qualifier("Gstr1AGetSupEcomAmdGstnRepository")
	private Gstr1AGetSupEcomAmdGstnRepository gstr1AGetSupEcomAmdGstnRepository;

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
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			batchId = dto.getBatchId();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Gstr1B2csB2csaSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.SUPECO.equalsIgnoreCase(dto.getType())) {
					gstr1AGetSupEcomGstnRepository.softlyDeleteSupEcomHeader(
							dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1AGetSupEcomAmdGstnRepository.softlyDeleteSupEcomAHeader(
							dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				if (APIConstants.SUPECO.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1ASupEcomHeaderEntity> b2csEntities = gstr1ASupEcomSupEcomADataParser
							.parseSupEcomData(dto, apiResp);
					if (b2csEntities != null && !b2csEntities.isEmpty()) {
						gstr1AGetSupEcomGstnRepository.saveAll(b2csEntities);
					}
				} else {
					List<GetGstr1ASupEcomAmdHeaderEntity> b2csaEntities = gstr1ASupEcomSupEcomADataParser
							.parseSupEcomAmdData(dto, apiResp);
					if (b2csaEntities != null && !b2csaEntities.isEmpty()) {
						gstr1AGetSupEcomAmdGstnRepository.saveAll(b2csaEntities);
					}
				}
			});
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					isTokenResp);
			gstinGetStatusRepo.updateDBLoad(dto.getGstin(),
					dto.getType().toUpperCase(), dto.getReturnPeriod(), "GSTR1A",
					true);
		} catch (Exception e) {
			String errMsg = String
					.format("Exception in SupEcom SuccessHandler");
			LOGGER.error(errMsg, e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			throw new APIException(e.getMessage());
		}
	}
}
