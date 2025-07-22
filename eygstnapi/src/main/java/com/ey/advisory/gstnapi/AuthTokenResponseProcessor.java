package com.ey.advisory.gstnapi;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.APIPersistenceManager;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIParams;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.api.impl.APIAuthInfo;
import com.ey.advisory.core.api.impl.APIConfig;
import com.ey.advisory.core.api.impl.APIExecParties;
import com.ey.advisory.core.api.impl.APIResponseProcessor;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;

@Component("AuthTokenResponseProcessor")
public class AuthTokenResponseProcessor implements APIResponseProcessor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuthTokenResponseProcessor.class);

	@Autowired
	@Qualifier("APIDBPersistenceManagerImpl")
	private APIPersistenceManager persistenceManager;

	@Override
	public APIResponse processResponse(APIParams params, APIConfig config,
			APIExecParties parties, APIAuthInfo authInfo, APIResponse response,
			Map<String, Object> context) {

		// The Auth Token Details should be persisted here, for later use.
		GSTNAPIAuthTokenResponse authResp = (GSTNAPIAuthTokenResponse) response;
		GSTNAPIEndUser endUser = (GSTNAPIEndUser) parties.getApiEndUser();
		String gstin = endUser.getGstinConfig().getGstin();
		Date curDate = java.sql.Timestamp.valueOf(
				EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
		if (authInfo == null) {
			String msg = String.format("Authorization Info is not available. "
					+ "The AuthToken Request should be made only after "
					+ "a successful OTP request. GSTIN = '%s'", gstin);
			LOGGER.error(msg);
			throw new APIException(msg);
		}
		Date expiryTime = java.sql.Timestamp.valueOf(
				EYDateUtil.toUTCDateTimeFromLocal(authResp.getExpiryTime()));
		GstnAPIAuthInfo apiAuthInfo = (GstnAPIAuthInfo) authInfo;
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Before Conversion Expiry Time is %s for GSTIN %s",
					authResp.getExpiryTime().toString(), apiAuthInfo.getGstin());
			LOGGER.debug(msg);
		}
		apiAuthInfo.setGstnToken(authResp.getAuthToken());
		apiAuthInfo.setSessionKey(authResp.getSk());
		apiAuthInfo.setUpdatedDate(curDate);
		apiAuthInfo.setGstnTokenGenTime(curDate);
		apiAuthInfo.setGstnTokenExpiryTime(expiryTime);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Auth Token will expire at %s for GSTIN %s",
					expiryTime.toString(), apiAuthInfo.getGstin());
			LOGGER.debug(msg);
		}
		persistenceManager.saveAPIAuthInfo(apiAuthInfo);
		return response;
	}

}
