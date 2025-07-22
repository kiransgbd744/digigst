package com.ey.advisory.gstnapi.services;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * This Class is responsible to resolve the End point,base URL and Auth token
 * check for Public APIs
 * 
 * @author Jithendra.B
 *
 */
@Slf4j
@Component
public class PublicApiEndPointResolver {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	@Qualifier("GenerateAuthTokenServiceImpl")
	private GenerateAuthTokenService authTokenService;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private GSTNAuthTokenService gstnAuthService;

	@Autowired
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	LocalDateTime authExpTime;

	public static void main(String[] args) {
		LocalDateTime dateTime1 = LocalDateTime.of(2021, 1, 1, 9, 0, 0);
		LocalDateTime dateTime2 = LocalDateTime.of(2021, 1, 1, 9, 0, 1);

		if (dateTime2.isAfter(dateTime1)) {
			System.out.println("dateTime2 is after dateTime1");
		} else {
			System.out.println("dateTime1 is not after dateTime2");
		}
	}

	public void resolveEndPoint(String endPointIdentifier) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting the Public API configs from EY_CONFIG");
		}
		if (true) {
			PublicApiContext.setContextMap(PublicApiConstants.END_POINT,
					PublicApiConstants.EY);

			if (authExpTime == null) {
				GstnAPIAuthInfo gstinInfo = gstinAPIAuthInfoRepository
						.findByGstinAndProviderName("000000000000000", "GSTN");
				if (LocalDateTime.now().isAfter(EYDateUtil
						.toLocalDateTime(gstinInfo.getGstnTokenExpiryTime()))) {
					generateAuthToken();
				}
			} else {
				if (authExpTime.isAfter(LocalDateTime.now())) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"Auth Token is active already, Hence skipping auth generation.");
					}
					return;
				} else {
					generateAuthToken();
				}
			}
			// if ("I".equals(gstnAuthService.getAuthTokenStatusForGstin(
			// APIConstants.DEFAULT_PUBLIC_API_GSTIN))
			// && !authTokenService.generateAuthToken(
			// APIConstants.DEFAULT_PUBLIC_API_GSTIN, null)) {
			// LOGGER.error(
			// "Not able to generate Public Auth Token while Fetching Filling
			// Status");
			// throw new AppException(
			// "Not able to generate Public Auth Token");
			// }
		}

		Map<String, Config> publicCallconfigMap = configManager.getConfigs(
				PublicApiConstants.PUBLIC_API,
				PublicApiConstants.PUBLIC_CALL_API_KEY);

		String endPoint = publicCallconfigMap
				.get(PublicApiConstants.END_POINT_KEY) == null
						? PublicApiConstants.EY
						: publicCallconfigMap
								.get(PublicApiConstants.END_POINT_KEY)
								.getValue();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Endpoint found in EY_CONFIG is {}", endPoint);
		}

		if (PublicApiConstants.ALANKIT.equalsIgnoreCase(endPoint)) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"If EndPoint found is {} the get base url according to"
								+ " endPointIdentifier {}",
						endPoint, endPointIdentifier);
			}

			String endPointURL = null;

			if (PublicApiConstants.SEARCH
					.equalsIgnoreCase(endPointIdentifier)) {
				endPointURL = publicCallconfigMap
						.get(PublicApiConstants.SERACH_API_KEY).getValue();
			} else {
				endPointURL = publicCallconfigMap
						.get(PublicApiConstants.RETURNS_API_KEY).getValue();

			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"URL {} is set for EndPoint {} and"
								+ " endPointIdentifier {}",
						endPointURL, endPoint, endPointIdentifier);
			}

			PublicApiContext.setContextMap(PublicApiConstants.URL, endPointURL);
			PublicApiContext.setContextMap(PublicApiConstants.END_POINT,
					PublicApiConstants.ALANKIT);

		} else {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("No Change in URL as endPoint found is {}",
						endPoint);

			}

			PublicApiContext.setContextMap(PublicApiConstants.END_POINT,
					PublicApiConstants.EY);

		}

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("About to check if Public Auth Token is Active");

	}

	private void generateAuthToken() {
		boolean isAuthGen = authTokenService
				.generateAuthToken(APIConstants.DEFAULT_PUBLIC_API_GSTIN, null);
		if (isAuthGen) {
			authExpTime = LocalDateTime.now();
			return;
		} else {
			LOGGER.error(
					"Not able to generate Public Auth Token while Fetching Filling Status");
			throw new AppException("Not able to generate Public Auth Token");
		}

	}
}