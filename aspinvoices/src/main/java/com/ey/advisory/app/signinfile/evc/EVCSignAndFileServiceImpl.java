package com.ey.advisory.app.signinfile.evc;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.SignAndFileEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParamType;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@Component("EVCSignAndFileServiceImpl")
public class EVCSignAndFileServiceImpl implements EVCSignAndFileService {

	@Autowired
	@Qualifier("DefaultGSTNAPIExecutor")
	private APIExecutor apiExecutor;

	@Override
	public Pair<Boolean, String> getOtpGstn(SignAndFileEntity entity) {
		try {
			APIParam param1 = new APIParam("gstin", entity.getGstin());
			APIParam param2 = new APIParam("state-cd",
					entity.getGstin().substring(0, 2), APIParamType.HEADER);
			APIParam param3 = new APIParam("pan", entity.getPan(),APIParamType.URLPARAM);
			APIParam param4 = null;

			switch (entity.getReturnType()) {
			case "GSTR1":
				param4 = new APIParam("form_type", "R1",APIParamType.URLPARAM);
				break;
			case "GSTR3B":
				param4 = new APIParam("form_type", "R3B",APIParamType.URLPARAM);
			break;
			case "GSTR6":
				param4 = new APIParam("form_type", "R6",APIParamType.URLPARAM);
				break;
			case "ITC04":
				param4 = new APIParam("form_type", "ITC04",APIParamType.URLPARAM);
				break;
				
			case "DRC01B":
				param4 = new APIParam("form_type", "DRC01B",APIParamType.URLPARAM);
				break;	
			case "GSTR1A":
				param4 = new APIParam("form_type", "R1A",APIParamType.URLPARAM);
				break;
			}
			
			String groupCode = TenantContext.getTenantId();
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.EVC_SIGN_FILE, param1, param2, param3,
					param4);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("PARAMS {} passed to EVC OTP for groupcode {}",
						params, groupCode);
			}
			APIResponse apiRespOtp = apiExecutor.execute(params, "EVCOTP");

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Response from gstn for evc otp {} for groupcode {}",
						apiRespOtp.toString(), groupCode);
			}
			if (apiRespOtp.isSuccess()) {

				return new Pair<>(Boolean.TRUE, apiRespOtp.getResponse());
			} else {
				LOGGER.error(
						"we have received Error Response from GSTN"
								+ " for EVC OTP GSTIN {}, Taxperiod {}, Response is {} ",
						entity.getGstin(), entity.getTaxPeriod(),
						apiRespOtp.getErrors());
				return new Pair<>(Boolean.FALSE,
						apiRespOtp.getError().getErrorDesc());
			}
		} catch (Exception ex) {
			String msg = "Exception while EvcOtpGstn api Call";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

	}
}
