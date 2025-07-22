package com.ey.advisory.core.springconfig;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * Loads the trust store from the classpath
 * 
 * @author Sai.Pakanati
 *
 */
@Profile("hanadev")
@Component("TrustStoreLoader")
@Slf4j
public class LocalTrustStoreLoader implements TrustStoreLoader {
	
	private static final String TRUST_STORE_JKS_FILE = "cacertswp.jks";
	private static final String TRUST_STORE_TYPE = "JKS";
	private static final String TRUST_STORE_PASSWORD = "sapdigigst";
	
	@Override
	public KeyStore loadTrustStore() {			
		KeyStore trustStore = null;
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format("Loading the trust store from the "
					+ "file '%s' in the classpath", TRUST_STORE_JKS_FILE));
		}
		try(InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream(TRUST_STORE_JKS_FILE)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Opened the input stream to "
						+ "load the local trust store");
			}
 			trustStore = KeyStore.getInstance(TRUST_STORE_TYPE);
			char[] password = TRUST_STORE_PASSWORD.toCharArray();
			trustStore.load(stream, password);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(String.format("Loaded the local trust store from "
						+ "the classpath file: '%s'", TRUST_STORE_JKS_FILE));
			}
			return trustStore;
		} catch(KeyStoreException | CertificateException | 
					NoSuchAlgorithmException | IOException ex) {
			String msg = String.format("Failed to load the local keystore "
					+ "from the classpath file '%s'", TRUST_STORE_JKS_FILE);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

}
