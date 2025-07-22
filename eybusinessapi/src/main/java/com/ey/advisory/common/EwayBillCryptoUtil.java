package com.ey.advisory.common;

import java.security.InvalidKeyException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;

public class EwayBillCryptoUtil {

	public static final String AES_TRANSFORMATION = "AES/ECB/PKCS7Padding";
	public static final String AES_ALGORITHM = "AES";

	private static Cipher ENCRYPT_CIPHER;
	private static Cipher DECRYPT_CIPHER;

	static {
		try {
			Security.addProvider(
					new org.bouncycastle.jce.provider.BouncyCastleProvider());
					
			ENCRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION, "BC");
			DECRYPT_CIPHER = Cipher.getInstance(AES_TRANSFORMATION, "BC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] decrypt(String plainText, byte[] secret)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException {
		byte[] encryptedTextBytes = Base64
				.decodeBase64(plainText.getBytes(Charsets.UTF_8));
		SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		DECRYPT_CIPHER.init(Cipher.DECRYPT_MODE, sk);
		return DECRYPT_CIPHER.doFinal(encryptedTextBytes);
	}

	public static byte[] encryptData2Sel2(String text, String sek)
			throws InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException {
		byte[] dataToEncrypt = Base64.decodeBase64(text);
		byte[] keyBytes = Base64.decodeBase64(sek);

		SecretKeySpec sk = new SecretKeySpec(keyBytes, AES_ALGORITHM);
		ENCRYPT_CIPHER.init(Cipher.ENCRYPT_MODE, sk);
		return ENCRYPT_CIPHER.doFinal(dataToEncrypt);
	}

}
