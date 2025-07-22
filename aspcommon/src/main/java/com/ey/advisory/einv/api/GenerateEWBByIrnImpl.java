package com.ey.advisory.einv.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.einv.app.api.APIError;
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIParams;
import com.ey.advisory.einv.app.api.APIReqParamConstants;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.app.api.CommonUtil;
import com.ey.advisory.einv.common.EinvConstant;
import com.ey.advisory.einv.common.EyEInvCommonUtil;
import com.ey.advisory.einv.dto.GenerateEWBByIrnNICReqDto;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva Reddy
 *
 */
@Component("GenerateEWBByIrnImpl")
@Slf4j
public class GenerateEWBByIrnImpl implements GenerateEWBByIrn {

	private static final List<String> ERROR_LIST = ImmutableList.of("4003",
			"2148", "2143");

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("DefaultEINVAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	CommonUtil commUtil;

	@Override
	public APIResponse generateEWBByIrn(GenerateEWBByIrnNICReqDto req) {
		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"einv.distance", TenantContext.getTenantId());

		boolean nicCalculate = configMap
				.get("einv.distance.niccalculate") == null ? true
						: Boolean.valueOf(configMap
								.get("einv.distance.niccalculate").getValue());

		boolean userInputOpt = configMap
				.get("einv.distance.userinputoption") == null
						? false
						: Boolean.valueOf(
								configMap.get("einv.distance.userinputoption")
										.getValue());
		boolean userInputAlways = configMap
				.get("einv.distance.userinputalways") == null
						? false
						: Boolean.valueOf(
								configMap.get("einv.distance.userinputalways")
										.getValue());

		boolean isPartBPresent = isEWBDetailsPresent(req);

		if (userInputAlways) {
			return callExecute(req);
		}

		if (isPartBPresent) {
			if (nicCalculate) {
				calculateDistance(req);
			} else if (userInputOpt) {
				if (req.getDistance() == null || req.getDistance() == 0) {
					calculateDistance(req);
				}
			}
		}
		return callExecute(req);
	}

	private APIResponse callExecute(GenerateEWBByIrnNICReqDto requestDto) {
		requestDto.setCustPincd(null);
		requestDto.setDispatcherPincd(null);
		requestDto.setShipToPincd(null);
		requestDto.setSuppPincd(null);
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
				APIIdentifiers.GENERATE_EWBByIRN, gstnParam, irpParam);
		reqLogHelper.logAppMessage(null, requestDto.getIrn(), null,
				"Sending  Generate Ewb By Irn Request to Framework");
		APIResponse response = apiExecutor.execute(apiParams, reqBody);
		List<APIError> errors = response.getErrors();
		if (!response.isSuccess() && configuredSrc.equals(APIProviderEnum.EINV)
				&& ERROR_LIST.contains(response.getErrors().get(0).getErrorCode())) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to retry Generate Ewb by Irn with NIC2 for ErrorList {} For GroupCode {} ",
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
				reqLogHelper.logSwitchIdent(availableSources.get(0).name());
				apiParams = new APIParams(availableSources.get(0),
						APIIdentifiers.GENERATE_EWBByIRN, gstnParam, irpParam);
				response = apiExecutor.execute(apiParams, reqBody);
				errors = response.getErrors();
				if (!response.isSuccess() && configuredSrc.equals(APIProviderEnum.EINV)
						&& ERROR_LIST.contains(response.getErrors().get(0).getErrorCode())) {	
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("About to retry Generate Ewb by Irn with NIC2 for ErrorList {} For GroupCode {} ",
								response.getErrors(), TenantContext.getTenantId());
					}
					response = retryWithAlternativeSource(apiParams, gstnParam,
							irpParam, reqBody, configuredSrc);
				}
			}
		}
		return response;

	}

	private void calculateDistance(GenerateEWBByIrnNICReqDto requestDto) {

		Pair<Integer, Integer> pincodePair = getFromAndToPincodes(requestDto);
		Integer fromPinCode = pincodePair.getValue0();
		Integer toPinCode = pincodePair.getValue1();
		if (fromPinCode == null || toPinCode == null) {
			String errMsg = "From/To pincode is missing";
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"calculating the distance mannualy for " + "the given "
							+ "pincodes frompincode %s ,toPincode %s ",
					fromPinCode, toPinCode);
			LOGGER.info(msg);
		}
		if (fromPinCode.equals(toPinCode)) {
			Map<String, Config> configMap = configManager.getConfigs("EINV",
					"einv.distance", TenantContext.getTenantId());
			Integer nicCalculate = configMap.get("einv.distance.input") == null
					? EinvConstant.SAME_PINCODE_DISTANCE
					: Integer.valueOf(
							configMap.get("einv.distance.input").getValue());
			requestDto.setDistance(nicCalculate);
		} else
			requestDto.setDistance(0);
	}

	/*
	 * private boolean checkforPartBDetails(GenerateEWBByIrnNICReqDto
	 * ewbDetails) {
	 * 
	 * return (ewbDetails.getTrnDocDt() != null && ewbDetails.getTrnDocNo() !=
	 * null && ewbDetails.getTransId() != null && ewbDetails.getTransMode() !=
	 * null && ewbDetails.getTransName() != null && ewbDetails.getVehNo() !=
	 * null && ewbDetails.getVehType() != null); }
	 */

	private boolean isEWBDetailsPresent(GenerateEWBByIrnNICReqDto ewbDetails) {

		return ewbDetails != null;

	}

	private Pair<Integer, Integer> getFromAndToPincodes(
			GenerateEWBByIrnNICReqDto requestDto) {
		Integer buyerPincode = requestDto.getCustPincd();
		Integer sellerPincode = requestDto.getSuppPincd();
		Integer dispatchPin = requestDto.getDispatcherPincd();
		Integer shipPin = requestDto.getShipToPincd();

		Integer fromPincode = EyEInvCommonUtil.getFromPinocode(dispatchPin,
				sellerPincode, null);
		Integer toPincode = EyEInvCommonUtil.getToPinocode(shipPin,
				buyerPincode, null);
		return new Pair<Integer, Integer>(fromPincode, toPincode);
	}

	private APIResponse retryWithAlternativeSource(APIParams apiParams,
			KeyValuePair<String, String> gstnParam,
			KeyValuePair<String, String> irpParam, String reqBody,
			APIProviderEnum configuredSrc) {
		irpParam = new KeyValuePair<>(APIReqParamConstants.IRP.toLowerCase(),
				"NIC2");
		apiParams = new APIParams(configuredSrc,
				APIIdentifiers.GENERATE_EWBByIRN, gstnParam, irpParam);
		APIResponse response = apiExecutor.execute(apiParams, reqBody);
		return response;
	}
}
