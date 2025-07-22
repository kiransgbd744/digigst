package com.ey.advisory.einv.app.api;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EINVAuthTokenReqEncryptor")
public class AuthTokenReqEncryptor implements APIReqPartsEncryptor {

	public AuthTokenReqEncryptor() {
	}

	@Override
	public APIReqParts encrypt(APIParams params, String version,
			APIConfig config, APIExecParties parties,
			Map<String, Object> context, APIReqParts reqParts) {
		try {
			NICAPIProvider apiProvider = (NICAPIProvider) parties
					.getApiProvider();
			NICAPIEndUser endUser = (NICAPIEndUser) parties.getApiEndUser();
			if (endUser == null) {
				throw new APIException("EndUser Details Cannot be Empty");
			}
			LOGGER.info("NIC API username: " + endUser.getNicUserName());
			LOGGER.info("NIC API password: " + endUser.getNicPassword());
			LOGGER.info("NIC API user GSTIN: " + endUser.getGstin());
			String publicKeyPEM = apiProvider.getPublicKey();
			LOGGER.info("PublicKey PEM: " + publicKeyPEM);
			byte[] decoded = Base64.decodeBase64(publicKeyPEM);
			LOGGER.info("PublicKey PEM decoded: " + Arrays.toString(decoded));
			KeyFactory kf = KeyFactory.getInstance("RSA");
			RSAPublicKey pubKey = (RSAPublicKey) kf
					.generatePublic(new X509EncodedKeySpec(decoded));
			LOGGER.info("RSA PublicKey: " + pubKey.toString());
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			String nicPassword = endUser.getNicPassword();
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			SecretKey secretKey = keyGen.generateKey();
			byte[] appkey = secretKey.getEncoded();
			String appKeyString = Base64.encodeBase64String(appkey);
			LOGGER.info("appKeyString enc: " + appKeyString);
			context.put(APIContextConstants.APP_KEY, appKeyString);
			String nicUserName = endUser.getNicUserName();
			JsonObject reqJsonObj = new JsonObject();
			reqJsonObj.addProperty("ForceRefreshAccessToken", true);
			reqJsonObj.addProperty("UserName", nicUserName);
			reqJsonObj.addProperty("Password", nicPassword);
			reqJsonObj.addProperty("AppKey", appKeyString);
			String encodedPayload = Base64
					.encodeBase64String(reqJsonObj.toString().getBytes());
			byte encodedPayloadByte[] = cipher
					.doFinal(encodedPayload.getBytes("UTF-8"));
			String encryptedPayload = new String(
					Base64.encodeBase64(encodedPayloadByte));
			JsonObject finalJson = new JsonObject();
			finalJson.addProperty("Data", encryptedPayload);
			String reqJson = finalJson.toString();
			return new APIReqParts(reqParts.getHeaders(),
					reqParts.getQueryParams(), reqParts.getPathParams(),
					reqJson);
		} catch (Exception ex) {
			String errorMsg = "Exception while encrypting auth request";
			throw new APIException(errorMsg, ex);
		}

	}
}
