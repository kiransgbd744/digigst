package com.ey.advisory.gstnapi;

import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

/**
 * 
 * Load the GSTN certificate and keep it in memory at startup.
 * 
 * @author Sai.Pakanati
 *
 */
@Component
public class GSTNCertificate {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GSTNCertificate.class);
	
	private static final String CERT_PATH = 
			"certificates/GSTN_G2A_SANDBOX_UAT_public.cer";
	
	/**
	 * The variable that stores the GSTN certificate content
	 */
	private PublicKey gstnPubKey;
	
	@PostConstruct
	public void loadCertificate() {
		
		if (LOGGER.isInfoEnabled()) {
			String msg = String.format("Loading GSTN certificate "
					+ "from the path '%s'", CERT_PATH);
			LOGGER.info(msg);
		}
		try(InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(CERT_PATH)) {	
			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) 
					cf.generateCertificate(is);
			gstnPubKey = certificate.getPublicKey();
			
			if (LOGGER.isInfoEnabled()) {
				String msg = String.format("Loaded GSTN public key from the "
						+ "path: '%s'", CERT_PATH);
				LOGGER.info(msg);
			}
			
		} catch(Exception ex) {
			String msg = String.format("Error while Loading GSTN "
					+ "Certificate from '%s'", CERT_PATH);
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}
	
	public PublicKey getGSTNCertificate() {
		return gstnPubKey;
	}
}
