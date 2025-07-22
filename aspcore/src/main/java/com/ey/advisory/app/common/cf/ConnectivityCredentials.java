package com.ey.advisory.app.common.cf;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class ConnectivityCredentials {
	
	@SerializedName("token_service_url")
	private String tokenServiceUrl;
	
	@SerializedName("onpremise_proxy_host")
	private String onpremiseProxyHost;
	
	@SerializedName("onpremise_proxy_http_port")
	private String onpremiseProxyHttpPort;
	
	@SerializedName("clientid")
	private String clientid;
	
	@SerializedName("clientsecret")
	private String clientsecret;

}
