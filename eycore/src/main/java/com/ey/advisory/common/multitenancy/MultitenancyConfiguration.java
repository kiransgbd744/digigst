package com.ey.advisory.common.multitenancy;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MasterDataSourceProperties.class, TenantJpaProperties.class,
        TenantDataSourceProperties.class, TenantDataSource.class})
public class MultitenancyConfiguration {
}
