//package com.ey.advisory.test;
//
//import com.azure.identity.ClientSecretCredential;
//import com.azure.identity.ClientSecretCredentialBuilder;
//import com.azure.security.keyvault.secrets.SecretClient;
//import com.azure.security.keyvault.secrets.SecretClientBuilder;
//import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
//import com.azure.storage.file.share.ShareClient;
//import com.azure.storage.file.share.ShareClientBuilder;
//
//public class FileShareTest {
//
//	/*
//	 * Application (client) ID :47b538a9-26dd-4093-a49f-2f7618f70071 Directory
//	 * (tenant) ID:33b98860-21ef-4870-9beb-712f0cdac7b9 Secret value :
//	 * X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2
//	 */
//
//	private static final String clientSecret = "X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2";
//	private static final String clientId = "47b538a9-26dd-4093-a49f-2f7618f70071";
//	private static final String tenantId = "33b98860-21ef-4870-9beb-712f0cdac7b9";
//	// private static final String keyVaultUrl =
//	// "https://ci-dv-kvtest01.vault.azure.net/secrets/ciqadmsblbstr01-sas-123128/503b8d3e6c44405b8eed0babb5f98623";
////	private static final String vaultName = "ci-kv-infravault.vault.azure.net";
//	private static final String secretName = "ciqadmsblbstr01-sas-123128";
//	private static final String shareName = "qadmsfs";
//	private static final String keyVaultUrl = "https://ci-dv-kvtest01.vault.azure.net";
//
//	private static final String ACCOUNT_NAME = "ciqadmsblbstr01";
//
//	public static void main(String[] args) {
//
//		// Create a ClientSecretCredential for authentication
//		ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
//				.clientId(clientId).clientSecret(clientSecret)
//				.tenantId(tenantId).build();
//
//		// Create a SecretClient to interact with the Key Vault
//		SecretClient secretClient = new SecretClientBuilder()
//				.vaultUrl(keyVaultUrl).credential(clientSecretCredential)
//				.buildClient();
//
//		// Retrieve a secret by name from the Key Vault
//		KeyVaultSecret secret = secretClient.getSecret(secretName);
//
//		String shareURL = String.format("https://%s.file.core.windows.net",
//				ACCOUNT_NAME);
//		ShareClient shareClient = new ShareClientBuilder().endpoint(shareURL)
//				.sasToken(secret.getValue()).shareName(shareName).buildClient();
//
//		String dirName = "testdir";
//		shareClient.createDirectory(dirName);
//	}
//
//}
