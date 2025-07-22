package com.ey.advisory.blob.storage;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;


public class BlobStoreUtility {

	private static final String ACCOUNT_NAME = "ciqadmsblbstr01";
	private static final String CONTAINER_NAME = "sapcloudfoundary";

	public static File downloadBlob(String fileName) throws Exception {
		try {
			File file = new File(fileName);
			CloudBlockBlob blob = getCloudBlockBlob(file.getName());
			blob.downloadToFile(fileName); // file.getAbsolutePath()
			// InputStream inputStream = new FileInputStream(file);
			return file;

		} catch (Exception e) {
			// LOGGER.error("Exception while downloading the file from folder ",
			// e);
			throw e;
		}
	}

	public static File uploadBlob(String fileName) throws Exception {

		try {
			// File file = new File(Files.createTempDirectory("TempDirectory")
			// .toFile().getAbsolutePath() + File.separator + fileName);
			File file = new File(fileName);
			CloudBlockBlob blob = getCloudBlockBlob(file.getName());
			blob.upload(new FileInputStream(file), file.length());
			return file;

		} catch (Exception e) {
			// LOGGER.error("Exception while downloading the file from folder ",
			// e);
			throw e;
		}
	}

	public static CloudBlockBlob getCloudBlockBlob(String filename) {

		// String blobStoreKey = KeyVaultUtility.getKey(secretName);
		String sasToken = "sp=racwl&st=2025-02-06T03:22:02Z&se=2027-12-31T11:22:02Z&spr=https&sv=2022-11-02&sr=c&sig=EA9n%2Fyt1ItgFInnUrbgpCJvuqwsrs92Iv4hPJUp1vv0%3D"; // Replace

		CloudStorageAccount storageAccount = null;
		CloudBlobClient blobClient = null;
		CloudBlobContainer container = null;
		CloudBlockBlob blob = null;

		try {
			String storageConnectionString = String.format(
					"BlobEndpoint=https://%s.blob.core.windows.net/;SharedAccessSignature=%s",
					ACCOUNT_NAME, sasToken);

			BlobServiceClient serviceClient = new BlobServiceClientBuilder()
					.connectionString(storageConnectionString).buildClient();

			BlobContainerClient containerClient = serviceClient
					.getBlobContainerClient(CONTAINER_NAME);

			BlobClient blob1 = containerClient.getBlobClient("fileName.txt");

			System.out.println("blob1 " + blob1);

			// blob1.upload(data, length);

			System.out.println("Connection String " + storageConnectionString);

			storageAccount = CloudStorageAccount.parse(storageConnectionString);

			System.out.println("storageAccount " + storageAccount);

			blobClient = storageAccount.createCloudBlobClient();

			container = blobClient.getContainerReference(CONTAINER_NAME);

			blob = container.getBlockBlobReference(filename);

		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return blob;

	}

	public static BlobContainerClient getBlobContainerClient() {

		// String blobStoreKey = KeyVaultUtility.getKey(secretName);
		String sasToken = "sp=racwl&st=2025-02-06T03:22:02Z&se=2027-12-31T11:22:02Z&spr=https&sv=2022-11-02&sr=c&sig=EA9n%2Fyt1ItgFInnUrbgpCJvuqwsrs92Iv4hPJUp1vv0%3D"; // Replace

		BlobServiceClient serviceClient = null;
		BlobContainerClient container = null;

		String storageConnectionString = String.format(
				"BlobEndpoint=https://%s.blob.core.windows.net/;SharedAccessSignature=%s",
				ACCOUNT_NAME, sasToken);

		try {
			serviceClient = new BlobServiceClientBuilder()
					.connectionString(storageConnectionString).buildClient();

			container = serviceClient.getBlobContainerClient(CONTAINER_NAME);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return container;

	}

	public static void zipJsonFiles(String folderPath, String zipFilePath)
			throws IOException {
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			throw new FileNotFoundException("Folder not found: " + folderPath);
		}

		// Get list of JSON files (only 3 files)
		File[] jsonFiles = folder
				.listFiles((dir, name) -> name.endsWith(".json"));
		if (jsonFiles == null || jsonFiles.length < 3) {
			throw new IOException(
					"Less than 3 JSON files found in the folder.");
		}

		// Create ZIP output stream
		try (FileOutputStream fos = new FileOutputStream(zipFilePath);
				ZipOutputStream zos = new ZipOutputStream(fos)) {

			for (int i = 0; i < 3; i++) { // Process only 3 JSON files
				File jsonFile = jsonFiles[i];
				addFileToZip(jsonFile, zos);
			}
		}
	}

	private static void addFileToZip(File file, ZipOutputStream zos)
			throws IOException {
		try (FileInputStream fis = new FileInputStream(file)) {
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}

			zos.closeEntry();
		}
	}

