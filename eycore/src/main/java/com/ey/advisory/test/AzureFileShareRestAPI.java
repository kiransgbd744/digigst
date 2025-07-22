/**
 * 
 */
package com.ey.advisory.test;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AzureFileShareRestAPI {

	// Azure Key Vault details
	private static final String KEY_VAULT_URL = "https://<your-keyvault-name>.vault.azure.net";
	private static final String STORAGE_SECRET_NAME = "storage-account-key";

	// Storage Account details
	private static final String STORAGE_ACCOUNT_NAME = "<your-storage-account-name>";
	private static final String FILE_SHARE_NAME = "<your-file-share-name>";
	private static final String API_VERSION = "2021-12-02"; // Latest API
															// version
	
/*	Storage  Account :  cidvsappocblbstr01.blob.core.windows.net
	Container Name :  sapirpgstinmaster

	  Key Vault Name  :  ci-dv-kvtest01.vault.azure.net
	  Key Vault Secret Identifier :  https://ci-dv-kvtest01.vault.azure.net/secrets/cidvsappocblbstr01-key1/4ebc9d8b60364dd591b5148f4c0bfc3c
*/

	public static void main(String[] args) throws Exception {
		String storageKey = getStorageKeyFromKeyVault();
		listFilesInAzureFileShare(storageKey);
	}

	// Step 1: Retrieve Storage Account Key from Azure Key Vault
	private static String getStorageKeyFromKeyVault() {
		SecretClient secretClient = new SecretClientBuilder()
				.vaultUrl(KEY_VAULT_URL)
				.credential(new DefaultAzureCredentialBuilder().build())
				.buildClient();

		return secretClient.getSecret(STORAGE_SECRET_NAME).getValue();
	}

	// Step 2: Call Azure File Share REST API
	private static void listFilesInAzureFileShare(String storageKey)
			throws Exception {
		String urlStr = String.format(
				"https://%s.file.core.windows.net/%s?restype=directory&comp=list",
				STORAGE_ACCOUNT_NAME, FILE_SHARE_NAME);

		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		// Generate Authorization Header
		String authHeader = generateAuthorizationHeader("GET", urlStr,
				storageKey);
		conn.setRequestProperty("Authorization", authHeader);
		conn.setRequestProperty("x-ms-version", API_VERSION);
		conn.setRequestProperty("x-ms-date",
				java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
						.format(java.time.ZonedDateTime.now()));

		// Read Response
		int responseCode = conn.getResponseCode();
		if (responseCode == 200) {
			System.out
					.println("Successfully listed files in Azure File Share.");
		} else {
			System.err.println(
					"Failed to list files. Response Code: " + responseCode);
		}
	}

	// Step 3: Generate Azure Storage Authorization Header
	private static String generateAuthorizationHeader(String method, String url,
			String storageKey) throws Exception {
		String stringToSign = method + "\n\n\n\n\n\n\n\n\n\n\n\n" + "x-ms-date:"
				+ java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME
						.format(java.time.ZonedDateTime.now())
				+ "\nx-ms-version:" + API_VERSION + "\n" + "/"
				+ STORAGE_ACCOUNT_NAME + "/" + FILE_SHARE_NAME;

		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(
				Base64.getDecoder().decode(storageKey), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		String signature = Base64.getEncoder().encodeToString(sha256_HMAC
				.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8)));

		return "SharedKey " + STORAGE_ACCOUNT_NAME + ":" + signature;
	}
}
