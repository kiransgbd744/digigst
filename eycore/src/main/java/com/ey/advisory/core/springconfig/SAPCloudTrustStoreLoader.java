package com.ey.advisory.core.springconfig;

import java.security.KeyStore;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.sap.cloud.crypto.keystore.api.KeyStoreService;
import com.sap.cloud.crypto.keystore.api.KeyStoreServiceException;

import lombok.extern.slf4j.Slf4j;

@Profile("!hanadev")
@Component("TrustStoreLoader")
@Slf4j
public class SAPCloudTrustStoreLoader implements TrustStoreLoader {

	private static final String TRUST_STORE_JKS_FILE = "cacertswp";	
	private static final String TRUST_STORE_PASSWORD = "sapdigigst";
	
	@Override
	public KeyStore loadTrustStore() {
		try {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(String.format("About to load the trust store "
						+ "named '%s' using SAP KeyStoreService", 
						TRUST_STORE_JKS_FILE));
			}
			Context context = new InitialContext();
			KeyStoreService keystoreService = (KeyStoreService) context
					.lookup("java:comp/env/KeyStoreService");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Looked up the KeyStoreService "
						+ "from Java Naming Context");
			}
			char[] pswd = TRUST_STORE_PASSWORD.toCharArray();
			KeyStore trustStore =  
					keystoreService.getKeyStore(TRUST_STORE_JKS_FILE, pswd);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(String.format("Loaded the trust store named "
						+ "'%s' using SAP KeyStoreService", 
						TRUST_STORE_JKS_FILE));
			}
			return trustStore;
		} catch (NamingException ex) {
			String msg = "Error while looking up KeyStoreService";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		} catch (KeyStoreServiceException ex) {
			String msg = String.format("Error while loading the Trust Store "
					+ "named '%s' using SAP KeyStoreService API", 
					TRUST_STORE_JKS_FILE);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

}
