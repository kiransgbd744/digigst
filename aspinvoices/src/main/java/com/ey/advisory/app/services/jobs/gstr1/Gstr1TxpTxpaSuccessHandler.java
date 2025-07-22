package com.ey.advisory.app.services.jobs.gstr1;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr1TxpHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1TxpaHeaderEntity;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetTxpGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1GetTxpaGstnRepository;
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

@Service("Gstr1TxpTxpaSuccessHandler")
@Slf4j
public class Gstr1TxpTxpaSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("gstr1TxpTxpaDataParserImpl")
	private Gstr1TxpTxpaDataParser gstr1TxpTxpaDataParser;

	@Autowired
	@Qualifier("Gstr1GetTxpGstnRepository")
	private Gstr1GetTxpGstnRepository gstr1GetTxpGstnRepository;

	@Autowired
	@Qualifier("Gstr1GetTxpaGstnRepository")
	private Gstr1GetTxpaGstnRepository gstr1GetTxpaGstnRepository;

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
				LOGGER.debug("GET Call is Success for the batchId {} inside Gstr1TxpTxpaSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}
			
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.TXP.equalsIgnoreCase(dto.getType())) {
					gstr1GetTxpGstnRepository.softlyDeleteTxpHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				} else {
					gstr1GetTxpaGstnRepository.softlyDeleteTxpaHeader(dto.getGstin(), dto.getReturnPeriod(), now);
				}
			}
			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);

				if (APIConstants.TXP.equalsIgnoreCase(dto.getType())) {
					List<GetGstr1TxpHeaderEntity> txpEntities = gstr1TxpTxpaDataParser.parseTxpData(dto, apiResp);
					if (txpEntities != null && !txpEntities.isEmpty()) {
						gstr1GetTxpGstnRepository.saveAll(txpEntities);
					}
				} else {
					List<GetGstr1TxpaHeaderEntity> txpaEntities = gstr1TxpTxpaDataParser.parseTxpaData(dto, apiResp);
					if (txpaEntities != null && !txpaEntities.isEmpty()) {
						gstr1GetTxpaGstnRepository.saveAll(txpaEntities);
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
