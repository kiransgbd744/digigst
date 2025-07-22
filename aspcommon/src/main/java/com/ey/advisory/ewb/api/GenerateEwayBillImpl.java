/**
 * 
 */
package com.ey.advisory.ewb.api;

import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.ewb.app.api.APIError;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIParams;
import com.ey.advisory.ewb.app.api.APIReqParamConstants;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.common.CalculateDistanceUtil;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("GenerateEwayBillImpl")
@Slf4j
public class GenerateEwayBillImpl implements GenerateEwayBill {

	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("CalculateDistanceUtilImpl")
	CalculateDistanceUtil calcDistUtil;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	private DefaultEWBDistanceCalculator defaultEwbCalcu;

	@Override
	public APIResponse generateEwbill(EwayBillRequestDto requestDto,
			String gstin) {
		reqLogHelper.logAppMessage(requestDto.getDocNo(), null, null,
				"Sending the Generate Eway Bill request to Framework");
		return callExecute(requestDto, gstin);
	}

	private APIResponse callExecute(EwayBillRequestDto requestDto,
			String gstin) {

		Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();

		Pair<EwayBillRequestDto, Boolean> defaultCalc = defaultEwbCalcu
				.calculatefinalDistance(requestDto);
		String reqBody = gson.toJson(defaultCalc.getValue0());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("the ewaybill json " + reqBody);
		}
		KeyValuePair<String, String> apiParam = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);
		APIParams apiParams = new APIParams(APIProviderEnum.EWB,
				APIIdentifiers.GENERATE_EWB, apiParam);
		APIResponse response = apiExecutor.execute(apiParams, reqBody);
		if (response.isSuccess() || !defaultCalc.getValue1())
			return response;
		else
			return checkForRetry(requestDto, apiParams, response);
	}

	private APIResponse checkForRetry(EwayBillRequestDto requestDto,
			APIParams apiParams, APIResponse response) {
		List<APIError> errors = response.getErrors();
		if (errors.size() > 1)
			return response;
		if ("702".equalsIgnoreCase(errors.get(0).getErrorCode())) {
			String errorResp = errors.get(0).getErrorDesc();
			errorResp = errorResp.replaceAll("[^\\d]", " ");
			errorResp = errorResp.trim();
			errorResp = errorResp.replaceAll(" +", " ");
			if (Strings.isNullOrEmpty(errorResp))
				return response;
			if (LOGGER.isErrorEnabled()) {
				String msg = String.format(
						"retrying because of error distance :"
								+ " the current distance %s is invalid for the given "
								+ "pincodes according to NIC for group %s",
						requestDto.getTransDistance(),
						TenantContext.getTenantId());
				LOGGER.error(msg);
			}
			Integer distance = Integer.parseInt(errorResp);
			if (LOGGER.isErrorEnabled()) {
				String msg = String.format(
						"The correct distance for the given "
								+ "pincodes according to NIC is %s so retrying with "
								+ "this distance for group %s",
						distance, TenantContext.getTenantId());
				LOGGER.error(msg);
			}
			requestDto.setTransDistance(distance);
			Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
			String reqBody = gson.toJson(requestDto);
			response = apiExecutor.execute(apiParams, reqBody);
		}
		return response;
	}

}
