package com.ey.advisory.app.config;

import org.springframework.stereotype.Component;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;

@Component("XsuaaConfiguration")
public class XsuaaConfiguration implements XsuaaServiceConfiguration {

	@Override
	public String getClientId() {
		// TODO Auto-generated method stub
		return "sb-digigst!t31012";
	}

	@Override
	public String getClientSecret() {
		// TODO Auto-generated method stub
		return "P3ygnutJ8RwDm7EdQwQzMmONlJU=";
	}

	@Override
	public String getUaaUrl() {
		// TODO Auto-generated method stub
		return "https://eycfdevmigration.authentication.ap21.hana.ondemand.com";
	}

	@Override
	public String getAppId() {
		return "digigst!t31012";
	}

	@Override
	public String getUaaDomain() {
		// TODO Auto-generated method stub
		return "authentication.ap21.hana.ondemand.com";
	}

	@Override
	public String getVerificationKey() {
		// TODO Auto-generated method stub
		return "authentication.ap21.hana.ondemand.com";
	}

}
