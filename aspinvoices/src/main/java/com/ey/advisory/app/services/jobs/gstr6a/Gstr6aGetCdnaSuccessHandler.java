package com.ey.advisory.app.services.jobs.gstr6a;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnaHeaderEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GetGstr6aStagingCdnaHeaderRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
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
@Service("Gstr6aGetCdnaSuccessHandler")
@Slf4j
public class Gstr6aGetCdnaSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr6aCdnaDataParserImpl")
	private Gstr6aCdnaGetDataParser gstr6aCdnaGetDataParser;

	@Autowired
	@Qualifier("GetGstr6aStagingCdnaHeaderRepository")
	private GetGstr6aStagingCdnaHeaderRepository cdnaStagingRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Autowired
	private Gstr6aCommonUtility commUtility;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		Long batchId = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			Gstr6aGetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr6aGetInvoicesReqDto.class);
			batchId = dto.getBatchId();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} inside Gstr6aGetCdnaSuccessHandler.class",
						ctxParamsObj.get("batchId").getAsLong());
			}

			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.CDN.equalsIgnoreCase(dto.getType())) {
					cdnaStagingRepo.softlyDeleteCdnaHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				}
			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				// Long batchId = null;
				if (APIConstants.CDN.equalsIgnoreCase(dto.getType())) {
					List<GetGstr6aStagingCdnaHeaderEntity> atEnties = gstr6aCdnaGetDataParser
							.parseCdnaGetData(dto, apiResp, dto.getType(),
									ctxParamsObj.get("batchId").getAsLong(),
									now);
					if (!atEnties.isEmpty()) {
						cdnaStagingRepo.saveAll(atEnties);
						// saveEinvClientData(atEnties);
					}
				}
			});

			docRepository.getGstr6aProcCall(dto.getGstin(),
					dto.getReturnPeriod(), dto.getType(), batchId, false);

			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			commUtility.post6ARevIntgJob(dto);
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					isTokenResp);
		} catch (Exception e) {
			String msg = "exception occured while GSTR6A get call CDNA success handler";
			LOGGER.error(msg, e);
			batchUtil.updateById(batchId, APIConstants.FAILED, "CATCH_BLOCK",
					e.getMessage(), false);
			throw new AppException(msg, e);
		}
	}

}
