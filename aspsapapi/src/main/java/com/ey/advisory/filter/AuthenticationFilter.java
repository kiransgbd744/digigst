package com.ey.advisory.filter;

import java.io.IOException;
import java.io.InputStream;
import java.security.Signature;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.X509CertPublicKeyExtractor;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.security.SignatureException;
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

	private static final List<String> AUTO_FETCH_URLS = ImmutableList.of(
			"/api/generateOtp.do", "/api/getInActiveGstins.do",
			"/api/generateAuthToken.do");

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
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String action = req.getServletPath();
		LOGGER.debug("Action url is {}", action);
		if (action.contains("generateAccessToken")) {
			LOGGER.debug("Action url is 'generateAccessToken' or UI Request,"
					+ " SO skipping the filter");
			chain.doFilter(request, response);
			return;
		}
		String idToken = req.getHeader("idtoken");
		if (idToken == null || idToken.isEmpty()) {
			String errMsg = "IDToken is Mandatory for ERP requests,"
					+ " Please configure as request header";
			LOGGER.error(errMsg);
			if (AUTO_FETCH_URLS.contains(action)) {
				jsonResp.addProperty("status", "0");
				jsonResp.addProperty("errorDetails", errMsg);
			} else {
				jsonResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", null)));
				jsonResp.addProperty("errMsg", errMsg);
			}
			resp.getWriter().println(jsonResp.toString());
			resp.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		}
		try {
			LOGGER.debug("Before extracting the JWT ID token");
			String[] tokenArray = idToken.split("\\.");
			if (tokenArray.length != 3) {
				String errMsg = "Invalid JWT Token";
				LOGGER.error(errMsg);
				if (AUTO_FETCH_URLS.contains(action)) {
					jsonResp.addProperty("status", "0");
					jsonResp.addProperty("errorDetails", errMsg);
				} else {
					jsonResp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", null)));
					jsonResp.addProperty("errMsg", errMsg);
				}
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
				LOGGER.error("Exception while Authenticating Token {}", errMsg);
				if (AUTO_FETCH_URLS.contains(action)) {
					jsonResp.addProperty("status", "0");
					jsonResp.addProperty("errorDetails", errMsg);
				} else {
					jsonResp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", null)));
					jsonResp.addProperty("errMsg", errMsg);
				}
				resp.getWriter().println(jsonResp.toString());
				resp.setStatus(HttpStatus.UNAUTHORIZED.value());
				return;
			}
			LOGGER.debug("After extracting the JWT ID token");
			if (groupPair.getValue0() == null) {
				String errMsg = "Group Code is Configured, Please check with Techinal team";
				LOGGER.error("Exception while Authenticating Token {}", errMsg);
				if (AUTO_FETCH_URLS.contains(action)) {
					jsonResp.addProperty("status", "0");
					jsonResp.addProperty("errorDetails", errMsg);
				} else {
					jsonResp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", null)));
					jsonResp.addProperty("errMsg", errMsg);
				}
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
		} catch (SignatureException ex) {
			String errMsg = "Invalid JWT signature ";
			LOGGER.error("Exception while Authenticating Token {}", errMsg, ex);
			if (AUTO_FETCH_URLS.contains(action)) {
				jsonResp.addProperty("status", "0");
				jsonResp.addProperty("errorDetails", errMsg);
			} else {
				jsonResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", null)));
				jsonResp.addProperty("errMsg", errMsg);
			}
			resp.getWriter().println(jsonResp.toString());
			resp.setStatus(HttpStatus.UNAUTHORIZED.value());
		} catch (Exception ex) {
			LOGGER.error("Exception while Authenticating", ex);
			if (AUTO_FETCH_URLS.contains(action)) {
				jsonResp.addProperty("status", "0");
				jsonResp.addProperty("errorDetails", ex.getMessage());
			} else {
				jsonResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", null)));
				jsonResp.addProperty("errMsg", ex.getMessage());
			}
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

}
