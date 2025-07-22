package com.ey.advisory.einv.app.api;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoUtils {
	
	public static final String AES_TRANSFORMATION = "AES/ECB/PKCS7Padding";
	public static final String AES_ALGORITHM = "AES";

	private static Cipher createCipher() {
		try {
			Security.addProvider(
					new org.bouncycastle.jce.provider.BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION, "BC");
			return cipher;
		} catch (Exception ex) {
			String msg = "Exception while instantiating "
					+ "Encrypt/Decrypt Ciphers";
			LOGGER.error(msg, ex);
			throw new APICryptoException(msg, ex);
		}
	}

	public static byte[] decrypt(
			String plainText, byte[] secret) {
		try {
			Cipher cipher = createCipher();
			byte[] encryptedTextBytes = 
					Base64.decodeBase64(plainText.getBytes(Charsets.UTF_8));
			SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, sk);
			return cipher.doFinal(encryptedTextBytes);
		} catch(Exception ex) {
			String msg = "Exception while decrypting data";
			throw new APICryptoException(msg, ex);
		}
	}

	public static byte[] encrypt(String text, String encKey) {
		try {
			Cipher cipher = createCipher();
			byte[] dataToEncrypt = Base64.decodeBase64(text);
			byte[] keyBytes = Base64.decodeBase64(encKey);
	
			SecretKeySpec sk = new SecretKeySpec(keyBytes, AES_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, sk);
			return cipher.doFinal(dataToEncrypt);
		} catch(Exception ex) {
			String msg = "Exception while encrypting data";
			throw new APICryptoException(msg, ex);
		}
	}

}
