package com.ey.advisory.app.azure;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.PerformRetryException;
import com.ey.advisory.core.api.APIProviderEnum;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Saif.S
 *
 */
@Slf4j
@Component("FetchAzureAuthTokensServiceImpl")
public class FetchAzureAuthTokensServiceImpl
		implements FetchAzureAuthTokensService {

	@Autowired
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	private Environment env;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	public static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final String API_KEY = "sap.azure.gsp.api.key";

	private static final String API_SECRET = "sap.azure.gsp.api.secret";

	private static final int RETRY_COUNT = 3;

	@Override
	public void getAuthTokens(String sapGroupCode, String azureGroupCode) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside FetchAzureAuthTokensServiceImpl.getAuthTokens()"
							+ " for group: %s", sapGroupCode);
			LOGGER.debug(msg);
		}
		List<String> activeGstins = gSTNDetailRepository
				.filterGstinBasedOnRegTypeAndgroupCode(sapGroupCode);

		if (activeGstins.isEmpty()) {
			LOGGER.error("There are no active gstins for group: {}",
					sapGroupCode);
			return;
		}

		List<GstnAPIAuthInfo> allAuthInfoEntries = gstinAPIAuthInfoRepository
				.findAllByProviderNameAndGroupCode(APIProviderEnum.GSTN.name(),
						sapGroupCode);

		if (allAuthInfoEntries.isEmpty()) {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("Fetching authtoken for active gstins for the"
								+ "  first time with all active gstins "
								+ " for group: %s", sapGroupCode);
				LOGGER.debug(msg);
			}
			callAzureApi(activeGstins, activeGstins, sapGroupCode,
					azureGroupCode);
		} else {

			Date expiryStTime = new Date();
			Calendar cal = Calendar.getInstance(); // creates calendar
			cal.setTime(expiryStTime); // sets calendar time/date
			cal.add(Calendar.MINUTE, 30); // adds 30 minutes
			Date expiryEndTime = cal.getTime();

			List<GstnAPIAuthInfo> refreshableAuthInfoList = gstinAPIAuthInfoRepository
					.findAllByProviderNameAndGroupCodeAndGstinInAndGstnTokenExpiryTimeLessThanOrGstnTokenExpiryTimeBetween(
							APIProviderEnum.GSTN.name(), sapGroupCode,
							activeGstins, expiryStTime, expiryStTime,
							expiryEndTime);

			List<String> existingGstins = filterOnlyGstins(allAuthInfoEntries);
			// filtering all existing gstins with active gstins
			activeGstins.removeAll(existingGstins);

			if (refreshableAuthInfoList.isEmpty() && activeGstins.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.warn("There are no eligible Gstin's to refresh"
							+ " for group: {}", sapGroupCode);
				}
				return;
			}
			List<String> refreshableGstins = filterOnlyGstins(
					refreshableAuthInfoList);
			if (!activeGstins.isEmpty()) {
				refreshableGstins.addAll(activeGstins);
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Fetching authtoken for gstins %s, for group: %s",
						refreshableGstins.toString(), sapGroupCode);
				LOGGER.debug(msg);
			}
			callAzureApi(refreshableGstins, activeGstins, sapGroupCode,
					azureGroupCode);
		}
	}

	private List<String> filterOnlyGstins(List<GstnAPIAuthInfo> authInfoList) {
		return authInfoList.stream().map(GstnAPIAuthInfo::getGstin)
				.collect(Collectors.toList());
	}

	private void callAzureApi(List<String> gstins, List<String> gstinsToSave,
			String sapGroupCode, String azureGroupCode) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			Map<String, Config> configMap = configManager
					.getConfigs("SAP_AZURE", "sap.azure.gsp.api");

			if (configMap.get(API_KEY) == null
					|| configMap.get(API_SECRET) == null) {
				LOGGER.error("There is no API key and Secret configured");
				return;
			}
			String jsonDto = gson.toJson(new GstinsDto(gstins));

			HttpPost httpPost = new HttpPost(
					env.getProperty("sap.azure.authtokens.url"));
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("X-TENANT-ID", azureGroupCode);
			httpPost.setHeader("api_key", configMap.get(API_KEY).getValue());
			httpPost.setHeader("api_secret",
					configMap.get(API_SECRET).getValue());
			StringEntity entity = new StringEntity(jsonDto.toString());
			httpPost.setEntity(entity);

			LOGGER.debug("Calling Azure API with Request :: {}",
					httpPost.toString());
			Pair<String, Integer> resp = execute(httpPost);
			LOGGER.debug("HTTP Response from Azure :: {}", resp.toString());
			Integer httpStatusCd = resp.getValue1();
			String apiResp = resp.getValue0();
			if (httpStatusCd == 200) {
				AzureAuthTokensDto response = gson.fromJson(apiResp.toString(),
						AzureAuthTokensDto.class);
				LOGGER.debug("Response from Azure :: {}", response.toString());
				if ("0".equals(response.getStatusCd())) {
					LOGGER.error(
							"Azure Api returned error with statusCd '0'"
									+ " for group {}, {} ",
							sapGroupCode, response);
					LOGGER.error("Error code : {} and ErrorMsg : {}",
							response.getError().getErrorCd(),
							response.getError().getErrorMsg());
					return;
				} else if ("1".equals(response.getStatusCd())) {
					LOGGER.debug("Success Response from Azure :: {}",
							response.getAuthTokenDetails().toString());
					saveOrUpdateAuthTokens(response.getAuthTokenDetails(),
							gstinsToSave, sapGroupCode);
				}
			} else {
				LOGGER.error("Azure retured httpStatusCode: {} for group {}",
						httpStatusCd, sapGroupCode);
				LOGGER.error("Recieved error response from azure:{}",
						resp.toString());
				return;
			}
		} catch (Exception ee) {
			String msg = String.format("Exception occured while calling azure"
					+ " api for group{}", sapGroupCode);
			LOGGER.error(msg, ee);
			throw new AppException(ee, msg);
		}
	}

	private Pair<String, Integer> execute(HttpUriRequest req) throws Exception {
		int retryCount = 0;
		Pair<String, Integer> pair = null;
		String reqUrl = req.getURI().toString();
		while (true) {
			try {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Hitting GATEWAY API {} for {} time", reqUrl,
							(retryCount + 1));
				return executeApi(req);
			} catch (PerformRetryException ex) {
				retryCount++;
				pair = ex.getRespPair();
				LOGGER.error(
						"We have received Invalid Response from GATEWAY API,"
								+ " Response is {},"
								+ " Hence About retry GATEWAY API {} for {} time",
						pair.getValue0(), reqUrl, (retryCount + 1));
				if (retryCount >= RETRY_COUNT)
					break;
			}
		}
		return pair;
	}

	private Pair<String, Integer> executeApi(HttpUriRequest req)
			throws Exception {
		HttpResponse resp = null;
		try {
			resp = httpClient.execute(req);
		} catch (Exception e) {
			LOGGER.error("Error While invoking GATEWAY API,", e);
			Pair<String, Integer> pair = new Pair<>(e.getMessage(), 500);
			throw new PerformRetryException(pair);
		}
		Integer httpStatusCd = resp.getStatusLine().getStatusCode();
		String apiResp = EntityUtils.toString(resp.getEntity());
		Pair<String, Integer> pair = new Pair<>(apiResp, httpStatusCd);
		boolean isRetryable = isRetryable(apiResp);
		if (isRetryable)
			throw new PerformRetryException(pair);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("GATEWAY has Returned Valid Response - {}", apiResp);
		return pair;

	}

	private static boolean isRetryable(String apiResp) {

		boolean isRetryEnabled = false;
		try {
			new JsonParser().parse(apiResp);
		} catch (Exception e) {
			LOGGER.error(
					"We have received Invalid Response from GATEWAY, response is {}",
					apiResp);
			isRetryEnabled = true;
		}
		return isRetryEnabled;
	}

	private void saveOrUpdateAuthTokens(
			List<AuthTokenDetailsDto> authTokenDetails,
			List<String> gstinsToSave, String groupCode) {
		try {
			/*
			 * If both the lists are of equal size then all are new Entries so we
			 * will save
			 */
			if (authTokenDetails.size() == gstinsToSave.size()) {
				saveAuthTokens(authTokenDetails, groupCode);
			}
			/*
			 * If gstinsToSave list is empty then we do not have any gstins to
			 * save, so we update all.
			 */
			else if (gstinsToSave.isEmpty()) {
				updateAuthTokens(authTokenDetails, groupCode);
			}
			/*
			 * else, If we have enties in gstinsToSave list and in authTokenList
			 * then we save new entries and update the existing entries.
			 */
			else {
				List<AuthTokenDetailsDto> toSaveList = new ArrayList<>();
				for (AuthTokenDetailsDto dto : authTokenDetails) {
					if (gstinsToSave.contains(dto.getGstin())) {
						toSaveList.add(dto);
					}
				}
				authTokenDetails.removeAll(toSaveList);
				saveAuthTokens(toSaveList, groupCode);
				updateAuthTokens(authTokenDetails, groupCode);
			}
		} catch (Exception ee) {
			String msg = String.format("Exception occured while saving/updating"
					+ " Authtoken details for group {}", groupCode);
			LOGGER.error(msg, ee);
			throw new AppException(msg, ee);
		}
	}

	private void updateAuthTokens(List<AuthTokenDetailsDto> authTokenDetails,
			String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside FetchAzureAuthTokensServiceImpl.updateAuthTokens()"
							+ " for group: %s. Recieved dtos to update- %s",
					groupCode, authTokenDetails.toString());
			LOGGER.debug(msg);
		}
		for (AuthTokenDetailsDto dto : authTokenDetails) {
			if (StringUtils.isEmpty(dto.getErrorMsg())
					&& !StringUtils.isEmpty(dto.getToken())) {
				Date genTime = convertToValidDate(dto.getGenTime());
				Date expTime = convertToValidDate(dto.getExpTime());
				gstinAPIAuthInfoRepository.updateAuthTokens(dto.getToken(),
						dto.getSk(), genTime, expTime, new Date(),
						dto.getGstin(), APIProviderEnum.GSTN.name());
			} else {
				LOGGER.error(
						"Recieved error reponse: {} for gstin: {} in group {}",
						dto.getErrorMsg(), dto.getGstin(), groupCode);
			}
		}
	}

	private void saveAuthTokens(List<AuthTokenDetailsDto> authTokenDetails,
			String groupCode) {
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Inside FetchAzureAuthTokensServiceImpl.saveAuthTokens()"
							+ " for group: %s. Recieved dtos to save - %s",
					groupCode, authTokenDetails.toString());
			LOGGER.debug(msg);
		}
		List<GstnAPIAuthInfo> authTokenEntities = convertToEntities(
				authTokenDetails, groupCode);
		if (!authTokenEntities.isEmpty()) {
			gstinAPIAuthInfoRepository.saveAll(authTokenEntities);
		}
	}

	private List<GstnAPIAuthInfo> convertToEntities(
			List<AuthTokenDetailsDto> authTokenDetails, String groupCode) {

		List<GstnAPIAuthInfo> entityList = new ArrayList<>();
		for (AuthTokenDetailsDto dto : authTokenDetails) {
			if (StringUtils.isEmpty(dto.getErrorMsg())
					&& !StringUtils.isEmpty(dto.getToken())) {
				GstnAPIAuthInfo entity = new GstnAPIAuthInfo();
				entity.setGroupCode(groupCode);
				entity.setProviderName(APIProviderEnum.GSTN.name());
				entity.setGstin(dto.getGstin());
				entity.setSessionKey(dto.getSk());
				entity.setGstnToken(dto.getToken());
				entity.setGstnTokenGenTime(
						convertToValidDate(dto.getGenTime()));
				entity.setGstnTokenExpiryTime(
						convertToValidDate(dto.getExpTime()));
				Date currTime = new Date();
				entity.setCreatedDate(currTime);
				entity.setUpdatedDate(currTime);
				entity.setAppKey(groupCode);
				entityList.add(entity);
			} else {
				LOGGER.error(
						"Recieved error reponse: {} for gstin: {} in group {}",
						dto.getErrorMsg(), dto.getGstin(), groupCode);
			}
		}
		return entityList;
	}

	private Date convertToValidDate(String date) {
		try {
			LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
			LocalDateTime localDateTime = EYDateUtil
					.toUTCDateTimeFromIST(dateTime);
			Date convertedDate = java.sql.Timestamp.valueOf(localDateTime);
			LOGGER.debug("Converted UTC Date::{}", convertedDate.toString());
			return convertedDate;
		} catch (Exception e) {
			LOGGER.error("Error occured in convertToValidDate", e);
			throw new AppException(e);
		}
	}
}
