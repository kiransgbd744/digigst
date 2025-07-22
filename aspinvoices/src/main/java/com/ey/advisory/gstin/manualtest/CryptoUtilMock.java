package com.ey.advisory.gstin.manualtest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CryptoUtilMock {

	public static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";
	public static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	public static final String AES_ALGORITHM = "AES";
	public static final int ENC_BITS = 256;
	public static final String HMAC_SHA_ALGORITHM = "HmacSHA256";
	public static final String CHARACTER_ENCODING = "UTF-8";
	public static final String GSTN_CERTIFICATE="GSTN_PublicKey.cer";
	public static final String X509 = "X.509";

	private static Cipher encryptCipher;
	private static Cipher decryptCipher;
	private static KeyGenerator keygen;

	static {
		try {
			encryptCipher = Cipher.getInstance(AES_TRANSFORMATION);
			decryptCipher = Cipher.getInstance(AES_TRANSFORMATION);
			keygen = KeyGenerator.getInstance(AES_ALGORITHM);
			keygen.init(ENC_BITS);
			
		} catch (NoSuchAlgorithmException|NoSuchPaddingException e) {
			LOGGER.error("Exception in getData() in  CryptoUtilMock : " + e.getMessage());
		} 
	}

	/**
	 * This method is used to encode bytes[] to base64 string.
	 * 
	 * @param bytes
	 *            : Bytes to encode
	 * @return : Encoded Base64 String
	 */
	public static String encodeBase64String(byte[] bytes) {
		return new String(Base64.encode(bytes));
	}

	/**
	 * This method is used to decode the base64 encoded string to byte[]
	 * 
	 * @param stringData
	 *            : String to decode
	 * @return : decoded String
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] decodeBase64StringTOByte(String stringData)
			throws Exception {
		return Base64.decode(stringData.getBytes(CHARACTER_ENCODING));
	}

	/**
	 * This method is used to generate the base64 encoded secure AES 256 key *
	 * 
	 * @return : base64 encoded secure Key
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String generateSecureKey() throws Exception {
		SecretKey secretKey = keygen.generateKey();
		return encodeBase64String(secretKey.getEncoded());
	}

	/**
	 * This method is used to encrypt the string which is passed to it as byte[]
	 * and return base64 encoded encrypted String
	 * 
	 * @param plainText
	 *            : byte[]
	 * @param secret
	 *            : Key using for encrypt
	 * @return : base64 encoded of encrypted string.
	 * 
	 */

	public static String encryptEK(byte[] plainText, byte[] secret) {
		try {

			SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
			encryptCipher.init(Cipher.ENCRYPT_MODE, sk);
			return new String(Base64.encode(encryptCipher.doFinal(plainText)));

		} catch (Exception e) {
			LOGGER.error("Exception in getData() in  CryptoUtilMock : " + e.getMessage());
			return "";
		}
	}
	
	/**
	 * @param gotrek
	 * @param sessionKey
	 * @return
	 */
	public static byte[] getapiEK(String gotrek, byte[] sessionKey) {
		byte[] apiEK = null;

		try {
			apiEK = decrypt(gotrek, sessionKey);
		} catch (InvalidKeyException|IllegalBlockSizeException|BadPaddingException e) {
			LOGGER.error("Exception in getData() in  CryptoUtil : " + e.getMessage());
		}catch (IOException e) {
			 LOGGER.error("Exception in getData() in  CryptoUtil : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in getData() in  CryptoUtil : " + e.getMessage());
		}
		return apiEK;

	}

	/**
	 * This method is used to decrypt base64 encoded string using an AES 256 bit
	 * key.
	 * 
	 * @param plainText
	 *            : plain text to decrypt
	 * @param secret
	 *            : key to decrypt
	 * @return : Decrypted String
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] decrypt(String plainText, byte[] secret)
			throws InvalidKeyException, IOException, IllegalBlockSizeException,
			BadPaddingException, Exception {
		SecretKeySpec sk = new SecretKeySpec(secret, AES_ALGORITHM);
		decryptCipher.init(Cipher.DECRYPT_MODE, sk);

		return decryptCipher.doFinal(Base64.decode(plainText.getBytes()));
	}

	/**
	 * @param input
	 * @param pkey
	 * @return
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public static byte[] encryptData(String input, byte[] pkey)
			throws InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException {
		SecretKeySpec sk = new SecretKeySpec(pkey, AES_ALGORITHM);
		encryptCipher.init(Cipher.ENCRYPT_MODE, sk);

		byte[] inputBytes = input.getBytes();

		return encryptCipher.doFinal(inputBytes);
	}
	
	public static String encrypt(byte[] plaintext) throws Exception,
	NoSuchAlgorithmException, NoSuchPaddingException,
	InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

PublicKey key = readPublicKey(getPublicKey());
Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
cipher.init(Cipher.ENCRYPT_MODE, key);
byte[] encryptedByte = cipher.doFinal(plaintext);
String encodedString = new String(Base64.encode(encryptedByte));
return encodedString;
}

	/**
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String hmacSHA256(String data, String key) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CHARACTER_ENCODING),
				HMAC_SHA_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA_ALGORITHM);
		mac.init(secretKey);
		byte[] hmacData = mac.doFinal(data.getBytes(CHARACTER_ENCODING));
		return new String(Base64.encode(hmacData), CHARACTER_ENCODING);
	}

	public static InputStream getPublicKey() {
		InputStream result = null;
		ClassLoader classLoader = CryptoUtilMock.class.getClassLoader();

		try {
			result = new BufferedInputStream(new FileInputStream(new File("C:\\Users\\nikhil.duseja\\git\\sapdigigstmaster1\\aspsapapi\\src\\main\\resources\\certificates\\GSTN_G2A_SANDBOX_UAT_public.cer")));
		} catch (IOException e) {
			LOGGER.error("Exception in getData() in  CryptoUtilMock : " + e.getMessage());
		}
		return result;
	}

	private static PublicKey readPublicKey(InputStream filename)
			throws Exception {

		CertificateFactory f = CertificateFactory.getInstance(X509);
		X509Certificate certificate = (X509Certificate) f
				.generateCertificate(filename);
		PublicKey pk = certificate.getPublicKey();
		return pk;

	}
	
	public static String hmacSHA256(String data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {

		SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(secretKey);
		byte[] hmacData = mac.doFinal(data.getBytes("UTF-8"));
		return new String(Base64.encode(hmacData));
	}

	/**
	 * This method is used to encrypt the string , passed to it using a public
	 * key provided
	 * 
	 * @param planTextToEncrypt
	 *            : Text to encrypt
	 * @return :encrypted string
	 */
	

	public static String generateEncAppkey(byte[] key)  {
		try {
			return encrypt(key);
		} catch (Exception e) {
			LOGGER.error("Exception in getData() in  CryptoUtilMock : " + e.getMessage());
		}
		return null;
	}

	
	/**
	 * @param data
	 * @param apikey
	 * @return
	 */
	private static String getJsonDataFinal(String data, byte[] apikey) {
		String jsonData = null;
		try {
			jsonData = new String(
					decodeBase64StringTOByte(new String(
							decrypt(data, apikey))));
			LOGGER.info("json string " + jsonData);
		} catch (InvalidKeyException|IllegalBlockSizeException|BadPaddingException e) {
			LOGGER.error("Exception in getData() in  CryptoUtilMock : " + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("Exception in getData() in  CryptoUtilMock : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in getData() in  CryptoUtilMock : " + e.getMessage());
		}
		LOGGER.info(jsonData);
		return jsonData;

	}
	/**
	 * @param gotrek
	 * @param authKey
	 * @return
	 */
	public static String getJsonData(String data, byte[] apikey) {
		String jsonData = null;
		if(LOGGER.isInfoEnabled())
		LOGGER.info("In method : getJsonData() in : RestClientUtility ");
		
		try {
			jsonData = new String(decodeBase64StringTOByte(new String(decrypt(data, apikey))));

		} catch (InvalidKeyException|IllegalBlockSizeException|BadPaddingException e) {
			LOGGER.error("Exception in getData() in  CryptoUtil : " + e.getMessage());
		}catch (IOException e) {
			LOGGER.error("Exception in getData() in  CryptoUtil : " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception in getData() in  CryptoUtil : " + e.getMessage());
		}
		return jsonData;
	}

}
