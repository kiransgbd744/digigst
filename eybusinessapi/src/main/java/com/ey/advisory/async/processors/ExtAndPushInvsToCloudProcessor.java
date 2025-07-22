package com.ey.advisory.async.processors;

import java.net.SocketException;
import java.util.List;

import javax.net.ssl.SSLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.master.IdTokenGrpMapEntity;
import com.ey.advisory.app.data.repositories.client.BCAPIPushCtrlRepository;
import com.ey.advisory.app.data.repositories.master.IdTokenGrpMapRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.DefaultInvoiceDataExtractor;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.MonitorCommonUtility;
import com.ey.advisory.common.TaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@PropertySource("classpath:application.properties")
@Component("ExtAndPushInvsToCloudProcessor")
public class ExtAndPushInvsToCloudProcessor implements TaskProcessor {

	@Autowired
	private LoggerAdviceRepository logAdvRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	private DefaultInvoiceDataExtractor defaultInvoiceDataExtractor;

	@Autowired
	private BCAPIPushCtrlRepository bcAPIPushCtrlRepo;

	@Autowired
	private Environment env;

	@Autowired
	private IdTokenGrpMapRepository idtokenGrpMapRepo;

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	private MonitorCommonUtility monCommUtility;

	@Override
	public void execute(Message message, AppExecContext context) {
		String jsonString = message.getParamsJson();
		JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
		String batchId = json.get("batchId").getAsString();
		String companyCode = json.get("companyCode").getAsString();
		String sourceId = json.get("sourceId").getAsString();
		String apiIdentifier = json.get("apiIdentifier").getAsString();
		String sapRespPayload = null;
		try {
			bcAPIPushCtrlRepo.updateBatchPushStatus(
					BusinessCriticalConstants.INPROGRESS, "", batchId);
			List<ERPRequestLogEntity> getSuccessReqPayloads = logAdvRepo
					.findByBatchId(batchId);
			JsonObject respObj = defaultInvoiceDataExtractor
					.createCloudJson(getSuccessReqPayloads);
			updateReqJson(respObj, batchId);
			String groupCode = message.getGroupCode();
			sapRespPayload = callSAPAPI(respObj, batchId,
					getSuccessReqPayloads.size(), groupCode, companyCode,
					sourceId, apiIdentifier);
			JsonObject sapRespObj = JsonParser.parseString(sapRespPayload)
					.getAsJsonObject();
			String status = sapRespObj.get("hdr").getAsJsonObject()
					.get("status").getAsString();
			String statusStr = status.equalsIgnoreCase("S")
					? BusinessCriticalConstants.SUCCESS
					: BusinessCriticalConstants.FAILED;

			logAdvRepo
					.updateIsAutoDraftedFlag(batchId,
							statusStr.equalsIgnoreCase(
									BusinessCriticalConstants.SUCCESS) ? true
											: false);

			bcAPIPushCtrlRepo.updateBatchComplStatus(statusStr, sapRespPayload,
					batchId);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"SAPIntegrationAsyncJob  with Report id : %s", batchId);
				LOGGER.debug(msg);
			}
		} catch (JsonParseException jsonEx) {
			String errMsg = String.format(
					"We have received json from Cloud, the response is %s",
					sapRespPayload);
			LOGGER.error(errMsg, jsonEx);
			updateBatchStatus(BusinessCriticalConstants.FAILED,
					jsonEx.getMessage(), batchId);
			throw new AppException(jsonEx);
		} catch (Exception ex) {
			if (ex instanceof SSLException) {
				String errMsg = String.format(
						"We have received SSLException, the response is %s,Hence Reseting the BatchId",
						sapRespPayload);
				LOGGER.error(errMsg, ex);
				updateBatchStatus("FailedSSL", ex.getMessage(), batchId);
				logAdvRepo.resetAutoDraftBatch(batchId);
				throw new AppException(ex);
			} else if (ex instanceof SocketException) {
				String errMsg = String.format(
						"We have received SocketException, the response is %s, Hence Reseting the BatchId",
						sapRespPayload);
				LOGGER.error(errMsg, ex);
				updateBatchStatus(BusinessCriticalConstants.FAILED,
						ex.getMessage(), batchId);
				throw new AppException(ex);
			} else {
				String msg = "Exception occured in AutoDrafting Async Job report";
				LOGGER.error(msg, ex);
				updateBatchStatus(BusinessCriticalConstants.FAILED,
						ex.getMessage(), batchId);
				throw new AppException(ex);
			}
		}
	}

	private String callSAPAPI(JsonObject respObj, String payloadId, int docSize,
			String groupCode, String companyCode, String sourceId,
			String apiIden) throws Exception {

		IdTokenGrpMapEntity idtokenGrpEntity = idtokenGrpMapRepo
				.findByGroupCode(groupCode);
		String idToken = idtokenGrpEntity.getIdToken();
		String uri = "";
		if (BusinessCriticalConstants.CANEWB_V3.equalsIgnoreCase(apiIden)) {
			uri = env.getProperty("sap.savecanewbdocument.api");
		} else if (BusinessCriticalConstants.GENEWB_IRN_V3
				.equalsIgnoreCase(apiIden)) {
			uri = env.getProperty("sap.savegenewbirndocument.api");
		} else {
			uri = env.getProperty("sap.savedocument.api");
		}

		Triplet<Integer, Integer, Integer> connTimeOut = monCommUtility
				.getTimeOutDtls();

		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(connTimeOut.getValue0())
				.setConnectionRequestTimeout(connTimeOut.getValue2())
				.setSocketTimeout(connTimeOut.getValue1()).build();

		HttpPost httpPost = new HttpPost(uri);
		StringEntity params = new StringEntity(respObj.toString());
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.setHeader("idtoken", idToken);
		httpPost.setHeader("payloadId", payloadId);
		httpPost.setHeader("checksum", APIConstants.NOCHECKSUM);
		httpPost.setHeader("docCount", String.valueOf(docSize));
		httpPost.setHeader("companyCode", companyCode);
		httpPost.setHeader("sourceId", sourceId);
		httpPost.setEntity(params);
		httpPost.setConfig(config);
		HttpResponse resp = httpClient.execute(httpPost);
		String apiResponse = EntityUtils.toString(resp.getEntity());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Response Received from Cloud for BatchId {} is {}",
					payloadId, apiResponse);
		}
		return apiResponse;

	}

	private void updateReqJson(JsonObject obj, String batchId) {
		try {
			String reqStr = obj.toString();
			bcAPIPushCtrlRepo.updateSAPReq(reqStr, batchId);
		} catch (Exception e) {
			String msg = "Exception occured while  Saving Req Payload";
			LOGGER.error(msg, e);
		}
	}

	private void updateBatchStatus(String status, String msg, String id) {

		try {
			bcAPIPushCtrlRepo.updateBatchStatus(status, msg, id);
		} catch (Exception e) {
			String msg1 = "Exception occured while updating the job ";
			LOGGER.error(msg1, e);
		}
	}
}
