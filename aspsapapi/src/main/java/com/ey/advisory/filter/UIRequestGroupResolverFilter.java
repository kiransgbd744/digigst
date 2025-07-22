package com.ey.advisory.filter;

import java.io.IOException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
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

@Order(2)
public class UIRequestGroupResolverFilter implements Filter {

	@Autowired
	@Qualifier("UserCreationRepository")
	private UserCreationRepository userCreationRepository;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UIRequestGroupResolverFilter.class);

	@Override
	public void doFilter(ServletRequest servletrequest,
			ServletResponse servletresponse, FilterChain filterChain)
			throws IOException, ServletException {
		LOGGER.debug("Hi, I am from UI Request Filter");
		HttpServletRequest request = (HttpServletRequest) servletrequest;

		HttpServletResponse response = (HttpServletResponse) servletresponse;

		LOGGER.debug("Action url is 'generateAccessToken' or UI Request,"
				+ " SO skipping the filter");

		if (request.getRequestURI().contains("/api")) {
			LOGGER.debug("Action url is 'generateAccessToken',"
					+ " SO skipping the filter");
			filterChain.doFilter(request, response);
			return;
		}
		if (request.getRequestURI().contains("/actuator")) {
						LOGGER.debug("Action url is '/actuator' or UI Request," + " SO skipping the filter");
						filterChain.doFilter(request, response);
					return;
					}
			
		if (request.getRequestURI().contains("/ui")) {
			JsonObject jsonResp = new JsonObject();
			try {
				String jwtToken = extractJwtToken(request);
				if (jwtToken != null) {
					LOGGER.debug(
							"Token received while intercepting API Request is - {}",
							jwtToken);
					String jsonObj = extractAttributeFromJWT(jwtToken,
							"xs.user.attributes");
					JsonObject nestedObj = (JsonObject) JsonParser
							.parseString(jsonObj);

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
							response.getWriter().println(jsonResp.toString());
							response.setStatus(HttpStatus.UNAUTHORIZED.value());
						} else {
							LOGGER.error("JWT Username is {}", nestedObj);
							JsonArray digigstgroupcodeArray = nestedObj
									.getAsJsonArray("digigstgroupcode");
							String digigstgroupcode = digigstgroupcodeArray
									.get(0).getAsString();
							JsonArray digigstgroupnameArray = nestedObj
									.getAsJsonArray("digigstgroupname");
							String digigstgroupname = digigstgroupnameArray
									.get(0).getAsString();

							request.setAttribute("DigiGSTGroupCode",
									digigstgroupcode);
							request.setAttribute("DigiGSTGroupName",
									digigstgroupname);
							LOGGER.debug("groupCode {} ", digigstgroupcode);
							LOGGER.debug("DigiGSTGroupName {} ",
									digigstgroupname);
							TenantContext.setTenantId(digigstgroupcode);

							if (nestedObj.has("uid")) {
								JsonArray digigstUserArray = nestedObj
										.getAsJsonArray("uid");
								String digigstUserName = digigstUserArray.get(0)
										.getAsString();
								LOGGER.debug("usrPrnplName {} ",
										digigstUserName);
								request.setAttribute("usrPrnplName",
										digigstUserName);
								LOGGER.debug(
										"About to chain the User Loader Filter");
								filterChain.doFilter(request, response);
								return;
							} else {
								JsonArray digigstEmailArray = nestedObj
										.getAsJsonArray("email");
								String digigstEmail = digigstEmailArray.get(0)
										.getAsString();
								UserCreationEntity userEntity = userCreationRepository
										.findUserEntityByEmail(digigstEmail);
								if (userEntity != null) {
									request.setAttribute("usrPrnplName",
											userEntity.getUserName());
									LOGGER.debug(
											"About to chain the User Loader Filter");
									filterChain.doFilter(request, response);
									return;
								} else {
									LOGGER.error(
											"Request doesn't contain while Authenticating UI Auth Info ");
									jsonResp.add("hdr", new Gson().toJsonTree(
											new APIRespDto("E", null)));
									jsonResp.addProperty("errMsg",
											"Request doesn't contain while Authenticating UI Auth Info");
									response.getWriter()
											.println(jsonResp.toString());
									response.setStatus(
											HttpStatus.UNAUTHORIZED.value());
								}
							}

						}
					}
				}
			} catch (Exception ex) {
				LOGGER.error("Exception while extracting the Nested token", ex);
				jsonResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", null)));
				jsonResp.addProperty("errMsg", ex.getMessage());
				response.getWriter().println(jsonResp.toString());
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
			} finally {
				TenantContext.clearTenant();
			}
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
		return authorizationHeader;
	}

	private static String extractAttributeFromJWT(String jwtToken,
			String attName) throws Exception {
		LOGGER.debug("Att Name {} ", attName);
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
