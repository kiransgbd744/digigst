/**
 * 
 */
package com.ey.advisory.app.services.jobs.gstr2a;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTCDSSummaryDetailsAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTCSAndTCSADetailsAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTDSAndTDSADetailsAtGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Service("Gstr2XGetTdsSuccessHandler")
@Slf4j
public class Gstr2XGetTdsSuccessHandler implements SuccessHandler {

	@Autowired
	@Qualifier("Gstr2xTdsTcsDataParserImpl")
	private Gstr2xTdsAndTcsDataParser gstr2xDataParser;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("Gstr2xGetTCDSSummaryDetailsAtGstnRepository")
	private Gstr2xGetTCDSSummaryDetailsAtGstnRepository gstr2xGetTCDSSummaryDetailsAtGstnRepository;

	@Autowired
	@Qualifier("Gstr2xGetTDSAndTDSADetailsAtGstnRepository")
	private Gstr2xGetTDSAndTDSADetailsAtGstnRepository gstr2xGetTDSAndTDSADetailsAtGstnRepository;

	@Autowired
	@Qualifier("Gstr2xGetTCSAndTCSADetailsAtGstnRepository")
	private Gstr2xGetTCSAndTCSADetailsAtGstnRepository gstr2xGetTCSAndTCSADetailsAtGstnRepository;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Success for the batchId {} "
								+ "inside Gstr2aGetTdsSuccessHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Gstr1GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr1GetInvoicesReqDto.class);
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (resultIds != null && !resultIds.isEmpty()) {
				if (APIConstants.TCSANDTDS.equalsIgnoreCase(dto.getType())) {
					gstr2xGetTCSAndTCSADetailsAtGstnRepository
							.softlyDeleteTdsHeader(dto.getGstin(),
									dto.getReturnPeriod(), now);
					gstr2xGetTDSAndTDSADetailsAtGstnRepository
							.softlyDeleteTdsHeader(dto.getGstin(),
									dto.getReturnPeriod(), now);
					gstr2xGetTCDSSummaryDetailsAtGstnRepository
							.softlyDeleteTdsHeader(dto.getGstin(),
									dto.getReturnPeriod(), now);
				}
			}

			resultIds.forEach(id -> {
				String apiResp = APIInvokerUtil.getResultById(id);
				if (APIConstants.TCSANDTDS.equalsIgnoreCase(dto.getType())) {
					gstr2xDataParser.parseTdsData(dto, apiResp, dto.getType(),
							ctxParamsObj.get("batchId").getAsLong());
				}
			});
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
					APIConstants.SUCCESS, null, null, isTokenResp);

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw new AppException(e);
		}
	}
}