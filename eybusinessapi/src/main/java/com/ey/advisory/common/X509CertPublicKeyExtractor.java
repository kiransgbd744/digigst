package com.ey.advisory.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class X509CertPublicKeyExtractor {
	
	/**
	 * Extract the public key as a base 64 encoded string from the base 64 
	 * encoded X.509 certificate data.
	 * 
	 * @param certData the certificate data as a base 64 encoded string.
	 * 
	 * @return the public key as a base 64 encoded string.
	 */
	public static PublicKey extractPublicKey(String certData) {
		
		try {
	        byte[] encodedCert = certData.getBytes("UTF-8");	        
	        byte[] decodedCert = Base64.getDecoder().decode(encodedCert);
	        
	        CertificateFactory certFactory = 
	        		CertificateFactory.getInstance("X.509");
	        InputStream in = new ByteArrayInputStream(decodedCert);
	        X509Certificate certificate = 
	        		(X509Certificate) certFactory.generateCertificate(in);
	       
	
	        PublicKey publicKey = certificate.getPublicKey();
	        return publicKey;
		} catch(Exception ex) {
			throw new RuntimeException("Unable to extract public "
					+ "key from certificate.", ex);
		}
	}
}
