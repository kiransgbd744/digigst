package com.ey.advisory.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.services.onboarding.UserLoadService;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(3)
public class UserLoaderFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserLoaderFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		JsonObject jsonResp = new JsonObject();
		LOGGER.debug("Hi, I am from User Request Filter");
		try {
			String action = req.getServletPath();
			LOGGER.debug("Action url is {}", action);
			if (action.contains("generateAccessToken")
					|| action.contains("vendorApi")
					|| action.contains("signreturn") || action.equalsIgnoreCase("/error")||  action.contains("actuator")) {
				LOGGER.debug(
						"Action url is 'generateAccessToken' or UI Request,"
								+ " SO skipping the filter");
				chain.doFilter(request, response);
				return;
			}

			// First try to resolve the group code from request for ERP calls.
			// This will be available as request attribute as
			// 'DigiGSTGroupCode'.
			String groupCode = TenantContext.getTenantId();
			String usrPrnplName = (String) request.getAttribute("usrPrnplName");
			LOGGER.debug("Extracted group code {} from the ERP request",
					groupCode);
			if (groupCode != null && usrPrnplName != null) {
				UserLoadService userLoadService = StaticContextHolder
						.getBean("UserLoadServiceImpl", UserLoadService.class);
				User userObj = userLoadService.loadUser(groupCode,
						usrPrnplName);
				userObj.setUserPrincipalName(usrPrnplName);
				SecurityContext.setUser(userObj);
				chain.doFilter(request, response);
				return;
			}

			String msg = String.format(
					"Unable to fetch the GroupCode: UserPrincipal = %s",
					usrPrnplName);
			LOGGER.error(msg);
			jsonResp.add("hdr",
					new Gson().toJsonTree(new APIRespDto("E", msg)));
			resp.getWriter().println(jsonResp.toString());

		} catch (Exception ex) {
			LOGGER.error(
					"Exception while extracting the groupCode from provider",
					ex);
			jsonResp.add("hdr", new Gson()
					.toJsonTree(new APIRespDto("E", ex.getMessage())));
			resp.getWriter().println(jsonResp.toString());
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
