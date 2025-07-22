package com.ey.advisory.app.services.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIExecutor;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.core.api.APIParam;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthOtpCommonUtility {

	@Autowired
	@Qualifier("DefaultNonStubExecutor")
	private APIExecutor apiExecutor;

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	public APIResponse getOtpApiResp(String gstin, String groupCode) {
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			GstnAPIAuthInfo apiAuthInfo = persistenceManager
					.loadAPIAuthInfo(gstin, APIProviderEnum.GSTN.name());
			if (apiAuthInfo != null && apiAuthInfo.getAppKey() != null
					&& apiAuthInfo.getGstnToken() != null) {
				APIParams logoutParams = new APIParams(groupCode,
						APIProviderEnum.GSTN,
						APIIdentifiers.GSTN_TAXPAYER_LOGOUT, param1);
				boolean isLoggedOut = authTokenLogout(logoutParams);
				if (!isLoggedOut)
					LOGGER.error("Logout API Request Failed Before"
							+ " OTP Generation for Gstin {} ", gstin);
			} else {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("OTP or AuthToken is never "
							+ "generated in the system for this gstin '%s',"
							+ " Hence we are proceed with OTP Request"
							+ " skipping Logout", gstin);
					LOGGER.debug(msg);
				}
			}
			APIParams params = new APIParams(groupCode, APIProviderEnum.GSTN,
					APIIdentifiers.GET_OTP, param1);
			APIResponse resp = apiExecutor.execute(params, "");
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executed the OTP API Request, Response is '%s'", resp);
				LOGGER.debug(msg);
			}
			return resp;
		} catch (Exception e) {
			LOGGER.error("Exception while generating OTP ", e);
			throw new AppException(e.getMessage());
		}
	}

	public Boolean authTokenLogout(APIParams logoutParams) {
		APIResponse logoutResp = apiExecutor.execute(logoutParams, "");
		return logoutResp.isSuccess();
	}

	public APIResponse getAuthTokenResp(String gstin, String otp) {
		String groupCode = TenantContext.getTenantId();
		try {
			APIParam param1 = new APIParam("gstin", gstin);
			APIParam param3 = new APIParam("otp", otp);
			String apiIdentifier = gstin
					.equals(APIConstants.DEFAULT_PUBLIC_API_GSTIN)
							? APIIdentifiers.GET_GSP_AUTH_TOKEN
							: APIIdentifiers.GET_AUTH_TOKEN;
			APIParams authParams = new APIParams(groupCode,
					APIProviderEnum.GSTN, apiIdentifier, param1, param3);
			APIResponse resp = apiExecutor.execute(authParams, "");
			return resp;
		} catch (Exception ex) {
			LOGGER.error("Exception while generating Auth Token ", ex);
			throw new AppException(ex.getMessage());
		}
	}
}
