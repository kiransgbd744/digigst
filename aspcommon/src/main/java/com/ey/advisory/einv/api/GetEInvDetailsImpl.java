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
import com.ey.advisory.einv.dto.RequestDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva Reddy
 *
 */
@Component("GetEInvDetailsImpl")
@Slf4j
public class GetEInvDetailsImpl implements GetEInvDetails {

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
	public APIResponse getEInvDetails(String irnNo, String gstin,
			String source) {
		return callExecute(irnNo, gstin, source);
	}

	private APIResponse callExecute(String irnNo, String gstin, String source) {

		/*
		 * / APIProviderEnum apiIdentifier = null;
		 * 
		 * if (Strings.isNullOrEmpty(source)) { apiIdentifier =
		 * commUtil.getSource(gstin); } else if
		 * (APIReqParamConstants.EY.equalsIgnoreCase(source) ||
		 * APIReqParamConstants.IRP.equalsIgnoreCase(source)) { apiIdentifier =
		 * APIProviderEnum.EYEINV; } else { apiIdentifier =
		 * APIProviderEnum.EINV; }
		 * 
		 * if (!Strings.isNullOrEmpty(source)) { apiIdentifier =
		 * commUtil.getSource(gstin);
		 * reqLogHelper.logSwitchIdent(apiIdentifier.name()); }
		 * KeyValuePair<String, String> gstinstr = new KeyValuePair<>(
		 * APIReqParamConstants.GSTIN, gstin);
		 * 
		 * KeyValuePair<String, String> irnNostr = new KeyValuePair<>(
		 * APIReqParamConstants.IRN, irnNo);
		 * 
		 * APIParams apiParams = new APIParams(apiIdentifier,
		 * APIIdentifiers.GET_EINVBYIRN, irnNostr, gstinstr); return
		 * apiExecutor.execute(apiParams, null);
		 * 
		 */
		RequestDto requestDto = new RequestDto(irnNo, gstin, source);

		Gson gson = GsonUtil.gsonInstanceWithExpose();
		String reqBody = gson.toJson(requestDto);
		List<APIProviderEnum> availableSources = new ArrayList<>();
		availableSources.add(APIProviderEnum.EINV);
		availableSources.add(APIProviderEnum.EYEINV);
		KeyValuePair<String, String> gstnParam = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, requestDto.getGstin());
		KeyValuePair<String, String> irpParam = new KeyValuePair<>(
				APIReqParamConstants.IRP.toLowerCase(), "NIC1");
		KeyValuePair<String, String> irnNoParam = new KeyValuePair<>(
				APIReqParamConstants.IRN, irnNo);
		APIProviderEnum configuredSrc = commUtil
				.getSource(requestDto.getGstin());
		availableSources.remove(configuredSrc);
		APIParams apiParams = new APIParams(configuredSrc,
				APIIdentifiers.GET_EINVBYIRN, gstnParam, irnNoParam, irpParam);
		reqLogHelper.logAppMessage(null, requestDto.getIrnNo(), null,
				"Sending  Get E Invoice Request to Framework");
		APIResponse response = apiExecutor.execute(apiParams, null);
		if (response.isSuccess()) {
			return response;
		}
		List<APIError> errors = response.getErrors();
		if (!response.isSuccess() && configuredSrc.equals(APIProviderEnum.EINV)
				&& ERROR_LIST
						.contains(response.getErrors().get(0).getErrorCode())) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"About to retry with NIC2 for ErrorList {} For GroupCode {} ",
						response.getErrors(), TenantContext.getTenantId());
			}
			response = retryWithAlternativeSource(apiParams, gstnParam,
					irnNoParam, irpParam, reqBody, configuredSrc);
			if (response.isSuccess()) {
				return response;
			} else {
				errors = response.getErrors();
			}
		}
		for (int i = 0; i < errors.size(); i++) {
			if (!Strings.isNullOrEmpty(errors.get(i).getErrorCode())
					&& ERROR_LIST.contains(errors.get(i).getErrorCode())) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("About to retry to source is {}",
							availableSources.get(0));
				}
				apiParams = new APIParams(availableSources.get(0),
						APIIdentifiers.GET_EINVBYIRN, gstnParam, irnNoParam,
						irpParam);
				reqLogHelper.logSwitchIdent(availableSources.get(0).name());
				response = apiExecutor.execute(apiParams, null);
				if (response.isSuccess()) {
					return response;
				}
				errors = response.getErrors();
				if (!response.isSuccess()
						&& configuredSrc.equals(APIProviderEnum.EINV)
						&& ERROR_LIST.contains(
								response.getErrors().get(0).getErrorCode())) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"About to retry with NIC2 for ErrorList {} For GroupCode {} ",
								response.getErrors(),
								TenantContext.getTenantId());
					}
					response = retryWithAlternativeSource(apiParams, gstnParam,
							irnNoParam, irpParam, reqBody, configuredSrc);
				}
			}
		}
		return response;

	}

	private APIResponse retryWithAlternativeSource(APIParams apiParams,
			KeyValuePair<String, String> gstnParam,
			KeyValuePair<String, String> irnNoParam,
			KeyValuePair<String, String> irpParam, String reqBody,
			APIProviderEnum configuredSrc) {

		irpParam = new KeyValuePair<>(APIReqParamConstants.IRP.toLowerCase(),
				"NIC2");
		apiParams = new APIParams(configuredSrc, APIIdentifiers.GET_EINVBYIRN,
				gstnParam, irnNoParam, irpParam);
		APIResponse response = apiExecutor.execute(apiParams, null);

		return response;
	}
}