	// Helper method to add files to ZIP
	public static void addToZip(InputStream inputStream, String fileName,
			ZipOutputStream zos) throws IOException {
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			zos.write(buffer, 0, length);
		}

		zos.closeEntry();
		inputStream.close();
	}

	// Unused method
	public static void readFromClassPath() {
		System.out.println("Hello");

		String file1 = "zip_sample1.json";
		String file2 = "zip_sample2.json";

		try {
			// Get class loader to read files from resources folder
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();

			// Read files as InputStreams
			InputStream inputStream1 = classLoader.getResourceAsStream(file1);
			InputStream inputStream2 = classLoader.getResourceAsStream(file2);

			if (inputStream1 == null || inputStream2 == null) {
				throw new FileNotFoundException(
						"One or both resource files not found");
			}

			// Create a temporary directory
			Path tempDir = Files.createTempDirectory("zip_temp_dir");
			System.out.println(
					"Temporary directory created: " + tempDir.toAbsolutePath());

			// Define the ZIP file path inside the temp directory
			Path zipFilePath = tempDir.resolve("output.zip");

			// Create ZIP output stream
			try (FileOutputStream fos = new FileOutputStream(
					zipFilePath.toFile());
					ZipOutputStream zos = new ZipOutputStream(fos)) {

				// Add file1 to ZIP
				addToZip(inputStream1, file1, zos);
				// Add file2 to ZIP
				addToZip(inputStream2, file2, zos);

				System.out.println(
						"ZIP file created successfully: " + zipFilePath);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Unused method
	public static void listBlobContainer() {

		// private static final String ACCOUNT_NAME = "cidvsappocapp01";
		final String BLOB_STORE_KEY = "X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2";

		final String ACCOUNT_NAME = "ciqadmsblbstr01";
		final String CONTAINER_NAME = "sapcloudfoundary";

		// private static final String STORAGE_CONNECTION_STRING =
		//
		// "DefaultEndpointsProtocol=https;AccountName="+"your_account_name;AccountKey=your_account_key";

		final String sasToken = "sp=racwl&st=2025-02-06T03:22:02Z&se=2027-12-31T11:22:02Z&spr=https&sv=2022-11-02&sr=c&sig=EA9n%2Fyt1ItgFInnUrbgpCJvuqwsrs92Iv4hPJUp1vv0%3D"; // Replace
																																												// with
																																												// a
																																												// valid
																																												// SAS
																																												// token

		try {
			System.out.println("AzureBlobUtilityDemo ...");
			String STORAGE_CONNECTION_STRING = String.format(
					"BlobEndpoint=https://%s.blob.core.windows.net/;SharedAccessSignature=%s",
					ACCOUNT_NAME, sasToken);

			// Connect to Azure Storage Account

			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(STORAGE_CONNECTION_STRING);

			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			// List all containers

			for (CloudBlobContainer container : blobClient.listContainers()) {

				System.out.println("Container Name: " + container.getName());

			}

			System.out.println("AzureBlobUtilityDemo .... finish");

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public static void main(String[] args) {
		// Folder containing JSON files
		String folderPath = "C:/App/json_input_files"; // Change to your JSON
														// folder path
		String zipFilePath = "C:/App/zip_output_files/json_files.zip"; // Change
																		// to
																		// your
																		// desired
																		// ZIP
																		// path

		try {
			zipJsonFiles(folderPath, zipFilePath);
			System.out.println("ZIP file created successfully: " + zipFilePath);
		} catch (IOException e) {
			System.err.println(
					"Error while creating ZIP file: " + e.getMessage());
		}
	}

}