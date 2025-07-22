package com.ey.advisory.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("hanadev")
@Component("DefaultTenantIdLocator")
public class LocalDevTenantIdLocator implements TenantIdLocator {

	@Override
	public String getTenantId(Object obj) {
		// Return the statically hard coded group code.
		//return "ern00002";
		return "y8nvcqp4f9";
		//return "shi00005";
		
		//return "EYAdmin";
	}

}
