package com.ey.advisory.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(1)
public class UIRequestGroupResolverFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		LOGGER.debug("Hi, I am from UI Request Filter");
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		HttpServletResponse httpResponse = (HttpServletResponse) response;

		LOGGER.debug("request.getRequestURI() {} ",
				httpRequest.getRequestURI());
		JsonObject jsonResp = new JsonObject();
		if (httpRequest.getRequestURI().contains("/actuator")) {
						LOGGER.debug("Action url is 'actuator',"
								+ " SO skipping the filter");
						filterChain.doFilter(request, response);
						return;
					}
		try {
			String jwtToken = extractJwtToken(httpRequest);
			if (jwtToken != null) {
				LOGGER.debug(
						"Token received while intercepting API Request is - {}",
						jwtToken);
				String jsonObj = null;

				jsonObj = extractAttributeFromJWT(jwtToken,
						"xs.user.attributes");
				JsonObject nestedObj = (JsonObject) JsonParser
						.parseString(jsonObj);
				LOGGER.error("JWT Username is {}", nestedObj);

				if (nestedObj.has("utype")) {
					JsonArray digigstUtypeArray = nestedObj
							.getAsJsonArray("utype");
					String uType = digigstUtypeArray.size() > 0
							? digigstUtypeArray.get(0).getAsString()
							: "PORTAL";
					LOGGER.debug("uType {} ", uType);
					if (Strings.isNullOrEmpty(uType)
							|| !uType.equalsIgnoreCase("PORTAL")) {
						String errMsg = "Invalid User Type for Portal Access.";
						LOGGER.error(errMsg);
						jsonResp.add("hdr", new Gson()
								.toJsonTree(new APIRespDto("E", null)));
						jsonResp.addProperty("errMsg", errMsg);
						httpResponse.getWriter().println(jsonResp.toString());
						httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
					} else {

						JsonArray digigstgroupcodeArray = nestedObj
								.getAsJsonArray("digigstgroupcode");
						String digigstgroupcode = digigstgroupcodeArray.get(0)
								.getAsString();
						JsonArray digigstgroupnameArray = nestedObj
								.getAsJsonArray("digigstgroupname");
						String digigstgroupname = digigstgroupnameArray.get(0)
								.getAsString();
						JsonArray digigstUserArray = nestedObj
								.getAsJsonArray("uid");
						String digigstUserName = digigstUserArray.get(0)
								.getAsString();

						if (digigstgroupcode != null) {
							if (digigstgroupcode.equalsIgnoreCase("EYAdmin")) {
								HttpSession session = httpRequest.getSession();
								String newGroupCode = (String) session
										.getAttribute("newGroupCode");
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Group code from HTTP Session is "
													+ newGroupCode);
								}
								if (newGroupCode != null) {
									TenantContext.setTenantId(newGroupCode);
									httpRequest.setAttribute("DigiGSTGroupCode",
											newGroupCode);
									httpRequest.setAttribute("DigiGSTGroupName",
											digigstgroupname);
								} else {
									LOGGER.warn(
											"No group code is set in the HTTP "
													+ "session... Skipping the Tenant "
													+ "Group Code setting");
									TenantContext.setTenantId(digigstgroupcode);
									httpRequest.setAttribute("DigiGSTGroupName",
											digigstgroupname);
									httpRequest.setAttribute("DigiGSTGroupCode",
											digigstgroupcode);
								}
								httpRequest.setAttribute("usrPrnplName",
										digigstUserName);
								filterChain.doFilter(httpRequest, httpResponse);
								return;
							} else {
								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											"Group Code is  NOT EYAdmin... "
													+ "Using the same group code obtained "
													+ "from the Identity provider.");
								}
								TenantContext.setTenantId(digigstgroupcode);
								httpRequest.setAttribute("usrPrnplName",
										digigstUserName);
								httpRequest.setAttribute("DigiGSTGroupCode",
										digigstgroupcode);
								httpRequest.setAttribute("DigiGSTGroupName",
										digigstgroupname);
								filterChain.doFilter(httpRequest, httpResponse);
								return;
							}
						} else {
							LOGGER.error(
									"Request doesn't contain while Authenticating UI Auth Info ");
							jsonResp.add("hdr", new Gson()
									.toJsonTree(new APIRespDto("E", null)));
							jsonResp.addProperty("errMsg",
									"Request doesn't contain GroupCode.");
							httpResponse.getWriter()
									.println(jsonResp.toString());
							httpResponse
									.setStatus(HttpStatus.UNAUTHORIZED.value());
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Exception while extracting the Nested token", ex);
			jsonResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", null)));
			jsonResp.addProperty("errMsg", ex.getMessage());
			httpResponse.getWriter().println(jsonResp.toString());
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		} finally {
			TenantContext.clearTenant();
		}
	}

	private String extractJwtToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		LOGGER.debug("Debug Extracting request , authorizationHeader is {}",
				authorizationHeader);
		if (authorizationHeader != null
				&& authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7); // Extract token excluding
														// "Bearer "
		}
		return null;
	}

	private static String extractAttributeFromJWT(String jwtToken,
			String attName) throws Exception {
		String data = jwtToken.split("\\.")[1];
		byte[] res = Base64.getUrlDecoder().decode(data);
		String resp = new String(res);
		JsonObject obj = (JsonObject) JsonParser.parseString(resp);
		if (obj.get(attName) == null) {
			throw new Exception("Attribute Not Found");
		}
		if (attName.equals("xs.system.attributes"))
			return obj.get(attName).getAsJsonObject().toString();
		else if (attName.equals("xs.user.attributes"))
			return obj.get(attName).getAsJsonObject().toString();
		else
			return obj.get(attName).getAsString();
	}
}
