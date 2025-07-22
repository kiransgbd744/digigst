package com.ey.advisory.app.approvalWorkflow;

import java.io.File;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApprovalEmailUtility {

	@Autowired
	@Qualifier("InternalHttpClient")
	private HttpClient httpClient;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public String multiPartEmail(boolean isFileRequred, File files,
			ApprWkflwEmailDetailsDto requestDto) {

		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			Map<String, Config> configMap = configManager.getConfigs("AZURE",
					"azure.approvalemail", "DEFAULT");

			String url = configMap != null
					&& configMap.get("azure.approvalemail.url") != null
							? configMap.get("azure.approvalemail.url")
									.getValue()
							: "https://uatft.eyasp.in/aspwebemail/sap/apprWkflwEmail";

			HttpPost httpPost = new HttpPost(url);
			HttpEntity reqEntity = createReqEntity(isFileRequred, files,
					requestDto);
			httpPost.setHeader("X-TENANT-ID", TenantContext.getTenantId());
			httpPost.setEntity(reqEntity);
			System.out.println("Requesting : " + httpPost.getRequestLine());
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpClient.execute(httpPost, responseHandler);
			System.out.println("responseBody : " + responseBody);
			return responseBody;
		} catch (Exception ex) {
			LOGGER.error("Exception while calling azure email api ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", ewbGson.toJsonTree(ex.getMessage()));
			return ex.getMessage();
		}
	}

	private HttpEntity createReqEntity(boolean isFileReq, File pdfFile,
			ApprWkflwEmailDetailsDto requestDto) {
		try {
			HttpEntity reqEntity = null;
			if (isFileReq) {
				//File file = files;
				//InputStream stream = file.getInputStream();
				reqEntity = MultipartEntityBuilder.create()
						.addTextBody("action", requestDto.getAction())
						.addTextBody("actionEmail", requestDto.getActionEmail())
						.addTextBody("gstin", requestDto.getGstin())
						.addTextBody("taxPeriod", requestDto.getTaxPeriod())
						.addTextBody("retType", requestDto.getRetType())
						.addTextBody("refId", requestDto.getRefId())
						.addTextBody("checkerEmails",
								requestDto.getCheckerEmails())
						.addTextBody("makerEmails", requestDto.getMakerEmails())
						.addTextBody("dueDate", requestDto.getDueDate())
						.addTextBody("appUrl", requestDto.getAppUrl())
						.addTextBody("identifier", requestDto.getIdentifier())
						.addBinaryBody("file", pdfFile,
								ContentType.create("application/octet-stream"),
								pdfFile.getName())
						.build();

			} else {
				reqEntity = MultipartEntityBuilder.create()
						.addTextBody("action", requestDto.getAction())
						.addTextBody("actionEmail", requestDto.getActionEmail())
						.addTextBody("gstin", requestDto.getGstin())
						.addTextBody("taxPeriod", requestDto.getTaxPeriod())
						.addTextBody("retType", requestDto.getRetType())
						.addTextBody("refId", requestDto.getRefId())
						.addTextBody("checkerEmails",
								requestDto.getCheckerEmails())
						.addTextBody("makerEmails", requestDto.getMakerEmails())
						.addTextBody("appUrl", requestDto.getAppUrl())
						.addTextBody("identifier", requestDto.getIdentifier())
						.build();
			}
			return reqEntity;
		} catch (Exception e) {
			LOGGER.error(
					"Exception while creating the Req Entity for azure email api",
					e);
			throw new AppException(e.getMessage());
		}
	}

	/*private static File stream2file(InputStream in, String fileName)
			throws IOException {
		String fileNameWithoutExt = FilenameUtils.getBaseName(fileName);
		final File tempFile = File.createTempFile(fileNameWithoutExt, ".pdf");

		tempFile.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
		}
		LOGGER.debug("File Name {} and File Path {}", tempFile.getName(),
				tempFile.getAbsolutePath());
		return tempFile;
	}*/
}