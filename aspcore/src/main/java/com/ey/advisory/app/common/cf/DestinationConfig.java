package com.ey.advisory.app.common.cf;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DestinationConfig {

	@SerializedName(value = "Name")
	private String name;

	@SerializedName(value = "Type")
	private String type;

	@SerializedName(value = "URL")
	private String url;

	@SerializedName(value = "Authentication")
	private String authentication;

	@SerializedName(value = "ProxyType")
	private String proxyType;

	@SerializedName(value = "User")
	private String user;

	@SerializedName(value = "CloudConnectorLocationId")
	private String cloudConnectorLocationId;

	@SerializedName(value = "Password")
	private String password;

}
