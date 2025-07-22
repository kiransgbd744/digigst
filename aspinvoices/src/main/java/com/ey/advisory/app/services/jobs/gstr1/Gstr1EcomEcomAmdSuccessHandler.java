package com.ey.advisory.app.services.jobs.gstr1;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupAmdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupHeaderEntity;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEcomAmdGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetEcomGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
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
@Service("Gstr1EcomEcomAmdSuccessHandler")
@Slf4j
public class Gstr1EcomEcomAmdSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr1EcomEcomAmdDataParserImpl")
	private Gstr1EcomEcomAmdDataParser gstr1EcomEcomADataParser;

	@Autowired
	@Qualifier("Gstr1GetEcomGstnRepository")
	private Gstr1GetEcomGstnRepository gstr1GetEcomGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetEcomAmdGstnRepository")
	private Gstr1GetEcomAmdGstnRepository gstr1GetEcomAmdGstnRepository;

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
						"GET Call is Success for the batchId {} inside Gstr1EcomEcomAmdSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.ECOM.equalsIgnoreCase(dto.getType())) {
					gstr1GetEcomGstnRepository.softlyDeleteEcomHeader(
							dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1GetEcomAmdGstnRepository.softlyDeleteEcomAmdHeader(
							dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				if (APIConstants.ECOM.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1EcomSupHeaderEntity> ecomEntities = gstr1EcomEcomADataParser
							.parseEcomData(dto, apiResp);
					if (ecomEntities != null && !ecomEntities.isEmpty()) {
						gstr1GetEcomGstnRepository.saveAll(ecomEntities);
					}
				} else {
					List<GetGstr1EcomSupAmdHeaderEntity> ecomAmdEntities = gstr1EcomEcomADataParser
							.parseEcomAmdData(dto, apiResp);
					if (ecomAmdEntities != null && !ecomAmdEntities.isEmpty()) {
						gstr1GetEcomAmdGstnRepository.saveAll(ecomAmdEntities);
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
					dto.getType().toUpperCase(), dto.getReturnPeriod(), "GSTR1",
					true);
		} catch (Exception e) {
			String errMsg = String
					.format("Exception in Ecom/EcomAmd SuccessHandler");
			LOGGER.error(errMsg, e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			throw new APIException(e.getMessage());
		}
	}
}
