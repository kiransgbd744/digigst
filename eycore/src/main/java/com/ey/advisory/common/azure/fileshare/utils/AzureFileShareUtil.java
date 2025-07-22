/**
* 
*/
package com.ey.advisory.common.azure.fileshare.utils;
 
import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareClientBuilder;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.KeyVaultUtility;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;
 
/**
* @author ashutosh.kar
*
*/
 
@Slf4j
@Component("AzureFileShareUtil")
public class AzureFileShareUtil {
 
	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
 
 
	public String uploadFileToAzure(File file, String fileName,
			String dirName) {

		try {
			Map<String, Config> configMap = configManager.getConfigs("DMS_AZURE_FILESHARE", "dms.details", "DEFAULT");

			String shareName = configMap.get("dms.details.azureFile.ShareName").getValue();
			String accountName = configMap.get("dms.details.azureFile.accountName").getValue();
			String secretName = configMap.get("dms.details.azureFile.keyVaultSecretName").getValue();
			
			String secretKey=KeyVaultUtility.getKey(secretName);

			String shareURL = String.format("https://%s.file.core.windows.net", accountName);
			
			ShareClient shareClient = new ShareClientBuilder()
					.endpoint(shareURL)
					.sasToken(secretKey)
					.shareName(shareName)
					.buildClient();
			
			LOGGER.error("shareClient" + shareClient);
			if (!shareClient.exists()) {
				LOGGER.error("Share does not exist: " + shareName);
				throw new AppException();
			}
			
			String baseDir = "ETL";
		    String subDir = "Queue_files";
		    String uuidDir = dirName;

		    ShareDirectoryClient baseDirectoryClient = shareClient.getDirectoryClient(baseDir);
		    if (!baseDirectoryClient.exists()) {
		        baseDirectoryClient.create();
		        LOGGER.error("Created base directory: {}", baseDir);
		    }

		    ShareDirectoryClient subDirectoryClient = baseDirectoryClient.getSubdirectoryClient(subDir);
		    if (!subDirectoryClient.exists()) {
		        subDirectoryClient.create();
		        LOGGER.error("Created sub-directory: {}", subDir);
		    }

		    // Get UUID folder reference & ensure it's created properly
		    ShareDirectoryClient uuidDirectoryClient = subDirectoryClient.getSubdirectoryClient(uuidDir);
		    if (!uuidDirectoryClient.exists()) {
		        uuidDirectoryClient.create();
		        LOGGER.error("Created UUID directory: {}", uuidDir);
		    } else {
		        LOGGER.error("UUID directory '{}' already exists.", uuidDir);
		    }

		    ShareFileClient fileClient = uuidDirectoryClient.getFileClient(fileName);

			LOGGER.error("Uploading file '{}' to Azure File Share directory '{}/{}/{}'", fileClient,fileName, baseDir, subDir, uuidDir);

			// Upload File
			long fileSize = file.length();
			if (!fileClient.exists()) {
                fileClient.create(fileSize);
                LOGGER.error("Created file '{}' in directory '{}'", fileName, uuidDir);
            } else {
            	LOGGER.error("File '{}' already exists, overwriting...", fileName);
            }
			fileClient.create(fileSize);

			fileClient.uploadFromFile(file.getAbsolutePath());
			// fileClient.downloadToFile("C:\\Users\\QD194RK\\Downloads\\Test21Azure.xlsx");

			LOGGER.error("File uploaded successfully: {} in {}", fileName, dirName);
			return "Success: File uploaded to Azure File Share.";

		} catch (Exception e) {
			LOGGER.error("Error uploading file to Azure File Share: {}", e.getMessage(), e);
			throw new AppException("Error: File upload failed.{} ", e);

		}
	}
 
	
	
	/*
	 * public static void main(String[] args) throws java.io.IOException { File file
	 * = new File("C:\\Downloads\\Shashikant121.xlsx"); String response = null; try
	 * { AzureFileShareUtil util = new AzureFileShareUtil(); response =
	 * util.uploadFileToAzure(file, file.getName(), UUID.randomUUID().toString()); }
	 * catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } System.out.println(response);
	 * 
	 * }
	 */
	 
	 
}