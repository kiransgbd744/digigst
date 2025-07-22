package com.ey.advisory.app.common.cf;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ConnectivityService {

	@SerializedName(value = "binding_name")
	private String bindingName;

	@SerializedName(value = "instance_name")
	private String instanceName;

	@SerializedName(value = "label")
	private String label;

	@SerializedName(value = "name")
	private String name;

	@SerializedName(value = "plan")
	private String plan;

	@SerializedName(value = "provider")
	private String provider;

	@SerializedName(value = "credentials")
	private ConnectivityCredentials credentials;
}
