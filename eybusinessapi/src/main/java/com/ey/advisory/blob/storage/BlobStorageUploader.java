package com.ey.advisory.blob.storage;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobOutputStream;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;

public class BlobStorageUploader {
	
//	private static final String ACCOUNT_NAME = "cidvsappocapp01";
//	private static final String BLOB_STORE_KEY = "X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2";
//	private static final String CONTAINER_NAME = "sapirpgstinmaster";
	
	
	
	//Key Vault Name  
	private static final String keyVaultUrl="https://ci-dv-kvtest01.vault.azure.net/secrets/cidvsappocblbstr01-key1/4ebc9d8b60364dd591b5148f4c0bfc3c";
	//Application (client) ID
	private static final String clientId="47b538a9-26dd-4093-a49f-2f7618f70071";
	//Secret value
	private static final String clientSecret="X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2";
	//Directory (tenant) ID
	private static final String tenantId="33b98860-21ef-4870-9beb-712f0cdac7b9";
	//Key Vault Secret Identifier 
	private static final String secretName="ci-dv-kvtest01.vault.azure.net";
	//Storage  Account 
	private static final String accountName="cidvsappocblbstr01.blob.core.windows.net"; 
//	private static final String accountName="cidvsappocblbstr01"; 
	//Container Name 
	private static final String containerName="sapirpgstinmaster";
	private static final String fileName="";

	
    
	private static final String CONNECTION_STRING = "DefaultEndpointsProtocol=https;"
                           +"AccountName=cidvsappocapp01;"
			               +"AccountKey=X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2;"
                           +"EndpointSuffix=core.windows.net";
	
	private static final String storageConnectionString = String.format(
			"BlobEndpoint=https://%s.blob.core.windows.net/;SharedAccessSignature=%s", accountName,
			clientSecret);
	
	

    public static void main(String[] args) {
//        String zipFilePath = "C:/path/to/your/file.zip";  // Change this to your ZIP file path
//        String blobFileName = "uploaded-file.zip";  // Name to store in Blob Storage
//        uploadZipToBlob(zipFilePath, blobFileName);
        
        
        String folderPath = "C:/App/json_input_files";  // Change to your JSON folder path
        String zipFilePath = "C:/App/zip_output_files/json_files.zip";  // Change to your desired ZIP path
        

        
        
        try {
//        	upload(zipFilePath,zipFilePath);
			downloadSummaryReport(keyVaultUrl, clientId, clientSecret, tenantId, 
					secretName, accountName, containerName, zipFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * @param localFile   path of file, that resides in your local machine
     * @param storeFileAs path where you want to upload the blob
     * @throws Exception
     */
    public static void upload(String filePath, String storeFileAs) throws Exception {
    	
    	
    	try
    	{
    		System.out.println("Execution started..");
    		String ACCOUNT_NAME = "ciqadmsblbstr01";
    		String sasToken = "sp=racwl&st=2025-02-06T03:22:02Z&se=2027-12-31T11:22:02Z&spr=https&sv=2022-11-02&sr=c&sig=EA9n%2Fyt1ItgFInnUrbgpCJvuqwsrs92Iv4hPJUp1vv0%3D"; 
    		// Replace with a valid SAS token
//    		String storageConnectionString = String.format(
//	                "BlobEndpoint=https://%s.blob.core.windows.net/;SharedAccessSignature=%s",
//	                ACCOUNT_NAME, sasToken);
    		
//    		String clientId="47b538a9-26dd-4093-a49f-2f7618f70071";
//    		String clientSecret="X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2";
//    		String tenantId="33b98860-21ef-4870-9beb-712f0cdac7b9";
//    		
//    		// Create a ClientSecretCredential for authentication
//			ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
//					.clientId(clientId).clientSecret(clientSecret)
//					.tenantId(tenantId).build();
//
//			// Create a SecretClient to interact with the Key Vault
//			SecretClient secretClient = new SecretClientBuilder()
//					.vaultUrl(keyVaultUrl).credential(clientSecretCredential)
//					.buildClient();
//
//			// Retrieve a secret by name from the Key Vault
//			KeyVaultSecret secret = secretClient.getSecret(secretName);
    					
    	    // Retrieve storage account from connection-string.
    		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
    		
    		System.out.println("storageAccount " + storageAccount);

    		// Create the blob client.
    	   CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
    	   
    	   System.out.println("blobClient " + blobClient);

    	   // Get a reference to a container.
    	   // The container name must be lower case
    	   CloudBlobContainer container = blobClient.getContainerReference(containerName);
    	   
    	   System.out.println("container " + container);
    	   
//    	   System.out.println("Creating container start ");
    	   
//    	   // Create the container if it does not exist.
//    		container.createIfNotExists();
//    		
//    		System.out.println("Creating container end ");
//    		
//    		// Create a permissions object.
//        	BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
//
//        	// Include public access in the permissions object.
//        	containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
//
//        	// Set the permissions on the container.
//        	container.uploadPermissions(containerPermissions);
        	
        	
        	/*
			 * File file = new File(Files.createTempDirectory("tempDirectory")
			 * .toFile().getAbsolutePath() + File.separator + fileName);
			 */
        	// Define the path to a local file. filePath = "C:\\myimages\\myimage.jpg";
        	File source = new File(filePath);
			CloudBlockBlob blob = container.getBlockBlobReference(source.getName());
			if (storeFileAs.endsWith(".zip")) {
	            blob.getProperties().setContentType("application/zip");
	        }
			blob.upload(new FileInputStream(source), source.length());
			
//	        BlobOutputStream blobOutputStream = blob.openOutputStream();
//	        InputStream input = new FileInputStream(source.getPath());
//
//	        int next = -1;
//	        while ((next = input.read()) != -1) {
//	            blobOutputStream.write(next);
//	        }
//	        blobOutputStream.close();
//	        input.close();
			
			System.out.println("Execution ended");
    		
    	}
    	catch (Exception e)
    	{
    	    // Output the stack trace.
    	    e.printStackTrace();
    	}
    	
    	
    	

       
    }
    
    
   
    
    
    public static InputStream downloadSummaryReport(String keyVaultUrl,
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
//			File file = new File(Files.createTempDirectory("TempDirectory")
//					.toFile().getAbsolutePath() + File.separator + fileName);
//
//			CloudBlockBlob blob = container
//					.getBlockBlobReference("IRPoutput_batch_10.csv");
//			blob.downloadToFile(file.getAbsolutePath());
			File file = new File(fileName);
			InputStream inputStream = new FileInputStream(file);
			return inputStream;

		} catch (Exception e) {
			System.out.println("Exception while downloading the file from folder ");
			throw e;
		}
	}
    
    
    
    
    
    
    public static void uploadZipToBlob(String filePath, String blobName) {
        try {
        	
        	
        	
            // Create a BlobServiceClient
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(CONNECTION_STRING)
                    .buildClient();
            
          

            // Get the container client
           // BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);

            // Get the blob client
           // BlobClient blobClient = containerClient.getBlobClient(blobName);

            // Read the ZIP file
//            File file = new File(filePath);
//            try (InputStream fileInputStream = new FileInputStream(file)) {
//                // Upload the file to Blob Storage
//                blobClient.upload(fileInputStream, file.length(), true);
//                System.out.println("Upload successful: " + blobName);
//            }
        } catch (Exception e) {
            System.err.println("Error uploading ZIP file: " + e.getMessage());
        }
    }
    
    public void deleteFile(String path) throws Exception {
//        CloudBlob cloudBlob = rootContainer.getBlockBlobReference(path);
//        cloudBlob.delete();
    }
}