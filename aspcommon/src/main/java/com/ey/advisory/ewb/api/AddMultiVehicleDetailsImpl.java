/**
 * 
 */
package com.ey.advisory.ewb.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.KeyValuePair;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.ewb.app.api.APIExecutor;
import com.ey.advisory.ewb.app.api.APIIdentifiers;
import com.ey.advisory.ewb.app.api.APIParams;
import com.ey.advisory.ewb.app.api.APIReqParamConstants;
import com.ey.advisory.ewb.app.api.APIResponse;
import com.ey.advisory.ewb.dto.AddMultiVehicleDetailsReqDto;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Component("AddMultiVehicleDetailsImpl")
@Slf4j
public class AddMultiVehicleDetailsImpl implements AddMultiVehicleDetails {

	@Autowired
	@Qualifier("DefaultEWBAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public APIResponse addMultiVehicle(AddMultiVehicleDetailsReqDto req,
			String gstin) {

		try {
			Gson gson = GsonUtil.newSAPGsonInstanceWithEWBDateFmt();
			String reqBody = gson.toJson(req);

			KeyValuePair<String, String> apiParam = new KeyValuePair<>(
					APIReqParamConstants.GSTIN, gstin);
			APIParams apiParams = new APIParams(APIProviderEnum.EWB,
					APIIdentifiers.MultiVehAdd, apiParam);
			return apiExecutor.execute(apiParams, reqBody);

		} catch (Exception ex) {
			String msg = String.format(
					"Exception while invoking '%s', GSTIN - '%s'",
					APIIdentifiers.MultiVehAdd, gstin);
			LOGGER.error(msg, ex);
			throw new AppException(ex.getMessage());
		}

	}

}
