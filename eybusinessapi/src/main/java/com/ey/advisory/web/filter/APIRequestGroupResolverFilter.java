package com.ey.advisory.web.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(1)
public class APIRequestGroupResolverFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		JsonObject jsonResp = new JsonObject();
		LOGGER.debug("Hi, I am from API Request Filter");
		String action = request.getRequestURI();
		LOGGER.debug("Action url is {}", action);
		if (action.contains("generateAccessToken")
				|| action.contains("/api/healthCheck")) {
			LOGGER.debug("Action url is 'generateAccessToken' or UI Request,"
					+ " SO skipping the filter");
			filterChain.doFilter(request, response);
			return;
		}

		if (request.getRequestURI().contains("/api")) {
			String jwtToken = request.getHeader("idtoken");
			if(jwtToken==null)
			{
		        String authRequest = request.getHeader("Authorization");
		        if (authRequest != null && !authRequest.isEmpty()) {
		            String[] authDetails = authRequest.split("\\s");
		            if (authDetails.length > 1) {
		                jwtToken = authDetails[1];
		            }
		        }
		        
			}
			if (jwtToken != null && jwtToken.length() != 0) {
				LOGGER.debug(
						"Token received while intercepting API Request is - {}",
						jwtToken);
				try {
					String groupCode = extractAttributeFromJWT(jwtToken,
							"digigstgroupcode");
					String groupName = extractAttributeFromJWT(jwtToken,
							"digigstgroupname");
					String uId = extractAttributeFromJWT(jwtToken, "uid");
					request.setAttribute("usrPrnplName", uId);
					request.setAttribute("DigiGSTGroupName", groupName);
					LOGGER.error("JWT Username is {}", groupCode);
					TenantContext.setTenantId(groupCode);
					filterChain.doFilter(request, response);
				} catch (Exception e) {
					LOGGER.error("Exception while extracting the Nested token",
							e);
					jsonResp.add("hdr",
							new Gson().toJsonTree(new APIRespDto("E", null)));
					jsonResp.addProperty("errMsg",
							"Exception while Authenticating API Auth Info");
					response.getWriter().println(jsonResp.toString());
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
				}

			} else {
				LOGGER.error(
						"Request doesn't contain while Authenticating API Auth Info ");
				jsonResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", null)));
				jsonResp.addProperty("errMsg",
						"Request doesn't contain while Authenticating API Auth Info");
				response.getWriter().println(jsonResp.toString());
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
			}
		} else if (request.getRequestURI().contains("/ui")) {
			filterChain.doFilter(request, response);
		}

	}

	private String extractJwtToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		LOGGER.debug("Debug Extracting request , authorizationHeader is {}",
				authorizationHeader);
		LOGGER.error("Error Extracting request , authorizationHeader is {}",
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
