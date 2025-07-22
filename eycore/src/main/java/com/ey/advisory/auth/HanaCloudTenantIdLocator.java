package com.ey.advisory.auth;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.async.domain.master.AccountToGroupMapping;
import com.ey.advisory.core.async.repositories.master
			.AccountToGroupMappingRepository;

@Profile("!hanadev")
@Component("DefaultTenantIdLocator")
public class HanaCloudTenantIdLocator implements TenantIdLocator {

	private static final Logger LOGGER = 
				LoggerFactory.getLogger(HanaCloudTenantIdLocator.class);
	@Autowired
	@Qualifier("accToGrpMappingRepo")
	private AccountToGroupMappingRepository accToGrpRepo;
	
	@Override
	public String getTenantId(Object obj) {
		//SAP Tenant 
		com.sap.cloud.account.TenantContext tenantContext = 
					getSAPCloudTenantContext();

        if(tenantContext == null){
        	throw new AppException("COULD_NOT_LOOKUP_TENANT_CONTEXT_API");
        }

		String tenantId = tenantContext.getTenant().getId();
		LOGGER.debug(String.format("Tenant Id: %s", tenantId));
		String tenantAccountId = tenantContext.getTenant().getAccount().getId();
		LOGGER.debug(String.format("Tenant Account Id: %s", tenantAccountId));
		String tenantAccountName = tenantContext.getTenant().getAccount()
				.getName();
		LOGGER.debug(
				String.format("Tenant Account Name: %s", tenantAccountName));

		String groupCode = getGroupCodeFromPrincipal(tenantAccountId);
		LOGGER.debug(String.format("Group Code obtained is: %s", groupCode));

		if(groupCode == null) {
			throw new AppException("Invalid Group Code. Null obtained");
		}
		
		return groupCode;
	}
	

	private String getGroupCodeFromPrincipal(String tenantAccountId) {
		try {
			AccountToGroupMapping mapping = accToGrpRepo
					.findByPrincipal(tenantAccountId);
			return mapping.getGroupCode();
		} catch (Exception ex) {
			throw new AppException("Error fetching mapping from DB", ex);
		}
	}

	private com.sap.cloud.account.TenantContext getSAPCloudTenantContext() {

		try {
			//Test Comment
			InitialContext ctx = new InitialContext();
			com.sap.cloud.account.TenantContext tenantContext = 
					(com.sap.cloud.account.TenantContext) ctx
						.lookup("java:comp/env/TenantContext");

			return tenantContext;

		} catch (NamingException e) {

			return null;

		}

	}

}
