/*package com.ey.advisory.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.ey.advisory.common.AppException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.azure.storage.AccessCondition;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import lombok.extern.slf4j.Slf4j;

*//**
 * @author Siva.Reddy
 *
 *//*
@Slf4j
@RestController
public class BlobDownloadController {

	@PostMapping(value = "/ui/downloadFileFromBlob")
	public void downloadSummaryReport(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		try {
			InputStream inputStream = null;

			JsonObject reqJson = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			String fileName = reqJson.get("fileName").getAsString();
			String clientId = reqJson.get("clientId").getAsString();
			String clientSecret = reqJson.get("clientSecret").getAsString();
			String tenantId = reqJson.get("tenantId").getAsString();
			String keyVaultUrl = reqJson.get("keyVaultUrl").getAsString();

			String secretName = reqJson.get("secretName").getAsString();

			String accountName = reqJson.get("accountName").getAsString();

			String containerName = reqJson.get("containerName").getAsString();

			inputStream = downloadSummaryReport(keyVaultUrl, clientId,
					clientSecret, tenantId, secretName, accountName,
					containerName, fileName);
			byte[] bytes = new byte[1024];
			int read = 0;
			if (inputStream != null) {
				response.setContentType(
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				response.setHeader("Content-Encoding", "UTF-8");
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while downloading summary report", ex);
		}

		LOGGER.debug("End of download summary file method");
	}

	@PostMapping(value = "/ui/downloadFileFromBlobWithProxy")
	public void downloadFileFromBlobWithProxy(@RequestBody String jsonString,
			HttpServletResponse response) throws Exception {

		try {
			InputStream inputStream = null;

			JsonObject reqJson = JsonParser.parseString(jsonString)
					.getAsJsonObject();
			String fileName = reqJson.get("fileName").getAsString();
			String clientId = reqJson.get("clientId").getAsString();
			String clientSecret = reqJson.get("clientSecret").getAsString();
			String tenantId = reqJson.get("tenantId").getAsString();
			String keyVaultUrl = reqJson.get("keyVaultUrl").getAsString();

			String secretName = reqJson.get("secretName").getAsString();

			String accountName = reqJson.get("accountName").getAsString();

			String containerName = reqJson.get("containerName").getAsString();

			inputStream = downloadSummaryReport(keyVaultUrl, clientId,
					clientSecret, tenantId, secretName, accountName,
					containerName, fileName);
			byte[] bytes = new byte[1024];
			int read = 0;
			if (inputStream != null) {
				response.setContentType(
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition",
						String.format("attachment; filename=" + fileName));
				response.setHeader("Content-Encoding", "UTF-8");
				OutputStream outputStream = response.getOutputStream();
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while downloading the file from folder ", ex);
			throw new AppException(ex);
		}

		LOGGER.debug("End of download summary file method");
	}

	public InputStream downloadSummaryReport(String keyVaultUrl,
			String clientId, String clientSecret, String tenantId,
			String secretName, String accountName, String containerName,
			String fileName) throws Exception {
		try {

			// Create a ClientSecretCredential for authentication
			ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
					.clientId(clientId).clientSecret(clientSecret)
					.tenantId(tenantId).build();

			// Create a SecretClient to interact with the Key Vault
			SecretClient secretClient = new SecretClientBuilder()
					.vaultUrl(keyVaultUrl).credential(clientSecretCredential)
					.buildClient();

			// Retrieve a secret by name from the Key Vault
			KeyVaultSecret secret = secretClient.getSecret(secretName);

			CloudStorageAccount storageAccount = null;
			CloudBlobClient blobClient = null;
			CloudBlobContainer container = null;
			String storageConnectionString = String.format(
					"DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s",
					accountName, secret.getValue());

			System.out.println("Connection String " + storageConnectionString);

			storageAccount = CloudStorageAccount.parse(storageConnectionString);

			System.out.println("storageAccount " + storageAccount);

			blobClient = storageAccount.createCloudBlobClient();
			container = blobClient.getContainerReference(containerName);
//			OperationContext ctx = getOperationContext(true);
//			boolean isContainerAvailable = container.exists(
//					AccessCondition.generateIfExistsCondition(),
//					new BlobRequestOptions(), ctx);
//
//			if (!isContainerAvailable) {
//				if (LOGGER.isDebugEnabled()) {
//					String logMsg = String.format(
//							"Container didn't already exist "
//									+ "Expected Container Name: '%s' ",
//							containerName);
//					LOGGER.debug(logMsg);
//					throw new Exception(logMsg);
//				}
//			} else {
//				if (LOGGER.isDebugEnabled()) {
//					String logMsg = String.format(
//							"Container  already exist, Container Name: '%s' ",
//							containerName);
//					LOGGER.debug(logMsg);
//				}
//			}
			File file = new File(Files.createTempDirectory("TempDirectory")
					.toFile().getAbsolutePath() + File.separator + fileName);

			CloudBlockBlob blob = container
					.getBlockBlobReference("IRPoutput_batch_10.csv");
			blob.downloadToFile(file.getAbsolutePath());
			InputStream inputStream = new FileInputStream(file);
			return inputStream;

		} catch (Exception e) {
			LOGGER.error("Exception while downloading the file from folder ", e);
			throw new AppException(e);
		}
	}

	private static OperationContext getOperationContext(
			boolean isProxyRequired) {
		OperationContext ctx = new OperationContext();
		if (isProxyRequired) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					"r2inblrubci13pxy01.mea.ey.net", 8080));
			OperationContext.setDefaultProxy(proxy);
		}
		return ctx;
	}
}
*/