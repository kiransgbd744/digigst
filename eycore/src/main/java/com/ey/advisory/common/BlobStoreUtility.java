package com.ey.advisory.common;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BlobStoreUtility {

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	public File downloadSummaryReport(String fileName, String blobSecretName) throws Exception {

		try {
			Map<String, Config> configMap = configManager.getConfigs("DMS_BLOB", "dms.details", "DEFAULT");
			String accountName = configMap.get("dms.details.blob.accountName").getValue();
			String containerName = configMap.get("dms.details.blob.containerName").getValue();
			String blobStoreKey = KeyVaultUtility.getKey(blobSecretName);

			CloudStorageAccount storageAccount = null;
			CloudBlobClient blobClient = null;
			CloudBlobContainer container = null;
			String storageConnectionString = String.format(
					"DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s",
					accountName, blobStoreKey);

			System.out.println("Connection String " + storageConnectionString);

			storageAccount = CloudStorageAccount.parse(storageConnectionString);

			System.out.println("storageAccount " + storageAccount);

			blobClient = storageAccount.createCloudBlobClient();
			container = blobClient.getContainerReference(containerName);

			File downloadedFile = new File(fileName);
			CloudBlockBlob blob = container.getBlockBlobReference(fileName);
			blob.downloadToFile(downloadedFile.getAbsolutePath());
//			InputStream inputStream = new FileInputStream(file);
			return downloadedFile;

		} catch (Exception e) {
			LOGGER.error("Exception while downloading the file from folder ", e);
			throw new AppException(e);
		}
	}
}
