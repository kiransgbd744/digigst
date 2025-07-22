package com.ey.advisory.web.filter;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.auth.TenantIdLocator;
import com.ey.advisory.common.APIRespDto;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.UUIDContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserProvider;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GroupResolverFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GroupResolverFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		JsonObject jsonResp = new JsonObject();
		UUID uuid = UUID.randomUUID();
		UUIDContext.setUniqueID(uuid.toString());
		try {
			String action = req.getServletPath();
			LOGGER.debug("Action url is {}", action);
			if (action.contains("generateAccessToken")||
					action.contains("executeAsyncJob") || action.contains("healthCheck")) {
				LOGGER.debug(
						"Action url is 'generateAccessToken' or UI Request,"
						+ " SO skipping the filter");
				chain.doFilter(request, response);
				return;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("About to get the user provider...");
			}
			UserProvider provider = UserManagementAccessor.getUserProvider();
			if (provider == null) {
				String msg = "Provider is not available";
				jsonResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				LOGGER.error(msg);
				resp.getWriter().println(jsonResp.toString());
				return;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Obtained the user provider...");
			}

			String groupCode = null;
			String usrPrnplName = null;

			// First try to resolve the group code from request for ERP calls.
			// This will be available as request attribute as
			// 'DigiGSTGroupCode'.
			if (action.contains("/api/") || action.contains("/wsapi/")) {
				groupCode = (String) req.getAttribute("DigiGSTGroupCode");
				LOGGER.debug("Extracted group code {} from the ERP request",
						groupCode);
				if (groupCode != null) {
					TenantContext.setTenantId(groupCode);
					chain.doFilter(request, response);
					return;
				}
			}

			// Then try to get the user name and resolve the group code
			// using the SAML attributes. The SAML attribute for group code
			// will be available as 'DigiGSTGroupCode'.
			if (req.getUserPrincipal() != null && action.contains("/ui/")) {
				usrPrnplName = req.getUserPrincipal().getName();
				User user = provider.getUser(usrPrnplName);
				groupCode = user.getAttribute("DigiGSTGroupCode");
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User Principal Name = '" + usrPrnplName
							+ "', Group Code = '" + groupCode + "'");
				}

				if (groupCode != null) {
					TenantContext.setTenantId(groupCode);
					chain.doFilter(request, response);
					return;
				}

			} else {
				LOGGER.warn("User Principal is not available. "
						+ "If this is the case for HCI, then it should be "
						+ "fine. Otherwise, this warning shold be taken"
						+ "very seriously.");
				LOGGER.warn("About to check for tenant id for HCI..");
			}

			// If the SAML attribute for group code is not available, then
			// attempt to get the group code from using the Tenant Details.
			TenantIdLocator tenantIdLocator = StaticContextHolder
					.getBean("DefaultTenantIdLocator", TenantIdLocator.class);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("Tenant Id Locator %s",
						tenantIdLocator);
				LOGGER.debug(msg);
			}
			groupCode = tenantIdLocator.getTenantId(null);

			if (groupCode != null) {
				TenantContext.setTenantId(groupCode);
				chain.doFilter(request, response);
				return;
			}

			String msg = String.format(
					"Unable to fetch the Group " + "Code: UserPrincipal = %s",
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
			return;
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
