package com.ey.advisory.app.services.jobs.itc04;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.Itc04GetInvoicesGstnRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Itc04GetInvoicesReqDto;
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
@Service("Itc04GetCommonFailureHandler")
public class Itc04GetCommonFailureHandler implements FailureHandler {

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	@Qualifier("Itc04GetInvoicesGstnRepository")
	private Itc04GetInvoicesGstnRepository itc04HeaderRepo;

	private static final List<String> ERROR_CODES = ImmutableList.of("RET13508",
			"RET13509", "RET13510");

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
								+ "Itc04GetCommonFailureHandler.java",
						ctxParamsObj.get("batchId").getAsLong());
			}
			Itc04GetInvoicesReqDto dto = gson.fromJson(ctxParamsObj,
					Itc04GetInvoicesReqDto.class);
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			if (ERROR_CODES.contains(errorCode)) {
				int rowAffected = itc04HeaderRepo.softlyDeleteItc04Header(dto.getGstin(),
						dto.getReturnPeriod(), now);
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.SUCCESS_WITH_NO_DATA, errorCode, errorDesc,
						false);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Total Number of soft deleted "
							+ "for gstin {}, tax period {}, section "
							+ "ATA is {} ", dto.getGstin(),  dto.getReturnPeriod(),rowAffected);
				}
			} else {
				batchUtil.updateById(ctxParamsObj.get("batchId").getAsLong(),
						APIConstants.FAILED, errorCode, errorDesc, false);
			}
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw new APIException(e.getLocalizedMessage());
		}
	}
}
