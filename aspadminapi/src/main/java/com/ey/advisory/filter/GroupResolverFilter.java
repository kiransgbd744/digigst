package com.ey.advisory.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ey.advisory.auth.TenantIdLocator;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.dto.APIRespDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.User;
import com.sap.security.um.user.UserProvider;

public class GroupResolverFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GroupResolverFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		JsonObject jsonResp = new JsonObject();
		try {
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

			/*if (req.getUserPrincipal() == null) {
				String msg = "UserPrincial Name not available";
				jsonResp.add("hdr",
						new Gson().toJsonTree(new APIRespDto("E", msg)));
				LOGGER.error(msg);
				resp.getWriter().println(jsonResp.toString());
				return;
			}*/

			String groupCode = null;
			String usrPrnplName = null;
			
			// First try to get the user name and resolve the group code
			// using the SAML attributes. The SAML attribute for group code
			// will be available as 'DigiGSTGroupCode'.
			if (req.getUserPrincipal() != null) {
				usrPrnplName = req.getUserPrincipal().getName();
				User user = provider.getUser(usrPrnplName);
				groupCode = user.getAttribute("DigiGSTGroupCode");
				//groupCode = "EYAdmin";
				
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("User Principal Name = '" + usrPrnplName + 
							"', Group Code = '" + groupCode + "'");
				}
				
				if (groupCode != null) {
					if (groupCode.equalsIgnoreCase("EYAdmin")) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Group Code is  EYAdmin... "
									+ "About to fetch the group code "
									+ "from HTTP Session and override the"
									+ "group code sent from the Identity "
									+ "Provider");
						}
						HttpSession session = req.getSession();
						String newGroupCode = (String) session
								.getAttribute("newGroupCode");
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Group code from HTTP Session is " + 
									newGroupCode);
						}
						if(newGroupCode != null){
							TenantContext.setTenantId(newGroupCode);
						} else {
							LOGGER.warn("No group code is set in the HTTP "
									+ "session... Skipping the Tenant "
									+ "Group Code setting");
						}
						chain.doFilter(request, response);
						return;
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Group Code is  NOT EYAdmin... "
									+ "Using the same group code obtained "
									+ "from the Identity provider.");
						}						
						TenantContext.setTenantId(groupCode);
						chain.doFilter(request, response);
						return;
					}
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
			TenantIdLocator tenantIdLocator = StaticContextHolder.getBean(
					"DefaultTenantIdLocator", TenantIdLocator.class);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("TenantIdLocator " + tenantIdLocator);
			}
			groupCode = tenantIdLocator.getTenantId(null);
			
			//String groupCode = "ern00002";
			if (groupCode != null) {
				TenantContext.setTenantId(groupCode);
				chain.doFilter(request, response);
				return;
			}
			
			String msg = String.format("Unable to fetch the Group "
					+ "Code: UserPrincipal = %s", usrPrnplName);	
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
