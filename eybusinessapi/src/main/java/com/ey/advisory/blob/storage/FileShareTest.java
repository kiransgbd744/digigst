package com.ey.advisory.blob.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareClientBuilder;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;

public class FileShareTest {
	 
	/*
	 * Application (client) ID :47b538a9-26dd-4093-a49f-2f7618f70071 Directory
	 * (tenant) ID:33b98860-21ef-4870-9beb-712f0cdac7b9 Secret value :
	 * X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2
	 */
 
	private static final String clientSecret = "X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2";
	private static final String clientId = "47b538a9-26dd-4093-a49f-2f7618f70071";
	private static final String tenantId = "33b98860-21ef-4870-9beb-712f0cdac7b9";
	// private static final String keyVaultUrl =
	// "https://ci-dv-kvtest01.vault.azure.net/secrets/ciqadmsblbstr01-sas-123128/503b8d3e6c44405b8eed0babb5f98623";
//	private static final String vaultName = "ci-kv-infravault.vault.azure.net";
	private static final String secretName = "ciqadmsblbstr01-sas-123128";
	private static final String shareName = "qadmsfs";
	private static final String keyVaultUrl = "https://ci-dv-kvtest01.vault.azure.net";
 
	private static final String ACCOUNT_NAME = "ciqadmsblbstr01";
 
	public static void main(String[] args) {
 
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
 
		String shareURL = String.format("https://%s.file.core.windows.net",
				ACCOUNT_NAME);
		ShareClient shareClient = new ShareClientBuilder().endpoint(shareURL)
				.sasToken(secret.getValue()).shareName(shareName).buildClient();
		
		System.out.println("step 1");
 
		String dirName = "testdir1";
		shareClient.createDirectory(dirName);
		
		System.out.println("step 1.1");
		
		// Create directory if it doesn't exist
        ShareDirectoryClient directoryClient = shareClient.getDirectoryClient(dirName);
//        if (!directoryClient.exists()) {
//            directoryClient.create();
//        }
        System.out.println("step 2");
//        String filePath = "C:/Users/YourName/Documents/sample.txt";
//        String fileName = "sample.txt";
        
        String filename="json_files_test.zip";
		
		String folderPath = "C:/App/json_input_files";  // Change to your JSON folder path
        String zipFilePath = "C:/App/zip_output_files/"+filename;  // Change to your desired ZIP path
        
        String dwnFilePath = "C:/App/blob_storage_dwn_files/"+filename;
        System.out.println("step 3");
        // Upload file
        ShareFileClient fileClient = directoryClient.getFileClient(filename);
        File file = new File(zipFilePath);
        try {
            byte[] fileData = Files.readAllBytes(file.toPath());
            fileClient.create(fileData.length);
            fileClient.upload(new FileInputStream(file), fileData.length);
            System.out.println("File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("step 4");
        
     // Download file
        try (FileOutputStream outputStream = new FileOutputStream(dwnFilePath)) {
            long fileSize = fileClient.getProperties().getContentLength();
            byte[] buffer = new byte[(int) fileSize];
            fileClient.download(outputStream);
            System.out.println("File downloaded successfully to " + dwnFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("step 5");
		
		System.out.println("program end");
		
		
	}
	
}