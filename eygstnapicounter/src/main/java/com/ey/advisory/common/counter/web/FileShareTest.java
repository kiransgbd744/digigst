package com.ey.advisory.common.counter.web;

import java.io.File;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareClientBuilder;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;

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

		String dirName = "TestPostMan";
		String fileName = "TestDoc12.docx";
		/*String sourcefilePath = "C:\\Users\\QD194RK\\Downloads\\Test1.txt";*/
		
		String sourcefilePath = "/eygstnapicounter/src/main/resources/Test1.txt";

		ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
				.clientId(clientId).clientSecret(clientSecret)
				.tenantId(tenantId).build();

		SecretClient secretClient = new SecretClientBuilder()
				.vaultUrl(keyVaultUrl).credential(clientSecretCredential)
				.buildClient();

		KeyVaultSecret secret = secretClient.getSecret(secretName);

		String shareURL = String.format("https://%s.file.core.windows.net",
				ACCOUNT_NAME);
		ShareClient shareClient = new ShareClientBuilder().endpoint(shareURL)
				.sasToken(secret.getValue()).shareName(shareName).buildClient();

		ShareDirectoryClient directoryClient = shareClient
				.getDirectoryClient(dirName);

		if (!directoryClient.exists()) {
			directoryClient = shareClient.createDirectory(dirName);
		}

		ShareFileClient fileClient = new ShareFileClientBuilder()
				.endpoint(shareURL).sasToken(secret.getValue())
				.shareName(shareName).resourcePath(dirName + "/" + fileName)
				.buildFileClient();

		long fileSize = new File(sourcefilePath).length();
		fileClient.create(fileSize);
		fileClient.uploadFromFile(sourcefilePath);

		System.out.println("created");
	}

}
