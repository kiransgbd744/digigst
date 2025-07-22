/**
 * 
 */
package com.ey.advisory.ewb.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.ewb.app.api.APIError;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIParams;
import com.ey.advisory.ewb.app.api.APIReqParamConstants;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.common.CalculateDistanceUtil;
import com.ey.advisory.ewb.dto.ExtendEWBReqDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("ExtendEWBImpl")
@Slf4j
public class ExtendEwbImpl implements ExtendEWB {

	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("CalculateDistanceUtilImpl")
	CalculateDistanceUtil calcDistUtil;

	@Override
	public APIResponse extendEwb(ExtendEWBReqDto req, String gstin) {
		Map<String, Config> configMap = configManager.getConfigs("EWB",
				"ewb.distance", TenantContext.getTenantId());
		boolean retry = configMap != null
				&& configMap.get("ewb.distance.isretryenabled") != null
						? "true".equalsIgnoreCase(configMap
								.get("ewb.distance.isretryenabled").getValue())
						: Boolean.FALSE;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  extendewb with requestInfo  " + req);
		}
		Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
		if (req.getRemainingDistance() == null
				|| req.getRemainingDistance() == 0)
			retry = true;
		/**the below line of code is because erp is not sending this 
		 * field in few scenarios and its a mandatory field from nic
		 *  so we are setting it as empty string and sending this field*/
		req.setExtnRemarks(Strings.isNullOrEmpty(req.getExtnRemarks())?"":req.getExtnRemarks());
		String reqBody = gson.toJson(req);
		KeyValuePair<String, String> apiParam = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);
		APIParams apiParams = new APIParams(APIProviderEnum.EWB,
				APIIdentifiers.EXTEND_VEHICLE_DETAILS, apiParam);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Begin  extendewb with requestjson  " + reqBody);
		}
		APIResponse response = apiExecutor.execute(apiParams, reqBody);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("End  extendewb ,response =  " + response);
		}
		if (response.isSuccess() || !retry)
			return response;
		else
			return checkForRetry(req, apiParams, response);
	}

	private APIResponse checkForRetry(ExtendEWBReqDto requestDto,
			APIParams apiParams, APIResponse response) {
		List<APIError> errors = response.getErrors();
		Integer distance ;
		if (errors.size() > 1)
			return response;
		if ("702".equalsIgnoreCase(errors.get(0).getErrorCode())) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("retrying because of error distance :"
						+ " the current distance {} is invalid for the given "
						+ "pincodes according to NIC"
						+ requestDto.getRemainingDistance());
			}
			String errorResp = errors.get(0).getErrorDesc();
			errorResp = errorResp.replaceAll("[^\\d]", " ");
			errorResp = errorResp.trim();
			errorResp = errorResp.replaceAll(" +", " ");
			if (Strings.isNullOrEmpty(errorResp))
				return response;
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("retrying because of error distance :"
								+ " the current distance {} is invalid for the given "
								+ "pincodes according to NIC"
								+ requestDto.getRemainingDistance());
				LOGGER.info(msg);
			}
			distance = Integer.parseInt(errorResp);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("the correct distance for the given "
						+ "pincodes according to NIC is {} so retrying with "
						+ "this distance" + distance);
				LOGGER.info(msg);
			}
			requestDto.setRemainingDistance(distance);
			Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
			String reqBody = gson.toJson(requestDto);
			response = apiExecutor.execute(apiParams, reqBody);
		}
		return response;
	}

}
