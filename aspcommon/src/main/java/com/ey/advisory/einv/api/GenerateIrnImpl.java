/**
 * 
 */
package com.ey.advisory.einv.api;

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
import com.ey.advisory.einv.app.api.APIExecutor;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.app.api.APIParams;
import com.ey.advisory.einv.app.api.APIReqParamConstants;
import com.ey.advisory.einv.app.api.APIResponse;
import com.ey.advisory.einv.common.CalculateDistanceUtil;
import com.ey.advisory.einv.common.EinvConstant;
import com.ey.advisory.einv.common.EyEInvCommonUtil;
import com.ey.advisory.einv.dto.EinvEwbDetails;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("GenerateIrnImpl")
@Slf4j
public class GenerateIrnImpl implements GenerateIrn {
	@Autowired
	@Qualifier("DefaultEINVAPIExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	@Qualifier("EinvCalculateDistanceUtilImpl")
	private CalculateDistanceUtil calcDistUtil;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	public APIResponse generateIrn(EinvoiceRequestDto requestDto,
			String gstin) {
		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"einv.distance", TenantContext.getTenantId());

		boolean nicCalculate = configMap
				.get("einv.distance.niccalculate") == null ? Boolean.TRUE
						: Boolean.valueOf(configMap
								.get("einv.distance.niccalculate").getValue());

		boolean userInputOpt = configMap
				.get("einv.distance.userinputoption") == null
						? Boolean.FALSE
						: Boolean.valueOf(
								configMap.get("einv.distance.userinputoption")
										.getValue());
		boolean userInputAlways = configMap
				.get("einv.distance.userinputalways") == null
						? Boolean.FALSE
						: Boolean.valueOf(
								configMap.get("einv.distance.userinputalways")
										.getValue());

		boolean isPartBPresent = isEWBDetailsPresent(
				requestDto.getEwbDetails());

		if (userInputAlways) {
			return callExecute(requestDto, gstin);
		}

		if (isPartBPresent && (nicCalculate || (userInputOpt
				&& requestDto.getEwbDetails().getDistance() == null
				|| requestDto.getEwbDetails().getDistance() == 0)))
			calculateDistance(requestDto);

		reqLogHelper.logAppMessage(requestDto.getDocDtls().getNo(), null, null,
				"Sending the Generate Einvoice request to Framework");

		return callExecute(requestDto, gstin);

	}

	private void calculateDistance(EinvoiceRequestDto requestDto) {

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
			requestDto.getEwbDetails().setDistance(nicCalculate);
		} else
			requestDto.getEwbDetails()
					.setDistance(EinvConstant.DIFF_PINCODE_DISTANCE);
	}

	private boolean isEWBDetailsPresent(EinvEwbDetails ewbDetails) {
		return ewbDetails != null;
	}

	private APIResponse callExecute(EinvoiceRequestDto requestDto,
			String gstin) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String reqBody = gson.toJson(requestDto);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("the EINVOICE json " + reqBody);
		}
		KeyValuePair<String, String> apiParam = new KeyValuePair<>(
				APIReqParamConstants.GSTIN, gstin);
		APIParams apiParams = new APIParams(APIProviderEnum.EINV,
				APIIdentifiers.GENERATE_EINV, apiParam);
		return apiExecutor.execute(apiParams, reqBody);
	}

	private Pair<Integer, Integer> getFromAndToPincodes(
			EinvoiceRequestDto requestDto) {
		Integer buyerPincode = requestDto.getBuyerDtls() != null
				? requestDto.getBuyerDtls().getPin() : null;
		Integer sellerPincode = requestDto.getSellerDtls() != null
				? requestDto.getSellerDtls().getPin() : null;
		Integer dispatchPin = requestDto.getDispDtls() != null
				? requestDto.getDispDtls().getPin() : null;
		Integer shipPin = requestDto.getShipDtls() != null
				? requestDto.getShipDtls().getPin() : null;

		Integer fromPincode = EyEInvCommonUtil.getFromPinocode(dispatchPin,
				sellerPincode, requestDto.getDocCategory());
		Integer toPincode = EyEInvCommonUtil.getToPinocode(shipPin,
				buyerPincode, requestDto.getDocCategory());
		return new Pair<Integer, Integer>(fromPincode, toPincode);
	}

}
