package com.ey.advisory.blob.storage;

public class BlobStorageApp {

	public static void main(String[] args) {

		try {
			
			System.out.println("execution started");
			
			AzureBlobStorageImpl service = new AzureBlobStorageImpl();
			
			String filename = "json_files_test4.zip";

			String folderPath = "C:/App/json_input_files"; 
			String zipFilePath = "C:/App/zip_output_files/"; 
			String dwnFilePath = "C:/App/blob_storage_dwn_files/";

			// Make zip of json files
			BlobStoreUtility.zipJsonFiles(folderPath, zipFilePath+filename);
            
			// Upload file to Blob
			Storage uploadStorage= new Storage(zipFilePath, filename, null);
			service.write(uploadStorage);
			
			// Download file from Blob
			Storage dwnStorage= new Storage(dwnFilePath, filename, null);
			service.downloadBlob(dwnStorage);
			
			

			System.out.println("execution End");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}