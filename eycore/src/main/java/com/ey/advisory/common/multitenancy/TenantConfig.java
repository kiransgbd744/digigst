package com.ey.advisory.common.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConditionalOnProperty(name = "isTwoTier", havingValue = "true")
public class TenantConfig {

    @Bean(name = "tenantIdentifierResolverImpl")
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver(
            @Value("${tenant.default}") String defaultTenant) {
        return new TenantIdentifierResolverImpl(defaultTenant);
    }

    @Bean(name = "tenantConnectionProviderImpl")
    public MultiTenantConnectionProvider multiClientTenantConnectionProvider() {
        return new TenantConnectionProviderImpl();
    }
}
