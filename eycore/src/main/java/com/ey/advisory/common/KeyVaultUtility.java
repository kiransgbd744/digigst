package com.ey.advisory.common;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyVaultUtility {

	private static final String CLIENT_SECRET = "X6U8Q~Z1ELY0JPOFTpIrQ9gw6.wKsy3b95GTHct2";
	private static final String CLIENT_ID = "47b538a9-26dd-4093-a49f-2f7618f70071";
	private static final String TENANT_ID = "33b98860-21ef-4870-9beb-712f0cdac7b9";
	private static final String KEY_VAULT_URL = "https://ci-dv-kvtest01.vault.azure.net";

	public static String getKey(String secretName) throws Exception {

		try {
			// Azure authentication
			ClientSecretCredential credential = new ClientSecretCredentialBuilder()
					.clientId(CLIENT_ID).clientSecret(CLIENT_SECRET)
					.tenantId(TENANT_ID).build();

			// Fetch SAS token from Azure Key Vault
			SecretClient secretClient = new SecretClientBuilder()
					.vaultUrl(KEY_VAULT_URL).credential(credential)
					.buildClient();

			KeyVaultSecret secret = secretClient.getSecret(secretName);
			return secret.getValue();

		} catch (Exception e) {
			LOGGER.error("Exception while downloading the file from folder ",
					e);
			throw new AppException(e);
		} 
		
	}
	
	/*
	 * public static void main(String[] args) throws Exception { String
	 * secret="ci-dv-dms-sb01-sapcloudfoundary-PrimaryString"; String key =
	 * getKey(secret); System.out.println("Key: "+ key); }
	 */
}
