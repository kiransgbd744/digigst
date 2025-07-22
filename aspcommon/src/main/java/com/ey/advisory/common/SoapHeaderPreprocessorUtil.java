/**
 * 
 */
package com.ey.advisory.common;

import java.io.IOException;
import java.io.InputStream;
import java.security.Signature;
import java.util.Base64;
import java.util.Properties;

import org.javatuples.Pair;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
public class SoapHeaderPreprocessorUtil {

	private SoapHeaderPreprocessorUtil() {
	}

	public static void processSoapHeader(String idToken) {
		if (Strings.isNullOrEmpty(idToken)) {
			LOGGER.error("ID Token is Mandatory in the XML Payload");
			throw new AppException("idToken is mandatory");
		}
		String certificateData = loadCertificateData();
		Pair<String, String> groupPair = null;
		try {
			LOGGER.debug("Before extracting the JWT ID token");
			String[] tokenArray = idToken.split("\\.");
			if (tokenArray.length != 3) {
				String errMsg = "Invalid JWT Token";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			String signStr = tokenArray[2];
			String dataToVerify = tokenArray[0] + "." + tokenArray[1];
			if (isValidSignature(signStr, certificateData, dataToVerify)) {
				groupPair = extractGroupCode(
						dataToVerify.substring(dataToVerify.indexOf('.') + 1));
			} else {
				String errMsg = "Invalid JWT signature ";
				LOGGER.error("Invalid Token is {}", idToken);
				LOGGER.error("Exception while Authenticating Token {}", errMsg);
				throw new AppException(errMsg);
			}
			LOGGER.debug("After extracting the JWT ID token");
			if (groupPair.getValue0() == null) {
				String errMsg = String.format(
						"Group Code is not Configured for %s",
						groupPair.getValue1());
				LOGGER.error("Exception while Authenticating Token {}", errMsg);
				throw new AppException(errMsg);
			}
			String groupCode = groupPair.getValue0();
			String usrPrnplName = groupPair.getValue1();
			LOGGER.debug("Group Code is {}, user Principal Name is {}",
					groupCode, usrPrnplName);
			TenantContext.setTenantId(groupCode);
		} catch (Exception ex) {
			String errMsg = "Exception while Authenticating Token";
			LOGGER.error(errMsg, ex);
			throw new AppException(errMsg);
		}

	}

	public static boolean isValidSignature(String signStr,
			String certificateData, String dataToVerify) {
		try {
			Signature signature = Signature.getInstance("SHA256WithRSA");
			byte[] digitalSignature = Base64.getUrlDecoder().decode(signStr);
			signature.initVerify(X509CertPublicKeyExtractor
					.extractPublicKey(certificateData));
			byte[] data = dataToVerify.getBytes();
			signature.update(data);
			return signature.verify(digitalSignature);
		} catch (Exception e) {
			LOGGER.error("Excpetion while verifying the Signature", e);
			throw new AppException(e);
		}
	}

	private static Pair<String, String> extractGroupCode(String data) {
		byte[] res = Base64.getUrlDecoder().decode(data);
		String resp = new String(res);
		JsonParser parser = new JsonParser();
		JsonObject obj = (JsonObject) parser.parse(resp);
		String groupCode = obj.has("DigiGSTGroupCode")
				? obj.get("DigiGSTGroupCode").getAsString() : null;
		String groupName = obj.has("uid") ? obj.get("uid").getAsString() : null;
		return new Pair<>(groupCode, groupName);
	}

	private static String loadCertificateData() {
		Properties prop = new Properties();

		try (InputStream stream = SoapHeaderPreprocessorUtil.class
				.getClassLoader()
				.getResourceAsStream("application.properties");) {
			prop.load(stream);
			return prop.getProperty("oauth.internal.ldap_certificate");
		} catch (IOException ex) {
			String msg = "Unexpected error occured while "
					+ "loading the application properties ";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}

}
