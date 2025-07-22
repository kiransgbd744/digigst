package com.ey.advisory.einv.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIParams;
import com.ey.advisory.einv.app.api.APIReqParamConstants;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.app.api.CommonUtil;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Component("CancelIrnImpl")
@Slf4j
public class CancelIrnImpl implements CancelIrn {

	private static final List<String> ERROR_LIST = ImmutableList.of("2143",
			"2148", "4003");

	@Autowired
	@Qualifier("DefaultEINVAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	CommonUtil commUtil;

	@Override
	public APIResponse cancelIrn(CancelIrnReqDto req) {
		return callExecute(req);
	}

	private APIResponse callExecute(CancelIrnReqDto requestDto) {

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		String reqBody = gson.toJson(requestDto);
		List<APIProviderEnum> availableSources = new ArrayList<>();
		availableSources.add(APIProviderEnum.EINV);
		availableSources.add(APIProviderEnum.EYEINV);
		KeyValuePair<String, String> gstnParam = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, requestDto.getGstin());
		KeyValuePair<String, String> irpParam = new KeyValuePair<>(
				APIReqParamConstants.IRP.toLowerCase(), "NIC1");
		APIProviderEnum configuredSrc = commUtil
				.getSource(requestDto.getGstin());
		availableSources.remove(configuredSrc);
		APIParams apiParams = new APIParams(configuredSrc,
				APIIdentifiers.CANCEL_EINV, gstnParam, irpParam);
		reqLogHelper.logAppMessage(null, requestDto.getIrn(), null,
				"Sending  Cancel E Invoice Request to Framework");
		APIResponse response = apiExecutor.execute(apiParams, reqBody);
		List<APIError> errors = response.getErrors();
		if (!response.isSuccess() && configuredSrc.equals(APIProviderEnum.EINV)
				&& ERROR_LIST.contains(response.getErrors().get(0).getErrorCode())) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"About to retry with NIC2 for ErrorList {} For GroupCode {} ",
						response.getErrors(), TenantContext.getTenantId());
			}
			response = retryWithAlternativeSource(apiParams, gstnParam,
					irpParam, reqBody, configuredSrc);
		}
		for (int i = 0; i < errors.size(); i++) {
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode())
					&& ERROR_LIST.contains(errors.get(i).getErrorCode())) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("About to retry to source is {}",
							availableSources.get(0));
				}
				apiParams = new APIParams(availableSources.get(0),
						APIIdentifiers.CANCEL_EINV, gstnParam, irpParam);
				reqLogHelper.logSwitchIdent(availableSources.get(0).name());
				response = apiExecutor.execute(apiParams, reqBody);
				errors = response.getErrors();
				if (!response.isSuccess() && configuredSrc.equals(APIProviderEnum.EINV)
						&& ERROR_LIST.contains(response.getErrors().get(0).getErrorCode())) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"About to retry with NIC2 for ErrorList {} For GroupCode {} ",
								response.getErrors(),
								TenantContext.getTenantId());
					}
					response = retryWithAlternativeSource(apiParams, gstnParam,
							irpParam, reqBody, configuredSrc);
				}
			}
		}
		return response;
	}

	private APIResponse retryWithAlternativeSource(APIParams apiParams,
			KeyValuePair<String, String> gstnParam,
			KeyValuePair<String, String> irpParam, String reqBody,
			APIProviderEnum configuredSrc) {

		irpParam = new KeyValuePair<>(APIReqParamConstants.IRP.toLowerCase(),
				"NIC2");
		apiParams = new APIParams(configuredSrc, APIIdentifiers.CANCEL_EINV,
				gstnParam, irpParam);
		APIResponse response = apiExecutor.execute(apiParams, reqBody);

		return response;
	}
}