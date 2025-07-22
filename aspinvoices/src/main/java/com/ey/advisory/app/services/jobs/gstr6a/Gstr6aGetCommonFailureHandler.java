package com.ey.advisory.app.services.jobs.gstr6a;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Gstr6aGetB2bGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetB2baGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetCdnGstnRepository;
import com.ey.advisory.app.data.repositories.client.Gstr6aGetCdnaGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr6aGetInvoicesReqDto;
import com.ey.advisory.gstnapi.FailureHandler;
import com.ey.advisory.gstnapi.FailureResult;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Slf4j
@Service("Gstr6aGetCommonFailureHandler")
public class Gstr6aGetCommonFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("Gstr6aGetB2baGstnRepository")
	private Gstr6aGetB2baGstnRepository b2baHeaderRepo;

	@Autowired
	@Qualifier("Gstr6aGetB2bGstnRepository")
	private Gstr6aGetB2bGstnRepository b2bHeaderRepo;

	@Autowired
	@Qualifier("Gstr6aGetCdnGstnRepository")
	private Gstr6aGetCdnGstnRepository cdnHeaderRepo;

	@Autowired
	@Qualifier("Gstr6aGetCdnaGstnRepository")
	private Gstr6aGetCdnaGstnRepository cdnaHeaderRepo;

	private static final List<String> ERROR_CODES = ImmutableList.of("RET13508",
			"RET13509", "RET13510", "RET11416", "RET11417");

	@Override
	public void handleFailure(FailureResult result, String apiParams) {
		try {
			String errorCode = result.getError().getErrorCode();
			String errorDesc = result.getError().getErrorDesc();
			Gson gson = GsonUtil.newSAPGsonInstance();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = (new JsonParser()).parse(ctxParams)
					.getAsJsonObject();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"GET Call is Failed for the batchId {} inside "
								+ "Gstr6aGetCommonFailureHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Gstr6aGetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Gstr6aGetInvoicesReqDto.class);
			TenantContext.setTenantId(dto.getGroupcode());
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (ERROR_CODES.contains(errorCode)) {
				if (APIConstants.B2B.equalsIgnoreCase(dto.getType())) {
					b2bHeaderRepo.softlyDeleteB2bHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				} else if (APIConstants.B2BA.equalsIgnoreCase(dto.getType())) {
					b2baHeaderRepo.softlyDeleteB2baHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				} else if (APIConstants.CDN.equalsIgnoreCase(dto.getType())) {
					cdnHeaderRepo.softlyDeleteCdnHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				} else if (APIConstants.CDNA.equalsIgnoreCase(dto.getType())) {
					cdnaHeaderRepo.softlyDeleteCdnaHeader(dto.getGstin(),
							dto.getReturnPeriod(), now);
				}
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.SUCCESS_WITH_NO_DATA, errorCode, errorDesc,
						false);
			} else {
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.FAILED, errorCode, errorDesc, false);
			}
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new APIException(e.getLocalizedMessage());
		}
	}
}
