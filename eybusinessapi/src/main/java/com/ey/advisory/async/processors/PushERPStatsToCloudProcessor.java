package com.ey.advisory.async.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.master.IdTokenGrpMapEntity;
import com.ey.advisory.app.data.repositories.master.IdTokenGrpMapRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.IdTokenUtility;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.common.client.domain.ErpReqAggSummaryEntity;
import com.ey.advisory.common.client.repositories.ErpReqAggSummaryRepo;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@PropertySource("classpath:application.properties")
@Component("PushERPStatsToCloudProcessor")
public class PushERPStatsToCloudProcessor implements TaskProcessor {

	@Autowired
	private IdTokenUtility idTokeUtility;

	@Autowired
	private Environment env;

	@Autowired
	private ErpReqAggSummaryRepo erpReqAggSummRepo;

	@Autowired
	private IdTokenGrpMapRepository idtokenGrpMapRepo;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Override
	public void execute(Message message, AppExecContext context) {
		String jsonString = message.getParamsJson();
		String sapRespPayload = null;
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject sapRespObj = null;
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		String dateFromProc = json.get("dateFromProc").getAsString();
		String dateToProc = json.get("dateToProc").getAsString();

		LocalDate datetoFetchData = convertStrtoDate(dateFromProc);
		LocalDate datetoFetchDataToDate = convertStrtoDate(dateToProc);

		JsonObject finalObj = new JsonObject();
		try {
			List<ErpReqAggSummaryEntity> summaryList = erpReqAggSummRepo
					.findActiveRecordsBetweenSummaryDates(datetoFetchData, datetoFetchDataToDate);

			Map<String, Config> regionconfigMap = configManager
					.getConfigs("BCAPI", "auto.drafting", "DEFAULT");
			String activeRegion = regionconfigMap
					.containsKey("auto.drafting.region")
							? regionconfigMap.get("auto.drafting.region")
									.getValue()
							: "AMD";
			activeRegion = activeRegion.equalsIgnoreCase("FF") ? "Frankfurt"
					: "Amsterdam";

			for (ErpReqAggSummaryEntity erpReqAggSummaryEntity : summaryList) {
				erpReqAggSummaryEntity.setRegion(activeRegion);
			}

			if (summaryList.isEmpty()) {
				String errMsg = String.format(
						"No Data Available in erpReqAggSummRepo Entity for the"
								+ " group %s and summaryDate %s",
						message.getGroupCode(), dateFromProc);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Data is Available in erpReqAggSummRepo Entity for the"
								+ " group %s and summaryDate %s",
						message.getGroupCode(), dateFromProc);
				LOGGER.debug(msg);
			}
			String reqDtos = gson.toJson(summaryList);
			JsonElement dataElement = JsonParser.parseString(reqDtos);
			JsonArray dataArray = dataElement.getAsJsonArray();
			finalObj.add("req", dataArray);
			finalObj.addProperty("summaryDate", dateFromProc);
			finalObj.addProperty("summaryDateTo", dateToProc);
			finalObj.addProperty("region", activeRegion);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Sap Request Json {}", finalObj);
			}
			sapRespPayload = callSaveStatsAPI(finalObj, message.getGroupCode());
			sapRespObj = JsonParser.parseString(sapRespPayload)
					.getAsJsonObject();
			String status = sapRespObj.get("hdr").getAsJsonObject()
					.get("status").getAsString();
			String statusStr = status.equalsIgnoreCase("S") ? "Success"
					: "Failed";

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Response Received from SAP %s for  Group %s and Summary Date %s",
						sapRespObj.toString(), message.getGroupCode(),
						datetoFetchData);
				LOGGER.debug(msg);
			}
			if (statusStr.equals("Success")) {
				erpReqAggSummRepo.updatePushedCloudStatuses(true, datetoFetchData, datetoFetchDataToDate);
			} else {
				erpReqAggSummRepo.updatePushedCloudStatuses(false, datetoFetchData, datetoFetchDataToDate);
			}

		} catch (JsonParseException jsonEx) {
			String errMsg = String.format(
					"We have received json from Cloud, the response is %s",
					sapRespPayload);
			LOGGER.error(errMsg, jsonEx);
		} catch (Exception ex) {
			String msg = "Exception occured in Async Job report";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
	}

	private String callSaveStatsAPI(JsonObject respObj, String groupCode) {
		try {
			IdTokenGrpMapEntity idtokenGrpEntity = idtokenGrpMapRepo
					.findByGroupCode(groupCode);
			String idToken = null;
			if (Strings.isNullOrEmpty(idtokenGrpEntity.getIdToken())) {
				idToken = idTokeUtility.getIdTokenValue(
						idtokenGrpEntity.getUsername(),
						idtokenGrpEntity.getPassword());
			LocalDateTime expiryTime = LocalDateTime.now().plusHours(1);
				idtokenGrpMapRepo.updateIdToken(idToken, groupCode, expiryTime);
			} else {
				idToken = idtokenGrpEntity.getIdToken();
			}
			HttpPost httpPost = new HttpPost(
					env.getProperty("sap.saveerplogs.api"));
			StringEntity params = new StringEntity(respObj.toString());
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.setHeader("idtoken", idToken);
			httpPost.setEntity(params);
			HttpResponse resp = httpClient.execute(httpPost);
			String apiResponse = EntityUtils.toString(resp.getEntity());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Response Received from Cloud for Group {} is {}",
						groupCode, apiResponse);
			}
			return apiResponse;
		} catch (Exception e) {
			String msg = "Exception occured while calling Save Document API";
			LOGGER.error(msg, e);
			throw new AppException(e);
		}
	}

	private LocalDate convertStrtoDate(String dateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(dateStr, formatter);
		return localDate;
	}

}
