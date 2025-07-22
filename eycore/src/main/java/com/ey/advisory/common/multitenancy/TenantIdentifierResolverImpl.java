package com.ey.advisory.common.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Copied from the existing ASP tenant service code.
 * 
 * @author Sai.Pakanati
 * 
 */

public class TenantIdentifierResolverImpl
		implements CurrentTenantIdentifierResolver {
	
	private final String defaultTenant;

	public TenantIdentifierResolverImpl(String defaultTenant) {
        this.defaultTenant = defaultTenant;
    }

	@Override
	public String resolveCurrentTenantIdentifier() {
		String t = TenantContext.getTenantId();
		if (t != null) {
			return t;
		} else {
			return defaultTenant;
		}
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

}