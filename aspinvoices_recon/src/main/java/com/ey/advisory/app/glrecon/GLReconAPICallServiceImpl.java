package com.ey.advisory.app.glrecon;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.GlReconReportConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlReconGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.domain.client.GlReconSFTPConfigEntity;
import com.ey.advisory.repositories.client.GLReconSFTPConfigRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sakshi.jain
 *	
 * This service calls GL engine with the request id 
 */

@Component
@Slf4j
public class GLReconAPICallServiceImpl {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	@Autowired
	private GLReconSFTPConfigRepository sftpConfig;
	
	@Autowired
	private GLReconReportConfigRepository configRepo;
	
	@Autowired
	private GlReconGstinRepository configGstinRepo;
	
	public String glReconSendReqId(Long reqId) {
		
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			
			// have to be changed
			
			String accesToken = null;
			Map<String, Config> configMapGlRecon = configManager
					.getConfigs("GL_RECON", "glrecon.details",
							"DEFAULT");

			String usernamePdf = configMapGlRecon
					.get("glrecon.details.username") == null
							? ""
							: configMapGlRecon
									.get("glrecon.details.username")
									.getValue();

			String passwordPdf = configMapGlRecon
					.get("glrecon.details.password") == null
							? ""
							: configMapGlRecon
									.get("glrecon.details.password")
									.getValue();

			String apiAccesskeyPdf = configMapGlRecon
					.get("glrecon.details.apiAccessKey") == null
							? ""
							: configMapGlRecon
									.get("glrecon.details.apiAccessKey")
									.getValue();

			String getAuthRespBody = getAccessToken(usernamePdf, passwordPdf,
							apiAccesskeyPdf);

			JsonObject requestObject = JsonParser
					.parseString(getAuthRespBody)
					.getAsJsonObject();
			
			LOGGER.debug(" requestObject {} ",requestObject);
			
			int statusCode = requestObject.get("status")
					.getAsInt();

			if (statusCode == 1) {
				accesToken = requestObject.get("accessToken")
						.getAsString();
			} else {
				String errMsg = "Access Token is not Available from gl recon .";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			
			Map<String, Config> configMap = configManager
					.getConfigs("GL_RECON", "glrecon.sendConfigId", "DEFAULT");

			String tenantCode = TenantContext.getTenantId();
			String url = configMap.get("glrecon.sendConfigId").getValue();
			
			List<GlReconSFTPConfigEntity> configs = (List<GlReconSFTPConfigEntity>) sftpConfig
					.findAll();
			
			GlReconReportConfigEntity confgEnt = configRepo.findByConfigId(reqId);
			
			List<String> gstins = configGstinRepo.findAllGstinsByConfigId(reqId);
			
			HttpPost httpPost = new HttpPost(url);
			JsonObject obj = new JsonObject();
			obj.addProperty("requestId", reqId.toString());
			obj.addProperty("groupCode", tenantCode);
			obj.addProperty("sftpUsername", configs.get(0).getSftpUsername());
			obj.addProperty("sftpPassword", configs.get(0).getSftpPaswrd());
			obj.addProperty("fromTaxPeriod", confgEnt.getFromTaxPeriod());
			obj.addProperty("toTaxPeriod", confgEnt.getToTaxPeriod());
			obj.addProperty("gstins", String.join(",",gstins));
			
			httpPost.setHeader("apiaccesskey", apiAccesskeyPdf);
			httpPost.setHeader("accesstoken", accesToken);
		
			
			JsonObject req = new JsonObject();
			req.add("req", obj);
			LOGGER.debug("request send to GL Recon Engine req- {} ",req);
			httpPost.setHeader("Content-Type", "application/json");
			
			StringEntity entity = new StringEntity(gson.toJson(req).toString());
			httpPost.setEntity(entity);
			
			LOGGER.debug("request send to GL Recon Engine - {} ",entity.getContent());
			
			HttpResponse resp = httpClient.execute(httpPost);

			Integer httpStatusCd = resp.getStatusLine().getStatusCode();
			String apiResp = EntityUtils.toString(resp.getEntity());
			if (httpStatusCd == 200) {

				LOGGER.debug(
						"Received success response from GL Recon Engine for reqid push,"
								+ " the response is {}",
						apiResp);
			} else {
				LOGGER.error(
						"Received failure response from GL Recon Engine for reqid push, "
								+ "the response is {}",
						apiResp);
			}
			return apiResp;

		} catch (Exception ex) {
			JsonObject respObj = new JsonObject();
			LOGGER.error("Exception while calling glReconSendReqId ", ex);
			respObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			respObj.add("resp", ewbGson.toJsonTree(ex.getMessage()));
			return respObj.toString();
		}
	}
	
	public String getAccessToken(String userName, String password,
			String apiAccessKey) {
		try {
			Map<String, Config> configMap = configManager
					.getConfigs("GLRECON", "glrecon.details", "DEFAULT");

			String authUrl = configMap.get("glrecon.details.authtoken") == null ? ""
					: configMap.get("glrecon.details.authtoken").getValue();

			HttpPost httpPost = new HttpPost(authUrl);
			httpPost.setHeader("username", userName);
			httpPost.setHeader("password", password);
			httpPost.setHeader("apiaccesskey", apiAccessKey);
			
			

			LOGGER.debug("Requesting pdf {} ", httpPost.getRequestLine());
			

			for (org.apache.http.Header header : httpPost.getAllHeaders()) {
				LOGGER.debug("Header: " + header.getName() + " = "
						+ header.getValue());
			}
			
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpPost, responseHandler);
			LOGGER.debug("responseBody pdf {} ", responseBody);

			return responseBody;
		} catch (Exception ex) {
			LOGGER.error("Exception in PDF Code Validator {}", ex);
			return ex.getMessage();
		}

	}
}
