package com.ey.advisory.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.security.Signature;
import java.util.Base64;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AuthenticationRespDto;
import com.ey.advisory.common.BusinessCriticalConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.X509CertPublicKeyExtractor;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class AuthenticationFilter
 */
@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AuthenticationFilter.class);

	private String certificateData = null;

	/**
	 * Default constructor.
	 */
	public AuthenticationFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		LOGGER.debug("Entered Authentication Filter");
		Pair<String, String> groupPair = null;
		JsonObject jsonResp = new JsonObject();
		AuthenticationRespDto apiresponse = new AuthenticationRespDto();
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String action = req.getServletPath();
		LOGGER.debug("Action url is {}", action);
		if (action.contains("generateAccessToken")
				|| action.contains("healthCheck")) {
			LOGGER.debug("Action url is 'generateAccessToken' or UI Request,"
					+ " SO skipping the filter");
			chain.doFilter(request, response);
			return;
		}
		if (action.contains("saveB2CQROnboardingParamsBCAPI")
				|| action.contains("saveB2CQRAmtOnboardingParamsBCAPI")
				|| action.contains("updateBatchStatus")
				|| action.contains("updateNICUsersBCAPI")
				|| action.contains("copyNICUsers")
				|| action.contains("getEinvEwbData")) {
			LOGGER.debug(
					"Action url is 'saveB2COnboardingParBCAPI' or UI Request,"
							+ " SO setting the GroupCode Manually");
			String groupCode = req.getHeader("groupCode");
			req.setAttribute("DigiGSTGroupCode", groupCode);
			chain.doFilter(request, response);
			return;
		}

		String idToken = req.getHeader("idtoken");
		if (idToken == null || idToken.isEmpty()) {
			String errMsg = "IDToken is Mandatory for ERP requests,"
					+ " Please configure as request header";
			LOGGER.error(errMsg);
			apiresponse.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			apiresponse.setErrorMessage(errMsg);
			jsonResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonResp.add("resp", gson.toJsonTree(apiresponse));
			resp.getWriter().println(jsonResp.toString());
			resp.setStatus(HttpStatus.UNAUTHORIZED.value());
			logReqBody(req, action);
			return;
		}
		try {
			LOGGER.debug("Before extracting the JWT ID token");
			String[] tokenArray = idToken.split("\\.");
			if (tokenArray.length != 3) {
				String errMsg = "Invalid JWT Token";
				LOGGER.error(errMsg);
				apiresponse.setErrorCode(
						BusinessCriticalConstants.INTERNAL_ERROR_CODE);
				apiresponse.setErrorMessage(errMsg);
				jsonResp.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonResp.add("resp", gson.toJsonTree(apiresponse));
				resp.getWriter().println(jsonResp.toString());
				resp.setStatus(HttpStatus.UNAUTHORIZED.value());
				return;
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
				apiresponse.setErrorCode(
						BusinessCriticalConstants.INTERNAL_ERROR_CODE);
				apiresponse.setErrorMessage(errMsg);
				jsonResp.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonResp.add("resp", gson.toJsonTree(apiresponse));
				resp.getWriter().println(jsonResp.toString());
				resp.setStatus(HttpStatus.UNAUTHORIZED.value());
				logReqBody(req, action);
				return;
			}
			LOGGER.debug("After extracting the JWT ID token");
			if (groupPair.getValue0() == null) {
				String errMsg = String.format(
						"Group Code is not Configured for %s",
						groupPair.getValue1());
				LOGGER.error("Exception while Authenticating Token {}", errMsg);
				apiresponse.setErrorCode(
						BusinessCriticalConstants.INTERNAL_ERROR_CODE);
				apiresponse.setErrorMessage(errMsg);
				jsonResp.add("hdr",
						gson.toJsonTree(APIRespDto.creatErrorResp()));
				jsonResp.add("resp", gson.toJsonTree(apiresponse));
				resp.getWriter().println(jsonResp.toString());
				resp.setStatus(HttpStatus.UNAUTHORIZED.value());
				return;
			}
			String groupCode = groupPair.getValue0();
			String usrPrnplName = groupPair.getValue1();
			LOGGER.debug("Group Code is {}, user Principal Name is {}",
					groupCode, usrPrnplName);
			req.setAttribute("DigiGSTGroupCode", groupCode);
			req.setAttribute("usrPrnplName", usrPrnplName);
			LOGGER.debug("Chaining to Next filter");
			chain.doFilter(request, response);
		} catch (Exception ex) {
			String errMsg = "Error while Authenticating the request, Please Contact HelpDesk.";
			LOGGER.error("Exception while Authenticating", ex);
			apiresponse.setErrorCode(
					BusinessCriticalConstants.INTERNAL_ERROR_CODE);
			apiresponse.setErrorMessage(errMsg);
			jsonResp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonResp.add("resp", gson.toJsonTree(apiresponse));
			resp.getWriter().println(jsonResp.toString());
			resp.setStatus(HttpStatus.UNAUTHORIZED.value());
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
			String errMsg = "Error while verifying the Signature";
			LOGGER.error(errMsg, e);
			throw new AppException(errMsg);
		}
	}

	private static Pair<String, String> extractGroupCode(String data) {
		byte[] res = Base64.getUrlDecoder().decode(data);
		String resp = new String(res);
		JsonObject obj = JsonParser.parseString(resp).getAsJsonObject();
		String groupCode = obj.has("DigiGSTGroupCode")
				? obj.get("DigiGSTGroupCode").getAsString() : null;
		String groupName = obj.has("uid") ? obj.get("uid").getAsString() : null;
		return new Pair<>(groupCode, groupName);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		Properties prop = new Properties();
		try (InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream("application.properties");) {
			prop.load(stream);
			certificateData = prop
					.getProperty("oauth.internal.ldap_certificate");
		} catch (IOException ex) {
			String msg = "Unexpected error occured while "
					+ "loading the application properties ";
			LOGGER.error(msg);
			throw new AppException(msg, ex);
		}
	}

	private void logReqBody(HttpServletRequest request, String action) {
		if (action.contains("generateEinvoice")
				|| action.contains("generateEwayBill")) {
			try {
				String reqBody = IOUtils.toString(request.getReader());
				LOGGER.debug("Request Payload is {}", reqBody);
				JsonObject jsonObj = JsonParser.parseString(reqBody).getAsJsonObject();
				JsonObject reqObj = jsonObj.get("req").getAsJsonObject();
				String docNo = reqObj.get("docNo") != null
						? reqObj.get("docNo").getAsString() : "";
				String sgstin = reqObj.get("suppGstin") != null
						? reqObj.get("suppGstin").getAsString() : "";
				LOGGER.error(
						"IDToken is not available/Invalid ID Token in request Header,"
								+ " DocNo is {} and SGSTN is {}",
						docNo, sgstin);
			} catch (Exception ex) {
				LOGGER.error("Exception while logging the req body", ex);
			}
		}
	}

}
